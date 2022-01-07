package com.kayu.business_car_owner.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import com.kongzue.dialog.v3.TipGifDialog
import com.kayu.business_car_owner.model.SystemParam
import com.kayu.utils.location.LocationManagerUtil
import com.kayu.business_car_owner.wxapi.WXShare
import com.kayu.business_car_owner.model.WashStationDetailBean
import com.kayu.business_car_owner.model.WashStationDetailBean.ServicesDTO.ListDTO
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import com.kayu.business_car_owner.wxapi.WxPayBean
import com.kayu.business_car_owner.wxapi.AliPayBean
import com.kayu.business_car_owner.wxapi.PayResult
import com.amap.api.location.AMapLocation
import com.kayu.business_car_owner.wxapi.OnResponseListener
import com.alipay.sdk.app.PayTask
import com.kayu.business_car_owner.*
import com.kayu.business_car_owner.R
import com.kayu.utils.*
import com.kongzue.dialog.interfaces.OnDismissListener

class WashOrderActivity constructor() : BaseActivity() {
    private var selectedListDTO //已选择的洗车服务
            : ListDTO? = null
    private var order_name: TextView? = null
    private var order_img_bg: ImageView? = null
    private var order_price: TextView? = null
    private var order_sub_price: TextView? = null
    private var order_distance: TextView? = null
    private var order_open_time: TextView? = null
    private var services_type: TextView? = null
    private var services_mode: TextView? = null
    private var order_full_price: TextView? = null
    private var order_rebate_price: TextView? = null
    private var order_sale_price: TextView? = null
    private var order_price_tg: TextView? = null
    private var order_sub_price_tg: TextView? = null
    private var order_pay_btn: TextView? = null
    private var mainViewModel: MainViewModel? = null
    private var wechat_option: ConstraintLayout? = null
    private var alipay_option: ConstraintLayout? = null
    private var wechat_checked: ImageView? = null
    private var alipay_checked: ImageView? = null
    private var payOrderViewModel: PayOrderViewModel? = null
    private var wxShare: WXShare? = null
    private var mWxPayBean: WxPayBean? = null
    private var shopCode: String = ""
    private var mAliPayBean: AliPayBean? = null
    private var payWay: Int = 4 //支付方式 0:微信JSAPI 、1:微信APP 、4:支付宝
    private var isPaying: Boolean = false //是否正在发起支付

