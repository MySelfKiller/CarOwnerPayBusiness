package com.kayu.business_car_owner.http

import android.os.Build
import kotlin.Throws
import org.apache.http.conn.scheme.SocketFactory
import org.apache.http.conn.ssl.SSLSocketFactory
import org.apache.http.conn.ssl.X509HostnameVerifier
import java.io.IOException
import java.lang.Exception
import java.net.InetAddress
import java.net.Socket
import java.net.UnknownHostException
import java.security.KeyManagementException
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.UnrecoverableKeyException
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.*

class TrustAllSSLSocketFactory private constructor() : SSLSocketFactory(null) {
    @Throws(IOException::class)
    override fun createSocket(): Socket {
        return factory.createSocket()
    }

    @Throws(IOException::class, UnknownHostException::class)
    override fun createSocket(socket: Socket, host: String, port: Int, autoClose: Boolean): Socket {
        if (Build.VERSION.SDK_INT < 11) { // 3.0
            injectHostname(socket, host)
        }
        return factory.createSocket(socket, host, port, autoClose)
    }

    private fun injectHostname(socket: Socket, host: String) {
        try {
            val field = InetAddress::class.java.getDeclaredField("hostName")
            field.isAccessible = true
            field[socket.inetAddress] = host
        } catch (ignored: Exception) {
        }
    }

    inner class TrustAllManager : X509TrustManager {
        @Throws(CertificateException::class)
        override fun checkClientTrusted(arg0: Array<X509Certificate>, arg1: String) {
        }

        @Throws(CertificateException::class)
        override fun checkServerTrusted(arg0: Array<X509Certificate>, arg1: String) {
        }

        override fun getAcceptedIssuers(): Array<X509Certificate>? {
            return null
        }
    }

    companion object {
        private lateinit var factory: javax.net.ssl.SSLSocketFactory
        private var instance: TrustAllSSLSocketFactory? = null
        val default: SocketFactory?
            get() {
                if (instance == null) {
                    try {
                        instance = TrustAllSSLSocketFactory()
                    } catch (e: KeyManagementException) {
                        e.printStackTrace()
                    } catch (e: UnrecoverableKeyException) {
                        e.printStackTrace()
                    } catch (e: NoSuchAlgorithmException) {
                        e.printStackTrace()
                    } catch (e: KeyStoreException) {
                        e.printStackTrace()
                    }
                }
                return instance
            }
        val defaultfactory: javax.net.ssl.SSLSocketFactory
            get() {
                if (instance == null) {
                    try {
                        instance = TrustAllSSLSocketFactory()
                    } catch (e: KeyManagementException) {
                        e.printStackTrace()
                    } catch (e: UnrecoverableKeyException) {
                        e.printStackTrace()
                    } catch (e: NoSuchAlgorithmException) {
                        e.printStackTrace()
                    } catch (e: KeyStoreException) {
                        e.printStackTrace()
                    }
                }
                return factory
            }
    }

    init {
        val context = SSLContext.getInstance("TLS")
        context.init(null, arrayOf<TrustManager>(TrustAllManager()), null)
        factory = context.socketFactory
        hostnameVerifier = object : X509HostnameVerifier {
            @Throws(SSLException::class)
            override fun verify(host: String, cns: Array<String>, subjectAlts: Array<String>) {
            }

            @Throws(SSLException::class)
            override fun verify(host: String, cert: X509Certificate) {
            }

            @Throws(IOException::class)
            override fun verify(host: String, ssl: SSLSocket) {
            }

            override fun verify(host: String, session: SSLSession): Boolean {
                return true
            }
        }
    }
}