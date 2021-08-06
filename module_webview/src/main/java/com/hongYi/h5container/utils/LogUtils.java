/*
 * Copyright (C)  Justson(https://github.com/Justson/AgentWeb)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hongYi.h5container.utils;

import android.util.Log;

import com.hongYi.h5container.BuildConfig;
import com.hongYi.h5container.webview.X5WebView;


/**
 * @author cenxiaozhong
 * @date 2017/5/28
 * @since 1.0.0
 */
public class LogUtils {

    private static final String PREFIX = X5WebView.TAG + "_";

    static boolean isDebug() {
        return BuildConfig.DEBUG;
    }


    public static void d(String tag, String message) {
        if (isDebug()) {
            Log.d(PREFIX.concat(tag), message);
        }
    }

    public static void i(String tag, String message) {
        if (isDebug()) {
            Log.i(PREFIX.concat(tag), message);
        }
    }

    public static void v(String tag, String message) {
        if (isDebug()) {
            Log.v(PREFIX.concat(tag), message);
        }

    }


    public static void e(String tag, String message) {
        if (isDebug()) {
            Log.e(PREFIX.concat(tag), message);
        }
    }


    public static void i(String message) {
        if (isDebug()) {
            Log.i(X5WebView.TAG, message);
        }
    }

    public static void v(String message) {
        if (isDebug()) {
            Log.v(X5WebView.TAG, message);
        }

    }

    public static void d(String message) {
        if (isDebug()) {
            Log.d(X5WebView.TAG, message);
        }
    }


    public static void e(String message) {
        if (isDebug()) {
            Log.e(X5WebView.TAG, message);
        }
    }


}
