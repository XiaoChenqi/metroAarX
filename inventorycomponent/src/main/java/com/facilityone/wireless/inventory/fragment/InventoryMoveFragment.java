package com.facilityone.wireless.inventory.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.facilityone.wireless.a.arch.ec.module.Page;
import com.facilityone.wireless.a.arch.mvp.BaseFragment;
import com.facilityone.wireless.a.arch.utils.NoDoubleClickListener;
import com.facilityone.wireless.a.arch.widget.CustomContentItemView;
import com.facilityone.wireless.a.arch.widget.EditNumberView;
import com.facilityone.wireless.a.arch.widget.FMWarnDialogBuilder;
import com.facilityone.wireless.basiclib.app.FM;
import com.facilityone.wireless.basiclib.utils.StringUtils;
import com.facilityone.wireless.inventory.R;
import com.facilityone.wireless.inventory.adapter.MaterialAdapter;
import com.facilityone.wireless.inventory.model.BatchService;
import com.facilityone.wireless.inventory.model.InventoryConstant;
import com.facilityone.wireless.inventory.model.InventorySelectDataBean;
import com.facilityone.wireless.inventory.model.MaterialService;
import com.facilityone.wireless.inventory.model.StorageService;
import com.facilityone.wireless.inventory.presenter.InventoryMovePresenter;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.facilityone.wireless.inventory.model.InventoryConstant.SELECT_ADMINISTRATOR;

/**
 * Created by peter.peng on 2018/12/6.
 * 移库界面
 */

public class InventoryMoveFragment extends BaseFragment<InventoryMovePresenter> implements View.OnClickListener, MaterialAdapter.OnItemClick, BaseQuickAdapter.OnItemChildClickListener {
    private static final int MOVE_SELECT_STORAGE_REQUEST_CODE = 7001;
    private static final int MOVE_SELECT_TARGET_STORAGE_REQUEST_CODE = 7002;
    private static final int MOVE_SELECT_ADMINISTRATOR_REQUEST_CODE = 7003;
    private static final int MOVE_SELECT_TARGET_ADMINISTRATOR_REQUEST_CODE = 7004;
    private static final int MOVE_SELECT_MATERIAL_REQUEST_CODE = 7005;
    private static final int INVENTORY_MOVE_REQUEST_CODE = 7006;
    public static final int INVENTORY_MOVE_QRCODE_REQUEST_CODE = 7007;

    private LinearLayout mStorageLl;//原仓库
    private TextView mStorageNameTv;//原仓库名字
    private LinearLayout mTargetStorageLl;//目标仓库
    private TextView mTargetStorageNameTv;//目标仓库名字
    private CustomContentItemView mAdministratorTv;//选择原仓库管理员
    private CustomContentItemView mTargetAdministratorTv;//选择目标原仓库管理员
    private LinearLayout mDescLl;//备注
    private EditNumberView mDescEnv;//备注
    private LinearLayout mMaterialLl;//物料
    private RecyclerView mMaterialRv;//物料
    private Button mInventoryMoveBtn;//移库

    private long mWarehouseId = -1;//原仓库id
    private long mTargetWarehouseId = -1;//目标仓库id
    private long mAdministratorId = -1;//原仓库管理员id
    private long mTargetAdministratorId = -1;//目标仓库管理员id

    private MaterialAdapter mMaterialAdapter;
    private List<MaterialService.MaterialInfo> mMaterialList;

    private List<StorageService.Administrator> mAdministratorList;//原仓库管理员数组
    private List<StorageService.Administrator> mTargetAdministratorList;//目标仓库管理员数组
    private int mSelectPosition = -1;

    @Override
    public InventoryMovePresenter createPresenter() {
        return new InventoryMovePresenter();
    }

    @Override
    public Object setLayout() {
        return R.layout.fragment_inventory_move;
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
        mAdministratorList = new ArrayList<>();
        mTargetAdministratorList = new ArrayList<>();
    }

