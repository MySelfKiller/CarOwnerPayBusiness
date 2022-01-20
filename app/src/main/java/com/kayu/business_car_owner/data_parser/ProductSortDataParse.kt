package com.kayu.business_car_owner.data_parser

import android.os.Handler
import com.kayu.business_car_owner.http.parser.BaseParse
import kotlin.Throws
import com.kayu.business_car_owner.http.ResponseInfo
import com.kayu.utils.GsonHelper
import com.kayu.business_car_owner.model.ProductSortBean
import org.json.JSONObject
import java.lang.Exception

class ProductSortDataParse : BaseParse<Any?>() {
    @Throws(Exception::class)
    override fun parseJSON(handler: Handler, jsonStr: String, dataVersion: Double): ResponseInfo {
        val obj = JSONObject(jsonStr)
        val status = obj.optInt("status")
        val msg = obj.optString("message")
        val responseInfo = ResponseInfo(status, msg)
        if (status == 1) {
            val dataArr = obj.optJSONArray("data")
            if (null != dataArr && dataArr.length() > 0) {
                val itemDataList: MutableList<ProductSortBean?> = ArrayList()
                for (x in 0 until dataArr.length()) {
                    val itemData =
                        GsonHelper.fromJson(dataArr[x].toString(), ProductSortBean::class.java)
                    itemDataList.add(itemData)
                }
                responseInfo.responseData = itemDataList
            }
        }
        return responseInfo
    }
}