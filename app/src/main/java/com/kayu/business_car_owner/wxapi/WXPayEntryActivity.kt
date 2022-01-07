package com.kayu.business_car_owner.wxapi

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.tencent.mm.opensdk.openapi.IWXAPI
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.constants.ConstantsAPI
import com.kayu.utils.*
import java.lang.Exception

class WXPayEntryActivity : Activity(), IWXAPIEventHandler {
    private var api: IWXAPI? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LogUtil.e("hm", "WXPayEntryActivity")
        val share = WXShare(this)
        api = share //                                .register()
            .api

        //注意：
        //第三方开发者如果使用透明界面来实现WXEntryActivity，需要判断handleIntent的返回值，如果返回值为false，则说明入参不合法未被SDK处理，应finish当前透明界面，避免外部通过传递非法参数的Intent导致停留在透明界面，引起用户的疑惑
        try {
            if (!api!!.handleIntent(intent, this)) {
                finish()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        LogUtil.e("hm", "onNewIntent")
        setIntent(intent)
        if (!api!!.handleIntent(intent, this)) {
            finish()
        }
    }

    override fun onReq(baseReq: BaseReq) {}
    override fun onResp(baseResp: BaseResp) {
        val intent: Intent = Intent(WXShare.Companion.ACTION_SHARE_RESPONSE)
        //        if(Build.VERSION.SDK_INT >= 26){
//            intent.addFlags(0x01000000);
//        }
        if (baseResp is SendAuth.Resp) {
            val code = baseResp.code //需要转换一下才可以
            intent.action = WXShare.Companion.TYPE_LOGIN
            intent.putExtra(WXShare.Companion.EXTRA_RESULT, code)
        } else {
            if (baseResp.type == ConstantsAPI.COMMAND_SENDAUTH) {
                intent.action = WXShare.Companion.TYPE_SHARE
            } else if (baseResp.type == ConstantsAPI.COMMAND_PAY_BY_WX) {
                intent.action = WXShare.Companion.TYPE_PAY
            }
            intent.putExtra(WXShare.Companion.EXTRA_RESULT, WXShare.Response(baseResp))
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        finish()
    }
}