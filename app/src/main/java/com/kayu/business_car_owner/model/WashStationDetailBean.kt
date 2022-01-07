package com.kayu.business_car_owner.model

import android.os.Parcel
import android.os.Parcelable
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

class WashStationDetailBean {
    /**
     * shopCode : 130204001
     * shopName : 金洁士汽车清洗美容服务
     * address : 唐山市路南区国防道凯旋铭居底商2号
     * startTime : 2019-09-29
     * endTime : 2019-09-29
     * openTimeStart : 08:00
     * openTimeEnd : 18:00
     * isStatus : 4
     * doorPhotoUrl : http://150.242.239.250:8131/group1/M00/01/F8/wKhkEVs-ESGESv9tAAAAAJTx4PY377.jpg
     * longitude : 118.167631
     * latitude : 39.625703
     * rating : A
     * score : 5.0
     * totalNum : 0
     * telephone : 15614582498
     * isOpen : 1
     * proNumber : 130000
     * proName : 河北
     * cityNumber : 130200
     * cityName : 唐山市
     * imgList : [{"shopCode":"130204001","shopImgUrl":"http://150.242.239.250:8131/group1/M00/00/63/wKhkEVqUtw6AD17RAAIFD18b7GA651.jpg"},{"shopCode":"130204001","shopImgUrl":"http://150.242.239.250:8131/group1/M00/00/63/wKhkEVqUtw6AeajjAAHKUX8zAk4633.jpg"},{"shopCode":"130204001","shopImgUrl":"http://150.242.239.250:8131/group1/M00/00/63/wKhkEVqUtw6AVyTMAAHTt7hvWkM376.jpg"}]
     * serviceList : [{"serviceCode":"81","serviceName":"标准洗车-五座轿车","serviceType":"1","price":"30.00","finalPrice":"23.00","carModel":1},{"serviceCode":"3000","serviceName":"标准洗车-SUV/MPV","serviceType":"1","price":"35.00","finalPrice":"28.00","carModel":2}]
     * services : [{"washType":1,"name":"普通洗车","list":[{"serviceCode":"81","serviceName":"标准洗车-五座轿车","serviceType":"1","price":"30.00","finalPrice":"23.00","carModel":1},{"serviceCode":"3000","serviceName":"标准洗车-SUV/MPV","serviceType":"1","price":"35.00","finalPrice":"28.00","carModel":2}]}]
     */
    @SerializedName("shopCode")
    var shopCode: String? = null

    @SerializedName("shopName")
    var shopName: String? = null

    @SerializedName("address")
    var address: String? = null

    @SerializedName("startTime")
    var startTime: String? = null

    @SerializedName("endTime")
    var endTime: String? = null

    @SerializedName("openTimeStart")
    var openTimeStart: String? = null

    @SerializedName("openTimeEnd")
    var openTimeEnd: String? = null

    @SerializedName("isStatus")
    var isStatus: String? = null

    @SerializedName("doorPhotoUrl")
    var doorPhotoUrl: String? = null

    @SerializedName("longitude")
    var longitude: String? = null

    @SerializedName("latitude")
    var latitude: String? = null

    @SerializedName("rating")
    var rating: String? = null

    @SerializedName("score")
    var score: String? = null

    @SerializedName("totalNum")
    var totalNum: String? = null

    @SerializedName("telephone")
    var telephone: String? = null

    @SerializedName("isOpen")
    var isOpen: String? = null

    @SerializedName("proNumber")
    var proNumber: String? = null

    @SerializedName("proName")
    var proName: String? = null

    @SerializedName("cityNumber")
    var cityNumber: String? = null

    @SerializedName("cityName")
    var cityName: String? = null

    @SerializedName("imgList")
    var imgList: List<ImgListDTO>? = null

    @SerializedName("serviceList")
    var serviceList: List<ServiceListDTO>? = null

    @SerializedName("services")
    var services: List<ServicesDTO>? = null

    class ImgListDTO {
        /**
         * shopCode : 130204001
         * shopImgUrl : http://150.242.239.250:8131/group1/M00/00/63/wKhkEVqUtw6AD17RAAIFD18b7GA651.jpg
         */
        @SerializedName("shopCode")
        var shopCode: String? = null

        @SerializedName("shopImgUrl")
        var shopImgUrl: String? = null
    }

    class ServiceListDTO {
        /**
         * serviceCode : 81
         * serviceName : 标准洗车-五座轿车
         * serviceType : 1
         * price : 30.00
         * finalPrice : 23.00
         * carModel : 1
         */
        @SerializedName("serviceCode")
        var serviceCode: String? = null

        @SerializedName("serviceName")
        var serviceName: String? = null

        @SerializedName("serviceType")
        var serviceType: String? = null

        @SerializedName("price")
        var price: String? = null

        @SerializedName("finalPrice")
        var finalPrice: String? = null

        @SerializedName("carModel")
        var carModel: Int? = null
    }

    class ServicesDTO {
        /**
         * washType : 1
         * name : 普通洗车
         * list : [{"serviceCode":"81","serviceName":"标准洗车-五座轿车","serviceType":"1","price":"30.00","finalPrice":"23.00","carModel":1},{"serviceCode":"3000","serviceName":"标准洗车-SUV/MPV","serviceType":"1","price":"35.00","finalPrice":"28.00","carModel":2}]
         */
        @SerializedName("washType")
        var washType //清洗类型 1:标准洗车 2:精致洗车
                : Int? = null

        @SerializedName("name")
        var name: String? = null

        @SerializedName("list")
        var list: List<ListDTO>? = null

        class ListDTO protected constructor(input: Parcel) : Parcelable {
            /**
             * serviceCode : 81
             * serviceName : 标准洗车-五座轿车
             * serviceType : 1
             * price : 30.00
             * finalPrice : 23.00
             * carModel : 1
             */
            @SerializedName("serviceCode") //服务编码
            var serviceCode: String? = input.readString()

            @SerializedName("serviceName") //服务名称
            var serviceName: String? = input.readString()

            @SerializedName("serviceType")
            var serviceType: String? = input.readString()

            @SerializedName("price")
            var price //门店市场价
                    : String? = input.readString()

            @SerializedName("finalPrice")
            var finalPrice //最终价
                    : String? = input.readString()

            @SerializedName("carModel")
            var carModel //车型 1：小轿车 2:大车 3:全部车型
                    : Int? = null

            override fun describeContents(): Int {
                return 0
            }

            override fun writeToParcel(dest: Parcel, flags: Int) {
                dest.writeString(serviceCode)
                dest.writeString(serviceName)
                dest.writeString(serviceType)
                dest.writeString(price)
                dest.writeString(finalPrice)
                if (carModel == null) {
                    dest.writeByte(0.toByte())
                } else {
                    dest.writeByte(1.toByte())
                    dest.writeInt(carModel!!)
                }
            }

            init {
                carModel = input.readValue(Int::class.java.classLoader) as? Int
            }

            companion object CREATOR : Parcelable.Creator<ListDTO> {
                override fun createFromParcel(parcel: Parcel): ListDTO {
                    return ListDTO(parcel)
                }

                override fun newArray(size: Int): Array<ListDTO?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }
}