package org.pinwheel.agility.cache.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Build;

import org.pinwheel.agility.cache.ObjectEntity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
final class BitmapEntity extends ObjectEntity<Bitmap> {

    private Bitmap.Config config;

    public BitmapEntity() {
        this(Bitmap.Config.ARGB_8888);
    }

    public BitmapEntity(Bitmap.Config config) {
        this.config = config;
    }

    private BitmapFactory.Options getOptions() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = this.config;
        options.inPurgeable = true;
        options.inInputShareable = true;
        return options;
    }

    @Override
    protected InputStream getInputStream() {
        if (get() == null) {
            return null;
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        get().compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    @Override
    public void decodeFrom(InputStream inputStream) {
        if (inputStream == null) {
            return;
        }
        setObj(BitmapFactory.decodeStream(inputStream, null, getOptions()));
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void decodeFrom(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return;
        }
        setObj(BitmapFactory.decodeByteArray(bytes, 0, bytes.length, getOptions()));
    }

    @Deprecated
    @Override
    public void decodeFrom(Bitmap obj) {
        throw new UnsupportedOperationException();
    }

    protected void decodeFrom(byte[] bytes, BitmapReceiver.Options options) {
        if (options.getFixedWidth() > 0 && options.getFixedHeight() > 0) {
            decodeByFixedBound(bytes, options.getFixedWidth(), options.getFixedHeight());
        } else if (options.getMaxWidth() < 0 || options.getMaxHeight() < 0) {
            decodeFrom(bytes);
        } else {
            if (options instanceof ViewReceiver.Options) {
                decodeByMaxBound(bytes, options.getMaxWidth(), options.getMaxHeight());
                // TODO: 1/7/16  else something to do
            } else {
                decodeByMaxBound(bytes, options.getMaxWidth(), options.getMaxHeight());
            }
        }
    }

    /**
     * According fixed bound
     */
    @Deprecated
    protected void decodeByFixedBound(byte[] bytes, int fixedWidth, int fixedHeight) {
        decodeByMaxBound(bytes, (int) (fixedWidth * 1.5), (int) (fixedHeight * 1.5));
        Bitmap bitmap = get();
        if (bitmap != null) {
            setObj(ThumbnailUtils.extractThumbnail(bitmap, fixedWidth, fixedHeight, ThumbnailUtils.OPTIONS_RECYCLE_INPUT));
        }
    }

    /**
     * According max bound, auto resize
     */
    protected void decodeByMaxBound(byte[] bytes, int maxWidth, int maxHeight) {
        if (maxWidth < 0 || maxHeight < 0) {
            return;
        }
        if (bytes == null || bytes.length == 0) {
            return;
        }
        BitmapFactory.Options options = getOptions();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        options.inSampleSize = computeSampleSize(options, -1, maxWidth * maxHeight);
        options.inJustDecodeBounds = false;
        setObj(BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options));
    }

    @Override
    protected int sizeOf() {
        Bitmap bitmap = get();
        if (bitmap == null || bitmap.isRecycled()) {
            return 0;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return bitmap.getAllocationByteCount();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getByteCount();
        } else {
            return bitmap.getRowBytes() * bitmap.getHeight();
        }
    }

    private static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

}
