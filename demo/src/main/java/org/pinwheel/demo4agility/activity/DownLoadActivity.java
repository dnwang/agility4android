package org.pinwheel.demo4agility.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import org.pinwheel.agility.dialog.SimpleProgressDialog;
import org.pinwheel.agility.dialog.SweetDialog;
import org.pinwheel.agility.net.Request;
import org.pinwheel.agility.net.RequestManager;
import org.pinwheel.agility.net.parser.BitmapParser;
import org.pinwheel.agility.net.parser.FileParser;
import org.pinwheel.agility.util.BaseUtils;
import org.pinwheel.agility.util.BitmapHelper;
import org.pinwheel.agility.util.BitmapLoader;
import org.pinwheel.demo4agility.R;
import org.pinwheel.demo4agility.multithread.MultiThreadDownloader;

import java.io.File;

public class DownLoadActivity extends Activity {
    private static final String TAG = DownLoadActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        this.setContentView(org.pinwheel.demo4agility.R.layout.download);

        RequestManager.init(this);
        RequestManager.debug = true;

        BitmapLoader.init(this)
                .setDefaultImage(R.drawable.bg_card, R.drawable.holo_ic_alerts_and_states_error_dark)
                .setDefaultThumbnail(200, 150);

        this.init();
    }

    private void init() {
        findViewById(R.id.btn_requestmanager_download).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String img_url = "http://img.pconline.com.cn/images/upload/upc/tx/wallpaper/1301/11/c2/17316496_1357897236550.jpg";
                testDownLoadBitmap(img_url);

                String file_url = "http://gdown.baidu.com/data/wisegame/4f9b25fb0e093ac6/QQ_220.apk";
                testDownLoadFile(file_url);
            }
        });
        findViewById(R.id.btn_multiTheard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String file_url = "http://gdown.baidu.com/data/wisegame/4f9b25fb0e093ac6/QQ_220.apk";
                testMultiThreadDownLoad(file_url);
            }
        });
    }

    /**
     * 普通单线程小文件下载
     * @param url
     */
    private void testDownLoadFile(String url) {
        final SweetDialog progress = SimpleProgressDialog.create(this, "正在下载手机QQ...");
        progress.show();

        final Request api = new Request(url);
        api.setKeepSingle(true);
        RequestManager.doGet(api, new FileParser("/sdcard/temp.apk"), new RequestManager.OnRequestListener<File>() {
            @Override
            public void onError(Exception e) {
                progress.dismiss();
            }

            @Override
            public void onSuccess(File file) {
                progress.dismiss();
                if (file != null) {
                    BaseUtils.installApk(DownLoadActivity.this, false, file);
                }
            }
        });
    }

    /**
     * 图片下载
     * @param url
     */
    private void testDownLoadBitmap(String url) {
        final String bitmap_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/temp.jpg";
//        Bitmap thumbnail = BitmapHelper.getBitmapThumbnail(bitmap_path, 200, 150);
//        findViewById(R.id.btn).setBackgroundDrawable(new BitmapDrawable(thumbnail));
        BitmapLoader.getInstance().setThumbnailFromNative(
                (ImageView) findViewById(R.id.image),
                bitmap_path
        );

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                BitmapLoader.getInstance().setImageFromNative(
                        (ImageView) findViewById(R.id.image),
                        bitmap_path
                );
            }
        }, 3000);


        final SweetDialog progress = new SweetDialog(this, R.layout.dialog_layout_progress);
        progress.setSweetMessage("图片下载中...");
        progress.show();

        Request api = new Request(url);
        api.setKeepSingle(true);
        RequestManager.doGet(api, new BitmapParser(bitmap_path, Bitmap.CompressFormat.JPEG), new RequestManager.OnRequestListener<Bitmap>() {
            @Override
            public void onError(Exception e) {
                progress.dismiss();
            }

            @Override
            public void onSuccess(Bitmap obj) {
                progress.dismiss();
//                Bitmap temp = BitmapHelper.getBitmapThumbnail(obj, 30, 30);
//                Bitmap temp = BitmapHelper.setScale(obj, 300, 300);
//                Bitmap temp = BitmapHelper.setGrayscale(obj);
                Bitmap temp = BitmapHelper.setAlpha(obj, 8);

                findViewById(R.id.btn_requestmanager_download).setBackgroundDrawable(new BitmapDrawable(temp));
            }
        });
    }

    /**
     * 多线程下载
     * @param url
     */
    private void testMultiThreadDownLoad(String url) {
        final SweetDialog progress = SimpleProgressDialog.create(this, "正在下载手机QQ...");
        progress.show();
        final File target_file = new File("/sdcard/multi_qq.apk");

        MultiThreadDownloader.DownloadTask task = new MultiThreadDownloader.DownloadTask(url, target_file.getAbsolutePath());

        MultiThreadDownloader multiThreadDownloader = new MultiThreadDownloader(6);

        multiThreadDownloader.setOnDownloadListener(new MultiThreadDownloader.OnDownloadListener() {
            @Override
            public void onSuccess(File file) {
                Log.e(TAG, "onSuccess()");
                BaseUtils.installApk(DownLoadActivity.this, false, target_file);
                progress.dismiss();
            }

            @Override
            public void onProgress(long progress, long length) {
                Log.e(TAG, "onProgress() progress: " + progress + ", length:" + length);
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "onError() msg: " + e.getMessage());
                progress.dismiss();
                Toast.makeText(DownLoadActivity.this, "onError() msg: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        multiThreadDownloader.start(task);
    }

}