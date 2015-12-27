package org.pinwheel.agility.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;

import org.pinwheel.agility.net.HttpClientAgent;
import org.pinwheel.agility.net.HttpClientAgentHelper;
import org.pinwheel.agility.net.HttpConnectionAgent;
import org.pinwheel.agility.net.OkHttpAgent;
import org.pinwheel.agility.net.Request;
import org.pinwheel.agility.net.parser.DataParserAdapter;
import org.pinwheel.agility.util.BaseUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
    private final Map<String, AsyncLoaderTask> taskMap;
    /**
     * Thread cache pool
     */
    private ExecutorService executor;
    /**
     * Memory and disk cache loader
     */
    private MemoryCache memoryCache;
    private DiskCache diskCache;
    /**
     * Network engine
     */
    private HttpClientAgent httpEngine;
    /**
     * ImageLoader options
     */
    private final ImageLoaderOptions loaderOptions;
    /**
     * Global default ViewReceiverOptions
     */
    private ViewReceiver.Options defaultOptions;

    private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

    /**
     * Default loader options. {@link ImageLoaderOptions}
     *
     * @param context context
     */
    public ImageLoader(Context context) {
        this(context, new ImageLoaderOptions.Builder().create());
    }

    /**
     * Full constructor
     *
     * @param context context
     * @param options loader options
     */
    public ImageLoader(Context context, ImageLoaderOptions options) {
        if (context == null || options == null) {
            throw new NullPointerException(getClass().getSimpleName() + " init error. please make sure 'Context' or 'ImageLoaderOptions' not empty.");
        }

        // init thread pool
        int parallelSize = options.getParallelSize();
        if (parallelSize <= 0) {
            this.executor = Executors.newCachedThreadPool();
        } else {
            this.executor = Executors.newFixedThreadPool(parallelSize);
        }

        // init task map. ConcurrentHashMap (thread safety); 16 default size
        this.taskMap = new ConcurrentHashMap<>(16);

        // init disk cache
        this.diskCache = new DiskCache(
                CacheUtils.getDiskCacheDir(context, PATH),
                BaseUtils.getVersionCode(context),
                options.getDiskCacheSize());
        // init memory cache
        this.memoryCache = new MemoryCache(options.getMemoryCacheSize());

        // init network engine, auto select
        if (HttpClientAgentHelper.isImportOkHttp()) {
            this.httpEngine = new OkHttpAgent(options.getParallelSize());
        } else {
            this.httpEngine = new HttpConnectionAgent(options.getParallelSize());
        }

        this.loaderOptions = options;
    }

    public void setDefaultOptions(ViewReceiver.Options Options) {
        this.defaultOptions = Options;
    }

    public void setImage(View view, String uri) {
        setImage(view, uri, new ViewReceiver.OptionsBuilder().copy(defaultOptions));
    }

    /**
     * Auto load bitmap to target view (ImageView:BackgroundResource; View:Background)
     *
     * @param view           target view
     * @param uri            bitmap connection uri
     * @param optionsBuilder image load options builder
     */
    public void setImage(View view, String uri, ViewReceiver.OptionsBuilder optionsBuilder) {
        if (view == null) {
            // no need show everything
            return;
        }
        ViewReceiver receiver = new ViewReceiver(view);
        if (optionsBuilder != null) {
            // dispatch default bitmap
            if (optionsBuilder.isJustViewBound()) {
                // auto resize to view bound
                final int viewWidth = view.getMeasuredWidth();
                final int viewHeight = view.getMeasuredHeight();
                if (viewWidth > 0 && viewHeight > 0) {
                    optionsBuilder.setMax(viewWidth, viewHeight);
                }
            }
            // create final options
            ViewReceiver.Options options = optionsBuilder.create();
            receiver.dispatch(options.getDefaultRes());
            getBitmap(receiver, uri, options);
        } else {
            getBitmap(receiver, uri, null);
        }
    }

    public void getBitmap(BitmapReceiver receiver, String uri) {
        getBitmap(receiver, uri, new BitmapReceiver.OptionsBuilder().create());
    }

    /**
     * Get bitmap to receiver
     *
     * @param receiver bitmap receiver
     * @param uri      bitmap connection uri
     * @param options  bitmap load params
     */
    public void getBitmap(BitmapReceiver receiver, String uri, BitmapReceiver.Options options) {
        if (receiver == null || executor == null) {
            return;
        }
        // bind options to receiver
        receiver.setOptions(options);

        if (TextUtils.isEmpty(uri)) {
            receiver.dispatch(null);
            return;
        }
        // clear receiver
        clearReceiverInTaskMap(receiver);
        // loading memory cache first
        final String memoryKey = getMemoryKey(getDiskKey(uri), options);
        ObjectEntity cacheEntity = memoryCache.getCache(memoryKey);
        if (cacheEntity != null && cacheEntity instanceof BitmapEntity) {
            receiver.dispatch(((BitmapEntity) cacheEntity).get());
            return;
        }

        // async load disk cache and network bitmap
        AsyncLoaderTask loaderTask = new AsyncLoaderTask(uri);
        // check task is already in queue, if not put it
        if (!checkAndPutTask(loaderTask)) {
            // add this receiver to task
            loaderTask.addReceiver(receiver);
            // start new task
            executor.execute(loaderTask);
        } else {
            // put receiver to task only, no need start new task
            addReceiverToTask(loaderTask.diskKey, receiver);
        }
    }

    public final DiskCache getDiskCache() {
        return this.diskCache;
    }

    public final MemoryCache getMemoryCache() {
        return this.memoryCache;
    }

    /**
     * Load disk cache to memory and return it
     *
     * @param diskKey the key of disk cache
     * @param options params
     * @return memory cache bitmap
     */
    protected Bitmap getDiskCacheToMemory(String diskKey, BitmapReceiver.Options options) {
        BitmapEntity bitmapEntity;
        if (options == null) {
            bitmapEntity = new BitmapEntity();
            bitmapEntity.decodeFrom(diskCache.getCache(diskKey));
        } else {
            bitmapEntity = new BitmapEntity(options.getConfig());
            bitmapEntity.decodeFrom(diskCache.getCache(diskKey), options);
        }
        memoryCache.setCache(getMemoryKey(diskKey, options), bitmapEntity);
        return bitmapEntity.get();
    }

    protected final String getMemoryKey(String diskKey, BitmapReceiver.Options options) {
        return options == null ? diskKey : (diskKey + "#" + options.hashCode());
    }

    protected final String getDiskKey(String url) {
        return CacheUtils.convertKey(url);
    }

    /**
     * Check a task is already in task ,if not put it
     *
     * @param task task
     * @return is put
     */
    protected boolean checkAndPutTask(AsyncLoaderTask task) {
        final boolean is = taskMap.containsKey(task.diskKey);
        if (!is) {
            taskMap.put(task.diskKey, task);
        }
        return is;
    }

    /**
     * Remove task
     *
     * @param key key
     */
    protected void removeTask(String key) {
        AsyncLoaderTask task = taskMap.remove(key);
        if (task != null) {
            task.release();
        }
        if (taskMap.size() == 0) {
            System.gc();
        }
    }

    /**
     * Add receiver to task
     *
     * @param key      key
     * @param receiver receiver
     */
    protected void addReceiverToTask(String key, BitmapReceiver receiver) {
        AsyncLoaderTask task = taskMap.get(key);
        if (task != null) {
            task.addReceiver(receiver);
        }
    }

    /**
     * Remove receiver in task
     *
     * @param receiver receiver
     */
    protected void clearReceiverInTaskMap(BitmapReceiver receiver) {
        Collection<AsyncLoaderTask> tasks = taskMap.values();
        for (AsyncLoaderTask task : tasks) {
            task.removeReceiver(receiver);
        }
    }

    /**
     * Release all task reference
     */
    public void release() {
        Collection<AsyncLoaderTask> tasks = taskMap.values();
        for (AsyncLoaderTask task : tasks) {
            task.release();
        }
        taskMap.clear();

        if (executor != null) {
            executor.shutdown();
            executor = null;
        }
        if (memoryCache != null) {
            memoryCache.release();
            memoryCache = null;
        }
        if (diskCache != null) {
            diskCache.release();
            diskCache = null;
        }
        System.gc();
    }

    /**
     * Load bitmap task.
     */
    private class AsyncLoaderTask implements Runnable {

        private final Map<Integer, BitmapReceiver> receivers;
        private final String diskKey;
        private final String uri;

        public AsyncLoaderTask(String uri) {
            this.diskKey = getDiskKey(uri);
            this.receivers = new ConcurrentHashMap<>(10);
            this.uri = uri;
        }

        /**
         * Release all receiver
         */
        public void release() {
            receivers.clear();
        }

        /**
         * Add receiver to this task
         *
         * @param receiver receiver
         */
        public void addReceiver(BitmapReceiver receiver) {
            receivers.put(receiver.hashCode(), receiver);
        }

        /**
         * Remove receiver from this task
         *
         * @param targetReceiver receiver
         */
        public void removeReceiver(BitmapReceiver targetReceiver) {
            receivers.remove(targetReceiver.hashCode());
        }

        public void postToReceiver(final BitmapReceiver receiver, final Bitmap bitmap) {
            if (receiver == null) {
                return;
            }
            mainThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    receiver.dispatch(bitmap);
                }
            });
        }

        /**
         * Start download and show bitmap
         */
        private void getBitmapFromNetwork() {
            Request request = new Request.Builder().url(uri).timeOut(loaderOptions.getNetworkTimeOut(), 0).create();
            request.setResponseParser(new DataParserAdapter() {
                @Override
                public void parse(InputStream inStream) throws Exception {
                    diskCache.setCache(diskKey, inStream);
                    Collection<BitmapReceiver> collection = receivers.values();
                    for (BitmapReceiver receiver : collection) {
                        postToReceiver(receiver, getDiskCacheToMemory(diskKey, receiver.getOptions()));
                    }
                    removeTask(diskKey);
                }
            });
            httpEngine.enqueue(request);
        }

        private void getBitmapFromNativePath() {
            try {
                FileInputStream fileInStream = new FileInputStream(new File(uri));
                Collection<BitmapReceiver> collection = receivers.values();
                for (BitmapReceiver receiver : collection) {
                    BitmapEntity bitmapEntity;
                    BitmapReceiver.Options options = receiver.getOptions();
                    if (options == null) {
                        bitmapEntity = new BitmapEntity();
                        bitmapEntity.decodeFrom(fileInStream);
                    } else {
                        bitmapEntity = new BitmapEntity(options.getConfig());
                        bitmapEntity.decodeFrom(fileInStream, options);
                    }
                    memoryCache.setCache(getMemoryKey(diskKey, options), bitmapEntity);
                    postToReceiver(receiver, bitmapEntity.get());
                }
                removeTask(diskKey);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        private boolean isNativeUri() {
            return !uri.startsWith("http");
        }

        @Override
        public void run() {
            if (isNativeUri()) {
                // this is a native path
                getBitmapFromNativePath();
                return;
            }

            if (!diskCache.isContains(diskKey)) {
                // have no disk cache, download bitmap from network
                getBitmapFromNetwork();
            } else {
                // just load disk cache
                Collection<BitmapReceiver> collection = receivers.values();
                for (BitmapReceiver receiver : collection) {
                    postToReceiver(receiver, getDiskCacheToMemory(diskKey, receiver.getOptions()));
                }
                removeTask(diskKey);
            }
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
    public static final class ImageLoaderOptions implements Serializable {

        private int networkTimeOut;
        private int memoryCacheSize;
        private int diskCacheSize;
        private int parallelSize;

        private ImageLoaderOptions(Builder builder) {
            this.parallelSize = builder.parallelSize;
            this.memoryCacheSize = builder.memoryCacheSize;
            this.diskCacheSize = builder.diskCacheSize;
            this.networkTimeOut = builder.connectTimeOut;
        }

        public int getNetworkTimeOut() {
            return networkTimeOut;
        }

        public int getMemoryCacheSize() {
            return memoryCacheSize;
        }

        public int getDiskCacheSize() {
            return diskCacheSize;
        }

        public int getParallelSize() {
            return parallelSize;
        }

        /**
         * Options builder
         */
        public static final class Builder {

            private int connectTimeOut;
            private int memoryCacheSize;
            private int diskCacheSize;
            private int parallelSize;

            public Builder() {
                memoryCacheSize = CacheUtils.DEFAULT_MAX_MEMORY_CACHE;
                diskCacheSize = CacheUtils.DEFAULT_MAX_DISK_CACHE;
                parallelSize = 4;
                connectTimeOut = 30;// 30s
            }

            public Builder connectTimeOut(int timeOut) {
                this.connectTimeOut = Math.max(0, timeOut);
                return this;
            }

            public Builder memoryCacheSize(int size) {
                this.memoryCacheSize = Math.max(0, size);
                return this;
            }

            public Builder diskCacheSize(int size) {
                this.diskCacheSize = Math.max(0, size);
                return this;
            }

            public Builder parallelSize(int size) {
                this.parallelSize = Math.max(0, size);
                return this;
            }

            public ImageLoaderOptions create() {
                return new ImageLoaderOptions(this);
            }
        }

    }

}
