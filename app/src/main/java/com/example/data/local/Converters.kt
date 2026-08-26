package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.local.entity.*

class Converters {
    @TypeConverter
    fun fromAccountType(value: AccountType): String = value.name

    @TypeConverter
    fun toAccountType(value: String): AccountType = try {
        AccountType.valueOf(value)
    } catch (e: Exception) {
        AccountType.CASH
    }

    @TypeConverter
    fun fromCategoryType(value: CategoryType): String = value.name

    @TypeConverter
    fun toCategoryType(value: String): CategoryType = try {
        CategoryType.valueOf(value)
    } catch (e: Exception) {
        CategoryType.EXPENSE
    }

    @TypeConverter
    fun fromTransactionType(value: TransactionType): String = value.name

    @TypeConverter
    fun toTransactionType(value: String): TransactionType = try {
        TransactionType.valueOf(value)
    } catch (e: Exception) {
        TransactionType.EXPENSE
    }

    @TypeConverter
    fun fromBudgetPeriod(value: BudgetPeriod): String = value.name

    @TypeConverter
    fun toBudgetPeriod(value: String): BudgetPeriod = try {
        BudgetPeriod.valueOf(value)
    } catch (e: Exception) {
        BudgetPeriod.MONTHLY
    }

    @TypeConverter
    fun fromDebtType(value: DebtType): String = value.name

    @TypeConverter
    fun toDebtType(value: String): DebtType = try {
        DebtType.valueOf(value)
    } catch (e: Exception) {
        DebtType.I_OWE
    }

    @TypeConverter
    fun fromRecurrenceFrequency(value: RecurrenceFrequency): String = value.name

    @TypeConverter
    fun toRecurrenceFrequency(value: String): RecurrenceFrequency = try {
        RecurrenceFrequency.valueOf(value)
    } catch (e: Exception) {
        RecurrenceFrequency.MONTHLY
    }
}
