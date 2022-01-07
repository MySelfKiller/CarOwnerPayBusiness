package com.kayu.business_car_owner.activity

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.AlertDialog
import android.content.ClipData
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.*
import android.provider.MediaStore
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.webkit.*
import android.widget.LinearLayout
import android.widget.TextView
import com.hjq.toast.ToastUtils
import com.kongzue.dialog.v3.TipGifDialog
import com.kayu.business_car_owner.http.parser.NormalParse
import com.kayu.utils.status_bar_set.StatusBarUtil
import com.bytedance.sdk.openadsdk.TTAdNative
import com.kayu.business_car_owner.config_ad.TTAdManagerHolder
import com.bytedance.sdk.openadsdk.AdSlot
import com.bytedance.sdk.openadsdk.TTAdConstant
import com.bytedance.sdk.openadsdk.TTAppDownloadListener
import com.kayu.business_car_owner.SettingInterface
import com.kayu.business_car_owner.JsXiaojuappApi
import com.kayu.business_car_owner.LocalJavascriptInterface
import com.kongzue.dialog.v3.BottomMenu
import com.bytedance.sdk.openadsdk.TTRewardVideoAd
import com.bytedance.sdk.openadsdk.TTAdManager
import com.bytedance.sdk.openadsdk.TTAdNative.RewardVideoAdListener
import com.bytedance.sdk.openadsdk.TTRewardVideoAd.RewardAdInteractionListener
import androidx.core.content.FileProvider
import com.kayu.business_car_owner.R
import com.kayu.business_car_owner.http.*
import com.kayu.utils.*
import com.kongzue.dialog.interfaces.OnDismissListener
import com.kongzue.dialog.interfaces.OnMenuItemClickListener
import java.io.File
import java.lang.Exception
import java.lang.StringBuilder
import java.util.*

class WebViewActivity : BaseActivity() {
    var wvWebView: WebView? = null
    private var url: String? = null
    private var from: String? = null
    private val titleName: String = "加载中..."
    private var title_name: TextView? = null
    var headMap: MutableMap<String, String?> = HashMap()
    private var adID: Long = 0L

    @SuppressLint("HandlerLeak")
    private val jsHandler: Handler = object : Handler() {
        public override fun handleMessage(msg: Message) {
            LogUtil.e("WebViewActivity", "advert----what:" + msg.what + "------arg1:" + msg.arg1)
            if (msg.what == 1) {
                adID = msg.obj as Long
                loadAd(TTAdManagerHolder.videoID)
                //                if (mttRewardVideoAd != null&&mIsLoaded) {
//                    //step6:在获取到广告后展示,强烈建议在onRewardVideoCached回调后，展示广告，提升播放体验
//                    //该方法直接展示广告
////                    mttRewardVideoAd.showRewardVideoAd(RewardVideoActivity.this);
//
//                    //展示广告，并传入广告展示的场景

//                } else {
////                    TToast.show(com.union_test.toutiao.activity.RewardVideoActivity.this, "请先加载广告");
//            mContext.finish();
//                }
            } else if (msg.what == 2) {
                mttRewardVideoAd!!.showRewardVideoAd(
                    this@WebViewActivity,
                    TTAdConstant.RitScenes.CUSTOMIZE_SCENES,
                    "scenes_test"
                )
                mttRewardVideoAd = null
            }
            super.handleMessage(msg)
        }
    }
    private var data //需要用到的加密数据
            : String? = null
    private var channel //加油平台渠道 团油:ty ，淘油宝:tyb 青桔:qj
            : String? = null
    private var gasId: String? = null
    public override fun onConfigurationChanged(newConfig: Configuration) {
        //非默认值
        if (newConfig.fontScale != 1f) {
            getResources()
        }
        super.onConfigurationChanged(newConfig)
    }

