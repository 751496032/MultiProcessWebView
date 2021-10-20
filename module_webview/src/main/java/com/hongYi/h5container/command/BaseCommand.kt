package com.hongYi.h5container.command

import com.hongYi.h5container.ICallbackFromMainToWebInterface
import com.hongYi.h5container.main.MainCommandManager

/**
 * @author: HZWei
 * @date: 2021/10/14
 * @desc:
 */
abstract class BaseCommand : Command {
    private var mCommandMonitor: ICommandMonitor? = null

    // 注册监听 ，必须在交互前注册
    fun registerCommandMonitor(commandMonitor: ICommandMonitor?) {
        mCommandMonitor = commandMonitor
    }

    // 解注册 GC
    fun unregisterCommandMonitor() {
        mCommandMonitor = null
    }

    override fun executeCommand(parameters: Map<*, *>, webInterface: ICallbackFromMainToWebInterface) {
//        if (mCommandMonitor != null) {
//            CommandHelper.INSTANCE.unregisterJsCallback(parameters,webInterface)
//            unregisterCommandMonitor()
//            return
//        }
        MainCommandManager.INSTANCE.mMainHandler.post {
            if (mCommandMonitor != null) {
                mCommandMonitor!!.onMonitor(parameters, webInterface)
            }
            execCommand(parameters, webInterface)
        }
    }

    abstract fun execCommand(parameters: Map<*, *>, callback: ICallbackFromMainToWebInterface)


}
