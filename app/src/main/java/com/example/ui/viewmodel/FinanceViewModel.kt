package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.currency.AppCurrency
import com.example.core.localization.Language
import com.example.data.local.AppDatabase
import com.example.data.local.dao.TransactionWithDetails
import com.example.data.local.entity.*
import com.example.data.preferences.AppThemeMode
import com.example.data.preferences.PreferenceManager
import com.example.data.repository.FinanceRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

enum class TimeFilter {
    TODAY,
    THIS_WEEK,
    THIS_MONTH,
    THIS_YEAR,
    ALL_TIME
}

data class CategoryBreakdown(
    val categoryName: String,
    val categoryNameBn: String,
    val iconName: String,
    val colorHex: String,
    val amount: Double,
    val percentage: Float
)

data class FinancialInsight(
    val titleEn: String,
    val titleBn: String,
    val descriptionEn: String,
    val descriptionBn: String,
    val type: InsightType
)

enum class InsightType {
    SAVINGS,
    SPENDING_ALERT,
    TOP_CATEGORY,
    POSITIVE_TREND
}

class FinanceViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val repository = FinanceRepository(db.financeDao())
    val preferenceManager = PreferenceManager(application)

    // Preferences Flows
    val language: StateFlow<Language> = preferenceManager.language
    val currency: StateFlow<AppCurrency> = preferenceManager.currency
    val themeMode: StateFlow<AppThemeMode> = preferenceManager.themeMode
    val isAppLockEnabled: StateFlow<Boolean> = preferenceManager.isAppLockEnabled
    val isOnboardingCompleted: StateFlow<Boolean> = preferenceManager.isOnboardingCompleted
    val isBalanceHidden: StateFlow<Boolean> = preferenceManager.isBalanceHidden

    // App Lock Unlock state for current session
    private val _isAppUnlocked = MutableStateFlow(!preferenceManager.isAppLockEnabled.value)
    val isAppUnlocked: StateFlow<Boolean> = _isAppUnlocked.asStateFlow()

    // Filters & Search
    private val _selectedTimeFilter = MutableStateFlow(TimeFilter.THIS_MONTH)
    val selectedTimeFilter: StateFlow<TimeFilter> = _selectedTimeFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filterCategory = MutableStateFlow<Long?>(null)
    val filterCategory: StateFlow<Long?> = _filterCategory.asStateFlow()

    private val _filterAccount = MutableStateFlow<Long?>(null)
    val filterAccount: StateFlow<Long?> = _filterAccount.asStateFlow()

    private val _filterType = MutableStateFlow<TransactionType?>(null)
    val filterType: StateFlow<TransactionType?> = _filterType.asStateFlow()

    // Base Data Flows
    val allTransactions: StateFlow<List<TransactionWithDetails>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeAccounts: StateFlow<List<AccountEntity>> = repository.activeAccounts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAccounts: StateFlow<List<AccountEntity>> = repository.allAccounts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCategories: StateFlow<List<CategoryEntity>> = repository.allCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allBudgets: StateFlow<List<BudgetEntity>> = repository.allBudgets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSavingsGoals: StateFlow<List<SavingsGoalEntity>> = repository.allSavingsGoals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allDebts: StateFlow<List<DebtEntity>> = repository.allDebts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allRecurring: StateFlow<List<RecurringTransactionEntity>> = repository.allRecurring
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filter criteria combined into a single internal state
    private data class FilterCriteria(
        val filter: TimeFilter,
        val query: String,
        val type: TransactionType?,
        val catId: Long?,
        val accId: Long?
    )

    private val filterCriteria: Flow<FilterCriteria> = combine(
        _selectedTimeFilter,
        _searchQuery,
        _filterType,
        _filterCategory,
        _filterAccount
    ) { filter, query, type, catId, accId ->
        FilterCriteria(filter, query, type, catId, accId)
    }

    // Filtered Transactions for Dashboard & List
    val filteredTransactions: StateFlow<List<TransactionWithDetails>> = combine(
        allTransactions,
        filterCriteria
    ) { txList, criteria ->
        val range = getTimeRange(criteria.filter)
        txList.filter { item ->
            val matchesTime = if (criteria.filter == TimeFilter.ALL_TIME) true else (item.transaction.dateMillis in range.first..range.second)
            val matchesQuery = criteria.query.isBlank() ||
                    item.transaction.note.contains(criteria.query, ignoreCase = true) ||
                    (item.category?.name?.contains(criteria.query, ignoreCase = true) == true) ||
                    (item.category?.nameBn?.contains(criteria.query, ignoreCase = true) == true) ||
                    item.transaction.personName.contains(criteria.query, ignoreCase = true) ||
                    item.transaction.tags.contains(criteria.query, ignoreCase = true)
            val matchesType = criteria.type == null || item.transaction.type == criteria.type
            val matchesCat = criteria.catId == null || item.transaction.categoryId == criteria.catId
            val matchesAcc = criteria.accId == null || item.transaction.accountId == criteria.accId || item.transaction.targetAccountId == criteria.accId

            matchesTime && matchesQuery && matchesType && matchesCat && matchesAcc
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Financial Totals in Filtered Period
    val totalIncomeInFilter: StateFlow<Double> = filteredTransactions.map { list ->
        list.filter { it.transaction.type == TransactionType.INCOME || it.transaction.type == TransactionType.REFUND }
            .sumOf { it.transaction.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalExpenseInFilter: StateFlow<Double> = filteredTransactions.map { list ->
        list.filter { it.transaction.type == TransactionType.EXPENSE }
            .sumOf { it.transaction.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalBalance: StateFlow<Double> = activeAccounts.map { list ->
        list.sumOf { it.balance }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Category Breakdown for Charts
    val categoryBreakdowns: StateFlow<List<CategoryBreakdown>> = combine(
        filteredTransactions,
        totalExpenseInFilter
    ) { txList, totalExp ->
        if (totalExp <= 0.0) return@combine emptyList<CategoryBreakdown>()
        val expenses = txList.filter { it.transaction.type == TransactionType.EXPENSE }
        val grouped = expenses.groupBy { it.category }
        grouped.map { (cat, items) ->
            val sum = items.sumOf { it.transaction.amount }
            CategoryBreakdown(
                categoryName = cat?.name ?: "Other",
                categoryNameBn = cat?.nameBn ?: "অন্যান্য",
                iconName = cat?.iconName ?: "category",
                colorHex = cat?.colorHex ?: "#78909C",
                amount = sum,
                percentage = ((sum / totalExp) * 100f).toFloat()
            )
        }.sortedByDescending { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Financial Insights
    val insights: StateFlow<List<FinancialInsight>> = combine(
        totalIncomeInFilter,
        totalExpenseInFilter,
        categoryBreakdowns
    ) { inc, exp, catBreakdowns ->
        val list = mutableListOf<FinancialInsight>()
        if (inc > 0 && exp > 0) {
            val net = inc - exp
            val savingsRate = ((net / inc) * 100).toInt()
            if (savingsRate > 0) {
                list.add(
                    FinancialInsight(
                        titleEn = "Positive Savings Rate ($savingsRate%)",
                        titleBn = "ইতিবাচক সঞ্চয় হার ($savingsRate%)",
                        descriptionEn = "You saved $savingsRate% of your income in this period. Great financial discipline!",
                        descriptionBn = "এই সময়ে আপনি আয়ের $savingsRate% সঞ্চয় করেছেন। চমৎকার আর্থিক নিয়ন্ত্রণ!",
                        type = InsightType.SAVINGS
                    )
                )
            } else {
                list.add(
                    FinancialInsight(
                        titleEn = "Expense Exceeded Income",
                        titleBn = "আয় অপেক্ষা ব্যয় বেশি",
                        descriptionEn = "Your spending exceeded your income by ${(exp - inc).toInt()}. Review top expense categories to balance.",
                        descriptionBn = "আপনার মোট খরচ আয়ের চেয়ে বেশি হয়েছে। শীর্ষ খরচের খাতগুলো পর্যালোচনা করুন।",
                        type = InsightType.SPENDING_ALERT
                    )
                )
            }
        }
        if (catBreakdowns.isNotEmpty()) {
            val top = catBreakdowns.first()
            list.add(
                FinancialInsight(
                    titleEn = "Top Expense: ${top.categoryName} (${top.percentage.toInt()}%)",
                    titleBn = "সর্বোচ্চ খরচের খাত: ${top.categoryNameBn} (${top.percentage.toInt()}%)",
                    descriptionEn = "${top.categoryName} accounted for the highest portion of your spending.",
                    descriptionBn = "${top.categoryNameBn} খাতে এই মাসে সবচেয়ে বেশি খরচ হয়েছে।",
                    type = InsightType.TOP_CATEGORY
                )
            )
        }
        list
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Actions ---

    fun setTimeFilter(filter: TimeFilter) {
        _selectedTimeFilter.value = filter
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setFilterType(type: TransactionType?) {
        _filterType.value = type
    }

    fun setFilterCategory(catId: Long?) {
        _filterCategory.value = catId
    }

    fun setFilterAccount(accId: Long?) {
        _filterAccount.value = accId
    }

    fun clearFilters() {
        _searchQuery.value = ""
        _filterType.value = null
        _filterCategory.value = null
        _filterAccount.value = null
    }

    fun setLanguage(lang: Language) {
        preferenceManager.setLanguage(lang)
    }

    fun setCurrency(curr: AppCurrency) {
        preferenceManager.setCurrency(curr)
    }

    fun setThemeMode(mode: AppThemeMode) {
        preferenceManager.setThemeMode(mode)
    }

    fun toggleBalanceHidden() {
        preferenceManager.toggleBalanceHidden()
    }

    fun setOnboardingCompleted() {
        preferenceManager.setOnboardingCompleted(true)
    }

    fun setAppLock(enabled: Boolean, pin: String? = null) {
        preferenceManager.setAppLock(enabled, pin)
        _isAppUnlocked.value = true
    }

    fun unlockAppWithPin(pin: String): Boolean {
        val success = preferenceManager.verifyPin(pin)
        if (success) {
            _isAppUnlocked.value = true
        }
        return success
    }

    // CRUD Transactions
    fun addTransaction(
        amount: Double,
        type: TransactionType,
        categoryId: Long,
        accountId: Long,
        targetAccountId: Long? = null,
        dateMillis: Long = System.currentTimeMillis(),
        note: String = "",
        paymentMethod: String = "",
        tags: String = "",
        personName: String = ""
    ) {
        viewModelScope.launch {
            repository.insertTransaction(
                TransactionEntity(
                    amount = amount,
                    type = type,
                    categoryId = categoryId,
                    accountId = accountId,
                    targetAccountId = targetAccountId,
                    dateMillis = dateMillis,
                    note = note,
                    paymentMethod = paymentMethod,
                    tags = tags,
                    personName = personName
                )
            )
        }
    }

    fun updateTransaction(old: TransactionEntity, updated: TransactionEntity) {
        viewModelScope.launch {
            repository.updateTransaction(old, updated)
        }
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
        }
    }

    // CRUD Accounts
    fun addAccount(name: String, type: AccountType, initialBalance: Double, colorHex: String, maskedNumber: String = "") {
        viewModelScope.launch {
            repository.insertAccount(
                AccountEntity(
                    name = name,
                    type = type,
                    balance = initialBalance,
                    initialBalance = initialBalance,
                    colorHex = colorHex,
                    maskedNumber = maskedNumber
                )
            )
        }
    }

    fun updateAccount(account: AccountEntity) {
        viewModelScope.launch {
            repository.updateAccount(account)
        }
    }

    fun deleteAccount(account: AccountEntity) {
        viewModelScope.launch {
            repository.deleteAccount(account)
        }
    }

    // CRUD Categories
    fun addCategory(name: String, nameBn: String, type: CategoryType, iconName: String, colorHex: String) {
        viewModelScope.launch {
            repository.insertCategory(
                CategoryEntity(
                    name = name,
                    nameBn = nameBn,
                    type = type,
                    iconName = iconName,
                    colorHex = colorHex,
                    isDefault = false,
                    isCustom = true
                )
            )
        }
    }

    // CRUD Budgets
    fun addBudget(name: String, amount: Double, categoryId: Long?, period: BudgetPeriod, threshold: Int = 80) {
        viewModelScope.launch {
            repository.insertBudget(
                BudgetEntity(
                    name = name,
                    amount = amount,
                    categoryId = categoryId,
                    period = period,
                    warningThresholdPercent = threshold
                )
            )
        }
    }

    fun deleteBudget(budget: BudgetEntity) {
        viewModelScope.launch {
            repository.deleteBudget(budget)
        }
    }

    // CRUD Savings Goals
    fun addSavingsGoal(title: String, targetAmount: Double, currentAmount: Double, deadlineMillis: Long, colorHex: String = "#00897B") {
        viewModelScope.launch {
            repository.insertSavingsGoal(
                SavingsGoalEntity(
                    title = title,
                    targetAmount = targetAmount,
                    currentAmount = currentAmount,
                    deadlineMillis = deadlineMillis,
                    colorHex = colorHex
                )
            )
        }
    }

    fun contributeToGoal(goal: SavingsGoalEntity, amount: Double, sourceAccountId: Long?) {
        viewModelScope.launch {
            val newAmount = goal.currentAmount + amount
            val updated = goal.copy(currentAmount = newAmount, isCompleted = newAmount >= goal.targetAmount)
            repository.updateSavingsGoal(updated)
            if (sourceAccountId != null) {
                // Record transfer to savings
                repository.addSavingsContribution(goal.id, amount, sourceAccountId)
            }
        }
    }

    fun deleteSavingsGoal(goal: SavingsGoalEntity) {
        viewModelScope.launch {
            repository.deleteSavingsGoal(goal)
        }
    }

    // CRUD Debts
    fun addDebt(personName: String, amount: Double, type: DebtType, dueDateMillis: Long, note: String) {
        viewModelScope.launch {
            repository.insertDebt(
                DebtEntity(
                    personName = personName,
                    amount = amount,
                    type = type,
                    dueDateMillis = dueDateMillis,
                    note = note
                )
            )
        }
    }

    fun recordDebtPayment(debt: DebtEntity, paymentAmount: Double, accountId: Long?) {
        viewModelScope.launch {
            repository.recordDebtPayment(debt, paymentAmount, accountId)
        }
    }

    fun deleteDebt(debt: DebtEntity) {
        viewModelScope.launch {
            repository.deleteDebt(debt)
        }
    }

    // CRUD Recurring
    fun addRecurring(title: String, amount: Double, categoryId: Long, accountId: Long, frequency: RecurrenceFrequency, nextDue: Long) {
        viewModelScope.launch {
            repository.insertRecurring(
                RecurringTransactionEntity(
                    title = title,
                    amount = amount,
                    categoryId = categoryId,
                    accountId = accountId,
                    frequency = frequency,
                    nextDueDateMillis = nextDue
                )
            )
        }
    }

    fun deleteRecurring(recurring: RecurringTransactionEntity) {
        viewModelScope.launch {
            repository.deleteRecurring(recurring)
        }
    }

    // Clear Data & Sample Data
    fun clearAllData(onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.clearAllData()
            onComplete()
        }
    }

    fun loadSampleData(onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.loadSampleData()
            onComplete()
        }
    }

    suspend fun getExportCsvString(): String = repository.exportToCsv()
    suspend fun getExportJsonString(): String = repository.exportToJsonBackup()

    // Helpers
    private fun getTimeRange(filter: TimeFilter): Pair<Long, Long> {
        val cal = Calendar.getInstance()
        val now = cal.timeInMillis

        return when (filter) {
            TimeFilter.TODAY -> {
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                val start = cal.timeInMillis
                cal.set(Calendar.HOUR_OF_DAY, 23)
                cal.set(Calendar.MINUTE, 59)
                cal.set(Calendar.SECOND, 59)
                val end = cal.timeInMillis
                Pair(start, end)
            }
            TimeFilter.THIS_WEEK -> {
                cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                val start = cal.timeInMillis
                cal.add(Calendar.DAY_OF_WEEK, 6)
                cal.set(Calendar.HOUR_OF_DAY, 23)
                cal.set(Calendar.MINUTE, 59)
                cal.set(Calendar.SECOND, 59)
                val end = cal.timeInMillis
                Pair(start, end)
            }
            TimeFilter.THIS_MONTH -> {
                cal.set(Calendar.DAY_OF_MONTH, 1)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                val start = cal.timeInMillis
                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
                cal.set(Calendar.HOUR_OF_DAY, 23)
                cal.set(Calendar.MINUTE, 59)
                cal.set(Calendar.SECOND, 59)
                val end = cal.timeInMillis
                Pair(start, end)
            }
            TimeFilter.THIS_YEAR -> {
                cal.set(Calendar.DAY_OF_YEAR, 1)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                val start = cal.timeInMillis
                cal.set(Calendar.MONTH, Calendar.DECEMBER)
                cal.set(Calendar.DAY_OF_MONTH, 31)
                cal.set(Calendar.HOUR_OF_DAY, 23)
                cal.set(Calendar.MINUTE, 59)
                cal.set(Calendar.SECOND, 59)
                val end = cal.timeInMillis
                Pair(start, end)
            }
            TimeFilter.ALL_TIME -> Pair(0L, Long.MAX_VALUE)
        }
    }
}
