package com.kayu.business_car_owner.model

import com.google.gson.annotations.SerializedName

/**
 * Author by killer, Email xx@xx.com, Date on 2020/9/28.
 * PS: Not easy to write code, please indicate.
 */
class DistancesParam {
    @SerializedName("val")
    var value //条件值
            = 0
    var name //条件名称
            : String = ""

    var isDefault //是否为默认条件 0:否 1:是
            = 0
}