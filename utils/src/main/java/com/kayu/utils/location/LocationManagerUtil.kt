package com.kayu.utils.location

import android.content.Context
import android.location.LocationManager
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.amap.api.location.AMapLocationQualityReport
import kotlin.jvm.Synchronized
import com.google.zxing.MultiFormatReader
import com.google.zxing.BinaryBitmap
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.common.GlobalHistogramBinarizer
import java.lang.Exception

class LocationManagerUtil private constructor(private val context: Context) {
    var loccation: AMapLocation? = null
        private set
    private var locationClient: AMapLocationClient? = null
    private var locationOption: AMapLocationClientOption? = null

    /**
     * 手机是否开启位置服务，如果没有开启那么所有app将不能使用定位功能
     */
    val isLocServiceEnable: Boolean
        get() {
            val locationManager =
                context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            return if (gps || network) {
                true
            } else false
        }
    var listener: LocationCallback? = null
    fun setLocationListener(listener: LocationCallback?) {
        this.listener = listener
    }

    /**
     * 初始化定位
     *
     */
    private fun initLocation(context: Context) {
        //初始化client
        AMapLocationClient.updatePrivacyShow(context, true, true)
        AMapLocationClient.updatePrivacyAgree(context, true)
        try {
            locationClient = AMapLocationClient(context)
        } catch (e: Exception) {
        }
        locationOption = defaultOption
        //设置定位参数
        locationClient!!.setLocationOption(locationOption)
        // 设置定位监听
        locationClient!!.setLocationListener(locationListener)
    }//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
    //可选，设置是否gps优先，只在高精度模式下有效。默认关闭
    //可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
    //可选，设置定位间隔。默认为2秒
    //可选，设置是否返回逆地理地址信息。默认是true
    //可选，设置是否单次定位。默认是false
    //可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
    //可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
    //可选，设置是否使用传感器。默认是false
    //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
    //可选，设置是否使用缓存定位，默认为true
    //可选，设置逆地理信息的语言，默认值为默认语言（根据所在地区选择语言）
    /**
     * 默认的定位参数
     * @since 2.8.0
     * @author hongming.wang
     */
    private val defaultOption: AMapLocationClientOption
        private get() {
            val mOption = AMapLocationClientOption()
            mOption.locationPurpose = AMapLocationClientOption.AMapLocationPurpose.SignIn
            mOption.locationMode =
                AMapLocationClientOption.AMapLocationMode.Hight_Accuracy //可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
            mOption.isGpsFirst = true //可选，设置是否gps优先，只在高精度模式下有效。默认关闭
            mOption.httpTimeOut = 10000 //可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
            mOption.interval = 2000 //可选，设置定位间隔。默认为2秒
            mOption.isNeedAddress = true //可选，设置是否返回逆地理地址信息。默认是true
            mOption.isOnceLocation = false //可选，设置是否单次定位。默认是false
            mOption.isOnceLocationLatest =
                false //可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
            AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP) //可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
            mOption.isSensorEnable = false //可选，设置是否使用传感器。默认是false
            mOption.isWifiScan =
                true //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
            mOption.isLocationCacheEnable = false //可选，设置是否使用缓存定位，默认为true
            mOption.geoLanguage =
                AMapLocationClientOption.GeoLanguage.DEFAULT //可选，设置逆地理信息的语言，默认值为默认语言（根据所在地区选择语言）
            return mOption
        }

    /**
     * 定位监听
     */
    var locationListener: AMapLocationListener = object : AMapLocationListener {
        override fun onLocationChanged(location: AMapLocation) {
            if (location.errorCode == 0) {
                if (location.longitude > 1.0 && location.longitude > 1.0) {
                    loccation = location
                }
                if (null != listener) {
                    listener!!.onLocationChanged(location)
                }
                //                    sb.append("定位成功" + "\n");
//                    sb.append("定位类型: " + location.getLocationType() + "\n");
//                    sb.append("经    度    : " + location.getLongitude() + "\n");
//                    sb.append("纬    度    : " + location.getLatitude() + "\n");
//                    sb.append("精    度    : " + location.getAccuracy() + "米" + "\n");
//                    sb.append("提供者    : " + location.getProvider() + "\n");
//
//                    sb.append("速    度    : " + location.getSpeed() + "米/秒" + "\n");
//                    sb.append("角    度    : " + location.getBearing() + "\n");
//                    // 获取当前提供定位服务的卫星个数
//                    sb.append("星    数    : " + location.getSatellites() + "\n");
//                    sb.append("国    家    : " + location.getCountry() + "\n");
//                    sb.append("省            : " + location.getProvince() + "\n");
//                    sb.append("市            : " + location.getCity() + "\n");
//                    sb.append("城市编码 : " + location.getCityCode() + "\n");
//                    sb.append("区            : " + location.getDistrict() + "\n");
//                    sb.append("区域 码   : " + location.getAdCode() + "\n");
//                    sb.append("地    址    : " + location.getAddress() + "\n");
//                    sb.append("兴趣点    : " + location.getPoiName() + "\n");
//                    //定位完成的时间
//                    sb.append("定位时间: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(location.getTime()) + "\n");
            } else {
                //定位失败
//                    sb.append("定位失败" + "\n");
//                    sb.append("错误码:" + location.getErrorCode() + "\n");
//                    sb.append("错误信息:" + location.getErrorInfo() + "\n");
//                    sb.append("错误描述:" + location.getLocationDetail() + "\n");
            }
        }
    }

    /**
     * 获取GPS状态的字符串
     * @param statusCode GPS状态码
     * @return
     */
    private fun getGPSStatusString(statusCode: Int): String {
        var str = ""
        when (statusCode) {
            AMapLocationQualityReport.GPS_STATUS_OK -> str = "GPS状态正常"
            AMapLocationQualityReport.GPS_STATUS_NOGPSPROVIDER -> str =
                "手机中没有GPS Provider，无法进行GPS定位"
            AMapLocationQualityReport.GPS_STATUS_OFF -> str = "GPS关闭，建议开启GPS，提高定位质量"
            AMapLocationQualityReport.GPS_STATUS_MODE_SAVING -> str =
                "选择的定位模式中不包含GPS定位，建议选择包含GPS定位的模式，提高定位质量"
            AMapLocationQualityReport.GPS_STATUS_NOGPSPERMISSION -> str = "没有GPS定位权限，建议开启gps定位权限"
        }
        return str
    }

    /**
     * 开始定位
     *
     * @since 2.8.0
     * @author hongming.wang
     */
    fun startLocation() {
        // 设置定位参数
//    locationClient.setLocationOption(locationOption);
        // 启动定位
        if (!locationClient!!.isStarted) {
            locationClient!!.startLocation()
        }
    }

    /**
     * 重新开始定位
     *
     * @since 2.8.0
     * @author hongming.wang
     */
    fun reStartLocation() {
        // 设置定位参数
//    locationClient.setLocationOption(locationOption);
        // 启动定位
        if (locationClient!!.isStarted) {
            locationClient!!.stopLocation()
        }
        locationClient!!.startLocation()
    }

    /**
     * 停止定位
     *
     * @since 2.8.0
     * @author hongming.wang
     */
    fun stopLocation() {
        // 停止定位
        if (null != locationClient) locationClient!!.stopLocation()
    }

    /**
     * 销毁定位
     *
     * @since 2.8.0
     * @author hongming.wang
     */
    fun destroyLocation() {
        if (null != locationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            locationClient!!.onDestroy()
            //            locationClient = null;
//            locationOption = null;
        }
    }

    companion object {
        private var manager: LocationManagerUtil? = null
        @Synchronized
        fun init(context: Context?) {
            requireNotNull(context) { "Context must not be null." }
            if (null == manager) {
                manager = LocationManagerUtil(context)
            }
        }

        val self: LocationManagerUtil?
            get() {
                requireNotNull(manager) { "please init() before." }
                return manager
            }
    }

    init {
        initLocation(context)
    }
}