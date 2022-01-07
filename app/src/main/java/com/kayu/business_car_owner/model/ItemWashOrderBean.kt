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

class ItemWashOrderBean {
    /**
     * shopCode : 310113003
     * realAmount : 0.01
     * address : 上海上海市宝山区上海上海市宝山区上海市宝山区蕰川路289号101室
     * latitude : 31.352045
     * shopName : 上海景邦汽车技术服务有限公司
     * telephone : 021-3456789
     * id : 53
     * busTime : 09:00-23:00
     * serviceName : 标准洗车-五座轿车
     * doorPhotoUrl : http://150.242.239.250:8131/group1/M00/02/03/wKhkEVs-EXKEY8D_AAAAALM0KfQ765.jpg
     * longitude : 121.438553
     */

    @SerializedName("shopCode")
    var shopCode: String = ""


    @SerializedName("realAmount")
    var realAmount: Double? = null


    @SerializedName("address")
    var address: String = ""


    @SerializedName("latitude")
    var latitude: String = ""


    @SerializedName("shopName")
    var shopName: String = ""


    @SerializedName("state")
    var state //状态筛选(暂定) 0:待支付 1:已支付待使用 2:已取消 3:已使用 4:退款中 5:已退款 6:支付失败、7:退款失败
            : Int = -1


    @SerializedName("telephone")
    var telephone: String = ""


    @SerializedName("id")
    var id: Long? = null


    @SerializedName("busTime")
    var busTime: String = ""


    @SerializedName("serviceName")
    var serviceName: String = ""


    @SerializedName("doorPhotoUrl")
    var doorPhotoUrl: String = ""


    @SerializedName("longitude")
    var longitude: String = ""


    @SerializedName("surplusDay")
    var surplusDay: Int = 0
}