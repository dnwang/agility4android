package org.pinwheel.agility.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.widget.ImageView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

public final class BitmapLoader {

    public static BitmapLoader instance;

    private BitmapCache mCache;
    private BitmapCache mCacheWithThumbnail;

    private ImageLoader mImageLoader;
    private RequestQueue mQueue;

    private int default_src, error_src;
    private int default_thumbnail_width, default_thumbnail_height;

    public static synchronized BitmapLoader init(Context context) {
        if (instance == null) {
            instance = new BitmapLoader(context);
        }
        return instance;
    }

    public static synchronized void release() {
        if (instance != null) {
            instance.mQueue.cancelAll(new RequestQueue.RequestFilter() {
                @Override
                public boolean apply(Request<?> request) {
                    return true;
                }
            });
            instance.mCache.clear();
            instance.mCache = null;
            instance.mCacheWithThumbnail.clear();
            instance.mCacheWithThumbnail = null;
            instance.mImageLoader = null;
            instance.mQueue = null;
            instance = null;
        }
    }

    public static BitmapLoader getInstance() {
        return instance;
    }

    private BitmapLoader(Context context) {
        this.mQueue = Volley.newRequestQueue(context);
        this.mCache = new BitmapCache(10 * 1024 * 1024);
        this.mCacheWithThumbnail = new BitmapCache(5 * 1024 * 1024);
        this.mImageLoader = new ImageLoader(mQueue, mCache);
    }

    public BitmapLoader setDefaultImage(int default_src, int error_src) {
        this.default_src = default_src;
        this.error_src = error_src;
        return instance;
    }

    public BitmapLoader setDefaultThumbnail(int default_thumbnail_width, int default_thumbnail_height) {
        this.default_thumbnail_width = default_thumbnail_width;
        this.default_thumbnail_height = default_thumbnail_height;
        return instance;
    }

    public void setImageFromNetwork(NetworkImageView imageView, String img_url) {
        this.setImageFromNetwork(imageView, img_url, default_src, error_src);
    }

    public void setImageFromNetwork(NetworkImageView imageView, String img_url, int default_src, int error_src) {
        imageView.setDefaultImageResId(default_src);
        imageView.setErrorImageResId(error_src);
        imageView.setImageUrl(img_url, mImageLoader);
    }

    public void setImageFromNetwork(String img_url, ImageLoader.ImageListener listener) {
        mImageLoader.get(img_url, listener);
    }


    public void setThumbnailFromNative(ImageView imageView, String img_path) {
        this.setThumbnailFromNative(imageView, img_path, default_thumbnail_width, default_thumbnail_height);
    }

    public void setThumbnailFromNative(ImageView imageView, String img_path, int width, int height) {
        this.setThumbnailFromNative(imageView, img_path, width, height, default_src, error_src);
    }

    public void setThumbnailFromNative(final ImageView imageView, final String img_path, final int width, final int height, int default_src, final int error_src) {
        if (TextUtils.isEmpty(img_path)) {
            return;
        }
        Bitmap cache = mCacheWithThumbnail.getBitmap(img_path);
        if (cache != null) {
            imageView.setImageBitmap(cache);
            return;
        } else {
            imageView.setImageResource(default_src);
        }
        new Thread() {
            @Override
            public void run() {
                final Bitmap thumbnail = BitmapUtils.getBitmapThumbnail(img_path, width, height);
                imageView.post(new Runnable() {
                    @Override
                    public void run() {
                        if (thumbnail == null) {
                            imageView.setImageResource(error_src);
                        } else {
                            mCacheWithThumbnail.putBitmap(img_path, thumbnail);
                            imageView.setImageBitmap(thumbnail);
                        }
                    }
                });
            }
        }.start();
    }


    public void setImageFromNative(ImageView imageView, String img_path) {
        this.setImageFromNative(imageView, img_path, default_src, error_src);
    }

    public void setImageFromNative(final ImageView imageView, final String img_path, int default_src, final int error_src) {
        if (TextUtils.isEmpty(img_path)) {
            return;
        }
        Bitmap cache = mCache.getBitmap(img_path);
        if (cache != null) {
            imageView.setImageBitmap(cache);
            return;
        } else {
            imageView.setImageResource(default_src);
        }
        new Thread() {
            @Override
            public void run() {
                final Bitmap fillBitmap = BitmapFactory.decodeFile(img_path);
                imageView.post(new Runnable() {
                    @Override
                    public void run() {
                        if (fillBitmap == null) {
                            imageView.setImageResource(error_src);
                        } else {
                            mCache.putBitmap(img_path, fillBitmap);
                            imageView.setImageBitmap(fillBitmap);
                        }
                    }
                });
            }
        }.start();
    }
}
