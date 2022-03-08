package com.kayu.business_car_owner

import android.annotation.SuppressLint
import android.app.Activity
import android.content.*
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatButton
import com.kayu.business_car_owner.config_ad.TTAdManagerHolder.init
import com.kayu.business_car_owner.activity.AppManager.Companion.appManager
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.hjq.toast.ToastUtils
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.kayu.utils.location.LocationManagerUtil
import androidx.multidex.MultiDexApplication
import com.kayu.business_car_owner.model.SystemParam
import com.kayu.business_car_owner.model.SystemParamContent
import com.squareup.leakcanary.LeakCanary
import com.kayu.business_car_owner.activity.SplashActivity
import com.kayu.business_car_owner.activity.SplashHotActivity
import com.qq.e.comm.managers.GDTAdSdk
import com.kayu.business_car_owner.http.cookie.PersistentCookieStore
import com.kayu.business_car_owner.http.OkHttpManager
import com.kayu.business_car_owner.activity.login.LoginAutoActivity
import cn.jiguang.verifysdk.api.JVerificationInterface
import com.hjq.toast.style.ToastWhiteStyle
import com.kongzue.dialog.util.DialogSettings
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.kayu.business_car_owner.http.HttpConfig
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.ViewTarget
import com.davemorrissey.labs.subscaleview.ImageSource
import com.kayu.utils.callback.ImageCallback
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.kongzue.dialog.v3.MessageDialog
import com.kongzue.dialog.interfaces.OnDialogButtonClickListener
import com.kayu.business_car_owner.model.MapInfoModel
import com.kayu.utils.location.CoordinateTransformUtil
import com.kongzue.dialog.v3.BottomMenu
import com.kongzue.dialog.v3.TipGifDialog
import com.kayu.business_car_owner.ui.text_link.UrlClickableSpan
import com.kongzue.dialog.v3.CustomDialog
import com.kayu.business_car_owner.activity.WebViewActivity
import com.kayu.business_car_owner.activity.ActivationActivity
import com.kayu.utils.*
import com.kayu.utils.callback.Callback
import com.kongzue.dialog.interfaces.OnMenuItemClickListener
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.lang.Exception
import java.lang.StringBuilder
import java.net.URISyntaxException
import java.util.ArrayList
import java.util.concurrent.atomic.AtomicInteger
import kotlin.properties.Delegates

class KWApplication() : MultiDexApplication() {
    //身份 -2：游客、0:普通用户、1:会员用户、2:经销商(团长)、3:运营商
    var userRole = 0
    var isGasPublic = 0
    var isWashPublic = 0

    //注册dialog内容,引导游客身份注册办卡等相关信息
    var regDialogTip: SystemParam? = null
    var displayWidth = 0
    var displayHeight = 0

    //    public String token_key;
    //    private Picasso picasso;
    //    private String photographName;
    //    private String fileName;
    var token //登录成功后返回的token
            : String? = null

