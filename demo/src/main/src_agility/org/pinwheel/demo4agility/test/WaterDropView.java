package org.pinwheel.demo4agility.test;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import org.pinwheel.agility.util.UIUtils;
import org.pinwheel.demo4agility.R;

public class WaterDropView extends View {

    private Circle topCircle;
    private Circle bottomCircle;

    private Paint mPaint;
    private Path mPath;
    private float mMaxCircleRadius;
    private float mMinCircleRaidus;
    private Bitmap arrowBitmap;
    private final static int BACK_ANIM_DURATION = 180;
    private final static float STROKE_WIDTH = 2;

    public WaterDropView(Context context) {
        super(context);
        init(context, null);
    }

    public WaterDropView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public WaterDropView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void parseAttrs(Context context, AttributeSet attrs) {
//        if (attrs != null) {
//            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.WaterDropView, 0, 0);
//            try {
//                if (a.hasValue(R.styleable.WaterDropView_waterdrop_color)) {
//                    int waterDropColor = a.getColor(R.styleable.WaterDropView_waterdrop_color, Color.GRAY);
//                    mPaint.setColor(waterDropColor);
//                }
//                if (a.hasValue(R.styleable.WaterDropView_max_circle_radius)) {
//                    mMaxCircleRadius = a.getDimensionPixelSize(R.styleable.WaterDropView_max_circle_radius, 0);
//
//                    topCircle.setRadius(mMaxCircleRadius);
//                    bottomCircle.setRadius(mMaxCircleRadius);
//
//                    topCircle.setX(STROKE_WIDTH + mMaxCircleRadius);
//                    topCircle.setY(STROKE_WIDTH + mMaxCircleRadius);
//
//                    bottomCircle.setX(STROKE_WIDTH + mMaxCircleRadius);
//                    bottomCircle.setY(STROKE_WIDTH + mMaxCircleRadius);
//                }
//                if (a.hasValue(R.styleable.WaterDropView_min_circle_radius)) {
//                    mMinCircleRaidus = a.getDimensionPixelSize(R.styleable.WaterDropView_min_circle_radius, 0);
//                    if (mMinCircleRaidus > mMaxCircleRadius) {
//                        throw new IllegalStateException("Circle's MinRaidus should be equal or lesser than the MaxRadius");
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            } finally {
//                a.recycle();
//            }
//        }

        mPaint.setColor(Color.GRAY);
        mMaxCircleRadius = UIUtils.dip2px(getContext(), 20);
        topCircle.setRadius(mMaxCircleRadius);
        bottomCircle.setRadius(mMaxCircleRadius);
        topCircle.setX(STROKE_WIDTH + mMaxCircleRadius);
        topCircle.setY(STROKE_WIDTH + mMaxCircleRadius);
        bottomCircle.setX(STROKE_WIDTH + mMaxCircleRadius);
        bottomCircle.setY(STROKE_WIDTH + mMaxCircleRadius);
        mMinCircleRaidus = UIUtils.dip2px(getContext(), 5);
    }

