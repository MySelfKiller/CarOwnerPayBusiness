package com.kayu.business_car_owner.activity

import android.Manifest
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.kongzue.dialog.v3.TipGifDialog
import com.kongzue.dialog.interfaces.OnDialogButtonClickListener
import com.kongzue.dialog.util.BaseDialog
import com.kayu.utils.location.LocationManagerUtil
import com.kayu.utils.permission.EasyPermissions.DialogCallback
import com.kongzue.dialog.v3.MessageDialog
import com.kayu.utils.permission.EasyPermissions
import com.kayu.business_car_owner.model.WashStationDetailBean
import com.kayu.business_car_owner.model.WashStationDetailBean.ServicesDTO.ListDTO
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import com.amap.api.location.AMapLocation
import com.youth.banner.Banner
import com.youth.banner.BannerConfig
import com.kayu.business_car_owner.model.WashStationDetailBean.ImgListDTO
import com.kayu.business_car_owner.model.WashStationDetailBean.ServicesDTO
import com.kayu.business_car_owner.*
import com.kayu.business_car_owner.R
import com.kayu.utils.*
import com.kayu.utils.callback.Callback
import java.util.ArrayList

/**
 * Author by killer, Email xx@xx.com, Date on 2020/10/15.
 * PS: Not easy to write code, please indicate.
 */
class WashStationActivity constructor() : BaseActivity() {
    private var shopCode //洗车店编号
            : String = ""
    private var mainViewModel: MainViewModel? = null
    private var pay_btn: TextView? = null
    private var station_banner: Banner? = null
    private var station_open_time: TextView? = null
    private var station_name: TextView? = null
    private var station_tag: TextView? = null
    private var station_address: TextView? = null
    private var station_distance: TextView? = null
    private var navi_lay: LinearLayout? = null
    private var phone_lay: LinearLayout? = null
    private var rebate_price: TextView? = null
    private var station_divider1: View? = null
    private var station_type1_lay: ConstraintLayout? = null
    private var station_car_lay: ConstraintLayout? = null
    private var car_select_btn: ImageView? = null
    private var station_car_price: TextView? = null
    private var station_car_sub_price: TextView? = null
    private var station_suv_lay: ConstraintLayout? = null
    private var suv_select_btn: ImageView? = null
    private var station_suv_price: TextView? = null
    private var station_suv_sub_price: TextView? = null
    private var station_type2_lay: ConstraintLayout? = null
    private var all_car_select_btn: ImageView? = null
    private var station_all_car_price: TextView? = null
    private var station_all_car_sub_price: TextView? = null
    private var station_car_name: TextView? = null
    private var station_suv_name: TextView? = null
    private var station_type1_name: TextView? = null
    private var station_type2_name: TextView? = null
    private var station_all_car_name: TextView? = null
    private var wash_pay_price: TextView? = null

