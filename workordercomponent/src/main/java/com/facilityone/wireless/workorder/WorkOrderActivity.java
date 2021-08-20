package com.facilityone.wireless.workorder;

import android.os.Bundle;

import com.blankj.utilcode.util.ToastUtils;
import com.facilityone.wireless.a.arch.base.FMFragment;
import com.facilityone.wireless.a.arch.mvp.BaseFragmentActivity;
import com.facilityone.wireless.componentservice.common.empty.EmptyFragment;
import com.facilityone.wireless.componentservice.common.permissions.CommonConstant;
import com.facilityone.wireless.workorder.fragment.WorkorderMenuFragment;
import com.kongzue.dialogx.DialogX;
import com.luojilab.router.facade.annotation.RouteNode;

import static com.facilityone.wireless.a.arch.xcq.Constants.Constant.THEME_COLOR;

/**
 * Author：gary
 * Email: xuhaozv@163.com
 * description:工单首页
 * Date: 2018/7/3 下午4:07
 */

@RouteNode(path = "/workorderHome", desc = "工单首页")
public class WorkOrderActivity extends BaseFragmentActivity implements EmptyFragment.OnGoFragmentListener {

    //再点一次退出程序时间设置
    private static final long WAIT_TIME = 2000L;
    private long TOUCH_TIME = 0L;
    private EmptyFragment mInstance;
    public static int themeColor =0;
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
        if(0 != getIntent().getIntExtra("COLOR",0)){
            themeColor = getIntent().getIntExtra("COLOR",0);
            THEME_COLOR = themeColor;
        }
        DialogX.init(this);
    }

    @Override
    public void goFragment(Bundle bundle) {
        mInstance.startWithPop(WorkorderMenuFragment.getInstance(bundle));
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
                ToastUtils.showShort(R.string.workorder_press_exit_again);
            }
        }
    }

    @Override
    public Object createPresenter() {
        return null;
    }
}