    private void initView() {
        setTitle(R.string.inventory_move_title);

        setRightIcon(R.string.icon_scan, R.id.inventory_material_scan_id, R.dimen.topbar_title_size, new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View view) {
                //扫描物资二维码
                getPresenter().scanMaterialQRCode(mWarehouseId, mStorageNameTv == null ? "" : mStorageNameTv.getText().toString());
            }
        });

        setRightIcon(R.string.icon_add, R.id.inventory_material_select_id, R.dimen.topbar_title_size, new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View view) {
                if (TextUtils.isEmpty(mStorageNameTv.getText()) || mWarehouseId < 0) {
                    ToastUtils.showShort(R.string.inventory_storage_empty_hint);
                    return;
                }
                //选择物料
                startForResult(InventorySelectDataFragment.getInstance(InventoryConstant.SELECT_MATERIAL_MOVE, mWarehouseId), MOVE_SELECT_MATERIAL_REQUEST_CODE);
            }
        });


        mStorageLl = findViewById(R.id.inventory_move_storage_ll);
        mStorageNameTv = findViewById(R.id.inventory_move_storage_name_tv);
        mTargetStorageLl = findViewById(R.id.inventory_move_target_storage_ll);
        mTargetStorageNameTv = findViewById(R.id.inventory_move_target_storage_name_ll);
        mAdministratorTv = findViewById(R.id.inventory_move_select_administrator_tv);
        mTargetAdministratorTv = findViewById(R.id.inventory_move_select_target_administrator_tv);
        mDescLl = findViewById(R.id.inventory_move_desc_ll);
        mDescEnv = findViewById(R.id.inventory_move_desc_env);
        mMaterialLl = findViewById(R.id.inventory_move_material_ll);
        mMaterialRv = findViewById(R.id.inventory_move_material_rv);
        mInventoryMoveBtn = findViewById(R.id.inventory_move_material_btn);

        mMaterialRv.setLayoutManager(new LinearLayoutManager(getContext()));
        mMaterialList = new ArrayList<>();
        mMaterialAdapter = new MaterialAdapter(mMaterialList, InventoryConstant.INVENTORY_MOVE);
        mMaterialRv.setAdapter(mMaterialAdapter);
        mMaterialAdapter.setOnItemClick(this);
        mMaterialAdapter.setOnItemChildClickListener(this);

        mStorageLl.setOnClickListener(this);
        mTargetStorageLl.setOnClickListener(this);
        mAdministratorTv.setOnClickListener(this);
        mTargetAdministratorTv.setOnClickListener(this);
        mInventoryMoveBtn.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.inventory_move_storage_ll) {//选择原仓库
            if (mMaterialList.size() > 0) {
                ToastUtils.showShort(R.string.inventory_storage_useing);
                return;
            }
            Long emId = FM.getEmId();
            if (emId != null) {
                startForResult(InventorySelectDataFragment.getInstance(InventoryConstant.SELECT_STORAGE, emId), MOVE_SELECT_STORAGE_REQUEST_CODE);
            }
        } else if (v.getId() == R.id.inventory_move_target_storage_ll) {//选择目标仓库
            startForResult(InventorySelectDataFragment.getInstance(InventoryConstant.SELECT_STORAGE), MOVE_SELECT_TARGET_STORAGE_REQUEST_CODE);
        } else if (v.getId() == R.id.inventory_move_select_administrator_tv) {//选择原仓库管理员
            selectAdministrator();
        } else if (v.getId() == R.id.inventory_move_select_target_administrator_tv) {//选择目标仓库管理员
            selectTargetAdministrator();
        } else if (v.getId() == R.id.inventory_move_material_btn) {
            //物资移库
            InventoryMaterialMove();
        }
    }

    /**
     * 物资移库
     */
    private void InventoryMaterialMove() {
        MaterialService.MaterialOutRequest request = new MaterialService.MaterialOutRequest();
        if (isValid(request)) {
            request.type = InventoryConstant.INVENTORY_MATERIAL_MOVE;
            request.warehouseId = mWarehouseId;
            request.targetWarehouseId = mTargetWarehouseId;
            request.administrator = mAdministratorId;
            request.targetAdministrator = mTargetAdministratorId;
            request.remarks = mDescEnv.getDesc().trim();
            getPresenter().InventoryMaterialOut(request);
        }
    }

    /**
     * 判断用户界面输入值是否有效
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
        if (mTargetWarehouseId == -1) {
            ToastUtils.showShort(R.string.inventory_target_storage_empty_hint);
            return false;
        }

        if (mAdministratorId == -1) {
            ToastUtils.showShort(R.string.inventory_original_administrator_useless);
            return false;
        }

        if (mTargetAdministratorId == -1) {
            ToastUtils.showShort(R.string.inventory_target_administrator_useless);
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
            ToastUtils.showShort(R.string.inventory_select_material_and_amount);
            return false;
        }

        if (countOut <= 0) {
            ToastUtils.showShort(R.string.inventory_select_material_move_and_amount_gt_zero);
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
     * 选择仓库管理员
     */
    private void selectAdministrator() {
        if (TextUtils.isEmpty(mStorageNameTv.getText()) || mWarehouseId == -1) {
            ToastUtils.showShort(R.string.inventory_no_storage_or_no_administrator);
            return;
        }
        if (mAdministratorList == null || mAdministratorList.size() == 0) {
            ToastUtils.showShort(R.string.inventory_no_administrator);
            return;
        }
        if (mAdministratorList != null && mAdministratorList.size() > 1) {
            startForResult(InventorySelectDataFragment.getInstance(SELECT_ADMINISTRATOR, mWarehouseId), MOVE_SELECT_ADMINISTRATOR_REQUEST_CODE);
        }
    }


    /**
     * 选择目标仓库管理员
     */
    private void selectTargetAdministrator() {
        if (TextUtils.isEmpty(mTargetStorageNameTv.getText()) || mTargetWarehouseId == -1) {
            ToastUtils.showShort(R.string.inventory_no_storage_or_no_administrator);
            return;
        }
        if (mTargetAdministratorList == null || mTargetAdministratorList.size() == 0) {
            ToastUtils.showShort(R.string.inventory_no_administrator);
            return;
        }
        if (mTargetAdministratorList != null && mTargetAdministratorList.size() > 1) {
            startForResult(InventorySelectDataFragment.getInstance(SELECT_ADMINISTRATOR, mTargetWarehouseId), MOVE_SELECT_TARGET_ADMINISTRATOR_REQUEST_CODE);
        }
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            if (requestCode == INVENTORY_MOVE_QRCODE_REQUEST_CODE) {
                mMaterialList.remove(mSelectPosition);
                mMaterialAdapter.notifyDataSetChanged();
                if (mMaterialList.size() > 0) {
                    mMaterialLl.setVisibility(View.VISIBLE);
                } else {
                    mMaterialLl.setVisibility(View.GONE);
                }
            }
            return;
        }
        if (data == null) {
            return;
        }

        InventorySelectDataBean selectDataBean = data.getParcelable(InventorySelectDataFragment.SELECT_DATA);
        switch (requestCode) {
            case MOVE_SELECT_STORAGE_REQUEST_CODE://选择仓库
                if (selectDataBean != null) {
                    if (mTargetStorageNameTv.getText().equals(selectDataBean.name) || mTargetWarehouseId == selectDataBean.id) {
                        ToastUtils.showShort(R.string.inventory_storage_equal);
                        mStorageNameTv.setText("");
                        mWarehouseId = -1;
                        mAdministratorList.clear();
                    } else {
                        mStorageNameTv.setText(StringUtils.formatString(selectDataBean.name));
                        mWarehouseId = selectDataBean.id;
                        StorageService.Storage storage = (StorageService.Storage) selectDataBean.target;
                        if (storage.administrator != null) {
                            mAdministratorList.clear();
                            mAdministratorList.addAll(storage.administrator);
                        }
                    }
                    //显示管理员
                    showAdministrator(null);
                    //刷新备注
                    refreshDesc();
                }
                break;
            case MOVE_SELECT_TARGET_STORAGE_REQUEST_CODE://选择目标仓库
                if (selectDataBean != null) {
                    if (mStorageNameTv.getText().equals(selectDataBean.name) || mWarehouseId == selectDataBean.id) {
                        ToastUtils.showShort(R.string.inventory_storage_equal);
                        mTargetStorageNameTv.setText("");
                        mTargetWarehouseId = -1;
                        mTargetAdministratorList.clear();
                    } else {
                        mTargetStorageNameTv.setText(selectDataBean.name);
                        mTargetWarehouseId = selectDataBean.id;
                        StorageService.Storage storage = (StorageService.Storage) selectDataBean.target;
                        if (storage.administrator != null) {
                            mTargetAdministratorList.clear();
                            mTargetAdministratorList.addAll(storage.administrator);
                        }
                    }
                    //显示目标管理员
                    showTargetAdministrator();
                    //刷新备注
                    refreshDesc();
                }
                break;
            case MOVE_SELECT_ADMINISTRATOR_REQUEST_CODE://选择仓库管理员
                if (selectDataBean != null) {
                    mAdministratorTv.setTipText(selectDataBean.name);
                    mAdministratorId = selectDataBean.id;
                    List<StorageService.Administrator> administratorList = (List<StorageService.Administrator>) selectDataBean.target;
                    if (administratorList != null) {
                        mAdministratorList.clear();
                        mAdministratorList.addAll(administratorList);
                    }
                }
                break;
            case MOVE_SELECT_TARGET_ADMINISTRATOR_REQUEST_CODE://选择目标仓库管理员
                if (selectDataBean != null) {
                    mTargetAdministratorTv.setTipText(selectDataBean.name);
                    mTargetAdministratorId = selectDataBean.id;
                    List<StorageService.Administrator> administratorList = (List<StorageService.Administrator>) selectDataBean.target;
                    if (administratorList != null) {
                        mTargetAdministratorList.clear();
                        mTargetAdministratorList.addAll(administratorList);
                    }
                }
                break;
            case MOVE_SELECT_MATERIAL_REQUEST_CODE://选择物资
                if (selectDataBean != null) {
                    MaterialService.Material material = (MaterialService.Material) selectDataBean.target;
                    if (material != null) {
                        MaterialService.MaterialInfo materialInfo = getPresenter().Material2MaterInfo(material);
                        if (!checkMaterial(materialInfo)) {
                            mMaterialList.add(materialInfo);
                        }
                    }
                }
                if (mMaterialList.size() > 0) {
                    mMaterialLl.setVisibility(View.VISIBLE);
                } else {
                    mMaterialLl.setVisibility(View.GONE);
                }
                mMaterialAdapter.notifyDataSetChanged();
                break;
            case INVENTORY_MOVE_QRCODE_REQUEST_CODE:
            case INVENTORY_MOVE_REQUEST_CODE:
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
                if (mWarehouseId == -1 || TextUtils.isEmpty(mStorageNameTv.getText())) {
                    mWarehouseId = mMaterialList.get(mSelectPosition).warehouseId == null ? -1 : mMaterialList.get(mSelectPosition).warehouseId;
                    mStorageNameTv.setText(getMaterialList().get(mSelectPosition).warehouseName);
                    //联网获取仓库管理员
                    requestStorageAdministrator();
                }
                if (mWarehouseId == -1 || TextUtils.isEmpty(mStorageNameTv.getText())) {
                    mDescLl.setVisibility(View.GONE);
                } else {
                    mDescLl.setVisibility(View.VISIBLE);
                }
                break;
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
     * 刷新备注的显示
     */
    private void refreshDesc() {
        if (mWarehouseId == -1 || TextUtils.isEmpty(mStorageNameTv.getText()) || mTargetWarehouseId == -1 || TextUtils.isEmpty(mTargetStorageNameTv.getText())) {
            mDescLl.setVisibility(View.GONE);
        } else {
            mDescLl.setVisibility(View.VISIBLE);
        }
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
            mAdministratorTv.setTipText(StringUtils.formatString(mAdministratorList.get(0).name));
            mAdministratorId = mAdministratorList.get(0).administratorId;
            mAdministratorTv.setClickable(mAdministratorList.size() > 1);
        } else {
            mAdministratorTv.setTipText("");
            mAdministratorId = -1;
            mAdministratorTv.setClickable(false);
        }
    }

    /**
     * 显示目标仓库管理员
     */
    public void showTargetAdministrator() {
        if (mTargetAdministratorList != null && mTargetAdministratorList.size() > 0) {
            mTargetAdministratorTv.setTipText(StringUtils.formatString(mTargetAdministratorList.get(0).name));
            mTargetAdministratorId = mTargetAdministratorList.get(0).administratorId;
            mTargetAdministratorTv.setClickable(mTargetAdministratorList.size() > 1);
        } else {
            mTargetAdministratorTv.setTipText("");
            mTargetAdministratorId = -1;
            mTargetAdministratorTv.setClickable(false);
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
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        MaterialService.MaterialInfo material = ((MaterialAdapter) adapter).getData().get(position);
        if (material != null) {
            mSelectPosition = position;
            startForResult(MaterialBatchFragment.getInstance(material, InventoryConstant.INVENTORY_BATCH_MOVE), INVENTORY_MOVE_REQUEST_CODE);
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

    public static InventoryMoveFragment getInstance() {
        InventoryMoveFragment fragment = new InventoryMoveFragment();
        return fragment;
    }
}
