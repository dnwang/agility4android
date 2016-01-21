package org.pinwheel.demo4agility.test;

import android.content.Context;
import android.view.View;
import org.pinwheel.agility.view.SweetCircularView;

public class GalleryAnimatorAdapter implements SweetCircularView.OnItemSwitchListener {

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
    public void onItemSelected(SweetCircularView v, int dataIndex) {
        value = 0;

        View currentView = v.getItemView(dataIndex);
        currentView.setScaleX(1);
        currentView.setScaleY(1);
        currentView.setRotationY(0);
        currentView.setAlpha(1);


        int l = v.getRecycleItemSize() / 2;
        int a = 2;
        while (a <= l) {
            v.getItemView(dataIndex - a).setScaleX(zoomX);
            v.getItemView(v.cycleItemIndex(dataIndex - a)).setScaleY(zoomY);
            v.getItemView(v.cycleItemIndex(dataIndex - a)).setRotationY(angle + angle);
            v.getItemView(v.cycleItemIndex(dataIndex - a)).setAlpha(alpha);

            v.getItemView(dataIndex + a).setScaleX(zoomX);
            v.getItemView(dataIndex + a).setScaleY(zoomY);
            v.getItemView(dataIndex + a).setRotationY(-angle - angle);
            v.getItemView(dataIndex + a).setAlpha(alpha);
            a++;
        }
        v.getItemView(dataIndex - 1).setScaleX(zoomX);
        v.getItemView(dataIndex - 1).setScaleY(zoomY);
        v.getItemView(dataIndex - 1).setRotationY(angle);
        v.getItemView(dataIndex - 1).setAlpha(alpha);

        v.getItemView(dataIndex + 1).setScaleX(zoomX);
        v.getItemView(dataIndex + 1).setScaleY(zoomY);
        v.getItemView(dataIndex + 1).setRotationY(-angle);
        v.getItemView(dataIndex + 1).setAlpha(alpha);
    }

    @Override
    public void onItemScrolled(SweetCircularView v, int dataIndex, float offset) {
        final int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.5);
        value += offset;
        float temp = Math.min(width, Math.abs(value));
        View currentView = v.getItemView(dataIndex);
        float percent = Math.min(1, Math.max(0.0f, temp / width));

        View preView = v.getItemView(dataIndex - 1);

        View lastView = v.getItemView(dataIndex + 1);

        if (value > 0) {//右

            currentView.setScaleX(1 - percent * (1 - zoomX));
            currentView.setScaleY(1 - percent * (1 - zoomY));
            currentView.setRotationY(-(angle * percent));
            currentView.setAlpha(1);

            int l = v.getRecycleItemSize() / 2;
            int a = 2;
            while (a <= l) {
                v.getItemView(dataIndex - a).setScaleX((zoomX + percent * (1 - zoomX)));
                v.getItemView(dataIndex - a).setScaleY(zoomY + percent * (1 - zoomY));
                v.getItemView(dataIndex - a).setRotationY((angle + angle - (angle * percent)));
                v.getItemView(dataIndex - a).setAlpha(alpha);

                v.getItemView(dataIndex + a).setScaleX((zoomX - percent * (1 - zoomX)));
                v.getItemView(dataIndex + a).setScaleY((zoomY - percent * (1 - zoomY)));
                v.getItemView(dataIndex + a).setRotationY((-angle - angle + (angle * percent)));
                v.getItemView(dataIndex + a).setAlpha(alpha);
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

            int l = v.getRecycleItemSize() / 2;
            int a = 2;
            while (a <= l) {
                v.getItemView(dataIndex - a).setScaleX((zoomX - percent * (1 - zoomX)));
                v.getItemView(dataIndex - a).setScaleY(zoomY - percent * (1 - zoomY));
                v.getItemView(dataIndex - a).setRotationY((angle + angle + (angle * percent)));
                v.getItemView(dataIndex - a).setAlpha(alpha);

                v.getItemView(dataIndex + a).setScaleX((zoomX + percent * (1 - zoomX)));
                v.getItemView(dataIndex + a).setScaleY((zoomY + percent * (1 - zoomY)));
                v.getItemView(dataIndex + a).setRotationY((-angle - angle + (angle * percent)));
                v.getItemView(dataIndex + a).setAlpha(alpha);
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

    float value;

}