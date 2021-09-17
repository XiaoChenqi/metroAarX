package com.facilityone.wireless.inventory.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.facilityone.wireless.a.arch.ec.module.Page;
import com.facilityone.wireless.a.arch.mvp.BaseFragment;
import com.facilityone.wireless.inventory.R;
import com.facilityone.wireless.inventory.adapter.InventoryReserveListAdapter;
import com.facilityone.wireless.inventory.model.InventoryConstant;
import com.facilityone.wireless.inventory.model.ReserveService;
import com.facilityone.wireless.inventory.presenter.InventoryApprovalListPresenter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.List;

/**
 * Created by peter.peng on 2018/12/10.
 * 库存审核预订记录列表界面
 */

public class InventoryApprovalListFragment extends BaseFragment<InventoryApprovalListPresenter> implements OnRefreshLoadMoreListener, BaseQuickAdapter.OnItemClickListener {
    private static final String FROM_TYPE = "from_type";
    public static final int INVENTORY_APPROVAL_REQUEST_CODE = 1201;

    private SmartRefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;

    private Page mPage;
    private InventoryReserveListAdapter mAdapter;
    private int mType;

    @Override
    public InventoryApprovalListPresenter createPresenter() {
        return new InventoryApprovalListPresenter();
    }

    @Override
    public Object setLayout() {
        return R.layout.fragment_inventory_approval_list;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
        initView();
    }

    private void initData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            mType = bundle.getInt(FROM_TYPE, -1);
        }

        setSwipeBackEnable(false);
    }

    private void initView() {
        mRefreshLayout = findViewById(R.id.refreshLayout);
        mRecyclerView = findViewById(R.id.recyclerView);

        mRefreshLayout.setOnRefreshLoadMoreListener(this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new InventoryReserveListAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
        //首次联网获取数据
        onRefresh();
    }

    public void onRefresh() {
        mAdapter.setEmptyView(getLoadingView(mRefreshLayout));
        if (mPage == null) {
            mPage = new Page();
        }
        mPage.reset();

        int queryType = -1;
        switch (mType) {
            case InventoryConstant.INVENTORY_APPROVAL_WAIT://待审核
                queryType = InventoryConstant.RESERVE_QUERY_APPROVAL_WAIT;
                break;
            case InventoryConstant.INVENTORY_APPROVALED://已审核
                queryType = InventoryConstant.RESERVE_QUERY_APPROVALED;
                break;
        }
        getPresenter().getReserveRecordList(mPage, queryType, true);
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        if (mPage != null && mPage.haveNext()) {
            int queryType = -1;
            switch (mType) {
                case InventoryConstant.INVENTORY_APPROVAL_WAIT://待审核
                    queryType = InventoryConstant.RESERVE_QUERY_APPROVAL_WAIT;
                    break;
                case InventoryConstant.INVENTORY_APPROVALED://已审核
                    queryType = InventoryConstant.RESERVE_QUERY_APPROVALED;
                    break;
            }
            getPresenter().getReserveRecordList(mPage.nextPage(), queryType, false);
        } else {
            ToastUtils.showShort(R.string.inventory_no_more_data);
            mRefreshLayout.finishLoadMore();
        }
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        onRefresh();
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        ReserveService.ReserveRecordBean reserveRecordBean = ((InventoryReserveListAdapter) adapter).getData().get(position);
        if(reserveRecordBean != null) {
            InventoryApprovalFragment parentFragment = (InventoryApprovalFragment) getParentFragment();
            parentFragment.startForResult(ReserveRecordInfoFragment.getInstance(mType,reserveRecordBean.activityId,false),
                    INVENTORY_APPROVAL_REQUEST_CODE);
        }
    }


    /**
     * 联网获取预订记录成功后回调
     *
     * @param contents
     * @param page
     * @param refresh
     */
    public void getReserveRecordListSuccess(List<ReserveService.ReserveRecordBean> contents, Page page, boolean refresh) {
        this.mPage = page;
        if (refresh) {
            mAdapter.setNewData(contents);
            mRefreshLayout.finishRefresh();
        } else {
            if (contents != null) {
                mAdapter.addData(contents);
            }
            mRefreshLayout.finishLoadMore();
        }

        if (mAdapter.getData().size() == 0) {
            mAdapter.setEmptyView(getNoDataView(mRefreshLayout));
        }
    }

    /**
     * 联网获取预定记录失败后回调
     */
    public void getReserveRecordListError() {
        mAdapter.setEmptyView(getNoDataView(mRefreshLayout));
        mRefreshLayout.finishRefresh(false);
        mRefreshLayout.finishLoadMore(false);
    }


    public static InventoryApprovalListFragment getInstance(int type) {
        InventoryApprovalListFragment fragment = new InventoryApprovalListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(FROM_TYPE, type);
        fragment.setArguments(bundle);
        return fragment;
    }
}
