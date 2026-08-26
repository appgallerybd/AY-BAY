package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.currency.AppCurrency
import com.example.core.localization.AppStrings
import com.example.core.localization.Language
import com.example.data.local.entity.AccountEntity
import com.example.data.local.entity.CategoryEntity
import com.example.data.local.entity.CategoryType
import com.example.data.local.entity.TransactionType
import com.example.ui.components.CategoryAvatar
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTransactionSheet(
    categories: List<CategoryEntity>,
    accounts: List<AccountEntity>,
    currency: AppCurrency,
    language: Language,
    initialType: TransactionType = TransactionType.EXPENSE,
    onDismiss: () -> Unit,
    onSave: (
        amount: Double,
        type: TransactionType,
        categoryId: Long,
        accountId: Long,
        targetAccountId: Long?,
        dateMillis: Long,
        note: String,
        tags: String,
        person: String
    ) -> Unit
) {
    var selectedType by remember { mutableStateOf(initialType) }
    var amountText by remember { mutableStateOf("") }
    var selectedCategoryId by remember {
        mutableStateOf(
            categories.firstOrNull {
                if (selectedType == TransactionType.INCOME) it.type == CategoryType.INCOME else it.type == CategoryType.EXPENSE
            }?.id ?: 1L
        )
    }
    var selectedAccountId by remember { mutableStateOf(accounts.firstOrNull()?.id ?: 1L) }
    var targetAccountId by remember { mutableStateOf(accounts.getOrNull(1)?.id ?: accounts.firstOrNull()?.id ?: 1L) }
    var noteText by remember { mutableStateOf("") }
    var personText by remember { mutableStateOf("") }
    var tagsText by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val relevantCategories = remember(categories, selectedType) {
        if (selectedType == TransactionType.INCOME) {
            categories.filter { it.type == CategoryType.INCOME }
        } else {
            categories.filter { it.type == CategoryType.EXPENSE }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header: Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when (selectedType) {
                        TransactionType.INCOME -> AppStrings.get("add_income", language)
                        TransactionType.TRANSFER -> AppStrings.get("add_transfer", language)
                        else -> AppStrings.get("add_expense", language)
                    },
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Rounded.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Type Segmented Control (Expense | Income | Transfer)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Expense
                TypeTabButton(
                    title = AppStrings.get("expense", language),
                    isSelected = selectedType == TransactionType.EXPENSE,
                    activeColor = ExpenseRed,
                    onClick = {
                        selectedType = TransactionType.EXPENSE
                        selectedCategoryId = categories.firstOrNull { it.type == CategoryType.EXPENSE }?.id ?: 1L
                    },
                    modifier = Modifier.weight(1f)
                )
                // Income
                TypeTabButton(
                    title = AppStrings.get("income", language),
                    isSelected = selectedType == TransactionType.INCOME,
                    activeColor = IncomeGreen,
                    onClick = {
                        selectedType = TransactionType.INCOME
                        selectedCategoryId = categories.firstOrNull { it.type == CategoryType.INCOME }?.id ?: 14L
                    },
                    modifier = Modifier.weight(1f)
                )
                // Transfer
                TypeTabButton(
                    title = AppStrings.get("transfer", language),
                    isSelected = selectedType == TransactionType.TRANSFER,
                    activeColor = TransferPurple,
                    onClick = {
                        selectedType = TransactionType.TRANSFER
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Amount Input Card with Currency Symbol
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = AppStrings.get("amount", language),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = currency.symbol,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = amountText,
                            onValueChange = { amountText = it.filter { ch -> ch.isDigit() || ch == '.' } },
                            placeholder = { Text("0.00", fontSize = 28.sp, fontWeight = FontWeight.Bold) },
                            textStyle = LocalTextStyle.current.copy(
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                textAlign = TextAlign.Start
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent
                            ),
                            modifier = Modifier.widthIn(min = 120.dp, max = 220.dp)
                        )
                    }

                    // Quick Increment Chips (+100, +500, +1000, +5000)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        listOf(100, 500, 1000, 5000).forEach { inc ->
                            SuggestionChip(
                                onClick = {
                                    val current = amountText.toDoubleOrNull() ?: 0.0
                                    amountText = (current + inc).toInt().toString()
                                },
                                label = { Text("+$inc", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                }
            }

            // Quick Smart Presets Row
            Text(
                text = "Quick Presets",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 12.dp, bottom = 6.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val presets = listOf(
                    Triple("Lunch ৳250", 250.0, "Lunch"),
                    Triple("Tea/Snacks ৳60", 60.0, "Tea & Snacks"),
                    Triple("Grocery ৳1500", 1500.0, "Grocery"),
                    Triple("Rickshaw ৳80", 80.0, "Rickshaw/Fare"),
                    Triple("Internet ৳1200", 1200.0, "Internet Bill")
                )
                presets.forEach { (label, amt, note) ->
                    AssistChip(
                        onClick = {
                            amountText = amt.toInt().toString()
                            noteText = note
                        },
                        label = { Text(label, fontSize = 11.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Category Selection (if not Transfer)
            if (selectedType != TransactionType.TRANSFER) {
                Text(
                    text = AppStrings.get("category", language),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 190.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(relevantCategories) { cat ->
                        val isSelected = cat.id == selectedCategoryId
                        val catName = if (language == Language.BANGLA && cat.nameBn.isNotBlank()) cat.nameBn else cat.name

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                )
                                .clickable { selectedCategoryId = cat.id }
                                .padding(8.dp)
                        ) {
                            CategoryAvatar(
                                iconName = cat.iconName,
                                colorHex = cat.colorHex,
                                size = 36.dp,
                                iconSize = 18.dp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = catName,
                                fontSize = 10.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Account / Wallet Selector
            Text(
                text = if (selectedType == TransactionType.TRANSFER) AppStrings.get("from_account", language) else AppStrings.get("account", language),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                accounts.forEach { acc ->
                    val isSelected = acc.id == selectedAccountId
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedAccountId = acc.id },
                        label = { Text(acc.name, fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Rounded.AccountBalanceWallet,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    )
                }
            }

            // Target Account for Transfer
            if (selectedType == TransactionType.TRANSFER) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = AppStrings.get("to_account", language),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    accounts.forEach { acc ->
                        val isSelected = acc.id == targetAccountId
                        FilterChip(
                            selected = isSelected,
                            onClick = { targetAccountId = acc.id },
                            label = { Text(acc.name, fontSize = 12.sp) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Rounded.AccountBalance,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Note / Description Field
            OutlinedTextField(
                value = noteText,
                onValueChange = { noteText = it },
                label = { Text(AppStrings.get("note", language)) },
                placeholder = { Text("e.g. Lunch with team, monthly wifi bill...") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Optional Person & Tags Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = personText,
                    onValueChange = { personText = it },
                    label = { Text(AppStrings.get("person", language)) },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = tagsText,
                    onValueChange = { tagsText = it },
                    label = { Text(AppStrings.get("tags", language)) },
                    placeholder = { Text("Family, Trip") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.weight(1f)
                )
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Save Button
            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull()
                    if (amount == null || amount <= 0.0) {
                        errorMessage = "Please enter a valid amount greater than 0"
                        return@Button
                    }
                    if (selectedType == TransactionType.TRANSFER && selectedAccountId == targetAccountId) {
                        errorMessage = "Source and Target accounts cannot be the same"
                        return@Button
                    }
                    onSave(
                        amount,
                        selectedType,
                        selectedCategoryId,
                        selectedAccountId,
                        if (selectedType == TransactionType.TRANSFER) targetAccountId else null,
                        System.currentTimeMillis(),
                        noteText.trim(),
                        tagsText.trim(),
                        personText.trim()
                    )
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = when (selectedType) {
                        TransactionType.INCOME -> IncomeGreen
                        TransactionType.TRANSFER -> TransferPurple
                        else -> MaterialTheme.colorScheme.primary
                    }
                )
            ) {
                Icon(imageVector = Icons.Rounded.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = AppStrings.get("save_transaction", language),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun TypeTabButton(
    title: String,
    isSelected: Boolean,
    activeColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) activeColor else Color.Transparent)
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
