package org.pinwheel.agility.view.drag;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
final class IndicatorEventConverter implements Draggable.OnDragListener {

    private Indicator indicator;

    public IndicatorEventConverter(){
    }

    public IndicatorEventConverter(Indicator indicator){
        this.indicator = indicator;
    }

    public void setIndicator(Indicator indicator) {
        this.indicator = indicator;
    }

    @Override
    public void onDragStateChanged(Draggable draggable, int position, int state) {
        if (indicator == null) {
            return;
        }
        if (state == Draggable.STATE_NONE) {
            indicator.reset();
        } else if (state == Draggable.STATE_HOLD) {
            indicator.onHold();
        }
    }

    @Override
    public void onDragging(Draggable draggable, float distance, float offset) {
        if (indicator == null) {
            return;
        }
        if (draggable.getPosition() != Draggable.EDGE_NONE) {
            indicator.onMove(distance, offset);
        }
    }

}
