package org.pinwheel.agility.tools;

import android.os.Handler;
import android.os.Looper;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
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
 */
public final class Downloader {

    public interface Callback<V> {
        void call(V arg0);
    }

    public interface Callback2<V, K> {
        void call(V arg0, K arg1);
    }

    private Handler mainHandler;
    private ExecutorService executor;
    private CopyOnWriteArraySet<Worker> workers;
    private int maxThreadSize;

    private DownloadInfo downloadInfo;

    private Callback<Boolean> completeCallback;
    private Callback2<Long, Long> progressCallback;

    public Downloader() {
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.workers = new CopyOnWriteArraySet<>();
        this.executor = Executors.newCachedThreadPool();
        this.maxThreadSize = 3;
    }

    public Downloader onProcess(Callback2<Long, Long> callable) {
        progressCallback = callable;
        return this;
    }

    public Downloader onComplete(Callback<Boolean> callable) {
        completeCallback = callable;
        return this;
    }

    public Downloader setMaxThreadSize(int size) {
        this.maxThreadSize = Math.max(1, size);
        return this;
    }

    private long getContentLength() {
        return null == downloadInfo ? -1 : downloadInfo.contentLength;
    }

    private long getProgress() {
        return null == downloadInfo ? 0 : downloadInfo.progress;
    }

    private File getFile() {
        return null == downloadInfo ? null : downloadInfo.file;
    }

    private void dividerProgress(final long progress) {
        post(new Runnable() {
            @Override
            public void run() {
                if (progressCallback != null) {
                    progressCallback.call(progress, getContentLength());
                }
            }
        });
    }

    private void dividerError(String msg) {
        post(new Runnable() {
            @Override
            public void run() {
                if (completeCallback != null) {
                    completeCallback.call(false);
                }
            }
        });
    }

