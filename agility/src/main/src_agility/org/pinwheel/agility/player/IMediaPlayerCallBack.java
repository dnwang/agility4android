package org.pinwheel.agility.player;

import android.media.TimedText;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved
 *
 * @author dnwang
 */
interface IMediaPlayerCallBack {

    public void onPrepared(Object player);

    public boolean onInfo(Object player, int what, int extra);

    public boolean onError(Object player, int what, int extra);

    public void onBufferingUpdate(Object player, int percent);

    public void onCompletion(Object player);

    public void onSeekComplete(Object player);

    public void onTimedText(Object player, TimedText text);

    public void onProgressInSecond();

    public void onSurfaceCreated();

    public void onSurfaceChanged(int format, int width, int height);

    public void onSurfaceDestroyed();

}
