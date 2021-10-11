package com.hongYi.h5container.commands

import android.widget.Toast
import com.google.auto.service.AutoService
import com.hongYi.h5container.App
import com.hongYi.h5container.ICallbackFromMainToWebInterface
import com.hongYi.h5container.command.Command
import com.hongYi.h5container.utils.LogUtils

/**
 *@author: HZWei
 *@date:  2021/8/6
 *@desc:
 */
@AutoService(Command::class)
class CommandShowToast : Command {

    override fun commandName(): String {
        return "showToast"
    }

    override fun executeCommand(parameters: Map<*, *>, webInterface: ICallbackFromMainToWebInterface) {
        LogUtils.d("executeCommand curr thread "+ Thread.currentThread().name)
        Toast.makeText(App.INSTANCE, parameters["message"].toString(), Toast.LENGTH_SHORT).show()
    }
}
