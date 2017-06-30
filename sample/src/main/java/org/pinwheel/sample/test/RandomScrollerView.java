package org.pinwheel.sample.test;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import org.pinwheel.agility.util.UIUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Copyright (C), 2017 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 * @version 2017/6/27,15:41
 * @see
 */
public final class RandomScrollerView extends View {

    public RandomScrollerView(Context context) {
        super(context);
        init();
    }

    public RandomScrollerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RandomScrollerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    long lastTime;

    private final Runnable looper = new Runnable() {
        @Override
        public void run() {
            long time = System.currentTimeMillis();
            if (lastTime != 0) {
                onUpdate((int) (time - lastTime));
            }
            lastTime = time;
            invalidate();

            if (v_curr != 0 || a != 0) {
                postDelayed(looper, 10);
            } else {
                lastTime = 0;
            }
        }
    };

    private final Paint txtPaint = new Paint();

    private void init() {
        txtPaint.setTextSize(UIUtils.dip2px(getContext(), 16));// dp
        txtPaint.setColor(Color.WHITE);// color
        txtPaint.setFakeBoldText(true);// bold
        txtPaint.setAntiAlias(true);
        txtPaint.setTextAlign(Paint.Align.LEFT);

        // default
        setTextList(Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"));
    }

    public void start() {
        v_curr = 0;
        a = 0.005f;// /ms (dt)
        v_max = 1f;// px/ms (dt)
        lastTime = 0;
        removeCallbacks(looper);
        post(looper);
    }

    public void stop(int target) {
        removeCallbacks(looper);

        final Node node = nodeList.get(target);
        final float dy;
        final float dCenter = Math.abs(getMeasuredHeight() / 2 - node.y);
        if (node.y > getMeasuredHeight() / 2) {
            dy = totalNodeHeight - dCenter;
        } else {
            dy = dCenter;
        }

        ValueAnimator animator = ValueAnimator.ofFloat(0, dy + totalNodeHeight);
        animator.setDuration(2000);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            float lastDy;

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float dy = (float) animation.getAnimatedValue();
                cycleMove(dy - lastDy);
                invalidate();
                lastDy = dy;
            }
        });
        animator.start();
    }

    private float v_max;
    private float v_curr;
    private float a;

    private void onUpdate(final int dt) {
        v_curr += (a * dt);
        v_curr = Math.max(0f, Math.min(v_curr, v_max));
        cycleMove(v_curr * dt);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        final int size = nodeList.size();
        for (int i = 0; i < size; i++) {
            Node node = nodeList.get(i);
            canvas.drawText(node.text,
                    (getMeasuredWidth() - node.width) / 2,
                    node.y - node.height / 2 + space,
                    txtPaint);
        }
    }

    private final int space = 20;// divider height
    private int totalNodeHeight;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        totalNodeHeight = 0;
        final int size = nodeList.size();
        for (int i = 0; i < size; i++) {
            Node node = nodeList.get(i);
            node.y = getMeasuredHeight() / 2;
            node.y += totalNodeHeight;

            totalNodeHeight += (node.height + space);
        }
        cycleMove(0);
    }

    private void cycleMove(final float d) {
        for (Node node : nodeList) {
            node.y += d;
            if (node.y > (getMeasuredHeight() + totalNodeHeight) / 2) {
                node.y -= totalNodeHeight;
            }
        }
    }

    private final List<Node> nodeList = new ArrayList<>();

    public void setTextList(@NonNull final List<String> textList) {
        nodeList.clear();
        final Rect bounds = new Rect();
        for (String text : textList) {
            Node node = new Node();
            nodeList.add(node);
            node.text = text;
            txtPaint.getTextBounds(text, 0, text.length(), bounds);
            node.width = bounds.width();
            node.height = bounds.height();
        }
    }

    private static final class Node {
        String text;
        int width, height;
        float y;
    }

}