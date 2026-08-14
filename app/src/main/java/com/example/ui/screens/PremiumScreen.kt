package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.SandookViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PremiumScreen(
    viewModel: SandookViewModel
) {
    val profile by viewModel.userProfile.collectAsState()
    val isPremium = profile?.isPremium ?: false

    var showPaymentSimulator by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
    ) {
        // Dynamic banner status based on current premium state
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .border(
                        BorderStroke(
                            width = if (isPremium) 2.dp else 1.dp,
                            color = if (isPremium) TrueGold else SurfaceVariant
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
            ) {
                Column(
                    modifier = Modifier
                        .background(
                            brush = Brush.verticalGradient(
                                colors = if (isPremium) {
                                    listOf(SurfaceVariant, DarkBackground)
                                } else {
                                    listOf(DarkSurface, DarkBackground)
                                }
                            )
                        )
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .background(TrueGold, shape = RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "SANDOOK GOLD 👑",
                            color = DeepForest,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp,
                            letterSpacing = 1.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = if (isPremium) "Your Premium Membership is Active!" else "Unlock Premium Savings Perks",
                        style = MaterialTheme.typography.titleLarge,
                        color = DarkGray,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Take complete command over daily wealth builders.",
                        color = SoftGray,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "₹1",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Black,
                        color = TrueGold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "per month billing",
                        fontSize = 12.sp,
                        color = SoftGray,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Feature benefits list layout
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SurfaceVariant, RoundedCornerShape(16.dp))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Gold membership advantages:",
                        color = DarkGray,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                    PremiumFeatureBenefitRow(
                        title = "Ad-free experience",
                        description = "Zero popups or interstitial banners on any interface layout."
                    )

                    PremiumFeatureBenefitRow(
                        title = "Sub-rupee UPI auto transfers",
                        description = "Enable ultra-micro savings starting under ₹10 daily without blocks."
                    )

                    PremiumFeatureBenefitRow(
                        title = "Unlimited visual goals tracker",
                        description = "Setup infinite visual milestone targets concurrently."
                    )

                    PremiumFeatureBenefitRow(
                        title = "Enterprise financial reports",
                        description = "Download CSV spreadsheets of historic savings transactions ledger."
                    )
                }
            }
        }

        // Master interaction CTAs (Subscribe / Cancel)
        item {
            AnimatedContent(targetState = isPremium, label = "PaymentCTAs") { premiumState ->
                if (!premiumState) {
                    Button(
                        onClick = { showPaymentSimulator = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TrueGold,
                            contentColor = DeepForest
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("subscribe_pro_button")
                    ) {
                        Text(
                            text = "Upgrade for ₹1 Monthly",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Button(
                        onClick = { viewModel.handleCancelPremiumPro() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF321419),
                            contentColor = Color(0xFFCF6679)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .border(1.dp, Color(0xFFCF6679).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .testTag("cancel_pro_button")
                    ) {
                        Text(
                            text = "Cancel Sandook Pro subscription",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    // Interactive Payment Simulator overlay
    if (showPaymentSimulator) {
        AlertDialog(
            onDismissRequest = { showPaymentSimulator = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Payment, contentDescription = null, tint = TrueGold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Razorpay Gateway Simulator", color = DarkGray, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Mock sandbox payment authorized. Verify UPI address & secure mandate consent parameters:",
                        color = SoftGray,
                        fontSize = 13.sp
                    )

                    Card(
                        colors = CardDefaults.cardColors(containerColor = SurfaceVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Merchant Detail", color = SoftGray, fontSize = 11.sp)
                                Text("SANDOOK GOLD CORP", color = DarkGray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Subscription Term", color = SoftGray, fontSize = 11.sp)
                                Text("₹1 / month (recurring)", color = TrueGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Text(
                        text = "Clicking Confirm executes a simulated Razorpay charge loop in the DB and marks your single active user profile as PRO.",
                        color = SoftGray,
                        fontSize = 11.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.handleUnlockPremiumPro()
                        showPaymentSimulator = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TrueGold, contentColor = DeepForest)
                ) {
                    Text("Confirm ₹1 Charge", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showPaymentSimulator = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = SoftGray)
                ) {
                    Text("Decline Charge")
                }
            },
            containerColor = DarkSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun PremiumFeatureBenefitRow(
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = TrueGold,
            modifier = Modifier
                .size(20.dp)
                .padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                color = DarkGray,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                text = description,
                color = SoftGray,
                fontSize = 11.sp
            )
        }
    }
}
