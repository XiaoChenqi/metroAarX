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
import com.facilityone.wireless.a.arch.ec.module.Page;
import com.facilityone.wireless.a.arch.ec.module.UserService;
import com.facilityone.wireless.a.arch.mvp.BaseFragment;
import com.facilityone.wireless.a.arch.widget.CustomContentItemView;
import com.facilityone.wireless.a.arch.widget.EditNumberView;
import com.facilityone.wireless.a.arch.widget.FMWarnDialogBuilder;
import com.facilityone.wireless.basiclib.app.FM;
import com.facilityone.wireless.basiclib.utils.StringUtils;
import com.facilityone.wireless.inventory.R;
import com.facilityone.wireless.inventory.adapter.MaterialAdapter;
import com.facilityone.wireless.inventory.model.BatchService;
import com.facilityone.wireless.inventory.model.InventoryConstant;
import com.facilityone.wireless.inventory.model.MaterialService;
import com.facilityone.wireless.inventory.model.StorageService;
import com.facilityone.wireless.inventory.model.SupervisorService;
import com.facilityone.wireless.inventory.presenter.InventoryDirectOutPresenter;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.facilityone.wireless.inventory.model.InventoryConstant.SELECT_ADMINISTRATOR;
import static com.facilityone.wireless.inventory.model.InventoryConstant.SELECT_STORAGE;

/**
 * Created by peter.peng on 2018/12/3.
 * 直接出库界面
 */

public class InventoryDirectOutFragment extends BaseFragment<InventoryDirectOutPresenter> implements View.OnClickListener, MaterialAdapter.OnItemClick, BaseQuickAdapter.OnItemChildClickListener {
    public static final int DIRECT_OUT_SELECT_STORAGE_REQUEST_CODE = 5001;
    public static final int DIRECT_OUT_SELECT_RECEIVING_PERSON_REQUEST_CODE = 5002;
    public static final int INVENTORY_DIRECT_OUT_REQUEST_CODE = 5003;
    public static final int INVENTORY_DIRECT_OUT_QRCODE_REQUEST_CODE = 5004;
    public static final int DIRECT_OUT_SELECT_SUPERVISOR_REQUEST_CODE = 5005;
    public static final int DIRECT_OUT_SELECT_ADMINISTRATOR_REQUEST_CODE = 5006;

    private CustomContentItemView mSelectStorageTv;//选择仓库
    private CustomContentItemView mSelectAdministratorTv;//选择仓库管理员
    private CustomContentItemView mSelectReceivingPersonTv;//选择领用人
    private CustomContentItemView mSelectSupervisorTv;//选择主管
    private LinearLayout mDescLl;//备注
    private EditNumberView mDescEnv;//备注
    private LinearLayout mMaterialLl;//物料
    private RecyclerView mMaterialRv;//物料
    private Button mMaterialOutBtn;//出库按钮

    private long mWarehouseId = -1;//仓库id
    private long mReceivingPersonId = -1;//领用人id
    private long mAdministratorId = -1;//管理员id
    private long mSupervisorId = -1;//主管id
    private int mSelectPosition = -1;

    private List<StorageService.Administrator> mAdministratorList;//仓库管理员数组

    private MaterialAdapter mMaterialAdapter;
    private List<MaterialService.MaterialInfo> mMaterialList;

    @Override
    public InventoryDirectOutPresenter createPresenter() {
        return new InventoryDirectOutPresenter();
    }

    @Override
    public Object setLayout() {
        return R.layout.fragment_inventory_direct_out;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
        initView();
    }

    private void initData() {
        setSwipeBackEnable(false);
        mAdministratorList = new ArrayList<>();
    }


