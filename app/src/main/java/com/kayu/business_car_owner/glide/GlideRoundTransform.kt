package com.kayu.business_car_owner.glide

import android.content.Context
import android.graphics.*
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.kayu.business_car_owner.R
import java.security.MessageDigest

class GlideRoundTransform(dp: Int) : BitmapTransformation() {
    constructor(context: Context) : this(context.resources.getDimensionPixelSize(R.dimen.dp_5)) {}

    override fun transform(
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap {
        //变换的时候裁切
//        Bitmap bitmap = TransformationUtils.centerCrop(pool, toTransform, tagWidth, tagHeight);
        return roundCrop(pool, toTransform)!!
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {}
    private fun roundCrop(pool: BitmapPool, source: Bitmap?): Bitmap? {
        if (source == null) {
            return null
        }
        var result: Bitmap? = pool[source.width, source.height, Bitmap.Config.ARGB_8888]
        if (result == null) {
            result = Bitmap.createBitmap(source.width, source.height, Bitmap.Config.ARGB_8888)
        }
        val canvas = Canvas(result!!)
        val paint = Paint()
        paint.shader = BitmapShader(
            source,
            Shader.TileMode.CLAMP,
            Shader.TileMode.CLAMP
        )
        paint.isAntiAlias = true
        val rectF = RectF(
            0f, 0f, source.width
                .toFloat(), source.height.toFloat()
        )
        canvas.drawRoundRect(rectF, radius, radius, paint)
        //上边圆角 下边直角 (注释掉下边这两行代码 全是圆角)
        val rectRound = RectF(
            0f, 100f, source.width
                .toFloat(), source.height.toFloat()
        )
        canvas.drawRect(rectRound, paint)
        return result
    }

    companion object {
        private var radius = 0f
    }

    init {
        radius = dp.toFloat()
    }
}