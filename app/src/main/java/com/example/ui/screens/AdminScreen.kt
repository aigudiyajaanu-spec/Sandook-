package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.SandookViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AdminScreen(
    viewModel: SandookViewModel
) {
    val plans by viewModel.allPlans.collectAsState()
    val transactions by viewModel.allTransactions.collectAsState()
    val profile by viewModel.userProfile.collectAsState()

    // Analytical metrics calculations based on live local structures
    val activeLocalPlansCount = plans.count { it.isActive }
    val isUserPremium = profile?.isPremium == true

    val totalPremiumSubsCollected = if (isUserPremium) 4 else 3 // Simulate live subscription sandbox users counts
    val totalSimulatedPremiumRevenue = totalPremiumSubsCollected * 1.00 // ₹1 per premium rate

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
    ) {
        // Intro Admin card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceVariant.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(TrueGold, shape = RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = DeepForest)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Sandbox Operations Console",
                            color = DarkGray,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "Platform analytics and mock users monitoring.",
                            color = SoftGray,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Platform metrics dashboard grids
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "System Statistics Dashboard",
                    color = DarkGray,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Global savings box
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, SurfaceVariant, RoundedCornerShape(12.dp))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Est. Active Plans", color = SoftGray, fontSize = 10.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "${activeLocalPlansCount + 44}",
                                color = TrueGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        }
                    }

                    // Revenue collections metric
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, SurfaceVariant, RoundedCornerShape(12.dp))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Monthly Gold Rev.", color = SoftGray, fontSize = 10.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "₹${String.format("%.2f", totalSimulatedPremiumRevenue)}",
                                color = MintGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        }
                    }
                }
            }
        }

        // Custom canvas bar chart to display Simulated Platform Deposit Flow
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SurfaceVariant, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Real-time Platform Traffic Inflow",
                        color = DarkGray,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "Global mock API deposits load metrics",
                        color = SoftGray,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Draw basic bar columns via Canvas to represent beautiful metrics
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height
                            val barWidth = w / 12f

                            val heights = listOf(0.4f, 0.7f, 0.5f, 0.9f, 0.6f, 0.3f, 0.8f, 0.5f)

                            heights.forEachIndexed { i, portion ->
                                val barH = h * portion
                                drawRect(
                                    color = if (i % 2 == 0) TrueGold else MintGreen,
                                    topLeft = Offset(i * (barWidth + 14f) + 10f, h - barH),
                                    size = Size(barWidth, barH)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("10am", color = SoftGray, fontSize = 9.sp)
                        Text("12pm", color = SoftGray, fontSize = 9.sp)
                        Text("2pm", color = SoftGray, fontSize = 9.sp)
                        Text("4pm", color = SoftGray, fontSize = 9.sp)
                    }
                }
            }
        }

        // User list management block
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Simulated User Accounts Ledger",
                    color = DarkGray,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                viewModel.mockUsers.forEach { mockUser ->
                    MockUserRow(
                        user = mockUser,
                        onToggleState = { viewModel.toggleAdminUserStatus(mockUser.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun MockUserRow(
    user: com.example.ui.viewmodel.MockUser,
    onToggleState: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, SurfaceVariant, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(SurfaceVariant, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        user.name.take(1),
                        color = TrueGold,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = user.name,
                        color = DarkGray,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "+91 ${user.phone}",
                        color = SoftGray,
                        fontSize = 11.sp
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "₹${user.totalSaved.toInt()}",
                        color = MintGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = user.status,
                        color = if (user.status == "Active") MintGreen else Color.Red,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }

                // Suspend button simulator
                Button(
                    onClick = onToggleState,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (user.status == "Active") Color(0xFF331414) else DeepForest,
                        contentColor = if (user.status == "Active") Color.Red else MintGreen
                    ),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Text(
                        text = if (user.status == "Active") "Suspend" else "Resume",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
