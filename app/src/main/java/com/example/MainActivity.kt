package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.localization.AppStrings
import com.example.core.localization.Language
import com.example.data.preferences.AppThemeMode
import com.example.ui.screens.*
import com.example.ui.theme.FinFlowTheme
import com.example.ui.viewmodel.FinanceViewModel

enum class MainNavTab(val icon: ImageVector, val labelKey: String) {
    HOME(Icons.Rounded.Home, "home"),
    TRANSACTIONS(Icons.Rounded.ReceiptLong, "transactions"),
    BUDGETS(Icons.Rounded.PieChart, "budgets"),
    REPORTS(Icons.Rounded.BarChart, "reports"),
    MORE(Icons.Rounded.GridView, "more")
}

enum class SubScreen {
    NONE,
    ACCOUNTS,
    DEBTS,
    RECURRING,
    CALENDAR
}

class MainActivity : ComponentActivity() {

    private val viewModel: FinanceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
            val isDark = when (themeMode) {
                AppThemeMode.SYSTEM -> isSystemInDarkTheme()
                AppThemeMode.LIGHT -> false
                AppThemeMode.DARK -> true
            }

            FinFlowTheme(darkTheme = isDark) {
                MainAppContainer(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppContainer(viewModel: FinanceViewModel) {
    val language by viewModel.language.collectAsStateWithLifecycle()
    val isOnboardingCompleted by viewModel.isOnboardingCompleted.collectAsStateWithLifecycle()
    val isAppLockEnabled by viewModel.isAppLockEnabled.collectAsStateWithLifecycle()
    val isAppUnlocked by viewModel.isAppUnlocked.collectAsStateWithLifecycle()

    var currentTab by remember { mutableStateOf(MainNavTab.HOME) }
    var currentSubScreen by remember { mutableStateOf(SubScreen.NONE) }

    when {
        !isOnboardingCompleted -> {
            OnboardingScreen(
                viewModel = viewModel,
                onComplete = {
                    // Handled internally by viewModel
                }
            )
        }
        isAppLockEnabled && !isAppUnlocked -> {
            AppLockScreen(viewModel = viewModel)
        }
        else -> {
            // Main App Scaffold with Sub-screen routing
            if (currentSubScreen != SubScreen.NONE) {
                when (currentSubScreen) {
                    SubScreen.ACCOUNTS -> AccountsScreen(
                        viewModel = viewModel,
                        onNavigateBack = { currentSubScreen = SubScreen.NONE }
                    )
                    SubScreen.DEBTS -> DebtsScreen(
                        viewModel = viewModel,
                        onNavigateBack = { currentSubScreen = SubScreen.NONE }
                    )
                    SubScreen.RECURRING -> RecurringScreen(
                        viewModel = viewModel,
                        onNavigateBack = { currentSubScreen = SubScreen.NONE }
                    )
                    SubScreen.CALENDAR -> CalendarScreen(
                        viewModel = viewModel,
                        onNavigateBack = { currentSubScreen = SubScreen.NONE }
                    )
                    else -> currentSubScreen = SubScreen.NONE
                }
            } else {
                Scaffold(
                    bottomBar = {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface,
                            tonalElevation = 6.dp
                        ) {
                            MainNavTab.entries.forEach { tab ->
                                val isSelected = currentTab == tab
                                NavigationBarItem(
                                    selected = isSelected,
                                    onClick = { currentTab = tab },
                                    icon = {
                                        Icon(
                                            imageVector = tab.icon,
                                            contentDescription = AppStrings.get(tab.labelKey, language),
                                            modifier = Modifier.size(24.dp)
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = AppStrings.get(tab.labelKey, language),
                                            fontSize = 11.sp
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = MaterialTheme.colorScheme.primary,
                                        selectedTextColor = MaterialTheme.colorScheme.primary,
                                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                    )
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        AnimatedContent(
                            targetState = currentTab,
                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                            label = "TabTransition"
                        ) { tab ->
                            when (tab) {
                                MainNavTab.HOME -> HomeScreen(
                                    viewModel = viewModel,
                                    onNavigateToTransactions = { currentTab = MainNavTab.TRANSACTIONS },
                                    onNavigateToBudgets = { currentTab = MainNavTab.BUDGETS },
                                    onNavigateToMore = { currentTab = MainNavTab.MORE }
                                )
                                MainNavTab.TRANSACTIONS -> TransactionsScreen(viewModel = viewModel)
                                MainNavTab.BUDGETS -> BudgetsScreen(viewModel = viewModel)
                                MainNavTab.REPORTS -> ReportsScreen(viewModel = viewModel)
                                MainNavTab.MORE -> MoreHubScreen(
                                    viewModel = viewModel,
                                    onNavigateToAccounts = { currentSubScreen = SubScreen.ACCOUNTS },
                                    onNavigateToDebts = { currentSubScreen = SubScreen.DEBTS },
                                    onNavigateToRecurring = { currentSubScreen = SubScreen.RECURRING },
                                    onNavigateToCalendar = { currentSubScreen = SubScreen.CALENDAR }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
