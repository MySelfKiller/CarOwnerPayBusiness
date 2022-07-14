package com.kayu.business_car_owner.activity.login

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.ViewModelProviders
import com.kayu.form_verify.Validate
import com.kayu.form_verify.validator.PhoneValidator
import com.kayu.form_verify.validator.NotEmptyValidator
import com.hjq.toast.ToastUtils
import com.kongzue.dialog.v3.TipGifDialog
import com.kayu.business_car_owner.model.SystemParam
import com.kongzue.dialog.v3.AgreementDialog
import com.kongzue.dialog.interfaces.OnDialogButtonClickListener
import com.kongzue.dialog.util.BaseDialog
import com.kayu.business_car_owner.http.parser.LoginDataParse
import com.kayu.business_car_owner.model.LoginInfo
import com.kayu.business_car_owner.http.parser.NormalParse
import androidx.lifecycle.Observer
import com.kayu.business_car_owner.*
import com.kayu.business_car_owner.R
import com.kayu.business_car_owner.activity.*
import com.kayu.business_car_owner.data_parser.ImgCodeDataParse
import com.kayu.business_car_owner.http.*
import com.kayu.business_car_owner.model.ImgCode
import com.kayu.form_verify.Form
import com.kayu.utils.*
import com.kayu.utils.status_bar_set.StatusBarUtil
import java.util.HashMap
import java.util.regex.Pattern

class LoginActivity : BaseActivity() {
    private var phone_number: EditText? = null
    private var password_edt: EditText? = null
    private var sms_code_edt: EditText? = null
    private var ask_btn: AppCompatButton? = null
    private var timer: SMSCountDownTimer? = null
    private var send_sms_tv: TextView? = null
    private var img_code_iv: ImageView? = null
    private var password_target: TextView? = null
    private var password_edt_lay: LinearLayout? = null
    private var sms_code_edt_lay: LinearLayout? = null
    private var password_target_lay: LinearLayout? = null
    private var login_sms_target_lay: LinearLayout? = null
    private var login_sms_target: TextView? = null
    private var login_forget_password: TextView? = null
    private var isSMSLogin = true
    private var mViewModel: MainViewModel? = null
    private var user_agreement: TextView? = null
    private var user_privacy: TextView? = null
    private var sp: SharedPreferences? = null
    private var isFirstShow = false
    private var auto_progress: LinearLayout? = null
    private var login_checkbox: CheckBox? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtil.setRootViewFitsSystemWindows(this, true)
        //设置状态栏透明
        StatusBarUtil.setTranslucentStatus(this)
        setContentView(R.layout.activity_login)

        sp = getSharedPreferences(Constants.SharedPreferences_name, MODE_PRIVATE)
        mViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        login_sms_target_lay = findViewById(R.id.login_sms_target_lay)
        password_target_lay = findViewById(R.id.login_password_target_lay)
        password_target = findViewById(R.id.login_password_target)
        login_sms_target = findViewById(R.id.login_sms_target)
        login_checkbox = findViewById(R.id.login_checkbox)
        auto_progress = findViewById(R.id.login_auto_progress)
        auto_progress?.setClickable(false)
        auto_progress?.setFocusable(false)

        ask_btn = findViewById(R.id.login_ask_btn)
        phone_number = findViewById(R.id.login_number_edt)
        password_edt = findViewById(R.id.login_password_edt)
        password_edt_lay = findViewById(R.id.login_password_lay)
        sms_code_edt_lay = findViewById(R.id.login_sms_code_lay)
        send_sms_tv = findViewById(R.id.login_send_sms_tv)
        img_code_iv = findViewById(R.id.login_img_code_iv)
        sms_code_edt = findViewById(R.id.login_sms_code_edt)


