package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.currency.CurrencyFormatter
import com.example.core.localization.AppStrings
import com.example.core.localization.Language
import com.example.data.local.dao.TransactionWithDetails
import com.example.data.local.entity.TransactionType
import com.example.ui.components.*
import com.example.ui.dialogs.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.FinanceViewModel
import com.example.ui.viewmodel.InsightType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: FinanceViewModel,
    onNavigateToTransactions: () -> Unit,
    onNavigateToBudgets: () -> Unit,
    onNavigateToMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    val language by viewModel.language.collectAsStateWithLifecycle()
    val currency by viewModel.currency.collectAsStateWithLifecycle()
    val isBalanceHidden by viewModel.isBalanceHidden.collectAsStateWithLifecycle()
    val selectedTimeFilter by viewModel.selectedTimeFilter.collectAsStateWithLifecycle()

    val totalBalance by viewModel.totalBalance.collectAsStateWithLifecycle()
    val totalIncome by viewModel.totalIncomeInFilter.collectAsStateWithLifecycle()
    val totalExpense by viewModel.totalExpenseInFilter.collectAsStateWithLifecycle()

    val transactions by viewModel.filteredTransactions.collectAsStateWithLifecycle()
    val categoryBreakdowns by viewModel.categoryBreakdowns.collectAsStateWithLifecycle()
    val insights by viewModel.insights.collectAsStateWithLifecycle()
    val categories by viewModel.allCategories.collectAsStateWithLifecycle()
    val accounts by viewModel.activeAccounts.collectAsStateWithLifecycle()
    val budgets by viewModel.allBudgets.collectAsStateWithLifecycle()

    // Dialog state
    var showAddTransactionSheet by remember { mutableStateOf(false) }
    var initialTransactionType by remember { mutableStateOf(TransactionType.EXPENSE) }
    var selectedTransactionForDetails by remember { mutableStateOf<TransactionWithDetails?>(null) }
    var showAddBudgetDialog by remember { mutableStateOf(false) }
    var showAddDebtDialog by remember { mutableStateOf(false) }

    val todayDate = remember {
        val sdf = SimpleDateFormat("EEEE, dd MMMM", Locale.getDefault())
        sdf.format(Date())
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = AppStrings.get("app_name", language),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = todayDate,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    // Quick language toggle chip
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                viewModel.setLanguage(if (language == Language.BANGLA) Language.ENGLISH else Language.BANGLA)
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                    ) {
                        Text(
                            text = if (language == Language.BANGLA) "বাং" else "EN",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = onNavigateToMore) {
                        Icon(
                            imageVector = Icons.Rounded.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    initialTransactionType = TransactionType.EXPENSE
                    showAddTransactionSheet = true
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                icon = { Icon(imageVector = Icons.Rounded.Add, contentDescription = null) },
                text = { Text(AppStrings.get("save", language), fontWeight = FontWeight.Bold) },
                shape = RoundedCornerShape(18.dp)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // 1. Time Filter Chips
            item {
                TimeFilterChips(
                    selectedFilter = selectedTimeFilter,
                    language = language,
                    onFilterSelected = { viewModel.setTimeFilter(it) }
                )
            }

            // 2. Financial Balance Overview Card
            item {
                BalanceOverviewCard(
                    totalBalance = totalBalance,
                    income = totalIncome,
                    expense = totalExpense,
                    currency = currency,
                    language = language,
                    isBalanceHidden = isBalanceHidden,
                    onToggleBalanceHidden = { viewModel.toggleBalanceHidden() }
                )
            }

            // 3. Quick Action Row
            item {
                QuickActionRow(
                    language = language,
                    onAddIncome = {
                        initialTransactionType = TransactionType.INCOME
                        showAddTransactionSheet = true
                    },
                    onAddExpense = {
                        initialTransactionType = TransactionType.EXPENSE
                        showAddTransactionSheet = true
                    },
                    onAddTransfer = {
                        initialTransactionType = TransactionType.TRANSFER
                        showAddTransactionSheet = true
                    },
                    onAddBudget = { showAddBudgetDialog = true },
                    onAddDebt = { showAddDebtDialog = true }
                )
            }

            // 4. Financial Insights (if available)
            if (insights.isNotEmpty()) {
                item {
                    val insight = insights.first()
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = when (insight.type) {
                                InsightType.SAVINGS -> IncomeGreenLight
                                InsightType.SPENDING_ALERT -> ExpenseRedLight
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            }
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(
                                        when (insight.type) {
                                            InsightType.SAVINGS -> IncomeGreen
                                            InsightType.SPENDING_ALERT -> ExpenseRed
                                            else -> MaterialTheme.colorScheme.primary
                                        },
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (insight.type) {
                                        InsightType.SAVINGS -> Icons.Rounded.TrendingUp
                                        InsightType.SPENDING_ALERT -> Icons.Rounded.Warning
                                        else -> Icons.Rounded.Lightbulb
                                    },
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (language == Language.BANGLA) insight.titleBn else insight.titleEn,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (language == Language.BANGLA) insight.descriptionBn else insight.descriptionEn,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // 5. Expense by Category Donut Chart (if expenses exist)
            if (categoryBreakdowns.isNotEmpty()) {
                item {
                    Column {
                        Text(
                            text = AppStrings.get("expense_by_category", language),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ExpenseCategoryChart(
                            breakdowns = categoryBreakdowns,
                            currency = currency,
                            language = language
                        )
                    }
                }
            }

            // 6. Recent Transactions Section Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = AppStrings.get("recent_transactions", language),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = AppStrings.get("view_all", language),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable { onNavigateToTransactions() }
                    )
                }
            }

            // 7. Recent Transactions List (take 6)
            if (transactions.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.ReceiptLong,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                                modifier = Modifier.size(44.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = AppStrings.get("no_transactions_title", language),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = AppStrings.get("no_transactions_sub", language),
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(transactions.take(6), key = { it.transaction.id }) { item ->
                    TransactionListItem(
                        item = item,
                        currency = currency,
                        language = language,
                        onClick = { selectedTransactionForDetails = item }
                    )
                }
            }
        }
    }

    // Modal Add Transaction Sheet
    if (showAddTransactionSheet) {
        AddEditTransactionSheet(
            categories = categories,
            accounts = accounts,
            currency = currency,
            language = language,
            initialType = initialTransactionType,
            onDismiss = { showAddTransactionSheet = false },
            onSave = { amount, type, catId, accId, targetAccId, dateMillis, note, tags, person ->
                viewModel.addTransaction(
                    amount = amount,
                    type = type,
                    categoryId = catId,
                    accountId = accId,
                    targetAccountId = targetAccId,
                    dateMillis = dateMillis,
                    note = note,
                    tags = tags,
                    personName = person
                )
            }
        )
    }

    // Details Dialog
    selectedTransactionForDetails?.let { item ->
        TransactionDetailsDialog(
            item = item,
            currency = currency,
            language = language,
            onDismiss = { selectedTransactionForDetails = null },
            onDelete = { viewModel.deleteTransaction(item.transaction) }
        )
    }

    // Add Budget Dialog
    if (showAddBudgetDialog) {
        AddBudgetDialog(
            categories = categories,
            currency = currency,
            language = language,
            onDismiss = { showAddBudgetDialog = false },
            onSave = { name, amt, catId, period, threshold ->
                viewModel.addBudget(name, amt, catId, period, threshold)
            }
        )
    }

    // Add Debt Dialog
    if (showAddDebtDialog) {
        AddDebtDialog(
            currency = currency,
            language = language,
            onDismiss = { showAddDebtDialog = false },
            onSave = { person, amt, type, dueDays, note ->
                val dueDate = System.currentTimeMillis() + (dueDays.toLong() * 24 * 60 * 60 * 1000)
                viewModel.addDebt(person, amt, type, dueDate, note)
            }
        )
    }
}
