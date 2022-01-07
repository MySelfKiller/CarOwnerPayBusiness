package com.kayu.business_car_owner.data_parser

import android.os.Handler
import com.kayu.business_car_owner.http.parser.BaseParse
import kotlin.Throws
import com.kayu.business_car_owner.http.ResponseInfo
import com.kayu.utils.GsonHelper
import com.kayu.business_car_owner.model.CategoryBean
import org.json.JSONObject
import java.lang.Exception

class CategoryDataParse : BaseParse<Any?>() {
    @Throws(Exception::class)
    override fun parseJSON(handler: Handler, jsonStr: String, dataVersion: Double): ResponseInfo {
        val obj = JSONObject(jsonStr)
        val status = obj.optInt("status")
        val msg = obj.optString("message")
        val responseInfo = ResponseInfo(status, msg)
        if (status == 1) {
            val dataArrP = obj.optJSONArray("data")
            if (null != dataArrP && dataArrP.length() > 0) {
                val dataList: MutableList<MutableList<CategoryBean>> = ArrayList()
                for (z in 0 until dataArrP.length()) {
                    val dataArr = dataArrP.getJSONArray(z)
                    if (null != dataArr && dataArr.length() > 0) {
                        val itemDataList: MutableList<CategoryBean> = ArrayList()
                        for (x in 0 until dataArr.length()) {
                            val itemData =
                                GsonHelper.fromJson(dataArr[x].toString(), CategoryBean::class.java)
                            if (itemData != null) {
                                itemDataList.add(itemData)
                            }
                        }
                        dataList.add(itemDataList)
                    }
                }
                responseInfo.responseData = dataList
            }
        }
        return responseInfo
    }
}