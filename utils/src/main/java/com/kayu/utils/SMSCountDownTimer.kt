package com.kayu.utils

import android.os.CountDownTimer
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
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

class SMSCountDownTimer : CountDownTimer {
    private var mView: View

    /**
     * @param millisInFuture    The number of millis in the future from the call
     * to [.start] until the countdown is done and [.onFinish]
     * is called.
     * @param countDownInterval The interval along the way to receive
     * [.onTick] callbacks.
     */
    constructor(view: TextView, millisInFuture: Long, countDownInterval: Long) : super(
        millisInFuture,
        countDownInterval
    ) {
        mView = view
    }

    constructor(view: AppCompatButton, millisInFuture: Long, countDownInterval: Long) : super(
        millisInFuture,
        countDownInterval
    ) {
        mView = view
    }

    override fun onTick(millisUntilFinished: Long) {
//防止计时过程中重复点击
        mView.isClickable = false
        if (mView is TextView) {
            (mView as TextView).setText((millisUntilFinished / 1000).toString() + "秒")
        } else if (mView is AppCompatButton) {
            (mView as AppCompatButton).setText((millisUntilFinished / 1000).toString() + "秒")
        }
    }

    override fun onFinish() {
        //重新给Button设置文字
        if (mView is TextView) {
            (mView as TextView).text = "重新获取"
        } else if (mView is AppCompatButton) {
            (mView as AppCompatButton).text = "重新获取"
        }
        //设置可点击
        mView.isClickable = true
    }

    fun clear() {
        cancel()
        //重新给Button设置文字
        if (mView is TextView) {
            (mView as TextView).text = "重新获取"
        } else if (mView is AppCompatButton) {
            (mView as AppCompatButton).text = "重新获取"
        }
        //设置可点击
        mView.isClickable = true
    }
}