package com.kayu.business_car_owner.activity.login

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.kayu.form_verify.Validate
import com.kayu.form_verify.validator.NotEmptyValidator
import com.hjq.toast.ToastUtils
import com.kongzue.dialog.v3.TipGifDialog
import com.kayu.business_car_owner.http.parser.LoginDataParse
import com.kayu.business_car_owner.activity.AppManager
import com.kayu.business_car_owner.http.parser.NormalParse
import com.kayu.business_car_owner.model.UserBean
import com.kayu.utils.location.LocationManagerUtil
import com.kayu.business_car_owner.R
import com.kayu.business_car_owner.http.*
import com.kayu.form_verify.Form
import com.kayu.utils.*
import java.util.HashMap
import java.util.regex.Pattern

class LogOffActivity : AppCompatActivity() {
    private var phone_number: EditText? = null
    private var sms_code: EditText? = null
    private var ask_btn: AppCompatButton? = null
    private var timer: SMSCountDownTimer? = null
    private var send_sms: TextView? = null
    private var useData: UserBean? = null
    private var sp: SharedPreferences? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_off)
        val title_name: TextView = findViewById(R.id.title_name_tv)
        title_name.setText("账号注销")
        findViewById<View>(R.id.title_back_btu).setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                onBackPressed()
            }

            override fun OnMoreErrorClick() {}
        })
        sp = getSharedPreferences(Constants.SharedPreferences_name, MODE_PRIVATE)
        val jsonUser: String? = sp?.getString(Constants.userInfo, "")
        useData = GsonHelper.fromJson(jsonUser, UserBean::class.java)
        phone_number = findViewById(R.id.logoff_number_edt)
        send_sms = findViewById(R.id.logoff_send_sms_tv)
        sms_code = findViewById(R.id.logoff_sms_code_edt)
        sms_code!!.setInputType(InputType.TYPE_CLASS_NUMBER)
        val filters: Array<InputFilter> = arrayOf(InputFilter.LengthFilter(4))
        sms_code!!.setFilters(filters)
        sms_code!!.setHint("请输入验证码")
        ask_btn = findViewById(R.id.logoff_ask_btn)
        ask_btn!!.setClickable(false)
        ask_btn!!.setEnabled(false)
        phone_number!!.setText(useData?.phone)
        phone_number!!.setClickable(false)
        phone_number!!.setEnabled(false)
        phone_number!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
            }

            override fun afterTextChanged(s: Editable) {
                val pattern: Pattern = Pattern.compile("^1[0-9]{10}$")
                if (pattern.matcher(s).matches()) {
                    send_sms!!.setClickable(true)
                    send_sms!!.setTextColor(getResources().getColor(R.color.colorAccent))
                } else {
                    send_sms!!.setClickable(false)
                    send_sms!!.setTextColor(getResources().getColor(R.color.grayText))
                }
                val pasPattern: Pattern = Pattern.compile("[0-9]{4}$")
                if (pattern.matcher(s).matches() && pasPattern.matcher(
                        sms_code!!.getText().toString().trim({ it <= ' ' })
                    ).matches()
                ) {
                    ask_btn!!.setClickable(true)
                    ask_btn!!.setEnabled(true)
                    ask_btn!!.setBackground(getResources().getDrawable(R.drawable.blue_bg_shape))
                    ask_btn!!.setTextColor(getResources().getColor(R.color.slight_yellow))
                } else {
                    ask_btn!!.setEnabled(false)
                    ask_btn!!.setClickable(false)
                    ask_btn!!.setBackground(getResources().getDrawable(R.drawable.gray_bg_shape))
                    ask_btn!!.setTextColor(getResources().getColor(R.color.white))
                }
            }
        })
        send_sms!!.setClickable(false)
        sms_code!!.addTextChangedListener(object : TextWatcher {
            public override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            public override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
            }

            public override fun afterTextChanged(s: Editable) {
                val pattern: Pattern = Pattern.compile("^1[0-9]{10}$")
                val pasPattern: Pattern = Pattern.compile("[0-9]{4}$")
                if (pasPattern.matcher(s).matches()) {
                    ask_btn!!.setClickable(true)
                    ask_btn!!.setEnabled(true)
                    ask_btn!!.setBackground(getResources().getDrawable(R.drawable.blue_bg_shape))
                } else {
                    ask_btn!!.setEnabled(false)
                    ask_btn!!.setClickable(false)
                    ask_btn!!.setBackground(getResources().getDrawable(R.drawable.gray_bg_shape))
                }
            }
        })
        timer = SMSCountDownTimer(send_sms!!, 60 * 1000 * 2, 1000)
        send_sms!!.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                val form: Form = Form()
                val phoneValiv: Validate = Validate(phone_number)
                phoneValiv.addValidator(NotEmptyValidator(this@LogOffActivity))
                form.addValidates(phoneValiv)
                val isOk: Boolean = form.validate()
                if (isOk) {
                    timer!!.start()
                    sendSmsRequest()
                }
            }

            override fun OnMoreErrorClick() {}
        })
        ask_btn!!.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                val form: Form = Form()
                val phoneValiv: Validate = Validate(phone_number)
                phoneValiv.addValidator(NotEmptyValidator(this@LogOffActivity))
                form.addValidates(phoneValiv)
                val smsValiv: Validate = Validate(sms_code)
                smsValiv.addValidator(NotEmptyValidator(this@LogOffActivity))
                form.addValidates(smsValiv)
                val isOk: Boolean = form.validate()
                if (isOk) {
                    sendSubRequest()
                }
            }

            override fun OnMoreErrorClick() {}
        })
    }

    @SuppressLint("HandlerLeak")
    private fun sendSmsRequest() {
        TipGifDialog.show(
            this@LogOffActivity,
            "发送验证码...",
            TipGifDialog.TYPE.OTHER,
            R.drawable.loading_gif
        )
        val reqInfo: RequestInfo = RequestInfo()
        reqInfo.context = this@LogOffActivity
        reqInfo.reqUrl = HttpConfig.HOST + HttpConfig.INTERFACE_VERIFICATION_CODE
        reqInfo.parser = NormalParse()
        val reqDateMap: HashMap<String, Any> = HashMap()
        reqDateMap.put("", "18888888888")
        reqInfo.reqDataMap = reqDateMap
        reqInfo.handler = object : Handler() {
            public override fun handleMessage(msg: Message) {
                TipGifDialog.dismiss()
                val resInfo: ResponseInfo = msg.obj as ResponseInfo
                if (resInfo.status == 1) {
                    ToastUtils.show("验证码发送成功")
                } else {
                    timer!!.clear()
                    ToastUtils.show(resInfo.msg)
                }
                super.handleMessage(msg)
            }
        }
        val callback: ResponseCallback = ResponseCallback(reqInfo)
        ReqUtil.setReqInfo(reqInfo)
        ReqUtil.requestGetJSON(callback)
    }

    @SuppressLint("HandlerLeak")
    private fun sendSubRequest() {
        TipGifDialog.show(
            this@LogOffActivity,
            "确认中...",
            TipGifDialog.TYPE.OTHER,
            R.drawable.loading_gif
        )
        val reqInfo: RequestInfo = RequestInfo()
        reqInfo.context = this@LogOffActivity
        reqInfo.reqUrl = HttpConfig.HOST + HttpConfig.INTERFACE_LOGIN
        reqInfo.parser = LoginDataParse()
        val reqDateMap: HashMap<String, Any> = HashMap()
        reqDateMap.put("phone", "18888888887")
        reqDateMap.put("code", sms_code!!.getText().toString().trim({ it <= ' ' }))
        reqInfo.reqDataMap = reqDateMap
        reqInfo.handler = object : Handler() {
            public override fun handleMessage(msg: Message) {
                TipGifDialog.dismiss()
                val resInfo: ResponseInfo = msg.obj as ResponseInfo
                if (resInfo.status == 1) {
                    val editor: SharedPreferences.Editor = sp!!.edit()
                    editor.putBoolean(Constants.isLogin, false)
                    editor.putString(Constants.userInfo, "")
                    editor.apply()
                    editor.commit()
                    AppManager.appManager?.finishAllActivity()
                    LocationManagerUtil.self?.stopLocation()
                    //                                LocationManagerUtil.getSelf().destroyLocation();
                    startActivity(Intent(this@LogOffActivity, LoginAutoActivity::class.java))
                    finish()
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
}