package com.kayu.business_car_owner.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.kayu.business_car_owner.model.DistancesParam
import com.kayu.business_car_owner.model.OilsParam
import com.kayu.business_car_owner.model.SortsParam
import com.kayu.business_car_owner.model.WashParam
import androidx.recyclerview.widget.RecyclerView
import com.kayu.business_car_owner.R
import com.kayu.utils.ItemCallback

class ParamAdapter(
    private val parentIndex: Int,
    private val context: Context?,
    private val dataList: ArrayList<Any>?,
    private val callback: ItemCallback,
    private val flag: Int,
    private val parentCallback: ItemCallback
) : RecyclerView.Adapter<ParamAdapter.NoticeHolder>() {
    fun addAllData(dataList: ArrayList<Any>?) {
        this.dataList?.addAll(dataList!!)
        notifyDataSetChanged()
    }

    @SuppressLint("InflateParams")
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): NoticeHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_term_lay, null)
        return NoticeHolder(view)
    }

    var selectedView: NoticeHolder? = null
    override fun onBindViewHolder(viewHolder: NoticeHolder, i: Int) {
        if (flag == 1) {
            val sortsParam = dataList!![i] as DistancesParam
            if (sortsParam.isDefault == 1) {
                viewHolder.nameText.isSelected = true
                viewHolder.nameText.setTextColor(context!!.resources.getColor(R.color.endColor_btn))
                viewHolder.nameText.typeface = Typeface.DEFAULT_BOLD
                selectedView = viewHolder
                callback.onItemCallback(i, null)
            }
            viewHolder.nameText.text = sortsParam.name
        } else if (flag == 2) {
            val sortsParam = dataList!![i] as OilsParam
            if (sortsParam.isDefault == 1) {
                viewHolder.nameText.isSelected = true
                viewHolder.nameText.setTextColor(context!!.resources.getColor(R.color.endColor_btn))
                viewHolder.nameText.typeface = Typeface.DEFAULT_BOLD
                selectedView = viewHolder
                callback.onItemCallback(i, null)
            }
            viewHolder.nameText.text = sortsParam.oilName
        } else if (flag == 3) {
            val sortsParam = dataList!![i] as SortsParam
            if (sortsParam.isDefault == 1) {
                viewHolder.nameText.isSelected = true
                viewHolder.nameText.setTextColor(context!!.resources.getColor(R.color.endColor_btn))
                viewHolder.nameText.typeface = Typeface.DEFAULT_BOLD
                selectedView = viewHolder
                callback.onItemCallback(i, null)
            }
            viewHolder.nameText.text = sortsParam.name
        } else if (flag == 4 || flag == 5) {
            val sortsParam = dataList!![i] as WashParam
            if (sortsParam.isDefault == 1) {
                viewHolder.nameText.isSelected = true
                viewHolder.nameText.setTextColor(context!!.resources.getColor(R.color.endColor_btn))
                viewHolder.nameText.typeface = Typeface.DEFAULT_BOLD
                selectedView = viewHolder
                callback.onItemCallback(i, null)
            }
            viewHolder.nameText.text = sortsParam.name
        }
        viewHolder.nameText.setOnClickListener {
            if (!viewHolder.nameText.isSelected) {
                if (null != selectedView) {
                    selectedView!!.nameText.isSelected = false
                    selectedView!!.nameText.setTextColor(context!!.resources.getColor(R.color.black1))
                    selectedView!!.nameText.typeface = Typeface.DEFAULT
                } else {
                    parentCallback.onItemCallback(parentIndex, null)
                }
                viewHolder.nameText.setTextColor(context!!.resources.getColor(R.color.endColor_btn))
                viewHolder.nameText.typeface = Typeface.DEFAULT_BOLD
                viewHolder.nameText.isSelected = true
                selectedView = viewHolder
                //                    if (flag == 1) {
//                        DistancesParam sortsParam = (DistancesParam) dataList.get(i);
//                    } else if (flag == 2) {
//                        OilsParam sortsParam = (OilsParam) dataList.get(i);
//                    } else if (flag ==3) {
//                        SortsParam sortsParam = (SortsParam) dataList.get(i);
//                        sortsParam.isDefault
//                    }
                callback.onItemCallback(i, dataList!![i])
            }
        }
    }

    override fun getItemCount(): Int {
        return dataList?.size ?: 0
    }

    inner class NoticeHolder(private val mView: View) : RecyclerView.ViewHolder(
        mView
    ) {
        var nameText: TextView

        init {
            nameText = itemView.findViewById(R.id.item_type_tv)
        }
    }
}