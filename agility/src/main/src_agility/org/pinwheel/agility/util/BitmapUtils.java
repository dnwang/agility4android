package org.pinwheel.agility.util;

import android.graphics.*;
import android.media.ThumbnailUtils;

public final class BitmapUtils {

    private BitmapUtils() {

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
        return Bitmap.createBitmap(argb, source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
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
    @Deprecated
    public static Bitmap getBitmapThumbnail(Bitmap source, int width, int height) {
        return ThumbnailUtils.extractThumbnail(source, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
    }

    /**
     * 添加阴影
     *
     * @param bitmap
     * @param radius
     * @return
     */
    public static Bitmap setShadow(Bitmap bitmap, int radius) {
        BlurMaskFilter blurFilter = new BlurMaskFilter(radius, BlurMaskFilter.Blur.OUTER);
        Paint shadowPaint = new Paint();
        shadowPaint.setAlpha(50);
        shadowPaint.setColor(0xff424242);
        shadowPaint.setMaskFilter(blurFilter);
        int[] offsetXY = new int[2];
        Bitmap shadowBitmap = bitmap.extractAlpha(shadowPaint, offsetXY);
        Bitmap shadowImage32 = shadowBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas c = new Canvas(shadowImage32);
        c.drawBitmap(bitmap, -offsetXY[0], -offsetXY[1], null);
        return shadowImage32;
    }

    /**
     * 转换图片成圆形
     *
     * @param bitmap
     * @return
     */
    public static Bitmap toRoundBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float roundPx;
        float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
        if (width <= height) {
            roundPx = width / 2;
            left = 0;
            top = 0;
            right = width;
            bottom = width;
            height = width;
            dst_left = 0;
            dst_top = 0;
            dst_right = width;
            dst_bottom = width;
        } else {
            roundPx = height / 2;
            float clip = (width - height) / 2;
            left = clip;
            right = width - clip;
            top = 0;
            bottom = height;
            width = height;
            dst_left = 0;
            dst_top = 0;
            dst_right = height;
            dst_bottom = height;
        }
        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();
        final Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
        final Rect dst = new Rect((int) dst_left, (int) dst_top, (int) dst_right, (int) dst_bottom);
        final RectF rectF = new RectF(dst);
        paint.setAntiAlias(true);// 设置画笔无锯齿
        canvas.drawARGB(0, 0, 0, 0); // 填充整个Canvas
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);// 画圆角矩形，第一个参数为图形显示区域，第二个参数和第三个参数分别是水平圆角半径和垂直圆角半径。
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));// 设置两张图片相交时的模式,参考http://trylovecatch.iteye.com/blog/1189452
        canvas.drawBitmap(bitmap, src, dst, paint); // 以Mode.SRC_IN模式合并bitmap和已经draw了的Circle
        return output;
    }

    /**
     * 圆角图片
     *
     * @param bitmap
     * @param radius
     * @return
     */
    public static Bitmap toRoundedCornerBitmap(Bitmap bitmap, int radius) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, radius, radius, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    /**
     * 设置图片倒影
     *
     * @param bitmap
     * @param distance
     * @param ratio
     * @return
     */
    public static Bitmap setReflection(Bitmap bitmap, int distance, float ratio) {
        final int reflectionGap = distance;// 图片与倒影间隔距离
        int width = bitmap.getWidth();// 图片的宽度
        int height = bitmap.getHeight();// 图片的高度
        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);// 图片缩放，x轴变为原来的1倍，y轴为-1倍,实现图片的反转
        Bitmap reflectionBitmap = Bitmap.createBitmap(bitmap, 0, height / 2, width, (int) (height * ratio), matrix, false);
        Bitmap withReflectionBitmap = Bitmap.createBitmap(width, (height + (int) (height * ratio) + reflectionGap), Bitmap.Config.ARGB_8888);// 创建标准的Bitmap对象，宽和原图一致，高是原图的1.5倍。
        Canvas canvas = new Canvas(withReflectionBitmap);// 构造函数传入Bitmap对象，为了在图片上画图
        canvas.drawBitmap(bitmap, 0, 0, null);// 画原始图片
        Paint defaultPaint = new Paint();// 画间隔矩形
        defaultPaint.setColor(Color.TRANSPARENT);
        canvas.drawRect(0, height, width, height + reflectionGap, defaultPaint);
        canvas.drawBitmap(reflectionBitmap, 0, height + reflectionGap, null);// 画倒影图片

        // 实现倒影效果
        Paint paint = new Paint();
        LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0,
                withReflectionBitmap.getHeight(), 0x70ffffff, 0x00ffffff,
                Shader.TileMode.MIRROR);
        paint.setShader(shader);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));

        canvas.drawRect(0, height, width, withReflectionBitmap.getHeight(), paint);// 覆盖效果
        return withReflectionBitmap;
    }

}
