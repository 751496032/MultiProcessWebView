package com.hongYi.h5container.webview.callback

/**
 *@author: HZWei
 *@date:  2021/8/6
 *@desc:
 */
@Deprecated("过时")
interface CallbackFromNativeToWeb {
    fun handleWebCallback(jsCallbackName: String, result: String)

}