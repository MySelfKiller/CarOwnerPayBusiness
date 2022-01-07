package com.kayu.business_car_owner.model

import com.google.gson.annotations.SerializedName
import com.kayu.business_car_owner.model.RefundInfo.RefundWayResultsDTO
import com.kayu.business_car_owner.model.DistancesParam
import com.kayu.business_car_owner.model.SortsParam
import com.kayu.business_car_owner.model.OilsTypeParam
import com.kayu.business_car_owner.model.OilsParam
import com.kayu.business_car_owner.model.WashParam
import com.kayu.business_car_owner.model.SystemParamContent.AndroidDTO
import com.kayu.business_car_owner.model.WashStationDetailBean.ImgListDTO
import com.kayu.business_car_owner.model.WashStationDetailBean.ServicesDTO
import com.kayu.business_car_owner.model.WashStationDetailBean.ServicesDTO.ListDTO

class UserBean {
    /**
     * username : 黄敏
     * wxName : 痞子哥哥
     * wxNo : null
     * phone : 151******46
     * idNo : null
     * headPic : https://thirdwx.qlogo.cn/mmopen/vi_32/DYAIOgq83er0yGmJvZZJ0vYIkA7arNh8fDpCxyGWadnEGBxydPEAwibiaLoH5FhOLZu1Rn4ARBhcGV6UF3mXP48g/132
     * balance : 0
     * rewardAmt : 0
     * lastLoginTime : 2020-11-23 16:23:36
     * inviteNo : 1328608206898941954
     * expAmt : 0
     * activateTime : 2020-11-17 15:57:42
     * type : 2
     * "busTitle":"已为您节省0.0"
     */
    @SerializedName("username")
    var username //姓名(暂定)
            : String = ""
    @SerializedName("idenName")
    var idenName //用户身份(暂定)
            : String = ""

    @SerializedName("wxName")
    var wxName //微信昵称
            : String = ""

    @SerializedName("wxNo")
    var wxNo //微信号(暂定)
            : String = ""


    @SerializedName("phone")
    var phone //手机号
            : String = ""

    @SerializedName("idNo")
    var idNo //证件号(暂定)
            : String = ""


    @SerializedName("headPic")
    var headPic //微信头像
            : String = ""


    @SerializedName("balance")
    var balance //账户余额/元
            : Double? = null


    @SerializedName("rewardAmt")
    var rewardAmt //累计收益/元
            : Double = 0.0

    @SerializedName("lastLoginTime")
    var lastLoginTime //最后一次登陆时间
            : String = ""


    @SerializedName("inviteNo")
    var inviteNo = "" //激活卡号码

    @SerializedName("expAmt")
    var expAmt //累计节省金额
            : Double = 0.0

    @SerializedName("activateTime")
    var activateTime //激活时间
            : String = ""


    @SerializedName("type")
    var type //账号类型 1:普通用户,2:经销商,3:运营商,-2 游客
            : Int = 0


    @SerializedName("busTitle")
    var busTitle //已为你节省xxx元
            : String = ""
}