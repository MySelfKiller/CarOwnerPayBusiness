package com.kayu.utils

import android.text.TextUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.GsonBuilder
import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception

/**
 * @author  ddnosh
 * @website http://blog.csdn.net/ddnosh
 */
object GsonHelper {
    private const val TAG = "GsonHelper"
    private var gson: Gson? = null

    /**
     * 返回指定key的值
     *
     * @param json
     * @param key
     * @param type 数据类型0表示String类，1表示int类型
     * @return
     */
    fun getField(json: String?, key: String?, type: Int): Any? {
        if (StringUtil.isEmpty(json) || StringUtil.isEmpty(key)) return null
        try {
            val obj = JSONObject(json)
            if (obj.has(key)) {
                return if (type == 0) {
                    obj.getString(key)
                } else {
                    obj.getInt(key)
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return null
    }

    fun <T> fromJson(JSONString: String?, typeToken: TypeToken<T>): T? {
        if (TextUtils.isEmpty(JSONString)) return null
        var t: T? = null
        t = try {
            gson!!.fromJson(JSONString, typeToken.type)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
        return t
    }

    fun <T> fromJson(JSONString: String?, classOfT: Class<T>?): T? {
        if (TextUtils.isEmpty(JSONString)) return null
        var t: T? = null
        t = try {
            gson!!.fromJson(JSONString, classOfT)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
        return t
    }

    fun toJsonString(obj: Any?): String {
        var gsonString: String = ""
        if (gson != null) {
            gsonString = gson!!.toJson(obj)
        }
        return gsonString
    }

    init {
        if (gson == null) {
            gson = GsonBuilder().disableHtmlEscaping().create()
        }
    }
}