    //    private int downloadIndex;
    var oid: String? = null
    var localBroadcastManager: LocalBroadcastManager? = null
    private var sp: SharedPreferences? = null
    var systemArgs: SystemParamContent? = null
    private val mActivityCount = AtomicInteger(0)
    private var mAppStopTimeMillis = 0L
    override fun onCreate() {
        instance = this
        sp = getSharedPreferences(Constants.SharedPreferences_name, MODE_PRIVATE)
        val sysArgs = sp?.getString(Constants.system_args, "")
        if (!StringUtil.isEmpty(sysArgs)) {
            systemArgs = GsonHelper.fromJson(sysArgs, SystemParamContent::class.java)
        }
        super.onCreate()
        if (!LeakCanary.isInAnalyzerProcess(this)) {
            var sRefWatcher = LeakCanary.install(this)
        }
        initAdSdk()
        //        setFornts();
        initDialogSetting()
        LocationManagerUtil.init(this)
        initJPushSetting()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

//        ZoomMediaLoader.getInstance().init(new TestImageLoader());
        val sp = getSharedPreferences(Constants.SharedPreferences_name, MODE_PRIVATE)
        token = sp.getString(Constants.token, "")
        LogUtil.setIsDebug(BuildConfig.LOG_DEBUG)
        localBroadcastManager = LocalBroadcastManager.getInstance(this) // 获取实例
        val intentFilter = IntentFilter()
        intentFilter.addAction("com.kayu.broadcasttest.JUMP")
        val localReceiver = LocalReceiver()
        localBroadcastManager!!.registerReceiver(localReceiver, intentFilter) // 注册本地广播监听器

        //监听是所有activity生命周期
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
            override fun onActivityStarted(activity: Activity) {
                val isLogin = sp.getBoolean(Constants.isLogin, false) //判断用户是否登录
                //热启动 && 应用退到后台时间超过10s
                if (((mActivityCount.get() == 0) && isLogin
                            && (System.currentTimeMillis() - mAppStopTimeMillis > 10 * 1000
                            ) && activity !is SplashActivity)
                ) {
                    activity.startActivity(Intent(activity, SplashHotActivity::class.java))
                }

                //+1
                mActivityCount.getAndAdd(1)
            }

            override fun onActivityResumed(activity: Activity) {}
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {
//-1
                mActivityCount.getAndDecrement()

                //退到后台，记录时间
                if (mActivityCount.get() == 0) {
                    mAppStopTimeMillis = System.currentTimeMillis()
                }
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {}
        })
    }

    //初始化广告SDK
    fun initAdSdk() {
        //是否弹出过隐私弹框
        val isLogin = sp!!.getBoolean(Constants.isLogin, false)
        if (isLogin) {
            //腾讯广告SDK
            var appID: String? = "1200140135"
            if (null != systemArgs && !StringUtil.isEmpty(systemArgs!!.android!!.ylhAppid)) {
                appID = systemArgs!!.android!!.ylhAppid
            }
            GDTAdSdk.init(this, appID)
            //穿山甲SDK初始化
            //强烈建议在应用对应的Application#onCreate()方法中调用，避免出现content为null的异常
            init(this)
        }
    }

    //记录首次异常时间
    private var firstTime: Long = 0
    private var xxx = 1
    private var yyy = 1

    internal inner class LocalReceiver() : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
