package com.kayu.business_car_owner.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.kayu.business_car_owner.KWApplication
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import com.kayu.business_car_owner.model.SysOrderBean
import com.kayu.business_car_owner.activity.WebViewActivity
import com.kayu.business_car_owner.activity.MainViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.kongzue.dialog.v3.MessageDialog
import com.gcssloop.widget.PagerGridLayoutManager
import com.hjq.toast.ToastUtils
import com.kayu.business_car_owner.R
import com.kongzue.dialog.v3.TipGifDialog
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.kayu.utils.view.RoundImageView
import com.kayu.business_car_owner.ui.income.BalanceFragment
import com.kayu.business_car_owner.activity.CustomerActivity
import com.kayu.business_car_owner.activity.SettingsActivity
import com.kayu.business_car_owner.ui.adapter.OrderCategoryAdapter
import com.kayu.business_car_owner.activity.OilOrderListActivity
import com.kayu.business_car_owner.activity.WashOrderListActivity
import com.kayu.utils.*
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import org.json.JSONException
import org.json.JSONObject
import java.lang.StringBuilder

class PersonalFragment : Fragment() {
    private var refreshLayout: SmartRefreshLayout? = null
    var isLoadmore = false
    var isRefresh = false
    private var mainViewModel: MainViewModel? = null
    private var user_head_img: RoundImageView? = null
    private var user_name: TextView? = null
    private var user_balance: TextView? = null
    private var web_info_tv: TextView? = null
    private var card_num: TextView? = null
    private var user_tip: TextView? = null
    private var card_valid: TextView? = null
    private var explain_content: TextView? = null

    //    private ConstraintLayout all_order_lay;
    //    private LinearLayout more_lay;
    //    private ImageView user_card_bg;
    private var income_lay: LinearLayout? = null
    private var user_expAmt: TextView? = null
    private var user_reacharge: TextView? = null
    private var user_rewad: TextView? = null
    private var category_rv: RecyclerView? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        mainViewModel = ViewModelProviders.of(requireActivity())
            .get(MainViewModel::class.java)
        return inflater.inflate(R.layout.fragment_personal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        refreshLayout = view.findViewById(R.id.refreshLayout)
        //用户头像
        user_head_img = view.findViewById(R.id.personal_user_head_img)
        //用户名称
        user_name = view.findViewById(R.id.personal_user_name)
        //累计节省
        user_expAmt = view.findViewById(R.id.personal_user_expAmt)
        user_reacharge = view.findViewById(R.id.personal_recharge)
        explain_content = view.findViewById(R.id.personal_explain_content)

        //收益
        user_rewad = view.findViewById(R.id.personal_user_rewad)
        //可体现
        user_balance = view.findViewById(R.id.personal_user_balance)
//        user_balance = view.findViewById(R.id.personal_user_balance)
        //        user_card_bg = view.findViewById(R.id.personal_user_card_bg);
//        KWApplication.getInstance().loadImg(R.mipmap.ic_personal_bg,user_card_bg,new GlideRoundTransform(getContext()));
        card_num = view.findViewById(R.id.personal_card_num)
        //账户提示语
        user_tip = view.findViewById(R.id.personal_user_tip)
        card_valid = view.findViewById(R.id.personal_card_valid)
        web_info_tv = view.findViewById(R.id.personal_web_info)
        income_lay = view.findViewById(R.id.personal_income_lay)
        refreshLayout?.setEnableAutoLoadMore(false)
        refreshLayout?.setEnableLoadMore(false)
        refreshLayout?.setEnableLoadMoreWhenContentNotFull(true) //是否在列表不满一页时候开启上拉加载功能
        refreshLayout?.setEnableOverScrollBounce(true) //是否启用越界回弹
        refreshLayout?.setEnableOverScrollDrag(true)
        refreshLayout?.setOnRefreshListener(OnRefreshListener {
            if (isRefresh || isLoadmore) return@OnRefreshListener
            isRefresh = true
            initView()
        })
//        val detailed_list = view.findViewById<TextView>(R.id.personal_detailed_list)
//        detailed_list.setOnClickListener(object : NoMoreClickListener() {
//            override fun OnMoreClick(view: View) {
//                val fg = requireActivity().supportFragmentManager
//                val fragmentTransaction = fg.beginTransaction()
//                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//                fragmentTransaction.add(R.id.main_root_lay, BalanceFragment())
//                fragmentTransaction.addToBackStack("ddd")
//                fragmentTransaction.commit()
//            }
//
//            override fun OnMoreErrorClick() {}
//        })
        val customer_services_lay: ConstraintLayout =
            view.findViewById(R.id.personal_customer_services_lay)
        customer_services_lay.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                startActivity(Intent(context, CustomerActivity::class.java))
            }

