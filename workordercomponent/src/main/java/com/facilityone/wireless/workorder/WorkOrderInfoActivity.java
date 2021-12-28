package com.facilityone.wireless.workorder;

import android.os.Bundle;

import com.blankj.utilcode.util.ToastUtils;
import com.facilityone.wireless.a.arch.base.FMFragment;
import com.facilityone.wireless.a.arch.mvp.BaseFragmentActivity;
import com.facilityone.wireless.a.arch.utils.MetroUtils;
import com.facilityone.wireless.componentservice.common.empty.EmptyFragment;
import com.facilityone.wireless.componentservice.common.permissions.CommonConstant;
import com.facilityone.wireless.workorder.fragment.WorkorderCreateFragment;
import com.facilityone.wireless.workorder.fragment.WorkorderInfoFragment;

import static com.facilityone.wireless.a.arch.xcq.Constants.Constant.THEME_COLOR;

public class WorkOrderInfoActivity extends
        //AppCompatActivity
        BaseFragmentActivity implements EmptyFragment.OnGoFragmentListener {


    //再点一次退出程序时间设置
    private static final long WAIT_TIME = 2000L;
    private long TOUCH_TIME = 0L;
    private EmptyFragment mInstance;
    public static int themeColor = 0;
    public static long woId = -1L;

    @Override
    protected int getContextViewId() {
        return R.id.workorder_main_id;
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
        if (getIntent().getStringExtra("woId") != null) {
            woId = Long.parseLong(getIntent().getStringExtra("woId"));
        }

    }

    @Override
    public Object createPresenter() {
        return null;
    }

    @Override
    public void goFragment(Bundle bundle) {
        WorkorderInfoFragment fragment=WorkorderInfoFragment.getInstance(woId);
        Bundle bundle1=new Bundle();
        bundle1.putBoolean("fromAct",true);
        bundle1.putLong("workorder_id",woId);
        fragment.setArguments(bundle1);
        mInstance.startWithPop(fragment);

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
            if (System.currentTimeMillis() - TOUCH_TIME < WAIT_TIME) {
                this.finish();
            } else {
                TOUCH_TIME = System.currentTimeMillis();
                ToastUtils.showShort(R.string.workorder_press_exit_again);
            }
        }
    }
}
