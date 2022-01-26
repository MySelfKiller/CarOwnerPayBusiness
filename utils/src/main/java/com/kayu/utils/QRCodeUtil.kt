package com.kayu.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.text.TextUtils
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import java.util.*

object QRCodeUtil {
    /**
     * 生成自定义二维码
     *
     * @param content                字符串内容
     * @param width                  二维码宽度
     * @param height                 二维码高度
     * @param character_set          编码方式（一般使用UTF-8）
     * @param error_correction_level 容错率 L：7% M：15% Q：25% H：35%
     * @param margin                 空白边距（二维码与边框的空白区域）
     * @param color_black            黑色色块
     * @param color_white            白色色块
     * @param logoBitmap             logo图片（传null时不添加logo）
     * @param logoPercent            logo所占百分比
     * @param bitmap_black           用来代替黑色色块的图片（传null时不代替）
     * @return
     */
    fun createQRCodeBitmap(
        content: String?,
        width: Int,
        height: Int,
        character_set: String?,
        error_correction_level: String?,
        margin: String?,
        color_black: Int,
        color_white: Int,
        logoBitmap: Bitmap?,
        logoPercent: Float,
        bitmap_black: Bitmap?
    ): Bitmap? {
        // 字符串内容判空
        var bitmap_black = bitmap_black
        if (TextUtils.isEmpty(content)) {
            return null
        }
        // 宽和高>=0
        return if (width < 0 || height < 0) {
            null
        } else try {
            /** 1.设置二维码相关配置,生成BitMatrix(位矩阵)对象  */
            val hints = Hashtable<EncodeHintType, String?>()
            // 字符转码格式设置
            if (!TextUtils.isEmpty(character_set)) {
                hints[EncodeHintType.CHARACTER_SET] = character_set
            }
            // 容错率设置
            if (!TextUtils.isEmpty(error_correction_level)) {
                hints[EncodeHintType.ERROR_CORRECTION] = error_correction_level
            }
            // 空白边距设置
            if (!TextUtils.isEmpty(margin)) {
                hints[EncodeHintType.MARGIN] = margin
            }
            /** 2.将配置参数传入到QRCodeWriter的encode方法生成BitMatrix(位矩阵)对象  */
            val bitMatrix =
                QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints)
            /** 3.创建像素数组,并根据BitMatrix(位矩阵)对象为数组元素赋颜色值  */
            if (bitmap_black != null) {
                //从当前位图按一定的比例创建一个新的位图
                bitmap_black = Bitmap.createScaledBitmap(bitmap_black, width, height, false)
            }
            val pixels = IntArray(width * height)
            for (y in 0 until height) {
                for (x in 0 until width) {
                    //bitMatrix.get(x,y)方法返回true是黑色色块，false是白色色块
                    if (bitMatrix[x, y]) { // 黑色色块像素设置
                        if (bitmap_black != null) { //图片不为null，则将黑色色块换为新位图的像素。
                            pixels[y * width + x] = bitmap_black.getPixel(x, y)
                        } else {
                            pixels[y * width + x] = color_black
                        }
                    } else {
                        pixels[y * width + x] = color_white // 白色色块像素设置
                    }
                }
            }
            /** 4.创建Bitmap对象,根据像素数组设置Bitmap每个像素点的颜色值,并返回Bitmap对象  */
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
            /** 5.为二维码添加logo图标  */
            if (logoBitmap != null) {
                addLogo(bitmap, logoBitmap, logoPercent)
            } else bitmap
        } catch (e: WriterException) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 向二维码中间添加logo图片(图片合成)
     *
     * @param srcBitmap   原图片（生成的简单二维码图片）
     * @param logoBitmap  logo图片
     * @param logoPercent 百分比 (用于调整logo图片在原图片中的显示大小, 取值范围[0,1] )
     * 原图片是二维码时,建议使用0.2F,百分比过大可能导致二维码扫描失败。
     * @return
     */
    private fun addLogo(srcBitmap: Bitmap?, logoBitmap: Bitmap?, logoPercent: Float): Bitmap? {
        var logoPercent = logoPercent
        if (srcBitmap == null) {
            return null
        }
        if (logoBitmap == null) {
            return srcBitmap
        }
        //传值不合法时使用0.2F
        if (logoPercent < 0f || logoPercent > 1f) {
            logoPercent = 0.2f
        }
        /** 1. 获取原图片和Logo图片各自的宽、高值  */
        val srcWidth = srcBitmap.width
        val srcHeight = srcBitmap.height
        val logoWidth = logoBitmap.width
        val logoHeight = logoBitmap.height
        /** 2. 计算画布缩放的宽高比  */
        val scaleWidth = srcWidth * logoPercent / logoWidth
        val scaleHeight = srcHeight * logoPercent / logoHeight
        /** 3. 使用Canvas绘制,合成图片  */
        val bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawBitmap(srcBitmap, 0f, 0f, null)
        canvas.scale(scaleWidth, scaleHeight, (srcWidth / 2).toFloat(), (srcHeight / 2).toFloat())
        canvas.drawBitmap(
            logoBitmap,
            (srcWidth / 2 - logoWidth / 2).toFloat(),
            (srcHeight / 2 - logoHeight / 2).toFloat(),
            null
        )
        return bitmap
    }
}