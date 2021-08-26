package com.facilityone.wireless.inventory.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.facilityone.wireless.a.arch.ec.adapter.GridTagAdapter;
import com.facilityone.wireless.a.arch.ec.module.AttachmentBean;
import com.facilityone.wireless.a.arch.ec.module.Page;
import com.facilityone.wireless.a.arch.mvp.BaseFragment;
import com.facilityone.wireless.inventory.R;
import com.facilityone.wireless.inventory.adapter.MaterialListAdapter;
import com.facilityone.wireless.inventory.model.MaterialService;
import com.facilityone.wireless.inventory.model.StorageService;
import com.facilityone.wireless.inventory.presenter.MaterialListPresenter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by peter.peng on 2018/12/11.
 * 库存查询物资列表界面
 */

public class MaterialListFragment extends BaseFragment<MaterialListPresenter> implements OnRefreshLoadMoreListener, BaseQuickAdapter.OnItemClickListener, DrawerLayout.DrawerListener, View.OnClickListener, TextWatcher {
    private static final String DATA_WAREHOUSE = "data_warehouse";
    private static final int MAX_NUMBER = 3;

    private SmartRefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private DrawerLayout mDrawerLayout;
    private ImageView mQueryMenuIv;

    private EditText mQueryNameEt;//查询菜单物资名称
    private ImageView mClearNameIv;
    private RecyclerView mQueryTypeRv;//查询菜单数量类型
    private Button mResetBtn;//重置按钮
    private Button mSureBtn;//确定按钮

    private MaterialListAdapter mMaterialAdapter;
    private Page mPage;
    private MaterialService.MaterialCondition mCondition;//物资查询条件

    private GridTagAdapter mMaterialTypeAdapter;
    private List<AttachmentBean> mMaterialNumberTypeList;

    private StorageService.WareHouse mWareHouse;
    private long mWarehouseId = -1;

    @Override
    public MaterialListPresenter createPresenter() {
        return new MaterialListPresenter();
    }

    @Override
    public Object setLayout() {
        return R.layout.fragment_material_list;
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
            mWareHouse = bundle.getParcelable(DATA_WAREHOUSE);
        }

        if (mWareHouse != null) {
            mWarehouseId = mWareHouse.warehouseId;
        }

        mCondition = new MaterialService.MaterialCondition();

