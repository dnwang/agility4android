package org.pinwheel.agility.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import org.pinwheel.agility.util.ViewHolder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Copyright (C), 2016 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 * @version 2016/10/26,11:22
 * @see
 */
public abstract class MultiCacheAdapter extends SimpleArrayAdapter<Object> {

    protected abstract CacheDesc getCacheDesc(int position, Object obj);

    @Override
    public final View getView(int position, View convertView, ViewGroup parent) {
        final Object obj = getItem(position);
        final CacheDesc cacheDesc = getCacheDesc(position, obj);

        CacheBundle cacheBundle = getCacheBundle(convertView);
        if (null != cacheBundle && !cacheViewPool.contains(convertView)) {
            cacheViewPool.add(convertView);
        }

        final View cacheView = popCacheView(cacheDesc);
        cacheBundle = getCacheBundle(cacheView);
        if (null != cacheBundle) {
            cacheBundle.cacheItem.onUpdate(position, obj, cacheBundle.viewHolder);
            return cacheView;
        } else {
            ICacheItem cacheItem;
            try {
                cacheItem = cacheDesc.newInstance();
            } catch (Exception e) {
                throw new NullPointerException("Can't create cache item.");
            }
            View view = cacheItem.onCreate(parent.getContext());
            CacheBundle bundle = new CacheBundle(cacheItem, new ViewHolder(view));
            bindCacheBundle(view, bundle);
            cacheItem.onUpdate(position, obj, bundle.viewHolder);
            return view;
        }
    }

    private final Set<View> cacheViewPool = new HashSet<>();

    private void bindCacheBundle(View view, CacheBundle bundle) {
        view.setTag(bundle);
    }

    private CacheBundle getCacheBundle(View view) {
        Object tagObj = (null != view) ? view.getTag() : null;
        return (null != tagObj && tagObj instanceof CacheBundle) ? (CacheBundle) tagObj : null;
    }

    private View popCacheView(CacheDesc cacheDesc) {
        if (null == cacheDesc) {
            return null;
        }
        View cacheView;
        Iterator<View> iterator = cacheViewPool.iterator();
        while (iterator.hasNext()) {
            cacheView = iterator.next();
            CacheBundle bundle = (CacheBundle) cacheView.getTag();
            if (cacheDesc.compareTo(bundle.cacheItem)) {
                iterator.remove();
                return cacheView;
            }
        }
        return null;
    }

    private static final class CacheBundle {
        ICacheItem cacheItem;
        ViewHolder viewHolder;

        CacheBundle(ICacheItem cacheItem, ViewHolder viewHolder) {
            this.cacheItem = cacheItem;
            this.viewHolder = viewHolder;
        }
    }

    public static final class CacheDesc {

        private Class<? extends ICacheItem> type;
        private Object[] args;

        public CacheDesc(Class<? extends ICacheItem> type, Object... args) {
            this.type = type;
            this.args = args;
        }

        private ICacheItem newInstance() throws Exception {
            Constructor[] constructors = type.getDeclaredConstructors();
            for (Constructor constructor : constructors) {
                if (constructor.isAnnotationPresent(Inject.class)) {
                    constructor.setAccessible(true);
                    return (ICacheItem) constructor.newInstance(args);
                }
            }
            return type.newInstance();
        }

        private boolean compareTo(ICacheItem cacheItem) {
            return null != cacheItem && cacheItem.getClass().getName().equals(type.getName());
        }
    }

    @Target(ElementType.CONSTRUCTOR)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Inject {
    }

    public interface ICacheItem {
        View onCreate(Context context);

        void onUpdate(int position, Object obj, ViewHolder holder);
    }

}
