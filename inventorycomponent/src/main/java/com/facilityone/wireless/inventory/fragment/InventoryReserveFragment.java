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
import com.facilityone.wireless.a.arch.mvp.BaseFragment;
import com.facilityone.wireless.a.arch.utils.NoDoubleClickListener;
import com.facilityone.wireless.a.arch.widget.CustomContentItemView;
import com.facilityone.wireless.a.arch.widget.EditNumberView;
import com.facilityone.wireless.a.arch.widget.FMWarnDialogBuilder;
import com.facilityone.wireless.basiclib.app.FM;
import com.facilityone.wireless.basiclib.utils.StringUtils;
import com.facilityone.wireless.inventory.R;
import com.facilityone.wireless.inventory.adapter.MaterialAdapter;
import com.facilityone.wireless.inventory.model.InventoryConstant;
import com.facilityone.wireless.inventory.model.InventorySelectDataBean;
import com.facilityone.wireless.inventory.model.MaterialService;
import com.facilityone.wireless.inventory.model.StorageService;
import com.facilityone.wireless.inventory.model.SupervisorService;
import com.facilityone.wireless.inventory.presenter.InventoryReservePresenter;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;

import java.util.ArrayList;
import java.util.List;

import static com.facilityone.wireless.inventory.model.InventoryConstant.SELECT_ADMINISTRATOR;

/**
 * Created by peter.peng on 2018/12/7.
 * 物资预定界面
 */

public class InventoryReserveFragment extends BaseFragment<InventoryReservePresenter> implements MaterialAdapter.OnItemClick, BaseQuickAdapter.OnItemChildClickListener, View.OnClickListener {
    private static final int INVENTORY_RESERVE_MATERIAL_ADD_REQUEST_CODE = 9001;
    private static final int INVENTORY_RESERVE_MATERIAL_MODIFY_REQUEST_CODE = 9002;
    private static final int RESERVE_SELECT_STORAGE_REQUEST_CODE = 9003;
    private static final int RESERVE_SELECT_SUPERVISOR_REQUEST_CODE = 9004;
    private static final int RESERVE_SELECT_ADMINISTRATOR_REQUEST_CODE = 9005;
    private static final String WOID = "woid";
    private static final String WOCODE = "wocode";
    private static final String FROM_TYPE = "from_type";


    private CustomContentItemView mSelectStorageTv;//选择仓库
    private CustomContentItemView mSelectAdministratorTv;//选择仓库管理员
    private CustomContentItemView mSelectSupervisorTv;//选择审批主管
    private CustomContentItemView mReservePersonTv;//预订人
    private EditNumberView mDescEnv;//备注
    private LinearLayout mMaterialLl;//物料
    private RecyclerView mMaterialRv;//物料
    private TextView mTotalMoneyTv;//总计
    private Button mReserveBtn;//预定按钮

    private MaterialAdapter mMaterialAdapter;
    private List<MaterialService.MaterialInfo> mMaterialList;

    private long mWarehouseId = -1;//仓库id
    private long mAdministratorId = -1;//仓库管理员id
    private long mSupervisorId = -1;//审批主管id
    private int mSelectPosition = -1;

    private List<StorageService.Administrator> mAdministratorList;//仓库管理员列表
    private List<SupervisorService.Supervisor> mSupervisorList;//主管列表
    private long mWoId;//关联工单id
    private String mWoCode;//关联工单编码
    private int mFromtype;//请求类型


    @Override
    public InventoryReservePresenter createPresenter() {
        return new InventoryReservePresenter();
    }

    @Override
    public Object setLayout() {
        return R.layout.fragment_inventory_reserve;
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
        mSupervisorList = new ArrayList<>();

        Bundle bundle = getArguments();
        if(bundle != null) {
            mWoId = bundle.getLong(WOID,-1);
            mWoCode = bundle.getString(WOCODE,"");
            mFromtype = bundle.getInt(FROM_TYPE,-1);
        }
    }

