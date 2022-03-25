package com.kayu.business_car_owner.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import android.os.Message
import com.hjq.toast.ToastUtils
import com.kayu.business_car_owner.http.parser.UserDataParse
import com.kayu.business_car_owner.http.parser.WebDataParse
import com.kayu.business_car_owner.http.parser.NormalStringParse
import com.kayu.business_car_owner.http.parser.NormalIntParse
import com.kayu.business_car_owner.http.parser.NormalStringListParse
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kayu.business_car_owner.data_parser.*
import com.kayu.business_car_owner.http.*
import com.kayu.business_car_owner.model.*
import com.kayu.utils.*
import java.util.HashMap

class MainViewModel : ViewModel() {
    private var bannerListData //横幅数据
            : MutableLiveData<MutableList<BannerBean>?>? = null
    private var stationListData //加油站列表数据
            : MutableLiveData<MutableList<OilStationBean>?>? = null
    private var washStationListData //洗车站列表数据
            : MutableLiveData<MutableList<WashStationBean>?>? = null
    private var paramOilData //加油站筛选参数
            : MutableLiveData<ParamOilBean?>? = null
    private var paramWashData //洗车站筛选参数
            : MutableLiveData<ParamWashBean?>? = null
    private var oilStationData //加油站详情数据
            : MutableLiveData<OilStationBean?>? = null
    private var payUrlData //加油站H5支付信息
            : MutableLiveData<WebBean?>? = null
    private var userLiveData //用户信息
            : MutableLiveData<UserBean?>? = null

    fun getUserInfo(context: Context): LiveData<UserBean?> {
        userLiveData = MutableLiveData()
        loadUserData(context)
        return userLiveData!!
    }

    @SuppressLint("HandlerLeak")
    private fun loadUserData(context: Context) {
        val reques = RequestInfo()
        reques.context = context
        reques.reqUrl = HttpConfig.HOST + HttpConfig.INTERFACE_USER_INFO
        reques.parser = UserDataParse()
        reques.handler = object : Handler() {
            public override fun handleMessage(msg: Message) {
                val resInfo: ResponseInfo = msg.obj as ResponseInfo
                var userBean: UserBean? = null
                if (resInfo.status == 1) {
                    userBean = resInfo.responseData as UserBean?
                    if (null != userBean) {
                        val sp: SharedPreferences = context.getSharedPreferences(
                            Constants.SharedPreferences_name,
                            Context.MODE_PRIVATE
                        )
                        val editor: SharedPreferences.Editor = sp.edit()
                        editor.putBoolean(Constants.isLogin, true)
                        editor.putString(Constants.userInfo, GsonHelper.toJsonString(userBean))
                        editor.apply()
                        editor.commit()
                    }
                } else {
                    ToastUtils.show(resInfo.msg)
                }
                userLiveData!!.value = userBean
                super.handleMessage(msg)
            }
        }
        val callback = ResponseCallback(reques)
        ReqUtil.setReqInfo(reques)
        ReqUtil.requestGetJSON(callback)
    }

    fun getPayUrl(
        context: Context,
        id: String,
        gunNo: Int,
        oilNo: Int,
        latitude: Double,
        longitude: Double
    ): LiveData<WebBean?> {
//        if (null == payUrlData)
        payUrlData = MutableLiveData()
        loadPayInfo(context, id, gunNo, oilNo, latitude, longitude)
        return payUrlData!!
    }

    @SuppressLint("HandlerLeak")
    private fun loadPayInfo(
        context: Context,
        id: String,
        gunNo: Int,
        oilNo: Int,
        latitude: Double,
        longitude: Double
    ) {
        val request: RequestInfo = RequestInfo()
        request.context = context
        request.reqUrl = HttpConfig.HOST + HttpConfig.INTERFACE_GAS_PAY
        val dataMap: HashMap<String, Any> = HashMap()
        dataMap.put("gasId", id)
        if (gunNo != -1) {
            dataMap.put("gunNo", gunNo)
        }
        dataMap.put("oilNo", oilNo)
        dataMap.put("latitude", latitude)
        dataMap.put("longitude", longitude)
        request.reqDataMap = dataMap
        request.parser = WebDataParse()
        request.handler = object : Handler() {
            public override fun handleMessage(msg: Message) {
                val response: ResponseInfo = msg.obj as ResponseInfo
                var payUrl: WebBean? = null
                if (response.status == 1) {
                    payUrl = response.responseData as WebBean?
                } else {
                    ToastUtils.show(response.msg)
                }
                payUrlData!!.setValue(payUrl)
                super.handleMessage(msg)
            }
        }
        ReqUtil.setReqInfo(request)
        ReqUtil.requestPostJSON(ResponseCallback(request))
    }

