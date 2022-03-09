package com.kayu.business_car_owner.activity

import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.kayu.utils.status_bar_set.StatusBarUtil
import com.kayu.business_car_owner.*
import com.kayu.business_car_owner.R
import com.kayu.utils.*

class SplashHotActivity : AppCompatActivity() {
//    private var mTTAdNative: TTAdNative? = null
    private var mSplashContainer: FrameLayout? = null

    //是否强制跳转到主页面
    private val mForceGoMain: Boolean = false
//    private var mCodeId: String = TTAdManagerHolder.splashID

    //    private boolean mIsExpress = false; //是否请求模板广告
    private var splash_img: LinearLayout? = null

    //    private boolean isLogin;
    //    private boolean isSetPsd;
    //    private MainViewModel mainViewModel;
//    private var splashAD: SplashAD? = null
    public override fun onConfigurationChanged(newConfig: Configuration) {
        //非默认值
        if (newConfig.fontScale != 1f) {
            getResources()
        }
        super.onConfigurationChanged(newConfig)
    }

    public override fun getResources(): Resources { //还原字体大小
        val res: Resources = super.getResources()
        //非默认值
        if (res.getConfiguration().fontScale != 1f) {
            val newConfig: Configuration = Configuration()
            newConfig.setToDefaults() //设置默认
            res.updateConfiguration(newConfig, res.getDisplayMetrics())
        }
        return res
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //        mainViewModel = ViewModelProviders.of(SplashHotActivity.this).get(MainViewModel.class);
        KWApplication.instance.displayWidth = ScreenUtils.getDisplayWidth(this)
        KWApplication.instance.displayHeight = ScreenUtils.getDisplayHeight(this)
        //        StatusBarUtil.setStatusBarColor(this, getResources().getColor(R.color.dark));
        StatusBarUtil.setRootViewFitsSystemWindows(this, true)
        StatusBarUtil.setTranslucentStatus(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        }
        setContentView(R.layout.activity_splash)
        mSplashContainer = findViewById<View>(R.id.splash_container) as FrameLayout?
        splash_img = findViewById<View>(R.id.splash_img_lay) as LinearLayout?

//        getExtraInfo();
        //在合适的时机申请权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题
        //在开屏时候申请不太合适，因为该页面倒计时结束或者请求超时会跳转，在该页面申请权限，体验不好

        //step2:创建TTAdNative对象
//        try {
//            mTTAdNative = TTAdManagerHolder.get().createAdNative(this)
//        } catch (e: Exception) {
//            KWApplication.instance.initAdSdk()
//        }
//        if (null == mTTAdNative) {
//            KWApplication.instance.initAdSdk()
//        }
//        mTTAdNative = TTAdManagerHolder.get().createAdNative(this)
//        if (null != KWApplication.instance.systemArgs) {
//            if (StringUtil.isEmpty(KWApplication.instance.systemArgs!!.android?.showAd)) {
//                goToMainActivity()
//            } else {
//                val isCsjAD: Boolean = (KWApplication.instance.systemArgs!!.android?.showAd == "csj")
//                loadSplashAd(isCsjAD)
//            }
//        } else {
//            loadSplashAd(true)
//        }
        Handler(Looper.getMainLooper()).postDelayed({ goToMainActivity() },1500*1)
        //        new Handler().postDelayed(runnable,1200*1);
    }

    /**
     * 拉取开屏广告，开屏广告的构造方法有3种，详细说明请参考开发者文档。
     *
     * @param activity        展示广告的activity
     * @param adContainer     展示广告的大容器
     * @param posId           广告位ID
     * @param adListener      广告状态监听器
     * @param fetchDelay      拉取广告的超时时长：取值范围[1500, 5000]，设为0表示使用优量汇SDK默认的超时时长。
     */
//    private fun fetchSplashAD(
//        activity: Activity,
//        adContainer: ViewGroup?,
//        posId: String,
//        adListener: SplashADListener,
//        fetchDelay: Int
//    ) {
////        fetchSplashADTime = System.currentTimeMillis();
//        // skipContainer 此时必须是 VISIBLE 状态，否则将不能正常曝光计费
//        splashAD = SplashAD(activity, posId, adListener, fetchDelay)
//        splashAD!!.fetchAndShowIn(adContainer)
//        //        splashAD.fetchAdOnly();
//    }

