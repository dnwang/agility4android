package org.pinwheel.agility.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;
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

import org.pinwheel.agility.adapter.SimpleArrayAdapter;
import org.pinwheel.agility.adapter.SimplePagerAdapter;
import org.pinwheel.agility.util.callback.Action0;
import org.pinwheel.agility.util.callback.Function1;
import org.pinwheel.agility.view.drag.BaseDragIndicator;
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

    /******************** getter */
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

    public TextView getTextView(int id) {
        return getView(id);
    }

    public TextView getEditText(int id) {
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

    public ViewPager getViewPager(int id) {
        return getView(id);
    }

    public DragRefreshWrapper getDragRefreshWrapper(int id) {
        return getView(id);
    }

    /******************* function */

    public String getStringByTag(int id) {
        Object tag = getTag(id);
        return (null == tag ? "" : (String) tag);
    }

    public String getStringByText(int id) {
        return BaseUtils.getStringByText(getView(id));
    }

    public Adapter getAdapter(int id) {
        View view = getView(id);
        if (view instanceof AdapterView) {
            return ((AdapterView) view).getAdapter();
        }
        return null;
    }

    public <T> SimpleArrayAdapter<T> getSimpleArrayAdapter(int id) {
        Adapter adapter = getAdapter(id);
        if (null != adapter) {
            return (SimpleArrayAdapter<T>) adapter;
        }
        return null;
    }

    public SimplePagerAdapter getSimplePagerAdapter(int id) {
        ViewPager viewPager = getViewPager(id);
        if (null != viewPager) {
            PagerAdapter adapter = viewPager.getAdapter();
            if (null != adapter) {
                return (SimplePagerAdapter) adapter;
            }
        }
        return null;
    }

    public <T> T getTag(int id) {
        Object tag = getView(id).getTag();
        return null == tag ? null : (T) tag;
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

    public ViewHolder postDelayed(Runnable runnable, long delay) {
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

    public Context getContext() {
        return (null != contentView) ? contentView.getContext() : null;
    }

    public Setter select(int id) {
        return new Setter(getView(id));
    }

    /**
     * view properties setter
     */
    public static final class Setter {

        private View target;

        public Setter(View target) {
            if (null == target) {
                throw new NullPointerException("target view is null");
            }
            this.target = target;
        }

        public View getTarget() {
            return target;
        }

        public Setter setCompoundDrawables(int left, int top, int right, int bottom) {
            if (target instanceof TextView) {
                Context ctx = target.getContext();
                Drawable leftImg = BaseUtils.getCompoundDrawables(ctx, left);
                Drawable topImg = BaseUtils.getCompoundDrawables(ctx, top);
                Drawable rightImg = BaseUtils.getCompoundDrawables(ctx, right);
                Drawable bottomImg = BaseUtils.getCompoundDrawables(ctx, bottom);
                ((TextView) target).setCompoundDrawables(leftImg, topImg, rightImg, bottomImg);
            }
            return this;
        }

        public Setter setTag(Object obj) {
            target.setTag(obj);
            return this;
        }

        public Setter setId(int id) {
            target.setId(id);
            return this;
        }

        public Setter setBackgroundResource(int img) {
            target.setBackgroundResource(img);
            return this;
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        public Setter setBackground(Drawable drawable) {
            target.setBackground(drawable);
            return this;
        }

        public Setter setBackgroundColor(int color) {
            target.setBackgroundColor(color);
            return this;
        }

        public Setter setFocusable(boolean focusable) {
            target.setFocusable(focusable);
            return this;
        }

        public Setter setVisibility(int visibility) {
            target.setVisibility(visibility);
            return this;
        }

        public Setter setText(int txt) {
            if (target instanceof TextView) {
                ((TextView) target).setText(txt);
            }
            return this;
        }

        public Setter setText(CharSequence txt) {
            if (target instanceof TextView) {
                ((TextView) target).setText(txt);
            }
            return this;
        }

        public Setter setTextColor(int color) {
            if (target instanceof TextView) {
                ((TextView) target).setTextColor(color);
            }
            return this;
        }

        public Setter setHint(int txt) {
            if (target instanceof TextView) {
                ((TextView) target).setHint(txt);
            }
            return this;
        }

        public Setter setHint(CharSequence txt) {
            if (target instanceof TextView) {
                ((TextView) target).setHint(txt);
            }
            return this;
        }

        public Setter setImageResource(int img) {
            if (target instanceof ImageView) {
                ((ImageView) target).setImageResource(img);
            }
            return this;
        }

        public Setter setImageDrawable(Drawable drawable) {
            if (target instanceof ImageView) {
                ((ImageView) target).setImageDrawable(drawable);
            }
            return this;
        }

        public Setter setEnabled(boolean isEnable) {
            target.setEnabled(isEnable);
            return this;
        }

        public Setter setChecked(boolean isChecked) {
            if (target instanceof CompoundButton) {
                ((CompoundButton) target).setChecked(isChecked);
            }
            return this;
        }

        public Setter setSelected(boolean selected) {
            target.setSelected(selected);
            return this;
        }

        public Setter setClickable(boolean selected) {
            target.setClickable(selected);
            return this;
        }

        public Setter setInputType(int type) {
            if (target instanceof TextView) {
                ((TextView) target).setInputType(type);
            }
            return this;
        }

        public Setter setNumColumns(int numColumns) {
            if (target instanceof GridView) {
                ((GridView) target).setNumColumns(numColumns);
            }
            return this;
        }

        public Setter setOnClickListener(View.OnClickListener listener) {
            target.setOnClickListener(listener);
            return this;
        }

        public Setter setOnLongClickListener(View.OnLongClickListener listener) {
            target.setOnLongClickListener(listener);
            return this;
        }

        public Setter setOnTouchListener(View.OnTouchListener listener) {
            target.setOnTouchListener(listener);
            return this;
        }

        public Setter setOnScrollListener(AbsListView.OnScrollListener listener) {
            if (target instanceof AbsListView) {
                ((AbsListView) target).setOnScrollListener(listener);
            }
            return this;
        }

        public Setter setOnItemSelectedListener(AbsListView.OnItemSelectedListener listener) {
            if (target instanceof AbsListView) {
                ((AbsListView) target).setOnItemSelectedListener(listener);
            }
            return this;
        }

        public Setter setOnItemClickListener(AbsListView.OnItemClickListener listener) {
            if (target instanceof AbsListView) {
                ((AbsListView) target).setOnItemClickListener(listener);
            }
            return this;
        }

        public Setter setOnItemLongClickListener(AbsListView.OnItemLongClickListener listener) {
            if (target instanceof AbsListView) {
                ((AbsListView) target).setOnItemLongClickListener(listener);
            }
            return this;
        }

        public Setter setAdapter(Adapter adapter) {
            if (target instanceof AdapterView) {
                ((AdapterView) target).setAdapter(adapter);
            }
            return this;
        }

        public Setter setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
            if (target instanceof ViewPager) {
                ((ViewPager) target).setOnPageChangeListener(listener);
            }
            return this;
        }

        public Setter setOnRefreshListener(DragRefreshWrapper.OnRefreshListener listener) {
            if (target instanceof DragRefreshWrapper) {
                ((DragRefreshWrapper) target).setOnRefreshListener(listener);
            }
            return this;
        }

        public Setter setHeaderIndicator(BaseDragIndicator indicator) {
            if (target instanceof DragRefreshWrapper) {
                ((DragRefreshWrapper) target).setHeaderIndicator(indicator);
            }
            return this;
        }

        public Setter setFooterIndicator(BaseDragIndicator indicator) {
            if (target instanceof DragRefreshWrapper) {
                ((DragRefreshWrapper) target).setFooterIndicator(indicator);
            }
            return this;
        }

        public Setter setHeaderVisibility(boolean isVisible) {
            if (target instanceof DragRefreshWrapper) {
                ((DragRefreshWrapper) target).setHeaderVisibility(isVisible);
            }
            return this;
        }

        public Setter setFooterVisibility(boolean isVisible) {
            if (target instanceof DragRefreshWrapper) {
                ((DragRefreshWrapper) target).setFooterVisibility(isVisible);
            }
            return this;
        }

    }

}
