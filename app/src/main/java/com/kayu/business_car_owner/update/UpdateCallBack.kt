package com.kayu.business_car_owner.update

import android.os.Handler
import com.kayu.business_car_owner.http.RequestInfo
import com.kayu.business_car_owner.http.ResponseInfo
import kotlin.Throws
import com.kayu.utils.Constants
import com.kayu.utils.LogUtil
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import java.lang.Exception

/**
 * Created by hm on 2018/3/14.
 */
class UpdateCallBack(info: RequestInfo?) : Callback {
    private var reqInfo: RequestInfo? = null
    private var handler: Handler? = null
    override fun onFailure(call: Call, e: IOException) {
        val handler = reqInfo!!.handler
        val responseInfo = ResponseInfo(-1, "网络异常")
        handler!!.sendMessage(handler.obtainMessage(Constants.PARSE_DATA_SUCCESS, responseInfo))
        LogUtil.e("network req", "IOException: $e")
    }

    @Throws(IOException::class)
    override fun onResponse(call: Call, response: Response) {
        val result = response.body()!!.string()
        LogUtil.e("network req", "errorcode:" + response.code())
        LogUtil.e("network req", "返回的数据: $result")
        var obj: ResponseInfo? = null
        if (null == result) {
            obj = ResponseInfo(-1, "网络异常")
            handler!!.sendMessage(handler!!.obtainMessage(Constants.PARSE_DATA_SUCCESS, obj))
            return
        }
        try {
            obj = reqInfo!!.parser!!.parseJSON(reqInfo!!.handler!!, result, 0.0) as ResponseInfo?
        } catch (e: Exception) {
            e.printStackTrace()
            obj = ResponseInfo(-1, "服务器出错，稍后重试")
            handler!!.sendMessage(handler!!.obtainMessage(Constants.PARSE_DATA_SUCCESS, obj))
        }
        handler!!.sendMessage(handler!!.obtainMessage(Constants.PARSE_DATA_SUCCESS, obj))
    }

    init {
        reqInfo = info
        handler = reqInfo!!.handler
    }
}