package org.pinwheel.agility.animation;

import android.util.Log;
import android.view.View;

import org.pinwheel.agility.view.SweetCircularView;

public class SimpleCircularAnimator implements SweetCircularView.OnItemSwitchListener {

    private static final String TAG = SimpleCircularAnimator.class.getSimpleName();

    private float scale = 0.8f;
    private float alpha = 0.8f;

    public SimpleCircularAnimator() {
        this.scale = 0.8f;
        this.alpha = 0.8f;
    }

    public SimpleCircularAnimator scale(float scale) {
        this.scale = Math.min(1.0f, Math.max(scale, 0));
        return this;
    }

    public SimpleCircularAnimator alpha(float alpha) {
        this.alpha = Math.min(1.0f, Math.max(alpha, 0));
        return this;
    }

    @Override
    public void onItemSelected(SweetCircularView v, int dataIndex) {
        allOffset = 0;
        final int currentItemIndex = v.getCurrentItemIndex();
        final int sizeOfSideItem = v.getRecycleItemSize() / 2;
        View item;
        // center
        item = v.getView(currentItemIndex);
        if (item != null) {
            item.setScaleX(1.0f);
            item.setScaleY(1.0f);
        }
        for (int direction = -1; direction < 2; direction += 2) {
            // left(direction = -1),right(direction = 1)
            for (int i = 1; i <= sizeOfSideItem; i++) {
                item = v.getView(v.cycleItemIndex(currentItemIndex + direction * i));
                if (item != null) {
                    item.setScaleX(scale);
                    item.setScaleY(scale);
                    item.setAlpha(alpha);
                }
            }
        }
    }

    private int lastDataIndex;

    @Override
    public void onItemScrolled(SweetCircularView v, int dataIndex, float offset) {
        if (dataIndex != lastDataIndex) {
            allOffset = 0;
        }
        lastDataIndex = dataIndex;
        final float percent = getPercent((v.getMeasuredWidth() - v.getLeftIndent() - v.getRightIndent()) * 0.5f, offset);

        // center
        final int currentItemIndex = v.getCurrentItemIndex();
        Log.e(TAG, "onItemScrolled: percent:" + percent + ", currentItemIndex: " + currentItemIndex);
        View item;
        float scale;
        float alpha;
        item = v.getView(currentItemIndex);
        if (item != null) {
            float p = Math.abs(percent);
            scale = 1 - ((1 - this.scale) * p);
            item.setScaleX(scale);
            item.setScaleY(scale);
            alpha = 1 - ((1 - this.alpha) * p);
            item.setAlpha(alpha);
        }

        float p = Math.abs(percent);
        int itemIndex = percent > 0 ? currentItemIndex + 1 : currentItemIndex - 1;
        item = v.getView(v.cycleItemIndex(itemIndex));
        if (item != null) {
            scale = this.scale + ((1 - this.scale) * p);
            item.setScaleX(scale);
            item.setScaleY(scale);
            alpha = this.alpha + ((1 - this.alpha) * p);
            item.setAlpha(alpha);
        }
    }

    private float allOffset;

    private float getPercent(float max, float offset) {
        allOffset += offset;
        float absAllOffset = Math.abs(allOffset);
        float percent = Math.min(1.0f, Math.max(absAllOffset / max, 0.0f));
        return allOffset > 0 ? percent : -percent;
    }

}