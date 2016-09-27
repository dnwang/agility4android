package org.pinwheel.agility.cache.image;

import android.content.res.Resources;
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
        super.dispatch(bitmap);
        View v = reference.get();
        if (v != null) {
            Object options = getOptions();
            if (bitmap == null && options != null && options instanceof Options) {
                dispatch(((Options) options).errorRes);
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

        private int defaultRes;
        private int errorRes;

        private boolean justViewBound;

        protected Options(OptionsBuilder builder) {
            super(builder);
            this.defaultRes = builder.defaultRes;
            this.errorRes = builder.errorRes;
            this.justViewBound = builder.justViewBound;
        }

        public int getDefaultRes() {
            return defaultRes;
        }

        public int getErrorRes() {
            return errorRes;
        }

        public boolean isJustViewBound() {
            return justViewBound;
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
    public static class OptionsBuilder extends BitmapReceiver.OptionsBuilder {

        private int defaultRes;
        private int errorRes;

        private boolean justViewBound;

        public OptionsBuilder() {
            this.defaultRes = -1;
            this.errorRes = -1;
            this.justViewBound = false;
        }

        public boolean isJustViewBound() {
            return justViewBound;
        }

        public OptionsBuilder setDefaultRes(int defaultRes) {
            this.defaultRes = defaultRes;
            return this;
        }

        public OptionsBuilder setErrorRes(int errorRes) {
            this.errorRes = errorRes;
            return this;
        }

        public OptionsBuilder setJustViewBound(boolean justViewBound) {
            this.justViewBound = justViewBound;
            return this;
        }

        public OptionsBuilder setAutoSize(Resources resources) {
            setJustViewBound(true);
            if (resources != null) {
                DisplayMetrics displayMetrics = resources.getDisplayMetrics();
                setMax(displayMetrics.widthPixels / 2, displayMetrics.heightPixels / 2);
            }
            return this;
        }

        @Override
        public OptionsBuilder copy(BitmapReceiver.Options basicOptions) {
            super.copy(basicOptions);
            if (basicOptions instanceof Options) {
                Options options = (Options) basicOptions;
                setDefaultRes(options.defaultRes);
                setErrorRes(options.errorRes);
                setJustViewBound(options.justViewBound);
            }
            return this;
        }

        @Override
        public Options create() {
            return new Options(this);
        }
    }

}
