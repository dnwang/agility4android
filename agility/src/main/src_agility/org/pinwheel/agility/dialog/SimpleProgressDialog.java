package org.pinwheel.agility.dialog;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.pinwheel.agility.util.UIUtils;
import org.pinwheel.agility.view.ProgressCircular;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
public class SimpleProgressDialog extends SweetDialog {

    public static SweetDialog create(Context context, int res_id) {
        return create(context, context.getResources().getString(res_id));
    }

    public static SweetDialog create(Context context, String message) {
        View contentView = createContentView(context, message);
        return new SweetDialog(context, contentView);
    }

    private static View createContentView(Context context, String message) {
        int dp1 = UIUtils.dip2px(context, 1);
        int dp12 = dp1 * 12;

        LinearLayout container = new LinearLayout(context);
        container.setPadding(dp12, dp12, dp12, dp12 + dp1 * 2);
        container.setOrientation(LinearLayout.HORIZONTAL);
        container.setGravity(Gravity.CENTER);
        container.setBackgroundDrawable(new BitmapDrawable(createBackground(context)));

        ProgressCircular progressCircular = new ProgressCircular(context);
        progressCircular.setBarColor(Color.WHITE);

        TextView textView = new TextView(context);
        textView.setPadding(dp12, 0, 0, 0);
        textView.setMaxWidth(dp12 * 18);// normal
        textView.setMinimumWidth(dp12 * 9);// normal
        textView.setGravity(Gravity.CENTER);
//        TextPaint textPaint = textView.getPaint();
//        textPaint.setFakeBoldText(true);// 粗体
        textView.setTextColor(Color.WHITE);
        textView.setText(message);

        container.addView(progressCircular);
        container.addView(textView);
        return container;
    }

    private SimpleProgressDialog(Context context) {
        super(context);
    }

    private static Bitmap createBackground(Context context) {
        int radius_x = UIUtils.dip2px(context, 7);
        int radius_y = UIUtils.dip2px(context, 8);

        int width = UIUtils.dip2px(context, 192);
        int height = UIUtils.dip2px(context, 88);

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setColor(0xAA000000);
//        paint.setMaskFilter(new BlurMaskFilter(1, BlurMaskFilter.Blur.NORMAL));

        RectF rectF = new RectF(0, 0, width - 10, height - 10);
        canvas.drawRoundRect(rectF, radius_x, radius_y, paint);

        return bitmap;
    }

}
