package com.kayu.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.util.DisplayMetrics
import android.view.Display
import android.view.View
import android.view.ViewConfiguration
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.text.DecimalFormat
import java.text.NumberFormat

/**
 * @author  ddnosh
 * @website http://blog.csdn.net/ddnosh
 */
object AppUtil {
    /**
     * 调用第三方浏览器打开
     * @param context
     * @param url 要浏览的资源地址
     */
    fun openBrowser(context: Context, url: String?) {
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.data = Uri.parse(url)
        // 注意此处的判断intent.resolveActivity()可以返回显示该Intent的Activity对应的组件名
        // 官方解释 : Name of the component implementing an activity that can display the intent
        if (intent.resolveActivity(context.packageManager) != null) {
            val componentName = intent.resolveActivity(context.packageManager)
            // 打印Log   ComponentName到底是什么
            LogUtil.d("hm", "componentName = " + componentName.className)
            context.startActivity(Intent.createChooser(intent, "请选择浏览器"))
        } else {
            Toast.makeText(context.applicationContext, "请下载浏览器", Toast.LENGTH_SHORT).show()
            //            Toasty.warning(context.getApplicationContext(), "请下载浏览器", Toast.LENGTH_SHORT).show();
        }
    }

    fun getAppName(context: Context): String? {
        try {
            val packageManager = context.packageManager
            val packageInfo = packageManager.getPackageInfo(
                context.packageName, 0
            )
            val labelRes = packageInfo.applicationInfo.labelRes
            return context.resources.getString(labelRes)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * get App versionCode
     * @param context
     * @return
     */
    fun getVersionCode(context: Context): String {
        val packageManager = context.packageManager
        val packageInfo: PackageInfo
        var versionCode = ""
        try {
            packageInfo = packageManager.getPackageInfo(context.packageName, 0)
            versionCode = packageInfo.versionCode.toString() + ""
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return versionCode
    }

    /**
     * get App versionName
     * @param context
     * @return
     */
    fun getVersionName(context: Context): String {
        val packageManager = context.packageManager
        val packageInfo: PackageInfo
        var versionName = ""
        try {
            packageInfo = packageManager.getPackageInfo(context.packageName, 0)
            versionName = packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return versionName
    }

    /**
     * 安装一个apk文件
     *
     * @param file
     */
    fun installApk(activity: Activity, filePath: String?) {
        val intent = Intent(Intent.ACTION_VIEW)
        val apkFile = File(filePath)
        var data: Uri? = null
        // 判断版本大于等于7.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            data = FileProvider.getUriForFile(activity, Constants.authority, apkFile)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        } else {
            data = Uri.fromFile(apkFile)
        }
        intent.setDataAndType(data, "application/vnd.android.package-archive")
        activity.startActivity(intent)
    }

    /***
     * 显示底部虚拟按键的颜色
     *
     * @param activity
     */
    fun setBottomNavigationColor(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = activity.window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.navigationBarColor = Color.BLACK
        }
    }

    /***
     * 隐藏虚拟按键
     *
     * @param activity
     */
    fun hideVirtualNavigation(activity: Activity) {
        if (hasNavBar(activity.applicationContext)) {
            hideBottomUIMenu(activity)
        }
    }

    /**
     * 隐藏虚拟按键，并且全屏
     */
    private fun hideBottomUIMenu(activity: Activity) {
        // 隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            val v = activity.window.decorView
            v.systemUiVisibility = View.GONE
        } else if (Build.VERSION.SDK_INT >= 19) {
            // for new api versions.
            val decorView = activity.window.decorView
            val uiOptions =
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE
            decorView.systemUiVisibility = uiOptions
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        }
    }

    /**
     * 检查是否存在虚拟按键栏
     *
     * @param context
     * @return
     */
    fun hasNavBar(context: Context): Boolean {
        val res = context.resources
        val resourceId = res.getIdentifier("config_showNavigationBar", "bool", "android")
        return if (resourceId != 0) {
            var hasNav = res.getBoolean(resourceId)
            // check override flag
            val sNavBarOverride = navBarOverride
            if ("1" == sNavBarOverride) {
                hasNav = false
            } else if ("0" == sNavBarOverride) {
                hasNav = true
            }
            hasNav
        } else { // fallback
            !ViewConfiguration.get(context).hasPermanentMenuKey()
        }
    }

    /**
     * 判断虚拟按键栏是否重写
     *
     * @return
     */
    private val navBarOverride: String?
        private get() {
            var sNavBarOverride: String? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                try {
                    val c = Class.forName("android.os.SystemProperties")
                    val m = c.getDeclaredMethod("get", String::class.java)
                    m.isAccessible = true
                    sNavBarOverride = m.invoke(null, "qemu.hw.mainkeys") as String
                } catch (e: Throwable) {
                }
            }
            return sNavBarOverride
        }

//    fun getNavigationBarHeight(activity: Activity): Int {
//        val resources = activity.resources
//        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
//        val height = resources.getDimensionPixelSize(resourceId)
//        LogUtil.e("hm", "Navi height:$height")
//        return height
//    }

    /**
     * 判断是否有NavigationBar
     *
     * @param activity
     * @return
     */
    fun checkHasNavigationBar(activity: Activity): Boolean {
        val windowManager = activity.windowManager
        val d: Display = windowManager.defaultDisplay
        val realDisplayMetrics = DisplayMetrics()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            d.getRealMetrics(realDisplayMetrics)
        }
        val realHeight: Int = realDisplayMetrics.heightPixels
        val realWidth: Int = realDisplayMetrics.widthPixels
        val displayMetrics = DisplayMetrics()
        d.getMetrics(displayMetrics)
        val displayHeight: Int = displayMetrics.heightPixels
        val displayWidth: Int = displayMetrics.widthPixels
        return realWidth - displayWidth > 0 || realHeight - displayHeight > 0
    }

    /**
     * 获得NavigationBar的高度
     */
    fun getNavigationBarHeight(activity: Activity): Int {
        var result = 0
        val resources: Resources = activity.resources
        val resourceId: Int = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        if (resourceId > 0 && checkHasNavigationBar(activity)) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        LogUtil.e("hm", "NavigationBar height:$result")
        return result
    }


    fun getStringAmount(amuont: Long): String {
        val format = DecimalFormat("0.00")
        return format.format((amuont.toFloat() / 100).toDouble())
    }

    fun getStringAutoAmount(amount: Long): String {
        return if (amount % 100 == 0L) {
            getStringIntAmount(amount)
        } else {
            getStringAmount(amount)
        }
    }

    fun getStringIntAmount(amuont: Long): String {
        return (amuont / 100).toString()
    }

    fun getStringDouble(amuont: Double): String {
        val nf = NumberFormat.getNumberInstance()
        //digits 显示的数字位数 为格式化对象设定小数点后的显示的最多位,显示的最后位是舍入的
        nf.maximumFractionDigits = 2
        return nf.format(amuont)
    }

    fun doubleTrans(d: Double): String {
        if (Math.round(d) - d == 0.0) {
            return d.toString()
        }
        return d.toString()
    }

    fun formartCard(number: String): String {
        val decimalFormat: NumberFormat = DecimalFormat("####,####,####,####,####")
        val intCardNumber = number.toLong()
        val numStr = decimalFormat.format(intCardNumber)
        return numStr.replace(",".toRegex(), "  ")
    }

    fun showSoftInputFromWindow(activity: Activity, editText: EditText) {
        editText.isFocusable = true
        editText.isFocusableInTouchMode = true
        editText.requestFocus()
        activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    }
}