package com.kayu.business_car_owner.popupWindow

import android.app.Activity
import android.content.Context
import android.view.WindowManager

/**
 * <pre>
 * @author
 * blog  : https://github.com/yangchong211
 * time  : 2016/05/1
 * desc  : 弹窗工具类
 * revise:
</pre> *
 */
object DialogUtils {
    fun dip2px(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    /**
     * 设置页面的透明度
     * 主要作用于：弹窗时设置宿主Activity的背景色
     * @param bgAlpha 1表示不透明
     */
    fun setBackgroundAlpha(activity: Activity, bgAlpha: Float) {
        val lp = activity.window.attributes
        lp.alpha = bgAlpha
        val window = activity.window
        if (window != null) {
            if (bgAlpha == 1f) {
                //不移除该Flag的话,在有视频的页面上的视频会出现黑屏的bug
                window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            } else {
                //此行代码主要是解决在华为手机上半透明效果无效的bug
                window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            }
            window.attributes = lp
        }
    }
}