    private void initView() {

        mSelectStorageTv = findViewById(R.id.inventory_direct_select_storage_tv);
        mSelectAdministratorTv = findViewById(R.id.inventory_direct_select_administrator_tv);
        mSelectReceivingPersonTv = findViewById(R.id.inventory_direct_select_receiving_person_tv);
        mSelectSupervisorTv = findViewById(R.id.inventory_direct_select_supervisor_tv);
        mDescLl = findViewById(R.id.inventory_direct_desc_ll);
        mDescEnv = findViewById(R.id.inventory_direct_desc_env);
        mMaterialLl = findViewById(R.id.inventory_direct_material_ll);
        mMaterialRv = findViewById(R.id.inventory_direct_material_rv);
        mMaterialOutBtn = findViewById(R.id.inventory_direct_material_out_btn);

        mMaterialRv.setNestedScrollingEnabled(false);

        mSelectStorageTv.setOnClickListener(this);
        mSelectAdministratorTv.setOnClickListener(this);
        mSelectReceivingPersonTv.setOnClickListener(this);
        mSelectSupervisorTv.setOnClickListener(this);
        mMaterialOutBtn.setOnClickListener(this);

        mMaterialRv.setLayoutManager(new LinearLayoutManager(getContext()));
        mMaterialList = new ArrayList<>();
        mMaterialAdapter = new MaterialAdapter(mMaterialList, InventoryConstant.INVENTORY_OUT);
        mMaterialRv.setAdapter(mMaterialAdapter);
        mMaterialAdapter.setOnItemClick(this);
        mMaterialAdapter.setOnItemChildClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.inventory_direct_select_storage_tv) {//选择仓库
            selectStorage();
        } else if (v.getId() == R.id.inventory_direct_select_administrator_tv) {//选择仓库管理员
            selectAdministrator();
        } else if (v.getId() == R.id.inventory_direct_select_receiving_person_tv) {//选择领用人
            selectReceivingPerson();
        } else if (v.getId() == R.id.inventory_direct_select_supervisor_tv) {//选择主管
            selectSupervisor();
        } else if (v.getId() == R.id.inventory_direct_material_out_btn) {//物资出库
            InventoryMaterialOut();
        }
    }

    /**
     * 物资出库
     */
    private void InventoryMaterialOut() {
        MaterialService.MaterialOutRequest request = new MaterialService.MaterialOutRequest();
        if (isValid(request)) {
            request.type = InventoryConstant.INVENTORY_MATERIAL_DIRECT_OUT;
            request.warehouseId = mWarehouseId;
            request.administrator = mAdministratorId;
            request.receivingPersonId = mReceivingPersonId;
            request.remarks = mDescEnv.getDesc().trim();
            if (mSupervisorId != -1) {
                request.supervisor = mSupervisorId;
            }
            getPresenter().InventoryMaterialOut(request);
        }
    }

    /**
     * 判断界面输入值是否有效
     *
     * @param request
     * @return
     */
    private boolean isValid(MaterialService.MaterialOutRequest request) {
        if (mWarehouseId == -1) {
            ToastUtils.showShort(R.string.inventory_storage_empty_hint);
            return false;
        }
        if (mMaterialList == null || mMaterialList.size() == 0) {
            ToastUtils.showShort(R.string.inventory_material_describe_material_hint);
            return false;
        }
        if (mAdministratorId == -1) {
            ToastUtils.showShort(R.string.inventory_useless_administrator);
            return false;
        }
        if (mReceivingPersonId == -1) {
            ToastUtils.showShort(R.string.inventory_select_need_person_tip);
            return false;
        }

        double countOut = 0.0D;
        List<MaterialService.Inventory> inventoryList = new ArrayList<>();
        for (MaterialService.MaterialInfo materialInfo : mMaterialList) {
            if (materialInfo.batch == null || materialInfo.batch.size() == 0) {
                ToastUtils.showShort(R.string.inventory_select_material_out_and_amount_not_null);
                return false;
            }
            MaterialService.Inventory inventory = new MaterialService.Inventory();
            inventory.inventoryId = materialInfo.inventoryId;
            List<BatchService.Batch> batchList = new ArrayList<>();
            for (BatchService.Batch batch : materialInfo.batch) {
                BatchService.Batch tempBatch = new BatchService.Batch();
                tempBatch.batchId = batch.batchId;
                tempBatch.amount = batch.number;
                if (tempBatch.amount != null) {
                    countOut += tempBatch.amount;
                }

                if (tempBatch.amount != null && tempBatch.amount != 0) {
                    batchList.add(tempBatch);
                }

            }
            inventory.batch = batchList;
            inventoryList.add(inventory);
        }

        if (inventoryList.size() == 0) {
            ToastUtils.showShort(R.string.inventory_select_material_out_and_amount);
            return false;
        }

        if (countOut <= 0) {
            ToastUtils.showShort(R.string.inventory_select_material_out_and_amount_gt_zero);
            return false;
        }

        Iterator<MaterialService.Inventory> iterator = inventoryList.iterator();
        while (iterator.hasNext()) {
            MaterialService.Inventory material = iterator.next();
            if (material == null || material.batch == null || material.batch.size() == 0) {
                iterator.remove();
            }
        }

        request.inventory = inventoryList;

        return true;
    }


    /**
     * 选择仓库
     */
    private void selectStorage() {
        if (mMaterialList.size() > 0) {
            ToastUtils.showShort(R.string.inventory_storage_useing);
            return;
        }
        Long emId = FM.getEmId();
        if (emId != null) {
            InventoryOutFragment patntFragment = (InventoryOutFragment) getParentFragment();
            patntFragment.startForResult(InventorySelectDataFragment.getInstance(SELECT_STORAGE, emId), DIRECT_OUT_SELECT_STORAGE_REQUEST_CODE);
        }

    }

    /**
     * 选择仓库管理员
     */
    private void selectAdministrator() {
        if (TextUtils.isEmpty(mSelectStorageTv.getTipText()) || mWarehouseId == -1) {
            ToastUtils.showShort(R.string.inventory_no_storage_or_no_administrator);
            return;
        }
        if (mAdministratorList == null || mAdministratorList.size() == 0) {
            ToastUtils.showShort(R.string.inventory_no_administrator);
            return;
        }
        if (mAdministratorList != null && mAdministratorList.size() > 1) {
            InventoryOutFragment patntFragment = (InventoryOutFragment) getParentFragment();
            patntFragment.startForResult(InventorySelectDataFragment.getInstance(SELECT_ADMINISTRATOR, mWarehouseId), DIRECT_OUT_SELECT_ADMINISTRATOR_REQUEST_CODE);
        }
    }

    /**
     * 选择领用人
     */
    private void selectReceivingPerson() {
        mSelectSupervisorTv.setTipText("");
        mSupervisorId = -1;
        InventoryOutFragment patntFragment = (InventoryOutFragment) getParentFragment();
        patntFragment.startForResult(InventorySelectDataFragment.getInstance(InventoryConstant.SELECT_RECEIVING_PERSON), DIRECT_OUT_SELECT_RECEIVING_PERSON_REQUEST_CODE);
    }

    /**
     * 选择主管
     */
    private void selectSupervisor() {
        if (TextUtils.isEmpty(mSelectReceivingPersonTv.getTipText()) || mReceivingPersonId == -1) {
            ToastUtils.showShort(R.string.inventory_confrim_select_need_people);
            return;
        }
        InventoryOutFragment patntFragment = (InventoryOutFragment) getParentFragment();
        patntFragment.startForResult(InventorySelectDataFragment.getInstance(InventoryConstant.SELECT_SUPERVISOR, mReceivingPersonId), DIRECT_OUT_SELECT_SUPERVISOR_REQUEST_CODE);
    }

    /**
     * 刷新仓库
     *
     * @param storage
     */
    public void refreshStorage(StorageService.Storage storage) {
        if (storage != null) {
            mSelectStorageTv.setTipText(StringUtils.formatString(storage.name));
            mWarehouseId = storage.warehouseId;
            if (TextUtils.isEmpty(mSelectStorageTv.getTipText())) {
                mDescLl.setVisibility(View.GONE);
            } else {
                mDescLl.setVisibility(View.VISIBLE);
            }

            if (storage.administrator != null) {
                mAdministratorList.clear();
                mAdministratorList.addAll(storage.administrator);
            }
            showAdministrator(null);
        }
    }

    /**
     * 刷新领用人
     *
     * @param userInfo
     */
    public void refreshReceivingPerson(UserService.UserInfoBean userInfo) {
        if (userInfo != null) {
            mSelectReceivingPersonTv.setTipText(StringUtils.formatString(userInfo.name));
            mReceivingPersonId = userInfo.emId;
        }
    }

    /**
     * 刷新主管
     *
     * @param supervisor
     */
    public void refreshSuperVisor(SupervisorService.Supervisor supervisor) {
        if (supervisor != null) {
            mSelectSupervisorTv.setTipText(StringUtils.formatString(supervisor.name));
            mSupervisorId = supervisor.supervisorId;
        }
    }

    /**
     * 刷新仓库管理员
     *
     * @param administrator
     * @param administratorList
     */
    public void refreshAdministrator(StorageService.Administrator administrator, List<StorageService.Administrator> administratorList) {
        if (administrator != null) {
            mSelectAdministratorTv.setTipText(administrator.name);
            mAdministratorId = administrator.administratorId;
        }
        if (administratorList != null) {
            mAdministratorList.clear();
            mAdministratorList.addAll(administratorList);
        }
    }

    /**
     * 刷新物资
     *
     * @param materialInfo
     */
    public void refreshMaterial(MaterialService.MaterialInfo materialInfo) {
        if (materialInfo != null) {
            mMaterialList.get(mSelectPosition).batch = materialInfo.batch;
            mMaterialList.get(mSelectPosition).number = 0f;
            if (materialInfo.batch != null) {
                for (BatchService.Batch batch : materialInfo.batch) {
                    mMaterialList.get(mSelectPosition).number += batch.number;
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
            mSelectStorageTv.setTipText(StringUtils.formatString(getMaterialList().get(mSelectPosition).warehouseName));
            //联网获取仓库管理员
            requestStorageAdministrator();
        }
        if (mWarehouseId == -1 || TextUtils.isEmpty(mSelectStorageTv.getTipText())) {
            mDescLl.setVisibility(View.GONE);
        } else {
            mDescLl.setVisibility(View.VISIBLE);
        }

    }


    /**
     * 移除物资
     */
    public void removeMaterial() {
        mMaterialList.remove(mSelectPosition);
        mMaterialAdapter.notifyDataSetChanged();
        if (mMaterialList.size() > 0) {
            mMaterialLl.setVisibility(View.VISIBLE);
        } else {
            mMaterialLl.setVisibility(View.GONE);
        }
    }

    /**
     * 联网获取仓库管理员
     */
    private void requestStorageAdministrator() {
        Page page = new Page();
        page.reset();
        List<StorageService.Administrator> administratorList = new ArrayList<>();
        getPresenter().getStorageAdministrator(page, mWarehouseId, administratorList);
    }


    /**
     * 显示仓库管理员
     *
     * @param administratorList
     */
    public void showAdministrator(List<StorageService.Administrator> administratorList) {
        if (administratorList != null) {
            mAdministratorList.clear();
            mAdministratorList.addAll(administratorList);
        }
        if (mAdministratorList != null && mAdministratorList.size() > 0) {
            mSelectAdministratorTv.setTipText(StringUtils.formatString(mAdministratorList.get(0).name));
            mAdministratorId = mAdministratorList.get(0).administratorId;
            mSelectAdministratorTv.setClickable(mAdministratorList.size() > 1);
        } else {
            mSelectAdministratorTv.setTipText("");
            mAdministratorId = -1;
            mSelectAdministratorTv.setClickable(false);
        }
    }

    /**
     * 判断当前是否已经选择仓库
     *
     * @return
     */
    public boolean isSelectStorage() {
        if (mSelectStorageTv != null) {
            if (TextUtils.isEmpty(mSelectStorageTv.getTipText()) || mWarehouseId == 1) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }


    /**
     * 当点击删除物资按钮时回调
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
     * 当点击物资item是回调
     *
     * @param adapter
     * @param view
     * @param position
     */
    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        MaterialService.MaterialInfo material = ((MaterialAdapter) adapter).getData().get(position);
        if (material != null) {
            mSelectPosition = position;
            InventoryOutFragment parentFragment = (InventoryOutFragment) getParentFragment();
            parentFragment.startForResult(MaterialBatchFragment.getInstance(material, InventoryConstant.INVENTORY_BATCH_DIRECT_OUT), INVENTORY_DIRECT_OUT_REQUEST_CODE);
        }
    }

    /**
     * 添加物资
     *
     * @param material
     */
    public void addMaterial(MaterialService.Material material) {
        if (mMaterialList == null) {
            mMaterialList = new ArrayList<>();
        }

        if (material != null) {
            MaterialService.MaterialInfo materialInfo = getPresenter().Material2MaterInfo(material);
            if (!checkMaterial(materialInfo)) {
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

    public CustomContentItemView getSelectStorageTv() {
        return mSelectStorageTv;
    }

    public long getWarehouseId() {
        return mWarehouseId;
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

    public List<StorageService.Administrator> getAdministratorList() {
        if (mAdministratorList == null) {
            mAdministratorList = new ArrayList<>();
        }
        return mAdministratorList;
    }

    public static InventoryDirectOutFragment getInstance() {
        InventoryDirectOutFragment fragment = new InventoryDirectOutFragment();
        return fragment;
    }

}
