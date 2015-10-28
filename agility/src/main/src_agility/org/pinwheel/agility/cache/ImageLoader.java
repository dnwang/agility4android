package org.pinwheel.agility.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import org.pinwheel.agility.net.HttpClientAgent;
import org.pinwheel.agility.net.HttpConnectionAgent;
import org.pinwheel.agility.net.OkHttpAgent;
import org.pinwheel.agility.net.VolleyAgent;
import org.pinwheel.agility.net.parser.IDataParser;

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
public final class ImageLoader {
    private static final String TAG = ImageLoader.class.getSimpleName();

    public static final int DEFAULT = 0x00;
    public static final int OKHTTP = 0x01;
    public static final int VOLLEY = 0x02;

    public static int HTTP_ENGINE = DEFAULT;

    private static final String PATH = "bitmap";
    private static final int CACHE_SIZE_OF_DISK = 1024 * 1024 * 1024;//1G
    private static final int CACHE_SIZE_OF_MEMORY = (int) (Runtime.getRuntime().maxMemory() / 1024 / 8);// 1/8 total memory size

    private static ImageLoader instance = null;

    public synchronized static ImageLoader getInstance(Context context) {
        if (instance == null) {
            instance = new ImageLoader(context);
        }
        return instance;
    }

    private final HashMap<String, ImageTaskDispatcher.Task> taskMap;
    private SimpleCacheLoader cacheLoader;
    private ImageTaskDispatcher taskDispatcher;
    private ExecutorService executor;

    private ImageLoader(Context context) {
        executor = Executors.newCachedThreadPool();
        taskMap = new HashMap<>();
        DiskCache diskCache = new DiskCache(Tools.getDiskCacheDir(context, PATH), 0, CACHE_SIZE_OF_DISK);
        MemoryCache memoryCache = new MemoryCache(CACHE_SIZE_OF_MEMORY);
        cacheLoader = new SimpleCacheLoader(memoryCache, diskCache);

        // Auto select http engine
        HttpClientAgent httpClientAgent;
        if (OKHTTP == HTTP_ENGINE) {
            httpClientAgent = new OkHttpAgent();
        } else if (VOLLEY == HTTP_ENGINE) {
            httpClientAgent = new VolleyAgent(context);
        } else {
            httpClientAgent = new HttpConnectionAgent();
        }
        taskDispatcher = new ImageTaskDispatcher(5, httpClientAgent);
    }

    public CacheLoader getCacheLoader() {
        return cacheLoader;
    }

    public void setImage(View view, final String url) {
        if (view == null || TextUtils.isEmpty(url) || executor == null) {
            return;
        }
        final WeakReference<View> viewReference = new WeakReference<>(view);
        executor.submit(new Runnable() {
            @Override
            public void run() {
                String key = Tools.convertByMD5(url);
                Bitmap cache = cacheLoader.getBitmap(key);

                if (cache != null) {
                    // load cache success
                    Tools.setBitmapInUIThread(viewReference, cache);
                } else {
                    Tools.setBitmapInUIThread(viewReference, null);
                    getBitmapAsTask(viewReference, key, url);
                }
            }
        });
    }

    public void setImage(View view, final String url, final int width, final int height) {
        if (view == null || TextUtils.isEmpty(url) || executor == null) {
            return;
        }
        final WeakReference<View> viewReference = new WeakReference<>(view);
        executor.submit(new Runnable() {
            @Override
            public void run() {
                String key = Tools.convertByMD5(url);
                Bitmap cache = cacheLoader.getBitmap(key, width, height);

                if (cache != null) {
                    // load cache success
                    Tools.setBitmapInUIThread(viewReference, cache);
                } else {
                    Tools.setBitmapInUIThread(viewReference, null);// default bitmap
                    getBitmapAsTask(viewReference, key, url);
                }
            }
        });
    }

    public void setImageByScaleType(ImageView imageView, final String url) {
        if (imageView == null || TextUtils.isEmpty(url) || executor == null) {
            return;
        }
        final WeakReference<ImageView> viewReference = new WeakReference<>(imageView);
        executor.submit(new Runnable() {
            @Override
            public void run() {
                String key = Tools.convertByMD5(url);
                if (viewReference.get() == null) {
                    return;
                }
                ImageView imageView = viewReference.get();
                ImageView.ScaleType scaleType = imageView.getScaleType();
                Bitmap cache = cacheLoader.getBitmap(key, scaleType, imageView.getMeasuredWidth(), imageView.getMeasuredHeight());

                if (cache != null) {
                    // load cache success
                    Tools.setBitmapInUIThread(viewReference, cache);
                } else {
                    Tools.setBitmapInUIThread(viewReference, null);
                    getBitmapAsTask(viewReference, key, url);
                }
            }
        });
    }

    private void getBitmapAsTask(final WeakReference<? extends View> viewReference, String key, String url) {
        final ImageTaskDispatcher.Task task = new ImageTaskDispatcher.Task(key, url);
        task.addView(viewReference);
        task.setResponseParser(new CacheParser(task), new HttpClientAgent.OnRequestAdapter<Bitmap>() {
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
        if (!checkAndPutTask(task)) {
            // start this new task now
            taskDispatcher.post(task);
        } else {
            // no need start task, because task was already loaded
            clearViewInTaskMap(viewReference);
            addViewToTask(key, viewReference);
        }
    }

    private boolean checkAndPutTask(ImageTaskDispatcher.Task task) {
        synchronized (taskMap) {
            boolean is = taskMap.containsKey(task.getId());
            if (!is) {
                taskMap.put(task.getId(), task);
            }
            return is;
        }
    }

    private void removeTaskInLoadingComplete(String taskId) {
        synchronized (taskMap) {
            taskMap.remove(taskId);
        }
    }

    private void addViewToTask(String taskId, WeakReference<? extends View> view) {
        synchronized (taskMap) {
            ImageTaskDispatcher.Task task = taskMap.get(taskId);
            if (task != null) {
                task.addView(view);
            }
        }
    }

    private void clearViewInTaskMap(WeakReference<? extends View> view) {
        synchronized (taskMap) {
            Collection<ImageTaskDispatcher.Task> tasks = taskMap.values();
            for (ImageTaskDispatcher.Task task : tasks) {
                task.removeView(view);
            }
        }
    }

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
        instance = null;
    }

    /**
     *
     */
    private class CacheParser implements IDataParser<Bitmap> {

        private ImageTaskDispatcher.Task task;

        public CacheParser(ImageTaskDispatcher.Task task) {
            this.task = task;
        }

        @Override
        public void parse(InputStream inStream) throws Exception {
            cacheLoader.getDiskCache().setCache(task.getId(), inStream);
        }

        @Override
        public void parse(byte[] dataBytes) throws Exception {

        }

        @Override
        public void parse(String dataString) throws Exception {

        }

        @Override
        public Bitmap getResult() {
            return cacheLoader.getBitmap(task.getId(), 250, 250);
        }

        @Override
        public void release() {

        }

        @Override
        public void setOnParseAdapter(OnParseAdapter listener) {

        }
    }

}
