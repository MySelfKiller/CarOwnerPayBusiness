package com.kayu.business_car_owner.activity

import android.R
import android.app.Activity
import android.graphics.Rect
import android.view.View
import android.view.ViewTreeObserver
import android.widget.FrameLayout

class AndroidBug5497Workaround private constructor(activity: Activity) {
    private val mChildOfContent: View
    private var usableHeightPrevious: Int = 0
    private val frameLayoutParams: FrameLayout.LayoutParams
    private fun possiblyResizeChildOfContent() {
        val usableHeightNow = computeUsableHeight()
        if (usableHeightNow != usableHeightPrevious) {
            //如果两次高度不一致
            //将计算的可视高度设置成视图的高度
            frameLayoutParams.height = usableHeightNow
            mChildOfContent.requestLayout() //请求重新布局
            usableHeightPrevious = usableHeightNow
        }
    }

    private fun computeUsableHeight(): Int {
        val r = Rect()
        mChildOfContent.getWindowVisibleDisplayFrame(r)
        return r.bottom
    }

    companion object {
        // For more information, see https://code.google.com/p/android/issues/detail?id=5497
        // To use this class, simply invoke assistActivity() on an Activity that already has its content view set.
        fun assistActivity(activity: Activity) {
            AndroidBug5497Workaround(activity)
        }
    }

    init {
        val content: FrameLayout = activity.findViewById<View>(R.id.content) as FrameLayout
        mChildOfContent = content.getChildAt(0)
        mChildOfContent.getViewTreeObserver()
            .addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                public override fun onGlobalLayout() {
                    possiblyResizeChildOfContent()
                }
            })
        frameLayoutParams = mChildOfContent.getLayoutParams() as FrameLayout.LayoutParams
    }
}