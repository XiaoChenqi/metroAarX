package com.facilityone.wireless.maintenance.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.facilityone.wireless.a.arch.ec.adapter.FunctionAdapter;
import com.facilityone.wireless.a.arch.ec.collect.CollectUtils;
import com.facilityone.wireless.a.arch.ec.module.FunctionService;
import com.facilityone.wireless.a.arch.ec.module.IService;
import com.facilityone.wireless.a.arch.mvp.BaseFragment;
import com.facilityone.wireless.a.arch.xcq.bean.BaseResponse;
import com.facilityone.wireless.maintenance.R;
import com.facilityone.wireless.maintenance.model.MaintenanceConstant;
import com.facilityone.wireless.maintenance.presenter.MaintenanceMenuPresenter;

import java.util.ArrayList;
import java.util.List;

import me.yokeyword.fragmentation.ISupportFragment;

/**
 * @Auther: karelie
 * @Date: 2021/8/17
 * @Infor: 计划性维护菜单界面
 */
public class MaintenanceMenuFragment extends BaseFragment<MaintenanceMenuPresenter> implements BaseQuickAdapter.OnItemClickListener {

    private RecyclerView mRecyclerView;
    private FunctionAdapter mFunctionAdapter;
    private List<FunctionService.FunctionBean> mFunctionBeanList;

    @Override
    public MaintenanceMenuPresenter createPresenter() {
        return new MaintenanceMenuPresenter();
    }

    @Override
    public Object setLayout() {
        return R.layout.fragment_maintenance_menu;
    }

    @Override
    protected int setTitleBar() {
        return R.id.ui_topbar;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
        initView();
    }

    private void initData() {
        mFunctionBeanList = new ArrayList<>();
        Bundle arguments = getArguments();
        if (arguments != null) {
            ArrayList<FunctionService.FunctionBean> bean = (ArrayList<FunctionService.FunctionBean>) arguments.getSerializable(IService.FRAGMENT_CHILD_KEY);
            if (bean != null) {
                mFunctionBeanList.addAll(bean);
            } else {
                ToastUtils.showShort("暂无权限");
            }

            boolean runAlone = arguments.getBoolean(IService.COMPONENT_RUNALONE, false);
            if (runAlone) {
                setSwipeBackEnable(false);
            }
        }
    }

    private void initView() {
//        setTitle(R.string.workorder_name_title);
        setTitle("维护管理");
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), FunctionService.COUNT));
//        mRecyclerView.addItemDecoration(new GridItemDecoration(getResources().getColor(R.color.grey_d6)));

        mFunctionAdapter = new FunctionAdapter(mFunctionBeanList);
        mRecyclerView.setAdapter(mFunctionAdapter);
        mFunctionAdapter.setOnItemClickListener(this);
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        //获取角标
        getUndoNumber();
    }

    private void getUndoNumber() {
        getPresenter().getUndoNumber(FunctionService.UNDO_TYPE_MAINTENANCE);
    }

    public List<FunctionService.FunctionBean> getFunctionBeanList() {
        if (mFunctionBeanList == null) {
            return new ArrayList<>();
        }
        return mFunctionBeanList;
    }

    public void updateFunction(List<FunctionService.FunctionBean> functionBeanList) {
        mFunctionAdapter.replaceData(functionBeanList);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        FunctionService.FunctionBean functionBean = mFunctionBeanList.get(position);
        ISupportFragment baseFragment = null;
        switch (functionBean.index) {
            case MaintenanceConstant.MAINTENANCE_ONE: //维护日历
                baseFragment = MaintenanceFragment.getInstance();
                break;
            case MaintenanceConstant.MAINTENANCE_TWO: //待处理维护工单
                baseFragment = MaintenanceListFragment.getInstance(MaintenanceConstant.ONE);
                break;
            case MaintenanceConstant.MAINTENANCE_THREE: //待派工维护工单
                baseFragment = MaintenanceListFragment.getInstance(MaintenanceConstant.TWO);
                break;
            case MaintenanceConstant.MAINTENANCE_FOUR: //待审批维护工单
                baseFragment = MaintenanceListFragment.getInstance(MaintenanceConstant.THREE);
                break;
            case MaintenanceConstant.MAINTENANCE_FIVE: //异常维护工单
                baseFragment = MaintenanceListFragment.getInstance(MaintenanceConstant.FOUR);
                break;
            case MaintenanceConstant.MAINTENANCE_SIX: //待存档维护工单
                baseFragment = MaintenanceListFragment.getInstance(MaintenanceConstant.FIVE);
                break;
            case MaintenanceConstant.MAINTENANCE_SEVEN: //维护工单查询
                baseFragment = MaintenanceQueryFragment.getInstance(false);
                break;
            case MaintenanceConstant.MAINTENANCE_EIGHT: //待抽检维护工单
                baseFragment = MaintenanceListFragment.getInstance(MaintenanceConstant.SEVEN);
                break;

        }
        if (baseFragment != null) {
            start(baseFragment);
        }
    }

    @Override
    public boolean isUseDialog() {
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();
        CollectUtils.targetPageStart(this,"ppm");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CollectUtils.targetPageEnd(this,"ppm");
    }

    public static MaintenanceMenuFragment getInstance(Bundle bundle) {
        MaintenanceMenuFragment menuFragment = new MaintenanceMenuFragment();
        menuFragment.setArguments(bundle);
        return menuFragment;
    }

    public static MaintenanceMenuFragment getInstance() {
        MaintenanceMenuFragment menuFragment = new MaintenanceMenuFragment();
        return menuFragment;
    }
}
