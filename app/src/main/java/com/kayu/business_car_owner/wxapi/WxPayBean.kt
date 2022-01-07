package com.kayu.business_car_owner.wxapi

import com.google.gson.annotations.SerializedName

class WxPayBean {
    /**
     * orderNo : CW20201023163639183SLZHSSXWVPXTN
     * orderId : 266
     * body : {"package":"sign=WXPay","appid":"wxa0cc46db3c2c4aa4","sign":"13EB52A0DD91422FAFF4BA317ACA48C9","partnerid":"1603374952","noncestr":"AF1B5ACBE0CE40E388FEF7B33BC9618F","perpayid":"wx23163639424749427f8340e28ff9f80000","timestamp":1603442199}
     */
    @SerializedName("orderNo")
    var orderNo: String? = null

    @SerializedName("orderId")
    var orderId: Long? = null

    @SerializedName("body")
    var body: BodyDTO? = null

    class BodyDTO {
        /**
         * package : sign=WXPay
         * appid : wxa0cc46db3c2c4aa4
         * sign : 13EB52A0DD91422FAFF4BA317ACA48C9
         * partnerid : 1603374952
         * noncestr : AF1B5ACBE0CE40E388FEF7B33BC9618F
         * perpayid : wx23163639424749427f8340e28ff9f80000
         * timestamp : 1603442199
         */
        @SerializedName("package")
        var packageX: String? = null

        @SerializedName("appid")
        var appid: String? = null

        @SerializedName("sign")
        var sign: String? = null

        @SerializedName("partnerid")
        var partnerid: String? = null

        @SerializedName("noncestr")
        var noncestr: String? = null

        @SerializedName("perpayid")
        var perpayid: String? = null

        @SerializedName("timestamp")
        var timestamp: Long? = null
    }
}