    private var reminderLiveData: MutableLiveData<String?>? = null
    fun getReminder(context: Context, city: String): LiveData<String?> {
//        if (null == parameterLiveData)
        reminderLiveData = MutableLiveData()
        loadReminder(context, city)
        return reminderLiveData!!
    }

    @SuppressLint("HandlerLeak")
    private fun loadReminder(context: Context, city: String) {
        val request: RequestInfo = RequestInfo()
        request.context = context
        request.reqUrl = HttpConfig.HOST + HttpConfig.INTERFACE_GET_ACCOUNT_REMINDER
        val dataMap: HashMap<String, Any> = HashMap()
        dataMap.put("content", city)
        request.reqDataMap = dataMap
        request.parser = NormalStringParse()
        request.handler = object : Handler() {
            public override fun handleMessage(msg: Message) {
                val response: ResponseInfo = msg.obj as ResponseInfo
                var parameter: String? = null
                if (response.status == 1) {
                    parameter = response.responseData as String?
                } else {
                    ToastUtils.show(response.msg)
                }
                reminderLiveData!!.setValue(parameter)
                super.handleMessage(msg)
            }
        }
        ReqUtil.setReqInfo(request)
        ReqUtil.requestPostJSON(ResponseCallback(request))
    }

    private var parameterLiveData: MutableLiveData<SystemParam?>? = null
    private var userTipLiveData: MutableLiveData<SystemParam?>? = null
    private var regDialogTipLiveData: MutableLiveData<SystemParam?>? = null
    private var activityHomeLiveData: MutableLiveData<SystemParam?>? = null
    private var activitySettingLiveData: MutableLiveData<SystemParam?>? = null
    fun getHomeActivity(context: Context): LiveData<SystemParam?> {
//        if (null == parameterLiveData)
        activityHomeLiveData = MutableLiveData()
        loadSysParameter(context, 38)
        return activityHomeLiveData!!
    }

    fun getSettingActivity(context: Context): LiveData<SystemParam?> {
//        if (null == parameterLiveData)
        activitySettingLiveData = MutableLiveData()
        loadSysParameter(context, 39)
        return activitySettingLiveData!!
    }

    fun getRegDialogTip(context: Context): LiveData<SystemParam?> {
//        if (null == parameterLiveData)
        regDialogTipLiveData = MutableLiveData()
        loadSysParameter(context, 34)
        return regDialogTipLiveData!!
    }

    fun getUserTips(context: Context): LiveData<SystemParam?> {
//        if (null == parameterLiveData)
        userTipLiveData = MutableLiveData()
        loadSysParameter(context, 30)
        return userTipLiveData!!
    }

    private var userRoleLiveData: MutableLiveData<Int?>? = null

    //身份 -2：游客、0:普通用户、1:会员用户、2:经销商(团长)、3:运营商
    fun getUserRole(context: Context): LiveData<Int?> {
//        if (null == userRoleLiveData)
        userRoleLiveData = MutableLiveData()
        loadUserRole(context)
        return userRoleLiveData!!
    }

    @SuppressLint("HandlerLeak")
    private fun loadUserRole(context: Context) {
        val request: RequestInfo = RequestInfo()
        request.context = context
        request.reqUrl = HttpConfig.HOST + HttpConfig.INTERFACE_GET_USER_ROLE
        request.parser = NormalIntParse()
        request.handler = object : Handler() {
            public override fun handleMessage(msg: Message) {
                val response: ResponseInfo = msg.obj as ResponseInfo
                if (response.status == 1) {
                    //身份 -2：游客、0:普通用户、1:会员用户、2:经销商(团长)、3:运营商
                    userRoleLiveData!!.setValue(response.responseData as Int?)
                } else {
                    ToastUtils.show(response.msg)
                }
                super.handleMessage(msg)
            }
        }
        ReqUtil.setReqInfo(request)
        ReqUtil.requestGetJSON(ResponseCallback(request))
    }

    fun getParameter(context: Context, type: Int): LiveData<SystemParam?> {
//        if (null == parameterLiveData)
        parameterLiveData = MutableLiveData()
        loadParameter(context, type)
        return parameterLiveData!!
    }

