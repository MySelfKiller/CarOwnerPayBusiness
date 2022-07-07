//package com.kayu.business_car_owner.activity
//
//import android.annotation.SuppressLint
//import android.content.Intent
//import android.os.Bundle
//import android.os.Handler
//import android.os.Message
//import android.text.InputType
//import android.view.View
//import android.widget.EditText
//import android.widget.TextView
//import androidx.appcompat.app.AppCompatActivity
//import androidx.appcompat.widget.AppCompatButton
//import com.kayu.utils.SMSCountDownTimer
//import com.kayu.utils.NoMoreClickListener
//import com.kayu.form_verify.Validate
//import com.kayu.form_verify.validator.PhoneValidator
//import com.hjq.toast.ToastUtils
//import com.kayu.business_car_owner.R
//import com.kongzue.dialog.v3.TipGifDialog
//import com.kayu.business_car_owner.http.parser.NormalParse
//import com.kayu.business_car_owner.data_parser.OrderDetailParse
//import com.kayu.business_car_owner.model.OrderDetailBean
//import com.kayu.business_car_owner.http.*
//import com.kayu.form_verify.Form
//import java.util.HashMap
//
//class OrderTrackingActivity constructor() : AppCompatActivity() {
//    private var ver_code_et: EditText? = null
//    private var phone_et: EditText? = null
//    private var send_ver_code: TextView? = null
//    private var ask_btn: AppCompatButton? = null
//    private var timer: SMSCountDownTimer? = null
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_order_tracking)
//
//        //标题栏
////        LinearLayout title_lay = findViewById(R.id.title_lay);
////        title_lay.setBackgroundColor(getResources().getColor(R.color.background_gray));
//        val title_name: TextView = findViewById(R.id.title_name_tv)
//        title_name.setText("查询订单")
//        findViewById<View>(R.id.title_back_btu).setOnClickListener(object : NoMoreClickListener() {
//            override fun OnMoreClick(view: View) {
//                onBackPressed()
//            }
//
//            override fun OnMoreErrorClick() {}
//        })
//        //        TextView back_tv = view.findViewById(R.id.title_back_tv);
////        back_tv.setText("我的");
//        phone_et = findViewById(R.id.tracking_phone_et)
//        ver_code_et = findViewById(R.id.tracking_ver_code_et)
//        send_ver_code = findViewById(R.id.tracking_send_ver_code)
//        ask_btn = findViewById(R.id.tracking_ask_btn)
//        phone_et?.inputType = InputType.TYPE_CLASS_NUMBER
//        ver_code_et?.inputType = InputType.TYPE_CLASS_NUMBER
//        timer = SMSCountDownTimer(send_ver_code!!, 60 * 1000 * 2, 1000)
//        send_ver_code?.setOnClickListener(object : NoMoreClickListener() {
//            override fun OnMoreClick(view: View) {
//                val form: Form = Form()
//                val phoneValiv: Validate = Validate(phone_et)
//                phoneValiv.addValidator(PhoneValidator(this@OrderTrackingActivity))
//                form.addValidates(phoneValiv)
//                val isOk: Boolean = form.validate()
//                if (isOk) {
//                    timer!!.start()
//                    sendSmsRequest()
//                }
//            }
//
//            override fun OnMoreErrorClick() {}
//        })
//        ask_btn = findViewById(R.id.tracking_ask_btn)
//        ask_btn?.setOnClickListener(object : NoMoreClickListener() {
//            override fun OnMoreClick(view: View) {
//                val form: Form = Form()
//                val phoneValiv: Validate = Validate(phone_et)
//                phoneValiv.addValidator(PhoneValidator(this@OrderTrackingActivity))
//                form.addValidates(phoneValiv)
//                val smsValiv: Validate = Validate(ver_code_et)
//                form.addValidates(smsValiv)
//                val isOk: Boolean = form.validate()
//                if (isOk) {
////                    sendSubRequest();
//                    getInfo(
//                        phone_et?.text.toString().trim { it <= ' ' },
//                        ver_code_et?.text.toString().trim { it <= ' ' }
//                    )
//                }
//            }
//
//            override fun OnMoreErrorClick() {}
//        })
//    }
//
//    @SuppressLint("HandlerLeak")
//    private fun sendSmsRequest() {
//        TipGifDialog.show(
//            this@OrderTrackingActivity,
//            "发送验证码...",
//            TipGifDialog.TYPE.OTHER,
//            R.drawable.loading_gif
//        )
//        val reqInfo: RequestInfo = RequestInfo()
//        reqInfo.context = this@OrderTrackingActivity
//        reqInfo.reqUrl = HttpConfig.HOST + HttpConfig.INTERFACE_ORDER_CODE
//        reqInfo.parser = NormalParse()
//        val reqDateMap: HashMap<String, Any> = HashMap()
//        reqDateMap.put("", phone_et!!.getText().toString().trim({ it <= ' ' }))
//        reqInfo.reqDataMap = reqDateMap
//        reqInfo.handler = object : Handler() {
//            public override fun handleMessage(msg: Message) {
//                TipGifDialog.dismiss()
//                val resInfo: ResponseInfo = msg.obj as ResponseInfo
//                if (resInfo.status == 1) {
//                    ToastUtils.show("验证码发送成功")
//                } else {
//                    timer!!.clear()
//                    ToastUtils.show(resInfo.msg)
//                }
//                super.handleMessage(msg)
//            }
//        }
//        val callback: ResponseCallback = ResponseCallback(reqInfo)
//        ReqUtil.setReqInfo(reqInfo)
//        ReqUtil.requestGetJSON(callback)
//    }
//
//    @SuppressLint("HandlerLeak")
//    private fun getInfo(phone: String, code: String) {
//        TipGifDialog.show(
//            this@OrderTrackingActivity,
//            "查询中...",
//            TipGifDialog.TYPE.OTHER,
//            R.drawable.loading_gif
//        )
//        val reqInfo: RequestInfo = RequestInfo()
//        reqInfo.context = this@OrderTrackingActivity
//        reqInfo.reqUrl = HttpConfig.HOST + HttpConfig.INTERFACE_ORDER_DETAIL
//        reqInfo.parser = OrderDetailParse()
//        val reqDateMap: HashMap<String, Any> = HashMap()
//        reqDateMap.put("phone", phone)
//        reqDateMap.put("code", code)
//
////        reqDateMap.put("code",sms_code.getText().toString().trim());
//        reqInfo.reqDataMap = reqDateMap
//        reqInfo.handler = object : Handler() {
//            public override fun handleMessage(msg: Message) {
//                TipGifDialog.dismiss()
//                val resInfo: ResponseInfo = msg.obj as ResponseInfo
//                if (resInfo.status == 1) {
//                    val orderDetailBean: OrderDetailBean? = resInfo.responseData as OrderDetailBean?
//                    if (null != orderDetailBean) {
//                        val intent: Intent =
//                            Intent(this@OrderTrackingActivity, OrderDetailsActivity::class.java)
//                        intent.putExtra("waybillNo", orderDetailBean.waybillNo)
//                        intent.putExtra("cardNo", orderDetailBean.cardNo)
//                        intent.putExtra("cardCode", orderDetailBean.cardCode)
//                        startActivity(intent)
//                    }
//                } else {
//                    ToastUtils.show(resInfo.msg)
//                }
//                super.handleMessage(msg)
//            }
//        }
//        val callback: ResponseCallback = ResponseCallback(reqInfo)
//        ReqUtil.setReqInfo(reqInfo)
//        ReqUtil.requestPostJSON(callback)
//    }
//}