package com.kayu.utils

import android.util.Base64
import kotlin.Throws
import java.lang.Exception
import java.lang.RuntimeException
import java.nio.charset.Charset
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.DESKeySpec
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object DesCoderUtil {
    private val keys = byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0)
    var key = "leagsoft"

    /**
     *
     *
     *
     * 对password进行MD5加密
     * @param source
     * @return
     * @return byte[]
     * author: Heweipo
     */
    fun getMD5(source: ByteArray?): ByteArray? {
        var tmp: ByteArray? = null
        try {
            val md = MessageDigest
                .getInstance("MD5")
            md.update(source)
            tmp = md.digest()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return tmp
    }

    /**
     *
     *
     *
     * 采用JDK内置类进行真正的加密操作
     * @param byteS
     * @param password
     * @return
     * @return byte[]
     * author: Heweipo
     */
    private fun encryptByte(byteS: ByteArray, password: ByteArray): ByteArray? {
        var byteFina: ByteArray? = null
        byteFina = try { // 初始化加密/解密工具
            val cipher = Cipher.getInstance("DES/CBC/PKCS5Padding")
            val desKeySpec = DESKeySpec(password)
            val keyFactory = SecretKeyFactory.getInstance("DES")
            val secretKey = keyFactory.generateSecret(desKeySpec)
            val iv = IvParameterSpec(keys)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv)
            cipher.doFinal(byteS)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
        return byteFina
    }

    /**
     *
     *
     *
     * 采用JDK对应的内置类进行解密操作
     * @param byteS
     * @param password
     * @return
     * @return byte[]
     * author: Heweipo
     */
    fun decryptByte(byteS: ByteArray?, password: ByteArray?): ByteArray? {
        var byteFina: ByteArray? = null
        byteFina = try { // 初始化加密/解密工具
            val cipher = Cipher.getInstance("DES/CBC/PKCS5Padding")
            val desKeySpec = DESKeySpec(password)
            val keyFactory = SecretKeyFactory.getInstance("DES")
            val secretKey = keyFactory.generateSecret(desKeySpec)
            val iv = IvParameterSpec(keys)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv)
            cipher.doFinal(byteS)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
        return byteFina
    }

    @Throws(Exception::class)
    fun decryptDES(decryptString: String?, decryptKey: String): String {
        val sourceBytes = Base64.decode(decryptString, 0)
        val cipher = Cipher.getInstance("DES/ECB/PKCS5Padding")
        if (decryptKey.toByteArray().size > 8) {
            for (x in keys.indices) {
                keys[x] = decryptKey.toByteArray()[x]
            }
            cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(keys, "DES"))
        } else {
            cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(decryptKey.toByteArray(), "DES"))
        }
        val decoded = cipher.doFinal(sourceBytes)
        return String(decoded, Charset.defaultCharset())
    }

    @Throws(Exception::class)
    fun encryptDES(encryptString: String, encryptKey: String): String {
        val cipher = Cipher.getInstance("DES/ECB/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(encryptKey.toByteArray(), "DES"))
        val encryptedData = cipher.doFinal(encryptString.toByteArray(charset("UTF-8")))
        return String(Base64.encode(encryptedData, 0), Charset.defaultCharset())
    }
}