package com.kayu.business_car_owner.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.kayu.business_car_owner.model.ItemOilOrderBean
import androidx.recyclerview.widget.RecyclerView
import com.kayu.business_car_owner.R
import com.kayu.utils.*
import java.util.ArrayList

class ItemOrderViewAdapter(
    context: Context?,
    dataList: MutableList<ItemOilOrderBean>?,
    isShowEmptyPage: Boolean,
    itemCallback: ItemCallback
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var isShowEmptyPage = false
    private var dataList: MutableList<ItemOilOrderBean>? = ArrayList()
    private val itemCallback: ItemCallback
    private val context: Context?
    fun addAllData(dataList: List<ItemOilOrderBean>?, isRemoveAllData: Boolean) {
        if (isRemoveAllData && null != this.dataList) {
            this.dataList!!.clear()
        }
        this.dataList!!.addAll(dataList!!)
        notifyDataSetChanged()
    }

    fun removeAllData() {
        if (null != dataList) {
            dataList!!.clear()
        }
        isShowEmptyPage = false
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        //在这里根据不同的viewType进行引入不同的布局
        if (viewType == VIEW_TYPE_EMPTY) {
            val emptyView =
                LayoutInflater.from(parent.context).inflate(R.layout.empty_view_tab, parent, false)
            return object : RecyclerView.ViewHolder(emptyView) {}
        }
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_oil_order_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        if (holder is ViewHolder) {
            val vh = holder
            val oilOrderData = dataList!![position]
            //92#(2号枪)
            vh.order_name.text = oilOrderData.gasName
            vh.order_number.text = oilOrderData.orderNo
            var orderStatusName = "未知"
            when (oilOrderData.state) {
                0 -> orderStatusName = "未支付"
                1 -> orderStatusName = "已支付"
                2 -> orderStatusName = "已取消"
                3 -> orderStatusName = "已退款"
                4 -> orderStatusName = "待退款"
                5 -> orderStatusName = "退款失败"
            }
            vh.oil_state.text = orderStatusName
            vh.pay_model.text = oilOrderData.payType
            vh.pay_time.text = oilOrderData.createTime
            vh.oil_info.text = oilOrderData.oilNo + "(" + oilOrderData.gunNo + "号枪)"
            vh.full_price.text = oilOrderData.totalAmt.toString() //订单总金额
            vh.rebate_price.text =
                (oilOrderData.totalAmt!! - oilOrderData.payAmt!!).toString() //优惠金额
            vh.sale_price.text = oilOrderData.payAmt.toString() //实际支付金额
            if (!StringUtil.isEmpty(oilOrderData.qrCode)) {
                vh.qr_btn.visibility = View.VISIBLE
                vh.qr_btn.setOnClickListener(object : NoMoreClickListener() {
                    override fun OnMoreClick(view: View) {
                        itemCallback.onItemCallback(position, oilOrderData.qrCode)
                    }

                    override fun OnMoreErrorClick() {}
                })
            } else {
                vh.qr_btn.visibility = View.GONE
            }
        } else {
            if (!isShowEmptyPage) {
                holder.itemView.visibility = View.GONE
            } else {
                holder.itemView.visibility = View.VISIBLE
            }
        }
    }

    override fun getItemCount(): Int {
        return if (null == dataList || dataList?.size == 0) {
            1
        } else dataList!!.size
    }

    override fun getItemViewType(position: Int): Int {
        //在这里进行判断，如果我们的集合的长度为0时，我们就使用emptyView的布局
        return if (null == dataList || dataList?.size == 0) {
            VIEW_TYPE_EMPTY
        } else VIEW_TYPE_ITEM
        //如果有数据，则使用ITEM的布局
    }

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(
        mView
    ) {
        val order_name: TextView
        val order_number: TextView
        val oil_state: TextView
        val pay_model: TextView
        val pay_time: TextView
        val oil_info: TextView
        val full_price: TextView
        val rebate_price: TextView
        val sale_price: TextView
        val qr_btn: TextView

        init {
            qr_btn = itemView.findViewById(R.id.item_wash_qr_btn)
            order_name = itemView.findViewById(R.id.item_order_oil_name)
            order_number = itemView.findViewById(R.id.item_order_oil_number)
            oil_state = itemView.findViewById(R.id.item_order_oil_state)
            pay_model = itemView.findViewById(R.id.item_order_oil_pay_model)
            pay_time = itemView.findViewById(R.id.item_order_oil_pay_time)
            oil_info = itemView.findViewById(R.id.item_order_oil_info)
            full_price = itemView.findViewById(R.id.item_order_oil_full_price)
            rebate_price = itemView.findViewById(R.id.item_order_oil_rebate_price)
            sale_price = itemView.findViewById(R.id.item_order_oil_sale_price)
        }
    }

    companion object {
        /**
         * viewType--分别为item以及空view
         */
        const val VIEW_TYPE_ITEM = 1
        const val VIEW_TYPE_EMPTY = 0
    }

    init {
        this.dataList = dataList
        this.isShowEmptyPage = isShowEmptyPage
        this.itemCallback = itemCallback
        this.context = context
    }
}