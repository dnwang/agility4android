package org.pinwheel.agility.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

import java.lang.ref.SoftReference;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
public class ViewReceiver extends BitmapReceiver {

    private SoftReference<View> reference;

    public ViewReceiver(View view) {
        super();
        this.reference = new SoftReference<>(view);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ViewReceiver that = (ViewReceiver) o;
        return reference.get() == that.reference.get();
    }

    @Override
    public int hashCode() {
        return reference.get() == null ? 0 : reference.get().hashCode();
    }

    public void dispatch(int res) {
        if (res <= 0) {
            return;
        }
        View v = reference.get();
        if (v != null) {
            if (v instanceof ImageView) {
                ((ImageView) v).setImageResource(res);
            } else {
                v.setBackgroundResource(res);
            }
        }
    }

    @Override
    public void dispatch(Bitmap bitmap) {
        View v = reference.get();
        if (v != null) {
            if (bitmap == null && getOptions() instanceof Options) {
                dispatch(((Options) getOptions()).errorRes);
                return;
            }
            if (v instanceof ImageView) {
                ((ImageView) v).setImageBitmap(bitmap);
            } else {
                v.setBackgroundDrawable(new BitmapDrawable(bitmap));
            }
        }
    }

    /**
     * Copyright (C), 2015 <br>
     * <br>
     * All rights reserved <br>
     * <br>
     *
     * @author dnwang
     */
    public static class Options extends BitmapReceiver.Options {

        public int defaultRes;
        public int errorRes;

        public boolean justViewBound;

        @Deprecated
        public Options() {
            super();
            this.defaultRes = -1;
            this.errorRes = -1;
            this.justViewBound = false;
        }

        public Options(Context context) {
            this();
            this.justViewBound = true;
            if (context != null) {
                DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
                this.maxWidth = displayMetrics.widthPixels / 2;
                this.maxHeight = displayMetrics.heightPixels / 2;
            }
        }

        public Options(Options options) {
            super(options);
            this.defaultRes = options.defaultRes;
            this.errorRes = options.errorRes;
            this.justViewBound = options.justViewBound;
        }

    }

}
