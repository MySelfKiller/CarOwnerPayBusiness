package com.kayu.utils.view

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import kotlin.jvm.JvmOverloads
import com.kayu.utils.*
import java.lang.NullPointerException

class RoundTypeImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : AppCompatImageView(context, attrs, defStyle) {
    private var mRadius = 0f
    private var mShadowRadius = 0f
    private var mShadowColor = 0
    private var mIsCircle = false
    private var mIsShadow = false
    private var width = 0
    private var height = 0
    private var imageWidth = 0
    private var imageHeight = 0
    private var mPaint: Paint? = null
    public override fun onDraw(canvas: Canvas) {
        width = canvas.width - paddingLeft - paddingRight //控件实际大小
        height = canvas.height - paddingTop - paddingBottom
        if (!mIsShadow) mShadowRadius = 0f
        imageWidth = width - mShadowRadius.toInt() * 2
        imageHeight = height - mShadowRadius.toInt() * 2
        val image = drawableToBitmap(drawable)
        val reSizeImage = reSizeImage(image, imageWidth, imageHeight)
        initPaint()
        if (mIsCircle) {
            canvas.drawBitmap(
                createCircleImage(reSizeImage),
                paddingLeft.toFloat(), paddingTop.toFloat(), null
            )
        } else {
            canvas.drawBitmap(
                createRoundImage(reSizeImage),
                paddingLeft.toFloat(), paddingTop.toFloat(), null
            )
        }
    }

    private fun initPaint() {
        mPaint = Paint()
        mPaint!!.isAntiAlias = true
    }

    private fun createRoundImage(bitmap: Bitmap?): Bitmap {
        if (bitmap == null) {
            throw NullPointerException("Bitmap can't be null")
        }
        val bitmapShader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        val targetBitmap = Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ARGB_8888)
        val targetCanvas = Canvas(targetBitmap)
        mPaint!!.shader = bitmapShader
        val rect = RectF(0F, 0F, imageWidth.toFloat(), imageHeight.toFloat())
        targetCanvas.drawRoundRect(rect, mRadius, mRadius, mPaint!!)
        return if (mIsShadow) {
            mPaint!!.shader = null
            mPaint!!.color = mShadowColor
            mPaint!!.setShadowLayer(mShadowRadius, 1f, 1f, mShadowColor)
            val target = Bitmap.createBitmap(
                width,
                height,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(target)
            val rectF = RectF(
                mShadowRadius,
                mShadowRadius,
                width - mShadowRadius,
                height - mShadowRadius
            )
            canvas.drawRoundRect(rectF, mRadius, mRadius, mPaint!!)
            mPaint!!.xfermode =
                PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)
            mPaint!!.setShadowLayer(0f, 0f, 0f, 0xffffff)
            canvas.drawBitmap(targetBitmap, mShadowRadius, mShadowRadius, mPaint)
            target
        } else {
            targetBitmap
        }
    }

    private fun createCircleImage(bitmap: Bitmap?): Bitmap {
        if (bitmap == null) {
            throw NullPointerException("Bitmap can't be null")
        }
        val bitmapShader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        val targetBitmap = Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ARGB_8888)
        val targetCanvas = Canvas(targetBitmap)
        mPaint!!.shader = bitmapShader
        targetCanvas.drawCircle(
            (imageWidth / 2).toFloat(),
            (imageWidth / 2).toFloat(),
            (Math.min(imageWidth, imageHeight) / 2).toFloat(),
            mPaint!!
        )
        return if (mIsShadow) {
            mPaint!!.shader = null
            mPaint!!.color = mShadowColor
            mPaint!!.setShadowLayer(mShadowRadius, 1f, 1f, mShadowColor)
            val target = Bitmap.createBitmap(
                width,
                height,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(target)
            canvas.drawCircle(
                (width / 2).toFloat(),
                (height / 2).toFloat(),
                (Math.min(imageWidth, imageHeight) / 2).toFloat(),
                mPaint!!
            )
            mPaint!!.xfermode =
                PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)
            mPaint!!.setShadowLayer(0f, 0f, 0f, 0xffffff)
            canvas.drawBitmap(targetBitmap, mShadowRadius, mShadowRadius, mPaint)
            target
        } else {
            targetBitmap
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(width, height)
    }

    /**
     * drawable转bitmap
     *
     * @param drawable
     * @return
     */
    private fun drawableToBitmap(drawable: Drawable?): Bitmap? {
        if (drawable == null) {
            return null
        } else if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicHeight,
            drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    /**
     * 重设Bitmap的宽高
     *
     * @param bitmap
     * @param newWidth
     * @param newHeight
     * @return
     */
    private fun reSizeImage(bitmap: Bitmap?, newWidth: Int, newHeight: Int): Bitmap {
        val width = bitmap!!.width
        val height = bitmap.height
        // 计算出缩放比
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height
        // 矩阵缩放bitmap
        val matrix = Matrix()
        matrix.postScale(scaleWidth, scaleHeight)
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
    }

    init {
        this.scaleType = ScaleType.FIT_XY
        val ta = context.obtainStyledAttributes(
            attrs,
            R.styleable.RoundImageView, defStyle, 0
        )
        if (ta != null) {
            mRadius = ta.getDimension(R.styleable.RoundImageView_image_radius, 0f)
            mShadowRadius = ta.getDimension(R.styleable.RoundImageView_image_shadow_radius, 0f)
            mIsCircle = ta.getBoolean(R.styleable.RoundImageView_image_circle, false)
            mIsShadow = ta.getBoolean(R.styleable.RoundImageView_image_shadow, false)
            mShadowColor = ta.getInteger(R.styleable.RoundImageView_shadow_color, -0x1b1b1c)
            ta.recycle()
        } else {
            mRadius = 0f
            mShadowRadius = 0f
            mIsCircle = false
            mIsShadow = false
            mShadowColor = -0x1b1b1c
        }
    }
}