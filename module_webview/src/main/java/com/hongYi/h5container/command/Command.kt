package com.hongYi.h5container.command

import com.hongYi.h5container.ICallbackFromMainToWebInterface

/**
 *@author: HZWei
 *@date:  2021/8/5
 *@desc:
 */
interface Command {

    fun commandName(): String

    fun executeCommand(parameters: Map<*,*>, callback: ICallbackFromMainToWebInterface)


}