package com.kayu.business_car_owner.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kayu.utils.status_bar_set.StatusBarUtil
import androidx.fragment.app.Fragment
import com.kayu.business_car_owner.R

class ConsultFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        StatusBarUtil.setStatusBarColor(
            requireActivity(),
            resources.getColor(R.color.white)
        )
        return inflater.inflate(R.layout.fragment_consult, container, false)
    }
}