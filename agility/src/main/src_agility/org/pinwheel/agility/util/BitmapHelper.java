package org.pinwheel.agility.util;

import android.graphics.*;
import android.media.ThumbnailUtils;

public final class BitmapHelper {

    private BitmapHelper() {

    }

    /**
     * 图片去色,返回灰度图片
     *
     * @param source
     * @return
     */
    public static Bitmap setGrayscale(Bitmap source) {
        int width, height;
        height = source.getHeight();
        width = source.getWidth();
        Bitmap bmpGrayScale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayScale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(source, 0, 0, paint);
        source.recycle();
        return bmpGrayScale;
    }

    /**
     * 设置透明度
     *
     * @param source
     * @param number
     * @return
     */
    @Deprecated
    public static Bitmap setAlpha(Bitmap source, int number) {
        int[] argb = new int[source.getWidth() * source.getHeight()];
        source.getPixels(argb, 0, source.getWidth(), 0, 0, source.getWidth(), source.getHeight());
        // 获得图片的ARGB值
        number = number * 255 / 100;
        for (int i = 0; i < argb.length; i++) {
            argb[i] = (number << 24) | (argb[i] & 0x00FFFFFF);
            // 修改最高2位的值
        }
        Bitmap newBitmap = Bitmap.createBitmap(argb, source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        source.recycle();
        return newBitmap;
    }

    /**
     * 放缩
     *
     * @param source
     * @param width
     * @param height
     * @return
     */
    public static Bitmap setScale(Bitmap source, int width, int height) {
        if (source == null || width < 1 || height < 1) {
            return null;
        }
        int w = source.getWidth();
        int h = source.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) width / w);
        float scaleHeight = ((float) height / h);
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(source, 0, 0, w, h, matrix, true);
        source.recycle();
        return bitmap;
    }

    /**
     * 获取本地文件缩略图
     *
     * @param path
     * @param width
     * @param height
     * @return
     */
    public static Bitmap getBitmapThumbnail(String path, int width, int height) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inJustDecodeBounds = false; // 设为 false
        int h = options.outHeight;
        int w = options.outWidth;
        int scaleWidth = w / width;
        int scaleHeight = h / height;
        int scale = 1;
        if (scaleWidth < scaleHeight) {
            scale = scaleWidth;
        } else {
            scale = scaleHeight;
        }
        if (scale <= 0) {
            scale = 1;
        }
        options.inSampleSize = scale;
        bitmap = BitmapFactory.decodeFile(path, options);
        return bitmap;
    }

    /**
     * 获取bitmap缩略图
     *
     * @param source
     * @param width
     * @param height
     * @return
     */
    public static Bitmap getBitmapThumbnail(Bitmap source, int width, int height) {
        return ThumbnailUtils.extractThumbnail(source, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
    }

}
