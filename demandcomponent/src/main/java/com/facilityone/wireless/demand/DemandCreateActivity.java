package com.facilityone.wireless.demand;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.blankj.utilcode.util.ToastUtils;
import com.facilityone.wireless.a.arch.base.FMFragment;
import com.facilityone.wireless.a.arch.mvp.BaseFragmentActivity;
import com.facilityone.wireless.componentservice.common.empty.EmptyFragment;
import com.facilityone.wireless.componentservice.common.permissions.CommonConstant;
import com.facilityone.wireless.demand.fragment.DemandCreateFragment;
import com.facilityone.wireless.demand.fragment.DemandFragment;

public class DemandCreateActivity extends BaseFragmentActivity
        implements EmptyFragment.OnGoFragmentListener{
    //再点一次退出程序时间设置
    private static final long WAIT_TIME = 2000L;
    private long TOUCH_TIME = 0L;
    private EmptyFragment mInstance;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSwipeBackEnable(false);
    }

    @Override
    protected int getContextViewId() {
        return R.id.demand_create_upload_id;//todo  xcq
    }

    @Override
    protected FMFragment setRootFragment() {
        mInstance = EmptyFragment.getInstance(CommonConstant.MESSAGE_DEMAND);
        mInstance.setOnGoFragmentListener(this);
        return mInstance;
        //return DemandCreateFragment.getInstance();
    }

    @Override
    public Object createPresenter() {
        return null;
    }

    @Override
    public void goFragment(Bundle bundle) {
        mInstance.startWithPop(DemandCreateFragment.getInstance());
    }

    @Override
    protected boolean isImmersionBarEnabled() {//fmfactivity中
        return true;
    }

    @Override
    public void onBackPressedSupport() {

        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            pop();
        } else {
            this.finish();
//            if (System.currentTimeMillis() - TOUCH_TIME < WAIT_TIME) {
//                this.finish();
//            } else {
//                TOUCH_TIME = System.currentTimeMillis();
//                ToastUtils.showShort("再按一次退出");
//            }
        }
    }
}
