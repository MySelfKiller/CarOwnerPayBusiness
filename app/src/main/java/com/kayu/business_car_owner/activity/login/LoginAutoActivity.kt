package com.kayu.business_car_owner.activity.login

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ForegroundColorSpan
import android.view.*
import android.webkit.CookieManager
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.ViewModelProviders
import com.hjq.toast.ToastUtils
import com.kongzue.dialog.v3.TipGifDialog
import com.kayu.business_car_owner.model.SystemParam
import com.kongzue.dialog.v3.AgreementDialog
import com.kayu.business_car_owner.KWApplication
import com.kongzue.dialog.interfaces.OnDialogButtonClickListener
import com.kongzue.dialog.util.BaseDialog
import com.kayu.business_car_owner.http.parser.LoginDataParse
import com.kayu.business_car_owner.model.LoginInfo
import com.kayu.business_car_owner.wxapi.WXShare
import com.kayu.utils.status_bar_set.StatusBarUtil
import com.kayu.business_car_owner.ui.text_link.UrlClickableSpan
import androidx.lifecycle.Observer
import com.kayu.business_car_owner.R
import com.kayu.business_car_owner.activity.*
import com.kayu.business_car_owner.http.*
import com.kayu.utils.*
import java.lang.StringBuilder
import java.util.HashMap

class LoginAutoActivity : BaseActivity() {
    //    private EditText phone_number;
    //    private EditText sms_code;
    private var ask_btn: AppCompatButton? = null
    private var activation_btn: AppCompatButton? = null
    private var mViewModel: MainViewModel? = null
    private var user_agreement: TextView? = null
    private var order_list_tv: TextView? = null
    private var sp: SharedPreferences? = null
    private var isFirstShow: Boolean = false
    var titles //协议标题
            : Array<String>? = null
    var urls //协议连接
            : Array<String>? = null
    private var wxShare: WXShare? = null
    private var auto_progress: LinearLayout? = null
    private var bg_img: ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtil.setStatusBarColor(this, getResources().getColor(R.color.transparent))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        }
        setContentView(R.layout.activity_login_new)
        //登录的时候先清楚一下cookie缓存
        val cookieManager: CookieManager = CookieManager.getInstance()
        cookieManager.removeAllCookie()
        sp = getSharedPreferences(Constants.SharedPreferences_name, MODE_PRIVATE)
        mViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        ask_btn = findViewById(R.id.login_auto_btn)
        bg_img = findViewById(R.id.login_auto_bg)
        activation_btn = findViewById(R.id.login_activation_btn)
        order_list_tv = findViewById(R.id.login_order_list_tv)
        auto_progress = findViewById(R.id.login_auto_progress)
        auto_progress?.setClickable(false)
        auto_progress?.setFocusable(false)
        order_list_tv?.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                val intent: Intent =
                    Intent(this@LoginAutoActivity, OrderTrackingActivity::class.java)
                startActivity(intent)
            }

            override fun OnMoreErrorClick() {}
        })
        activation_btn?.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                val intent: Intent = Intent(this@LoginAutoActivity, ActivationActivity::class.java)
                startActivity(intent)
            }

            override fun OnMoreErrorClick() {}
        })
        ask_btn?.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
