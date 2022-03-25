package com.kayu.business_car_owner.ui.adapter

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.kayu.business_car_owner.model.ParamParent
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.kayu.business_car_owner.R
import com.kayu.utils.ItemCallback

class ParamParentAdapter(
    private val context: Context,
    private val dataList: ArrayList<ParamParent>,
    private val itemCallback: ItemCallback,
    private val flag: Int
) : RecyclerView.Adapter<ParamParentAdapter.loanHolder>() {
    private val childAdapterList: ArrayList<ParamAdapter> = ArrayList()
    private var parentCallback: ItemCallback = object : ItemCallback {
        override fun onItemCallback(position: Int, obj: Any?) {
            for (x in childAdapterList.indices) {
                if (x != position) {
                    if (null != childAdapterList[x].selectedView) {
                        childAdapterList[x].selectedView!!.nameText.isSelected = false
                        childAdapterList[x].selectedView!!.nameText.setTextColor(
                            context.resources.getColor(
                                R.color.black1
                            )
                        )
                        childAdapterList[x].selectedView!!.nameText.typeface = Typeface.DEFAULT
                        childAdapterList[x].selectedView = null
                    }
                }
            }
        }

        override fun onDetailCallBack(position: Int, obj: Any?) {}
    }

    //    private ParamAdapter paramAdapter;
    //    private String flag;
    fun addAllData(dataList: ArrayList<ParamParent>?, isRemoveAllData: Boolean) {
        if (isRemoveAllData) {
            this.dataList.clear()
        }
        this.dataList.addAll(dataList!!)
        notifyDataSetChanged()
    }

    fun removeAllData() {
        dataList.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        i: Int
    ): loanHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_param_lay, viewGroup, false)
        return loanHolder(view)
    }

    override fun onBindViewHolder(loanHolder: loanHolder, i: Int) {
        val item = dataList!![i]
        if (item.type <= 0) {
            loanHolder.param_name.visibility = View.GONE
        } else {
            loanHolder.param_name.visibility = View.VISIBLE
        }
        if (flag == 3) {
            loanHolder.param_rv.layoutManager = StaggeredGridLayoutManager(
                2,
                StaggeredGridLayoutManager.VERTICAL
            )
        } else if (flag == 4 || flag == 5) {
            loanHolder.param_rv.layoutManager = StaggeredGridLayoutManager(
                3,
                StaggeredGridLayoutManager.VERTICAL
            )
        } else {
            loanHolder.param_rv.layoutManager = StaggeredGridLayoutManager(
                4,
                StaggeredGridLayoutManager.VERTICAL
            )
        }
        loanHolder.param_rv.itemAnimator = null
        val paramAdapter =
            ParamAdapter(i, context, item.objList, itemCallback, flag, parentCallback)
        childAdapterList.add(paramAdapter)
        loanHolder.param_rv.adapter = paramAdapter
        when (item.type) {
            1 -> {
                loanHolder.param_name.visibility = View.VISIBLE
                loanHolder.param_name.text = "汽油"
            }
            2 -> {
                loanHolder.param_name.visibility = View.VISIBLE
                loanHolder.param_name.text = "柴油"
            }
            3 -> {
                loanHolder.param_name.visibility = View.VISIBLE
                loanHolder.param_name.text = "天然气"
            }
            else -> loanHolder.param_name.visibility = View.GONE
        }
        //        loanHolder.mView.setOnClickListener(new NoMoreClickListener() {
//            @Override
//            protected void OnMoreClick(View view) {
//                itemCallback.onItemCallback(i,null);
//            }
//
//            @Override
//            protected void OnMoreErrorClick() {
//
//            }
//        });
    }

    override fun getItemCount(): Int {
        return dataList?.size ?: 0
    }

    inner class loanHolder(private val mView: View) : RecyclerView.ViewHolder(
        mView
    ) {
        val param_name: TextView
        val param_rv: RecyclerView

        init {
            param_name = itemView.findViewById(R.id.item_param_name)
            param_rv = itemView.findViewById(R.id.item_param_rv)

//            pay_oil = itemView.findViewById(R.id.item_station_pay_oil);
        }
    }

    init {
        //        this.flag = flag;
    }
}