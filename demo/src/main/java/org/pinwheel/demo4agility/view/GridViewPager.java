package org.pinwheel.demo4agility.view;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class GridViewPager extends ViewPager {

	GestureDetector mGestureDetector;
	List<GridView> mPages ;
	PagerAdapter mAdapter ;
	
	OnPageItemListener mPageItemListener ;
	
	public GridViewPager(Context context) {
		super(context);
		mPages = new ArrayList<GridView>() ;
		mGestureDetector = new GestureDetector(context, new GestureListener()) ;
		mAdapter = new PagerAdapter() ;
		setAdapter(mAdapter) ;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mGestureDetector.onTouchEvent(event) ;
		return super.onTouchEvent(event) ;
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		mGestureDetector.onTouchEvent(event) ;
		return super.onInterceptTouchEvent(event) ;
	}
	
	public void addPage(GridView page){
		mPages.add(page) ;
		mAdapter.notifyDataSetChanged() ;
	}
	
	public void removePage(int index){
		mPages.remove(index) ;
		mAdapter.notifyDataSetChanged() ;
	}
	
	public void refresh(){
		//TODO ...
	}
	
	public void setOnGridPageListener(OnPageItemListener l){
		mPageItemListener = l ;
	}
	
	public void enterEditMode(){
		for (GridView page : mPages) {
			page.enterEditMode() ;
		}
		//TODO ...
		if(mPageItemListener != null)
			mPageItemListener.OnEnterEditMode() ;
	}
	
	public void exitEditMode(){
		for (GridView page : mPages) {
			page.exitEditMode() ;
		}
		//TODO ...
		if(mPageItemListener != null)
			mPageItemListener.OnExitEditMode() ;
	}
	
	/**
	 * GestureListener
	 * @author dnwang
	 */
	class GestureListener extends GestureDetector.SimpleOnGestureListener{
		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			Log.e("PageView", "onSingleTapUp") ;
			return false ;
		}
		
		@Override
		public void onLongPress(MotionEvent e) {
			Log.e("PageView", "onLongPress") ;
		}
	}
	
	/**
	 * PagerAdapter
	 * @author dnwang
	 */
	class PagerAdapter extends android.support.v4.view.PagerAdapter {
		@Override
		public int getCount() {
			return mPages.size() ;
		}
		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(mPages.get(position)) ;
		}
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			View v = mPages.get(position) ;
			container.addView(v) ;
			return v;
		}
	}
	
	/**
	 * 
	 * @author dnwang
	 */
	public interface OnPageItemListener {
		void OnItemClick(GridView page, View item, int c, int r) ;
		void OnItemClickInEdit(GridView page, View item, int c, int r) ;
		void OnItemLongClick(GridView page, View item, int c, int r) ;
		void OnItemLongClickInEdit(GridView page, View v, int c, int r) ;
		void OnItemExchanged(
				GridView fromPage, View fromItem, int fromC, int fromR,
				GridView toPage, View toItem, int toC, int toR) ;
		void OnEnterEditMode() ;
		void OnExitEditMode() ;
	}
	
}
