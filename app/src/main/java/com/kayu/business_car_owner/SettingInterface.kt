package com.kayu.business_car_owner

import android.webkit.JavascriptInterface
import com.kayu.utils.*
import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception

/**
 * 配置淘油宝的js调用信息
 */
class SettingInterface constructor(private val data: String, private val gasId: String) {
    private var jsonData: String = ""

    //    String json = "{\"app_id\": \"2a10fa39e3546d256bf993f546b6d73b\", \"secret\":\"fdbab8561f7138914179b773a732e1aa\"}";
    @JavascriptInterface
    fun tybRegisterData(): String {
        return jsonData
    }

    @JavascriptInterface
    fun openMiniProgram(data: String?): String {
        var sssd: JSONObject? = null
        LogUtil.e("---支付返回数据---", data)
        try {
            sssd = JSONObject(jsonData)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return "{\"app_id\": " + sssd!!.optString("app_id") + ", \"type\":\"1\",\"url\":\"https://tyb-qa-api.nucarf.cn/pay/#/toPay?order_sn=2021082017440048559749&amount=3\"}"
    }

    init {
        if (!StringUtil.isEmpty(data)) {
            try {
                jsonData = DesCoderUtil.decryptDES(data, gasId)
            } catch (e: Exception) {
                e.printStackTrace() //https://tyb-qa-api.nucarf.cn/pay/#/toPay?order_sn=2021082017440048559749&amount=3
            }
        }
    }
}