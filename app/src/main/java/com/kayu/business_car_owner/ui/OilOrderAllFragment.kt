package com.kayu.business_car_owner.ui

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.hjq.toast.ToastUtils
import com.kayu.business_car_owner.model.ItemOilOrderBean
import com.kayu.business_car_owner.ui.adapter.ItemOrderViewAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kayu.business_car_owner.R
import com.kayu.business_car_owner.data_parser.OilOrderListDataParser
import com.kongzue.dialog.v3.CustomDialog
import com.kayu.business_car_owner.http.*
import com.kayu.utils.*
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import java.util.ArrayList
import java.util.HashMap

class OilOrderAllFragment(private val orderStatus: Int) : Fragment() {
    var isLoadmore = false
    var isRefresh = false
    private var pageIndex = 0
    private var refreshLayout: RefreshLayout? = null
    private var orderData: ArrayList<ItemOilOrderBean>? = null
    private var recyclerView: RecyclerView? = null
    private var adapter: ItemOrderViewAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_order_all, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        refreshLayout = view.findViewById<View>(R.id.refreshLayout) as RefreshLayout
        refreshLayout!!.setEnableAutoLoadMore(false)
        refreshLayout!!.setEnableLoadMore(true)
        refreshLayout!!.setEnableLoadMoreWhenContentNotFull(true) //?????????????????????????????????????????????????????????
        refreshLayout!!.setEnableOverScrollBounce(true) //????????????????????????
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
        if (userVisibleHint && !mHasLoadedOnce) {
            refreshLayout!!.autoRefresh()
            //            mHasLoadedOnce = true;
        }
        isCreated = true
    }

    private val mHasLoadedOnce = false // ?????????????????????
    private var isCreated = false
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser && !mHasLoadedOnce && isCreated) {
            refreshLayout!!.autoRefresh()
            //            mHasLoadedOnce = true;
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        isCreated = false
    }

    @SuppressLint("HandlerLeak")
    private fun reqData(orderStatus: Int) {
        val reques = RequestInfo()
        reques.context = context
        reques.reqUrl = HttpConfig.HOST + HttpConfig.INTERFACE_GAS_ORDER_LIST
        reques.parser = OilOrderListDataParser()
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
                if (null == this@OilOrderAllFragment.activity) {
                    LogUtil.e("hm", "OilOrderAllFragment ?????????")
                    return
                }
                if (resInfo.status == 1) {
                    orderData = resInfo.responseData as ArrayList<ItemOilOrderBean>?
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
            adapter = ItemOrderViewAdapter(
                context,
                orderData,
                null == orderData || orderData!!.size == 0,
                object : ItemCallback {
                    override fun onItemCallback(position: Int, obj: Any?) {
                        showPop(obj as String)
                    }

                    override fun onDetailCallBack(position: Int, obj: Any?) {}
                })
            recyclerView!!.adapter = adapter
        }
    }

    private var popview: View? = null
    private var qrcode_iv: ImageView? = null
    private var save_btn: Button? = null
    private var compay_tv1: TextView? = null
    private var dialog: CustomDialog? = null
    private fun initPopView() {
        val nullParent: ViewGroup? = null
        popview = layoutInflater.inflate(R.layout.qrcode_lay, nullParent)
        qrcode_iv = popview?.findViewById(R.id.shared_qrcode_iv)
        save_btn = popview?.findViewById(R.id.shared_call_btn)
        compay_tv1 = popview?.findViewById(R.id.shared_compay_tv1)
    }

    private fun creatPopWindow(view: View?) {
        dialog = CustomDialog.build(context as AppCompatActivity?, view).setCancelable(true)
    }

    private fun showWindo() {
        if (null != dialog && !dialog!!.isShow) dialog!!.show()
    }

    var qrcodeBitmap: Bitmap? = null
    private fun showPop(qrCode: String) {
        if (StringUtil.isEmpty(qrCode)) {
            return
        }
        qrcodeBitmap = QRCodeUtil.createQRCodeBitmap(
            qrCode, 280, 280, "UTF-8",
            null, "0", Color.BLACK, Color.WHITE, null, 0f, null
        )
        qrcode_iv!!.setImageBitmap(qrcodeBitmap)
        initPopView()
        creatPopWindow(popview)
        showWindo()
        save_btn!!.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                if (null == qrcodeBitmap) {
                    ToastUtils.show("?????????????????????")
                    return
                }
                val fileName = "qr_" + System.currentTimeMillis() + ".jpg"
                val isSaveSuccess = ImageUtil.saveImageToGallery(requireContext(), qrcodeBitmap!!, fileName)
                if (isSaveSuccess) {
                    ToastUtils.show("????????????")
                } else {
                    ToastUtils.show("????????????")
                }
            }

            override fun OnMoreErrorClick() {}
        })
        compay_tv1!!.visibility = View.GONE
    }
}