package com.kayu.business_car_owner.http

import android.webkit.WebSettings
import com.kayu.business_car_owner.http.cookie.PersistentCookieStore
import kotlin.Throws
import okhttp3.OkHttpClient
import com.kayu.business_car_owner.KWApplication
import com.kayu.business_car_owner.http.cookie.CookiesManager
import com.kayu.business_car_owner.http.cookie.SetTokenCallBack
import kotlin.jvm.Synchronized
import com.kayu.utils.LogUtil
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * Created by Killer on 2018/5/24.
 */
object OkHttpManager {
    private var mClient: OkHttpClient? = null
    val httpClient: OkHttpClient?
        get() {
            if (null == mClient) initOkhttpClient()
            return mClient
        }

    fun resetHttpClient(): OkHttpClient? {
        mClient = null
        initOkhttpClient()
        return mClient
    }

    /**
     * 初始化okhttp
     */
    private fun initOkhttpClient() {
        LogUtil.e("hm", "执行OkHttpClient初始化")
        val okBuilder = OkHttpClient.Builder()
        okBuilder.connectTimeout(30, TimeUnit.SECONDS)
        okBuilder.readTimeout(30, TimeUnit.SECONDS)
        okBuilder.writeTimeout(30, TimeUnit.SECONDS)
        okBuilder.addInterceptor { chain ->
            val request = chain.request()
                .newBuilder()
                .removeHeader("User-Agent") //移除旧的
                //                                                 WebSettings.getDefaultUserAgent(mContext) 是获取原来的User-Agent
                .addHeader(
                    "User-Agent",
                    WebSettings.getDefaultUserAgent(KWApplication.instance)
                )
                .build()
            chain.proceed(request)
        }
        okBuilder.hostnameVerifier { hostname, session -> true }
        if (HttpConfig.HOST.startsWith("https")) {
            var contextSSL: SSLContext? = null
            try {
                contextSSL = SSLContext.getInstance("TLS")
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            }
            try {
                contextSSL!!.init(null, arrayOf<TrustManager>(object : X509TrustManager {
                    @Throws(CertificateException::class)
                    override fun checkClientTrusted(
                        chain: Array<X509Certificate>,
                        authType: String
                    ) {
                    }

                    @Throws(CertificateException::class)
                    override fun checkServerTrusted(
                        chain: Array<X509Certificate>,
                        authType: String
                    ) {
                    }

                    override fun getAcceptedIssuers(): Array<X509Certificate?> {
                        return arrayOfNulls(0)
                    }
                }), SecureRandom())
            } catch (e: KeyManagementException) {
                e.printStackTrace()
            }
            okBuilder.sslSocketFactory(
                TrustAllSSLSocketFactory.defaultfactory,
                object : X509TrustManager {
                    @Throws(CertificateException::class)
                    override fun checkClientTrusted(
                        chain: Array<X509Certificate>,
                        authType: String
                    ) {
                    }

                    @Throws(CertificateException::class)
                    override fun checkServerTrusted(
                        chain: Array<X509Certificate>,
                        authType: String
                    ) {
                    }

                    override fun getAcceptedIssuers(): Array<X509Certificate?> {
                        return arrayOfNulls(0)
                    }
                })
        }
        okBuilder.cookieJar(
            CookiesManager(
                PersistentCookieStore(KWApplication.instance),
                object : SetTokenCallBack{
                    override fun setToken(token: String?) {
//                        TODO("Not yet implemented")
                    }

                }
            )
        )
        mClient = okBuilder.build()
    }

    //    /**
    //     * 管理器初始化，建议在application中调用
    //     *
    //     * @param context
    //     */
    //    public static void init(Context context) {
    //        getInstance();
    //    }
    init {
        initOkhttpClient()
    }
}