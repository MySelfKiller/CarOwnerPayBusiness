package com.kayu.business_car_owner.http.parser

import android.os.Handler
import kotlin.Throws
import java.lang.Exception

/**
 * Created by HuangMin on 2016/7/10.
 */
abstract class BaseParse<ResponseInfo> {
    /**
     * json数据解析
     * @param jsonStr json数据串
     * @param handler
     * @return 解析后的Object
     * @throws JSONException
     */
    @Throws(Exception::class)
    abstract fun parseJSON(
        handler: Handler,
        jsonStr: String,
        dataVersion: Double
    ): ResponseInfo //	 public String checkResponse(String paramString) throws JSONException{
    //		if(paramString==null || "".equals(paramString.trim())){
    //			return null;
    //		}else{
    //			JSONObject jsonObject = new JSONObject(paramString);
    //
    //			if(jsonObject!=null && !jsonObject.equals("error")){
    //				return result;
    //			}else{
    //				return null;
    //			}
    //
    //		}
    //	 }
}