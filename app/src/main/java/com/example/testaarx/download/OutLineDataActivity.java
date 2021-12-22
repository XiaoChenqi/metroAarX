package com.example.testaarx.download;


import android.os.Bundle;

import com.facilityone.wireless.a.arch.base.FMFragment;
import com.facilityone.wireless.a.arch.mvp.BaseFragmentActivity;
import com.facilityone.wireless.a.arch.utils.MetroUtils;
import com.facilityone.wireless.componentservice.common.empty.EmptyFragment;
import com.facilityone.wireless.componentservice.common.permissions.CommonConstant;

public class OutLineDataActivity extends BaseFragmentActivity implements EmptyFragment.OnGoFragmentListener{


    //再点一次退出程序时间设置
    private static final long WAIT_TIME = 2000L;
    private long TOUCH_TIME = 0L;
    private EmptyFragment mInstance;
    public static int themeColor =0;

    @Override
    protected int getContextViewId() {
        return com.facilityone.wireless.patrol.R.id.patrol_main_id;
    }

    @Override
    protected FMFragment setRootFragment() {
        mInstance = EmptyFragment.getInstance(CommonConstant.MESSAGE_PATROL);
        mInstance.setOnGoFragmentListener(this);
        return mInstance;
        //return PatrolMenuFragment.getInstance(new Bundle());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSwipeBackEnable(false);

        MetroUtils.getParamFromMetro(this);
    }

    @Override
    public void goFragment(Bundle bundle) {
        MineFragment temp = MineFragment.newInstance();
        mInstance.startWithPop(temp);
//        NfcFragment temp = new NfcFragment();
//        mInstance.startWithPop(temp);
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
//            if (System.currentTimeMillis() - TOUCH_TIME < WAIT_TIME) {
//                this.finish();
//            } else {
//                TOUCH_TIME = System.currentTimeMillis();
//                ToastUtils.showShort(R.string.patrol_press_exit_again);
//            }
            this.finish();
        }
    }

    @Override
    public Object createPresenter() {
        return null;
    }
}
