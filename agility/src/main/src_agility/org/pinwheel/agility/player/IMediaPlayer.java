package org.pinwheel.agility.player;

import android.graphics.Bitmap;
import android.net.Uri;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved
 *
 * @author dnwang
 */
interface IMediaPlayer {

    public final static int CUSTOM_ERROR = 0x421;
    public final static int CUSTOM_ERROR_SURFACE = 0x100;
    public final static int CUSTOM_ERROR_SET_URI = 0x101;

    public final static int CUSTOM_INFO_BUFFER_START = 0x201;
    public final static int CUSTOM_INFO_BUffER_END = 0x202;

    public void setVideoURI(Uri uri);

    public Uri getCurrentURI();

    public void release(OnReleaseListener listener, Object args);

    public void start();

    public void pause();

    public void seekTo(long msec);

    public boolean isPlaying();

    public boolean isBuffering();

    public long getDuration();

    public long getCurrentPosition();

    public int getBufferPercentage();

    public int getVideoWidth();

    public int getVideoHeight();

    public void reSize(int width, int height);

    public void setVideoLayout(int layout, float aspectRatio);

    public void setPlayerEventCallBack(IMediaPlayerCallBack callBack);

    public Bitmap getCurrentFrame();

    public static interface OnReleaseListener {
        public void onReleaseComplete(boolean is, Object args);
    }

}
