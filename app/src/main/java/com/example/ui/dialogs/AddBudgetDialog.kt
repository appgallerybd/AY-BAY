package com.example.ui.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import com.example.data.local.entity.BudgetPeriod
import com.example.data.local.entity.CategoryEntity
import com.example.data.local.entity.CategoryType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBudgetDialog(
    categories: List<CategoryEntity>,
    currency: AppCurrency,
    language: Language,
    onDismiss: () -> Unit,
    onSave: (name: String, amount: Double, categoryId: Long?, period: BudgetPeriod, threshold: Int) -> Unit
) {
    var nameText by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf<Long?>(null) }
    var selectedPeriod by remember { mutableStateOf(BudgetPeriod.MONTHLY) }
    var warningThreshold by remember { mutableStateOf(80f) }

    val expenseCategories = remember(categories) {
        categories.filter { it.type == CategoryType.EXPENSE }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = AppStrings.get("create_budget", language),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = nameText,
                    onValueChange = { nameText = it },
                    label = { Text("Budget Name") },
                    placeholder = { Text("e.g. Monthly Dining, Grocery") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter { ch -> ch.isDigit() || ch == '.' } },
                    label = { Text("${AppStrings.get("budget_limit", language)} (${currency.symbol})") },
                    placeholder = { Text("e.g. 15000") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // Category selection dropdown / filter chip
                Text(
                    text = "Assign to Category (Optional):",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = selectedCategoryId == null,
                        onClick = { selectedCategoryId = null },
                        label = { Text("Overall", fontSize = 11.sp) }
                    )
                    expenseCategories.take(3).forEach { cat ->
                        val catName = if (language == Language.BANGLA && cat.nameBn.isNotBlank()) cat.nameBn else cat.name
                        FilterChip(
                            selected = selectedCategoryId == cat.id,
                            onClick = { selectedCategoryId = cat.id },
                            label = { Text(catName, fontSize = 11.sp) }
                        )
                    }
                }

                // Alert Threshold slider
                Column {
                    Text(
                        text = "Alert Threshold: ${warningThreshold.toInt()}%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Slider(
                        value = warningThreshold,
                        onValueChange = { warningThreshold = it },
                        valueRange = 50f..95f,
                        steps = 8
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountText.toDoubleOrNull() ?: return@Button
                    val name = if (nameText.isBlank()) "Monthly Budget" else nameText.trim()
                    onSave(name, amt, selectedCategoryId, selectedPeriod, warningThreshold.toInt())
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