    public override fun getResources(): Resources { //还原字体大小
        val res: Resources? = super.getResources()
        //非默认值
        if (res!!.getConfiguration().fontScale != 1f) {
            val newConfig: Configuration = Configuration()
            newConfig.setToDefaults() //设置默认
            res.updateConfiguration(newConfig, res.getDisplayMetrics())
        }
        return (res)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //        StatusBarUtil.setRootViewFitsSystemWindows(this, true);
        //设置状态栏透明
//        StatusBarUtil.setTranslucentStatus(this);
        StatusBarUtil.setStatusBarColor(this, getResources().getColor(R.color.white))
        setContentView(R.layout.activity_webview)
        AndroidBug5497Workaround.Companion.assistActivity(this)
        val webLay: LinearLayout = findViewById(R.id.llWebView)
        if (AppUtil.hasNavBar(this)) {
            val bottom: Int = AppUtil.getNavigationBarHeight(this)
            val lp: LinearLayout.LayoutParams = LinearLayout.LayoutParams(webLay.getLayoutParams())
            lp.setMargins(0, 0, 0, bottom + 80)
            webLay.setLayoutParams(lp)
        }
        val intent: Intent = getIntent()
        url = intent.getStringExtra("url")
        data = intent.getStringExtra("data")
        channel = intent.getStringExtra("channel")
        gasId = intent.getStringExtra("gasId")
        //        titleName = intent.getStringExtra("title");
//        from = intent.getStringExtra("from");
//        title = intent.getStringExtra("title");
        findViewById<View>(R.id.title_back_btu).setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
//                sendOilPayInfo(this@WebViewActivity)
                onBackPressed()
            }

            override fun OnMoreErrorClick() {}
        })
        findViewById<View>(R.id.title_close_btn).setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
//                sendOilPayInfo(this@WebViewActivity)
                finish()
            }

            override fun OnMoreErrorClick() {}
        })
        title_name = findViewById(R.id.title_name_tv)
        title_name?.text = titleName
        if (StringUtil.isEmpty(from)) {
            from = "返回"
        }
        wvWebView = findViewById(R.id.wvWebView)
        //        pbWebView = findViewById(R.id.pbWebView);
//        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
//        StrictMode.setVmPolicy(builder.build());
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
//            builder.detectFileUriExposure();
//        }
//        CookieSyncManager.createInstance(this);
//        CookieSyncManager.getInstance().startSync();
//        CookieManager.getInstance().removeSessionCookie();
        TipGifDialog.show(this, "加载中...", TipGifDialog.TYPE.OTHER, R.drawable.loading_gif)
        initData()
    }

    protected fun initViewsAndEvents() {
        initData()
    }

    fun initData() {
        if (StringUtil.isEmpty(url)) {
            url = URL
        }
        //        url = "https://wallet.xiaoying.com/fe/wallet-landing/blueRegPage/index.html?landId=306&source=100016303";
        val webSettings: WebSettings = wvWebView!!.getSettings()
        webSettings.setJavaScriptEnabled(true)
        webSettings.setBlockNetworkImage(false)
        //支持插件
//        webSettings.setPluginsEnabled(true);

//设置自适应屏幕，两者合用
        webSettings.setUseWideViewPort(true) //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true) // //和setUseWideViewPort(true)一起解决网页自适应问题

//缩放操作
        webSettings.setSupportZoom(true) //支持缩放，默认为true。是下面那个的前提。
        webSettings.setBuiltInZoomControls(true) //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.setDisplayZoomControls(false) //隐藏原生的缩放控件
        webSettings.setDomStorageEnabled(true)
        webSettings.setSupportZoom(true) //支持缩放，默认为true。是下面那个的前提。

        webSettings.builtInZoomControls = true //设置内置的缩放控件。若为false，则该WebView不可缩放

        webSettings.displayZoomControls = false //隐藏原生的缩放控件

        webSettings.setSupportMultipleWindows(false)


        webSettings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL

        val screenDensity = resources.displayMetrics.densityDpi
        var zoomDensity = WebSettings.ZoomDensity.MEDIUM
        when (screenDensity) {
            DisplayMetrics.DENSITY_LOW -> zoomDensity = WebSettings.ZoomDensity.CLOSE
            DisplayMetrics.DENSITY_MEDIUM -> zoomDensity = WebSettings.ZoomDensity.MEDIUM
            DisplayMetrics.DENSITY_HIGH -> zoomDensity = WebSettings.ZoomDensity.FAR
        }
        webSettings.defaultZoom = zoomDensity

//其他细节操作
        webSettings.setAllowFileAccess(true) //设置可以访问文件
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true) //支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true) //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8") //设置编码格式

