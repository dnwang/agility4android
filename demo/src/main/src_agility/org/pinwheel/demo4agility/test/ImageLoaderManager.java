package org.pinwheel.demo4agility.test;

import android.content.Context;

import org.pinwheel.agility.cache.ImageLoader;
import org.pinwheel.agility.cache.ImageLoaderOptions;
import org.pinwheel.agility.net.HttpClientAgent;
import org.pinwheel.agility.net.HttpClientAgentHelper;
import org.pinwheel.agility.net.HttpConnectionAgent;
import org.pinwheel.agility.net.OkHttpAgent;
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

    private static ImageLoader imageLoader = null;

    public synchronized static ImageLoader getImageLoader(Context context) {
        if (imageLoader == null) {
            imageLoader = newInstance(context);
        }
        return imageLoader;
    }

    public static void release() {
        if (imageLoader != null) {
            imageLoader.release();
            imageLoader = null;
        }
    }

    private static ImageLoader newInstance(Context context) {
        HttpClientAgent httpClientAgent;
        if (HttpClientAgentHelper.isImportOkHttp()) {
            httpClientAgent = new OkHttpAgent(6);
        } else {
            httpClientAgent = new HttpConnectionAgent(6);
        }
        ImageLoader imageLoader = new ImageLoader(context, httpClientAgent);
        ImageLoaderOptions options = new ImageLoaderOptions.Builder()
                .defaultRes(R.drawable.holo_btn_av_download)
                .errorRes(R.drawable.holo_btn_alerts_and_states_error)
                .create();
        imageLoader.setDefaultOptions(options);
        return imageLoader;
    }

}
