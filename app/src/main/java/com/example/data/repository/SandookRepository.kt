package com.example.data.repository

import com.example.data.dao.SandookDao
import com.example.data.entity.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.util.Calendar

class SandookRepository(private val sandookDao: SandookDao) {

    val userProfile: Flow<UserProfile?> = sandookDao.getUserProfile()
    val allPlans: Flow<List<SavingsPlan>> = sandookDao.getAllSavingsPlans()
    val allGoals: Flow<List<SavingsGoal>> = sandookDao.getAllSavingsGoals()
    val allTransactions: Flow<List<WalletTransaction>> = sandookDao.getAllTransactions()
    val allNotifications: Flow<List<AppNotification>> = sandookDao.getAllNotifications()

    suspend fun getOrCreateProfile(): UserProfile {
        val existing = sandookDao.getUserProfileSynchronous()
        if (existing != null) return existing
        val defaultProfile = UserProfile()
        sandookDao.insertOrUpdateUserProfile(defaultProfile)
        return defaultProfile
    }

    suspend fun updateProfile(profile: UserProfile) {
        sandookDao.insertOrUpdateUserProfile(profile)
    }

    suspend fun addSavingsPlan(name: String, frequency: String, amount: Double, isAutoPay: Boolean) {
        val nextDate = calculateNextDeduction(frequency)
        val plan = SavingsPlan(
            name = name,
            frequency = frequency,
            amount = amount,
            isAutoPayEnabled = isAutoPay,
            nextDeductionTime = nextDate
        )
        sandookDao.insertSavingsPlan(plan)
        
        // Push notification
        val freqLabel = frequency.lowercase().replaceFirstChar { it.uppercase() }
        addNotification(
            "New Savings Plan Created",
            "Your $freqLabel automated savings of ₹${amount.toInt()} for '$name' is active!"
        )
    }

    suspend fun togglePlanStatus(plan: SavingsPlan) {
        val updated = plan.copy(isActive = !plan.isActive)
        sandookDao.updateSavingsPlan(updated)
        val statusText = if (updated.isActive) "activated" else "paused"
        addNotification(
            "Savings Plan Status Changed",
            "Savings plan '${plan.name}' is now $statusText."
        )
    }

    suspend fun deletePlan(planId: Int) {
        sandookDao.deleteSavingsPlanById(planId)
    }

    suspend fun addSavingsGoal(title: String, targetAmount: Double, category: String) {
        val goal = SavingsGoal(
            title = title,
            targetAmount = targetAmount,
            category = category
        )
        sandookDao.insertSavingsGoal(goal)
        addNotification(
            "New Target Created",
            "Saved target of ₹${targetAmount.toInt()} for '$title' ($category) is set up successfully!"
        )
    }

    suspend fun deleteGoal(goalId: Int) {
        sandookDao.deleteSavingsGoalById(goalId)
    }

    suspend fun addNotification(title: String, message: String) {
        sandookDao.insertNotification(AppNotification(title = title, message = message))
    }

    suspend fun markNotificationsRead() {
        sandookDao.markAllAsRead()
    }

    // High fidelity simulator: execute deposit transaction
    suspend fun depositToWallet(amount: Double) {
        val profile = getOrCreateProfile()
        val updatedProfile = profile.copy(walletBalance = profile.walletBalance + amount)
        sandookDao.insertOrUpdateUserProfile(updatedProfile)

        // Insert Transaction log
        val tx = WalletTransaction(
            description = "Wallet Deposit",
            amount = amount,
            type = "DEPOSIT"
        )
        sandookDao.insertTransaction(tx)

        // Notification
        addNotification(
            "Deposit Successful",
            "Credited ₹${amount.toInt()} into your Sandook Savings Wallet."
        )

        // Try to allocate this deposit towards visual goals
        allocateTowardsGoals(amount)
    }

    suspend fun withdrawFromWallet(amount: Double): Boolean {
        val profile = getOrCreateProfile()
        if (profile.walletBalance < amount) return false

        val updatedProfile = profile.copy(walletBalance = profile.walletBalance - amount)
        sandookDao.insertOrUpdateUserProfile(updatedProfile)

        // Transaction Log
        val tx = WalletTransaction(
            description = "Sandook Withdrawal",
            amount = amount,
            type = "WITHDRAWAL"
        )
        sandookDao.insertTransaction(tx)

        // Notification
        addNotification(
            "Withdrawal Processed",
            "Debited ₹${amount.toInt()} successfully from your Sandook Savings Wallet."
        )
        return true
    }

    // Dynamic simulator logic: AutoPay simulation execution for the user
    suspend fun triggerAutoPaySimulate() {
        val activePlans = sandookDao.getActiveSavingsPlans()
        if (activePlans.isEmpty()) return

        var totalDeduction = 0.0

        for (plan in activePlans) {
            // Force deduction in simulation
            totalDeduction += plan.amount
            
            // Advance next deduction date
            val nextTime = calculateNextDeduction(plan.frequency)
            sandookDao.updateSavingsPlan(plan.copy(nextDeductionTime = nextTime))

            // Transaction log
            val tx = WalletTransaction(
                description = "AutoPay: ${plan.name}",
                amount = plan.amount,
                type = "AUTOPAY_DEDUCTION"
            )
            sandookDao.insertTransaction(tx)

            // Individual savings alert
            addNotification(
                "Savings Automated Successfully",
                "UPI AutoPay saved ₹${plan.amount.toInt()} towards '${plan.name}'!"
            )
        }

        if (totalDeduction > 0.0) {
            val profile = getOrCreateProfile()
            val updatedProfile = profile.copy(walletBalance = profile.walletBalance + totalDeduction)
            sandookDao.insertOrUpdateUserProfile(updatedProfile)
            
            // Allocate saved funds towards unfinished goals
            allocateTowardsGoals(totalDeduction)
        }
    }

    private suspend fun allocateTowardsGoals(amount: Double) {
        val goals = sandookDao.getAllSavingsGoals().firstOrNull() ?: emptyList()
        if (goals.isEmpty()) return

        var remaining = amount
        for (goal in goals) {
            val needed = goal.targetAmount - goal.savedAmount
            if (needed > 0 && remaining > 0) {
                val allocate = minOf(needed, remaining)
                val updatedGoal = goal.copy(savedAmount = goal.savedAmount + allocate)
                sandookDao.updateSavingsGoal(updatedGoal)
                remaining -= allocate

                if (updatedGoal.savedAmount >= updatedGoal.targetAmount) {
                    addNotification(
                        "Goal Accomplished! 🎉",
                        "Incredible! You have saved 100% of your ₹${goal.targetAmount.toInt()} goal for '${goal.title}'!"
                    )
                }
            }
        }
    }

    private fun calculateNextDeduction(frequency: String): Long {
        val cal = Calendar.getInstance()
        when (frequency) {
            "DAILY" -> cal.add(Calendar.DAY_OF_YEAR, 1)
            "WEEKLY" -> cal.add(Calendar.WEEK_OF_YEAR, 1)
            "MONTHLY" -> cal.add(Calendar.MONTH, 1)
            else -> cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        return cal.timeInMillis
    }
}
