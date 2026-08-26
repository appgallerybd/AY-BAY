package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "savings_goals")
data class SavingsGoalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val targetAmount: Double,
    val currentAmount: Double = 0.0,
    val deadlineMillis: Long,
    val iconName: String = "flag",
    val colorHex: String = "#00897B",
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
