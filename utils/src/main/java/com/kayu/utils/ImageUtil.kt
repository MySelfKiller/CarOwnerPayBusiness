package com.kayu.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object ImageUtil {
    fun compoundBitmap(bitmapOne: Bitmap, bitmapTwo: Bitmap): Bitmap? {
        var newBitmap: Bitmap? = null
        newBitmap = bitmapOne.copy(Bitmap.Config.ARGB_8888, true)
        // newBitmap = Bitmap.createBitmap(bitmapOne.getWidth(),bitmapOne.getHeight(),bitmapOne.getConfig());
        val canvas = Canvas(newBitmap)
        val paint = Paint()
        val w = bitmapOne.width
        val h = bitmapOne.height
        val w_2 = bitmapTwo.width
        val h_2 = bitmapTwo.height
        // paint = new Paint();
//设置第二张图片的 左、上的位置坐标
        canvas.drawBitmap(
            bitmapTwo, ((w - w_2) / 2).toFloat(), (
                    h - h_2 - 20).toFloat(), paint
        )
        canvas.save()
        // 存储新合成的图片
        canvas.restore()
        return newBitmap
    }

    /**
     * 保存图片到指定路径
     *
     * @param context
     * @param bitmap   要保存的图片
     * @param fileName 自定义图片名称
     * @return
     */
    fun saveImageToGallery(context: Context, bitmap: Bitmap, fileName: String?): Boolean {
        // 保存图片至指定路径
        val storePath = Utils.getEnaviBaseStorage(context) + File.separator
        val appDir = File(storePath)
        if (!appDir.exists()) {
            appDir.mkdir()
        }
        val file = File(appDir, fileName)
        try {
            val fos = FileOutputStream(file)
            //通过io流的方式来压缩保存图片(80代表压缩20%)
            val isSuccess = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
            fos.close()

            //发送广播通知系统图库刷新数据
            val uri = Uri.fromFile(file)
            context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
            return if (isSuccess) {
                true
            } else {
                false
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return false
    }
}