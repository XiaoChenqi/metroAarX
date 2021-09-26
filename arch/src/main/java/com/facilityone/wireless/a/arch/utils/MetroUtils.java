package com.facilityone.wireless.a.arch.utils;

import android.app.Activity;
import android.text.TextUtils;

import static com.facilityone.wireless.a.arch.xcq.Constants.Constant.PASSWORD;
import static com.facilityone.wireless.a.arch.xcq.Constants.Constant.THEME_COLOR;
import static com.facilityone.wireless.a.arch.xcq.Constants.Constant.USERNAME;

public class MetroUtils {
    /**
     * 获取从博坤传递过来的参数
     */
    public static void getParamFromMetro(Activity activity) {
        if(0 != activity.getIntent().getIntExtra("COLOR",0)){
            THEME_COLOR = activity.getIntent().getIntExtra("COLOR",0);
            //themeColor = (Color.parseColor("#ff6666"));;

        }
        if(!TextUtils.isEmpty(activity.getIntent().getStringExtra("USERNAME"))){
            USERNAME = activity.getIntent().getStringExtra("USERNAME");
        }
        if(!TextUtils.isEmpty(activity.getIntent().getStringExtra("PASSWORD"))){
            PASSWORD = activity.getIntent().getStringExtra("PASSWORD");
        }
    }
}
