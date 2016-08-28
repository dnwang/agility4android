package org.pinwheel.agility.view;

import android.content.Context;
import android.util.AttributeSet;

import org.pinwheel.agility.view.controller.MessageTipController;

/**
 * Copyright (C), 2016 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 * @version 8/25/16,22:39
 * @see
 */
public class MessageTipView extends DraggableBubbleView implements MessageTipController.IMessageTreeNode {

    public MessageTipView(Context context) {
        super(context);
        init();
    }

    public MessageTipView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MessageTipView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        parseNameInfo(String.valueOf(getTag()));

        setOnSplitListener(new OnSplitListener() {
            @Override
            public void onSplit() {
                setCount(0);
            }
        });
    }

    @Override
    public void setCount(int count) {
        super.setCount(count);
        super.setVisibility(count <= 0 ? GONE : VISIBLE);
        if (null != controller) {
            controller.update(this);
        }
    }

    private MessageTipController controller;
    private String parentNodeName, nodeName;

    private void parseNameInfo(String info) {
        if (!info.contains(".")) {
            nodeName = info;
            return;
        }
        String[] names = info.split("[.]");
        if (names.length > 1) {
            parentNodeName = names[0];
            nodeName = names[1];
        } else if (names.length == 1) {
            parentNodeName = names[0];
        }
    }

    @Override
    public void onBind(MessageTipController controller) {
        this.controller = controller;
    }

    @Override
    public String getNodeName() {
        return nodeName;
    }

    @Override
    public String getParentNodeName() {
        return parentNodeName;
    }

    @Override
    public void setParentNodeName(String name) {
        name = "".equals(name) ? null : name;
        boolean isSame;
        if (null != name) {
            isSame = name.equals(parentNodeName);
        } else {
            // null == name
            isSame = (null == parentNodeName);
        }
        parentNodeName = name;
        if (!isSame && !(null == parentNodeName && null == nodeName)) {
            requestLayout();
        }
    }

    @Override
    public void setNodeName(String name) {
        boolean isSame;
        if (null != name) {
            isSame = name.equals(nodeName);
        } else {
            // null == name
            isSame = (null == nodeName);
        }
        nodeName = name;
        if (!isSame && !(null == parentNodeName && null == nodeName)) {
            requestLayout();
        }
    }

    @Override
    public void onMessageCountChanged(int size) {
        // super
        super.setCount(size);
        super.setVisibility(size <= 0 ? INVISIBLE : VISIBLE);
    }

    @Override
    public int getMessageCount() {
        return getCount();
    }

    @Override
    public String toString() {
        return nodeName;
    }
}
