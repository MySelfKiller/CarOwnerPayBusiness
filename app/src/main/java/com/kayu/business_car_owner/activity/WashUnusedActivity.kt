package com.kayu.business_car_owner.activity

import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.kayu.utils.location.LocationManagerUtil
import com.kayu.utils.permission.EasyPermissions.DialogCallback
import com.kongzue.dialog.v3.MessageDialog
import com.kayu.utils.permission.EasyPermissions
import com.kayu.business_car_owner.model.WashOrderDetailBean
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import com.amap.api.location.AMapLocation
import com.youth.banner.Banner
import com.youth.banner.BannerConfig
import com.kayu.business_car_owner.*
import com.kayu.business_car_owner.R
import com.kayu.utils.*
import com.kayu.utils.callback.Callback

class WashUnusedActivity constructor() : BaseActivity() {
    private var station_banner: Banner? = null
    private var station_open_time: TextView? = null
    private var station_name: TextView? = null
    private var station_address: TextView? = null
    private var station_distance: TextView? = null
    private var navi_lay: LinearLayout? = null
    private var phone_lay: LinearLayout? = null
    private var orderId: Long? = null
    private var orderState //0:待支付 1:已支付待使用 2:已取消 3:已使用 4:退款中 5:已退款 6:支付失败、7:退款失败 8:本机支付成功后自定义的状态
            : Int = 0
    private var qr_img: ImageView? = null
    private var qr_state_img: ImageView? = null
    private var qr_string: TextView? = null
    private var valid_time: TextView? = null
    private var order_number: TextView? = null
    private var tag_order_nom: TextView? = null
    private var tag_pay_time: TextView? = null
    private var tag_expire_time: TextView? = null
    private var order_state: TextView? = null
    private var pay_time: TextView? = null
    private var expire_time: TextView? = null
    private var store_name: TextView? = null
    private var services_type: TextView? = null
    private var services_model: TextView? = null
    private var full_price: TextView? = null
    private var rebate_price: TextView? = null
    private var sale_price: TextView? = null
    private var unused_refund: TextView? = null
    private var unused_navi_btn: TextView? = null
    private var state_img: ImageView? = null
    private var state_tv: TextView? = null
    private var state_lay: ConstraintLayout? = null
    private var mainViewModel: MainViewModel? = null
    private var explain_tv: TextView? = null
    private var explain_img: ImageView? = null
    private var unused_refund_lay: ConstraintLayout? = null
    private var qr_code_lay: ConstraintLayout? = null

