package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.data.entity.SavingsPlan
import com.example.ui.theme.*
import com.example.ui.viewmodel.SandookViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AutoPayPlansScreen(
    viewModel: SandookViewModel
) {
    val plans by viewModel.allPlans.collectAsState()

    var showConsentDialog by remember { mutableStateOf(false) }
    var selectedBankForMandate by remember { mutableStateOf("State Bank of India (UPI)") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
    ) {
        // Plans scheduler title intro
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .border(1.dp, SurfaceVariant, RoundedCornerShape(16.dp))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(DeepForest, shape = RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Schedule, contentDescription = null, tint = TrueGold)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Save Without Thinking 🚀",
                            color = DarkGray,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Automate with Razorpay secure UPI mandates.",
                            color = SoftGray,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // New Plan Creator Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SurfaceVariant, RoundedCornerShape(16.dp))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Build Automated Savings Plan",
                        color = TrueGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )

                    OutlinedTextField(
                        value = viewModel.newPlanTitle,
                        onValueChange = { viewModel.newPlanTitle = it },
                        label = { Text("Savings Plan Title") },
                        placeholder = { Text("e.g. Dream Venture, Rent Backup") },
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
                            .testTag("plan_title_input")
                    )

                    OutlinedTextField(
                        value = viewModel.newPlanAmount,
                        onValueChange = { viewModel.newPlanAmount = it },
                        label = { Text("Automated Amount to Save (₹)") },
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
                            .testTag("plan_amount_input")
                    )

                    // Frequency selection chip row
                    Text("Select Saving Schedule Frequency", color = SoftGray, fontSize = 11.sp)
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf("DAILY", "WEEKLY", "MONTHLY").forEach { freq ->
                            val selected = viewModel.newPlanFrequency == freq
                            val label = freq.lowercase().replaceFirstChar { it.uppercase() }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 4.dp)
                                    .background(
                                        color = if (selected) TrueGold else SurfaceVariant,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { viewModel.newPlanFrequency = freq }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    color = if (selected) DeepForest else TrueGold,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // UPI AutoPay Mandate toggle description
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        Checkbox(
                            checked = viewModel.upiAutoPayEnabled,
                            onCheckedChange = { viewModel.upiAutoPayEnabled = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = TrueGold,
                                checkmarkColor = DeepForest
                            )
                        )
                        Column(modifier = Modifier.padding(start = 8.dp)) {
                            Text(
                                text = "Authorize UPI AutoPay Mandate",
                                color = DarkGray,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "Instantly register automated transfers via UPI secure vault.",
                                color = SoftGray,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(
                        onClick = {
                            if (viewModel.upiAutoPayEnabled) {
                                showConsentDialog = true
                            } else {
                                viewModel.handleAddSavingsPlan()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TrueGold,
                            contentColor = DeepForest
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("plan_submit_button")
                    ) {
                        Text(
                            text = if (viewModel.upiAutoPayEnabled) "Authorize Mandate via Razorpay" else "Save Instantly",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Active scheduled planes list header
        item {
            Column {
                Text(
                    text = "Scheduled Savings Mandates",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = DarkGray
                )
                Text(
                    text = "Toggle, pause, or remove recurring schedules",
                    color = SoftGray,
                    fontSize = 11.sp
                )
            }
        }

        if (plans.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Zero automated savings. Configure your first mandate above!",
                            color = SoftGray,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        } else {
            items(plans) { plan ->
                SavingsPlanRow(
                    plan = plan,
                    onToggleActive = { viewModel.handleTogglePlan(plan) },
                    onDelete = { viewModel.handleDeletePlan(plan.id) }
                )
            }
        }
    }

    // Razorpay UPI AutoPay simulated Consent dialog
    if (showConsentDialog) {
        AlertDialog(
            onDismissRequest = { showConsentDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = TrueGold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Razorpay Secure AutoPay", color = DarkGray, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "You are setting up a secure UPI AutoPay recurrent saving mandate for ₹${viewModel.newPlanAmount} scheduled ${viewModel.newPlanFrequency}.",
                        color = SoftGray,
                        fontSize = 13.sp
                    )

                    // Bank simulator selector options
                    Text("Select Linked Bank Account", color = TrueGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    listOf("State Bank of India (UPI)", "HDFC Bank (UPI)", "ICICI Pay (UPI)").forEach { bank ->
                        val active = selectedBankForMandate == bank
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedBankForMandate = bank }
                                .background(
                                    color = if (active) SurfaceVariant else Color.Transparent,
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .padding(vertical = 8.dp, horizontal = 12.dp)
                        ) {
                            Icon(
                                imageVector = if (active) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                                contentDescription = null,
                                tint = if (active) TrueGold else SoftGray,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(bank, color = DarkGray, fontSize = 13.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "✓ Powered by Razorpay Compliance & NPCI Mandate Guidelines.",
                        color = MintGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.handleAddSavingsPlan()
                        showConsentDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TrueGold, contentColor = DeepForest)
                ) {
                    Text("Authorize Mandate OTP", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showConsentDialog = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = SoftGray)
                ) {
                    Text("Cancel Registration")
                }
            },
            containerColor = DarkSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun SavingsPlanRow(
    plan: SavingsPlan,
    onToggleActive: () -> Unit,
    onDelete: () -> Unit
) {
    val formatter = remember { SimpleDateFormat("dd MMM, yyyy", Locale.getDefault()) }
    val formattedNextTime = formatter.format(Date(plan.nextDeductionTime))

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
                            .background(DeepForest, shape = RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Autorenew, contentDescription = null, tint = TrueGold)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = plan.name,
                            color = DarkGray,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "${plan.frequency} auto transfers active",
                            color = TrueGold,
                            fontSize = 11.sp
                        )
                    }
                }

                // Delete Action
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.6f))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress status variables
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Deduction amount", color = SoftGray, fontSize = 10.sp)
                    Text("₹${plan.amount.toInt()}", color = MintGreen, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }

                Column {
                    Text("Next Saving Date", color = SoftGray, fontSize = 10.sp)
                    Text(formattedNextTime, color = DarkGray, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = if (plan.isActive) "Active" else "Paused",
                        color = if (plan.isActive) MintGreen else SoftGray,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Switch(
                        checked = plan.isActive,
                        onCheckedChange = { onToggleActive() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = DeepForest,
                            checkedTrackColor = MintGreen,
                            uncheckedThumbColor = SoftGray,
                            uncheckedTrackColor = SurfaceVariant
                        )
                    )
                }
            }
        }
    }
}
