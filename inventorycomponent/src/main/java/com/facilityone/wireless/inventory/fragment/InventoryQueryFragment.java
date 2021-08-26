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
import com.facilityone.wireless.inventory.adapter.StorageListAdapter;
import com.facilityone.wireless.inventory.model.StorageService;
import com.facilityone.wireless.inventory.presenter.InventoryQueryPresenter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.List;

/**
 * Created by peter.peng on 2018/12/11.
 * 库存查询界面
 */

public class InventoryQueryFragment extends BaseFragment<InventoryQueryPresenter> implements OnRefreshLoadMoreListener, BaseQuickAdapter.OnItemClickListener {
    private SmartRefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;

    private Page mPage;
    private StorageListAdapter mAdapter;

    @Override
    public InventoryQueryPresenter createPresenter() {
        return new InventoryQueryPresenter();
    }

    @Override
    public Object setLayout() {
        return R.layout.fragment_inventory_query;
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
        setTitle(R.string.inventory_query_title);

        mRefreshLayout = findViewById(R.id.refreshLayout);
        mRecyclerView = findViewById(R.id.recyclerView);

        mRefreshLayout.setOnRefreshLoadMoreListener(this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new StorageListAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);

        //联网获取数据
        onRefresh();
    }

    private void onRefresh() {
        mAdapter.setEmptyView(getLoadingView(mRefreshLayout));
        if (mPage == null) {
            mPage = new Page();
        }
        mPage.reset();

        getPresenter().getStorageListData(mPage, true);
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        if (mPage != null && mPage.haveNext()) {
            getPresenter().getStorageListData(mPage.nextPage(), false);
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
        StorageService.WareHouse wareHouse = ((StorageListAdapter) adapter).getData().get(position);
        if(wareHouse != null) {
            start(MaterialListFragment.getInstance(wareHouse));
        }
    }

    public static InventoryQueryFragment getInstance() {
        InventoryQueryFragment fragment = new InventoryQueryFragment();
        return fragment;
    }

    /**
     * 联网获取仓库列表数据成功后回调
     *
     * @param contents
     * @param page
     * @param refresh
     */
    public void getStorageListDataSuccess(List<StorageService.WareHouse> contents, Page page, boolean refresh) {
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
     * 联网获取仓库列表数据失败后回调
     */
    public void getStorageListDataError() {
        mAdapter.setEmptyView(getNoDataView(mRefreshLayout));
        mRefreshLayout.finishRefresh(false);
        mRefreshLayout.finishRefresh(false);
    }
}
