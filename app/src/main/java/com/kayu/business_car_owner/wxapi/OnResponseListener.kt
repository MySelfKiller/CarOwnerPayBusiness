package com.kayu.business_car_owner.wxapi

interface OnResponseListener {
    fun onSuccess()
    fun onCancel()
    fun onFail(message: String)
}