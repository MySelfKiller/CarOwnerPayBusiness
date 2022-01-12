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
import com.flyco.tablayout.listener.CustomTabEntity
import com.flyco.tablayout.TabEntity
import com.flyco.tablayout.listener.OnTabSelectListener
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import androidx.viewpager.widget.PagerAdapter
import com.kayu.business_car_owner.activity.MyPagerAdapter
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.kayu.business_car_owner.model.CategoryBean
import com.kayu.business_car_owner.KWApplication
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.kayu.business_car_owner.activity.WebViewActivity
import com.kayu.business_car_owner.activity.MainViewModel
import com.youth.banner.Banner
import com.kayu.business_car_owner.text_banner.TextBannerView
import com.kayu.utils.view.AdaptiveHeightViewPager
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.amap.api.location.AMapLocation
import com.flyco.tablayout.SegmentTabLayout
import com.kayu.business_car_owner.activity.MessageActivity
import com.kayu.utils.location.LocationManagerUtil
import com.youth.banner.BannerConfig
import com.kayu.business_car_owner.activity.BannerImageLoader
import com.youth.banner.listener.OnBannerListener
import com.kayu.business_car_owner.activity.GasStationListActivity
import com.kayu.business_car_owner.activity.CarWashListActivity
import com.kongzue.dialog.v3.MessageDialog
import com.gcssloop.widget.PagerGridLayoutManager
import com.kayu.business_car_owner.R
import com.kayu.business_car_owner.ui.adapter.CategoryRootAdapter
import com.kayu.utils.location.CoordinateTransformUtil
import com.kayu.business_car_owner.popupWindow.CustomPopupWindow
import com.kayu.utils.*
import com.kayu.utils.callback.Callback
import com.kayu.utils.location.LocationCallback
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import org.json.JSONException
import org.json.JSONObject
import java.lang.StringBuilder
import java.util.ArrayList

class HomeFragmentNew
    (private val navigation: BottomNavigationView) : Fragment() {
    private var mainViewModel: MainViewModel? = null
    private var banner: Banner? = null
    private var category_rv: RecyclerView? = null
    private var hostTextBanner: TextBannerView? = null
    private var refreshLayout: RefreshLayout? = null
    var isLoadmore = false
    var isRefresh = false
    private var pageIndex = 0
    var isFirstLoad = true
    private var slidingTabLayout: SegmentTabLayout? = null
    private var mViewPager: AdaptiveHeightViewPager? = null
    private val mTabEntities = ArrayList<CustomTabEntity>()
    private val mFragments = ArrayList<Fragment>()
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
    private var location_tv: TextView? = null
    private var notify_show: TextView? = null
    private var adapter: PagerAdapter? = null
    private var title_lay_bg: LinearLayout? = null
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
        hostTextBanner = view.findViewById(R.id.home_hostTextBanner)
        slidingTabLayout = view.findViewById(R.id.list_ctl)
        mViewPager = view.findViewById(R.id.list_vp)
        refreshLayout = view.findViewById<View>(R.id.refreshLayout) as RefreshLayout
        //        refreshLayout.setEnableNestedScroll(false);
        refreshLayout!!.setEnableAutoLoadMore(false)
        refreshLayout!!.setEnableLoadMore(true)
        refreshLayout!!.setEnableLoadMoreWhenContentNotFull(true) //是否在列表不满一页时候开启上拉加载功能
        refreshLayout!!.setEnableOverScrollBounce(true) //是否启用越界回弹
        refreshLayout!!.setEnableOverScrollDrag(true)
        refreshLayout!!.setOnRefreshListener(OnRefreshListener { //                if (!isHasLocation){
//                    return;
//                }
            if (isRefresh || isLoadmore) return@OnRefreshListener
            isRefresh = true
            pageIndex = 1
            if (mHasLoadedOnce) {
//                    LogUtil.e("HomeFragment----","----setOnRefreshListener---mHasLoadedOnce");
                initView()
            }
            initListView()
            mHasLoadedOnce = true
        })
        refreshLayout!!.setOnLoadMoreListener(OnLoadMoreListener {
            if (isRefresh || isLoadmore) return@OnLoadMoreListener
            isLoadmore = true
            pageIndex = pageIndex + 1
            loadChildData()
        })
//        mTabEntities.add(TabEntity("加油", R.mipmap.ic_bg_close, R.mipmap.ic_bg_close))
//        mTabEntities.add(TabEntity("洗车", R.mipmap.ic_bg_close, R.mipmap.ic_bg_close))
        mFragments.add(HomeGasStationFragment(mViewPager, 0, callback))
        mFragments.add(HomeCarWashFragment(mViewPager, 1, callback))
        adapter = MyPagerAdapter(childFragmentManager, mFragments)
        mViewPager?.setAdapter(adapter)
        val mTitles_3 = arrayOf("加油", "洗车")
        slidingTabLayout?.setTabData(mTitles_3)
        slidingTabLayout?.setOnTabSelectListener(object : OnTabSelectListener {
            override fun onTabSelect(position: Int) {
                mViewPager?.setCurrentItem(position)
                fragIndex = position
            }

            override fun onTabReselect(position: Int) {}
        })
        mViewPager?.setOffscreenPageLimit(2)
        mViewPager?.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                fragIndex = position
                slidingTabLayout?.setCurrentTab(position)
                mViewPager?.resetHeight(position)
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })

