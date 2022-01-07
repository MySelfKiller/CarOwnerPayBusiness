package com.kayu.business_car_owner.model

import com.google.gson.annotations.SerializedName

class WXSharedBean {
    /**
     * qrCode : https://www.ky808.cn/images/carfriend/a6e9c849a9523936ecbe67aa4e14ad8_55T1V1.jpg
     * url : https://www.ky808.cn/carfriend/static/cyt/index.html#/activate?c=VYHRPD&p=lK4Ck4aMn6mgJXIBjKqmvgDAx0P--K3d&s=e27a8b69b20694d9f9ec7aad3d4f2033&isFee=1
     * title : 车友团激活卡
     * desc : 车友团权益激活卡
     * type : 0
     * object : 0
     */

    @SerializedName("qrCode")
    var qrCode: String? = null


    @SerializedName("url")
    var url: String? = null


    @SerializedName("title")
    var title: String? = null


    @SerializedName("desc")
    var desc: String? = null


    @SerializedName("type")
    var type: Int? = null


    @SerializedName("object")
    var `object`: Int? = null
}