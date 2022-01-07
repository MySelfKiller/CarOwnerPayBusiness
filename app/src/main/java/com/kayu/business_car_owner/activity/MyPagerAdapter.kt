package com.kayu.business_car_owner.activity

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class MyPagerAdapter constructor(fm: FragmentManager?, private val list: List<Fragment>?) :
    FragmentPagerAdapter(
        (fm)!!
    ) {
    public override fun instantiateItem(container: ViewGroup, position: Int): Any {
        return super.instantiateItem(container, position)
    }

    public override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        super.destroyItem(container, position, `object`)
    }

    public override fun getCount(): Int {
        return if (list != null) list.size else 0
    }

    public override fun getPageTitle(position: Int): CharSequence? {
        return "完成状态" + position
    }

    public override fun getItem(position: Int): Fragment {
        return list!!.get(position)
    }
}