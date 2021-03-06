package com.hongYi.h5container.webview;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.webkit.JavascriptInterface;

import com.google.gson.Gson;
import com.hongYi.h5container.bean.JsParam;
import com.hongYi.h5container.command.CommandHelper;
import com.hongYi.h5container.main.MainCommandService;
import com.hongYi.h5container.utils.LogUtils;
import com.hongYi.h5container.webview.callback.WebViewLifecycleListener;
import com.hongYi.h5container.webview.callback.WebViewLifecycleObserver;
import com.hongYi.h5container.webview.settings.WebViewDefaultSettings;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebView;

import java.util.Map;

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * author:HZWei
 * date:  2021/1/19
 * desc:
 */
public class X5WebView extends WebView implements WebViewLifecycleObserver {

    public static final String TAG = "X5WebView";

    private OnScrollChangedListener mOnScrollChangedListener;

    private WebViewLifecycleListener mLifecycleListener;

    private String mJsObject;

    private boolean mIsDestroy;

    public X5WebView(Context context) {
        super(context);
        init();
    }

    public X5WebView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public X5WebView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }


    private void init() {
        bindMainProcessServiceAidl();
        WebViewDefaultSettings.getInstance().setSettings(this);
//        ViewHelper.setDrawDuringWindowsAnimating(this);

    }

    public void bindMainProcessServiceAidl() {
        // 绑定主进程服务
        Intent intent = new Intent(getContext().getApplicationContext(), MainCommandService.class);
        getContext().getApplicationContext().bindService(intent, WebViewCommandDispatcher.Companion.getINSTANCE(), BIND_AUTO_CREATE);
    }

    public void registerJsInterface(String name) {
        if (!TextUtils.isEmpty(name)) {
            mJsObject = name;
            addJavascriptInterface(this, name);
        }
    }

    /**
     * js调用原生统一函数
     *
     * @param jsParam js传入的参数，json字符串，格式如下：
     *                {"name":"login","param":{"targetClassName":"com.xxx","callbackNameKeys":["success_nativetojs_callback_1633683965180_6434","fail_nativetojs_callback_1633683965180_6434","complete_nativetojs_callback_1633683965180_6434"]}}
     *                其中param对象中callbackNameKeys字段是一定存在的，当没有Js没有回调函数时，是一个空数组，其他字段都是业务字段
     */
    @JavascriptInterface
    public void takeNativeAction(String jsParam) {
        LogUtils.i("takeNativeAction: " + jsParam + "  destroy: " + mIsDestroy);
        if (TextUtils.isEmpty(jsParam)) return;
        Gson gson = new Gson();
        JsParam jsParamObject = gson.fromJson(jsParam, JsParam.class);
        String params = gson.toJson(jsParamObject.param);
        if (mIsDestroy) {
            Map map = gson.fromJson(params, Map.class);
            CommandHelper.Companion.getINSTANCE().unregisterJsCallback(map, this);
            return;
        }
        WebViewCommandDispatcher.Companion.getINSTANCE().dispatcherCommand(jsParamObject.name, params, this);
    }

    @JavascriptInterface
    public void config(String jsParam) {
        LogUtils.i("config: " + jsParam);
    }

    /**
     * 将结果回调给Js
     *
     * @param callbackNameKey Js回调函数名称key，在Js中会根据key来查找对应回调函数
     * @param response        Json字符串
     */
    public void handleWebCallback(final String callbackNameKey, final String response) {
        LogUtils.i("handleWebCallback  callbackNameKey: " + callbackNameKey + "  response: " + response);
        if (TextUtils.isEmpty(callbackNameKey)) return;
        String jscode = "javascript:window.nativetoJsCallback('" + callbackNameKey + "'," + response + ")";
        LogUtils.i("jscode: " + jscode);
        injectedCustomJscode(jscode);
    }


    /**
     * 注销Js的回调函数
     *
     * @param callbackNameKey
     */
    public void unregisterJsCallback(final String callbackNameKey) {
        LogUtils.i("unregisterJsCallback  callbackNameKey: " + callbackNameKey);
        if (TextUtils.isEmpty(callbackNameKey)) return;
        String jscode = "javascript:window.unregisterJsCallback('" + callbackNameKey + "')";
        LogUtils.i("jscode: " + jscode);
        injectedCustomJscode(jscode);
    }


    public void injectedCustomJscode(final String jscode) {
        LogUtils.i("injectedCustomJscode  jscode: " + jscode);
        if (TextUtils.isEmpty(jscode)) return;
        post(new Runnable() {
            @Override
            public void run() {
                evaluateJavascript(jscode, new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String s) {

                    }
                });
            }
        });
    }


    @Deprecated
    public void injectedCustomJscode() {
        String js = "javascript:(function(){" +
                "document.documentElement.style.overflow='visible';})()";
        injectedCustomJscode(js);
    }


    @Override
    public void destroy() {
        stopLoading();
        if (!TextUtils.isEmpty(mJsObject)) removeJavascriptInterface(mJsObject);
        super.destroy();
//        boolean empty = WebViewPool.getInstance().isEmpty();
//        if (empty) {
//            super.destroy();
//        }

    }

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        LogUtils.i("overScrollBy: " + deltaY + "  " + scrollY + "  " + scrollRangeY);
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        if (mOnScrollChangedListener != null) {
            mOnScrollChangedListener.onScrollChanged(t, oldt);
        }
        super.onScrollChanged(l, t, oldl, oldt);
    }

    @Override
    public void onStart() {
        LogUtils.i("WebView Lifecycle: onStart");
        mIsDestroy = false;
        if (mLifecycleListener != null) {
            mLifecycleListener.onStart();
        }
    }

    @Override
    public void onStop() {
        LogUtils.i("WebView Lifecycle: onStop");
        if (mLifecycleListener != null) {
            mLifecycleListener.onStop();
        }
    }

    @Override
    public void onDestroy() {
        LogUtils.i("WebView Lifecycle: onDestroy");
        mIsDestroy = true;
        if (mLifecycleListener != null) {
            mLifecycleListener.onDestroy();
        }
    }

    public boolean isDestroy() {
        return mIsDestroy;
    }

    public X5WebView setWebViewLifecycleListener(WebViewLifecycleListener lifecycleListener) {
        mLifecycleListener = lifecycleListener;
        return this;
    }

    public interface OnScrollChangedListener {
        void onScrollChanged(int newY, int oldY);
    }


    public X5WebView setOnScrollChangedListener(OnScrollChangedListener onScrollChangedListener) {
        mOnScrollChangedListener = onScrollChangedListener;
        return this;
    }


}
