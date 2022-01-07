package com.kayu.business_car_owner.config_ad

import android.content.Context
import android.util.Log
import com.bytedance.sdk.openadsdk.TTAdManager
import com.bytedance.sdk.openadsdk.TTAdSdk
import com.bytedance.sdk.openadsdk.TTAdSdk.InitCallback
import com.bytedance.sdk.openadsdk.TTAdConfig
import com.kayu.business_car_owner.KWApplication
import com.kayu.utils.StringUtil
import java.lang.RuntimeException

/**
 * 可以用一个单例来保存TTAdManager实例，在需要初始化sdk的时候调用
 */
object TTAdManagerHolder {
    private const val TAG = "TTAdManagerHolder"

    //    private static final String appID = "5138603";//应用ID
    //    public static final String videoID = "945853775";//激励视屏ID
    //    public static final String splashID = "887428694";//闪屏广告ID
    private var appID = "5144457" //应用ID
    const val videoID = "945900466" //激励视屏ID
    const val splashID = "887446448" //闪屏广告ID
    private var sInit = false
    fun get(): TTAdManager {
        if (!sInit) {
            throw RuntimeException("TTAdSdk is not init, please check.")
        }
        return TTAdSdk.getAdManager()
    }

    @JvmStatic
    fun init(context: Context) {
        doInit(context)
    }

    //step1:接入网盟广告sdk的初始化操作，详情见接入文档和穿山甲平台说明
    private fun doInit(context: Context) {
        if (!sInit) {
            TTAdSdk.init(context, buildConfig(context), object : InitCallback {
                override fun success() {
                    Log.i(TAG, "success: " + TTAdSdk.isInitSuccess())
                }

                override fun fail(code: Int, msg: String) {
                    Log.i(TAG, "fail:  code = $code msg = $msg")
                }
            })
            sInit = true
        }
    }

    private fun buildConfig(context: Context): TTAdConfig {
        if (null != KWApplication.instance.systemArgs && !StringUtil.isEmpty(KWApplication.instance.systemArgs!!.android?.csjAppid)) {
            appID = KWApplication.instance.systemArgs?.android?.csjAppid.toString()
        }
        return TTAdConfig.Builder()
            .appId(appID)
            .useTextureView(true) //使用TextureView控件播放视频,默认为SurfaceView,当有SurfaceView冲突的场景，可以使用TextureView
            .allowShowNotify(true) //是否允许sdk展示通知栏提示
            .allowShowPageWhenScreenLock(true) // 锁屏下穿山甲SDK不会再出落地页，此API已废弃，调用没有任何效果
            .debug(true) //测试阶段打开，可以通过日志排查问题，上线时去除该调用
            //                .directDownloadNetworkType(
            //                        TTAdConstant.NETWORK_STATE_WIFI,
            //                        TTAdConstant.NETWORK_STATE_4G) //允许直接下载的网络状态集合
            .directDownloadNetworkType()
            .supportMultiProcess(true) //是否支持多进程
            .needClearTaskReset()
            .build()
    }
}