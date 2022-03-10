package com.facilityone.wireless.construction.runalone;

import com.facilityone.wireless.InitApplication;
import com.facilityone.wireless.a.arch.Facility;
import com.luojilab.component.componentlib.router.Router;
import com.luojilab.component.componentlib.router.ui.UIRouter;

/**
 * Author：gary
 * Email: xuhaozv@163.com
 * description:
 * Date: 2018/10/30 2:43 PM
 */
public class ConstructionApplication extends InitApplication {
    @Override
    public void onCreate() {
        super.onCreate();
//        LogUtils.getConfig().setLogSwitch(BuildConfig.DEBUG);
        UIRouter.getInstance().registerUI("construction");
        Router.registerComponent("com.facilityone.wireless.construction.applike.ConstructionApplike");
        Facility.init(this);
        //DBManager.getInstance();
    }
}
