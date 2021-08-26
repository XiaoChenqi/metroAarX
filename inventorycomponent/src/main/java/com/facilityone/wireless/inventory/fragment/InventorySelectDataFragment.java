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
import com.facilityone.wireless.a.arch.widget.SearchBox;
import com.facilityone.wireless.inventory.R;
import com.facilityone.wireless.inventory.adapter.InventorySelectDataAdapter;
import com.facilityone.wireless.inventory.model.InventoryConstant;
import com.facilityone.wireless.inventory.model.InventorySelectDataBean;
import com.facilityone.wireless.inventory.model.MaterialService;
import com.facilityone.wireless.inventory.presenter.InventorySelectDataPresenter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by peter.peng on 2018/11/26.
 * 选择数据界面
 */

public class InventorySelectDataFragment extends BaseFragment<InventorySelectDataPresenter> implements BaseQuickAdapter.OnItemClickListener, OnRefreshListener {

    private static final String FROM_TYPE = "from_type";
    public static final String SELECT_DATA = "select_data";
    private static final String EMPLOYEE_ID = "employee_id";
    private static final String INVENTORY_ID = "inventory_id";
    private static final String WAREHOUSE_ID = "warehouse_id";
    private static final String LABORER_ID = "laborer_id";
    private SmartRefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private SearchBox mSearchBox;

    private Page mPage;
    private MaterialService.MaterialCondition mMaterialCondition;//物资查询条件
    private int mFromType;//请求类型
    private long mEmployeeId;//执行人id
    private long mInventoryId;//库存id
    private long mWarehouseId;//仓库id
    private long mLaborerId;//执行人id（选择主管）

    List<InventorySelectDataBean> mTotalSelectDataList;
    List<InventorySelectDataBean> mSelectDataList;
    private InventorySelectDataAdapter mAdapter;


    @Override
    public InventorySelectDataPresenter createPresenter() {
        return new InventorySelectDataPresenter();
    }

    @Override
    public Object setLayout() {
        return R.layout.fragment_inventory_select_data;
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
            mFromType = bundle.getInt(FROM_TYPE, -1);
            mEmployeeId = bundle.getLong(EMPLOYEE_ID, -1);
            mInventoryId = bundle.getLong(INVENTORY_ID, -1);
            mWarehouseId = bundle.getLong(WAREHOUSE_ID, -1);
            mLaborerId = bundle.getLong(LABORER_ID, -1);
        }

