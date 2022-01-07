package com.kayu.business_car_owner.data_parser

import android.os.Handler
import com.kayu.business_car_owner.http.parser.BaseParse
import kotlin.Throws
import com.kayu.business_car_owner.http.ResponseInfo
import com.kayu.utils.GsonHelper
import com.kayu.business_car_owner.model.ParamWashBean
import com.kayu.business_car_owner.model.WashParam
import org.json.JSONObject
import java.lang.Exception

/**
 * Author by killer, Email xx@xx.com, Date on 2020/9/28.
 * PS: Not easy to write code, please indicate.
 */
class ParamWashDataParser : BaseParse<ResponseInfo?>() {
    @Throws(Exception::class)
    override fun parseJSON(handler: Handler, jsonStr: String, dataVersion: Double): ResponseInfo {
        val responseJson = JSONObject(jsonStr)
        val state = responseJson.optInt("status")
        val message = responseJson.optString("message")
        val responseInfo = ResponseInfo(state, message)
        if (state == 1) {
            val dataJson = responseJson.optJSONObject("data")
            val paramSelect = ParamWashBean()
            if (null != dataJson) {
                val distanceJson = dataJson.optJSONArray("des")
                if (null != distanceJson && distanceJson.length() > 0) {
                    val distanceList = ArrayList<WashParam>()
                    for (x in 0 until distanceJson.length()) {
                        val stationBean =
                            GsonHelper.fromJson(distanceJson[x].toString(), WashParam::class.java)
                        if (stationBean != null) {
                            distanceList.add(stationBean)
                        }
                    }
                    paramSelect.desList = distanceList
                }
                val sortsJson = dataJson.optJSONArray("types")
                if (null != sortsJson && sortsJson.length() > 0) {
                    val sortsList = ArrayList<WashParam>()
                    for (x in 0 until sortsJson.length()) {
                        val WashParam =
                            GsonHelper.fromJson(sortsJson[x].toString(), WashParam::class.java)
                        if (WashParam != null) {
                            sortsList.add(WashParam)
                        }
                    }
                    paramSelect.typesList = sortsList
                }
                //                JSONArray oilsJson = dataJson.optJSONArray("oils");
//                if (null != oilsJson && oilsJson.length() > 0) {
//                    ArrayList<OilsTypeParam> oilsTypeParamList = new ArrayList<OilsTypeParam>();
//                    for (int x = 0; x < oilsJson.length(); x++) {
//                        OilsTypeParam oilsTypeParam = GsonHelper.fromJson(oilsJson.get(x).toString(), OilsTypeParam.class);
//                        if (null != oilsTypeParam) {
//                            JSONObject ibj = (JSONObject) oilsJson.get(x);
//                            JSONArray oilJsonArr = ibj.optJSONArray("list");
//                            if (null != oilJsonArr && oilJsonArr.length() > 0) {
//                                ArrayList<OilsParam> oilsParamList = new ArrayList<>();
//                                for (int y = 0; y < oilJsonArr.length(); y++) {
//                                    OilsParam oilsParam = GsonHelper.fromJson(oilJsonArr.get(y).toString(), OilsParam.class);
//                                    oilsParamList.add(oilsParam);
//                                }
//                                oilsTypeParam.oilsParamList = oilsParamList;
//                            }
//                        }
//                        oilsTypeParamList.add(oilsTypeParam);
//                    }
//                    paramSelect.oilsTypeParamList = oilsTypeParamList;
//                }
                responseInfo.responseData = paramSelect
            }
        }
        return responseInfo
    }
}