    fun getSysParameter(context: Context, type: Int): LiveData<SystemParam?> {
//        if (null == parameterLiveData)
        parameterLiveData = MutableLiveData()
        loadSysParameter(context, type)
        return parameterLiveData!!
    }

    @SuppressLint("HandlerLeak")
    private fun loadParameter(context: Context, type: Int) {
        val request = RequestInfo()
        request.context = context
        request.reqUrl = HttpConfig.HOST + HttpConfig.INTERFACE_GET_PARAMETER
        val dataMap: HashMap<String, Any> = HashMap()
        dataMap.put("type", type)
        request.reqDataMap = dataMap
        request.parser = ParameterDataParser()
        request.handler = object : Handler() {
            public override fun handleMessage(msg: Message) {
                val response: ResponseInfo = msg.obj as ResponseInfo
                var systemParam: SystemParam? = null
                if (response.status == 1) {
                    systemParam = response.responseData as SystemParam?
                } else {
                    ToastUtils.show(response.msg)
                }
                if (type == 30) {
                    userTipLiveData!!.setValue(systemParam)
                } else if (type == 34) {
                    regDialogTipLiveData!!.setValue(systemParam)
                } else if (type == 38) {
                    activityHomeLiveData!!.setValue(systemParam)
                } else if (type == 39) {
                    activitySettingLiveData!!.setValue(systemParam)
                } else {
                    parameterLiveData!!.setValue(systemParam)
                }
                super.handleMessage(msg)
            }
        }
        ReqUtil.setReqInfo(request)
        ReqUtil.requestPostJSON(ResponseCallback(request))
    }

    @SuppressLint("HandlerLeak")
    private fun loadSysParameter(context: Context, type: Int) {
        val request = RequestInfo()
        request.context = context
        request.reqUrl = HttpConfig.HOST + HttpConfig.INTERFACE_GET_SYS_PARAMETER
        val dataMap: HashMap<String, Any> = HashMap()
        dataMap.put("", type)
        request.reqDataMap = dataMap
        request.parser = ParameterDataParser()
        request.handler = object : Handler() {
            public override fun handleMessage(msg: Message) {
                val response: ResponseInfo = msg.obj as ResponseInfo
                var systemParam: SystemParam? = null
                if (response.status == 1) {
                    systemParam = response.responseData as SystemParam?
                } else {
                    ToastUtils.show(response.msg)
                }
                if (type == 30) {
                    userTipLiveData!!.setValue(systemParam)
                } else if (type == 34) {
                    regDialogTipLiveData!!.setValue(systemParam)
                } else if (type == 38) {
                    activityHomeLiveData!!.setValue(systemParam)
                } else if (type == 39) {
                    activitySettingLiveData!!.setValue(systemParam)
                } else {
                    parameterLiveData!!.setValue(systemParam)
                }
                super.handleMessage(msg)
            }
        }
        ReqUtil.setReqInfo(request)
        ReqUtil.requestGetJSON(ResponseCallback(request))
    }

    fun getCustomer(context: Context): LiveData<SystemParam?> {
//        if (null == parameterLiveData)
        parameterLiveData = MutableLiveData()
        loadCustomer(context)
        return parameterLiveData!!
    }

    @SuppressLint("HandlerLeak")
    private fun loadCustomer(context: Context) {
        val request: RequestInfo = RequestInfo()
        request.context = context
        request.reqUrl = HttpConfig.HOST + HttpConfig.INTERFACE_WECHAT
        request.parser = ParameterDataParser()
        request.handler = object : Handler() {
            public override fun handleMessage(msg: Message) {
                val response: ResponseInfo = msg.obj as ResponseInfo
                var systemParam: SystemParam? = null
                if (response.status == 1) {
                    systemParam = response.responseData as SystemParam?
                } else {
                    ToastUtils.show(response.msg)
                }
                parameterLiveData!!.setValue(systemParam)
                super.handleMessage(msg)
            }
        }
        ReqUtil.setReqInfo(request)
        ReqUtil.requestPostJSON(ResponseCallback(request))
    }

    fun getOilStationDetail(context: Context, gasId: String): LiveData<OilStationBean?> {
//        if (null == oilStationData) {
//        }
        oilStationData = MutableLiveData()
        if (StringUtil.isEmpty(gasId)) {
            return oilStationData!!
        }
        loadOilStationDetail(context, gasId)
        return oilStationData!!
    }

