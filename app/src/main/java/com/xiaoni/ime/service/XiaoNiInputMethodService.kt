package com.xiaoni.ime.service

import android.inputmethodservice.InputMethodService
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.media.AudioManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.widget.FrameLayout
import com.xiaoni.ime.R
import com.xiaoni.ime.ui.KeyboardViewManager
import com.xiaoni.ime.voice.VoiceInputManager
import com.xiaoni.ime.utils.PreferenceManager

/**
 * 小逆输入法核心服务
 */
class XiaoNiInputMethodService : InputMethodService(), 
    KeyboardView.OnKeyboardActionListener,
    VoiceInputManager.VoiceCallback {
    
    companion object {
        const val TAG = "XiaoNiIME"
    }
    
    private lateinit var keyboardContainer: FrameLayout
    private lateinit var keyboardViewManager: KeyboardViewManager
    private lateinit var voiceInputManager: VoiceInputManager
    
    private var isVoiceMode = false
    private var isContinuousMode = false
    
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "输入法服务创建")
        voiceInputManager = VoiceInputManager(this, this)
    }
    
    override fun onEvaluateFullscreenMode(): Boolean {
        // 不使用全屏模式，这样键盘会在底部弹出
        return false
    }
    
    override fun onComputeInsets(outInsets: Insets?) {
        super.onComputeInsets(outInsets)
        // 确保键盘显示在屏幕底部
        outInsets?.contentTopInsets = outInsets?.visibleTopInsets
    }
    
    override fun onCreateInputView(): View {
        Log.d(TAG, "创建输入视图")
        keyboardContainer = LayoutInflater.from(this)
            .inflate(R.layout.keyboard_container, null) as FrameLayout
        
        keyboardViewManager = KeyboardViewManager(this, keyboardContainer)
        keyboardViewManager.setOnKeyboardActionListener(this)
        
        return keyboardContainer
    }
    
    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        super.onStartInput(attribute, restarting)
        Log.d(TAG, "开始输入")
        
        // 根据输入框类型调整键盘（确保视图已初始化）
        attribute?.let {
            if (::keyboardViewManager.isInitialized) {
                keyboardViewManager.updateKeyboardForInputType(it.inputType)
            }
        }
    }
    
    override fun onFinishInput() {
        super.onFinishInput()
        Log.d(TAG, "结束输入")
        voiceInputManager.stopListening()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        voiceInputManager.destroy()
    }
    
    // ==================== 键盘事件处理 ====================
    
    override fun onPress(primaryCode: Int) {
        playClickSound()
        vibrate()
    }
    
    override fun onRelease(primaryCode: Int) {}
    
    override fun onKey(primaryCode: Int, keyCodes: IntArray?) {
        val ic = currentInputConnection ?: return
        
        when (primaryCode) {
            Keyboard.KEYCODE_DELETE -> {
                ic.deleteSurroundingText(1, 0)
            }
            Keyboard.KEYCODE_DONE -> {
                ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
                ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER))
            }
            Keyboard.KEYCODE_CANCEL -> {
                requestHideSelf(0)
            }
            // 语音输入键
            -100 -> {
                toggleVoiceInput()
            }
            // 切换键盘
            -101 -> {
                keyboardViewManager.switchKeyboard()
            }
            // 空格
            32 -> {
                ic.commitText(" ", 1)
            }
            // 普通字符
            else -> {
                if (primaryCode >= 32) {
                    ic.commitText(primaryCode.toChar().toString(), 1)
                }
            }
        }
    }
    
    override fun onText(text: CharSequence?) {
        text?.let {
            currentInputConnection?.commitText(it, 1)
        }
    }
    
    override fun swipeLeft() {}
    override fun swipeRight() {}
    override fun swipeDown() {}
    override fun swipeUp() {}
    
    // ==================== 语音输入 ====================
    
    private fun toggleVoiceInput() {
        if (isVoiceMode) {
            stopVoiceInput()
        } else {
            startVoiceInput()
        }
    }
    
    private fun startVoiceInput() {
        isVoiceMode = true
        isContinuousMode = PreferenceManager.isContinuousModeEnabled()
        
        keyboardViewManager.showVoicePanel()
        voiceInputManager.startListening()
        
        Log.d(TAG, "开始语音输入，连续模式: $isContinuousMode")
    }
    
    private fun stopVoiceInput() {
        isVoiceMode = false
        voiceInputManager.stopListening()
        keyboardViewManager.hideVoicePanel()
        
        Log.d(TAG, "停止语音输入")
    }
    
    // ==================== VoiceCallback ====================
    
    override fun onVoiceStart() {
        keyboardViewManager.updateVoiceStatus("正在聆听...")
    }
    
    override fun onVoiceResult(text: String) {
        Log.d(TAG, "语音识别结果: $text")
        
        // 提交文本
        currentInputConnection?.commitText(text, 1)
        
        // 自动发送（回车）
        if (PreferenceManager.isAutoSendEnabled()) {
            currentInputConnection?.sendKeyEvent(
                KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER)
            )
            currentInputConnection?.sendKeyEvent(
                KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER)
            )
        }
        
        // 连续模式：继续监听
        if (isContinuousMode && isVoiceMode) {
            keyboardViewManager.updateVoiceStatus("继续聆听...")
            voiceInputManager.startListening()
        } else {
            stopVoiceInput()
        }
    }
    
    override fun onVoiceError(errorMsg: String) {
        Log.e(TAG, "语音识别错误: $errorMsg")
        keyboardViewManager.updateVoiceStatus("识别失败: $errorMsg")
        
        // 错误后是否继续
        if (isContinuousMode && isVoiceMode) {
            voiceInputManager.startListening()
        }
    }
    
    override fun onVoiceVolumeChanged(volume: Int) {
        keyboardViewManager.updateVoiceVolume(volume)
    }
    
    // ==================== 辅助方法 ====================
    
    private fun playClickSound() {
        if (PreferenceManager.isSoundEnabled()) {
            val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
            audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, -1f)
        }
    }
    
    private fun vibrate() {
        if (PreferenceManager.isVibrationEnabled()) {
            val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(20)
            }
        }
    }
}
