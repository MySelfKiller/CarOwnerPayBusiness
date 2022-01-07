package com.kayu.business_car_owner.http

import android.text.TextUtils
import com.kayu.business_car_owner.KWApplication
import com.kayu.utils.*
import okhttp3.*
import java.io.File
import java.lang.Exception
import java.util.HashMap

/**
 * 网路请求公共入口
 * Created by Huangmin on 2018/1/17.
 */
object ReqUtil {
    var httpClient: OkHttpClient? = null
    var headerMap: MutableMap<String, String?> = HashMap()

    //    public static final MediaType PNG=MediaType.parse("image/jpeg");
    private var reqInfo: RequestInfo? = null
    private val fileDownload = false
    fun setReqInfo(reqInfo: RequestInfo?) {
        this.reqInfo = reqInfo
    }

    fun requestPostJSON(callback: Callback?) {
        if (null == reqInfo) {
            LogUtil.e("request", "Req NetWork:reqInfo is null")
            return
        }
        if (!NetUtil.isNetworkAvailable(reqInfo!!.context)) {
            val responseInfo = ResponseInfo(-1, "无可用网络")
            reqInfo!!.handler!!.sendMessage(
                reqInfo!!.handler!!.obtainMessage(
                    Constants.REQ_NETWORK_ERROR,
                    responseInfo
                )
            )
            return
        }
        var url = reqInfo!!.reqUrl

//        FormBody.Builder formBody = new FormBody.Builder();

//        StringBuffer buffer = new StringBuffer();
        var jsonParam = ""
        if (null != reqInfo!!.reqDataMap && !reqInfo!!.reqDataMap!!.isEmpty()) {
            jsonParam = GsonHelper.toJsonString(reqInfo!!.reqDataMap)
            //            Iterator map1it=reqInfo.reqDataMap.entrySet().iterator();
//            while (map1it.hasNext()){
//                Map.Entry<String,Object> entry = (Map.Entry<String,Object>) map1it.next();
//                LogUtil.e("hm","request param："+entry.getKey()+" , "+entry.getValue());
//                formBody.add(entry.getKey(),(String)entry.getValue());
//            }
        }
        if (null != reqInfo!!.reqDataMap && !reqInfo!!.reqDataMap!!.isEmpty()) {
            val map1it: Iterator<*> = reqInfo!!.reqDataMap!!.entries.iterator()
            while (map1it.hasNext()) {
                val entry = map1it.next() as Map.Entry<String, Any>
                LogUtil.e("hm", "request param：" + entry.key + " , " + entry.value)
                if (StringUtil.isEmpty(entry.key)) {
                    url = url + entry.value
                } else {
//                    urlBuilder.addQueryParameter(entry.getKey(),(String) entry.getValue());
                }
            }
        }
        LogUtil.e("hm", "request param：$jsonParam")
        val JsonBody = RequestBody.create(HttpConfig.JSON, jsonParam)
        LogUtil.e("request", "url=$url")
        headerMap["authorization"] = KWApplication.instance.token
        val headers = Headers.of(headerMap)
        val request = Request.Builder()
            .url(url)
            .headers(headers)
            .post(JsonBody)
            .build()
        httpClient!!.newCall(request).enqueue(callback)
    }

