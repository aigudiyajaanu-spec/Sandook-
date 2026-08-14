package com.example.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.entity.*
import com.example.data.repository.SandookRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SandookViewModel(private val repository: SandookRepository) : ViewModel() {

    // Database Reactive Flows
    val userProfile: StateFlow<UserProfile?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allPlans: StateFlow<List<SavingsPlan>> = repository.allPlans
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allGoals: StateFlow<List<SavingsGoal>> = repository.allGoals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTransactions: StateFlow<List<WalletTransaction>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allNotifications: StateFlow<List<AppNotification>> = repository.allNotifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI Transient States
    var showBalance by mutableStateOf(true)
    var isPinVerified by mutableStateOf(false)
    var transientMessage by mutableStateOf<String?>(null)

    // Form states for login OTP
    var loginPhoneInput by mutableStateOf("")
    var loginOtpInput by mutableStateOf("")
    var loginError by mutableStateOf<String?>(null)
    var otpSent by mutableStateOf(false)
    var verificationCode by mutableStateOf("1234") // Predefined mock OTP code

    // Form States for Goals setup
    var newGoalTitle by mutableStateOf("")
    var newGoalTarget by mutableStateOf("")
    var newGoalCategory by mutableStateOf("Travel")

    // Form States for Plans setup
    var newPlanTitle by mutableStateOf("")
    var newPlanAmount by mutableStateOf("")
    var newPlanFrequency by mutableStateOf("DAILY")
    var upiAutoPayEnabled by mutableStateOf(true)

    // Manual Transactions Form
    var depositInputAmount by mutableStateOf("")
    var withdrawInputAmount by mutableStateOf("")

    // Security Setup Forms
    var registerPinInput by mutableStateOf("")
    var currentPinInput by mutableStateOf("")

    // Admin Panel Mock Users Sandbox
    val mockUsers = mutableStateListOf(
        MockUser(101, "Aarif Khan", "9988776655", "Active", 12450.0),
        MockUser(102, "Sneha Sharma", "9122334455", "Active", 890.0),
        MockUser(103, "Dinesh Kumar", "9456712390", "Suspended", 32000.0),
        MockUser(104, "Robert D'souza", "8787878787", "Active", 54300.0)
    )

    init {
        // Prepare default mock profile on startup if empty
        viewModelScope.launch {
            repository.getOrCreateProfile()
            // Add custom welcome notifications for high fidelity feel on clean starts
            val emptyNotif = repository.allNotifications.firstOrNull()?.isEmpty() ?: true
            if (emptyNotif) {
                repository.addNotification(
                    "Swagat Hai! 👋 Welcome to Sandook",
                    "Sandook personal automated savings is ready. Tap 'Add Savings Plan' to schedule daily, weekly or monthly savings securely!"
                )
                // Add some default visual goals to look spectacular on first open
                repository.addSavingsGoal("Royal Enfield Hunter 350", 185000.0, "Bike")
                repository.addSavingsGoal("Gold Coin Investment", 64000.0, "Investment")
            }
        }
    }

    // OTP Simulated Authentication Flow
    fun sendOtpSimulate() {
        if (loginPhoneInput.length < 10) {
            loginError = "Please enter a valid 10-digit mobile number."
            return
        }
        loginError = null
        otpSent = true
        showPopupMessage("OTP Sent successfully to +91 $loginPhoneInput. Enter 1234 to log in!")
    }

    fun verifyOtpSimulate(onSuccess: () -> Unit) {
        if (loginOtpInput == "1234" || loginOtpInput == "0000") {
            loginError = null
            viewModelScope.launch {
                val profile = repository.getOrCreateProfile()
                repository.updateProfile(profile.copy(phoneNumber = loginPhoneInput, isLoggedIn = true))
                onSuccess()
            }
        } else {
            loginError = "Incorrect OTP code. Enter '1234' for simulator login."
        }
    }

    fun googleSignInSimulate(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val profile = repository.getOrCreateProfile()
            repository.updateProfile(
                profile.copy(
                    fullName = "All Backup Get Scam", // Matches user metadata context
                    phoneNumber = "9876543210",
                    isLoggedIn = true
                )
            )
            repository.addNotification("Google Authentication Enabled", "Welcome back, All Backup Get Scam!")
            onSuccess()
        }
    }

    // Sign out Flow
    fun handleSignOut(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            val profile = repository.getOrCreateProfile()
            repository.updateProfile(profile.copy(isLoggedIn = false))
            isPinVerified = false
            otpSent = false
            loginPhoneInput = ""
            loginOtpInput = ""
            onLoggedOut()
        }
    }

    // Secure PIN Passcode Locks
    fun handleSaveNewPin(onCompleted: () -> Unit) {
        if (registerPinInput.length != 4) {
            showPopupMessage("Security PIN must be exactly 4 digits.")
            return
        }
        viewModelScope.launch {
            val profile = repository.getOrCreateProfile()
            repository.updateProfile(profile.copy(pinCode = registerPinInput))
            isPinVerified = true
            showPopupMessage("Security PIN passcode matches! Lock enabled.")
            registerPinInput = ""
            onCompleted()
        }
    }

    fun handleVerifyPin(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val profile = repository.getOrCreateProfile()
            if (profile.pinCode == currentPinInput) {
                isPinVerified = true
                onSuccess()
                currentPinInput = ""
            } else {
                showPopupMessage("Incorrect 4-digit security PIN passcode.")
            }
        }
    }

    fun toggleBiometrics(enabled: Boolean) {
        viewModelScope.launch {
            val profile = repository.getOrCreateProfile()
            repository.updateProfile(profile.copy(isBiometricsEnabled = enabled))
            val status = if (enabled) "Biometric Fingerprint unlock active" else "Biometrics disabled"
            showPopupMessage(status)
        }
    }

    // Savings Plan Management
    fun handleAddSavingsPlan() {
        val amountNum = newPlanAmount.toDoubleOrNull()
        if (newPlanTitle.isBlank() || amountNum == null || amountNum <= 0) {
            showPopupMessage("Please enter a valid plan name & custom savings amount.")
            return
        }
        viewModelScope.launch {
            repository.addSavingsPlan(
                name = newPlanTitle,
                frequency = newPlanFrequency,
                amount = amountNum,
                isAutoPay = upiAutoPayEnabled
            )
            newPlanTitle = ""
            newPlanAmount = ""
            showPopupMessage("Savings Plan Added successfully with UPI AutoPay mandate!")
        }
    }

    fun handleTogglePlan(plan: SavingsPlan) {
        viewModelScope.launch {
            repository.togglePlanStatus(plan)
        }
    }

    fun handleDeletePlan(planId: Int) {
        viewModelScope.launch {
            repository.deletePlan(planId)
            showPopupMessage("Savings Plan cancelled successfully.")
        }
    }

    // Savings Goals Management
    fun handleAddSavingsGoal() {
        val targetNum = newGoalTarget.toDoubleOrNull()
        if (newGoalTitle.isBlank() || targetNum == null || targetNum <= 0) {
            showPopupMessage("Please enter a valid savings target name limit & amount.")
            return
        }
        viewModelScope.launch {
            repository.addSavingsGoal(
                title = newGoalTitle,
                targetAmount = targetNum,
                category = newGoalCategory
            )
            newGoalTitle = ""
            newGoalTarget = ""
            showPopupMessage("New visual target goals created!")
        }
    }

    fun handleDeleteGoal(goalId: Int) {
        viewModelScope.launch {
            repository.deleteGoal(goalId)
            showPopupMessage("Savings goal removed.")
        }
    }

    // Deposit or Withdrawal
    fun handleDeposit() {
        val amountNum = depositInputAmount.toDoubleOrNull()
        if (amountNum == null || amountNum <= 0) {
            showPopupMessage("Please input a valid positive amount to deposit.")
            return
        }
        viewModelScope.launch {
            repository.depositToWallet(amountNum)
            depositInputAmount = ""
            showPopupMessage("₹${amountNum.toInt()} successfully deposited to savings box!")
        }
    }

    fun handleWithdrawal() {
        val amountNum = withdrawInputAmount.toDoubleOrNull()
        if (amountNum == null || amountNum <= 0) {
            showPopupMessage("Please input a valid positive amount to withdraw.")
            return
        }
        viewModelScope.launch {
            val success = repository.withdrawFromWallet(amountNum)
            if (success) {
                withdrawInputAmount = ""
                showPopupMessage("₹${amountNum.toInt()} successfully moved to primary bank account.")
            } else {
                showPopupMessage("Insufficient Sandook wallet savings balance.")
            }
        }
    }

    // AutoPay Simulator Engine
    fun handleTriggerAutoPaySimulation() {
        viewModelScope.launch {
            repository.triggerAutoPaySimulate()
            showPopupMessage("AutoPay background scheduler executed of-date plans!")
        }
    }

    // Toggle Premium Pro Support (₹1 per month)
    fun handleUnlockPremiumPro() {
        viewModelScope.launch {
            val profile = repository.getOrCreateProfile()
            repository.updateProfile(profile.copy(isPremium = true))
            
            // Insert ₹1 transaction record
            repository.addNotification("Sandook PRO Enabled 👑", "Congratulations! You are now a Premium Member of Sandook. Enjoy premium financial tools ad-free.")
            showPopupMessage("₹1 payment confirmed! Sandook Premium Activated.")
        }
    }

    fun handleCancelPremiumPro() {
        viewModelScope.launch {
            val profile = repository.getOrCreateProfile()
            repository.updateProfile(profile.copy(isPremium = false))
            repository.addNotification("Subscription Cancelled", "Sandook PRO gold benefits paused.")
            showPopupMessage("Sandook Pro Gold subscription cancelled successfully.")
        }
    }

    // Clear notifications logs list
    fun handleClearNotifications() {
        viewModelScope.launch {
            repository.markNotificationsRead()
            showPopupMessage("Notifications cleared.")
        }
    }

    // Toggle simulation active account ban status (Admin simulator metrics)
    fun toggleAdminUserStatus(userId: Int) {
        val index = mockUsers.indexOfFirst { it.id == userId }
        if (index != -1) {
            val user = mockUsers[index]
            val newStatus = if (user.status == "Active") "Suspended" else "Active"
            mockUsers[index] = user.copy(status = newStatus)
            showPopupMessage("Sandbox User ${user.name} state set to $newStatus.")
        }
    }

    fun showPopupMessage(msg: String) {
        transientMessage = msg
    }

    fun clearPopupMessage() {
        transientMessage = null
    }
}

// Custom data holder class for the Admin Console simulator list
data class MockUser(
    val id: Int,
    val name: String,
    val phone: String,
    val status: String,
    val totalSaved: Double
)

class SandookViewModelFactory(private val repository: SandookRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SandookViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SandookViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
