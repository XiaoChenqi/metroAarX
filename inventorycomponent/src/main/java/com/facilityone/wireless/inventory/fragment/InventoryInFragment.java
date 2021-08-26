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
import com.facilityone.wireless.inventory.presenter.InventoryInPresenter;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;

import java.util.ArrayList;
import java.util.List;

import static com.facilityone.wireless.inventory.model.InventoryConstant.SELECT_STORAGE;

/**
 * Created by peter.peng on 2018/11/27.
 * 入库界面
 */

public class InventoryInFragment extends BaseFragment<InventoryInPresenter> implements View.OnClickListener, MaterialAdapter.OnItemClick, BaseQuickAdapter.OnItemChildClickListener {
    private static final int IN_SELECT_STORAGE_REQUEST_CODE = 2001;
    private static final int IN_SELECT_MATERIAL_REQUEST_CODE = 2002;
    private static final int INVENTORY_IN_REQUEST_CODE = 2003;
    public static final int INVENTORY_IN_QRCODE_REQUEST_CODE = 2004;

    private CustomContentItemView mSelectStorageTv;
    private EditNumberView mDescEnv;
    private RecyclerView mMaterialRv;
    private LinearLayout mDescLl;
    private LinearLayout mMaterialLl;
    private Button mInventoryInBtn;//入库
    
    private long mWarehouseId;//仓库id

    private MaterialAdapter mMaterialAdapter;
    private List<MaterialService.MaterialInfo> mMaterialList;
    private int mSelectPosition;

    @Override
    public InventoryInPresenter createPresenter() {
        return new InventoryInPresenter();
    }

    @Override
    public Object setLayout() {
        return R.layout.fragment_inventory_in;
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
        mWarehouseId = -1;
        mSelectPosition = -1;
    }


