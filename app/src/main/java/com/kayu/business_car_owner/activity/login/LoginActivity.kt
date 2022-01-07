package com.kayu.business_car_owner.activity.login

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
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
import com.kayu.business_car_owner.http.*
import com.kayu.form_verify.Form
import com.kayu.utils.*
import java.util.HashMap
import java.util.regex.Pattern

class LoginActivity() : BaseActivity() {
    private var phone_number: EditText? = null
    private var sms_code: EditText? = null
    private var ask_btn: AppCompatButton? = null
    private var timer: SMSCountDownTimer? = null
    private var send_sms: TextView? = null
    private var password_target: TextView? = null
    private var login_send_sms_lay: LinearLayout? = null
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
        setContentView(R.layout.activity_login)

//        //标题栏
//        findViewById(R.id.title_menu_btu).setVisibility(View.INVISIBLE);
//        TextView title_name = findViewById(R.id.title_name_tv);
//        title_name.setText(getResources().getString(R.string.title_login));

//        findViewById(R.id.title_menu_btu).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });
//        TextView back_tv = findViewById(R.id.title_back_tv);
//        back_tv.setText("微信登录");
        sp = getSharedPreferences(Constants.SharedPreferences_name, MODE_PRIVATE)
        mViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        login_send_sms_lay = findViewById(R.id.login_send_sms_lay)
        login_sms_target_lay = findViewById(R.id.login_sms_target_lay)
        password_target_lay = findViewById(R.id.login_password_target_lay)
        password_target = findViewById(R.id.login_password_target)
        login_sms_target = findViewById(R.id.login_sms_target)
        login_checkbox = findViewById(R.id.login_checkbox)
        auto_progress = findViewById(R.id.login_auto_progress)
        auto_progress?.setClickable(false)
        auto_progress?.setFocusable(false)
        login_sms_target?.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                login_sms_target_lay?.setVisibility(View.GONE)
                password_target_lay?.setVisibility(View.VISIBLE)
                login_send_sms_lay?.setVisibility(View.VISIBLE)
                sms_code!!.setText("")
                sms_code!!.hint = "请输入验证码"
                sms_code!!.inputType = InputType.TYPE_CLASS_NUMBER
                val filters = arrayOf<InputFilter>(InputFilter.LengthFilter(4))
                sms_code!!.filters = filters
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
        ask_btn = findViewById(R.id.login_ask_btn)
        phone_number = findViewById(R.id.login_number_edt)
        send_sms = findViewById(R.id.login_send_sms_tv)
        sms_code = findViewById(R.id.login_sms_code_edt)
        sms_code?.setInputType(InputType.TYPE_CLASS_NUMBER)
        val filters = arrayOf<InputFilter>(InputFilter.LengthFilter(4))
        sms_code?.setFilters(filters)
        sms_code?.setHint("请输入验证码")
        password_target?.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                login_send_sms_lay?.setVisibility(View.GONE)
                password_target_lay?.setVisibility(View.GONE)
                login_sms_target_lay?.setVisibility(View.VISIBLE)
                sms_code?.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT)
                sms_code?.setTypeface(Typeface.DEFAULT)
                sms_code?.setTransformationMethod(PasswordTransformationMethod())
                sms_code?.setText("")
                sms_code?.setHint("请输入登录密码")
                val filters = arrayOf<InputFilter>(InputFilter.LengthFilter(25))
                sms_code?.setFilters(filters)
                isSMSLogin = false
            }

            override fun OnMoreErrorClick() {}
        })
        ask_btn?.setClickable(false)
        ask_btn?.setEnabled(false)
        phone_number?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                val pattern = Pattern.compile("^1[0-9]{10}$")
                if (pattern.matcher(s).matches()) {
                    send_sms?.setClickable(true)
                    send_sms?.setTextColor(resources.getColor(R.color.colorAccent))
                } else {
                    send_sms?.setClickable(false)
                    send_sms?.setTextColor(resources.getColor(R.color.grayText))
                }
                if (isSMSLogin) {
                    val pasPattern = Pattern.compile("[0-9]{4}$")
                    if (pattern.matcher(s).matches() && pasPattern.matcher(
                            sms_code?.getText().toString().trim { it <= ' ' }).matches()
                    ) {
                        ask_btn?.setClickable(true)
                        ask_btn?.setEnabled(true)
                        ask_btn?.setBackground(resources.getDrawable(R.drawable.blue_bg_shape))
                        ask_btn?.setTextColor(resources.getColor(R.color.white))
                    } else {
                        ask_btn?.setEnabled(false)
                        ask_btn?.setClickable(false)
                        ask_btn?.setBackground(resources.getDrawable(R.drawable.gray_bg_shape))
                        ask_btn?.setTextColor(resources.getColor(R.color.white))
                    }
                } else {
                    if (pattern.matcher(s).matches() && !StringUtil.isEmpty(
                            sms_code?.getText().toString().trim { it <= ' ' })
                    ) {
                        ask_btn?.setClickable(true)
                        ask_btn?.setEnabled(true)
                        ask_btn?.setBackground(resources.getDrawable(R.drawable.blue_bg_shape))
                        ask_btn?.setTextColor(resources.getColor(R.color.white))
                    } else {
                        ask_btn?.setEnabled(false)
                        ask_btn?.setClickable(false)
                        ask_btn?.setBackground(resources.getDrawable(R.drawable.gray_bg_shape))
                        ask_btn?.setTextColor(resources.getColor(R.color.white))
                    }
                }
            }
        })
        send_sms?.setClickable(false)
        sms_code?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                val pattern = Pattern.compile("^1[0-9]{10}$")
                if (isSMSLogin) {
                    val pasPattern = Pattern.compile("[0-9]{4}$")
                    if (pattern.matcher(phone_number?.getText().toString().trim { it <= ' ' })
                            .matches() && pasPattern.matcher(s).matches()
                    ) {
                        ask_btn?.setClickable(true)
                        ask_btn?.setEnabled(true)
                        ask_btn?.setBackground(resources.getDrawable(R.drawable.blue_bg_shape))
                    } else {
                        ask_btn?.setEnabled(false)
                        ask_btn?.setClickable(false)
                        ask_btn?.setBackground(resources.getDrawable(R.drawable.gray_bg_shape))
                    }
                } else {
                    if (pattern.matcher(phone_number?.getText().toString().trim { it <= ' ' })
                            .matches() && !StringUtil.isEmpty(
                            sms_code?.getText().toString().trim { it <= ' ' })
                    ) {
                        ask_btn?.setClickable(true)
                        ask_btn?.setEnabled(true)
                        ask_btn?.setBackground(resources.getDrawable(R.drawable.blue_bg_shape))
                    } else {
                        ask_btn?.setEnabled(false)
                        ask_btn?.setClickable(false)
                        ask_btn?.setBackground(resources.getDrawable(R.drawable.gray_bg_shape))
                    }
                }
                //                if (!StringUtil.isEmpty(s.toString().trim()) && !StringUtil.isEmpty(phone_number.getText().toString().trim())){
