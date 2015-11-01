package org.pinwheel.agility.cache;

import android.graphics.Bitmap;

import org.pinwheel.agility.net.HttpClientAgent;
import org.pinwheel.agility.net.Request;

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
class NetworkTaskDispatcher {

    private boolean isRunning;
    private BlockingQueue<ImageLoader.AsyncLoaderTask> queue;
    private HttpClientAgent httpClientAgent;

    public NetworkTaskDispatcher(int maxTaskNum, HttpClientAgent httpClientAgent) {
        this.isRunning = false;
        this.httpClientAgent = httpClientAgent;
        this.queue = new ArrayBlockingQueue<>(maxTaskNum);
    }

    public void post(ImageLoader.AsyncLoaderTask task) {
        if (!queue.contains(task)) {
            try {
                queue.put(task);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        startRunning();
    }

    private void startRunning() {
        if (isRunning) {
            // TODO: 10/28/15 nothing to do
        } else if (!queue.isEmpty()) {
            execute(queue.poll());
        }
    }

    private void execute(final ImageLoader.AsyncLoaderTask task) {
        if (task == null) {
            isRunning = false;
            startRunning();
            return;
        }
        Request request = new Request.Builder()
                .url(task.getUrl())
                .method("GET")
                .timeOut(task.getOptions().getNetworkTimeOut(), 0)
                .create();
        request.setResponseParser(task.getDataParser(), new HttpClientAgent.OnRequestWrapper<Bitmap>(task.getRequestAdapter()) {
            @Override
            public void onDeliverComplete() {
                isRunning = false;
                startRunning();
            }
        });
        isRunning = true;
        httpClientAgent.enqueue(request);
    }

}
