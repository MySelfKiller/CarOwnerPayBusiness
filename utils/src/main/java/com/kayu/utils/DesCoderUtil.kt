package com.kayu.utils

import android.util.Base64
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
import java.lang.Exception
import java.lang.RuntimeException
import java.nio.charset.Charset
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.DESKeySpec
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object DesCoderUtil {
    private val keys = byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0)
    var key = "leagsoft"

    /**
     *
     *
     *
     * 对password进行MD5加密
     * @param source
     * @return
     * @return byte[]
     * author: Heweipo
     */
    fun getMD5(source: ByteArray?): ByteArray? {
        var tmp: ByteArray? = null
        try {
            val md = MessageDigest
                .getInstance("MD5")
            md.update(source)
            tmp = md.digest()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return tmp
    }

    /**
     *
     *
     *
     * 采用JDK内置类进行真正的加密操作
     * @param byteS
     * @param password
     * @return
     * @return byte[]
     * author: Heweipo
     */
    private fun encryptByte(byteS: ByteArray, password: ByteArray): ByteArray? {
        var byteFina: ByteArray? = null
        byteFina = try { // 初始化加密/解密工具
            val cipher = Cipher.getInstance("DES/CBC/PKCS5Padding")
            val desKeySpec = DESKeySpec(password)
            val keyFactory = SecretKeyFactory.getInstance("DES")
            val secretKey = keyFactory.generateSecret(desKeySpec)
            val iv = IvParameterSpec(keys)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv)
            cipher.doFinal(byteS)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
        return byteFina
    }

    /**
     *
     *
     *
     * 采用JDK对应的内置类进行解密操作
     * @param byteS
     * @param password
     * @return
     * @return byte[]
     * author: Heweipo
     */
    fun decryptByte(byteS: ByteArray?, password: ByteArray?): ByteArray? {
        var byteFina: ByteArray? = null
        byteFina = try { // 初始化加密/解密工具
            val cipher = Cipher.getInstance("DES/CBC/PKCS5Padding")
            val desKeySpec = DESKeySpec(password)
            val keyFactory = SecretKeyFactory.getInstance("DES")
            val secretKey = keyFactory.generateSecret(desKeySpec)
            val iv = IvParameterSpec(keys)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv)
            cipher.doFinal(byteS)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
        return byteFina
    }

    @Throws(Exception::class)
    fun decryptDES(decryptString: String?, decryptKey: String): String {
        val sourceBytes = Base64.decode(decryptString, 0)
        val cipher = Cipher.getInstance("DES/ECB/PKCS5Padding")
        if (decryptKey.toByteArray().size > 8) {
            for (x in keys.indices) {
                keys[x] = decryptKey.toByteArray()[x]
            }
            cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(keys, "DES"))
        } else {
            cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(decryptKey.toByteArray(), "DES"))
        }
        val decoded = cipher.doFinal(sourceBytes)
        return String(decoded, Charset.defaultCharset())
    }

    @Throws(Exception::class)
    fun encryptDES(encryptString: String, encryptKey: String): String {
        val cipher = Cipher.getInstance("DES/ECB/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(encryptKey.toByteArray(), "DES"))
        val encryptedData = cipher.doFinal(encryptString.toByteArray(charset("UTF-8")))
        return String(Base64.encode(encryptedData, 0), Charset.defaultCharset())
    }
}