    //    public WashStationFragment(String shopCode) {
    //        this.shopCode = shopCode;
    //    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wash_station)
        shopCode = getIntent().getStringExtra("shopCode").toString()
        mainViewModel = ViewModelProviders.of(this@WashStationActivity).get(
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
        title_name.setText("全国汽车特惠")
        //        title_name.setVisibility(View.GONE);
//        back_tv.setText("我的");
        station_banner = findViewById(R.id.wash_station_banner)
        station_name = findViewById(R.id.wash_station_name)
        station_tag = findViewById(R.id.wash_station_tag)
        station_open_time = findViewById(R.id.wash_station_time)
        station_address = findViewById(R.id.wash_station_location)
        station_distance = findViewById(R.id.wash_station_distance)
        navi_lay = findViewById(R.id.wash_station_navi_lay)
        phone_lay = findViewById(R.id.wash_station_phone_lay)
        station_type1_lay = findViewById(R.id.wash_station_type1_lay)
        station_type1_name = findViewById(R.id.wash_station_type1_name)
        station_divider1 = findViewById(R.id.wash_station_divider1)
        station_car_lay = findViewById(R.id.wash_station_car_lay)
        car_select_btn = findViewById(R.id.wash_station_car_select_btn)
        station_car_name = findViewById(R.id.wash_station_car_name)
        station_car_price = findViewById(R.id.wash_station_car_price)
        station_car_sub_price = findViewById(R.id.wash_station_car_price_sub)
        station_suv_lay = findViewById(R.id.wash_station_suv_lay)
        suv_select_btn = findViewById(R.id.wash_station_suv_select_btn)
        station_suv_name = findViewById(R.id.wash_station_suv_name)
        station_suv_price = findViewById(R.id.wash_station_suv_price)
        station_suv_sub_price = findViewById(R.id.wash_station_suv_price_sub)
        station_type2_lay = findViewById(R.id.wash_station_type2_lay)
        station_type2_name = findViewById(R.id.wash_station_type2_name)
        all_car_select_btn = findViewById(R.id.wash_station_all_car_select_btn)
        station_all_car_name = findViewById(R.id.wash_station_all_car_name)
        station_all_car_price = findViewById(R.id.wash_station_all_car_price)
        station_all_car_sub_price = findViewById(R.id.wash_station_all_car_price_sub)
        rebate_price = findViewById(R.id.wash_station_rebate_price)
        pay_btn = findViewById(R.id.wash_station_pay_btn)
        wash_pay_price = findViewById(R.id.wash_pay_price)
//        val station_services: TextView = findViewById(R.id.wash_station_services)
//        val drawable: Drawable = getResources().getDrawable(R.mipmap.ic_order_list)
//        val drawable1: Drawable = getResources().getDrawable(R.mipmap.ic_services)
//        drawable1.setBounds(0, 0, 50, 50)
//        drawable.setBounds(0, 0, 50, 50)
//        drawable.setTint(getResources().getColor(R.color.colorAccent))
//        drawable1.setTint(getResources().getColor(R.color.colorAccent))
        //40为设置图片的宽度，20为高度

        //（调用方法将图片设置进去）
//        station_order_list.setCompoundDrawables(null, drawable, null, null)
//        station_services.setCompoundDrawables(null, drawable1, null, null)
//        station_order_list.setOnClickListener(object : NoMoreClickListener() {
//            override fun OnMoreClick(view: View) {
//                startActivity(Intent(this@WashStationActivity, WashOrderListActivity::class.java))
//            }
//
//            override fun OnMoreErrorClick() {}
//        })
        pay_btn!!.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                if (null == selectedListDTO) return
                val intent: Intent = Intent(this@WashStationActivity, WashOrderActivity::class.java)
                intent.putExtra("selectedListDTO", selectedListDTO)
                intent.putExtra("serviceType", serviceType)
                intent.putExtra("shopCode", shopCode)
                startActivity(intent)
            }

