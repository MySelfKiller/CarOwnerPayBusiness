package com.kayu.utils

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
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
import java.io.*
import java.util.*

object DeviceIdUtils {
    private val TAG = DeviceIdUtils::class.java.simpleName
    private const val TEMP_DIR = "system_config"
    private const val TEMP_FILE_NAME = "system_file"
    private const val TEMP_FILE_NAME_MIME_TYPE = "application/octet-stream"
    private const val SP_NAME = "device_info"
    private const val SP_KEY_DEVICE_ID = "device_id"
    fun getDeviceId(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
        var deviceId = sharedPreferences.getString(SP_KEY_DEVICE_ID, null)
        if (!TextUtils.isEmpty(deviceId)) {
            return deviceId
        }
        deviceId = getIMEI(context)
        if (TextUtils.isEmpty(deviceId)) {
            deviceId = createUUID(context)
        }
        sharedPreferences.edit()
            .putString(SP_KEY_DEVICE_ID, deviceId)
            .apply()
        return deviceId
    }

    private fun createUUID(context: Context): String {
        var uuid = UUID.randomUUID().toString().replace("-", "")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val externalContentUri = MediaStore.Downloads.EXTERNAL_CONTENT_URI
            val contentResolver = context.contentResolver
            val projection = arrayOf(
                MediaStore.Downloads._ID
            )
            val selection = MediaStore.Downloads.TITLE + "=?"
            val args = arrayOf(
                TEMP_FILE_NAME
            )
            val query = contentResolver.query(externalContentUri, projection, selection, args, null)
            if (query != null && query.moveToFirst()) {
                val uri = ContentUris.withAppendedId(externalContentUri, query.getLong(0))
                query.close()
                var inputStream: InputStream? = null
                var bufferedReader: BufferedReader? = null
                try {
                    inputStream = contentResolver.openInputStream(uri)
                    if (inputStream != null) {
                        bufferedReader = BufferedReader(InputStreamReader(inputStream))
                        uuid = bufferedReader.readLine()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                } finally {
                    if (bufferedReader != null) {
                        try {
                            bufferedReader.close()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                    if (inputStream != null) {
                        try {
                            inputStream.close()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
            } else {
                val contentValues = ContentValues()
                contentValues.put(MediaStore.Downloads.TITLE, TEMP_FILE_NAME)
                contentValues.put(MediaStore.Downloads.MIME_TYPE, TEMP_FILE_NAME_MIME_TYPE)
                contentValues.put(MediaStore.Downloads.DISPLAY_NAME, TEMP_FILE_NAME)
                contentValues.put(
                    MediaStore.Downloads.RELATIVE_PATH,
                    Environment.DIRECTORY_DOWNLOADS + File.separator + TEMP_DIR
                )
                val insert = contentResolver.insert(externalContentUri, contentValues)
                if (insert != null) {
                    var outputStream: OutputStream? = null
                    try {
                        outputStream = contentResolver.openOutputStream(insert)
                        if (outputStream == null) {
                            return uuid
                        }
                        outputStream.write(uuid.toByteArray())
                    } catch (e: IOException) {
                        e.printStackTrace()
                    } finally {
                        if (outputStream != null) {
                            try {
                                outputStream.close()
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }
        } else {
            val externalDownloadsDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val applicationFileDir = File(externalDownloadsDir, TEMP_DIR)
            if (!applicationFileDir.exists()) {
                if (!applicationFileDir.mkdirs()) {
                    Log.e(TAG, "文件夹创建失败: " + applicationFileDir.path)
                }
            }
            val file = File(applicationFileDir, TEMP_FILE_NAME)
            if (!file.exists()) {
                var fileWriter: FileWriter? = null
                try {
                    if (file.createNewFile()) {
                        fileWriter = FileWriter(file, false)
                        fileWriter.write(uuid)
                    } else {
                        Log.e(TAG, "文件创建失败：" + file.path)
                    }
                } catch (e: IOException) {
                    Log.e(TAG, "文件创建失败：" + file.path)
                    e.printStackTrace()
                } finally {
                    if (fileWriter != null) {
                        try {
                            fileWriter.close()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
            } else {
                var fileReader: FileReader? = null
                var bufferedReader: BufferedReader? = null
                try {
                    fileReader = FileReader(file)
                    bufferedReader = BufferedReader(fileReader)
                    uuid = bufferedReader.readLine()
                    bufferedReader.close()
                    fileReader.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                } finally {
                    if (bufferedReader != null) {
                        try {
                            bufferedReader.close()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                    if (fileReader != null) {
                        try {
                            fileReader.close()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
        return uuid
    }

    fun getIMEI(context: Context): String? {
        var imei = IMEIUtil.getMeidOnly(context, 0)
        if (StringUtil.isEmpty(imei)) {
            imei = IMEIUtil.getIMEI1(context)
            if (StringUtil.isEmpty(imei)) {
                imei = IMEIUtil.getDeviceId(context)
                if (StringUtil.isEmpty(imei)) {
                    val aid = Settings.System.getString(
                        context.contentResolver, Settings.Secure.ANDROID_ID
                    )
                    if (!StringUtil.isEmpty(aid)) {
//                    aid = Md5Util.getStringMD5(aid);
                        imei = "aid#$aid"
                    }
                }
            }
        }
        //        LogUtil.e("getDeviceId",imei);
        return imei
    }
}