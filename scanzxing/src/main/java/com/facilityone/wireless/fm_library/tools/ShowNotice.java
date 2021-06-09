package com.facilityone.wireless.fm_library.tools;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

public class ShowNotice {
	
	public static void showShortNotice(Context context, String info) {
		if (context == null
				|| TextUtils.isEmpty(info)) {
			return;
		}
		
		Toast.makeText(context, info, Toast.LENGTH_SHORT).show();
	}
	
	public static void showLongNotice(Context context, String info) {
		if (context == null
				|| TextUtils.isEmpty(info)) {
			return;
		}
		
		Toast.makeText(context, info, Toast.LENGTH_LONG).show();
	}
	
	public static void showShortNotice(Context context, int resid) {
		if (context == null) {
			return;
		}
		
		Toast.makeText(context, context.getResources().getString(resid), Toast.LENGTH_SHORT).show();
	}
	
	public static void showLongNotice(Context context, int resid) {
		if (context == null) {
			return;
		}
		
		Toast.makeText(context, context.getResources().getString(resid), Toast.LENGTH_LONG).show();
	}

	public static void showShortNotice(Context context, String info, int resid1) {
		if (context == null) {
			return;
		}

		Toast.makeText(context, info + context.getResources().getString(resid1), Toast.LENGTH_LONG).show();
	}
}
