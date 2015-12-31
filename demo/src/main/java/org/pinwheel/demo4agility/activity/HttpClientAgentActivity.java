package org.pinwheel.demo4agility.activity;

import android.graphics.Bitmap;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;

import org.pinwheel.agility.net.HttpClientAgent;
import org.pinwheel.agility.net.HttpConnectionAgent;
import org.pinwheel.agility.net.OkHttpAgent;
import org.pinwheel.agility.net.Request;
import org.pinwheel.agility.net.parser.BitmapParser;
import org.pinwheel.agility.net.parser.DataParserAdapter;
import org.pinwheel.agility.net.parser.FileParser;
import org.pinwheel.agility.net.parser.GsonParser;
import org.pinwheel.agility.net.parser.IDataParser;
import org.pinwheel.agility.util.BaseUtils;
import org.pinwheel.agility.view.SweetListView;
import org.pinwheel.demo4agility.entity.WeatherEntity;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;


public class HttpClientAgentActivity extends AbsTestActivity {

    HttpClientAgent httpClientAgent;

    @Override
    protected void onInitInCreate() {
        httpClientAgent = new HttpConnectionAgent(6);
        httpClientAgent = new OkHttpAgent(6);
//        httpClientAgent = new VolleyAgent(getApplicationContext());
    }

    @Override
    protected View getContentView() {
        ArrayList<String> requestMethods = new ArrayList<>();
        Method[] methods = HttpClientAgentActivity.class.getMethods();
        for (Method method : methods) {
            String methodName = method.getName();
            if (methodName.endsWith("Request")) {
                requestMethods.add(methodName);
            }
        }
        ListView list = new SweetListView(this);
        BaseAdapter adapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_list_item_1, requestMethods);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String methodName = String.valueOf(parent.getAdapter().getItem(position));
                try {
                    Method method = HttpClientAgentActivity.class.getDeclaredMethod(methodName);
                    method.setAccessible(true);
                    method.invoke(HttpClientAgentActivity.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return list;
    }

    String finalTag;

    @Override
    protected void doTest() {
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

    public void bigFileRequest() {
        final long startTime = System.currentTimeMillis();

        String url = "http://p.gdown.baidu.com/0c355e38510b236a7066785eddb99285bcbd3275d05b82c1b9908946f9936812d17598cf52da65f01978bca62553b1325a2b910649edc9fde30f366d437849deae132f4e24caeedf1660f269eeaa6570653b82067c53c2d2c94cce6a5afad89959e955386fb850ba0c8caad2b6ac77ca076fc5824259d6fafe334c52b82205f589014d58ebdb7525bc6188febcbf0be4541af18c4c08790f58a5c8d6e46537e0823a8a8e49a79f0125f54b944cbac19a707755cdf8fcea1f0fc862fbd339ce3b8bfff40f271403ee4dd4e14dc8d79310ce3a1a66707b30270c59da0a55cfe7a696ff28566e4dd788175b5b39e0e4d7243652748f66f4bbf99f80a189f78b47b140c7626c7c1af11ba660fdedf9982497";
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

}