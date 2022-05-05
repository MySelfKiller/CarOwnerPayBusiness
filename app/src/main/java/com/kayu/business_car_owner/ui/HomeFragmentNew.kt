package com.kayu.business_car_owner.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.amap.api.location.AMapLocation
import com.gcssloop.widget.PagerGridLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.kayu.business_car_owner.KWApplication
import com.kayu.business_car_owner.R
import com.kayu.business_car_owner.activity.*
import com.kayu.business_car_owner.model.*
import com.kayu.business_car_owner.popupWindow.CustomPopupWindow
import com.kayu.business_car_owner.text_banner.TextBannerView
import com.kayu.business_car_owner.ui.adapter.Category2Adapter
import com.kayu.business_car_owner.ui.adapter.CategoryRootAdapter
import com.kayu.business_car_owner.ui.adapter.ImgTitleAdapter
import com.kayu.business_car_owner.ui.adapter.SortTitleAdapter
import com.kayu.utils.*
import com.kayu.utils.StringUtil.equals
import com.kayu.utils.callback.Callback
import com.kayu.utils.location.CoordinateTransformUtil
import com.kayu.utils.location.LocationCallback
import com.kayu.utils.location.LocationManagerUtil
import com.kayu.utils.view.AdaptiveHeightViewPager
import com.kongzue.dialog.v3.MessageDialog
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import com.youth.banner.Banner
import com.youth.banner.BannerConfig
import com.youth.banner.listener.OnBannerListener
import org.json.JSONException
import org.json.JSONObject

