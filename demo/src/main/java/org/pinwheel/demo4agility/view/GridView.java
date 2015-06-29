package org.pinwheel.demo4agility.view;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

public class GridView extends ViewGroup {

	WindowManager wm ;
	int screenWidth ,screenHeigth;
	int column = 4 ,row = 5 ;// 
	int maxSize = column * row ; 
	int width, height ;
	
	View[][] items = new View[column][row] ;
	
	boolean isEditMode = false ;
//	OnGridPageListener gridPageListener ;
//	GestureDetector gestureDetector;
	
	public GridView(Context context) {
		super(context);
		setBackgroundColor(Color.GRAY) ;
		wm = (WindowManager) context.getSystemService("window");
		//screen
//		screenWidth = wm.getDefaultDisplay().getWidth() ;
//		screenHeigth = wm.getDefaultDisplay().getHeight() ;
	}
	
	public void addView(View child, int c, int r) {
		if(getChildCount() > maxSize || c > column || r > row)
			return ;
		items[c][r] = child ;
		super.addView(child);
	}
	
	public void removeView(int c, int r){
		if(getChildCount() < 0 || c < 0 || r < 0)
			return ;
		View item = items[c][r] ;
		items[c][r] = null ;
		super.removeView(item) ;
	}
	
	public void replaceView(View newChild, int toC, int toR){
		if(toC > column || toR > row)
			return ;
		View oloItem = items[toC][toR] ;
		items[toC][toR] = newChild ;
		if(oloItem != null)
			super.removeView(oloItem) ;
		if(newChild != null)
			super.addView(newChild) ;
	}
	
//	public void setOnGridPageListener(OnGridPageListener l){
//		gridPageListener = l ;
//	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if(!changed)
			return ;
		for (int x = 0; x < column; x++) {
			for (int y = 0; y < row; y++) {
				View item = items[x][y] ;
				if(item == null)
					continue ;
				int ll = x*width ;
				int tt = y*height ;
				item.layout(ll, tt, ll+width, tt+height) ;
			}
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = MeasureSpec.getSize(widthMeasureSpec) ;
		int height = MeasureSpec.getSize(heightMeasureSpec) ;
		setMeasuredDimension(width, height) ;
		this.width = width / column ;
		this.height = height / row ;
		int size = getChildCount() ;
		for (int i = 0; i < size; i++) {
			getChildAt(i).measure(this.width, this.height) ;
		}
	}
	
	public View getTouchItem(MotionEvent event){
		int x = (int) event.getX() ;
		int y = (int) event.getY() ;
		x = x / width ;
		y = y / height ;
		Log.e("GridView", "getTouchItem()--->["+x+","+y+"]") ;
		return items[x][y] ;
	}
	
//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//		Log.e("onTouchEvent", event.getAction()+"") ;
//		
//		return super.onTouchEvent(event);
//	}
	
//	@Override
//	public boolean onInterceptTouchEvent(MotionEvent event) {
//		return gestureDetector.onTouchEvent(event) ;
//	}
	
	public void enterEditMode(){
		isEditMode = true ;
		//TODO ...
//		if(gridPageListener != null)
//			gridPageListener.OnEnterEditMode(this) ;
	}
	
	public void exitEditMode(){
		isEditMode = false ;
		//TODO ...
//		if(gridPageListener != null)
//			gridPageListener.OnExitEditMode(this) ;
	}
	
	public boolean isEditMode(){
		return isEditMode ;
	}
	
	public void refresh(){
		
	}
	
	/**
	 * 
	 * @author dnwang
	 */
//	public interface OnGridPageListener {
//		void OnItemClick(GridView page, View v, int c, int r) ;
//		void OnItemClickInEdit(GridView page, View v, int c, int r) ;
//		void OnItemLongClick(GridView page, View item, int c, int r) ;
//		void OnItemLongClickInEdit(GridView page, View v, int c, int r) ;
////		void OnItemExchanged(GridPageView page, View from, int fromC, int fromR, View to, int toC, int toR) ;
//		void OnEnterEditMode(GridView page) ;
//		void OnExitEditMode(GridView page) ;
//	}
	
	/**
	 * 
	 * @author dnwang
	 */
//	class GestureListener extends GestureDetector.SimpleOnGestureListener {
//		@Override
//		public boolean onDown(MotionEvent e) {
//			
//			int x = (int) e.getX() ;
//			int y = (int) e.getY() ;
//			x = x / width ;
//			y = y / height ;
//			Log.e("onDown", "-----------------------["+e.getEventTime()+"]") ;
//			return false;
//		}
//
//		@Override
//		public void onShowPress(MotionEvent e) {
//			super.onShowPress(e) ;
//		}
//
//		@Override
//		public boolean onSingleTapUp(MotionEvent e) {
//			int x = (int) e.getX() ;
//			int y = (int) e.getY() ;
//			x = x / width ;
//			y = y / height ;
//			Log.e("onSingleTapUp", "-----------------------["+x+","+y+"]") ;
//			View item = items[x][y] ;
//			if (isEditMode) {
//				if(gridPageListener != null)
//					gridPageListener.OnItemClickInEdit(GridView.this, item, x, y) ;
//			} else {
//				if(gridPageListener != null)
//					gridPageListener.OnItemClick(GridView.this, item, x, y);
//			}
//			return true ;
//		}
//
//		@Override
//		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//			return false;
//		}
//
//		@Override
//		public void onLongPress(MotionEvent e) {
//			int x = (int) e.getX() ;
//			int y = (int) e.getY() ;
//			x = x / width ;
//			y = y / height ;
//			View item = items[x][y] ;
//			if(item == null)
//				return ;
//			Log.e("onLongPress", "-----------------------["+x+","+y+"]") ;
//			if (isEditMode) {
//				if(gridPageListener != null)
//					gridPageListener.OnItemLongClickInEdit(GridView.this, item, x, y) ;
//			} else {
//				if(gridPageListener != null)
//					gridPageListener.OnItemLongClick(GridView.this, item, x, y) ;
//			}
//		}
//
//		@Override
//		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//			return false;
//		}
//
//		@Override
//		public boolean onDoubleTap(MotionEvent e) {
//			return false;
//		}
//	}
	
}
