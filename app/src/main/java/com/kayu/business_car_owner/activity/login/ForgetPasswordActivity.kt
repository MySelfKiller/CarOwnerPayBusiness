package com.kayu.business_car_owner.activity.login

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import com.kayu.utils.SMSCountDownTimer
import com.kayu.utils.NoMoreClickListener
import com.kayu.form_verify.Validate
import com.kayu.form_verify.validator.PhoneValidator
import com.kayu.form_verify.validator.NotEmptyValidator
import com.hjq.toast.ToastUtils
import com.kongzue.dialog.v3.TipGifDialog
import com.kayu.business_car_owner.http.parser.NormalParse
import com.kayu.form_verify.validate.ConfirmValidate
import com.kayu.business_car_owner.*
import com.kayu.business_car_owner.activity.*
import com.kayu.business_car_owner.http.*
import com.kayu.form_verify.Form
import java.util.HashMap

class ForgetPasswordActivity : BaseActivity() {
    private var code_edt: EditText? = null
    private var phone_edt: EditText? = null
    private var new_password_edt: EditText? = null
    private var new_password2_edt: EditText? = null
    private var ask_btn: AppCompatButton? = null
    private var send_sms_tv: TextView? = null
    private var timer: SMSCountDownTimer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password)

        //标题栏
//        val title_lay: LinearLayout = findViewById(R.id.title_lay)
        //        title_lay.setBackgroundColor(getResources().getColor(R.color.background_gray));
        val title_name: TextView = findViewById(R.id.title_name_tv)
        title_name.setText("忘记密码")
        findViewById<View>(R.id.title_back_btu).setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                onBackPressed()
            }

            override fun OnMoreErrorClick() {}
        })
        val back_tv: TextView = findViewById(R.id.title_back_tv)
        back_tv.setText("返回")
        code_edt = findViewById(R.id.forget_code)
        phone_edt = findViewById(R.id.forget_number_edt)
        new_password_edt = findViewById(R.id.forget_new_password)
        new_password2_edt = findViewById(R.id.forget_new_password2)
        send_sms_tv = findViewById(R.id.forget_send_sms_tv)
        timer = SMSCountDownTimer(send_sms_tv!! , 60 * 1000, 1000)
        send_sms_tv?.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                val form: Form = Form()
                val phoneValiv: Validate = Validate(phone_edt)
                phoneValiv.addValidator(PhoneValidator(this@ForgetPasswordActivity))
                form.addValidates(phoneValiv)
                val isOk: Boolean = form.validate()
                if (isOk) {
                    timer!!.start()
                    sendSmsRequest()
                }
            }

            override fun OnMoreErrorClick() {}
        })
        ask_btn = findViewById(R.id.forget_ask_btn)
        ask_btn?.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                val form: Form = Form()
                val phoneValidate: Validate = Validate(phone_edt)
                phoneValidate.addValidator(PhoneValidator(this@ForgetPasswordActivity))
                form.addValidates(phoneValidate)
                val codeValidate: Validate = Validate(code_edt)
                codeValidate.addValidator(NotEmptyValidator(this@ForgetPasswordActivity))
                form.addValidates(codeValidate)
                val newPassValidate: ConfirmValidate =
                    ConfirmValidate(new_password_edt, new_password2_edt)
                newPassValidate.addValidator(NotEmptyValidator(this@ForgetPasswordActivity))
                form.addValidates(newPassValidate)
                if (form.validate()) {
                    reqReSetPasswrod()
                }
            }

            override fun OnMoreErrorClick() {}
        })
    }

    @SuppressLint("HandlerLeak")
    private fun reqReSetPasswrod() {
        TipGifDialog.show(
            this@ForgetPasswordActivity,
            "稍等...",
            TipGifDialog.TYPE.OTHER,
            R.drawable.loading_gif
        )
        val reqInfo: RequestInfo = RequestInfo()
        reqInfo.context = this@ForgetPasswordActivity
        reqInfo.reqUrl = HttpConfig.HOST + HttpConfig.INTERFACE_RESET_PASSWORD
        reqInfo.parser = NormalParse()
        val reqDateMap: HashMap<String, Any> = HashMap()
        reqDateMap.put("phone", phone_edt!!.getText().toString().trim({ it <= ' ' }))
        reqDateMap.put("code", code_edt!!.getText().toString().trim({ it <= ' ' }))
        reqDateMap.put("newPwd", new_password2_edt!!.getText().toString().trim({ it <= ' ' }))
        //        reqDateMap.put("code",sms_code.getText().toString().trim());
        reqInfo.reqDataMap = reqDateMap
        reqInfo.handler = object : Handler() {
            public override fun handleMessage(msg: Message) {
                TipGifDialog.dismiss()
                val resInfo: ResponseInfo = msg.obj as ResponseInfo
                if (resInfo.status == 1) {
                    ToastUtils.show("密码已经重置成功！")
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

    @SuppressLint("HandlerLeak")
    private fun sendSmsRequest() {
        TipGifDialog.show(
            this@ForgetPasswordActivity,
            "稍等...",
            TipGifDialog.TYPE.OTHER,
            R.drawable.loading_gif
        )
        val reqInfo: RequestInfo = RequestInfo()
        reqInfo.context = this@ForgetPasswordActivity
        reqInfo.reqUrl = HttpConfig.HOST + HttpConfig.INTERFACE_VERIFICATION_CODE
        reqInfo.parser = NormalParse()
        val reqDateMap: HashMap<String, Any> = HashMap()
        reqDateMap.put("", phone_edt!!.getText().toString().trim({ it <= ' ' }))
        reqInfo.reqDataMap = reqDateMap
        reqInfo.handler = object : Handler() {
            public override fun handleMessage(msg: Message) {
                TipGifDialog.dismiss()
                val resInfo: ResponseInfo = msg.obj as ResponseInfo
                if (resInfo.status == 1) {
                    ToastUtils.show("验证码发送成功")
                } else {
                    ToastUtils.show(resInfo.msg)
                }
                super.handleMessage(msg)
            }
        }
        val callback: ResponseCallback = ResponseCallback(reqInfo)
        ReqUtil.setReqInfo(reqInfo)
        ReqUtil.requestGetJSON(callback)
    }
}