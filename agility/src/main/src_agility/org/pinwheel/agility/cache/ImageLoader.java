package org.pinwheel.agility.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;

import org.pinwheel.agility.net.HttpClientAgent;
import org.pinwheel.agility.net.HttpClientAgent.OnRequestAdapter;
import org.pinwheel.agility.net.parser.DataParserAdapter;
import org.pinwheel.agility.net.parser.IDataParser;
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
     * Default max parallel number
     */
    public static final int DEFAULT_PARALLEL_TASK = 6;
    /**
     * Network task map
     */
    private final HashMap<String, AsyncLoaderTask> asyncTaskMap;
    /**
     * Memory and disk cache loader
     */
    private SimpleCacheLoader cacheLoader;
    /**
     * Network bitmap task dispatcher
     */
    private NetworkTaskDispatcher taskDispatcher;

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
        this(context, CacheLoader.DEFAULT_MAX_MEMORY_CACHE, CacheLoader.DEFAULT_MAX_DISK_CACHE, httpEngine, DEFAULT_PARALLEL_TASK);
    }

    /**
     * Full constructor
     *
     * @param context         context
     * @param memoryCacheSize memoryCacheSize
     * @param diskCacheSize   diskCacheSize
     * @param httpEngine      HttpClientAgent
     * @param maxParallelTask Http task parallel num
     */
    public ImageLoader(Context context, int memoryCacheSize, int diskCacheSize, HttpClientAgent httpEngine, int maxParallelTask) {
//        executor = Executors.newFixedThreadPool(maxParallelTask);
        executor = Executors.newCachedThreadPool();
        asyncTaskMap = new HashMap<>();
        DiskCache diskCache = new DiskCache(
                ImageLoaderUtils.getDiskCacheDir(context, PATH),
                BaseUtils.getVersionCode(context),
                Math.max(0, diskCacheSize));
        MemoryCache memoryCache = new MemoryCache(Math.max(0, memoryCacheSize));
        cacheLoader = new SimpleCacheLoader(memoryCache, diskCache);
        taskDispatcher = new NetworkTaskDispatcher(Math.max(1, maxParallelTask), httpEngine);
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
        clearViewInAsyncTaskMap(viewReference);
        // show default bitmap
        ImageLoaderUtils.setBitmap(viewReference, options.getDefaultRes());
        // convert cache key
        String key = ImageLoaderUtils.convertUrl(url + options.getKey());
        // loading memory cache first
        CacheEntity memoryCache = cacheLoader.getMemoryCache().getCache(key);
        if (memoryCache != null && memoryCache instanceof BitmapEntity) {
            Bitmap bitmap = ((BitmapEntity) memoryCache).get();
            if (bitmap != null) {
                ImageLoaderUtils.setBitmap(viewReference, bitmap);
                return;
            }
        }
        // async load disk cache and network bitmap
        AsyncLoaderTask asyncLoader = new AsyncLoaderTask(key, url, options);
        // check task is already in queue, if not put it
        if (!checkAndPutAsyncTask(asyncLoader)) {
            // add this view to task
            asyncLoader.addView(viewReference);
            // start new task
            executor.execute(asyncLoader);
        } else {
            // put view to task only, no need start new task
            addViewToAsyncTask(key, viewReference);
        }
    }

    /**
     * Check a task is already in task ,if not put it
     *
     * @param task task
     * @return is put
     */
    protected boolean checkAndPutAsyncTask(AsyncLoaderTask task) {
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
    protected void removeAsyncTaskAtLoadingComplete(String key) {
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
    protected void addViewToAsyncTask(String key, SoftReference<? extends View> view) {
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
    protected void clearViewInAsyncTaskMap(SoftReference<? extends View> view) {
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

        /**
         * Network task response callback
         */
        private OnRequestAdapter<Bitmap> requestAdapter = new OnRequestAdapter<Bitmap>() {
            @Override
            public void onDeliverSuccess(Bitmap obj) {
                applyBitmap(obj);
            }

            @Override
            public void onDeliverError(Exception e) {
                applyBitmap(options.getErrorRes()); // show error bitmap
            }
        };

        /**
         * Network bitmap parser
         */
        private IDataParser<Bitmap> dataParser = new DataParserAdapter<Bitmap>() {
            @Override
            public void parse(InputStream inputStream) throws Exception {
                BitmapEntity bitmapEntity = new BitmapEntity();
                bitmapEntity.decodeFrom(inputStream, options);
                cacheLoader.setBitmap(key, bitmapEntity.get());
            }

            @Override
            public Bitmap getResult() {
                return cacheLoader.getBitmap(key);
            }
        };

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

        public String getUrl() {
            return url;
        }

        public IDataParser<Bitmap> getDataParser() {
            return dataParser;
        }

        public OnRequestAdapter<Bitmap> getRequestAdapter() {
            return requestAdapter;
        }

        public ImageLoaderOptions getOptions() {
            return options;
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
                        ImageLoaderUtils.setBitmap(viewReference, bitmap);
                    }
                    // remove this task from taskMap! task is all complete
                    removeAsyncTaskAtLoadingComplete(key);
                }
            });
        }

        public void applyBitmap(final int res) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    for (SoftReference<? extends View> viewReference : viewReferences) {
                        ImageLoaderUtils.setBitmap(viewReference, res);
                    }
                    // remove this task from taskMap! task is all complete
                    removeAsyncTaskAtLoadingComplete(key);
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

        @Override
        public void run() {
            // get memory / disk cache
            Bitmap cache = cacheLoader.getBitmap(key);
            if (cache != null) {
                // load cache success
                applyBitmap(cache);
            } else {
                // post this task to download queue
                taskDispatcher.post(this);
            }
        }
    }

}
