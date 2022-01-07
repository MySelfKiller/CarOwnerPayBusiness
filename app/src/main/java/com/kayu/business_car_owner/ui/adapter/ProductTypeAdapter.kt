package com.kayu.business_car_owner.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.kayu.business_car_owner.model.OilsParam
import com.kayu.business_car_owner.model.OilsTypeParam
import androidx.recyclerview.widget.RecyclerView
import com.kayu.business_car_owner.R
import com.kayu.utils.ItemCallback

class ProductTypeAdapter(
    private val context: Context,
    private val dataList: MutableList<Any>?,
    private val flag: Int,
    private val callback: ItemCallback
) : RecyclerView.Adapter<ProductTypeAdapter.NoticeHolder>() {
    fun addAllData(dataList: List<Any>?, isClean: Boolean) {
        if (isClean) {
            this.dataList!!.clear()
            if (null != selectedView) {
                selectedView!!.nameText.isSelected = false
                selectedView!!.nameText.setTextColor(context.resources.getColor(R.color.black1))
                selectedView!!.nameText.typeface = Typeface.DEFAULT
                selectedView = null
            }
        }
        this.dataList!!.addAll(dataList!!)
        notifyDataSetChanged()
    }

    @SuppressLint("InflateParams")
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): NoticeHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_term_lay, null)
        return NoticeHolder(view)
    }

    var selectedView: NoticeHolder? = null
    override fun onBindViewHolder(viewHolder: NoticeHolder, i: Int) {
//        Drawable drawable1 = context.getResources().getDrawable(R.mipmap.ic_arror_right);
//        drawable1.setBounds(0, 0, 25, 25);//第一0是距左边距离，第二0是距上边距离，40分别是长宽
//        viewHolder.button.setCompoundDrawables(null, null, drawable1, null);//只放左边
        var showValue = ""
        if (flag == 0) {
            val param = dataList!![i] as OilsTypeParam
            if (param.oilType == 1) {
                showValue = "汽油"
                viewHolder.nameText.text = "汽油"
            } else if (param.oilType == 2) {
                showValue = "柴油"
                viewHolder.nameText.text = "柴油"
            } else if (param.oilType == 3) {
                showValue = "天然气"
            }
        } else if (flag == 1) {
            val param = dataList!![i] as OilsParam
            showValue = param.oilName
        } else if (flag == 2) {
            showValue = dataList!![i] as String + "号"
        }
        if (i == 0 && flag != 2) {
            viewHolder.nameText.isSelected = true
            viewHolder.nameText.setTextColor(context.resources.getColor(R.color.endColor_btn))
            viewHolder.nameText.typeface = Typeface.DEFAULT_BOLD
            selectedView = viewHolder
        }
        viewHolder.nameText.text = showValue
        viewHolder.nameText.setOnClickListener {
            if (!viewHolder.nameText.isSelected) {
                if (null != selectedView) {
                    selectedView!!.nameText.isSelected = false
                    selectedView!!.nameText.setTextColor(context.resources.getColor(R.color.black1))
                    selectedView!!.nameText.typeface = Typeface.DEFAULT
                }
                viewHolder.nameText.setTextColor(context.resources.getColor(R.color.endColor_btn))
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

    inner class NoticeHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var nameText: TextView

        init {
            nameText = itemView.findViewById(R.id.item_type_tv)
        }
    }
}