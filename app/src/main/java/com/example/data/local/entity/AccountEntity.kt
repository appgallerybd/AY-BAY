package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class AccountType {
    CASH,
    BANK,
    MOBILE_WALLET, // bKash, Nagad, Rocket
    CREDIT_CARD,
    SAVINGS,
    OTHER
}

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val type: AccountType,
    val balance: Double = 0.0,
    val initialBalance: Double = 0.0,
    val currency: String = "BDT",
    val maskedNumber: String = "",
    val colorHex: String = "#00695C",
    val iconName: String = "account_balance_wallet",
    val isArchived: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
