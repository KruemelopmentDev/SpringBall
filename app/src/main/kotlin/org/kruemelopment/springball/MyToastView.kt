package org.kruemelopment.springball

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class MyToastView(context: Context?, attributeSet: AttributeSet?) : View(context, attributeSet) {
    var textcolor = Color.parseColor("#ffffff")
    private var bgcolor = Color.parseColor("#ff00ff")
    private var backgroundPaint: Paint = Paint()
    private var viewheight = 0
    private var viewwidth = 0

    init {
        backgroundPaint.color = bgcolor
        backgroundPaint.isAntiAlias = true
        backgroundPaint.isDither = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        viewheight = this.measuredHeight
        viewwidth = this.measuredWidth
        val kreisradius = viewheight / 2
        canvas.drawCircle(
            kreisradius.toFloat(),
            kreisradius.toFloat(),
            kreisradius.toFloat(),
            backgroundPaint
        )
        canvas.drawCircle(
            (viewwidth - kreisradius).toFloat(),
            kreisradius.toFloat(),
            kreisradius.toFloat(),
            backgroundPaint
        )
        canvas.drawRect(
            kreisradius.toFloat(),
            0f,
            (viewwidth - kreisradius).toFloat(),
            viewheight.toFloat(),
            backgroundPaint
        )
    }

    fun setColor(color: Int) {
        textcolor = color
        backgroundPaint.color = color
        requestLayout()
    }
}
