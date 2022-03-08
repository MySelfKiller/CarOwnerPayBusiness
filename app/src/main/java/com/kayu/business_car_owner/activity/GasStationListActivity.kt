package com.kayu.business_car_owner.activity

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.hjq.toast.ToastUtils
import com.kongzue.dialog.v3.TipGifDialog
import com.kayu.business_car_owner.KWApplication
import com.kongzue.dialog.interfaces.OnDialogButtonClickListener
import com.kongzue.dialog.util.BaseDialog
import com.kayu.utils.location.LocationManagerUtil
import com.kayu.utils.permission.EasyPermissions.DialogCallback
import com.kongzue.dialog.v3.MessageDialog
import com.kayu.utils.permission.EasyPermissions
import com.kayu.business_car_owner.model.OilStationBean
import com.kayu.business_car_owner.model.ParamOilBean
import com.kayu.business_car_owner.model.WebBean
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amap.api.location.AMapLocation
import com.kayu.business_car_owner.model.OilsParam
import com.kayu.business_car_owner.model.ParamParent
import com.kayu.business_car_owner.ui.adapter.ParamParentAdapter
import com.kayu.business_car_owner.R
import com.kayu.business_car_owner.model.DistancesParam
import com.kayu.business_car_owner.model.SortsParam
import com.kayu.business_car_owner.ui.adapter.OilStationAdapter
import com.kayu.utils.*
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import java.util.ArrayList
import java.util.HashMap

class GasStationListActivity constructor() : BaseActivity() {
    private var station_rv: RecyclerView? = null
    private var param_recycle_view: RecyclerView? = null
    private var param_distance: TextView? = null
    private var param_oil_type: TextView? = null
    private var param_sort: TextView? = null
    var selectDistanceParam: DistancesParam? = null
    var selectOilParam: OilsParam? = null
    var selectSortsParam: SortsParam? = null
    private var mainViewModel: MainViewModel? = null
    private var oilStationAdapter: OilStationAdapter? = null
    private val keyword: String = "" //搜索关键字
    var isLoadmore: Boolean = false
    var isRefresh: Boolean = false
    private var pageIndex: Int = 0
    private var refreshLayout: RefreshLayout? = null
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gas_station_list)
        mainViewModel = ViewModelProviders.of(this@GasStationListActivity).get(
            MainViewModel::class.java
        )
        //标题栏
