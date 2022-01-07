package com.kayu.business_car_owner.model

import com.google.gson.annotations.SerializedName

class ItemOilOrderBean {
    /**
     * orderId : ZF2231103562009251EXx01
     * paySn : CZBH582788473319401
     * phone : 176****9252
     * orderTime : 2020-09-25 14:37:08
     * payTime : 2020-09-25 14:37:26
     * refundTime : null
     * gasName : 众诚连锁卫星加油站
     * province : 吉林省
     * city : 长春市
     * county : 朝阳区
     * gunNo : 2
     * oilNo : 0#
     * amountPay : 0.10
     * amountGun : 0.10
     * amountDiscounts : 0.00
     * orderStatusName : 已支付
     * couponMoney : 0.00
     * couponId : -1
     * couponCode : null
     * litre : 0.02
     * payType : 微信支付
     * priceUnit : 5.89
     * priceOfficial : 6.88
     * priceGun : 5.99
     * orderSource : 车友团
     * qrCode4PetroChina : null
     * amountServiceCharge : 0.00
     */
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