        mMaterialNumberTypeList = new ArrayList<>();
        mMaterialNumberTypeList.addAll(getPresenter().getMaterialType(getContext()));

    }

    private void initView() {
        String title = "";
        if (mWareHouse != null) {
            title = mWareHouse.warehouseName;
        }
        setTitle(title);

        mRefreshLayout = findViewById(R.id.refreshLayout);
        mRecyclerView = findViewById(R.id.recyclerView);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mQueryMenuIv = findViewById(R.id.query_menu_iv);

        mQueryNameEt = findViewById(R.id.material_query_menu_name_et);
        mClearNameIv = findViewById(R.id.clear_name_iv);
        mQueryTypeRv = findViewById(R.id.material_query_menu_recyclerview);
        mResetBtn = findViewById(R.id.material_query_menu_reset_btn);
        mSureBtn = findViewById(R.id.material_query_menu_sure_btn);

        mRefreshLayout.setOnRefreshLoadMoreListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mMaterialAdapter = new MaterialListAdapter();
        mRecyclerView.setAdapter(mMaterialAdapter);
        mMaterialAdapter.setOnItemClickListener(this);

        mQueryTypeRv.setNestedScrollingEnabled(false);
        mQueryTypeRv.setLayoutManager(new GridLayoutManager(getContext(), MAX_NUMBER));
        mMaterialTypeAdapter = new GridTagAdapter(getContext(),mMaterialNumberTypeList,true);
        mQueryTypeRv.setAdapter(mMaterialTypeAdapter);

        mDrawerLayout.addDrawerListener(this);
        mQueryMenuIv.setOnClickListener(this);
        mResetBtn.setOnClickListener(this);
        mSureBtn.setOnClickListener(this);
        mClearNameIv.setOnClickListener(this);
        mQueryNameEt.addTextChangedListener(this);
        findViewById(R.id.menu_btn_ll).setOnClickListener(this);
        if (mMaterialNumberTypeList != null && mMaterialNumberTypeList.size() > 0) {
            mMaterialNumberTypeList.get(0).check = true;
        }

        //联网请求数据
        onRefresh();
    }

    private void onRefresh() {
        mMaterialAdapter.setEmptyView(getLoadingView(mRefreshLayout));
        if (mPage == null) {
            mPage = new Page();
        }
        mPage.reset();

        getPresenter().getMaterialListData(mWarehouseId, mCondition, mPage,true);
    }


    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        if(mPage != null && mPage.haveNext()) {
            getPresenter().getMaterialListData(mWarehouseId, mCondition, mPage.nextPage(),false);
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
        MaterialService.Material material = ((MaterialListAdapter) adapter).getData().get(position);
        if(material != null) {
            start(MaterialInfoFragment.getInstance(material.inventoryId));
        }
    }

    /**
     * 联网获取物资列表数据成功后回调
     * @param contents
     * @param page
     * @param refresh
     */
    public void getMaterialListDataSuccess(List<MaterialService.Material> contents, Page page, boolean refresh) {
        this.mPage = page;
        if(refresh) {
            mMaterialAdapter.setNewData(contents);
            mRefreshLayout.finishRefresh();
        }else {
            if(contents != null) {
                mMaterialAdapter.addData(contents);
            }
            mRefreshLayout.finishLoadMore();
        }

        if(mMaterialAdapter.getData().size() == 0) {
            mMaterialAdapter.setEmptyView(getNoDataView(mRefreshLayout));
        }
    }

    /**
     * 联网获取物资列表数据失败后回调
     */
    public void getMaterialListDataError() {
        mMaterialAdapter.setNewData(null);
        mMaterialAdapter.setEmptyView(getNoDataView(mRefreshLayout));
        mRefreshLayout.finishRefresh(false);
        mRefreshLayout.finishLoadMore(false);
    }


    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(View drawerView) {

    }

    /**
     * 当drawlayout关闭时回调
     * @param drawerView
     */
    @Override
    public void onDrawerClosed(View drawerView) {
        //关闭输入法面板
        KeyboardUtils.hideSoftInput(getActivity());
    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.query_menu_iv) {
            //打开右侧条件选择框
            mDrawerLayout.openDrawer(Gravity.RIGHT);
        }else if(v.getId() == R.id.material_query_menu_reset_btn) {//重置
            //重置物资查询条件
            resetCondition();
        }else if(v.getId() == R.id.material_query_menu_sure_btn) {//确定
            //获取查询条件
            getCondition();
            mDrawerLayout.closeDrawers();
            //根据查询条件重新获取数据
            onRefresh();
        }else if (v.getId() == R.id.clear_name_iv){
            mQueryNameEt.setText("");
            mCondition.name = null;
            mClearNameIv.setVisibility(View.GONE);
        }
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (TextUtils.isEmpty(mQueryNameEt.getText().toString())) {
            mClearNameIv.setVisibility(View.GONE);
        } else {
            mClearNameIv.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 获取查询条件
     */
    private void getCondition() {
        if(!TextUtils.isEmpty(mQueryNameEt.getText())) {
            mCondition.name = mQueryNameEt.getText().toString().trim();
        }else {
            mCondition.name = null;
        }

        for (int i = 0; i < mMaterialNumberTypeList.size(); i++) {
            AttachmentBean materialNumberType = mMaterialNumberTypeList.get(i);
            if(materialNumberType.check) {
                mCondition.type = (int) materialNumberType.value;
            }
        }
    }

    /**
     * 重置物资查询条件
     */
    private void resetCondition() {
        mQueryNameEt.setText("");
        for (int i = 0; i < mMaterialNumberTypeList.size(); i++) {
            AttachmentBean materialNumberType = mMaterialNumberTypeList.get(i);
            materialNumberType.check = false;
            if (materialNumberType.value == 0){
                materialNumberType.check = true;
            }
        }
        mMaterialTypeAdapter.notifyDataSetChanged();

        mCondition.name = null;
        mCondition.param = null;
        mCondition.type = null;
    }

    public static MaterialListFragment getInstance(StorageService.WareHouse wareHouse) {
        MaterialListFragment fragment = new MaterialListFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(DATA_WAREHOUSE, wareHouse);
        fragment.setArguments(bundle);
        return fragment;
    }
}
