package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.SandookViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AuthScreen(
    viewModel: SandookViewModel,
    onAuthSuccess: () -> Unit
) {
    val profile by viewModel.userProfile.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(DarkBackground, DeepForest)
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Sandook Title / Logo Icon
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .background(TrueGold, shape = RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Savings,
                        contentDescription = "Sandook Vault",
                        tint = DeepForest,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "SANDOOK",
                        style = MaterialTheme.typography.headlineMedium,
                        color = TrueGold,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "Your Secure Savings Box",
                        color = SoftGray,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            AnimatedContent(
                targetState = profile,
                label = "AuthStates"
            ) { state ->
                if (state == null) {
                    Box(modifier = Modifier.size(40.dp)) {
                        CircularProgressIndicator(color = TrueGold)
                    }
                } else if (!state.isLoggedIn) {
                    // Registration / Login Stage
                    LoginCard(viewModel, onAuthSuccess)
                } else if (state.pinCode.isEmpty()) {
                    // Force set 4-Digit Security PIN
                    CreatePinCard(viewModel, onAuthSuccess)
                } else if (!viewModel.isPinVerified) {
                    // Enter existing PIN passcode
                    VerifyPinCard(viewModel, onAuthSuccess)
                }
            }
        }
    }
}

@Composable
fun LoginCard(
    viewModel: SandookViewModel,
    onAuthSuccess: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, SurfaceVariant, RoundedCornerShape(20.dp))
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Log In to Your sandook",
                style = MaterialTheme.typography.titleLarge,
                color = DarkGray,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            Text(
                text = "OTP Login secure authentication",
                color = SoftGray,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )

            // Phone Input
            OutlinedTextField(
                value = viewModel.loginPhoneInput,
                onValueChange = { if (it.length <= 10) viewModel.loginPhoneInput = it },
                label = { Text("Mobile Number") },
                placeholder = { Text("10-digit number") },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = TrueGold) },
                prefix = { Text("+91 ", color = TrueGold, fontWeight = FontWeight.Bold) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TrueGold,
                    unfocusedBorderColor = SurfaceVariant,
                    focusedLabelColor = TrueGold,
                    unfocusedLabelColor = SoftGray,
                    focusedTextColor = DarkGray,
                    unfocusedTextColor = DarkGray
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("phone_input")
            )

            // OTP Input
            if (viewModel.otpSent) {
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = viewModel.loginOtpInput,
                    onValueChange = { if (it.length <= 4) viewModel.loginOtpInput = it },
                    label = { Text("Verification OTP Code") },
                    placeholder = { Text("Enter 1234") },
                    leadingIcon = { Icon(Icons.Default.LockOpen, contentDescription = null, tint = TrueGold) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TrueGold,
                        unfocusedBorderColor = SurfaceVariant,
                        focusedLabelColor = TrueGold,
                        unfocusedLabelColor = SoftGray,
                        focusedTextColor = DarkGray,
                        unfocusedTextColor = DarkGray
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("otp_input")
                )
                Text(
                    text = "Demo Verification Code inside simulator is: 1234",
                    color = TrueGold.copy(alpha = 0.8f),
                    fontSize = 11.sp,
                    modifier = Modifier
                        .padding(top = 6.dp)
                        .align(Alignment.Start)
                )
            }

            viewModel.loginError?.let { err ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = err,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action OTP trigger Button
            Button(
                onClick = {
                    if (viewModel.otpSent) {
                        viewModel.verifyOtpSimulate(onAuthSuccess)
                    } else {
                        viewModel.sendOtpSimulate()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = TrueGold,
                    contentColor = DeepForest
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("login_action_button")
            ) {
                Text(
                    text = if (viewModel.otpSent) "Verify OTP & Continue" else "Send Mobile OTP",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = SurfaceVariant)
                Text(" OR ", color = SoftGray, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 12.dp))
                HorizontalDivider(modifier = Modifier.weight(1f), color = SurfaceVariant)
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Google Sign In simulated visualizer
            Surface(
                onClick = { viewModel.googleSignInSimulate(onAuthSuccess) },
                color = DeepForest,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, SurfaceVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("google_login_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBalance,
                        contentDescription = "Google Sign In logo mock",
                        tint = TrueGold,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Sign In with Google",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
fun CreatePinCard(
    viewModel: SandookViewModel,
    onAuthSuccess: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, SurfaceVariant, RoundedCornerShape(20.dp))
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = null,
                tint = TrueGold,
                modifier = Modifier
                    .size(64.dp)
                    .padding(bottom = 12.dp)
            )
            Text(
                text = "Setup Security PIN",
                style = MaterialTheme.typography.titleLarge,
                color = DarkGray,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Create a 4-Digit lock code to secure your savings wallet vault.",
                color = SoftGray,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )

            OutlinedTextField(
                value = viewModel.registerPinInput,
                onValueChange = { if (it.length <= 4 && it.all { char -> char.isDigit() }) viewModel.registerPinInput = it },
                label = { Text("New Passcode PIN") },
                placeholder = { Text("e.g. 5555") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                leadingIcon = { Icon(Icons.Default.Pin, contentDescription = null, tint = TrueGold) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TrueGold,
                    unfocusedBorderColor = SurfaceVariant,
                    focusedLabelColor = TrueGold,
                    unfocusedLabelColor = SoftGray,
                    focusedTextColor = DarkGray,
                    unfocusedTextColor = DarkGray
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("setup_pin_input")
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.handleSaveNewPin(onAuthSuccess) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = TrueGold,
                    contentColor = DeepForest
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("save_pin_button")
            ) {
                Text(
                    text = "Save & Unlock Sandook",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun VerifyPinCard(
    viewModel: SandookViewModel,
    onAuthSuccess: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, SurfaceVariant, RoundedCornerShape(20.dp))
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = TrueGold,
                modifier = Modifier
                    .size(64.dp)
                    .padding(bottom = 12.dp)
            )
            Text(
                text = "Enterprise Locker active",
                style = MaterialTheme.typography.titleLarge,
                color = DarkGray,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Enter your 4-digit security PIN to access the Sandook dashboard.",
                color = SoftGray,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )

            OutlinedTextField(
                value = viewModel.currentPinInput,
                onValueChange = { if (it.length <= 4 && it.all { char -> char.isDigit() }) viewModel.currentPinInput = it },
                label = { Text("Security PIN Code") },
                placeholder = { Text("••••") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                leadingIcon = { Icon(Icons.Default.Pin, contentDescription = null, tint = TrueGold) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TrueGold,
                    unfocusedBorderColor = SurfaceVariant,
                    focusedLabelColor = TrueGold,
                    unfocusedLabelColor = SoftGray,
                    focusedTextColor = DarkGray,
                    unfocusedTextColor = DarkGray
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("verify_pin_input")
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.handleVerifyPin(onAuthSuccess) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = TrueGold,
                    contentColor = DeepForest
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("verify_pin_button")
            ) {
                Text(
                    text = "Verify & Access Wallet",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quick bypass click link
            Text(
                text = "Simulate Biometric Unlock",
                color = TrueGold,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .clickable {
                        viewModel.isPinVerified = true
                        onAuthSuccess()
                        viewModel.showPopupMessage("Biometric verified. Access Granted!")
                    }
                    .padding(8.dp)
            )
        }
    }
}
