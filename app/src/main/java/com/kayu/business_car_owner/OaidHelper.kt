package com.kayu.business_car_owner

import android.content.Context
import com.bun.miitmdid.core.ErrorCode
import com.bun.miitmdid.interfaces.IIdentifierListener
import com.bun.miitmdid.core.MdidSdkHelper
import com.bun.miitmdid.interfaces.IdSupplier
import com.kayu.utils.*
import java.lang.StringBuilder

class OaidHelper constructor(private val _listener: AppIdsUpdater) : IIdentifierListener {
    fun getDeviceIds(cxt: Context) {
        val timeb: Long = System.currentTimeMillis()
        // 方法调用
        val nres: Int = CallFromReflect(cxt)
        val timee: Long = System.currentTimeMillis()
        val offset: Long = timee - timeb
        if (nres == ErrorCode.INIT_ERROR_DEVICE_NOSUPPORT) { //不支持的设备
        } else if (nres == ErrorCode.INIT_ERROR_LOAD_CONFIGFILE) { //加载配置文件出错
        } else if (nres == ErrorCode.INIT_ERROR_MANUFACTURER_NOSUPPORT) { //不支持的设备厂商
        } else if (nres == ErrorCode.INIT_ERROR_RESULT_DELAY) { //获取接口是异步的，结果会在回调中返回，回调执行的回调可能在工作线程
        } else if (nres == ErrorCode.INIT_HELPER_CALL_ERROR) { //反射调用出错
        }
        LogUtil.e("oid", "return value: " + nres.toString())
    }

    /*
     * 方法调用
     *
     * */
    private fun CallFromReflect(cxt: Context): Int {
        return MdidSdkHelper.InitSdk(cxt, true, this)
    }

    /*
     * 获取相应id
     *
     * */
    public override fun OnSupport(isSupport: Boolean, _supplier: IdSupplier) {
        if (_supplier == null) {
            return
        }
        val oaid: String = _supplier.getOAID()
        val vaid: String = _supplier.getVAID()
        val aaid: String = _supplier.getAAID()
        val builder: StringBuilder = StringBuilder()
        builder.append("support: ").append(if (isSupport) "true" else "false").append("\n")
        builder.append("OAID: ").append(oaid).append("\n")
        builder.append("VAID: ").append(vaid).append("\n")
        builder.append("AAID: ").append(aaid).append("\n")
        val idstext: String = builder.toString()
        _listener.OnIdsAvalid(isSupport, oaid, vaid, aaid)
    }

    open interface AppIdsUpdater {
        fun OnIdsAvalid(isSupport: Boolean, oaid: String, vaid: String, aaid: String)
    }
}