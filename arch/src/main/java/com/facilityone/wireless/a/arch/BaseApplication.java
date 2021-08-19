package com.facilityone.wireless.a.arch;

import android.app.Application;
import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.orhanobut.logger.Logger;

import androidx.multidex.MultiDex;

/**
 * Author：gary
 * Email: xuhaozv@163.com
 * description:
 * Date: 2018/3/6 上午10:03
 */
public class BaseApplication extends Application {
//    private RefWatcher refWatcher;
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        //初始化内存泄漏检测工具LeakCanary
//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            return;
//        }
//        refWatcher = LeakCanary.install(this);
//        
//    }

    private static BaseApplication instance;

    public static BaseApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initGson();
    }

    public static boolean bLog = true;
    public Gson gson;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private void initGson() {
        gson = new GsonBuilder().create();
    }
    public static void d(String s) {
        if (bLog)
            Logger.d(s);
    }

    public static void e(String s) {
        if (bLog)
            Logger.e(s);
    }
//    public static RefWatcher getRefWatcher(Context context) {
//        BaseApplication application = (BaseApplication) context.getApplicationContext();
//        return application.refWatcher;
//    }
}
