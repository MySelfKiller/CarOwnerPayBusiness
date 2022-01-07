package com.kayu.business_car_owner.model

class OilStationBean {
    /**
     * "id": 1,
     * "gasId": "ZF223110356",
     * "gasName": "众诚连锁卫星加油站",
     *  "gasLogoSmall": "https://static.czb365.com/gas_images/ZF223110356_small.jpg?x-oss-process=image/resize,m_lfit,h_200,w_200/format,png",
     * "gasAddress": "吉林省长春市朝阳区卫星路7630号",
     * "gasAddressLatitude": 43.832146,
     * "gasAddressLongitude": 125.307571,
     * "distance": 0.0,
     * "priceYfq": 6.13,
     * "priceOfficial": 7.31,
     * "priceGun": 7.11
     */
    var id //主键
            : Long = 0

    var gasId //油站API主键
            : String = ""

    var gasName //油站名称
            : String = ""

    var gasLogoSmall //油站压缩图标
            : String = ""

    var gasAddress //油站详细地址
            : String = ""

    var gasAddressLatitude //油站纬度
            = 0.0

    var gasAddressLongitude //油站经度
            = 0.0

    var distance //距离/km
            = 0.0

    var priceYfq //油团价/元
            = 0.0
    var priceOfficial //国标价/元
            = 0.0

    var priceGun //枪价/元
            = 0.0
    var offDiscount //国标折扣/百分比
            : String = ""

    var gunDiscount //油站折扣/百分比
            : String = ""

    var channel //渠道编码 团油:ty ，淘油宝:tyb 青桔:qj
            : String = ""

    var nextIsBuy //下一步是否直接获取购买链接 0:否 1:是
            = 0
    var oilsTypeList: ArrayList<OilsTypeParam>? = null
}