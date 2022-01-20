package com.kayu.business_car_owner.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kayu.business_car_owner.R
import com.kayu.business_car_owner.model.ProductSortBean
import com.kayu.utils.ItemCallback

class SortTitleAdapter(
    private val dataList: MutableList<ProductSortBean>?,
    private val callback: ItemCallback,
) : RecyclerView.Adapter<SortTitleAdapter.SortTitleHolder>() {
    private var context: Context? = null
    @SuppressLint("NotifyDataSetChanged")
    fun addAllData(dataList: MutableList<ProductSortBean>?) {
        this.dataList?.addAll(dataList!!)
        notifyDataSetChanged()
    }

    @SuppressLint("InflateParams")
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): SortTitleHolder {
        context = viewGroup.context
        val view = LayoutInflater.from(context).inflate(R.layout.item_sort_title_lay, null)
        return SortTitleHolder(view)
    }

    var selectedView: SortTitleHolder? = null
    override fun onBindViewHolder(viewHolder: SortTitleHolder, i: Int) {
        if (i == 0) {
            viewHolder.nameText.isSelected = true
            viewHolder.nameText.setTextColor(context!!.resources.getColor(R.color.white))
            viewHolder.nameText.typeface = Typeface.DEFAULT_BOLD
            selectedView = viewHolder
//            callback.onItemCallback(i, "")
        }
        viewHolder.nameText.text = dataList!![i].name
        viewHolder.nameText.setOnClickListener {

            if (!viewHolder.nameText.isSelected) {
                if (null != selectedView) {
                    selectedView!!.nameText.isSelected = false
                    selectedView!!.nameText.setTextColor(context!!.resources.getColor(R.color.black1))
                    selectedView!!.nameText.typeface = Typeface.DEFAULT_BOLD
                }
                viewHolder.nameText.setTextColor(context!!.resources.getColor(R.color.white))
                viewHolder.nameText.typeface = Typeface.DEFAULT_BOLD
                viewHolder.nameText.isSelected = true
                selectedView = viewHolder
                callback.onItemCallback(i, dataList!![i])
            }
        }
    }

    override fun getItemCount(): Int {
        return dataList?.size ?: 0
    }

    inner class SortTitleHolder(private val mView: View) : RecyclerView.ViewHolder(
        mView
    ) {
        var nameText: TextView

        init {
            nameText = itemView.findViewById(R.id.item_type_tv)
        }
    }
}