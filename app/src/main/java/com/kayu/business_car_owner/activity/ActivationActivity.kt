package com.kayu.business_car_owner.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import com.kayu.form_verify.Validate
import com.kayu.form_verify.validator.PhoneValidator
import com.kayu.form_verify.validator.NotEmptyValidator
import com.hjq.toast.ToastUtils
import com.kongzue.dialog.v3.TipGifDialog
import com.kayu.business_car_owner.http.parser.LoginDataParse
import com.kayu.business_car_owner.http.parser.NormalParse
import com.kayu.business_car_owner.*
import com.kayu.business_car_owner.R
import com.kayu.business_car_owner.http.*
import com.kayu.form_verify.Form
import com.kayu.utils.*
import java.util.HashMap
import java.util.regex.Pattern

class ActivationActivity : BaseActivity() {
    private var ask_btn: AppCompatButton? = null
    private var send_ver_code: TextView? = null
    private var ver_code_et: EditText? = null
    private var card_num_et: EditText? = null
    private var phone_et: EditText? = null
    private var pwd_et: EditText? = null
    private var timer: SMSCountDownTimer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_activation)

        //标题栏
//        LinearLayout title_lay = findViewById(R.id.title_lay);
//        title_lay.setBackgroundColor(getResources().getColor(R.color.background_gray));
        val title_name: TextView = findViewById(R.id.title_name_tv)
        title_name.setText("激活")
        findViewById<View>(R.id.title_back_btu).setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                onBackPressed()
            }

            override fun OnMoreErrorClick() {}
        })
        //        TextView back_tv = view.findViewById(R.id.title_back_tv);
//        back_tv.setText("我的");
        phone_et = findViewById(R.id.activation_phone_et)
        phone_et?.setInputType(InputType.TYPE_CLASS_NUMBER)
        card_num_et = findViewById(R.id.activation_card_num_et)
        pwd_et = findViewById(R.id.activation_code_et)
        //        card_num_et.setClickable(false);
