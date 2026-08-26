package com.example.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.currency.AppCurrency
import com.example.core.localization.AppStrings
import com.example.core.localization.Language
import com.example.data.preferences.AppThemeMode
import com.example.ui.dialogs.SetPinDialog
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.ExpenseRed
import com.example.ui.viewmodel.FinanceViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreHubScreen(
    viewModel: FinanceViewModel,
    onNavigateToAccounts: () -> Unit,
    onNavigateToDebts: () -> Unit,
    onNavigateToRecurring: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val language by viewModel.language.collectAsStateWithLifecycle()
    val currency by viewModel.currency.collectAsStateWithLifecycle()
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val isAppLockEnabled by viewModel.isAppLockEnabled.collectAsStateWithLifecycle()

    var showCurrencyDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showSetPinDialog by remember { mutableStateOf(false) }
    var showClearDataConfirmDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = AppStrings.get("more", language),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Finance Management Tools Section
            item {
                Text(
                    text = "Financial Tools",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column {
                        HubMenuItem(
                            title = AppStrings.get("accounts_wallets", language),
                            subtitle = "Cash, Bank, Mobile Wallets",
                            icon = Icons.Rounded.AccountBalanceWallet,
                            onClick = onNavigateToAccounts
                        )
                        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                        HubMenuItem(
                            title = AppStrings.get("debts_loans", language),
                            subtitle = "Receivables & Payables tracker",
                            icon = Icons.Rounded.Handshake,
                            onClick = onNavigateToDebts
                        )
                        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                        HubMenuItem(
                            title = AppStrings.get("recurring_bills", language),
                            subtitle = "Monthly subscriptions & utility bills",
                            icon = Icons.Rounded.Subscriptions,
                            onClick = onNavigateToRecurring
                        )
                        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                        HubMenuItem(
                            title = AppStrings.get("calendar_view", language),
                            subtitle = "Day-by-day transaction matrix",
                            icon = Icons.Rounded.CalendarMonth,
                            onClick = onNavigateToCalendar
                        )
                    }
                }
            }

            // Preferences Section (Currency, Language, Theme, Security)
            item {
                Text(
                    text = AppStrings.get("preferences", language),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 4.dp, top = 6.dp)
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column {
                        // Currency
                        HubMenuItem(
                            title = AppStrings.get("currency", language),
                            subtitle = "${currency.code} (${currency.symbol})",
                            icon = Icons.Rounded.AttachMoney,
                            onClick = { showCurrencyDialog = true }
                        )
                        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                        // Language
                        HubMenuItem(
                            title = AppStrings.get("language", language),
                            subtitle = if (language == Language.BANGLA) "বাংলা (Bangla)" else "English",
                            icon = Icons.Rounded.Language,
                            onClick = { showLanguageDialog = true }
                        )
                        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                        // Theme
                        HubMenuItem(
                            title = AppStrings.get("theme", language),
                            subtitle = when (themeMode) {
                                AppThemeMode.SYSTEM -> AppStrings.get("system_default", language)
                                AppThemeMode.LIGHT -> AppStrings.get("light_mode", language)
                                AppThemeMode.DARK -> AppStrings.get("dark_mode", language)
                            },
                            icon = Icons.Rounded.DarkMode,
                            onClick = { showThemeDialog = true }
                        )
                        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                        // Security PIN Lock
                        HubMenuItemWithSwitch(
                            title = AppStrings.get("app_lock", language),
                            subtitle = if (isAppLockEnabled) "4-Digit PIN Protected" else "Disabled",
                            icon = Icons.Rounded.Security,
                            isChecked = isAppLockEnabled,
                            onCheckedChange = { enable ->
                                if (enable) {
                                    showSetPinDialog = true
                                } else {
                                    viewModel.setAppLock(false)
                                    Toast.makeText(context, "App lock disabled", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }
                }
            }

            // Data Management Section (Export, Demo Data, Reset)
            item {
                Text(
                    text = AppStrings.get("backup_export", language),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 4.dp, top = 6.dp)
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column {
                        HubMenuItem(
                            title = AppStrings.get("export_csv", language),
                            subtitle = "Excel-ready CSV spreadsheet",
                            icon = Icons.Rounded.TableChart,
                            onClick = {
                                coroutineScope.launch {
                                    val csv = viewModel.getExportCsvString()
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, csv)
                                        type = "text/csv"
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, "Share CSV Export"))
                                }
                            }
                        )
                        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                        HubMenuItem(
                            title = AppStrings.get("backup_json", language),
                            subtitle = "Encrypted local JSON data backup",
                            icon = Icons.Rounded.Backup,
                            onClick = {
                                coroutineScope.launch {
                                    val json = viewModel.getExportJsonString()
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, json)
                                        type = "application/json"
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, "Share JSON Backup"))
                                }
                            }
                        )
                        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                        HubMenuItem(
                            title = "Preload Sample Data",
                            subtitle = "Add sample transactions for instant preview",
                            icon = Icons.Rounded.PlaylistAddCheck,
                            onClick = {
                                viewModel.loadSampleData {
                                    Toast.makeText(context, "Sample data added successfully!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                        HubMenuItem(
                            title = AppStrings.get("clear_data", language),
                            subtitle = "Reset database & start fresh",
                            icon = Icons.Rounded.DeleteForever,
                            iconColor = ExpenseRed,
                            onClick = { showClearDataConfirmDialog = true }
                        )
                    }
                }
            }

            // About & Privacy Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column {
                        HubMenuItem(
                            title = AppStrings.get("about_app", language),
                            subtitle = "FinFlow v1.0.0 • Offline-First Personal Finance",
                            icon = Icons.Rounded.Info,
                            onClick = { showAboutDialog = true }
                        )
                    }
                }
            }
        }
    }

    // Currency Selection Dialog
    if (showCurrencyDialog) {
        AlertDialog(
            onDismissRequest = { showCurrencyDialog = false },
            title = { Text(AppStrings.get("currency", language), fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    AppCurrency.entries.forEach { curr ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    viewModel.setCurrency(curr)
                                    showCurrencyDialog = false
                                }
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val currName = if (language == Language.BANGLA) curr.nameBn else curr.nameEn
                            Text("${curr.symbol}  ${curr.code} - $currName", fontSize = 14.sp)
                            if (currency == curr) {
                                Icon(imageVector = Icons.Rounded.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCurrencyDialog = false }) {
                    Text(AppStrings.get("cancel", language))
                }
            }
        )
    }

    // Language Dialog
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text(AppStrings.get("language", language), fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Language.entries.forEach { lang ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    viewModel.setLanguage(lang)
                                    showLanguageDialog = false
                                }
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(lang.displayName, fontSize = 14.sp)
                            if (language == lang) {
                                Icon(imageVector = Icons.Rounded.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text(AppStrings.get("cancel", language))
                }
            }
        )
    }

    // Theme Dialog
    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text(AppStrings.get("theme", language), fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf(
                        AppThemeMode.SYSTEM to AppStrings.get("system_default", language),
                        AppThemeMode.LIGHT to AppStrings.get("light_mode", language),
                        AppThemeMode.DARK to AppStrings.get("dark_mode", language)
                    ).forEach { (mode, label) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    viewModel.setThemeMode(mode)
                                    showThemeDialog = false
                                }
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(label, fontSize = 14.sp)
                            if (themeMode == mode) {
                                Icon(imageVector = Icons.Rounded.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text(AppStrings.get("cancel", language))
                }
            }
        )
    }

    // Set PIN Dialog
    if (showSetPinDialog) {
        SetPinDialog(
            language = language,
            onDismiss = { showSetPinDialog = false },
            onSavePin = { pin ->
                viewModel.setAppLock(true, pin)
                showSetPinDialog = false
                Toast.makeText(context, "PIN Protection Enabled!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Clear Data Confirmation
    if (showClearDataConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearDataConfirmDialog = false },
            title = { Text("Confirm Clear All Data?", fontWeight = FontWeight.Bold) },
            text = { Text("This will permanently delete all recorded transactions, budgets, goals, and debts from your device.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllData {
                            showClearDataConfirmDialog = false
                            Toast.makeText(context, "All data wiped cleanly.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ExpenseRed)
                ) {
                    Text("Delete Everything")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDataConfirmDialog = false }) {
                    Text(AppStrings.get("cancel", language))
                }
            }
        )
    }

    // About Dialog
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Rounded.AccountBalanceWallet, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("FinFlow", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("FinFlow — Premium Income & Expense Management System", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Text("Version 1.0.0 Production Ready", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "FinFlow is an offline-first personal finance application crafted with modern Android architecture. Your data is stored 100% locally on your device with complete privacy, zero third-party tracking, and seamless export capabilities.",
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
private fun HubMenuItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(iconColor.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Icon(
            imageVector = Icons.Rounded.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun HubMenuItemWithSwitch(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange
        )
    }
}
