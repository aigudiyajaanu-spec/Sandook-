package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.SavingsGoal
import com.example.ui.theme.*
import com.example.ui.viewmodel.SandookViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun WalletScreen(
    viewModel: SandookViewModel
) {
    val profile by viewModel.userProfile.collectAsState()
    val goals by viewModel.allGoals.collectAsState()

    var showAddGoalForm by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
    ) {
        // Balance Banner
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .border(1.dp, SurfaceVariant, RoundedCornerShape(16.dp))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Vault Box Available Balance",
                        color = SoftGray,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "₹${String.format("%,.2f", profile?.walletBalance ?: 0.0)}",
                        color = TrueGold,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }

        // Add & Withdraw Cards (Double Columns/Collapsible actions)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "Load & Unload Vault",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = DarkGray
                )

                // Deposit Section
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, SurfaceVariant, RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Deposit Cash into Sandook",
                            color = DarkGray,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = viewModel.depositInputAmount,
                            onValueChange = { viewModel.depositInputAmount = it },
                            label = { Text("Deposit Amount (₹)") },
                            prefix = { Text("₹", color = TrueGold) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = DarkGray,
                                unfocusedTextColor = DarkGray,
                                focusedBorderColor = TrueGold,
                                unfocusedBorderColor = SurfaceVariant,
                                focusedLabelColor = TrueGold,
                                unfocusedLabelColor = SoftGray
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("deposit_input")
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Quick deposit suggestions chips row
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            listOf("100", "500", "2000", "5000").forEach { valAmount ->
                                Box(
                                    modifier = Modifier
                                        .background(SurfaceVariant, shape = RoundedCornerShape(6.dp))
                                        .clickable { viewModel.depositInputAmount = valAmount }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "+₹$valAmount",
                                        color = TrueGold,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { viewModel.handleDeposit() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = TrueGold,
                                contentColor = DeepForest
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("deposit_sum_button")
                        ) {
                            Text("Confirm Deposit", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Withdrawal Section
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, SurfaceVariant, RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Instant Release to Primary Bank",
                            color = DarkGray,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = viewModel.withdrawInputAmount,
                            onValueChange = { viewModel.withdrawInputAmount = it },
                            label = { Text("Withdraw Amount (₹)") },
                            prefix = { Text("₹", color = TrueGold) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = DarkGray,
                                unfocusedTextColor = DarkGray,
                                focusedBorderColor = TrueGold,
                                unfocusedBorderColor = SurfaceVariant,
                                focusedLabelColor = TrueGold,
                                unfocusedLabelColor = SoftGray
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("withdraw_input")
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { viewModel.handleWithdrawal() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SurfaceVariant,
                                contentColor = TrueGold
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, TrueGold, RoundedCornerShape(8.dp))
                                .testTag("withdraw_sum_button")
                        ) {
                            Text("Confirm Bank Outflow", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Goals Block Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Savings Milestones",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = DarkGray
                    )
                    Text(
                        text = "Track your dynamic visual goals progress",
                        color = SoftGray,
                        fontSize = 11.sp
                    )
                }

                Button(
                    onClick = { showAddGoalForm = !showAddGoalForm },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TrueGold,
                        contentColor = DeepForest
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(
                        imageVector = if (showAddGoalForm) Icons.Default.Close else Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (showAddGoalForm) "Close" else "New Goal", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Create Goal Form Toggle State
        item {
            AnimatedVisibility(
                visible = showAddGoalForm,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, TrueGold, RoundedCornerShape(16.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Define Savings Target Milestone",
                            color = TrueGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )

                        OutlinedTextField(
                            value = viewModel.newGoalTitle,
                            onValueChange = { viewModel.newGoalTitle = it },
                            label = { Text("Goal Title / Target Name") },
                            placeholder = { Text("e.g. MacBook Pro M4") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = DarkGray,
                                unfocusedTextColor = DarkGray,
                                focusedBorderColor = TrueGold,
                                unfocusedBorderColor = SurfaceVariant,
                                focusedLabelColor = TrueGold,
                                unfocusedLabelColor = SoftGray
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("goal_title_input")
                        )

                        OutlinedTextField(
                            value = viewModel.newGoalTarget,
                            onValueChange = { viewModel.newGoalTarget = it },
                            label = { Text("Target Threshold Amount (₹)") },
                            prefix = { Text("₹", color = TrueGold) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = DarkGray,
                                unfocusedTextColor = DarkGray,
                                focusedBorderColor = TrueGold,
                                unfocusedBorderColor = SurfaceVariant,
                                focusedLabelColor = TrueGold,
                                unfocusedLabelColor = SoftGray
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("goal_target_input")
                        )

                        // Category Drop selection chips row
                        Text("Goal Category", color = SoftGray, fontSize = 12.sp)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            listOf("Bike", "Travel", "Electronics", "Gold Coin").forEach { cat ->
                                val selected = viewModel.newGoalCategory == cat
                                Box(
                                    modifier = Modifier
                                        .background(
                                            color = if (selected) TrueGold else SurfaceVariant,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .clickable { viewModel.newGoalCategory = cat }
                                        .padding(horizontal = 14.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = cat,
                                        color = if (selected) DeepForest else TrueGold,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                viewModel.handleAddSavingsGoal()
                                showAddGoalForm = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = TrueGold,
                                contentColor = DeepForest
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("goal_submit_button")
                        ) {
                            Text("Create & Track Milestone", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Goals display
        if (goals.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No milestones configured yet. Tap 'New Goal'!",
                            color = SoftGray,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        } else {
            items(goals) { goal ->
                GoalProgressRow(goal = goal, onDelete = { viewModel.handleDeleteGoal(goal.id) })
            }
        }
    }
}

@Composable
fun GoalProgressRow(
    goal: SavingsGoal,
    onDelete: () -> Unit
) {
    val progressFraction = if (goal.targetAmount <= 0) 0f else (goal.savedAmount / goal.targetAmount).toFloat()
    val percentString = String.format("%.0f%%", progressFraction * 100f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, SurfaceVariant, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(DeepForest, shape = CircleShape)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (goal.category) {
                                "Bike" -> Icons.Default.DirectionsBike
                                "Travel" -> Icons.Default.FlightTakeoff
                                "Electronics" -> Icons.Default.LaptopMac
                                else -> Icons.Default.CardGiftcard
                            },
                            contentDescription = null,
                            tint = TrueGold,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = goal.title,
                            color = DarkGray,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Category: ${goal.category}",
                            color = SoftGray,
                            fontSize = 11.sp
                        )
                    }
                }

                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.6f))
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Progress bar and state numbers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Saved: ₹${goal.savedAmount.toInt()} / ₹${goal.targetAmount.toInt()}",
                    color = SoftGray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = percentString,
                    color = TrueGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { minOf(1.0f, progressFraction) },
                color = TrueGold,
                trackColor = SurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
            )
        }
    }
}
