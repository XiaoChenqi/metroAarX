package com.facilityone.wireless.maintenance;

import android.os.Bundle;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.didi.drouter.annotation.Router;
import com.facilityone.wireless.RouteTable;
import com.facilityone.wireless.a.arch.base.FMFragment;
import com.facilityone.wireless.a.arch.mvp.BaseFragment;
import com.facilityone.wireless.a.arch.mvp.BaseFragmentActivity;
import com.facilityone.wireless.a.arch.utils.MetroUtils;
import com.facilityone.wireless.componentservice.common.empty.EmptyFragment;
import com.facilityone.wireless.componentservice.common.permissions.CommonConstant;
import com.facilityone.wireless.componentservice.workorder.WorkorderService;
import com.facilityone.wireless.maintenance.fragment.MaintenanceContentFragment;
import com.facilityone.wireless.maintenance.fragment.MaintenanceFragment;
import com.facilityone.wireless.maintenance.fragment.MaintenanceMenuFragment;
import com.facilityone.wireless.maintenance.model.MaintenanceConstant;
import com.luojilab.component.componentlib.router.ui.UIRouter;
import com.luojilab.router.facade.annotation.RouteNode;

@RouteNode(path = "/maintenanceHome", desc = "计划性维护")
@Router(path = RouteTable.PPM)
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
        boolean isFromBk = getIntent().getBooleanExtra(RouteTable.FROM_BK_MSG,false);
        LogUtils.d("是否来自博坤消息",isFromBk);

        if (isFromBk){
            String type=getIntent().getExtras().getString("type");
            switch (type){
                case RouteTable.PPM_CONTENT:
                    Bundle params = getIntent().getExtras();
                    Long taskId = Long.parseLong(params.getString("taskId"));
                    Long code = Long.parseLong(params.getString("todoId"));
                    MaintenanceContentFragment instance=MaintenanceContentFragment.getInstance(true,taskId,code,true);
                    mInstance.startWithPop(instance);
                    break;
                default:
                    //默认进入菜单
                    mInstance.startWithPop( MaintenanceMenuFragment.getInstance(bundle));
                    break;
            }
        }else {
            mInstance.startWithPop( MaintenanceMenuFragment.getInstance(bundle));
        }
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
