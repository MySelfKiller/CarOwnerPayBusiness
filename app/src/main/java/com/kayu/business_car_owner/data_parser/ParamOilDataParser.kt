package com.kayu.business_car_owner.data_parser

import android.os.Handler
import com.kayu.business_car_owner.http.parser.BaseParse
import kotlin.Throws
import com.kayu.business_car_owner.http.ResponseInfo
import com.kayu.utils.GsonHelper
import com.kayu.business_car_owner.model.ParamOilBean
import com.kayu.business_car_owner.model.DistancesParam
import com.kayu.business_car_owner.model.SortsParam
import com.kayu.business_car_owner.model.OilsTypeParam
import com.kayu.business_car_owner.model.OilsParam
import org.json.JSONObject
import java.lang.Exception

/**
 * Author by killer, Email xx@xx.com, Date on 2020/9/28.
 * PS: Not easy to write code, please indicate.
 */
class ParamOilDataParser : BaseParse<ResponseInfo?>() {
    @Throws(Exception::class)
    override fun parseJSON(handler: Handler, jsonStr: String, dataVersion: Double): ResponseInfo {
        val responseJson = JSONObject(jsonStr)
        val state = responseJson.optInt("status")
        val message = responseJson.optString("message")
        val responseInfo = ResponseInfo(state, message)
        if (state == 1) {
            val dataJson = responseJson.optJSONObject("data")
            val paramSelect = ParamOilBean()
            if (null != dataJson) {
                val distanceJson = dataJson.optJSONArray("distances")
                if (null != distanceJson && distanceJson.length() > 0) {
                    val distanceList = ArrayList<DistancesParam>()
                    for (x in 0 until distanceJson.length()) {
                        val stationBean = GsonHelper.fromJson(
                            distanceJson[x].toString(),
                            DistancesParam::class.java
                        )
                        if (stationBean != null) {
                            distanceList.add(stationBean)
                        }
                    }
                    paramSelect.distancesParamList = distanceList
                }
                val sortsJson = dataJson.optJSONArray("sorts")
                if (null != sortsJson && sortsJson.length() > 0) {
                    val sortsList = ArrayList<SortsParam>()
                    for (x in 0 until sortsJson.length()) {
                        val sortsParam =
                            GsonHelper.fromJson(sortsJson[x].toString(), SortsParam::class.java)
                        if (sortsParam != null) {
                            sortsList.add(sortsParam)
                        }
                    }
                    paramSelect.sortsParamList = sortsList
                }
                val oilsJson = dataJson.optJSONArray("oils")
                if (null != oilsJson && oilsJson.length() > 0) {
//                    val oilsTypeParamList: MutableList<OilsParam> = ArrayList<OilsParam>()
                    val oilsParamList = ArrayList<OilsParam>()
                    for (y in 0 until oilsJson.length()) {
                        val oilsParam = GsonHelper.fromJson(oilsJson[y].toString(), OilsParam::class.java)
                        if (oilsParam != null) {
                            oilsParamList.add(oilsParam)
                        }

                    }
//                    oilsTypeParam.oilsParamList = oilsParamList
                    paramSelect.oilsTypeParamList = oilsParamList
                }
                responseInfo.responseData = paramSelect
            }
        }
        return responseInfo
    }
}