            override fun OnMoreErrorClick() {}
        })
        station_car_lay!!.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                if (!car_select_btn!!.isSelected) {
                    car_select_btn!!.isSelected = true
                    selectedListDTO = selectedListDTO1
                    val payPrice: String? = selectedListDTO!!.finalPrice
                    val rebatePirce: String =
                        (selectedListDTO!!.price?.toDouble()
                            ?.minus(selectedListDTO!!.finalPrice!!.toDouble())).toString()
                    wash_pay_price!!.setText(payPrice)
                    rebate_price!!.setText("￥" + rebatePirce)
                }
                if (suv_select_btn!!.isSelected) suv_select_btn!!.isSelected = false
                if (all_car_select_btn!!.isSelected) all_car_select_btn!!.isSelected = false
            }

            override fun OnMoreErrorClick() {}
        })
        station_suv_lay!!.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                if (!suv_select_btn!!.isSelected()) {
                    suv_select_btn!!.setSelected(true)
                    selectedListDTO = selectedListDTO2
                    val payPrice: String? = selectedListDTO!!.finalPrice
                    val rebatePirce: String =
                        (selectedListDTO!!.price!!.toDouble() - selectedListDTO!!.finalPrice!!.toDouble()).toString()
                    wash_pay_price!!.setText( payPrice)
                    rebate_price!!.setText( "￥"+ rebatePirce)
                }
                if (car_select_btn!!.isSelected()) car_select_btn!!.setSelected(false)
                if (all_car_select_btn!!.isSelected()) all_car_select_btn!!.setSelected(false)
            }

            override fun OnMoreErrorClick() {}
        })
        station_type2_lay!!.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                if (!all_car_select_btn!!.isSelected()) {
                    all_car_select_btn!!.setSelected(true)
                    selectedListDTO = selectedListDTO3
                    val payPrice: String = selectedListDTO!!.finalPrice!!
                    val rebatePirce: String =
                        (selectedListDTO!!.price!!.toDouble() - selectedListDTO!!.finalPrice!!.toDouble()).toString()
                    wash_pay_price!!.setText( payPrice)
                    rebate_price!!.setText("￥" + rebatePirce)
                }
                if (car_select_btn!!.isSelected()) car_select_btn!!.setSelected(false)
                if (suv_select_btn!!.isSelected()) suv_select_btn!!.setSelected(false)
            }

            override fun OnMoreErrorClick() {}
        })
        TipGifDialog.show(
            this@WashStationActivity,
            "请稍等...",
            TipGifDialog.TYPE.OTHER,
            R.drawable.loading_gif
        )
        mainViewModel!!.getWashStoreDetail(this@WashStationActivity, shopCode)
            .observe(this@WashStationActivity, object : Observer<WashStationDetailBean?> {
                public override fun onChanged(washStationDetailBean: WashStationDetailBean?) {
                    if (null == washStationDetailBean) {
                        TipGifDialog.show(
                            this@WashStationActivity,
                            "数据获取错误",
                            TipGifDialog.TYPE.ERROR
                        )
                    } else {
                        initViewData(washStationDetailBean)
                    }
                }
            })
    }

    //    @Override
    //    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    //                             Bundle savedInstanceState) {
    //        // Inflate the layout for this fragment
    ////        StatusBarUtil.setStatusBarColor(getActivity(), getResources().getColor(R.color.white));
    //        View root = inflater.inflate(R.layout.fragment_wash_station, container, false);
    //        mainViewModel = ViewModelProviders.of(requireActivity()).get(MainViewModel.class);
    //        return root;
    //    }
    //    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    //    @Override
    //    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    //        super.onViewCreated(view, savedInstanceState);
    //    }
    private var serviceType: String = ""
    private var selectedListDTO: ListDTO? = null
    private var selectedListDTO1: ListDTO? = null
    private var selectedListDTO2: ListDTO? = null
    private var selectedListDTO3: ListDTO? = null
    private fun initViewData(washStation: WashStationDetailBean) {
        if (null != washStation.imgList) {
            val loanBannrUrl: MutableList<String?> = ArrayList()
            for (loanBanner: ImgListDTO in washStation.imgList!!) {
                loanBannrUrl.add(loanBanner.shopImgUrl)
            }
            station_banner!!.setBannerStyle(BannerConfig.CIRCLE_INDICATOR)
                .setIndicatorGravity(BannerConfig.RIGHT)
                .setImageLoader(BannerImageLoader())
                .setImages(loanBannrUrl)
                .setDelayTime(2000)
                .start()
        }
        station_name!!.setText(washStation.shopName)
        station_address!!.setText(washStation.address)
        val sb: StringBuffer = StringBuffer()
        if ((washStation.isOpen == "1")) {
            sb.append("营业中 | ")
        } else {
            sb.append("休息中 | ")
        }
        sb.append(washStation.openTimeStart).append("-").append(washStation.openTimeEnd)

//        checkLocation(Double.parseDouble(washStation.latitude), Double.parseDouble(washStation.longitude));
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
        station_open_time!!.setText(sb.toString())
        navi_lay!!.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                KWApplication.instance.toNavi(
                    this@WashStationActivity,
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
                KWApplication.instance.permissionsCheck(
                    this@WashStationActivity,
                    arrayOf(Manifest.permission.CALL_PHONE),
                    R.string.permiss_call_phone,
                    object : Callback {
                        public override fun onSuccess() {
                            KWApplication.instance.callPhone(this@WashStationActivity, washStation.telephone!!)
                        }

                        public override fun onError() {}
                    })
            }

            override fun OnMoreErrorClick() {}
        })
        val servicesList: List<ServicesDTO> = washStation.services!!
        if (servicesList.size <= 1) {
            station_divider1!!.setVisibility(View.GONE)
        } else {
            station_divider1!!.setVisibility(View.VISIBLE)
        }
        for (x in servicesList.indices) {
            val servicesDTO: ServicesDTO = servicesList.get(x)
            if (servicesDTO.washType == 1) {
                station_type1_name!!.setText(servicesDTO.name)
                station_type1_lay!!.setVisibility(View.VISIBLE)
            }
            if (servicesDTO.washType == 2) {
                station_type2_name!!.setText(servicesDTO.name)
                station_type2_lay!!.setVisibility(View.VISIBLE)
            }
            val listDTOS: List<ListDTO>? = servicesDTO.list
            if (listDTOS != null) {
                for (y in listDTOS.indices) {
                    val listDTO: ListDTO = listDTOS.get(y)
                    if (y == 0 && x == 0) {
                        selectedListDTO = listDTO
                        val payPrice: String = listDTO.finalPrice!!
                        serviceType = servicesDTO.name!!
                        val rebatePirce: String =
                            (listDTO.price!!.toDouble() - listDTO.finalPrice!!.toDouble()).toString()
                        wash_pay_price!!.setText( payPrice)
                        rebate_price!!.setText("￥" + rebatePirce)
                    }
                    val sdf: Array<String>? = listDTO.serviceName!!.split("-".toRegex()).toTypedArray()
                    var ddd: String? = ""
                    if (null != sdf && sdf.size > 0) {
                        ddd = sdf.get(1)
                    } else {
                        if (selectedListDTO!!.carModel == 1) {
                            ddd = "小轿车"
                        }
                        if (selectedListDTO!!.carModel == 2) {
                            ddd = "SUV/MPV"
                        }
                        if (selectedListDTO!!.carModel == 3) {
                            ddd = "全车型"
                        }
                    }
                    when (listDTO.carModel) {
                        1 -> {
                            selectedListDTO1 = listDTO
                            station_car_lay!!.setVisibility(View.VISIBLE)
                            station_car_name!!.setText(ddd)
                            station_car_price!!.setText(listDTO.finalPrice)
                            station_car_sub_price!!.setText(listDTO.price)
                            if (y == 0 && x == 0) {
                                car_select_btn!!.setSelected(true)
                                serviceType = servicesDTO.name.toString()
                            }
                        }
                        2 -> {
                            selectedListDTO2 = listDTO
                            station_suv_lay!!.setVisibility(View.VISIBLE)
                            station_suv_name!!.setText(ddd)
                            station_suv_price!!.setText(listDTO.finalPrice)
                            station_suv_sub_price!!.setText(listDTO.price)
                            if (y == 0 && x == 0) {
                                suv_select_btn!!.setSelected(true)
                                serviceType = servicesDTO.name.toString()
                            }
                        }
                        3 -> {
                            selectedListDTO3 = listDTO
                            station_all_car_name!!.setText(ddd)
                            station_all_car_price!!.setText(listDTO.finalPrice)
                            station_all_car_sub_price!!.setText(listDTO.price)
                            if (y == 0 && x == 0) {
                                all_car_select_btn!!.setSelected(true)
                                serviceType = servicesDTO.name.toString()
                            }
                        }
                        else -> {}
                    }
                }
            }
        }
    }

