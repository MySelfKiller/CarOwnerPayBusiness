package com.kayu.business_car_owner.data_parser

import android.os.Handler
import com.kayu.business_car_owner.http.parser.BaseParse
import kotlin.Throws
import com.kayu.business_car_owner.http.ResponseInfo
import com.kayu.utils.GsonHelper
import com.kayu.business_car_owner.model.SystemParam
import org.json.JSONObject
import java.lang.Exception

/**
 * Author by killer, Email xx@xx.com, Date on 2020/9/28.
 * PS: Not easy to write code, please indicate.
 */
class ParameterDataParser : BaseParse<Any?>() {
    @Throws(Exception::class)
    override fun parseJSON(handler: Handler, jsonStr: String, dataVersion: Double): ResponseInfo {
        val responseJson = JSONObject(jsonStr)
        val state = responseJson.optInt("status")
        val message = responseJson.optString("message")
        val responseInfo = ResponseInfo(state, message)
        if (state == 1) {
            val dataJson = responseJson.optJSONObject("data")
            if (null != dataJson && dataJson.length() > 0) {
//                ArrayList<WashStationBean> listArray = new ArrayList<WashStationBean>();
                responseInfo.responseData =
                    GsonHelper.fromJson(dataJson.toString(), SystemParam::class.java)
            }
        }
        return responseInfo
    }
}