//                permissionsCheck()
                jumpDialog("一键登录验证失败")
            }

            override fun OnMoreErrorClick() {}
        })
        user_agreement = findViewById(R.id.login_user_agreement_tv)
        //        user_privacy = findViewById(R.id.login_user_privacy_tv);
        TipGifDialog.show(this, "请稍等...", TipGifDialog.TYPE.OTHER, R.drawable.loading_gif)
        mViewModel!!.getParameter(this, 3).observe(this, object : Observer<SystemParam?> {
             override fun onChanged(systemParam: SystemParam?) {
                TipGifDialog.dismiss()
                if (null != systemParam && systemParam.type == 3) {
                    if (!StringUtil.isEmpty(systemParam.content)) {
                        systemParam.content?.let { KWApplication.instance.loadImg(it, bg_img!!) }
                    }
                    titles = systemParam.title.split("@@".toRegex()).toTypedArray()
                    urls = systemParam.url.split("@@".toRegex()).toTypedArray()
                    if (titles!!.size != 2 || urls!!.size != 2) return
                    isFirstShow = sp!!.getBoolean(Constants.isShowDialog, true)
                    if (isFirstShow) {
//                        String menss = "请您务必谨慎阅读、充分理解\"" + titles[0] + "\"与\"" + titles[1] + "\"各条款，包括但不限于：为了向你提供及时通讯，内容分享等服务，我们需要收集你的定位信息，操作日志信息" +
//                                "等。你可以在\"设置\"中查看、变更、删除个人信息并管理你的授权。" +
//                                "<br>你可阅读<font color=\"#007aff\"><a href=\"" + urls[0] + "\" style=\"text-decoration:none;\">《" + titles[0] + "》</a></font>与<font color=\"#007aff\"><a href=\"" + urls[1] + "\" style=\"text-decoration:none;\">《" + titles[1] + "》</a></font>了解详细信息。" +
//                                "如您同意，请点击确定接收我们的服务";
                        AgreementDialog.show(
                            this@LoginAutoActivity,
                            titles!![1] + "与" + titles!![0],
                            KWApplication.instance.getClickableSpan(this@LoginAutoActivity, titles!!, urls!!),
                            "同意并继续",
                            "拒绝并退出"
                        )
                            .setCancelable(false).setOkButton(object : OnDialogButtonClickListener {
                                public override fun onClick(
                                    baseDialog: BaseDialog,
                                    v: View
                                ): Boolean {
                                    baseDialog.doDismiss()
                                    isFirstShow = false
                                    val editor: SharedPreferences.Editor = sp!!.edit()
                                    editor.putBoolean(Constants.isShowDialog, isFirstShow)
                                    editor.apply()
                                    editor.commit()
                                    return false
                                }
                            }).setCancelButton(object : OnDialogButtonClickListener {
                                public override fun onClick(
                                    baseDialog: BaseDialog,
                                    v: View
                                ): Boolean {
                                    baseDialog.doDismiss()
                                    finish()
                                    return false
                                }
                            })
                    }
                    val stringBuilder: StringBuilder = StringBuilder()
                    val title1Index: Int = stringBuilder.length
                    stringBuilder.append(titles!![1])
                    val title1End: Int = stringBuilder.length
                    stringBuilder.append("与")
                    val title2Index: Int = stringBuilder.length
                    stringBuilder.append(titles!!.get(0))
                    val title2End: Int = stringBuilder.length
                    val spannableString: SpannableString = SpannableString(stringBuilder.toString())
                    //设置下划线文字
//                    spannableString.setSpan(new NoUnderlineSpan(), title1Index, title1End, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    //设置文字的单击事件
                    spannableString.setSpan(
                        UrlClickableSpan(this@LoginAutoActivity, urls!![1]),
                        title1Index,
                        title1End,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    //设置文字的前景色
                    spannableString.setSpan(
                        ForegroundColorSpan(getResources().getColor(R.color.select_text_color)),
                        title1Index,
                        title1End,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )

                    //设置下划线文字
//                    spannableString.setSpan(new NoUnderlineSpan(), title2Index, title2End, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    //设置文字的单击事件
                    spannableString.setSpan(
                        UrlClickableSpan(this@LoginAutoActivity, urls!!.get(0)),
                        title2Index,
                        title2End,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    //设置文字的前景色
                    spannableString.setSpan(
                        ForegroundColorSpan(getResources().getColor(R.color.select_text_color)),
                        title2Index,
                        title2End,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    user_agreement?.setMovementMethod(LinkMovementMethod.getInstance())
                    user_agreement?.setText(spannableString)
                    //                    user_privacy.setText(titles[1]);
                    user_agreement?.setOnClickListener(object : NoMoreClickListener() {
                        override fun OnMoreClick(view: View) {
                            jumpWeb("服务条款", urls!!.get(0))
                        }

                        override fun OnMoreErrorClick() {}
                    })
                    //                    user_privacy.setOnClickListener(new NoMoreClickListener() {
//                        @Override
//                        protected void OnMoreClick(View view) {
//                            jumpWeb(titles[1],urls[1]);
//                        }
//
//                        @Override
//                        protected void OnMoreErrorClick() {
//
//                        }
//                    });
                }
            }
        })
    }

    private fun jumpWeb(title: String, url: String) {
        val intent: Intent = Intent(this@LoginAutoActivity, WebViewActivity::class.java)
        intent.putExtra("url", url)
        intent.putExtra("from", title)
        startActivity(intent)
    }

    @SuppressLint("HandlerLeak")
    private fun sendSubRequest(loginToken: String) {
        TipGifDialog.show(this, "请稍等...", TipGifDialog.TYPE.OTHER, R.drawable.loading_gif)
        val reqInfo: RequestInfo = RequestInfo()
        reqInfo.context = this@LoginAutoActivity
        reqInfo.reqUrl = HttpConfig.HOST + HttpConfig.INTERFACE_LOGIN
        reqInfo.parser = LoginDataParse()
        val reqDateMap: HashMap<String, Any> = HashMap()
        reqDateMap.put("loginToken", loginToken)
        val imei: String? = KWApplication.instance.oidImei
        reqDateMap["imei"]= imei?:""
        //        reqDateMap.put("password",sms_code.getText().toString().trim());
        reqInfo.reqDataMap = reqDateMap
        reqInfo.handler = object : Handler() {
            public override fun handleMessage(msg: Message) {
                TipGifDialog.dismiss()
                val resInfo: ResponseInfo = msg.obj as ResponseInfo
                if (resInfo.status == 1) {
                    val user: LoginInfo? = resInfo.responseData as LoginInfo?
                    if (null != user) {
                        if (StringUtil.isEmpty(user.lastLoginTime)) {
                            auto_progress!!.setVisibility(View.VISIBLE)
                            auto_progress!!.postDelayed(object : Runnable {
                                public override fun run() {
                                    auto_progress!!.setVisibility(View.GONE)
                                    saveLogin(user)
                                }
                            }, (1000 * 2).toLong())
                        } else {
                            saveLogin(user)
                        }
                    }
                } else {
                    ToastUtils.show(resInfo.msg)
                }
                super.handleMessage(msg)
            }
        }
        val callback: ResponseCallback = ResponseCallback(reqInfo)
        ReqUtil.setReqInfo(reqInfo)
        ReqUtil.requestPostJSON(callback)
    }

    private fun saveLogin(user: LoginInfo) {
        val editor: SharedPreferences.Editor = sp!!.edit()
        editor.putBoolean(Constants.isLogin, true)
        editor.putString(Constants.token, user.token)
        editor.putBoolean(Constants.isSetPsd, true)
        editor.putString(Constants.login_info, GsonHelper.toJsonString(user))
        editor.apply()
        editor.commit()
        KWApplication.instance.token = user.token
        AppManager.appManager?.finishAllActivity()
        startActivity(Intent(this@LoginAutoActivity, MainActivity::class.java))
        finish()
    }

//    fun permissionsCheck() {
////        String[] perms = {Manifest.permission.CAMERA};
//        val perms: Array<String> = arrayOf(Manifest.permission.READ_PHONE_STATE)
//        performCodeWithPermission(
//            1,
//            Constants.RC_PERMISSION_PERMISSION_FRAGMENT,
//            perms,
//            object : PermissionCallback {
//                 override fun hasPermission(allPerms: List<Array<String>>) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                        val helper: OaidHelper = OaidHelper(object : AppIdsUpdater {
//                            override fun OnIdsAvalid(
//                                isSupport: Boolean,
//                                oaid: String,
//                                vaid: String,
//                                aaid: String
//                            ) {
//                                if (!isSupport || StringUtil.isEmpty(oaid)) {
//                                    return
//                                }
//                                if (!oaid.startsWith("0000")) {
//                                    KWApplication.instance.oid = oaid
//                                }
//                            }
//
//                        })
//                        try { //fixme 获取不到是的时候 需要查明
//                            helper.getDeviceIds(this@LoginAutoActivity)
//                        } catch (e: Exception) {
//                            e.printStackTrace()
//                        }
//                    }
//                    // 检查当前是否初始化成功极光 SDK
//                    if (JVerificationInterface.isInitSuccess()) {
//                        // 判断当前的手机网络环境是否可以使用认证。
//                        if (!JVerificationInterface.checkVerifyEnable(this@LoginAutoActivity)) {
//                            jumpDialog("一键登录验证失败")
//                            return
//                        }
//                        TipGifDialog.show(
//                            this@LoginAutoActivity,
//                            "请稍等...",
//                            TipGifDialog.TYPE.OTHER,
//                            R.drawable.loading_gif
//                        )
//                        JVerificationInterface.setCustomUIWithConfig(fullScreenPortraitConfig)
//                        JVerificationInterface.loginAuth(
//                            this@LoginAutoActivity,
//                            true,
//                            object : VerifyListener {
//                                public override fun onResult(
//                                    code: Int,
//                                    content: String,
//                                    operator: String
//                                ) {
//                                    TipGifDialog.dismiss()
//                                    LogUtil.e(
//                                        "JPush",
//                                        "code=" + code + ", token=" + content + " ,operator=" + operator
//                                    )
//                                    if (code == 6000) {
////                                JVerificationInterface.dismissLoginAuthActivity();
//                                        sendSubRequest(content)
//                                    } else if (code == 6002) {
//                                        //取消登录
//                                    } else {
//                                        jumpDialog("一键登录验证失败")
//                                    }
//                                }
//                            },
//                            object : AuthPageEventListener() {
//                                public override fun onEvent(i: Int, s: String) {
//                                    LogUtil.e("JPush", "onEvent---code=" + i + ", msg=" + s)
//                                }
//                            })
//                    } else {
//                        jumpDialog("一键登录验证失败")
//                        //                    ToastUtils.show("尚未初始化成功～！");
//                    }
//                }
//
//                public override fun noPermission(
//                    deniedPerms: List<String>?,
//                    grantedPerms: List<String?>?,
//                    hasPermanentlyDenied: Boolean?
//                ) {
////                EasyPermissions.goSettingsPermissions(LoginAutoActivity.this, 1, Constants.RC_PERMISSION_PERMISSION_FRAGMENT, Constants.RC_PERMISSION_BASE);
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                        val helper: OaidHelper = OaidHelper(object : AppIdsUpdater {
//                            public override fun OnIdsAvalid(
//                                isSupport: Boolean,
//                                oaid: String,
//                                vaid: String,
//                                aaid: String
//                            ) {
//                                if (!isSupport || StringUtil.isEmpty(oaid)) {
//                                    return
//                                }
//                                if (!oaid.startsWith("0000")) {
//                                    KWApplication.instance.oid = oaid
//                                }
//                            }
//                        })
//                        try { //fixme 获取不到是的时候 需要查明
//                            helper.getDeviceIds(this@LoginAutoActivity)
//                        } catch (e: Exception) {
//                            e.printStackTrace()
//                        }
//                    }
//                    // 检查当前是否初始化成功极光 SDK
//                    if (JVerificationInterface.isInitSuccess()) {
//                        // 判断当前的手机网络环境是否可以使用认证。
//                        if (!JVerificationInterface.checkVerifyEnable(this@LoginAutoActivity)) {
//                            jumpDialog("一键登录验证失败")
//                            return
//                        }
//                        TipGifDialog.show(
//                            this@LoginAutoActivity,
//                            "请稍等...",
//                            TipGifDialog.TYPE.OTHER,
//                            R.drawable.loading_gif
//                        )
//                        JVerificationInterface.setCustomUIWithConfig(fullScreenPortraitConfig)
//                        JVerificationInterface.loginAuth(
//                            this@LoginAutoActivity,
//                            true,
//                            object : VerifyListener {
//                                public override fun onResult(
//                                    code: Int,
//                                    content: String,
//                                    operator: String
//                                ) {
//                                    TipGifDialog.dismiss()
//                                    LogUtil.e(
//                                        "JPush",
//                                        "code=" + code + ", token=" + content + " ,operator=" + operator
//                                    )
//                                    if (code == 6000) {
////                                JVerificationInterface.dismissLoginAuthActivity();
//                                        sendSubRequest(content)
//                                    } else if (code == 6002) {
//                                        //取消登录
//                                    } else {
//                                        jumpDialog("一键登录验证失败")
//                                    }
//                                }
//                            },
//                            object : AuthPageEventListener() {
//                                public override fun onEvent(i: Int, s: String) {
//                                    LogUtil.e("JPush", "onEvent---code=" + i + ", msg=" + s)
//                                    if (i == 6) { //选中隐私复选框
//                                    } else if (i == 7) { //未选中隐私复选框
//                                    }
//                                }
//                            })
//                    } else {
//                        jumpDialog("一键登录验证失败")
//                        //                    ToastUtils.show("尚未初始化成功～！");
//                    }
//                }
//
//                public override fun showDialog(dialogType: Int, callback: DialogCallback) {
//                    val dialog: MessageDialog =
//                        MessageDialog.build((this@LoginAutoActivity as AppCompatActivity?)!!)
//                    dialog.setTitle("需要获取以下权限")
//                    dialog.setMessage(getString(R.string.permiss_read_phone))
//                    dialog.setOkButton("下一步", object : OnDialogButtonClickListener {
//                        public override fun onClick(baseDialog: BaseDialog, v: View): Boolean {
//                            callback.onGranted()
//                            return false
//                        }
//                    })
//                    dialog.setCancelable(false)
//                    dialog.show()
//                }
//            })
//    }

    private fun jumpDialog(msg: String) {
//        MessageDialog.show(LoginAutoActivity.this,"提示",msg+"，是否需要使用其他手机号验证登录？","是","否").setCancelable(false)
//                .setOkButton(new OnDialogButtonClickListener() {
//                    @Override
//                    public boolean onClick(BaseDialog baseDialog, View v) {
//                        Intent intent = new Intent(LoginAutoActivity.this, LoginActivity.class);
//                        startActivity(intent);
//                        finish();
//                        return true;
//                    }
//                });
        val intent: Intent = Intent(this@LoginAutoActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    @SuppressLint("HandlerLeak")
    private fun reqSignIn(code: String?) {
        if (null == code || StringUtil.isEmpty(code)) {
            return
        }
        TipGifDialog.show(
            this@LoginAutoActivity,
            "请稍等...",
            TipGifDialog.TYPE.OTHER,
            R.drawable.loading_gif
        )
        val reqInfo: RequestInfo = RequestInfo()
        reqInfo.context = this@LoginAutoActivity
        reqInfo.reqUrl = HttpConfig.HOST + HttpConfig.INTERFACE_LOGIN
        reqInfo.parser = LoginDataParse()
        val reqDateMap: HashMap<String, Any> = HashMap()
        reqDateMap.put("wxCode", code)
        val imei: String? = KWApplication.instance.oidImei
        reqDateMap["imei"]= imei?:""
//        reqDateMap.put("code",sms_code.getText().toString().trim());
        reqInfo.reqDataMap = reqDateMap
        reqInfo.handler = object : Handler() {
            public override fun handleMessage(msg: Message) {
                TipGifDialog.dismiss()
                val resInfo: ResponseInfo = msg.obj as ResponseInfo
                if (resInfo.status == 1) {
                    val user: LoginInfo? = resInfo.responseData as LoginInfo?
                    if (null != user) {
                        if (StringUtil.isEmpty(user.lastLoginTime)) {
                            auto_progress!!.setVisibility(View.VISIBLE)
                            auto_progress!!.postDelayed(object : Runnable {
                                public override fun run() {
                                    auto_progress!!.setVisibility(View.GONE)
                                    saveLogin(user)
                                }
                            }, (1000 * 2).toLong())
                        } else {
                            saveLogin(user)
                        }
                    }
                } else {
                    ToastUtils.show(resInfo.msg)
                }
                super.handleMessage(msg)
            }
        }
        val callback: ResponseCallback = ResponseCallback(reqInfo)
        ReqUtil.setReqInfo(reqInfo)
        ReqUtil.requestPostJSON(callback)
    }

    override fun onDestroy() {
        if (null != wxShare) wxShare!!.unregister()
        super.onDestroy()
    }

    //记录用户首次点击返回键的时间
    private var firstTime: Long = 0
    public override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            val secondTime: Long = System.currentTimeMillis()
            if (secondTime - firstTime > 2000) {
                ToastUtils.show("再按一次退出应用")
                firstTime = secondTime
                return true
            } else {
                System.exit(0)
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun toNativeVerifyActivity() {
        val intent: Intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    //        uiConfigBuilder.setPrivacyNavColor(getResources().getColor(R.color.white));
    //        uiConfigBuilder.setAppPrivacyOne(titles[0], urls[0]);
//        uiConfigBuilder.setAppPrivacyTwo(titles[1], urls[1]);
    //                            .setLogoImgPath("logo_cm")
    //        PrivacyBean privacy1 = new PrivacyBean("用户协议","https://www.ky808.cn/carfriend/static/user_agree.html","和《","》、");
//        PrivacyBean privacy2 = new PrivacyBean("隐私政策","https://www.ky808.cn/carfriend/static/privacy_agree.html","《","》");
//    private val fullScreenPortraitConfig: JVerifyUIConfig
//        private get() {
//            val uiConfigBuilder: JVerifyUIConfig.Builder = JVerifyUIConfig.Builder()
//            uiConfigBuilder.setStatusBarDarkMode(false)
//            uiConfigBuilder.setNavColor(getResources().getColor(R.color.white))
//            uiConfigBuilder.setNavText("登录")
//            uiConfigBuilder.setNavTextSize(20)
//            //        uiConfigBuilder.setPrivacyNavColor(getResources().getColor(R.color.white));
//            uiConfigBuilder.setNavTextColor(getResources().getColor(R.color.black1))
//            uiConfigBuilder.setNavReturnImgPath("normal_btu_black")
//            uiConfigBuilder.setNavReturnBtnOffsetX(20)
//            uiConfigBuilder.setLogoImgPath("ic_login_bg")
//            uiConfigBuilder.setLogoWidth(80)
//            uiConfigBuilder.setLogoHeight(60)
//            uiConfigBuilder.setLogoHidden(false)
//            uiConfigBuilder.setNumberColor(getResources().getColor(R.color.black1))
//            uiConfigBuilder.setLogBtnText("一键登录")
//            uiConfigBuilder.setLogBtnTextSize(16)
//            uiConfigBuilder.setLogBtnHeight(40)
//            uiConfigBuilder.setLogBtnTextColor(getResources().getColor(R.color.select_text_color))
//            uiConfigBuilder.setLogBtnImgPath("ic_login_btn_bg")
//            //        uiConfigBuilder.setAppPrivacyOne(titles[0], urls[0]);
////        uiConfigBuilder.setAppPrivacyTwo(titles[1], urls[1]);
//            uiConfigBuilder.setAppPrivacyColor(
//                getResources().getColor(R.color.grayText4), getResources().getColor(
//                    R.color.endColor_btn
//                )
//            )
//            uiConfigBuilder.setPrivacyState(true)
//            uiConfigBuilder.setSloganTextColor(getResources().getColor(R.color.grayText2))
//            uiConfigBuilder.setSloganTextSize(12)
//            uiConfigBuilder.setLogoOffsetY(100)
//            //                            .setLogoImgPath("logo_cm")
//            uiConfigBuilder.setNumFieldOffsetY(190)
//            uiConfigBuilder.setSloganOffsetY(235)
//            uiConfigBuilder.setLogBtnOffsetY(260)
//            uiConfigBuilder.setNumberSize(22)
//            uiConfigBuilder.setPrivacyTextCenterGravity(false)
//            uiConfigBuilder.setPrivacyState(false)
//            uiConfigBuilder.setPrivacyTextSize(12)
//            uiConfigBuilder.setPrivacyCheckboxHidden(false)
//            uiConfigBuilder.setCheckedImgPath("ic_check_box_24dp")
//            uiConfigBuilder.setUncheckedImgPath("ic_uncheck_box_24dp")
//            uiConfigBuilder.setPrivacyCheckboxSize(20)
//            uiConfigBuilder.setPrivacyWithBookTitleMark(true)
//            val ddd: Toast = ToastUtils.getToast()
//            ddd.setText("请先阅读并同意《中国移动认证服务条款》和《用户协议》、《隐私政策》")
//            uiConfigBuilder.enableHintToast(true, ddd)
//            val listp: MutableList<PrivacyBean> = ArrayList()
//            //        PrivacyBean privacy1 = new PrivacyBean("用户协议","https://www.ky808.cn/carfriend/static/user_agree.html","和《","》、");
////        PrivacyBean privacy2 = new PrivacyBean("隐私政策","https://www.ky808.cn/carfriend/static/privacy_agree.html","《","》");
//            val privacy1: PrivacyBean =
//                PrivacyBean("《用户协议》", "https://www.sslm01.com/sslm/static/user_agree.html", "和")
//            val privacy2: PrivacyBean = PrivacyBean(
//                "《隐私政策》",
//                "https://www.sslm01.com/sslm/static/privacy_agree.html",
//                "、"
//            )
//            listp.add(privacy1)
//            listp.add(privacy2)
//            uiConfigBuilder.setPrivacyNameAndUrlBeanList(listp)
//            //        uiConfigBuilder.setAppPrivacyOne("用户协议asdfasdfasdf","https://www.ky808.cn/carfriend/static/user_agree.html");
////        uiConfigBuilder.setAppPrivacyTwo("隐私政策asdfasdfasd","https://www.ky808.cn/carfriend/static/privacy_agree.html");
//            uiConfigBuilder.setPrivacyText("我已阅读并同意 ", "")
//            uiConfigBuilder.setPrivacyOffsetX(52 - 15)
//            uiConfigBuilder.setPrivacyOffsetY(getResources().getDimensionPixelSize(R.dimen.dp_60))
//
//            // 手机登录按钮
//            val layoutParamPhoneLogin: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(
//                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
//            )
//            layoutParamPhoneLogin.setMargins(
//                0,
//                getResources().getDimensionPixelSize(R.dimen.dp_310),
//                0,
//                0
//            )
//            layoutParamPhoneLogin.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE)
//            layoutParamPhoneLogin.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE)
//            val tvPhoneLogin: TextView = TextView(this)
//            tvPhoneLogin.setText("手机号码登录")
//            tvPhoneLogin.setTextColor(getResources().getColor(R.color.grayText4))
//            tvPhoneLogin.setTextSize(16f)
//            tvPhoneLogin.setLayoutParams(layoutParamPhoneLogin)
//            uiConfigBuilder.addCustomView(tvPhoneLogin, false, object : JVerifyUIClickCallback {
//                public override fun onClicked(context: Context, view: View) {
//                    toNativeVerifyActivity()
//                }
//            })
//
//            // 微信登录
//            val linearLayout: LinearLayout = LinearLayout(this)
//            val layoutLoginGroupParam: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(
//                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
//            )
//            layoutLoginGroupParam.setMargins(
//                0,
//                getResources().getDimensionPixelSize(R.dimen.dp_370),
//                0,
//                0
//            )
//            layoutLoginGroupParam.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE)
//            layoutLoginGroupParam.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
//            layoutLoginGroupParam.setLayoutDirection(LinearLayout.VERTICAL)
//            linearLayout.setOrientation(LinearLayout.VERTICAL)
//            linearLayout.setGravity(Gravity.CENTER)
//            linearLayout.setLayoutParams(layoutLoginGroupParam)
//            val padding: Int = getResources().getDimensionPixelSize(R.dimen.dp_5)
//            linearLayout.setPadding(padding, padding, padding, padding)
//            val btnWechat: ImageView = ImageView(this)
//            val textView: TextView = TextView(this)
//            val texParam: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
//                ViewGroup.LayoutParams.WRAP_CONTENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT
//            )
//            texParam.setMargins(0, padding, 0, 0)
//            textView.setLayoutParams(texParam)
//            textView.setText("微信登录")
//            textView.setTextSize(14f)
//            textView.setTextColor(getResources().getColor(R.color.grayText4))
//            linearLayout.setOnClickListener(object : View.OnClickListener {
//                public override fun onClick(v: View) {
//                    wxShare = WXShare(this@LoginAutoActivity)
//                    wxShare!!.register()
//                    wxShare!!.getAuth(object : ItemCallback {
//                        public override fun onItemCallback(position: Int, obj: Any?) {
//                            reqSignIn(obj as String?)
//                        }
//
//                        public override fun onDetailCallBack(position: Int, obj: Any?) {}
//                    })
//                }
//            })
//            btnWechat.setImageResource(R.drawable.ic_contact_wx)
//            val btnParam: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
//                ViewGroup.LayoutParams.WRAP_CONTENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT
//            )
//            btnParam.setMargins(25, 0, 25, 0)
//            linearLayout.addView(btnWechat, btnParam)
//            linearLayout.addView(textView)
//            uiConfigBuilder.addCustomView(linearLayout, false
//            ) { context, view ->
//                wxShare = WXShare(this@LoginAutoActivity)
//                wxShare!!.register()
//                wxShare!!.getAuth(object : ItemCallback {
//                    public override fun onItemCallback(position: Int, obj: Any?) {
//                        reqSignIn(obj as String?)
//                    }
//
//                    public override fun onDetailCallBack(position: Int, obj: Any?) {}
//                })
//            }
//            return uiConfigBuilder.build()
//        }
}