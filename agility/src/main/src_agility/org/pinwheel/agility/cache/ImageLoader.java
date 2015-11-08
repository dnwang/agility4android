package org.pinwheel.agility.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;

import org.pinwheel.agility.net.HttpClientAgent;
import org.pinwheel.agility.net.HttpClientAgent.OnRequestAdapter;
import org.pinwheel.agility.net.Request;
import org.pinwheel.agility.net.parser.DataParserAdapter;
import org.pinwheel.agility.util.BaseUtils;

import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
    /**
     * Disk cache path
     */
    private static final String PATH = "bitmap";
    /**
     * Network task map
     */
    private final HashMap<String, AsyncLoaderTask> asyncTaskMap;
    /**
     * Memory and disk cache loader
     */
    private SimpleCacheLoader cacheLoader;
    /**
     * Network engine
     */
    private HttpClientAgent httpEngine;

    private ExecutorService executor;

    /**
     * Global default loader options
     */
    private ImageLoaderOptions defaultOptions;

    /**
     * Default diskCacheSize: {@link CacheLoader}
     * Default memoryCacheSize: {@link CacheLoader}
     *
     * @param context    context
     * @param httpEngine HttpClientAgent
     */
    public ImageLoader(Context context, HttpClientAgent httpEngine) {
        this(context, CacheLoader.DEFAULT_MAX_MEMORY_CACHE, CacheLoader.DEFAULT_MAX_DISK_CACHE, httpEngine, 0);
    }

    /**
     * Full constructor
     *
     * @param context         context
     * @param memoryCacheSize memoryCacheSize
     * @param diskCacheSize   diskCacheSize
     * @param httpEngine      HttpClientAgent
     */
    public ImageLoader(Context context, int memoryCacheSize, int diskCacheSize, HttpClientAgent httpEngine, int maxParallelSize) {
        if (maxParallelSize <= 0) {
            this.executor = Executors.newCachedThreadPool();
        } else {
            this.executor = Executors.newFixedThreadPool(maxParallelSize);
        }
        this.asyncTaskMap = new HashMap<>();
        DiskCache diskCache = new DiskCache(
                CacheUtils.getDiskCacheDir(context, PATH),
                BaseUtils.getVersionCode(context),
                Math.max(0, diskCacheSize));
        MemoryCache memoryCache = new MemoryCache(Math.max(0, memoryCacheSize));
        this.cacheLoader = new SimpleCacheLoader(memoryCache, diskCache);
        this.httpEngine = httpEngine;
    }

    /**
     * Get image cache controller
     *
     * @return cacheLoader
     */
    public CacheLoader getCacheLoader() {
        return cacheLoader;
    }

    public void setDefaultOptions(ImageLoaderOptions defaultOptions) {
        this.defaultOptions = defaultOptions == null ? new ImageLoaderOptions.Builder().create() : defaultOptions;
    }

    public void setImage(View view, String url) {
        setImage(view, url, defaultOptions);
    }

    public void setImage(View view, String url, ImageLoaderOptions options) {
        if (view == null || TextUtils.isEmpty(url) || options == null || executor == null) {
            return;
        }
        SoftReference<View> viewReference = new SoftReference<>(view);
        // clear view
        clearViewInTaskMap(viewReference);
        // show default bitmap
        CacheUtils.setBitmap(viewReference, options.getDefaultRes());
        // convert cache key
        String key = CacheUtils.convertKey(url + options.getKey());
        // loading memory cache first
        if (!options.isIgnoreCache()) {
            CacheEntity memoryCache = cacheLoader.getMemoryCache().getCache(key);
            if (memoryCache != null && memoryCache instanceof BitmapEntity) {
                Bitmap bitmap = ((BitmapEntity) memoryCache).get();
                if (bitmap != null) {
                    CacheUtils.setBitmap(viewReference, bitmap);
                    return;
                }
            }
        }
        // async load disk cache and network bitmap
        AsyncLoaderTask asyncLoader = new AsyncLoaderTask(key, url, options);
        // check task is already in queue, if not put it
        if (!checkAndPutTask(asyncLoader)) {
            // add this view to task
            asyncLoader.addView(viewReference);
            // start new task
            executor.execute(asyncLoader);
        } else {
            // put view to task only, no need start new task
            addViewToTask(key, viewReference);
        }
    }

    /**
     * Check a task is already in task ,if not put it
     *
     * @param task task
     * @return is put
     */
    protected boolean checkAndPutTask(AsyncLoaderTask task) {
        synchronized (asyncTaskMap) {
            boolean is = asyncTaskMap.containsKey(task.key);
            if (!is) {
                asyncTaskMap.put(task.key, task);
            }
            return is;
        }
    }

    /**
     * Clear task when network task complete
     *
     * @param key key
     */
    protected void removeTaskAtLoadingComplete(String key) {
        AsyncLoaderTask task = asyncTaskMap.remove(key);
        if (task != null) {
            task.release();
        }
    }

    /**
     * Add view to task
     *
     * @param key  key
     * @param view SoftReference
     */
    protected void addViewToTask(String key, SoftReference<? extends View> view) {
        synchronized (asyncTaskMap) {
            AsyncLoaderTask task = asyncTaskMap.get(key);
            if (task != null) {
                task.addView(view);
            }
        }
    }

    /**
     * Remove view in task
     *
     * @param view SoftReference
     */
    protected void clearViewInTaskMap(SoftReference<? extends View> view) {
        synchronized (asyncTaskMap) {
            Collection<AsyncLoaderTask> tasks = asyncTaskMap.values();
            for (AsyncLoaderTask task : tasks) {
                task.removeView(view);
            }
        }
    }

    /**
     * Release all reference
     */
    public void release() {
        synchronized (asyncTaskMap) {
            asyncTaskMap.clear();
        }
        if (executor != null) {
            executor.shutdown();
            executor = null;
        }
        if (cacheLoader != null) {
            cacheLoader.release();
            cacheLoader = null;
        }
    }

    /**
     * Load bitmap task
     */
    protected final class AsyncLoaderTask implements Runnable {

        private HashSet<SoftReference<? extends View>> viewReferences;
        private String key;
        private String url;
        private ImageLoaderOptions options;

        public AsyncLoaderTask(String key, String url, ImageLoaderOptions options) {
            this.viewReferences = new HashSet<>();
            this.key = key;
            this.url = url;
            this.options = options;
        }

        /**
         * Release all view references
         */
        public void release() {
            viewReferences.clear();
        }

        /**
         * Apply bitmap to all view
         *
         * @param bitmap bitmap
         */
        public void applyBitmap(final Bitmap bitmap) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    for (SoftReference<? extends View> viewReference : viewReferences) {
                        CacheUtils.setBitmap(viewReference, bitmap);
                    }
                    // remove this task from taskMap! task is all complete
                    removeTaskAtLoadingComplete(key);
                }
            });
        }

        public void applyBitmap(final int res) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    for (SoftReference<? extends View> viewReference : viewReferences) {
                        CacheUtils.setBitmap(viewReference, res);
                    }
                    // remove this task from taskMap! task is all complete
                    removeTaskAtLoadingComplete(key);
                }
            });
        }

        /**
         * Add view to this task
         *
         * @param targetView view
         */
        public void addView(SoftReference<? extends View> targetView) {
            if (targetView.get() == null) {
                return;
            }
            synchronized (asyncTaskMap) {
                viewReferences.add(targetView);
            }
        }

        /**
         * Remove view from this task
         *
         * @param targetView view
         */
        public void removeView(SoftReference<? extends View> targetView) {
            if (targetView.get() == null) {
                return;
            }
            synchronized (asyncTaskMap) {
                Iterator<SoftReference<? extends View>> iterator = viewReferences.iterator();
                while (iterator.hasNext()) {
                    SoftReference<? extends View> viewReference = iterator.next();
                    View v = viewReference.get();
                    if (v != null) {
                        if (v == targetView.get()) {
                            iterator.remove();
                        }
                    } else {
                        iterator.remove();
                    }
                }
            }
        }

        /**
         * Start download and show bitmap
         */
        private void getBitmapFromNetwork() {
            Request request = new Request.Builder()
                    .url(url)
                    .timeOut(options.getNetworkTimeOut(), 0)
                    .create();
            request.setResponseParser(new DataParserAdapter<Bitmap>() {
                @Override
                public void parse(InputStream inputStream) throws Exception {
                    BitmapEntity bitmapEntity = new BitmapEntity();
                    bitmapEntity.decodeFrom(inputStream, options);
                    cacheLoader.setBitmap(key, bitmapEntity.get());
                }

                @Override
                public Bitmap getResult() {
                    return cacheLoader.getBitmap(key, options.getBitmapOptions());
                }
            });
            request.setOnRequestListener(new OnRequestAdapter<Bitmap>() {
                @Override
                public void onDeliverSuccess(Bitmap obj) {
                    if (obj != null) {
                        applyBitmap(obj);
                    } else {
                        onDeliverError(new Exception("onDeliverSuccess: bitmap = null!"));
                    }
                }

                @Override
                public void onDeliverError(Exception e) {
                    applyBitmap(options.getErrorRes()); // show error bitmap
                }
            });
            httpEngine.enqueue(request);
        }

        @Override
        public void run() {
            // get memory / disk cache
            Bitmap cache = null;
            if (!options.isIgnoreCache()) {
                cache = cacheLoader.getBitmap(key, options.getBitmapOptions());
            }
            if (cache != null) {
                // load cache success
                applyBitmap(cache);
            } else {
                // post this task to download queue
                getBitmapFromNetwork();
            }
        }
    }

}
