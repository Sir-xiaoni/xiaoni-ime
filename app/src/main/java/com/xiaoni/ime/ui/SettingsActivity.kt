package com.xiaoni.ime.ui

import android.os.Bundle
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import com.xiaoni.ime.R
import com.xiaoni.ime.utils.PreferenceManager

/**
 * 设置页面
 */
class SettingsActivity : AppCompatActivity() {
    
    private lateinit var continuousModeSwitch: Switch
    private lateinit var autoSendSwitch: Switch
    private lateinit var soundSwitch: Switch
    private lateinit var vibrationSwitch: Switch
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        
        supportActionBar?.title = "小逆输入法设置"
        
        initViews()
        loadSettings()
    }
    
    private fun initViews() {
        continuousModeSwitch = findViewById(R.id.switch_continuous_mode)
        autoSendSwitch = findViewById(R.id.switch_auto_send)
        soundSwitch = findViewById(R.id.switch_sound)
        vibrationSwitch = findViewById(R.id.switch_vibration)
        
        continuousModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            PreferenceManager.setContinuousModeEnabled(isChecked)
        }
        
        autoSendSwitch.setOnCheckedChangeListener { _, isChecked ->
            PreferenceManager.setAutoSendEnabled(isChecked)
        }
        
        soundSwitch.setOnCheckedChangeListener { _, isChecked ->
            PreferenceManager.setSoundEnabled(isChecked)
        }
        
        vibrationSwitch.setOnCheckedChangeListener { _, isChecked ->
            PreferenceManager.setVibrationEnabled(isChecked)
        }
    }
    
    private fun loadSettings() {
        continuousModeSwitch.isChecked = PreferenceManager.isContinuousModeEnabled()
        autoSendSwitch.isChecked = PreferenceManager.isAutoSendEnabled()
        soundSwitch.isChecked = PreferenceManager.isSoundEnabled()
        vibrationSwitch.isChecked = PreferenceManager.isVibrationEnabled()
    }
}
