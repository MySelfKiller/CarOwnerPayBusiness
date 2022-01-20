package com.kayu.business_car_owner.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kayu.business_car_owner.KWApplication
import com.kayu.business_car_owner.R
import com.kayu.business_car_owner.model.PopNaviBean
import com.kayu.utils.ItemCallback

class ImgTitleAdapter(
    private val dataList: MutableList<PopNaviBean>?,
    private val callback: ItemCallback,
) : RecyclerView.Adapter<ImgTitleAdapter.ImgTitleHolder>() {
    @SuppressLint("NotifyDataSetChanged")
    fun addAllData(dataList: MutableList<PopNaviBean>?) {
        this.dataList?.addAll(dataList!!)
        notifyDataSetChanged()
    }

    @SuppressLint("InflateParams")
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ImgTitleHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_img_title_lay, null)
        return ImgTitleHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ImgTitleHolder, i: Int) {
        KWApplication.instance.loadImg(dataList!![i].logo, viewHolder.nameText)
        viewHolder.nameText.setOnClickListener {
            callback.onItemCallback(i, dataList[i])
        }
    }

    override fun getItemCount(): Int {
        return dataList?.size ?: 0
    }

    inner class ImgTitleHolder(private val mView: View) : RecyclerView.ViewHolder(
        mView
    ) {
        var nameText: ImageView

        init {
            nameText = itemView.findViewById(R.id.item_type_tv)
        }
    }
}