    private void initView() {
        setTitle(R.string.inventory_reserve_title);

        setRightIcon(R.string.icon_add, R.id.inventory_material_select_id, R.dimen.topbar_title_size, new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View view) {
                if (TextUtils.isEmpty(mSelectStorageTv.getTipText()) || mWarehouseId == -1) {
                    ToastUtils.showShort(R.string.inventory_storage_empty_hint);
                    return;
                }
                if(TextUtils.isEmpty(mSelectAdministratorTv.getTipText()) || mAdministratorId == -1) {
                    ToastUtils.showShort(R.string.inventory_select_administrator_tip);
                }
                if(TextUtils.isEmpty(mSelectSupervisorTv.getTipText()) || mSupervisorId == -1) {
                    ToastUtils.showShort(R.string.inventory_select_supervisor_tip);
                }

                startForResult(MaterialAddFragment.getInstance(InventoryConstant.TYPE_MATERIAL_ADD, mWarehouseId), INVENTORY_RESERVE_MATERIAL_ADD_REQUEST_CODE);
            }
        });

        mSelectStorageTv = findViewById(R.id.inventory_reserve_select_storage_tv);
        mSelectAdministratorTv = findViewById(R.id.inventory_reserve_select_administrator_tv);
        mSelectSupervisorTv = findViewById(R.id.inventory_reserve_select_supervisor_tv);
        mReservePersonTv = findViewById(R.id.inventory_reserve_person_tv);
        mDescEnv = findViewById(R.id.inventory_reserve_desc_env);
        mMaterialLl = findViewById(R.id.inventory_reserve_material_ll);
        mMaterialRv = findViewById(R.id.inventory_reserve_material_rv);
        mTotalMoneyTv = findViewById(R.id.inventory_reserve_total_money_tv);
        mReserveBtn = findViewById(R.id.inventory_reserve_material_btn);

        mMaterialRv.setNestedScrollingEnabled(false);
        mMaterialRv.setLayoutManager(new LinearLayoutManager(getContext()));
        mMaterialList = new ArrayList<>();
        mMaterialAdapter = new MaterialAdapter(mMaterialList, InventoryConstant.INVENTORY_RESERVE);
        mMaterialRv.setAdapter(mMaterialAdapter);
        mMaterialAdapter.setOnItemClick(this);
        mMaterialAdapter.setOnItemChildClickListener(this);

        mSelectStorageTv.setOnClickListener(this);
        mSelectAdministratorTv.setOnClickListener(this);
        mSelectSupervisorTv.setOnClickListener(this);
        mReserveBtn.setOnClickListener(this);

        Long emId = FM.getEmId();
        if(emId != null) {
            getPresenter().getSupervisorListData(emId);
        }
        mReservePersonTv.setTipText(StringUtils.formatString(FM.getEmName()));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.inventory_reserve_select_storage_tv) {//选择仓库
            if (mMaterialList.size() > 0) {
                ToastUtils.showShort(R.string.inventory_storage_useing);
                return;
            }
            startForResult(InventorySelectDataFragment.getInstance(InventoryConstant.SELECT_STORAGE), RESERVE_SELECT_STORAGE_REQUEST_CODE);
        } else if (v.getId() == R.id.inventory_reserve_select_administrator_tv) {//选择仓库管理员
            selectAdministrator();
        } else if (v.getId() == R.id.inventory_reserve_select_supervisor_tv) {//选择审批主管
            selectSupervisor();
        }else if(v.getId() == R.id.inventory_reserve_material_btn) {//物资预定按钮
            //物资预定
            InventoryMaterialReserve();
        }
    }

    /**
     * 物资预定
     */
    private void InventoryMaterialReserve() {
        MaterialService.MaterialReserveRequest request = new MaterialService.MaterialReserveRequest();
        if(isValid(request)) {
            request.userId = FM.getEmId();
            request.date = System.currentTimeMillis();
            request.warehouseId = mWarehouseId;
            request.administrator = mAdministratorId;
            request.supervisor = mSupervisorId;
            request.remarks = mDescEnv.getDesc().trim();
            if (mWoId != -1L) {
                request.woId = mWoId;
            }
            request.woCode = mWoCode;
            getPresenter().InventoryMaterialReserve(mFromtype,request);
        }
    }

    /**
     * 判断用户界面输入值是否有效
     *
     * @param request
     * @return
     */
    private boolean isValid(MaterialService.MaterialReserveRequest request) {
        if (mWarehouseId == -1) {
            ToastUtils.showShort(R.string.inventory_storage_empty_hint);
            return false;
        }

        if (mAdministratorId == -1) {
            ToastUtils.showShort(R.string.inventory_useless_administrator);
            return false;
        }

        if(mSupervisorId == -1) {
            ToastUtils.showShort(R.string.inventory_useless_supervisor);
        }

        if (mMaterialList == null || mMaterialList.size() == 0) {
            ToastUtils.showShort(R.string.inventory_material_describe_material_tip);
            return false;
        }


        List<MaterialService.MaterialReserve> materialReserveList = new ArrayList<>();
        for (MaterialService.MaterialInfo materialInfo : mMaterialList) {
            MaterialService.MaterialReserve materialReserve = new MaterialService.MaterialReserve();
            materialReserve.inventoryId = materialInfo.inventoryId;
            materialReserve.amount = materialInfo.number;
            materialReserveList.add(materialReserve);
        }

        if (materialReserveList.size() == 0) {
            ToastUtils.showShort(R.string.inventory_select_material_and_reserve_amount);
            return false;
        }

        request.materials = materialReserveList;

        return true;
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
            startForResult(InventorySelectDataFragment.getInstance(SELECT_ADMINISTRATOR, mWarehouseId), RESERVE_SELECT_ADMINISTRATOR_REQUEST_CODE);
        }
    }

    /**
     * 选择主管
     */
    private void selectSupervisor() {
        Long emId = FM.getEmId();
        if(emId != null) {
            startForResult(InventorySelectDataFragment.getInstance(InventoryConstant.SELECT_SUPERVISOR, emId), RESERVE_SELECT_SUPERVISOR_REQUEST_CODE);
        }
    }

    /**
     * 刷新审批主管
     */
    public void refreshSupervisor() {
        if (mSupervisorList != null && mSupervisorList.size() > 0) {
            SupervisorService.Supervisor supervisor = mSupervisorList.get(0);
            mSupervisorId = supervisor.supervisorId == null ? -1 : supervisor.supervisorId;
            mSelectSupervisorTv.setTipText(StringUtils.formatString(supervisor.name));
        }
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            return;
        }

        InventorySelectDataBean selectDataBean = data.getParcelable(InventorySelectDataFragment.SELECT_DATA);
        MaterialService.MaterialInfo materialInfo = data.getParcelable(MaterialAddFragment.MATERIAL_INFO);
        switch (requestCode) {
            case INVENTORY_RESERVE_MATERIAL_ADD_REQUEST_CODE://添加物料
                if (materialInfo != null && !checkMaterial(materialInfo)) {
                    mMaterialList.add(materialInfo);
                }
                if (mMaterialList.size() > 0) {
                    mMaterialLl.setVisibility(View.VISIBLE);
                } else {
                    mMaterialLl.setVisibility(View.GONE);
                }
                mMaterialAdapter.notifyDataSetChanged();
                //计算总计花费
                computeTotalCost();
                break;
            case INVENTORY_RESERVE_MATERIAL_MODIFY_REQUEST_CODE:
                if (materialInfo != null) {
                    mMaterialList.set(mSelectPosition, materialInfo);
                    mMaterialAdapter.notifyDataSetChanged();
                }
                //计算总计花费
                computeTotalCost();
                break;
            case RESERVE_SELECT_STORAGE_REQUEST_CODE://选择仓库
                if (selectDataBean != null) {
                    mSelectStorageTv.setTipText(StringUtils.formatString(selectDataBean.name));
                    mWarehouseId = selectDataBean.id;
                    StorageService.Storage storage = (StorageService.Storage) selectDataBean.target;
                    if (storage.administrator != null) {
                        mAdministratorList.clear();
                        mAdministratorList.addAll(storage.administrator);
                    }
                    //显示仓库管理员
                    showAdministrator();
                }
                break;
            case RESERVE_SELECT_SUPERVISOR_REQUEST_CODE://选择主管
                if (selectDataBean != null) {
                    mSelectSupervisorTv.setTipText(StringUtils.formatString(selectDataBean.name));
                    mSupervisorId = selectDataBean.id;
                }
                break;
            case RESERVE_SELECT_ADMINISTRATOR_REQUEST_CODE://选择仓库管理员
                if (selectDataBean != null) {
                    mSelectAdministratorTv.setTipText(StringUtils.formatString(selectDataBean.name));
                    mAdministratorId = selectDataBean.id;
                    mAdministratorList.clear();
                    List<StorageService.Administrator> administratorList = (List<StorageService.Administrator>) selectDataBean.target;
                    if (administratorList != null && administratorList.size() > 0) {
                        mAdministratorList.clear();
                        mAdministratorList.addAll(administratorList);
                    }
                }
                break;
        }
    }

    /**
     * 计算总计花费
     */
    private void computeTotalCost() {
        float totalCost = 0;
        try {
            if (mMaterialList != null && mMaterialList.size() > 0) {
                for (MaterialService.MaterialInfo materialInfo : mMaterialList) {
                    float number = materialInfo.number == null ? 0 : materialInfo.number;
                    float price = Float.parseFloat(materialInfo.price);
                    totalCost += number * price;
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        mTotalMoneyTv.setText("¥ " + StringUtils.formatFloatCost(totalCost));
        mMaterialAdapter.notifyDataSetChanged();
    }

    /**
     * 显示仓库管理员
     */
    public void showAdministrator() {
        if (mAdministratorList != null && mAdministratorList.size() > 0) {
            mSelectAdministratorTv.setTipText(StringUtils.formatString(mAdministratorList.get(0).name));
            mAdministratorId = mAdministratorList.get(0).administratorId;
            mSelectAdministratorTv.setClickable(mAdministratorList.size() > 1);
            mSelectAdministratorTv.showIcon(mAdministratorList.size() > 1);
        } else {
            mSelectAdministratorTv.setTipText("");
            mAdministratorId = -1;
            mSelectAdministratorTv.setClickable(false);
            mSelectAdministratorTv.showIcon(false);
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
        MaterialService.MaterialInfo materialInfo = ((MaterialAdapter) adapter).getData().get(position);
        if (materialInfo != null) {
            mSelectPosition = position;
            startForResult(MaterialAddFragment.getInstance(InventoryConstant.TYPE_MATERIAL_MODIFY, materialInfo), INVENTORY_RESERVE_MATERIAL_MODIFY_REQUEST_CODE);
        }
    }

    public List<SupervisorService.Supervisor> getSupervisorList() {
        if (mSupervisorList == null) {
            mSupervisorList = new ArrayList<>();
        }
        return mSupervisorList;
    }

    public static InventoryReserveFragment getInstance() {
        InventoryReserveFragment fragment = new InventoryReserveFragment();
        return fragment;
    }
    public static InventoryReserveFragment getInstance(int type,long woId,String woCode) {
        InventoryReserveFragment fragment = new InventoryReserveFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(FROM_TYPE,type);
        bundle.putLong(WOID,woId);
        bundle.putString(WOCODE,woCode);
        fragment.setArguments(bundle);
        return fragment;
    }

}
