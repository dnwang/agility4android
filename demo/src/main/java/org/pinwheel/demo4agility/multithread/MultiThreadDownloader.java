package org.pinwheel.demo4agility.multithread;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved
 *
 * @author dnwang
 * {@link org.pinwheel.agility.tools.Downloader}
 */
public class MultiThreadDownloader {

    private Handler mainHandler;
    private ExecutorService executor;
    private CopyOnWriteArraySet<Worker> workers;
    private long contentLength = -1;

    private int threadSize;
    private File file;

    private OnDownloadListener downloadListener;
    private Set<Callback<Boolean>> completeCallbacks;
    private Set<Callback<Long>> progressCallbacks;
    private Set<Callback<Long>> prepareCallbacks;

    public MultiThreadDownloader() {
        this.prepareCallbacks = new HashSet<>(2);
        this.progressCallbacks = new HashSet<>(2);
        this.completeCallbacks = new HashSet<>(2);
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.workers = new CopyOnWriteArraySet<>();
        this.executor = Executors.newCachedThreadPool();
        this.threadSize = 3;
    }

    @Deprecated
    public MultiThreadDownloader setOnDownloadListener(OnDownloadListener listener) {
        this.downloadListener = listener;
        return this;
    }

    public MultiThreadDownloader addCompleteCallback(Callback<Boolean> callable) {
        if (callable != null) {
            this.completeCallbacks.add(callable);
        }
        return this;
    }

    public MultiThreadDownloader removeCompleteCallback(Callback<Boolean> callable) {
        if (callable == null) {
            this.completeCallbacks.clear();
        } else {
            this.completeCallbacks.remove(callable);
        }
        return this;
    }

    public MultiThreadDownloader addProgressCallback(Callback<Long> callable) {
        if (callable != null) {
            this.progressCallbacks.add(callable);
        }
        return this;
    }

    public MultiThreadDownloader removeProgressCallback(Callback<Long> callable) {
        if (callable == null) {
            this.progressCallbacks.clear();
        } else {
            this.progressCallbacks.remove(callable);
        }
        return this;
    }

    public MultiThreadDownloader addPrepareCallback(Callback<Long> callable) {
        if (callable != null) {
            this.prepareCallbacks.add(callable);
        }
        return this;
    }

    public MultiThreadDownloader removePrepareCallback(Callback<Long> callable) {
        if (callable == null) {
            this.prepareCallbacks.clear();
        } else {
            this.prepareCallbacks.remove(callable);
        }
        return this;
    }

    public MultiThreadDownloader setFile(File file) {
        this.file = file;
        return this;
    }

    public File getFile() {
        return file;
    }

    public MultiThreadDownloader setThreadSize(int size) {
        this.threadSize = Math.max(1, size);
        return this;
    }

    public int getThreadSize() {
        return threadSize;
    }

    public long getContentLength() {
        return contentLength;
    }

    public MultiThreadDownloader clear() {
        contentLength = -1;
        workers.clear();
        return this;
    }

    public void release() {
        clear();
        file = null;
        downloadListener = null;
        completeCallbacks.clear();
        progressCallbacks.clear();
        if (executor != null) {
            executor.shutdownNow();
        }
    }

