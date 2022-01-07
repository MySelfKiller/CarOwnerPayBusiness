package com.kayu.business_car_owner

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Message
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kayu.business_car_owner.wxapi.WxPayBean
import com.hjq.toast.ToastUtils
import com.kongzue.dialog.v3.TipGifDialog
import com.kayu.business_car_owner.wxapi.AliPayBean
import com.kayu.business_car_owner.data_parser.WxPayDataParse
import com.kayu.business_car_owner.data_parser.AliPayDataParse
import com.kayu.business_car_owner.http.*
import com.kayu.business_car_owner.http.parser.NormalIntParse
import java.util.HashMap

class PayOrderViewModel : ViewModel() {
    private var wxPayLiveData: MutableLiveData<WxPayBean?>? = null
    private var alipayLiveData: MutableLiveData<AliPayBean?>? = null
    fun getAliPayInfo(
        context: Context,
        shopCode: String,
        serviceCode: String
    ): LiveData<AliPayBean?> {
        alipayLiveData = MutableLiveData()
        loadPayInfo(context, shopCode, serviceCode, 4)
        return alipayLiveData!!
    }

    fun getWeChatPayInfo(
        context: Context,
        shopCode: String,
        serviceCode: String
    ): LiveData<WxPayBean?> {
        wxPayLiveData = MutableLiveData()
        loadPayInfo(context, shopCode, serviceCode, 1)
        return wxPayLiveData!!
    }

    fun cancelPay(context: Context, orderId: Long) {
        cancelPayInfo(context, orderId)
    }

    @SuppressLint("HandlerLeak")
    private fun loadPayInfo(context: Context, shopCode: String, serviceCode: String, payFlag: Int) {
        TipGifDialog.show(
            context as AppCompatActivity?,
            "获取支付信息...",
            TipGifDialog.TYPE.OTHER,
            R.drawable.loading_gif
        )
        val reques = RequestInfo()
        reques.context = context
        //        if (orderFlag == 1) {
//            reques.reqUrl = HttpConfig.HOST + HttpConfig.interface_get_pay_course;
//        } else {
//        }
        reques.reqUrl = HttpConfig.HOST + HttpConfig.INTERFACE_WASH_PAY
        val reqDateMap: HashMap<String, Any> = HashMap()
        reqDateMap.put("shopCode", shopCode)
        reqDateMap.put("serviceCode", serviceCode)
        reqDateMap.put("payWay", payFlag)
        reques.reqDataMap = reqDateMap
        if (payFlag == 1) {
            reques.parser = WxPayDataParse()
        } else {
            reques.parser = AliPayDataParse()
        }
        reques.handler = object : Handler() {
            public override fun handleMessage(msg: Message) {
                val resInfo: ResponseInfo = msg.obj as ResponseInfo
                TipGifDialog.dismiss()
                if (resInfo.status == 1) {
                    if (payFlag == 1) { //微信订单
                        val wxPayBean: WxPayBean? = resInfo.responseData as WxPayBean?
                        wxPayLiveData!!.setValue(wxPayBean)
                    } else { //支付宝订单信息
                        val wxPayBean: AliPayBean? = resInfo.responseData as AliPayBean?
                        alipayLiveData!!.setValue(wxPayBean)
                    }
                } else {
                    ToastUtils.show(resInfo.msg)
                }
                super.handleMessage(msg)
            }
        }
        val callback = ResponseCallback(reques)
        ReqUtil.setReqInfo(reques)
        ReqUtil.requestPostJSON(callback)
    }

    @SuppressLint("HandlerLeak")
    private fun cancelPayInfo(context: Context, orderId: Long) {
        val reques: RequestInfo = RequestInfo()
        reques.context = context
        reques.reqUrl = HttpConfig.HOST + HttpConfig.INTERFACE_WASH_PAY_CANCEL
        val reqDateMap: HashMap<String, Any> = HashMap()
        reqDateMap.put("id", orderId)
        reques.reqDataMap = reqDateMap
        reques.parser = NormalIntParse()
        reques.handler = object : Handler() {
            public override fun handleMessage(msg: Message) {
//                ResponseInfo resInfo = (ResponseInfo) msg.obj;
                super.handleMessage(msg)
            }
        }
        val callback: ResponseCallback = ResponseCallback(reques)
        ReqUtil.setReqInfo(reques)
        ReqUtil.requestPostJSON(callback)
    }
}