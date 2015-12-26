package org.pinwheel.agility.cache;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.UUID;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
public abstract class BitmapReceiver {

    private String tag;

    private Options options = null;

    public BitmapReceiver() {
        this.tag = UUID.randomUUID().toString().replace("-", "");
    }

    final Options getOptions() {
        return options;
    }

    final void setOptions(Options options) {
        this.options = options;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BitmapReceiver that = (BitmapReceiver) o;
        return tag != null ? tag.equals(that.tag) : that.tag == null;
    }

    @Override
    public int hashCode() {
        return tag != null ? tag.hashCode() : 0;
    }

    public abstract void dispatch(Bitmap bitmap);

    /**
     * Copyright (C), 2015 <br>
     * <br>
     * All rights reserved <br>
     * <br>
     *
     * @author dnwang
     */
    public static class Options implements Serializable {

        @Deprecated
        public int fixedWidth;
        @Deprecated
        public int fixedHeight;

        public int maxWidth;
        public int maxHeight;

        public Bitmap.Config config;

        public Options() {
            this.maxWidth = -1;
            this.maxHeight = -1;
            this.fixedWidth = -1;
            this.fixedHeight = -1;
            this.config = Bitmap.Config.RGB_565;
        }

        public Options(Options options) {
            this.maxWidth = options.maxWidth;
            this.maxHeight = options.maxHeight;
            this.fixedWidth = options.fixedWidth;
            this.fixedHeight = options.fixedHeight;
            this.config = options.config;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Options that = (Options) o;
            return (config == that.config)
                    && (fixedWidth == that.fixedWidth)
                    && fixedHeight == that.fixedHeight
                    && (maxWidth == that.maxWidth)
                    && (maxHeight == that.maxHeight);
        }

        @Override
        public int hashCode() {
            int result = fixedWidth;
            result = 31 * result + fixedHeight;
            result = 31 * result + maxWidth;
            result = 31 * result + maxHeight;
            result = 31 * result + (config != null ? config.hashCode() : 0);
            return result;
        }

    }
}
