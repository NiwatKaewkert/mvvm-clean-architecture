package me.niwat.mvvm.custom

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.util.AttributeSet
import android.view.View


class CircleOverlayView constructor(
    context: Context?,
    attributeSet: AttributeSet?,
) : View(context, attributeSet) {
    private var bitmap: Bitmap? = null
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var osCanvas = Canvas()
    private var mDrawable: ShapeDrawable? = null

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        if (bitmap == null) {
            createWindowFrame()
        }
        canvas.drawBitmap(bitmap!!, 0f, 0f, null)
    }

    private fun createWindowFrame() {
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        osCanvas = Canvas(bitmap!!)

        //draw background
        val outerRectangle = RectF(0f, 0f, width.toFloat(), height.toFloat())

        paint.apply {
            color = Color.BLACK
            alpha = 150
        }
        osCanvas.drawRect(outerRectangle, paint)

        //draw circle
        paint.apply {
            color = Color.TRANSPARENT
            xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OUT)
        }
        osCanvas.drawOval(width * .10f, height * .10f, width * .90f, height * .70f, paint)

        //draw stroke
        mDrawable = ShapeDrawable(OvalShape())
        mDrawable?.paint?.apply {
            style = Paint.Style.STROKE
            color = Color.RED
            strokeWidth = 10f
        }
        mDrawable?.setBounds(
            (width * .10f).toInt(),
            (height * .10f).toInt(),
            (width * .90f).toInt(),
            (height * .70f).toInt()
        )
        mDrawable?.draw(osCanvas)
    }

    fun changeBorderColor(mColor: Int) {
        mDrawable?.paint?.apply {
            color = mColor
        }
        mDrawable?.draw(osCanvas)
        invalidate()
    }
}