package com.example.data.local

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.FinanceDao
import com.example.data.local.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        AccountEntity::class,
        CategoryEntity::class,
        TransactionEntity::class,
        BudgetEntity::class,
        SavingsGoalEntity::class,
        DebtEntity::class,
        RecurringTransactionEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun financeDao(): FinanceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "finflow_database.db"
                )
                .addCallback(DatabaseCallback())
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Prepopulate initial categories & accounts on creation
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        prepopulateDefaults(database.financeDao())
                    }
                }
            }
        }

        suspend fun prepopulateDefaults(dao: FinanceDao) {
            // Default Accounts
            val defaultAccounts = listOf(
                AccountEntity(
                    name = "Cash",
                    type = AccountType.CASH,
                    balance = 12500.0,
                    initialBalance = 12500.0,
                    currency = "BDT",
                    colorHex = "#2E7D32",
                    iconName = "payments"
                ),
                AccountEntity(
                    name = "Main Bank (City/DBBL)",
                    type = AccountType.BANK,
                    balance = 85000.0,
                    initialBalance = 85000.0,
                    currency = "BDT",
                    maskedNumber = "****4821",
                    colorHex = "#1565C0",
                    iconName = "account_balance"
                ),
                AccountEntity(
                    name = "bKash / Mobile Wallet",
                    type = AccountType.MOBILE_WALLET,
                    balance = 4200.0,
                    initialBalance = 4200.0,
                    currency = "BDT",
                    maskedNumber = "017****99",
                    colorHex = "#D81B60",
                    iconName = "phone_android"
                ),
                AccountEntity(
                    name = "Savings Account",
                    type = AccountType.SAVINGS,
                    balance = 150000.0,
                    initialBalance = 150000.0,
                    currency = "BDT",
                    colorHex = "#00897B",
                    iconName = "savings"
                )
            )
            dao.insertAccounts(defaultAccounts)

            // Default Expense Categories
            val defaultExpenseCategories = listOf(
                CategoryEntity(name = "Food & Dining", nameBn = "খাবার ও রেস্তোরাঁ", type = CategoryType.EXPENSE, iconName = "restaurant", colorHex = "#FF5722", sortOrder = 1),
                CategoryEntity(name = "Grocery & Market", nameBn = "কাঁচাবাজার ও মুদি", type = CategoryType.EXPENSE, iconName = "shopping_cart", colorHex = "#4CAF50", sortOrder = 2),
                CategoryEntity(name = "Transportation", nameBn = "যাতায়াত ও ভাড়া", type = CategoryType.EXPENSE, iconName = "directions_bus", colorHex = "#2196F3", sortOrder = 3),
                CategoryEntity(name = "Shopping & Clothing", nameBn = "কেনাকাটা ও পোশাক", type = CategoryType.EXPENSE, iconName = "shopping_bag", colorHex = "#E91E63", sortOrder = 4),
                CategoryEntity(name = "Bills & Utilities", nameBn = "বিদ্যুৎ, গ্যাস ও বিল", type = CategoryType.EXPENSE, iconName = "receipt_long", colorHex = "#FF9800", sortOrder = 5),
                CategoryEntity(name = "House Rent", nameBn = "বাড়ি ভাড়া", type = CategoryType.EXPENSE, iconName = "home", colorHex = "#795548", sortOrder = 6),
                CategoryEntity(name = "Healthcare & Medicine", nameBn = "চিকিৎসা ও ওষুধ", type = CategoryType.EXPENSE, iconName = "medical_services", colorHex = "#F44336", sortOrder = 7),
                CategoryEntity(name = "Family & Kids", nameBn = "পরিবার ও সন্তান", type = CategoryType.EXPENSE, iconName = "family_restroom", colorHex = "#9C27B0", sortOrder = 8),
                CategoryEntity(name = "Education", nameBn = "শিক্ষা ও বইপত্র", type = CategoryType.EXPENSE, iconName = "school", colorHex = "#3F51B5", sortOrder = 9),
                CategoryEntity(name = "Entertainment & Outing", nameBn = "বিনোদন ও ভ্রমণ", type = CategoryType.EXPENSE, iconName = "movie", colorHex = "#00BCD4", sortOrder = 10),
                CategoryEntity(name = "Donation & Zakat", nameBn = "দান ও সদকা", type = CategoryType.EXPENSE, iconName = "volunteer_activism", colorHex = "#009688", sortOrder = 11),
                CategoryEntity(name = "Subscriptions", nameBn = "সাবস্ক্রিপশন ও নেট", type = CategoryType.EXPENSE, iconName = "subscriptions", colorHex = "#673AB7", sortOrder = 12),
                CategoryEntity(name = "Other Expense", nameBn = "অন্যান্য ব্যয়", type = CategoryType.EXPENSE, iconName = "category", colorHex = "#607D8B", sortOrder = 13)
            )

            // Default Income Categories
            val defaultIncomeCategories = listOf(
                CategoryEntity(name = "Monthly Salary", nameBn = "মাসিক বেতন", type = CategoryType.INCOME, iconName = "payments", colorHex = "#2E7D32", sortOrder = 1),
                CategoryEntity(name = "Business & Sales", nameBn = "ব্যবসা ও বিক্রয়", type = CategoryType.INCOME, iconName = "storefront", colorHex = "#1B5E20", sortOrder = 2),
                CategoryEntity(name = "Freelancing", nameBn = "ফ্রিল্যান্সিং", type = CategoryType.INCOME, iconName = "laptop_mac", colorHex = "#00838F", sortOrder = 3),
                CategoryEntity(name = "Bonus & Incentives", nameBn = "বোনাস ও ইনসেন্টিভ", type = CategoryType.INCOME, iconName = "card_giftcard", colorHex = "#F57F17", sortOrder = 4),
                CategoryEntity(name = "Investment Returns", nameBn = "বিনিয়োগ মুনাফা", type = CategoryType.INCOME, iconName = "trending_up", colorHex = "#33691E", sortOrder = 5),
                CategoryEntity(name = "Rental Income", nameBn = "ভাড়া প্রাপ্তি", type = CategoryType.INCOME, iconName = "apartment", colorHex = "#4E342E", sortOrder = 6),
                CategoryEntity(name = "Gift / Remittance", nameBn = "উপহার বা রেমিট্যান্স", type = CategoryType.INCOME, iconName = "redeem", colorHex = "#880E4F", sortOrder = 7),
                CategoryEntity(name = "Other Income", nameBn = "অন্যান্য আয়", type = CategoryType.INCOME, iconName = "attach_money", colorHex = "#37474F", sortOrder = 8)
            )

            dao.insertCategories(defaultExpenseCategories + defaultIncomeCategories)

            // Initial Sample Budgets & Goals
            val currentMonth = System.currentTimeMillis()
            dao.insertBudget(
                BudgetEntity(
                    name = "Monthly Food & Dining",
                    amount = 15000.0,
                    categoryId = 1,
                    period = BudgetPeriod.MONTHLY,
                    warningThresholdPercent = 80
                )
            )
            dao.insertBudget(
                BudgetEntity(
                    name = "Monthly Grocery Budget",
                    amount = 20000.0,
                    categoryId = 2,
                    period = BudgetPeriod.MONTHLY,
                    warningThresholdPercent = 85
                )
            )
            dao.insertSavingsGoal(
                SavingsGoalEntity(
                    title = "Emergency Fund / জরুরী তহবিল",
                    targetAmount = 100000.0,
                    currentAmount = 45000.0,
                    deadlineMillis = currentMonth + (90L * 24 * 60 * 60 * 1000)
                )
            )
            dao.insertSavingsGoal(
                SavingsGoalEntity(
                    title = "New Laptop / ল্যাপটপ ক্রয়",
                    targetAmount = 85000.0,
                    currentAmount = 30000.0,
                    deadlineMillis = currentMonth + (120L * 24 * 60 * 60 * 1000)
                )
            )
        }
    }
}
