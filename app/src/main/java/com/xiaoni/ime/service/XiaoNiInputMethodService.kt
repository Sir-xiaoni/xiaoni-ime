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
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.widget.FrameLayout
import com.xiaoni.ime.R
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
    
    private lateinit var keyboardView: KeyboardView
    private lateinit var qwertyKeyboard: Keyboard
    private lateinit var symbolKeyboard: Keyboard
    private lateinit var voiceInputManager: VoiceInputManager
    
    private var isVoiceMode = false
    private var isContinuousMode = false
    private var currentKeyboard: Keyboard? = null
    
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "输入法服务创建")
        voiceInputManager = VoiceInputManager(this, this)
    }
    
    override fun onEvaluateFullscreenMode(): Boolean {
        return false
    }
    
    override fun onCreateInputView(): View {
        Log.d(TAG, "创建输入视图")
        
        // 创建根容器
        val container = FrameLayout(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
            setBackgroundColor(resources.getColor(R.color.keyboard_background, null))
        }
        
        // 初始化键盘
        qwertyKeyboard = Keyboard(this, R.xml.keyboard_qwerty)
        symbolKeyboard = Keyboard(this, R.xml.keyboard_symbols)
        
        // 创建 KeyboardView
        keyboardView = KeyboardView(this, null).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                (250 * resources.displayMetrics.density).toInt()
            )
            keyboard = qwertyKeyboard
            isPreviewEnabled = true
            isEnabled = true
            visibility = View.VISIBLE
            setBackgroundColor(resources.getColor(R.color.keyboard_background, null))
            setOnKeyboardActionListener(this@XiaoNiInputMethodService)
        }
        
        container.addView(keyboardView)
        currentKeyboard = qwertyKeyboard
        
        return container
    }
    
    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        super.onStartInput(attribute, restarting)
        Log.d(TAG, "开始输入")
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
                // TODO: 实现语音输入
            }
            // 切换键盘
            -101 -> {
                currentKeyboard = if (currentKeyboard == qwertyKeyboard) symbolKeyboard else qwertyKeyboard
                keyboardView.keyboard = currentKeyboard
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
    
    // ==================== VoiceCallback ====================
    
    override fun onVoiceStart() {}
    override fun onVoiceResult(text: String) {
        currentInputConnection?.commitText(text, 1)
    }
    override fun onVoiceError(errorMsg: String) {}
    override fun onVoiceVolumeChanged(volume: Int) {}
    
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