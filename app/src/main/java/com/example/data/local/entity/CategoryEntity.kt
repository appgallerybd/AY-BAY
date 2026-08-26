package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class CategoryType {
    INCOME,
    EXPENSE
}

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val nameBn: String,
    val type: CategoryType,
    val iconName: String,
    val colorHex: String,
    val isDefault: Boolean = true,
    val isCustom: Boolean = false,
    val sortOrder: Int = 0
)
