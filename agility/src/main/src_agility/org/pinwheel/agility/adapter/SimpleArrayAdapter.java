package org.pinwheel.agility.adapter;

import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 版权所有 (C), 2014 <br>
 * <br>
 * All rights reserved
 *
 * @author dnwang
 * @date 2014/8/27 15:27
 * @description
 */
public abstract class SimpleArrayAdapter<T> extends BaseAdapter {

    protected ArrayList<T> mDatas;

    protected SimpleArrayAdapter(){
        super();
        mDatas = new ArrayList<T>(0);
    }

    protected SimpleArrayAdapter(List<T> datas){
        super();
        mDatas = new ArrayList<T>(datas);
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public T getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public ArrayList<T> getDatas(){
        return mDatas;
    }

    public void addItem(T obj){
        this.mDatas.add(obj);
    }

    public void addItem(T obj, int index){
        this.mDatas.add(index, obj);
    }

    public void addAll(List<T> datas){
        this.mDatas.addAll(datas);
    }

    public void remove(int index) {
        this.mDatas.remove(index);
    }

    public void removeAll() {
        this.mDatas.clear();
    }

    public void removeAll(List<T> datas) {
        this.mDatas.removeAll(datas);
    }

}
