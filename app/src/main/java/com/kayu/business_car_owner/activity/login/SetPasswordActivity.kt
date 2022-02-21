package com.kayu.business_car_owner.activity.login

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import com.kayu.form_verify.Validate
import com.kayu.form_verify.validator.NotEmptyValidator
import com.hjq.toast.ToastUtils
import com.kongzue.dialog.v3.TipGifDialog
import com.kayu.business_car_owner.http.parser.NormalParse
import com.kayu.form_verify.validate.ConfirmValidate
import com.kayu.business_car_owner.*
import com.kayu.business_car_owner.R
import com.kayu.business_car_owner.activity.*
import com.kayu.business_car_owner.http.*
import com.kayu.form_verify.Form
import com.kayu.utils.*
import java.util.HashMap

class SetPasswordActivity constructor() : BaseActivity() {
    private var title: String? = "标题"
    private var back: String? = ""
    private var set_new_password: EditText? = null
    private var set_old_password: EditText? = null
    private var set_new_password2: EditText? = null
    private var set_ask_btn: AppCompatButton? = null
    private var isSetPwd: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_password)
        val intent: Intent = getIntent()
        title = intent.getStringExtra("title")
        back = intent.getStringExtra("back")
        isSetPwd = intent.getBooleanExtra("isSetPwd", false)

        //标题栏
        val title_name: TextView = findViewById(R.id.title_name_tv)
        title_name.setText(title)
        val back_lay: LinearLayout = findViewById(R.id.title_back_btu)
        if (StringUtil.isEmpty(back)) {
            back_lay.setVisibility(View.INVISIBLE)
        } else {
            back_lay.setVisibility(View.VISIBLE)
        }
        back_lay.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                onBackPressed()
            }

            override fun OnMoreErrorClick() {}
        })
        val back_tv: TextView = findViewById(R.id.title_back_tv)
        back_tv.setText(back)
        val old_pwd_lay: LinearLayout = findViewById(R.id.set_old_pwd_lay)
        if (isSetPwd) {
            old_pwd_lay.setVisibility(View.GONE)
        } else {
            old_pwd_lay.setVisibility(View.VISIBLE)
        }
        set_old_password = findViewById(R.id.set_old_password)
        set_new_password = findViewById(R.id.set_new_password)
        set_new_password2 = findViewById(R.id.set_new_password2)
        set_ask_btn = findViewById(R.id.set_ask_btn)
        set_ask_btn?.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                val form: Form = Form()
                if (!isSetPwd) {
                    val codeValidate: Validate = Validate(set_old_password)
                    codeValidate.addValidator(NotEmptyValidator(this@SetPasswordActivity))
                    form.addValidates(codeValidate)
                }
                val newPassValidate: ConfirmValidate =
                    ConfirmValidate(set_new_password, set_new_password2)
                newPassValidate.addValidator(NotEmptyValidator(this@SetPasswordActivity))
                form.addValidates(newPassValidate)
                if (form.validate()) {
                    reqSetPasswrod()
                }
            }

            override fun OnMoreErrorClick() {}
        })
    }

    @SuppressLint("HandlerLeak")
    private fun reqSetPasswrod() {
        TipGifDialog.show(
            this@SetPasswordActivity,
            "稍等...",
            TipGifDialog.TYPE.OTHER,
            R.drawable.loading_gif
        )
        val reqInfo: RequestInfo = RequestInfo()
        reqInfo.context = this@SetPasswordActivity
        reqInfo.reqUrl = HttpConfig.HOST + HttpConfig.INTERFACE_SET_PASSWORD
        reqInfo.parser = NormalParse()
        val reqDateMap: HashMap<String, Any> = HashMap()
        if (!isSetPwd) {
            reqDateMap.put("oldPwd", set_old_password!!.getText().toString().trim({ it <= ' ' }))
        }
        reqDateMap.put("newPwd", set_new_password2!!.getText().toString().trim({ it <= ' ' }))
        //        reqDateMap.put("code",sms_code.getText().toString().trim());
        reqInfo.reqDataMap = reqDateMap
        reqInfo.handler = object : Handler() {
            public override fun handleMessage(msg: Message) {
                TipGifDialog.dismiss()
                val resInfo: ResponseInfo = msg.obj as ResponseInfo
                if (resInfo.status == 1) {
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

    private var firstTime: Long = 0
    public override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isSetPwd) {
                val secondTime: Long = System.currentTimeMillis()
                if (secondTime - firstTime > 2000) {
                    ToastUtils.show("再按一次退出应用")
                    firstTime = secondTime
                    return true
                } else {
                    System.exit(0)
                }
            } else {
                onBackPressed()
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}