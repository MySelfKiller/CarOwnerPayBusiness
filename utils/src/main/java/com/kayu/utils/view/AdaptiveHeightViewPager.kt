package com.kayu.utils.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import kotlin.jvm.JvmOverloads
import androidx.viewpager.widget.ViewPager
import com.kayu.utils.callback.ImageCallback
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.kayu.utils.location.LocationCallback
import com.amap.api.location.AMapLocationListener
import com.amap.api.location.AMapLocationQualityReport
import com.kayu.utils.location.LocationManagerUtil
import kotlin.jvm.Synchronized
import com.kayu.utils.location.CoordinateTransformUtil
import com.kayu.utils.permission.EasyPermissions
import com.kayu.utils.permission.EasyPermissions.PermissionWithDialogCallbacks
import com.kayu.utils.permission.EasyPermissions.DialogCallback
import com.kayu.utils.permission.EasyPermissions.PermissionCallbacks
import com.kayu.utils.permission.AfterPermissionGranted
import com.kayu.utils.AppUtil
import kotlin.Throws
import com.kayu.utils.IMEIUtil
import com.kayu.utils.status_bar_set.OSUtils
import com.kayu.utils.status_bar_set.StatusBarUtil
import com.kayu.utils.status_bar_set.SystemBarTintManager
import com.kayu.utils.status_bar_set.StatusBarUtil.ViewType
import com.kayu.utils.status_bar_set.SystemBarTintManager.SystemBarConfig
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kayu.utils.GsonHelper
import com.google.gson.GsonBuilder
import com.google.zxing.EncodeHintType
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.BarcodeFormat
import com.kayu.utils.QRCodeUtil
import com.google.zxing.WriterException
import com.kayu.utils.DoubleUtils
import com.kayu.utils.ScreenUtils
import com.kayu.utils.DesCoderUtil
import com.kayu.utils.DisplayUtils
import com.kayu.utils.GetJuLiUtils
import com.kayu.utils.DeviceIdUtils
import com.google.zxing.DecodeHintType
import com.kayu.utils.QRCodeParseUtils
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.MultiFormatReader
import com.google.zxing.BinaryBitmap
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.common.GlobalHistogramBinarizer
import java.util.HashMap
import java.util.LinkedHashMap

class AdaptiveHeightViewPager : ViewPager {
    private var current = 0
    private var height = 0

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
                height = child.measuredHeight
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
                    LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, height)
            } else {
                layoutParams.height = height
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