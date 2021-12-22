package com.example.testaarx.mine;

import android.os.Bundle;

import com.blankj.utilcode.util.ToastUtils;
import com.facilityone.wireless.a.arch.base.FMFragment;
import com.facilityone.wireless.a.arch.mvp.BaseFragmentActivity;
import com.facilityone.wireless.a.arch.utils.MetroUtils;
import com.facilityone.wireless.componentservice.common.empty.EmptyFragment;
import com.facilityone.wireless.componentservice.common.permissions.CommonConstant;
import com.kongzue.dialogx.DialogX;

public class MineSignQrCodeActivity extends BaseFragmentActivity implements EmptyFragment.OnGoFragmentListener  {

    //再点一次退出程序时间设置
    private static final long WAIT_TIME = 2000L;
    private long TOUCH_TIME = 0L;
    private EmptyFragment mInstance;
    public static int themeColor =0;
    @Override
    protected int getContextViewId() {
        return com.facilityone.wireless.workorder.R.id.workorder_main_id;
    }

    @Override
    protected FMFragment setRootFragment() {
        mInstance = EmptyFragment.getInstance(CommonConstant.MESSAGE_WORK_ORDER);
        mInstance.setOnGoFragmentListener(this);
        return mInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSwipeBackEnable(false);
        MetroUtils.getParamFromMetro(this);
        DialogX.init(this);
    }

    @Override
    public Object createPresenter() {
        return null;
    }

    @Override
    public void goFragment(Bundle bundle) {
        mInstance.startWithPop(MineSignQrcodeFragment.getInstance());
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
                ToastUtils.showShort(com.facilityone.wireless.workorder.R.string.workorder_press_exit_again);
            }
        }
    }
}
