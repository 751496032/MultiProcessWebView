package com.hongYi.h5container.webview;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.JavascriptInterface;

import com.google.gson.Gson;
import com.hongYi.h5container.bean.JsParam;
import com.hongYi.h5container.main.MainCommandService;
import com.hongYi.h5container.utils.LogUtils;
import com.hongYi.h5container.utils.ViewHelper;
import com.hongYi.h5container.utils.WebViewPool;
import com.hongYi.h5container.webview.settings.WebViewDefaultSettings;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebView;

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * author:HZWei
 * date:  2021/1/19
 * desc:
 */
public class X5WebView extends WebView {

    public static final String TAG = "X5WebView";

    private OnScrollChangedListener mOnScrollChangedListener;

    private String mJsObjectName;

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
            mJsObjectName = name;
            addJavascriptInterface(this, name);
        }
    }

    /**
     * js调用原生统一函数
     *
     * @param jsParam js传入的参数，json字符串，格式如下：
     * {"name":"login","param":{"targetClassName":"com.xxx","callbackNameKeys":["success_nativetojs_callback_1633683965180_6434","fail_nativetojs_callback_1633683965180_6434","complete_nativetojs_callback_1633683965180_6434"]}}
     * 其中param对象中callbackNameKeys字段是一定存在的，当没有Js没有回调函数时，是一个空数组，其他字段都是业务字段
     */
    @JavascriptInterface
    public void takeNativeAction(String jsParam) {
        LogUtils.i("takeNativeAction: " + jsParam);
        if (TextUtils.isEmpty(jsParam)) return;
        Gson gson = new Gson();
        JsParam jsParamObject = gson.fromJson(jsParam, JsParam.class);
        String params = gson.toJson(jsParamObject.param);
        WebViewCommandDispatcher.Companion.getINSTANCE().dispatcherCommand(jsParamObject.name, params, this);

    }

    /**
     * 将结果回调给Js
     *
     * @param callbackNameKey Js回调函数名称key，在Js中会根据key来查找对应回调函数
     * @param response  Json字符串
     */
    public void handleWebCallback(final String callbackNameKey, final String response) {
        LogUtils.i("handleWebCallback  callbackNameKey: " + callbackNameKey + "  response: " + response);
        if (TextUtils.isEmpty(callbackNameKey)) return;
        post(new Runnable() {
            @Override
            public void run() {
                String jscode = "javascript:window.nativetoJsCallback('" + callbackNameKey + "'," + response + ")";
                LogUtils.i("jscode: " + jscode);
                evaluateJavascript(jscode, new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String s) {

                    }
                });
            }
        });
    }


    public void injectJsCode(final String jscode) {
        LogUtils.i("injectJsCode  jscode: " +jscode);
        if (TextUtils.isEmpty(jscode)) return;
        post(new Runnable() {
            @Override
            public void run() {
                loadUrl(jscode);
            }
        });
    }


    public void injectJsCode(){
        String js="javascript:(function(){" +
                "document.documentElement.style.overflow='visible';})()";
        injectJsCode(js);
    }



    @Override
    public void destroy() {
        stopLoading();
        if (!TextUtils.isEmpty(mJsObjectName)) removeJavascriptInterface(mJsObjectName);
        boolean empty = WebViewPool.getInstance().isEmpty();
        if (empty) {
            super.destroy();
        }

    }

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        LogUtils.i("overScrollBy: "+ deltaY +"  "+ scrollY +"  "+ scrollRangeY);
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        if (mOnScrollChangedListener != null) {
            mOnScrollChangedListener.onScrollChanged(t, oldt);
        }
        super.onScrollChanged(l, t, oldl, oldt);
    }


    public interface OnScrollChangedListener {
        void onScrollChanged(int newY, int oldY);
    }


    public void setOnScrollChangedListener(OnScrollChangedListener onScrollChangedListener) {
        mOnScrollChangedListener = onScrollChangedListener;
    }

}