//        checkLocation();
        LocationManagerUtil.self?.setLocationListener (object: LocationCallback{
            //                    LogUtil.e("HomeFragment----","----onStart--------LocationCallback");
            override fun onLocationChanged(location: AMapLocation) {
                latitude = location.latitude
                longitude = location.longitude
                cityName = location.city
                location_tv?.setText(cityName)
                isHasLocation = true
                if (!hasAutoRefresh) {
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

    private var fragIndex = 0
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
                try {
                    val jsonObject = JSONObject(systemParam.content)
                    val showGas = jsonObject.optInt("gas")
                    val showCarWash = jsonObject.optInt("carwash")
                    if (showGas == 1 && showCarWash == 1) {
                        slidingTabLayout!!.visibility = View.VISIBLE
                        mViewPager!!.currentItem = fragIndex
                        slidingTabLayout!!.currentTab = fragIndex
                        mViewPager!!.isScrollble = true
                    } else if (showGas == 0 && showCarWash == 0) {
                        slidingTabLayout!!.visibility = View.GONE
                        mViewPager!!.visibility = View.GONE
                    } else {
                        slidingTabLayout!!.visibility = View.GONE
                        if (showCarWash == 1) {
                            fragIndex = 1
                            mViewPager!!.currentItem = fragIndex
                            mViewPager!!.isScrollble = false
                        } else if (showGas == 1) {
                            fragIndex = 0
                            mViewPager!!.currentItem = fragIndex
                            mViewPager!!.isScrollble = false
                        }
                    }
                    loadChildData()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            })
        if (isRefresh) {
            refreshLayout!!.finishRefresh()
            isRefresh = false
        }
        if (isLoadmore) {
            refreshLayout!!.finishLoadMore()
            isLoadmore = false
        }
    }

    private var hasShow = false
    private var hasClose = false
    private fun initView() {
        mainViewModel!!.getRegDialogTip(requireContext()).observe(requireActivity(), { systemParam ->
            KWApplication.instance.regDialogTip = systemParam
            //KWApplication.getInstance().userRole == -2 &&
            if (null != KWApplication.instance.regDialogTip && KWApplication.instance.userRole == -2 && !hasShow) {
                showApplyCardDialog(activity, context, navigation)
                hasShow = true
            }
        })
        mainViewModel!!.getNotifyNum(requireContext()).observe(requireActivity(), Observer { integer ->
            if (null == integer) return@Observer
            if (integer == 0) {
                notify_show!!.visibility = View.GONE
            } else {
                notify_show!!.visibility = View.VISIBLE
            }
        })
        mainViewModel!!.getNotifyList(requireContext()).observe(
            requireActivity(),
            { strings -> //                List<String> hostBannerData = new ArrayList<>();
                if (null != strings && strings.size > 0) {
                    hostTextBanner!!.setDatas(strings)
                }
            })
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
                    .setImages(urlList) //                .setBannerTitles(titles)
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
//                                if (getUserVisibleHint()) {
//                                    title_lay.setBackgroundColor(Color.parseColor(bannerBeans.get(position).bgColor));
//                                    StatusBarUtil.setStatusBarColor(getActivity(), Color.parseColor(bannerBeans.get(position).bgColor));
//                                }
                        }

                        override fun onPageScrollStateChanged(state: Int) {}
                    })
                banner!!.setOnBannerListener(OnBannerListener { position ->
                    val target = bannerBeans[position].href
                    val isPublic = bannerBeans[position].isPublic
                    val userRole = KWApplication.instance.userRole
                    if (userRole == -2 && isPublic == 0) {
                        KWApplication.instance.showRegDialog(requireContext())
                        return@OnBannerListener
                    }
                    if (StringUtil.equals(bannerBeans[position].type, "KY_GAS")) {
                        startActivity(Intent(context, GasStationListActivity::class.java))
                    } else if (StringUtil.equals(bannerBeans[position].type, "KY_WASH")) {
                        startActivity(Intent(context, CarWashListActivity::class.java))
                    } else {
                        if (!StringUtil.isEmpty(target)) {
                            val intent = Intent(context, WebViewActivity::class.java)
                            val sb = StringBuilder()
                            sb.append(target)
                            if (StringUtil.equals(bannerBeans[position].type, "KY_H5")) {
                                if (target.contains("?")) {
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
                })
            })
        mainViewModel!!.getCategoryList(requireContext()).observe(
            requireActivity(),
            Observer<MutableList<MutableList<CategoryBean>>?> { categoryBeans ->
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
                val categoryAdapter = CategoryRootAdapter(categoryBeans, object : ItemCallback {
                    override fun onItemCallback(position: Int, obj: Any?) {
                        val categoryBean = obj as CategoryBean
                        val userRole = KWApplication.instance.userRole
                        val isPublic = categoryBean.isPublic
                        if (userRole == -2 && isPublic == 0) {
                            KWApplication.instance.showRegDialog(requireContext())
                            return
                        }
                        val target = categoryBean.href
                        if (StringUtil.equals(categoryBean.type, "KY_GAS")) {
                            startActivity(Intent(context, GasStationListActivity::class.java))
                        } else if (StringUtil.equals(categoryBean.type, "KY_WASH")) {
                            startActivity(Intent(context, CarWashListActivity::class.java))
                        } else {
                            if (!StringUtil.isEmpty(target)) {
                                val intent = Intent(context, WebViewActivity::class.java)
                                val sb = StringBuilder()
                                sb.append(target)
                                //                                sb.append("https://www.ky808.cn/carfriend/static/alone/demo.html"); //测试视屏广告链接
                                if (StringUtil.equals(categoryBean.type, "KY_H5")) {
                                    if (target!!.contains("?")) {
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
                                //                                intent.putExtra("url", "http://192.168.3.32:8080/#/index");
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


        // 设置滚动辅助工具
//        PagerGridSnapHelper pageSnapHelper = new PagerGridSnapHelper();
//        pageSnapHelper.attachToRecyclerView(category_rv);
    }

    private fun loadChildData() {
        if (isFirstLoad) {
            for (x in mFragments.indices) {
                if (mFragments[x] is HomeGasStationFragment) {
                    val homeGasStationFragment = mFragments[x] as HomeGasStationFragment
                    homeGasStationFragment.reqData(
                        refreshLayout,
                        pageIndex,
                        isRefresh,
                        isLoadmore,
                        latitude,
                        longitude
                    )
                }
                if (mFragments[x] is HomeCarWashFragment) {
                    val homeCarWashFragment = mFragments[x] as HomeCarWashFragment
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
            }
            isFirstLoad = false
        } else {
            if (fragIndex == 0) {
                val homeGasStationFragment = mFragments[fragIndex] as HomeGasStationFragment
                homeGasStationFragment.reqData(
                    refreshLayout,
                    pageIndex,
                    isRefresh,
                    isLoadmore,
                    latitude,
                    longitude
                )
            } else if (fragIndex == 1) {
                val homeCarWashFragment = mFragments[fragIndex] as HomeCarWashFragment
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
        }
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