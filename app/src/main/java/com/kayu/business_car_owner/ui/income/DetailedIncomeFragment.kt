package com.kayu.business_car_owner.ui.income

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.kayu.business_car_owner.data_parser.IncomeDetailedParser
import com.hjq.toast.ToastUtils
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kayu.business_car_owner.R
import com.kayu.business_car_owner.http.*
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import java.util.HashMap

class DetailedIncomeFragment(  //收入类型 null全部,0:支出 1:收入
    private val balanceType: Int
) : Fragment() {
    var isLoadmore = false
    var isRefresh = false
    private var refreshLayout: RefreshLayout? = null
    private var recyclerView: RecyclerView? = null
    private var adapter: IncomeDetialedItemRecyclerAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_detailed_income, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.detailed_recycler)
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
            reqData()
        })
        refreshLayout!!.setOnLoadMoreListener(OnLoadMoreListener {
            if (isRefresh || isLoadmore) return@OnLoadMoreListener
            isLoadmore = true
            pageIndex = pageIndex + 1
            reqData()
        })
        recyclerView?.setLayoutManager(LinearLayoutManager(context))
        if (userVisibleHint && !mHasLoadedOnce) {
            refreshLayout!!.autoRefresh()
            //            mHasLoadedOnce = true;
        }
        isCreated = true
    }

    private val mHasLoadedOnce = false // 页面已经加载过
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

    private var pageIndex = 0
    @SuppressLint("HandlerLeak")
    private fun reqData() {
        val reques = RequestInfo()
        reques.context = context
        reques.reqUrl = HttpConfig.HOST + HttpConfig.INTERFACE_BALANCE_DEAIL
        val reqDateMap = HashMap<String, Any>()
        reqDateMap["pageNow"] = pageIndex
        reqDateMap["pageSize"] = 20
        reqDateMap["type"] = balanceType
        reques.parser = IncomeDetailedParser()
        reques.reqDataMap = reqDateMap
        reques.handler = object : Handler() {
            override fun handleMessage(msg: Message) {
                val resInfo = msg.obj as ResponseInfo
                if (resInfo.status == 1) {
                    val dataList = resInfo.responseData as MutableList<IncomeDetailedData>?
                    initViewData(dataList)
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

    private fun initViewData(datalist: MutableList<IncomeDetailedData>?) {
        if (isLoadmore) {
            if (null != adapter) {
                if (null != datalist && datalist.size > 0) {
                    adapter!!.addAllData(datalist, false)
                }
            }
        } else {
            adapter =
                IncomeDetialedItemRecyclerAdapter(datalist, null == datalist || datalist.size == 0)
            recyclerView!!.adapter = adapter
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
    }
}