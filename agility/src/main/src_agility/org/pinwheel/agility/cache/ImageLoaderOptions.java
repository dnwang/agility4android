package org.pinwheel.agility.cache;

import java.io.Serializable;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
public final class ImageLoaderOptions implements Serializable {

    public static final int DEFAULT_MAX_WIDTH = 192;
    public static final int DEFAULT_MAX_HEIGHT = 192;

    private int defaultRes;
    private int errorRes;

    private int fixedWidth;
    private int fixedHeight;

    private int maxWidth;
    private int maxHeight;

    private boolean ignoreCache;
    private int networkTimeOut;
    private boolean lowMemoryMode;
    private boolean justViewBounds;

    private ImageLoaderOptions(Builder builder) {
        this.defaultRes = builder.defaultRes;
        this.errorRes = builder.errorRes;
        this.fixedWidth = builder.fixedWidth;
        this.fixedHeight = builder.fixedHeight;
        this.maxWidth = builder.maxWidth;
        this.maxHeight = builder.maxHeight;
        this.networkTimeOut = builder.networkTimeOut;
        this.ignoreCache = builder.ignoreCache;
        this.lowMemoryMode = builder.lowMemoryMode;
        this.justViewBounds = builder.justViewBounds;
    }

    protected String getKey() {
        int result = fixedWidth;
        result = 31 * result + fixedHeight;
        result = 31 * result + maxWidth;
        result = 31 * result + maxHeight;
        return String.valueOf(result);
    }

    public int getMaxWidth() {
        return maxWidth;
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    protected void setMaxSize(int maxWidth, int maxHeight) {
        this.maxWidth = Math.max(0, maxWidth);
        this.maxHeight = Math.max(0, maxHeight);
    }

    public int getFixedWidth() {
        return fixedWidth;
    }

    public int getFixedHeight() {
        return fixedHeight;
    }

    @Deprecated
    protected void setFixedSize(int fixedWidth, int fixedHeight) {
        this.fixedWidth = Math.max(0, fixedWidth);
        this.fixedHeight = Math.max(0, fixedHeight);
    }

    public boolean isIgnoreCache() {
        return ignoreCache;
    }

    public boolean lowMemoryMode() {
        return lowMemoryMode;
    }

    public boolean justViewBounds() {
        return justViewBounds;
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
        private int maxWidth;
        private int maxHeight;

        private int networkTimeOut;
        private boolean ignoreCache;
        private boolean lowMemoryMode;
        private boolean justViewBounds;

        public Builder() {
            maxWidth = DEFAULT_MAX_WIDTH;
            maxHeight = DEFAULT_MAX_HEIGHT;
            networkTimeOut = 30;//30s
            ignoreCache = false;
            lowMemoryMode = true;
            justViewBounds = false;
        }

        @Deprecated
        public Builder fixedSize(int fixedWidth, int fixedHeight) {
            this.fixedWidth = Math.max(0, fixedWidth);
            this.fixedHeight = Math.max(0, fixedHeight);
            return this;
        }

        public Builder maxSize(int maxWidth, int maxHeight) {
            this.maxWidth = Math.max(0, maxWidth);
            this.maxHeight = Math.max(0, maxHeight);
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

        public Builder ignoreCache(boolean is) {
            this.ignoreCache = is;
            return this;
        }

        public Builder lowMemoryMode(boolean is) {
            this.lowMemoryMode = is;
            return this;
        }

        public Builder justViewBounds(boolean is) {
            this.justViewBounds = is;
            return this;
        }

        public ImageLoaderOptions create() {
            return new ImageLoaderOptions(this);
        }
    }

}
