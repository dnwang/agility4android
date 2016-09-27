package org.pinwheel.agility.cache.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;

import org.pinwheel.agility.cache.DiskCache;
import org.pinwheel.agility.cache.MemoryCache;
import org.pinwheel.agility.cache.ObjectEntity;
import org.pinwheel.agility.net.HttpClientAgent;
import org.pinwheel.agility.net.HttpConnectionAgent;
import org.pinwheel.agility.net.OkHttp2Agent;
import org.pinwheel.agility.net.Request;
import org.pinwheel.agility.net.parser.DataParserAdapter;
import org.pinwheel.agility.util.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
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
        File diskCachePath = options.getDiskCachePath();
        diskCachePath = (null == diskCachePath) ? getDefaultDiskCacheDir(context) : diskCachePath;
        this.diskCache = new DiskCache(diskCachePath, 0, options.getDiskCacheSize());
        // init memory cache
        this.memoryCache = new MemoryCache(options.getMemoryCacheSize());

        // init network engine, auto select
        if (HttpClientAgent.isImportOkHttp()) {
            this.httpEngine = new OkHttp2Agent(options.getParallelSize());
        } else {
            this.httpEngine = new HttpConnectionAgent(options.getParallelSize());
        }

        this.loaderOptions = options;
    }

    public void setDefaultOptions(ViewReceiver.Options Options) {
        this.defaultOptions = Options;
    }

    public void setImage(View view, String uri) {
        setImage(view, uri, defaultOptions != null ? new ViewReceiver.OptionsBuilder().copy(defaultOptions) : null);
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
     * Load bitmap to memory and return it
     *
     * @param memoryKey memory key
     * @param bytes     data of bitmap
     * @param options   params
     * @return memory cache bitmap
     */
    protected Bitmap getBitmapToMemory(String memoryKey, byte[] bytes, BitmapReceiver.Options options) {
        if (TextUtils.isEmpty(memoryKey) || bytes == null || bytes.length == 0) {
            return null;
        }
        BitmapEntity bitmapEntity;
        if (options == null) {
            bitmapEntity = new BitmapEntity();
            bitmapEntity.decodeFrom(bytes);
        } else {
            bitmapEntity = new BitmapEntity(options.getConfig());
            bitmapEntity.decodeFrom(bytes, options);
        }
        memoryCache.setCache(memoryKey, bitmapEntity);
        return bitmapEntity.get();
    }

    protected final String getMemoryKey(String diskKey, BitmapReceiver.Options options) {
        return options == null ? diskKey : (diskKey + "#" + options.hashCode());
    }

    protected final String getDiskKey(String url) {
        return md5(url);
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

        private final Set<BitmapReceiver> receivers;
        private final String diskKey;
        private final String uri;

        public AsyncLoaderTask(String uri) {
            this.diskKey = getDiskKey(uri);
            this.receivers = new HashSet<>(5);
            this.uri = uri;
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
                receivers.remove(targetReceiver);
            }
        }

        public void postToReceiver(final BitmapReceiver receiver, final Bitmap bitmap) {
            if (receiver == null) {
                return;
            }
            if (Looper.myLooper() != Looper.getMainLooper()) {
                mainThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        receiver.dispatch(bitmap);
                    }
                });
            } else {
                receiver.dispatch(bitmap);
            }
        }

        /**
         * Start download and show bitmap
         */
        private void getBitmapFromNetwork() {
            Request request = new Request.Builder().url(uri).timeOut(loaderOptions.getNetworkTimeOut(), 0).create();
            request.setResponseParser(new DataParserAdapter() {
                @Override
                public void parse(InputStream inStream) throws Exception {
                    byte[] bytes = IOUtils.stream2Bytes(inStream);
                    diskCache.setCache(diskKey, new ByteArrayInputStream(bytes));
                    synchronized (receivers) {
                        Iterator<BitmapReceiver> iterator = receivers.iterator();
                        while (iterator.hasNext()) {
                            BitmapReceiver receiver = iterator.next();
                            BitmapReceiver.Options options = receiver.getOptions();
                            Bitmap bitmap = getBitmapToMemory(getMemoryKey(diskKey, options), bytes, options);
                            // notify update
                            postToReceiver(receiver, bitmap);
                            iterator.remove();
                        }
                        removeTask(diskKey);
                    }
                }
            }, new HttpClientAgent.OnRequestAdapter() {
                @Override
                public void onDeliverSuccess(Object obj) {
                    // nothing to do; already notify in parse() method;
                }

                @Override
                public void onDeliverError(Exception e) {
                    synchronized (receivers) {
                        Iterator<BitmapReceiver> iterator = receivers.iterator();
                        while (iterator.hasNext()) {
                            BitmapReceiver receiver = iterator.next();
                            // notify update
                            postToReceiver(receiver, null);
                            iterator.remove();
                        }
                        removeTask(diskKey);
                    }
                }
            });
            httpEngine.enqueue(request);
        }

        private void getBitmapFromNativePath() {
            byte[] bytes = null;
            try {
                FileInputStream fileInStream = new FileInputStream(new File(uri));
                bytes = IOUtils.stream2Bytes(fileInStream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                synchronized (receivers) {
                    Iterator<BitmapReceiver> iterator = receivers.iterator();
                    while (iterator.hasNext()) {
                        BitmapReceiver receiver = iterator.next();
                        BitmapReceiver.Options options = receiver.getOptions();
                        Bitmap bitmap = getBitmapToMemory(getMemoryKey(diskKey, options), bytes, options);
                        postToReceiver(receiver, bitmap);
                        iterator.remove();
                    }
                    removeTask(diskKey);
                }
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
                byte[] bytes = IOUtils.stream2Bytes(diskCache.getCache(diskKey));
                synchronized (receivers) {
                    Iterator<BitmapReceiver> iterator = receivers.iterator();
                    while (iterator.hasNext()) {
                        BitmapReceiver receiver = iterator.next();
                        BitmapReceiver.Options options = receiver.getOptions();
                        Bitmap bitmap = getBitmapToMemory(getMemoryKey(diskKey, options), bytes, options);
                        postToReceiver(receiver, bitmap);
                        iterator.remove();
                    }
                    removeTask(diskKey);
                }
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

        private static final int DEFAULT_MAX_DISK_CACHE = 128 * 1024 * 1024;//128M
        //    private static final int DEFAULT_MAX_MEMORY_CACHE = 12 * 1024 * 1024;//12M
        private static final int DEFAULT_MAX_MEMORY_CACHE = (int) (Runtime.getRuntime().maxMemory() / 10);

        private int networkTimeOut;
        private int memoryCacheSize;
        private int diskCacheSize;
        private int parallelSize;
        private File diskCachePath;

        private ImageLoaderOptions(Builder builder) {
            this.parallelSize = builder.parallelSize;
            this.memoryCacheSize = builder.memoryCacheSize;
            this.diskCacheSize = builder.diskCacheSize;
            this.networkTimeOut = builder.connectTimeOut;
            this.diskCachePath = builder.diskCachePath;
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

        File getDiskCachePath() {
            return diskCachePath;
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
            private File diskCachePath;

            public Builder() {
                memoryCacheSize = DEFAULT_MAX_MEMORY_CACHE;
                diskCacheSize = DEFAULT_MAX_DISK_CACHE;
                parallelSize = 4;
                connectTimeOut = 30;// 30s
                diskCachePath = null;
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

            public void diskCachePath(File diskCachePath) {
                this.diskCachePath = diskCachePath;
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

    private static File getDefaultDiskCacheDir(Context context) {
        final String fileName = "cache4bitmap";
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
            return new File(context.getExternalCacheDir(), fileName);
        } else {
            return new File(context.getCacheDir(), fileName);
        }
    }

    private static String md5(String data) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(data.getBytes());
            byte[] digest = mDigest.digest();
            StringBuilder sb = new StringBuilder();
            for (byte aDigest : digest) {
                String hex = Integer.toHexString(0xFF & aDigest);
                if (hex.length() == 1) {
                    sb.append('0');
                }
                sb.append(hex);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(data.hashCode());
        }
        return cacheKey;
    }

}