    @SuppressLint("HandlerLeak")
    private fun loadOilStationDetail(context: Context, gasId: String) {
        val request = RequestInfo()
        request.context = context
        request.reqUrl = HttpConfig.HOST + HttpConfig.INTERFACE_STATION_DETAIL
        val dataMap: HashMap<String, Any> = HashMap()
        dataMap.put("", gasId)
        request.reqDataMap = dataMap
        request.parser = StationDetailDataParser()
        request.handler = object : Handler() {
            public override fun handleMessage(msg: Message) {
                val response: ResponseInfo = msg.obj as ResponseInfo
                var stationBean: OilStationBean? = null
                if (response.status == 1) {
                    stationBean = response.responseData as OilStationBean?
                } else {
                    ToastUtils.show(response.msg)
                }
                oilStationData!!.setValue(stationBean)
                super.handleMessage(msg)
            }
        }
        ReqUtil.setReqInfo(request)
        ReqUtil.requestGetJSON(ResponseCallback(request))
    }

    private var refundInfoData: MutableLiveData<RefundInfo?>? = null
    fun getRefundInfo(context: Context, orderId: Long?): LiveData<RefundInfo?> {
        if (null == refundInfoData) {
            refundInfoData = MutableLiveData()
        }
        if (null == orderId) {
            return refundInfoData!!
        }
        loadRefundInfo(context, orderId)
        return refundInfoData!!
    }

    @SuppressLint("HandlerLeak")
    private fun loadRefundInfo(context: Context, orderId: Long) {
        val request: RequestInfo = RequestInfo()
        request.context = context
        request.reqUrl = HttpConfig.HOST + HttpConfig.INTERFACE_WASH_ORDER_REFUND_INFO
        val dataMap: HashMap<String, Any> = HashMap()
        dataMap.put("", orderId)
        request.reqDataMap = dataMap
        request.parser = RefundInfoDataParser()
        request.handler = object : Handler() {
            public override fun handleMessage(msg: Message) {
                val response: ResponseInfo = msg.obj as ResponseInfo
                var stationBean: RefundInfo? = null
                if (response.status == 1) {
                    stationBean = response.responseData as RefundInfo?
                } else {
                    ToastUtils.show(response.msg)
                }
                refundInfoData!!.setValue(stationBean)
                super.handleMessage(msg)
            }
        }
        ReqUtil.setReqInfo(request)
        ReqUtil.requestGetJSON(ResponseCallback(request))
    }

    @SuppressLint("HandlerLeak")
    fun sendRefund(
        context: Context,
        orderId: Long,
        way: Int,
        reason: String,
        itemCallback: ItemCallback
    ) {
        val reques: RequestInfo = RequestInfo()
        reques.context = context
        reques.reqUrl = HttpConfig.HOST + HttpConfig.INTERFACE_WASH_ORDER_REFUND
        val reqDateMap: HashMap<String, Any> = HashMap()
        reqDateMap.put("id", orderId)
        reqDateMap.put("way", way)
        reqDateMap.put("reason", reason)
        reques.reqDataMap = reqDateMap
        reques.parser = NormalIntParse()
        reques.handler = object : Handler() {
            public override fun handleMessage(msg: Message) {
                itemCallback.onItemCallback(0, msg.obj)
                super.handleMessage(msg)
            }
        }
        val callback: ResponseCallback = ResponseCallback(reques)
        ReqUtil.setReqInfo(reques)
        ReqUtil.requestPostJSON(callback)
    }

    private var washOrderDetailData: MutableLiveData<WashOrderDetailBean?>? = null
    fun getWashOrderDetail(context: Context, orderId: Long?): LiveData<WashOrderDetailBean?> {
        if (null == washOrderDetailData) {
            washOrderDetailData = MutableLiveData()
        }
        if (null == orderId) {
            return washOrderDetailData!!
        }
        loadWashOrderDetail(context, orderId)
        return washOrderDetailData!!
    }

