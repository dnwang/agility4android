package org.pinwheel.agility.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C), 2016 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
public abstract class SimpleRecycleAdapter<T> extends RecyclerView.Adapter<SimpleRecycleAdapter.ViewHolder> {

    private List<T> data;

    public SimpleRecycleAdapter() {
        data = new ArrayList<>();
    }

    public SimpleRecycleAdapter(List<T> list) {
        this();
        addAll(list);
    }

    public SimpleRecycleAdapter(T... list) {
        this();
        addAll(list);
    }

    public List<T> getAll() {
        return data;
    }

    public T getItem(int index) {
        if (index >= 0 && index < data.size()) {
            return data.get(index);
        }
        return null;
    }

    public SimpleRecycleAdapter<T> addItem(T obj) {
        if (obj != null) {
            data.add(obj);
        }
        return this;
    }

    public SimpleRecycleAdapter<T> addItem(T obj, int index) {
        if (obj != null && index >= 0 && index < data.size()) {
            data.add(index, obj);
        }
        return this;
    }

    public SimpleRecycleAdapter<T> addAll(List<T> list) {
        if (list != null && list.size() > 0) {
            data.addAll(list);
        }
        return this;
    }

    public SimpleRecycleAdapter<T> addAll(T... list) {
        if (list != null && list.length > 0) {
            for (T t : list) {
                if (t instanceof List) {
                    addAll((List<T>) t);
                } else {
                    data.add(t);
                }
            }
        }
        return this;
    }

    public T remove(int index) {
        if (index >= 0 && index < data.size()) {
            return data.remove(index);
        }
        return null;
    }

    public SimpleRecycleAdapter<T> removeAll() {
        data.clear();
        return this;
    }

    public SimpleRecycleAdapter<T> removeAll(List<T> list) {
        if (list != null) {
            data.removeAll(list);
        }
        return this;
    }

    public abstract int getItemLayout();

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View contentView = LayoutInflater.from(parent.getContext()).inflate(getItemLayout(), null);
        return new ViewHolder(contentView);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    /**
     * ViewHolder
     */
    public static final class ViewHolder extends RecyclerView.ViewHolder {
        private SparseArray<View> holder;

        public ViewHolder(View itemView) {
            super(itemView);
            holder = new SparseArray<>();
        }

        public <K extends View> K getView(int id) {
            View view = holder.get(id);
            if (view == null) {
                view = itemView.findViewById(id);
                holder.put(id, view);
            }
            return (K) view;
        }

        public void setOnClickListener(View.OnClickListener listener) {
            itemView.setOnClickListener(listener);
        }

        public void setOnLongClickListener(View.OnLongClickListener listener) {
            itemView.setOnLongClickListener(listener);
        }

    }

}
