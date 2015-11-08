package org.pinwheel.agility.cache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.pinwheel.agility.net.Request;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
public final class ImageLoaderOptions {

    public static final int DEFAULT_MAX_WIDTH = 196;
    public static final int DEFAULT_MAX_HEIGHT = 196;

    private int defaultRes;
    private int errorRes;

    private int fixedWidth;
    private int fixedHeight;
    private float scale;
    private int maxWidth;
    private int maxHeight;
    private BitmapFactory.Options options;

    private boolean ignoreCache;
    private int networkTimeOut;

    private ImageLoaderOptions(Builder builder) {
        this.defaultRes = builder.defaultRes;
        this.errorRes = builder.errorRes;
        this.fixedWidth = builder.fixedWidth;
        this.fixedHeight = builder.fixedHeight;
        this.scale = builder.scale;
        this.options = builder.options;
        this.maxWidth = builder.maxWidth;
        this.maxHeight = builder.maxHeight;
        this.networkTimeOut = builder.networkTimeOut;
        this.ignoreCache = builder.ignoreCache;
    }

    public String getKey() {
        int result = fixedWidth;
        result = 31 * result + fixedHeight;
        result = 31 * result + (scale != +0.0f ? Float.floatToIntBits(scale) : 0);
        result = 31 * result + maxWidth;
        result = 31 * result + maxHeight;
        return String.valueOf(result);
    }

    public float getScale() {
        return scale;
    }

    public int getMaxWidth() {
        return maxWidth;
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public int getFixedWidth() {
        return fixedWidth;
    }

    public int getFixedHeight() {
        return fixedHeight;
    }

    public BitmapFactory.Options getBitmapOptions() {
        return options;
    }

    public boolean isIgnoreCache() {
        return ignoreCache;
    }

    public int getDefaultRes() {
        return defaultRes;
    }

    public int getErrorRes() {
        return errorRes;
    }

    public int getNetworkTimeOut() {
        return networkTimeOut;
    }

    /**
     * Builder
     */
    public static final class Builder {

        private int defaultRes;
        private int errorRes;

        private int fixedWidth;
        private int fixedHeight;
        private float scale;
        private int maxWidth;
        private int maxHeight;
        private BitmapFactory.Options options;

        private boolean ignoreCache;
        private int networkTimeOut;

        public Builder() {
            maxWidth = DEFAULT_MAX_WIDTH;
            maxHeight = DEFAULT_MAX_HEIGHT;
            scale = 1f;
            networkTimeOut = 20;//20s

            // reduce memory expenses
            options = new BitmapFactory.Options();
            options.inPurgeable = true;
            options.inInputShareable = true;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
        }

        public Builder fixedBound(int fixedWidth, int fixedHeight) {
            this.fixedWidth = Math.max(0, fixedWidth);
            this.fixedHeight = Math.max(0, fixedHeight);
            return this;
        }

        public Builder scale(float scale, int maxWidth, int maxHeight) {
            this.scale = Math.max(0, Math.min(scale, 1));
            this.maxWidth = Math.max(0, maxWidth);
            this.maxHeight = Math.max(0, maxHeight);
            return this;
        }

        public Builder bitmapOptions(BitmapFactory.Options options) {
            this.options = options;
            return this;
        }

        public Builder defaultRes(int defaultRes) {
            this.defaultRes = defaultRes;
            return this;
        }

        public Builder errorRes(int errorRes) {
            this.errorRes = errorRes;
            return this;
        }

        public Builder networkTimeOut(int timeOut) {
            this.networkTimeOut = Math.max(0, timeOut);
            return this;
        }

        public Builder ignoreCache(boolean is){
            this.ignoreCache = is;
            return this;
        }

        public ImageLoaderOptions create() {
            return new ImageLoaderOptions(this);
        }
    }

}
