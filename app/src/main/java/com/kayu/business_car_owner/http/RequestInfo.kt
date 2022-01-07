package com.kayu.business_car_owner.http

import android.app.Activity
import android.content.Context
import android.os.Handler
import com.kayu.business_car_owner.http.parser.BaseParse
import java.io.File
import java.util.*

/**
 * Created by HuangMin on 2016/7/10.
 */
class RequestInfo {
    var key = "dcec4ddf84b6427bb1a05bceca22365a" // 请求服务端需要用到的验证key

    var reqUrl //请求URL
            : String? = null

    var context //上下文
            : Context? = null
    var activity: Activity? = null

    var reqDataMap //往后台传输请求参数，hashMap携带。
            : HashMap<String, Any>? = null

    //    public HashMap<String,List<Object>> reqDataListMap;
    var otherDataMap //往后台传输请求参数，hashMap携带。驾车需要的额外数据
            : HashMap<String, Any>? = null
    var fileDataMap: HashMap<String?, Any?>? = null

    var parser //返回json数据解析成需要的对象
            : BaseParse<*>? = null

    var handler: Handler? = null
    var file: File? = null
    var fileSize: Long = 0
    var downUrl: String? = null //    public boolean authorization = true;
}