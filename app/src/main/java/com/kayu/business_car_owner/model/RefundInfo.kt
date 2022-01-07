package com.kayu.business_car_owner.model

import com.google.gson.annotations.SerializedName

/**
 * Author by killer, Email xx@xx.com, Date on 2020/10/27.
 * PS: Not easy to write code, please indicate.
 */
class RefundInfo {
    /**
     * id : 53
     * amount : 0.01
     * refundWayResults : [{"way":1,"content":"原路退回(1-3个工作日内退回到原支付方)"}]
     * reasons : ["计划有变,没消费时间","选错门店","店里价格更优惠","无法联系到商家预约","商家不接待"]
     */
    @SerializedName("id")
    var id: Int = 0

    @SerializedName("amount")
    var amount: Double = 0.00

    @SerializedName("refundWayResults")
    var refundWayResults: MutableList<RefundWayResultsDTO>? = null

    @SerializedName("reasons")
    var reasons: MutableList<String>? = null

    class RefundWayResultsDTO {
        /**
         * way : 1
         * content : 原路退回(1-3个工作日内退回到原支付方)
         */
        @SerializedName("way")
        var way: Int = -1


        @SerializedName("content")
        var content: String = ""
    }
}