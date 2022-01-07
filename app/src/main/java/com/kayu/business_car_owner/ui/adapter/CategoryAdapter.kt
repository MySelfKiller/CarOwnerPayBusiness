/*
 * Copyright 2017 GcsSloop
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Last modified 2017-09-18 23:47:01
 *
 * GitHub: https://github.com/GcsSloop
 * WeiBo: http://weibo.com/GcsSloop
 * WebSite: http://www.gcssloop.com
 */
package com.kayu.business_car_owner.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.kayu.business_car_owner.model.CategoryBean
import com.kayu.business_car_owner.KWApplication
import androidx.recyclerview.widget.RecyclerView
import com.kayu.business_car_owner.R
import com.kayu.utils.*

class CategoryAdapter(data: MutableList<CategoryBean>?, itemCallback: ItemCallback?) :
    RecyclerView.Adapter<CategoryAdapter.MyViewHolder>() {
    var dataList: MutableList<CategoryBean>? = ArrayList()
    private val itemCallback: ItemCallback?
    fun addAllData(dataList: List<CategoryBean>?) {
        this.dataList!!.addAll(dataList!!)
        notifyDataSetChanged()
    }

    fun removeAllData() {
        if (null != dataList) {
            dataList!!.clear()
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        //    Log.i("GCS", "onCreateViewHolder");
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_category_lay, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.tv_title.text = dataList!![position].title
        holder.tv_title_sub.text = dataList!![position].remark
        if (!StringUtil.isEmpty(dataList!![position].tag)) {
            holder.tv_tag.text = dataList!![position].tag
            holder.tv_tag.visibility = View.VISIBLE
        } else {
            holder.tv_tag.visibility = View.GONE
        }
        KWApplication.instance.loadImg(dataList!![position].icon, holder.tv_img)
        holder.itemView.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                itemCallback?.onItemCallback(position, dataList!![position])
            }

            override fun OnMoreErrorClick() {}
        })
    }

    override fun getItemCount(): Int {
        return dataList!!.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tv_title: TextView
        var tv_title_sub: TextView
        var tv_tag: TextView
        var tv_img: ImageView

        init {
            tv_title = itemView.findViewById(R.id.item_cate_text)
            tv_tag = itemView.findViewById(R.id.item_cate_tag)
            tv_title_sub = itemView.findViewById(R.id.item_cate_text_sub)
            tv_img = itemView.findViewById(R.id.item_cate_img)
        }
    }

    //    private Context mContext;
    init {
        dataList = data
        //        this.mContext = mContent;
        this.itemCallback = itemCallback
        //        this.flag = flag;
    }
}