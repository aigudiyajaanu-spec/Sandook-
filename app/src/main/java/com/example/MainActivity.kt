package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.SandookDatabase
import com.example.data.repository.SandookRepository
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.SandookViewModel
import com.example.ui.viewmodel.SandookViewModelFactory

// Enum for robust client navigation state management
enum class SandookScreen {
    DASHBOARD, WALLET, PLANS, PREMIUM, ADMIN, NOTIFICATIONS
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Initialize Database and Repository
        val database = SandookDatabase.getDatabase(this)
        val repository = SandookRepository(database.sandookDao())

        setContent {
            SandookTheme {
                // Instantiate our shared command ViewModel
                val sandookViewModel: SandookViewModel = viewModel(
                    factory = SandookViewModelFactory(repository)
                )

                val profile by sandookViewModel.userProfile.collectAsState()
                var currentScreen by remember { mutableStateOf(SandookScreen.DASHBOARD) }

                // Collect and render popup notifications
                val context = LocalContext.current
                LaunchedEffect(sandookViewModel.transientMessage) {
                    sandookViewModel.transientMessage?.let { msg ->
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        sandookViewModel.clearPopupMessage()
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DarkBackground
                ) {
                    // Step 1: Check security - if not validated, require login or PIN entry
                    if (profile == null || !profile!!.isLoggedIn || profile!!.pinCode.isEmpty() || !sandookViewModel.isPinVerified) {
                        AuthScreen(
                            viewModel = sandookViewModel,
                            onAuthSuccess = {
                                currentScreen = SandookScreen.DASHBOARD
                            }
                        )
                    } else {
                        // Step 2: Show secure dashboard with custom bottom navigation bar
                        Scaffold(
                            containerColor = DarkBackground,
                            topBar = {
                                SandookTopBar(
                                    currentScreen = currentScreen,
                                    onSignOut = {
                                        sandookViewModel.handleSignOut {
                                            currentScreen = SandookScreen.DASHBOARD
                                        }
                                    },
                                    onGoBack = { currentScreen = SandookScreen.DASHBOARD }
                                )
                            },
                            bottomBar = {
                                SandookBottomNavigation(
                                    currentScreen = currentScreen,
                                    onSelectScreen = { currentScreen = it }
                                )
                            },
                            contentWindowInsets = WindowInsets.navigationBars
                        ) { innerPadding ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(innerPadding)
                            ) {
                                // Dynamic Slide animations between sections
                                AnimatedContent(
                                    targetState = currentScreen,
                                    transitionSpec = {
                                        fadeIn() togetherWith fadeOut()
                                    },
                                    label = "ScreenTransition"
                                ) { screen ->
                                    when (screen) {
                                        SandookScreen.DASHBOARD -> DashboardScreen(
                                            viewModel = sandookViewModel,
                                            onNavigateToPlans = { currentScreen = SandookScreen.PLANS },
                                            onNavigateToWallet = { currentScreen = SandookScreen.WALLET },
                                            onNavigateToNotifications = { currentScreen = SandookScreen.NOTIFICATIONS }
                                        )
                                        SandookScreen.WALLET -> WalletScreen(
                                            viewModel = sandookViewModel
                                        )
                                        SandookScreen.PLANS -> AutoPayPlansScreen(
                                            viewModel = sandookViewModel
                                        )
                                        SandookScreen.PREMIUM -> PremiumScreen(
                                            viewModel = sandookViewModel
                                        )
                                        SandookScreen.ADMIN -> AdminScreen(
                                            viewModel = sandookViewModel
                                        )
                                        SandookScreen.NOTIFICATIONS -> NotificationFeed(
                                            viewModel = sandookViewModel
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SandookTopBar(
    currentScreen: SandookScreen,
    onSignOut: () -> Unit,
    onGoBack: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = when (currentScreen) {
                    SandookScreen.DASHBOARD -> "SANDOOK"
                    SandookScreen.WALLET -> "Savings Vault"
                    SandookScreen.PLANS -> "AutoPay Schedules"
                    SandookScreen.PREMIUM -> "Gold Membership"
                    SandookScreen.ADMIN -> "Admin Operations"
                    SandookScreen.NOTIFICATIONS -> "System Updates"
                },
                color = DarkGray,
                fontWeight = FontWeight.Black,
                fontSize = 18.sp,
                letterSpacing = 1.sp
            )
        },
        navigationIcon = {
            if (currentScreen != SandookScreen.DASHBOARD) {
                IconButton(onClick = onGoBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back home",
                        tint = EmeraldGreen
                    )
                }
            } else {
                Icon(
                    imageVector = Icons.Default.Savings,
                    contentDescription = null,
                    tint = EmeraldGreen,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        },
        actions = {
            IconButton(onClick = onSignOut) {
                Icon(
                    imageVector = Icons.Default.Logout,
                    contentDescription = "Sign Out lock",
                    tint = Color.Red.copy(alpha = 0.8f)
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = DarkBackground,
            titleContentColor = DarkGray
        )
    )
}

@Composable
fun SandookBottomNavigation(
    currentScreen: SandookScreen,
    onSelectScreen: (SandookScreen) -> Unit
) {
    NavigationBar(
        containerColor = DarkSurface,
        tonalElevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .testTag("sandook_bottom_nav")
    ) {
        val navItems = listOf(
            NavigationItem("Home", Icons.Default.Home, SandookScreen.DASHBOARD, "nav_home"),
            NavigationItem("Vault", Icons.Default.AccountBalanceWallet, SandookScreen.WALLET, "nav_wallet"),
            NavigationItem("AutoPay", Icons.Default.Autorenew, SandookScreen.PLANS, "nav_plans"),
            NavigationItem("Gold 👑", Icons.Default.Star, SandookScreen.PREMIUM, "nav_premium"),
            NavigationItem("Admin", Icons.Default.SupervisorAccount, SandookScreen.ADMIN, "nav_admin")
        )

        navItems.forEach { item ->
            val isSelected = currentScreen == item.screen
            NavigationBarItem(
                selected = isSelected,
                onClick = { onSelectScreen(item.screen) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = if (isSelected) EmeraldGreen else SoftGray
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        color = if (isSelected) EmeraldGreen else SoftGray
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = SurfaceVariant,
                    selectedIconColor = EmeraldGreen,
                    unselectedIconColor = SoftGray
                ),
                modifier = Modifier.testTag(item.tag)
            )
        }
    }
}

data class NavigationItem(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val screen: SandookScreen,
    val tag: String
)