    @SuppressLint("HandlerLeak")
    private fun loadWashOrderDetail(context: Context, orderId: Long) {
        val request: RequestInfo = RequestInfo()
        request.context = context
        request.reqUrl = HttpConfig.HOST + HttpConfig.INTERFACE_WASH_ORDER_DETAIL
        val dataMap: HashMap<String, Any> = HashMap()
        dataMap.put("", orderId)
        request.reqDataMap = dataMap
        request.parser = WashOrderDetailDataParser()
        request.handler = object : Handler() {
            public override fun handleMessage(msg: Message) {
                val response: ResponseInfo = msg.obj as ResponseInfo
                var stationBean: WashOrderDetailBean? = null
                if (response.status == 1) {
                    stationBean = response.responseData as WashOrderDetailBean?
                } else {
                    ToastUtils.show(response.msg)
                }
                washOrderDetailData!!.setValue(stationBean)
                super.handleMessage(msg)
            }
        }
        ReqUtil.setReqInfo(request)
        ReqUtil.requestGetJSON(ResponseCallback(request))
    }

    private var washStoreData: MutableLiveData<WashStationDetailBean?>? = null
    fun getWashStoreDetail(context: Context, shopCode: String): LiveData<WashStationDetailBean?> {
        if (null == washStoreData) {
            washStoreData = MutableLiveData()
        }
        if (StringUtil.isEmpty(shopCode)) {
            return washStoreData!!
        }
        loadWashStoreDetail(context, shopCode)
        return washStoreData!!
    }

    @SuppressLint("HandlerLeak")
    private fun loadWashStoreDetail(context: Context, shopCode: String) {
        val request: RequestInfo = RequestInfo()
        request.context = context
        request.reqUrl = HttpConfig.HOST + HttpConfig.INTERFACE_WASH_STATION_DETAIL
        val dataMap: HashMap<String, Any> = HashMap()
        dataMap.put("", shopCode)
        request.reqDataMap = dataMap
        request.parser = WashStationDetailDataParser()
        request.handler = object : Handler() {
            public override fun handleMessage(msg: Message) {
                val response: ResponseInfo = msg.obj as ResponseInfo
                var stationBean: WashStationDetailBean? = null
                if (response.status == 1) {
                    stationBean = response.responseData as WashStationDetailBean?
                } else {
                    ToastUtils.show(response.msg)
                }
                washStoreData!!.setValue(stationBean)
                super.handleMessage(msg)
            }
        }
        ReqUtil.setReqInfo(request)
        ReqUtil.requestGetJSON(ResponseCallback(request))
    }

    fun getParamSelect(context: Context): LiveData<ParamOilBean?> {
        if (null == paramOilData) {
            paramOilData = MutableLiveData()
            loadParamSelect(context)
        }
        return paramOilData!!
    }

    @SuppressLint("HandlerLeak")
    private fun loadParamSelect(context: Context) {
        val requestInfo: RequestInfo = RequestInfo()
        requestInfo.context = context
        requestInfo.reqUrl = HttpConfig.HOST + HttpConfig.INTERFACE_GET_FILTER
        requestInfo.parser = ParamOilDataParser()
        requestInfo.handler = object : Handler() {
            public override fun handleMessage(msg: Message) {
                val response: ResponseInfo = msg.obj as ResponseInfo
                var stationBeans: ParamOilBean? = null
                if (response.status == 1) {
                    stationBeans = response.responseData as ParamOilBean?
                } else {
                    ToastUtils.show(response.msg)
                }
                paramOilData!!.setValue(stationBeans)
                super.handleMessage(msg)
            }
        }
        ReqUtil.setReqInfo(requestInfo)
        ReqUtil.requestGetJSON(ResponseCallback(requestInfo))
    }

    fun getParamWash(context: Context): LiveData<ParamWashBean?> {
        if (null == paramWashData) {
            paramWashData = MutableLiveData()
            loadParamWash(context)
        }
        return paramWashData!!
    }

    @SuppressLint("HandlerLeak")
    private fun loadParamWash(context: Context) {
        val requestInfo = RequestInfo()
        requestInfo.context = context
        requestInfo.reqUrl = HttpConfig.HOST + HttpConfig.INTERFACE_GET_WASH_FILTER
        requestInfo.parser = ParamWashDataParser()
        requestInfo.handler = object : Handler() {
            override fun handleMessage(msg: Message) {
                val response: ResponseInfo = msg.obj as ResponseInfo
                var stationBeans: ParamWashBean? = null
                if (response.status == 1) {
                    stationBeans = response.responseData as ParamWashBean?
                } else {
                    ToastUtils.show(response.msg)
                }
                paramWashData!!.setValue(stationBeans)
                super.handleMessage(msg)
            }
        }
        ReqUtil.setReqInfo(requestInfo)
        ReqUtil.requestGetJSON(ResponseCallback(requestInfo))
    }

