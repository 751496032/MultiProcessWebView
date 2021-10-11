package com.hongYi.h5container.command

import com.hongYi.h5container.ICallbackFromMainToWebInterface
import java.lang.annotation.Native
import java.util.*
import kotlin.collections.emptyList as emptyList1

/**
 *@author: HZWei
 *@date:  2021/10/11
 *@desc:  主要获取Js回调函数的类型，是success还是fail complete
 */
class CommandHelper {

    companion object {
        val SUCCESS_CALLBACK = "success"
        val FAIL_CALLBACK = "fail"
        val COMPLETE_CALLBACK = "complete"

        val INSTANCE by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            CommandHelper()
        }
    }

    private fun getJsCallbackKey(parameter: Map<*, *>, tag: String): String {
        if (parameter.containsKey("callbackNameKeys")) {
            val keys = parameter["callbackNameKeys"]
            if (keys is List<*> && keys.isNotEmpty()) {
                for (key in keys) {
                    if (key.toString().startsWith(tag, true)) {
                        return key.toString()
                    } else {
                        // TODO
                        // 抛出Js没有设置tag回调的错误信息，
                        // complete的可以排除，不抛出
                    }
                }
            }
        } else {
            // TODO
            // 抛出Js传入参数缺少字段错误
            // JsParam的param字段中没有callbackNameKeys字段的错误信息
            // 正常是不会发出这个错误，除非前端重写导致的
        }
        return ""
    }


    fun handleSuccessCallback(parameterFromJs: Map<*, *>, parameterFormNative: String, webInterface: ICallbackFromMainToWebInterface) {
        webInterface.handleWebCallback(getJsCallbackKey(parameterFromJs, SUCCESS_CALLBACK), parameterFormNative)
        webInterface.handleWebCallback(getJsCallbackKey(parameterFromJs, COMPLETE_CALLBACK),"{}")
    }

    fun handleFailCallback(parameterFromJs: Map<*, *>, parameterFormNative: String, webInterface: ICallbackFromMainToWebInterface) {
        webInterface.handleWebCallback(getJsCallbackKey(parameterFromJs, FAIL_CALLBACK), parameterFormNative)
        webInterface.handleWebCallback(getJsCallbackKey(parameterFromJs, COMPLETE_CALLBACK),"{}")
    }

}
