package com.example.data.preferences

import android.content.Context
import android.content.SharedPreferences
import com.example.core.currency.AppCurrency
import com.example.core.localization.Language
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.security.MessageDigest

enum class AppThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

class PreferenceManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("finflow_prefs", Context.MODE_PRIVATE)

    private val _language = MutableStateFlow(loadLanguage())
    val language: StateFlow<Language> = _language.asStateFlow()

    private val _currency = MutableStateFlow(loadCurrency())
    val currency: StateFlow<AppCurrency> = _currency.asStateFlow()

    private val _themeMode = MutableStateFlow(loadThemeMode())
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    private val _isAppLockEnabled = MutableStateFlow(prefs.getBoolean(KEY_APP_LOCK, false))
    val isAppLockEnabled: StateFlow<Boolean> = _isAppLockEnabled.asStateFlow()

    private val _isOnboardingCompleted = MutableStateFlow(prefs.getBoolean(KEY_ONBOARDING, false))
    val isOnboardingCompleted: StateFlow<Boolean> = _isOnboardingCompleted.asStateFlow()

    private val _isBalanceHidden = MutableStateFlow(prefs.getBoolean(KEY_HIDE_BALANCE, false))
    val isBalanceHidden: StateFlow<Boolean> = _isBalanceHidden.asStateFlow()

    private fun loadLanguage(): Language {
        val code = prefs.getString(KEY_LANG, Language.BANGLA.code)
        return if (code == Language.ENGLISH.code) Language.ENGLISH else Language.BANGLA
    }

    fun setLanguage(lang: Language) {
        prefs.edit().putString(KEY_LANG, lang.code).apply()
        _language.value = lang
    }

    private fun loadCurrency(): AppCurrency {
        val code = prefs.getString(KEY_CURRENCY, AppCurrency.BDT.code) ?: "BDT"
        return AppCurrency.fromCode(code)
    }

    fun setCurrency(curr: AppCurrency) {
        prefs.edit().putString(KEY_CURRENCY, curr.code).apply()
        _currency.value = curr
    }

    private fun loadThemeMode(): AppThemeMode {
        val mode = prefs.getString(KEY_THEME, AppThemeMode.SYSTEM.name) ?: AppThemeMode.SYSTEM.name
        return try {
            AppThemeMode.valueOf(mode)
        } catch (e: Exception) {
            AppThemeMode.SYSTEM
        }
    }

    fun setThemeMode(mode: AppThemeMode) {
        prefs.edit().putString(KEY_THEME, mode.name).apply()
        _themeMode.value = mode
    }

    fun setAppLock(enabled: Boolean, pin: String? = null) {
        val editor = prefs.edit().putBoolean(KEY_APP_LOCK, enabled)
        if (pin != null && pin.isNotBlank()) {
            editor.putString(KEY_PIN_HASH, hashPin(pin))
        }
        editor.apply()
        _isAppLockEnabled.value = enabled
    }

    fun verifyPin(pin: String): Boolean {
        val savedHash = prefs.getString(KEY_PIN_HASH, "") ?: ""
        if (savedHash.isEmpty()) return true
        return savedHash == hashPin(pin)
    }

    fun hasPinSet(): Boolean {
        val savedHash = prefs.getString(KEY_PIN_HASH, "") ?: ""
        return savedHash.isNotEmpty()
    }

    fun setOnboardingCompleted(completed: Boolean) {
        prefs.edit().putBoolean(KEY_ONBOARDING, completed).apply()
        _isOnboardingCompleted.value = completed
    }

    fun toggleBalanceHidden() {
        val newVal = !_isBalanceHidden.value
        prefs.edit().putBoolean(KEY_HIDE_BALANCE, newVal).apply()
        _isBalanceHidden.value = newVal
    }

    private fun hashPin(pin: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(pin.toByteArray())
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }

    companion object {
        private const val KEY_LANG = "key_lang"
        private const val KEY_CURRENCY = "key_currency"
        private const val KEY_THEME = "key_theme"
        private const val KEY_APP_LOCK = "key_app_lock"
        private const val KEY_PIN_HASH = "key_pin_hash"
        private const val KEY_ONBOARDING = "key_onboarding"
        private const val KEY_HIDE_BALANCE = "key_hide_balance"
    }
}
