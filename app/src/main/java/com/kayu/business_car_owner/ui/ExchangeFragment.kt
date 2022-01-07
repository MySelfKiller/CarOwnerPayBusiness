package com.kayu.business_car_owner.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.kayu.utils.NoMoreClickListener
import androidx.fragment.app.Fragment
import com.kongzue.dialog.v3.MessageDialog
import com.kayu.business_car_owner.R
import com.kayu.form_verify.Validate
import com.kayu.form_verify.validator.NotEmptyValidator
import com.kongzue.dialog.v3.TipGifDialog
import com.kayu.business_car_owner.http.parser.LoginDataParse
import com.kayu.business_car_owner.http.*
import com.kayu.form_verify.Form
import java.util.HashMap

class ExchangeFragment : Fragment() {
    private var apply_btn: AppCompatButton? = null
    private var code_et: EditText? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_exchange, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //标题栏
        view.findViewById<View>(R.id.title_back_btu)
            .setOnClickListener(object : NoMoreClickListener() {
                override fun OnMoreClick(view: View) {
                    requireActivity().onBackPressed()
                }

                override fun OnMoreErrorClick() {}
            })
        //        TextView back_tv = view.findViewById(R.id.title_back_tv);
        val title_name = view.findViewById<TextView>(R.id.title_name_tv)
        title_name.text = "兑换码"
        //        back_tv.setText("首页");
        code_et = view.findViewById(R.id.exchange_code_et)
        apply_btn = view.findViewById(R.id.exchange_apply_btn)
        apply_btn?.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                val form = Form()
                val codeValiv = Validate(code_et)
                codeValiv.addValidator(NotEmptyValidator(context))
                form.addValidates(codeValiv)
                val isOk = form.validate()
                if (isOk) {
                    sendSubRequest()
                }
            }

            override fun OnMoreErrorClick() {}
        })
    }

    @SuppressLint("HandlerLeak")
    private fun sendSubRequest() {
        TipGifDialog.show(
            context as AppCompatActivity?,
            "稍等...",
            TipGifDialog.TYPE.OTHER,
            R.drawable.loading_gif
        )
        val reqInfo = RequestInfo()
        reqInfo.context = context
        reqInfo.reqUrl = HttpConfig.HOST + HttpConfig.INTERFACE_GET_EXCHANGE
        reqInfo.parser = LoginDataParse()
        val reqDateMap = HashMap<String, Any>()
        reqDateMap["code"] = code_et!!.text.toString().trim { it <= ' ' }
        //        reqDateMap.put("code",sms_code.getText().toString().trim());
        reqInfo.reqDataMap = reqDateMap
        reqInfo.handler = object : Handler() {
            override fun handleMessage(msg: Message) {
                TipGifDialog.dismiss()
                val resInfo = msg.obj as ResponseInfo
                if (resInfo.status == 1) {
                    MessageDialog.show(
                        (context as AppCompatActivity?)!!,
                        "兑换成功",
                        "",
                        "继续兑换",
                        "返回首页"
                    )
                        .setCancelable(false)
                        .setOkButton { baseDialog, v ->
                            baseDialog.doDismiss()
                            false
                        }
                        .setCancelButton { baseDialog, v ->
                            baseDialog.doDismiss()
                            requireActivity().onBackPressed()
                            false
                        }
                } else {
                    TipGifDialog.show(
                        context as AppCompatActivity?,
                        resInfo.msg,
                        TipGifDialog.TYPE.WARNING
                    )
                }
                super.handleMessage(msg)
            }
        }
        val callback = ResponseCallback(reqInfo)
        ReqUtil.setReqInfo(reqInfo)
        ReqUtil.requestPostJSON(callback)
    }
}