//        LinearLayout title_lay = findViewById(R.id.title_lay);
//        title_lay.setBackgroundColor(getResources().getColor(R.color.background_gray));
        val title_name: TextView = findViewById(R.id.title_name_tv)
        title_name.setText("特惠加油")
        findViewById<View>(R.id.title_back_btu).setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                onBackPressed()
            }

            override fun OnMoreErrorClick() {}
        })
        station_rv = findViewById(R.id.gas_station_rv)
        param_distance = findViewById(R.id.station_param_distance)
        param_oil_type = findViewById(R.id.station_param_oil_type)
        param_sort = findViewById(R.id.station_param_sort)
        param_recycle_view = findViewById(R.id.station_param_recycler)
        param_recycle_view?.layoutManager = LinearLayoutManager(this@GasStationListActivity)
        refreshLayout = findViewById<View>(R.id.refreshLayout) as RefreshLayout?
        refreshLayout!!.setEnableAutoLoadMore(false)
        refreshLayout!!.setEnableLoadMore(true)
        refreshLayout!!.setEnableLoadMoreWhenContentNotFull(true) //是否在列表不满一页时候开启上拉加载功能
        refreshLayout!!.setEnableOverScrollBounce(true) //是否启用越界回弹
        refreshLayout!!.setEnableOverScrollDrag(true)
        refreshLayout!!.setOnRefreshListener(object : OnRefreshListener {
            public override fun onRefresh(refreshLayout: RefreshLayout) {
                val location: AMapLocation? = LocationManagerUtil.self?.loccation
                if (isRefresh || isLoadmore || (null == location)) {
                    refreshLayout.finishRefresh()
                    return
                }
                isRefresh = true
                pageIndex = 1
                if (null != oilStationAdapter) oilStationAdapter!!.removeAllData(true)
                reqData(refreshLayout, pageIndex, location.getLatitude(), location.getLongitude())
            }
        })
        refreshLayout!!.setOnLoadMoreListener(object : OnLoadMoreListener {
            public override fun onLoadMore(refreshLayout: RefreshLayout) {
                val location: AMapLocation? = LocationManagerUtil.self?.loccation
                if (isRefresh || isLoadmore || (null == location)) {
                    refreshLayout.finishLoadMore()
                    return
                }
                isLoadmore = true
                pageIndex = pageIndex + 1
                reqData(refreshLayout, pageIndex, location.getLatitude(), location.getLongitude())
            }
        })
        station_rv?.layoutManager = LinearLayoutManager(this@GasStationListActivity)
        oilStationAdapter =
            OilStationAdapter(this@GasStationListActivity, null, true, true, object : ItemCallback {
                override fun onItemCallback(position: Int, obj: Any?) {
                    val oilStationBean: OilStationBean = obj as OilStationBean
                    if (oilStationBean.nextIsBuy == 1) {
                        val location: AMapLocation = LocationManagerUtil.self?.loccation!!
                        mainViewModel!!.getPayUrl(
                            this@GasStationListActivity,
                            oilStationBean.gasId, -1, selectOilParam!!.oilNo,
                            location.getLatitude(), location.getLongitude()
                        )
                            .observe(this@GasStationListActivity, object : Observer<WebBean?> {
                                public override fun onChanged(webBean: WebBean?) {
                                    if (null == webBean) {
                                        ToastUtils.show("未获取到支付信息")
                                        return
                                    }
                                    val jumpCls: Class<*>
                                    //                            if (oilStationBean.channel.equals("qj")) {
//                                jumpCls = AgentWebViewActivity.class;
//                            } else {
//                                jumpCls = WebViewActivity.class;
//                            }
                                    jumpCls = WebViewActivity::class.java
                                    val intent: Intent =
                                        Intent(this@GasStationListActivity, jumpCls)
                                    intent.putExtra("url", webBean.link)
                                    intent.putExtra("title", "订单")
                                    intent.putExtra("data", webBean.data)
                                    intent.putExtra("channel", oilStationBean.channel)
                                    intent.putExtra("gasId", oilStationBean.gasId)
                                    //                                intent.putExtra("from", "首页");
                                    startActivityForResult(intent, 111)
                                }
                            })
                    } else {
//                        val userRole: Int = KWApplication.instance.userRole
//                        val isPublic: Int = KWApplication.instance.isGasPublic
//                        if (userRole == -2 && isPublic == 0) {
//                            KWApplication.instance.showRegDialog(this@GasStationListActivity)
//                            return
//                        }
                        val intent: Intent =
                            Intent(this@GasStationListActivity, OilStationActivity::class.java)
                        intent.putExtra("gasId", obj.gasId)
                        startActivity(intent)
                    }
                }

                override fun onDetailCallBack(position: Int, obj: Any?) {}
            })
        station_rv?.setAdapter(oilStationAdapter)
        permissionsCheck()
    }

    fun permissionsCheck() {
        val perms: Array<String> = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        //        String[] perms = needPermissions;
        performCodeWithPermission(
            1,
            Constants.RC_PERMISSION_PERMISSION_FRAGMENT,
            perms,
            object : PermissionCallback {
                override fun hasPermission(allPerms: List<Array<String>>) {
                    if (!LocationManagerUtil.self?.isLocServiceEnable!!) {
                        MessageDialog.show(
                            this@GasStationListActivity,
                            "定位服务未开启",
                            "请打开定位服务",
                            "开启定位服务",
                            "取消"
                        ).setCancelable(false)
                            .setOnOkButtonClickListener(object : OnDialogButtonClickListener {
                                public override fun onClick(
                                    baseDialog: BaseDialog,
                                    v: View
                                ): Boolean {
                                    baseDialog.doDismiss()
                                    val intent: Intent = Intent()
                                    intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(intent)
                                    //                                    appManager.finishAllActivity();
//                                    LocationManagerUtil.getSelf().stopLocation();
//                                    finish();
                                    onBackPressed()
                                    return true
                                }
                            }).setCancelButton(object : OnDialogButtonClickListener {
                                override fun onClick(
                                    baseDialog: BaseDialog,
                                    v: View
                                ): Boolean {
                                    onBackPressed()
                                    return false
                                }
                            })
                    } else {
                        loadParam()
                    }
                    if (null == LocationManagerUtil.self?.loccation) {
                        LocationManagerUtil.self?.reStartLocation()
                    }
                }

                public override fun noPermission(
                    deniedPerms: List<String>?,
                    grantedPerms: List<String?>?,
                    hasPermanentlyDenied: Boolean?
                ) {
                    EasyPermissions.goSettingsPermissions(
                        this@GasStationListActivity,
                        1,
                        Constants.RC_PERMISSION_PERMISSION_FRAGMENT,
                        Constants.RC_PERMISSION_BASE
                    )
                }

                public override fun showDialog(dialogType: Int, callback: DialogCallback) {
                    val dialog: MessageDialog =
                        MessageDialog.build((this@GasStationListActivity as AppCompatActivity?)!!)
                    dialog.setTitle("需要获取以下权限")
                    dialog.setMessage(getString(R.string.permiss_location))
                    dialog.setOkButton("下一步", object : OnDialogButtonClickListener {
                        public override fun onClick(baseDialog: BaseDialog, v: View): Boolean {
                            callback.onGranted()
                            return false
                        }
                    })
                    dialog.setCancelable(false)
                    dialog.show()
                }
            })
    }

    private fun loadParam() {
//        TipGifDialog.show(GasStationListActivity.this, "稍等...", TipGifDialog.TYPE.OTHER,R.drawable.loading_gif);
        mainViewModel!!.getParamSelect(this@GasStationListActivity)
            .observe(this@GasStationListActivity, object : Observer<ParamOilBean?> {
                public override fun onChanged(paramOilBean: ParamOilBean?) {
//                TipGifDialog.dismiss();
                    if (null == paramOilBean) return
                    for (item: DistancesParam in paramOilBean.distancesParamList!!) {
                        if (item.isDefault == 1) {
                            param_distance!!.setText(item.name)
                            selectDistanceParam = item
                        }
                    }
                    for (item: SortsParam in paramOilBean.sortsParamList!!) {
                        if (item.isDefault == 1) {
                            param_sort!!.setText(item.name)
                            selectSortsParam = item
                        }
                    }
                    for (oilsTypeParam: OilsParam in paramOilBean.oilsTypeParamList!!) {
                        if (oilsTypeParam.isDefault == 1) {
                            param_oil_type!!.setText(oilsTypeParam.oilName)
                            selectOilParam = oilsTypeParam
                        }
                    }
                    param_sort!!.setOnClickListener(object : View.OnClickListener {
                        public override fun onClick(v: View) {
                            param_oil_type!!.setSelected(false)
                            param_distance!!.setSelected(false)
                            if (param_sort!!.isSelected()) {
                                param_sort!!.setSelected(false)
                                param_recycle_view!!.setVisibility(View.GONE)
                                return
                            }
                            val parents: MutableList<ParamParent> = ArrayList()
                            val paramParent = ParamParent()
                            paramParent.type = -1
                            paramParent.objList = ArrayList<Any>(paramOilBean.sortsParamList)
                            parents.add(paramParent)
                            showParamViewData(3, parents)
                            param_sort!!.setSelected(true)
                        }
                    })
                    param_oil_type!!.setOnClickListener(object : View.OnClickListener {
                        public override fun onClick(v: View) {
                            param_sort!!.setSelected(false)
                            param_distance!!.setSelected(false)
                            if (param_oil_type!!.isSelected()) {
                                param_oil_type!!.setSelected(false)
                                param_recycle_view!!.setVisibility(View.GONE)
                                return
                            }
                            val parents: MutableList<ParamParent> = ArrayList()
                            val paramParent = ParamParent()
                            paramParent.type = -1
                            paramParent.objList = ArrayList<Any>(paramOilBean.oilsTypeParamList)
                            parents.add(paramParent)
                            showParamViewData(2, parents)
                            param_oil_type!!.setSelected(true)
                        }
                    })
                    param_distance!!.setOnClickListener(object : View.OnClickListener {
                        public override fun onClick(v: View) {
                            param_oil_type!!.setSelected(false)
                            param_sort!!.setSelected(false)
                            if (param_distance!!.isSelected()) {
                                param_distance!!.setSelected(false)
                                param_recycle_view!!.setVisibility(View.GONE)
                                return
                            }
                            val parents: MutableList<ParamParent> = ArrayList()
                            val paramParent: ParamParent = ParamParent()
                            paramParent.type = -1
                            paramParent.objList = ArrayList<Any>(paramOilBean.distancesParamList)
                            parents.add(paramParent)
                            showParamViewData(1, parents)
                            param_distance!!.setSelected(true)
                        }
                    })
                    //                refreshLayout.autoRefresh();
                    pageIndex = 1
                    if (null != oilStationAdapter) oilStationAdapter!!.removeAllData(true)
                    val location: AMapLocation? = LocationManagerUtil.self?.loccation
                    if (null != location) {
                        reqData(null, pageIndex, location.getLatitude(), location.getLongitude())
                    }
                }
            })
    }

    private fun showParamViewData(flag: Int, data: List<ParamParent>) {
        if (param_recycle_view!!.getVisibility() != View.VISIBLE) param_recycle_view!!.setVisibility(
            View.VISIBLE
        )
        param_recycle_view!!.setAdapter(
            ParamParentAdapter(
                this@GasStationListActivity,
                data as MutableList<ParamParent>,
                object : ItemCallback {
                    override fun onItemCallback(position: Int, obj: Any?) {
                        val paramOilBean: ParamOilBean? =
                            mainViewModel!!.getParamSelect(this@GasStationListActivity).value
                        if (null == paramOilBean || null == obj) return
                        if (flag == 1) {
                            selectDistanceParam = obj as DistancesParam?
                            selectDistanceParam!!.isDefault = 1
                            for (item: DistancesParam? in paramOilBean.distancesParamList!!) {
                                if (item!!.isDefault == 1) {
                                    item.isDefault = 0
                                }
                                if (item.value == selectDistanceParam!!.value) {
                                    item.isDefault = 1
                                    item.name = selectDistanceParam!!.name
                                    item.value = selectDistanceParam!!.value
                                    param_distance!!.text = item!!.name
                                }
                            }
                        } else if (flag == 2) {
                            selectOilParam = obj as OilsParam?
                            for (item: OilsParam? in paramOilBean.oilsTypeParamList!!) {
                                if (item!!.isDefault == 1) {
                                    item.isDefault = 0
                                }
                                if (item.oilNo == selectOilParam!!.oilNo) {
                                    item.id = selectOilParam!!.id
                                    item.gasId = selectOilParam!!.gasId
                                    item.oilNo = selectOilParam!!.oilNo
                                    item.oilName = selectOilParam!!.oilName
                                    item.oilType = selectOilParam!!.oilType
                                    item.gunNos = selectOilParam!!.gunNos
                                    item.priceYfq = selectOilParam!!.priceYfq
                                    item.priceGun = selectOilParam!!.priceGun
                                    item.priceOfficial = selectOilParam!!.priceOfficial
                                    item.offDiscount = selectOilParam!!.offDiscount
                                    item.gunDiscount = selectOilParam!!.gunDiscount
                                    item.isDefault = 1
                                    param_oil_type!!.text = item.oilName
                                }
                            }
                        } else if (flag == 3) {
                            selectSortsParam = obj as SortsParam?
                            for (item: SortsParam? in paramOilBean.sortsParamList!!) {
                                if (item!!.isDefault == 1) {
                                    item.isDefault = 0
                                }
                                if (item.value == selectSortsParam!!.value) {
                                    item.name = selectSortsParam!!.name
                                    item.value = selectSortsParam!!.value
                                    item.isDefault = 1
                                    param_sort!!.setText(item!!.name)
                                }
                            }
                        }
                        if (param_sort!!.isSelected()) {
                            param_sort!!.setSelected(false)
                        }
                        if (param_oil_type!!.isSelected()) {
                            param_oil_type!!.setSelected(false)
                        }
                        if (param_distance!!.isSelected()) {
                            param_distance!!.setSelected(false)
                        }
                        param_recycle_view!!.setVisibility(View.GONE)
                        isRefresh = true
                        pageIndex = 1
                        if (null != oilStationAdapter) oilStationAdapter!!.removeAllData(true)
                        val location: AMapLocation = LocationManagerUtil.self?.loccation!!
                        reqData(null, pageIndex, location.getLatitude(), location.getLongitude())
                    }

                    override fun onDetailCallBack(position: Int, obj: Any?) {}
                },
                flag
            )
        )
    }

    fun reqData(
        refreshLayout: RefreshLayout?,
        pageIndex: Int,
        latitude: Double,
        longitude: Double
    ) {
        if (null == refreshLayout) {
            TipGifDialog.show(
                this@GasStationListActivity,
                "稍等...",
                TipGifDialog.TYPE.OTHER,
                R.drawable.loading_gif
            )
        }
        if ((null == selectSortsParam) || (null == selectDistanceParam) || (null == selectOilParam)) {
            mainViewModel!!.getParamSelect(this@GasStationListActivity)
            TipGifDialog.show(this@GasStationListActivity, "查询参数错误,请重试", TipGifDialog.TYPE.WARNING)
            return
        }
        val dataMap: HashMap<String, Any> = HashMap()
        dataMap.put("pageNow", pageIndex)
        dataMap.put("pageSize", 20)
        dataMap.put("sort", selectSortsParam!!.value)
        dataMap.put("latitude", latitude)
        dataMap.put("longitude", longitude)
        dataMap.put("distance", selectDistanceParam!!.value)
        dataMap.put("oilNo", selectOilParam!!.oilNo)
        dataMap.put("keyword", keyword)
        mainViewModel!!.getStationList(this@GasStationListActivity, dataMap)
            .observe(this@GasStationListActivity,
                { oilStationBeans ->
                    if (null == refreshLayout) {
                        TipGifDialog.dismiss()
                    } else {
                        if (isRefresh) {
                            refreshLayout.finishRefresh()
                        }
                        if (isLoadmore) {
                            refreshLayout.finishLoadMore()
                        }
                    }
                    //                if (null == oilStationBeans)
                    //                    return;
                    if (isLoadmore) {
                        if (null != oilStationAdapter) {
                            if (null != oilStationBeans && oilStationBeans.size > 0) {
                                oilStationAdapter!!.addAllData(oilStationBeans, false)
                            }
                        }
                    } else {
                        oilStationAdapter!!.addAllData(oilStationBeans, true)
                    }
                    isRefresh = false
                    isLoadmore = false
                    //                viewPager.setObjectForPosition(view,fragment_id);
                })
    }
}