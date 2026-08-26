package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.currency.AppCurrency
import com.example.core.currency.CurrencyFormatter
import com.example.core.localization.Language
import com.example.data.local.dao.TransactionWithDetails
import com.example.data.local.entity.TransactionType
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TransactionListItem(
    item: TransactionWithDetails,
    currency: AppCurrency,
    language: Language,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
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

    val amountPrefix = when {
        isExpense -> "- "
        isIncome -> "+ "
        else -> ""
    }

    val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.US)
    val formattedDate = sdf.format(Date(tx.dateMillis))

    val categoryTitle = if (language == Language.BANGLA && category?.nameBn?.isNotBlank() == true) {
        category.nameBn
    } else {
        category?.name ?: if (isTransfer) "Transfer" else "Other"
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category Icon
            CategoryAvatar(
                iconName = if (isTransfer) "swap_horiz" else category?.iconName ?: "category",
                colorHex = if (isTransfer) "#7B1FA2" else category?.colorHex ?: "#78909C",
                size = 44.dp,
                iconSize = 22.dp
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Main Info: Title + Account + Date
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = categoryTitle,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Account badge
                    Text(
                        text = account?.name ?: "Wallet",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (tx.note.isNotBlank()) {
                        Text(
                            text = " • ${tx.note}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = formattedDate,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Amount Display
            Text(
                text = amountPrefix + CurrencyFormatter.format(tx.amount, currency, language),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = amountColor
            )
        }
    }
}
