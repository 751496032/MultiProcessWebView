package com.hongYi.h5container.commands

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.google.auto.service.AutoService
import com.google.gson.Gson
import com.hongYi.h5container.App
import com.hongYi.h5container.ICallbackFromMainToWebInterface
import com.hongYi.h5container.command.Command

/**
 *@author: HZWei
 *@date:  2021/8/6
 *@desc:
 */
@AutoService(Command::class)
class CommandLogin : Command {
    override fun commandName(): String {
        return "login"
    }

    override fun executeCommand(parameters: Map<*, *>, callback: ICallbackFromMainToWebInterface) {

        Toast.makeText(App.INSTANCE, "登录中...", Toast.LENGTH_LONG).show()

        Handler(Looper.getMainLooper()).postDelayed({

            val map = HashMap<String, String>()
            map["accountName"] = "张三"
            callback.handleWebCallback(parameters["callbackName"].toString(), Gson().toJson(map))
        }, 2000)


    }
}