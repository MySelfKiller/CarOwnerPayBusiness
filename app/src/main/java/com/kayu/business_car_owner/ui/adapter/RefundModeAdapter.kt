package com.kayu.business_car_owner.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.kayu.utils.NoMoreClickListener
import com.kayu.business_car_owner.model.RefundInfo.RefundWayResultsDTO
import androidx.recyclerview.widget.RecyclerView
import com.kayu.business_car_owner.R
import com.kayu.utils.ItemCallback

class RefundModeAdapter     //        this.flag = flag;
    (
    private val context: Context,
    private val dataList: MutableList<RefundWayResultsDTO>?,
    private val itemCallback: ItemCallback
) : RecyclerView.Adapter<RefundModeAdapter.loanHolder>() {
    fun addAllData(dataList: List<RefundWayResultsDTO>?, isRemoveAllData: Boolean) {
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
        notifyDataSetChanged()
    }

    private var selectedItem: ImageView? = null
    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        i: Int
    ): loanHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_refund_way, viewGroup, false)
        return loanHolder(view)
    }

    override fun onBindViewHolder(loanHolder: loanHolder, @SuppressLint("RecyclerView") i: Int) {
        val washStationBean = dataList!![i]
        loanHolder.mode_tv.text = washStationBean.content
        if (i == 0) {
            loanHolder.mode_check.isSelected = true
            itemCallback.onItemCallback(i, washStationBean)
            selectedItem = loanHolder.mode_check
        } else {
            loanHolder.mode_check.isSelected = false
        }
        loanHolder.mView.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                if (!loanHolder.mode_check.isSelected) {
                    loanHolder.mode_check.isSelected = true
                    if (null != selectedItem) selectedItem!!.isSelected = false
                    itemCallback.onItemCallback(i, washStationBean)
                    selectedItem = loanHolder.mode_check
                }
            }

            override fun OnMoreErrorClick() {}
        })
    }

    override fun getItemCount(): Int {
        return dataList?.size ?: 0
    }

    inner class loanHolder(val mView: View) : RecyclerView.ViewHolder(
        mView
    ) {
        val mode_tv: TextView
        val mode_check: ImageView

        init {
            mode_check = itemView.findViewById(R.id.item_refund_mode_check)
            mode_tv = itemView.findViewById(R.id.item_refund_mode_tv)
        }
    }
}