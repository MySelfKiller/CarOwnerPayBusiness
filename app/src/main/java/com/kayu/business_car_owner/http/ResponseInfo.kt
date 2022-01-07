package com.kayu.business_car_owner.http


/**
 * Created by Killer on 2018/2/11.
 */
class ResponseInfo(status: Int, msg: String) {

    var status = -1

    var msg = ""
    var url: String? = null

    var responseData: Any? = null

    init {
        this.status = status
        this.msg = msg
    }
}