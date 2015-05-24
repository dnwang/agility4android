package org.pinwheel.agility.util.ex;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Handler;
import android.webkit.WebView;
import android.widget.Toast;
import org.apache.http.protocol.HTTP;

import java.io.*;

/**
 * Assets目录工具类，用于访问各种Assets目录下的文件
 */
public class AssetsUtil {
    private Context context;
    private AssetManager assets;

    public AssetsUtil(Context context) {
        this.context = context;
        assets = context.getAssets();
    }

    /**
     * 安装assets目录下的文件
     *
     * @param fileName 文件名称
     */
    public void installapk(String fileName) {
        try {
            InputStream stream = assets.open(fileName);
            if (stream == null) {
                Toast.makeText(context, "文件不存在", Toast.LENGTH_SHORT).show();
                return;
            }

            String folder = "/mnt/sdcard/sm/";
            File f = new File(folder);
            if (!f.exists()) {
                f.mkdir();
            }
            String apkPath = "/mnt/sdcard/sm/test.apk";
            File file = new File(apkPath);
            if (!file.exists())
                file.createNewFile();
            writeStreamToFile(stream, file);
            installApk(apkPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 显示本地的Html的内容
     *
     * @param htmlName
     * @param web
     */
    @SuppressLint("SetJavaScriptEnabled")
    public void showHtml(String htmlName, WebView web) {
        try {
            web.getSettings().setJavaScriptEnabled(true);
            web.loadUrl("file:///android_asset/" + htmlName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 执行JS文件中的代码
     *
     * @param web     游览器对象
     * @param handler 主线程的标志
     * @param jsName  JS文件名称
     */
    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    public void excuteJs(final WebView web, final Handler handler, final String jsName) {
        try {
            final InputStreamReader in = new InputStreamReader(assets.open(jsName));
            final StringWriter write = new StringWriter();
            char[] ch = new char[1024];
            int len = 0;
            while ((len = in.read(ch)) != -1) {
                write.write(ch, 0, len);
            }
            web.getSettings().setJavaScriptEnabled(true);
            web.addJavascriptInterface(new Object() {
                @SuppressWarnings("unused")
                public void clickOnAndroid() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                web.loadUrl("javascript:" + write.toString());
                                write.close();
                                in.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }, "demo");
            web.loadData("<html><head></head><body onload=\"javascript:window.demo.clickOnAndroid()\"></body></html>", "text/html", HTTP.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeStreamToFile(InputStream stream, File file) {
        try {
            OutputStream output = null;
            try {
                output = new FileOutputStream(file);
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
            try {
                try {
                    final byte[] buffer = new byte[1024];
                    int read;

                    while ((read = stream.read(buffer)) != -1)
                        output.write(buffer, 0, read);
                    output.flush();
                } finally {
                    output.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void installApk(String apkPath) {
        AppUtils app_util = new AppUtils(context);
        app_util.installApk(apkPath);
    }
}
