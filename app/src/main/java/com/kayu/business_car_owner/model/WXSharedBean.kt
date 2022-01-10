package com.kayu.business_car_owner.model

import com.google.gson.annotations.SerializedName

class WXSharedBean {

    @SerializedName("qrCode")
    var qrCode: String? = null


    @SerializedName("url")
    var url: String? = null


    @SerializedName("title")
    var title: String? = null


    @SerializedName("desc")
    var desc: String? = null


    @SerializedName("type")
    var type: Int? = null


    @SerializedName("object")
    var `object`: Int? = null
}