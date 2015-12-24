package org.pinwheel.agility.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
     * Get cache controller
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
        if (view == null || TextUtils.isEmpty(url)) {
            return;
        }
        setImage(view, url, BaseUtils.deepClone(defaultOptions));
    }

    public void setImage(View view, String url, ImageLoaderOptions options) {
        if (view == null || TextUtils.isEmpty(url) || options == null) {
            return;
        }
        if (options.justViewBounds()) {
            int viewWidth = view.getMeasuredWidth();
            int viewHeight = view.getMeasuredHeight();
            options.setMaxSize((viewWidth <= 0 ? options.getMaxWidth() : viewWidth), (viewHeight <= 0 ? options.getMaxHeight() : viewHeight));
        }
        getBitmap(new ViewReceiver(view), url, options);
    }

    public void getBitmap(BitmapReceiver receiver, String url) {
        if (receiver == null || TextUtils.isEmpty(url)) {
            return;
        }
        getBitmap(receiver, url, BaseUtils.deepClone(defaultOptions));
    }

    public void getBitmap(BitmapReceiver receiver, String url, ImageLoaderOptions options) {
        if (receiver == null || TextUtils.isEmpty(url) || options == null || executor == null) {
            // show error res for receiver
            if (receiver != null && options != null) {
                receiver.dispatch(options.getErrorRes());
            }
            return;
        }
        // dispatch default bitmap
        receiver.dispatch(options.getDefaultRes());
        // clear receiver
        clearReceiverInTaskMap(receiver);
        // convert cache key
        String key = CacheUtils.convertKey(url + options.getKey());
        // loading memory cache first
        if (!options.isIgnoreCache()) {
            CacheEntity memoryCache = cacheLoader.getMemoryCache().getCache(key);
            if (memoryCache != null && memoryCache instanceof BitmapEntity) {
                Bitmap bitmap = ((BitmapEntity) memoryCache).get();
                if (bitmap != null) {
                    receiver.dispatch(bitmap);
                    return;
                }
            }
        }
        // async load disk cache and network bitmap
        AsyncLoaderTask asyncLoader = new AsyncLoaderTask(key, url, options);
        // check task is already in queue, if not put it
        if (!checkAndPutTask(asyncLoader)) {
            // add this receiver to task
            asyncLoader.addReceiver(receiver);
            // start new task
            executor.execute(asyncLoader);
        } else {
            // put receiver to task only, no need start new task
            addReceiverToTask(key, receiver);
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
     * Add receiver to task
     *
     * @param key      key
     * @param receiver receiver
     */
    protected void addReceiverToTask(String key, BitmapReceiver receiver) {
        synchronized (asyncTaskMap) {
            AsyncLoaderTask task = asyncTaskMap.get(key);
            if (task != null) {
                task.addReceiver(receiver);
            }
        }
    }

    /**
     * Remove receiver in task
     *
     * @param receiver receiver
     */
    protected void clearReceiverInTaskMap(BitmapReceiver receiver) {
        synchronized (asyncTaskMap) {
            Collection<AsyncLoaderTask> tasks = asyncTaskMap.values();
            for (AsyncLoaderTask task : tasks) {
                task.removeReceiver(receiver);
            }
        }
    }

    /**
     * Release all task reference
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
     * Load bitmap task.
     */
    protected final class AsyncLoaderTask implements Runnable {

        private final HashSet<BitmapReceiver> receivers;
        private String key;
        private String url;
        private ImageLoaderOptions options;

        public AsyncLoaderTask(String key, String url, ImageLoaderOptions options) {
            this.receivers = new HashSet<>(1);
            this.key = key;
            this.url = url;
            this.options = options;
        }

        /**
         * Release all receiver
         */
        public void release() {
            synchronized (receivers) {
                receivers.clear();
            }
        }

        /**
         * Apply bitmap to all receiver
         *
         * @param bitmap bitmap
         */
        public void applyBitmap(final Bitmap bitmap) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    for (BitmapReceiver receiver : receivers) {
                        receiver.dispatch(bitmap);
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
                    for (BitmapReceiver receiver : receivers) {
                        receiver.dispatch(res);
                    }
                    // remove this task from taskMap! task is all complete
                    removeTaskAtLoadingComplete(key);
                }
            });
        }

        /**
         * Add receiver to this task
         *
         * @param receiver receiver
         */
        public void addReceiver(BitmapReceiver receiver) {
            synchronized (receivers) {
                receivers.add(receiver);
            }
        }

        /**
         * Remove receiver from this task
         *
         * @param targetReceiver receiver
         */
        public void removeReceiver(BitmapReceiver targetReceiver) {
            synchronized (receivers) {
                Iterator<BitmapReceiver> iterator = receivers.iterator();
                while (iterator.hasNext()) {
                    BitmapReceiver receiver = iterator.next();
                    if (receiver.equals(targetReceiver)) {
                        iterator.remove();
                    }
                }
            }
        }

        /**
         * Start download and show bitmap
         */
        private void getBitmapFromNetwork() {
            Request request = new Request.Builder().url(url).timeOut(options.getNetworkTimeOut(), 0).create();
            request.setResponseParser(new DataParserAdapter<Bitmap>() {
                @Override
                public void parse(InputStream inputStream) throws Exception {
                    BitmapEntity bitmapEntity = new BitmapEntity(options.lowMemoryMode());
                    bitmapEntity.decodeFrom(inputStream, options);
                    cacheLoader.setBitmap(key, bitmapEntity.get());
                }

                @Override
                public Bitmap getResult() {
                    return cacheLoader.getBitmap(key);
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
                cache = cacheLoader.getBitmap(key);
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

    /**
     * Bitmap receiver.
     */
    public interface BitmapReceiver {

        void dispatch(int res);

        void dispatch(Bitmap bitmap);

    }

    /**
     * View receiver reference.
     */
    private static final class ViewReceiver implements BitmapReceiver {

        private SoftReference<View> reference;// !!

        public ViewReceiver(View view) {
            this.reference = new SoftReference<>(view);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ViewReceiver that = (ViewReceiver) o;
            return reference.get() == that.reference.get();
        }

        @Override
        public int hashCode() {
            return reference.get() == null ? 0 : reference.get().hashCode();
        }

        @Override
        public void dispatch(int res) {
            if (res <= 0) {
                dispatch(null);
            } else {
                View v = reference.get();
                if (v != null) {
                    if (v instanceof ImageView) {
                        ((ImageView) v).setImageResource(res);
                    } else {
                        v.setBackgroundResource(res);
                    }
                }
            }
        }

        @Override
        public void dispatch(Bitmap bitmap) {
            View v = reference.get();
            if (v != null) {
                if (v instanceof ImageView) {
                    ((ImageView) v).setImageBitmap(bitmap);
                } else {
                    if (bitmap == null) {
                        v.setBackgroundDrawable(null);
                    } else {
                        v.setBackgroundDrawable(new BitmapDrawable(bitmap));
                    }
                }
            }
        }
    }

}
