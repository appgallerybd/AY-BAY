package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class RecurrenceFrequency {
    DAILY,
    WEEKLY,
    MONTHLY,
    YEARLY
}

@Entity(tableName = "recurring_transactions")
data class RecurringTransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val amount: Double,
    val type: TransactionType = TransactionType.EXPENSE,
    val categoryId: Long,
    val accountId: Long,
    val frequency: RecurrenceFrequency = RecurrenceFrequency.MONTHLY,
    val nextDueDateMillis: Long,
    val isPaused: Boolean = false,
    val autoCreate: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
