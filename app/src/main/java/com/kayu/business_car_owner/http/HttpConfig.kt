package com.kayu.business_car_owner.http

import com.kayu.business_car_owner.BuildConfig
import okhttp3.MediaType

object HttpConfig {

    val HOST: String = BuildConfig.BASE_URL
    const val INTERFACE_LOGIN = "api/login" //登录
    const val INTERFACE_VERIFICATION_CODE = "api/getSmsCapt/" //登录验证码
    const val INTERFACE_ORDER_CODE = "api/aodsms?phone=" //查询订单验证码
    const val INTERFACE_ORDER_DETAIL = "api/v1/ua/detail" //查询订单详情
    const val INTERFACE_ACTVINFO = "api/v1/user/actvinfo" //查询激活卡
    const val INTERFACE_SET_PASSWORD = "" //设置密码
    const val INTERFACE_RESET_PASSWORD = "" //重置密码
    const val CLOSE_WEB_VIEW = "https://www.ky808.cn/close"
    const val CLOSE_WEB_VIEW1 = "https://www.kakayuy.com/close"
    const val INTERFACE_GET_CATEGORY = "api/v1/nav/list" //获取首页项目类别列表
    const val INTERFACE_GET_FILTER = "api/v1/gas/getfilter" //获取加油站条件
    const val INTERFACE_GET_BANNER = "api/v1/banner/list" //获取首页Banner列表
    const val INTERFACE_GET_POP_NAVI = "api/v1/nav-high/queryList" //获取首页热门导航列表
    const val INTERFACE_GET_PRO_LIST = "api/v1/hc-product/queryList" //获取首页产品分类及列表
    const val INTERFACE_GET_ACCOUNT_REMINDER = "api/parameter/getAccTitle" //获取账户提示语
    const val INTERFACE_GET_EXCHANGE = "api/v1/user/recharge" //兑换充值
    const val INTERFACE_GET_NOTIFY_LIST = "api/v1/notify/list" //获取消息列表
    const val INTERFACE_STATION_LIST = "api/v2/gas/list" //获取加油站列表
    const val INTERFACE_STATION_DETAIL = "api/v1/gas/getdetail/" //获取加油站详情
    const val INTERFACE_GAS_PAY = "api/v2/gas/buy" //获取加油站支付信息
    const val INTERFACE_GAS_NOTIFIED = "api/v1/gasorder/notified" //发起加油通知后台

    //    public static final String INTERFACE_GAS_ORDER_LIST = "api/v1/gasorder/list"; //获取加油站订单列表
    const val INTERFACE_GAS_ORDER_LIST = "api/v1/gasorder/queryList" //获取加油站订单列表
    const val INTERFACE_SYS_ORDER_LIST = "api/v1/sys-order-nav/queryList" //获取订单类别列表
    const val INTERFACE_GET_WASH_FILTER = "api/v1/carwash/getfilter" //洗车条件
    const val INTERFACE_WASH_STATION_LIST = "api/v1/carwash/list" //获取洗车站列表
    const val INTERFACE_WASH_STATION_DETAIL = "api/v1/carwash/getdetail/" //获取洗车站详情
    const val INTERFACE_WASH_PAY = "api/v1/carwash/buy" //获取洗车订单购买信息
    const val INTERFACE_WASH_PAY_CANCEL = "api/v1/cworder/cancel" //取消洗车订单
    const val INTERFACE_WASH_PAY_STATUS = "api/v1/cworder/getstatus/" //获取洗车订单支付后的状态
    const val INTERFACE_WASH_ORDER_LIST = "api/v1/cworder/list" //获取洗车订单列表
    const val INTERFACE_WASH_ORDER_DETAIL = "api/v1/cworder/getdetail/" //获取洗车订单详情
    const val INTERFACE_WASH_ORDER_REFUND_INFO = "api/v1/cworder/rfdinfo/" //获取洗车订单退款信息
    const val INTERFACE_WASH_ORDER_REFUND = "api/v1/cworder/refund" //洗车订单申请退款
    const val INTERFACE_GET_USER_ROLE = "api/v1/user/getRole" //获取用户身份
    const val INTERFACE_GET_PARAMETER = "api/parameter/getSystemParameter" //获取系统参数配置
    const val INTERFACE_GET_SYS_PARAMETER = "api/parameter/getSysParam/" //获取系统参数配置
    const val INTERFACE_WECHAT = "api/parameter/wechatCustomer" //微信客服
    const val INTERFACE_USER_INFO = "api/v1/user/getdetail" //用户信息
    const val INTERFACE_BALANCE_DEAIL = "api/v1/ioitem/list" //收入明细
    const val INTERFACE_CHECK_UPDAGE = "api/parameter/editionAndroid" //检查版本更新接口
    const val INTERFACE_MESSAGE_LIST = "api/v1/notify/list" //消息列表
    const val INTERFACE_MESSAGE_NUM = "api/v1/notify/getUnreadCnt" //消息列表
    const val INTERFACE_AD_COMPLETE = "api/v1/ad/complete" //看完广告回调

    //    public String authorization = "";
    //    public static final MediaType JSON =MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
    val FILE = MediaType.parse("application/octet-stream")
    val FORM = MediaType.parse("multipart/form-data; charset=utf-8")
    val JSON = MediaType.parse("application/json; charset=utf-8")
}