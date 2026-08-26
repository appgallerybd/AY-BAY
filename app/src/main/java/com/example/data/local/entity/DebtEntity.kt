package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class DebtType {
    I_OWE,    // Payable
    OWES_ME   // Receivable
}

@Entity(tableName = "debts")
data class DebtEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val personName: String,
    val amount: Double,
    val paidAmount: Double = 0.0,
    val type: DebtType,
    val dueDateMillis: Long = 0,
    val note: String = "",
    val isSettled: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
