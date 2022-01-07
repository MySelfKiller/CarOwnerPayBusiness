package com.kayu.utils.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.kayu.utils.R

class ArcImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {
    /*
     *弧形高度
     */
    private val mArcHeight: Int
    override fun onDraw(canvas: Canvas) {
        val path = Path()
        path.moveTo(0f, 0f)
        path.lineTo(0f, height.toFloat())
        path.quadTo(
            (width / 2).toFloat(),
            (height - 2 * mArcHeight).toFloat(),
            width.toFloat(),
            height.toFloat()
        )
        path.lineTo(width.toFloat(), 0f)
        path.close()
        canvas.clipPath(path)
        super.onDraw(canvas)
    }

    companion object {
        private const val TAG = "ArcImageView"
    }

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ArcImageView)
        mArcHeight = typedArray.getDimensionPixelSize(R.styleable.ArcImageView_arcHeight, 0)
    }
}