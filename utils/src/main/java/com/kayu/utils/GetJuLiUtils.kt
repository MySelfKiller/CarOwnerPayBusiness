package com.kayu.utils

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
import java.math.BigDecimal

/**
 * Author by killer, Email xx@xx.com, Date on 2020/10/22.
 * PS: Not easy to write code, please indicate.
 */
object GetJuLiUtils {
    /**
     * 根据两个经纬度计算距离km
     * @return
     */
    fun distance(
        latitude: Double,
        longitude: Double,
        latitude2: Double,
        longitude2: Double
    ): Double {
//        double lat1 = Double.parseDouble(lat1Str);
//        double lng1 = Double.parseDouble(lng1Str);
//        double lat2 = Double.valueOf(lat2Str);
//        double lng2 = Double.valueOf(lng2Str);
        val a1 = Math.pow(Math.sin((latitude - Math.abs(latitude2)) * Math.PI / 180 / 2), 2.0)
        val a2 = Math.cos(latitude * Math.PI / 180)
        val a3 = Math.cos(Math.abs(latitude2) * Math.PI / 180)
        val a4 = Math.pow(Math.sin((longitude - longitude2) * Math.PI / 180 / 2), 2.0)
        val result = EARTH_RADIUS * 2 * Math.asin(Math.sqrt(a1 + a2 * a3 * a4))
        return BigDecimal(result / 1000).setScale(1, BigDecimal.ROUND_HALF_UP).toDouble()
    }

    private const val EARTH_RADIUS = 6378137.0
    fun getDistance(
        longitude: Double,
        latitue: Double,
        longitude2: Double,
        latitue2: Double
    ): Double {
        val lat1 = rad(latitue)
        val lat2 = rad(latitue2)
        val a = lat1 - lat2
        val b = rad(longitude) - rad(longitude2)
        var s = 2 * Math.asin(
            Math.sqrt(
                Math.pow(Math.sin(a / 2), 2.0) + Math.cos(lat1) * Math.cos(lat2) * Math.pow(
                    Math.sin(b / 2), 2.0
                )
            )
        )
        s = s * EARTH_RADIUS
        s = (Math.round(s * 10000) / 10000).toDouble()
        return BigDecimal(s / 1000).setScale(1, BigDecimal.ROUND_HALF_UP).toDouble()
    }

    private fun rad(d: Double): Double {
        return d * Math.PI / 180.0
    }
}