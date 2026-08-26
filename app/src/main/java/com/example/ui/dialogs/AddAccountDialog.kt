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
import com.example.data.local.entity.AccountType

@Composable
fun AddAccountDialog(
    currency: AppCurrency,
    language: Language,
    onDismiss: () -> Unit,
    onSave: (name: String, type: AccountType, initialBalance: Double, colorHex: String, maskedNumber: String) -> Unit
) {
    var nameText by remember { mutableStateOf("") }
    var balanceText by remember { mutableStateOf("") }
    var numberText by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(AccountType.BANK) }

    val colorHex = when (selectedType) {
        AccountType.CASH -> "#2E7D32"
        AccountType.BANK -> "#1565C0"
        AccountType.MOBILE_WALLET -> "#D81B60"
        AccountType.CREDIT_CARD -> "#6A1B9A"
        AccountType.SAVINGS -> "#00897B"
        AccountType.OTHER -> "#455A64"
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add New Account / Wallet",
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
                    value = nameText,
                    onValueChange = { nameText = it },
                    label = { Text("Account Name") },
                    placeholder = { Text("e.g. City Bank, Nagad, Cash") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = balanceText,
                    onValueChange = { balanceText = it.filter { ch -> ch.isDigit() || ch == '.' } },
                    label = { Text("Initial Balance (${currency.symbol})") },
                    placeholder = { Text("0.00") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = numberText,
                    onValueChange = { numberText = it },
                    label = { Text("Account / Card Number (Masked)") },
                    placeholder = { Text("e.g. ****1234 or 017***") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Account Type:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        AccountType.BANK to "Bank",
                        AccountType.MOBILE_WALLET to "Mobile Wallet",
                        AccountType.CASH to "Cash",
                        AccountType.SAVINGS to "Savings"
                    ).forEach { (type, label) ->
                        FilterChip(
                            selected = selectedType == type,
                            onClick = { selectedType = type },
                            label = { Text(label, fontSize = 11.sp) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val bal = balanceText.toDoubleOrNull() ?: 0.0
                    val name = if (nameText.isBlank()) "New Account" else nameText.trim()
                    onSave(name, selectedType, bal, colorHex, numberText.trim())
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
