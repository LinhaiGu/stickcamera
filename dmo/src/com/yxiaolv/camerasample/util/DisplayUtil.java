package com.yxiaolv.camerasample.util;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

public class DisplayUtil {
	
	/** 屏幕宽度   */
	private static int DisplayWidthPixels = 0;     
	/** 屏幕高度   */  
	private static int DisplayheightPixels = 0;
	
	/**
     * 获取屏幕参数
     * @param context
     */
    private static void getDisplayMetrics(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
		DisplayWidthPixels = dm.widthPixels;// 宽度
		DisplayheightPixels = dm.heightPixels;// 高度
	}

	/**
	 * 获取屏幕宽度 
	 * @param context
	 * @return
	 */
	public static int getDisplayWidthPixels(Context context) {
		if (context == null) {
			return -1;
		} 
		if (DisplayWidthPixels == 0) {
			getDisplayMetrics(context);
		}
		return DisplayWidthPixels;
	} 

	/**
	 * 获取屏幕高度 
	 * @param context
	 * @return
	 */
	public static int getDisplayheightPixels(Context context) {
		if (context == null) {
			return -1;
		} 
		if (DisplayheightPixels == 0) {
			getDisplayMetrics(context);
		}
		return DisplayheightPixels;
	}

	/** 
     * 将px值转换为dip或dp�?
     *  
     * @param pxValue 
     * @return
     */  
    public static int px2dip(Context context, float pxValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (pxValue / scale + 0.5f);  
    }  
  
    /** 
     * 将dip或dp值转换为px�?
     *  
     * @param dipValue 
     * @return
     */  
    public static int dip2px(Context context, float dipValue) {
        if(context == null){
            return 0;
        }
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dipValue * scale + 0.5f);  
    }  
  
    /** 
     * 将px值转换为sp�?
     *  
     * @param pxValue 
     * @return
     */  
    public static int px2sp(Context context, float pxValue) {  
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;  
        return (int) (pxValue / fontScale + 0.5f);  
    }  
  
    /** 
     * 将sp值转换为px�?
     * @param spValue 
     * @param spValue   （DisplayMetrics类中属�?scaledDensity�?     * @return 
     */  
    public static int sp2px(Context context, float spValue) {  
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;  
        return (int) (spValue * fontScale + 0.5f);  
    }  
}
