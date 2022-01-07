package com.kayu.business_car_owner.update

import android.os.Handler
import com.kayu.business_car_owner.http.ResponseInfo
import kotlin.Throws
import com.kayu.business_car_owner.http.parser.BaseParse
import com.kayu.utils.GsonHelper
import org.json.JSONObject
import java.lang.Exception

/**
 * Created by hm on 2018/10/31.
 */
class UpdateInfoParse : BaseParse<Any?>() {
    @Throws(Exception::class)
    override fun parseJSON(handler: Handler, jsonStr: String, dataVersion: Double): ResponseInfo {
//        LogUtil.e("hm","UpdateInfoParse: jsonStr="+jsonStr);
        val obj = JSONObject(jsonStr)
        val status = obj.getInt("status")
        val msg = obj.getString("message")
        val responseInfo = ResponseInfo(status, msg)
        if (status == 0) {
            responseInfo.responseData = null
        }
        if (status == 1) {
            val updateInfo =
                GsonHelper.fromJson(obj.getString("data").toString(), UpdateInfo::class.java)
            if (updateInfo != null) {
                responseInfo.responseData = updateInfo
            }
        }
        return responseInfo
    }
}