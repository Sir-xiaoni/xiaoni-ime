package com.xiaoni.ime

import android.app.Application
import android.util.Log
import com.iflytek.cloud.SpeechUtility
import com.xiaoni.ime.utils.PreferenceManager

/**
 * 小逆输入法应用
 */
class XiaoNiApplication : Application() {
    
    companion object {
        const val TAG = "XiaoNiIME"
        lateinit var instance: XiaoNiApplication
            private set
    }
    
    override fun onCreate() {
        super.onCreate()
        instance = this
        
        // 初始化偏好设置
        PreferenceManager.init(this)
        
        // 初始化讯飞语音 SDK
        initSpeechSDK()
        
        Log.d(TAG, "小逆输入法初始化完成")
    }
    
    private fun initSpeechSDK() {
        // 讯飞语音 SDK 初始化
        // 需要在 local.properties 中配置 IFLYTEK_APPKEY
        val appKey = BuildConfig.IFLYTEK_APPKEY
        SpeechUtility.createUtility(this, "appid=$appKey")
    }
}
