package com.facilityone.wireless.inventory.runalone;

import com.blankj.utilcode.util.LogUtils;
import com.facilityone.wireless.a.arch.BaseApplication;
import com.facilityone.wireless.a.arch.Facility;
import com.facilityone.wireless.inventory.BuildConfig;
import com.luojilab.component.componentlib.router.ui.UIRouter;

/**
 * Created by peter.peng on 2018/11/23.
 */

public class InventoryApplication extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.getConfig().setLogSwitch(BuildConfig.DEBUG);
        UIRouter.getInstance().registerUI("inventory");
        Facility.init(this);
    }
}
