package com.kayu.utils

import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.lang.StringBuilder
import java.nio.channels.FileChannel
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * Created by Vector
 * on 2017/7/11 0011.
 */
object Md5Util {
    fun getStringMD5(string: String): String {
        if (StringUtil.isEmpty(string)) {
            return ""
        }
        var md5: MessageDigest? = null
        try {
            md5 = MessageDigest.getInstance("MD5")
            val bytes = md5.digest(string.toByteArray())
            val result = StringBuilder()
            for (b in bytes) {
                var temp = Integer.toHexString(b.toInt() and 0xff)
                if (temp.length == 1) {
                    temp = "0$temp"
                }
                result.append(temp)
            }
            return result.toString()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return ""
    }

    fun getFileMD5(file: File): String {
        if (!file.exists()) {
            return ""
        }
        var `in`: FileInputStream? = null
        try {
            `in` = FileInputStream(file)
            val channel = `in`.channel
            val buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, file.length())
            val md = MessageDigest.getInstance("MD5")
            md.update(buffer)
            return bytes2Hex(md.digest())
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (`in` != null) {
                try {
                    `in`.close()
                } catch (ignored: IOException) {
                }
            }
        }
        return ""
    }

    /**
     * 一个byte转为2个hex字符
     *
     * @param src byte数组
     * @return 16进制大写字符串
     */
    fun bytes2Hex(src: ByteArray): String {
        val res = CharArray(src.size shl 1)
        val hexDigits = charArrayOf(
            '0',
            '1',
            '2',
            '3',
            '4',
            '5',
            '6',
            '7',
            '8',
            '9',
            'a',
            'b',
            'c',
            'd',
            'e',
            'f'
        )
        var i = 0
        var j = 0
        while (i < src.size) {
            res[j++] = hexDigits[src[i].toInt() ushr 4 and 0x0f]
            res[j++] = hexDigits[src[i].toInt() and 0x0f]
            i++
        }
        return String(res)
    }
}