package org.pinwheel.agility.util;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Copyright (C), 2016 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 * @version 2016/10/17,14:37
 * @see
 */
public final class ViewHolder {

    private SparseArray<View> holder;
    private View contentView;

    public ViewHolder() {

    }

    public ViewHolder(View root) {
        this();
        setContentView(root);
    }

    public ViewHolder setContentView(View root) {
        if (contentView != root && null != holder) {
            holder.clear();
        }
        contentView = root;
        return this;
    }

    public View getContentView() {
        return contentView;
    }

    public Context getContext() {
        return (null != contentView) ? contentView.getContext() : null;
    }

    public <T extends View> T getView(int id) {
        if (holder == null) {
            holder = new SparseArray<>();
        }
        View view = holder.get(id);
        if (view == null && contentView != null) {
            view = contentView.findViewById(id);
            holder.put(id, view);
        }
        return (T) view;
    }

    public ViewHolder setTag(int id, Object obj) {
        getView(id).setTag(obj);
        return this;
    }

    public Object getTag(int id) {
        return getView(id).getTag();
    }

    public TextView getTextView(int id) {
        return getView(id);
    }

    public Button getButton(int id) {
        return getView(id);
    }

    public ImageView getImageView(int id) {
        return getView(id);
    }

    public ViewGroup getViewGroup(int id) {
        return getView(id);
    }

    public ListView getListView(int id) {
        return getView(id);
    }

    public GridView getGridView(int id) {
        return getView(id);
    }

}
