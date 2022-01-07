package com.kayu.business_car_owner.activity

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.kongzue.dialog.v3.TipGifDialog
import com.kongzue.dialog.interfaces.OnDialogButtonClickListener
import com.kongzue.dialog.util.BaseDialog
import com.kayu.utils.location.LocationManagerUtil
import com.kayu.utils.permission.EasyPermissions.DialogCallback
import com.kongzue.dialog.v3.MessageDialog
import com.kayu.utils.permission.EasyPermissions
import com.kayu.business_car_owner.model.WashStationBean
import com.kayu.business_car_owner.model.ParamWashBean
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amap.api.location.AMapLocation
import com.kayu.business_car_owner.model.WashParam
import com.kayu.business_car_owner.ui.adapter.WashStationAdapter
import com.kayu.utils.location.CoordinateTransformUtil
import com.kayu.business_car_owner.model.ParamParent
import com.kayu.business_car_owner.ui.adapter.ParamParentAdapter
import com.kayu.business_car_owner.R
import com.kayu.utils.*
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import java.util.ArrayList
import java.util.HashMap

class CarWashListActivity constructor() : BaseActivity() {
    var selectDistanceParam: WashParam? = null
    var selectSortsParam: WashParam? = null
    private var mainViewModel: MainViewModel? = null
    private var stationAdapter: WashStationAdapter? = null
    private var station_rv: RecyclerView? = null
    private var param_recycle_view: RecyclerView? = null
    private var param_distance: TextView? = null
    private var param_sort: TextView? = null

    //    private Callback callback;
    var isLoadmore: Boolean = false
    var isRefresh: Boolean = false
    private var pageIndex: Int = 0
    private var refreshLayout: RefreshLayout? = null
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_car_wash_list)
        mainViewModel = ViewModelProviders.of(this@CarWashListActivity).get(
            MainViewModel::class.java
        )

        //标题栏
//        LinearLayout title_lay = findViewById(R.id.title_lay);
//        title_lay.setBackgroundColor(getResources().getColor(R.color.background_gray));
        val title_name: TextView = findViewById(R.id.title_name_tv)
        title_name.setText("特惠洗车")
        findViewById<View>(R.id.title_back_btu).setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                onBackPressed()
            }

            override fun OnMoreErrorClick() {}
        })
        //        TextView back_tv = view.findViewById(R.id.title_back_tv);
