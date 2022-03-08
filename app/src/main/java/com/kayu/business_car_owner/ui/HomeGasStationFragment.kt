package com.kayu.business_car_owner.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.hjq.toast.ToastUtils
import com.kayu.business_car_owner.model.DistancesParam
import com.kayu.business_car_owner.model.OilsParam
import com.kayu.business_car_owner.model.SortsParam
import com.kayu.business_car_owner.KWApplication
import com.kayu.business_car_owner.model.OilStationBean
import com.kayu.business_car_owner.ui.adapter.OilStationAdapter
import com.kayu.business_car_owner.model.ParamParent
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.kayu.business_car_owner.activity.WebViewActivity
import com.kayu.business_car_owner.activity.MainViewModel
import com.kayu.utils.view.AdaptiveHeightViewPager
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kayu.business_car_owner.R
import com.kongzue.dialog.v3.TipGifDialog
import com.kayu.business_car_owner.ui.adapter.ParamParentAdapter
import com.kayu.business_car_owner.activity.OilStationActivity
import com.kayu.utils.*
import com.kayu.utils.callback.Callback
import java.util.ArrayList
import java.util.HashMap

class HomeGasStationFragment//        this.mainViewModel = mainViewModel;
//        this.context = context;
//    private Context context;
    (
    private val viewPager: AdaptiveHeightViewPager?,
    private val fragment_id: Int,
    private val callback: Callback?
) : Fragment() {
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
    private var mLatitude = 0.0 //纬度
    private var mLongitude = 0.0 //经度
    private val keyword: String = "" //搜索关键字
    private var mView: View? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mainViewModel = ViewModelProviders.of(requireActivity()).get(MainViewModel::class.java)
        LogUtil.e("-------HomeGasStationFragment----", "----onCreateView---")
        return inflater.inflate(R.layout.fragment_home_gas_station, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.mView = view
        station_rv = view.findViewById(R.id.gas_station_rv)
        param_distance = view.findViewById(R.id.station_param_distance)
        param_oil_type = view.findViewById(R.id.station_param_oil_type)
        param_sort = view.findViewById(R.id.station_param_sort)
        param_recycle_view = view.findViewById(R.id.station_param_recycler)
        param_recycle_view?.setLayoutManager(LinearLayoutManager(context))
        station_rv?.setLayoutManager(LinearLayoutManager(context))
        oilStationAdapter =
            OilStationAdapter(requireContext(), null, true, true, object : ItemCallback {
                override fun onItemCallback(position: Int, obj: Any?) {
                    val oilStationBean = obj as OilStationBean
                    if (oilStationBean.nextIsBuy == 1) {
                        mainViewModel!!.getPayUrl(
                            requireContext(),
                            oilStationBean.gasId, -1,
                            selectOilParam!!.oilNo, mLatitude, mLongitude
                        )
                            .observe(requireActivity(), Observer { webBean ->
                                if (null == webBean) {
                                    ToastUtils.show("未获取到支付信息")
                                    return@Observer
                                }
                                val jumpCls: Class<*>
                                //                            if (oilStationBean.channel.equals("qj")) {
//                                jumpCls = AgentWebViewActivity.class;
//                            } else {
//                                jumpCls = WebViewActivity.class;
//                            }
                                jumpCls = WebViewActivity::class.java
                                val intent = Intent(requireContext(), jumpCls)
                                intent.putExtra("url", webBean.link)
                                intent.putExtra("title", "订单")
                                intent.putExtra("data", webBean.data)
                                intent.putExtra("channel", oilStationBean.channel)
                                intent.putExtra("gasId", oilStationBean.gasId)
                                //                                intent.putExtra("from", "首页");
                                startActivityForResult(intent, 111)
                            })
                    } else {
//                        val userRole = KWApplication.instance.userRole
//                        val isPublic = KWApplication.instance.isGasPublic
//                        if (userRole == -2 && isPublic == 0) {
//                            KWApplication.instance.showRegDialog(requireContext())
//                            return
//                        }
                        val intent = Intent(requireContext(), OilStationActivity::class.java)
                        intent.putExtra("gasId", oilStationBean.gasId)
                        startActivity(intent)
                    }
                }

                override fun onDetailCallBack(position: Int, obj: Any?) {}
            })
        station_rv?.setAdapter(oilStationAdapter)
        mainViewModel!!.getParamSelect(requireContext())
            .observe(requireActivity(), Observer { paramOilBean ->
                if (null == paramOilBean) return@Observer
                for (item in paramOilBean.distancesParamList!!) {
                    if (item.isDefault == 1) {
                        param_distance?.setText(item.name)
                        //                        distance = item.val;
                        selectDistanceParam = item
                    }
                }
                for (item in paramOilBean.sortsParamList!!) {
                    if (item.isDefault == 1) {
                        param_sort?.setText(item.name)
                        //                        sort = item.val;
                        selectSortsParam = item
                    }
                }
                for (oilsParam in paramOilBean.oilsTypeParamList!!) {
                    if (oilsParam.isDefault == 1) {
                        param_oil_type?.setText(oilsParam.oilName)
                        //                            oilNo = oilsParam.oilNo;
                        selectOilParam = oilsParam
                    }
                }
                param_sort?.setOnClickListener(View.OnClickListener {
                    param_oil_type?.isSelected = false
                    param_distance?.isSelected = false
                    if (param_sort?.isSelected == true) {
                        param_sort?.isSelected = false
                        param_recycle_view?.visibility = View.GONE
                        return@OnClickListener
                    }
                    val parents: MutableList<ParamParent> = ArrayList()
                    val paramParent = ParamParent()
                    paramParent.type = -1
                    paramParent.objList = ArrayList(paramOilBean.sortsParamList)
                    parents.add(paramParent)
                    showParamViewData(3, parents)
                    param_sort?.setSelected(true)
                })
                param_oil_type?.setOnClickListener(View.OnClickListener {
                    param_sort?.isSelected = false
                    param_distance?.isSelected = false
                    if (param_oil_type?.isSelected == true) {
                        param_oil_type?.isSelected = false
                        param_recycle_view?.visibility = View.GONE
                        return@OnClickListener
                    }
                    val parents: MutableList<ParamParent> = ArrayList()
                    val paramParent = ParamParent()
                    paramParent.type = -1
                    paramParent.objList = ArrayList(paramOilBean.oilsTypeParamList)
                    parents.add(paramParent)
                    showParamViewData(2, parents)
                    param_oil_type?.isSelected = true
                })
                param_distance?.setOnClickListener(View.OnClickListener {
                    param_oil_type?.isSelected = false
                    param_sort?.isSelected = false
                    if (param_distance?.isSelected == true) {
                        param_distance?.isSelected = false
                        param_recycle_view?.visibility = View.GONE
                        return@OnClickListener
                    }
                    val parents: MutableList<ParamParent> = ArrayList()
                    val paramParent = ParamParent()
                    paramParent.type = -1
                    paramParent.objList = ArrayList(paramOilBean.distancesParamList)
                    parents.add(paramParent)
                    showParamViewData(1, parents)
                    param_distance?.setSelected(true)
                })
                viewPager!!.setObjectForPosition(view, fragment_id)
            })
        LogUtil.e("-------HomeGasStationFragment----", "----onViewCreated---")
    }

    private fun showParamViewData(flag: Int, data: MutableList<ParamParent>) {
        if (param_recycle_view!!.visibility != View.VISIBLE) param_recycle_view!!.visibility =
            View.VISIBLE
        param_recycle_view!!.adapter = ParamParentAdapter(requireContext(), data, object : ItemCallback {
            override fun onItemCallback(position: Int, obj: Any?) {
                val paramOilBean = mainViewModel!!.getParamSelect(requireContext()).value
                if (null == paramOilBean || null == obj) return
                if (flag == 1) {
                    selectDistanceParam = obj as DistancesParam
                    selectDistanceParam!!.isDefault = 1
                    for (item in paramOilBean.distancesParamList!!) {
                        if (item.isDefault == 1) {
                            item.isDefault = 0
                        }
                        if (item.value == selectDistanceParam!!.value) {
                            item.isDefault = 1
                            item.name = selectDistanceParam!!.name
                            item.value = selectDistanceParam!!.value
                            param_distance!!.text = item.name
                        }
                    }
                } else if (flag == 2) {
                    selectOilParam = obj as OilsParam
                    for (item in paramOilBean.oilsTypeParamList!!) {
                        if (item.isDefault == 1) {
                            item.isDefault = 0
                        }
                        if (item.oilNo == selectOilParam!!.oilNo) {
                            item.isDefault = 1
                            item.gasId = selectOilParam!!.gasId
                            item.gunDiscount = selectOilParam!!.gunDiscount
                            item.gunNos = selectOilParam!!.gunNos
                            item.id = selectOilParam!!.id
                            item.offDiscount = selectOilParam!!.offDiscount
                            item.oilName = selectOilParam!!.oilName
                            item.oilNo = selectOilParam!!.oilNo
                            item.oilType = selectOilParam!!.oilType
                            item.priceGun = selectOilParam!!.priceGun
                            item.priceOfficial = selectOilParam!!.priceOfficial
                            item.priceYfq = selectOilParam!!.priceYfq
                            param_oil_type!!.text = item.oilName
                        }
                    }
                } else if (flag == 3) {
                    selectSortsParam = obj as SortsParam
                    for (item in paramOilBean.sortsParamList!!) {
                        if (item.isDefault == 1) {
                            item.isDefault = 0
                        }
                        if (item.value == selectSortsParam!!.value) {
                            item.isDefault = 1
                            item.name = selectSortsParam!!.name
                            item.value = selectSortsParam!!.value
                            param_sort!!.text = item.name
                        }
                    }
                }
                if (param_sort!!.isSelected) {
                    param_sort!!.isSelected = false
                }
                if (param_oil_type!!.isSelected) {
                    param_oil_type!!.isSelected = false
                }
                if (param_distance!!.isSelected) {
                    param_distance!!.isSelected = false
                }
                param_recycle_view!!.visibility = View.GONE
                val pageIndex = 1
                callback!!.onError()
                reqData(null, pageIndex, true, false, mLatitude, mLongitude)
            }

            override fun onDetailCallBack(position: Int, obj: Any?) {}
        }, flag)
    }

    fun reqData(
        refreshLayout: RefreshLayout?,
        pageIndex: Int,
        isRefresh: Boolean,
        isLoadmore: Boolean,
        latitude: Double,
        longitude: Double
    ) {
        if (null == refreshLayout) {
            if (isAdded) {
                TipGifDialog.show(
                    requireContext() as AppCompatActivity,
                    "刷新数据,稍等...",
                    TipGifDialog.TYPE.OTHER,
                    R.drawable.loading_gif
                )
            }
        }
        if (null == selectSortsParam || null == selectDistanceParam || null == selectOilParam) {
            mainViewModel!!.getParamSelect(requireContext())
            //            if (isAdded()) {
//                TipGifDialog.show((AppCompatActivity) requireContext(),"查询参数错误,请重试", TipGifDialog.TYPE.WARNING);
//            }
            return
        }
        mLatitude = latitude
        mLongitude = longitude
        if (isRefresh && null != oilStationAdapter) oilStationAdapter!!.removeAllData(true)
        val dataMap = HashMap<String, Any>()
        dataMap["pageNow"] = pageIndex
        dataMap["pageSize"] = 20
        dataMap["sort"] = selectSortsParam!!.value
        dataMap["latitude"] = latitude
        dataMap["longitude"] = longitude
        dataMap["distance"] = selectDistanceParam!!.value
        dataMap["oilNo"] = selectOilParam!!.oilNo
        dataMap["keyword"] = keyword
        mainViewModel!!.getStationList(requireContext(), dataMap)
            .observe(requireActivity(), { oilStationBeans ->
                if (null == refreshLayout) {
                    TipGifDialog.dismiss()
                }
                callback?.onSuccess()
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
                viewPager!!.setObjectForPosition(mView!!, fragment_id)
            })
    }
}