package com.kayu.business_car_owner.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.kayu.utils.NoMoreClickListener
import com.kayu.business_car_owner.KWApplication
import com.kayu.business_car_owner.model.WashStationBean
import androidx.recyclerview.widget.RecyclerView
import com.kayu.business_car_owner.R
import com.kayu.utils.ItemCallback
import java.text.DecimalFormat
import java.util.ArrayList

class WashStationAdapter(
    private val context: Context?,
    data: MutableList<WashStationBean>?,
    isShowEmptyPage: Boolean,
    isLoadingPage: Boolean,
    itemCallback: ItemCallback
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var isShowEmptyPage = false
    var isLoadingPage = false
    private var dataList: MutableList<WashStationBean>?
    private val itemCallback: ItemCallback
    private var selectedVal: String? = null
    @SuppressLint("NotifyDataSetChanged")
    fun addAllData(
        dataList: List<WashStationBean>?,
        selectedVal: String?,
        isRemoveAllData: Boolean
    ) {
        if (isRemoveAllData && null != this.dataList) {
            this.dataList!!.clear()
        }
        this.selectedVal = selectedVal
        if (null == this.dataList) this.dataList = ArrayList()
        if (null != dataList) {
            this.dataList!!.addAll(dataList)
        }
        isLoadingPage = false
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun removeAllData(isLoadingPage: Boolean) {
        if (null != dataList) {
            dataList!!.clear()
        }
        this.isLoadingPage = isLoadingPage
        isShowEmptyPage = true
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        //在这里根据不同的viewType进行引入不同的布局
        return if (viewType == VIEW_TYPE_EMPTY) {
            val emptyView = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.empty_view_tab, viewGroup, false)
            object : RecyclerView.ViewHolder(emptyView) {}
        } else if (viewType == VIEW_TYPE_LOADING) {
            val emptyView = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.empty_view_tab_home, viewGroup, false)
            object : RecyclerView.ViewHolder(emptyView) {}
        } else {
            val view = LayoutInflater.from(context)
                .inflate(R.layout.item_home_wash, viewGroup, false)
            LoanHolder(view)
        }
    }

    override fun onBindViewHolder(loanHolder: RecyclerView.ViewHolder, @SuppressLint("RecyclerView") i: Int) {
        if (loanHolder is LoanHolder) {
            val vh = loanHolder
            val washStationBean = dataList!![i]
            KWApplication.instance.loadImg(washStationBean.doorPhotoUrl, vh.img)
            vh.name.text = washStationBean.shopName
            vh.location.text = washStationBean.address
            val df = DecimalFormat("0.0") //格式化小数
            val num = df.format(washStationBean.distance.toFloat() / 1000.0)
            vh.distance.text = num + "km"
            for (item in washStationBean.serviceList!!) {
                if (item.serviceCode == selectedVal) {
                    vh.oil_price.text = "￥" + item.finalPrice
                    vh.oil_price_sub.text = "￥" + item.price
                }
            }
            val sb = StringBuffer()
            if (washStationBean.isOpen == "1") {
                sb.append("营业中 | ")
            } else {
                sb.append("休息中 | ")
            }
            sb.append(washStationBean.openTimeStart).append("-").append(washStationBean.openTimeEnd)
            vh.time.text = sb.toString()
            vh.mView.setOnClickListener(object : NoMoreClickListener() {
                override fun OnMoreClick(view: View) {
                    itemCallback.onItemCallback(i, washStationBean)
                }

                override fun OnMoreErrorClick() {}
            })
            vh.navi.setOnClickListener(object : NoMoreClickListener() {
                override fun OnMoreClick(view: View) {
//                    val userRole = KWApplication.instance.userRole
//                    val isPublic = KWApplication.instance.isWashPublic
//                    if (userRole == -2 && isPublic == 0) {
//                        KWApplication.instance.showRegDialog(context!!)
//                        return
//                    }
                    KWApplication.instance.toNavi(
                        context!!,
                        washStationBean.latitude,
                        washStationBean.longitude,
                        washStationBean.address,
                        "BD09"
                    )
                }

                override fun OnMoreErrorClick() {}
            })
        } else {
            if (!isShowEmptyPage) {
                loanHolder.itemView.visibility = View.GONE
            } else {
                loanHolder.itemView.visibility = View.VISIBLE
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
            if (isLoadingPage) VIEW_TYPE_LOADING else VIEW_TYPE_EMPTY
        } else VIEW_TYPE_ITEM
        //如果有数据，则使用ITEM的布局
    }

    internal inner class LoanHolder(val mView: View) : RecyclerView.ViewHolder(
        mView
    ) {
        val name: TextView
        val location: TextView
        val distance: TextView
        val oil_price: TextView
        val oil_price_sub: TextView
        val navi: TextView
        val time: TextView
        val img: ImageView

        init {
            img = itemView.findViewById(R.id.item_wash_img)
            name = itemView.findViewById(R.id.item_wash_name)
            location = itemView.findViewById(R.id.item_wash_location)
            distance = itemView.findViewById(R.id.item_wash_distance)
            time = itemView.findViewById(R.id.item_wash_time)
            oil_price = itemView.findViewById(R.id.item_wash_oil_price)
            oil_price_sub = itemView.findViewById(R.id.item_wash_oil_price_sub)
            navi = itemView.findViewById(R.id.item_wash_navi)
            //            pay_oil = itemView.findViewById(R.id.item_station_pay_oil);
        }
    }

    companion object {
        /**
         * viewType--分别为item以及空view
         */
        const val VIEW_TYPE_ITEM = 1
        const val VIEW_TYPE_EMPTY = 0
        const val VIEW_TYPE_LOADING = -1
    }

    init {
        this.isShowEmptyPage = isShowEmptyPage
        this.isLoadingPage = isLoadingPage
        dataList = data
        this.itemCallback = itemCallback
        //        this.flag = flag;
    }
}