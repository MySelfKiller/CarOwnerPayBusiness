package com.kayu.business_car_owner.popupWindow

import android.app.Activity
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow

/**
 * <pre>
 * @author yangchong
 * blog  : https://github.com/yangchong211
 * time  : 2016/06/4
 * desc  : PopupWindow抽象类
 * revise:
</pre> *
 */
abstract class BasePopDialog(private val mContext: Context) : PopupWindow(
    mContext
) {
    private var contentView: View? = null
    private fun init() {
        contentView = LayoutInflater.from(mContext).inflate(viewResId, null)
        setContentView(contentView)
        width = ViewGroup.LayoutParams.WRAP_CONTENT
        height = ViewGroup.LayoutParams.WRAP_CONTENT
        setBackgroundDrawable(BitmapDrawable())
        /*ColorDrawable dw = new ColorDrawable(0xb0000000);
        // 设置弹出窗体的背景
        this.setBackgroundDrawable(dw);*/isOutsideTouchable = false
        isFocusable = true // 设置PopupWindow可获得焦点
        isTouchable = true // 设置PopupWindow可触摸
    }

    /**
     * 这两个抽象方法子类必须实现
     */
    abstract val viewResId: Int
    abstract fun initData(contentView: View?)

    /**
     * 设置屏幕透明度
     * @param bgAlpha               透明度
     */
    fun setBgAlpha(bgAlpha: Float) {
        DialogUtils.setBackgroundAlpha(mContext as Activity, bgAlpha)
    }

    /**
     * 设置延迟
     * @param time 毫秒
     */
    fun setDelayedMsDismiss(time: Long) {
        mHandler!!.postDelayed(delayedRun!!, time)
    }

    override fun dismiss() {
        super.dismiss()
        DialogUtils.setBackgroundAlpha(mContext as Activity, 1f)
        if (mHandler != null) {
            if (delayedRun != null) {
                mHandler!!.removeCallbacks(delayedRun)
            } else {
                mHandler!!.removeCallbacksAndMessages(null)
            }
            mHandler = null
        }
    }

    override fun getWidth(): Int {
        if (contentView == null) {
            return 0
        }
        contentView!!.measure(0, 0)
        return contentView!!.measuredWidth
    }

    override fun getHeight(): Int {
        if (contentView == null) {
            return 0
        }
        contentView!!.measure(0, 0)
        return contentView!!.measuredHeight
    }

    private val delayedRun: Runnable? = Runnable { dismiss() }
    private var mHandler: Handler? = Handler()

    init {
        init()
        initData(contentView)
    }
}