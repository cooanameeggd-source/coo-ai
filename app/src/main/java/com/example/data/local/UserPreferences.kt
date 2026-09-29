package com.example.data.local

import android.content.Context
import android.content.SharedPreferences

class UserPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("akin_ai_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_PASSCODE = "passcode"
        private const val KEY_TTS_ENABLED = "tts_enabled"
        private const val KEY_MATCHING_MODE = "matching_mode"
        const val DEFAULT_PASSCODE = "3345687"
    }

    var passcode: String
        get() = prefs.getString(KEY_PASSCODE, DEFAULT_PASSCODE) ?: DEFAULT_PASSCODE
        set(value) = prefs.edit().putString(KEY_PASSCODE, value).apply()

    var isTtsEnabled: Boolean
        get() = prefs.getBoolean(KEY_TTS_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_TTS_ENABLED, value).apply()

    var matchingMode: String
        get() = prefs.getString(KEY_MATCHING_MODE, "smart") ?: "smart" // "exact", "smart", "ai"
        set(value) = prefs.edit().putString(KEY_MATCHING_MODE, value).apply()

    fun resetPasscodeToDefault() {
        passcode = DEFAULT_PASSCODE
    }
}
