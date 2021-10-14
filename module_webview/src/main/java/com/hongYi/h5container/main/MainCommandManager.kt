package com.hongYi.h5container.main

import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.text.TextUtils
import android.widget.Toast
import com.google.gson.Gson
import com.hongYi.h5container.ICallbackFromMainToWebInterface
import com.hongYi.h5container.IWebToMainInterface
import com.hongYi.h5container.command.Command
import com.hongYi.h5container.utils.LogUtils
import java.lang.IllegalArgumentException
import java.lang.NullPointerException
import java.util.*
import kotlin.collections.HashMap

/**
 *@author: HZWei
 *@date:  2021/8/5
 *@desc:  Command管理者 是一个Binder对象
 */
class MainCommandManager private constructor() : IWebToMainInterface.Stub() {

    val TAG = "MainCommandManager"

    private val commands = HashMap<String, Command>()
    val mMainHandler = Handler(Looper.getMainLooper())

    init {
        /**
         * 需要传入应用的ClassLoader，不然会默认使用SystemClassLoader，是无法加载到应用内的AutoService注解
         */
        ServiceLoader.load(Command::class.java, javaClass.classLoader).toList().forEach {
            if (it.commandName().isNotEmpty())
                commands[it.commandName()] = it
            LogUtils.i("CommandsManager constructor ${it.commandName()}")
        }

    }


    companion object {
        val INSTANCE by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            MainCommandManager()
        }
    }


    override fun handleWebCommand(commandName: String, params: String, callback: ICallbackFromMainToWebInterface) {
        if (!checkCommand(commandName)) {
            // TODO 应该将错误提示给Web
            return
        }
        mMainHandler.post {
            INSTANCE.commands[commandName]?.executeCommand(Gson().fromJson(params, Map::class.java), callback)
        }
    }

    /**
     * 提供给外部获取Command对象
     */
    fun <T : Command?> getCommand(commandName: String?): T? {
        if (!checkCommand(commandName)){
            throw IllegalArgumentException(String.format("检测%s指令是否正确", commandName))
        }
        val command = commands[commandName]
        return command as T?
    }

    private fun checkCommand(commandName: String?):Boolean {
        if (TextUtils.isEmpty(commandName)) {
            LogUtils.d(TAG,"commandName参数不能为空")
            return false
        } else if (!commands.keys.contains(commandName)) {
            LogUtils.d(TAG,String.format("%s指令不存在", commandName))
            return false
        }
        return true
    }


}
