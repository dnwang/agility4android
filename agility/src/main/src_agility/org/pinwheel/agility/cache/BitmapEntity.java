package org.pinwheel.agility.cache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.widget.ImageView;

import java.io.*;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
final class BitmapEntity extends CacheEntity<Bitmap> {

    private boolean lowMemoryMode;

    public BitmapEntity(Bitmap bitmap) {
        this(bitmap, false);
    }

    public BitmapEntity(boolean lowMemoryMode) {
        this(null, lowMemoryMode);
    }

    public BitmapEntity(Bitmap bitmap, boolean lowMemoryMode) {
        super(bitmap);
        this.lowMemoryMode = lowMemoryMode;
    }

    private BitmapFactory.Options getOptions() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        if (lowMemoryMode) {
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inPurgeable = true;
            options.inInputShareable = true;
        }
        return options;
    }

    @Override
    protected void decodeFrom(InputStream inputStream) {
        if (inputStream == null) {
            return;
        }
        obj = BitmapFactory.decodeStream(inputStream, null, getOptions());
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * According fixed bound
     */
    @Deprecated
    protected void decodeStreamByFixedBound(InputStream inputStream, int fixedWidth, int fixedHeight) {
        // FIXME
        decodeStreamByMaxBound(inputStream, fixedWidth, fixedHeight);
    }

    /**
     * According max bound, auto resize
     */
    protected void decodeStreamByMaxBound(InputStream inputStream, int maxWidth, int maxHeight) {
        byte[] bytes = stream2Byte(inputStream);
        if (bytes == null || bytes.length == 0) {
            return;
        }
        BitmapFactory.Options options = getOptions();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        options.inSampleSize = findSampleSize(options.outWidth, options.outHeight, maxWidth, maxHeight);
        options.inJustDecodeBounds = false;
        obj = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
    }

//    @Deprecated
//    protected void decodeFrom(InputStream inputStream, ImageView.ScaleType scaleType, int maxWidth, int maxHeight) {
//        if (inputStream == null || scaleType == null || maxWidth < 0 || maxHeight < 0) {
//            return;
//        }
//        byte[] bytes = stream2Byte(inputStream);
//        if (bytes == null) {
//            return;
//        }
//        BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
//        // If we have to resize this image, first get the natural bounds.
//        decodeOptions.inJustDecodeBounds = true;
//        BitmapFactory.decodeByteArray(bytes, 0, bytes.length, decodeOptions);
//        int actualWidth = decodeOptions.outWidth;
//        int actualHeight = decodeOptions.outHeight;
//        // Then compute the dimensions we would ideally like to decode to.
//        int desiredWidth = getResizedDimension(maxWidth, maxHeight, actualWidth, actualHeight, scaleType);
//        int desiredHeight = getResizedDimension(maxHeight, maxWidth, actualHeight, actualWidth, scaleType);
//        // Decode to the nearest power of two scaling factor.
//        decodeOptions.inJustDecodeBounds = false;
//        decodeOptions.inSampleSize = findBestSampleSize(actualWidth, actualHeight, desiredWidth, desiredHeight);
//        Bitmap tempBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, decodeOptions);
//        // If necessary, scale down to the maximal acceptable size.
//        if (tempBitmap != null && desiredWidth > 0 && desiredHeight > 0 && (tempBitmap.getWidth() > desiredWidth || tempBitmap.getHeight() > desiredHeight)) {
//            obj = Bitmap.createScaledBitmap(tempBitmap, desiredWidth, desiredHeight, true);
//            tempBitmap.recycle();
//        } else {
//            obj = tempBitmap;
//        }
//    }

    protected void decodeFrom(InputStream inputStream, ImageLoaderOptions options) {
        if (options.getFixedWidth() > 0 && options.getFixedHeight() > 0) {
            decodeStreamByFixedBound(inputStream, options.getFixedWidth(), options.getFixedHeight());
        } else {
            decodeStreamByMaxBound(inputStream, options.getMaxWidth(), options.getMaxHeight());
        }
    }

    @Override
    protected int sizeOf() {
        return getBitmapSize();
    }

    @Deprecated
    @Override
    protected InputStream getInputStream() {
        if (get() == null) {
            return null;
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        get().compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    protected final int getBitmapSize() {
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

    protected final int getResizedDimension(int maxPrimary, int maxSecondary, int actualPrimary, int actualSecondary, ImageView.ScaleType scaleType) {
        // If no dominant value at all, just return the actual.
        if ((maxPrimary == 0) && (maxSecondary == 0)) {
            return actualPrimary;
        }

        // If ScaleType.FIT_XY fill the whole rectangle, ignore ratio.
        if (scaleType == ImageView.ScaleType.FIT_XY) {
            if (maxPrimary == 0) {
                return actualPrimary;
            }
            return maxPrimary;
        }

        // If primary is unspecified, scale primary to match secondary's scaling ratio.
        if (maxPrimary == 0) {
            double ratio = (double) maxSecondary / (double) actualSecondary;
            return (int) (actualPrimary * ratio);
        }

        if (maxSecondary == 0) {
            return maxPrimary;
        }

        double ratio = (double) actualSecondary / (double) actualPrimary;
        int resized = maxPrimary;

        // If ScaleType.CENTER_CROP fill the whole rectangle, preserve aspect ratio.
        if (scaleType == ImageView.ScaleType.CENTER_CROP) {
            if ((resized * ratio) < maxSecondary) {
                resized = (int) (maxSecondary / ratio);
            }
            return resized;
        }

        if ((resized * ratio) > maxSecondary) {
            resized = (int) (maxSecondary / ratio);
        }
        return resized;
    }

    protected final int findSampleSize(int actualWidth, int actualHeight, int desiredWidth, int desiredHeight) {
        float wr = actualWidth * 1.0f / desiredWidth;
        float hr = actualHeight * 1.0f / desiredHeight;
        float ratio = Math.min(wr, hr);
        return Math.round(ratio);
    }

    @Deprecated
    protected final int findBestSampleSize(int actualWidth, int actualHeight, int desiredWidth, int desiredHeight) {
        double wr = (double) actualWidth / desiredWidth;
        double hr = (double) actualHeight / desiredHeight;
        double ratio = Math.min(wr, hr);
        float n = 1.0f;
        while ((n * 2) <= ratio) {
            n *= 2;
        }
        return (int) n;
    }

    protected final byte[] stream2Byte(InputStream inputStream) {
        byte[] content = null;
        BufferedInputStream in = null;
        ByteArrayOutputStream out = null;
        try {
            in = new BufferedInputStream(inputStream);
            out = new ByteArrayOutputStream(1024);
            byte[] temp = new byte[1024];
            int size = 0;
            while ((size = in.read(temp)) != -1) {
                out.write(temp, 0, size);
            }
            content = out.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return content;
    }

}
