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

class WashOrderDetailBean {
    /**
     * useExplain : https://www.kakayuy.com/group1/M00/00/04/rBoO71-SoHOAWUIkAAMyjhPrIOg605.png
     * id : 55
     * orderNo : an30H5777tdS8h8aZ0268261z0u9C066
     * shopCode : 131203001
     * shopName : 名都洗车店
     * address : 河北省唐山市曹妃甸区新城大街兴海名都地下停车场D区
     * longitude : 118.45866
     * latitude : 39.283637
     * telephone : 13403259609
     * doorPhotoUrl :
     * doorImgList : null
     * serviceCode : 81
     * serviceName : 标准洗车-五座轿车
     * amount : 29
     * finalPrice : 0.01
     * realAmount : 0.01
     * state : 1
     * cmpTime : 2020-10-15 14:32:17
     * createTime : 2020-10-12 10:38:50
     * modifyTime : 2020-10-15 14:32:22
     * busTime : 08:00-18:00
     * serOrderNo : 1023SDXCNFR2010119856
     * effTime : 2020-11-10
     * qrCodeBase64 : data:image/PNG;base64,iVBORw0KGgoAAAANSUhEUgAAAJYAAACWCAIAAACzY+a1AAACIUlEQVR42u3aQY7EMAgEwPz/07tviEwDzlRfI2Ucag4I/PzJ5XmUAKEgFIQIBaEgFIQIBaEglDDh05XC331Xha5TJT4BIUKECBEiRPj+Cyu7qTdvbvM+edpWDYQIESJEiBBhHWFbsaa8l7wZIUKECBEiRHghYduQrLBvRIgQIUKECBEizKRwUYcQIUKECBEi/BxhbkOWu++UW1ta+SJEiBAhQoQ3EOZyAjz1tK0aCBEiRIgQIcIDwp2ZuqS0rg4IESJEiBAhwmyxTnrdtkaxbd52x8oXIUKECBEi/CBhbsK0BHiqix4bsCFEiBAhQoQ/RzjVoS2pbI6h5PMRIkSIECFChOE+Koe05BjjQYgQIUKECBH2joVuXBDm7kotvZCPECFChAgRfpBwp8oVc74cP0KECBEiRIhwZUeau2S9JN8fsCFEiBAhQoRLCU/aucLOMDdRKyx0/20ohAgRIkSIEGF4hJabqLXtKXMd6ZaVL0KECBEiRPh9wqksmbdtWxAiRIgQIUKECMP7wraGra3uhVPAkokaQoQIESJEiPCYsK3nLDxG2/qwbWSIECFChAgRIqwj7D90erL1DGWsI0WIECFChAgRXnCFaap9XbovRIgQIUKECBE2EU7tGu9aeSJEiBAhQoQI79kXTv11pr4IIUKECBEiRBgjnGrndg7ncptIhAgRIkSIEGEdoWwOQoSCUBAiFISCUBAiFISCUBD+cv4B0+tgvbFL9NsAAAAASUVORK5CYII=
     * qrString : 1023SDXCNFR2010119856
     * explain : 使用时请告知门店本券为盛大汽车服务券
     */
    @SerializedName("useExplain")
    var useExplain: String? = null

    @SerializedName("id")
    var id: Long? = null

    @SerializedName("orderNo")
    var orderNo: String? = null

    @SerializedName("shopCode")
    var shopCode: String? = null

    @SerializedName("shopName")
    var shopName: String? = null

    @SerializedName("address")
    var address: String? = null

    @SerializedName("longitude")
    var longitude: String? = null

    @SerializedName("latitude")
    var latitude: String? = null

    @SerializedName("telephone")
    var telephone: String? = null

    @SerializedName("doorPhotoUrl")
    var doorPhotoUrl: String? = null

    @SerializedName("doorImgList")
    var doorImgList: List<String>? = null

    @SerializedName("serviceCode")
    var serviceCode: String? = null

    @SerializedName("serviceName")
    var serviceName: String? = null

    @SerializedName("amount")
    var amount: Double? = null

    @SerializedName("finalPrice")
    var finalPrice: Double? = null

    @SerializedName("realAmount")
    var realAmount: Double? = null

    @SerializedName("state")
    var state: Int? = null

    @SerializedName("cmpTime")
    var cmpTime: String? = null

    @SerializedName("createTime")
    var createTime: String? = null

    @SerializedName("modifyTime")
    var modifyTime: String? = null

    @SerializedName("busTime")
    var busTime: String? = null

    @SerializedName("serOrderNo")
    var serOrderNo: String? = null

    @SerializedName("effTime")
    var effTime: String? = null

    @SerializedName("qrCodeBase64")
    var qrCodeBase64: String? = null

    @SerializedName("qrString")
    var qrString: String? = null

    @SerializedName("explain")
    var explain: String? = null
}