package org.pinwheel.demo4agility.test;

import android.content.Context;
import org.pinwheel.agility.cache.ImageLoader;
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
            httpClientAgent = new OkHttpAgent();
        } else {
            httpClientAgent = new HttpConnectionAgent();
        }
        ImageLoader imageLoader = new ImageLoader(context, httpClientAgent);
        return imageLoader;
    }

}
