package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.localization.AppStrings
import com.example.core.localization.Language
import com.example.ui.theme.*

@Composable
fun QuickActionRow(
    language: Language,
    onAddIncome: () -> Unit,
    onAddExpense: () -> Unit,
    onAddTransfer: () -> Unit,
    onAddBudget: () -> Unit,
    onAddDebt: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        QuickActionItem(
            label = AppStrings.get("income", language),
            icon = Icons.Rounded.ArrowDownward,
            color = IncomeGreen,
            bgColor = IncomeGreenLight,
            onClick = onAddIncome
        )
        QuickActionItem(
            label = AppStrings.get("expense", language),
            icon = Icons.Rounded.ArrowUpward,
            color = ExpenseRed,
            bgColor = ExpenseRedLight,
            onClick = onAddExpense
        )
        QuickActionItem(
            label = AppStrings.get("transfer", language),
            icon = Icons.Rounded.SwapHoriz,
            color = TransferPurple,
            bgColor = TransferPurpleLight,
            onClick = onAddTransfer
        )
        QuickActionItem(
            label = AppStrings.get("budget", language),
            icon = Icons.Rounded.PieChart,
            color = TealSecondary,
            bgColor = Color(0xFFE0F2F1),
            onClick = onAddBudget
        )
        QuickActionItem(
            label = AppStrings.get("debt", language),
            icon = Icons.Rounded.Handshake,
            color = DebtAmber,
            bgColor = DebtAmberLight,
            onClick = onAddDebt
        )
    }
}

@Composable
private fun QuickActionItem(
    label: String,
    icon: ImageVector,
    color: Color,
    bgColor: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(bgColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
    }
}
