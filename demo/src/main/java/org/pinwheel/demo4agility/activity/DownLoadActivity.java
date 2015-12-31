package org.pinwheel.demo4agility.activity;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import org.pinwheel.agility.dialog.SimpleProgressDialog;
import org.pinwheel.agility.dialog.SweetDialog;
import org.pinwheel.agility.net.Request;
import org.pinwheel.agility.net.VolleyImageLoader;
import org.pinwheel.agility.net.VolleyRequestHelper;
import org.pinwheel.agility.net.parser.BitmapParser;
import org.pinwheel.agility.net.parser.FileParser;
import org.pinwheel.agility.util.BaseUtils;
import org.pinwheel.agility.util.BitmapUtils;
import org.pinwheel.demo4agility.R;

import java.io.File;

public class DownLoadActivity extends AbsTestActivity {
    private static final String TAG = DownLoadActivity.class.getSimpleName();

    @Override
    protected void onInitInCreate() {
        VolleyRequestHelper.init(this);
        VolleyRequestHelper.debug = true;

        VolleyImageLoader.init(this)
                .setDefaultImage(R.drawable.bg_card, R.drawable.holo_ic_alerts_and_states_error_dark)
                .setDefaultThumbnail(200, 150);
    }

    @Override
    protected View getContentView() {
        View contentView = LayoutInflater.from(this).inflate(R.layout.activity_download, null);
        contentView.findViewById(R.id.btn_requestmanager_download).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String img_url = "http://img.pconline.com.cn/images/upload/upc/tx/wallpaper/1301/11/c2/17316496_1357897236550.jpg";
                testDownLoadBitmap(img_url);

                String file_url = "http://gdown.baidu.com/data/wisegame/4f9b25fb0e093ac6/QQ_220.apk";
                testDownLoadFile(file_url);
            }
        });
        return contentView;
    }

    @Override
    protected void doTest() {

    }

    /**
     * 普通单线程小文件下载
     *
     * @param url
     */
    private void testDownLoadFile(String url) {
        final SweetDialog progress = SimpleProgressDialog.create(this, "正在下载手机QQ...");
        progress.show();

        final Request api = new Request.Builder().url(url).keepSingle(true).create();
        VolleyRequestHelper.doGet(api, new FileParser("/sdcard/temp.apk"), new VolleyRequestHelper.OnRequestListener<File>() {
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
     *
     * @param url
     */
    private void testDownLoadBitmap(String url) {
        final String bitmap_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/temp.jpg";
//        Bitmap thumbnail = BitmapUtils.getBitmapThumbnail(bitmap_path, 200, 150);
//        findViewById(R.id.btn).setBackgroundDrawable(new BitmapDrawable(thumbnail));
        VolleyImageLoader.getInstance().setThumbnailFromNative(
                (ImageView) findViewById(R.id.image),
                bitmap_path
        );

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                VolleyImageLoader.getInstance().setImageFromNative(
                        (ImageView) findViewById(R.id.image),
                        bitmap_path
                );
            }
        }, 3000);


        final SweetDialog progress = new SweetDialog(this, R.layout.dialog_layout_progress);
        progress.setSweetMessage("图片下载中...");
        progress.show();

        Request api = new Request.Builder().url(url).keepSingle(true).create();
        VolleyRequestHelper.doGet(api, new BitmapParser(bitmap_path, Bitmap.CompressFormat.JPEG), new VolleyRequestHelper.OnRequestListener<Bitmap>() {
            @Override
            public void onError(Exception e) {
                progress.dismiss();
            }

            @Override
            public void onSuccess(Bitmap obj) {
                progress.dismiss();
//                Bitmap temp = BitmapUtils.getBitmapThumbnail(obj, 30, 30);
//                Bitmap temp = BitmapUtils.setScale(obj, 300, 300);
//                Bitmap temp = BitmapUtils.setGrayscale(obj);
                Bitmap temp = BitmapUtils.setAlpha(obj, 8);

                findViewById(R.id.btn_requestmanager_download).setBackgroundDrawable(new BitmapDrawable(temp));
            }
        });
    }

}