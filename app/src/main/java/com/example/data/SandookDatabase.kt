package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.SandookDao
import com.example.data.entity.*

@Database(
    entities = [
        UserProfile::class, 
        SavingsPlan::class, 
        SavingsGoal::class, 
        WalletTransaction::class, 
        AppNotification::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SandookDatabase : RoomDatabase() {
    abstract fun sandookDao(): SandookDao

    companion object {
        @Volatile
        private var INSTANCE: SandookDatabase? = null

        fun getDatabase(context: Context): SandookDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SandookDatabase::class.java,
                    "sandook_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
