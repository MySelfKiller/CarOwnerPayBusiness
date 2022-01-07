package com.kayu.business_car_owner.model

class SysOrderBean {
    /**
     * {
     * "icon": "https://www.kakayuy.com/group1/M00/00/04/rBoO71-9_hOAalibAAAOpjQmAxE902.png",
     * "isPublic": 0,
     * "id": 1,
     * "href": null,
     * "sort": "1-1",
     * "title": "加油订单",
     * "type": "KY_GAS"
     * }
     */
    var id //主键
            : Long = 0

    var title //主键
            : String = ""

    var icon //图标加载url
            : String = ""

    var href //H5跳转链接
            : String = ""

    //"type": "KY_H5"

    var type //跳转类型
            : String = ""
    var sort //排序
            : String = ""

    var isPublic //是否公开
            : Int = -1
}