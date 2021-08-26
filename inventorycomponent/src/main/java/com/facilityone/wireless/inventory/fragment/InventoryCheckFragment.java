package com.facilityone.wireless.inventory.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.facilityone.wireless.a.arch.mvp.BaseFragment;
import com.facilityone.wireless.a.arch.utils.NoDoubleClickListener;
import com.facilityone.wireless.a.arch.widget.CustomContentItemView;
import com.facilityone.wireless.a.arch.widget.FMWarnDialogBuilder;
import com.facilityone.wireless.basiclib.app.FM;
import com.facilityone.wireless.basiclib.utils.StringUtils;
import com.facilityone.wireless.inventory.R;
import com.facilityone.wireless.inventory.adapter.MaterialAdapter;
import com.facilityone.wireless.inventory.model.BatchService;
import com.facilityone.wireless.inventory.model.InventoryConstant;
import com.facilityone.wireless.inventory.model.InventorySelectDataBean;
import com.facilityone.wireless.inventory.model.MaterialService;
import com.facilityone.wireless.inventory.presenter.InventoryCheckPresenter;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;

import java.util.ArrayList;
import java.util.List;

import static com.facilityone.wireless.inventory.model.InventoryConstant.SELECT_STORAGE;

/**
 * Created by peter.peng on 2018/12/6.
 * 盘点界面
 */

public class InventoryCheckFragment extends BaseFragment<InventoryCheckPresenter> implements View.OnClickListener, MaterialAdapter.OnItemClick, BaseQuickAdapter.OnItemChildClickListener {
    private static final int INVENTORY_CHECK_REQUEST_CODE = 8001;
    private static final int CHECK_SELECT_MATERIAL_REQUEST_CODE = 8002;
    private static final int CHECK_SELECT_STORAGE_REQUEST_CODE = 8003;
    public static final int INVENTORY_CHECK_QRCODE_REQUEST_CODE = 8004;

    private CustomContentItemView mSelectStorageTv;//选择仓库
    private LinearLayout mMaterialLl;//物料
    private RecyclerView mMaterialRv;//物料
    private Button mMaterialCheckBtn;//盘点按钮

    private List<MaterialService.MaterialInfo> mMaterialList;
    private MaterialAdapter mMaterialAdapter;
    private int mSelectPosition = -1;
    private long mWarehouseId = -1;//仓库id

    @Override
    public InventoryCheckPresenter createPresenter() {
        return new InventoryCheckPresenter();
    }

