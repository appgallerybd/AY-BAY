package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

enum class TransactionType {
    INCOME,
    EXPENSE,
    TRANSFER,
    REFUND,
    ADJUSTMENT
}

@Entity(
    tableName = "transactions",
    indices = [
        Index("dateMillis"),
        Index("type"),
        Index("categoryId"),
        Index("accountId")
    ]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val type: TransactionType,
    val categoryId: Long,
    val accountId: Long,
    val targetAccountId: Long? = null, // Used for transfers
    val dateMillis: Long,
    val note: String = "",
    val paymentMethod: String = "",
    val tags: String = "", // Comma-separated
    val personName: String = "",
    val referenceNumber: String = "",
    val isRecurring: Boolean = false,
    val receiptUri: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
