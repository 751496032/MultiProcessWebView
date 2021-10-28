package com.hongYi.h5container

import android.app.Application
import com.hongYi.h5container.business.ImageLoader
import com.previewlibrary.ZoomMediaLoader

/**
 * author:HZWei
 * date:  2021/1/19
 * desc:
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        WebViewManager.init(this, false)
        ZoomMediaLoader.getInstance().init(ImageLoader())

    }

    companion object {
        lateinit var INSTANCE: App
            private set
    }
}