            override fun OnMoreErrorClick() {}
        })
        //        ConstraintLayout course_lay = view.findViewById(R.id.personal_course_lay);
//        course_lay.setOnClickListener(new NoMoreClickListener() {
//            @Override
//            protected void OnMoreClick(View view) {
//                mainViewModel.getParameter(getContext(),11).observe(requireActivity(), new Observer<SystemParam>() {
//                    @Override
//                    public void onChanged(SystemParam systemParam) {
//                        String target = systemParam.url;
//                        if (!StringUtil.isEmpty(target)){
//                            Intent intent = new Intent(getContext(), WebViewActivity.class);
//                            intent.putExtra("url",target);
//                            intent.putExtra("from","新手教程");
//                            requireActivity().startActivity(intent);
//                        }
//                    }
//                });
//
//
//            }
//
//            @Override
//            protected void OnMoreErrorClick() {
//
//            }
//        });
        val setting_lay: ConstraintLayout = view.findViewById(R.id.personal_setting_lay)
        setting_lay.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                startActivity(Intent(context, SettingsActivity::class.java))
            }

            override fun OnMoreErrorClick() {}
        })
        //        all_order_lay = view.findViewById(R.id.id_all_order_lay);
//        more_lay = view.findViewById(R.id.personal_more_lay);
        category_rv = view.findViewById(R.id.personal_category_rv)
        //        oil_order_lay = view.findViewById(R.id.personal_oil_order_lay);
