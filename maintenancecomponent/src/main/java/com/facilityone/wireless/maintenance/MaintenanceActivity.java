package com.facilityone.wireless.maintenance;

import android.os.Bundle;

import com.blankj.utilcode.util.ToastUtils;
import com.facilityone.wireless.a.arch.base.FMFragment;
import com.facilityone.wireless.a.arch.mvp.BaseFragmentActivity;
import com.facilityone.wireless.a.arch.utils.MetroUtils;
import com.facilityone.wireless.componentservice.common.empty.EmptyFragment;
import com.facilityone.wireless.componentservice.common.permissions.CommonConstant;
import com.facilityone.wireless.maintenance.fragment.MaintenanceFragment;
import com.facilityone.wireless.maintenance.fragment.MaintenanceMenuFragment;
import com.luojilab.component.componentlib.router.ui.UIRouter;
import com.luojilab.router.facade.annotation.RouteNode;

@RouteNode(path = "/maintenanceHome", desc = "计划性维护")
public class MaintenanceActivity extends BaseFragmentActivity implements EmptyFragment.OnGoFragmentListener {

    //再点一次退出程序时间设置
    private static final long WAIT_TIME = 2000L;
    private long TOUCH_TIME = 0L;
    private EmptyFragment mInstance;

    @Override
    protected int getContextViewId() {
        return R.id.maintenance_main_id;
    }

    @Override
    protected FMFragment setRootFragment() {
        //TODO 加个登录判断，以后的联调位置
        mInstance = EmptyFragment.getInstance(CommonConstant.MESSAGE_MAINTANCE);
        mInstance.setOnGoFragmentListener(this);
        return mInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSwipeBackEnable(false);
        MetroUtils.getParamFromMetro(this);
    }

    @Override
    public void goFragment(Bundle bundle) {
        mInstance.startWithPop(MaintenanceMenuFragment.getInstance(bundle));
    }

    @Override
    protected boolean isImmersionBarEnabled() {
        return true;
    }

    @Override
    public void onBackPressedSupport() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            pop();
        } else {
            if (System.currentTimeMillis() - TOUCH_TIME < WAIT_TIME) {
                this.finish();
            } else {
                TOUCH_TIME = System.currentTimeMillis();
                ToastUtils.showShort(R.string.maintenance_press_exit_again);
            }
        }
    }

    @Override
    public Object createPresenter() {
        return null;
    }

}