//        card_num_et.setFocusable(false);
//        code_et.setClickable(true);
//        code_et.setFocusable(false);
        ver_code_et = findViewById(R.id.activation_ver_code_et)
        ver_code_et?.setInputType(InputType.TYPE_CLASS_NUMBER)
        send_ver_code = findViewById(R.id.activation_send_ver_code)
        timer = SMSCountDownTimer(send_ver_code!!, 60 * 1000 * 2, 1000)
        send_ver_code?.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                val form: Form = Form()
                val phoneValiv: Validate = Validate(phone_et)
                phoneValiv.addValidator(PhoneValidator(this@ActivationActivity))
                form.addValidates(phoneValiv)
                val isOk: Boolean = form.validate()
                if (isOk) {
                    timer!!.start()
                    sendSmsRequest()
                }
            }

            override fun OnMoreErrorClick() {}
        })
        ask_btn = findViewById(R.id.activation_ask_btn)
        ask_btn?.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                val form: Form = Form()
                val phoneValiv: Validate = Validate(phone_et)
                phoneValiv.addValidator(PhoneValidator(this@ActivationActivity))
                form.addValidates(phoneValiv)
                val smsValiv: Validate = Validate(ver_code_et)
                smsValiv.addValidator(NotEmptyValidator(this@ActivationActivity))
                val carNoValiv: Validate = Validate(card_num_et)
                carNoValiv.addValidator(NotEmptyValidator(this@ActivationActivity))
                val pwdValiv: Validate = Validate(pwd_et)
                pwdValiv.addValidator(NotEmptyValidator(this@ActivationActivity))
                form.addValidates(smsValiv)
                form.addValidates(carNoValiv)
                form.addValidates(pwdValiv)
                val isOk: Boolean = form.validate()
                if (isOk) {
                    sendSubRequest()
                }
            }

            override fun OnMoreErrorClick() {}
        })
        phone_et?.addTextChangedListener(object : TextWatcher {
            public override fun beforeTextChanged(
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
                    val form: Form = Form()
                    val phoneValiv: Validate = Validate(phone_et)
                    phoneValiv.addValidator(PhoneValidator(this@ActivationActivity))
                    form.addValidates(phoneValiv)
                    val isOk: Boolean = form.validate()
                    if (isOk) {
//                        getActivInfo();
                    }
                } else {
//                    card_num_et.setText("");
//                    pwd_et.setText("");
                }
            }
        })
    }

    @SuppressLint("HandlerLeak")
    private fun sendSmsRequest() {
        TipGifDialog.show(
            this@ActivationActivity,
            "发送验证码...",
            TipGifDialog.TYPE.OTHER,
            R.drawable.loading_gif
        )
        val reqInfo: RequestInfo = RequestInfo()
        reqInfo.context = this@ActivationActivity
        //        reqInfo.reqUrl = HttpConfig.HOST+HttpConfig.INTERFACE_VERIFICATION_CODE;
        reqInfo.reqUrl = "https://www.sslm01.com/sslm/spv/api/getRgSmsCapt/"
        reqInfo.parser = NormalParse()
        val reqDateMap: HashMap<String, Any> = HashMap()
        reqDateMap.put("", phone_et!!.getText().toString().trim({ it <= ' ' }))
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
        val callback = ResponseCallback(reqInfo)
        ReqUtil.setReqInfo(reqInfo)
        ReqUtil.requestGetJSON(callback)
    }

    @SuppressLint("HandlerLeak")
    private fun sendSubRequest() {
        TipGifDialog.show(
            this@ActivationActivity,
            "确认中...",
            TipGifDialog.TYPE.OTHER,
            R.drawable.loading_gif
        )
        val reqInfo: RequestInfo = RequestInfo()
        reqInfo.context = this@ActivationActivity
        //        reqInfo.reqUrl = HttpConfig.HOST +HttpConfig.INTERFACE_LOGIN;
        reqInfo.reqUrl = "https://www.sslm01.com/sslm/spv/api/recharge"
        reqInfo.parser = LoginDataParse()
        val reqDateMap: HashMap<String, Any> = HashMap()
        reqDateMap["phone"] = phone_et!!.getText().toString().trim({ it <= ' ' })
        reqDateMap["code"] = ver_code_et!!.getText().toString().trim({ it <= ' ' })
        reqDateMap["cardNo"] = card_num_et!!.getText().toString().trim({ it <= ' ' })
        reqDateMap["pwd"] = pwd_et!!.getText().toString().trim({ it <= ' ' })
        val imei: String? = KWApplication.instance.oidImei
        reqDateMap["imei"]= imei?:""

//        reqDateMap.put("code",sms_code.getText().toString().trim());
        reqInfo.reqDataMap = reqDateMap
        reqInfo.handler = object : Handler() {
            public override fun handleMessage(msg: Message) {
                TipGifDialog.dismiss()
                val resInfo: ResponseInfo = msg.obj as ResponseInfo
                if (resInfo.status == 1) {
                    ToastUtils.show("激活成功，请登录！")
                    finish()
                    //                    LoginInfo user = (LoginInfo) resInfo.responseData;
//                    if (null != user){
//                        SharedPreferences sp = getSharedPreferences(Constants.SharedPreferences_name, MODE_PRIVATE);
//                        SharedPreferences.Editor editor = sp.edit();
//                        editor.putBoolean(Constants.isLogin,true);
//                        editor.putString(Constants.token,user.token);
//                        editor.putBoolean(Constants.isSetPsd,true);
//                        editor.putString(Constants.login_info, GsonHelper.toJsonString(user));
//                        editor.apply();
//                        editor.commit();
//                        KWApplication.getInstance().token = user.token;
//                        AppManager.getAppManager().finishAllActivity();
//                        Intent intent = new Intent(ActivationActivity.this, MainActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        startActivity(intent);
//                    }
                } else {
                    ToastUtils.show(resInfo.msg)
                }
                super.handleMessage(msg)
            }
        }
        val callback = ResponseCallback(reqInfo)
        ReqUtil.setReqInfo(reqInfo)
        ReqUtil.requestPostJSON(callback)
    } //    @SuppressLint("HandlerLeak")
    //    private void getActivInfo() {
    //        TipGifDialog.show(ActivationActivity.this, "查询中...", TipGifDialog.TYPE.OTHER,R.drawable.loading_gif);
    //        final RequestInfo reqInfo = new RequestInfo();
    //        reqInfo.context = ActivationActivity.this;
    //        reqInfo.reqUrl = HttpConfig.HOST +HttpConfig.INTERFACE_ACTVINFO;
    //        reqInfo.parser = new ActvInfoParse();
    //        HashMap<String,Object> reqDateMap = new HashMap<>();
    //        reqDateMap.put("phone",phone_et.getText().toString().trim());
    ////        reqDateMap.put("code",sms_code.getText().toString().trim());
    //        reqInfo.reqDataMap = reqDateMap;
    //        reqInfo.handler = new Handler(){
    //            @Override
    //            public void handleMessage(Message msg) {
    //                TipGifDialog.dismiss();
    //                ResponseInfo resInfo = (ResponseInfo)msg.obj;
    //                if (resInfo.status ==1 ){
    //                    ActivationCard actvInfo = (ActivationCard) resInfo.responseData;
    //                    if (null != actvInfo){
    //                        String no = actvInfo.no;
    //                        String hidNO = no.replaceAll("(\\d{3})\\d{4}(\\d{4})","$1****$2");
    //                        String hidCode = actvInfo.code;
    //                        card_num_et.setText(hidNO);
    //                        pwd_et.setText(hidCode.replaceAll(actvInfo.code.substring(3),"***"));
    //
    //                    }
    //                }
    //                ToastUtils.show(resInfo.msg);
    //                super.handleMessage(msg);
    //            }
    //        };
    //
    //        ResponseCallback callback = new ResponseCallback(reqInfo);
    //        ReqUtil.getInstance().setReqInfo(reqInfo);
    //        ReqUtil.getInstance().requestPostJSON(callback);
    //
    //    }
}