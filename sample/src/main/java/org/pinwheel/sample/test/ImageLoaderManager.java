package org.pinwheel.sample.test;

import android.content.Context;
import android.widget.ImageView;

import org.pinwheel.agility.cache.image.ImageLoader;
import org.pinwheel.agility.cache.image.ViewReceiver;

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
        // init agility
        agilityImageLoader = newInstance(context);
    }

    public static void release() {
        // TODO
    }

    private static ImageLoader newInstance(Context context) {
        ImageLoader.ImageLoaderOptions loaderOptions = new ImageLoader.ImageLoaderOptions.Builder().create();
        ImageLoader loader = new ImageLoader(context, loaderOptions);
        ViewReceiver.Options options = new ViewReceiver.OptionsBuilder()
                .setAutoSize(context.getResources())
                .setDefaultRes(org.pinwheel.agility.R.drawable.holo_btn_av_download)
                .setErrorRes(org.pinwheel.agility.R.drawable.holo_btn_alerts_and_states_error)
                .create();
        loader.setDefaultOptions(options);
        return loader;
    }

    public void setImageByAgility(ImageView view, String uri) {
        agilityImageLoader.setImage(view, uri);
    }

    public void setImageByAgility(ImageView view, String uri, ViewReceiver.OptionsBuilder optionsBuilder) {
        agilityImageLoader.setImage(view, uri, optionsBuilder);
    }

}
