package org.pinwheel.demo4agility.test;

import android.content.Context;
import android.view.View;

import org.pinwheel.agility.view.SweetCircularView;

public class GalleryAnimatorAdapter extends SweetCircularView.AnimatorAdapter {

    private Context context = null;

    public GalleryAnimatorAdapter(Context context) {
        this.context = context;
    }


    int lastIndex;
    //缩放大小
    private float zoomX = 0.8f, zoomY = 0.8f;
    //倾斜角度
    private int angle = 20;
    //透敏度
    private float alpha = 0.8f;

    public void setZoomX(float zoomX) {
        this.zoomX = zoomX;
    }

    public float getZoomX() {
        return zoomX;
    }

    public void setZoomY(float zoomY) {
        this.zoomY = zoomY;
    }

    public float getZoomY() {
        return zoomY;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }

    public int getAngle() {
        return angle;
    }

    @Override
    public void onItemSelected(int newItemIndex, int oldItemIndex) {
        value = 0;
        lastIndex = newItemIndex;

        View currentView = getItemView(newItemIndex);
        currentView.setScaleX(1);
        currentView.setScaleY(1);
        currentView.setRotationY(0);
        currentView.setAlpha(1);


        int l = getView().getRecycleItemSize() / 2;
        int a = 2;
        while (a <= l) {
            getItemView(cycleItemIndex(newItemIndex - a)).setScaleX(zoomX);
            getItemView(cycleItemIndex(newItemIndex - a)).setScaleY(zoomY);
            getItemView(cycleItemIndex(newItemIndex - a)).setRotationY(angle + angle);
            getItemView(cycleItemIndex(newItemIndex - a)).setAlpha(alpha);

            getItemView(cycleItemIndex(newItemIndex + a)).setScaleX(zoomX);
            getItemView(cycleItemIndex(newItemIndex + a)).setScaleY(zoomY);
            getItemView(cycleItemIndex(newItemIndex + a)).setRotationY(-angle - angle);
            getItemView(cycleItemIndex(newItemIndex + a)).setAlpha(alpha);
            a++;
        }
        getItemView(cycleItemIndex(newItemIndex - 1)).setScaleX(zoomX);
        getItemView(cycleItemIndex(newItemIndex - 1)).setScaleY(zoomY);
        getItemView(cycleItemIndex(newItemIndex - 1)).setRotationY(angle);
        getItemView(cycleItemIndex(newItemIndex - 1)).setAlpha(alpha);

        getItemView(cycleItemIndex(newItemIndex + 1)).setScaleX(zoomX);
        getItemView(cycleItemIndex(newItemIndex + 1)).setScaleY(zoomY);
        getItemView(cycleItemIndex(newItemIndex + 1)).setRotationY(-angle);
        getItemView(cycleItemIndex(newItemIndex + 1)).setAlpha(alpha);
    }

    float value;

    @Override
    public void onItemScrolled(int itemIndex, float offset) {

        final int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.5);
        value += offset;
        float temp = Math.min(width, Math.abs(value));
        View currentView = getItemView(itemIndex);
        float percent = Math.min(1, Math.max(0.0f, temp / width));

        View preView = getItemView(cycleItemIndex(itemIndex - 1));

        View lastView = getItemView(cycleItemIndex(itemIndex + 1));

        if (value > 0) {//右

            currentView.setScaleX(1 - percent * (1 - zoomX));
            currentView.setScaleY(1 - percent * (1 - zoomY));
            currentView.setRotationY(-(angle * percent));
            currentView.setAlpha(1);

            int l = getView().getRecycleItemSize() / 2;
            int a = 2;
            while (a <= l) {
                getItemView(cycleItemIndex(itemIndex - a)).setScaleX((zoomX + percent * (1 - zoomX)));
                getItemView(cycleItemIndex(itemIndex - a)).setScaleY(zoomY + percent * (1 - zoomY));
                getItemView(cycleItemIndex(itemIndex - a)).setRotationY((angle + angle - (angle * percent)));
                getItemView(cycleItemIndex(itemIndex - a)).setAlpha(alpha);

                getItemView(cycleItemIndex(itemIndex + a)).setScaleX((zoomX - percent * (1 - zoomX)));
                getItemView(cycleItemIndex(itemIndex + a)).setScaleY((zoomY - percent * (1 - zoomY)));
                getItemView(cycleItemIndex(itemIndex + a)).setRotationY((-angle - angle + (angle * percent)));
                getItemView(cycleItemIndex(itemIndex + a)).setAlpha(alpha);
                a++;
            }

            preView.setScaleX(zoomX + percent * (1 - zoomX));
            preView.setScaleY(zoomY + percent * (1 - zoomY));
            preView.setRotationY(angle - (angle * percent));
            preView.setAlpha(alpha);

            lastView.setScaleX(zoomX - percent * (1 - zoomX));
            lastView.setScaleY(zoomY - percent * (1 - zoomY));
            lastView.setRotationY(-angle + (angle * percent));
            lastView.setAlpha(alpha);

        } else if (value < 0) {//左
            currentView.setScaleX(1 - percent * (1 - zoomX));
            currentView.setScaleY(1 - percent * (1 - zoomY));
            currentView.setRotationY((angle * percent));
            currentView.setAlpha(1);

            int l = getView().getRecycleItemSize() / 2;
            int a = 2;
            while (a <= l) {
                getItemView(cycleItemIndex(itemIndex - a)).setScaleX((zoomX - percent * (1 - zoomX)));
                getItemView(cycleItemIndex(itemIndex - a)).setScaleY(zoomY - percent * (1 - zoomY));
                getItemView(cycleItemIndex(itemIndex - a)).setRotationY((angle + angle + (angle * percent)));
                getItemView(cycleItemIndex(itemIndex - a)).setAlpha(alpha);

                getItemView(cycleItemIndex(itemIndex + a)).setScaleX((zoomX + percent * (1 - zoomX)));
                getItemView(cycleItemIndex(itemIndex + a)).setScaleY((zoomY + percent * (1 - zoomY)));
                getItemView(cycleItemIndex(itemIndex + a)).setRotationY((-angle - angle + (angle * percent)));
                getItemView(cycleItemIndex(itemIndex + a)).setAlpha(alpha);
                a++;
            }

            preView.setScaleX(zoomX - percent * (1 - zoomX));
            preView.setScaleY(zoomY - percent * (1 - zoomY));
            preView.setRotationY(angle + (angle * percent));
            preView.setAlpha(alpha);

            lastView.setScaleX(zoomX + percent * (1 - zoomX));
            lastView.setScaleY(zoomY + percent * (1 - zoomY));
            lastView.setRotationY(-angle + (angle * percent));
            lastView.setAlpha(alpha);

        }

    }
}