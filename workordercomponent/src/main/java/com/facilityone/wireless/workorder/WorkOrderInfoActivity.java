package com.facilityone.wireless.workorder;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.facilityone.wireless.a.arch.base.FMFragment;
import com.facilityone.wireless.a.arch.mvp.BaseFragmentActivity;
import com.facilityone.wireless.componentservice.common.empty.EmptyFragment;
import com.facilityone.wireless.componentservice.common.permissions.CommonConstant;
import com.facilityone.wireless.workorder.fragment.WorkorderCreateFragment;
import com.facilityone.wireless.workorder.fragment.WorkorderInfoFragment;

public class WorkOrderInfoActivity extends BaseFragmentActivity
        implements EmptyFragment.OnGoFragmentListener {


    //再点一次退出程序时间设置
    private static final long WAIT_TIME = 2000L;
    private long TOUCH_TIME = 0L;
    private EmptyFragment mInstance;
    public static int themeColor =0;
    public static long woId = -1L;
    @Override
    protected int getContextViewId() {
        return R.id.workorder_info_id;
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
        }
        if(getIntent().getStringExtra("woId")!=null){
            woId = getIntent().getLongExtra("woId",-1L);
        }
    }

    @Override
    public Object createPresenter() {
        return null;
    }

    @Override
    public void goFragment(Bundle bundle) {
        //mInstance.startWithPop(WorkorderInfoFragment.getInstance(status, code, woId));
    }
}
