package com.kayu.business_car_owner.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import com.hjq.toast.ToastUtils
import com.kongzue.dialog.v3.TipGifDialog
import com.kayu.business_car_owner.KWApplication
import com.kayu.utils.location.LocationManagerUtil
import com.kayu.business_car_owner.model.OilStationBean
import com.kayu.business_car_owner.model.WebBean
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.amap.api.location.AMapLocation
import com.kayu.business_car_owner.ui.adapter.ProductTypeAdapter
import com.kayu.business_car_owner.model.OilsParam
import com.kayu.business_car_owner.model.OilsTypeParam
import com.kayu.business_car_owner.R
import com.kayu.utils.*
import java.util.*

class OilStationActivity : BaseActivity() {
    private var gasId: String = ""
    private var select_oil_rv: RecyclerView? = null
    private var select_oil_gun_rv: RecyclerView? = null
    private var mainViewModel: MainViewModel? = null
    private var next_ask_btn: TextView? = null
    private var oil_price_sub2: TextView? = null
    private var oil_price_sub1: TextView? = null
    private var oil_price: TextView? = null
    private var station_location: TextView? = null
    private var station_name: TextView? = null
    private var station_img: ImageView? = null
    private var select_oil_type_rv: RecyclerView? = null
    private var rootTypeAdapter: ProductTypeAdapter? = null
    private var childTypeAdapter: ProductTypeAdapter? = null
    private var parentTypeAdapter: ProductTypeAdapter? = null
    private var gunNo: String? = null //默认选中的枪号

