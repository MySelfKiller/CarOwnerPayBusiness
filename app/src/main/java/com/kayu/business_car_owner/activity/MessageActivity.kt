package com.kayu.business_car_owner.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.TextView
import com.hjq.toast.ToastUtils
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.kayu.business_car_owner.ui.adapter.ItemMessageAdapter
import com.kayu.business_car_owner.model.ItemMessageBean
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.kayu.business_car_owner.data_parser.MessageListParser
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kayu.business_car_owner.R
import com.kayu.business_car_owner.http.*
import com.kayu.utils.*
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import java.util.ArrayList
import java.util.HashMap

class MessageActivity constructor() : BaseActivity() {
    var isLoadmore: Boolean = false
    var isRefresh: Boolean = false
    private var pageIndex: Int = 0
    private var refreshLayout: RefreshLayout? = null
    private var recyclerView: RecyclerView? = null
    private var adapter: ItemMessageAdapter? = null
    private var messageDataList: ArrayList<ItemMessageBean>? = null
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_message)
        //标题栏
        findViewById<View>(R.id.title_back_btu).setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                onBackPressed()
            }

            override fun OnMoreErrorClick() {}
        })
        val back_tv: TextView = findViewById(R.id.title_back_tv)
        val title_name: TextView = findViewById(R.id.title_name_tv)
        title_name.setText("消息通知")
        //        title_name.setVisibility(View.GONE);
        back_tv.setText("我的")
        refreshLayout = findViewById<View>(R.id.refreshLayout) as RefreshLayout?
        refreshLayout!!.setEnableAutoLoadMore(false)
        refreshLayout!!.setEnableLoadMore(true)
        refreshLayout!!.setEnableLoadMoreWhenContentNotFull(true) //是否在列表不满一页时候开启上拉加载功能
        refreshLayout!!.setEnableOverScrollBounce(true) //是否启用越界回弹
        refreshLayout!!.setEnableOverScrollDrag(true)
        refreshLayout!!.setOnRefreshListener(object : OnRefreshListener {
            public override fun onRefresh(refreshLayout: RefreshLayout) {
                if (isRefresh || isLoadmore) return
                isRefresh = true
                pageIndex = 1
                if (null != adapter) {
                    adapter!!.removeAllData()
                }
                reqData()
            }
        })
        refreshLayout!!.setOnLoadMoreListener(object : OnLoadMoreListener {
            public override fun onLoadMore(refreshLayout: RefreshLayout) {
                if (isRefresh || isLoadmore) return
                isLoadmore = true
                pageIndex = pageIndex + 1
                reqData()
            }
        })
        recyclerView = findViewById<View>(R.id.custom_list_recycler) as RecyclerView?
        val context: Context = this@MessageActivity
        recyclerView!!.setLayoutManager(LinearLayoutManager(context))
        refreshLayout!!.autoRefresh()
        //        isCreated = true;
    }

    //    private boolean mHasLoadedOnce = false;// 页面已经加载过
    //    private boolean isCreated = false;
    //    @Override
    //    public void setUserVisibleHint(boolean isVisibleToUser) {
    //        super.setUserVisibleHint(isVisibleToUser);
    //        if (isVisibleToUser && !mHasLoadedOnce && isCreated) {
    //            refreshLayout.autoRefresh();
    //        }
    //    }
    public override fun onDestroy() {
        super.onDestroy()
        //        isCreated = false;
    }

    @SuppressLint("HandlerLeak")
    private fun reqData() {
        val reques: RequestInfo = RequestInfo()
        reques.context = this@MessageActivity
        reques.reqUrl = HttpConfig.HOST + HttpConfig.INTERFACE_MESSAGE_LIST
        reques.parser = MessageListParser()
        val reqDateMap: HashMap<String, Any> = HashMap()
        reqDateMap.put("pageNow", pageIndex)
        reqDateMap.put("pageSize", 20)
        reques.reqDataMap = reqDateMap
        reques.handler = object : Handler() {
            public override fun handleMessage(msg: Message) {
                val resInfo: ResponseInfo = msg.obj as ResponseInfo
                if (resInfo.status == 1) {
                    messageDataList = resInfo.responseData as ArrayList<ItemMessageBean>?
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
                if (null != messageDataList && messageDataList!!.size > 0) {
                    adapter!!.addAllData(messageDataList, false)
                }
            }
        } else {
            adapter = ItemMessageAdapter(
                this@MessageActivity,
                messageDataList,
                (null == messageDataList || messageDataList!!.size == 0),
                object : ItemCallback {
                    override fun onItemCallback(position: Int, obj: Any?) {
                        val intent = Intent(this@MessageActivity, WebViewActivity::class.java)
                        intent.putExtra("url", obj as String?)
                        //                    intent.putExtra("from",title);
                        startActivity(intent)
                    }

                    override fun onDetailCallBack(position: Int, obj: Any?) {}
                })
            recyclerView!!.adapter = adapter
        }
    }
}