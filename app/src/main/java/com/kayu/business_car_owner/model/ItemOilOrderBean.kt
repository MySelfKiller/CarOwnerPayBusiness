package com.kayu.business_car_owner.model

import com.google.gson.annotations.SerializedName

class ItemOilOrderBean {

    @SerializedName("id") //主键ID
    val id: Long = 0L


    @SerializedName("gasName")
    val gasName //油站名称
            : String = ""


    @SerializedName("orderNo")
    val orderNo //订单号
            : String = ""


    @SerializedName("payType")
    val payType //支付方式
            : String = ""


    @SerializedName("oilNo")
    val oilNo //油号
            : String = ""


    @SerializedName("gunNo")
    val gunNo //枪号
            : String = ""


    @SerializedName("totalAmt")
    val totalAmt //订单总金额/元
            : Double = 0.00

    @SerializedName("disAmt")
    val disAmt //优惠金额/元
            : Double = 0.00


    @SerializedName("payAmt")
    val payAmt //支付金额/元
            : Double = 0.00

    @SerializedName("couponAmt")
    val couponAmt //优惠券金额/元
            : Double = 0.00


    @SerializedName("createTime")
    val createTime //创建时间
            : String = ""


    @SerializedName("state")
    val state //订单状态 0:未支付、1:已支付、2:已取消 3:已退款、4:待退款、5:退款失败
            : Int? = null

    @SerializedName("liter")
    val liter //加油量 单位（升）
            : Int = 0


    @SerializedName("qrCode")
    val qrCode //订单二维码图片
            : String = ""
}