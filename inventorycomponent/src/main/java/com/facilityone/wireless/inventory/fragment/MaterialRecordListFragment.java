package com.facilityone.wireless.inventory.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.blankj.utilcode.util.ToastUtils;
import com.facilityone.wireless.a.arch.ec.module.Page;
import com.facilityone.wireless.a.arch.mvp.BaseFragment;
import com.facilityone.wireless.basiclib.utils.StringUtils;
import com.facilityone.wireless.inventory.R;
import com.facilityone.wireless.inventory.adapter.MaterialRecordAdapter;
import com.facilityone.wireless.inventory.model.MaterialService;
import com.facilityone.wireless.inventory.presenter.MaterialRecordListPresenter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.List;

/**
 * Created by peter.peng on 2018/12/12.
 * 物资记录列表界面
 */

public class MaterialRecordListFragment extends BaseFragment<MaterialRecordListPresenter> implements OnRefreshLoadMoreListener {
    private static final String INVENTORY_ID = "inventory_id";
    private static final String MATERIAL_NAME = "material_name";

    private SmartRefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;

    private MaterialRecordAdapter mMaterialRecordAdapter;

    private Page mPage;
    private long mInventoryId = -1;
    private String mMaterialName;

    @Override
    public MaterialRecordListPresenter createPresenter() {
        return new MaterialRecordListPresenter();
    }

    @Override
    public Object setLayout() {
        return R.layout.fragment_material_record_list;
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
        Bundle bundle = getArguments();
        if (bundle != null) {
            mInventoryId = bundle.getLong(INVENTORY_ID, -1);
            mMaterialName = bundle.getString(MATERIAL_NAME, "");
        }
    }

    private void initView() {
        String title = StringUtils.formatString(mMaterialName);
        setTitle(title);

        mRefreshLayout = findViewById(R.id.refreshLayout);
        mRecyclerView = findViewById(R.id.recyclerView);

        mRefreshLayout.setOnRefreshLoadMoreListener(this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mMaterialRecordAdapter = new MaterialRecordAdapter();
        mRecyclerView.setAdapter(mMaterialRecordAdapter);

        //联网请求数据
        onRefresh();
    }

    private void onRefresh() {
        mMaterialRecordAdapter.setEmptyView(getLoadingView(mRefreshLayout));
        if(mPage == null) {
            mPage = new Page();
        }
        mPage.reset();

        getPresenter().getMaterialRecordListData(mInventoryId,mPage,true);
    }


    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        if(mPage != null && mPage.haveNext()) {
            getPresenter().getMaterialRecordListData(mInventoryId,mPage.nextPage(),false);
        }else {
            ToastUtils.showShort(R.string.inventory_no_more_data);
            mRefreshLayout.finishLoadMore();
        }
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        onRefresh();
    }


    public void getMaterialRecordListDataSuccess(List<MaterialService.MaterialRecord> contents, Page page, boolean refresh) {
        this.mPage = page;
        if(refresh) {
            mMaterialRecordAdapter.setNewData(contents);
            mRefreshLayout.finishRefresh();
        }else {
            if(contents != null) {
                mMaterialRecordAdapter.addData(contents);
            }
            mRefreshLayout.finishLoadMore();
        }

        if(mMaterialRecordAdapter.getData().size() == 0) {
            mMaterialRecordAdapter.setEmptyView(getNoDataView(mRefreshLayout));
        }
    }

    public void getMaterialRecordListDataError() {
        mMaterialRecordAdapter.setEmptyView(getNoDataView(mRefreshLayout));
        mRefreshLayout.finishRefresh(false);
        mRefreshLayout.finishLoadMore(false);
    }

    public static MaterialRecordListFragment getInstance(long inventoryId, String materialName) {
        MaterialRecordListFragment fragment = new MaterialRecordListFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(INVENTORY_ID, inventoryId);
        bundle.putString(MATERIAL_NAME, materialName);
        fragment.setArguments(bundle);
        return fragment;
    }
}
