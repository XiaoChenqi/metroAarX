package com.facilityone.wireless.inventory.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.facilityone.wireless.a.arch.ec.adapter.FunctionAdapter;
import com.facilityone.wireless.a.arch.ec.module.FunctionService;
import com.facilityone.wireless.a.arch.ec.module.IService;
import com.facilityone.wireless.a.arch.ec.ui.FzScanActivity;
import com.facilityone.wireless.a.arch.mvp.BaseFragment;
import com.facilityone.wireless.inventory.R;
import com.facilityone.wireless.inventory.model.InventoryConstant;
import com.facilityone.wireless.inventory.model.MaterialService;
import com.facilityone.wireless.inventory.presenter.InventoryPresenter;
import com.fm.tool.scan.ScanActivity;
import com.huawei.hms.ml.scan.HmsScan;
import com.joanzapata.iconify.widget.IconTextView;
import com.zdf.activitylauncher.ActivityLauncher;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by peter.peng on 2018/11/23.
 */

public class InventoryFragment extends BaseFragment<InventoryPresenter> implements BaseQuickAdapter.OnItemClickListener, View.OnClickListener {

    private RecyclerView mRecyclerView;
    private IconTextView mScanItv;

    private List<FunctionService.FunctionBean> mFunctionBeanList;
    private FunctionAdapter mFunctionAdapter;

    @Override
    public InventoryPresenter createPresenter() {
        return new InventoryPresenter();
    }

    @Override
    public Object setLayout() {
        return R.layout.fragment_inventory;
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
        Bundle bundle = getArguments();
        if (bundle != null) {
            ArrayList<FunctionService.FunctionBean> bean = (ArrayList<FunctionService.FunctionBean>) bundle.getSerializable(IService.FRAGMENT_CHILD_KEY);
            if (bean != null) {
                mFunctionBeanList.addAll(bean);
            } else {
                ToastUtils.showShort(R.string.inventory_no_function);
            }

            boolean runAlone = bundle.getBoolean(IService.COMPONENT_RUNALONE, false);
            if (runAlone) {
                setSwipeBackEnable(false);
            }
        }
    }

    private void initView() {
        setTitle(R.string.inventory_title);

        mRecyclerView = findViewById(R.id.recyclerView);
        mScanItv = findViewById(R.id.scan_inventory_itv);

        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),FunctionService.COUNT));
//        mRecyclerView.addItemDecoration(new GridItemDecoration(getResources().getColor(R.color.grey_d6)));

        mFunctionAdapter = new FunctionAdapter(mFunctionBeanList);
        mRecyclerView.setAdapter(mFunctionAdapter);
        mFunctionAdapter.setOnItemClickListener(this);

        mScanItv.setOnClickListener(this);
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        //获取角标
        getUndoNumber();
    }


    @Override
    public void leftBackListener() {
        getActivity().finish();
    }

    private void getUndoNumber() {
        getPresenter().getUndoNumber(FunctionService.UNDO_TYPE_INVENTORY);
    }


    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        FunctionService.FunctionBean functionBean = ((FunctionAdapter) adapter).getData().get(position);
        switch (functionBean.index) {
            case InventoryConstant.INVENTORY_CREATE :
                start(InventoryCreateFragment.getInstance());
                break;
            case InventoryConstant.INVENTORY_IN :
                start(InventoryInFragment.getInstance());
                break;
            case InventoryConstant.INVENTORY_OUT :
                start(InventoryOutFragment.getInstance());
                break;
            case InventoryConstant.INVENTORY_MOVE :
                start(InventoryMoveFragment.getInstance());
                break;
            case InventoryConstant.INVENTORY_CHECK :
                start(InventoryCheckFragment.getInstance());
                break;
            case InventoryConstant.INVENTORY_RESERVE :
                start(InventoryReserveFragment.getInstance());
                break;
            case InventoryConstant.INVENTORY_APPROVAL :
                start(InventoryApprovalFragment.getInstance());
                break;
            case InventoryConstant.INVENTORY_QUERY :
                start(InventoryQueryFragment.getInstance());
                break;
            case InventoryConstant.INVENTORY_MY :
                start(InventoryMyFragment.getInstance());
                break;
        }
    }

    public List<FunctionService.FunctionBean> getFunctionBeanList() {
        if(mFunctionBeanList == null) {
            mFunctionBeanList = new ArrayList<>();
        }
        return mFunctionBeanList;
    }

    public void updateFunction(List<FunctionService.FunctionBean> functionBeanList) {
        mFunctionAdapter.replaceData(functionBeanList);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.scan_inventory_itv) {//二维码扫描
            Intent intent = new Intent(getContext(), FzScanActivity.class);
            ActivityLauncher.init(getActivity())
                    .startActivityForResult(intent, new ActivityLauncher.Callback() {
                        @Override
                        public void onActivityResult(int resultCode, Intent data) {
                            if (data != null){
                                HmsScan result=data.getParcelableExtra("scanResult");
                                if (result!=null){
                                    if (result.originalValue != null){
                                        MaterialService.InventoryQRCodeBean inventoryQRCodeBean = getPresenter().getInventoryQRCodeBean(result.originalValue);
                                        try {
                                            long wareHouseId = Long.parseLong(inventoryQRCodeBean.wareHouseId);
                                            start(MaterialInfoFragment.getInstance(inventoryQRCodeBean.code,wareHouseId,true));
                                        } catch (NumberFormatException e) {
                                            e.printStackTrace();
                                            ToastUtils.showShort("此二维码无法识别");
                                        }
                                    }else {
                                        ToastUtils.showShort(R.string.inventory_qr_code_error);
                                    }
                                }
                            }

                        }
                    });
        }
    }

    public static InventoryFragment getInstance(Bundle bundle) {
        InventoryFragment fragment = new InventoryFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

}
