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

/**
 * Author by killer, Email xx@xx.com, Date on 2020/10/28.
 * PS: Not easy to write code, please indicate.
 */
class SystemParam {
    /**
     * id : 55
     * title : 加油洗车功能
     * content : {"gas":1,"carwash":1}
     * url : null
     * state : 1
     * type : 10
     * pathMd5 : null
     * pathLength : 0
     * force : null
     * blank1 : null
     * blank2 : null
     * blank3 : null
     * blank4 : null
     * blank5 : null
     * blank6 : null
     * blank7 : null
     * blank8 : null
     * blank9 : null
     */
    @SerializedName("id")
    var id: Long = 0

    @SerializedName("title")
    var title: String = ""


    @SerializedName("content")
    var content: String = ""


    @SerializedName("url")
    var url: String = ""

    @SerializedName("state")
    var state: Int = 0

    @SerializedName("type")
    var type: Int = 0

    @SerializedName("pathMd5")
    var pathMd5: String = ""

    @SerializedName("pathLength")
    var pathLength: Int = 0

    @SerializedName("force")
    var force: String = ""

    @SerializedName("blank1")//传参带10的时候，返回的是判断是否上线参数
    var blank1: String = ""

    @SerializedName("blank2")
    var blank2: Any? = null

    @SerializedName("blank3")
    var blank3: Any? = null

    @SerializedName("blank4")
    var blank4: Any? = null

    @SerializedName("blank5")
    var blank5: Any? = null

    @SerializedName("blank6")
    var blank6: Any? = null

    @SerializedName("blank7")
    var blank7: Any? = null

    @SerializedName("blank8")
    var blank8: Any? = null

    @SerializedName("blank9")
    var blank9: String = ""
}