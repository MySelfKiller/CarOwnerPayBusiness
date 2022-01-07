package com.kayu.business_car_owner.data_parser

import android.os.Handler
import com.kayu.business_car_owner.http.parser.BaseParse
import kotlin.Throws
import com.kayu.business_car_owner.http.ResponseInfo
import com.kayu.utils.GsonHelper
import com.kayu.business_car_owner.model.OilStationBean
import org.json.JSONObject

/**
 * Author by killer, Email xx@xx.com, Date on 2020/9/28.
 * PS: Not easy to write code, please indicate.
 */
class StationListDataParser : BaseParse<Any?>() {
    @Throws(Exception::class)
    override fun parseJSON(handler: Handler, jsonStr: String, dataVersion: Double): ResponseInfo {
        val responseJson = JSONObject(jsonStr)
        val state = responseJson.optInt("status")
        val message = responseJson.optString("message")
        val responseInfo = ResponseInfo(state, message)
        if (state == 1) {
            val listJson = responseJson.optJSONArray("data")
            if (null != listJson && listJson.length() > 0) {
                val listArray = ArrayList<OilStationBean>()
                for (x in 0 until listJson.length()) {
                    val stationBean =
                        GsonHelper.fromJson(listJson[x].toString(), OilStationBean::class.java)
                    if (stationBean != null) {
                        listArray.add(stationBean)
                    }
                }
                responseInfo.responseData = listArray
            }
        }
        return responseInfo
    }
}