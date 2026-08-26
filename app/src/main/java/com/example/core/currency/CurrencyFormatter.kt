package com.example.core.currency

import com.example.core.localization.Language
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object CurrencyFormatter {

    private val banglaDigits = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')

    fun format(
        amount: Double,
        currency: AppCurrency = AppCurrency.BDT,
        language: Language = Language.ENGLISH,
        includeSymbol: Boolean = true,
        compact: Boolean = false
    ): String {
        val formattedNumber = if (compact && Math.abs(amount) >= 100000) {
            formatCompact(amount, language)
        } else {
            val symbols = DecimalFormatSymbols(Locale.US)
            val formatter = DecimalFormat("#,##0.00", symbols)
            val raw = formatter.format(amount)
            if (language == Language.BANGLA) convertToBanglaDigits(raw) else raw
        }

        return if (includeSymbol) {
            "${currency.symbol} $formattedNumber"
        } else {
            formattedNumber
        }
    }

    private fun formatCompact(amount: Double, language: Language): String {
        val abs = Math.abs(amount)
        val sign = if (amount < 0) "-" else ""

        return if (language == Language.BANGLA) {
            when {
                abs >= 10000000 -> {
                    val cr = abs / 10000000.0
                    val text = String.format(Locale.US, "%.2f", cr)
                    sign + convertToBanglaDigits(text) + " কোটি"
                }
                abs >= 100000 -> {
                    val lakh = abs / 100000.0
                    val text = String.format(Locale.US, "%.2f", lakh)
                    sign + convertToBanglaDigits(text) + " লাখ"
                }
                abs >= 1000 -> {
                    val k = abs / 1000.0
                    val text = String.format(Locale.US, "%.1f", k)
                    sign + convertToBanglaDigits(text) + "হাজার"
                }
                else -> {
                    val text = String.format(Locale.US, "%.0f", abs)
                    sign + convertToBanglaDigits(text)
                }
            }
        } else {
            when {
                abs >= 1000000 -> {
                    val m = abs / 1000000.0
                    sign + String.format(Locale.US, "%.2fM", m)
                }
                abs >= 1000 -> {
                    val k = abs / 1000.0
                    sign + String.format(Locale.US, "%.1fK", k)
                }
                else -> {
                    sign + String.format(Locale.US, "%.0f", abs)
                }
            }
        }
    }

    fun convertToBanglaDigits(input: String): String {
        val sb = StringBuilder()
        for (ch in input) {
            if (ch in '0'..'9') {
                sb.append(banglaDigits[ch - '0'])
            } else {
                sb.append(ch)
            }
        }
        return sb.toString()
    }
}
