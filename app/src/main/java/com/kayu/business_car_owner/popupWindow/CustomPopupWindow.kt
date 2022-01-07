package com.kayu.business_car_owner.popupWindow

import android.app.Activity
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.PopupWindow
import androidx.annotation.RequiresApi

/**
 * <pre>
 * @author yangchong
 * blog  : https://github.com/yangchong211
 * time  : 2017/1/5
 * desc  : 自定义PopupWindow控件【学习Builder模式，可以借鉴AlertDialog.Builder模式】
 * revise:
</pre> *
 */
class CustomPopupWindow private constructor(context: Context) : PopupWindow.OnDismissListener {
    private val mContext: Context
    private var mResLayoutId: Int
    private var mContentView: View? = null
    private var mAnimationStyle: Int
    var width = 0
        private set
    var height = 0
        private set
    private var mIsFocusable: Boolean
    private var mIsOutside: Boolean
    private var mPopupWindow: PopupWindow? = null
    private var mClippEnable: Boolean
    private var mIgnoreCheekPress: Boolean
    private var mInputMode: Int
    private var mSoftInputMode: Int
    private var mTouchable: Boolean
    private var mIsBackgroundDark: Boolean
    private var mBackgroundDrakValue: Float
    private var mOnTouchListener: View.OnTouchListener? = null
    private var mOnDismissListener: PopupWindow.OnDismissListener? = null
    private var mWindow: Window? = null

    /**
     * 第一步，继承PopupWindow.OnDismissListener，实现该方法
     */
    override fun onDismiss() {
        dismiss()
    }

    val isShowing: Boolean
        get() = mPopupWindow!!.isShowing

    /**
     * 关闭对话框
     */
    fun dismiss() {
//        i```
        if (mWindow != null) {
            val params = mWindow!!.attributes
            params.alpha = 1.0f
            mWindow!!.attributes = params
        }
        if (mPopupWindow != null && mPopupWindow!!.isShowing) {
            mPopupWindow!!.dismiss()
        }
    }

    /**
     * 第二步：创建具体的Builder类
     */
    class PopupWindowBuilder(context: Context) {
        private val mCustomPopWindow: CustomPopupWindow

        /**第四步：创建create方法 */
        fun create(): CustomPopupWindow {
            mCustomPopWindow.build()
            return mCustomPopWindow
        }
        /**-----------------------第六步：以下是设置相关操作----------------------------- */
        /**
         * 设置布局
         * @param resLayoutId       资源文件
         */
        fun setView(resLayoutId: Int): PopupWindowBuilder {
            mCustomPopWindow.mResLayoutId = resLayoutId
            mCustomPopWindow.mContentView = null
            return this
        }

        /**
         * 设置布局
         * @param view              view
         */
        fun setView(view: View?): PopupWindowBuilder {
            mCustomPopWindow.mContentView = view
            mCustomPopWindow.mResLayoutId = -1
            return this
        }

        /**
         * 设置动画
         * @param animationStyle   资源文件
         */
        fun setAnimationStyle(animationStyle: Int): PopupWindowBuilder {
            mCustomPopWindow.mAnimationStyle = animationStyle
            return this
        }

        /**
         * 设置大小
         * @param width             宽
         * @param height            高
         */
        fun size(width: Int, height: Int): PopupWindowBuilder {
            mCustomPopWindow.width = width
            mCustomPopWindow.height = height
            return this
        }

        /**
         * 设置是否可以设置焦点
         * @param focusable         布尔
         */
        fun setFocusable(focusable: Boolean): PopupWindowBuilder {
            mCustomPopWindow.mIsFocusable = focusable
            return this
        }

        /**
         * 设置是否可以点击弹窗外部
         * @param outsideTouchable  布尔
         */
        fun setOutsideTouchable(outsideTouchable: Boolean): PopupWindowBuilder {
            mCustomPopWindow.mIsOutside = outsideTouchable
            return this
        }

        /**
         * 设置是否可以裁剪
         * @param enable            布尔
         */
        fun setClippingEnable(enable: Boolean): PopupWindowBuilder {
            mCustomPopWindow.mClippEnable = enable
            return this
        }

        /**
         * 设置是否忽略按下
         * @param ignoreCheekPress  布尔
         */
        fun setIgnoreCheekPress(ignoreCheekPress: Boolean): PopupWindowBuilder {
            mCustomPopWindow.mIgnoreCheekPress = ignoreCheekPress
            return this
        }

        /**
         * 设置类型
         * @param mode              类型
         */
        fun setInputMethodMode(mode: Int): PopupWindowBuilder {
            mCustomPopWindow.mInputMode = mode
            return this
        }

        /**
         * 设置弹窗关闭监听
         * @param onDismissListener listener
         */
        fun setOnDissmissListener(onDismissListener: PopupWindow.OnDismissListener?): PopupWindowBuilder {
            mCustomPopWindow.mOnDismissListener = onDismissListener
            return this
        }

        /**
         * 设置类型
         * @param softInputMode
         */
        fun setSoftInputMode(softInputMode: Int): PopupWindowBuilder {
            mCustomPopWindow.mSoftInputMode = softInputMode
            return this
        }

        /**
         * 设置是否可以触摸
         * @param touchable     布尔
         */
        fun setTouchable(touchable: Boolean): PopupWindowBuilder {
            mCustomPopWindow.mTouchable = touchable
            return this
        }

