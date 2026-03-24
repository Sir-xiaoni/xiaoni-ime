package com.xiaoni.ime.service

import android.inputmethodservice.InputMethodService
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ImageButton
import android.widget.TextView
import com.xiaoni.ime.R
import com.xiaoni.ime.voice.VoiceInputManager
import com.xiaoni.ime.utils.PreferenceManager

/**
 * 小逆语音输入法 - 纯语音输入，无需键盘
 */
class XiaoNiInputMethodService : InputMethodService(), 
    VoiceInputManager.VoiceCallback {
    
    companion object {
        const val TAG = "XiaoNiIME"
    }
    
    private var voiceInputManager: VoiceInputManager? = null
    private var statusText: TextView? = null
    private var micButton: ImageButton? = null
    private var stopButton: ImageButton? = null
    
    private var isListening = false
    private var isContinuousMode = true
    private val handler = Handler(Looper.getMainLooper())
    
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "输入法服务创建")
    }
    
    override fun onEvaluateFullscreenMode(): Boolean {
        return false
    }
    
    override fun onCreateInputView(): View {
        Log.d(TAG, "创建输入视图")
        
        val view = LayoutInflater.from(this).inflate(R.layout.voice_input_panel, null)
        
        statusText = view.findViewById(R.id.voice_status_text)
        micButton = view.findViewById(R.id.mic_button)
        stopButton = view.findViewById(R.id.stop_button)
        
        // 初始化语音识别器
        try {
            voiceInputManager = VoiceInputManager(this, this)
        } catch (e: Exception) {
            Log.e(TAG, "初始化语音识别器失败: ${e.message}")
            statusText?.text = "初始化失败，点击重试"
        }
        
        micButton?.setOnClickListener {
            if (isListening) {
                stopVoiceInput()
            } else {
                startVoiceInput()
            }
        }
        
        stopButton?.setOnClickListener {
            if (isListening) {
                stopVoiceInputAndSend()
            }
        }
        
        // 延迟自动开始
        handler.postDelayed({
            startVoiceInput()
        }, 800)
        
        return view
    }
    
    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        super.onStartInput(attribute, restarting)
        Log.d(TAG, "开始输入")
    }
    
    override fun onFinishInput() {
        super.onFinishInput()
        Log.d(TAG, "结束输入")
        stopVoiceInput()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        voiceInputManager?.destroy()
    }
    
    private fun startVoiceInput() {
        if (isListening) return
        
        // 如果语音识别器为null，尝试重新创建
        if (voiceInputManager == null) {
            try {
                voiceInputManager = VoiceInputManager(this, this)
            } catch (e: Exception) {
                statusText?.text = "无法启动语音识别"
                return
            }
        }
        
        isListening = true
        statusText?.text = "正在聆听..."
        micButton?.setImageResource(R.drawable.ic_mic_on)
        
        voiceInputManager?.startListening()
        vibrate()
        
        Log.d(TAG, "开始语音输入")
    }
    
    private fun stopVoiceInput() {
        if (!isListening) return
        
        isListening = false
        statusText?.text = "点击麦克风开始说话"
        micButton?.setImageResource(R.drawable.ic_mic_off)
        
        voiceInputManager?.stopListening()
        
        Log.d(TAG, "停止语音输入")
    }
    
    private fun stopVoiceInputAndSend() {
        voiceInputManager?.stopListening()
        statusText?.text = "识别中..."
        Log.d(TAG, "说完了，等待识别结果")
    }
    
    // ==================== VoiceCallback ====================
    
    override fun onVoiceStart() {
        statusText?.text = "正在聆听..."
    }
    
    override fun onVoiceResult(text: String) {
        Log.d(TAG, "识别结果: $text")
        
        currentInputConnection?.commitText(text, 1)
        
        currentInputConnection?.sendKeyEvent(
            KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER)
        )
        currentInputConnection?.sendKeyEvent(
            KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER)
        )
        
        statusText?.text = "已发送，继续聆听..."
        
        if (isContinuousMode) {
            handler.postDelayed({
                voiceInputManager?.startListening()
            }, 500)
        } else {
            isListening = false
            micButton?.setImageResource(R.drawable.ic_mic_off)
        }
    }
    
    override fun onVoiceError(errorMsg: String) {
        Log.e(TAG, "语音识别错误: $errorMsg")
        statusText?.text = errorMsg
        
        // 显示错误后，允许用户点击麦克风重试
        isListening = false
        micButton?.setImageResource(R.drawable.ic_mic_off)
    }
    
    override fun onVoiceVolumeChanged(volume: Int) {}
    
    private fun vibrate() {
        try {
            if (PreferenceManager.isVibrationEnabled()) {
                val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(30)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}