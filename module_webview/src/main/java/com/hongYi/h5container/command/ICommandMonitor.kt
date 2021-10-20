package com.hongYi.h5container.command

import com.hongYi.h5container.ICallbackFromMainToWebInterface

/**
 *@author: HZWei
 *@date:  2021/10/14
 *@desc: 提供外部设置监听的接口
 */
interface ICommandMonitor{

   fun onMonitor(parameters: Map<*, *>, callback: ICallbackFromMainToWebInterface)

}
