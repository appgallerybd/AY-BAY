package com.example.core.currency

enum class AppCurrency(
    val code: String,
    val symbol: String,
    val nameEn: String,
    val nameBn: String
) {
    BDT("BDT", "৳", "Bangladeshi Taka", "বাংলাদেশি টাকা"),
    USD("USD", "$", "US Dollar", "ইউএস ডলার"),
    EUR("EUR", "€", "Euro", "ইউরো"),
    GBP("GBP", "£", "British Pound", "ব্রিটিশ পাউন্ড"),
    INR("INR", "₹", "Indian Rupee", "ভারতীয় রুপি"),
    SAR("SAR", "﷼", "Saudi Riyal", "সৌদি রিয়াল"),
    AED("AED", "د.إ", "UAE Dirham", "ইউএই দিরহাম"),
    CAD("CAD", "CA$", "Canadian Dollar", "কানাডিয়ান ডলার"),
    AUD("AUD", "AU$", "Australian Dollar", "অস্ট্রেলিয়ান ডলার"),
    JPY("JPY", "¥", "Japanese Yen", "জাপানিজ ইয়েন");

    companion object {
        fun fromCode(code: String): AppCurrency {
            return entries.find { it.code.equals(code, ignoreCase = true) } ?: BDT
        }
    }
}
