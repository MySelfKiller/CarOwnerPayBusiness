package com.kayu.utils

import android.view.View
import kotlin.jvm.JvmOverloads
import androidx.viewpager.widget.ViewPager
import com.kayu.utils.callback.ImageCallback
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.kayu.utils.location.LocationCallback
import com.amap.api.location.AMapLocationListener
import com.amap.api.location.AMapLocationQualityReport
import com.kayu.utils.location.LocationManagerUtil
import kotlin.jvm.Synchronized
import com.kayu.utils.location.CoordinateTransformUtil
import com.kayu.utils.permission.EasyPermissions
import com.kayu.utils.permission.EasyPermissions.PermissionWithDialogCallbacks
import com.kayu.utils.permission.EasyPermissions.DialogCallback
import com.kayu.utils.permission.EasyPermissions.PermissionCallbacks
import com.kayu.utils.permission.AfterPermissionGranted
import com.kayu.utils.AppUtil
import kotlin.Throws
import com.kayu.utils.IMEIUtil
import com.kayu.utils.status_bar_set.OSUtils
import com.kayu.utils.status_bar_set.StatusBarUtil
import com.kayu.utils.status_bar_set.SystemBarTintManager
import com.kayu.utils.status_bar_set.StatusBarUtil.ViewType
import com.kayu.utils.status_bar_set.SystemBarTintManager.SystemBarConfig
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kayu.utils.GsonHelper
import com.google.gson.GsonBuilder
import com.google.zxing.EncodeHintType
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.BarcodeFormat
import com.kayu.utils.QRCodeUtil
import com.google.zxing.WriterException
import com.kayu.utils.DoubleUtils
import com.kayu.utils.ScreenUtils
import com.kayu.utils.DesCoderUtil
import com.kayu.utils.DisplayUtils
import com.kayu.utils.GetJuLiUtils
import com.kayu.utils.DeviceIdUtils
import com.google.zxing.DecodeHintType
import com.kayu.utils.QRCodeParseUtils
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.MultiFormatReader
import com.google.zxing.BinaryBitmap
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.common.GlobalHistogramBinarizer
import java.util.*

abstract class NoMoreClickListener : View.OnClickListener {
    private var MIN_CLICK_DELAY_TIME = 1000 //多少秒点击一次 默认2.5秒
    private var lastClickTime: Long = 0

    constructor() {}

    /**
     * 设置多少秒之内
     *
     * @param time
     */
    constructor(time: Int) {
        MIN_CLICK_DELAY_TIME = time
    }

    override fun onClick(view: View) {
        val currentTime = Calendar.getInstance().timeInMillis
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime
            OnMoreClick(view)
        } else {
            OnMoreErrorClick()
        }
    }

    /**
     * 在N秒之内的 ==1 次点击回调次方法
     *
     * @param view
     */
    protected abstract fun OnMoreClick(view: View)

    /**
     * 在N秒之内的 >= 2次点击回调次方法
     */
    protected abstract fun OnMoreErrorClick()
}