package org.pinwheel.agility.view.drag;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
public interface Indicator {

    void onMove(float distance, float offset);

    void onHold();

    void reset();

    int getState();

    void setState(int state);

}
