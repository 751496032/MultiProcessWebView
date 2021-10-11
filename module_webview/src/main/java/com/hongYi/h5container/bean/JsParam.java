package com.hongYi.h5container.bean;

import com.google.gson.JsonObject;
/**
* @author: HZWei
* @date: 2021/10/11
* @desc:
 * {"name":"login","param":{"targetClassName":"com.xxx","callbackNameKeys":["success_nativetojs_callback_1633683965180_6434","fail_nativetojs_callback_1633683965180_6434","complete_nativetojs_callback_1633683965180_6434"]}}
 * @see com.hongYi.h5container.webview.X5WebView#takeNativeAction(String)
*/
public class JsParam {
    public String name;
    public JsonObject param;
}
