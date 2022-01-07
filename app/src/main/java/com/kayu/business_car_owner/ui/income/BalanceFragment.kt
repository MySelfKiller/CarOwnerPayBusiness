package com.kayu.business_car_owner.ui.income

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.flyco.tablayout.listener.CustomTabEntity
import androidx.viewpager.widget.ViewPager
import com.flyco.tablayout.CommonTabLayout
import com.kayu.utils.status_bar_set.StatusBarUtil
import com.kayu.utils.NoMoreClickListener
import com.flyco.tablayout.TabEntity
import com.flyco.tablayout.listener.OnTabSelectListener
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import androidx.viewpager.widget.PagerAdapter
import com.kayu.business_car_owner.activity.MyPagerAdapter
import androidx.fragment.app.Fragment
import com.kayu.business_car_owner.R
import com.kayu.utils.LogUtil
import java.util.ArrayList

class BalanceFragment : Fragment() {
    private val mTabEntities = ArrayList<CustomTabEntity>()
    private val mFragments = ArrayList<Fragment>()
    private var mViewPager: ViewPager? = null
    private var slidingTabLayout: CommonTabLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        StatusBarUtil.setStatusBarColor(requireActivity(), resources.getColor(R.color.white))
        return inflater.inflate(R.layout.fragment_balance, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //标题栏
        view.findViewById<View>(R.id.title_back_btu)
            .setOnClickListener(object : NoMoreClickListener() {
                override fun OnMoreClick(view: View) {
                    requireActivity().onBackPressed()
                }

                override fun OnMoreErrorClick() {}
            })
        val back_tv = view.findViewById<TextView>(R.id.title_back_tv)
        val title_name = view.findViewById<TextView>(R.id.title_name_tv)
        title_name.text = "明细"
        //        title_name.setVisibility(View.GONE);
        back_tv.text = "我的"
        slidingTabLayout = view.findViewById(R.id.detailed_list_sl)
        mViewPager = view.findViewById(R.id.detailed_view_pager)
        if (userVisibleHint && !mHasLoadedOnce) {
            initView()
            mHasLoadedOnce = true
        }
        isCreated = true
    }

    private var isCreated = false
    private var mHasLoadedOnce = false // 页面已经加载过
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        LogUtil.e("hm", "CustomerListBankFragment---------setUserVisibleHint====$isVisibleToUser")
        if (isVisibleToUser && !mHasLoadedOnce && isCreated) {
            initView()
            mHasLoadedOnce = true
        }
    }

    private fun initView() {
        mTabEntities.add(TabEntity("支出", R.mipmap.ic_bg_close, R.mipmap.ic_bg_close))
        mTabEntities.add(TabEntity("收入", R.mipmap.ic_bg_close, R.mipmap.ic_bg_close))
        slidingTabLayout!!.setTabData(mTabEntities)
        slidingTabLayout!!.setOnTabSelectListener(object : OnTabSelectListener {
            override fun onTabSelect(position: Int) {
                mViewPager!!.currentItem = position
            }

            override fun onTabReselect(position: Int) {}
        })
        mViewPager!!.offscreenPageLimit = 4
        mViewPager!!.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                slidingTabLayout!!.currentTab = position
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
        mFragments.add(DetailedIncomeFragment(0))
        mFragments.add(DetailedIncomeFragment(1))
        val adapter: PagerAdapter = MyPagerAdapter(childFragmentManager, mFragments)
        mViewPager!!.adapter = adapter
        mViewPager!!.currentItem = 0
        slidingTabLayout!!.currentTab = 0
    }

    override fun onDetach() {
        super.onDetach()
        //        StatusBarUtil.setStatusBarColor(getActivity(), getResources().getColor(R.color.startOrgColor_btn));
        LogUtil.e("StationFragment----", "----onDetach---")
    }
}