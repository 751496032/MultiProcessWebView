@file:Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package com.hongYi.h5container.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.os.Binder
import android.os.IBinder
import android.text.TextUtils
import android.util.ArrayMap
import android.util.SparseArray
import com.hongYi.h5container.command.CommandHelper
import com.hongYi.h5container.ui.WebViewActivity
import com.tencent.smtt.utils.i
import net.grandcentrix.tray.AppPreferences
import java.util.*
import kotlin.reflect.KClass

/**
 *@author: HZWei
 *@date:  2021/11/1
 *@desc:
 */
class ActivityHelper private constructor() {

    companion object {
        val INSTANCE by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            ActivityHelper()
        }
        private const val TAG = "ActivityHelper"
        private const val MAX_PROCESS_NUMBER = 4
    }


    fun printProcess(context: Context) {
//        val preferences = AppPreferences(context)
//        preferences.all.forEach {
//            LogUtils.d(TAG, "item: ${it.key()}  size: ${preferences.all.size}")
//        }

    }

    private val smallClass = mutableListOf<Class<*>>(WebViewActivity.Small1::class.java,
            WebViewActivity.Small2::class.java, WebViewActivity.Small3::class.java,
            WebViewActivity.Small4::class.java)

    /**
     * 获取待启动的WebView的Activity
     * 进程的管理是一个先进先出的队列结构，当进程数达到最大数{@link MAX_PROCESS_NUMBER}时，会将最早入队的进程kill移除
     * 进程启动存储记录 @see WebViewManager#getProcessName()
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : WebViewActivity> getSmallActivity(context: Context): Class<T> {
        val preferences = AppPreferences(context)
        if (preferences.all.isEmpty()) {
            return WebViewActivity.Small1::class.java as Class<T>
        }
        // 在启动新web进程时，如果当前web进程数达到最大数，则移除最后一个进程
        if (preferences.all.size == MAX_PROCESS_NUMBER) {
            val item = preferences.all.toList()[0]
            LogUtils.d(TAG,"kill name: ${item.key()}  id: ${item.value()}")
            item.value()?.let { android.os.Process.killProcess(it.toInt()) }
            preferences.remove(item.key())
        }
        val ids = mutableListOf<Int>(1, 2, 3, 4)
        val runIds = mutableListOf<Int>()
        // 查询未使用的web进程--small
        run breaking@{
            preferences.all.forEach inside@{
                LogUtils.d(TAG, "item: ${it.key()}  size: ${preferences.all.size}")
                val key = it.key()
                if (key.contains(":app_small")) {
                    val id = key.substring(key.length - 1)
                    runIds.add(id.toInt())
                }
            }
        }
        // 排除真正运行的进程
        ids.removeAll(runIds)
        var id = ids[0]
        LogUtils.d(TAG, "id: $id  class name: ${smallClass[id-1]}")
        return smallClass[id-1] as Class<T>

    }

    @SuppressLint("PrivateApi", "DiscouragedPrivateApi")
    private fun getActivityTasks() {
        val clz = Class.forName("android.app.ActivityThread")
        val currentActivityThreadMethod = clz.getDeclaredMethod("currentActivityThread")
        currentActivityThreadMethod.isAccessible = true
        val sCurrentActivityThread = currentActivityThreadMethod.invoke(null) // sCurrentActivityThread

        val mActivitiesField = sCurrentActivityThread.javaClass.getDeclaredField("mActivities")
        mActivitiesField.isAccessible = true
        // final ArrayMap<IBinder, ActivityClientRecord> mActivities = new ArrayMap<>();
        val mActivities = mActivitiesField.get(sCurrentActivityThread) as ArrayMap<*, *> // mActivities

        val clazz = Class.forName("android.app.ActivityThread\$ActivityClientRecord")
        val activityField = clazz.getDeclaredField("activity")
        activityField.isAccessible = true
        mActivities.forEach {
            LogUtils.d("mActivities key: ${it.key}  value: ${it.value}")
            val actiRecor = it.key as IBinder


            val activity = activityField.get(it.value) as Activity
            LogUtils.d("activity info: ${activity.javaClass.canonicalName}")
        }
    }


}