        login_sms_target?.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                login_sms_target_lay?.visibility = View.GONE
                password_target_lay?.visibility = View.VISIBLE
                sms_code_edt_lay?.visibility = View.VISIBLE
                send_sms_tv?.visibility = View.VISIBLE
                img_code_iv?.visibility = View.GONE
                password_edt_lay?.visibility = View.GONE
                isSMSLogin = true
            }

            override fun OnMoreErrorClick() {}
        })
        login_forget_password = findViewById(R.id.login_forget_password)
        login_forget_password?.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                startActivity(Intent(this@LoginActivity, ForgetPasswordActivity::class.java))
            }

            override fun OnMoreErrorClick() {}
        })

        password_target?.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                if (imgCode?.isCaptcha == false) {
                    sms_code_edt_lay?.visibility = View.GONE
                    img_code_iv?.visibility = View.GONE
                    send_sms_tv?.visibility = View.VISIBLE
                } else {
                    sms_code_edt_lay?.visibility = View.VISIBLE
                    img_code_iv?.visibility = View.VISIBLE
                    img_code_iv?.setImageBitmap(FileUtil.convertStringToIcon(imgCode?.imageBase64))
                    send_sms_tv?.visibility = View.GONE
                }
                password_edt_lay?.visibility = View.VISIBLE
                password_target_lay?.visibility = View.GONE
                login_sms_target_lay?.visibility = View.VISIBLE
                isSMSLogin = false
            }

            override fun OnMoreErrorClick() {}
        })
        timer = SMSCountDownTimer(send_sms_tv!!, 60 * 1000 * 2, 1000)
        send_sms_tv?.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                val form = Form()
                val phoneValiv = Validate(phone_number)
                phoneValiv.addValidator(PhoneValidator(this@LoginActivity))
                form.addValidates(phoneValiv)
                val isOk = form.validate()
                if (isOk) {
                    timer!!.start()
                    sendSmsRequest()
                }
            }

            override fun OnMoreErrorClick() {}
        })
        ask_btn?.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                val form = Form()
                val phoneValiv = Validate(phone_number)
                phoneValiv.addValidator(PhoneValidator(this@LoginActivity))
                form.addValidates(phoneValiv)
                val smsValiv: Validate
                if (isSMSLogin) {
                    smsValiv = Validate(sms_code_edt)
                } else {
                    smsValiv = Validate(password_edt)
                    if (imgCode != null && imgCode!!.isCaptcha) {
                        val imgCodeValiv = Validate(sms_code_edt)
                        imgCodeValiv.addValidator(NotEmptyValidator(this@LoginActivity))
                        form.addValidates(imgCodeValiv)
                    }
                }
                smsValiv.addValidator(NotEmptyValidator(this@LoginActivity))
                form.addValidates(smsValiv)
                val isOk = form.validate()
                if (isOk) {
                    if (login_checkbox?.isChecked() == true) {
                        sendSubRequest()
                    } else {
                        ToastUtils.show("请先阅读并同意《用户协议》、《隐私政策》")
                    }
                }
            }

            override fun OnMoreErrorClick() {}
        })
        user_agreement = findViewById(R.id.login_user_agreement_tv)
        user_privacy = findViewById(R.id.login_user_privacy_tv)
        TipGifDialog.show(
            this@LoginActivity,
            "稍等...",
            TipGifDialog.TYPE.OTHER,
            R.drawable.loading_gif
        )
        mViewModel!!.getParameter(this, 3).observe(this, object : Observer<SystemParam?> {
            override fun onChanged(systemParam: SystemParam?) {
                TipGifDialog.dismiss()
                if (null != systemParam && systemParam.type == 3) {
                    val titles = systemParam.title.split("@@".toRegex()).toTypedArray()
                    val urls = systemParam.url.split("@@".toRegex()).toTypedArray()
                    isFirstShow = sp?.getBoolean(Constants.isShowDialog, true) == true
                    if (isFirstShow) {
                        AgreementDialog.show(
                            this@LoginActivity,
                            titles.get(0) + "与" + titles[1],
                            KWApplication.instance.getClickableSpan(this@LoginActivity, titles, urls),
                            "同意",
                            "拒接并退出"
                        )
                            .setCancelable(false)
                            .setOkButton { baseDialog, v ->
                                baseDialog.doDismiss()
                                isFirstShow = false
                                val editor = sp?.edit()
                                editor?.putBoolean(Constants.isShowDialog, isFirstShow)
                                editor?.apply()
                                editor?.commit()
                                false
                            }.setCancelButton(object : OnDialogButtonClickListener {
                                override fun onClick(baseDialog: BaseDialog, v: View): Boolean {
                                    baseDialog.doDismiss()
                                    finish()
                                    return false
                                }
                            })
                    }
                    user_agreement?.setText("《" + titles[1] + "》")
                    user_privacy?.setText("《" + titles[0] + "》")
                    user_agreement?.setOnClickListener(object : NoMoreClickListener() {
                        override fun OnMoreClick(view: View) {
                            jumpWeb(titles[1], urls[1])
                        }

                        override fun OnMoreErrorClick() {}
                    })
                    user_privacy?.setOnClickListener(object : NoMoreClickListener() {
                        override fun OnMoreClick(view: View) {
                            jumpWeb(titles[0], urls[0])
                        }

                        override fun OnMoreErrorClick() {}
                    })
                }
            }
        })
        getImgCode()
    }

    private fun jumpWeb(title: String, url: String) {
        val intent = Intent(this@LoginActivity, WebViewActivity::class.java)
        intent.putExtra("url", url)
        intent.putExtra("from", title)
        startActivity(intent)
    }

    @SuppressLint("HandlerLeak")
    private fun sendSubRequest() {
        TipGifDialog.show(
            this@LoginActivity,
            "确认中...",
            TipGifDialog.TYPE.OTHER,
            R.drawable.loading_gif
        )
        val reqInfo = RequestInfo()
        reqInfo.context = this@LoginActivity
        reqInfo.reqUrl = HttpConfig.HOST + HttpConfig.INTERFACE_LOGIN
        reqInfo.parser = LoginDataParse()
        val reqDateMap = HashMap<String, Any>()
        reqDateMap["phone"] = phone_number!!.text.toString().trim { it <= ' ' }
        val imei: String? = null
        reqDateMap["imei"]= imei?:""
        if (isSMSLogin) {
            reqDateMap["code"] = sms_code_edt!!.text.toString().trim { it <= ' ' }
        } else {
            reqDateMap["password"] = password_edt!!.text.toString().trim { it <= ' ' }
            if (imgCode != null && imgCode!!.isCaptcha) {
                reqDateMap["code"] = sms_code_edt!!.text.toString().trim { it <= ' ' }
            }
        }
        //        reqDateMap.put("code",sms_code_edt.getText().toString().trim());
        reqInfo.reqDataMap = reqDateMap
        reqInfo.handler = object : Handler() {
            override fun handleMessage(msg: Message) {
                TipGifDialog.dismiss()
                val resInfo = msg.obj as ResponseInfo
                if (resInfo.status == 1) {
                    val user = resInfo.responseData as LoginInfo
                    if (StringUtil.isEmpty(user.lastLoginTime)) {
                        auto_progress!!.visibility = View.VISIBLE
                        auto_progress!!.postDelayed(object : Runnable {
                            override fun run() {
                                auto_progress!!.visibility = View.GONE
                                saveLogin(user)
                            }
                        }, (1000 * 2).toLong())
                    } else {
                        saveLogin(user)
                    }
                } else {
                    if (!isSMSLogin) {
                        getImgCode()
                    }
                    ToastUtils.show(resInfo.msg)
                }
                super.handleMessage(msg)
            }
        }
        val callback = ResponseCallback(reqInfo)
        ReqUtil.setReqInfo(reqInfo)
        ReqUtil.requestPostJSON(callback)
    }

    private fun saveLogin(user: LoginInfo) {
        val editor = sp!!.edit()
        editor.putBoolean(Constants.isLogin, true)
        editor.putString(Constants.token, user.token)
        editor.putBoolean(Constants.isSetPsd, true)
        editor.putString(Constants.login_info, GsonHelper.toJsonString(user))
        editor.apply()
        editor.commit()
        KWApplication.instance.token = user.token
        AppManager.appManager?.finishAllActivity()
        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        finish()
    }

    @SuppressLint("HandlerLeak")
    private fun sendSmsRequest() {
        TipGifDialog.show(
            this@LoginActivity,
            "发送验证码...",
            TipGifDialog.TYPE.OTHER,
            R.drawable.loading_gif
        )
        val reqInfo = RequestInfo()
        reqInfo.context = this@LoginActivity
        reqInfo.reqUrl = HttpConfig.HOST + HttpConfig.INTERFACE_VERIFICATION_CODE
        reqInfo.parser = NormalParse()
        val reqDateMap = HashMap<String, Any>()
        reqDateMap[""] = phone_number!!.text.toString().trim { it <= ' ' }
        reqInfo.reqDataMap = reqDateMap
        reqInfo.handler = object : Handler() {
            override fun handleMessage(msg: Message) {
                TipGifDialog.dismiss()
                val resInfo = msg.obj as ResponseInfo
                if (resInfo.status == 1) {
                    ToastUtils.show("验证码发送成功")
                } else {
                    timer!!.clear()
                    ToastUtils.show(resInfo.msg)
                }
                super.handleMessage(msg)
            }
        }
        val callback = ResponseCallback(reqInfo)
        ReqUtil.setReqInfo(reqInfo)
        ReqUtil.requestGetJSON(callback)
    }

    private var imgCode : ImgCode? = null
    @SuppressLint("HandlerLeak")
    private fun getImgCode() {
        val reqInfo = RequestInfo()
        reqInfo.context = this@LoginActivity
        reqInfo.reqUrl = HttpConfig.HOST + HttpConfig.INTERFACE_IMG_CODE
        reqInfo.parser = ImgCodeDataParse()
        val reqDateMap = HashMap<String, Any>()
        reqInfo.reqDataMap = reqDateMap
        reqInfo.handler = object : Handler() {
            override fun handleMessage(msg: Message) {
                TipGifDialog.dismiss()
                val resInfo = msg.obj as ResponseInfo
                if (resInfo.status == 1) {
                    imgCode = resInfo.responseData as ImgCode
                    img_code_iv?.setImageBitmap(FileUtil.convertStringToIcon(imgCode?.imageBase64))
                    if (imgCode!!.isCaptcha && !isSMSLogin) {
                        sms_code_edt_lay?.visibility = View.VISIBLE
                        img_code_iv?.visibility = View.VISIBLE
                        send_sms_tv?.visibility = View.GONE
                    }
                } else {
                }
                super.handleMessage(msg)
            }
        }
        val callback = ResponseCallback(reqInfo)
        ReqUtil.setReqInfo(reqInfo)
        ReqUtil.requestGetJSON(callback)
    }
}