//        back_tv.setText("我的");
        station_rv = findViewById(R.id.car_wash_rv)
        param_distance = findViewById(R.id.car_wash_param_distance)
        param_sort = findViewById(R.id.car_wash_param_sort)
        param_recycle_view = findViewById(R.id.car_wash_param_recycler)
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
                if (null != stationAdapter) {
                    stationAdapter!!.removeAllData(true)
                }
                val bddfsdfs: DoubleArray = CoordinateTransformUtil.gcj02tobd09(
                    location.getLongitude(),
                    location.getLatitude()
                )
                reqData(
                    refreshLayout,
                    pageIndex,
                    bddfsdfs.get(1),
                    bddfsdfs.get(0),
                    location.getCity()
                )
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
                val bddfsdfs: DoubleArray = CoordinateTransformUtil.gcj02tobd09(
                    location.getLongitude(),
                    location.getLatitude()
                )
                reqData(
                    refreshLayout,
                    pageIndex,
                    bddfsdfs.get(1),
                    bddfsdfs.get(0),
                    location.getCity()
                )
            }
        })
        station_rv?.layoutManager = LinearLayoutManager(this@CarWashListActivity)
        stationAdapter =
            WashStationAdapter(this@CarWashListActivity, null, true, true, object : ItemCallback {
                override fun onItemCallback(position: Int, obj: Any?) {
                    val intent: Intent =
                        Intent(this@CarWashListActivity, WashStationActivity::class.java)
                    intent.putExtra("shopCode", (obj as WashStationBean).shopCode)
                    startActivity(intent)
                }

                override fun onDetailCallBack(position: Int, obj: Any?) {}
            })
        station_rv?.adapter = stationAdapter
        param_recycle_view?.layoutManager = LinearLayoutManager(this@CarWashListActivity)
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
                public override fun hasPermission(allPerms: List<Array<String>>) {
                    if (!LocationManagerUtil.self?.isLocServiceEnable!!) {
                        MessageDialog.show(
                            this@CarWashListActivity,
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
                                public override fun onClick(
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
                        this@CarWashListActivity,
                        1,
                        Constants.RC_PERMISSION_PERMISSION_FRAGMENT,
                        Constants.RC_PERMISSION_BASE
                    )
                }

                public override fun showDialog(dialogType: Int, callback: DialogCallback) {
                    val dialog: MessageDialog =
                        MessageDialog.build((this@CarWashListActivity as AppCompatActivity?)!!)
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
//        TipGifDialog.show(CarWashListActivity.this, "稍等...", TipGifDialog.TYPE.OTHER,R.drawable.loading_gif);
        mainViewModel!!.getParamWash(this@CarWashListActivity)
            .observe(this@CarWashListActivity, object : Observer<ParamWashBean?> {
                override fun onChanged(paramWashBean: ParamWashBean?) {
//                TipGifDialog.dismiss();
                    if (null == paramWashBean) return
                    for (item: WashParam in paramWashBean.desList!!) {
                        if (item.isDefault == 1) {
                            param_distance!!.setText(item.name)
                            selectDistanceParam = item
                        }
                    }
                    for (item: WashParam in paramWashBean.typesList!!) {
                        if (item.isDefault == 1) {
                            param_sort!!.setText(item.name)
                            selectSortsParam = item
                        }
                    }
                    param_sort!!.setOnClickListener(object : View.OnClickListener {
                        override fun onClick(v: View) {
                            param_distance!!.setSelected(false)
                            if (param_sort!!.isSelected()) {
                                param_sort!!.setSelected(false)
                                param_recycle_view!!.setVisibility(View.GONE)
                                return
                            }
                            val parents: MutableList<ParamParent> = ArrayList()
                            val paramParent: ParamParent = ParamParent()
                            paramParent.type = -1
                            paramParent.objList = ArrayList<Any>(paramWashBean.typesList)
                            parents.add(paramParent)
                            showParamViewData(5, parents)
                            param_sort!!.setSelected(true)
                        }
                    })
                    param_distance!!.setOnClickListener(object : View.OnClickListener {
                        override fun onClick(v: View) {
                            param_sort!!.setSelected(false)
                            if (param_distance!!.isSelected()) {
                                param_distance!!.setSelected(false)
                                param_recycle_view!!.setVisibility(View.GONE)
                                return
                            }
                            val parents: MutableList<ParamParent> = ArrayList()
                            val paramParent: ParamParent = ParamParent()
                            paramParent.type = -1
                            paramParent.objList = ArrayList<Any>(paramWashBean.desList)
                            parents.add(paramParent)
                            showParamViewData(4, parents)
                            param_distance!!.setSelected(true)
                        }
                    })
                    pageIndex = 1
                    if (null != stationAdapter) {
                        stationAdapter!!.removeAllData(true)
                    }
                    val location: AMapLocation? = LocationManagerUtil.self?.loccation
                    if (null != location) {
                        val bddfsdfs: DoubleArray = CoordinateTransformUtil.gcj02tobd09(
                            location.longitude,
                            location.latitude
                        )
                        reqData(
                            null,
                            pageIndex,
                            bddfsdfs[1],
                            bddfsdfs[0],
                            location.city
                        )
                    }
                }
            })
    }

    private fun showParamViewData(flag: Int, data: List<ParamParent>) {
        if (param_recycle_view!!.getVisibility() == View.GONE) param_recycle_view!!.setVisibility(
            View.VISIBLE
        )
        param_recycle_view!!.adapter = ParamParentAdapter(
            this@CarWashListActivity,
            data as MutableList<ParamParent>,
            object : ItemCallback {
                override fun onItemCallback(position: Int, obj: Any?) {
                    val paramWashBean: ParamWashBean? =
                        mainViewModel!!.getParamWash(this@CarWashListActivity).value
                    if (null == paramWashBean || null == obj) return
                    if (flag == 4) {
                        selectDistanceParam = obj as WashParam?
                        selectDistanceParam!!.isDefault = 1
                        for (item: WashParam? in paramWashBean.desList!!) {
                            if (item!!.isDefault == 1) {
                                item.isDefault = 0
                            }
                            if ((item.value == selectDistanceParam!!.value)) {
                                item.isDefault = 1
                                item.value = selectDistanceParam!!.value
                                item.name = selectDistanceParam!!.name
                                param_distance!!.text = item.name
                            }
                        }
                    } else if (flag == 5) {
                        selectSortsParam = obj as WashParam?
                        for (item: WashParam? in paramWashBean.typesList!!) {
                            if (item!!.isDefault == 1) {
                                item.isDefault = 0
                            }
                            if ((item.value == selectSortsParam!!.value)) {
                                item.isDefault = 1
                                item.value = selectSortsParam!!.value
                                item.name = selectSortsParam!!.name
                                param_sort!!.text = item.name
                            }
                        }
                    }
                    if (param_sort!!.isSelected()) {
                        param_sort!!.setSelected(false)
                    }
                    if (param_distance!!.isSelected()) {
                        param_distance!!.setSelected(false)
                    }
                    param_recycle_view!!.setVisibility(View.GONE)
                    pageIndex = 1
                    isRefresh = true
                    val location: AMapLocation = LocationManagerUtil.self?.loccation!!
                    val bddfsdfs: DoubleArray = CoordinateTransformUtil.gcj02tobd09(
                        location.getLongitude(),
                        location.getLatitude()
                    )
                    if (null != stationAdapter) {
                        stationAdapter!!.removeAllData(true)
                    }
                    reqData(
                        null,
                        pageIndex,
                        bddfsdfs.get(1),
                        bddfsdfs.get(0),
                        location.getCity()
                    )
                }

                override fun onDetailCallBack(position: Int, obj: Any?) {}
            },
            flag
        )
    }

    fun reqData(
        refreshLayout: RefreshLayout?,
        pageIndex: Int,
        latitude: Double,
        longitude: Double,
        cityName: String
    ) {
        if (null == refreshLayout) {
            TipGifDialog.show(
                this@CarWashListActivity,
                "稍等...",
                TipGifDialog.TYPE.OTHER,
                R.drawable.loading_gif
            )
        }
        if (null == selectSortsParam || null == selectDistanceParam) {
            mainViewModel!!.getParamWash(this@CarWashListActivity)
            TipGifDialog.show(this@CarWashListActivity, "查询参数错误,请重试", TipGifDialog.TYPE.WARNING)
            return
        }
        val dataMap: HashMap<String, Any> = HashMap()
        dataMap.put("pageNum", pageIndex)
        dataMap.put("pageSize", 20)
        dataMap.put("cusLatitude", latitude.toString())
        dataMap.put("cusLongitude", longitude.toString())
        dataMap.put("cityName", cityName)
        selectDistanceParam!!.value?.let { dataMap.put("priority", it) }
        selectSortsParam!!.value?.let { dataMap.put("serviceCode", it) }
        mainViewModel!!.getWashStationList(this@CarWashListActivity, dataMap)
            .observe(this@CarWashListActivity,
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
                    if (isLoadmore) {
                        if (null != stationAdapter) {
                            if (null != oilStationBeans && oilStationBeans.size > 0) {
                                stationAdapter!!.addAllData(
                                    oilStationBeans,
                                    selectSortsParam!!.value,
                                    false
                                )
                            }
                        }
                    } else {
                        stationAdapter!!.addAllData(oilStationBeans, selectSortsParam!!.value, true)
                    }
                    isLoadmore = false
                    isRefresh = false
                })
    }
}