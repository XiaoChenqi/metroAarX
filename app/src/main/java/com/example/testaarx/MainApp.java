package com.example.testaarx;

import android.app.Application;


import com.didi.drouter.api.DRouter;
import com.facilityone.wireless.AppConfig;
import com.facilityone.wireless.ObjectBox;
import com.facilityone.wireless.a.arch.Facility;
import com.facilityone.wireless.a.arch.offline.util.DBManager;
import com.facilityone.wireless.basiclib.app.FM;
import com.facilityone.wireless.basiclib.app.FMChannel;
import com.facilityone.wireless.componentservice.app.AppService;
import com.hjq.toast.ToastUtils;
import com.luojilab.component.componentlib.router.Router;
import com.tencent.mmkv.MMKV;

import java.util.HashMap;


public class MainApp extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        //saveChannelParam();
//        Facility.init(this, "http://192.168.1.66:8080", BuildConfig.DEBUG);
//        DBManager.getInstance();
        MMKV.initialize(this);
        saveChannelParam();
        Facility.init(this, AppConfig.INSTANCE.getServerHost(), BuildConfig.DEBUG);
        DBManager.getInstance();
        ObjectBox.init(this);
        Router.registerComponent("com.facilityone.wireless.demand.applike.DemandApplike");
        Router.registerComponent("com.facilityone.wireless.workorder.applike.WorkorderApplike");
        Router.registerComponent("com.facilityone.wireless.maintenance.applike.MaintenanceApplike");
        Router.registerComponent("com.facilityone.wireless.patrol.applike.PatrolApplike");
        Router.registerComponent("com.facilityone.wireless.inventory.applike.InventoryApplike");
        Router.registerComponent("com.facilityone.wireless.boardingpatrol.applike.BoardingPatrolApplike");
        Router.registerComponent("com.facilityone.wireless.construction.applike.ConstructionApplike");
        DRouter.init(this);
//        ToastUtils.setDebugMode(false);
//        ToastUtils.init(this);
//        Facility.init(this, "http://192.168.1.89:23456/fz_iframe", BuildConfig.DEBUG);

    }


    /**
     * 不同渠道参数不同（向下传递）这里如果提供给别人在封装一下 不对外 直接写死 内部调用一下即可
     */
    private void saveChannelParam() {
        HashMap<Object, Object> fmConfigs = FM.getFMConfigs();
        fmConfigs.put(FMChannel.CHANNEL_APP_SERVER, BuildConfig.SERVER_URL);
        fmConfigs.put(FMChannel.CHANNEL_APP_KEY, BuildConfig.APP_KEY);
        fmConfigs.put(FMChannel.CHANNEL_APP_SECRET, BuildConfig.APP_SECRET);
        fmConfigs.put(FMChannel.CHANNEL_UMENG_KEY, BuildConfig.UMENG_CHANNEL_KEY);
        fmConfigs.put(FMChannel.CHANNEL_UMENG_VALUE, BuildConfig.UMENG_CHANNEL_VALUE);
        fmConfigs.put(FMChannel.CHANNEL_UPDATE_APP_KEY, BuildConfig.UPDATE_APP_KEY);
        fmConfigs.put(FMChannel.CHANNEL_UPDATE_CHANNEL, BuildConfig.UPDATE_CHANNEL);
        fmConfigs.put(FMChannel.CHANNEL_CUSTOMER_CODE, BuildConfig.CUSTOMER_CODE);
        fmConfigs.put(FMChannel.CHANNEL_QQZONE_KEY, BuildConfig.QQZONE_KEY);
        fmConfigs.put(FMChannel.CHANNEL_QQZONE_SECRET, BuildConfig.QQZONE_SECRET);
        fmConfigs.put(FMChannel.CHANNEL_DING_DING_SECRET, BuildConfig.DING_DING_SECRET);
        fmConfigs.put(FMChannel.CHANNEL_WEI_XIN_KEY, BuildConfig.WEI_XIN_KEY);
        fmConfigs.put(FMChannel.CHANNEL_WEI_XIN_SECRET, BuildConfig.WEI_XIN_SECRET);
    }
}



