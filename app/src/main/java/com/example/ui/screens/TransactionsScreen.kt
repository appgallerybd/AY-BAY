package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import com.example.core.localization.AppStrings
import com.example.core.localization.Language
import com.example.data.local.dao.TransactionWithDetails
import com.example.data.local.entity.TransactionType
import com.example.ui.components.TimeFilterChips
import com.example.ui.components.TransactionListItem
import com.example.ui.dialogs.AddEditTransactionSheet
import com.example.ui.dialogs.TransactionDetailsDialog
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.TransferPurple
import com.example.ui.viewmodel.FinanceViewModel
import com.example.ui.viewmodel.TimeFilter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    viewModel: FinanceViewModel,
    modifier: Modifier = Modifier
) {
    val language by viewModel.language.collectAsStateWithLifecycle()
    val currency by viewModel.currency.collectAsStateWithLifecycle()
    val selectedTimeFilter by viewModel.selectedTimeFilter.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val filterType by viewModel.filterType.collectAsStateWithLifecycle()
    val filterCategory by viewModel.filterCategory.collectAsStateWithLifecycle()
    val filterAccount by viewModel.filterAccount.collectAsStateWithLifecycle()

    val transactions by viewModel.filteredTransactions.collectAsStateWithLifecycle()
    val categories by viewModel.allCategories.collectAsStateWithLifecycle()
    val accounts by viewModel.activeAccounts.collectAsStateWithLifecycle()

    var showAddTransactionSheet by remember { mutableStateOf(false) }
    var selectedTransactionForDetails by remember { mutableStateOf<TransactionWithDetails?>(null) }
    var isSearchExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (isSearchExpanded) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.setSearchQuery(it) },
                            placeholder = { Text(AppStrings.get("search_hint", language), fontSize = 14.sp) },
                            singleLine = true,
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                        Icon(imageVector = Icons.Rounded.Clear, contentDescription = "Clear")
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Text(
                            text = AppStrings.get("transactions", language),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        isSearchExpanded = !isSearchExpanded
                        if (!isSearchExpanded) viewModel.setSearchQuery("")
                    }) {
                        Icon(
                            imageVector = if (isSearchExpanded) Icons.Rounded.Close else Icons.Rounded.Search,
                            contentDescription = "Search"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddTransactionSheet = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(imageVector = Icons.Rounded.Add, contentDescription = "Add Transaction")
            }
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // Time Filters
            TimeFilterChips(
                selectedFilter = selectedTimeFilter,
                language = language,
                onFilterSelected = { viewModel.setTimeFilter(it) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Type Filter Chips Row (All, Expense, Income, Transfer)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = filterType == null,
                    onClick = { viewModel.setFilterType(null) },
                    label = { Text(AppStrings.get("all", language), fontSize = 11.sp) }
                )
                FilterChip(
                    selected = filterType == TransactionType.EXPENSE,
                    onClick = {
                        viewModel.setFilterType(if (filterType == TransactionType.EXPENSE) null else TransactionType.EXPENSE)
                    },
                    label = { Text(AppStrings.get("expense", language), fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ExpenseRed.copy(alpha = 0.2f),
                        selectedLabelColor = ExpenseRed
                    )
                )
                FilterChip(
                    selected = filterType == TransactionType.INCOME,
                    onClick = {
                        viewModel.setFilterType(if (filterType == TransactionType.INCOME) null else TransactionType.INCOME)
                    },
                    label = { Text(AppStrings.get("income", language), fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = IncomeGreen.copy(alpha = 0.2f),
                        selectedLabelColor = IncomeGreen
                    )
                )
                FilterChip(
                    selected = filterType == TransactionType.TRANSFER,
                    onClick = {
                        viewModel.setFilterType(if (filterType == TransactionType.TRANSFER) null else TransactionType.TRANSFER)
                    },
                    label = { Text(AppStrings.get("transfer", language), fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = TransferPurple.copy(alpha = 0.2f),
                        selectedLabelColor = TransferPurple
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Transaction List or Empty State
            if (transactions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Rounded.ReceiptLong,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = AppStrings.get("no_transactions_title", language),
                            fontSize = 16.sp,
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
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(transactions, key = { it.transaction.id }) { item ->
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
    }

    // Modal Add Transaction Sheet
    if (showAddTransactionSheet) {
        AddEditTransactionSheet(
            categories = categories,
            accounts = accounts,
            currency = currency,
            language = language,
            initialType = filterType ?: TransactionType.EXPENSE,
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
}
