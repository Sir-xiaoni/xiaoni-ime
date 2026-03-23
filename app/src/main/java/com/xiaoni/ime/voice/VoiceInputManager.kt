package com.xiaoni.ime.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import com.xiaoni.ime.utils.PreferenceManager

/**
 * 语音输入管理器 - 使用 Android 原生语音识别
 */
class VoiceInputManager(
    private val context: Context,
    private val callback: VoiceCallback
) {
    companion object {
        const val TAG = "VoiceInputManager"
    }
    
    private var speechRecognizer: SpeechRecognizer? = null
    private var isListening = false
    
    interface VoiceCallback {
        fun onVoiceStart()
        fun onVoiceResult(text: String)
        fun onVoiceError(errorMsg: String)
        fun onVoiceVolumeChanged(volume: Int)
    }
    
    init {
        initSpeechRecognizer()
    }
    
    private fun initSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
            Log.d(TAG, "语音识别器初始化成功")
        } else {
            Log.e(TAG, "设备不支持语音识别")
            callback.onVoiceError("设备不支持语音识别")
        }
    }
    
    /**
     * 开始语音识别
     */
    fun startListening() {
        if (isListening) {
            Log.d(TAG, "已经在监听中")
            return
        }
        
        if (speechRecognizer == null) {
            callback.onVoiceError("语音识别器未初始化")
            return
        }
        
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            // 识别模式: 自由形式
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            // 语言: 中文
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "zh-CN")
            // 提示文本
            putExtra(RecognizerIntent.EXTRA_PROMPT, "请说话...")
            // 最大结果数
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            // 部分结果
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }
        
        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                Log.d(TAG, "准备就绪，可以说话")
                isListening = true
                callback.onVoiceStart()
            }
            
            override fun onBeginningOfSpeech() {
                Log.d(TAG, "开始说话")
            }
            
            override fun onRmsChanged(rmsdB: Float) {
                // 音量变化 0-10 转为 0-100
                val volume = ((rmsdB + 2.5) * 8).toInt().coerceIn(0, 100)
                callback.onVoiceVolumeChanged(volume)
            }
            
            override fun onBufferReceived(buffer: ByteArray?) {}
            
            override fun onEndOfSpeech() {
                Log.d(TAG, "结束说话")
                isListening = false
            }
            
            override fun onError(error: Int) {
                isListening = false
                val errorMsg = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> "音频错误"
                    SpeechRecognizer.ERROR_CLIENT -> "客户端错误"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "权限不足"
                    SpeechRecognizer.ERROR_NETWORK -> "网络错误"
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "网络超时"
                    SpeechRecognizer.ERROR_NO_MATCH -> "未能识别，请重试"
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "识别器忙"
                    SpeechRecognizer.ERROR_SERVER -> "服务器错误"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "说话超时"
                    else -> "未知错误: $error"
                }
                Log.e(TAG, "识别错误: $errorMsg")
                callback.onVoiceError(errorMsg)
            }
            
            override fun onResults(results: Bundle?) {
                isListening = false
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val text = matches[0]
                    Log.d(TAG, "识别结果: $text")
                    callback.onVoiceResult(text)
                } else {
                    callback.onVoiceError("未能识别")
                }
            }
            
            override fun onPartialResults(partialResults: Bundle?) {
                // 部分结果，可选处理
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                matches?.firstOrNull()?.let {
                    Log.d(TAG, "部分结果: $it")
                }
            }
            
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
        
        speechRecognizer?.startListening(intent)
        Log.d(TAG, "开始监听")
    }
    
    /**
     * 停止语音识别
     */
    fun stopListening() {
        if (!isListening) return
        
        speechRecognizer?.stopListening()
        isListening = false
        Log.d(TAG, "停止监听")
    }
    
    /**
     * 销毁语音识别器
     */
    fun destroy() {
        stopListening()
        speechRecognizer?.destroy()
        speechRecognizer = null
        Log.d(TAG, "语音识别器已销毁")
    }
}
