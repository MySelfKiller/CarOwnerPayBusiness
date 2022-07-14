package com.kayu.utils.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import androidx.viewpager.widget.ViewPager
import java.util.HashMap
import java.util.LinkedHashMap

class AdaptiveHeightViewPager : ViewPager {
    private var current = 0
    private var mheight = 0

    /**
     * 保存position与对于的View
     */
    private val mChildrenViews: HashMap<Int, View> = LinkedHashMap()
    var isScrollble = true

    constructor(context: Context?) : super(context!!) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var heightMeasureSpec = heightMeasureSpec
        if (mChildrenViews.size > current) {
            val child = mChildrenViews[current]
            if (child != null) {
                child.measure(
                    widthMeasureSpec,
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
                )
                mheight = child.measuredHeight
            }
        }
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    fun resetHeight(current: Int) {
        this.current = current
        if (mChildrenViews.size > current) {
            var layoutParams = layoutParams as LinearLayout.LayoutParams
            if (layoutParams == null) {
                layoutParams =
                    LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, mheight)
            } else {
                layoutParams.height = mheight
            }
            setLayoutParams(layoutParams)
        }
    }

    /**
     * 保存position与对于的View
     */
    fun setObjectForPosition(view: View, position: Int) {
        mChildrenViews[position] = view
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        return if (!isScrollble) {
            true
        } else super.onTouchEvent(ev)
    }

    private var startX = 0
    private var startY = 0
    override fun onInterceptTouchEvent(arg0: MotionEvent): Boolean {
        if (!isScrollble) {
            when (arg0.action) {
                MotionEvent.ACTION_DOWN -> {
                    startX = arg0.x.toInt()
                    startY = arg0.y.toInt()
                }
                MotionEvent.ACTION_MOVE -> {

//                int dX = (int) (ev.getX() - startX);
                    val dY = (arg0.y - startX).toInt()
                    return if (Math.abs(dY) > 0) // 说明上下方向滑动了
                    {
                        false
                    } else {
                        true
                    }
                }
                MotionEvent.ACTION_UP -> {}
            }
        }
        return super.onInterceptTouchEvent(arg0)
    }
}