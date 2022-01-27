package com.kayu.business_car_owner.model

class CategoryBean {
    /**
     * "icon": "https://www.ky808.cn/images/20200514/24ae2f6d0b054404bf2907e719eff49d.jpg",
     * "id": 1,
     * "tag": "热门推荐",
     * "href": null,
     * "title": "金融技术"
     */
    val id //主键
            : Long = 0

    val title //主键
            : String = ""

    val icon //图标加载url
            : String = ""

    val tag //小标签
            : String = ""

    val remark //小标题
            : String = ""

    val href //H5跳转链接
            : String = ""

    //"type": "KY_H5"

    val type //跳转类型
            : String = ""

    val isPublic //是否公开
            : Int = 0
}