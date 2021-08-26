package com.facilityone.wireless.inventory.applike;

import com.facilityone.wireless.componentservice.inventory.InventoryService;
import com.facilityone.wireless.inventory.serviceimpl.InventoryServiceImpl;
import com.luojilab.component.componentlib.applicationlike.IApplicationLike;
import com.luojilab.component.componentlib.router.Router;
import com.luojilab.component.componentlib.router.ui.UIRouter;

/**
 * Created by peter.peng on 2018/11/23.
 */

public class InventoryApplike implements IApplicationLike {

    UIRouter mUiRouter = UIRouter.getInstance();
    Router mRouter = Router.getInstance();
    @Override
    public void onCreate() {
        mUiRouter.registerUI("inventory");
        mRouter.addService(InventoryService.class.getSimpleName(),new InventoryServiceImpl());
    }

    @Override
    public void onStop() {
        mUiRouter.unregisterUI("inventory");
        mRouter.removeService(InventoryService.class.getSimpleName());
    }
}
