package com.hongYi.h5container.commands

import com.google.auto.service.AutoService
import com.hongYi.h5container.ICallbackFromMainToWebInterface
import com.hongYi.h5container.command.BaseCommand
import com.hongYi.h5container.command.Command

/**
 *@author: HZWei
 *@date:  2021/10/14
 *@desc:
 */
@AutoService(Command::class)
class CommandUI:BaseCommand() {

    override fun execCommand(parameters: Map<*, *>, callback: ICallbackFromMainToWebInterface) {
        // 不需要外部类的铺助，直接在这里实现逻辑

    }

    override fun commandName(): String {
        return "updateui"
    }
}
