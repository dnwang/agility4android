package org.pinwheel.agility.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import org.pinwheel.agility.net.HttpClientAgent;
import org.pinwheel.agility.net.parser.DataParserAdapter;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
public class ImageLoader {

    private static final String PATH = "bitmap";

    /**
     * Network task map
     */
    private final HashMap<String, ImageTaskDispatcher.Task> taskMap;
    /**
     * Memory and disk cache loader
     */
    private SimpleCacheLoader cacheLoader;
    /**
     * Network bitmap task dispatcher
     */
    private ImageTaskDispatcher taskDispatcher;

    private ExecutorService executor;

    private int defaultRes;

    private int errorRes;

    /**
     * default diskCacheSize: 1024 * 1024 * 1024;//1G
     * default memoryCacheSize: (int) (Runtime.getRuntime().maxMemory() / 1024 / 8);// 1/8 total memory size
     *
     * @param context    context
     * @param httpEngine HttpClientAgent
     */
    public ImageLoader(Context context, HttpClientAgent httpEngine) {
        this(context, 1024 * 1024 * 1024, (int) (Runtime.getRuntime().maxMemory() / 1024 / 8), httpEngine);
    }

    /**
     * Full constructor
     *
     * @param context         context
     * @param memoryCacheSize memoryCacheSize
     * @param diskCacheSize   diskCacheSize
     * @param httpEngine      HttpClientAgent
     */
    public ImageLoader(Context context, int memoryCacheSize, int diskCacheSize, HttpClientAgent httpEngine) {
        executor = Executors.newCachedThreadPool();
        taskMap = new HashMap<>();
        DiskCache diskCache = new DiskCache(Tools.getDiskCacheDir(context, PATH), 0, diskCacheSize);
        MemoryCache memoryCache = new MemoryCache(memoryCacheSize);
        cacheLoader = new SimpleCacheLoader(memoryCache, diskCache);
        taskDispatcher = new ImageTaskDispatcher(5, httpEngine);
    }

    public CacheLoader getCacheLoader() {
        return cacheLoader;
    }

    public void setErrorRes(int errorRes) {
        this.errorRes = errorRes;
    }

    public void setDefaultRes(int defaultRes) {
        this.defaultRes = defaultRes;
    }

    public void setImage(View view, final String url) {
        if (view == null || TextUtils.isEmpty(url) || executor == null) {
            return;
        }
        WeakReference<View> viewReference = new WeakReference<>(view);
        // convert cache key
        String key = Tools.convertByMD5(url);
        CacheEntity memoryCache = cacheLoader.getMemoryCache().getCache(key);
        if (memoryCache != null && memoryCache instanceof BitmapEntity) {
            Bitmap bitmap = ((BitmapEntity) memoryCache).get();
            if (bitmap != null) {
                Tools.setBitmap(viewReference, bitmap);
                return;
            }
        }

        // async load disk cache and network bitmap
        AsyncLoader asyncLoader = new AsyncLoader(viewReference, key, url);
        executor.submit(asyncLoader);
    }

    public void setImage(View view, final String url, final int width, final int height) {
        if (view == null || TextUtils.isEmpty(url) || executor == null) {
            return;
        }
        WeakReference<View> viewReference = new WeakReference<>(view);
        // convert cache key
        String key = Tools.convertByMD5(url);
        // according view params load cache
        String memoryKey = key + String.valueOf(width) + String.valueOf(height);
        CacheEntity memoryCache = cacheLoader.getMemoryCache().getCache(memoryKey);
        if (memoryCache != null && memoryCache instanceof BitmapEntity) {
            Bitmap bitmap = ((BitmapEntity) memoryCache).get();
            if (bitmap != null) {
                Tools.setBitmap(viewReference, bitmap);
                return;
            }
        }

        // async load disk cache and network bitmap
        AsyncLoader asyncLoader = new AsyncLoader(viewReference, key, url);
        asyncLoader.setBitmapBound(width, height);
        executor.submit(asyncLoader);
    }

    public void setImageByScaleType(ImageView imageView, String url) {
        if (imageView == null || TextUtils.isEmpty(url) || executor == null) {
            return;
        }
        WeakReference<ImageView> viewReference = new WeakReference<>(imageView);
        // convert cache key
        String key = Tools.convertByMD5(url);
        // according imageView params load cache
        ImageView.ScaleType scaleType = imageView.getScaleType();
        int maxWidth = imageView.getMeasuredWidth();
        int maxHeight = imageView.getMeasuredHeight();
        String memoryKey = key + String.valueOf(maxWidth) + String.valueOf(maxHeight) + scaleType.toString();
        CacheEntity memoryCache = cacheLoader.getMemoryCache().getCache(memoryKey);
        if (memoryCache != null && memoryCache instanceof BitmapEntity) {
            Bitmap bitmap = ((BitmapEntity) memoryCache).get();
            if (bitmap != null) {
                Tools.setBitmap(viewReference, bitmap);
                return;
            }
        }

        // async load disk cache and network bitmap
        AsyncLoader asyncLoader = new AsyncLoader(viewReference, key, url);
        asyncLoader.setImageScaleType(scaleType);
        asyncLoader.setBitmapBound(maxWidth, maxHeight);
        executor.submit(asyncLoader);
    }

