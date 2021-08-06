package com.hongYi.h5container.main

import android.os.Handler
import android.os.IBinder
import android.os.Looper
import com.google.gson.Gson
import com.hongYi.h5container.ICallbackFromMainToWebInterface
import com.hongYi.h5container.IWebToMainInterface
import com.hongYi.h5container.command.Command
import com.hongYi.h5container.utils.LogUtils
import java.util.*
import kotlin.collections.HashMap

/**
 *@author: HZWei
 *@date:  2021/8/5
 *@desc:  所有Command的管理者
 */
class MainCommandManager private constructor() : IWebToMainInterface.Stub() {

    private val commands = HashMap<String, Command>()
    private val mMainHandler = Handler(Looper.getMainLooper())

    init {
        /**
         * 需要传入应用的ClassLoader，不然会默认使用SystemClassLoader，是无法加载到应用内的AutoService注解
         */
        ServiceLoader.load(Command::class.java, javaClass.classLoader).toList().forEach {
            commands[it.commandName()] = it
            LogUtils.i("WebViewCommandsManager constructor ${it.commandName()}")
        }

    }


    companion object {
        val INSTANCE by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            MainCommandManager()
        }
    }


    override fun handleWebCommand(commandName: String, params: String, callback: ICallbackFromMainToWebInterface) {
        mMainHandler.post {
            INSTANCE.commands[commandName]?.executeCommand(Gson().fromJson(params, Map::class.java), callback)
        }
    }


}