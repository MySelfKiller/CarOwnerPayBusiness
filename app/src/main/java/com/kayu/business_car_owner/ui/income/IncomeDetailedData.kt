package com.kayu.business_car_owner.ui.income

import com.google.gson.annotations.SerializedName

class IncomeDetailedData {
    /**
     * id : 1
     * orderNo : ZF2231103562009251CdQ01
     * amount : 0.0
     * explain : 加油优惠
     * type : 0
     * createTime : 2020-09-27 10:14:55
     */
    @SerializedName("id")
    var id: Int? = null

    @SerializedName("orderNo")
    var orderNo: String? = null

    @SerializedName("amount")
    var amount: Double? = null

    @SerializedName("explain")
    var explain: String? = null

    @SerializedName("type")
    var type: Int? = null

    @SerializedName("createTime")
    var createTime: String? = null
}