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
import java.io.Closeable
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.AssertionError
import java.lang.Exception
import java.lang.RuntimeException

/**
 * @author  ddnosh
 * @website http://blog.csdn.net/ddnosh
 */
class IOUtil private constructor() {
    companion object {
        /**
         * Close closable object and wrap [IOException] with [ ]
         *
         * @param closeable closeable object
         */
        fun close(closeable: Closeable?) {
            if (closeable != null) {
                try {
                    closeable.close()
                } catch (e: IOException) {
                    throw RuntimeException("IOException occurred. ", e)
                }
            }
        }

        /**
         * Close closable and hide possible [IOException]
         *
         * @param closeable closeable object
         */
        fun closeQuietly(closeable: Closeable?) {
            if (closeable != null) {
                try {
                    closeable.close()
                } catch (e: IOException) {
                    // Ignored
                }
            }
        }

        /**
         * 保存文本
         * @param fileName  文件名字
         * @param content  内容
         * @param append  是否累加
         * @return  是否成功
         */
        fun saveTextValue(fileName: String?, content: String, append: Boolean): Boolean {
            try {
                val textFile = File(fileName)
                if (!append && textFile.exists()) textFile.delete()
                val os = FileOutputStream(textFile)
                os.write(content.toByteArray(charset("UTF-8")))
                os.close()
            } catch (ee: Exception) {
                return false
            }
            return true
        }

        /**
         * 删除目录下所有文件
         * @param Path    路径
         */
        fun deleteAllFile(Path: String?) {

            // 删除目录下所有文件
            val path = File(Path)
            val files = path.listFiles()
            if (files != null) {
                for (tfi in files) {
                    if (tfi.isDirectory) {
                        println(tfi.name)
                    } else {
                        tfi.delete()
                    }
                }
            }
        }
    }

    init {
        throw AssertionError()
    }
}