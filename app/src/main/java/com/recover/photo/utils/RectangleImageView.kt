package com.recover.photo.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

class RectangleImageView : AppCompatImageView {
    private var cornerRadius = 20.0f // Adjust this value to change the corner radius
    private val path = Path()
    private val rect = RectF()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    constructor(context: Context?) : super(context!!) {
        init()
    }

    constructor(context: Context?, attributeSet: AttributeSet?) : super(
        context!!, attributeSet
    ) {
        init()
    }

    constructor(context: Context?, attributeSet: AttributeSet?, i: Int) : super(
        context!!, attributeSet, i
    ) {
        init()
    }

    private fun init() {
        // Enable hardware acceleration for better performance
        setLayerType(LAYER_TYPE_HARDWARE, null)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec) // Measure the view as a rectangle
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // Update the rectangle and path for rounded corners
        rect[0f, 0f, w.toFloat()] = h.toFloat()
        path.reset()
        path.addRoundRect(rect, cornerRadius, cornerRadius, Path.Direction.CW)
        path.close()
    }

    override fun onDraw(canvas: Canvas) {
        // Clip the canvas to the rounded path
        canvas.clipPath(path)

        // Draw the image
        super.onDraw(canvas)
    }

    /**
     * Set the corner radius for the rounded corners.
     *
     * @param radius The radius of the corners in pixels.
     */
    fun setCornerRadius(radius: Float) {
        this.cornerRadius = radius
        invalidate() // Redraw the view
    }

    /**
     * Get the current corner radius.
     *
     * @return The current corner radius in pixels.
     */
    fun getCornerRadius(): Float {
        return cornerRadius
    }
}
