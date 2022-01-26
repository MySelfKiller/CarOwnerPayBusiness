package com.kayu.business_car_owner.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.kayu.utils.NoMoreClickListener
import com.kayu.business_car_owner.KWApplication
import com.kayu.business_car_owner.model.SysOrderBean
import androidx.recyclerview.widget.RecyclerView
import com.kayu.business_car_owner.R
import com.kayu.utils.ItemCallback
import java.util.ArrayList

class OrderCategorySubAdapter(data: MutableList<SysOrderBean>?, itemCallback: ItemCallback?) :
    RecyclerView.Adapter<OrderCategorySubAdapter.MyViewHolder>() {
    var dataList: MutableList<SysOrderBean>? = ArrayList()
    private val itemCallback: ItemCallback?
    fun addAllData(dataList: List<SysOrderBean>?) {
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
        val view = inflater.inflate(R.layout.item_order_category_lay, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.tv_title.text = dataList!![position].title
        //        holder.tv_title_sub.setText(dataList.get(position).remark);
//        if (!StringUtil.isEmpty(dataList.get(position).tag)) {
//            holder.tv_tag.setText(dataList.get(position).tag);
//            holder.tv_tag.setVisibility(View.VISIBLE);
//        } else {
//
//        }
        holder.tv_tag.visibility = View.GONE
        holder.tv_title_sub.visibility = View.GONE
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