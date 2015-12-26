package org.pinwheel.demo4agility.test;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.android.volley.toolbox.NetworkImageView;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import org.pinwheel.agility.cache.ImageLoader;
import org.pinwheel.agility.cache.ViewReceiver;
import org.pinwheel.agility.net.VolleyImageLoader;
import org.pinwheel.demo4agility.R;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
public final class ImageLoaderManager {

    private static ImageLoaderManager instance = null;

    public synchronized static ImageLoaderManager getInstance(Context context) {
        if (instance == null) {
            instance = new ImageLoaderManager(context);
        }
        return instance;
    }

    private ImageLoader agilityImageLoader;

    private ImageLoaderManager(Context context) {
        //Volley init
        VolleyImageLoader.init(context);

        //ImageLoader init
        DisplayImageOptions options;
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.holo_btn_av_download) //设置图片在下载期间显示的图片
                .showImageForEmptyUri(R.drawable.holo_btn_alerts_and_states_error)//设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.holo_btn_av_download)  //设置图片加载/解码过程中错误时候显示的图片
                .cacheInMemory(true)//设置下载的图片是否缓存在内存中
                .cacheOnDisc(true)//设置下载的图片是否缓存在SD卡中
                .considerExifParams(true)  //是否考虑JPEG图像EXIF参数（旋转，翻转）
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)//设置图片以如何的编码方式显示
                .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//
                .resetViewBeforeLoading(true)//设置图片在下载前是否重置，复位
                .build();//构建完成

        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(context)
                .defaultDisplayImageOptions(options)
//                    .memoryCacheExtraOptions(480, 480) // max width, max height，即保存的每个缓存文件的最大长宽
                .threadPoolSize(6)//线程池内加载的数量
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())//将保存的时候的URI名称用MD5 加密
                .imageDownloader(new BaseImageDownloader(context, 5 * 1000, 30 * 1000)) // connectTimeout (5 s), readTimeout (30 s)超时时间
                .writeDebugLogs() // Remove for release app
                .build();//开始构建
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().init(config);

        // init agility
        agilityImageLoader = newInstance(context);
    }

    public static void release() {
        // TODO

    }

    private static ImageLoader newInstance(Context context) {
        ImageLoader.ImageLoaderOptions loaderOptions = new ImageLoader.ImageLoaderOptions.Builder().create();
        ImageLoader loader = new ImageLoader(context, loaderOptions);
        ViewReceiver.Options options = new ViewReceiver.Options(context);
        options.defaultRes = org.pinwheel.agility.R.drawable.holo_btn_av_download;
        options.errorRes = org.pinwheel.agility.R.drawable.holo_btn_alerts_and_states_error;
        loader.setDefaultViewReceiverOptions(options);
        return loader;
    }

    public void setImageByAgility(ImageView view, String uri) {
        agilityImageLoader.setImage(view, uri);
    }

    public void setImageByAgility(ImageView view, String uri, ViewReceiver.Options options) {
        agilityImageLoader.setImage(view, uri, options);
    }

    public void setImageByImageLoader(ImageView view, String uri) {
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(uri, view);
    }

    public void setImageByVolley(NetworkImageView view, String uri) {
        VolleyImageLoader.getInstance().setImage(view, uri);
    }

}
