package com.hongYi.h5container.main

import android.app.Service
import android.content.Intent
import android.os.IBinder

/**
 *@author: HZWei
 *@date:  2021/8/6
 *@desc: 主进程服务
 */
class MainCommandService: Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return MainCommandManager.INSTANCE
    }


}