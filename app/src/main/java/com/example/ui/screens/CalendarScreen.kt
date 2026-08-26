package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.currency.CurrencyFormatter
import com.example.core.localization.AppStrings
import com.example.core.localization.Language
import com.example.data.local.dao.TransactionWithDetails
import com.example.data.local.entity.TransactionType
import com.example.ui.components.TransactionListItem
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.ui.viewmodel.FinanceViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    viewModel: FinanceViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val language by viewModel.language.collectAsStateWithLifecycle()
    val currency by viewModel.currency.collectAsStateWithLifecycle()
    val allTransactions by viewModel.allTransactions.collectAsStateWithLifecycle()

    var selectedCalendarMonth by remember { mutableStateOf(Calendar.getInstance()) }
    var selectedDay by remember { mutableStateOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) }

    val monthYearFormat = remember { SimpleDateFormat("MMMM yyyy", Locale.getDefault()) }

    // Compute day items for the current selected month
    val daysInMonth = remember(selectedCalendarMonth) {
        val cal = selectedCalendarMonth.clone() as Calendar
        cal.set(Calendar.DAY_OF_MONTH, 1)
        val maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1 // 0-indexed offset

        val list = mutableListOf<Int?>()
        repeat(firstDayOfWeek) { list.add(null) }
        for (i in 1..maxDays) { list.add(i) }
        list
    }

    // Filter transactions for the currently selected date
    val transactionsForSelectedDay = remember(allTransactions, selectedCalendarMonth, selectedDay) {
        val cal = selectedCalendarMonth.clone() as Calendar
        cal.set(Calendar.DAY_OF_MONTH, selectedDay)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val startOfDay = cal.timeInMillis
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        val endOfDay = cal.timeInMillis

        allTransactions.filter { it.transaction.dateMillis in startOfDay..endOfDay }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = AppStrings.get("calendar_view", language),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // Month Navigation Header (< August 2026 >)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    val cal = selectedCalendarMonth.clone() as Calendar
                    cal.add(Calendar.MONTH, -1)
                    selectedCalendarMonth = cal
                }) {
                    Icon(imageVector = Icons.Rounded.ChevronLeft, contentDescription = "Previous Month")
                }

                Text(
                    text = monthYearFormat.format(selectedCalendarMonth.time),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                IconButton(onClick = {
                    val cal = selectedCalendarMonth.clone() as Calendar
                    cal.add(Calendar.MONTH, 1)
                    selectedCalendarMonth = cal
                }) {
                    Icon(imageVector = Icons.Rounded.ChevronRight, contentDescription = "Next Month")
                }
            }

            // Days of week header (Sun, Mon, Tue...)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa").forEach { day ->
                    Text(
                        text = day,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.width(36.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Calendar Grid
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .height(230.dp),
                    userScrollEnabled = false
                ) {
                    items(daysInMonth) { day ->
                        if (day == null) {
                            Box(modifier = Modifier.size(36.dp))
                        } else {
                            val isSelected = day == selectedDay

                            // Check if this day has transactions
                            val cal = selectedCalendarMonth.clone() as Calendar
                            cal.set(Calendar.DAY_OF_MONTH, day)
                            cal.set(Calendar.HOUR_OF_DAY, 0)
                            cal.set(Calendar.MINUTE, 0)
                            val start = cal.timeInMillis
                            cal.set(Calendar.HOUR_OF_DAY, 23)
                            cal.set(Calendar.MINUTE, 59)
                            val end = cal.timeInMillis
                            val dayTxs = allTransactions.filter { it.transaction.dateMillis in start..end }
                            val hasIncome = dayTxs.any { it.transaction.type == TransactionType.INCOME }
                            val hasExpense = dayTxs.any { it.transaction.type == TransactionType.EXPENSE }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                                    )
                                    .clickable { selectedDay = day }
                            ) {
                                Text(
                                    text = day.toString(),
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                                // Dots indicator
                                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                    if (hasIncome) {
                                        Box(
                                            modifier = Modifier
                                                .size(4.dp)
                                                .background(if (isSelected) Color.White else IncomeGreen, CircleShape)
                                        )
                                    }
                                    if (hasExpense) {
                                        Box(
                                            modifier = Modifier
                                                .size(4.dp)
                                                .background(if (isSelected) Color.White else ExpenseRed, CircleShape)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Transactions on Selected Day
            Text(
                text = "Transactions on $selectedDay ${monthYearFormat.format(selectedCalendarMonth.time)}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (transactionsForSelectedDay.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No records on this date",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 60.dp)
                ) {
                    items(transactionsForSelectedDay, key = { it.transaction.id }) { item ->
                        TransactionListItem(
                            item = item,
                            currency = currency,
                            language = language,
                            onClick = {}
                        )
                    }
                }
            }
        }
    }
}
