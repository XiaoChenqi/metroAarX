package com.facilityone.wireless.demand;

import android.os.Bundle;

import com.blankj.utilcode.util.ToastUtils;
import com.didi.drouter.annotation.Router;
import com.facilityone.wireless.RouteTable;
import com.facilityone.wireless.a.arch.base.FMFragment;
import com.facilityone.wireless.a.arch.base.FMFragmentActivity;
import com.facilityone.wireless.a.arch.mvp.BaseFragmentActivity;
import com.facilityone.wireless.a.arch.utils.MetroUtils;
import com.facilityone.wireless.componentservice.common.empty.EmptyFragment;
import com.facilityone.wireless.componentservice.common.permissions.CommonConstant;
import com.facilityone.wireless.demand.fragment.DemandFragment;
import com.facilityone.wireless.demand.fragment.DemandInfoFragment;
import com.facilityone.wireless.demand.module.DemandConstant;
import com.luojilab.router.facade.annotation.RouteNode;

@RouteNode(path = "/demandHome", desc = "需求首页")
@Router(path = RouteTable.DEMAND)
public class DemandActivity extends BaseFragmentActivity implements EmptyFragment.OnGoFragmentListener {
    //再点一次退出程序时间设置
    private static final long WAIT_TIME = 2000L;
    private long TOUCH_TIME = 0L;
    private EmptyFragment mInstance;

    @Override
    protected int getContextViewId() {
        return R.id.demand_main_id;
    }

    @Override
    protected FMFragment setRootFragment() {
        mInstance = EmptyFragment.getInstance(CommonConstant.MESSAGE_DEMAND);
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
        if (isFromBk){
            String type=getIntent().getExtras().getString("type");
            switch (type){
                case RouteTable.DEMAND_DETAIL:
                    Bundle params = getIntent().getExtras();
                    Long demandId = Long.parseLong(params.getString("taskId"));
                    DemandInfoFragment instance = DemandInfoFragment.getInstance(DemandConstant.DEMAND_REQUES_QUERY, demandId, true,true);
                    mInstance.startWithPop(instance);
                    break;
                default:
                    mInstance.startWithPop(DemandFragment.getInstance(bundle));

            }

        }else {
            mInstance.startWithPop(DemandFragment.getInstance(bundle));

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
                ToastUtils.showShort("再按一次退出");
            }
        }
    }

    @Override
    public Object createPresenter() {
        return null;
    }
}
