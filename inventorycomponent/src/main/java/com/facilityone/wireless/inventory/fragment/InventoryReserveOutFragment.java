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
import com.facilityone.wireless.inventory.presenter.InventoryReserveOutPresenter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.List;

/**
 * Created by peter.peng on 2018/12/3.
 * 预定出库界面
 */

public class InventoryReserveOutFragment extends BaseFragment<InventoryReserveOutPresenter> implements OnRefreshLoadMoreListener, BaseQuickAdapter.OnItemClickListener {
    public static final int INVENTORY_RESERVE_OUT_REQUEST_CODE = 4003;
    private SmartRefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;

    private Page mPage;

    private InventoryReserveListAdapter mAdapter;

    @Override
    public InventoryReserveOutPresenter createPresenter() {
        return new InventoryReserveOutPresenter();
    }

    @Override
    public Object setLayout() {
        return R.layout.fragment_inventory_reserve_out;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
        initView();
    }


    private void initData() {
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

        //联网请求数据
        onRefresh();
    }


    public void onRefresh() {
        mAdapter.setEmptyView(getLoadingView(mRefreshLayout));
        if(mPage == null) {
            mPage = new Page();
        }
        mPage.reset();

        getPresenter().getReserveRecordList(mPage,InventoryConstant.RESERVE_QUERY_READY_OUT,true);
    }


    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        if(mPage != null && mPage.haveNext()) {
            getPresenter().getReserveRecordList(mPage.nextPage(),InventoryConstant.RESERVE_QUERY_READY_OUT,false);
        }else {
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
        InventoryOutFragment parentFragment = (InventoryOutFragment) getParentFragment();
        ReserveService.ReserveRecordBean reserveRecordBean = ((InventoryReserveListAdapter) adapter).getData().get(position);
        if(reserveRecordBean != null) {
            parentFragment.startForResult(ReserveRecordInfoFragment.getInstance(InventoryConstant.INVENTORY_OUT,reserveRecordBean.activityId),INVENTORY_RESERVE_OUT_REQUEST_CODE);
        }
    }

    /**
     * 获取预订记录列表成功后回调
     * @param contents
     * @param page
     * @param refresh
     */
    public void getReserveRecordListSuccess(List<ReserveService.ReserveRecordBean> contents, Page page, boolean refresh) {
        this.mPage = page;
        if(refresh) {
            mAdapter.setNewData(contents);
            mRefreshLayout.finishRefresh();
        }else {
            if(contents != null) {
                mAdapter.addData(contents);
            }
            mRefreshLayout.finishLoadMore();
        }

        if(mAdapter.getData().size() == 0) {
            mAdapter.setEmptyView(getNoDataView(mRefreshLayout));
        }
    }

    /**
     * 获取预订记录列表失败后回调
     */
    public void getReserveRecordListError() {
        mAdapter.setEmptyView(getNoDataView(mRefreshLayout));
        mRefreshLayout.finishRefresh(false);
        mRefreshLayout.finishLoadMore(false);
    }



    public static InventoryReserveOutFragment getInstance() {
        InventoryReserveOutFragment fragment = new InventoryReserveOutFragment();
        return fragment;
    }
}