//        webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.setAppCacheEnabled(true) //是否使用缓存

        //启用数据库
        webSettings.setDatabaseEnabled(true)

//设置定位的数据库路径
        val dir: String = getApplicationContext().getDir("database", MODE_PRIVATE).getPath()
        webSettings.setGeolocationDatabasePath(dir)

//启用地理定位
        webSettings.setGeolocationEnabled(true)
//        webSettings.setSupportMultipleWindows(true)
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT)
        //开启DomStorage缓存
//        LogUtil.e("WebView","UserAgent: "+webSettings.getUserAgentString());

        // android 5.0及以上默认不支持Mixed Content
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW)
        }
        if (!StringUtil.isEmpty(channel)) {
            if ((channel == "tyb")) {
                data?.let { gasId?.let { it1 -> SettingInterface(it, it1) } }
                    ?.let { wvWebView!!.addJavascriptInterface(it, "app") }
            } else if ((channel == "qj")) {
//            wvWebView.addJavascriptInterface(new JsXiaojuappApi(WebViewActivity.this, wvWebView, new Handler()), "xiaojuapp");
                wvWebView!!.addJavascriptInterface(
                    JsXiaojuappApi(this@WebViewActivity, Handler()),
                    "androidMethod"
                )
            }
        } else {
            wvWebView!!.addJavascriptInterface(
                LocalJavascriptInterface(this, jsHandler),
                "androidMethod"
            )
        }
        wvWebView!!.requestFocus()
        wvWebView!!.clearCache(true)
        wvWebView!!.clearHistory()
        /**
         * 当下载文件时打开系统自带的浏览器进行下载，当然也可以对捕获到的 url 进行处理在应用内下载。
         */
