package com.kayu.business_car_owner.http.cookie

import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import com.kayu.utils.LogUtil
import okhttp3.*
import java.io.*
import java.lang.StringBuilder
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class PersistentCookieStore(context: Context) {
    private val cookies: MutableMap<String, ConcurrentHashMap<String, Cookie>>
    private val cookiePrefs: SharedPreferences
    protected fun getCookieToken(cookie: Cookie): String {
        return cookie.name() + "@" + cookie.domain()
    }

    fun add(url: HttpUrl, cookie: Cookie) {
        val name = getCookieToken(cookie)

        //将cookies缓存到内存中 如果缓存过期 就重置此cookie
        if (!cookies.isEmpty()) {
            if (!cookie.persistent()) {
                if (!cookies.containsKey(url.host())) {
                    cookies[url.host()] = ConcurrentHashMap()
                }
                cookies[url.host()]!![name] = cookie
            } else {
                if (cookies.containsKey(url.host())) {
//                if (!cookies.get(url.host()).containsKey(name)){
//                }
                    cookies[url.host()]!![name] = cookie
                    //                else {
//                    cookies[url.host()]?.remove(name)
                } else {
                    cookies[url.host()] = ConcurrentHashMap()
                    cookies[url.host()]!![name] = cookie
                }
            }
        } else {
            cookies[url.host()] = ConcurrentHashMap()
            cookies[url.host()]!![name] = cookie
        }


        //讲cookies持久化到本地
        val prefsWriter = cookiePrefs.edit()
        prefsWriter.putString(url.host(), TextUtils.join(",", cookies[url.host()]!!.keys))
        prefsWriter.putString(name, encodeCookie(SerializableOkHttpCookies(cookie)))
        prefsWriter.apply()
    }

    operator fun get(url: HttpUrl): List<Cookie> {
        val ret = ArrayList<Cookie>()
        if (cookies.containsKey(url.host())) ret.addAll(cookies[url.host()]!!.values)
        return ret
    }

    fun removeAll(): Boolean {
        val prefsWriter = cookiePrefs.edit()
        prefsWriter.clear()
        prefsWriter.apply()
        cookies.clear()
        return true
    }

    fun remove(url: HttpUrl, cookie: Cookie): Boolean {
        val name = getCookieToken(cookie)
        return if (cookies.containsKey(url.host()) && cookies[url.host()]!!.containsKey(name)) {
            cookies[url.host()]!!.remove(name)
            val prefsWriter = cookiePrefs.edit()
            if (cookiePrefs.contains(name)) {
                prefsWriter.remove(name)
            }
            prefsWriter.putString(
                url.host(),
                TextUtils.join(",", cookies[url.host()]!!.keys)
            )
            prefsWriter.apply()
            true
        } else {
            false
        }
    }

    fun getCookies(): List<Cookie> {
        val ret = ArrayList<Cookie>()
        for (key in cookies.keys) ret.addAll(cookies[key]!!.values)
        return ret
    }

    /**
     * cookies 序列化成 string
     *
     * @param cookie 要序列化的cookie
     * @return 序列化之后的string
     */
    protected fun encodeCookie(cookie: SerializableOkHttpCookies?): String? {
        if (cookie == null) return null
        val os = ByteArrayOutputStream()
        try {
            val outputStream = ObjectOutputStream(os)
            outputStream.writeObject(cookie)
        } catch (e: IOException) {
            LogUtil.e(LOG_TAG, "IOException in encodeCookie", e)
            return null
        }
        return byteArrayToHexString(os.toByteArray())
    }

    /**
     * 将字符串反序列化成cookies
     *
     * @param cookieString cookies string
     * @return cookie object
     */
    protected fun decodeCookie(cookieString: String): Cookie? {
        val bytes = hexStringToByteArray(cookieString)
        val byteArrayInputStream = ByteArrayInputStream(bytes)
        var cookie: Cookie? = null
        try {
            val objectInputStream = ObjectInputStream(byteArrayInputStream)
            cookie = (objectInputStream.readObject() as SerializableOkHttpCookies).getCookies()
        } catch (e: IOException) {
            LogUtil.e(LOG_TAG, "IOException in decodeCookie", e)
        } catch (e: ClassNotFoundException) {
            LogUtil.e(LOG_TAG, "ClassNotFoundException in decodeCookie", e)
        }
        return cookie
    }

    /**
     * 二进制数组转十六进制字符串
     *
     * @param bytes byte array to be converted
     * @return string containing hex values
     */
    protected fun byteArrayToHexString(bytes: ByteArray): String {
        val sb = StringBuilder(bytes.size * 2)
        for (element in bytes) {
            val v: Int = element.toInt() and 0xff
            if (v < 16) {
                sb.append('0')
            }
            sb.append(Integer.toHexString(v))
        }
        return sb.toString().toUpperCase(Locale.US)
    }

    /**
     * 十六进制字符串转二进制数组
     *
     * @param hexString string of hex-encoded values
     * @return decoded byte array
     */
    protected fun hexStringToByteArray(hexString: String): ByteArray {
        val len = hexString.length
        val data = ByteArray(len / 2)
        var i = 0
        while (i < len) {
            data[i / 2] = ((Character.digit(
                hexString[i],
                16
            ) shl 4) + Character.digit(hexString[i + 1], 16)).toByte()
            i += 2
        }
        return data
    }

    companion object {
        private const val LOG_TAG = "PersistentCookieStore"
        private const val COOKIE_PREFS = "Cookies_Prefs"
    }

    init {
        cookiePrefs = context.getSharedPreferences(COOKIE_PREFS, Context.MODE_PRIVATE)
        cookies = HashMap()

        //将持久化的cookies缓存到内存中 即map cookies
        val prefsMap:MutableMap<String, String> = cookiePrefs.all as MutableMap<String, String>
        for ((key, value) in prefsMap) {
            val cookieNames = TextUtils.split(value as String?, ",")
            for (name in cookieNames) {
                val encodedCookie = cookiePrefs.getString(name, null)
                if (encodedCookie != null) {
                    val decodedCookie = decodeCookie(encodedCookie)
                    if (decodedCookie != null) {
                        if (!cookies.containsKey(key)) {
                            cookies[key] = ConcurrentHashMap<String,Cookie>()
                        }
                        cookies[key]!![name!!] = decodedCookie
                    }
                }
            }
        }
    }
}