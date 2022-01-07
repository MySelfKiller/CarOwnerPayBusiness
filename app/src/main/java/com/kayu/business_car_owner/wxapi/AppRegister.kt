package com.kayu.business_car_owner.wxapi

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.kayu.utils.*

class AppRegister : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val api = WXAPIFactory.createWXAPI(context, Constants.WX_APP_ID, false)

        // 将该app注册到微信
        api.registerApp(Constants.WX_APP_ID)
    }
}