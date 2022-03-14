package com.kayu.utils

object Constants {
    const val RC_PERMISSION_BASE = 1000
    const val RC_PERMISSION_PERMISSION_ACTIVITY = RC_PERMISSION_BASE + 1
    const val RC_PERMISSION_PERMISSION_FRAGMENT = RC_PERMISSION_BASE + 2
    const val INSTALL_APP_REQUESTCODE = RC_PERMISSION_BASE + 3
    const val SharedPreferences_name = "login_info"
    const val login_info = "login_info"
    const val isLogin = "isLogin"
    const val token = "token"
    const val isSetPsd = "isSetPsd"
    const val isShowDialog = "isShowDialog"
    const val system_args = "system_args"
    const val userInfo = "user_info"
    const val authority = "com.kayu.business_car_owner.provider"
    const val PATH_ROOT = "com.kayu.business_car_owner"
    var PATH_IMG = "imag/" // 图片
    var PATH_PHOTO = "photo/" // 图片及其他数据保存文件夹

    //服务端请求数据解析状态
    const val REQ_NETWORK_ERROR = -2
    const val PARSE_DATA_ERROR = -1
    const val PARSE_DATA_SUCCESS = 1
    const val PARSE_DATA_REFRESH = 3
    const val PARSE_DATA_END = 4

    //接口请求响应状态码说明
    const val response_code_0 = 0 // 失败
    const val response_code_1 = 1 // 成功
    const val response_code_301 = 301 // 参数错误
    const val response_code_302 = 302 // 结果不存在
    const val response_code_303 = 303 // 结果已存在
    const val response_code_304 = 304 // 数据错误
    const val response_code_305 = 305 // 数据上传失败
    const val response_code_306 = 306 // 非法操作
    const val response_code_401 = 401 // 无权限
    const val response_code_500 = 500 // 服务错误
    const val response_code_10100 = 10100 // 用户名或密码错误
    const val response_code_10101 = 10101 // 用户未登陆
    const val response_code_10102 = 10102 // 账号禁用
    const val response_code_10103 = 10103 // 密码修改失败
    const val response_code_10104 = 10104 // 验证码错误
    const val response_code_10105 = 10105 //
    const val FLAG_CREDIT_CARD = "creditCard"
    const val FLAG_LOAN = "loan"
    const val FLAG_LOAN_BANNER = "loanBanner"
    const val FLAG_ACTIVITY = "activity"
    const val BASE_ID = 1100000
    const val WX_APP_ID = "wx5a7ad977455126cc"
}