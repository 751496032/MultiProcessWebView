package com.hongYi.h5container.command

import com.hongYi.h5container.ICallbackFromMainToWebInterface

/**
 *@author: HZWei
 *@date:  2021/10/14
 *@desc:
 */
interface CommandMonitor {

   fun onMonitor(parameters: Map<*, *>, callback: ICallbackFromMainToWebInterface)
}
