package com.xiaoni.ime.ui

import android.content.Context
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import com.xiaoni.ime.R

/**
 * 键盘视图管理器
 */
class KeyboardViewManager(
    private val context: Context,
    private val container: FrameLayout
) {
    
    private lateinit var keyboardView: KeyboardView
    private lateinit var voicePanel: View
    private lateinit var voiceStatusText: TextView
    private lateinit var voiceVolumeBar: ProgressBar
    private lateinit var voiceStopButton: ImageButton
    
    private var currentKeyboard: Keyboard? = null
    private var qwertyKeyboard: Keyboard
    private var symbolKeyboard: Keyboard
    
    private var keyboardListener: KeyboardView.OnKeyboardActionListener? = null
    
    init {
        // 初始化键盘
        qwertyKeyboard = Keyboard(context, R.xml.keyboard_qwerty)
        symbolKeyboard = Keyboard(context, R.xml.keyboard_symbols)
        
        initKeyboardView()
        initVoicePanel()
    }
    
    private fun initKeyboardView() {
        keyboardView = container.findViewById(R.id.keyboard_view) ?: run {
            val view = LayoutInflater.from(context).inflate(R.layout.keyboard_main, container, false)
            container.addView(view)
            view.findViewById(R.id.keyboard_view)
        }
        
        keyboardView.apply {
            keyboard = qwertyKeyboard
            isPreviewEnabled = true
            currentKeyboard = qwertyKeyboard
        }
    }
    
    private fun initVoicePanel() {
        voicePanel = container.findViewById(R.id.voice_panel) ?: run {
            val view = LayoutInflater.from(context).inflate(R.layout.voice_panel, container, false)
            container.addView(view)
            view
        }
        
        voiceStatusText = voicePanel.findViewById(R.id.voice_status_text)
        voiceVolumeBar = voicePanel.findViewById(R.id.voice_volume_bar)
        voiceStopButton = voicePanel.findViewById(R.id.voice_stop_button)
        
        voiceStopButton.setOnClickListener {
            // 停止语音输入
            hideVoicePanel()
        }
        
        voicePanel.visibility = View.GONE
    }
    
    fun setOnKeyboardActionListener(listener: KeyboardView.OnKeyboardActionListener) {
        keyboardListener = listener
        keyboardView.setOnKeyboardActionListener(listener)
    }
    
    /**
     * 切换键盘布局
     */
    fun switchKeyboard() {
        currentKeyboard = if (currentKeyboard == qwertyKeyboard) {
            symbolKeyboard
        } else {
            qwertyKeyboard
        }
        keyboardView.keyboard = currentKeyboard
    }
    
    /**
     * 根据输入类型更新键盘
     */
    fun updateKeyboardForInputType(inputType: Int) {
        // 根据输入框类型调整键盘
        when (inputType and android.text.InputType.TYPE_MASK_CLASS) {
            android.text.InputType.TYPE_CLASS_NUMBER,
            android.text.InputType.TYPE_CLASS_PHONE -> {
                // 数字键盘
            }
        }
    }
    
    /**
     * 显示语音输入面板
     */
    fun showVoicePanel() {
        keyboardView.visibility = View.GONE
        voicePanel.visibility = View.VISIBLE
        updateVoiceStatus("点击麦克风开始说话")
    }
    
    /**
     * 隐藏语音输入面板
     */
    fun hideVoicePanel() {
        voicePanel.visibility = View.GONE
        keyboardView.visibility = View.VISIBLE
    }
    
    /**
     * 更新语音状态文本
     */
    fun updateVoiceStatus(status: String) {
        voiceStatusText.text = status
    }
    
    /**
     * 更新音量显示
     */
    fun updateVoiceVolume(volume: Int) {
        voiceVolumeBar.progress = volume
    }
}
