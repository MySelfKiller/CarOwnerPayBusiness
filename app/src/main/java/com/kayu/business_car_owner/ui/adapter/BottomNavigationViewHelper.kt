package com.kayu.business_car_owner.ui.adapter

import android.annotation.SuppressLint
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.LabelVisibilityMode
import java.lang.Exception

object BottomNavigationViewHelper {
    @SuppressLint("RestrictedApi")
    fun disableShiftMode(view: BottomNavigationView) {
        val menuView = view.getChildAt(0) as BottomNavigationMenuView
        try {
            menuView.labelVisibilityMode = LabelVisibilityMode.LABEL_VISIBILITY_LABELED
            for (i in 0 until menuView.childCount) {
                val item = menuView.getChildAt(i) as BottomNavigationItemView
                item.setShifting(false)
            }
        } catch (e: Exception) {
        }
    }
}