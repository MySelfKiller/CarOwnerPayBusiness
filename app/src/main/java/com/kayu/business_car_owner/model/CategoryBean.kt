package com.kayu.business_car_owner.model

class CategoryBean {
    /**
     * "icon": "https://www.ky808.cn/images/20200514/24ae2f6d0b054404bf2907e719eff49d.jpg",
     * "id": 1,
     * "tag": "热门推荐",
     * "href": null,
     * "title": "金融技术"
     */
    var id //主键
            : Long = 0

    var title //主键
            : String = ""

    var icon //图标加载url
            : String = ""

    var tag //小标签
            : String = ""

    var remark //小标题
            : String = ""

    var href //H5跳转链接
            : String = ""

    //"type": "KY_H5"

    var type //跳转类型
            : String = ""

    var isPublic //是否公开
            : Int = 0
}