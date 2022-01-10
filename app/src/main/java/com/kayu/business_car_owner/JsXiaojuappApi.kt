package com.kayu.business_car_owner

import android.content.Context
import android.os.Handler
import android.webkit.JavascriptInterface
import com.kayu.utils.location.LocationManagerUtil
import com.kayu.utils.*
import org.json.JSONException
import org.json.JSONObject
import java.nio.channels.CompletionHandler

/**
 * 配置青桔js调用信息
 */
class JsXiaojuappApi(private val mContext: Context, private val mHandler: Handler) {

    @JavascriptInterface
    fun getLocation(args: Any?, handler: CompletionHandler<*, *>?) {
        // handler.complete("");
        val location = LocationManagerUtil.self?.loccation
        //        mHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                webView.evaluateJavascript("window.locationCallback(" + location.getLongitude() + "," + location.getLatitude() + ")", null);
//            }
//        });
        LogUtil.e(
            "qingju",
            "JsXiaojuappApi getLocation==" + location?.longitude + "," + location?.latitude
        )
    }

    @JavascriptInterface
    fun launchNav(args: Any) {
        // handler
        LogUtil.e("qingju", "JsXiaojuappApi launchNav==$args")
        //        mHandler.post(() -> {
//            try {
//                JSONObject jsonObject = new JSONObject((String) args);
////                AppUtil.openUrl(mContext, "http://uri.amap.com/marker?position=" + jsonObject.getLong("toLng") + "," + jsonObject.getLong("toLat"));
////                KWApplication.getInstance().toNavi(mContext, jsonObject.getLong("toLat"), jsonObject.getLong("toLng"),"","GCJ02");
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        });
    }

    @JavascriptInterface
    fun getLocation(args: Any?) {
        // handler
        val location = LocationManagerUtil.self?.loccation
        //        mHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                webView.evaluateJavascript("window.locationCallback(" + location.getLongitude() + "," + location.getLatitude() + ")", null);
//            }
//        });
        LogUtil.e(
            "qingju",
            "JsXiaojuappApi getLocation==" + location?.longitude + "," + location?.latitude
        )
    }

    @JavascriptInterface
    fun openGMap(args: String) {
        LogUtil.e("qingju", "JsXiaojuappApi-------$args")
        mHandler.post {
            //{"fromLng":118.180237,"fromLat":39.623863,"toLng":"118.02162","toLat":"39.7285","toName":"红利加油站"}
            try {
                val jsonObject = JSONObject(args)
                KWApplication.instance.toNavi(
                    mContext,
                    jsonObject.getString("toLat"),
                    jsonObject.getString("toLng"),
                    jsonObject.getString("toName"), "GCJ02"
                )
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }
}