//            Toast.makeText(context, "received local broadcast", Toast.LENGTH_SHORT).show();
//            LogUtil.e("接收退出广告","received local broadcast"+yyy);
            yyy++
            val secondTime = System.currentTimeMillis()
            if (firstTime == 0L || secondTime - firstTime > 1000 * 30) {
//                LogUtil.e("强制退出","退出次数"+xxx);
                xxx += 1
                firstTime = secondTime
                // 2020/6/8 判断用户登录信息失效跳转
                val sp = getSharedPreferences(Constants.SharedPreferences_name, MODE_PRIVATE)
                val editor = sp.edit()
                editor.putBoolean(Constants.isLogin, false)
                editor.putString(Constants.login_info, "")
                editor.apply()
                editor.commit()
                PersistentCookieStore(applicationContext).removeAll()
                OkHttpManager.resetHttpClient()
                appManager!!.finishAllActivity()
                LocationManagerUtil.self?.stopLocation()
                //                    LocationManager.getSelf().destroyLocation();
                val inx = Intent(context, LoginAutoActivity::class.java)
                inx.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(inx)
            }
        }
    }
    //    public Picasso getPicasso(){
    //        return picasso;
    //    }
    //    private void initPicasso(Context context){
    //        LocationManager.init(this);
    //        Picasso.Builder builder = new Picasso.Builder(context);
    //        builder.downloader(new OkHttp3Downloader(OkHttpManager.getInstance().getHttpClient()));
    //        picasso = builder.listener(new Picasso.Listener() {
    //            @Override
    //            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
    //                exception.printStackTrace();
    //            }
    //        })
    //                .loggingEnabled(true)// 打log
    //                .build();
    //    }
    /**
     * 初始化极光认证
     */
    private fun initJPushSetting() {
        // 打开调试模式
        JVerificationInterface.setDebugMode(true)
        JVerificationInterface.init(this) { i, s -> LogUtil.e("JPush", "code:$i,msg:$s") }
    }

    private fun initDialogSetting() {
        ToastUtils.init(this, ToastWhiteStyle(this))
        ToastUtils.setGravity(Gravity.CENTER, 0, 0)
        DialogSettings.isUseBlur = true //是否开启模糊效果，默认关闭
        DialogSettings.modalDialog = false //是否开启模态窗口模式，一次显示多个对话框将以队列形式一个一个显示，默认关闭
        DialogSettings.style =
            DialogSettings.STYLE.STYLE_IOS //全局主题风格，提供三种可选风格，STYLE_MATERIAL, STYLE_KONGZUE, STYLE_IOS
        DialogSettings.theme = DialogSettings.THEME.LIGHT //全局对话框明暗风格，提供两种可选主题，LIGHT, DARK
        DialogSettings.tipTheme = (DialogSettings.THEME.LIGHT) //全局提示框明暗风格，提供两种可选主题，LIGHT, DARK
        //        DialogSettings.titleTextInfo = (TextInfo);              //全局对话框标题文字样式
//        DialogSettings.menuTitleInfo = (TextInfo);              //全局菜单标题文字样式
//        DialogSettings.menuTextInfo = (TextInfo);               //全局菜单列表文字样式
//        DialogSettings.contentTextInfo = (TextInfo);            //全局正文文字样式
//        DialogSettings.buttonTextInfo = (TextInfo);             //全局默认按钮文字样式
//        DialogSettings.buttonPositiveTextInfo = (TextInfo);     //全局焦点按钮文字样式（一般指确定按钮）
//        DialogSettings.inputInfo = (InputInfo);                 //全局输入框文本样式
//        DialogSettings.backgroundColor = (ColorInt);            //全局对话框背景颜色，值0时不生效
//        DialogSettings.cancelable = (boolean);                  //全局对话框默认是否可以点击外围遮罩区域或返回键关闭，此开关不影响提示框（TipGifDialog）以及等待框（TipDialog）
//        DialogSettings.cancelableTipDialog = (boolean);         //全局提示框及等待框（WaitDialog、TipDialog）默认是否可以关闭
//        DialogSettings.DEBUGMODE = (boolean);                   //是否允许打印日志
//        DialogSettings.blurAlpha = (int);                       //开启模糊后的透明度（0~255）
//        DialogSettings.systemDialogStyle = (styleResId);        //自定义系统对话框style，注意设置此功能会导致原对话框风格和动画失效
//        DialogSettings.dialogLifeCycleListener = (DialogLifeCycleListener);  //全局Dialog生命周期监听器
//        DialogSettings.defaultCancelButtonText = (String);      //设置 BottomMenu 和 ShareDialog 默认“取消”按钮的文字
//        DialogSettings.tipBackgroundResId = (drawableResId);    //设置 TipDialog 和 WaitDialog 的背景资源
//        DialogSettings.tipTextInfo = (InputInfo);               //设置 TipDialog 和 WaitDialog 文字样式
//        DialogSettings.autoShowInputKeyboard = (boolean);       //设置 InputDialog 是否自动弹出输入法
//        DialogSettings.okButtonDrawable = (drawable);           //设置确定按钮背景资源
//        DialogSettings.cancelButtonDrawable = (drawable);       //设置取消按钮背景资源
//        DialogSettings.otherButtonDrawable = (drawable);        //设置其他按钮背景资源
//        Notification.mode = Notification.Mode.FLOATING_WINDOW;  //通知实现方式。可选 TOAST 使用自定义吐司实现以及 FLOATING_WINDOW 悬浮窗实现方式

//检查 Renderscript 兼容性，若设备不支持，DialogSettings.isUseBlur 会自动关闭；
        val renderscriptSupport = DialogSettings.checkRenderscriptSupport(applicationContext)
        DialogSettings.init() //初始化清空 BaseDialog 队列
    }

    private fun setFornts() {
        val typeface = Typeface.createFromAsset(assets, "fonts/Quicksand-Medium.ttf")
        try {
            val field = Typeface::class.java.getDeclaredField("MONOSPACE")
            field.isAccessible = true
            field[null] = typeface
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        //非默认值
        if (newConfig.fontScale != 1f) {
            resources
        }
        super.onConfigurationChanged(newConfig)
    }

    override fun getResources(): Resources { //还原字体大小
        val res = super.getResources()
        //非默认值
        if (res.configuration.fontScale != 1f) {
            val newConfig = Configuration()
            newConfig.setToDefaults() //设置默认
            res.updateConfiguration(newConfig, res.displayMetrics)
        }
        return res
    }

    @SuppressLint("CheckResult")
    fun loadImg(
        activity: Activity?,
        url: String,
        view: SubsamplingScaleImageView,
        callback: Callback
    ) {
        val mUrl: String
        if (url.startsWith("http")) {
            mUrl = url
        } else {
            mUrl = HttpConfig.HOST + url
        }
        //        KWApplication.getInstance().getPicasso()
//                .load(mUrl)
////                .placeholder(R.mipmap.ic_defult_img)
////                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
//                .transform(new BlurTransformation())
//                .into(view, callback);
        Glide.with(this)
            .load(mUrl)
            .downloadOnly(object : ViewTarget<SubsamplingScaleImageView?, File?>(view) {


                override fun onLoadFailed(errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)
                    callback.onError()
                }

                override fun onResourceReady(resource: File, transition: Transition<in File?>?) {
                    view.setImage(ImageSource.uri(Uri.fromFile(resource)))
                    //                        Toasty.warning(activity, "渲染完成", Toast.LENGTH_SHORT).show();
//                        view.setImage(ImageSource.asset("norway_test.jpg"));
                    callback.onSuccess()                }
            })
    }

    fun loadImg(url: String?, view: ImageView) {
        val mUrl: String
        if (StringUtil.isEmpty(url)) {
            return
        }
        if (url != null) {
            if (url.startsWith("http")) {
                mUrl = url
            } else {
                mUrl = HttpConfig.HOST + url
            }
            Glide.with(this).load(mUrl).into((view))
        }

    }

    //加载本地资源，可裁剪
    fun loadImg(ids: Int, view: ImageView?, transformation: BitmapTransformation?) {
        Glide.with(this).load(ids).transform(transformation).into((view)!!)
    }

    //加载本地资源
    fun loadImg(ids: Int, view: ImageView?) {
        Glide.with(this).load(ids).into((view)!!)
    }

    //带回调的图片加载
    fun loadImg(url: String?, view: ImageView?, callback: ImageCallback?) {
        val mUrl: String?
        if (StringUtil.isEmpty(url)) {
            return
        }
        if (url!!.startsWith("http")) {
            mUrl = url
        } else {
            mUrl = HttpConfig.HOST + url
        }
        Glide.with(this).asBitmap().load(mUrl).into(object : CustomTarget<Bitmap?>() {
            override fun onLoadCleared(placeholder: Drawable?) {}
            override fun onLoadFailed(errorDrawable: Drawable?) {
                super.onLoadFailed(errorDrawable)
                callback!!.onError()
            }

            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap?>?) {
                view?.setImageBitmap(resource)
                callback?.onSuccess(resource)            }
        })
    }

    val dataPath: String
        get() = Utils.getEnaviBaseStorage(this)

    /**
     * 拨打电话（直接拨打电话）
     *
     * @param phoneNum 电话号码
     */
    fun callPhone(context: Activity, phoneNum: String) {
        MessageDialog.show((context as AppCompatActivity), "拨打电话", phoneNum, "呼叫", "取消")
            .setOkButton(
                OnDialogButtonClickListener { baseDialog, v ->
                    val intent = Intent(Intent.ACTION_CALL)
                    val data = Uri.parse("tel:$phoneNum")
                    intent.data = data
                    context.startActivity(intent)
                    false
                })
    }

    fun toNavi(
        context: Context,
        latitude: String,
        longtitude: String,
        address: String,
        flag: String?
    ) {
        val mapList = getMapInfoModels(context) ?: return
        val menuArr = ArrayList<CharSequence?>()
        for (model: MapInfoModel in mapList) {
            menuArr.add(model.mapName)
        }
        val bdCoordinate: DoubleArray
        val gcj02Coordinate: DoubleArray
        when (flag) {
            "WGS84" -> {
                bdCoordinate =
                    CoordinateTransformUtil.wgs84tobd09(longtitude.toDouble(), latitude.toDouble())
                gcj02Coordinate =
                    CoordinateTransformUtil.wgs84togcj02(longtitude.toDouble(), latitude.toDouble())
            }
            "GCJ02" -> {
                bdCoordinate =
                    CoordinateTransformUtil.gcj02tobd09(longtitude.toDouble(), latitude.toDouble())
                gcj02Coordinate = doubleArrayOf(longtitude.toDouble(), latitude.toDouble())
            }
            "BD09" -> {
                gcj02Coordinate =
                    CoordinateTransformUtil.bd09togcj02(longtitude.toDouble(), latitude.toDouble())
                bdCoordinate = doubleArrayOf(longtitude.toDouble(), latitude.toDouble())
            }
            else -> {
                gcj02Coordinate = doubleArrayOf(longtitude.toDouble(), latitude.toDouble())
                bdCoordinate = doubleArrayOf(longtitude.toDouble(), latitude.toDouble())
            }
        }
        BottomMenu.show((context as AppCompatActivity), menuArr, object : OnMenuItemClickListener {
            override fun onClick(text: String, index: Int) {
                when (text) {
                    "高德地图" -> goGaodeMap(
                        context,
                        gcj02Coordinate[1].toString(),
                        gcj02Coordinate[0].toString(),
                        address
                    )
                    "谷歌地图" -> goGoogleMap(
                        context,
                        gcj02Coordinate[1].toString(),
                        gcj02Coordinate[0].toString(),
                        address
                    )
                    "百度地图" -> goBaiduMap(
                        context,
                        bdCoordinate[1].toString(),
                        bdCoordinate[0].toString(),
                        address
                    )
                    "腾讯地图" -> goTencentMap(
                        context,
                        gcj02Coordinate[1].toString(),
                        gcj02Coordinate[0].toString(),
                        address
                    )
                }
            }
        })
    }

    private fun getMapInfoModels(context: Context): List<MapInfoModel>? {
        val mapList: MutableList<MapInfoModel> = ArrayList()
        if (isNavigationApk(context, "com.autonavi.minimap")) {
            val model = MapInfoModel()
            model.mapId = "0"
            model.mapName = "高德地图"
            mapList.add(model)
        }
        if (isNavigationApk(context, "com.google.android.apps.maps")) {
            val model = MapInfoModel()
            model.mapId = "1"
            model.mapName = "谷歌地图"
            mapList.add(model)
        }
        if (isNavigationApk(context, "com.baidu.BaiduMap")) {
            val model = MapInfoModel()
            model.mapId = "2"
            model.mapName = "百度地图"
            mapList.add(model)
        }
        if (isNavigationApk(context, "com.tencent.map")) {
            val model = MapInfoModel()
            model.mapId = "3"
            model.mapName = "腾讯地图"
            mapList.add(model)
        }
        if (mapList.size == 0) {
            TipGifDialog.show(context as AppCompatActivity, "您尚未安装导航APP", TipGifDialog.TYPE.WARNING)
            return null
        }
        return mapList
    }

    /**
     * 判断手机中是否有导航app
     *
     * @param context
     * @param packagename 包名
     */
    fun isNavigationApk(context: Context, packagename: String): Boolean {
        val packages = context.packageManager.getInstalledPackages(0)
        for (i in packages.indices) {
            val packageInfo = packages[i]
            return if ((packageInfo.packageName == packagename)) {
                true
            } else {
                continue
            }
        }
        return false
    }

    /**
     * 跳转到百度地图
     *
     * @param activity
     * @param latitude   纬度
     * @param longtitude 经度
     * @param address    终点
     */
    private fun goBaiduMap(
        activity: Context,
        latitude: String,
        longtitude: String,
        address: String
    ) {
        if (isNavigationApk(activity, "com.baidu.BaiduMap")) {
            try {
                val intent = Intent.getIntent(
                    ("intent://map/direction?destination=latlng:"
                            + latitude + ","
                            + longtitude + "|name:" + address +  //终点：该地址会在导航页面的终点输入框显示
                            "&mode=driving&" +  //选择导航方式 此处为驾驶
                            "region=" +  //
                            "&src=#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end")
                )
                activity.startActivity(intent)
            } catch (e: URISyntaxException) {
                LogUtil.e("goError", e.message)
            }
        } else {
            ToastUtils.show("您尚未安装百度地图")
        }
    }

    /**
     * 跳转到高德地图
     *
     * @param activity
     * @param latitude   纬度
     * @param longtitude 经度
     * @param address    终点
     */
    private fun goGaodeMap(
        activity: Context,
        latitude: String,
        longtitude: String,
        address: String
    ) {
        if (isNavigationApk(activity, "com.autonavi.minimap")) {
            try {
                val intent = Intent.getIntent(
                    ("androidamap://navi?sourceApplication=&poiname=" + address + "&lat=" + latitude
                            + "&lon=" + longtitude + "&dev=0")
                )
                activity.startActivity(intent)
            } catch (e: URISyntaxException) {
                LogUtil.e("goError", e.message)
            }
        } else {
            ToastUtils.show("您尚未安装高德地图")
        }
    }

    /**
     * 跳转到谷歌地图
     *
     * @param activity
     * @param latitude   纬度
     * @param longtitude 经度
     * @param address    终点
     */
    private fun goGoogleMap(
        activity: Context,
        latitude: String,
        longtitude: String,
        address: String
    ) {
        if (isNavigationApk(activity, "com.autonavi.minimap")) {
            try {
                val intent = Intent(
                    Intent.ACTION_VIEW, Uri.parse(
                        ("http://ditu" +
                                ".google" + ".cn/maps?hl=zh&mrt=loc&q=" + latitude + "," + longtitude + "(" + address + ")")
                    )
                )
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                intent.setClassName(
                    "com.google.android.apps.maps",
                    "com.google.android.maps.MapsActivity"
                )
                activity.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            ToastUtils.show("您尚未安装谷歌地图")
        }
    }

    /**
     * 跳转到腾讯地图
     *
     * @param activity
     * @param latitude   纬度
     * @param longtitude 经度
     * @param address    终点
     */
    private fun goTencentMap(
        activity: Context,
        latitude: String,
        longtitude: String,
        address: String
    ) {
        if (isNavigationApk(activity, "com.autonavi.minimap")) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(
                ("qqmap://map/routeplan?type=bus&from=我的位置&fromcoord=0,0"
                        + "&to=" + address
                        + "&tocoord=" + latitude + "," + longtitude
                        + "&policy=1&referer=myapp")
            )
            activity.startActivity(intent)
        } else {
            ToastUtils.show("您尚未安装腾讯地图")
        }
    }

    /**
     * 获取可点击的SpannableString
     *
     * @return
     */
    fun getClickableSpan(
        context: Context,
        titles: Array<String>,
        urls: Array<String>
    ): SpannableString {
        val messSB = StringBuilder()
        messSB.append("感谢您选择省省联盟APP！\n我们非常重视您的个信息和隐私安全。为了更好的保障您的个人权益，在您使用我们的产品前，请务必审慎阅读《")
        val title1Index = messSB.length - 1
        messSB.append(titles[1])
        val title1End = messSB.length + 1
        messSB.append("》与《")
        val title2Index = messSB.length - 1
        messSB.append(titles[0])
        val title2End = messSB.length + 1
        messSB.append(
            "》内的全部内容，同意并接受全部条款后开始使用我们的产品和服务。我们深知个人信息对您的重要性，我们将严格遵守相关法律法规，并采取相应的重要保护技术措施，" +
                    "尽力保护您的个人信息安全。在使用APP过程中，我们会基于您的授权获取您的以下权限，您有权拒绝和取消授权：\n"
        )
        messSB.append(
            ("1、定位权限：用于获取周边的特权信息，如优惠加油站，洗车门店等；\n" +
                    "2、设备信息权限：用于账号信息的验证，以保障交易安全；\n" +
                    "3、存储权限：以实现保存联系客服二维码图片功能；\n" +
                    "4、拨打电话权限：用于一键拨打客服电话功能。")
        )
        val spannableString = SpannableString(messSB.toString())

        //设置下划线文字
//        spannableString.setSpan(new NoUnderlineSpan(), title1Index, title1End, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //设置文字的单击事件
        spannableString.setSpan(
            UrlClickableSpan((context)!!, (urls[1])!!),
            title1Index,
            title1End,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        //设置文字的前景色
        spannableString.setSpan(
            ForegroundColorSpan(Color.BLUE),
            title1Index,
            title1End,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        //设置下划线文字
//        spannableString.setSpan(new NoUnderlineSpan(), title2Index, title2End, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //设置文字的单击事件
        spannableString.setSpan(
            UrlClickableSpan((context)!!, (urls[0])!!),
            title2Index,
            title2End,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        //设置文字的前景色
        spannableString.setSpan(
            ForegroundColorSpan(Color.BLUE),
            title2Index,
            title2End,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return spannableString
    }

    private var title: String? = null
    private var desc: String? = null
    private var regBtn: String? = null
    private var pastTitle: String? = null
    private var pastBtn: String? = null

    //    private String url = null;
//    fun showRegDialog(context: Context) {
//        if (null == regDialogTip || StringUtil.isEmpty(regDialogTip!!.content)) {
//        }
//        try {
//            //{
//            // "title": "免费办理会员",
//            // "desc": "成为会员，立享全球超百项特权",
//            // "regBtn": "立即免费办理",
//            // "pastTitle": "已办理熊猫特权卡",
//            // "pastBtn": "激活熊猫特权卡"
//            //}
//            val contentJSon = JSONObject(regDialogTip!!.content)
//            title = contentJSon.getString("title")
//            desc = contentJSon.getString("desc")
//            regBtn = contentJSon.getString("regBtn")
//            pastTitle = contentJSon.getString("pastTitle")
//            pastBtn = contentJSon.getString("pastBtn")
//        } catch (e: JSONException) {
//            e.printStackTrace()
//            return
//        }
//        CustomDialog.show(
//            context as AppCompatActivity?,
//            R.layout.dialog_activition,
//            object : CustomDialog.OnBindView {
//                override fun onBind(dialog: CustomDialog, v: View) {
//                    val dia_title: TextView = v.findViewById(R.id.dia_act_title)
//                    val dia_content: TextView = v.findViewById(R.id.dia_act_context)
//                    val dia_btn_handle: AppCompatButton = v.findViewById(R.id.dia_act_btn_handle)
//                    dia_btn_handle.setOnClickListener(object : NoMoreClickListener() {
//                        override fun OnMoreClick(view: View) {
//                            if (StringUtil.isEmpty(regDialogTip!!.url)) return
//                            val intent: Intent = Intent(context, WebViewActivity::class.java)
//                            intent.putExtra("url", regDialogTip!!.url)
//                            context.startActivity(intent)
//                        }
//
//                        override fun OnMoreErrorClick() {}
//                    })
//                    val dia_title_sub: TextView = v.findViewById(R.id.dia_act_title_sub)
//                    val dia_btn_activ: AppCompatButton = v.findViewById(R.id.dia_act_btn_activ)
//                    if (userRole != -2) {
//                        if (!StringUtil.isEmpty(title)) {
//                            dia_title.text = title
//                        }
//                        if (!StringUtil.isEmpty(desc)) {
//                            dia_content.text = desc
//                        }
//                        if (!StringUtil.isEmpty(regBtn)) {
//                            dia_btn_handle.text = regBtn
//                        }
//                        if (!StringUtil.isEmpty(pastTitle)) {
//                            dia_title_sub.text = pastTitle
//                        }
//                        if (!StringUtil.isEmpty(pastBtn)) {
//                            dia_btn_activ.text = pastBtn
//                        }
//                    }
//                    dia_btn_activ.setOnClickListener(object : NoMoreClickListener() {
//                        override fun OnMoreClick(view: View) {
//                            val intent: Intent = Intent(context, ActivationActivity::class.java)
//                            context.startActivity(intent)
//                        }
//
//                        override fun OnMoreErrorClick() {}
//                    })
//                }
//            }).setFullScreen(false).customLayoutParams = RelativeLayout.LayoutParams(
//            ViewGroup.LayoutParams.MATCH_PARENT,
//            ViewGroup.LayoutParams.WRAP_CONTENT
//        )
//    }

    //                    aid = Md5Util.getStringMD5(aid);
    val oidImei: String?
        get() {
            var imei: String? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (!StringUtil.isEmpty(oid)) {
//                    aid = Md5Util.getStringMD5(aid);
                    imei = "oid#$oid"
                }
            } else {
                imei = DeviceIdUtils.getIMEI(this)
            }
            return imei
        }

    companion object {
        var instance:KWApplication by Delegates.notNull()
    }
}