    /**
     * Check a task is already in taskMap ,if not put it
     *
     * @param task task
     * @return is put
     */
    protected boolean checkAndPutTask(ImageTaskDispatcher.Task task) {
        synchronized (taskMap) {
            boolean is = taskMap.containsKey(task.getId());
            if (!is) {
                taskMap.put(task.getId(), task);
            }
            return is;
        }
    }

    /**
     * Clear task when network task complete
     *
     * @param taskId taskId
     */
    protected void removeTaskInLoadingComplete(String taskId) {
        synchronized (taskMap) {
            taskMap.remove(taskId);
        }
    }

    /**
     * Add view to taskMap
     *
     * @param taskId taskId
     * @param view   weakReference
     */
    protected void addViewToTask(String taskId, WeakReference<? extends View> view) {
        synchronized (taskMap) {
            ImageTaskDispatcher.Task task = taskMap.get(taskId);
            if (task != null) {
                task.addView(view);
            }
        }
    }

    /**
     * Remove view in taskMap
     *
     * @param view weakReference
     */
    protected void clearViewInTaskMap(WeakReference<? extends View> view) {
        synchronized (taskMap) {
            Collection<ImageTaskDispatcher.Task> tasks = taskMap.values();
            for (ImageTaskDispatcher.Task task : tasks) {
                task.removeView(view);
            }
        }
    }

    /**
     * Release all reference
     */
    public void release() {
        synchronized (taskMap) {
            taskMap.clear();
        }
        if (executor != null) {
            executor.shutdown();
            executor = null;
        }
        if (cacheLoader != null) {
            cacheLoader.release();
        }
    }

    /**
     * Time consuming loader (disk cache load / network task)
     */
    protected final class AsyncLoader implements Runnable {

        private WeakReference<? extends View> viewReference;
        private String key;
        private String url;
        private ImageView.ScaleType scaleType;
        private int maxWidth;
        private int maxHeight;

        public AsyncLoader(WeakReference<? extends View> viewReference, String key, String url) {
            this.viewReference = viewReference;
            this.key = key;
            this.url = url;
            this.maxHeight = -1;
            this.maxHeight = -1;
            this.scaleType = null;
        }

        public void setImageScaleType(ImageView.ScaleType scaleType) {
            this.scaleType = scaleType;
        }

        public void setBitmapBound(int maxWidth, int maxHeight) {
            this.maxWidth = maxWidth;
            this.maxHeight = maxHeight;
        }

        @Override
        public void run() {
            if (viewReference.get() == null) {
                return;
            }
            // get disk cache
            Bitmap cache;
            if (scaleType != null && maxHeight != -1 & maxWidth != -1) {
                cache = cacheLoader.getBitmap(key, scaleType, maxWidth, maxHeight);
            } else if (scaleType == null && maxHeight != -1 && maxWidth != -1) {
                cache = cacheLoader.getBitmap(key, maxWidth, maxHeight);
            } else {
                cache = cacheLoader.getBitmap(key);
            }
            // set default bitmap and dispatch task
            if (cache != null) {
                // load disk cache success
                Tools.setBitmapInUIThread(viewReference, cache);
            } else {
                Tools.setBitmapInUIThread(viewReference, null);

                ImageTaskDispatcher.Task task = createTask(viewReference, key, url);
                if (!checkAndPutTask(task)) {
                    // start this new task now
                    taskDispatcher.post(task);
                } else {
                    // no need start task, because task was already loaded
                    clearViewInTaskMap(viewReference);
                    addViewToTask(key, viewReference);
                }
            }
        }

        /**
         * Create a network task
         *
         * @param viewReference view
         * @param key           taskId
         * @param url           task image url
         * @return ImageTask
         */
        private ImageTaskDispatcher.Task createTask(final WeakReference<? extends View> viewReference, String key, String url) {
            final ImageTaskDispatcher.Task task = new ImageTaskDispatcher.Task(key, url);
            task.addView(viewReference);
            task.setResponseParser(new CacheParser(this), new HttpClientAgent.OnRequestAdapter<Bitmap>() {
                @Override
                public void onDeliverSuccess(Bitmap obj) {
                    task.applyBitmap(obj);
                    removeTaskInLoadingComplete(task.getId());
                }

                @Override
                public void onDeliverError(Exception e) {
                    task.applyBitmap(null); // error bitmap
                    removeTaskInLoadingComplete(task.getId());
                }
            });
            return task;
        }
    }

    /**
     * Network bitmap parser
     */
    protected final class CacheParser extends DataParserAdapter<Bitmap> {

        private AsyncLoader asyncLoader;

        public CacheParser(AsyncLoader asyncLoader) {
            this.asyncLoader = asyncLoader;
        }

        @Override
        public void parse(InputStream inputStream) throws Exception {
            cacheLoader.getDiskCache().setCache(asyncLoader.key, inputStream);
        }

        @Override
        public Bitmap getResult() {
            Bitmap cache;
            if (asyncLoader.scaleType != null && asyncLoader.maxHeight != -1 & asyncLoader.maxWidth != -1) {
                cache = cacheLoader.getBitmap(asyncLoader.key, asyncLoader.scaleType, asyncLoader.maxWidth, asyncLoader.maxHeight);
            } else if (asyncLoader.scaleType == null && asyncLoader.maxHeight != -1 && asyncLoader.maxWidth != -1) {
                cache = cacheLoader.getBitmap(asyncLoader.key, asyncLoader.maxWidth, asyncLoader.maxHeight);
            } else {
                cache = cacheLoader.getBitmap(asyncLoader.key);
            }
            return cache;
        }

    }

}
