package com.kayu.business_car_owner.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.kayu.business_car_owner.model.ItemMessageBean
import androidx.recyclerview.widget.RecyclerView
import com.kayu.business_car_owner.R
import com.kayu.utils.*

class ItemMessageAdapter(
    context: Context,
    dataList: MutableList<ItemMessageBean>?,
    isShowEmptyPage: Boolean,
    itemCallback: ItemCallback?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var isShowEmptyPage = false
    private var dataList: MutableList<ItemMessageBean>? = ArrayList()
    private val itemCallback: ItemCallback?
    private val context: Context
    fun addAllData(dataList: List<ItemMessageBean>?, isRemoveAllData: Boolean) {
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
            .inflate(R.layout.item_message, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        if (holder is ViewHolder) {
            val vh = holder
            val messageBean = dataList!![position]
            //92#(2号枪)
            vh.time_tv.text = messageBean.createTime
            vh.title_tv.text = messageBean.title
            vh.content_tv.text = messageBean.content
            vh.mView.setOnClickListener(object : NoMoreClickListener() {
                override fun OnMoreClick(view: View) {
                    if (!StringUtil.isEmpty(messageBean.url!!.trim { it <= ' ' })) {
                        itemCallback?.onItemCallback(position, messageBean.url)
                    }
                }

                override fun OnMoreErrorClick() {}
            })
        } else {
            if (!isShowEmptyPage) {
                holder.itemView.visibility = View.GONE
            } else {
                holder.itemView.visibility = View.VISIBLE
            }
        }
    }

    override fun getItemCount(): Int {
        return if (null == dataList || dataList!!.size == 0) {
            1
        } else dataList!!.size
    }

    override fun getItemViewType(position: Int): Int {
        //在这里进行判断，如果我们的集合的长度为0时，我们就使用emptyView的布局
        return if (null == dataList || dataList!!.size == 0) {
            VIEW_TYPE_EMPTY
        } else VIEW_TYPE_ITEM
        //如果有数据，则使用ITEM的布局
    }

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(
        mView
    ) {
        val time_tv: TextView
        val title_tv: TextView
        val content_tv: TextView

        init {
            time_tv = itemView.findViewById(R.id.item_message_time_tv)
            title_tv = itemView.findViewById(R.id.item_message_title_tv)
            content_tv = itemView.findViewById(R.id.item_message_content_tv)
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