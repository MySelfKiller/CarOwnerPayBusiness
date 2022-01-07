package com.kayu.business_car_owner.model

/**
 * Author by killer, Email xx@xx.com, Date on 2020/9/28.
 * PS: Not easy to write code, please indicate.
 * "id": null,
 * "gasId": null,
 * "oilNo": 92,
 * "oilName": "92#",
 * "priceYfq": 6.13,
 * "priceGun": 7.11,
 * "priceOfficial": 7.31,
 * "oilType": 1,
 * "gunNos": "1,3,6"
 * id	Long	主键(备用)
 * gasId	String	Api主键(备用)
 * oilNo	Int	油号编码
 * oilName	String	油号名称
 * priceYfq	Double	团油价/元
 * priceGun	Double	油枪价/元
 * priceOfficial	Double	国标价/元
 * oilType	Int	类型 (备用)
 * gunNos	String	油枪(,)逗号分隔
 */
class OilsParam {

    var oilNo //油号编码
            = 0

    var oilName //油号名称
            : String = ""
    var oilType //燃油类型 1:汽油 2:柴油 3:天然气
            = 0

    var isDefault //是否为默认条件 0:否 1:是
            = 0
    var id //主键(备用)
            : Long = 0
    var gasId //Api主键(备用)
            : String = ""
    var gunNos //油枪(,)逗号分隔
            : String = ""
    var priceYfq //团油价/元
            = 0.0
    var priceGun //油枪价/元
            = 0.0
    var priceOfficial //国标价/元
            = 0.0
    var offDiscount //国标折扣/百分比
            : String = ""
    var gunDiscount //油站折扣/百分比
            : String = ""
}