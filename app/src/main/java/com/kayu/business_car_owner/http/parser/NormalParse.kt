package com.kayu.business_car_owner.http.parser

import android.os.Handler
import com.kayu.business_car_owner.http.ResponseInfo
import org.json.JSONObject

class NormalParse : BaseParse<Any?>() {
    @Throws(Exception::class)
    override fun parseJSON(
        handler: Handler,
        jsonStr: String,
        dataVersion: Double
    ): ResponseInfo? {
        val jsonObject = JSONObject(jsonStr)
        val status = jsonObject.optInt("status")
        val msg = jsonObject.optString("message")
        return ResponseInfo(status, msg)
    }
}