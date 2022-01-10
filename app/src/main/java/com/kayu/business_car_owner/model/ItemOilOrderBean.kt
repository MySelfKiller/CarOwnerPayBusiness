package com.kayu.business_car_owner.model

import com.google.gson.annotations.SerializedName

class ItemOilOrderBean {

    @SerializedName("id") //主键ID
    var id: Long? = null


    @SerializedName("gasName")
    var gasName //油站名称
            : String? = null


    @SerializedName("orderNo")
    var orderNo //订单号
            : String? = null


    @SerializedName("payType")
    var payType //支付方式
            : String? = null


    @SerializedName("oilNo")
    var oilNo //油号
            : String? = null


    @SerializedName("gunNo")
    var gunNo //枪号
            : String? = null


    @SerializedName("totalAmt")
    var totalAmt //订单总金额/元
            : Double? = null

    @SerializedName("disAmt")
    var disAmt //优惠金额/元
            : Double? = null


    @SerializedName("payAmt")
    var payAmt //支付金额/元
            : Double? = null

    @SerializedName("couponAmt")
    var couponAmt //优惠券金额/元
            : Double? = null


    @SerializedName("createTime")
    var createTime //创建时间
            : String? = null


    @SerializedName("state")
    var state //订单状态 0:未支付、1:已支付、2:已取消 3:已退款、4:待退款、5:退款失败
            : Int? = null

    @SerializedName("liter")
    var liter //加油量 单位（升）
            : Int? = null


    @SerializedName("qrCode")
    var qrCode //订单二维码图片
            : String? = null
}