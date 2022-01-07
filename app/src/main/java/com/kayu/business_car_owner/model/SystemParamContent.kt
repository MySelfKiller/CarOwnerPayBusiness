package com.kayu.business_car_owner.model

import com.google.gson.annotations.SerializedName

/**
 * Author by killer, Email xx@xx.com, Date on 2020/10/28.
 * PS: Not easy to write code, please indicate.
 */
class SystemParamContent {
    @SerializedName("gas")
    var gas: Int? = null

    @SerializedName("carwash")
    var carwash: Int? = null


    @SerializedName("android")
    var android: AndroidDTO? = null

    class AndroidDTO {
        @SerializedName("csj_appId")
        var csjAppid: String = ""

        @SerializedName("csj_placementId")
        var csjPlacementid: String = ""


        @SerializedName("ylh_appId")
        var ylhAppid: String = ""

        @SerializedName("ylh_splashID")
        var ylhSplashid: String = ""

        @SerializedName("showAd")
        var showAd: String = ""
    }
    /**
     * {"gas":1,"carwash":1,
     * "ios":{"csj_appId":"5144458","csj_placementId":"887439766","ylh_appId":"1105344611","ylh_splashID":"9040714184494018","showAd":"csj"},
     * "android":{"csj_appId":"5144457","csj_placementId":"887446448","ylh_appId":"1105344611","ylh_splashID":"9040714184494018","showAd":"csj"}}
     */
}