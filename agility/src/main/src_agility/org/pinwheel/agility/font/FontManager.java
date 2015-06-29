package org.pinwheel.agility.font;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;

/**
 * 字体管理
 * @author WangDenan
 *
 */
public final class FontManager {

    private FontManager(){

    }

	/**
	 * 更换字体
	 * @param root
	 * @param context
	 */
	public static void changeFont(ViewGroup root ,Context context ,String path) {
		
		Typeface tf = Typeface.createFromAsset(context.getAssets(), path) ;
		for (int i = 0; i < root.getChildCount(); i++) {
			View view = root.getChildAt(i) ;
			if (view instanceof TextView) {
				((TextView) view).setTypeface(tf) ;
			} else if (view instanceof Button) {
				((TextView) view).setTypeface(tf) ;
			} else if (view instanceof CheckBox) {
				((TextView) view).setTypeface(tf) ;
			} else if (view instanceof RadioButton) {
				((TextView) view).setTypeface(tf) ;
			} else if (view instanceof ViewGroup) {
				changeFont((ViewGroup) view, context, path) ;
			}
		}
		
	}
	
}