    fun getStationList(
        context: Context,
        dataMap: HashMap<String, Any>
    ): LiveData<MutableList<OilStationBean>?> {
//        if (null == stationListData) {
//        }
        stationListData = MutableLiveData()
        loadStationList(context, dataMap)
        return stationListData!!
    }

    @SuppressLint("HandlerLeak")
    private fun loadStationList(context: Context, dataMap: HashMap<String, Any>) {
        val request = RequestInfo()
        request.context = context
        request.reqUrl = HttpConfig.HOST + HttpConfig.INTERFACE_STATION_LIST
        request.reqDataMap = dataMap
        request.parser = StationListDataParser()
        request.handler = object : Handler() {
            public override fun handleMessage(msg: Message) {
                val response: ResponseInfo = msg.obj as ResponseInfo
                var stationBeans: MutableList<OilStationBean>? = null
                if (response.status == 1) {
                    stationBeans = response.responseData as MutableList<OilStationBean>?
                } else {
                    ToastUtils.show(response.msg)
                }
                stationListData!!.postValue(stationBeans)
                super.handleMessage(msg)
            }
        }
        ReqUtil.setReqInfo(request)
        ReqUtil.requestPostJSON(ResponseCallback(request))
    }

    fun getWashStationList(
        context: Context,
        dataMap: HashMap<String, Any>
    ): LiveData<MutableList<WashStationBean>?> {
//        if (null == washStationListData) {
//        }
        washStationListData = MutableLiveData()
        loadWashStationList(context, dataMap)
        return washStationListData!!
    }

    @SuppressLint("HandlerLeak")
    private fun loadWashStationList(context: Context, dataMap: HashMap<String, Any>) {
        val request: RequestInfo = RequestInfo()
        request.context = context
        request.reqUrl = HttpConfig.HOST + HttpConfig.INTERFACE_WASH_STATION_LIST
        request.reqDataMap = dataMap
        request.parser = WashStationListDataParser()
        request.handler = object : Handler() {
            public override fun handleMessage(msg: Message) {
                val response: ResponseInfo = msg.obj as ResponseInfo
                var stationBeans: MutableList<WashStationBean>? = null
                if (response.status == 1) {
                    stationBeans = response.responseData as MutableList<WashStationBean>?
                } else {
                    ToastUtils.show(response.msg)
                }
                washStationListData!!.postValue(stationBeans)
                super.handleMessage(msg)
            }
        }
        ReqUtil.setReqInfo(request)
        ReqUtil.requestPostJSON(ResponseCallback(request))
    }

    /**
     * 获取banner数据
     * @return
     */
    fun getBannerList(mContext: Context): LiveData<MutableList<BannerBean>?> {
//        if (null == bannerListData) {
//        }
        bannerListData = MutableLiveData()
        loadBanners(mContext)
        return bannerListData!!
    }

    @SuppressLint("HandlerLeak")
    private fun loadBanners(mContext: Context) {
        val reques: RequestInfo = RequestInfo()
        reques.context = mContext
        reques.reqUrl = HttpConfig.HOST + HttpConfig.INTERFACE_GET_BANNER
        reques.parser = BannerDataParse()
        reques.handler = object : Handler() {
            public override fun handleMessage(msg: Message) {
                val resInfo: ResponseInfo = msg.obj as ResponseInfo
                var myTeamData: MutableList<BannerBean>? = null
                if (resInfo.status == 1) {
                    myTeamData = resInfo.responseData as MutableList<BannerBean>?
                } else {
                    ToastUtils.show(resInfo.msg)
                }
                bannerListData!!.value = myTeamData
                super.handleMessage(msg)
            }
        }
        val callback: ResponseCallback = ResponseCallback(reques)
        ReqUtil.setReqInfo(reques)
        ReqUtil.requestGetJSON(callback)
    }

    private var popNaviListData //热门导航数据
            : MutableLiveData<MutableList<PopNaviBean>?>? = null
    /**
     * 获取热门导航列表数据
     * @return
     */
    fun getPopNaviList(mContext: Context): LiveData<MutableList<PopNaviBean>?> {
        popNaviListData = MutableLiveData()
        loadPopNaviList(mContext)
        return popNaviListData!!
    }

