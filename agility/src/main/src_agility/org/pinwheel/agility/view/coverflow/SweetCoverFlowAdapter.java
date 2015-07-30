/*
 * Copyright 2013 David Schreiber
 *           2013 John Paul Nalog
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.pinwheel.agility.view.coverflow;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


public abstract class SweetCoverFlowAdapter extends BaseAdapter {

    // =============================================================================
    // Supertype overrides
    // =============================================================================

    @Override
    public final View getView(int position, View convertView, ViewGroup parent) {
        SweetCoverFlow coverFlow = (SweetCoverFlow) parent;

        View wrappedView = null;
        SweetCoverFlowItemWrapper coverFlowItem;

        if (convertView != null) {
            coverFlowItem = (SweetCoverFlowItemWrapper) convertView;
            wrappedView = coverFlowItem.getChildAt(0);
            coverFlowItem.removeAllViews();
        } else {
            coverFlowItem = new SweetCoverFlowItemWrapper(parent.getContext());
        }

        wrappedView = this.getCoverFlowItem(position, wrappedView, parent);

        if (wrappedView == null) {
            throw new NullPointerException("getCoverFlowItem() was expected to return a view, but null was returned.");
        }

        final boolean isReflectionEnabled = coverFlow.isReflectionEnabled();
        coverFlowItem.setReflectionEnabled(isReflectionEnabled);

        if(isReflectionEnabled) {
            coverFlowItem.setReflectionGap(coverFlow.getReflectionGap());
            coverFlowItem.setReflectionRatio(coverFlow.getReflectionRatio());
        }


        coverFlowItem.addView(wrappedView);
        // dnwang;
//        coverFlowItem.setLayoutParams(wrappedView.getLayoutParams());
        coverFlowItem.setTag(wrappedView.getTag());

        return coverFlowItem;
    }

    // =============================================================================
    // Abstract methods
    // =============================================================================

    public abstract View getCoverFlowItem(int position, View convertView, ViewGroup parent);
}
