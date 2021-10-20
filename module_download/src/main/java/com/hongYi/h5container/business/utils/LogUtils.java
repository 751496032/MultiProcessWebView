package com.hongYi.h5container.business.utils;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;

/**
 * 日志工具类
 *
 * @author Administrator
 */
public class LogUtils {
    public static String LOG_TAG = "hyLog";
    public static boolean debug = true;

    public static void init(boolean debug) {
        init(debug, LOG_TAG);
    }

    public static void init(final boolean debug, String tag) {
        LogUtils.debug = debug;
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(false)  // (Optional) Whether to show thread info or not. Default true
                .methodCount(1)         // (Optional) How many method line to show. Default 2
                .methodOffset(1)        // (Optional) Hides internal method calls up to offset. Default 5
                .tag(tag)   // (Optional) Global tag for every log. Default PRETTY_LOGGER
                .build();

        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy) {
            @Override
            public boolean isLoggable(int priority, String tag) {
                return debug;
            }
        });


    }

    /**
     * 打印日志(Verbose)
     *
     * @param msg 内容
     */
    public static void v(String msg) {
        if (debug) {
            Logger.v(msg);
        }
    }

    /**
     * 打印日志(Debug)
     *
     * @param msg 内容
     */
    public static void d(String msg) {
        if (debug) {
            Logger.d(msg);
        }
    }


    /**
     * 打印日志(Info)
     *
     * @param msg 内容
     */
    public static void i(String msg) {
        if (debug) {
            Logger.i(msg);
        }
    }

    /**
     * 打印日志(Warm)
     *
     * @param msg 内容
     */
    public static void w(String msg) {
        if (debug) {
            Logger.w(msg);
        }
    }

    /**
     * 打印日志(wtf)
     *
     * @param msg 内容
     */
    public static void wtf(String msg) {
        if (debug) {
            Logger.wtf(msg);
        }
    }


    /**
     * 打印日志(Error)
     *
     * @param msg 内容
     */
    public static void e(String msg) {
        if (debug) {
            Logger.e(msg);
        }
    }

    /**
     * 打印日志(Error)
     *
     * @param throwable
     */
    public static void e(Throwable throwable) {
        if (debug) {
            Logger.e(throwable, "");
        }
    }


    /**
     * 打印日志(Erro)
     *
     * @param msg       内容
     * @param throwable
     */
    public static void e(String msg, Throwable throwable) {
        if (debug) {
            Logger.e(throwable, msg);
        }
    }

    /**
     * 打印日志(json)
     *
     * @param msg 内容
     */
    public static void json(String msg) {
        if (debug) {
            Logger.json(msg);
        }
    }

}