    @SuppressLint("HandlerLeak")
    private fun loadPopNaviList(mContext: Context) {
        val reques = RequestInfo()
        reques.context = mContext
        reques.reqUrl = HttpConfig.HOST + HttpConfig.INTERFACE_GET_POP_NAVI
        reques.parser = PopNaviDataParse()
        reques.handler = object : Handler() {
            public override fun handleMessage(msg: Message) {
                val resInfo: ResponseInfo = msg.obj as ResponseInfo
                var myTeamData: MutableList<PopNaviBean>? = null
                if (resInfo.status == 1) {
                    myTeamData = resInfo.responseData as MutableList<PopNaviBean>?
                } else {
                    ToastUtils.show(resInfo.msg)
                }
                popNaviListData!!.value = myTeamData
                super.handleMessage(msg)
            }
        }
        val callback = ResponseCallback(reques)
        ReqUtil.setReqInfo(reques)
        ReqUtil.requestGetJSON(callback)
    }


    private var productSortListData //热门导航数据
            : MutableLiveData<MutableList<ProductSortBean>?>? = null
    /**
     * 获取热门导航列表数据
     * @return
     */
    fun getProductSortList(mContext: Context): LiveData<MutableList<ProductSortBean>?> {
        productSortListData = MutableLiveData()
        loadProductSortList(mContext)
        return productSortListData!!
    }

    @SuppressLint("HandlerLeak")
    private fun loadProductSortList(mContext: Context) {
        val reques = RequestInfo()
        reques.context = mContext
        reques.reqUrl = HttpConfig.HOST + HttpConfig.INTERFACE_GET_PRO_LIST
        reques.parser = ProductSortDataParse()
        reques.handler = object : Handler() {
            public override fun handleMessage(msg: Message) {
                val resInfo: ResponseInfo = msg.obj as ResponseInfo
                var myTeamData: MutableList<ProductSortBean>? = null
                if (resInfo.status == 1) {
                    myTeamData = resInfo.responseData as MutableList<ProductSortBean>?
                } else {
                    ToastUtils.show(resInfo.msg)
                }
                productSortListData!!.value = myTeamData
                super.handleMessage(msg)
            }
        }
        val callback = ResponseCallback(reques)
        ReqUtil.setReqInfo(reques)
        ReqUtil.requestGetJSON(callback)
    }

    private var notifyListLiveData: MutableLiveData<MutableList<String>?>? = null

    /**
     * 获取Notify数据
     * @return
     */
    fun getNotifyList(mContext: Context): LiveData<MutableList<String>?> {
//        if (null == notifyListLiveData) {
//        }
        notifyListLiveData = MutableLiveData()
        loadNotifyList(mContext)
        return notifyListLiveData!!
    }

    @SuppressLint("HandlerLeak")
    private fun loadNotifyList(mContext: Context) {
        val reques: RequestInfo = RequestInfo()
        reques.context = mContext
        reques.reqUrl = HttpConfig.HOST + HttpConfig.INTERFACE_GET_NOTIFY_LIST
        reques.parser = NormalStringListParse()
        reques.handler = object : Handler() {
            public override fun handleMessage(msg: Message) {
                val resInfo: ResponseInfo = msg.obj as ResponseInfo
                var data: MutableList<String>? = null
                if (resInfo.status == 1) {
                    data = resInfo.responseData as MutableList<String>?
                } else {
                    ToastUtils.show(resInfo.msg)
                }
                notifyListLiveData!!.setValue(data)
                super.handleMessage(msg)
            }
        }
        val callback: ResponseCallback = ResponseCallback(reques)
        ReqUtil.setReqInfo(reques)
        ReqUtil.requestGetJSON(callback)
    }

    private var notifyNumLiveData: MutableLiveData<Int?>? = null

    /**
     * 获取Notify数据
     * @return
     */
    fun getNotifyNum(mContext: Context): LiveData<Int?> {
//        if (null == notifyNumLiveData) {
//        }
        notifyNumLiveData = MutableLiveData()
        loadNotifyNum(mContext)
        return notifyNumLiveData!!
    }

    @SuppressLint("HandlerLeak")
    private fun loadNotifyNum(mContext: Context) {
        val reques: RequestInfo = RequestInfo()
        reques.context = mContext
        reques.reqUrl = HttpConfig.HOST + HttpConfig.INTERFACE_MESSAGE_NUM
        reques.parser = NormalIntParse()
        reques.handler = object : Handler() {
            public override fun handleMessage(msg: Message) {
                val resInfo: ResponseInfo = msg.obj as ResponseInfo
                var data: Int? = null
                if (resInfo.status == 1) {
                    data = resInfo.responseData as Int?
                } else {
                    ToastUtils.show(resInfo.msg)
                }
                notifyNumLiveData!!.setValue(data)
                super.handleMessage(msg)
            }
        }
        val callback: ResponseCallback = ResponseCallback(reques)
        ReqUtil.setReqInfo(reques)
        ReqUtil.requestGetJSON(callback)
    }

