package com.decentapps.supre.photorecovery.datarecovery.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatImageView;

public class SquareImageView extends AppCompatImageView {

    private float cornerRadius = 20.0f; // Adjust this value to change the corner radius
    private Path path = new Path();
    private RectF rect = new RectF();
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public SquareImageView(Context context) {
        super(context);
        init();
    }

    public SquareImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public SquareImageView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }

    private void init() {
        // Enable hardware acceleration for better performance
        setLayerType(LAYER_TYPE_HARDWARE, null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec); // Make the view square
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // Update the rectangle and path for rounded corners
        rect.set(0, 0, w, h);
        path.reset();
        path.addRoundRect(rect, cornerRadius, cornerRadius, Path.Direction.CW);
        path.close();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Clip the canvas to the rounded path
        canvas.clipPath(path);

        // Draw the image
        super.onDraw(canvas);
    }

    /**
     * Set the corner radius for the rounded corners.
     *
     * @param radius The radius of the corners in pixels.
     */
    public void setCornerRadius(float radius) {
        this.cornerRadius = radius;
        invalidate(); // Redraw the view
    }

    /**
     * Get the current corner radius.
     *
     * @return The current corner radius in pixels.
     */
    public float getCornerRadius() {
        return cornerRadius;
    }
}