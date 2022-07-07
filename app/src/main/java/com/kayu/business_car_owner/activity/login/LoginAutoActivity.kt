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
import com.kongzue.dialog.v3.AgreementDialog
import com.kayu.business_car_owner.KWApplication
import com.kongzue.dialog.interfaces.OnDialogButtonClickListener
import com.kongzue.dialog.util.BaseDialog
import com.kayu.business_car_owner.http.parser.LoginDataParse
import com.kayu.business_car_owner.model.LoginInfo
import com.kayu.business_car_owner.wxapi.WXShare
import com.kayu.utils.status_bar_set.StatusBarUtil
import com.kayu.business_car_owner.ui.text_link.UrlClickableSpan
import com.kayu.business_car_owner.R
import com.kayu.business_car_owner.activity.*
import com.kayu.business_car_owner.http.*
import com.kayu.utils.*
import java.lang.StringBuilder
import java.util.HashMap

class LoginAutoActivity : BaseActivity() {
    private var ask_btn: AppCompatButton? = null
    private var activation_btn: AppCompatButton? = null
    private var mViewModel: MainViewModel? = null
    private var user_agreement: TextView? = null
//    private var order_list_tv: TextView? = null
    private var sp: SharedPreferences? = null
    private var isFirstShow: Boolean = false
    private val titlesStr = "隐私政策@@用户协议"
    private val urlsStr = "https://www.sslm01.com/sslm/static/privacy_agree.html@@https://www.sslm01.com/sslm/static/user_agree.html"
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
//        order_list_tv = findViewById(R.id.login_order_list_tv)
        auto_progress = findViewById(R.id.login_auto_progress)
        auto_progress?.setClickable(false)
        auto_progress?.setFocusable(false)
//        order_list_tv?.setOnClickListener(object : NoMoreClickListener() {
//            override fun OnMoreClick(view: View) {
//                val intent: Intent =
//                    Intent(this@LoginAutoActivity, OrderTrackingActivity::class.java)
//                startActivity(intent)
//            }
//
//            override fun OnMoreErrorClick() {}
//        })
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
//        TipGifDialog.show(this, "请稍等...", TipGifDialog.TYPE.OTHER, R.drawable.loading_gif)
//        mViewModel!!.getParameter(this, 3).observe(this, object : Observer<SystemParam?> {
//             override fun onChanged(systemParam: SystemParam?) {
////                TipGifDialog.dismiss()
//                if (null != systemParam && systemParam.type == 3) {
//                    if (!StringUtil.isEmpty(systemParam.content)) {
//                        systemParam.content.let { KWApplication.instance.loadImg(it, bg_img!!) }
//                    }
//                }
//            }
//        })
        showAgreementDialog()
    }

    private fun showAgreementDialog() {
        titles = titlesStr.split("@@".toRegex()).toTypedArray()
        urls = urlsStr.split("@@".toRegex()).toTypedArray()
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
                        KWApplication.instance.initSDKs()
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
        val stringBuilder = StringBuilder()
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
        val imei: String? = null
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

    private fun jumpDialog(msg: String) {
        val intent = Intent(this@LoginAutoActivity, LoginActivity::class.java)
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
        val imei: String? = null
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
        val callback = ResponseCallback(reqInfo)
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
}