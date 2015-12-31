package org.pinwheel.demo4agility.multithread;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved
 *
 * @author dnwang
 */
@Deprecated
final class MultiThreadDownloader {

    private int thread_count;
    private OnDownloadListener listener;
    private Handler uiHandler;
    private Set<DownloadRunnable> downloadthreads;
    private ExecutorService executorService;

    private DownloadTask currentTask;

    public MultiThreadDownloader(int thread_count) {
        if (thread_count < 1) {
            throw new IllegalStateException("thread count must be more than one ! :" + thread_count);
        }
        this.thread_count = thread_count;
        this.downloadthreads = new HashSet<>(thread_count);
        this.uiHandler = new Handler(Looper.getMainLooper());
        this.executorService = Executors.newFixedThreadPool(thread_count);
    }

    public void setOnDownloadListener(OnDownloadListener listener) {
        this.listener = listener;
    }

    public void start(final DownloadTask task) {
        if (task == null || !task.verify()) {
            postError(new IllegalStateException("DownloadTask params error"));
            return;
        }
        currentTask = task;
        File file = new File(task.path);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                postError(new IllegalStateException("create file error"));
                return;
            }
        } else {
            file.delete();
        }

        new Thread() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
                try {
                    URL url = new URL(task.url);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(20 * 1000);
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Accept", "image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
                    conn.setRequestProperty("Accept-Language", "zh-CN");
                    conn.setRequestProperty("Accept-Encoding", "identity");
                    conn.setRequestProperty("Referer", task.url);
                    conn.setRequestProperty("Charset", "UTF-8");
                    conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.connect();
                    int code = conn.getResponseCode();
                    if (code == 200) {
                        long length = conn.getContentLength();
                        if (length < 0) {
                            throw new IllegalStateException("unkown file size");
                        } else {
                            allotThread(task, thread_count, length);
                        }
                    } else {
                        throw new IllegalStateException("response code=" + code);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    postError(e);
                } finally {
                    if (conn != null) {
                        conn.disconnect();
                    }
                }
            }
        }.start();
    }

    private void allotThread(DownloadTask task, int thread_count, long length) {
        for (int i = 0; i < thread_count; i++) {
            long from = length / thread_count * i;
            long end = (i < thread_count - 1) ? (length / thread_count * (i + 1)) : (length % thread_count);
            downloadthreads.add(new DownloadRunnable(task.url, task.path, from, end));
        }

        for (DownloadRunnable downloadRunnable : downloadthreads) {
            executorService.submit(downloadRunnable);
        }
    }

    private void checkEveryThreadAndNotify() {
        int complete_thread = 0;
        for (DownloadRunnable downloadRunnable : downloadthreads) {
            if (downloadRunnable.state == DownloadRunnable.STATE_ERROR) {
                postError(new Exception("Download thread error"));
                return;
            } else if (downloadRunnable.state == DownloadRunnable.STATE_WAITING) {
                executorService.submit(downloadRunnable);
            } else if (downloadRunnable.state == DownloadRunnable.STATE_COMPLETE) {
                complete_thread++;
            }
        }
        if (complete_thread == thread_count) {
            postSuccess(null);
        }
    }

    private void clearAll() {
        if (downloadthreads != null) {
            downloadthreads.clear();
        }
        if (executorService != null) {
            executorService.shutdown();
        }
    }

    private void postError(final Exception e) {
        clearAll();
        if (listener != null) {
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onError(e);
                }
            });
        }
    }

    private void postProgress(final long progress, final long length) {
        if (listener != null) {
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onProgress(progress, length);
                }
            });
        }
    }

    private void postSuccess(final File file) {
        clearAll();
        if (listener != null) {
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onSuccess(file);
                }
            });
        }
    }

    private class DownloadRunnable implements Runnable {

        public static final int STATE_WAITING = 1;
        public static final int STATE_RUNNING = 0;
        public static final int STATE_COMPLETE = 2;
        public static final int STATE_ERROR = 3;

        private String url;
        private String file_name;
        private long from, end;

        public int state;

        public DownloadRunnable(String url, String file_name, long from, long end) {
            this.url = url;
            this.file_name = file_name;
            this.from = from;
            this.end = end;
            this.state = STATE_WAITING;
        }

        @Override
        public void run() {
            state = STATE_RUNNING;
            System.out.println("from: " + from + ", end: " + end);

            HttpURLConnection conn = null;
            try {
                URL url = new URL(this.url);
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(20 * 1000);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
                conn.setRequestProperty("Accept-Language", "zh-CN");
                conn.setRequestProperty("Referer", this.url);
                conn.setRequestProperty("Charset", "UTF-8");
                conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("Range", "bytes=" + from + "-" + end);
                RandomAccessFile file = new RandomAccessFile(this.file_name, "rw");
                BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
                byte[] buf = new byte[1024 * 8];
                int len;
                while ((len = bis.read(buf)) > 0) {
                    synchronized (file) {
                        file.seek(from);
                        file.write(buf, 0, len);
                    }
                    from += len;
                    System.out.println("from: " + from + ", end: " + end);
                }
                file.close();
            } catch (Exception e) {
                state = STATE_ERROR;
                e.printStackTrace();
                postError(e);
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }

            state = STATE_COMPLETE;
            checkEveryThreadAndNotify();
        }
    }

    /**
     * Copyright (C), 2015 <br>
     * <br>
     * All rights reserved
     *
     * @author dnwang
     */
    public static class DownloadTask {
        public String url;
        public String path;

        public DownloadTask(String url, String path) {
            this.url = url;
            this.path = path;
        }

        private boolean verify() {
            return !TextUtils.isEmpty(url) && !TextUtils.isEmpty(path);
        }
    }

    /**
     * Copyright (C), 2015 <br>
     * <br>
     * All rights reserved
     *
     * @author dnwang
     */
    public static interface OnDownloadListener {
        public void onSuccess(File file);

        public void onProgress(long progress, long length);

        public void onError(Exception e);
    }

}
