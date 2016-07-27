package org.pinwheel.demo4agility.activity;

import android.Manifest;
import android.graphics.Bitmap;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;

import org.pinwheel.agility.net.HttpClientAgent;
import org.pinwheel.agility.net.OkHttpAgent;
import org.pinwheel.agility.net.Request;
import org.pinwheel.agility.net.parser.BitmapParser;
import org.pinwheel.agility.net.parser.DataParserAdapter;
import org.pinwheel.agility.net.parser.FileParser;
import org.pinwheel.agility.net.parser.GsonParser;
import org.pinwheel.agility.tools.Downloader;
import org.pinwheel.agility.util.BaseUtils;
import org.pinwheel.demo4agility.entity.WeatherEntity;
import org.pinwheel.demo4agility.multithread.MultiThreadDownloader;

import java.io.File;


public class HttpClientAgentActivity extends AbsMethodListActivity {

    HttpClientAgent httpClientAgent;

    @Override
    protected void beforeInitView() {
        httpClientAgent = new OkHttpAgent(6);
//        httpClientAgent = new HttpConnectionAgent(6);
//        httpClientAgent = new VolleyAgent(getApplicationContext());
    }

    @Override
    protected void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        showLogger(!isLoggerShown());
    }

    @Override
    protected void afterInitView() {
        // TODO: 10/15/15 cancel test (ok)
//        for (int i = 0; i < 5; i++) {
//            finalTag = System.currentTimeMillis() + i + "";
//            gsonRequest();
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        httpClientAgent.release();
    }

    @TestMethod(title = "json响应")
    public void gsonRequest() {
        final long startTime = System.currentTimeMillis();

        String url = "http://wthrcdn.etouch.cn/weather_mini";
        final String tag = "gsonRequest";
        Request request = new Request.Builder().url(url).tag(tag).keepSingle(true).addParam("citykey", "101010100").create();
        request.setResponseParser(new GsonParser<>(WeatherEntity.class), new HttpClientAgent.OnRequestAdapter<WeatherEntity>() {
            @Override
            public void onDeliverSuccess(WeatherEntity obj) {
                String log = "<" + tag.toString() + "> --> " + "OK; " + (System.currentTimeMillis() - startTime) + "s\n";
                log += String.valueOf(obj);
                logout(log);
            }

            @Override
            public void onDeliverError(Exception e) {
                String log = "<" + tag.toString() + "> --> " + "Error; " + (System.currentTimeMillis() - startTime) + "s\n";
                log += e.getMessage();
                logout(log);
            }
        });
        httpClientAgent.enqueue(request);
    }

    @TestMethod(title = "图片下载")
    public void bitmapRequest() {
        final long startTime = System.currentTimeMillis();

        String url = "http://f.hiphotos.baidu.com/image/pic/item/b21c8701a18b87d662ab501d050828381e30fdc3.jpg";
        final String tag = "bitmapRequest";
        Request request = new Request.Builder().url(url).tag(tag).keepSingle(true).create();
        request.setResponseParser(new BitmapParser(new File(Environment.getExternalStorageDirectory(), "bitmap.png"), Bitmap.CompressFormat.PNG),
                new HttpClientAgent.OnRequestAdapter<Bitmap>() {
                    @Override
                    public void onDeliverError(Exception e) {
                        String log = "<" + tag.toString() + "> --> " + "Error; " + (System.currentTimeMillis() - startTime) + "s\n";
                        log += e.getMessage();
                        logout(log);
                    }

                    @Override
                    public void onDeliverSuccess(Bitmap obj) {
                        String log = "<" + tag.toString() + "> --> " + "OK; " + (System.currentTimeMillis() - startTime) + "s\n";
                        log += String.valueOf(obj);
                        logout(log);
                    }
                });
        httpClientAgent.enqueue(request);
    }

    @TestMethod(title = "字符串文件")
    public void stringFileRequest() {
        final long startTime = System.currentTimeMillis();

        String url = "http://www.baidu.com";
        final String tag = "stringFileRequest";
        Request request = new Request.Builder().url(url).tag(tag).keepSingle(true).create();
        request.setResponseParser(new FileParser(new File(Environment.getExternalStorageDirectory(), "web.html")),
                new HttpClientAgent.OnRequestAdapter<File>() {
                    @Override
                    public void onDeliverError(Exception e) {
                        String log = "<" + tag + "> --> " + "Error; " + (System.currentTimeMillis() - startTime) + "s\n";
                        log += e.getMessage();
                        logout(log);
                    }

                    @Override
                    public void onDeliverSuccess(File obj) {
                        String log = "<" + tag + "> --> " + "OK; " + (System.currentTimeMillis() - startTime) + "s\n";
                        log += String.valueOf(obj);
                        logout(log);
                    }
                });
        httpClientAgent.enqueue(request);
    }

    @TestMethod(title = "大文件下载")
    public void bigFileRequest() {
        final long startTime = System.currentTimeMillis();

        String url = "https://qd.myapp.com/myapp/qqteam/AndroidQQ/mobileqq_android.apk";
        final String tag = "bigFileRequest";
        DataParserAdapter parser = new FileParser(new File(Environment.getExternalStorageDirectory(), "file.apk"));
        parser.setOnParseListener(new DataParserAdapter.OnParseListener() {
            @Override
            public void onProgress(long progress, long total) {
                logout("<" + tag + "> --> " + "onProgress; progress:" + BaseUtils.longSizeToStr(progress) + ", total:" + total);
            }

            @Override
            public void onComplete() {
                logout("<" + tag + "> --> " + "onComplete;");
            }
        });

        Request request = new Request.Builder().url(url).tag(tag).keepSingle(true).create();
        request.setResponseParser(parser);
        request.setOnRequestListener(new HttpClientAgent.OnRequestAdapter<File>() {
            @Override
            public void onDeliverError(Exception e) {
                String log = "<" + tag + "> --> " + "Error; " + (System.currentTimeMillis() - startTime) + "s\n";
                log += e.getMessage();
                logout(log);
            }

            @Override
            public void onDeliverSuccess(File obj) {
                String log = "<" + tag + "> --> " + "OK; " + (System.currentTimeMillis() - startTime) + "s\n";
                log += String.valueOf(obj);
                logout(log);
            }
        });
        httpClientAgent.enqueue(request);
    }

    long total = 0;

    @TestMethod(title = "多线程下载文件")
    public void multiThreadDownload() {
        final String url = "https://qd.myapp.com/myapp/qqteam/AndroidQQ/mobileqq_android.apk";
        MultiThreadDownloader multiThreadDownloader = new MultiThreadDownloader()
                .setFile(new File(Environment.getExternalStorageDirectory(), "multiDownloaded.apk"))
                .setThreadSize(3)
                .addPrepareCallback(contentLength -> total = contentLength)
                .addCompleteCallback(success -> logout((success ? "success" : "error")))
                .addProgressCallback(progress -> logout("progress:" + BaseUtils.longSizeToStr(progress) + ", total:" + BaseUtils.longSizeToStr(total)))
                .open(url);
    }

    @TestMethod(title = "多线程断点下载")
    public void continueDownload() {
        if (downloader == null) {
            downloader = new Downloader()
                    .setMaxThreadSize(3)
                    .onComplete(arg0 -> logout(arg0 ? "success" : "error"))
                    .onProcess((arg0, arg1) -> logout("percent: " + (arg0 * 100 / arg1) + "% [" + BaseUtils.longSizeToStr(arg0) + "/" + BaseUtils.longSizeToStr(arg1) + "]"));
        }
        final String url = "https://qd.myapp.com/myapp/qqteam/AndroidQQ/mobileqq_android.apk";
        final File file = new File(Environment.getExternalStorageDirectory(), "QQMobile.apk");
        // request running permissions in android M
        requestPermissions(() -> {
            downloader.open(file, url);
        }, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    Downloader downloader;

    @TestMethod(title = "停止下载")
    public void stopDownload() {
        logout("call cancel");
        downloader.cancel();
    }

}