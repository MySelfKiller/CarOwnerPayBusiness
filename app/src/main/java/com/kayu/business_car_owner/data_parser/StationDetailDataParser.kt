package com.kayu.business_car_owner.data_parser

import android.os.Handler
import com.kayu.business_car_owner.http.parser.BaseParse
import kotlin.Throws
import com.kayu.business_car_owner.http.ResponseInfo
import com.kayu.utils.GsonHelper
import com.kayu.business_car_owner.model.OilsTypeParam
import com.kayu.business_car_owner.model.OilsParam
import com.kayu.business_car_owner.model.OilStationBean
import org.json.JSONObject
import java.lang.Exception

/**
 * Author by killer, Email xx@xx.com, Date on 2020/9/28.
 * PS: Not easy to write code, please indicate.
 */
class StationDetailDataParser : BaseParse<Any?>() {
    @Throws(Exception::class)
    override fun parseJSON(handler: Handler, jsonStr: String, dataVersion: Double): ResponseInfo {
        val responseJson = JSONObject(jsonStr)
        val state = responseJson.optInt("status")
        val message = responseJson.optString("message")
        val responseInfo = ResponseInfo(state, message)
        if (state == 1) {
            val dataJson = responseJson.optJSONObject("data")
            if (null != dataJson) {
                val stationBean =
                    GsonHelper.fromJson(dataJson.toString(), OilStationBean::class.java)
                val oilTypeJsonArr = dataJson.optJSONArray("oilTypes")
                if (null != stationBean && null != oilTypeJsonArr && oilTypeJsonArr.length() > 0) {
                    val listArray = ArrayList<OilsTypeParam>()
                    for (x in 0 until oilTypeJsonArr.length()) {
                        val oilsTypeParam = GsonHelper.fromJson(
                            oilTypeJsonArr[x].toString(),
                            OilsTypeParam::class.java
                        )
                        if (null != oilsTypeParam) {
                            val ibj = oilTypeJsonArr[x] as JSONObject
                            val oilJsonArr = ibj.optJSONArray("list")
                            if (null != oilJsonArr && oilJsonArr.length() > 0) {
                                val oilsParamList = ArrayList<OilsParam>()
                                for (y in 0 until oilJsonArr.length()) {
                                    val oilsParam = GsonHelper.fromJson(
                                        oilJsonArr[y].toString(),
                                        OilsParam::class.java
                                    )
                                    if (oilsParam != null) {
                                        oilsParamList.add(oilsParam)
                                    }
                                }
                                oilsTypeParam.oilsParamList = oilsParamList
                            }
                        }
                        if (oilsTypeParam != null) {
                            listArray.add(oilsTypeParam)
                        }
                    }
                    stationBean.oilsTypeList = listArray
                }
                responseInfo.responseData = stationBean
            }
        }
        return responseInfo
    }
}