    //    public WashUnusedActivity(Long orderId, Integer orderState) {
    //        this.orderId = orderId;
    //        this.orderState = orderState;
    //    }
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wash_unused)
        orderId = getIntent().getLongExtra("orderId", 0)
        orderState = getIntent().getIntExtra("orderState", -1)
        mainViewModel = ViewModelProviders.of(this@WashUnusedActivity).get(
            MainViewModel::class.java
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
        title_name.setText("全国洗车特惠")
        //        title_name.setVisibility(View.GONE);
        back_tv.setText("洗车订单")
        state_lay = findViewById(R.id.wash_unused_state_Lay)
        state_img = findViewById(R.id.wash_unused_state_img)
        state_tv = findViewById(R.id.wash_unused_state_tv)
        station_banner = findViewById(R.id.wash_unused_banner)
        station_name = findViewById(R.id.wash_unused_name)
        station_open_time = findViewById(R.id.wash_unused_time)
        station_address = findViewById(R.id.wash_unused_location)
        station_distance = findViewById(R.id.wash_unused_distance)
        navi_lay = findViewById(R.id.wash_unused_navi_lay)
        phone_lay = findViewById(R.id.wash_unused_phone_lay)
        qr_code_lay = findViewById(R.id.wash_unused_qr_code_lay)
        qr_img = findViewById(R.id.wash_unused_qr_img)
        qr_state_img = findViewById(R.id.wash_unused_qr_state_img)
        qr_string = findViewById(R.id.wash_unused_qr_code)
        valid_time = findViewById(R.id.wash_unused_valid_time)
        explain_tv = findViewById(R.id.wash_unused_explain_tv)
        explain_img = findViewById(R.id.wash_unused_explain_img)
        tag_order_nom = findViewById(R.id.id_tag_order_nom)
        tag_pay_time = findViewById(R.id.id_tag_order_pay_time)
        tag_expire_time = findViewById(R.id.id_tag_order_expire_time)
        order_number = findViewById(R.id.wash_unused_order_number)
        order_state = findViewById(R.id.wash_unused_order_state)
        pay_time = findViewById(R.id.wash_unused_pay_time)
        expire_time = findViewById(R.id.wash_unused_expire_time)
        store_name = findViewById(R.id.wash_unused_store_name)
        services_type = findViewById(R.id.wash_unused_services_type)
        services_model = findViewById(R.id.wash_unused_model)
        full_price = findViewById(R.id.wash_unused_full_price)
        rebate_price = findViewById(R.id.wash_unused_rebate)
        sale_price = findViewById(R.id.wash_unused_sale_price)

        //退款按钮
        unused_refund_lay = findViewById(R.id.wash_unused_refund_lay)
        unused_refund = findViewById(R.id.wash_unused_refund)
        unused_navi_btn = findViewById(R.id.wash_unused_navi_btn)
        mainViewModel!!.getWashOrderDetail(this@WashUnusedActivity, orderId)
            .observe(this@WashUnusedActivity, object : Observer<WashOrderDetailBean?> {
                public override fun onChanged(orderDetailBean: WashOrderDetailBean?) {
                    if (null != orderDetailBean) {
                        initViewData(orderDetailBean)
                    }
                }
            })
    }

    //    @Override
    //    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    //                             Bundle savedInstanceState) {
    //        mainViewModel = ViewModelProviders.of(requireActivity()).get(MainViewModel.class);
    //        return inflater.inflate(R.layout.fragment_wash_unused, container, false);
    //    }
    //    @Override
    //    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    //        super.onViewCreated(view, savedInstanceState);
    //
    //    }
    private fun initViewData(washStation: WashOrderDetailBean) {
        if (null != washStation.doorImgList) {
            station_banner!!.setBannerStyle(BannerConfig.CIRCLE_INDICATOR)
                .setIndicatorGravity(BannerConfig.RIGHT)
                .setImageLoader(BannerImageLoader())
                .setImages(washStation.doorImgList)
                .setDelayTime(2000)
                .start()
        }
        station_name!!.setText(washStation.shopName)
        station_address!!.setText(washStation.address)

//        StringBuffer sb = new StringBuffer();
//        if (washStation.isOpen.equals("1")) {
//            sb.append("营业中 | ");
//        } else {
//            sb.append("休息中 | ");
//
//        }
        station_open_time!!.setText("营业时间：" + washStation.busTime)
        val location: AMapLocation? = LocationManagerUtil.self?.loccation
        if (null != location) {
            val latitude: Double = location.getLatitude()
            val longitude: Double = location.getLongitude()
            val dis: Double = GetJuLiUtils.getDistance(
                longitude,
                latitude,
                washStation.longitude!!.toDouble(),
                washStation.latitude!!.toDouble()
            )
            station_distance!!.setText("距您" + dis + "km")
        }
        navi_lay!!.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                KWApplication.instance.toNavi(
                    this@WashUnusedActivity,
                    washStation.latitude!!,
                    washStation.longitude!!,
                    washStation.address!!,
                    "BD09"
                )
            }

            override fun OnMoreErrorClick() {}
        })
        phone_lay!!.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                permissionsCheck(
                    arrayOf(Manifest.permission.CALL_PHONE),
                    R.string.permiss_call_phone,
                    object : Callback {
                        public override fun onSuccess() {
                            KWApplication.instance.callPhone(this@WashUnusedActivity, washStation.telephone!!)
                        }

                        public override fun onError() {}
                    })
            }

            override fun OnMoreErrorClick() {}
        })
        store_name!!.setText(washStation.shopName)
        order_number!!.setText(washStation.orderNo)
        val orderStateStr: String
        var qr_color: Int = Color.LTGRAY
        when (washStation.state) {
            0 -> {
                //                if (isAdded())
                state_img!!.setImageResource(R.mipmap.ic_unpaid_time)
                orderStateStr = "待支付"
                state_lay!!.setVisibility(View.VISIBLE)
                qr_state_img!!.setVisibility(View.GONE)
            }
            1 -> {
                if (orderState == 8) {
//                    if (isAdded())
                    state_img!!.setImageResource(R.mipmap.ic_selected)

//            state_img.setImageDrawable(getActivity().getResources().getDrawable(R.mipmap.ic_unpaid_time));
                    orderStateStr = "支付成功"
                } else {
                    orderStateStr = "待使用"
                }
                state_lay!!.setVisibility(View.VISIBLE)
                qr_color = Color.BLACK
                qr_state_img!!.setVisibility(View.GONE)
                //                if (isAdded())
                state_img!!.setImageResource(R.mipmap.ic_selected)
            }
            2 -> {
                orderStateStr = "已取消"
                //                if (isAdded())
                state_img!!.setImageResource(R.mipmap.ic_pay_state_cancle)
                state_lay!!.setVisibility(View.VISIBLE)
                qr_state_img!!.setVisibility(View.GONE)
            }
            3 -> {
                orderStateStr = "已使用"
                qr_color = Color.LTGRAY
                //                if (isAdded())
                state_img!!.setImageResource(R.mipmap.ic_selected)
                state_lay!!.setVisibility(View.VISIBLE)
                qr_state_img!!.setVisibility(View.GONE)
            }
            4 -> {
                qr_state_img!!.setVisibility(View.VISIBLE)
                qr_state_img!!.setImageResource(R.mipmap.ic_refunding)
                orderStateStr = "退款中"
                qr_color = Color.LTGRAY
                state_lay!!.setVisibility(View.GONE)
            }
            5 -> {
                qr_state_img!!.setVisibility(View.VISIBLE)
                qr_state_img!!.setImageResource(R.mipmap.ic_refunded)
                orderStateStr = "已退款"
                qr_color = Color.LTGRAY
                state_lay!!.setVisibility(View.GONE)
            }
            6 -> {
                orderStateStr = "支付失败"
                qr_state_img!!.setVisibility(View.GONE)
                state_lay!!.setVisibility(View.VISIBLE)
            }
            7 -> {
                orderStateStr = "退款失败"
                qr_color = Color.LTGRAY
                state_lay!!.setVisibility(View.VISIBLE)
            }
            else -> {
                orderStateStr = "暂无"
                qr_state_img!!.setVisibility(View.GONE)
                state_lay!!.setVisibility(View.GONE)
            }
        }
        order_state!!.setText(orderStateStr)
        state_tv!!.setText(orderStateStr)
        qr_img!!.setImageBitmap(
            QRCodeUtil.createQRCodeBitmap(
                washStation.qrString, 280, 280, "UTF-8",
                null, "0", qr_color, Color.WHITE, null, 0f, null
            )
        )
        qr_string!!.setText(washStation.qrString)
        valid_time!!.setText("有效期至" + washStation.effTime + " 00:00:00")
        explain_tv!!.setText("*" + washStation.explain)
        KWApplication.instance.loadImg(washStation.useExplain, explain_img!!)
        pay_time!!.setText(washStation.createTime)
        expire_time!!.setText(washStation.effTime + " 00:00:00")
        services_type!!.setText(washStation.serviceName!!.split("-".toRegex()).toTypedArray().get(0))
        services_model!!.setText(washStation.serviceName!!.split("-".toRegex()).toTypedArray().get(1))
        full_price!!.setText("￥" + washStation.amount)
        rebate_price!!.setText("-￥" + (washStation.amount!! - washStation.realAmount!!))
        sale_price!!.setText(washStation.realAmount.toString())
        if (washStation.state == 1) {
            qr_code_lay!!.setVisibility(View.VISIBLE)
            unused_refund_lay!!.setVisibility(View.VISIBLE)
            unused_navi_btn!!.setVisibility(View.VISIBLE)
            unused_navi_btn!!.setText("地图导航")
            tag_order_nom!!.setVisibility(View.VISIBLE)
            order_number!!.setVisibility(View.VISIBLE)
            tag_pay_time!!.setVisibility(View.VISIBLE)
            pay_time!!.setVisibility(View.VISIBLE)
            expire_time!!.setVisibility(View.VISIBLE)
            tag_expire_time!!.setVisibility(View.VISIBLE)
        } else {
            unused_refund_lay!!.setVisibility(View.GONE)
            if ((washStation.state == 3
                        ) || (washStation.state == 4
                        ) || (washStation.state == 5
                        ) || (washStation.state == 7)
            ) {
                qr_code_lay!!.setVisibility(View.VISIBLE)
                unused_navi_btn!!.setText("再来一单")
                unused_navi_btn!!.setVisibility(View.VISIBLE)
                tag_order_nom!!.setVisibility(View.VISIBLE)
                order_number!!.setVisibility(View.VISIBLE)
                tag_pay_time!!.setVisibility(View.VISIBLE)
                pay_time!!.setVisibility(View.VISIBLE)
                expire_time!!.setVisibility(View.VISIBLE)
                tag_expire_time!!.setVisibility(View.VISIBLE)
            } else {
                unused_navi_btn!!.setVisibility(View.GONE)
                qr_code_lay!!.setVisibility(View.GONE)
                tag_order_nom!!.setVisibility(View.GONE)
                order_number!!.setVisibility(View.GONE)
                tag_pay_time!!.setVisibility(View.GONE)
                pay_time!!.setVisibility(View.GONE)
                expire_time!!.setVisibility(View.GONE)
                tag_expire_time!!.setVisibility(View.GONE)
            }
        }
        unused_refund!!.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