        mMaterialCondition = new MaterialService.MaterialCondition();

    }

    private void initView() {
        setSwipeBackEnable(false);
        String title = "";
        switch (mFromType) {
            case InventoryConstant.SELECT_STORAGE:
                title = getString(R.string.inventory_select_storage_title);
                break;
            case InventoryConstant.SELECT_MATERIAL:
            case InventoryConstant.SELECT_MATERIAL_OUT:
            case InventoryConstant.SELECT_MATERIAL_MOVE:
            case InventoryConstant.SELECT_MATERIAL_RESERVE:
                title = getString(R.string.inventory_select_material_title);
                break;
            case InventoryConstant.SELECT_PROVIDER:
                title = getString(R.string.inventory_material_provider_select_title);
                break;
            case InventoryConstant.SELECT_SUPERVISOR:
                title = getString(R.string.inventory_select_supervisor_title);
                break;
            case InventoryConstant.SELECT_RECEIVING_PERSON:
                title = getString(R.string.inventory_person_select_title);
                break;
            case InventoryConstant.SELECT_ADMINISTRATOR:
                title = getString(R.string.inventory_administrator_select_title);
                break;
            case InventoryConstant.SELECT_RESERVATION_PERSON:
                title = getString(R.string.inventory_reservation_select_title);
                break;
        }
        setTitle(title);

        mRefreshLayout = findViewById(R.id.refreshLayout);
        mRecyclerView = findViewById(R.id.recyclerView);
        mSearchBox = findViewById(R.id.search_box);

        //是否启用上拉加载更多
        mRefreshLayout.setEnableLoadMore(false);
        //是否启用下拉刷新
        //        mRefreshLayout.setEnableRefresh(false);
        //是否启用纯滚动模式
        //        mRefreshLayout.setEnablePureScrollMode(true);
        //设置刷新监听
        mRefreshLayout.setOnRefreshListener(this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mTotalSelectDataList = new ArrayList<>();
        mSelectDataList = new ArrayList<>();
        mAdapter = new InventorySelectDataAdapter(mSelectDataList, mFromType);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);

        mSearchBox.setOnSearchBox(new SearchBox.OnSearchBox() {
            @Override
            public void onSearchTextChanged(String curCharacter) {
                //数据过滤
                getPresenter().filter(mFromType,curCharacter, mTotalSelectDataList);

            }
        });

        //联网请求数据
        getListData();

    }

    private void getListData() {
        mAdapter.setEmptyView(getLoadingView(mRefreshLayout));
        if (mPage == null) {
            mPage = new Page();
        }
        mPage.reset();
        mPage.setPageSize(1000);
        mTotalSelectDataList.clear();

        switch (mFromType) {
            case InventoryConstant.SELECT_STORAGE://选择仓库
                getPresenter().getStorageListData(mPage, mEmployeeId);
                break;
            case InventoryConstant.SELECT_PROVIDER://选择供应商
                getPresenter().getProviderListData(mPage, mInventoryId);
                break;
            case InventoryConstant.SELECT_MATERIAL://选择物资
            case InventoryConstant.SELECT_MATERIAL_OUT://选择物资
            case InventoryConstant.SELECT_MATERIAL_MOVE://选择物资
            case InventoryConstant.SELECT_MATERIAL_RESERVE://选择物资
                getPresenter().getMaterialListData(mPage, mWarehouseId, mMaterialCondition);
                break;
            case InventoryConstant.SELECT_SUPERVISOR://选择主管
                getPresenter().getSupervisorListData(mLaborerId);
                break;
            case InventoryConstant.SELECT_RECEIVING_PERSON://选择领用人
            case InventoryConstant.SELECT_RESERVATION_PERSON://选择预订人
                getPresenter().getReceivingPersonList();
                break;
            case InventoryConstant.SELECT_ADMINISTRATOR://选择仓库管理员
                if (mWarehouseId == -1) {
                    ToastUtils.showShort(R.string.inventory_no_storage_or_no_administrator);
                    pop();
                }
                getPresenter().getStorageAdministrator(mPage, mWarehouseId);
                break;
        }
    }


    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        InventorySelectDataBean inventorySelectDataBean = ((InventorySelectDataAdapter) adapter).getData().get(position);
        switch (mFromType) {
            case InventoryConstant.SELECT_MATERIAL_OUT:
            case InventoryConstant.SELECT_MATERIAL_MOVE:
            case InventoryConstant.SELECT_MATERIAL_RESERVE:
                MaterialService.Material material = (MaterialService.Material) inventorySelectDataBean.target;
                if(material != null && material.totalNumber <= 0) {
                    ToastUtils.showShort(R.string.inventory_material_no_amount);
                    return;
                }
                break;
        }

        Bundle bundle = null;
        if (inventorySelectDataBean != null) {
            bundle = new Bundle();
            bundle.putParcelable(SELECT_DATA, inventorySelectDataBean);
        }

        setFragmentResult(RESULT_OK, bundle);
        pop();
    }

    public List<InventorySelectDataBean> getTotalSelectDataList() {
        if (mTotalSelectDataList == null) {
            mTotalSelectDataList = new ArrayList<>();
        }
        return mTotalSelectDataList;
    }


    public void refreshView(List<InventorySelectDataBean> selectDataBeanList) {
        mRefreshLayout.finishRefresh();
        mSelectDataList.clear();
        if(mFromType == InventoryConstant.SELECT_MATERIAL_OUT
                || mFromType == InventoryConstant.SELECT_MATERIAL_MOVE) {
            if (selectDataBeanList != null && selectDataBeanList.size() > 0) {
                for (InventorySelectDataBean selectDataBean : selectDataBeanList) {
                    MaterialService.Material material = (MaterialService.Material) selectDataBean.target;
                    if(material != null && material.totalNumber > 0) {
                        mSelectDataList.add(selectDataBean);
                    }
                }
            }
        }else {
            mSelectDataList.addAll(selectDataBeanList);
        }

        mAdapter.notifyDataSetChanged();

        if (mAdapter.getData().size() == 0) {
            mAdapter.setEmptyView(getNoDataView(mRefreshLayout));
        }
    }

    public void getListDataError() {
        mRefreshLayout.finishRefresh();
        mAdapter.setEmptyView(getNoDataView(mRefreshLayout));
    }

    /**
     * 当下拉刷新时回调
     *
     * @param refreshLayout
     */
    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        getListData();
    }


    /**
     * @param type 请求类型
     * @param id
     * @return
     */
    public static InventorySelectDataFragment getInstance(int type, long id) {
        InventorySelectDataFragment fragment = new InventorySelectDataFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(FROM_TYPE, type);
        switch (type) {
            case InventoryConstant.SELECT_STORAGE://选择仓库
                bundle.putLong(EMPLOYEE_ID, id);
                break;
            case InventoryConstant.SELECT_PROVIDER://选择供应商
                bundle.putLong(INVENTORY_ID, id);
                break;
            case InventoryConstant.SELECT_MATERIAL://选择物资
            case InventoryConstant.SELECT_MATERIAL_OUT://选择物资
            case InventoryConstant.SELECT_MATERIAL_MOVE://选择物资
            case InventoryConstant.SELECT_MATERIAL_RESERVE://选择物资
                bundle.putLong(WAREHOUSE_ID, id);
                break;
            case InventoryConstant.SELECT_SUPERVISOR://选择主管
                bundle.putLong(LABORER_ID, id);
                break;
            case InventoryConstant.SELECT_ADMINISTRATOR://选择仓库管理员
                bundle.putLong(WAREHOUSE_ID, id);
                break;
        }

        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * @param type 请求类型
     * @return
     */
    public static InventorySelectDataFragment getInstance(int type) {
        InventorySelectDataFragment fragment = new InventorySelectDataFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(FROM_TYPE, type);
        fragment.setArguments(bundle);
        return fragment;
    }

}
