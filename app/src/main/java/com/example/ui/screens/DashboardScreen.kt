package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.SavingsPlan
import com.example.data.entity.WalletTransaction
import com.example.ui.theme.*
import com.example.ui.viewmodel.SandookViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun DashboardScreen(
    viewModel: SandookViewModel,
    onNavigateToPlans: () -> Unit,
    onNavigateToWallet: () -> Unit,
    onNavigateToNotifications: () -> Unit
) {
    val profile by viewModel.userProfile.collectAsState()
    val plans by viewModel.allPlans.collectAsState()
    val transactions by viewModel.allTransactions.collectAsState()
    val notifications by viewModel.allNotifications.collectAsState()

    val unreadNotifCount = notifications.count { !it.isRead }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
    ) {
        // Top Premium Bar Row
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(TrueGold, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (profile?.fullName?.take(1) ?: "S"),
                            fontWeight = FontWeight.Bold,
                            color = DeepForest,
                            fontSize = 18.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Swagat Hai,",
                            color = SoftGray,
                            fontSize = 12.sp
                        )
                        Text(
                            text = if (profile?.fullName.isNullOrBlank()) "+91 ${profile?.phoneNumber ?: "User"}" else profile?.fullName!!,
                            color = DarkGray,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Premium Gold Badge
                    if (profile?.isPremium == true) {
                        Box(
                            modifier = Modifier
                                .background(TrueGold, shape = RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "PRO 👑",
                                color = DeepForest,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 11.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                    }

                    // Alerts Bell Badge
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(DarkSurface, shape = RoundedCornerShape(12.dp))
                            .clickable { onNavigateToNotifications() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Alerts",
                            tint = TrueGold
                        )
                        if (unreadNotifCount > 0) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(Color.Red, shape = CircleShape)
                                    .align(Alignment.TopEnd)
                                    .offset(x = (-4).dp, y = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // Vault Card Block
        item {
            VaultBalanceCard(
                walletBalance = profile?.walletBalance ?: 0.0,
                showBalance = viewModel.showBalance,
                isPremium = profile?.isPremium ?: false,
                onToggleVisibility = { viewModel.showBalance = !viewModel.showBalance },
                onNavigateToWallet = onNavigateToWallet
            )
        }

        // Direct simulator controller (AutoPay triggers)
        item {
            AutoPaySimulationTriggerCard(
                plansCount = plans.count { it.isActive },
                onTriggerSimulate = { viewModel.handleTriggerAutoPaySimulation() }
            )
        }

        // Cumulative Savings Line Chart (Canvas-based visual analytics)
        item {
            SavingsProgressChart(transactions = transactions)
        }

        // Active Savings Plan list header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Active Savings Plans",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = DarkGray
                    )
                    Text(
                        text = "Your auto debit mandates scheduled",
                        color = SoftGray,
                        fontSize = 11.sp
                    )
                }
                TextButton(
                    onClick = onNavigateToPlans,
                    colors = ButtonDefaults.textButtonColors(contentColor = TrueGold)
                ) {
                    Text("View All", fontWeight = FontWeight.Bold)
                    Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp))
                }
            }
        }

        // Horizontal Active Plans Slider items
        item {
            if (plans.isEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .border(1.dp, SurfaceVariant, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.AddCard, contentDescription = null, tint = SoftGray, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "No active savings plans yet.",
                            color = SoftGray,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Tap here to schedule a daily/weekly/monthly savings",
                            color = TrueGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { onNavigateToPlans() }
                        )
                    }
                }
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(end = 16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(plans) { plan ->
                        SavingsPlanHorizontalItem(plan = plan)
                    }
                }
            }
        }

        // Recent Transaction Logs block
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Recent Transactions Ledger",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = DarkGray
                )

                if (transactions.isEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Zero transacted logs. Make your first deposit!",
                                color = SoftGray,
                                fontSize = 13.sp
                            )
                        }
                    }
                } else {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            transactions.take(5).forEach { tx ->
                                TransactionRow(tx = tx)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VaultBalanceCard(
    walletBalance: Double,
    showBalance: Boolean,
    isPremium: Boolean,
    onToggleVisibility: () -> Unit,
    onNavigateToWallet: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = DeepForest),
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(DeepForest, Color(0xFF002713))
                    )
                )
                .fillMaxWidth()
                .border(1.dp, if (isPremium) TrueGold else SurfaceVariant, RoundedCornerShape(20.dp))
                .padding(24.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Inventory2,
                            contentDescription = null,
                            tint = TrueGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "SANDOOK TOTAL SAVINGS",
                            color = TrueGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            letterSpacing = 1.sp
                        )
                    }

                    Icon(
                        imageVector = if (showBalance) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                        contentDescription = "Toggle Balance",
                        tint = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier
                            .size(20.dp)
                            .clickable { onToggleVisibility() }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (showBalance) "₹${String.format(Locale.getDefault(), "%,.2f", walletBalance)}" else "••••••",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 32.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Safety Protection Mode",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 10.sp
                        )
                        Text(
                            text = "256-bit AES Encrypted",
                            color = PremiumGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }

                    Button(
                        onClick = onNavigateToWallet,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TrueGold,
                            contentColor = DeepForest
                        ),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text(
                            text = "Transact Box",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AutoPaySimulationTriggerCard(
    plansCount: Int,
    onTriggerSimulate: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceVariant.copy(alpha = 0.4f)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, SurfaceVariant, RoundedCornerShape(12.dp))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "AutoPay Simulation Gear",
                    color = DarkGray,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = "Simulate $plansCount active UPI mandates to subtract and save right now.",
                    color = SoftGray,
                    fontSize = 11.sp
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = onTriggerSimulate,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MintGreen,
                    contentColor = DeepForest
                ),
                modifier = Modifier.height(36.dp)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Trigger", fontWeight = FontWeight.Bold, fontSize = 12.dp.value.sp)
            }
        }
    }
}

