package com.xiaoni.ime

import android.app.Application
import android.util.Log
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
        
        Log.d(TAG, "小逆输入法初始化完成")
    }
}
