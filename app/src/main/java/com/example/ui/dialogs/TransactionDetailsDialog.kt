package com.example.ui.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.currency.AppCurrency
import com.example.core.currency.CurrencyFormatter
import com.example.core.localization.AppStrings
import com.example.core.localization.Language
import com.example.data.local.dao.TransactionWithDetails
import com.example.data.local.entity.TransactionType
import com.example.ui.components.CategoryAvatar
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TransactionDetailsDialog(
    item: TransactionWithDetails,
    currency: AppCurrency,
    language: Language,
    onDismiss: () -> Unit,
    onDelete: () -> Unit
) {
    val tx = item.transaction
    val category = item.category
    val account = item.account

    val isExpense = tx.type == TransactionType.EXPENSE
    val isIncome = tx.type == TransactionType.INCOME || tx.type == TransactionType.REFUND
    val isTransfer = tx.type == TransactionType.TRANSFER

    val amountColor = when {
        isExpense -> ExpenseRed
        isIncome -> IncomeGreen
        isTransfer -> TransferPurple
        else -> MaterialTheme.colorScheme.onSurface
    }

    val categoryTitle = if (language == Language.BANGLA && category?.nameBn?.isNotBlank() == true) {
        category.nameBn
    } else {
        category?.name ?: if (isTransfer) "Transfer" else "Other"
    }

    val sdf = SimpleDateFormat("dd MMMM yyyy, hh:mm a", Locale.US)
    val formattedDate = sdf.format(Date(tx.dateMillis))

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CategoryAvatar(
                    iconName = if (isTransfer) "swap_horiz" else category?.iconName ?: "category",
                    colorHex = if (isTransfer) "#7B1FA2" else category?.colorHex ?: "#78909C",
                    size = 40.dp,
                    iconSize = 20.dp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = categoryTitle,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = tx.type.name,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Large Amount
                Text(
                    text = CurrencyFormatter.format(tx.amount, currency, language),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = amountColor
                )

                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

                DetailRow(label = AppStrings.get("account", language), value = account?.name ?: "Wallet")
                if (tx.targetAccountId != null && item.targetAccount != null) {
                    DetailRow(label = AppStrings.get("to_account", language), value = item.targetAccount.name)
                }
                DetailRow(label = AppStrings.get("date", language), value = formattedDate)

                if (tx.note.isNotBlank()) {
                    DetailRow(label = AppStrings.get("note", language), value = tx.note)
                }
                if (tx.personName.isNotBlank()) {
                    DetailRow(label = AppStrings.get("person", language), value = tx.personName)
                }
                if (tx.tags.isNotBlank()) {
                    DetailRow(label = AppStrings.get("tags", language), value = tx.tags)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(AppStrings.get("cancel", language))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDelete()
                    onDismiss()
                },
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Icon(imageVector = Icons.Rounded.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(AppStrings.get("delete", language))
            }
        },
        shape = RoundedCornerShape(24.dp)
    )
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
