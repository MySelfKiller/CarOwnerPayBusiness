package com.kayu.business_car_owner.activity

import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.kayu.utils.NoMoreClickListener
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import androidx.viewpager.widget.ViewPager
import androidx.fragment.app.Fragment
import com.flyco.tablayout.listener.OnTabSelectListener
import com.kayu.business_car_owner.ui.OilOrderAllFragment
import androidx.viewpager.widget.PagerAdapter
import com.flyco.tablayout.SegmentTabLayout
import com.kayu.business_car_owner.*
import java.util.ArrayList

class OilOrderListActivity constructor() : BaseActivity() {
    private val mFragments: ArrayList<Fragment> = ArrayList()
    private var mViewPager: ViewPager? = null
    private var slidingTabLayout: SegmentTabLayout? = null

    //    public OilOrderListFragment() {
    //    }
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_oil_order_list)

        //标题栏
        findViewById<View>(R.id.title_back_btu).setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                onBackPressed()
            }

            override fun OnMoreErrorClick() {}
        })
        val back_tv: TextView = findViewById(R.id.title_back_tv)
        val title_name: TextView = findViewById(R.id.title_name_tv)
        title_name.setText("加油订单")
        //        title_name.setVisibility(View.GONE);
        back_tv.setText("我的")
        slidingTabLayout = findViewById(R.id.oil_order_list_ctl)
        mViewPager = findViewById(R.id.oil_order_list_vp)
        initView()
    }

    private fun initView() {
        val mTitles_3 = arrayOf("全部", "已支付","退款")
        slidingTabLayout!!.setTabData(mTitles_3)
        slidingTabLayout!!.setOnTabSelectListener(object : OnTabSelectListener {
            public override fun onTabSelect(position: Int) {
                mViewPager!!.setCurrentItem(position)
            }

            public override fun onTabReselect(position: Int) {}
        })
        mViewPager!!.setOffscreenPageLimit(4)
        mViewPager!!.addOnPageChangeListener(object : OnPageChangeListener {
            public override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            public override fun onPageSelected(position: Int) {
                slidingTabLayout!!.setCurrentTab(position)
            }

            public override fun onPageScrollStateChanged(state: Int) {}
        })
        mFragments.add(OilOrderAllFragment(-1))
        mFragments.add(OilOrderAllFragment(1))
        mFragments.add(OilOrderAllFragment(3))
        val adapter: PagerAdapter = MyPagerAdapter(getSupportFragmentManager(), mFragments)
        mViewPager!!.setAdapter(adapter)
        mViewPager!!.setCurrentItem(0)
        slidingTabLayout!!.setCurrentTab(0)
    } //    @Override
    //    public void onDetach() {
    //        super.onDetach();
    ////        StatusBarUtil.setStatusBarColor(getActivity(), getResources().getColor(R.color.startOrgColor_btn));
    //        LogUtil.e("StationFragment----", "----onDetach---");
    //    }
}