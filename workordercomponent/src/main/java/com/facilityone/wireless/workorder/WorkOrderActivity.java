package com.facilityone.wireless.workorder;

import static com.facilityone.wireless.a.arch.xcq.Constants.Constant.PASSWORD;
import static com.facilityone.wireless.a.arch.xcq.Constants.Constant.USERNAME;

import android.os.Bundle;

import com.blankj.utilcode.util.ToastUtils;
import com.didi.drouter.annotation.Router;
import com.facilityone.wireless.RouteTable;
import com.facilityone.wireless.a.arch.base.FMFragment;
import com.facilityone.wireless.a.arch.mvp.BaseFragmentActivity;
import com.facilityone.wireless.a.arch.utils.MetroUtils;
import com.facilityone.wireless.componentservice.common.empty.EmptyFragment;
import com.facilityone.wireless.componentservice.common.permissions.CommonConstant;
import com.facilityone.wireless.workorder.fragment.WorkorderInfoFragment;
import com.facilityone.wireless.workorder.fragment.WorkorderMenuFragment;
import com.kongzue.dialogx.DialogX;
import com.luojilab.router.facade.annotation.RouteNode;

/**
 * Author：gary
 * Email: xuhaozv@163.com
 * description:工单首页
 * Date: 2018/7/3 下午4:07
 */

@RouteNode(path = "/workorderHome", desc = "工单首页")
@Router(path = RouteTable.WORKORDER)
public class WorkOrderActivity extends BaseFragmentActivity implements EmptyFragment.OnGoFragmentListener {

    //再点一次退出程序时间设置
    private static final long WAIT_TIME = 2000L;
    private long TOUCH_TIME = 0L;
    private EmptyFragment mInstance;

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
        DialogX.init(this);
        MetroUtils.getParamFromMetro(this);
    }

    @Override
    public void goFragment(Bundle bundle) {
        boolean isFromBk = getIntent().getBooleanExtra(RouteTable.FROM_BK_MSG,false);
        if (isFromBk){
            String type=getIntent().getExtras().getString("type");
            switch (type){
                case RouteTable.WORKORDER_DETAIL:
                    Bundle params = getIntent().getExtras();
                    String taskId = params.getString("taskId");
                    String taskName = params.getString("taskName");
                    WorkorderInfoFragment fragment=WorkorderInfoFragment.getInstance(Long.valueOf(taskId));
                    Bundle bundle1=new Bundle();
                    bundle1.putBoolean("fromAct",true);
                    bundle1.putBoolean(RouteTable.FROM_BK_MSG, true);
                    bundle1.putLong("workorder_id",Long.parseLong(taskId));
                    fragment.setArguments(bundle1);
                    mInstance.startWithPop(fragment);
                    break;
                default:
                    //默认进入菜单
                    mInstance.startWithPop(WorkorderMenuFragment.getInstance(bundle));
                    break;
            }


        }else {
            mInstance.startWithPop(WorkorderMenuFragment.getInstance(bundle));
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
                ToastUtils.showShort(R.string.workorder_press_exit_again);
            }
        }
    }

    @Override
    public Object createPresenter() {
        return null;
    }
}
