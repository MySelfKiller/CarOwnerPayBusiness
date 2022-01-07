package com.kayu.business_car_owner.activity

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import java.lang.Exception
import java.util.*

class AppManager {
    /**
     * 添加Activity到堆栈
     */
    fun addActivity(activity: Activity?) {
        if (activityStack == null) {
            activityStack = Stack()
        }
        activityStack!!.add(activity)
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    fun currentActivity(): Activity? {
        val activity: Activity? = activityStack!!.lastElement()
        return activity
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    fun finishActivity() {
        var activity: Activity? = activityStack!!.lastElement()
        if (activity != null) {
            activity.finish()
            activity = null
        }
    }

    /**
     * 结束指定的Activity
     */
    fun finishActivity(activity: Activity?) {
        var activity: Activity? = activity
        if (activity != null) {
            activityStack!!.remove(activity)
            activity.finish()
            activity = null
        }
    }

    /**
     * 结束指定类名的Activity
     */
    fun finishActivity(cls: Class<*>) {
        for (activity: Activity? in activityStack!!) {
            if ((activity!!.javaClass == cls)) {
                finishActivity(activity)
            }
        }
    }

    /**
     * 结束所有Activity
     */
    fun finishAllActivity() {
        var i: Int = 0
        val size: Int = activityStack!!.size
        while (i < size) {
            if (null != activityStack!!.get(i)) {
                activityStack!!.get(i)!!.finish()
            }
            i++
        }
        activityStack!!.clear()
    }

    /**
     * 退出应用程序
     */
    fun AppExit(context: Context) {
        try {
            finishAllActivity()
            val activityMgr: ActivityManager = context
                .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            activityMgr.restartPackage(context.getPackageName())
            System.exit(0)
        } catch (e: Exception) {
        }
    }

    companion object {
        private var activityStack: Stack<Activity?>? = null
        private var instance: AppManager? = null

        /**
         * 单例模式实例
         */
        @kotlin.jvm.JvmStatic
        val appManager: AppManager?
            get() {
                if (instance == null) {
                    instance = AppManager()
                }
                return instance
            }
    }
}