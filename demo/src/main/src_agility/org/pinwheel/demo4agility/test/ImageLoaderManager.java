package org.pinwheel.demo4agility.test;

import android.content.Context;

import org.pinwheel.agility.cache.ImageLoader;
import org.pinwheel.agility.net.HttpClientAgent;
import org.pinwheel.agility.net.HttpClientAgentHelper;
import org.pinwheel.agility.net.HttpConnectionAgent;
import org.pinwheel.agility.net.OkHttpAgent;

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

    public synchronized static ImageLoader getImageLoader(Context context) {
        if (instance == null) {
            getInstance(context);
        }
        return instance.imageLoader;
    }

    private ImageLoader imageLoader;

    private ImageLoaderManager(Context context) {
        // Auto select http engine
        HttpClientAgent httpClientAgent;
        if (HttpClientAgentHelper.isImportOkHttp()) {
            httpClientAgent = new OkHttpAgent();
        } else {
            httpClientAgent = new HttpConnectionAgent();
        }
        imageLoader = new ImageLoader(context, httpClientAgent);
    }

    public void release() {
        if (imageLoader != null) {
            imageLoader.release();
            imageLoader = null;
        }
        instance = null;
    }

}