class HomeFragmentNew
    (private val navigation: BottomNavigationView) : Fragment() {
    private var mainViewModel: MainViewModel? = null
    private var banner: Banner? = null
    private var category_rv: RecyclerView? = null
    private var img_title_rv: RecyclerView? = null
    private var sort_title_rv: RecyclerView? = null
    private var category2_rv: RecyclerView? = null
    private var hostTextBanner: TextBannerView? = null
    private var refreshLayout: RefreshLayout? = null
    var isRefresh = false
    private var pageIndex = 0
    private var location_tv: TextView? = null
    private var notify_show: TextView? = null
    private var title_lay_bg: LinearLayout? = null
    private var home_child_lay: LinearLayout? = null
    private var pageAdapter: PagerAdapter? = null
    private var mViewPager: AdaptiveHeightViewPager? = null
    private var mFragments = ArrayList<Fragment>()
    private var isOnline = ""
    var isLoadmore = false
    var isFirstLoad = true
//    private var fragIndex = 0

    private val callback: Callback = object : Callback {
        override fun onSuccess() {
            if (isRefresh) {
                refreshLayout!!.finishRefresh()
                isRefresh = false
            }
            if (isLoadmore) {
                refreshLayout!!.finishLoadMore()
                isLoadmore = false
            }
        }

        override fun onError() {
            pageIndex = 1
        }
    }
    private var scrollView: FadingScrollView? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        LogUtil.e("HomeFragment----", "----onCreateView---")
        mainViewModel = ViewModelProviders.of(requireActivity()).get(MainViewModel::class.java)
        return inflater.inflate(R.layout.fragment_home_new, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        LogUtil.e("HomeFragment----", "----onViewCreated---")
        banner = view.findViewById(R.id.home_smart_banner)
        location_tv = view.findViewById(R.id.home_location_tv)
        view.findViewById<View>(R.id.home_exchange_code)
            .setOnClickListener(object : NoMoreClickListener() {
                override fun OnMoreClick(view: View) {
                    startActivity(Intent(context, MessageActivity::class.java))
                }

                override fun OnMoreErrorClick() {}
            })
        notify_show = view.findViewById(R.id.home_notify_show)
        title_lay_bg = view.findViewById(R.id.home_title_lay)
        title_lay_bg?.setAlpha(0f)
        scrollView = view.findViewById(R.id.home_scroll)
        scrollView?.setFadingView(title_lay_bg)
        scrollView?.setFadingHeightView(banner)
        category_rv = view.findViewById(R.id.home_category_rv)
        mViewPager = view.findViewById(R.id.list_vp)
        home_child_lay = view.findViewById(R.id.home_child_lay)
        img_title_rv = view.findViewById(R.id.home_img_title_rv)
        sort_title_rv = view.findViewById(R.id.home_sort_title_rv)
        category2_rv = view.findViewById(R.id.home_category2_rv)
        hostTextBanner = view.findViewById(R.id.home_hostTextBanner)
        refreshLayout = view.findViewById<View>(R.id.refreshLayout) as RefreshLayout
        //        refreshLayout.setEnableNestedScroll(false);
        mFragments.add(HomeCarWashFragment(mViewPager, 0, callback))
        pageAdapter = MyPagerAdapter(childFragmentManager, mFragments)

        mViewPager?.setAdapter(pageAdapter)
        mViewPager?.setOffscreenPageLimit(2)
        mViewPager?.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                mViewPager?.resetHeight(position)
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
        refreshLayout!!.setEnableLoadMoreWhenContentNotFull(true) //是否在列表不满一页时候开启上拉加载功能
        refreshLayout!!.setEnableOverScrollBounce(true) //是否启用越界回弹
        refreshLayout!!.setEnableOverScrollDrag(true)
        refreshLayout!!.setOnRefreshListener(OnRefreshListener { //                if (!isHasLocation){
//                    return;
//                }
            if (isRefresh) return@OnRefreshListener
            isRefresh = true
            pageIndex = 1
            if (mHasLoadedOnce) {
//                    LogUtil.e("HomeFragment----","----setOnRefreshListener---mHasLoadedOnce");
                initView()
            }
            initListView()
            mHasLoadedOnce = true
        })
        LocationManagerUtil.self?.setLocationListener (object: LocationCallback{
            //                    LogUtil.e("HomeFragment----","----onStart--------LocationCallback");
            override fun onLocationChanged(location: AMapLocation) {
                latitude = location.latitude
                longitude = location.longitude
                cityName = location.city
                location_tv?.setText(cityName)
                isHasLocation = true
                if (!hasAutoRefresh ) {
//                    LogUtil.e("HomeFragment----","----onLocationChanged--- hasAutoRefresh----" );
                    isRefresh = true
                    pageIndex = 1
                    initListView()
                    mHasLoadedOnce = true
                    hasAutoRefresh = true
                }
            }
        })
        isCreated = true
    }

    private var isHasLocation = false
    private var mHasLoadedOnce = false // 页面已经加载过
    private var isCreated = false
    private var hasAutoRefresh = false
    override fun onStart() {
        super.onStart()
        if (!userVisibleHint) return
        //        LogUtil.e("HomeFragment----","----onStart---");
        if (!mHasLoadedOnce) {
//            LogUtil.e("HomeFragment----","----onStart---mHasLoadedOnce");
            initView()
            if (isHasLocation) {
                isRefresh = true
                pageIndex = 1
                initListView()
                mHasLoadedOnce = true
                hasAutoRefresh = true
                //            LogUtil.e("HomeFragment----","----onStart---isHasLocation");
            }
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        //        LogUtil.e("HomeFragment----","----setUserVisibleHint---");
        if (isVisibleToUser && isCreated) {
//            LogUtil.e("HomeFragment----","----setUserVisibleHint---isCreated");
            if (!mHasLoadedOnce) {
//                LogUtil.e("HomeFragment----","----setUserVisibleHint---mHasLoadedOnce");
                initView()
                if (isHasLocation) {
                    isRefresh = true
                    pageIndex = 1
                    initListView()
                    mHasLoadedOnce = true
                    hasAutoRefresh = true
                    //                LogUtil.e("HomeFragment----","----setUserVisibleHint---isHasLocation");
                }
            }
        }
        if (null != popWindow && !hasClose) {
            if (isVisibleToUser) {
                popWindow!!.showAtLocation(
                    navigation,
                    Gravity.NO_GRAVITY,
                    (KWApplication.instance.displayWidth - navigation.measuredWidth) / 2,
                    KWApplication.instance.displayHeight - navigation.measuredHeight - navigation.measuredHeight / 3
                )
            } else {
                popWindow!!.dismiss()
            }
        }
    }

    private fun initListView() {
        mainViewModel!!.getSysParameter(requireContext(), 10)
            .observe(requireActivity(), Observer { systemParam ->
                if (null == systemParam) return@Observer
                val editor = requireActivity().getSharedPreferences(
                    Constants.SharedPreferences_name,
                    Context.MODE_PRIVATE
                ).edit()
                editor.putString(Constants.system_args, systemParam.content)
                editor.apply()
                editor.commit()
                //todo 上线应用商店审核作的判断
                isOnline = systemParam.blank1
                if (StringUtil.equals(isOnline, "isOnline") || KWApplication.instance.userRole ==-2) {
                    refreshLayout!!.setEnableAutoLoadMore(true)
                    refreshLayout!!.setEnableLoadMore(true)
                    refreshLayout!!.setOnLoadMoreListener(OnLoadMoreListener {
                        if (isRefresh || isLoadmore) return@OnLoadMoreListener
                        isLoadmore = true
                        pageIndex = pageIndex + 1
                        loadChildData()
                    })
                    mViewPager?.visibility = View.VISIBLE
                    home_child_lay?.visibility = View.GONE
                    img_title_rv?.visibility = View.GONE
                    loadChildData()
                } else {
                    refreshLayout!!.setEnableAutoLoadMore(false)
                    refreshLayout!!.setEnableLoadMore(false)
                    mViewPager?.visibility = View.GONE
                    home_child_lay?.visibility = View.VISIBLE
                    img_title_rv?.visibility = View.VISIBLE
                    mainViewModel!!.getPopNaviList(requireContext()).observe(requireActivity(), Observer<MutableList<PopNaviBean>?> { popNaviBeanlist->
                        if (popNaviBeanlist == null) {
                            return@Observer
                        }
                        img_title_rv!!.layoutManager = GridLayoutManager(requireContext(),3)
                        img_title_rv!!.adapter = ImgTitleAdapter(popNaviBeanlist,object :ItemCallback{
                            override fun onItemCallback(position: Int, obj: Any?) {
                                val popNaviBean = obj as PopNaviBean
                                val url = popNaviBean.url
                                if (!popNaviBean.type.isNullOrEmpty()) {
                                    when {
                                        StringUtil.equals(popNaviBean.type, "KY_GAS") -> {
                                            startActivity(Intent(context, GasStationListActivity::class.java))
                                        }
                                        StringUtil.equals(popNaviBean.type, "KY_WASH") -> {
                                            startActivity(Intent(context, CarWashListActivity::class.java))
                                        }
                                        else -> {
                                            judgeURL2Jump(url,popNaviBean.type)
                                        }
                                    }
                                } else {
                                    judgeURL2Jump(url,null)
                                }
                            }

                            override fun onDetailCallBack(position: Int, obj: Any?) {
                            }

                        })

                    })

                    mainViewModel!!.getProductSortList(requireContext()).observe(requireActivity()) {
                        if (it == null) {
                            return@observe
                        }
                        sort_title_rv!!.layoutManager = GridLayoutManager(requireContext(), 4)
                        category2_rv!!.layoutManager =
                            StaggeredGridLayoutManager(5, StaggeredGridLayoutManager.VERTICAL)
                        val category2Adapter = Category2Adapter(it[0].products, object : ItemCallback {
                            override fun onItemCallback(position: Int, obj: Any?) {
                                val product = obj as Product
                                val url = product.link
                                if (!StringUtil.isEmpty(url)) {
                                    val intent = Intent(context, WebViewActivity::class.java)
                                    val sb = StringBuilder()
                                    sb.append(url)
                                    if (url.contains("?")) {
                                        sb.append("&token=")
                                    } else {
                                        sb.append("?token=")
                                    }
                                    sb.append(KWApplication.instance.token)
                                    sb.append("&id=").append(product.id)
                                    sb.append("&locationName=")
                                    sb.append(cityName)
                                    sb.append("&selectLocation=")
                                    sb.append(longitude)
                                    sb.append(",")
                                    sb.append(latitude)
                                    intent.putExtra("url", sb.toString())
//                                    intent.putExtra("url", "http://192.168.3.41:8020/%E7%A4%BA%E4%BE%8B%E9%A1%B5%E9%9D%A2/demo.html?__hbt=1651719988425")
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

                            override fun onDetailCallBack(position: Int, obj: Any?) {
                            }
                        })
                        category2_rv!!.adapter = category2Adapter
                        sort_title_rv!!.adapter = SortTitleAdapter(it, object : ItemCallback {
                            override fun onItemCallback(position: Int, obj: Any?) {
                                category2Adapter.removeAllData()
                                category2Adapter.addAllData((obj as ProductSortBean).products)

                            }

                            override fun onDetailCallBack(position: Int, obj: Any?) {

                            }

                        })
                    }
                }
            })
        if (isRefresh) {
            refreshLayout!!.finishRefresh()
            isRefresh = false
        }
    }
    private var hasShow = false
    private var hasClose = false
    private fun initView() {
        mainViewModel!!.getRegDialogTip(requireContext()).observe(requireActivity()) { systemParam ->
            KWApplication.instance.regDialogTip = systemParam
            //KWApplication.getInstance().userRole == -2 &&
            if (null != KWApplication.instance.regDialogTip && KWApplication.instance.userRole == -2 && !hasShow) {
//                showApplyCardDialog(activity, context, navigation)
                hasShow = true
            }
        }
        mainViewModel!!.getNotifyNum(requireContext()).observe(requireActivity(), Observer { integer ->
            if (null == integer) return@Observer
            if (integer == 0) {
                notify_show!!.visibility = View.GONE
            } else {
                notify_show!!.visibility = View.VISIBLE
            }
        })
        mainViewModel!!.getNotifyList(requireContext()).observe(
            requireActivity()) { strings -> //                List<String> hostBannerData = new ArrayList<>();
            if (null != strings && strings.size > 0) {
                hostTextBanner!!.setDatas(strings)
            }
        }
        mainViewModel!!.getBannerList(requireContext())
            .observe(requireActivity(), Observer { bannerBeans ->
                if (null == bannerBeans) return@Observer
                val urlList: MutableList<String?> = ArrayList()
                for (item in bannerBeans) {
                    if (StringUtil.equals(item.type, "KY_GAS")) {
                        KWApplication.instance.isGasPublic = item.isPublic
                    }
                    if (StringUtil.equals(item.type, "KY_WASH")) {
                        KWApplication.instance.isWashPublic = item.isPublic
                    }
                    urlList.add(item.img)
                }
                //                title_lay.setBackgroundColor(Color.parseColor(bannerBeans.get(0).bgColor));
//                StatusBarUtil.setStatusBarColor(getActivity(), Color.parseColor(bannerBeans.get(0).bgColor));
                banner!!.setBannerStyle(BannerConfig.CIRCLE_INDICATOR)
                    .setIndicatorGravity(BannerConfig.RIGHT)
                    .setImageLoader(BannerImageLoader())
                    .setImages(urlList)
                    .setDelayTime(2000)
                    .start()
                    .setOnPageChangeListener(object : OnPageChangeListener {
                        override fun onPageScrolled(
                            position: Int,
                            positionOffset: Float,
                            positionOffsetPixels: Int
                        ) {
                        }

                        override fun onPageSelected(position: Int) {
                        }

                        override fun onPageScrollStateChanged(state: Int) {}
                    })
                banner!!.setOnBannerListener(OnBannerListener { position ->
                    val target = bannerBeans[position].href
//                    val isPublic = bannerBeans[position].isPublic
//                    val userRole = KWApplication.instance.userRole
//                    if (userRole == -2 && isPublic == 0) {
//                        KWApplication.instance.showRegDialog(requireContext())
//                        return@OnBannerListener
//                    }
                    if (!bannerBeans[position].type.isNullOrEmpty()) {
                        when {
                            StringUtil.equals(bannerBeans[position].type, "KY_GAS") -> {
                                startActivity(Intent(context, GasStationListActivity::class.java))
                            }
                            StringUtil.equals(bannerBeans[position].type, "KY_WASH") -> {
                                startActivity(Intent(context, CarWashListActivity::class.java))
                            }
                            else -> {
                                judgeURL2Jump(target,bannerBeans[position].type)
                            }
                        }
                    } else {
                        judgeURL2Jump(target,null)
                    }
                })
            })

        mainViewModel!!.getCategoryList(requireContext()).observe(
            requireActivity(),
            Observer<MutableList<MutableList<CategoryBean>>?> { categoryBeans ->
                if (null == categoryBeans) return@Observer
                var categoryListNew: MutableList<MutableList<CategoryBean>> = ArrayList()
                val categoryBeans1: MutableList<CategoryBean> = ArrayList()
                for (list in categoryBeans) {
                    for (categoryBean in list) {
                        if (equals(categoryBean.title, "特惠加油")
                            || equals(categoryBean.title, "特惠洗车")
//                            || equals(categoryBean.title, "电影订票")
                        ) {
                            categoryBeans1.add(categoryBean)
                        }
                        if (StringUtil.equals(categoryBean.type, "KY_GAS")) {
                            KWApplication.instance.isGasPublic = categoryBean.isPublic
                        }
                        if (StringUtil.equals(categoryBean.type, "KY_WASH")) {
                            KWApplication.instance.isWashPublic = categoryBean.isPublic
                        }
                    }
                }
                categoryListNew.add(categoryBeans1)
                //当前是游客模式展示3个
                if (StringUtil.equals(isOnline, "isOnline") || KWApplication.instance.userRole != -2
                ) {
                    categoryListNew = categoryBeans
                }
                val mColumns = 1
                val mRows = categoryListNew.size
                val layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, resources.getDimensionPixelSize(
                        R.dimen.dp_90
                    ) * mRows
                )
                layoutParams.topMargin = resources.getDimensionPixelSize(R.dimen.dp_10)
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
                val categoryAdapter = CategoryRootAdapter(categoryListNew, object : ItemCallback {
                    override fun onItemCallback(position: Int, obj: Any?) {
                        val categoryBean = obj as CategoryBean
//                        val userRole = KWApplication.instance.userRole
//                        val isPublic = categoryBean.isPublic
//                        if (userRole == -2 && isPublic == 0) {
//                            KWApplication.instance.showRegDialog(requireContext())
//                            return
//                        }
                        val target = categoryBean.href
                        if (!categoryBean.type.isNullOrEmpty()) {
                            when {
                                StringUtil.equals(categoryBean.type, "KY_GAS") -> {
                                    startActivity(Intent(context, GasStationListActivity::class.java))
                                }
                                StringUtil.equals(categoryBean.type, "KY_WASH") -> {
                                    startActivity(Intent(context, CarWashListActivity::class.java))
                                }
                                else -> {
                                    judgeURL2Jump(target,categoryBean.type)
                                }
                            }
                        } else {
                            judgeURL2Jump(target,null)
                        }

                    }

                    override fun onDetailCallBack(position: Int, obj: Any?) {}
                })
                category_rv!!.adapter = categoryAdapter
            })

    }

    /**
     *验证url并调整相应的activity
     */
    private fun judgeURL2Jump(url:String?, type: String?) {
        if (!url.isNullOrEmpty()) {
            val intent = Intent(context, WebViewActivity::class.java)
            val sb = StringBuilder()
            sb.append(url)
            if (!type.isNullOrEmpty()) {
                if (StringUtil.equals(type, "KY_H5")) {
                    if (url.contains("?")) {
                        sb.append("&token=")
                    } else {
                        sb.append("?token=")
                    }
                    sb.append(KWApplication.instance.token)
                    sb.append("&locationName=")
                    sb.append(cityName)
                    sb.append("&selectLocation=")
                    sb.append(longitude)
                    sb.append(",")
                    sb.append(latitude)
                }
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

    private fun loadChildData() {
        var homeCarWashFragment = mFragments[0] as HomeCarWashFragment
        val bddfsdfs = CoordinateTransformUtil.gcj02tobd09(longitude, latitude)
        homeCarWashFragment.reqData(
            refreshLayout,
            pageIndex,
            isRefresh,
            isLoadmore,
            bddfsdfs[1],
            bddfsdfs[0],
            cityName
        )
    }


    private var latitude = 0.0
    private var longitude = 0.0
    private var cityName: String? = null
    private var popWindow: CustomPopupWindow? = null
    private var regTips: String? = null
    fun showApplyCardDialog(activity: Activity?, context: Context?, v: View) {
        val regDialogTip = KWApplication.instance.regDialogTip
        if (null == regDialogTip || StringUtil.isEmpty(regDialogTip.content)) {
            return
        }
        regTips = try {
            val contentJSon = JSONObject(regDialogTip.content)
            contentJSon.getString("regTips")
        } catch (e: JSONException) {
            e.printStackTrace()
            return
        }
        val tips = regTips?.split("#".toRegex())?.toTypedArray()
        if (!StringUtil.isEmpty(regTips)) {
            if (null == tips || tips.size != 2) return
        }
        val view = requireActivity().layoutInflater.inflate(R.layout.dialog_apply_card, null)
        val dia_close = view.findViewById<ImageView>(R.id.dia_close_iv)
        dia_close.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                popWindow!!.dismiss()
                hasClose = true
            }

            override fun OnMoreErrorClick() {}
        })
        val dia_content = view.findViewById<TextView>(R.id.dia_act_context)
        dia_content.text = tips!![0]
        val dia_btn_handle: AppCompatButton = view.findViewById(R.id.dia_act_btn_handle)
        dia_btn_handle.text = tips[1]
        dia_btn_handle.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                if (StringUtil.isEmpty(regDialogTip.url)) return
                val intent = Intent(context, WebViewActivity::class.java)
                intent.putExtra("url", regDialogTip.url)
                context!!.startActivity(intent)
            }

            override fun OnMoreErrorClick() {}
        })
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        popWindow = CustomPopupWindow.PopupWindowBuilder(requireContext()) //.setView(R.layout.pop_layout)
            .setView(view)
            .size(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            .setFocusable(false) //弹出popWindow时，背景是否变暗
            .enableBackgroundDark(false) //控制亮度
            .setBgDarkAlpha(0.0f)
            .setOutsideTouchable(false) //                            .setAnimationStyle(R.style.popWindowStyle)
            .setOnDissmissListener {
                //对话框销毁时
            }
            .create()
        popWindow?.showAtLocation(
            v,
            Gravity.NO_GRAVITY,
            (KWApplication.instance.displayWidth - view.measuredWidth) / 2,
            KWApplication.instance.displayHeight - v.measuredHeight - v.measuredHeight / 3
        )
    }
}