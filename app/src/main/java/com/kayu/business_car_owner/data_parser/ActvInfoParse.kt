package com.kayu.business_car_owner.data_parser

import android.os.Handler
import com.kayu.business_car_owner.http.parser.BaseParse
import kotlin.Throws
import com.kayu.business_car_owner.http.ResponseInfo
import com.kayu.business_car_owner.model.ActivationCard
import com.kayu.utils.GsonHelper
import org.json.JSONObject
import java.lang.Exception

class ActvInfoParse : BaseParse<Any?>() {
    @Throws(Exception::class)
    override fun parseJSON(handler: Handler, jsonStr: String, dataVersion: Double): ResponseInfo {
        val obj = JSONObject(jsonStr)
        val status = obj.optInt("status")
        val msg = obj.optString("message")
        val responseInfo = ResponseInfo(status, msg)
        if (status == 1) {
            val dataObj = obj.optJSONObject("data")
            if (null != dataObj && dataObj.length() > 0) {
                val subItem = GsonHelper.fromJson(dataObj.toString(), ActivationCard::class.java)
                responseInfo.responseData = subItem
            }
        }
        return responseInfo
    }
}