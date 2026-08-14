package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1, // Always 1 for single-user profile
    val phoneNumber: String = "",
    val fullName: String = "",
    val pinCode: String = "", // Secure PIN lock
    val isBiometricsEnabled: Boolean = false,
    val isPremium: Boolean = false,
    val walletBalance: Double = 0.0,
    val isLoggedIn: Boolean = false
)

@Entity(tableName = "savings_plan")
data class SavingsPlan(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String, // e.g., "Daily Coffee Cache", "Monthly Rent Reserve"
    val frequency: String, // "DAILY", "WEEKLY", "MONTHLY"
    val amount: Double,
    val isActive: Boolean = true,
    val isAutoPayEnabled: Boolean = true,
    val nextDeductionTime: Long, // timestamp
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "savings_goal")
data class SavingsGoal(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val targetAmount: Double,
    val savedAmount: Double = 0.0,
    val category: String = "General", // "Travel", "Gadget", "Emergency", etc.
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "wallet_transaction")
data class WalletTransaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val description: String,
    val amount: Double,
    val type: String, // "DEPOSIT", "WITHDRAWAL", "AUTOPAY_DEDUCTION"
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "SUCCESS" // "SUCCESS", "PENDING"
)

@Entity(tableName = "app_notification")
data class AppNotification(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)