//                    ask_btn.setClickable(true);
//                    ask_btn.setEnabled(true);
//                    ask_btn.setBackground(getResources().getDrawable(R.drawable.blue_bg_shape));
//                }else {
//                    ask_btn.setClickable(false);
//                    ask_btn.setEnabled(false);
//                    ask_btn.setBackground(getResources().getDrawable(R.drawable.gray_bg_shape));
//                }
            }
        })
        timer = SMSCountDownTimer(send_sms!!, 60 * 1000 * 2, 1000)
        send_sms?.setOnClickListener(object : NoMoreClickListener() {
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
                val smsValiv = Validate(sms_code)
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
                            "暂不使用"
                        )
                            .setCancelable(false)
                            .setOkButton(OnDialogButtonClickListener { baseDialog, v ->
                                baseDialog.doDismiss()
                                isFirstShow = false
                                val editor = sp?.edit()
                                editor?.putBoolean(Constants.isShowDialog, isFirstShow)
                                editor?.apply()
                                editor?.commit()
                                false
                            }).setCancelButton(object : OnDialogButtonClickListener {
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
        val imei: String? = KWApplication.instance.oidImei
        reqDateMap["imei"]= imei?:""
        if (isSMSLogin) {
            reqDateMap["code"] = sms_code!!.text.toString().trim { it <= ' ' }
        } else {
            reqDateMap["password"] = sms_code!!.text.toString().trim { it <= ' ' }
        }
        //        reqDateMap.put("code",sms_code.getText().toString().trim());
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
}