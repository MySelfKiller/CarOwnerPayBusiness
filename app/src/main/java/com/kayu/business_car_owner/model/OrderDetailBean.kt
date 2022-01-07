package com.kayu.business_car_owner.model

import com.google.gson.annotations.SerializedName

class OrderDetailBean {
    /**
     * orderNo : UAC20201207162148128GXAOYQROJWCO
     * username : 金章冀
     * phone : 13717699831
     * cardNo : 000000001
     * cardCode : 218T84
     * waybillNo : null
     */
    @SerializedName("orderNo")
    var orderNo: String? = null

    @SerializedName("username")
    var username: String? = null

    @SerializedName("phone")
    var phone: String? = null

    @SerializedName("cardNo")
    var cardNo: String? = null

    @SerializedName("cardCode")
    var cardCode: String? = null

    @SerializedName("waybillNo")
    var waybillNo: String? = null
}