    private void init(Context context, AttributeSet attrs) {
        topCircle = new Circle();
        bottomCircle = new Circle();
        mPath = new Path();
        mPaint = new Paint();
        mPaint.setColor(Color.GRAY);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setStrokeWidth(STROKE_WIDTH);
        Drawable drawable = getResources().getDrawable(R.drawable.refresh_arrow);
        arrowBitmap = drawableToBitmap(drawable);
        parseAttrs(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = (int) ((mMaxCircleRadius + STROKE_WIDTH) * 2);
        int height = (int) Math.ceil(bottomCircle.getY() + bottomCircle.getRadius() + STROKE_WIDTH * 2);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        makeBezierPath();
//        mPaint.setColor(Color.RED);
//        mPaint.setAlpha(200);
        canvas.drawPath(mPath, mPaint);
//        mPaint.setColor(Color.GRAY);
//        mPaint.setAlpha(50);
        canvas.drawCircle(topCircle.getX(), topCircle.getY(), topCircle.getRadius(), mPaint);
        canvas.drawCircle(bottomCircle.getX(), bottomCircle.getY(), bottomCircle.getRadius(), mPaint);
//        canvas.drawBitmap(arrowBitmap, topCircle.getX() - topCircle.getRadius(), topCircle.getY() - topCircle.getRadius(), mPaint);
        RectF bitmapArea = new RectF(topCircle.getX() - 0.5f * topCircle.getRadius(), topCircle.getY() - 0.5f * topCircle.getRadius(), topCircle.getX() + 0.5f * topCircle.getRadius(), topCircle.getY() + 0.5f * topCircle.getRadius());
        canvas.drawBitmap(arrowBitmap, null, bitmapArea, mPaint);
        super.onDraw(canvas);
    }

    private void makeBezierPath() {
        mPath.reset();
        double angle = getAngle();
        float top_x1 = (float) (topCircle.getX() - topCircle.getRadius() * Math.cos(angle));
        float top_y1 = (float) (topCircle.getY() + topCircle.getRadius() * Math.sin(angle));

        float top_x2 = (float) (topCircle.getX() + topCircle.getRadius() * Math.cos(angle));
        float top_y2 = top_y1;

        float bottom_x1 = (float) (bottomCircle.getX() - bottomCircle.getRadius() * Math.cos(angle));
        float bottom_y1 = (float) (bottomCircle.getY() + bottomCircle.getRadius() * Math.sin(angle));

        float bottom_x2 = (float) (bottomCircle.getX() + bottomCircle.getRadius() * Math.cos(angle));
        float bottom_y2 = bottom_y1;

        mPath.moveTo(topCircle.getX(), topCircle.getY());

        mPath.lineTo(top_x1, top_y1);

        mPath.quadTo((bottomCircle.getX() - bottomCircle.getRadius()),
                (bottomCircle.getY() + topCircle.getY()) / 2,

                bottom_x1,
                bottom_y1);
        mPath.lineTo(bottom_x2, bottom_y2);

        mPath.quadTo((bottomCircle.getX() + bottomCircle.getRadius()),
                (bottomCircle.getY() + top_y2) / 2,
                top_x2,
                top_y2);

        mPath.close();
    }

    private double getAngle() {
        if (bottomCircle.getRadius() > topCircle.getRadius()) {
            throw new IllegalStateException("bottomCircle's radius must be less than the topCircle's");
        }
        return Math.asin((topCircle.getRadius() - bottomCircle.getRadius()) / (bottomCircle.getY() - topCircle.getY()));
    }

    public Animator createAnimator() {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(1, 0).setDuration(BACK_ANIM_DURATION);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                updateComleteState((float) valueAnimator.getAnimatedValue());
            }
        });
        return valueAnimator;
    }

    public void updateComleteState(float percent) {
        if (percent < 0 || percent > 1) {
            throw new IllegalStateException("completion percent should between 0 and 1!");
        }
        float top_r = (float) (mMaxCircleRadius - 0.25 * percent * mMaxCircleRadius);
        float bottom_r = (mMinCircleRaidus - mMaxCircleRadius) * percent + mMaxCircleRadius;
        float bottomCricleOffset = 2 * percent * mMaxCircleRadius;
        topCircle.setRadius(top_r);
        bottomCircle.setRadius(bottom_r);
        bottomCircle.setY(topCircle.getY() + bottomCricleOffset);
        requestLayout();
        postInvalidate();
    }

    public Circle getTopCircle() {
        return topCircle;
    }

    public Circle getBottomCircle() {
        return bottomCircle;
    }

    public void setIndicatorColor(int color) {
        mPaint.setColor(color);
    }

    public int getIndicatorColor() {
        return mPaint.getColor();
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;

    }

    private class Circle {
        private float x;
        private float y;
        private float radius;
        private int color;

        public float getX() {
            return x;
        }

        public void setX(float x) {
            this.x = x;
        }

        public float getY() {
            return y;
        }

        public void setY(float y) {
            this.y = y;
        }

        public float getRadius() {
            return radius;
        }

        public void setRadius(float radius) {
            this.radius = radius;
        }

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }
    }
}
