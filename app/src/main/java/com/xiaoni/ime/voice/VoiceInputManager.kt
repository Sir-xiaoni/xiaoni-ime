package com.xiaoni.ime.voice

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.iflytek.cloud.*
import com.xiaoni.ime.utils.PreferenceManager

/**
 * 语音输入管理器 - 集成讯飞语音识别
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
        speechRecognizer = SpeechRecognizer.createRecognizer(context) { code ->
            if (code != ErrorCode.SUCCESS) {
                Log.e(TAG, "语音识别器初始化失败: $code")
            } else {
                Log.d(TAG, "语音识别器初始化成功")
            }
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
        
        speechRecognizer?.let { recognizer ->
            // 设置识别参数
            val params = getRecognizerParams()
            recognizer.setParameter(SpeechConstant.PARAMS, null)
            
            // 应用参数
            params.forEach { (key, value) ->
                recognizer.setParameter(key, value)
            }
            
            // 开始监听
            val ret = recognizer.startListening(object : RecognizerListener {
                override fun onBeginOfSpeech() {
                    Log.d(TAG, "开始说话")
                    isListening = true
                    callback.onVoiceStart()
                }
                
                override fun onEndOfSpeech() {
                    Log.d(TAG, "结束说话")
                    isListening = false
                }
                
                override fun onResult(results: RecognizerResult?, isLast: Boolean) {
                    results?.let {
                        val text = parseResult(it.resultString)
                        if (text.isNotEmpty() && isLast) {
                            callback.onVoiceResult(text)
                        }
                    }
                }
                
                override fun onError(error: SpeechError?) {
                    isListening = false
                    error?.let {
                        Log.e(TAG, "识别错误: ${it.errorCode} - ${it.errorDescription}")
                        // 忽略用户主动取消的错误
                        if (it.errorCode != ErrorCode.ERROR_EXIT) {
                            callback.onVoiceError(it.errorDescription)
                        }
                    }
                }
                
                override fun onEvent(eventType: Int, arg1: Int, arg2: Int, obj: Bundle?) {
                    // 处理特殊事件
                }
                
                override fun onVolumeChanged(volume: Int, data: ByteArray?) {
                    callback.onVoiceVolumeChanged(volume)
                }
            })
            
            if (ret != ErrorCode.SUCCESS) {
                Log.e(TAG, "开始监听失败: $ret")
                callback.onVoiceError("启动识别失败")
            }
        } ?: run {
            Log.e(TAG, "语音识别器未初始化")
            callback.onVoiceError("语音识别器未初始化")
        }
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
    
    /**
     * 获取识别参数
     */
    private fun getRecognizerParams(): Map<String, String> {
        return mutableMapOf<String, String>().apply {
            // 应用领域: 日常用语
            put(SpeechConstant.DOMAIN, "iat")
            // 语言: 中文
            put(SpeechConstant.LANGUAGE, "zh_cn")
            // 方言: 普通话
            put(SpeechConstant.ACCENT, "mandarin")
            // 采样率
            put(SpeechConstant.SAMPLE_RATE, "16000")
            // 返回结果格式: JSON
            put(SpeechConstant.RESULT_TYPE, "json")
            // 标点符号: 有标点
            put(SpeechConstant.ASR_PTT, "1")
            // 音频编码
            put(SpeechConstant.AUDIO_FORMAT, "wav")
            // 静音检测时间(毫秒)
            put(SpeechConstant.VAD_BOS, "4000")
            put(SpeechConstant.VAD_EOS, "1000")
            // 网络超时
            put(SpeechConstant.NET_TIMEOUT, "20000")
            // 是否开启语义理解
            put(SpeechConstant.NLP_VERSION, "3.0")
        }
    }
    
    /**
     * 解析识别结果
     */
    private fun parseResult(json: String): String {
        // 讯飞返回的 JSON 格式解析
        val sb = StringBuilder()
        try {
            // 简单解析，实际项目中建议使用 Gson
            val regex = """"w":"([^"]+)"""".toRegex()
            regex.findAll(json).forEach { match ->
                sb.append(match.groupValues[1])
            }
        } catch (e: Exception) {
            Log.e(TAG, "解析结果失败", e)
        }
        return sb.toString()
    }
}
