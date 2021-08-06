package com.hongYi.h5container.webview

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import com.hongYi.h5container.ICallbackFromMainToWebInterface
import com.hongYi.h5container.IWebToMainInterface
import com.hongYi.h5container.main.MainCommandManager

/**
 *@author: HZWei
 *@date:  2021/8/5
 *@desc: 命令分发器，主要有两个核心功能：
 * 1、Js向Native发送的命令，执行原生操作，调用Native方法
 * 2、Native向Js发送的命令，执行Js操作，调用Js方法，一般是Js调用native操作后，需要把结果回调给Js的场景
 * @see dispatcherCommand
 *
 * @see WebViewCommandDispatcher 又实现了ServiceConnection接口，可以监听Web进程与主进程服务的连接状态
 *
 */
class WebViewCommandDispatcher private constructor() : ServiceConnection {

    private lateinit var mMainBinderInterface: IWebToMainInterface
    private var mWebView: X5WebView? = null


    // 单例
    companion object {
        val INSTANCE by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            WebViewCommandDispatcher()
        }
    }


    fun dispatcherCommand(commandName: String, params: String, webView: X5WebView) {
        if (mWebView == null) {
            mWebView = webView
        }

        //  要拿到主进程的binder对象，必须与主进程服务连接成功后在onServiceConnected回调中获取
        //  接收到Js的通知后，Js向Native发送命令，执行Native操作
        mMainBinderInterface.handleWebCommand(commandName, params, object : ICallbackFromMainToWebInterface.Stub() { // Web进程的binder对象传到主进程中
            override fun handleWebCallback(jsCallbackName: String?, result: String?) {
                // Native向Js发送命令，执行Js操作
                webView.handleWebCallback(jsCallbackName, result)
            }
        })

    }

    // 主进程服务已连接
    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        // 如果要获取到主进程的Binder对象，实现IWebToMainInterface.Stub接口就是Binder对象
        mMainBinderInterface = IWebToMainInterface.Stub.asInterface(service)
    }

    // 主进程服务已断开
    override fun onServiceDisconnected(name: ComponentName?) {
        // 如果断开了重新再绑定，确保Web进程能够与主进程建立连接
        if (mWebView != null) {
            mWebView?.bindMainProcessServiceAidl()
        }
    }

    // 与主进程连接失效
    override fun onBindingDied(name: ComponentName?) {
        if (mWebView != null) {
            mWebView?.bindMainProcessServiceAidl()
        }
    }


}