    /**
     * 加载开屏广告
     */
//    private fun loadSplashAd(isCsjAD: Boolean) {
//        if (!isCsjAD) {
//            var posid: String = "5042238596128420"
//            if (null != KWApplication.instance.systemArgs && !StringUtil.isEmpty(KWApplication.instance.systemArgs!!.android?.ylhSplashid)) posid =
//                KWApplication.instance.systemArgs!!.android?.ylhSplashid.toString()
//            fetchSplashAD(this@SplashHotActivity, mSplashContainer, posid, adListener, AD_TIME_OUT)
//        } else {
//            //穿山甲广告
//            //step3:创建开屏广告请求参数AdSlot,具体参数含义参考文档
//            var adSlot: AdSlot? = null
//            if (null != KWApplication.instance.systemArgs && !StringUtil.isEmpty(KWApplication.instance.systemArgs!!.android?.csjPlacementid)) mCodeId =
//                KWApplication.instance.systemArgs!!.android?.csjPlacementid.toString()
//            adSlot = AdSlot.Builder()
//                .setCodeId(mCodeId)
//                .setImageAcceptedSize(
//                    KWApplication.instance.displayWidth,
//                    KWApplication.instance.displayHeight
//                )
//                .build()
//            //step4:请求广告，调用开屏广告异步请求接口，对请求回调的广告作渲染处理
//            mTTAdNative!!.loadSplashAd(adSlot, splashAdListener, AD_TIME_OUT)
//        }
//    }

    //穿山甲广告回调
//    var splashAdListener: SplashAdListener = object : SplashAdListener {
//        @MainThread
//        public override fun onError(code: Int, message: String) {
//            Log.d(TAG, message.toString())
//            //                ToastUtils.show(message);
//            goToMainActivity()
//        }
//
//        @MainThread
//        public override fun onTimeout() {
////                ToastUtils.show("开屏广告加载超时");
//            goToMainActivity()
//        }
//
//        @MainThread
//        public override fun onSplashAdLoad(ad: TTSplashAd) {
//            Log.d(TAG, "开屏广告请求成功")
//            if (ad == null) {
//                return
//            }
//            splash_img!!.setVisibility(View.GONE)
//            //获取SplashView
//            val view: View? = ad.getSplashView()
//            if ((view != null) && (mSplashContainer != null) && !isFinishing()) {
////                    mSplashContainer.removeAllViews();
//                //把SplashView 添加到ViewGroup中,注意开屏广告view：width >=70%屏幕宽；height >=50%屏幕高
//                mSplashContainer!!.addView(view)
//                //设置不开启开屏广告倒计时功能以及不显示跳过按钮,如果这么设置，您需要自定义倒计时逻辑
//                //ad.setNotAllowSdkCountdown();
//            } else {
//                goToMainActivity()
//            }
//
//            //设置SplashView的交互监听器
//            ad.setSplashInteractionListener(object : TTSplashAd.AdInteractionListener {
//                public override fun onAdClicked(view: View, type: Int) {
//                    Log.d(TAG, "onAdClicked")
//                    //                        ToastUtils.show("开屏广告点击");
////                    MessageDialog.show(SplashActivity.this,"提示","即将跳转到广告详情页面或第三方应用，是否允许","允许","取消");
//                }
//
//                public override fun onAdShow(view: View, type: Int) {
//                    Log.d(TAG, "onAdShow")
//                    //                        ToastUtils.show("开屏广告展示");
//                }
//
//                public override fun onAdSkip() {
//                    Log.d(TAG, "onAdSkip")
//                    //                        ToastUtils.show("开屏广告跳过");
//                    goToMainActivity()
//                }
//
//                public override fun onAdTimeOver() {
//                    Log.d(TAG, "onAdTimeOver")
//                    //                        ToastUtils.show("开屏广告倒计时结束");
//                    goToMainActivity()
//                }
//            })
//            if (ad.getInteractionType() == TTAdConstant.INTERACTION_TYPE_DOWNLOAD) {
//                ad.setDownloadListener(object : TTAppDownloadListener {
//                    var hasShow: Boolean = false
//                    public override fun onIdle() {}
//                    public override fun onDownloadActive(
//                        totalBytes: Long,
//                        currBytes: Long,
//                        fileName: String,
//                        appName: String
//                    ) {
//                        if (!hasShow) {
////                                ToastUtils.show("下载中...");
//                            hasShow = true
//                        }
//                    }
//
//                    public override fun onDownloadPaused(
//                        totalBytes: Long,
//                        currBytes: Long,
//                        fileName: String,
//                        appName: String
//                    ) {
////                            ToastUtils.show("下载暂停...");
//                    }
//
//                    public override fun onDownloadFailed(
//                        totalBytes: Long,
//                        currBytes: Long,
//                        fileName: String,
//                        appName: String
//                    ) {
////                            ToastUtils.show("下载失败...");
//                    }
//
//                    public override fun onDownloadFinished(
//                        totalBytes: Long,
//                        fileName: String,
//                        appName: String
//                    ) {
////                            ToastUtils.show("下载完成...");
//                    }
//
//                    public override fun onInstalled(fileName: String, appName: String) {
////                            ToastUtils.show("安装完成...");
//                    }
//                })
//            }
//        }
//    }

