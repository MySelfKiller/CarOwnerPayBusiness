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
import com.kayu.business_car_owner.model.OilStationBean
import com.kayu.utils.DoubleUtils
import androidx.recyclerview.widget.RecyclerView
import com.kayu.business_car_owner.R
import com.kayu.utils.AppUtil
import com.kayu.utils.ItemCallback
import java.util.ArrayList

class OilStationAdapter(
    private val context: Context,
    private var dataList: MutableList<OilStationBean>?,
    isShowEmptyPage: Boolean,
    isLoadingPage: Boolean,
    itemCallback: ItemCallback
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var isShowEmptyPage = false
    var isLoadingPage = false
    private val itemCallback: ItemCallback

    //    private String flag;
    fun addAllData(dataList: List<OilStationBean>?, isRemoveAllData: Boolean) {
        if (isRemoveAllData && null != this.dataList) {
            this.dataList!!.clear()
        }
        if (null == this.dataList) this.dataList = ArrayList()
        if (null != dataList) {
            this.dataList!!.addAll(dataList)
        }
        isLoadingPage = false
        notifyDataSetChanged()
    }

    fun removeAllData(isLoadingPage: Boolean) {
        if (null != dataList) {
            dataList!!.clear()
        }
        this.isLoadingPage = isLoadingPage
        isShowEmptyPage = true
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
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
                .inflate(R.layout.item_home_station, viewGroup, false)
            LoanHolder(view)
        }
    }

    override fun onBindViewHolder(loanHolder: RecyclerView.ViewHolder, @SuppressLint("RecyclerView") i: Int) {
        if (loanHolder is LoanHolder) {
            val vh = loanHolder
            val oilStationBean = dataList!![i]
            KWApplication.instance.loadImg(oilStationBean.gasLogoSmall, vh.img)
            vh.name.text = oilStationBean.gasName
            vh.location.text = oilStationBean.gasAddress
            vh.distance.setText(oilStationBean.distance.toString() + "km")
            vh.oil_price.text = "￥" + AppUtil.getStringDouble(oilStationBean.priceYfq)
            vh.oil_price_full.text = "￥" + AppUtil.getStringDouble(oilStationBean.priceGun)
            vh.oil_rebate.text = oilStationBean.gunDiscount + "折"
            vh.oil_rebate.visibility = View.VISIBLE
            vh.oil_price_sub.text =
                "降" + AppUtil.getStringDouble(DoubleUtils.sub(oilStationBean.priceGun, oilStationBean.priceYfq)) + "元"
            vh.mView.setOnClickListener(object : NoMoreClickListener() {
                override fun OnMoreClick(view: View) {
                    itemCallback.onItemCallback(i, oilStationBean)
                }

                override fun OnMoreErrorClick() {}
            })
            vh.navi.setOnClickListener(object : NoMoreClickListener() {
                override fun OnMoreClick(view: View) {
                    val userRole = KWApplication.instance.userRole
                    val isPublic = KWApplication.instance.isGasPublic
                    if (userRole == -2 && isPublic == 0) {
                        KWApplication.instance.showRegDialog(context)
                        return
                    }
                    KWApplication.instance.toNavi(
                        context,
                        oilStationBean.gasAddressLatitude.toString(),
                        oilStationBean.gasAddressLongitude.toString(),
                        oilStationBean.gasAddress,
                        "GCJ02"
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
        val oil_price_full: TextView
        val oil_rebate: TextView
        val img: ImageView

        init {
            img = itemView.findViewById(R.id.item_station_img)
            name = itemView.findViewById(R.id.item_station_name)
            location = itemView.findViewById(R.id.item_station_location)
            distance = itemView.findViewById(R.id.item_station_distance)
            oil_price_full = itemView.findViewById(R.id.item_station_oil_price_full)
            oil_price = itemView.findViewById(R.id.item_station_oil_price)
            oil_price_sub = itemView.findViewById(R.id.item_station_oil_price_sub)
            oil_rebate = itemView.findViewById(R.id.item_station_oil_rebate)
            navi = itemView.findViewById(R.id.item_station_navi)
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
        this.itemCallback = itemCallback
        //        this.flag = flag;
    }
}