    @Override
    public Object setLayout() {
        return R.layout.fragment_inventory_check;
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
        setTitle(R.string.inventory_check_title);

        setRightIcon(R.string.icon_scan, R.id.inventory_material_scan_id, R.dimen.topbar_title_size, new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View view) {
                //扫描物资二维码
                getPresenter().scanMaterialQRCode(mWarehouseId, mSelectStorageTv == null ? "" : mSelectStorageTv.getTipText().toString());
            }
        });

        setRightIcon(R.string.icon_add, R.id.inventory_material_select_id, R.dimen.topbar_title_size, new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View view) {
                if (TextUtils.isEmpty(mSelectStorageTv.getTipText()) || mWarehouseId < 0) {
                    ToastUtils.showShort(R.string.inventory_storage_empty_hint);
                    return;
                }
                //选择物料
                startForResult(InventorySelectDataFragment.getInstance(InventoryConstant.SELECT_MATERIAL, mWarehouseId), CHECK_SELECT_MATERIAL_REQUEST_CODE);
            }
        });

        mSelectStorageTv = findViewById(R.id.inventory_check_select_storage_tv);
        mMaterialLl = findViewById(R.id.inventory_check_material_ll);
        mMaterialRv = findViewById(R.id.inventory_check_material_rv);
        mMaterialCheckBtn = findViewById(R.id.inventory_check_material_check_btn);

        mMaterialRv.setNestedScrollingEnabled(false);
        mMaterialRv.setLayoutManager(new LinearLayoutManager(getContext()));
        mMaterialList = new ArrayList<>();
        mMaterialAdapter = new MaterialAdapter(mMaterialList, InventoryConstant.INVENTORY_CHECK);
        mMaterialRv.setAdapter(mMaterialAdapter);
        mMaterialAdapter.setOnItemClick(this);
        mMaterialAdapter.setOnItemChildClickListener(this);

        mSelectStorageTv.setOnClickListener(this);
        mMaterialCheckBtn.setOnClickListener(this);


    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.inventory_check_select_storage_tv) {//选择仓库
            if (mMaterialList.size() > 0) {
                ToastUtils.showShort(R.string.inventory_storage_useing);
                return;
            }
            Long emId = FM.getEmId();
            if(emId != null) {
                startForResult(InventorySelectDataFragment.getInstance(SELECT_STORAGE, emId), CHECK_SELECT_STORAGE_REQUEST_CODE);
            }
        } else if (v.getId() == R.id.inventory_check_material_check_btn) {//物资盘点
            inventoryMaterialCheck();
        }
    }

    /**
     * 物资盘点
     */
    private void inventoryMaterialCheck() {
        MaterialService.MaterialCheckRequest request = new MaterialService.MaterialCheckRequest();
        if (isValid(request)) {
            request.warehouseId = mWarehouseId;
            getPresenter().inventoryMaterialCheck(request);
        }
    }

    /**
     * 判断用户界面输入值是否有效
     *
     * @param request
     * @return
     */
    private boolean isValid(MaterialService.MaterialCheckRequest request) {
        if (mWarehouseId == -1) {
            ToastUtils.showShort(R.string.inventory_storage_empty_hint);
            return false;
        }
        if (mMaterialList == null || mMaterialList.size() == 0) {
            ToastUtils.showShort(R.string.inventory_material_describe_material_hint);
            return false;
        }

        List<MaterialService.Inventory> inventoryList = new ArrayList<>();
        for (MaterialService.MaterialInfo materialInfo : mMaterialList) {
            if (materialInfo.batch == null || materialInfo.batch.size() == 0) {
                continue;
            }
            MaterialService.Inventory inventory = new MaterialService.Inventory();
            inventory.inventoryId = materialInfo.inventoryId;
            List<BatchService.Batch> batchList = new ArrayList<>();
            for (BatchService.Batch batch : materialInfo.batch) {
                BatchService.Batch tempBatch = new BatchService.Batch();
                tempBatch.batchId = batch.batchId;
                tempBatch.inventoryNumber = batch.amount;
                tempBatch.number = batch.number;
                if(!TextUtils.isEmpty(batch.desc)) {
                    tempBatch.desc = batch.desc;
                }
                batchList.add(tempBatch);
            }
            inventory.batch = batchList;
            inventoryList.add(inventory);
        }

        if (inventoryList.size() == 0) {
            ToastUtils.showShort(R.string.inventory_select_material_and_check_amount);
            return false;
        }

        request.inventory = inventoryList;

        return true;
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            if (requestCode == INVENTORY_CHECK_QRCODE_REQUEST_CODE) {
                mMaterialList.remove(mSelectPosition);
                if (mMaterialList.size() > 0) {
                    mMaterialLl.setVisibility(View.VISIBLE);
                } else {
                    mMaterialLl.setVisibility(View.GONE);
                }
                mMaterialAdapter.notifyDataSetChanged();
            }
            return;
        }
        if (data == null) {
            return;
        }

        InventorySelectDataBean selectDataBean = data.getParcelable(InventorySelectDataFragment.SELECT_DATA);

        switch (requestCode) {
            case CHECK_SELECT_MATERIAL_REQUEST_CODE://选择物资
                if (selectDataBean != null) {
                    MaterialService.Material material = (MaterialService.Material) selectDataBean.target;
                    if (material != null) {
                        MaterialService.MaterialInfo materialInfo = getPresenter().Material2MaterInfo(material);
                        if (!checkMaterial(materialInfo)) {
                            materialInfo.number = materialInfo.totalNumber;
                            mMaterialList.add(materialInfo);
                        }
                    }
                    if (mMaterialList.size() > 0) {
                        mMaterialLl.setVisibility(View.VISIBLE);
                    } else {
                        mMaterialLl.setVisibility(View.GONE);
                    }
                    mMaterialAdapter.notifyDataSetChanged();
                }
                break;
            case CHECK_SELECT_STORAGE_REQUEST_CODE://选择仓库
                if (selectDataBean != null) {
                    mSelectStorageTv.setTipText(StringUtils.formatString(selectDataBean.name));
                    mWarehouseId = selectDataBean.id;
                }
                break;
            case INVENTORY_CHECK_QRCODE_REQUEST_CODE:
            case INVENTORY_CHECK_REQUEST_CODE:
                MaterialService.MaterialInfo materialInfo = data.getParcelable(MaterialBatchFragment.DATA_MATERIAL);
                if (materialInfo != null) {
                    mMaterialList.get(mSelectPosition).batch = materialInfo.batch;
                    mMaterialList.get(mSelectPosition).number = 0f;
                    if (materialInfo.batch != null) {
                        for (BatchService.Batch batch : materialInfo.batch) {
                            if (batch.number != null) {
                                mMaterialList.get(mSelectPosition).number += batch.number;
                            }
                        }
                    }
                    if (mMaterialList.size() > 0) {
                        mMaterialLl.setVisibility(View.VISIBLE);
                    } else {
                        mMaterialLl.setVisibility(View.GONE);
                    }
                    mMaterialAdapter.notifyDataSetChanged();
                }
                if (mWarehouseId == -1 || TextUtils.isEmpty(mSelectStorageTv.getTipText())) {
                    mWarehouseId = mMaterialList.get(mSelectPosition).warehouseId == null ? -1 : mMaterialList.get(mSelectPosition).warehouseId;
                    mSelectStorageTv.setTipText(getMaterialList().get(mSelectPosition).warehouseName);
                }
                break;
        }
    }

    /**
     * 判断是否重复添加物资
     *
     * @param material
     * @return
     */
    public boolean checkMaterial(MaterialService.MaterialInfo material) {
        for (MaterialService.MaterialInfo tempMaterial : mMaterialList) {
            if (tempMaterial.inventoryId.equals(material.inventoryId)) {
                ToastUtils.showShort(R.string.inventory_material_exist);
                return true;
            }
        }
        return false;
    }

    /**
     * 当删除物资时回调
     *
     * @param material
     * @param position
     */
    @Override
    public void onBtnDelete(MaterialService.MaterialInfo material, final int position) {
        new FMWarnDialogBuilder(getContext())
                .setIconVisible(false)
                .setSureBluBg(true)
                .setTitle(R.string.inventory_remind)
                .setSure(R.string.inventory_sure)
                .setTip(R.string.inventory_delete_material)
                .addOnBtnSureClickListener(new FMWarnDialogBuilder.OnBtnClickListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, View view) {
                        mMaterialList.remove(position);
                        mMaterialAdapter.notifyDataSetChanged();
                        if (mMaterialList.size() == 0) {
                            mMaterialLl.setVisibility(View.GONE);
                        }
                        dialog.dismiss();
                    }
                }).create(R.style.fmDefaultWarnDialog).show();
    }

    /**
     * 当点击物资item时回调
     *
     * @param adapter
     * @param view
     * @param position
     */
    @Override
    public void
    onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        MaterialService.MaterialInfo material = ((MaterialAdapter) adapter).getData().get(position);
        if (material != null) {
            mSelectPosition = position;
            startForResult(MaterialBatchFragment.getInstance(material, InventoryConstant.INVENTORY_BATCH_CHECK), INVENTORY_CHECK_REQUEST_CODE);
        }
    }

    public List<MaterialService.MaterialInfo> getMaterialList() {
        if (mMaterialList == null) {
            mMaterialList = new ArrayList<>();
        }
        return mMaterialList;
    }

    public void setSelectPosition(int selectPosition) {
        this.mSelectPosition = selectPosition;
    }

    public static InventoryCheckFragment getInstance() {
        InventoryCheckFragment fragment = new InventoryCheckFragment();
        return fragment;
    }
}
