package com.example.ui.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.currency.AppCurrency
import com.example.core.localization.AppStrings
import com.example.core.localization.Language
import com.example.data.local.entity.AccountEntity
import com.example.data.local.entity.CategoryEntity
import com.example.data.local.entity.CategoryType
import com.example.data.local.entity.RecurrenceFrequency

@Composable
fun AddRecurringDialog(
    categories: List<CategoryEntity>,
    accounts: List<AccountEntity>,
    currency: AppCurrency,
    language: Language,
    onDismiss: () -> Unit,
    onSave: (title: String, amount: Double, categoryId: Long, accountId: Long, frequency: RecurrenceFrequency) -> Unit
) {
    var titleText by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var selectedCategoryId by remember {
        mutableStateOf(categories.firstOrNull { it.type == CategoryType.EXPENSE }?.id ?: 1L)
    }
    var selectedAccountId by remember { mutableStateOf(accounts.firstOrNull()?.id ?: 1L) }
    var selectedFrequency by remember { mutableStateOf(RecurrenceFrequency.MONTHLY) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = AppStrings.get("add_recurring", language),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = titleText,
                    onValueChange = { titleText = it },
                    label = { Text("Subscription / Bill Title") },
                    placeholder = { Text("e.g. Netflix, Wi-Fi, Gym, Rent") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter { ch -> ch.isDigit() || ch == '.' } },
                    label = { Text("${AppStrings.get("amount", language)} (${currency.symbol})") },
                    placeholder = { Text("1200") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "${AppStrings.get("frequency", language)}:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        RecurrenceFrequency.MONTHLY to AppStrings.get("frequency_monthly", language),
                        RecurrenceFrequency.WEEKLY to AppStrings.get("frequency_weekly", language),
                        RecurrenceFrequency.YEARLY to AppStrings.get("frequency_yearly", language)
                    ).forEach { (freq, label) ->
                        FilterChip(
                            selected = selectedFrequency == freq,
                            onClick = { selectedFrequency = freq },
                            label = { Text(label, fontSize = 11.sp) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountText.toDoubleOrNull() ?: return@Button
                    val title = if (titleText.isBlank()) "Subscription" else titleText.trim()
                    onSave(title, amt, selectedCategoryId, selectedAccountId, selectedFrequency)
                    onDismiss()
                },
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(AppStrings.get("save", language))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(AppStrings.get("cancel", language))
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}
