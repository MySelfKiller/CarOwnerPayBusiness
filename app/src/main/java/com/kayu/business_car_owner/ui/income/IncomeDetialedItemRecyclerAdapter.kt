package com.kayu.business_car_owner.ui.income

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kayu.business_car_owner.R

class IncomeDetialedItemRecyclerAdapter(
    private val dataList: MutableList<IncomeDetailedData>?,
    isShowEmptyPage: Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var isShowEmptyPage = false
    fun addAllData(dataList: List<IncomeDetailedData>?, isRemoveAllData: Boolean) {
        if (isRemoveAllData && null != this.dataList) {
            this.dataList.clear()
        }
        this.dataList!!.addAll(dataList!!)
        notifyDataSetChanged()
    }

    fun removeAllData() {
        if (null != dataList) {
            dataList.clear()
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
            .inflate(R.layout.item_detailed_lay, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            val vh = holder
            vh.income_name.text = dataList!![position].explain
            vh.income_time.text = dataList[position].createTime
            vh.income_amout.text = dataList[position].amount.toString() + "元"
        } else {
            if (!isShowEmptyPage) {
                holder.itemView.visibility = View.GONE
            } else {
                holder.itemView.visibility = View.VISIBLE
            }
        }
    }

    override fun getItemCount(): Int {
        return if (null == dataList || dataList.size == 0) {
            1
        } else dataList.size
    }

    override fun getItemViewType(position: Int): Int {
        //在这里进行判断，如果我们的集合的长度为0时，我们就使用emptyView的布局
        return if (null == dataList || dataList.size == 0) {
            VIEW_TYPE_EMPTY
        } else VIEW_TYPE_ITEM
        //如果有数据，则使用ITEM的布局
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val income_name: TextView
        val income_time: TextView
        val income_amout: TextView

        //        public final TextView mContentView;
        init {
            income_name = view.findViewById<View>(R.id.item_income_name) as TextView
            income_time = view.findViewById<View>(R.id.item_income_time) as TextView
            income_amout = view.findViewById<View>(R.id.item_income_amout) as TextView
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
        this.isShowEmptyPage = isShowEmptyPage
    }
}