package com.example.data.dao

import androidx.room.*
import com.example.data.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SandookDao {
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun getUserProfileSynchronous(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUserProfile(profile: UserProfile)

    @Query("SELECT * FROM savings_plan ORDER BY createdAt DESC")
    fun getAllSavingsPlans(): Flow<List<SavingsPlan>>

    @Query("SELECT * FROM savings_plan WHERE isActive = 1")
    suspend fun getActiveSavingsPlans(): List<SavingsPlan>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavingsPlan(plan: SavingsPlan)

    @Update
    suspend fun updateSavingsPlan(plan: SavingsPlan)

    @Delete
    suspend fun deleteSavingsPlan(plan: SavingsPlan)

    @Query("DELETE FROM savings_plan WHERE id = :id")
    suspend fun deleteSavingsPlanById(id: Int)

    @Query("SELECT * FROM savings_goal ORDER BY createdAt DESC")
    fun getAllSavingsGoals(): Flow<List<SavingsGoal>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavingsGoal(goal: SavingsGoal)

    @Update
    suspend fun updateSavingsGoal(goal: SavingsGoal)

    @Query("DELETE FROM savings_goal WHERE id = :id")
    suspend fun deleteSavingsGoalById(id: Int)

    @Query("SELECT * FROM wallet_transaction ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<WalletTransaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: WalletTransaction)

    @Query("SELECT * FROM app_notification ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<AppNotification>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: AppNotification)

    @Query("UPDATE app_notification SET isRead = 1 WHERE isRead = 0")
    suspend fun markAllAsRead()
    
    // Admin raw analytics query support
    @Query("SELECT SUM(amount) FROM wallet_transaction WHERE type = 'AUTOPAY_DEDUCTION'")
    fun getTotalAutoPaySavings(): Flow<Double?>
}