        /**
         * 设置触摸拦截
         * @param touchIntercepter  拦截器
         */
        fun setTouchIntercepter(touchIntercepter: View.OnTouchListener?): PopupWindowBuilder {
            mCustomPopWindow.mOnTouchListener = touchIntercepter
            return this
        }

        /**
         * 设置是否点击背景变暗
         * @param isDark        布尔
         */
        fun enableBackgroundDark(isDark: Boolean): PopupWindowBuilder {
            mCustomPopWindow.mIsBackgroundDark = isDark
            return this
        }

        /**
         * 设置背景
         * @param darkValue     值
         */
        fun setBgDarkAlpha(darkValue: Float): PopupWindowBuilder {
            mCustomPopWindow.mBackgroundDrakValue = darkValue
            return this
        }

        /**第三步：构造方法 */
        init {
            mCustomPopWindow = CustomPopupWindow(context)
        }
    }

    /**第四步：创建create方法，待实现 */
    private fun build(): PopupWindow? {
        //判断添加的view是否为null
        if (mContentView == null) {
            //如果view为null，则获取的资源布局文件
            mContentView = LayoutInflater.from(mContext).inflate(mResLayoutId, null as ViewGroup?)
        }

        //获取Activity对象
        val activity = mContentView!!.context as Activity
        if (activity != null && mIsBackgroundDark) {
            //设置背景透明度
            val alpha =
                if (mBackgroundDrakValue > 0.0f && mBackgroundDrakValue < 1.0f) mBackgroundDrakValue else 0.7f
            mWindow = activity.window
            val params = mWindow!!.getAttributes()
            params.alpha = alpha
            mWindow!!.setAttributes(params)
        }

        //设置宽高
        if (width != 0 && height != 0) {
            mPopupWindow = PopupWindow(mContentView, width, height)
        } else {
            mPopupWindow = PopupWindow(mContentView, -2, -2)
        }

        //设置动画
        if (mAnimationStyle != -1) {
            mPopupWindow!!.animationStyle = mAnimationStyle
        }
        this.apply(mPopupWindow)
        //设置是否捕获焦点，默认为true
        mPopupWindow!!.isFocusable = mIsFocusable
        //设置背景，默认是全透明
        mPopupWindow!!.setBackgroundDrawable(ColorDrawable(0))
        //设置是否可以点击外部，默认是true
        mPopupWindow!!.isOutsideTouchable = mIsOutside
        //        this.mPopupWindow.setTouchInterceptor(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return true;
//            }
//        });
        if (width == 0 || height == 0) {
            mPopupWindow!!.contentView.measure(0, 0)
            width = mPopupWindow!!.contentView.measuredWidth
            height = mPopupWindow!!.contentView.measuredHeight
        }

        //实现关闭监听
        mPopupWindow!!.setOnDismissListener(this)
        mPopupWindow!!.update()
        return mPopupWindow
    }

    private fun apply(mPopupWindow: PopupWindow?) {
        mPopupWindow!!.isClippingEnabled = mClippEnable
        if (mIgnoreCheekPress) {
            mPopupWindow.setIgnoreCheekPress()
        }
        if (mInputMode != -1) {
            mPopupWindow.inputMethodMode = mInputMode
        }
        if (mSoftInputMode != -1) {
            mPopupWindow.softInputMode = mSoftInputMode
        }
        if (mOnDismissListener != null) {
            mPopupWindow.setOnDismissListener(mOnDismissListener)
        }
        if (mOnTouchListener != null) {
            mPopupWindow.setTouchInterceptor(mOnTouchListener)
        }
        mPopupWindow.isTouchable = mTouchable
    }
    /**----------------设置弹窗显示，添加可以设置弹窗位置--------------------------------------------- */
    /**
     * 直接展示
     */
    fun showAsDropDown(anchor: View?): CustomPopupWindow {
        if (mPopupWindow != null) {
            mPopupWindow!!.showAsDropDown(anchor)
        }
        return this
    }

    /**
     * 传入x，y值位置展示
     */
    fun showAsDropDown(anchor: View?, xOff: Int, yOff: Int): CustomPopupWindow {
        if (mPopupWindow != null) {
            mPopupWindow!!.showAsDropDown(anchor, xOff, yOff)
        }
        return this
    }

    /**
     * 传入x，y值，和gravity位置展示。是相对坐标的gravity位置
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    fun showAsDropDown(anchor: View?, xOff: Int, yOff: Int, gravity: Int): CustomPopupWindow {
        if (mPopupWindow != null) {
            mPopupWindow!!.showAsDropDown(anchor, xOff, yOff, gravity)
        }
        return this
    }

    /**
     * 传入x，y值，和gravity位置展示。是相对gravity的相对位置
     */
    fun showAtLocation(parent: View?, gravity: Int, x: Int, y: Int): CustomPopupWindow {
        if (mPopupWindow != null && !mPopupWindow!!.isShowing) {
            mPopupWindow!!.showAtLocation(parent, gravity, x, y)
        }
        return this
    }

    /**
     * 第四步
     * 构造方法[私有化]
     * @param context       上下文
     */
    init {
        mResLayoutId = -1
        mAnimationStyle = -1
        mIsFocusable = true
        mIsOutside = true
        mClippEnable = true
        mIgnoreCheekPress = false
        mInputMode = -1
        mSoftInputMode = -1
        mTouchable = true
        mIsBackgroundDark = false
        mBackgroundDrakValue = 0.0f
        mContext = context
    }
}