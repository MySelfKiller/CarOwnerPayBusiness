package com.kayu.business_car_owner.model

data class ItemOilOrderBean(
    val couponAmt: Double,
    val createTime: String,
    val disAmt: Double,
    val gasName: String,
    val gunNo: String,
    val liter: Double,
    val oilNo: String,
    val orderNo: String,
    val payAmt: Double,
    val payType: String,
    val qrCode: String,
    val state: Int,
    val stateName: String,
    val totalAmt: Double
)