    //    private ConstraintLayout tip_lay;
    //    private TextView tip_content;
    //    private TextView tip_title;
    //    private RefreshLayout refreshLayout;
    //    boolean isLoadmore = false;
    //    boolean isRefresh = false;
    //    public OilStationActivity(String gasId) {
    //        this.gasId = gasId;
    //    }
    public override fun onCreate(savedInstanceState: Bundle?) {
        LogUtil.e("StationFragment----", "----onCreate---")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_oil_station)
        mainViewModel = ViewModelProviders.of(this@OilStationActivity).get(
            MainViewModel::class.java
        )
        gasId = intent.getStringExtra("gasId").toString()
        //标题栏
        findViewById<View>(R.id.title_back_btu).setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                onBackPressed()
            }

            override fun OnMoreErrorClick() {}
        })
        val back_tv: TextView = findViewById(R.id.title_back_tv)
        val title_name: TextView = findViewById(R.id.title_name_tv)
        title_name.setVisibility(View.VISIBLE)
        title_name.setText("详情")
        station_img = findViewById(R.id.station_img)
        station_name = findViewById(R.id.station_name)
        station_location = findViewById(R.id.station_location)
        oil_price = findViewById(R.id.station_oil_price)
        oil_price_sub1 = findViewById(R.id.station_oil_price_sub1)
        oil_price_sub2 = findViewById(R.id.station_oil_price_sub2)
        next_ask_btn = findViewById(R.id.station_next_tv)
        select_oil_type_rv = findViewById(R.id.station_select_oil_type_rv)
        select_oil_rv = findViewById(R.id.station_select_oil_rv)
        select_oil_gun_rv = findViewById(R.id.station_select_oil_gun_rv)
        val manager = StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL)
        select_oil_type_rv?.layoutManager = manager
        select_oil_rv?.layoutManager = StaggeredGridLayoutManager(
            4,
            StaggeredGridLayoutManager.VERTICAL
        )
        val manager1 = StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL)
        select_oil_gun_rv?.layoutManager = manager1

        loadView()
    }
    private fun loadView() {
        TipGifDialog.show(
            this@OilStationActivity,
            "请稍等...",
            TipGifDialog.TYPE.OTHER,
            R.drawable.loading_gif
        )
        mainViewModel!!.getOilStationDetail(this@OilStationActivity, gasId)
            .observe(this@OilStationActivity, object : Observer<OilStationBean?> {
                @SuppressLint("SetTextI18n")
                public override fun onChanged(oilStationBean: OilStationBean?) {
                    if (null == oilStationBean) {
                        TipGifDialog.show(
                            this@OilStationActivity,
                            "数据获取错误",
                            TipGifDialog.TYPE.ERROR
                        )
                        return
                    }
                    TipGifDialog.dismiss()
                    station_name!!.setText(oilStationBean.gasName)
                    station_location!!.setText(oilStationBean.gasAddress)
                    KWApplication.instance.loadImg(oilStationBean.gasLogoSmall, station_img!!)
                    val rootParamItemCallback: RootParamItemCallback = RootParamItemCallback()
                    val parentParamItemCallback: ParentParamItemCallback = ParentParamItemCallback()
                    val childParamItemCallback: ChildParamItemCallback = ChildParamItemCallback()
                    rootTypeAdapter = ProductTypeAdapter(
                        this@OilStationActivity,
                        ArrayList<Any>(oilStationBean.oilsTypeList),
                        0,
                        rootParamItemCallback
                    )
                    select_oil_type_rv!!.setAdapter(rootTypeAdapter)
                    parentTypeAdapter = ProductTypeAdapter(
                        this@OilStationActivity,
                        ArrayList<Any>(oilStationBean.oilsTypeList?.get(0)?.oilsParamList),
                        1,
                        parentParamItemCallback
                    )
                    select_oil_rv!!.adapter = parentTypeAdapter
                    val defultOilParam: OilsParam =
                        oilStationBean.oilsTypeList?.get(0)?.oilsParamList?.get(0)!!
                    oil_price!!.setText(defultOilParam.priceYfq.toString())
                    selectedOilNo = defultOilParam.oilNo
                    oil_price_sub1!!.text = "比国标价降" + DoubleUtils.sub(
                        defultOilParam.priceOfficial,
                        defultOilParam.priceYfq
                    ) + "元"
                    oil_price_sub2!!.text = "比油站降" + DoubleUtils.sub(
                        defultOilParam.priceGun,
                        defultOilParam.priceYfq
                    ) + "元"
                    val gunArrs: Array<String> =
                        defultOilParam.gunNos.split(",".toRegex()).toTypedArray()
                    //                gunNo = gunArrs[0];
                    childTypeAdapter = ProductTypeAdapter(
                        this@OilStationActivity, ArrayList<Any>(
                            Arrays.asList(*gunArrs)
                        ), 2, childParamItemCallback
                    )
                    select_oil_gun_rv!!.setAdapter(childTypeAdapter)
                    next_ask_btn!!.setOnClickListener(object : NoMoreClickListener() {
                        override fun OnMoreClick(view: View) {
                            if (StringUtil.isEmpty(gunNo)) {
                                TipGifDialog.show(
                                    this@OilStationActivity,
                                    "请选择枪号",
                                    TipGifDialog.TYPE.WARNING
                                )
                                return
                            }
                            val location: AMapLocation =
                                LocationManagerUtil.self?.loccation!!
                            mainViewModel!!.getPayUrl(
                                this@OilStationActivity, gasId, gunNo!!.toInt(), selectedOilNo,
                                location.getLatitude(), location.getLongitude()
                            )
                                .observe(this@OilStationActivity, object : Observer<WebBean?> {
                                    public override fun onChanged(webBean: WebBean?) {
                                        if (null == webBean) {
                                            ToastUtils.show("未获取到支付信息")
                                            return
                                        }
                                        val intent: Intent = Intent(
                                            this@OilStationActivity,
                                            WebViewActivity::class.java
                                        )
                                        intent.putExtra("url", webBean.link)
                                        intent.putExtra("title", "订单")
                                        intent.putExtra("data", webBean.data)
                                        intent.putExtra("gasId", oilStationBean.gasId)
                                        //                                intent.putExtra("from", "首页");
                                        startActivityForResult(intent, 111)
                                    }
                                })
                        }

                        override fun OnMoreErrorClick() {}
                    })
                }
            })
    }

    internal inner class RootParamItemCallback : ItemCallback {
        public override fun onItemCallback(position: Int, obj: Any?) {
            val typeParam: OilsTypeParam = obj as OilsTypeParam
            //            rootSelectedIndex = position;
            parentTypeAdapter!!.addAllData(ArrayList<Any>(typeParam.oilsParamList), true)
            val param: OilsParam = typeParam.oilsParamList?.get(0)!!
            oil_price!!.setText(param.priceYfq.toString())
            oil_price_sub1!!.text = "比国标价降" + DoubleUtils.sub(
                param.priceOfficial,
                param.priceYfq
            ) + "元"
            oil_price_sub2!!.setText("比油站降" + DoubleUtils.sub(param.priceGun, param.priceYfq) + "元")
            val gunArrs: Array<String> = param.gunNos.split(",".toRegex()).toTypedArray()
            gunNo = null
            childTypeAdapter!!.addAllData(ArrayList<Any>(Arrays.asList(*gunArrs)), true)
        }

        override fun onDetailCallBack(position: Int, obj: Any?) {}
    }

    var selectedOilNo: Int = -100

    internal inner class ParentParamItemCallback : ItemCallback {
        public override fun onItemCallback(position: Int, obj: Any?) {
//            parentSelectedIndex = position;
            val param: OilsParam = obj as OilsParam
            selectedOilNo = param.oilNo
            oil_price!!.setText(param.priceYfq.toString())
            oil_price_sub1!!.setText(
                "比国标价降" + DoubleUtils.sub(
                    param.priceOfficial,
                    param.priceYfq
                ) + "元"
            )
            oil_price_sub2!!.setText("比油站降" + DoubleUtils.sub(param.priceGun, param.priceYfq) + "元")
            val gunArrs: Array<String> = param.gunNos.split(",".toRegex()).toTypedArray()
            gunNo = null
            childTypeAdapter!!.addAllData(ArrayList<Any>(Arrays.asList(*gunArrs)), true)
        }

        override fun onDetailCallBack(position: Int, obj: Any?) {}
    }

    internal inner class ChildParamItemCallback : ItemCallback {
        override fun onItemCallback(position: Int, obj: Any?) {
            gunNo = obj as String?
        }

        public override fun onDetailCallBack(position: Int, obj: Any?) {}
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == 111) {
//            mainViewModel!!.sendOilPayInfo(this@OilStationActivity)
//        }
    }
}