    private var sysOrderListData //个人中心订单类别列表数据
            : MutableLiveData<MutableList<MutableList<SysOrderBean>>?>? = null

    /**
     * 获取系统订单类型列表数据
     * @returnS
     */
    fun getSysOrderList(mContext: Context): LiveData<MutableList<MutableList<SysOrderBean>>?> {
//        if (null == categoryListData) {
//        }
        sysOrderListData = MutableLiveData()
        loadSysOrderList(mContext)
        return sysOrderListData!!
    }

    @SuppressLint("HandlerLeak")
    private fun loadSysOrderList(mContext: Context) {
        val reques: RequestInfo = RequestInfo()
        reques.context = mContext
        reques.reqUrl = HttpConfig.HOST + HttpConfig.INTERFACE_SYS_ORDER_LIST
        reques.parser = SysOrderDataParse()
        reques.handler = object : Handler() {
            public override fun handleMessage(msg: Message) {
                val resInfo: ResponseInfo = msg.obj as ResponseInfo
                var myTeamData: MutableList<MutableList<SysOrderBean>>? = null
                if (resInfo.status == 1) {
                    myTeamData = resInfo.responseData as MutableList<MutableList<SysOrderBean>>?
                } else {
                    ToastUtils.show(resInfo.msg)
                }
                sysOrderListData!!.setValue(myTeamData)
                super.handleMessage(msg)
            }
        }
        val callback: ResponseCallback = ResponseCallback(reques)
        ReqUtil.setReqInfo(reques)
        ReqUtil.requestGetJSON(callback)
    }

    private var categoryListData //首页类别列表数据
            : MutableLiveData<MutableList<MutableList<CategoryBean>>?>? = null

    /**
     * 获取类型列表数据
     * @return
     */
    fun getCategoryList(mContext: Context): LiveData<MutableList<MutableList<CategoryBean>>?> {
//        if (null == categoryListData) {
//        }
        categoryListData = MutableLiveData()
        loadCategorys(mContext)
        return categoryListData!!
    }

    @SuppressLint("HandlerLeak")
    private fun loadCategorys(mContext: Context) {
        val reques: RequestInfo = RequestInfo()
        reques.context = mContext
        reques.reqUrl = HttpConfig.HOST + HttpConfig.INTERFACE_GET_CATEGORY
        reques.parser = CategoryDataParse()
        reques.handler = object : Handler() {
            public override fun handleMessage(msg: Message) {
                val resInfo: ResponseInfo = msg.obj as ResponseInfo
                var myTeamData: MutableList<MutableList<CategoryBean>>? = null
                if (resInfo.status == 1) {
                    myTeamData = resInfo.responseData as MutableList<MutableList<CategoryBean>>?
                } else {
                    ToastUtils.show(resInfo.msg)
                }
                categoryListData!!.setValue(myTeamData)
                super.handleMessage(msg)
            }
        }
        val callback: ResponseCallback = ResponseCallback(reques)
        ReqUtil.setReqInfo(reques)
        ReqUtil.requestGetJSON(callback)
    }

//    @SuppressLint("HandlerLeak")
//    fun sendOilPayInfo(context: Context?) {
//        val reques: RequestInfo = RequestInfo()
//        reques.context = context
//        reques.reqUrl = HttpConfig.HOST + HttpConfig.INTERFACE_GAS_NOTIFIED
//        val reqDateMap: HashMap<String, Any> = HashMap()
//        reques.reqDataMap = reqDateMap
//        reques.parser = NormalIntParse()
//        reques.handler = object : Handler() {
//            public override fun handleMessage(msg: Message) {
////                ResponseInfo resInfo = (ResponseInfo) msg.obj;
//                super.handleMessage(msg)
//            }
//        }
//        val callback: ResponseCallback = ResponseCallback(reques)
//        ReqUtil.setReqInfo(reques)
//        ReqUtil.requestPostJSON(callback)
//    }

    public override fun onCleared() {
        super.onCleared()
    }
}