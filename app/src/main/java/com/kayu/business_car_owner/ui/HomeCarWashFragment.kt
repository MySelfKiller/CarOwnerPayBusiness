package com.kayu.business_car_owner.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.kayu.business_car_owner.model.WashParam
import com.kayu.business_car_owner.model.ParamParent
import com.kayu.business_car_owner.model.WashStationBean
import com.kayu.business_car_owner.ui.adapter.WashStationAdapter
import com.kayu.business_car_owner.activity.WashStationActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.kayu.business_car_owner.activity.MainViewModel
import com.kayu.utils.view.AdaptiveHeightViewPager
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kayu.business_car_owner.R
import com.kongzue.dialog.v3.TipGifDialog
import com.kayu.business_car_owner.ui.adapter.ParamParentAdapter
import com.kayu.utils.*
import com.kayu.utils.callback.Callback
import java.util.HashMap
import kotlin.collections.ArrayList

class HomeCarWashFragment(
    private val viewPager: AdaptiveHeightViewPager?,
    private var fragment_id: Int,
    private val callback: Callback?
) : Fragment() {
    open var mView: View? = null
    var selectDistanceParam: WashParam? = null
    var selectSortsParam: WashParam? = null
    private var mainViewModel: MainViewModel? = null
    private var stationAdapter: WashStationAdapter? = null
    private var station_rv: RecyclerView? = null
    private var param_recycle_view: RecyclerView? = null
    private var param_distance: TextView? = null
    private var param_sort: TextView? = null
    private var mCityName: String? = null
    private var mLatitude = 0.0 //纬度
    private var mLongitude = 0.0 //经度
    fun setFragment_id(fragment_id: Int): HomeCarWashFragment {
        this.fragment_id = fragment_id
        return this
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mainViewModel = ViewModelProviders.of(requireActivity()).get(MainViewModel::class.java)
        LogUtil.e("-------HomeCarWashFragment----", "----onCreateView---")
        mView = inflater.inflate(R.layout.fragment_home_car_wash, container, false)
        initView()
        return mView
    }

    fun initView() {
//        this.view = view;
        station_rv = mView?.findViewById(R.id.car_wash_rv)
        param_distance = mView?.findViewById(R.id.car_wash_param_distance)
        param_sort = mView?.findViewById(R.id.car_wash_param_sort)
        param_recycle_view = mView?.findViewById(R.id.car_wash_param_recycler)
        station_rv?.setLayoutManager(LinearLayoutManager(context))
        param_recycle_view?.setLayoutManager(LinearLayoutManager(context))
        stationAdapter = WashStationAdapter(context, null, true, true, object : ItemCallback {
            override fun onItemCallback(position: Int, obj: Any?) {
//                val userRole = KWApplication.instance.userRole
//                val isPublic = KWApplication.instance.isWashPublic
//                if (userRole == -2 && isPublic == 0) {
//                    KWApplication.instance.showRegDialog(requireContext())
//                    return
//                }
                val intent = Intent(context, WashStationActivity::class.java)
                intent.putExtra("shopCode", (obj as WashStationBean).shopCode)
                startActivity(intent)
            }

            override fun onDetailCallBack(position: Int, obj: Any?) {}
        })
        station_rv?.setAdapter(stationAdapter)
        mainViewModel!!.getParamWash(requireContext())
            .observe(requireActivity(), Observer { paramWashBean ->
                if (null == paramWashBean) return@Observer
                for (item in paramWashBean.desList!!) {
                    if (item.isDefault == 1) {
                        param_distance?.setText(item.name)
                        selectDistanceParam = item
                    }
                }
                for (item in paramWashBean.typesList!!) {
                    if (item.isDefault == 1) {
                        param_sort?.setText(item.name)
                        selectSortsParam = item
                    }
                }
                param_sort?.setOnClickListener(View.OnClickListener {
                    param_distance?.setSelected(false)
                    if (param_sort?.isSelected() == true) {
                        param_sort?.setSelected(false)
                        param_recycle_view?.setVisibility(View.GONE)
                        return@OnClickListener
                    }
                    val parents: ArrayList<ParamParent> = ArrayList()
                    val paramParent = ParamParent()
                    paramParent.type = -1
                    paramParent.objList = ArrayList(paramWashBean.typesList)
                    parents.add(paramParent)
                    showParamViewData(5, parents)
                    param_sort?.setSelected(true)
                })
                param_distance?.setOnClickListener(View.OnClickListener {
                    param_sort?.setSelected(false)
                    if (param_distance?.isSelected() == true) {
                        param_distance?.setSelected(false)
                        param_recycle_view?.setVisibility(View.GONE)
                        return@OnClickListener
                    }
                    val parents: ArrayList<ParamParent> = ArrayList()
                    val paramParent = ParamParent()
                    paramParent.type = -1
                    paramParent.objList = ArrayList(paramWashBean.desList)
                    parents.add(paramParent)
                    showParamViewData(4, parents)
                    param_distance?.setSelected(true)
                })
                viewPager!!.setObjectForPosition( mView!!, fragment_id)
            })
        LogUtil.e("-------HomeCarWashFragment----", "----onViewCreated---")
    }

    private fun showParamViewData(flag: Int, data: ArrayList<ParamParent>) {
        if (param_recycle_view!!.visibility == View.GONE) param_recycle_view!!.visibility =
            View.VISIBLE
        param_recycle_view!!.adapter = ParamParentAdapter(requireContext(), data, object : ItemCallback {
            override fun onItemCallback(position: Int, obj: Any?) {
                val paramWashBean = mainViewModel!!.getParamWash(requireContext()).value
                if (null == paramWashBean || null == obj) return
                if (flag == 4) {
                    selectDistanceParam = obj as WashParam
                    selectDistanceParam!!.isDefault = 1
                    for (item in paramWashBean.desList!!) {
                        if (item.isDefault == 1) {
                            item.isDefault = 0
                        }
                        if (item.value == selectDistanceParam!!.value) {
                            item.isDefault = 1
                            item.value = selectDistanceParam!!.value
                            item.name = selectDistanceParam!!.name
                            param_distance!!.text = item.name
                        }
                    }
                } else if (flag == 5) {
                    selectSortsParam = obj as WashParam
                    for (item in paramWashBean.typesList!!) {
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
                if (param_distance!!.isSelected) {
                    param_distance!!.isSelected = false
                }
                param_recycle_view!!.visibility = View.GONE
                val pageIndex = 1
                callback!!.onError()
                reqData(null, pageIndex, true, false, mLatitude, mLongitude, mCityName)
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
        longitude: Double,
        cityName: String?
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
        //        else {
//            callback.onSuccess();
//        }
        if (null == selectSortsParam || null == selectDistanceParam) {
            mainViewModel!!.getParamWash(requireContext())
            //            if (isAdded()) {
//                TipGifDialog.show((AppCompatActivity) requireContext(),"查询参数错误,请重试", TipGifDialog.TYPE.WARNING);
//            }
            return
        }
        mLatitude = latitude
        mLongitude = longitude
        mCityName = cityName
        if (isRefresh && null != stationAdapter) stationAdapter!!.removeAllData(true)
        val dataMap = HashMap<String, Any>()
        dataMap["pageNum"] = pageIndex
        dataMap["pageSize"] = 20
        dataMap["cusLatitude"] = latitude.toString()
        dataMap["cusLongitude"] = longitude.toString()
        dataMap["cityName"] = cityName!!
        dataMap["priority"] = selectDistanceParam?.value!!
        dataMap["serviceCode"] = selectSortsParam?.value!!
        mainViewModel!!.getWashStationList(requireContext(), dataMap)
            .observe(requireActivity(), { oilStationBeans ->
                if (null == refreshLayout) {
                    TipGifDialog.dismiss()
                }
                callback?.onSuccess()
                //                if (null == oilStationBeans)
//                    return;
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
                viewPager!!.setObjectForPosition(mView!!, fragment_id)
            })
    }
}