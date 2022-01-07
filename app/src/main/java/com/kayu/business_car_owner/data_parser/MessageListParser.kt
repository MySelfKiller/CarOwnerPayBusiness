package com.kayu.business_car_owner.data_parser

import android.os.Handler
import com.kayu.business_car_owner.http.parser.BaseParse
import kotlin.Throws
import com.kayu.business_car_owner.http.ResponseInfo
import com.kayu.utils.GsonHelper
import com.kayu.business_car_owner.model.ItemMessageBean
import org.json.JSONObject
import java.lang.Exception

class MessageListParser : BaseParse<Any?>() {
    @Throws(Exception::class)
    override fun parseJSON(handler: Handler, jsonStr: String, dataVersion: Double): ResponseInfo {
        val jsonObject = JSONObject(jsonStr)
        val status = jsonObject.optInt("status")
        val msg = jsonObject.optString("message")
        val responseInfo = ResponseInfo(status, msg)
        //        PersionalNotice notice = GsonHelper.fromJson(jsonStr,PersionalNotice.class);
        val dataObj = jsonObject.optJSONObject("data")
        if (null != dataObj) {
            val noticeArr = dataObj.optJSONArray("list")
            if (null != noticeArr && noticeArr.length() > 0) {
                val dataList: MutableList<ItemMessageBean> = ArrayList()
                for (x in 0 until noticeArr.length()) {
                    val obj = noticeArr[x] as JSONObject
                    val noticeData =
                        GsonHelper.fromJson(obj.toString(), ItemMessageBean::class.java)
                    if (noticeData != null) {
                        dataList.add(noticeData)
                    }
                }
                responseInfo.responseData = dataList
            }
        }
        return responseInfo
    }
}