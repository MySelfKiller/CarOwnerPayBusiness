package com.kayu.business_car_owner.http

import android.os.Handler
import kotlin.Throws
import com.kayu.utils.Constants
import com.kayu.utils.LogUtil
import okhttp3.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Created by xubin on 2018/3/14.
 */
class ImageResCallBack(requestInfo: RequestInfo?) : Callback {
    private var reqInfo: RequestInfo? = null
    private var handler: Handler? = null
    private var downfileSize: Long = 0
    private var downloadFile: File? = null
    private var apkSize: Long = 0
    var stop = false
        private set

    fun Stop() {
        stop = true
    }

    override fun onFailure(call: Call, e: IOException) {
        val handler = reqInfo!!.handler
        val responseInfo = ResponseInfo(-1, "网络异常")
        handler!!.sendMessage(handler.obtainMessage(Constants.REQ_NETWORK_ERROR, responseInfo))
        LogUtil.e("network req", "IOException: $e")
    }

    @Throws(IOException::class)
    override fun onResponse(call: Call, response: Response) {
        LogUtil.e(
            "network req", """
     IOException: response.code()=${response.code()}
     $response
     """.trimIndent()
        )
        if (response.code() == 200) {
            val buff = ByteArray(1024)
            var len: Int
            var total = downfileSize
            val `in` = response.body()!!.byteStream()
            var fos: FileOutputStream? = null
            fos = if (downfileSize > 0) FileOutputStream(
                downloadFile,
                true
            ) else FileOutputStream(downloadFile)
            handler!!.sendMessage(
                handler!!.obtainMessage(
                    Constants.PARSE_DATA_REFRESH,
                    downfileSize.toInt(),
                    apkSize.toInt()
                )
            )
            while (`in`.read(buff).also { len = it } != -1) {
                if (stop) {
                    handler!!.sendMessage(
                        handler!!.obtainMessage(
                            Constants.PARSE_DATA_END,
                            total.toInt(),
                            apkSize.toInt()
                        )
                    )
                    return
                }
                fos.write(buff, 0, len)
                total += len.toLong()
                val finalTotal = total
                handler!!.sendMessage(
                    handler!!.obtainMessage(
                        Constants.PARSE_DATA_REFRESH,
                        finalTotal.toInt(),
                        apkSize.toInt()
                    )
                )
            }
            fos.flush()
            fos.close()
            `in`.close()
            handler!!.sendMessage(handler!!.obtainMessage(Constants.PARSE_DATA_SUCCESS))
        } else {
            val obj = ResponseInfo(-1, "网络异常")
            handler!!.sendMessage(handler!!.obtainMessage(Constants.REQ_NETWORK_ERROR, obj))
        }
    }

    init {
        reqInfo = requestInfo
        handler = reqInfo!!.handler
        downloadFile = reqInfo!!.file
        apkSize = reqInfo!!.fileSize
        if (reqInfo!!.file != null) {
            downfileSize = reqInfo!!.file!!.length()
        }
    }
}