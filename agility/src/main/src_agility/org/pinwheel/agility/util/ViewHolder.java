package org.pinwheel.agility.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.pinwheel.agility.util.callback.Action0;
import org.pinwheel.agility.util.callback.Function1;
import org.pinwheel.agility.view.drag.DragRefreshWrapper;

import java.lang.ref.SoftReference;

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

    private SparseArray<SoftReference<View>> holder;
    private View contentView;

    public ViewHolder() {
    }

    public ViewHolder(View root) {
        this();
        setContentView(root);
    }

    public ViewHolder foreach(Function1<Boolean, View> function1) {
        if (null != contentView && null != function1) {
            BaseUtils.foreachViews(contentView, function1);
        }
        return this;
    }

    public ViewHolder setOnGlobalLayoutListener(final Action0 action0) {
        if (null != contentView && null != action0) {
            contentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    contentView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    action0.call();
                }
            });
        }
        return this;
    }

    public ViewHolder postDelay(Runnable runnable, int delay) {
        if (null != contentView && null != runnable) {
            contentView.postDelayed(runnable, delay);
        }
        return this;
    }

    public ViewHolder post(Runnable runnable) {
        if (null != contentView && null != runnable) {
            contentView.post(runnable);
        }
        return this;
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
        SoftReference<View> softReference = holder.get(id);
        View view = (null == softReference ? null : softReference.get());
        if (view == null && contentView != null) {
            view = contentView.findViewById(id);
            holder.put(id, new SoftReference<>(view));
        }
        return (T) view;
    }

    public ViewHolder setTag(int id, Object obj) {
        getView(id).setTag(obj);
        return this;
    }

    public <T extends View> T getTag(int id) {
        Object tag = getView(id).getTag();
        return (null == tag ? null : (T) tag);
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

    public CheckBox getCheckBox(int id) {
        return getView(id);
    }

    public Spinner getSpinner(int id) {
        return getView(id);
    }

    public ProgressBar getProgressBar(int id) {
        return getView(id);
    }

    public SeekBar getSeekBar(int id) {
        return getView(id);
    }

    public RadioButton getRadioButton(int id) {
        return getView(id);
    }

    public ToggleButton getToggleButton(int id) {
        return getView(id);
    }

    public Switch getSwitch(int id) {
        return getView(id);
    }

    public WebView getWebView(int id) {
        return getView(id);
    }

    public ScrollView getScrollView(int id) {
        return getView(id);
    }

    public ImageButton getImageButton(int id) {
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

    public DragRefreshWrapper getDragRefreshWrapper(int id) {
        return getView(id);
    }

    public String getStringByTag(int id) {
        Object tag = getTag(id);
        return (null == tag ? "" : (String) tag);
    }

    public String getStringByText(int id) {
        return BaseUtils.getStringByText(getView(id));
    }

    public ViewHolder setCompoundDrawables(int id, int left, int top, int right, int bottom) {
        View v = getView(id);
        if (v instanceof TextView) {
            Context ctx = v.getContext();
            Drawable leftImg = BaseUtils.getCompoundDrawables(ctx, left);
            Drawable topImg = BaseUtils.getCompoundDrawables(ctx, top);
            Drawable rightImg = BaseUtils.getCompoundDrawables(ctx, right);
            Drawable bottomImg = BaseUtils.getCompoundDrawables(ctx, bottom);
            ((TextView) v).setCompoundDrawables(leftImg, topImg, rightImg, bottomImg);
        }
        return this;
    }

    public ViewHolder setOnClickListener(int id, View.OnClickListener listener) {
        getView(id).setOnClickListener(listener);
        return this;
    }

    public ViewHolder setOnLongClickListener(int id, View.OnLongClickListener listener) {
        getView(id).setOnLongClickListener(listener);
        return this;
    }

    public ViewHolder setOnTouchListener(int id, View.OnTouchListener listener) {
        getView(id).setOnTouchListener(listener);
        return this;
    }

    public ViewHolder setOnScrollListener(int id, AbsListView.OnScrollListener listener) {
        ((AbsListView) getView(id)).setOnScrollListener(listener);
        return this;
    }

    public ViewHolder setOnItemSelectedListener(int id, AbsListView.OnItemSelectedListener listener) {
        ((AbsListView) getView(id)).setOnItemSelectedListener(listener);
        return this;
    }

    public ViewHolder setOnItemClickListener(int id, AbsListView.OnItemClickListener listener) {
        ((AbsListView) getView(id)).setOnItemClickListener(listener);
        return this;
    }

    public ViewHolder setOnItemLongClickListener(int id, AbsListView.OnItemLongClickListener listener) {
        ((AbsListView) getView(id)).setOnItemLongClickListener(listener);
        return this;
    }

    public ViewHolder setVisibility(int id, int visibility) {
        getView(id).setVisibility(visibility);
        return this;
    }

    public ViewHolder setText(int id, int txt) {
        getTextView(id).setText(txt);
        return this;
    }

    public ViewHolder setText(int id, CharSequence txt) {
        getTextView(id).setText(txt);
        return this;
    }

    public ViewHolder setTextColor(int id, int color) {
        getTextView(id).setTextColor(color);
        return this;
    }

    public ViewHolder setHint(int id, int txt) {
        getTextView(id).setHint(txt);
        return this;
    }

    public ViewHolder setHint(int id, CharSequence txt) {
        getTextView(id).setHint(txt);
        return this;
    }

    public ViewHolder setImageResource(int id, int resId) {
        getImageView(id).setImageResource(resId);
        return this;
    }

    public ViewHolder setEnabled(int id, boolean isEnable) {
        getView(id).setEnabled(isEnable);
        return this;
    }

    public ViewHolder setChecked(int id, boolean isChecked) {
        ((CompoundButton) getView(id)).setChecked(isChecked);
        return this;
    }

    public ViewHolder setSelected(int id, boolean selected) {
        getView(id).setSelected(selected);
        return this;
    }

    public ViewHolder setClickable(int id, boolean selected) {
        getView(id).setClickable(selected);
        return this;
    }

}
