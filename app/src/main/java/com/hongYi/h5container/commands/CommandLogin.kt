package com.hongYi.h5container.commands

import com.google.auto.service.AutoService
import com.google.gson.Gson
import com.hongYi.h5container.ICallbackFromMainToWebInterface
import com.hongYi.h5container.command.BaseCommand
import com.hongYi.h5container.command.Command
import com.hongYi.h5container.command.CommandHelper

/**
* @author: HZWei
* @date: 2021/10/12
* @desc:
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
