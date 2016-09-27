package org.pinwheel.agility.cache.image;

import android.graphics.Bitmap;

import org.pinwheel.agility.util.callback.Action1;

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
    private Action1<Bitmap> action = null;

    public BitmapReceiver() {
        this.tag = UUID.randomUUID().toString().replace("-", "");
    }

    BitmapReceiver(Action1<Bitmap> action) {
        this();
        this.action = action;
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

    public void dispatch(Bitmap bitmap){
       if (null != action){
           action.call(bitmap);
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
    public static class Options implements Serializable {

        @Deprecated
        private int fixedWidth;
        @Deprecated
        private int fixedHeight;

        private int maxWidth;
        private int maxHeight;

        private Bitmap.Config config;

        protected Options(OptionsBuilder builder) {
            this.maxWidth = builder.maxWidth;
            this.maxHeight = builder.maxHeight;
            this.fixedWidth = builder.fixedWidth;
            this.fixedHeight = builder.fixedHeight;
            this.config = builder.config;
        }

        @Deprecated
        public int getFixedWidth() {
            return fixedWidth;
        }

        @Deprecated
        public int getFixedHeight() {
            return fixedHeight;
        }

        public int getMaxWidth() {
            return maxWidth;
        }

        public int getMaxHeight() {
            return maxHeight;
        }

        public Bitmap.Config getConfig() {
            return config;
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

    /**
     * Copyright (C), 2015 <br>
     * <br>
     * All rights reserved <br>
     * <br>
     *
     * @author dnwang
     */
    public static class OptionsBuilder {

        @Deprecated
        private int fixedWidth;
        @Deprecated
        private int fixedHeight;

        private int maxWidth;
        private int maxHeight;

        private Bitmap.Config config;

        public OptionsBuilder() {
            this.fixedWidth = -1;
            this.fixedHeight = -1;
            this.maxWidth = -1;
            this.maxHeight = -1;
            this.config = Bitmap.Config.RGB_565;
        }

        public OptionsBuilder setFixed(int fixedWidth, int fixedHeight) {
            this.fixedWidth = fixedWidth;
            this.fixedHeight = fixedHeight;
            return this;
        }

        public OptionsBuilder setMax(int maxWidth, int maxHeight) {
            this.maxWidth = maxWidth;
            this.maxHeight = maxHeight;
            return this;
        }

        public OptionsBuilder setConfig(Bitmap.Config config) {
            this.config = config;
            return this;
        }

        public OptionsBuilder copy(Options options) {
            setFixed(options.fixedWidth, options.fixedHeight);
            setMax(options.maxWidth, options.maxHeight);
            setConfig(options.config);
            return this;
        }

        public Options create() {
            return new Options(this);
        }
    }

}
