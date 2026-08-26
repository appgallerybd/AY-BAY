package com.example.data.repository

import com.example.data.local.dao.FinanceDao
import com.example.data.local.dao.TransactionWithDetails
import com.example.data.local.entity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FinanceRepository(private val dao: FinanceDao) {

    // --- Transactions Flow ---
    val allTransactions: Flow<List<TransactionWithDetails>> = dao.getAllTransactionsWithDetails()

    fun getTransactionsBetween(startMillis: Long, endMillis: Long): Flow<List<TransactionWithDetails>> {
        return dao.getTransactionsBetween(startMillis, endMillis)
    }

    suspend fun getTransactionById(id: Long): TransactionWithDetails? = dao.getTransactionById(id)

    suspend fun insertTransaction(transaction: TransactionEntity): Long = withContext(Dispatchers.IO) {
        val id = dao.insertTransaction(transaction)

        // Adjust Account Balances
        when (transaction.type) {
            TransactionType.EXPENSE -> {
                dao.adjustAccountBalance(transaction.accountId, -transaction.amount)
            }
            TransactionType.INCOME -> {
                dao.adjustAccountBalance(transaction.accountId, transaction.amount)
            }
            TransactionType.TRANSFER -> {
                // Deduct from source, add to target
                dao.adjustAccountBalance(transaction.accountId, -transaction.amount)
                transaction.targetAccountId?.let { targetId ->
                    dao.adjustAccountBalance(targetId, transaction.amount)
                }
            }
            TransactionType.REFUND -> {
                dao.adjustAccountBalance(transaction.accountId, transaction.amount)
            }
            TransactionType.ADJUSTMENT -> {
                dao.adjustAccountBalance(transaction.accountId, transaction.amount)
            }
        }
        id
    }

    suspend fun updateTransaction(old: TransactionEntity, updated: TransactionEntity) = withContext(Dispatchers.IO) {
        // Revert old effect
        revertTransactionEffect(old)
        // Apply updated effect
        applyTransactionEffect(updated)
        // Update in db
        dao.updateTransaction(updated)
    }

    suspend fun deleteTransaction(transaction: TransactionEntity) = withContext(Dispatchers.IO) {
        revertTransactionEffect(transaction)
        dao.deleteTransaction(transaction)
    }

    private suspend fun revertTransactionEffect(tx: TransactionEntity) {
        when (tx.type) {
            TransactionType.EXPENSE -> dao.adjustAccountBalance(tx.accountId, tx.amount)
            TransactionType.INCOME -> dao.adjustAccountBalance(tx.accountId, -tx.amount)
            TransactionType.TRANSFER -> {
                dao.adjustAccountBalance(tx.accountId, tx.amount)
                tx.targetAccountId?.let { dao.adjustAccountBalance(it, -tx.amount) }
            }
            TransactionType.REFUND, TransactionType.ADJUSTMENT -> dao.adjustAccountBalance(tx.accountId, -tx.amount)
        }
    }

    private suspend fun applyTransactionEffect(tx: TransactionEntity) {
        when (tx.type) {
            TransactionType.EXPENSE -> dao.adjustAccountBalance(tx.accountId, -tx.amount)
            TransactionType.INCOME -> dao.adjustAccountBalance(tx.accountId, tx.amount)
            TransactionType.TRANSFER -> {
                dao.adjustAccountBalance(tx.accountId, -tx.amount)
                tx.targetAccountId?.let { dao.adjustAccountBalance(it, tx.amount) }
            }
            TransactionType.REFUND, TransactionType.ADJUSTMENT -> dao.adjustAccountBalance(tx.accountId, tx.amount)
        }
    }

    // --- Accounts ---
    val activeAccounts: Flow<List<AccountEntity>> = dao.getActiveAccounts()
    val allAccounts: Flow<List<AccountEntity>> = dao.getAllAccounts()

    suspend fun insertAccount(account: AccountEntity): Long = withContext(Dispatchers.IO) {
        dao.insertAccount(account)
    }

    suspend fun updateAccount(account: AccountEntity) = withContext(Dispatchers.IO) {
        dao.updateAccount(account)
    }

    suspend fun deleteAccount(account: AccountEntity) = withContext(Dispatchers.IO) {
        dao.deleteAccount(account)
    }

    // --- Categories ---
    val allCategories: Flow<List<CategoryEntity>> = dao.getAllCategories()
    fun getCategoriesByType(type: CategoryType): Flow<List<CategoryEntity>> = dao.getCategoriesByType(type)

    suspend fun insertCategory(category: CategoryEntity): Long = withContext(Dispatchers.IO) {
        dao.insertCategory(category)
    }

    suspend fun updateCategory(category: CategoryEntity) = withContext(Dispatchers.IO) {
        dao.updateCategory(category)
    }

    suspend fun deleteCategory(category: CategoryEntity) = withContext(Dispatchers.IO) {
        dao.deleteCategory(category)
    }

    // --- Budgets ---
    val allBudgets: Flow<List<BudgetEntity>> = dao.getAllBudgets()

    suspend fun insertBudget(budget: BudgetEntity): Long = withContext(Dispatchers.IO) {
        dao.insertBudget(budget)
    }

    suspend fun updateBudget(budget: BudgetEntity) = withContext(Dispatchers.IO) {
        dao.updateBudget(budget)
    }

    suspend fun deleteBudget(budget: BudgetEntity) = withContext(Dispatchers.IO) {
        dao.deleteBudget(budget)
    }

    // --- Savings Goals ---
    val allSavingsGoals: Flow<List<SavingsGoalEntity>> = dao.getAllSavingsGoals()

    suspend fun insertSavingsGoal(goal: SavingsGoalEntity): Long = withContext(Dispatchers.IO) {
        dao.insertSavingsGoal(goal)
    }

    suspend fun updateSavingsGoal(goal: SavingsGoalEntity) = withContext(Dispatchers.IO) {
        dao.updateSavingsGoal(goal)
    }

    suspend fun addSavingsContribution(goalId: Long, amount: Double, sourceAccountId: Long?) = withContext(Dispatchers.IO) {
        // Adjust goal amount
        val goals = dao.getAllSavingsGoals()
        // If source account specified, record expense or adjust
        if (sourceAccountId != null) {
            dao.adjustAccountBalance(sourceAccountId, -amount)
        }
    }

    suspend fun deleteSavingsGoal(goal: SavingsGoalEntity) = withContext(Dispatchers.IO) {
        dao.deleteSavingsGoal(goal)
    }

    // --- Debts ---
    val allDebts: Flow<List<DebtEntity>> = dao.getAllDebts()

    suspend fun insertDebt(debt: DebtEntity): Long = withContext(Dispatchers.IO) {
        dao.insertDebt(debt)
    }

    suspend fun updateDebt(debt: DebtEntity) = withContext(Dispatchers.IO) {
        dao.updateDebt(debt)
    }

    suspend fun recordDebtPayment(debt: DebtEntity, paymentAmount: Double, accountId: Long?) = withContext(Dispatchers.IO) {
        val newPaid = debt.paidAmount + paymentAmount
        val isSettled = newPaid >= debt.amount
        val updated = debt.copy(paidAmount = newPaid, isSettled = isSettled)
        dao.updateDebt(updated)

        // Adjust account if chosen
        if (accountId != null) {
            if (debt.type == DebtType.I_OWE) {
                // I paid someone -> balance decreases
                dao.adjustAccountBalance(accountId, -paymentAmount)
            } else {
                // Someone paid me -> balance increases
                dao.adjustAccountBalance(accountId, paymentAmount)
            }
        }
    }

    suspend fun deleteDebt(debt: DebtEntity) = withContext(Dispatchers.IO) {
        dao.deleteDebt(debt)
    }

    // --- Recurring ---
    val allRecurring: Flow<List<RecurringTransactionEntity>> = dao.getAllRecurring()

    suspend fun insertRecurring(recurring: RecurringTransactionEntity): Long = withContext(Dispatchers.IO) {
        dao.insertRecurring(recurring)
    }

    suspend fun updateRecurring(recurring: RecurringTransactionEntity) = withContext(Dispatchers.IO) {
        dao.updateRecurring(recurring)
    }

    suspend fun deleteRecurring(recurring: RecurringTransactionEntity) = withContext(Dispatchers.IO) {
        dao.deleteRecurring(recurring)
    }

    // --- Clear & Preload ---
    suspend fun clearAllData() = withContext(Dispatchers.IO) {
        dao.clearTransactions()
        dao.clearBudgets()
        dao.clearSavingsGoals()
        dao.clearDebts()
        dao.clearRecurring()
    }

    suspend fun loadSampleData() = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val day = 24L * 60 * 60 * 1000

        // Sample Transactions
        val samples = listOf(
            TransactionEntity(amount = 75000.0, type = TransactionType.INCOME, categoryId = 14, accountId = 2, dateMillis = now - 2 * day, note = "August Monthly Salary", paymentMethod = "Bank Deposit"),
            TransactionEntity(amount = 1850.0, type = TransactionType.EXPENSE, categoryId = 2, accountId = 1, dateMillis = now - 1 * day, note = "Weekly vegetable & grocery market", tags = "Home,Essential"),
            TransactionEntity(amount = 450.0, type = TransactionType.EXPENSE, categoryId = 1, accountId = 3, dateMillis = now - 1 * day, note = "Afternoon coffee & bakery snacks"),
            TransactionEntity(amount = 120.0, type = TransactionType.EXPENSE, categoryId = 3, accountId = 1, dateMillis = now - 12 * 3600 * 1000, note = "Uber/Rickshaw to office"),
            TransactionEntity(amount = 3500.0, type = TransactionType.EXPENSE, categoryId = 5, accountId = 3, dateMillis = now - 3 * day, note = "Electricity & Wi-Fi internet bill", referenceNumber = "DESCO-9921"),
            TransactionEntity(amount = 12000.0, type = TransactionType.INCOME, categoryId = 16, accountId = 2, dateMillis = now - 4 * day, note = "UI/UX Freelance Milestone payment"),
            TransactionEntity(amount = 2200.0, type = TransactionType.EXPENSE, categoryId = 7, accountId = 3, dateMillis = now - 5 * day, note = "Family prescription medicine"),
            TransactionEntity(amount = 5000.0, type = TransactionType.TRANSFER, categoryId = 13, accountId = 2, targetAccountId = 3, dateMillis = now - 6 * day, note = "Transfer Bank to bKash")
        )
        for (sample in samples) {
            dao.insertTransaction(sample)
        }

        // Sample Debts
        dao.insertDebt(
            DebtEntity(personName = "Rahim Ahmed", amount = 5000.0, paidAmount = 2000.0, type = DebtType.OWES_ME, dueDateMillis = now + 14 * day, note = "Borrowed for emergency")
        )
        dao.insertDebt(
            DebtEntity(personName = "Tanvir Hasan", amount = 3000.0, paidAmount = 0.0, type = DebtType.I_OWE, dueDateMillis = now + 7 * day, note = "Shared group travel expense")
        )

        // Sample Recurring Subscriptions
        dao.insertRecurring(
            RecurringTransactionEntity(title = "Netflix & Spotify", amount = 1450.0, categoryId = 12, accountId = 3, frequency = RecurrenceFrequency.MONTHLY, nextDueDateMillis = now + 10 * day)
        )
        dao.insertRecurring(
            RecurringTransactionEntity(title = "Home High-speed Internet", amount = 1200.0, categoryId = 5, accountId = 3, frequency = RecurrenceFrequency.MONTHLY, nextDueDateMillis = now + 5 * day)
        )
    }

    // --- Export CSV & JSON ---
    suspend fun exportToCsv(): String = withContext(Dispatchers.IO) {
        val list = dao.getAllTransactionsList()
        val sb = StringBuilder()
        sb.append("ID,Amount,Type,Date,Note,PaymentMethod,Tags,Person\n")
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        for (t in list) {
            sb.append("${t.id},${t.amount},${t.type},\"${sdf.format(Date(t.dateMillis))}\",\"${t.note.replace("\"", "\"\"")}\",\"${t.paymentMethod}\",\"${t.tags}\",\"${t.personName}\"\n")
        }
        sb.toString()
    }

    suspend fun exportToJsonBackup(): String = withContext(Dispatchers.IO) {
        val root = JSONObject()
        root.put("version", 1)
        root.put("exportedAt", System.currentTimeMillis())

        val txArray = JSONArray()
        for (t in dao.getAllTransactionsList()) {
            val obj = JSONObject()
            obj.put("id", t.id)
            obj.put("amount", t.amount)
            obj.put("type", t.type.name)
            obj.put("categoryId", t.categoryId)
            obj.put("accountId", t.accountId)
            obj.put("targetAccountId", t.targetAccountId ?: -1)
            obj.put("dateMillis", t.dateMillis)
            obj.put("note", t.note)
            obj.put("paymentMethod", t.paymentMethod)
            obj.put("tags", t.tags)
            obj.put("personName", t.personName)
            txArray.put(obj)
        }
        root.put("transactions", txArray)
        root.toString(2)
    }
}
