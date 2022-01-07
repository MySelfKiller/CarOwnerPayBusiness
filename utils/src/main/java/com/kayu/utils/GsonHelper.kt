package com.kayu.utils

import android.text.TextUtils
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
import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception

/**
 * @author  ddnosh
 * @website http://blog.csdn.net/ddnosh
 */
object GsonHelper {
    private const val TAG = "GsonHelper"
    private var gson: Gson? = null

    /**
     * 返回指定key的值
     *
     * @param json
     * @param key
     * @param type 数据类型0表示String类，1表示int类型
     * @return
     */
    fun getField(json: String?, key: String?, type: Int): Any? {
        if (StringUtil.isEmpty(json) || StringUtil.isEmpty(key)) return null
        try {
            val obj = JSONObject(json)
            if (obj.has(key)) {
                return if (type == 0) {
                    obj.getString(key)
                } else {
                    obj.getInt(key)
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return null
    }

    fun <T> fromJson(JSONString: String?, typeToken: TypeToken<T>): T? {
        if (TextUtils.isEmpty(JSONString)) return null
        var t: T? = null
        t = try {
            gson!!.fromJson(JSONString, typeToken.type)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
        return t
    }

    fun <T> fromJson(JSONString: String?, classOfT: Class<T>?): T? {
        if (TextUtils.isEmpty(JSONString)) return null
        var t: T? = null
        t = try {
            gson!!.fromJson(JSONString, classOfT)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
        return t
    }

    fun toJsonString(obj: Any?): String {
        var gsonString: String = ""
        if (gson != null) {
            gsonString = gson!!.toJson(obj)
        }
        return gsonString
    }

    init {
        if (gson == null) {
            gson = GsonBuilder().disableHtmlEscaping().create()
        }
    }
}