    public MultiThreadDownloader open(final String urlStr) {
        clear();
        if (TextUtils.isEmpty(urlStr)) {
            dividerError(new NullPointerException("open url error, url is empty"));
            return this;
        }
        if (file == null) {
            dividerError(new NullPointerException("open url error, file is empty"));
            return this;
        }
        if (!file.exists()) {
            File path = file.getParentFile();
            if (!path.exists() && !path.mkdirs()) {
                dividerError(new IllegalStateException("open url error, can't create dir"));
                return this;
            }
        } else {
            file.delete();
        }
        executor.submit((Runnable) () -> {
            boolean loopCheck = false;

            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection) new URL(urlStr).openConnection();
                conn.setConnectTimeout(20 * 1000);
                conn.setReadTimeout(20 * 1000);
                conn.setRequestProperty("Accept-Encoding", "identity");
                conn.connect();
                final int code = conn.getResponseCode();
                if (code == HttpURLConnection.HTTP_OK) {
                    contentLength = conn.getContentLength();
                    dividerPrepare(contentLength);
                    if (contentLength < 1024) {// < 1M
                        allotThread(urlStr, 1, contentLength);
                    } else {
                        allotThread(urlStr, threadSize, contentLength);
                    }
                    loopCheck = true;
                } else {
                    throw new IllegalStateException("open url error, response code=" + code);
                }
            } catch (Exception e) {
                dividerError(e);
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }

            if (loopCheck) {
                long lastTime = 0;
                long currentTime = 0;
                while (true) {
                    currentTime = System.currentTimeMillis();
                    if (currentTime - lastTime > 1000) {
                        lastTime = currentTime;
                        if (checkWorkerStatus()) {
                            break;
                        }
                    }
                }
            }
        });
        return this;
    }

    private void allotThread(final String url, final int threadSize, long contentLength) {
        long begin, end;
        for (int i = 0; i < threadSize; i++) {
            begin = contentLength / threadSize * i;
            end = (i == threadSize - 1) ? contentLength : contentLength / threadSize * (i + 1);
            Worker worker = new Worker(url, file, begin, end);
            worker.setFuture(executor.submit(worker));
            workers.add(worker);
        }
    }

    private boolean checkWorkerStatus() {
        if (workers == null || workers.isEmpty()) {
            return true;
        }
        boolean isError = false;
        int sumOfCompletedWorker = 0;
        long progress = 0;
        for (Worker worker : workers) {
            progress += worker.progress;
            if (Status.ERROR == worker.status) {
                isError = true;
            } else if (Status.COMPLETE == worker.status) {
                sumOfCompletedWorker++;
            }
        }
        if (isError) {
            dividerError(new Exception("worker throw exception, download error."));
            for (Worker worker : workers) {
                worker.selfFuture.cancel(true);// intercept all
            }
            clear();
        } else if (sumOfCompletedWorker == threadSize) {
            dividerSuccess();
            clear();
        } else {
            dividerProgress(progress, contentLength);
        }
        return isError || (sumOfCompletedWorker == threadSize);
    }

    private void dividerPrepare(long contentLength) {
        post(() -> {
            if (prepareCallbacks != null && !prepareCallbacks.isEmpty()) {
                for (Callback<Long> callback : prepareCallbacks) {
                    callback.run(contentLength);
                }
            }
        });
    }

    private void dividerProgress(long progress, long total) {
        post(() -> {
            if (downloadListener != null) {
                downloadListener.onProgress(progress, total);
            }
            if (progressCallbacks != null && !progressCallbacks.isEmpty()) {
                for (Callback<Long> callback : progressCallbacks) {
                    callback.run(progress);
                }
            }
        });
    }

    private void dividerError(Exception e) {
        post(() -> {
            if (downloadListener != null) {
                downloadListener.onError(e);
            }
            if (completeCallbacks != null && !completeCallbacks.isEmpty()) {
                for (Callback<Boolean> callback : completeCallbacks) {
                    callback.run(false);
                }
            }
        });
    }

    private void dividerSuccess() {
        post(() -> {
            if (downloadListener != null) {
                downloadListener.onSuccess(file);
            }
            if (completeCallbacks != null && !completeCallbacks.isEmpty()) {
                for (Callback<Boolean> callback : completeCallbacks) {
                    callback.run(true);
                }
            }
        });
    }

    private void post(Runnable runnable) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            mainHandler.post(runnable);
        } else {
            runnable.run();
        }
    }

    private static class Worker implements Runnable {

        private final static int TIME_OUT = 20 * 1000;

        private Status status = Status.NONE;
        private String url;
        private File file;
        private long begin, end;
        private long progress;

        public Worker(String url, File file, long begin, long end) {
            this.url = url;
            this.file = file;
            this.begin = begin;
            this.end = end;
        }

        @Override
        public void run() {
            RandomAccessFile accessFile = null;
            HttpURLConnection conn = null;
            InputStream inStream = null;
            try {
                status = Status.START;
                accessFile = new RandomAccessFile(file, "rwd");
                accessFile.seek(begin);
                conn = (HttpURLConnection) new URL(url).openConnection();
                conn.setConnectTimeout(TIME_OUT);
                conn.setReadTimeout(TIME_OUT);
                conn.setRequestProperty("Accept-Encoding", "identity");
                conn.setRequestProperty("Range", "bytes=" + begin + "-" + end);
                conn.connect();
                inStream = conn.getInputStream();
                byte[] buf = new byte[1024];
                int len;
                while ((len = inStream.read(buf)) != -1) {
                    accessFile.write(buf, 0, len);
                    progress += len;
                }
                status = Status.COMPLETE;
            } catch (Exception e) {
                status = Status.ERROR;
            } finally {
                close(inStream);
                close(accessFile);
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }

        Future selfFuture;

        public void setFuture(Future future) {
            this.selfFuture = future;
        }

    }

    private static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private enum Status {
        NONE, START, COMPLETE, ERROR
    }

    @Deprecated
    public interface OnDownloadListener {
        void onSuccess(File file);

        void onProgress(long progress, long total);

        void onError(Exception e);
    }

    public interface Callback<V> {
        void run(V value);
    }

}
