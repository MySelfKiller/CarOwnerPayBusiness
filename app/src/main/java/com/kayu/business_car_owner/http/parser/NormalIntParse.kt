package com.kayu.business_car_owner.http.parser

import android.os.Handler
import kotlin.Throws
import com.kayu.business_car_owner.http.ResponseInfo
import org.json.JSONObject
import java.lang.Exception

class NormalIntParse : BaseParse<Any?>() {
    @Throws(Exception::class)
    override fun parseJSON(
        handler: Handler,
        jsonStr: String,
        dataVersion: Double
    ): ResponseInfo {
        val jsonObject = JSONObject(jsonStr)
        val status = jsonObject.optInt("status")
        val msg = jsonObject.optString("message")
        val data = jsonObject.optInt("data")
        val responseInfo = ResponseInfo(status, msg)
        responseInfo.responseData = data
        return responseInfo
    }
}