//        wvWebView.getSettings().setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:54.0) Gecko/20100101 Firefox/54.0");
        wvWebView!!.setDownloadListener(FileDownLoadListener())
        wvWebView!!.setWebChromeClient(object : WebChromeClient() {
            public override fun onJsPrompt(
                view: WebView,
                url: String,
                message: String,
                defaultValue: String,
                result: JsPromptResult
            ): Boolean {
                return super.onJsPrompt(view, url, message, defaultValue, result)
            }

            public override fun onJsConfirm(
                view: WebView,
                url: String,
                message: String,
                result: JsResult
            ): Boolean {
                val builder: AlertDialog.Builder = AlertDialog.Builder(view.getContext())
                builder.setTitle("提示").setMessage(message)
                    .setPositiveButton("确定", object : DialogInterface.OnClickListener {
                        public override fun onClick(dialog: DialogInterface, which: Int) {
//                                if (!StringUtil.isEmpty(url)){
//                                    Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                    intent.setComponent(new ComponentName("com.android.browser","com.android.browser.BrowserActivity"));
//                                    startActivity(intent);
//                                }
                            result.confirm()
                        }
                    }).setNeutralButton("取消", object : DialogInterface.OnClickListener {
                        public override fun onClick(dialog: DialogInterface, which: Int) {
                            result.cancel()
                        }
                    }).create().show()
                return true
            }

            public override fun onJsAlert(
                view: WebView,
                url: String,
                message: String,
                result: JsResult
            ): Boolean {
                val b2: AlertDialog.Builder = AlertDialog.Builder(this@WebViewActivity)
                    .setTitle("提示")
                    .setMessage(message)
                    .setPositiveButton("确定",
                        object : DialogInterface.OnClickListener {
                            public override fun onClick(dialog: DialogInterface, which: Int) {
                                result.confirm()
                            }
                        }).setNeutralButton("取消", object : DialogInterface.OnClickListener {
                        public override fun onClick(dialog: DialogInterface, which: Int) {
                            result.cancel()
                        }
                    })
                b2.setCancelable(true)
                b2.create()
                b2.show()
                return true
            }

            public override fun onReceivedTitle(view: WebView, title: String) {
                super.onReceivedTitle(view, title)
                title_name!!.setText(titleName)
            }

            public override fun onProgressChanged(view: WebView, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                //                pbWebView.setProgress(newProgress);
                if (newProgress == 100) {
                    TipGifDialog.dismiss()
                }

            }

            public override fun onGeolocationPermissionsShowPrompt(
                origin: String,
                callback: GeolocationPermissions.Callback
            ) {
                callback.invoke(origin, true, false)
                super.onGeolocationPermissionsShowPrompt(origin, callback)
            }

            // For Android 3.0+
            fun openFileChooser(uploadMsg: ValueCallback<Uri?>?) {
                mUploadMessage = uploadMsg
                openImageChooserActivity()
            }

            // For Android 3.0+
            fun openFileChooser(uploadMsg: ValueCallback<*>?, acceptType: String?) {
                mUploadMessage = uploadMsg as ValueCallback<Uri?>?
                openImageChooserActivity()
            }

            // For Android 4.1
            fun openFileChooser(
                uploadMsg: ValueCallback<Uri?>?,
                acceptType: String?,
                capture: String?
            ) {
                mUploadMessage = uploadMsg
                openImageChooserActivity()
            }

            // For Android 5.0+
            public override fun onShowFileChooser(
                webView: WebView,
                filePathCallback: ValueCallback<Array<Uri?>>,
                fileChooserParams: FileChooserParams
            ): Boolean {
                mUploadCallbackAboveL = filePathCallback
                openImageChooserActivity()
                return true
            }
        })
        wvWebView!!.webViewClient = object : WebViewClient() {
            override fun onReceivedHttpError(
                view: WebView,
                request: WebResourceRequest,
                errorResponse: WebResourceResponse
            ) {
                super.onReceivedHttpError(view, request, errorResponse)
                LogUtil.e("webview", "errorResponse=" + errorResponse.toString())
            }

            override fun onReceivedError(
                view: WebView,
                errorCode: Int,
                description: String,
                failingUrl: String
            ) {
                super.onReceivedError(view, errorCode, description, failingUrl)
                LogUtil.e("webview", "description=" + description + "  failingUrl=" + failingUrl)
            }
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                LogUtil.e("WebView", "shouldOverrideUrlLoading: " + url)

    //                view.loadUrl(url);
                if (url.startsWith("http:") || url.startsWith("https:")) {
                    if ((url == HttpConfig.CLOSE_WEB_VIEW)) {
                        onBackPressed()
                        return true
                    } else {
                        headMap.put("Referer", lastOpenUrl)
                        view.loadUrl(url, headMap)
                        lastOpenUrl = url
                        return false
                    }
                } else {
                    try {
                        // 以下固定写法,表示跳转到第三方应用
                        val intent: Intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        startActivity(intent)
                        isDownload = false //该字段是用于判断是否需要跳转浏览器下载
                    } catch (e: Exception) {
    //                        if (url.startsWith("xywallet://")){
    //                            String mUrl = "https://wallet.xiaoying.com/fe/wallet-activity/download/index.html?source=100021313&landId=910#/";
    //                            Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(mUrl));
    //                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    //                            intent.setComponent(new ComponentName("com.android.browser","com.android.browser.BrowserActivity"));
    //                            startActivity(intent);
    //                        }
    //                        String mUrl = "https://wallet.xiaoying.com/fe/wallet-activity/download/index.html?source=100021313&landId=910#/";
    //                        if (!url.startsWith("qihooloan://")){
    //                            Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(lastOpenUrl));
    //                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    //                            intent.setComponent(new ComponentName("com.android.browser","com.android.browser.BrowserActivity"));
    //                            startActivity(intent);
    //                        }
                        // 防止没有安装的情况
                        e.printStackTrace()
                        ToastUtils.show("未安装相应的客户端")
                    }
                    return false
                }
            }

            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                title_name?.text = titleName
                TipGifDialog.show(
                    this@WebViewActivity,
                    "加载中...",
                    TipGifDialog.TYPE.OTHER,
                    R.drawable.loading_gif
                )
                //                pbWebView.setVisibility(View.VISIBLE);
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView, url: String) {
                view.getSettings().setLoadsImagesAutomatically(true)
                //                pbWebView.setVisibility(View.GONE);
    //                LogUtil.e("WebView","onPageFinished-----title:"+view.getTitle());
                title_name!!.postDelayed({ title_name!!.text = view.title }, 1)
//                val cookieManager: CookieManager = CookieManager.getInstance()
//                val CookieStr: String? = cookieManager.getCookie(url)
                lastOpenUrl = url
                LogUtil.e("WebView", "onPageFinished: " + url)
//                LogUtil.e("WebView", "Cookies = " + CookieStr)
                super.onPageFinished(view, url)
            }

            override fun onReceivedSslError(
                view: WebView,
                handler: SslErrorHandler,
                error: SslError
            ) {
                LogUtil.e(
                    "webview",
                    "SslErrorHandler=" + handler.toString() + "  SslError=" + error.toString()
                )
                handler.proceed()
            }
        }
        wvWebView!!.loadUrl((url)!!)
        wvWebView!!.loadUrl( "javascript:window.location.reload( true )" )
    }

    private var lastOpenUrl: String? = null

    /**
     * 是否是支付宝的 url
     *
     * @param checkUrl url
     * @return 结果
     */
    private fun isHaveAliPayLink(checkUrl: String): Boolean {
        return !TextUtils.isEmpty(checkUrl) && (checkUrl.startsWith("alipays:") || checkUrl.startsWith(
            "alipay"
        ))
    }

    private var isDownload: Boolean = true

    internal inner class FileDownLoadListener constructor() : DownloadListener {
        public override fun onDownloadStart(
            url: String,
            userAgent: String,
            contentDisposition: String,
            mimetype: String,
            contentLength: Long
        ) {
            LogUtil.e("WebView", "DownloadListener-->url=" + url)
            LogUtil.e("WebView", "isDownload-->" + isDownload)
            if (isDownload) {
                val intent: Intent = Intent(Intent.ACTION_VIEW)
                val uri: Uri = Uri.parse(url)
                intent.addCategory(Intent.CATEGORY_BROWSABLE)
                intent.setData(uri)
                startActivity(intent)
            }
            isDownload = true //重置为初始状态
        }
    }

    private var cameraFielPath: String? = null
    private val FILE_CHOOSER_RESULT_CODE: Int = 1
    private var mUploadCallbackAboveL: ValueCallback<Array<Uri?>>? = null
    private var mUploadMessage: ValueCallback<Uri?>? = null

    ///选择拍照还是相册
    fun openImageChooserActivity() {
        showCustomDialog()
    }

    //拍照
    private fun takeCamera() {
        val intent: Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraFielPath = Environment.getExternalStorageDirectory()
            .toString() + "//" + System.currentTimeMillis() + ".jpg"
        val outputImage: File = File(cameraFielPath)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { //7.0及以上
            val photoUri: Uri = FileProvider.getUriForFile(
                this, Constants.authority,
                outputImage
            )
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            intent.putExtra("return-data", true)
        } else {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outputImage))
        }
        startActivityForResult(intent, FILE_CAMERA_RESULT_CODE)
    }

    //选择图片
    private fun takePhoto() {
        val i: Intent = Intent(Intent.ACTION_GET_CONTENT)
        i.addCategory(Intent.CATEGORY_OPENABLE)
        i.setType("image/*")
        startActivityForResult(Intent.createChooser(i, "Image Chooser"), FILE_CHOOSER_RESULT_CODE)
    }

    private fun showCustomDialog() {
        BottomMenu.show(
            this@WebViewActivity,
            arrayOf("拍照", "从相册选择"),
            object : OnMenuItemClickListener {
                public override fun onClick(text: String, index: Int) {
                    if (index == 0) {
                        // 2018/12/10 拍照
//                    requestCode = FILE_CAMERA_RESULT_CODE;
                        takeCamera()
                    } else if (index == 1) {
//                    requestCode = FILE_CHOOSER_RESULT_CODE;
                        // 2018/12/10 从相册选择
                        takePhoto()
                    } else {
//                    mUploadCallbackAboveL = null;
//                    mUploadMessage = null;
                    }
                }
            }).setOnDismissListener(object : OnDismissListener {
            public override fun onDismiss() {
                if (mUploadCallbackAboveL != null) {
                    mUploadCallbackAboveL!!.onReceiveValue(null)
                    mUploadCallbackAboveL = null
                }
                if (mUploadMessage != null) {
                    mUploadMessage!!.onReceiveValue(null)
                    mUploadMessage = null
                }
            }
        })
    }

    //    private int requestCode = -2;
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (null == mUploadMessage && null == mUploadCallbackAboveL) return
        if (resultCode != RESULT_OK) { //同上所说需要回调onReceiveValue方法防止下次无法响应js方法
            if (mUploadCallbackAboveL != null) {
                mUploadCallbackAboveL!!.onReceiveValue(null)
                mUploadCallbackAboveL = null
            }
            if (mUploadMessage != null) {
                mUploadMessage!!.onReceiveValue(null)
                mUploadMessage = null
            }
            return
        }
        var result: Uri? = null
        if (requestCode == FILE_CAMERA_RESULT_CODE) {
            if (null != data && null != data.getData()) {
                result = data.getData()
            }
            if (result == null && hasFile(cameraFielPath)) {
                result = Uri.fromFile(File(cameraFielPath))
            }
            if (mUploadCallbackAboveL != null) {
                mUploadCallbackAboveL!!.onReceiveValue(arrayOf(result))
                mUploadCallbackAboveL = null
            } else if (mUploadMessage != null) {
                mUploadMessage!!.onReceiveValue(result)
                mUploadMessage = null
            }
        } else if (requestCode == FILE_CHOOSER_RESULT_CODE) {
            if (data != null) {
                result = data.getData()
            }
            if (mUploadCallbackAboveL != null) {
                onActivityResultAboveL(requestCode, resultCode, data)
            } else if (mUploadMessage != null) {
                mUploadMessage!!.onReceiveValue(result)
                mUploadMessage = null
            }
        }
    }

    /**
     * 判断文件是否存在
     */
    fun hasFile(path: String?): Boolean {
        try {
            val f: File = File(path)
            if (!f.exists()) {
                return false
            }
        } catch (e: Exception) {
            Log.i("error", e.toString())
            return false
        }
        return true
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun onActivityResultAboveL(requestCode: Int, resultCode: Int, intent: Intent?) {
        if ((requestCode != FILE_CAMERA_RESULT_CODE && requestCode != FILE_CHOOSER_RESULT_CODE
                    || mUploadCallbackAboveL == null)
        ) {
            return
        }
        var results: Array<Uri?>? = null
        if (intent != null) {
            val dataString: String? = intent.dataString
            val clipData: ClipData? = intent.clipData
            if (clipData != null) {
                results = arrayOfNulls(clipData.itemCount)
                for (i in 0 until clipData.itemCount) {
                    val item: ClipData.Item = clipData.getItemAt(i)
                    results[i] = item.uri
                }
            }
            if (dataString != null) results = arrayOf(Uri.parse(dataString))
        }
        mUploadCallbackAboveL!!.onReceiveValue(results)
        mUploadCallbackAboveL = null
    }

    //系统自带监听方法
    public override fun onBackPressed() {
        if (wvWebView!!.canGoBack()) {
            wvWebView!!.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE)
            wvWebView!!.goBack()
            return
        } else {
            finish()
        }
        super.onBackPressed()
    }

    //类相关监听
    override fun onPause() {
        super.onPause()
        wvWebView!!.onPause()
    }

    override fun onResume() {
        wvWebView!!.onResume()
        super.onResume()
    }

    override fun onDestroy() {
//        if (wvWebView != null) {
//            wvWebView!!.destroy()
//
//
//            wvWebView = null
//        }
        if (wvWebView != null) {
            // 要首先移除webview
//            removeView(webView);

            // 清理缓存
            wvWebView?.stopLoading()
            wvWebView?.onPause()
            wvWebView?.clearHistory()
            wvWebView?.clearCache(true)
            wvWebView?.clearFormData()
            wvWebView?.clearSslPreferences()
            WebStorage.getInstance().deleteAllData()
            wvWebView?.destroyDrawingCache()
            wvWebView?.removeAllViews()

            // 最后再去webView.destroy();
            wvWebView?.destroy()
            wvWebView = null
        }

        // 清理cookie
        val cookieManager: CookieManager = CookieManager.getInstance()
        cookieManager.removeAllCookies {  }
        super.onDestroy()
    }

    private var mHasShowDownloadActive: Boolean = false
    private var mTTAdNative: TTAdNative? = null
    private var mttRewardVideoAd: TTRewardVideoAd? = null
    private val mIsExpress: Boolean = false //是否请求模板广告
    private var mIsLoaded: Boolean = true //视频是否加载完成
    private fun loadAd(codeId: String) {
        //step1:初始化sdk
        val ttAdManager: TTAdManager = TTAdManagerHolder.get()
        //step2:(可选，强烈建议在合适的时机调用):申请部分权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题。
        TTAdManagerHolder.get().requestPermissionIfNecessary(this)
        //step3:创建TTAdNative对象,用于调用广告请求接口
        mTTAdNative = ttAdManager.createAdNative(this)
        //        getExtraInfo();
//        initClickEvent();

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
                LogUtil.e(TAG, "WebViewActivity --> onError: " + code + ", " + message.toString())
                TipGifDialog.dismiss()
                //                TToast.show(com.union_test.toutiao.activity.RewardVideoActivity.this, message);
            }

            //视频广告加载后，视频资源缓存到本地的回调，在此回调后，播放本地视频，流畅不阻塞。
            public override fun onRewardVideoCached() {
                LogUtil.e(TAG, "Callback --> onRewardVideoCached")
                mIsLoaded = true
                jsHandler.sendMessage(jsHandler.obtainMessage(2))
                //                TToast.show(com.union_test.toutiao.activity.RewardVideoActivity.this, "Callback --> rewardVideoAd video cached");
            }

            public override fun onRewardVideoCached(ttRewardVideoAd: TTRewardVideoAd) {}

            //视频广告的素材加载完毕，比如视频url等，在此回调后，可以播放在线视频，网络不好可能出现加载缓冲，影响体验。
            public override fun onRewardVideoAdLoad(ad: TTRewardVideoAd) {
                LogUtil.e(TAG, "Callback --> onRewardVideoAdLoad")

//                TToast.show(com.union_test.toutiao.activity.RewardVideoActivity.this, "rewardVideoAd loaded 广告类型：" + getAdType(ad.getRewardVideoAdType()));
                mIsLoaded = false
                mttRewardVideoAd = ad
                mttRewardVideoAd!!.setRewardAdInteractionListener(object :
                    RewardAdInteractionListener {
                    public override fun onAdShow() {
                        LogUtil.d(TAG, "Callback --> rewardVideoAd show")
                        //                        TToast.show(com.union_test.toutiao.activity.RewardVideoActivity.this, "rewardVideoAd show");
                    }

                    public override fun onAdVideoBarClick() {
                        LogUtil.d(TAG, "Callback --> rewardVideoAd bar click")
                        //                        TToast.show(com.union_test.toutiao.activity.RewardVideoActivity.this, "rewardVideoAd bar click");
                    }

                    public override fun onAdClose() {
                        Log.d(TAG, "Callback --> rewardVideoAd close")
                        //                        TToast.show(com.union_test.toutiao.activity.RewardVideoActivity.this, "rewardVideoAd close");
                    }

                    //视频播放完成回调
                    public override fun onVideoComplete() {
                        LogUtil.d(TAG, "Callback --> rewardVideoAd complete")
                        //                        TToast.show(com.union_test.toutiao.activity.RewardVideoActivity.this, "rewardVideoAd complete");
                    }

                    public override fun onVideoError() {
                        LogUtil.e(TAG, "Callback --> rewardVideoAd error")
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
                        LogUtil.e(TAG, "Callback --> " + logString)
                        //                        TToast.show(com.union_test.toutiao.activity.RewardVideoActivity.this, logString);
                        if (adID == 0L) return
                        if (rewardVerify) sendADComplete(adID)
                    }

                    public override fun onSkippedVideo() {
                        LogUtil.e(TAG, "Callback --> rewardVideoAd has onSkippedVideo")
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
                        LogUtil.d(
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
                        LogUtil.d(
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
                        LogUtil.d(
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
                        LogUtil.d(
                            "DML",
                            "onDownloadFinished==totalBytes=" + totalBytes + ",fileName=" + fileName + ",appName=" + appName
                        )
                        //                        TToast.show(com.union_test.toutiao.activity.RewardVideoActivity.this, "下载完成，点击下载区域重新下载", Toast.LENGTH_LONG);
                    }

                    public override fun onInstalled(fileName: String, appName: String) {
                        LogUtil.d(
                            "DML",
                            "onInstalled==" + ",fileName=" + fileName + ",appName=" + appName
                        )
                        //                        TToast.show(com.union_test.toutiao.activity.RewardVideoActivity.this, "安装完成，点击下载区域打开", Toast.LENGTH_LONG);
                    }
                })
            }
        })
    }

    @SuppressLint("HandlerLeak")
    private fun sendADComplete(mfid: Long) {

        //生成requestId
        val requestId: String = UUID.randomUUID().toString().replace("-".toRegex(), "")
        //秘钥key
        val keyBuilder: StringBuilder = StringBuilder()
        //占位符
        val seatBuilder: StringBuilder = StringBuilder()
        val random: Random = Random()
        for (i in 0..7) {
            //随机索引 <= 9
            val index: Int = random.nextInt(10)
            //记录索引
            seatBuilder.append(index)
            //记录索引对应的key
            keyBuilder.append(requestId.get(index))
        }
        val map: MutableMap<String, Any> = HashMap()
        map.put("mfId", mfid)
        map.put("timeMillis", System.currentTimeMillis())
        val reqDateMap: HashMap<String, Any> = HashMap()
        try {
            val seats: String = seatBuilder.toString()
            val key: String = keyBuilder.toString()
            //            JSONObject jsonObject = new JSONObject(map);

//            String data = DesCoderUtil.encryptDES(jsonObject.toString(),key);
            val data: String = DesCoderUtil.encryptDES(GsonHelper.toJsonString(map), key)
            reqDateMap.put("seats", seats)
            reqDateMap.put("data", data)
            reqDateMap.put("requestId", requestId)
            LogUtil.e("key", key)
            LogUtil.e("requestId", requestId)
            LogUtil.e("data", data)
        } catch (e: Exception) {
        }
        val reqInfo: RequestInfo = RequestInfo()
        reqInfo.context = this@WebViewActivity
        reqInfo.reqUrl = HttpConfig.HOST + HttpConfig.INTERFACE_AD_COMPLETE
        reqInfo.parser = NormalParse()
        reqInfo.reqDataMap = reqDateMap
        reqInfo.handler = object : Handler() {
            public override fun handleMessage(msg: Message) {
                val resInfo: ResponseInfo = msg.obj as ResponseInfo
                if (resInfo.status == 1) {
//                    ToastUtils.show("奖励发放成功");
                } else {
//                    ToastUtils.show(resInfo.msg);
                }
                super.handleMessage(msg)
            }
        }
        val callback: ResponseCallback = ResponseCallback(reqInfo)
        ReqUtil.setReqInfo(reqInfo)
        ReqUtil.requestPostJSON(callback)
    }

//    @SuppressLint("HandlerLeak")
//    fun sendOilPayInfo(context: Context?) {
//        val reques: RequestInfo = RequestInfo()
//        reques.context = context
//        reques.reqUrl = HttpConfig.HOST + HttpConfig.INTERFACE_GAS_NOTIFIED
//        val reqDateMap: HashMap<String, Any> = HashMap()
//        reques.reqDataMap = reqDateMap
//        reques.parser = NormalIntParse()
//        reques.handler = object : Handler() {
//            public override fun handleMessage(msg: Message) {
////                ResponseInfo resInfo = (ResponseInfo) msg.obj;
//                super.handleMessage(msg)
//            }
//        }
//        val callback: ResponseCallback = ResponseCallback(reques)
//        ReqUtil.setReqInfo(reques)
//        ReqUtil.requestPostJSON(callback)
//    }

    companion object {
        //    ProgressBar pbWebView;
        val URL: String = "https://www.baidu.com"
        private val TAG: String = "RewardVideoActivity"
        private val FILE_CAMERA_RESULT_CODE: Int = 0
    }
}