    fun requestPostMD5JSON(callback: Callback?) {
        if (null == reqInfo) {
            LogUtil.e("request", "Req NetWork:reqInfo is null")
            return
        }
        if (!NetUtil.isNetworkAvailable(reqInfo!!.context)) {
            val responseInfo = ResponseInfo(-1, "无可用网络")
            reqInfo!!.handler!!.sendMessage(
                reqInfo!!.handler!!.obtainMessage(
                    Constants.REQ_NETWORK_ERROR,
                    responseInfo
                )
            )
            return
        }
        val url = reqInfo!!.reqUrl

//        FormBody.Builder formBody = new FormBody.Builder();

//        StringBuffer buffer = new StringBuffer();
        var jsonParam = ""
        if (null != reqInfo!!.reqDataMap && !reqInfo!!.reqDataMap!!.isEmpty()) {
            jsonParam = GsonHelper.toJsonString(reqInfo!!.reqDataMap)
            //            Iterator map1it=reqInfo.reqDataMap.entrySet().iterator();
//            while (map1it.hasNext()){
//                Map.Entry<String,Object> entry = (Map.Entry<String,Object>) map1it.next();
//                LogUtil.e("hm","request param："+entry.getKey()+" , "+entry.getValue());
//                formBody.add(entry.getKey(),(String)entry.getValue());
//            }
        }
        LogUtil.e("hm", "request param：$jsonParam")
        val jsonMap = HashMap<String, Any>()
        try {
            val value = DesCoderUtil.encryptDES(
                jsonParam, NetUtil.token!!.substring(
                    NetUtil.token!!.length - 8
                )
            )
            jsonMap["data"] = value
            LogUtil.e("hm", "request param：$value")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        var JsonBody: RequestBody? = null
        try {
            JsonBody = RequestBody.create(HttpConfig.JSON, GsonHelper.toJsonString(jsonMap))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        LogUtil.e("request", "url=$url")
        val headers = Headers.of(headerMap)
        val request = Request.Builder()
            .url(url)
            .headers(headers)
            .post(JsonBody)
            .build()
        httpClient!!.newCall(request).enqueue(callback)
    }

    fun requestGetJSON(callback: Callback?) {
        if (null == reqInfo) {
            LogUtil.e("request", "Req NetWork:reqInfo is null")
            return
        }
        if (!NetUtil.isNetworkAvailable(reqInfo!!.context)) {
            val responseInfo = ResponseInfo(-1, "无可用网络")
            reqInfo!!.handler!!.sendMessage(
                reqInfo!!.handler!!.obtainMessage(
                    Constants.REQ_NETWORK_ERROR,
                    responseInfo
                )
            )
            return
        }
        var url = reqInfo!!.reqUrl

//        HttpUrl.Builder urlBuilder =HttpUrl.parse(url)
//                .newBuilder();
        if (null != reqInfo!!.reqDataMap && !reqInfo!!.reqDataMap!!.isEmpty()) {
            val map1it: Iterator<*> = reqInfo!!.reqDataMap!!.entries.iterator()
            while (map1it.hasNext()) {
                val entry = map1it.next() as Map.Entry<String, Any>
                LogUtil.e("hm", "request param：" + entry.key + " , " + entry.value)
                if (StringUtil.isEmpty(entry.key)) {
                    url = url + entry.value
                } else {
//                    urlBuilder.addQueryParameter(entry.getKey(),(String) entry.getValue());
                }
            }
        }
        LogUtil.e("request", "url=$url")
        headerMap["authorization"] = KWApplication.instance.token
        val headers = Headers.of(headerMap)
        val request = Request.Builder()
            .url(url)
            .headers(headers)
            .get()
            .build()
        httpClient!!.newCall(request).enqueue(callback)
    }

    fun requestGet(callback: Callback?) {
        if (reqInfo == null) {
            LogUtil.e("request", "Req NetWork:reqInfo is null")
            return
        }
        if (!NetUtil.isNetworkAvailable(reqInfo!!.context)) {
            val responseInfo = ResponseInfo(-1, "无可用网络")
            reqInfo!!.handler!!.sendMessage(
                reqInfo!!.handler!!.obtainMessage(
                    Constants.REQ_NETWORK_ERROR,
                    responseInfo
                )
            )
            return
        }
        var downfileSize: Long = 0
        if (reqInfo!!.file != null && reqInfo!!.file!!.exists()) downfileSize =
            reqInfo!!.file!!.length()
        var url: String? = HttpConfig.HOST + reqInfo!!.reqUrl
        if (!TextUtils.isEmpty(reqInfo!!.downUrl)) url = reqInfo!!.downUrl

//        Map<String, String> headerMap = new HashMap<>();
        headerMap["Range"] = "bytes=" + downfileSize + "-" + reqInfo!!.fileSize
        val headers = Headers.of(headerMap)
        val request = Request.Builder()
            .url(url)
            .headers(headers)
            .get()
            .build()
        httpClient!!.newCall(request).enqueue(callback)
    }

    fun requestGetImage(callback: Callback?) {
        if (reqInfo == null) {
            LogUtil.e("hm", "Req NetWork:reqInfo is null")
            return
        }
        if (!NetUtil.isNetworkAvailable(reqInfo!!.context)) {
            val responseInfo = ResponseInfo(-1, "无可用网络")
            reqInfo!!.handler!!.sendMessage(
                reqInfo!!.handler!!.obtainMessage(
                    Constants.REQ_NETWORK_ERROR,
                    responseInfo
                )
            )
            return
        }
        var url = reqInfo!!.downUrl
        if (null != reqInfo!!.reqDataMap && !reqInfo!!.reqDataMap!!.isEmpty()) {
            val map1it: Iterator<*> = reqInfo!!.reqDataMap!!.entries.iterator()
            while (map1it.hasNext()) {
                val entry = map1it.next() as Map.Entry<String, Any>
                LogUtil.e("hm", "request param：" + entry.key + " , " + entry.value)
                if (StringUtil.isEmpty(entry.key)) {
                    url = url + entry.value
                } else {
//                    urlBuilder.addQueryParameter(entry.getKey(),(String) entry.getValue());
                }
            }
        }
        val request = Request.Builder()
            .url(url)
            .get()
            .build()
        httpClient!!.newCall(request).enqueue(callback)
    }

    /**
     * post请求，json，带文件上传，暂时不知道是服务端问题，还是本地代码写的有问题
     */
    fun requestForm(callback: Callback?) {
        if (reqInfo == null) {
            return
        }
        val url = reqInfo!!.reqUrl
        val multipartBodyBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)
        val reqJson = ""
        if (null != reqInfo!!.reqDataMap && !reqInfo!!.reqDataMap!!.isEmpty()) {
            val iter: Iterator<*> = reqInfo!!.reqDataMap!!.entries.iterator()
            while (iter.hasNext()) {
                val entry = iter.next() as Map.Entry<*, *>
                val key = entry.key!!
                val `val` = entry.value!!
                LogUtil.e("hm", "request param：" + entry.key + " , " + entry.value)
                multipartBodyBuilder.addFormDataPart(key.toString(), `val`.toString())
            }
        }
        if (reqInfo!!.fileDataMap != null && !reqInfo!!.fileDataMap!!.isEmpty()) {
            val iter: Iterator<*> = reqInfo!!.fileDataMap!!.entries.iterator()
            while (iter.hasNext()) {
                val entry = iter.next() as Map.Entry<*, *>
                val key = entry.key!!
                val `val` = entry.value!!
                LogUtil.e("hm", "request param：" + entry.key + " , " + entry.value)
                val file = File(`val`.toString())
                val fileBody = RequestBody.create(HttpConfig.FILE, file)
                multipartBodyBuilder.addFormDataPart(key.toString(), "$key.jpg", fileBody)
            }
        }
        LogUtil.e("hm", "url=$url")
        //        Map<String, String> headerMap = new HashMap<>();
//        //mt_from=app
//        headerMap.put("mt_from","app");
//        LogUtil.getSelf().i("authorization="+getSkey());
//        headerMap.put("authorization",getSkey());
//        Headers headers = Headers.of(headerMap);
        val request = Request.Builder()
            .url(url)
            .post(multipartBodyBuilder.build())
            .build()
        httpClient!!.newCall(request).enqueue(callback)
    }

    init {
//        OkHttpClient. = new OkHttpClient.Builder();
        //请求超时设置
        headerMap["terminal"] = "app"
        headerMap["Referer"] = HttpConfig.HOST
        Referer@ http@ //192.168.0.112:8081/
        httpClient = OkHttpManager.httpClient
    }
}