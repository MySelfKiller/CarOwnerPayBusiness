package com.kayu.utils

import android.view.View
import java.util.*

abstract class NoMoreClickListener : View.OnClickListener {
    private var MIN_CLICK_DELAY_TIME = 1000 //多少秒点击一次 默认2.5秒
    private var lastClickTime: Long = 0

    constructor() {}

    /**
     * 设置多少秒之内
     *
     * @param time
     */
    constructor(time: Int) {
        MIN_CLICK_DELAY_TIME = time
    }

    override fun onClick(view: View) {
        val currentTime = Calendar.getInstance().timeInMillis
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime
            OnMoreClick(view)
        } else {
            OnMoreErrorClick()
        }
    }

    /**
     * 在N秒之内的 ==1 次点击回调次方法
     *
     * @param view
     */
    protected abstract fun OnMoreClick(view: View)

    /**
     * 在N秒之内的 >= 2次点击回调次方法
     */
    protected abstract fun OnMoreErrorClick()
}