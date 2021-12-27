package com.facilityone.wireless.workorder;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.blankj.utilcode.util.ActivityUtils;
import com.facilityone.wireless.a.arch.base.FMFragment;
import com.facilityone.wireless.a.arch.mvp.BaseFragmentActivity;
import com.facilityone.wireless.a.arch.utils.MetroUtils;
import com.facilityone.wireless.componentservice.common.empty.EmptyFragment;
import com.facilityone.wireless.componentservice.common.permissions.CommonConstant;
import com.facilityone.wireless.workorder.fragment.WorkorderCreateFragment;
import com.facilityone.wireless.workorder.fragment.WorkorderInfoFragment;

import androidx.annotation.Nullable;

import static com.facilityone.wireless.a.arch.xcq.Constants.Constant.THEME_COLOR;

public class WorkorderCreateActivity extends BaseFragmentActivity
        implements EmptyFragment.OnGoFragmentListener{
    //再点一次退出程序时间设置
    private static final long WAIT_TIME = 2000L;
    private long TOUCH_TIME = 0L;
    private EmptyFragment mInstance;
    public static int themeColor =0;
    public static String equipId = "";

    String TAG = "林晓旭";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSwipeBackEnable(false);
        Log.d(TAG, "onCreate: ");
        MetroUtils.getParamFromMetro(this);
        if(getIntent().getStringExtra("EquipmentId")!=null){
            equipId = getIntent().getStringExtra("EquipmentId");
        }


    }

    @Override
    protected int getContextViewId() {
        Log.d(TAG, "getContextViewId: ");
        return R.id.workorder_main_id;//todo  xcq
    }

    @Override
    protected FMFragment setRootFragment() {
        Log.d(TAG, "setRootFragment: ");
        mInstance = EmptyFragment.getInstance(CommonConstant.MESSAGE_WORK_ORDER);
        mInstance.setOnGoFragmentListener(this);

        return mInstance;
        //return DemandCreateFragment.getInstance();
    }

    @Override
    public Object createPresenter() {
        Log.d(TAG, "createPresenter: ");
        return null;
    }

    int CREATE_ORDER_BY_OTHER = 2001;//其他页面来创建工单
    int CREATE_ORDER_BY_PATROL_QUERY_REPAIR = 2002;//巡检查询报修
    @Override
    public void goFragment(Bundle bundle) {
        Log.d(TAG, "goFragment: ");
        WorkorderCreateFragment workorderCreateFragment=WorkorderCreateFragment.getInstance(CREATE_ORDER_BY_OTHER,equipId);
        Bundle bundle1=new Bundle();
        bundle1.putBoolean("fromAct",true);
        workorderCreateFragment.setArguments(bundle1);
       mInstance.startWithPop(workorderCreateFragment);
    }

    @Override
    protected boolean isImmersionBarEnabled() {//fmfactivity中
        Log.d(TAG, "isImmersionBarEnabled: ");
        return true;
    }

    @Override
    public void onBackPressedSupport() {

        Log.d(TAG, "onBackPressedSupport: ");

        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            pop();


        } else  if (getSupportFragmentManager().getBackStackEntryCount()==1){

            if (getSupportFragmentManager().getFragments().get(0) instanceof  EmptyFragment){
                finish();
            }
        }else {
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