    private void initView() {
        setTitle(R.string.inventory_in_title);

        setRightIcon(R.string.icon_scan, R.id.inventory_material_scan_id, R.dimen.topbar_title_size, new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View view) {
                //扫描物资二维码
                getPresenter().scanMaterialQRCode(mWarehouseId,mSelectStorageTv == null ? "" : mSelectStorageTv.getTipText().toString());
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
                startForResult(InventorySelectDataFragment.getInstance(InventoryConstant.SELECT_MATERIAL, mWarehouseId), IN_SELECT_MATERIAL_REQUEST_CODE);
            }
        });

        mSelectStorageTv = findViewById(R.id.inventory_in_select_storage_tv);
        mDescEnv = findViewById(R.id.inventory_in_material_desc_env);
        mMaterialRv = findViewById(R.id.inventory_in_material_rv);
        mDescLl = findViewById(R.id.inventory_in_desc_ll);
        mMaterialLl = findViewById(R.id.inventory_in_material_ll);
        mInventoryInBtn = findViewById(R.id.inventory_create_material_save_btn);

        mMaterialRv.setNestedScrollingEnabled(false);
        mMaterialRv.setLayoutManager(new LinearLayoutManager(getContext()));
        mMaterialList = new ArrayList<>();
        mMaterialAdapter = new MaterialAdapter(mMaterialList, InventoryConstant.INVENTORY_IN);
        mMaterialRv.setAdapter(mMaterialAdapter);
        mMaterialAdapter.setOnItemClick(this);
        mMaterialAdapter.setOnItemChildClickListener(this);


        mSelectStorageTv.setOnClickListener(this);
        mInventoryInBtn.setOnClickListener(this);
    }




    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.inventory_in_select_storage_tv) {
            if (mMaterialList.size() > 0) {
                ToastUtils.showShort(R.string.inventory_storage_useing);
                return;
            }
            Long emId = FM.getEmId();
            if(emId != null) {
                //选择仓库
                startForResult(InventorySelectDataFragment.getInstance(SELECT_STORAGE, emId), IN_SELECT_STORAGE_REQUEST_CODE);
            }
        }else if(v.getId() == R.id.inventory_create_material_save_btn) {
            //入库保存物资信息
            saveMaterial();
        }
    }

    /**
     * 入库保存物资信息
     */
    private void saveMaterial() {
        if (TextUtils.isEmpty(mSelectStorageTv.getTipText()) || mWarehouseId < 0) {
            ToastUtils.showShort(R.string.inventory_storage_empty_hint);
            return;
        }

        if(mMaterialList == null || mMaterialList.size() == 0) {
            ToastUtils.showShort(R.string.inventory_add_material);
            return;
        }

        for (MaterialService.MaterialInfo material : mMaterialList) {
            if(material.batch == null || material.batch.size() == 0) {
                ToastUtils.showShort(R.string.inventory_add_batch);
                return;
            }
        }

        String desc = mDescEnv.getDesc().trim();
        //联网入库保存物资信息
        List<MaterialService.Inventory> inventoryList = new ArrayList<>();
        for (MaterialService.MaterialInfo material : mMaterialList) {
            MaterialService.Inventory inventory = new MaterialService.Inventory();
            inventory.inventoryId = material.inventoryId;
            inventory.batch = material.batch;
            inventoryList.add(inventory);
        }
        getPresenter().InventoryIn(mWarehouseId,desc,inventoryList);
    }


    @Override
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK) {
            if(requestCode == INVENTORY_IN_QRCODE_REQUEST_CODE) {
                mMaterialList.remove(mSelectPosition);
                mMaterialAdapter.notifyDataSetChanged();
                if(mMaterialList.size() > 0) {
                    mMaterialLl.setVisibility(View.VISIBLE);
                }else {
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
            case IN_SELECT_STORAGE_REQUEST_CODE:
                if(selectDataBean != null) {
                    mSelectStorageTv.setTipText(StringUtils.formatString(selectDataBean.name));
                    mWarehouseId = selectDataBean.id;
                }
                if (TextUtils.isEmpty(mSelectStorageTv.getTipText())) {
                    mDescLl.setVisibility(View.GONE);
                } else {
                    mDescLl.setVisibility(View.VISIBLE);
                }
                break;
            case IN_SELECT_MATERIAL_REQUEST_CODE:
                if(selectDataBean != null) {
                    MaterialService.Material material = (MaterialService.Material) selectDataBean.target;
                    if (material != null) {
                        MaterialService.MaterialInfo materialInfo = getPresenter().Material2MaterInfo(material);
                        if(!checkMaterial(materialInfo)) {
                            mMaterialList.add(materialInfo);
                        }
                    }
                }
                if (mMaterialList.size() > 0) {
                    mMaterialLl.setVisibility(View.VISIBLE);
                }else {
                    mMaterialLl.setVisibility(View.GONE);
                }
                mMaterialAdapter.notifyDataSetChanged();
                break;
            case INVENTORY_IN_QRCODE_REQUEST_CODE:
            case INVENTORY_IN_REQUEST_CODE:
                MaterialService.MaterialInfo materialInfo =  data.getParcelable(MaterialBatchFragment.DATA_MATERIAL);
                if(materialInfo != null) {
                    mMaterialList.get(mSelectPosition).batch = materialInfo.batch;
                    mMaterialList.get(mSelectPosition).number = 0f;
                    if(materialInfo.batch != null) {
                        for (BatchService.Batch batch : materialInfo.batch) {
                            if(batch.number != null) {
                                mMaterialList.get(mSelectPosition).number += batch.number;
                            }
                        }
                    }
                    if (mMaterialList.size() > 0) {
                        mMaterialLl.setVisibility(View.VISIBLE);
                    }else {
                        mMaterialLl.setVisibility(View.GONE);
                    }
                    mMaterialAdapter.notifyDataSetChanged();
                }
                if(mWarehouseId == -1 || TextUtils.isEmpty(mSelectStorageTv.getTipText())) {
                    mWarehouseId = mMaterialList.get(mSelectPosition).warehouseId == null ? -1 : mMaterialList.get(mSelectPosition).warehouseId;
                    mSelectStorageTv.setTipText(StringUtils.formatString(getMaterialList().get(mSelectPosition).warehouseName));
                }
                if (mWarehouseId == -1 || TextUtils.isEmpty(mSelectStorageTv.getTipText())) {
                    mDescLl.setVisibility(View.GONE);
                } else {
                    mDescLl.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    /**
     * 判断是否重复添加物资
     * @param material
     * @return
     */
    public boolean checkMaterial(MaterialService.MaterialInfo material) {
        for (MaterialService.MaterialInfo tempMaterial : mMaterialList) {
            if(tempMaterial.inventoryId.equals(material.inventoryId)) {
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
     * @param adapter
     * @param view
     * @param position
     */
    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        MaterialService.MaterialInfo material = ((MaterialAdapter) adapter).getData().get(position);
        if(material != null ) {
            mSelectPosition = position;
            startForResult(MaterialBatchFragment.getInstance(material,InventoryConstant.INVENTORY_BATCH_IN),INVENTORY_IN_REQUEST_CODE);
        }
    }

    public List<MaterialService.MaterialInfo> getMaterialList() {
        if(mMaterialList == null) {
            return new ArrayList<>();
        }
        return mMaterialList;
    }

    public void setSelectPosition(int selectPosition) {
        this.mSelectPosition = selectPosition;
    }

    public static InventoryInFragment getInstance() {
        InventoryInFragment fragment = new InventoryInFragment();
        return fragment;
    }
}
