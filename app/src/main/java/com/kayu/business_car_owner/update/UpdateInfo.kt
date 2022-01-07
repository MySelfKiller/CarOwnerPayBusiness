package com.kayu.business_car_owner.update

class UpdateInfo {
    var id: String? = null
    var url //url下载连接
            : String = ""
    var content //url下载连接
            : String = ""
    var state = 0
    var type = 0
    var pathLength //文件总大小
            : Long = 0
    var pathMd5 //下载文件校验码
            : String = ""
    var force //更新状态0无需更新1选择性更新2强制更新
            = 0
}