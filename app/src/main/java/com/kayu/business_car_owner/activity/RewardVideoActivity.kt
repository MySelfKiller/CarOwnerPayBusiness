package com.kayu.business_car_owner.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import com.bytedance.sdk.openadsdk.TTAdNative
import com.kayu.business_car_owner.config_ad.TTAdManagerHolder
import com.bytedance.sdk.openadsdk.AdSlot
import com.bytedance.sdk.openadsdk.TTAdConstant
import com.bytedance.sdk.openadsdk.TTAppDownloadListener
import com.bytedance.sdk.openadsdk.TTRewardVideoAd
import com.bytedance.sdk.openadsdk.TTAdManager
import com.bytedance.sdk.openadsdk.TTAdNative.RewardVideoAdListener
import com.bytedance.sdk.openadsdk.TTRewardVideoAd.RewardAdInteractionListener

/**
 * Created by bytedance on 2018/2/1.
 */
class RewardVideoActivity constructor() : Activity() {
    private val mLoadAd: Button? = null
    private val mLoadAdVertical: Button? = null
    private val mShowAd: Button? = null
    private var mTTAdNative: TTAdNative? = null
    private var mttRewardVideoAd: TTRewardVideoAd? = null
    private var mHorizontalCodeId: String? = null
    private var mVerticalCodeId: String? = null
    private var mIsExpress: Boolean = false //是否请求模板广告
    private var mIsLoaded: Boolean = false //视频是否加载完成
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        setContentView(R.layout.activity_reward_video);
//        mLoadAd = (Button) findViewById(R.id.btn_reward_load);
//        mLoadAdVertical = (Button) findViewById(R.id.btn_reward_load_vertical);
//        mShowAd = (Button) findViewById(R.id.btn_reward_show);
        //step1:初始化sdk
        val ttAdManager: TTAdManager = TTAdManagerHolder.get()
        //step2:(可选，强烈建议在合适的时机调用):申请部分权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题。
        TTAdManagerHolder.get().requestPermissionIfNecessary(this)
        //step3:创建TTAdNative对象,用于调用广告请求接口
        mTTAdNative = ttAdManager.createAdNative(getApplicationContext())
        extraInfo
        //        initClickEvent();
        loadAd(mVerticalCodeId, TTAdConstant.VERTICAL)
        if (mttRewardVideoAd != null && mIsLoaded) {
            //step6:在获取到广告后展示,强烈建议在onRewardVideoCached回调后，展示广告，提升播放体验
            //该方法直接展示广告
//                    mttRewardVideoAd.showRewardVideoAd(RewardVideoActivity.this);

            //展示广告，并传入广告展示的场景
            mttRewardVideoAd!!.showRewardVideoAd(
                this@RewardVideoActivity,
                TTAdConstant.RitScenes.CUSTOMIZE_SCENES,
                "scenes_test"
            )
            mttRewardVideoAd = null
        } else {
//                    TToast.show(com.union_test.toutiao.activity.RewardVideoActivity.this, "请先加载广告");
            finish()
        }
    }

    private val extraInfo: Unit
        private get() {
            val intent: Intent? = getIntent()
            if (intent == null) {
                return
            }
            mHorizontalCodeId = intent.getStringExtra("horizontal_rit")
            mVerticalCodeId = intent.getStringExtra("vertical_rit")
            mIsExpress = intent.getBooleanExtra("is_express", false)
        }

    private fun initClickEvent() {
        mLoadAd!!.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                loadAd(mHorizontalCodeId, TTAdConstant.HORIZONTAL)
            }
        })
        mLoadAdVertical!!.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                loadAd(mVerticalCodeId, TTAdConstant.VERTICAL)
            }
        })
        mShowAd!!.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {}
        })
    }

    private var mHasShowDownloadActive: Boolean = false
    private fun loadAd(codeId: String?, orientation: Int) {
        //step4:创建广告请求参数AdSlot,具体参数含义参考文档
        val adSlot: AdSlot
        if (mIsExpress) {
            //个性化模板广告需要传入期望广告view的宽、高，单位dp，
            adSlot = AdSlot.Builder()
                .setCodeId(codeId) //模板广告需要设置期望个性化模板广告的大小,单位dp,激励视频场景，只要设置的值大于0即可
                .setExpressViewAcceptedSize(500f, 500f)
                .build()
        } else {
            //模板广告需要设置期望个性化模板广告的大小,单位dp,代码位是否属于个性化模板广告，请在穿山甲平台查看
            adSlot = AdSlot.Builder()
                .setCodeId(codeId)
                .build()
        }
        //step5:请求广告
        mTTAdNative!!.loadRewardVideoAd(adSlot, object : RewardVideoAdListener {
            public override fun onError(code: Int, message: String) {
                Log.e(TAG, "Callback --> onError: " + code + ", " + message.toString())
                //                TToast.show(com.union_test.toutiao.activity.RewardVideoActivity.this, message);
            }

            //视频广告加载后，视频资源缓存到本地的回调，在此回调后，播放本地视频，流畅不阻塞。
            public override fun onRewardVideoCached() {
                Log.e(TAG, "Callback --> onRewardVideoCached")
                mIsLoaded = true
                //                TToast.show(com.union_test.toutiao.activity.RewardVideoActivity.this, "Callback --> rewardVideoAd video cached");
            }

            public override fun onRewardVideoCached(ttRewardVideoAd: TTRewardVideoAd) {}

            //视频广告的素材加载完毕，比如视频url等，在此回调后，可以播放在线视频，网络不好可能出现加载缓冲，影响体验。
            public override fun onRewardVideoAdLoad(ad: TTRewardVideoAd) {
                Log.e(TAG, "Callback --> onRewardVideoAdLoad")

//                TToast.show(com.union_test.toutiao.activity.RewardVideoActivity.this, "rewardVideoAd loaded 广告类型：" + getAdType(ad.getRewardVideoAdType()));
                mIsLoaded = false
                mttRewardVideoAd = ad
                mttRewardVideoAd!!.setRewardAdInteractionListener(object :
                    RewardAdInteractionListener {
                    public override fun onAdShow() {
                        Log.d(TAG, "Callback --> rewardVideoAd show")
                        //                        TToast.show(com.union_test.toutiao.activity.RewardVideoActivity.this, "rewardVideoAd show");
                    }

                    public override fun onAdVideoBarClick() {
                        Log.d(TAG, "Callback --> rewardVideoAd bar click")
                        //                        TToast.show(com.union_test.toutiao.activity.RewardVideoActivity.this, "rewardVideoAd bar click");
                    }

                    public override fun onAdClose() {
                        Log.d(TAG, "Callback --> rewardVideoAd close")
                        //                        TToast.show(com.union_test.toutiao.activity.RewardVideoActivity.this, "rewardVideoAd close");
                    }

                    //视频播放完成回调
                    public override fun onVideoComplete() {
                        Log.d(TAG, "Callback --> rewardVideoAd complete")
                        //                        TToast.show(com.union_test.toutiao.activity.RewardVideoActivity.this, "rewardVideoAd complete");
                    }

                    public override fun onVideoError() {
                        Log.e(TAG, "Callback --> rewardVideoAd error")
                        //                        TToast.show(com.union_test.toutiao.activity.RewardVideoActivity.this, "rewardVideoAd error");
                    }

                    //视频播放完成后，奖励验证回调，rewardVerify：是否有效，rewardAmount：奖励梳理，rewardName：奖励名称
                    public override fun onRewardVerify(
                        rewardVerify: Boolean,
                        rewardAmount: Int,
                        rewardName: String,
                        errorCode: Int,
                        errorMsg: String
                    ) {
                        val logString: String =
                            ("verify:" + rewardVerify + " amount:" + rewardAmount +
                                    " name:" + rewardName + " errorCode:" + errorCode + " errorMsg:" + errorMsg)
                        Log.e(TAG, "Callback --> " + logString)
                        //                        TToast.show(com.union_test.toutiao.activity.RewardVideoActivity.this, logString);
                    }

                    public override fun onSkippedVideo() {
                        Log.e(TAG, "Callback --> rewardVideoAd has onSkippedVideo")
                        //                        TToast.show(com.union_test.toutiao.activity.RewardVideoActivity.this, "rewardVideoAd has onSkippedVideo");
                    }
                })
                mttRewardVideoAd!!.setDownloadListener(object : TTAppDownloadListener {
                    public override fun onIdle() {
                        mHasShowDownloadActive = false
                    }

                    public override fun onDownloadActive(
                        totalBytes: Long,
                        currBytes: Long,
                        fileName: String,
                        appName: String
                    ) {
                        Log.d(
                            "DML",
                            "onDownloadActive==totalBytes=" + totalBytes + ",currBytes=" + currBytes + ",fileName=" + fileName + ",appName=" + appName
                        )
                        if (!mHasShowDownloadActive) {
                            mHasShowDownloadActive = true
                            //                            TToast.show(com.union_test.toutiao.activity.RewardVideoActivity.this, "下载中，点击下载区域暂停", Toast.LENGTH_LONG);
                        }
                    }

                    public override fun onDownloadPaused(
                        totalBytes: Long,
                        currBytes: Long,
                        fileName: String,
                        appName: String
                    ) {
                        Log.d(
                            "DML",
                            "onDownloadPaused===totalBytes=" + totalBytes + ",currBytes=" + currBytes + ",fileName=" + fileName + ",appName=" + appName
                        )
                        //                        TToast.show(com.union_test.toutiao.activity.RewardVideoActivity.this, "下载暂停，点击下载区域继续", Toast.LENGTH_LONG);
                    }

                    public override fun onDownloadFailed(
                        totalBytes: Long,
                        currBytes: Long,
                        fileName: String,
                        appName: String
                    ) {
                        Log.d(
                            "DML",
                            "onDownloadFailed==totalBytes=" + totalBytes + ",currBytes=" + currBytes + ",fileName=" + fileName + ",appName=" + appName
                        )
                        //                        TToast.show(com.union_test.toutiao.activity.RewardVideoActivity.this, "下载失败，点击下载区域重新下载", Toast.LENGTH_LONG);
                    }

                    public override fun onDownloadFinished(
                        totalBytes: Long,
                        fileName: String,
                        appName: String
                    ) {
                        Log.d(
                            "DML",
                            "onDownloadFinished==totalBytes=" + totalBytes + ",fileName=" + fileName + ",appName=" + appName
                        )
                        //                        TToast.show(com.union_test.toutiao.activity.RewardVideoActivity.this, "下载完成，点击下载区域重新下载", Toast.LENGTH_LONG);
                    }

                    public override fun onInstalled(fileName: String, appName: String) {
                        Log.d(
                            "DML",
                            "onInstalled==" + ",fileName=" + fileName + ",appName=" + appName
                        )
                        //                        TToast.show(com.union_test.toutiao.activity.RewardVideoActivity.this, "安装完成，点击下载区域打开", Toast.LENGTH_LONG);
                    }
                })
            }
        })
    }

    private fun getAdType(type: Int): String {
        when (type) {
            TTAdConstant.AD_TYPE_COMMON_VIDEO -> return "普通激励视频，type=" + type
            TTAdConstant.AD_TYPE_PLAYABLE_VIDEO -> return "Playable激励视频，type=" + type
            TTAdConstant.AD_TYPE_PLAYABLE -> return "纯Playable，type=" + type
        }
        return "未知类型+type=" + type
    }

    companion object {
        private val TAG: String = "RewardVideoActivity"
    }
}