package com.kayu.business_car_owner.data_parser

import android.os.Handler
import com.kayu.business_car_owner.http.ResponseInfo
import com.kayu.business_car_owner.http.parser.BaseParse
import com.kayu.business_car_owner.model.ImgCode
import kotlin.Throws
import com.kayu.utils.GsonHelper
import org.json.JSONObject
import java.lang.Exception

class ImgCodeDataParse : BaseParse<Any?>() {
    @Throws(Exception::class)
    override fun parseJSON(
        handler: Handler,
        jsonStr: String,
        dataVersion: Double
    ): ResponseInfo {
        val obj = JSONObject(jsonStr)
        val status = obj.optInt("status")
        val msg = obj.optString("message")
        val responseInfo = ResponseInfo(status, msg)
        if (status == 1) {
            val dataObj = obj.optJSONObject("data")
            if (null != dataObj) {
                val userBean =
                    GsonHelper.fromJson(obj.optJSONObject("data")?.toString(), ImgCode::class.java)
                if (userBean != null) {
                    responseInfo.responseData = userBean
                }
            }
        }
        return responseInfo
    }
}