    @SuppressLint("HandlerLeak")
    private val mHandler: Handler = object : Handler() {
        public override fun handleMessage(msg: Message) {
            when (msg.what) {
                SDK_PAY_FLAG -> {
                    val payResult = PayResult(msg.obj as Map<String?, String?>)

                    /**
                     * 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
//                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    val resultStatus: String = payResult.resultStatus
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
//                        showAlert(PayDemoActivity.this, getString(R.string.pay_success) + payResult);
                        TipGifDialog.show(this@WashOrderActivity, "支付成功", TipGifDialog.TYPE.SUCCESS)
                            .setOnDismissListener(object : OnDismissListener {
                                public override fun onDismiss() {
                                    if (null != mAliPayBean) {
//                                    onBackPressed();
//                                    onBackPressed();
//                                    FragmentManager fg = requireActivity().getSupportFragmentManager();
//                                    FragmentTransaction fragmentTransaction = fg.beginTransaction();
//                                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//                                    fragmentTransaction.add(R.id.main_root_lay, new WashUnusedActivity(mAliPayBean.orderId,8));
//                                    fragmentTransaction.addToBackStack("ddd");
//                                    fragmentTransaction.commit();
                                        val intent: Intent = Intent(
                                            this@WashOrderActivity,
                                            WashUnusedActivity::class.java
                                        )
                                        intent.putExtra("orderId", mAliPayBean!!.orderId)
                                        intent.putExtra("orderState", 8)
                                        startActivity(intent)
                                        finish()
                                    }
                                }
                            })
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        if (null != mAliPayBean && !TextUtils.equals(resultStatus, "5000")) {
                            payOrderViewModel!!.cancelPay(
                                this@WashOrderActivity,
                                mAliPayBean!!.orderId
                            )
                        }
                        TipGifDialog.show(
                            this@WashOrderActivity,
                            "支付已取消",
                            TipGifDialog.TYPE.WARNING
                        ).setOnDismissListener(object : OnDismissListener {
                            override fun onDismiss() {}
                        })
                    }
                    isPaying = false
                }
                else -> {}
            }
        }
    }
    private var serviceType: String? = null
    private var pay_way_lay: LinearLayout? = null

    //    public WashOrderFragment(WashStationDetailBean.ServicesDTO.ListDTO selectedListDTO,String serviceType ) {
    //        this.selectedListDTO = selectedListDTO;
    //        this.serviceType = serviceType;
    //    }
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wash_order)
        selectedListDTO = getIntent().getParcelableExtra("selectedListDTO")
        serviceType = getIntent().getStringExtra("serviceType")
        shopCode = getIntent().getStringExtra("shopCode").toString()
        mainViewModel = ViewModelProviders.of(this@WashOrderActivity).get(
            MainViewModel::class.java
        )
        payOrderViewModel = ViewModelProviders.of(this@WashOrderActivity).get(
            PayOrderViewModel::class.java
        )

        //标题栏
        findViewById<View>(R.id.title_back_btu).setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                onBackPressed()
            }

            override fun OnMoreErrorClick() {}
        })
        val back_tv: TextView = findViewById(R.id.title_back_tv)
        val title_name: TextView = findViewById(R.id.title_name_tv)
        title_name.setText("全国汽车特惠")
        //        title_name.setVisibility(View.GONE);
//        back_tv.setText("我的");
        order_img_bg = findViewById(R.id.wash_order_img_bg)
        order_name = findViewById(R.id.wash_order_name)
        order_price = findViewById(R.id.wash_order_price)
        order_sub_price = findViewById(R.id.wash_order_sub_price)
        order_distance = findViewById(R.id.wash_order_distance)
        order_open_time = findViewById(R.id.wash_order_time)
        services_type = findViewById(R.id.wash_order_services_type)
        services_mode = findViewById(R.id.wash_order_mode)
        order_full_price = findViewById(R.id.wash_order_full_price)
        order_rebate_price = findViewById(R.id.wash_order_rebate_price)
        order_sale_price = findViewById(R.id.wash_order_sale_price)
        order_price_tg = findViewById(R.id.wash_order_price_tg)
        order_sub_price_tg = findViewById(R.id.wash_order_sub_price_tg)
        pay_way_lay = findViewById(R.id.wash_order_pay_way_lay)
        wechat_option = findViewById(R.id.wash_order_wechat_option)
        wechat_checked = findViewById(R.id.wash_order_wechat_checked)
        alipay_option = findViewById(R.id.wash_order_alipay_option)
        alipay_checked = findViewById(R.id.wash_order_alipay_checked)
        alipay_option?.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                if (!alipay_checked!!.isSelected) {
                    alipay_checked!!.isSelected = true
                    payWay = 4
                }
                if (wechat_checked!!.isSelected) wechat_checked!!.isSelected = false
            }

            override fun OnMoreErrorClick() {}
        })
        wechat_option!!.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                if (!wechat_checked!!.isSelected) {
                    wechat_checked!!.isSelected = true
                    payWay = 1
                }
                if (alipay_checked!!.isSelected) alipay_checked!!.isSelected = false
            }

            override fun OnMoreErrorClick() {}
        })
        order_pay_btn = findViewById(R.id.wash_order_pay_btn)
        order_pay_btn!!.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                if (isPaying) {
                    return
                }
                isPaying = true
                if (payWay == 1) {
                    wechatPayOrder()
                } else {
                    aliapyPayOrder()
                }
            }

            override fun OnMoreErrorClick() {}
        })
        mainViewModel!!.getWashStoreDetail(this@WashOrderActivity, shopCode)
            .observe(this@WashOrderActivity, object : Observer<WashStationDetailBean?> {
                public override fun onChanged(washStationDetailBean: WashStationDetailBean?) {
                    if (null != washStationDetailBean) initViewData(washStationDetailBean)
                }
            })
        mainViewModel!!.getSysParameter(this@WashOrderActivity, 20)
            .observe(this@WashOrderActivity, object : Observer<SystemParam?> {
                public override fun onChanged(systemParam: SystemParam?) {
                    if (null == systemParam) {
                        return
                    }
                    if (!StringUtil.isEmpty(systemParam.content.trim { it <= ' ' })) {
                        val arr: Array<String> =
                            systemParam.content.split("#".toRegex()).toTypedArray()
                        //                    LogUtil.e("hm","截取---"+arr[0]
                        for (x in arr.indices) {
                            if ((arr.get(x) == "支付宝")) {
                                alipay_option?.visibility = View.VISIBLE
                                if (x == 0) {
                                    alipay_checked?.isSelected = true
                                    payWay = 4
                                    pay_way_lay?.visibility = View.VISIBLE
                                    order_pay_btn?.visibility = View.VISIBLE
                                }
                            } else if ((arr.get(x) == "微信")) {
                                wechat_option?.visibility = View.VISIBLE
                                if (x == 0) {
                                    wechat_checked?.isSelected = true
                                    payWay = 1
                                    pay_way_lay?.visibility = View.VISIBLE
                                    order_pay_btn?.visibility = View.VISIBLE
                                }
                            }
                        }
                    }
                }
            })
    }

    //    @Override
    //    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    //                             Bundle savedInstanceState) {
    //        mainViewModel = ViewModelProviders.of(WashOrderFragment.this).get(MainViewModel.class);
    //        payOrderViewModel = ViewModelProviders.of(WashOrderFragment.this).get(PayOrderViewModel.class);
    //        return inflater.inflate(R.layout.fragment_wash_order, container, false);
    //    }
    //    @Override
    //    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    //        super.onViewCreated(view, savedInstanceState);
    //
    //    }
    private fun initViewData(washStation: WashStationDetailBean) {
        shopCode = washStation.shopCode.toString()
        services_type!!.setText(serviceType)
        val sdf: Array<String>? = selectedListDTO!!.serviceName?.split("-".toRegex())?.toTypedArray()
        if (null != sdf && sdf.size > 0) {
            services_mode!!.setText(sdf.get(1))
        } else {
            if (selectedListDTO!!.carModel == 1) {
                services_mode!!.setText("小轿车")
            }
            if (selectedListDTO!!.carModel == 2) {
                services_mode!!.setText("SUV/MPV")
            }
            if (selectedListDTO!!.carModel == 3) {
                services_mode!!.setText("全车型")
            }
        }
        KWApplication.instance.loadImg(washStation.doorPhotoUrl, order_img_bg!!)
        order_name!!.setText(washStation.shopName)
        val sb: StringBuffer = StringBuffer()
        if ((washStation.isOpen == "1")) {
            sb.append("营业中 | ")
        } else {
            sb.append("休息中 | ")
        }
        sb.append(washStation.openTimeStart).append("-").append(washStation.openTimeEnd)
        order_price!!.setText(selectedListDTO!!.finalPrice)
        order_sale_price!!.setText(selectedListDTO!!.finalPrice)
        order_price_tg!!.setText(selectedListDTO!!.finalPrice)
        order_sub_price!!.setText("￥" + selectedListDTO!!.price)
        order_full_price!!.setText("￥" + selectedListDTO!!.price)
        order_sub_price_tg!!.setText("￥" + selectedListDTO!!.price)
        val rebatePirce: String =
            (selectedListDTO!!.finalPrice?.toDouble()
                ?.let { selectedListDTO!!.price?.toDouble()?.minus(it) }).toString()
        order_rebate_price!!.setText("-￥" + rebatePirce)
        val location: AMapLocation? = LocationManagerUtil.self?.loccation
        if (null != location) {
            val latitude: Double = location.latitude
            val longitude: Double = location.longitude
            val dis: Double = GetJuLiUtils.getDistance(
                longitude,
                latitude,
                washStation.longitude?.toDouble()!!,
                washStation.latitude?.toDouble()!!
            )
            order_distance!!.setText("距您" + dis + "km")
        }
        order_open_time!!.setText(sb.toString())
    }

    private fun wechatPayOrder() {
        wxShare = WXShare(this@WashOrderActivity)
        wxShare!!.register()
        wxShare!!.setListener(object : OnResponseListener {
            public override fun onSuccess() {
//                LogUtil.e("hm", "支付成功");
                TipGifDialog.show(this@WashOrderActivity, "支付成功", TipGifDialog.TYPE.SUCCESS)
                    .setOnDismissListener(object : OnDismissListener {
                        public override fun onDismiss() {
                            if (null != mWxPayBean) {
//                            onBackPressed();
//                            onBackPressed();
//                            FragmentManager fg = requireActivity().getSupportFragmentManager();
//                            FragmentTransaction fragmentTransaction = fg.beginTransaction();
//                            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//                            fragmentTransaction.add(R.id.main_root_lay, new WashUnusedActivity(mWxPayBean.orderId,8));
//                            fragmentTransaction.addToBackStack("ddd");
//                            fragmentTransaction.commit();
                                val intent: Intent =
                                    Intent(this@WashOrderActivity, WashUnusedActivity::class.java)
                                intent.putExtra("orderId", mWxPayBean!!.orderId)
                                intent.putExtra("orderState", 8)
                                startActivity(intent)
                                finish()
                            }
                        }
                    })
                isPaying = false
            }

            public override fun onCancel() {
//                LogUtil.e("hm", "支付已取消");
                if (null != mWxPayBean) {
                    mWxPayBean!!.orderId?.let {
                        payOrderViewModel!!.cancelPay(this@WashOrderActivity,
                            it
                        )
                    }
                }
                TipGifDialog.show(this@WashOrderActivity, "支付已取消", TipGifDialog.TYPE.WARNING)
                    .setOnDismissListener(object : OnDismissListener {
                        public override fun onDismiss() {}
                    })
                isPaying = false
            }

            public override fun onFail(message: String) {
                LogUtil.e("hm", message)
                TipGifDialog.show(this@WashOrderActivity, message, TipGifDialog.TYPE.ERROR)
                    .setOnDismissListener(object : OnDismissListener {
                        public override fun onDismiss() {
                            if (null != mWxPayBean) {
                                mWxPayBean!!.orderId?.let {
                                    payOrderViewModel!!.cancelPay(
                                        this@WashOrderActivity,
                                        it
                                    )
                                }
                            }
                            TipGifDialog.show(
                                this@WashOrderActivity,
                                "支付失败",
                                TipGifDialog.TYPE.ERROR
                            ).setOnDismissListener(object : OnDismissListener {
                                public override fun onDismiss() {}
                            })
                        }
                    })
                isPaying = false
            }
        })
        payOrderViewModel!!.getWeChatPayInfo(
            this@WashOrderActivity,
            shopCode,
            selectedListDTO!!.serviceCode!!
        ).observe(this,
            { wxPayBean ->
                mWxPayBean = wxPayBean
                reqWxPay(wxPayBean)
            })
    }

    private fun aliapyPayOrder() {
        payOrderViewModel!!.getAliPayInfo(
            this@WashOrderActivity,
            shopCode,
            selectedListDTO!!.serviceCode!!
        ).observe(this,
            { aliPayBean ->
                mAliPayBean = aliPayBean
                val payRunnable: Runnable = object : Runnable {
                    override fun run() {
                        val alipay = PayTask(this@WashOrderActivity)
                        val result: Map<String, String> = alipay.payV2(aliPayBean?.body, true)
                        LogUtil.e("alipay", result.toString())
                        val msg = Message()
                        msg.what = SDK_PAY_FLAG
                        msg.obj = result
                        mHandler.sendMessage(msg)
                    }
                }
                // 必须异步调用
                val payThread = Thread(payRunnable)
                payThread.start()
            })
    }

    private fun reqWxPay(wxPayBean: WxPayBean?) {
        if (null != wxPayBean) {
            wxShare!!.getWXPay(wxPayBean, null)
        }
    }

    companion object {
        //    private long vipId;
        private val SDK_PAY_FLAG: Int = 1
    }
}