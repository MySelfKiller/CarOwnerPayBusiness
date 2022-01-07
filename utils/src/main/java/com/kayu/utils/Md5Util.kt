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
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.lang.StringBuilder
import java.nio.channels.FileChannel
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * Created by Vector
 * on 2017/7/11 0011.
 */
object Md5Util {
    fun getStringMD5(string: String): String {
        if (StringUtil.isEmpty(string)) {
            return ""
        }
        var md5: MessageDigest? = null
        try {
            md5 = MessageDigest.getInstance("MD5")
            val bytes = md5.digest(string.toByteArray())
            val result = StringBuilder()
            for (b in bytes) {
                var temp = Integer.toHexString(b.toInt() and 0xff)
                if (temp.length == 1) {
                    temp = "0$temp"
                }
                result.append(temp)
            }
            return result.toString()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return ""
    }

    fun getFileMD5(file: File): String {
        if (!file.exists()) {
            return ""
        }
        var `in`: FileInputStream? = null
        try {
            `in` = FileInputStream(file)
            val channel = `in`.channel
            val buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, file.length())
            val md = MessageDigest.getInstance("MD5")
            md.update(buffer)
            return bytes2Hex(md.digest())
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (`in` != null) {
                try {
                    `in`.close()
                } catch (ignored: IOException) {
                }
            }
        }
        return ""
    }

    /**
     * 一个byte转为2个hex字符
     *
     * @param src byte数组
     * @return 16进制大写字符串
     */
    fun bytes2Hex(src: ByteArray): String {
        val res = CharArray(src.size shl 1)
        val hexDigits = charArrayOf(
            '0',
            '1',
            '2',
            '3',
            '4',
            '5',
            '6',
            '7',
            '8',
            '9',
            'a',
            'b',
            'c',
            'd',
            'e',
            'f'
        )
        var i = 0
        var j = 0
        while (i < src.size) {
            res[j++] = hexDigits[src[i].toInt() ushr 4 and 0x0f]
            res[j++] = hexDigits[src[i].toInt() and 0x0f]
            i++
        }
        return String(res)
    }
}