package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class BudgetPeriod {
    MONTHLY,
    WEEKLY,
    YEARLY,
    CUSTOM
}

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val amount: Double,
    val categoryId: Long? = null, // null means overall budget
    val period: BudgetPeriod = BudgetPeriod.MONTHLY,
    val warningThresholdPercent: Int = 80,
    val startDateMillis: Long = 0,
    val endDateMillis: Long = 0,
    val createdAt: Long = System.currentTimeMillis()
)
