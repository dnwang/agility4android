package org.pinwheel.agility.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DrawFilter;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import org.pinwheel.agility.util.UIUtils;

/**
 * Copyright (C), 2016 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
public class SweetIndicatorView extends View {

    private static final int FOREGROUND = 0x01;
    private static final int BACKGROUND = 0x00;

    private int pointerSize = 0;
    private int[] pointerColor = new int[]{Color.WHITE, Color.rgb(23, 132, 215)};
    private int currentIndex = 0;
    private int orientation = LinearLayout.HORIZONTAL;

    private float pointerRadius = 4f;// default 4dp
    private float pointerMargin = 6f;// default 6dp
    private float zoomScale = 1.3f;
    private float shadowRadius = 0.0f;

    private Paint paint;
    private DrawFilter drawFilter;

    public SweetIndicatorView(Context context) {
        super(context);
        this.init();
    }

    public SweetIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public SweetIndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    private void init() {
        drawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        paint = new Paint();
        pointerRadius = UIUtils.dip2px(getContext(), pointerRadius);
        pointerMargin = UIUtils.dip2px(getContext(), pointerMargin);
        setShadowLayer(3, 0, 0, Color.BLACK);
    }

    public int getPointerSize() {
        return pointerSize;
    }

    public void setPointerSize(int size) {
        pointerSize = Math.max(0, size);
        requestLayout();
        invalidate();
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int index) {
        currentIndex = index;
        invalidate();
    }

    public int getPointerForegroundColor() {
        return pointerColor[FOREGROUND];
    }

    public int getPointerBackgroundColor() {
        return pointerColor[BACKGROUND];
    }

    public void setPointerColor(int foreground, int background) {
        pointerColor[FOREGROUND] = foreground;
        pointerColor[BACKGROUND] = background;
        invalidate();
    }

    public float getPointerRadius() {
        return pointerRadius;
    }

    public void setPointerRadius(float pointerRadius) {
        this.pointerRadius = Math.max(0.0f, pointerRadius);
        requestLayout();
        invalidate();
    }

    public float getPointerMargin() {
        return pointerMargin;
    }

    public void setPointerMargin(float margin) {
        this.pointerMargin = Math.max(0.0f, margin);
        requestLayout();
        invalidate();
    }

    public void setShadowLayer(float radius, float dx, float dy, int color) {
        shadowRadius = Math.max(0.0f, radius);
        setLayerType(LAYER_TYPE_SOFTWARE, paint);
        paint.setShadowLayer(radius, dx, dy, color);
        requestLayout();
        invalidate();
    }

    public float getZoomScale() {
        return zoomScale;
    }

    public void setZoomScale(float scale) {
        zoomScale = Math.max(0.0f, scale);
        requestLayout();
        invalidate();
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation == LinearLayout.VERTICAL ? orientation : LinearLayout.HORIZONTAL;
        requestLayout();
        invalidate();
    }

    private float width;
    private float height;

    private void updateBound() {
        final float margins = pointerSize < 2 ? 0 : (pointerSize - 1) * pointerMargin;
        final float zoomScaleOffset = zoomScale > 1.0f ? pointerRadius * (zoomScale - 1) : 0;
        width = pointerSize * (pointerRadius * 2) + margins + zoomScaleOffset * 2 + shadowRadius * 2;
        height = pointerRadius * 2 + zoomScaleOffset * 2 + shadowRadius * 2;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        updateBound();

        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measureHeight = MeasureSpec.getSize(heightMeasureSpec);

        if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.UNSPECIFIED) {
            measureWidth = (int) (orientation == LinearLayout.VERTICAL ? height : width);
        }

        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.UNSPECIFIED) {
            measureHeight = (int) (orientation == LinearLayout.VERTICAL ? width : height);
        }

        setMeasuredDimension(
                measureWidth + getPaddingLeft() + getPaddingRight(),
                measureHeight + getPaddingTop() + getPaddingBottom());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.setDrawFilter(drawFilter);

        canvas.save();
        canvas.translate((getMeasuredWidth() - width) / 2, (getMeasuredHeight() - height) / 2);
        if (orientation == LinearLayout.VERTICAL) {
            canvas.rotate(90, width / 2, height / 2);
        }

        final float centerOffset = pointerMargin + pointerRadius * 2;
        final float scaleOffset = zoomScale > 1.0f ? pointerRadius * (zoomScale - 1) : 0;

        float radius;
        for (int i = 0; i < pointerSize; i++) {
            radius = pointerRadius;
            if (currentIndex == i) {
                paint.setColor(pointerColor[FOREGROUND]);
                radius *= zoomScale;
            } else {
                paint.setColor(pointerColor[BACKGROUND]);
            }
            // radius+scale+shadow
            canvas.drawCircle(pointerRadius + scaleOffset + shadowRadius + i * centerOffset, height / 2, radius, paint);
        }

        canvas.restore();
    }

}
