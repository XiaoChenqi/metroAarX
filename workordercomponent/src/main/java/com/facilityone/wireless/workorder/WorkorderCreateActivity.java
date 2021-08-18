package com.facilityone.wireless.workorder;

import android.os.Bundle;

import com.facilityone.wireless.a.arch.base.FMFragment;
import com.facilityone.wireless.a.arch.mvp.BaseFragmentActivity;
import com.facilityone.wireless.componentservice.common.empty.EmptyFragment;
import com.facilityone.wireless.componentservice.common.permissions.CommonConstant;
import com.facilityone.wireless.workorder.fragment.WorkorderCreateFragment;
import com.facilityone.wireless.workorder.fragment.WorkorderMenuFragment;

import androidx.annotation.Nullable;

public class WorkorderCreateActivity extends BaseFragmentActivity
        implements EmptyFragment.OnGoFragmentListener{
    //再点一次退出程序时间设置
    private static final long WAIT_TIME = 2000L;
    private long TOUCH_TIME = 0L;
    private EmptyFragment mInstance;
    public static int themeColor =0;
    public static String equipId = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSwipeBackEnable(false);
        if(0 != getIntent().getIntExtra("COLOR",0)){
            themeColor = getIntent().getIntExtra("COLOR",0);
        }
        if(getIntent().getStringExtra("EquipmentId")!=null){
            equipId = getIntent().getStringExtra("EquipmentId");
        }
        //equipId = "1819191105010000000010";
    }

    @Override
    protected int getContextViewId() {
        return R.id.workorder_main_id;//todo  xcq
    }

    @Override
    protected FMFragment setRootFragment() {
        mInstance = EmptyFragment.getInstance(CommonConstant.MESSAGE_WORK_ORDER);
        mInstance.setOnGoFragmentListener(this);
        return mInstance;
        //return DemandCreateFragment.getInstance();
    }

    @Override
    public Object createPresenter() {
        return null;
    }

    int CREATE_ORDER_BY_OTHER = 2001;//其他页面来创建工单
    int CREATE_ORDER_BY_PATROL_QUERY_REPAIR = 2002;//巡检查询报修
    @Override
    public void goFragment(Bundle bundle) {
        mInstance.startWithPop(WorkorderCreateFragment.getInstance(CREATE_ORDER_BY_OTHER,equipId));
        //mInstance.startWithPop(WorkorderMenuFragment.getInstance(bundle));
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
