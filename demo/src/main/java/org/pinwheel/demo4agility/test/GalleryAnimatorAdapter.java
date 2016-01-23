package org.pinwheel.demo4agility.test;

import org.pinwheel.agility.view.SweetCircularView;

public class GalleryAnimatorAdapter implements SweetCircularView.OnItemSwitchListener {

    public float scale = 0.8f;
    public float alpha = 0.8f;

    private float value;

    @Override
    public void onItemSelected(SweetCircularView v, int dataIndex) {
    }

    @Override
    public void onItemScrolled(SweetCircularView v, int dataIndex, float offset) {
        final int width = (int) (v.getContext().getResources().getDisplayMetrics().widthPixels * 0.5);
        value += offset;
        float temp = Math.min(width, Math.abs(value));
        float percent = Math.min(1, Math.max(0.0f, temp / width));

    }

}