@Composable
fun SavingsProgressChart(transactions: List<WalletTransaction>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Savings Velocity Chart",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = DarkGray
                    )
                    Text(
                        text = "Visualizing cumulative asset progress in real-time",
                        color = SoftGray,
                        fontSize = 10.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .background(DeepForest, shape = RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("LIVE FEED", color = MintGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Custom Dynamic Line Chart via Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                // Compute visual coordinates based on last transactions
                val points = remember(transactions) {
                    var runningSum = 1000f // starter simulation offset
                    val sums = mutableListOf(runningSum)
                    transactions.sortedBy { it.timestamp }.takeLast(10).forEach { tx ->
                        if (tx.type == "DEPOSIT" || tx.type == "AUTOPAY_DEDUCTION") {
                            runningSum += tx.amount.toFloat()
                        } else {
                            runningSum = maxOf(0f, runningSum - tx.amount.toFloat())
                        }
                        sums.add(runningSum)
                    }
                    if (sums.size < 4) {
                        sums.addAll(listOf(1200f, 1800f, 2500f, 3100f))
                    }
                    sums
                }

                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height
                    val maxVal = points.maxOrNull() ?: 1f
                    val minVal = points.minOrNull() ?: 0f
                    val delta = if (maxVal == minVal) 1f else (maxVal - minVal)

                    val p = Path()
                    val stepX = width / (points.size - 1)

                    points.forEachIndexed { idx, valItem ->
                        val x = idx * stepX
                        val fraction = (valItem - minVal) / delta
                        // Grid heights are inverted in android canvas coordinate systems
                        val y = height - (fraction * (height - 20f)) - 10f

                        if (idx == 0) {
                            p.moveTo(x, y)
                        } else {
                            p.lineTo(x, y)
                        }

                        // Plot single gold point circles
                        drawCircle(
                            color = TrueGold,
                            radius = 4f,
                            center = Offset(x, y)
                        )
                    }

                    drawPath(
                        path = p,
                        color = MintGreen,
                        style = Stroke(width = 4f)
                    )

                    // Draw baseline indicator line
                    drawLine(
                        color = SurfaceVariant,
                        start = Offset(0f, height),
                        end = Offset(width, height),
                        strokeWidth = 2f
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Mon 1", color = SoftGray, fontSize = 10.sp)
                Text("Week 2", color = SoftGray, fontSize = 10.sp)
                Text("Week 3", color = SoftGray, fontSize = 10.sp)
                Text("Active Today", color = TrueGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun SavingsPlanHorizontalItem(plan: SavingsPlan) {
    Card(
        modifier = Modifier
            .width(180.dp)
            .height(110.dp)
            .border(1.dp, SurfaceVariant, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .background(DeepForest, shape = RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = plan.frequency,
                        color = PremiumGold,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (plan.isAutoPayEnabled) {
                    Icon(
                        imageVector = Icons.Default.FlashOn,
                        contentDescription = "AutoPay Active",
                        tint = TrueGold,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Text(
                text = plan.name,
                color = DarkGray,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "₹${plan.amount.toInt()}",
                    color = MintGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Text(
                    text = if (plan.isActive) "Active" else "Paused",
                    color = if (plan.isActive) MintGreen else SoftGray,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun TransactionRow(tx: WalletTransaction) {
    val formatter = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }
    val formattedDate = formatter.format(Date(tx.timestamp))

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            color = when (tx.type) {
                                "DEPOSIT" -> DeepForest
                                "WITHDRAWAL" -> Color(0xFF3E1F1F)
                                else -> SurfaceVariant
                            },
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (tx.type) {
                            "DEPOSIT" -> Icons.Default.ArrowUpward
                            "WITHDRAWAL" -> Icons.Default.ArrowDownward
                            else -> Icons.Default.Autorenew
                        },
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = when (tx.type) {
                            "DEPOSIT" -> MintGreen
                            "WITHDRAWAL" -> Color(0xFFCF6679)
                            else -> TrueGold
                        }
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = tx.description,
                        color = DarkGray,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = formattedDate,
                        color = SoftGray,
                        fontSize = 10.sp
                    )
                }
            }

            Text(
                text = when (tx.type) {
                    "WITHDRAWAL" -> "-₹${tx.amount.toInt()}"
                    else -> "+₹${tx.amount.toInt()}"
                },
                color = when (tx.type) {
                    "WITHDRAWAL" -> Color(0xFFCF6679)
                    else -> MintGreen
                },
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
        HorizontalDivider(color = SurfaceVariant, modifier = Modifier.padding(horizontal = 8.dp))
    }
}