    private void dividerSuccess() {
        post(new Runnable() {
            @Override
            public void run() {
                if (completeCallback != null) {
                    completeCallback.call(true);
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

    public Downloader cancel() {
        if (null != workers) {
            if (!workers.isEmpty()) {
                for (Worker worker : workers) {
                    worker.self.cancel(true);// intercept all
                }
            }
            workers.clear();
        }
        if (null != downloadInfo && !downloadInfo.isCompleted()) {
            downloadInfo.syncProp();
        }
        downloadInfo = null;
        return this;
    }

    public void release() {
        cancel();
        completeCallback = null;
        progressCallback = null;
        if (null != executor) {
            executor.shutdownNow();
        }
    }

    public Downloader open(final File file, final String url) {
        cancel();// cancel first
        if (null == url || "".equals(url) || null == file || file.isDirectory()) {
            dividerError("error, url or file is empty");
            return this;
        }
        executor.execute(new Runnable() {
            @Override
            public void run() {
                downloadInfo = findRecorder(file, url);
                if (null == downloadInfo) {
                    final long contentLength = Common.getContentLength(url);
                    if (contentLength != -1) {
                        downloadInfo = new DownloadInfo(file, url);
                        downloadInfo.contentLength = contentLength;
                    }
                }
                if (null == downloadInfo) {
                    dividerError("error, can't create downloadInfo");
                    return;
                }
                allotWorker(downloadInfo.getSpaceBlocks());// allot worker
                if (null != workers && workers.isEmpty()) {
                    dividerError("error, can't allot worker to download");
                    return;
                }
                // monitoring
                long lastTime = 0;
                long currentTime = 0;
                while (true) {
                    currentTime = System.currentTimeMillis();
                    if (currentTime - lastTime > 999) {
                        lastTime = currentTime;
                        syncWorkerProgress();
                        if (checkWorkerState()) break;
                    }
                }
            }
        });
        return this;
    }

    private DownloadInfo findRecorder(File file, String url) {
        if (null == file || null == url || "".equals(url)) {
            return null;
        }
        DownloadInfo tmp = DownloadInfo.load(file);
        return (tmp != null && url.equals(tmp.url)) ? tmp : null;
    }

    private void allotWorker(List<Block> spaceBlocks) {
        if (null == downloadInfo || null == spaceBlocks || spaceBlocks.isEmpty()) {
            return;
        }
        spaceBlocks = reSplitBlock(spaceBlocks);
        final int sizeOfBlock = spaceBlocks.size();
        final int workerSize = Math.min(maxThreadSize, sizeOfBlock);
        for (int i = 0; i < workerSize; i++) {
            Block block = spaceBlocks.get(i);
            Worker worker = new Worker(downloadInfo.url, downloadInfo.file, block.begin, block.end);
            worker.self = executor.submit(worker);
            workers.add(worker);
        }
    }

    private List<Block> reSplitBlock(List<Block> spaceBlocks) {
        if (downloadInfo.contentLength < 1024) {// > 1M
            return spaceBlocks;
        }
        List<Block> tmp = new ArrayList<>();
        final long maxWorkerLength = downloadInfo.contentLength / maxThreadSize;
        for (Block block : spaceBlocks) {
            List<Block> subBlocks = splitBlock(block, maxWorkerLength);
            if (null == subBlocks) {
                tmp.add(block);
            } else {
                tmp.addAll(subBlocks);
            }
        }
        return tmp;
    }

    private List<Block> splitBlock(Block rawBlock, long subBlockMaxLength) {
        if (null == rawBlock || rawBlock.getLength() <= subBlockMaxLength) {
            return null;
        }
        int size = 2;
        long length;
        while ((length = rawBlock.getLength() / size) > subBlockMaxLength) {
            size++;
        }
        List<Block> subBlocks = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            long begin = rawBlock.begin + i * length;
            subBlocks.add(new Block((i == 0 ? begin : begin + 1), Math.min(begin + length, rawBlock.end)));
        }
        return subBlocks;
    }

    private void syncWorkerProgress() {
        if (workers == null || workers.isEmpty()) {
            return;
        }
        for (Worker worker : workers) {
            Block block = worker.getNewProgress();
            if (null != block && null != downloadInfo) {
                if (downloadInfo.merge(block)) {
                    downloadInfo.progress += block.getLength();
                } else {
                    Common.logout("merge error. " + block.toString() + " -> " + downloadInfo.areaToString());
                }
            }
        }
        if (null != downloadInfo) {
            downloadInfo.syncProp();
        }
    }

    private boolean checkWorkerState() {
        if (null == downloadInfo && null == workers || workers.isEmpty()) {
            dividerError("unknown error, params is empty.");
            return true;
        }
        final int sizOfWorkers = workers.size();
        boolean isError = false;
        boolean isSuccess = false;
        int sumOfCompletedWorker = 0;
        for (Worker worker : workers) {
            if (Worker.State.ERROR == worker.state) {
                isError = true;
            } else if (Worker.State.COMPLETE == worker.state) {
                sumOfCompletedWorker++;
            }
        }
        if (isError) {
            dividerError("worker throw exception, download error.");
            cancel();
        } else if (sumOfCompletedWorker == sizOfWorkers) {
            if (downloadInfo.isCompleted()) {
                downloadInfo.clearProp();// clear prop
                isSuccess = true;
                dividerSuccess();
                cancel();
            } else {
                // allot again.
                allotWorker(downloadInfo.getSpaceBlocks());
            }
        } else {
            dividerProgress(getProgress());
        }
        return isError || isSuccess;
    }

}

/**
 * block download thread
 */
class Worker implements Callable<Boolean> {

    enum State {
        NONE, START, COMPLETE, ERROR
    }

    private final static int TIME_OUT = 30 * 1000;

    Future self;
    State state = State.NONE;
    private String url;
    private File file;
    private long begin, end;
    long progress;

    Worker(String url, File file, long begin, long end) {
        this.url = url;
        this.file = file;
        this.begin = begin;
        this.end = end;
    }

    @Override
    public Boolean call() {
        RandomAccessFile accessFile = null;
        HttpURLConnection conn = null;
        InputStream inStream = null;
        try {
            state = State.START;
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
            state = State.COMPLETE;
        } catch (Exception e) {
            state = State.ERROR;
        } finally {
            Common.close(inStream);
            Common.close(accessFile);
            Common.close(conn);
        }
        return State.COMPLETE == state;
    }

    private long lastOffset = 0;

    Block getNewProgress() {
        long newBegin = begin + lastOffset;
        long newEnd = Math.min(begin + progress, end);
        if (newBegin < newEnd) {
            lastOffset = progress + 1;
            return new Block(newBegin, newEnd);
        } else {
            return null;
        }
    }

}

class DownloadInfo implements Serializable {

    private static final String SUFFIX_PROP = ".prop";

    static DownloadInfo load(File file) {
        if (null == file || !file.exists() || file.isDirectory()) {
            return null;
        }
        File propFile = getPropFile(file);
        if (!propFile.exists() || file.isDirectory()) {
            return null;
        }
        DownloadInfo recorder = null;
        ObjectInputStream inputStream = null;
        try {
            inputStream = new ObjectInputStream(new FileInputStream(propFile));
            DownloadInfo tmp = (DownloadInfo) inputStream.readObject();
            if (file.getAbsolutePath().equals(tmp.file.getAbsolutePath())) {
                recorder = tmp;
            }
        } catch (IOException | ClassNotFoundException e) {
            Common.logout(e.getMessage());
        } finally {
            Common.close(inputStream);
        }
        return recorder;
    }

    private static File getPropFile(File file) {
        return new File(file.getParentFile(), file.getName() + SUFFIX_PROP);
    }

    String url;
    File file;
    private File propFile;
    long contentLength;
    long progress;
    private List<Block> downloadedBlocks;

    DownloadInfo(File f, String url) {
        this.file = f;
        this.url = url;
        this.propFile = getPropFile(file);
        this.contentLength = -1;
        this.progress = 0;
        this.downloadedBlocks = new CopyOnWriteArrayList<>();
        // create new file.
        Common.newFile(file);
        Common.newFile(propFile);
    }

    void syncProp() {
        ObjectOutputStream outputStream = null;
        try {
            if (!propFile.exists()) {
                Common.newFile(propFile);
            }
            outputStream = new ObjectOutputStream(new FileOutputStream(propFile));
            outputStream.writeObject(this);
        } catch (Exception e) {
            Common.logout(e.getMessage());
        } finally {
            Common.close(outputStream);
        }
    }

    void clearProp() {
        if (null != propFile && propFile.exists()) {
            propFile.delete();
        }
    }

    boolean isCompleted() {
        return contentLength != -1 && downloadedBlocks.size() == 1 && downloadedBlocks.get(0).getLength() >= contentLength;
    }

    boolean merge(Block block) {
        if (block == null || block.end < block.begin) {
            return false;
        }
        boolean result = false;
        final int blockSize = downloadedBlocks.size();
        if (blockSize == 0) {
            downloadedBlocks.add(block);
            result = true;
        } else {
            Block left, right;
            int removeIndex = -1;
            for (int insertIndex = blockSize; insertIndex >= 0; insertIndex--) {
                left = getBlock(insertIndex - 1);
                right = getBlock(insertIndex);

                if (left != null && right == null) {// 最右边
                    if (left.end + 1 > block.begin) {
                        result = false;// 错误,左侧区域重合 [1,3]&<3,5>
                    } else if (left.end + 1 == block.begin) {
                        left.end = block.end;
                        result = true;// 合并左侧 [1,2]&<3,5>
                        break;
                    } else {
                        downloadedBlocks.add(block);
                        result = true;// 正常添加至最右
                        break;
                    }
                } else if (left == null && right != null) {// 最左边
                    if (right.begin - 1 < block.end) {
                        result = false;// 错误,右侧区域重合 <3,5>&[5,8]
                    } else if (right.begin - 1 == block.end) {
                        right.begin = block.begin;
                        result = true;// 合并右侧 <3,5>&[6,8]
                        break;
                    } else {
                        downloadedBlocks.add(insertIndex, block);// insertIndex=0
                        result = true;// 正常添加至最左
                        break;
                    }
                } else if (left != null && right != null) {// 中间
                    if (left.end + 1 > block.begin || right.begin - 1 < block.end) {
                        result = false;// 错误,左右边界重合 [1,3]&<3,5>&[4,8]
                    } else if (left.end + 1 == block.begin && right.begin - 1 == block.end) {
                        left.end = right.end; // 合并左右两侧 [1,2]&<3,5>&[6,8]
                        removeIndex = insertIndex;
                        result = true;
                        break;
                    } else if (left.end + 1 == block.begin) {
                        left.end = block.end;// 合并左侧 [1,2]&<3,5>
                        result = true;
                        break;
                    } else if (right.begin - 1 == block.end) {
                        right.begin = block.begin;// 合并右侧 <3,5>&[6,8]
                        result = true;
                        break;
                    } else {
                        downloadedBlocks.add(insertIndex, block);// 添加至中间 [0,1]&<3,5>&[7,8]
                        result = true;
                        break;
                    }
                }
            }
            if (removeIndex >= 0) {
                downloadedBlocks.remove(removeIndex);
            }
        }
        return result;
    }

    List<Block> getSpaceBlocks() {
        if (isCompleted()) {
            return null;
        }
        List<Block> spaceBlocks = new ArrayList<>();
        if (downloadedBlocks.isEmpty()) {
            spaceBlocks.add(new Block(0, contentLength));
        } else {
            long begin, end;
            Block lastBlock = null;
            for (Block block : downloadedBlocks) {
                begin = lastBlock == null ? 0 : lastBlock.end + 1;
                end = block.begin - 1;
                if (begin < end) {
                    spaceBlocks.add(new Block(begin, end));
                }

                lastBlock = block;
            }
            Block endBlock = downloadedBlocks.get(downloadedBlocks.size() - 1);
            if (endBlock.end < contentLength) {
                spaceBlocks.add(new Block(endBlock.end + 1, contentLength));
            }
        }
        return spaceBlocks;
    }

    Block findSpaceBlockAfterPosition(long pos) {
        pos = Math.max(0, pos);
        Block lastBlock = null;
        boolean lastAreaIsInsert = false;

        for (Block block : downloadedBlocks) {
            if (pos < block.begin) {
                if (lastBlock == null) {
                    return new Block(pos, block.begin - 1);
                } else {
                    if (lastAreaIsInsert) {
                        return new Block(lastBlock.end + 1, block.begin - 1);
                    } else {
                        return new Block(pos, block.begin - 1);
                    }
                }
            }

            lastAreaIsInsert = (block.begin <= pos && pos <= block.end);
            lastBlock = block;
        }

        if (lastBlock == null) {
            return new Block(pos, -1);
        } else {
            if (lastAreaIsInsert) {
                return new Block(lastBlock.end + 1, -1);
            } else {
                return new Block(pos, -1);
            }
        }
    }

    private Block getBlock(int index) {
        if (index < 0 || index >= downloadedBlocks.size()) {
            return null;
        }
        return downloadedBlocks.get(index);
    }

    String areaToString() {
        String s = "";
        for (Block block : downloadedBlocks) {
            s += block.toString();
        }
        return "{" + s + "}";
    }
}

/**
 * block downloadInfo. [begin, end]
 */
class Block implements Serializable {
    long begin, end;

    Block(long begin, long end) {
        this.begin = begin;
        this.end = end;
    }

    boolean contains(long pos) {
        return begin <= pos && pos <= end;
    }

    long getLength() {
        return Math.max(0, end - begin + 1);
    }

    @Override
    public String toString() {
        return "[" + begin + "," + end + "]";
    }
}

class Common {

    static void close(Closeable closeable) {
        if (null != closeable) {
            try {
                closeable.close();
            } catch (IOException e) {
                Common.logout(e.getMessage());
            }
        }
    }

    static void close(HttpURLConnection connection) {
        if (null != connection) {
            connection.disconnect();
        }
    }

    /**
     * encode By MD5
     *
     * @param str
     * @return String
     */
    static String md5(String str) {
        if (str == null) {
            return null;
        }
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(str.getBytes());
            return new String(encodeHex(messageDigest.digest()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Used to build output as Hex
     */
    private static final char[] DIGITS_LOWER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * Converts an array of bytes into an array of characters representing the hexadecimal values of each byte in order.
     * The returned array will be double the length of the passed array, as it takes two characters to represent any
     * given byte.
     *
     * @param data a byte[] to convert to Hex characters
     * @return A char[] containing hexadecimal characters
     */
    private static char[] encodeHex(final byte[] data) {
        final int l = data.length;
        final char[] out = new char[l << 1];
        // two characters form the hex value.
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = DIGITS_LOWER[(0xF0 & data[i]) >>> 4];
            out[j++] = DIGITS_LOWER[0x0F & data[i]];
        }
        return out;
    }

    static long getContentLength(String url) {
        long contentLength = -1;
        if (null == url || "".equals(url)) {
            return contentLength;
        }
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestProperty("Accept-Encoding", "identity");
            conn.connect();
            final int code = conn.getResponseCode();
            if (code == HttpURLConnection.HTTP_OK) {
                contentLength = conn.getContentLength();
            }
        } catch (Exception e) {
            Common.logout(e.getMessage());
        } finally {
            close(conn);
        }
        return contentLength;
    }

    /**
     * cover
     */
    static boolean newFile(File file) {
        if (null == file) {
            return false;
        }
        if (file.exists()) {
            file.delete();
        } else {
            File parent = file.getParentFile();
            if (!parent.exists()) {
                parent.mkdirs();
            }
        }
        try {
            return file.createNewFile();
        } catch (Exception e) {
            Common.logout(e.getMessage());
        }
        return false;
    }

    static void logout(Object obj) {
        System.out.println("Downloader: " + (obj == null ? "" : obj.toString()));
    }

}
