package com.xiaoni.ime.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * 偏好设置管理器
 */
object PreferenceManager {
    
    private const val PREF_NAME = "xiaoni_ime_prefs"
    
    // 设置项 Key
    private const val KEY_CONTINUOUS_MODE = "continuous_mode"
    private const val KEY_AUTO_SEND = "auto_send"
    private const val KEY_SOUND = "sound"
    private const val KEY_VIBRATION = "vibration"
    
    private lateinit var prefs: SharedPreferences
    
    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }
    
    // ==================== 连续模式 ====================
    
    fun isContinuousModeEnabled(): Boolean {
        return if (::prefs.isInitialized) prefs.getBoolean(KEY_CONTINUOUS_MODE, true) else true
    }
    
    fun setContinuousModeEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_CONTINUOUS_MODE, enabled).apply()
    }
    
    // ==================== 自动发送 ====================
    
    fun isAutoSendEnabled(): Boolean {
        return if (::prefs.isInitialized) prefs.getBoolean(KEY_AUTO_SEND, true) else true
    }
    
    fun setAutoSendEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_AUTO_SEND, enabled).apply()
    }
    
    // ==================== 按键音效 ====================
    
    fun isSoundEnabled(): Boolean {
        return if (::prefs.isInitialized) prefs.getBoolean(KEY_SOUND, true) else true
    }
    
    fun setSoundEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SOUND, enabled).apply()
    }
    
    // ==================== 按键震动 ====================
    
    fun isVibrationEnabled(): Boolean {
        return if (::prefs.isInitialized) prefs.getBoolean(KEY_VIBRATION, true) else true
    }
    
    fun setVibrationEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_VIBRATION, enabled).apply()
    }
}
