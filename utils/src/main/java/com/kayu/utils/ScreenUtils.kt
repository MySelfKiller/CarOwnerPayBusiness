package com.kayu.utils

import android.R
import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.os.Build
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.lang.StringBuilder
import java.lang.reflect.Field

object ScreenUtils {
    private const val TAG = "WXTBUtil"
    private var isSupportSmartBar = false
    fun getDisplayWidth(activity: Activity?): Int {
        var width = 0
        if (activity != null && activity.windowManager != null && activity.windowManager.defaultDisplay != null) {
            val point = Point()
            activity.windowManager.defaultDisplay.getSize(point)
            width = point.x
        }
        return width
    }

    fun getDisplayHeight(activity: Activity?): Int {
        var height = 0
        if (activity != null && activity.windowManager != null && activity.windowManager.defaultDisplay != null) {
            val point = Point()
            activity.windowManager.defaultDisplay.getSize(point)
            height = point.y
        }
        LogUtil.e(TAG, "isSupportSmartBar:" + isSupportSmartBar)
        if (isSupportSmartBar) {
            val smartBarHeight = getSmartBarHeight(activity)
            LogUtil.e(TAG, "smartBarHeight:$smartBarHeight")
            height -= smartBarHeight
        }
        if (activity != null && activity.actionBar != null) {
            var actionbar = activity.actionBar!!.height
            if (actionbar == 0) {
                val actionbarSizeTypedArray =
                    activity.obtainStyledAttributes(intArrayOf(R.attr.actionBarSize))
                actionbar = actionbarSizeTypedArray.getDimension(0, 0f).toInt()
            }
            LogUtil.d(TAG, "actionbar:$actionbar")
            height -= actionbar
        }
        val status = getStatusBarHeight(activity)
        LogUtil.d(TAG, "status:$status")
        height -= status
        LogUtil.d(TAG, "height:$height")
        return height
    }

    private fun getStatusBarHeight(activity: Activity?): Int {
        val c: Class<*>
        val obj: Any
        val field: Field
        val x: Int
        var statusBarHeight = 0
        try {
            c = Class.forName("com.android.internal.R\$dimen")
            obj = c.newInstance()
            field = c.getField("status_bar_height")
            x = field[obj].toString().toInt()
            statusBarHeight = activity!!.resources.getDimensionPixelSize(x)
        } catch (e1: Exception) {
            e1.printStackTrace()
        }
        return statusBarHeight
    }

    private fun getSmartBarHeight(activity: Activity?): Int {
        val actionbar = activity!!.actionBar
        if (actionbar != null) try {
            val c = Class.forName("com.android.internal.R\$dimen")
            val obj = c.newInstance()
            val field = c.getField("mz_action_button_min_height")
            val height = field[obj].toString().toInt()
            return activity.resources.getDimensionPixelSize(height)
        } catch (e: Exception) {
            e.printStackTrace()
            actionbar.height
        }
        return 0
    }

    private fun isSupportSmartBar(): Boolean {
        var hasSmartBar = false
        try {
            val method = Build::class.java.getMethod("hasSmartBar")
            if (method != null) {
                hasSmartBar = true
            }
        } catch (e: Exception) {
            // return false;
        }
        return hasSmartBar
    }

    fun <T : Exception> throwIfNull(obj: Any?, e: T) {
        if (obj == null) {
            throw e
        }
    }

    /**
     * dp转px
     *
     * @param context context
     * @param dpValue dp
     * @return px
     */
    fun dipToPx(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    fun getDp(saveFilePath: String) {
        val width = 1080 //屏幕宽度
        val height = 1920 //屏幕高度
        val screenInch = 5.0f //屏幕尺寸
        //设备密度公式
        val density = Math.sqrt((width * width + height * height).toDouble())
            .toFloat() / screenInch / 160
        print("设备密度$density")
        val stringBuilder = StringBuilder()
        stringBuilder.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<resources>\n")
        var px = 0
        while (px <= width) {

            //像素值除以density
            var dp: String = (px * 1.0f / density).toString() + ""
            //拼接成资源文件的内容，方便引用
            if (dp.indexOf(".") + 4 < dp.length) { //保留3位小数
                dp = dp.substring(0, dp.indexOf(".") + 4)
            }
            stringBuilder.append("<dimen name=\"px").append(px.toString() + "").append("dp\">")
                .append(dp).append("dp</dimen>\n")
            px += 2
        }
        stringBuilder.append("</resources>")
        saveFile(stringBuilder.toString(), saveFilePath)
    }

    fun saveFile(str: String, filePath: String) {
        try {
            val file = File(filePath + "px2dp.txt")
            if (!file.exists()) {
                val dir = File(file.parent)
                dir.mkdirs()
                file.createNewFile()
            }
            val outStream = FileOutputStream(file)
            outStream.write(str.toByteArray())
            outStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    init {
        isSupportSmartBar = isSupportSmartBar()
    }
}