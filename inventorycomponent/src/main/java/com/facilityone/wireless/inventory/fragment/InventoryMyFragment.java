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
import com.facilityone.wireless.inventory.presenter.InventoryMyPresenter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.List;

/**
 * Created by peter.peng on 2018/12/10.
 * 我的预定界面
 */

public class InventoryMyFragment extends BaseFragment<InventoryMyPresenter> implements OnRefreshLoadMoreListener, BaseQuickAdapter.OnItemClickListener {
    private static final int INVENTORY_RESERVE_MY_REQUEST_CODE = 1101;
    private SmartRefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;

    private Page mPage;
    private InventoryReserveListAdapter mAdapter;

    @Override
    public InventoryMyPresenter createPresenter() {
        return new InventoryMyPresenter();
    }

    @Override
    public Object setLayout() {
        return R.layout.fragment_inventory_my;
    }

    @Override
    protected int setTitleBar() {
        return R.id.ui_topbar;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    private void initView() {
        setTitle(R.string.inventory_my_title);

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

    private void onRefresh() {
        mAdapter.setEmptyView(getLoadingView(mRefreshLayout));
        if (mPage == null) {
            mPage = new Page();
        }
        mPage.reset();

        getPresenter().getReserveRecordList(mPage, InventoryConstant.RESERVE_QUERY_MY_BOOK, true);
    }


    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        if (mPage != null && mPage.haveNext()) {
            getPresenter().getReserveRecordList(mPage.nextPage(), InventoryConstant.RESERVE_QUERY_MY_BOOK, false);
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
            startForResult(ReserveRecordInfoFragment.getInstance(InventoryConstant.INVENTORY_MY,reserveRecordBean.activityId,reserveRecordBean.status),
                    INVENTORY_RESERVE_MY_REQUEST_CODE);
        }
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case INVENTORY_RESERVE_MY_REQUEST_CODE :
                onRefresh();
                break;
        }
    }

    /**
     * 联网获取预定记录列表数据成功后回调
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
     * 联网获取预订记录列表数据失败后回调
     */
    public void getReserveRecordListError() {
        mAdapter.setEmptyView(getNoDataView(mRefreshLayout));
        mRefreshLayout.finishRefresh(false);
        mRefreshLayout.finishLoadMore(false);
    }

    public static InventoryMyFragment getInstance() {
        InventoryMyFragment fragment = new InventoryMyFragment();
        return fragment;
    }
}
