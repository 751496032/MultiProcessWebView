package com.hongYi.h5container.command

import com.hongYi.h5container.ICallbackFromMainToWebInterface
import com.hongYi.h5container.main.MainCommandManager

/**
 * @author: HZWei
 * @date: 2021/10/14
 * @desc:
 */
abstract class BaseCommand : Command {
    private var mCommandMonitor: CommandMonitor? = null
    
    // 注册监听
    fun registerCommandMonitor(commandMonitor: CommandMonitor?) {
        mCommandMonitor = commandMonitor
    }

    // 解注册 GC
    fun unregisterCommandMonitor(){
        mCommandMonitor = null
    }

    override fun executeCommand(parameters: Map<*, *>, webInterface: ICallbackFromMainToWebInterface) {
        MainCommandManager.INSTANCE.mMainHandler.post {
            if (mCommandMonitor != null) {
                mCommandMonitor!!.onMonitor(parameters, webInterface)
            }
            execCommand(parameters, webInterface)
        }

    }

    abstract fun execCommand(parameters: Map<*, *>, callback: ICallbackFromMainToWebInterface)


}
