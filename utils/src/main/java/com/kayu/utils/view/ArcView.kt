package com.kayu.utils.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import kotlin.jvm.JvmOverloads
import com.kayu.utils.*

class ArcView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var mWidth = 0
    private var mHeight = 0

    /**
     * 弧形高度
     */
    private val mArcHeight: Int

    /**
     * 背景颜色
     */
    private val mBgColor: Int
    private val mPaint: Paint
    private val mContext: Context
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mPaint.style = Paint.Style.FILL
        mPaint.color = mBgColor
        //
//        Rect rect = new Rect(0, 0, mWidth, mHeight - mArcHeight);
//        canvas.drawRect(rect, mPaint);
//
//
//        Path path = new Path();
//        path.moveTo(0, mHeight );
//        path.quadTo(mWidth / 2, mHeight-mArcHeight, mWidth, mHeight);
        val path = Path()
        path.moveTo(0f, mHeight.toFloat())
        path.quadTo(
            (mWidth / 2).toFloat(),
            (mHeight - mArcHeight).toFloat(),
            mWidth.toFloat(),
            mHeight.toFloat()
        )
        path.lineTo(0f, mHeight.toFloat())
        path.close()
        canvas.clipPath(path)
        canvas.drawPath(path, mPaint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        if (widthMode == MeasureSpec.EXACTLY) {
            mWidth = widthSize
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            mHeight = heightSize
        }
        setMeasuredDimension(mWidth, mHeight)
    }

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ArcView)
        mArcHeight = typedArray.getDimensionPixelSize(R.styleable.ArcView_arcHeight, 0)
        mBgColor = typedArray.getColor(R.styleable.ArcView_bgColor, Color.parseColor("#303F9F"))
        mContext = context
        mPaint = Paint()
    }
}