//        oil_order_lay.setOnClickListener(new NoMoreClickListener() {
//            @Override
//            protected void OnMoreClick(View view) {
//                startActivity(new Intent(getContext(),OilOrderListActivity.class));
//            }
//
//            @Override
//            protected void OnMoreErrorClick() {
//
//            }
//        });
//        wash_order_lay = view.findViewById(R.id.personal_shop_order_lay);
//        wash_order_lay.setOnClickListener(new NoMoreClickListener() {
//            @Override
//            protected void OnMoreClick(View view) {
//                startActivity(new Intent(getContext(),WashOrderListActivity.class));
//            }
//
//            @Override
//            protected void OnMoreErrorClick() {
//
//            }
//        });
//        if (getUserVisibleHint()){
//            refreshLayout.autoRefresh();
//            mHasLoadedOnce = true;
//        }
        isCreated = true
    }

    private var isCreated = false
    private var mHasLoadedOnce = false // 页面已经加载过
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        LogUtil.e("PersonalFragment----", "----setUserVisibleHint---")
        if (isVisibleToUser && isCreated && !mHasLoadedOnce) {
            LogUtil.e("PersonalFragment----", "----setUserVisibleHint---isCreated")
            TipGifDialog.show(
                requireContext() as AppCompatActivity,
                "加载中...",
                TipGifDialog.TYPE.OTHER,
                R.drawable.loading_gif
            )
            isRefresh = true
            mHasLoadedOnce = true
            initView()
        }
    }

    override fun onStart() {
        super.onStart()
        LogUtil.e("PersonalFragment----", "----onStart---")
        if (!userVisibleHint || mHasLoadedOnce) return
        LogUtil.e("PersonalFragment----", "----onStart---isVisibleToUser")
        TipGifDialog.show(
            requireContext() as AppCompatActivity,
            "加载中...",
            TipGifDialog.TYPE.OTHER,
            R.drawable.loading_gif
        )
        isRefresh = true
        mHasLoadedOnce = true
        initView()
    }

    private fun initView() {
//        if (null != LocationManagerUtil.getSelf().getLoccation()){
//            mainViewModel.getReminder(getContext(), LocationManagerUtil.getSelf().getLoccation().getCity()).observe(requireActivity(), new Observer<String>() {
//                @Override
//                public void onChanged(String parameter) {
//                    explain_content.setText(parameter);
//                }
//            });
//        }
//        mainViewModel!!.sendOilPayInfo(context)
        mainViewModel!!.getUserInfo(requireContext()).observe(requireActivity(), Observer { userBean ->
            if (null == userBean) return@Observer
            mainViewModel!!.getUserTips(requireContext())
                .observe(requireActivity(), Observer { systemParam ->
                    if (null == systemParam) return@Observer
                    try {
                        val jsonObject = JSONObject(systemParam.content)
                        var tipStr = ""
                        var btnStr = ""
                        if (userBean.type == 1) {
                            val json1 = jsonObject.optJSONObject("1")
                            tipStr = json1.getString("tip")
                            btnStr = json1.getString("btn")
                        } else if (userBean.type == 2) {
                            val json2 = jsonObject.optJSONObject("2")
                            tipStr = json2.getString("tip")
                            btnStr = json2.getString("btn")
                        } else if (userBean.type == 3) {
                            val json3 = jsonObject.optJSONObject("3")
                            tipStr = json3.getString("tip")
                            btnStr = json3.getString("btn")
                        }

                        if ( !StringUtil.isEmpty(jsonObject.getString("name"))) {
                            user_reacharge?.text = jsonObject.getString("name")
                            user_reacharge?.setOnClickListener(object : NoMoreClickListener() {
                                override fun OnMoreClick(view: View) {
                                    val target = jsonObject.getString("url")
                                    if (StringUtil.isEmpty(target)) {
                                        ToastUtils.show("链接不存在！")
                                        return
                                    }
                                    val jumpUrl = StringBuilder().append(target)
                                    if (target.contains("?")) {
                                        jumpUrl.append("&token=")
                                    } else {
                                        jumpUrl.append("?token=")
                                    }
                                    val randomNum = System.currentTimeMillis()
                                    jumpUrl.append(KWApplication.instance.token).append("&").append(randomNum)
                                    val intent = Intent(context, WebViewActivity::class.java)
                                    intent.putExtra("url", jumpUrl.toString())
                                    requireActivity().startActivity(intent)
                                }

                                override fun OnMoreErrorClick() {
                                }

                            })
                            user_reacharge?.visibility = View.VISIBLE
                        } else {
                            user_reacharge?.visibility = View.GONE
                        }
                        user_tip!!.text = tipStr
                        web_info_tv!!.text = btnStr
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                })
            KWApplication.instance.loadImg(userBean.headPic, user_head_img!!)
//            var useType = ""
//            when (userBean.type) {
//                -2 -> useType = "游客"
//                1 -> useType = "VIP"
//                2 -> useType = "团长"
//                3 -> useType = "运营商"
//            }
            user_name!!.text = userBean.phone
            card_valid!!.text = userBean.idenName
            user_balance!!.text = userBean.balance.toString()
            val sss = userBean.busTitle.split("#".toRegex()).toTypedArray()
            if (sss.size == 2) {
                explain_content!!.text = sss[0]
                user_expAmt!!.text = sss[1]
            }
            user_rewad!!.text = userBean.rewardAmt.toString()
            if (!StringUtil.isEmpty(userBean.inviteNo)) {
                card_num!!.text = "卡号：" + userBean.inviteNo
                card_num!!.visibility = View.VISIBLE
            } else {
                card_num!!.visibility = View.INVISIBLE
            }
            if (userBean.type < 1) {
                income_lay!!.visibility = View.GONE
            } else {
                income_lay!!.visibility = View.VISIBLE
            }
            web_info_tv!!.setOnClickListener(object : NoMoreClickListener() {
                override fun OnMoreClick(view: View) {
                    val target = userBean.equityUrl
                    val jumpUrl = StringBuilder().append(target)
                    if (target.contains("?")) {
                        jumpUrl.append("&token=")
                    } else {
                        jumpUrl.append("?token=")
                    }
                    val randomNum = System.currentTimeMillis()
                    jumpUrl.append(KWApplication.instance.token).append("&").append(randomNum)
                    val intent = Intent(context, WebViewActivity::class.java)
                    intent.putExtra("url", jumpUrl.toString())
                    requireActivity().startActivity(intent)
                }

                override fun OnMoreErrorClick() {}
            })
        })
        mainViewModel!!.getSysOrderList(requireContext()).observe(
            requireActivity(),
            Observer<MutableList<MutableList<SysOrderBean>>?> { categoryBeans ->
                if (null == categoryBeans) return@Observer
                for (list in categoryBeans) {
                    for (categoryBean in list) {
                        if (StringUtil.equals(categoryBean.type, "KY_GAS")) {
                            KWApplication.instance.isGasPublic = categoryBean.isPublic
                        }
                        if (StringUtil.equals(categoryBean.type, "KY_WASH")) {
                            KWApplication.instance.isWashPublic = categoryBean.isPublic
                        }
                    }
                }
                val mColumns = 1
                val mRows = categoryBeans.size
                //                if (categoryBeans.size() <= 4) {
//                    mColumns = 4;
//                    mRows = 1;
//
//                } else {
//                    mRows = categoryBeans.size() % 4 == 0 ? categoryBeans.size() / 4 : categoryBeans.size() / 4 + 1;
//                    mColumns = 4;
//                }
                val layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, resources.getDimensionPixelSize(
                        R.dimen.dp_84
                    ) * mRows
                )
                layoutParams.topMargin = resources.getDimensionPixelSize(R.dimen.dp_14)
                category_rv!!.layoutParams = layoutParams
                val mLayoutManager =
                    PagerGridLayoutManager(mRows, mColumns, PagerGridLayoutManager.HORIZONTAL)
                // 系统带的 RecyclerView，无需自定义


                // 水平分页布局管理器
                mLayoutManager.setPageListener(object : PagerGridLayoutManager.PageListener {
                    override fun onPageSizeChanged(pageSize: Int) {}
                    override fun onPageSelect(pageIndex: Int) {}
                }) // 设置页面变化监听器
                category_rv!!.layoutManager = mLayoutManager
                val categoryAdapter = OrderCategoryAdapter(categoryBeans, object : ItemCallback {
                    override fun onItemCallback(position: Int, obj: Any?) {
                        val categoryBean = obj as SysOrderBean
                        val userRole = KWApplication.instance.userRole
                        val isPublic = categoryBean.isPublic
                        if (userRole == -2 && isPublic == 0) {
                            KWApplication.instance.showRegDialog(requireContext())
                            return
                        }
                        val target = categoryBean.href
                        if (StringUtil.equals(categoryBean.type, "KY_GAS")) {
                            startActivity(Intent(context, OilOrderListActivity::class.java))
                        } else if (StringUtil.equals(categoryBean.type, "KY_WASH")) {
                            startActivity(Intent(context, WashOrderListActivity::class.java))
                        } else {
                            if (!StringUtil.isEmpty(target)) {
                                val intent = Intent(
                                    context, WebViewActivity::class.java
                                )
                                val sb = StringBuilder()
                                sb.append(target)
                                //                                sb.append("https://www.ky808.cn/carfriend/static/cyt/text/index.html#/advertising"); 测试视屏广告链接
                                if (StringUtil.equals(categoryBean.type, "KY_H5")) {
                                    if (target!!.contains("?")) {
                                        sb.append("&token=")
                                    } else {
                                        sb.append("?token=")
                                    }
                                    sb.append(KWApplication.instance.token)
                                }
                                intent.putExtra("url", sb.toString())
                                intent.putExtra("from", "首页")
                                startActivity(intent)
                            } else {
                                MessageDialog.show(
                                    (requireContext() as AppCompatActivity),
                                    "温馨提示",
                                    "功能未开启，敬请期待"
                                )
                            }
                        }
                    }

                    override fun onDetailCallBack(position: Int, obj: Any?) {}
                })
                category_rv!!.adapter = categoryAdapter
            })

//        mainViewModel.getSysParameter(getContext(),10).observe(requireActivity(), new Observer<SystemParam>() {
//            @Override
//            public void onChanged(SystemParam systemParam) {
//                if (null  == systemParam)
//                    return;
//                try {
//                    JSONObject jsonObject = new JSONObject(systemParam.content);
//                    int showGas = jsonObject.optInt("gas");
//                    int showCarWash = jsonObject.optInt("carwash");
//                    if (showGas == 1 && showCarWash ==1 ) {
//                        all_order_lay.setVisibility(View.VISIBLE);
//                        wash_order_lay.setVisibility(View.VISIBLE);
//                        oil_order_lay.setVisibility(View.VISIBLE);
//
//                    }else if(showGas == 0 && showCarWash == 0){
//                        all_order_lay.setVisibility(View.GONE);
//                        wash_order_lay.setVisibility(View.GONE);
//                        oil_order_lay.setVisibility(View.GONE);
//                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(more_lay.getLayoutParams());
//                        layoutParams.setMargins(getResources().getDimensionPixelSize(R.dimen.dp_15)
//                                ,getResources().getDimensionPixelSize(R.dimen.dp_90)
//                                ,getResources().getDimensionPixelSize(R.dimen.dp_15)
//                                ,getResources().getDimensionPixelSize(R.dimen.dp_20));
//                        more_lay.setLayoutParams(layoutParams);
//
//                    } else {
//
//                        if (showCarWash == 1) {
//                            wash_order_lay.setVisibility(View.VISIBLE);
//                        }else{
//                            wash_order_lay.setVisibility(View.GONE);
//                        }
//                        if (showGas == 1) {
//                            oil_order_lay.setVisibility(View.VISIBLE);
//                        } else {
//                            oil_order_lay.setVisibility(View.GONE);
//                        }
//                        all_order_lay.setVisibility(View.VISIBLE);
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        });
        if (isRefresh) {
            refreshLayout!!.finishRefresh()
            isRefresh = false
        }
        if (isLoadmore) {
            refreshLayout!!.finishLoadMore()
            isLoadmore = false
        }
    }
}