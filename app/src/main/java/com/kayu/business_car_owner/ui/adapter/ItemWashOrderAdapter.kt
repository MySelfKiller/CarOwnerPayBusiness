package com.kayu.business_car_owner.ui.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.kayu.utils.NoMoreClickListener
import com.kayu.business_car_owner.KWApplication
import com.kayu.business_car_owner.model.ItemWashOrderBean
import com.kayu.business_car_owner.activity.WashStationActivity
import com.kayu.business_car_owner.activity.WashUnusedActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.kayu.business_car_owner.R
import com.kayu.utils.AppUtil
import com.kayu.utils.ItemCallback

class ItemWashOrderAdapter(
    private val context: FragmentActivity,
    private val dataList: MutableList<ItemWashOrderBean>?,
    var isShowEmptyPage: Boolean,
    private val itemCallback: ItemCallback
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    fun addAllData(dataList: List<ItemWashOrderBean>?, isRemoveAllData: Boolean) {
        if (isRemoveAllData && null != this.dataList) {
            this.dataList.clear()
        }
        this.dataList!!.addAll(dataList!!)
        notifyDataSetChanged()
    }

    fun removeAllData() {
        if (null != dataList) {
            dataList.clear()
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
            .inflate(R.layout.item_wash_order_list, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        if (holder is ViewHolder) {
            val vh = holder
            val oilOrderData = dataList!![position]
            KWApplication.instance.loadImg(oilOrderData.doorPhotoUrl, vh.img_bg)
            vh.order_name.text = oilOrderData.shopName
            val orderState: String
            orderState = when (oilOrderData.state) {
                0 -> "待支付"
                1 -> "待使用"
                2 -> "已取消"
                3 -> "已使用"
                4 -> "退款中"
                5 -> "已退款"
                6 -> "支付失败"
                7 -> "退款失败"
                else -> "暂无"
            }
            vh.order_state.text = orderState
            vh.order_price.text = AppUtil.getStringDouble(oilOrderData.realAmount)
            vh.wash_type.text = oilOrderData.serviceName.split("-".toRegex()).toTypedArray()[0]
            if (oilOrderData.state == 4 || oilOrderData.state == 6 || oilOrderData.state == 7) {
                vh.open_time.visibility = View.GONE
            } else {
                vh.open_time.text = "营业时间：" + oilOrderData.busTime
                vh.open_time.visibility = View.VISIBLE
            }
            if (null != oilOrderData.surplusDay) {
                val str7 = "请在<font color=\"#ca4747\">" + oilOrderData.surplusDay + "</font>天内使用"
                vh.vali_time.text = Html.fromHtml(str7)
                vh.vali_time.visibility = View.VISIBLE
            } else {
                vh.vali_time.visibility = View.GONE
            }
            //            vh.vali_time.setText("请在"+oilOrderData.surplusDay+"天内使用");
            vh.store_address.text = oilOrderData.address
            if (oilOrderData.state == 4 || oilOrderData.state == 6 || oilOrderData.state == 7) {
                vh.pay_lay.visibility = View.GONE
                vh.location_lay.visibility = View.GONE
            } else if (oilOrderData.state == 1) {
                vh.location_lay.visibility = View.VISIBLE
                vh.pay_lay.visibility = View.GONE
                vh.navi_lay.setOnClickListener(object : NoMoreClickListener() {
                    override fun OnMoreClick(view: View) {
                        itemCallback.onItemCallback(position, oilOrderData)
                    }

                    override fun OnMoreErrorClick() {}
                })
                vh.phone_lay.setOnClickListener(object : NoMoreClickListener() {
                    override fun OnMoreClick(view: View) {
                        itemCallback.onDetailCallBack(position, oilOrderData)
                    }

                    override fun OnMoreErrorClick() {}
                })
            } else {
                vh.location_lay.visibility = View.GONE
                vh.pay_lay.visibility = View.VISIBLE
                vh.pay_btn.text = "再次购买"
                vh.pay_btn.setOnClickListener(object : NoMoreClickListener() {
                    override fun OnMoreClick(view: View) {
//                        FragmentManager fg = context.getSupportFragmentManager();
//                        FragmentTransaction fragmentTransaction = fg.beginTransaction();
//                        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//                        fragmentTransaction.add(R.id.main_root_lay, new WashStationActivity(oilOrderData.shopCode));
//                        fragmentTransaction.addToBackStack("ddd");
//                        fragmentTransaction.commit();
                        val intent = Intent(context, WashStationActivity::class.java)
                        intent.putExtra("shopCode", oilOrderData.shopCode)
                        context.startActivity(intent)
                    }

                    override fun OnMoreErrorClick() {}
                })
            }
            vh.mView.setOnClickListener(object : NoMoreClickListener() {
                override fun OnMoreClick(view: View) {
                    // 2020/10/21 是否还需要细分订单状态再跳转不同页面
//                    Fragment jumpFragment;
//                    if (oilOrderData.state == 1) {
//                    } else {
//                    }
//                    if (oilOrderData.state == 0 || oilOrderData.state == 2 || oilOrderData.state == 6) {
//                        jumpFragment = new WashStationFragment(oilOrderData.shopCode);
//                    } else {
//                        jumpFragment = new WashUnusedActivity(oilOrderData.id, oilOrderData.state);
//                    }
//                    FragmentManager fg = context.getSupportFragmentManager();
//                    FragmentTransaction fragmentTransaction = fg.beginTransaction();
//                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//                    fragmentTransaction.add(R.id.main_root_lay,jumpFragment );
//                    fragmentTransaction.addToBackStack("ddd");
//                    fragmentTransaction.commit();
                    val intent = Intent(context, WashUnusedActivity::class.java)
                    intent.putExtra("orderId", oilOrderData.id)
                    intent.putExtra("orderState", oilOrderData.state)
                    context.startActivity(intent)
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
        return if (null == dataList || dataList.size == 0) {
            1
        } else dataList.size
    }

    override fun getItemViewType(position: Int): Int {
        //在这里进行判断，如果我们的集合的长度为0时，我们就使用emptyView的布局
        return if (null == dataList || dataList.size == 0) {
            VIEW_TYPE_EMPTY
        } else VIEW_TYPE_ITEM
        //如果有数据，则使用ITEM的布局
    }

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(
        mView
    ) {
        val order_name: TextView
        val wash_type: TextView
        val order_state: TextView
        val open_time: TextView
        val vali_time: TextView
        val store_address: TextView
        val order_price: TextView
        val pay_btn: TextView
        val img_bg: ImageView
        val navi_lay: LinearLayout
        val phone_lay: LinearLayout
        val location_lay: ConstraintLayout
        val pay_lay: ConstraintLayout

        init {
            img_bg = itemView.findViewById(R.id.item_wash_order_img_bg)
            order_name = itemView.findViewById(R.id.item_wash_order_name)
            order_state = itemView.findViewById(R.id.item_wash_order_state)
            order_price = itemView.findViewById(R.id.item_wash_order_price)
            wash_type = itemView.findViewById(R.id.item_wash_order_type)
            open_time = itemView.findViewById(R.id.item_wash_order_time)
            vali_time = itemView.findViewById(R.id.item_wash_order_vali_time)
            store_address = itemView.findViewById(R.id.item_wash_order_location)
            location_lay = itemView.findViewById(R.id.item_wash_order_location_lay)
            pay_lay = itemView.findViewById(R.id.item_wash_order_pay_lay)
            navi_lay = itemView.findViewById(R.id.item_wash_order_navi_lay)
            phone_lay = itemView.findViewById(R.id.item_wash_order_phone_lay)
            pay_btn = itemView.findViewById(R.id.item_wash_order_pay_btn)
        }
    }

    companion object {
        /**
         * viewType--分别为item以及空view
         */
        const val VIEW_TYPE_ITEM = 1
        const val VIEW_TYPE_EMPTY = 0
    }
}