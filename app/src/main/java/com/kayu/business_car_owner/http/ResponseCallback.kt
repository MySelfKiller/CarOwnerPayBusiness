package com.kayu.business_car_owner.http

import android.content.Intent
import android.os.Handler
import kotlin.Throws
import com.kayu.business_car_owner.KWApplication
import com.kayu.utils.Constants
import com.kayu.utils.LogUtil
import okhttp3.*
import java.io.IOException
import java.lang.Exception

/**
 * Created by hm on 2018/10/31.
 */
class ResponseCallback(requestInfo: RequestInfo?) : Callback {
    private var reqInfo: RequestInfo? = null
    private var handler: Handler? = null
    override fun onFailure(call: Call, e: IOException) {
//        Handler handler = reqInfo.handler;
        val responseInfo = ResponseInfo(-1, "网络异常")
        handler!!.sendMessage(handler!!.obtainMessage(Constants.REQ_NETWORK_ERROR, responseInfo))
        LogUtil.e("network req", "IOException: $e")
    }

    @Throws(IOException::class)
    override fun onResponse(call: Call, response: Response) {
        val result = response.body()!!.string()
        LogUtil.e("network req", "errorcode:" + response.code())
        LogUtil.e("network req", "返回的数据: $result")
        var obj: ResponseInfo? = null
        if (response.code() == 200) {
            if (null == result) {
                obj = ResponseInfo(-1, "网络异常")
                handler!!.sendMessage(handler!!.obtainMessage(Constants.REQ_NETWORK_ERROR, obj))
                return
            }
            try {
                obj = reqInfo!!.parser!!.parseJSON(reqInfo!!.handler!!, result, 0.0) as ResponseInfo
                if (obj!!.status == Constants.response_code_10101 || obj.status == Constants.response_code_10102) {
                    handler!!.sendMessage(handler!!.obtainMessage(Constants.PARSE_DATA_ERROR, obj))
                    val intent = Intent("com.kayu.broadcasttest.JUMP")
                    KWApplication.instance.localBroadcastManager?.sendBroadcast(intent) // 发送本地广播
                } else if (obj.status == Constants.response_code_1) {
                    handler!!.sendMessage(
                        handler!!.obtainMessage(
                            Constants.PARSE_DATA_SUCCESS,
                            obj
                        )
                    )
                } else {
                    handler!!.sendMessage(handler!!.obtainMessage(Constants.PARSE_DATA_ERROR, obj))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                obj = ResponseInfo(-1, "服务器出错，稍后重试")
                handler!!.sendMessage(handler!!.obtainMessage(Constants.PARSE_DATA_ERROR, obj))
            }
        } else {
            obj = ResponseInfo(-1, "网络异常")
            handler!!.sendMessage(handler!!.obtainMessage(Constants.REQ_NETWORK_ERROR, obj))
        }
    }

    init {
        reqInfo = requestInfo
        handler = reqInfo!!.handler
    }
}