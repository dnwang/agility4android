package org.pinwheel.sample.activity;

import android.Manifest;
import android.graphics.Bitmap;
import android.os.Environment;

import org.pinwheel.agility.net.HttpClientAgent;
import org.pinwheel.agility.net.OkHttp2Agent;
import org.pinwheel.agility.net.Request;
import org.pinwheel.agility.net.parser.BitmapParser;
import org.pinwheel.agility.net.parser.DataParserAdapter;
import org.pinwheel.agility.net.parser.FileParser;
import org.pinwheel.agility.net.parser.GsonParser;
import org.pinwheel.agility.util.BaseUtils;
import org.pinwheel.agility.util.Downloader;
import org.pinwheel.sample.entity.WeatherEntity;

import java.io.File;


public class HttpClientAgentActivity extends AbsMethodListActivity {

    HttpClientAgent httpClientAgent;

    @Override
    protected void beforeInitView() {
        httpClientAgent = new OkHttp2Agent(6);
//        httpClientAgent = new HttpConnectionAgent(6);
//        httpClientAgent = new VolleyAgent(getApplicationContext());
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
        request.setParserAndAdapter(new GsonParser<>(WeatherEntity.class), new HttpClientAgent.RequestAdapter<WeatherEntity>() {
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
        showLogger(!isLoggerShown());
        //
        newCallStyle(url, tag);
    }

    private void newCallStyle(String url, String tag) {
        httpClientAgent.enqueue(new Request.Builder()
                .url(url)
                .tag(tag)
                .keepSingle(true)
                .addParam("citykey", "101010100")
                .create()
                .setDataParser(new GsonParser<>(WeatherEntity.class))
                .setRequestAction((is, obj, e) -> logout(obj, "new style callback --> ")));
    }

    @TestMethod(title = "图片下载")
    public void bitmapRequest() {
        final long startTime = System.currentTimeMillis();

        String url = "http://f.hiphotos.baidu.com/image/pic/item/b21c8701a18b87d662ab501d050828381e30fdc3.jpg";
        final String tag = "bitmapRequest";
        Request request = new Request.Builder().url(url).tag(tag).keepSingle(true).create();
        request.setParserAndAdapter(new BitmapParser(new File(Environment.getExternalStorageDirectory(), "bitmap.png"), Bitmap.CompressFormat.PNG),
                new HttpClientAgent.RequestAdapter<Bitmap>() {
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
        showLogger(!isLoggerShown());
    }

    @TestMethod(title = "字符串文件")
    public void stringFileRequest() {
        final long startTime = System.currentTimeMillis();

        String url = "http://www.baidu.com";
        final String tag = "stringFileRequest";
        Request request = new Request.Builder().url(url).tag(tag).keepSingle(true).create();
        request.setParserAndAdapter(new FileParser(new File(Environment.getExternalStorageDirectory(), "web.html")),
                new HttpClientAgent.RequestAdapter<File>() {
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
        showLogger(!isLoggerShown());
    }

    @TestMethod(title = "大文件下载")
    public void bigFileRequest() {
        final long startTime = System.currentTimeMillis();

        String url = "http://yf-runningpig.oss-cn-hangzhou.aliyuncs.com/runningpig_v0.0.2_2.apk";
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
        request.setDataParser(parser);
        request.setRequestAdapter(new HttpClientAgent.RequestAdapter<File>() {
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
        showLogger(!isLoggerShown());
    }

    long total = 0;

    @TestMethod(title = "多线程下载文件")
    public void multiThreadDownload() {
        // Downloader
    }

    @TestMethod(title = "多线程断点下载")
    public void continueDownload() {
        if (downloader == null) {
            downloader = new Downloader().onComplete(arg0 -> logout(arg0 ? "success" : "error"))
                    .onProcess((arg0, arg1) -> logout("percent: " + (arg0 * 100 / arg1) + "% [" + BaseUtils.longSizeToStr(arg0) + "/" + BaseUtils.longSizeToStr(arg1) + "]"));
        }
        final String url = "http://yf-runningpig.oss-cn-hangzhou.aliyuncs.com/runningpig_v0.0.2_2.apk";
        final File file = new File(Environment.getExternalStorageDirectory(), "upgrade.apk");
        // request running permissions in android M
        requestPermissions((isSuccess) -> {
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