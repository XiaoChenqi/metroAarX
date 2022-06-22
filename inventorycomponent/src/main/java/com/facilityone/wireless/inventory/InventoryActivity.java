package com.facilityone.wireless.inventory;

import android.os.Bundle;

import com.blankj.utilcode.util.ToastUtils;
import com.didi.drouter.annotation.Router;
import com.facilityone.wireless.RouteTable;
import com.facilityone.wireless.a.arch.base.FMFragment;
import com.facilityone.wireless.a.arch.mvp.BaseFragmentActivity;
import com.facilityone.wireless.a.arch.utils.MetroUtils;
import com.facilityone.wireless.componentservice.common.empty.EmptyFragment;
import com.facilityone.wireless.componentservice.common.permissions.CommonConstant;
import com.facilityone.wireless.inventory.fragment.InventoryFragment;
import com.facilityone.wireless.inventory.fragment.InventoryQueryFragment;
import com.facilityone.wireless.inventory.fragment.ReserveRecordInfoFragment;
import com.facilityone.wireless.inventory.model.InventoryConstant;
import com.luojilab.router.facade.annotation.RouteNode;

@RouteNode(path = "/inventoryHome", desc = "库存")
@Router(path = RouteTable.INVENTORY)

public class InventoryActivity extends BaseFragmentActivity implements EmptyFragment.OnGoFragmentListener {

    //再点一次退出程序时间设置
    private static final long WAIT_TIME = 2000L;
    private long TOUCH_TIME = 0L;
    private EmptyFragment mInstance;

    @Override
    protected int getContextViewId() {
        return R.id.inventory_main_id;
    }

    @Override
    protected FMFragment setRootFragment() {
        mInstance = EmptyFragment.getInstance(CommonConstant.MESSAGE_INVENTORY);
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
                case RouteTable.INVENTORY_DETAIL:
                Bundle params = getIntent().getExtras();
                long activityId = Long.parseLong(params.getString("taskId"));
                ReserveRecordInfoFragment instance=ReserveRecordInfoFragment.getInstance(InventoryConstant.INVENTORY_MY,activityId,true,true);
                mInstance.startWithPop(instance);
                    break;
                case RouteTable.INVENTORY_SEARCH:
                InventoryQueryFragment instanceQuery=InventoryQueryFragment.getInstance(true);
                mInstance.startWithPop(instanceQuery);
                    break;
                default:
                mInstance.startWithPop(InventoryFragment.getInstance(bundle));

                    break;
            }
        }else {
            mInstance.startWithPop(InventoryFragment.getInstance(bundle));

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
                ToastUtils.showShort(R.string.inventory_press_exit_again);
            }
        }
    }

    @Override
    public Object createPresenter() {
        return null;
    }
}
