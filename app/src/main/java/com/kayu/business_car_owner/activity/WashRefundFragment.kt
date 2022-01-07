package com.kayu.business_car_owner.activity

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.kongzue.dialog.v3.TipGifDialog
import com.kongzue.dialog.interfaces.OnDialogButtonClickListener
import com.kongzue.dialog.util.BaseDialog
import com.kayu.business_car_owner.http.ResponseInfo
import com.kongzue.dialog.v3.MessageDialog
import com.kayu.business_car_owner.model.RefundInfo
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kayu.business_car_owner.model.RefundInfo.RefundWayResultsDTO
import com.kayu.business_car_owner.ui.adapter.RefundModeAdapter
import com.kayu.business_car_owner.ui.adapter.RefundReasonAdapter
import com.kayu.business_car_owner.R
import com.kayu.utils.*

class WashRefundFragment constructor() : BaseActivity() {
    private var orderId: Long? = null
    private var refund_price: TextView? = null
    private var mode_rv: RecyclerView? = null
    private var reason_rv: RecyclerView? = null
    private var apply_btn: TextView? = null
    private var mainViewModel: MainViewModel? = null

    //    public WashRefundFragment(Long orderId) {
    //        this.orderId = orderId;
    //    }
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wash_refund)
        orderId = getIntent().getLongExtra("orderId", 0)
        mainViewModel = ViewModelProvider(this@WashRefundFragment).get(
            MainViewModel::class.java
        )
        //标题栏
        findViewById<View>(R.id.title_back_btu).setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                onBackPressed()
            }

            override fun OnMoreErrorClick() {}
        })
        //        TextView back_tv = findViewById(R.id.title_back_tv);
        val title_name: TextView = findViewById(R.id.title_name_tv)
        title_name.setText("申请退款")
        //        title_name.setVisibility(View.GONE);
//        back_tv.setText("我的");
        refund_price = findViewById(R.id.wash_refund_price)
        mode_rv = findViewById(R.id.wash_refund_way_rv)
        reason_rv = findViewById(R.id.wash_refund_reason_rv)
        apply_btn = findViewById(R.id.wash_unused_apply_btn)
        val linearLayoutManager: LinearLayoutManager = LinearLayoutManager(this@WashRefundFragment)
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL)
        val linearLayoutManager1: LinearLayoutManager = LinearLayoutManager(this@WashRefundFragment)
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL)
        mode_rv?.setLayoutManager(linearLayoutManager)
        reason_rv?.setLayoutManager(linearLayoutManager1)
        val dividerItemDecoration: DividerItemDecoration =
            DividerItemDecoration(this@WashRefundFragment, DividerItemDecoration.VERTICAL)
        dividerItemDecoration.setDrawable(
            ColorDrawable(
                ContextCompat.getColor(
                    this@WashRefundFragment,
                    R.color.divider2
                )
            )
        )
        mode_rv?.addItemDecoration(dividerItemDecoration)
        reason_rv?.addItemDecoration(dividerItemDecoration)
        mainViewModel!!.getRefundInfo(this@WashRefundFragment, orderId)
            .observe(this@WashRefundFragment, object : Observer<RefundInfo?> {
                public override fun onChanged(refundInfo: RefundInfo?) {
                    if (null != refundInfo) {
                        initViewData(refundInfo)
                    }
                }
            })
    }

    //    @Override
    //    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    //                             Bundle savedInstanceState) {
    //        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
    //        return inflater.inflate(R.layout.fragment_wash_refund, container, false);
    //    }
    //    @Override
    //    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    //        super.onViewCreated(view, savedInstanceState);
    //
    //
    //    }
    private var selectedMode: RefundWayResultsDTO? = null
    private var selectedReason: String? = null
    private fun initViewData(refundInfo: RefundInfo) {
        refund_price!!.setText("￥" + refundInfo.amount)
        mode_rv!!.setAdapter(
            RefundModeAdapter(
                this@WashRefundFragment,
                refundInfo.refundWayResults,
                object : ItemCallback {
                    override fun onItemCallback(position: Int, obj: Any?) {
                        selectedMode = obj as RefundWayResultsDTO?
                    }

                    override fun onDetailCallBack(position: Int, obj: Any?) {}
                })
        )
        reason_rv!!.setAdapter(
            RefundReasonAdapter(
                this@WashRefundFragment,
                refundInfo.reasons,
                object : ItemCallback {
                    override fun onItemCallback(position: Int, obj: Any?) {
                        selectedReason = obj as String?
                    }

                    override fun onDetailCallBack(position: Int, obj: Any?) {}
                })
        )
        apply_btn!!.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                if (null == selectedMode || StringUtil.isEmpty(selectedReason)) {
                    TipGifDialog.show(this@WashRefundFragment, "请选择退款原因", TipGifDialog.TYPE.WARNING)
                    return
                }
                MessageDialog.show(
                    this@WashRefundFragment,
                    "确认申请退款？",
                    "若门店信息有变更，重新下单可能不再享受优惠",
                    "确定",
                    "取消"
                )
                    .setOkButton { baseDialog, v ->
                        TipGifDialog.show(
                            this@WashRefundFragment,
                            "稍等...",
                            TipGifDialog.TYPE.OTHER,
                            R.drawable.loading_gif
                        )
                        mainViewModel!!.sendRefund(
                            this@WashRefundFragment,
                            (orderId)!!,
                            selectedMode!!.way,
                            selectedReason!!,
                            object : ItemCallback {
                                override fun onItemCallback(position: Int, obj: Any?) {
                                    val resInfo: ResponseInfo = obj as ResponseInfo
                                    if (resInfo.status == 1) {
                                        TipGifDialog.show(
                                            this@WashRefundFragment,
                                            "申请成功，返回上一页",
                                            TipGifDialog.TYPE.SUCCESS
                                        )
                                        finish()
                                    } else {
                                        TipGifDialog.show(
                                            this@WashRefundFragment,
                                            resInfo.msg,
                                            TipGifDialog.TYPE.ERROR
                                        )
                                    }
                                }

                                override fun onDetailCallBack(position: Int, obj: Any?) {}
                            })
                        false
                    }.setCancelButton(object : OnDialogButtonClickListener {
                        override fun onClick(baseDialog: BaseDialog, v: View): Boolean {
                            baseDialog.doDismiss()
                            return false
                        }
                    }).setCancelable(false)
            }

            override fun OnMoreErrorClick() {}
        })
    }
}