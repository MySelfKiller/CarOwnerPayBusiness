package com.kayu.business_car_owner.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.core.widget.NestedScrollView
import kotlin.Throws
import java.lang.Exception

class FadingScrollView : NestedScrollView {
    constructor(context: Context) : super(context) {}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
    }

    //渐变view
    private var fadingView: View? = null

    //滑动view的高度，如果这里fadingHeightView是一张图片，
    // 那么就是这张图片上滑至完全消失时action bar 完全显示，
    // 过程中透明度不断增加，直至完全显示
    private var fadingHeightView: View? = null
    private val oldY = 0

    //滑动距离，默认设置滑动500 时完全显示，根据实际需求自己设置
    private var fadingHeight = 500
    private var fadingViewHeight = 200
    fun setFadingView(view: View?) {
        fadingView = view
    }

    fun setFadingHeightView(v: View?) {
        fadingHeightView = v
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (fadingHeightView != null) fadingHeight = fadingHeightView!!.measuredHeight
        if (null != fadingView) fadingViewHeight = fadingView!!.measuredHeight
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        //        LogUtil.e(TAG,"top="+t+"oldTop="+oldt);
//        l,t  滑动后 xy位置，
//        oldl lodt 滑动前 xy 位置-----
        val fading =
            if (t > fadingViewHeight) fadingViewHeight.toFloat() else (if (t > 30) t else 0).toFloat()
        //        LogUtil.e(TAG,"fading="+fading);
//        if (t<fadingHeight-fadingViewHeight){
////            fadingView.setAlpha();
//            fadingView.setBackgroundColor(getResources().getColor(R.color.colorTransparent));
//        }
//        if (t>fadingHeight-fadingViewHeight){
//
//            fadingView.setBackgroundColor(getResources().getColor(R.color.black1));
//        }
        updateActionBarAlpha(fading / fadingViewHeight)
    }

    fun updateActionBarAlpha(alpha: Float) {
        try {
            setActionBarAlpha(alpha)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Throws(Exception::class)
    fun setActionBarAlpha(alpha: Float) {
        if (fadingView == null) {
            throw Exception("fadingView is null...")
        }
        fadingView!!.alpha = alpha
    }

    companion object {
        private const val TAG = "-----------FadingScrollView----------"
    }
}