package com.kayu.business_car_owner.model

import com.google.gson.annotations.SerializedName

class WashStationBean {
    /**
     * shopCode : 131203001
     * shopName : 名都洗车店
     * address : 河北省唐山市曹妃甸区新城大街兴海名都地下停车场D区
     * startTime : 2019-09-29
     * endTime : 2019-09-29
     * openTimeStart : 08:00
     * openTimeEnd : 18:00
     * isStatus : 4
     * doorPhotoUrl : null
     * longitude : 118.45866
     * latitude : 39.283637
     * score : 5.0
     * totalNum : 0
     * distance : 882322
     * isOpen : 1
     * chain : 0
     * serviceList : [{"serviceCode":"3000","serviceName":"标准洗车-SUV/MPV","serviceType":"1","price":"33.00","finalPrice":"26.00"},{"serviceCode":"81","serviceName":"标准洗车-五座轿车","serviceType":"1","price":"29.00","finalPrice":"22.00"}]
     */

    @SerializedName("shopCode")
    var shopCode: String = ""


    @SerializedName("shopName")
    var shopName: String = ""


    @SerializedName("address")
    var address: String = ""

    @SerializedName("startTime")
    var startTime: String = ""

    @SerializedName("endTime")
    var endTime: String = ""


    @SerializedName("openTimeStart")
    var openTimeStart: String = ""


    @SerializedName("openTimeEnd")
    var openTimeEnd: String = ""

    @SerializedName("isStatus")
    var isStatus: String = ""


    @SerializedName("doorPhotoUrl")
    var doorPhotoUrl: String = ""


    @SerializedName("longitude")
    var longitude: String = ""


    @SerializedName("latitude")
    var latitude: String = ""

    @SerializedName("score")
    var score: String = ""

    @SerializedName("totalNum")
    var totalNum: Int? = null


    @SerializedName("distance")
    var distance: String = ""


    @SerializedName("isOpen")
    var isOpen: String = ""

    @SerializedName("chain")
    var chain: String = ""


    @SerializedName("serviceList")
    var serviceList: List<ServiceListDTO>? = null

    class ServiceListDTO {
        /**
         * serviceCode : 3000
         * serviceName : 标准洗车-SUV/MPV
         * serviceType : 1
         * price : 33.00
         * finalPrice : 26.00
         */

        @SerializedName("serviceCode")
        var serviceCode: String = ""

        @SerializedName("serviceName")
        var serviceName: String = ""

        @SerializedName("serviceType")
        var serviceType: String = ""


        @SerializedName("price")
        var price: String = ""


        @SerializedName("finalPrice")
        var finalPrice: String = ""
    }
}