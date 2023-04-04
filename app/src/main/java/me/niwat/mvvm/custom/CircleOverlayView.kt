package me.niwat.mvvm.custom

import android.content.Context
import android.graphics.*
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.util.AttributeSet
import android.view.View


class CircleOverlayView constructor(
    context: Context?,
    attributeSet: AttributeSet?,
) : View(context, attributeSet) {
    private var ovalBitmap: Bitmap? = null

    private var ovalCanvas = Canvas()

    private var ovalDrawable: ShapeDrawable? = null

    private val ovalPaint = Paint(ANTI_ALIAS_FLAG)
    private val textPaint = Paint(ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        color = Color.WHITE
        textSize = 60f
    }
    private var text: String? = null


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            drawBorder(it)
            drawText(it)

        }
    }

    private fun drawText(canvas: Canvas) {
        val xPos = canvas.width / 2
        val yPos = canvas.height / 1.2

        text?.let {
            canvas.drawText(
                it,
                xPos.toFloat(),
                yPos.toFloat(),
                textPaint
            )
        }
    }

    private fun drawBorder(canvas: Canvas) {
        if (ovalBitmap == null) {
            createWindowFrame()
        }
        canvas.drawBitmap(ovalBitmap!!, 0f, 0f, null)
    }

    private fun createWindowFrame() {
        ovalBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        ovalCanvas = Canvas(ovalBitmap!!)

        //draw background
        val outerRectangle = RectF(0f, 0f, width.toFloat(), height.toFloat())

        ovalPaint.apply {
            color = Color.BLACK
            alpha = 150
        }
        ovalCanvas.drawRect(outerRectangle, ovalPaint)

        //draw circle
        ovalPaint.apply {
            color = Color.TRANSPARENT
            xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OUT)
        }
        ovalCanvas.drawOval(width * .10f, height * .10f, width * .90f, height * .70f, ovalPaint)

        //draw stroke
        ovalDrawable = ShapeDrawable(OvalShape())
        ovalDrawable?.paint?.apply {
            style = Paint.Style.STROKE
            color = Color.RED
            strokeWidth = 10f
        }
        ovalDrawable?.setBounds(
            (width * .10f).toInt(),
            (height * .10f).toInt(),
            (width * .90f).toInt(),
            (height * .70f).toInt()
        )
        ovalDrawable?.draw(ovalCanvas)
    }

    fun changeBorderColor(mColor: Int) {
        ovalDrawable?.paint?.apply {
            color = mColor
        }
        ovalDrawable?.draw(ovalCanvas)
        invalidate()
    }

    fun getBounds(): Rect? {
        return ovalDrawable?.bounds
    }

    fun setText(newText: String) {
        text = newText
        invalidate()
    }
}