package com.kayu.business_car_owner.http.parser

import android.os.Handler
import kotlin.Throws
import com.kayu.business_car_owner.http.ResponseInfo
import org.json.JSONObject
import java.lang.Exception
import java.util.ArrayList

class NormalStringListParse : BaseParse<Any?>() {
    @Throws(Exception::class)
    override fun parseJSON(
        handler: Handler,
        jsonStr: String,
        dataVersion: Double
    ): ResponseInfo {
        val jsonObject = JSONObject(jsonStr)
        val status = jsonObject.optInt("status")
        val msg = jsonObject.optString("message")
        val responseInfo = ResponseInfo(status, msg)
        if (status == 1) {
            val data = jsonObject.optJSONArray("data")
            if (null != data && data.length() > 0) {
                val strList: MutableList<String> = ArrayList()
                for (x in 0 until data.length()) {
                    strList.add(data[x] as String)
                    responseInfo.responseData = strList
                }
            }
        }
        return responseInfo
    }
}