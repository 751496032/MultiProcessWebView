package com.hongYi.h5container.commands

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.google.auto.service.AutoService
import com.google.gson.Gson
import com.hongYi.h5container.App
import com.hongYi.h5container.ICallbackFromMainToWebInterface
import com.hongYi.h5container.command.BaseCommand
import com.hongYi.h5container.command.Command
import com.hongYi.h5container.command.CommandHelper
import com.hongYi.h5container.command.CommandMonitor
import java.net.CookieHandler

/**
 *@author: HZWei
 *@date:  2021/8/6
 *@desc:
 */
@AutoService(Command::class)
class CommandLogin : BaseCommand() {

    override fun commandName(): String {
        return "login"
    }

    override fun execCommand(parameters: Map<*, *>, callback: ICallbackFromMainToWebInterface) {
        val map = HashMap<String, String>()

        val num = parameters["count"]
        map["accountName"] = "张三"
        CommandHelper.INSTANCE.handleSuccessCallback(parameters, Gson().toJson(map), callback)
    }
}