//                FragmentManager fg = getSupportFragmentManager();
//                FragmentTransaction fragmentTransaction = fg.beginTransaction();
//                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//                fragmentTransaction.add(R.id.main_root_lay, new WashRefundFragment(orderId));
//                fragmentTransaction.addToBackStack("ddd");
//                fragmentTransaction.commit();
                val intent: Intent = Intent(this@WashUnusedActivity, WashRefundFragment::class.java)
                intent.putExtra("orderId", orderId)
                startActivity(intent)
                finish()
            }

            override fun OnMoreErrorClick() {}
        })
        unused_navi_btn!!.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                if (washStation.state == 1) {
                    KWApplication.instance.toNavi(
                        this@WashUnusedActivity,
                        washStation.latitude!!,
                        washStation.longitude!!,
                        washStation.address!!,
                        "BD09"
                    )
                } else if ((washStation.state == 3
                            ) || (washStation.state == 4
                            ) || (washStation.state == 5
                            ) || (washStation.state == 7)
                ) {
                    onBackPressed()
                    //                    FragmentManager fg = requireActivity().getSupportFragmentManager();
//                    FragmentTransaction fragmentTransaction = fg.beginTransaction();
//                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//                    fragmentTransaction.add(R.id.main_root_lay, new WashStationActivity(washStation.shopCode));
//                    fragmentTransaction.addToBackStack("ddd");
//                    fragmentTransaction.commit();
                    val intent: Intent =
                        Intent(this@WashUnusedActivity, WashStationActivity::class.java)
                    intent.putExtra("shopCode", washStation.shopCode)
                    startActivity(intent)
                }
            }

            override fun OnMoreErrorClick() {}
        })
    }

    fun permissionsCheck(perms: Array<String>, resId: Int, callback: Callback) {
//        String[] perms = {Manifest.permission.CAMERA};
        performCodeWithPermission(
            1,
            Constants.RC_PERMISSION_PERMISSION_FRAGMENT,
            perms,
            object : PermissionCallback {
                override fun hasPermission(allPerms: List<Array<String>>) {
                    callback.onSuccess()
                }

                override fun noPermission(
                    deniedPerms: List<String>?,
                    grantedPerms: List<String?>?,
                    hasPermanentlyDenied: Boolean?
                ) {
                    EasyPermissions.goSettingsPermissions(
                        this@WashUnusedActivity,
                        1,
                        Constants.RC_PERMISSION_PERMISSION_FRAGMENT,
                        Constants.RC_PERMISSION_BASE
                    )
                }

                override fun showDialog(dialogType: Int, callback: DialogCallback) {
                    val dialog: MessageDialog =
                        MessageDialog.build((this@WashUnusedActivity as AppCompatActivity?)!!)
                    dialog.setTitle("需要获取以下权限")
                    dialog.setMessage(getString(resId))
                    dialog.setOkButton("下一步"
                    ) { baseDialog, v ->
                        callback.onGranted()
                        false
                    }
                    dialog.setCancelable(false)
                    dialog.show()
                }
            })
    }
}