    //腾讯广告回调
//    var adListener: SplashADListener = object : SplashADListener {
//        public override fun onADPresent() {
////            Log.i("AD_DEMO", "SplashADPresent");
//        }
//
//        public override fun onADClicked() {
////            Log.i("AD_DEMO", "SplashADClicked clickUrl: "
////                    + (splashAD.getExt() != null ? splashAD.getExt().get("clickUrl") : ""));
//        }
//
//        /**
//         * 倒计时回调，返回广告还将被展示的剩余时间。
//         * 通过这个接口，开发者可以自行决定是否显示倒计时提示，或者还剩几秒的时候显示倒计时
//         *
//         * @param millisUntilFinished 剩余毫秒数
//         */
//        public override fun onADTick(millisUntilFinished: Long) {
////            Log.i("AD_DEMO", "SplashADTick " + millisUntilFinished + "ms");
//        }
//
//        public override fun onADExposure() {
////            Log.i("AD_DEMO", "SplashADExposure");
//        }
//
//        public override fun onADLoaded(l: Long) {
//            splash_img!!.setVisibility(View.GONE)
//            splashAD!!.setDownloadConfirmListener(object : DownloadConfirmListener {
//                public override fun onDownloadConfirm(
//                    activity: Activity,
//                    i: Int,
//                    s: String,
//                    downloadConfirmCallBack: DownloadConfirmCallBack
//                ) {
//                    MessageDialog.show(
//                        this@SplashHotActivity,
//                        "提示",
//                        "即将跳转到广告详情页面或第三方应用，是否允许",
//                        "允许",
//                        "取消"
//                    )
//                        .setOnOkButtonClickListener(object : OnDialogButtonClickListener {
//                            public override fun onClick(baseDialog: BaseDialog, v: View): Boolean {
//                                baseDialog.doDismiss()
//                                downloadConfirmCallBack.onConfirm()
//                                return false
//                            }
//                        })
//                        .setOnOkButtonClickListener(object : OnDialogButtonClickListener {
//                            public override fun onClick(baseDialog: BaseDialog, v: View): Boolean {
//                                baseDialog.doDismiss()
//                                downloadConfirmCallBack.onCancel()
//                                return false
//                            }
//                        })
//                }
//            })
//        }
//
//        public override fun onADDismissed() {
////            next();
//            goToMainActivity()
//        }
//
//        public override fun onNoAD(error: AdError) {
//            goToMainActivity()
//        }
//    }

    /**
     * 跳转到主页面
     */
    private fun goToMainActivity() {
        finish()
    }

    //    private Runnable runnable = new Runnable() {
    //        @Override
    //        public void run() {
    //
    //        }
    //    };
    public override fun onBackPressed() {
        super.onBackPressed()
    }

    companion object {
        private val TAG: String = "SplashActivity"

        //开屏广告加载超时时间,建议大于3000,这里为了冷启动第一次加载到广告并且展示,示例设置了3000ms
        private val AD_TIME_OUT: Int = 3500
    }
}