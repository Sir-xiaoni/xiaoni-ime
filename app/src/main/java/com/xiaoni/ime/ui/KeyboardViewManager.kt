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
    private lateinit var qwertyKeyboard: Keyboard
    private lateinit var symbolKeyboard: Keyboard
    
    private var keyboardListener: KeyboardView.OnKeyboardActionListener? = null
    
    init {
        try {
            // 初始化键盘
            qwertyKeyboard = Keyboard(context, R.xml.keyboard_qwerty)
            symbolKeyboard = Keyboard(context, R.xml.keyboard_symbols)
            
            initKeyboardView()
            initVoicePanel()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun initKeyboardView() {
        // 创建 KeyboardView
        keyboardView = KeyboardView(context, null).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                250.dpToPx()
            )
            keyboard = qwertyKeyboard
            isPreviewEnabled = true
            isEnabled = true
            visibility = View.VISIBLE
        }
        
        container.addView(keyboardView)
        currentKeyboard = qwertyKeyboard
    }
    
    private fun initVoicePanel() {
        // 动态添加 voice_panel 布局
        val voicePanelView = LayoutInflater.from(context).inflate(R.layout.voice_panel, container, false)
        container.addView(voicePanelView)
        voicePanel = voicePanelView.findViewById(R.id.voice_panel)
        
        voiceStatusText = voicePanel.findViewById(R.id.voice_status_text)
        voiceVolumeBar = voicePanel.findViewById(R.id.voice_volume_bar)
        voiceStopButton = voicePanel.findViewById(R.id.voice_stop_button)
        
        voiceStopButton.setOnClickListener {
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
    
    /**
     * dp 转 px
     */
    private fun Int.dpToPx(): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }
}