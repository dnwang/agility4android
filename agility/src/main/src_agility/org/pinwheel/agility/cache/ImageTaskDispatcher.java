package org.pinwheel.agility.cache;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import org.pinwheel.agility.net.HttpClientAgent;
import org.pinwheel.agility.net.Request;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
class ImageTaskDispatcher {

    private boolean isDownloading;
    private BlockingQueue<Request> queue;
    private HttpClientAgent httpClientAgent;

    public ImageTaskDispatcher(int maxTaskNum, HttpClientAgent httpClientAgent) {
        this.isDownloading = false;
        this.httpClientAgent = httpClientAgent;
        this.queue = new ArrayBlockingQueue<>(maxTaskNum);
    }

    public void post(Request request) {
        if (!queue.contains(request)) {
            try {
                queue.put(request);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        startDownload();
    }

    private void startDownload() {
        if (isDownloading) {
            // TODO: 10/28/15
        } else if (!queue.isEmpty()) {
            download(queue.poll());
        }
    }

    private void download(final Request request) {
        if (request == null) {
            return;
        }
        request.setOnRequestListener(new OnRequestWrapper(request.getRequestListener()) {
            @Override
            public void onDeliverComplete() {
                isDownloading = false;
                startDownload();
            }
        });
        isDownloading = true;
        httpClientAgent.enqueue(request);
    }

    /**
     * Request callback wrapper
     */
    private static abstract class OnRequestWrapper extends HttpClientAgent.OnRequestAdapter {

        private HttpClientAgent.OnRequestAdapter adapter;

        public OnRequestWrapper(HttpClientAgent.OnRequestAdapter adapter) {
            this.adapter = adapter;
        }

        @Override
        public boolean onRequest(Request request) {
            if (adapter != null) {
                return adapter.onRequest(request);
            }
            return super.onRequest(request);
        }

        @Override
        public boolean onResponse(Object args) {
            if (adapter != null) {
                return adapter.onResponse(args);
            }
            return super.onResponse(args);
        }

        @Override
        public void onDeliverSuccess(Object obj) {
            if (adapter != null) {
                adapter.onDeliverSuccess(obj);
            }
            onDeliverComplete();
        }

        @Override
        public void onDeliverError(Exception e) {
            if (adapter != null) {
                adapter.onDeliverError(e);
            }
            onDeliverComplete();
        }

        public abstract void onDeliverComplete();
    }

    /**
     * Copyright (C), 2015 <br>
     * <br>
     * All rights reserved <br>
     * <br>
     *
     * @author dnwang
     */
    static class Task extends Request {
        private static final String TAG = Task.class.getSimpleName();

        private String id;
        private final HashSet<WeakReference<? extends View>> views;

        public Task(String id, String url) {
            super("GET", url);
            this.id = id;
            this.views = new HashSet<>(1);
        }

        public String getId() {
            return id;
        }

        public String getUrl() {
            return getUrlByMethod();
        }

        public void release() {
            synchronized (views) {
                views.clear();
            }
            id = null;
        }

        public void applyBitmap(final Bitmap bitmap) {
            Log.e(TAG, "applyBitmap()--> url:" + getUrl() + ", size:" + views.size());
            synchronized (views) {
                for (WeakReference<? extends View> viewReference : views) {
                    Tools.setBitmap(viewReference, bitmap);
                }
            }
        }

        public void addView(WeakReference<? extends View> targetView) {
            if (targetView.get() == null) {
                return;
            }
            synchronized (views) {
                views.add(targetView);
            }
        }

        public void removeView(WeakReference<? extends View> targetView) {
            if (targetView.get() == null) {
                return;
            }
            synchronized (views) {
                for (WeakReference<? extends View> reference : views) {
                    View v = reference.get();
                    if (v != null && v == targetView.get()) {
                        views.remove(reference);
                    }
                }
            }
        }

    }
}
