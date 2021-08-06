package com.hongYi.h5container.commands

import android.content.Intent
import com.google.auto.service.AutoService
import com.hongYi.h5container.App
import com.hongYi.h5container.ICallbackFromMainToWebInterface
import com.hongYi.h5container.command.Command
import com.hongYi.h5container.utils.Constants

/**
 *@author: HZWei
 *@date:  2021/8/6
 *@desc:
 */
@AutoService(Command::class)
class CommandOpenPage : Command {
    override fun commandName(): String {
        return "openPage"
    }

    override fun executeCommand(parameters: Map<*, *>, callback: ICallbackFromMainToWebInterface) {
        val intent = Intent()
        intent.setClassName(App.INSTANCE.packageName, parameters["target_class"].toString())
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra(Constants.URL,"http://lgmy.hmeshop.cn/default.aspx?ReferralId=100831&go=1")
        App.INSTANCE.startActivity(intent)

    }
}