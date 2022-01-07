package com.kayu.business_car_owner.data_parser

import android.os.Handler
import com.kayu.business_car_owner.http.parser.BaseParse
import kotlin.Throws
import com.kayu.business_car_owner.http.ResponseInfo
import com.kayu.utils.GsonHelper
import com.kayu.business_car_owner.model.ItemOilOrderBean
import org.json.JSONObject
import java.lang.Exception

/**
 * Author by killer, Email xx@xx.com, Date on 2020/9/28.
 * PS: Not easy to write code, please indicate.
 */
class OilOrderListDataParser : BaseParse<Any?>() {
    @Throws(Exception::class)
    override fun parseJSON(handler: Handler, jsonStr: String, dataVersion: Double): ResponseInfo {
        val responseJson = JSONObject(jsonStr)
        val state = responseJson.optInt("status")
        val message = responseJson.optString("message")
        val dataJson = responseJson.optJSONObject("data")
        val responseInfo = ResponseInfo(state, message)
        if (null != dataJson) {
            if (state == 1) {
                val jsonList = dataJson.optJSONArray("list")
                if (null != jsonList && jsonList.length() > 0) {
                    val listArray = ArrayList<ItemOilOrderBean>()
                    for (x in 0 until jsonList.length()) {
                        val stationBean = GsonHelper.fromJson(
                            jsonList[x].toString(),
                            ItemOilOrderBean::class.java
                        )
                        if (stationBean != null) {
                            listArray.add(stationBean)
                        }
                    }
                    responseInfo.responseData = listArray
                }
            }
        }
        return responseInfo
    }
}