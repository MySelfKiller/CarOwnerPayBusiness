package com.kayu.business_car_owner.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kayu.business_car_owner.model.SysOrderBean
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kayu.business_car_owner.R
import com.kayu.utils.ItemCallback

class OrderCategoryAdapter     //        this.mContext = mContent;
//        this.flag = flag;
    (
    var dataList: MutableList<MutableList<SysOrderBean>>?,
    private val itemCallback: ItemCallback?
) : RecyclerView.Adapter<OrderCategoryAdapter.MyViewHolder>() {
    private var mContext: Context? = null
    fun addAllData(dataList: List<MutableList<SysOrderBean>>?) {
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
        mContext = parent.context
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_category2_root_lay, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
//        holder.tv_title.setText(dataList.get(position).title);
//        holder.tv_title_sub.setText(dataList.get(position).remark);
//        if (!StringUtil.isEmpty(dataList.get(position).tag.trim())) {
//            holder.tv_tag.setText(dataList.get(position).tag);
//            holder.tv_tag.setVisibility(View.VISIBLE);
//        } else {
//            holder.tv_tag.setVisibility(View.GONE);
//        }
//
//        KWApplication.getInstance().loadImg(dataList.get(position).icon,holder.tv_img);
        holder.root_rv.layoutManager = GridLayoutManager(mContext, dataList!![position].size)
        val categoryAdapter = OrderCategorySubAdapter(dataList!![position], object : ItemCallback {
            override fun onItemCallback(position: Int, obj: Any?) {
                itemCallback?.onItemCallback(position, obj)
            }

            override fun onDetailCallBack(position: Int, obj: Any?) {}
        })
        holder.root_rv.adapter = categoryAdapter
    }

    override fun getItemCount(): Int {
        return dataList!!.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var root_rv: RecyclerView

        init {
            root_rv = itemView.findViewById(R.id.item_cate_root_rv)
        }
    }
}