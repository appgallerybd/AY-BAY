package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.currency.AppCurrency
import com.example.core.localization.AppStrings
import com.example.core.localization.Language
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldPrimaryDark
import com.example.ui.viewmodel.FinanceViewModel

data class OnboardingStep(
    val titleEn: String,
    val titleBn: String,
    val descriptionEn: String,
    val descriptionBn: String,
    val icon: ImageVector
)

@Composable
fun OnboardingScreen(
    viewModel: FinanceViewModel,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val language by viewModel.language.collectAsStateWithLifecycle()
    val currency by viewModel.currency.collectAsStateWithLifecycle()

    var currentStep by remember { mutableStateOf(0) }

    val steps = listOf(
        OnboardingStep(
            titleEn = "Track Expenses with Precision",
            titleBn = "নির্ভুলভাবে আয়-ব্যয় হিসাব রাখুন",
            descriptionEn = "Manage your cash, bank accounts, and mobile wallets effortlessly with double-entry precision.",
            descriptionBn = "ক্যাশ, ব্যাংক এবং মোবাইল ওয়ালেটের প্রতিটি লেনদেন এক জায়গায় সহজে ট্র্যাক করুন।",
            icon = Icons.Rounded.AccountBalanceWallet
        ),
        OnboardingStep(
            titleEn = "Smart Budgets & Savings Goals",
            titleBn = "স্মার্ট বাজেট ও সঞ্চয়ের লক্ষ্য",
            descriptionEn = "Stay disciplined with category budgets and visual milestone trackers for long-term wealth.",
            descriptionBn = "নির্দিষ্ট খাতের জন্য বাজেট নির্ধারণ করুন এবং কাঙ্ক্ষিত সঞ্চয় লক্ষ্য অর্জন করুন।",
            icon = Icons.Rounded.Savings
        ),
        OnboardingStep(
            titleEn = "100% Offline & Private",
            titleBn = "সম্পূর্ণ অফলাইন ও নিরাপদ",
            descriptionEn = "Your financial data stays encrypted on your device. No cloud leaks, no tracking, complete privacy.",
            descriptionBn = "আপনার সকল আর্থিক তথ্য সম্পূর্ণ আপনার ডিভাইসে সুরক্ষিত থাকে। কোনো ট্র্যাকিং নেই।",
            icon = Icons.Rounded.Security
        )
    )

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Language & Currency Setup Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Language Switch
                FilterChip(
                    selected = true,
                    onClick = {
                        viewModel.setLanguage(if (language == Language.BANGLA) Language.ENGLISH else Language.BANGLA)
                    },
                    label = { Text(if (language == Language.BANGLA) "বাংলা" else "English", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    leadingIcon = { Icon(imageVector = Icons.Rounded.Language, contentDescription = null, modifier = Modifier.size(14.dp)) }
                )

                // Currency selector chip
                FilterChip(
                    selected = true,
                    onClick = {
                        val nextCurr = when (currency) {
                            AppCurrency.BDT -> AppCurrency.USD
                            AppCurrency.USD -> AppCurrency.INR
                            AppCurrency.INR -> AppCurrency.EUR
                            else -> AppCurrency.BDT
                        }
                        viewModel.setCurrency(nextCurr)
                    },
                    label = { Text("${currency.symbol} ${currency.code}", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                )
            }

            // Center Illustration & Text Content
            val step = steps[currentStep]
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .background(
                            Brush.linearGradient(listOf(EmeraldPrimary, EmeraldPrimaryDark)),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = step.icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(64.dp)
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = if (language == Language.BANGLA) step.titleBn else step.titleEn,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (language == Language.BANGLA) step.descriptionBn else step.descriptionEn,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
            }

            // Bottom Navigation Indicators & Next Button
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Step Indicator Dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    steps.indices.forEach { idx ->
                        Box(
                            modifier = Modifier
                                .height(6.dp)
                                .width(if (idx == currentStep) 24.dp else 6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(
                                    if (idx == currentStep) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (currentStep < steps.size - 1) {
                            currentStep++
                        } else {
                            viewModel.setOnboardingCompleted()
                            onComplete()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = if (currentStep < steps.size - 1) "Next / পরবর্তী" else AppStrings.get("get_started", language),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(imageVector = Icons.Rounded.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}
