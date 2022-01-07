package com.kayu.business_car_owner.ui

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.hjq.toast.ToastUtils
import com.kayu.business_car_owner.KWApplication
import com.kayu.business_car_owner.model.ItemWashOrderBean
import com.kayu.business_car_owner.ui.adapter.ItemWashOrderAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kongzue.dialog.v3.MessageDialog
import com.kayu.business_car_owner.R
import com.kayu.business_car_owner.activity.*
import com.kayu.business_car_owner.data_parser.WashOrderListDataParser
import com.kayu.business_car_owner.activity.BaseActivity.PermissionCallback
import com.kayu.business_car_owner.http.*
import com.kayu.utils.permission.EasyPermissions
import com.kayu.utils.permission.EasyPermissions.DialogCallback
import com.kayu.utils.*
import com.kayu.utils.callback.Callback
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import java.util.ArrayList
import java.util.HashMap

class WashOrderAllFragment(private val orderStatus: Int) : Fragment() {
    var isLoadmore = false
    var isRefresh = false
    private var pageIndex = 0
    private var refreshLayout: RefreshLayout? = null
    private var orderData: ArrayList<ItemWashOrderBean>? = null
    private var recyclerView: RecyclerView? = null
    private var adapter: ItemWashOrderAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wash_order_all, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        refreshLayout = view.findViewById<View>(R.id.refreshLayout) as RefreshLayout
        refreshLayout!!.setEnableAutoLoadMore(false)
        refreshLayout!!.setEnableLoadMore(true)
        refreshLayout!!.setEnableLoadMoreWhenContentNotFull(true) //是否在列表不满一页时候开启上拉加载功能
        refreshLayout!!.setEnableOverScrollBounce(true) //是否启用越界回弹
        refreshLayout!!.setEnableOverScrollDrag(true)
        refreshLayout!!.setOnRefreshListener(OnRefreshListener {
            if (isRefresh || isLoadmore) return@OnRefreshListener
            isRefresh = true
            pageIndex = 1
            if (null != adapter) {
                adapter!!.removeAllData()
            }
            reqData(orderStatus)
        })
        refreshLayout!!.setOnLoadMoreListener(OnLoadMoreListener {
            if (isRefresh || isLoadmore) return@OnLoadMoreListener
            isLoadmore = true
            pageIndex = pageIndex + 1
            reqData(orderStatus)
        })
        recyclerView = view.findViewById<View>(R.id.custom_list_recycler) as RecyclerView
        val context = view.context
        recyclerView!!.layoutManager = LinearLayoutManager(context)
        //        if (getUserVisibleHint() && !mHasLoadedOnce){
//            refreshLayout.autoRefresh();
////            mHasLoadedOnce = true;
//        }
        isCreated = true
    }

    private val mHasLoadedOnce = false // 页面已经加载过
    private var isCreated = false
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        LogUtil.e("hm", "WashOrderAllFragment---------setUserVisibleHint====$isVisibleToUser")
        if (isVisibleToUser && !mHasLoadedOnce && isCreated) {
            refreshLayout!!.autoRefresh()
            //            mHasLoadedOnce = true;
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        isCreated = false
    }

    override fun onPause() {
        super.onPause()
        LogUtil.e("hm", "WashOrderAllFragment---------onPause====$userVisibleHint$orderStatus")
    }

    override fun onResume() {
        super.onResume()
        LogUtil.e("hm", "WashOrderAllFragment---------onResume====$userVisibleHint$orderStatus")
        if (userVisibleHint && !mHasLoadedOnce && isCreated) {
            refreshLayout!!.autoRefresh()
            //            mHasLoadedOnce = true;
        }
    }

    override fun onStart() {
        super.onStart()
        LogUtil.e("hm", "WashOrderAllFragment---------onStart====$userVisibleHint$orderStatus")
    }

    override fun onStop() {
        super.onStop()
        LogUtil.e("hm", "WashOrderAllFragment---------onStop====$userVisibleHint$orderStatus")
    }

    @SuppressLint("HandlerLeak")
    private fun reqData(orderStatus: Int) {
        val reques = RequestInfo()
        reques.context = context
        reques.reqUrl = HttpConfig.HOST + HttpConfig.INTERFACE_WASH_ORDER_LIST
        reques.parser = WashOrderListDataParser()
        val reqDateMap = HashMap<String, Any>()
        reqDateMap["pageNow"] = pageIndex
        if (orderStatus > 0) {
            reqDateMap["state"] = orderStatus
        }
        reqDateMap["pageSize"] = 20
        reques.reqDataMap = reqDateMap
        reques.handler = object : Handler() {
            override fun handleMessage(msg: Message) {
                val resInfo = msg.obj as ResponseInfo
                if (null == this@WashOrderAllFragment.activity) {
                    LogUtil.e("hm", "WashOrderAllFragment 以销毁")
                    return
                }
                if (resInfo.status == 1) {
                    orderData = resInfo.responseData as ArrayList<ItemWashOrderBean>?
                    initViewData()
                } else {
                    ToastUtils.show(resInfo.msg)
                }
                if (isRefresh) {
                    refreshLayout!!.finishRefresh()
                    isRefresh = false
                }
                if (isLoadmore) {
                    refreshLayout!!.finishLoadMore()
                    isLoadmore = false
                }
                super.handleMessage(msg)
            }
        }
        val callback = ResponseCallback(reques)
        ReqUtil.setReqInfo(reques)
        ReqUtil.requestPostJSON(callback)
    }

    private fun initViewData() {
        if (isLoadmore) {
            if (null != adapter) {
                if (null != orderData && orderData!!.size > 0) {
                    adapter!!.addAllData(orderData, false)
                }
            }
        } else {
            adapter = ItemWashOrderAdapter(
                requireActivity(),
                orderData,
                null == orderData || orderData!!.size == 0,
                object : ItemCallback {
                    override fun onItemCallback(position: Int, obj: Any?) {
                        val washOrderBean = obj as ItemWashOrderBean
                        KWApplication.instance.toNavi(
                            requireContext(),
                            washOrderBean.latitude,
                            washOrderBean.longitude,
                            washOrderBean.address,
                            "BD09"
                        )
                    }

                    override fun onDetailCallBack(position: Int, obj: Any?) {
                        permissionsCheck(
                            arrayOf(Manifest.permission.CALL_PHONE),
                            R.string.permiss_call_phone,
                            object : Callback {
                                override fun onSuccess() {
                                    KWApplication.instance.callPhone(requireActivity(), (obj as ItemWashOrderBean).telephone!!)
                                }

                                override fun onError() {}
                            })
                    }
                })
            recyclerView!!.adapter = adapter
        }
    }

    fun permissionsCheck(perms: Array<String>, resId: Int, callback: Callback) {
//        String[] perms = {Manifest.permission.CAMERA};
        (activity as BaseActivity?)!!.performCodeWithPermission(
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
                        requireContext(),
                        1,
                        Constants.RC_PERMISSION_PERMISSION_FRAGMENT,
                        Constants.RC_PERMISSION_BASE
                    )
                }

                override fun showDialog(dialogType: Int, callback: DialogCallback) {
                    val dialog = MessageDialog.build((requireActivity() as AppCompatActivity))
                    dialog.title = "需要获取以下权限"
                    dialog.message = getString(resId)
                    dialog.setOkButton("下一步") { baseDialog, v ->
                        callback.onGranted()
                        false
                    }
                    dialog.cancelable = false
                    dialog.show()
                }
            })
    }
}