//    fun permissionsCheck(perms: Array<String>, resId: Int, callback: Callback) {
////        String[] perms = {Manifest.permission.CAMERA};
//        performCodeWithPermission(
//            1,
//            Constants.RC_PERMISSION_PERMISSION_FRAGMENT,
//            perms,
//            object : PermissionCallback {
//                override fun hasPermission(allPerms: List<Array<String>>) {
//                    callback.onSuccess()
//                }
//
//                override fun noPermission(
//                    deniedPerms: List<String>?,
//                    grantedPerms: List<String?>?,
//                    hasPermanentlyDenied: Boolean?
//                ) {
//                    EasyPermissions.goSettingsPermissions(
//                        this@WashStationActivity,
//                        1,
//                        Constants.RC_PERMISSION_PERMISSION_FRAGMENT,
//                        Constants.RC_PERMISSION_BASE
//                    )
//                }
//
//                public override fun showDialog(dialogType: Int, callback: DialogCallback) {
//                    val dialog: MessageDialog =
//                        MessageDialog.build((this@WashStationActivity as AppCompatActivity?)!!)
//                    dialog.setTitle("需要获取以下权限")
//                    dialog.setMessage(getString(resId))
//                    dialog.setOkButton("下一步", object : OnDialogButtonClickListener {
//                        public override fun onClick(baseDialog: BaseDialog, v: View): Boolean {
//                            callback.onGranted()
//                            return false
//                        }
//                    })
//                    dialog.setCancelable(false)
//                    dialog.show()
//                }
//            })
//    }
}