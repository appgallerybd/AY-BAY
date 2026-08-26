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
import com.example.data.local.entity.SavingsGoalEntity

@Composable
fun AddSavingsGoalDialog(
    currency: AppCurrency,
    language: Language,
    onDismiss: () -> Unit,
    onSave: (title: String, targetAmount: Double, initialSaved: Double, deadlineDays: Int) -> Unit
) {
    var titleText by remember { mutableStateOf("") }
    var targetAmountText by remember { mutableStateOf("") }
    var initialSavedText by remember { mutableStateOf("") }
    var targetDays by remember { mutableStateOf(90) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = AppStrings.get("create_goal", language),
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
                    label = { Text("Goal Title") },
                    placeholder = { Text("e.g. New Laptop, Emergency Fund") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = targetAmountText,
                    onValueChange = { targetAmountText = it.filter { ch -> ch.isDigit() || ch == '.' } },
                    label = { Text("${AppStrings.get("target_amount", language)} (${currency.symbol})") },
                    placeholder = { Text("100000") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = initialSavedText,
                    onValueChange = { initialSavedText = it.filter { ch -> ch.isDigit() || ch == '.' } },
                    label = { Text("Already Saved Amount (${currency.symbol})") },
                    placeholder = { Text("0") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Deadline Timeline:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(30 to "1 Month", 90 to "3 Months", 180 to "6 Months", 365 to "1 Year").forEach { (days, label) ->
                        FilterChip(
                            selected = targetDays == days,
                            onClick = { targetDays = days },
                            label = { Text(label, fontSize = 11.sp) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val target = targetAmountText.toDoubleOrNull() ?: return@Button
                    val initial = initialSavedText.toDoubleOrNull() ?: 0.0
                    val title = if (titleText.isBlank()) "Savings Milestone" else titleText.trim()
                    onSave(title, target, initial, targetDays)
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

@Composable
fun ContributeGoalDialog(
    goal: SavingsGoalEntity,
    accounts: List<AccountEntity>,
    currency: AppCurrency,
    language: Language,
    onDismiss: () -> Unit,
    onContribute: (amount: Double, sourceAccountId: Long?) -> Unit
) {
    var amountText by remember { mutableStateOf("") }
    var selectedAccountId by remember { mutableStateOf(accounts.firstOrNull()?.id) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "${AppStrings.get("add_contribution", language)}: ${goal.title}",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter { ch -> ch.isDigit() || ch == '.' } },
                    label = { Text("${AppStrings.get("amount", language)} (${currency.symbol})") },
                    placeholder = { Text("5000") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Deduct from Account (Optional):",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    accounts.take(3).forEach { acc ->
                        FilterChip(
                            selected = selectedAccountId == acc.id,
                            onClick = { selectedAccountId = acc.id },
                            label = { Text(acc.name, fontSize = 11.sp) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountText.toDoubleOrNull() ?: return@Button
                    onContribute(amt, selectedAccountId)
                    onDismiss()
                },
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(AppStrings.get("add", language))
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
