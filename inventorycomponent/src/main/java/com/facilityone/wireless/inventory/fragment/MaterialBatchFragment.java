package com.facilityone.wireless.inventory.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.facilityone.wireless.a.arch.ec.module.Page;
import com.facilityone.wireless.a.arch.mvp.BaseFragment;
import com.facilityone.wireless.a.arch.utils.ViewUtil;
import com.facilityone.wireless.a.arch.widget.FMBottomInputSheetBuilder;
import com.facilityone.wireless.a.arch.widget.FMWarnDialogBuilder;
import com.facilityone.wireless.basiclib.utils.StringUtils;
import com.facilityone.wireless.inventory.R;
import com.facilityone.wireless.inventory.adapter.BatchAdapter;
import com.facilityone.wireless.inventory.adapter.BatchInAdapter;
import com.facilityone.wireless.inventory.model.BatchService;
import com.facilityone.wireless.inventory.model.InventoryConstant;
import com.facilityone.wireless.inventory.model.InventorySelectDataBean;
import com.facilityone.wireless.inventory.model.MaterialService;
import com.facilityone.wireless.inventory.model.StorageService;
import com.facilityone.wireless.inventory.presenter.MaterialBatchPresenter;
import com.facilityone.wireless.inventory.widget.InventoryBottomInputSheetBuilder;
import com.joanzapata.iconify.widget.IconTextView;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;

import java.util.ArrayList;
import java.util.List;

import static com.facilityone.wireless.inventory.model.InventoryConstant.INVENTORY_INFO_BATCH_OUT;
import static com.facilityone.wireless.inventory.model.InventoryConstant.SELECT_ADMINISTRATOR;
import static com.facilityone.wireless.inventory.model.InventoryConstant.SELECT_STORAGE;

/**
 * Created by peter.peng on 2018/11/28.
 * 物资批次更改界面
 */

public class MaterialBatchFragment extends BaseFragment<MaterialBatchPresenter> implements View.OnClickListener, BatchInAdapter.OnItemClick, BaseQuickAdapter.OnItemChildClickListener, BaseQuickAdapter.OnItemClickListener {


    public static final String DATA_MATERIAL = "data_material";
    private static final String FROM_TYPE = "from_type";
    private static final String ADMINISTRATOR_LIST = "administrator_list";
    private static final int ADD_BATCH_REQUEST_CODE = 4001;
    private static final int MODIFY_BATCH_REQUEST_CODE = 4002;
    private static final int MODIFY_DESC_REQUEST_CODE = 4003;
    private static final int SELECT_RECEIVING_PERSON_REQUEST_CODE = 4004;
    private static final int SELECT_SUPERVISOR_REQUEST_CODE = 4005;
    private static final int SELECT_ADMINISTRATOR_REQUEST_CODE = 4006;
    private static final int SELECT_ORIGINAL_ADMINISTRATOR_REQUEST_CODE = 4007;
    private static final int SELECT_TARGET_ADMINISTRATOR_REQUEST_CODE = 4008;
    private static final int SELECT_TARGET_STORAGE_REQUEST_CODE = 4009;

    private LinearLayout mMaterialInfoTitleLl;//物资信息标题
    private LinearLayout mMaterialInfoLl;//物资信息隐藏部分
    private LinearLayout mPhotoLl;//物资信息图片布局
    private LinearLayout mAttachmentLl;//物资信息附件
    private IconTextView mExpandItv;//标题扩展图标
    private TextView mCodeTv;//物资编码
    private TextView mNameTv;//物资名称
    private TextView mStorageNameTv;//仓库名称
    private TextView mBrandTv;//品牌
    private TextView mShelvesTv;//货架
    private TextView mUnitTv;//单位
    private TextView mModelTv;//型号
    private TextView mPriceTv;//核定价格
    private TextView mMinNumberTv;//最低库存量
    private TextView mTotalNumberTv;//账面数量
    private TextView mReservedNumberTv;//已预订数量
    private TextView mDescTv;//备注
    private RecyclerView mPhotoRv;//图片
    private RecyclerView mAttachmentRv;//附件

    private LinearLayout mReserveCountLl;//标题预定数量
    private TextView mReserveCountTv;//标题预定数量


    private LinearLayout mBatchInLl;//入库批次
    private TextView mAddTv;//入库批次添加
    private RecyclerView mBatchInRv;//入库批次

    private LinearLayout mBatchLl;//出库、盘点批次
    private TextView mBatchNameTv;//出库、盘点批次名称
    private LinearLayout mCountLl;//批次标题物资出库、移库数量布局
    private TextView mCountNameTv;//批次标题物资出库、移库数量名字
    private TextView mCountTv;//批次标题物资出库、移库数量
    private RecyclerView mBatchRv;//出库、移库、盘点批次

    private View mInventoryMoveView;//移库布局
    private LinearLayout mOriginalStorageLl;//原仓库布局
    private TextView mOriginalStorageTv;//原仓库
    private LinearLayout mTargetStorageLl;//目标仓库布局
    private TextView mTargetStorageTv;//目标仓库
    private TextView mOriginalAdministratorTv;//原仓库管理员
    private LinearLayout mOriginalAdministratorLl;//原仓库管理员布局
    private TextView mTargetAdministratorTv;//目标仓库管理员
    private LinearLayout mTargetAdministratorLl;//目标仓库管理员布局
    private List<StorageService.Administrator> mOriginalAdministratorList;//原仓原库管理员列表
    private List<StorageService.Administrator> mTargetAdministratorList;//目标仓库管理员列表
    private long mOriginalAdministratorId = -1;//原仓原库管理员id
    private long mTargetAdministratorId = -1;//目标仓库管理员id
    private long mOriginalWarehouseId = -1;//原仓库id
    private long mTargetWarehouseId = -1;//目标仓库id

    private LinearLayout mAdministratorLl;//仓库管理员布局
    private TextView mAdministratorTv;//仓库管理员
    private List<StorageService.Administrator> mAdministratorList;//仓库管理员列表
    private long mAdministratorId = -1;//仓库管理员id
    private long mWarehouseId = -1;//仓库id

    private LinearLayout mReceivingPersonLl;//领用人布局
    private TextView mReceivingPersonTv;//领用人
    private long mReceivingPersonId = -1;//领用人id

    private LinearLayout mSupervisorLl;//主管布局
    private TextView mSupervisorTv;//主管
    private long mSupervisorId = -1;//主管id

    private LinearLayout mDescLl;//备注
    private TextView mDescAddedTv;//备注是否添加状态
    private String mDesc;//备注内容

    private BatchInAdapter mBatchInAdapter;//入库批次适配器
    private List<BatchService.Batch> mBatchInList;//入库批次数据列表

    private BatchAdapter mBatchAdapter;//出库、盘点、移库批次适配器
    private List<BatchService.Batch> mBatchList;//出库、盘点、移库批次数据列表
    private List<BatchService.Batch> mTempBatchList;//出库、盘点、移库批次数据列表


    private long mInventoryId;//库存id
    private int mType;//请求类型
    private float mBookAmount;//预定数量
    private MaterialService.MaterialInfo mMaterialInfo;//物资详情
    private int mSelectPosition;
    private List<StorageService.Storage> mStorageList;//所有仓库列表，用于判断是否有权限
    private String mWarehouseName;//仓库名称


    @Override
    public MaterialBatchPresenter createPresenter() {
        return new MaterialBatchPresenter();
    }

    @Override
    public Object setLayout() {
        return R.layout.fragment_material_batch;
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
        getMaterialInfo();
    }


    private void initData() {
        mSelectPosition = -1;
        mAdministratorList = new ArrayList<>();
        mOriginalAdministratorList = new ArrayList<>();
        mTargetAdministratorList = new ArrayList<>();
        mStorageList = new ArrayList<>();

        Bundle bundle = getArguments();
        if (bundle != null) {
            mMaterialInfo = bundle.getParcelable(DATA_MATERIAL);
            mType = bundle.getInt(FROM_TYPE, -1);
            ArrayList<StorageService.Administrator> list = bundle.getParcelableArrayList(ADMINISTRATOR_LIST);
            if (list != null) {
                mAdministratorList.addAll(list);
            }
        }

        if (mMaterialInfo != null) {
            mInventoryId = mMaterialInfo.inventoryId == null ? -1 : mMaterialInfo.inventoryId;
            mBookAmount = mMaterialInfo.bookAmount == null ? 0 : mMaterialInfo.bookAmount;
            mWarehouseId = mMaterialInfo.warehouseId == null ? 0 : mMaterialInfo.warehouseId;
            mOriginalWarehouseId = mMaterialInfo.warehouseId == null ? 0 : mMaterialInfo.warehouseId;
            mWarehouseName = StringUtils.formatString(mMaterialInfo.warehouseName);
        }
    }

    private void initView() {

        mMaterialInfoTitleLl = findViewById(R.id.material_info_title_ll);
        mMaterialInfoLl = findViewById(R.id.material_info_ll);
        mPhotoLl = findViewById(R.id.material_info_photo_ll);
        mAttachmentLl = findViewById(R.id.material_info_attachment_ll);
        mExpandItv = findViewById(R.id.material_info_expand_itv);
        mCodeTv = findViewById(R.id.material_info_code_tv);
        mNameTv = findViewById(R.id.material_info_name_tv);
        mStorageNameTv = findViewById(R.id.material_info_storage_name_tv);
        mBrandTv = findViewById(R.id.material_info_brand_tv);
        mShelvesTv = findViewById(R.id.material_info_shelves_tv);
        mUnitTv = findViewById(R.id.material_info_unit_tv);
        mModelTv = findViewById(R.id.material_info_model_tv);
        mPriceTv = findViewById(R.id.material_info_price_tv);
        mMinNumberTv = findViewById(R.id.material_info_min_number_tv);
        mTotalNumberTv = findViewById(R.id.material_info_total_number_tv);
        mReservedNumberTv = findViewById(R.id.material_info_reserved_number_tv);
        mDescTv = findViewById(R.id.material_info_desc_tv);
        mPhotoRv = findViewById(R.id.material_info_photo_rv);
        mAttachmentRv = findViewById(R.id.material_info_attachment_tv);

        mReserveCountLl = findViewById(R.id.material_info_reserve_count_ll);
        mReserveCountTv = findViewById(R.id.material_info_reserve_count_tv);

        mBatchInLl = findViewById(R.id.material_batch_in_ll);
        mAddTv = findViewById(R.id.material_batch_in_add_tv);
        mBatchInRv = findViewById(R.id.material_batch_in_rv);

        mBatchLl = findViewById(R.id.material_batch_ll);
        mBatchNameTv = findViewById(R.id.material_batch_name_tv);
        mCountLl = findViewById(R.id.material_batch_count_ll);
        mCountNameTv = findViewById(R.id.material_batch_count_name_tv);
        mCountTv = findViewById(R.id.material_batch_count_tv);
        mBatchRv = findViewById(R.id.material_batch_rv);

        mInventoryMoveView = findViewById(R.id.material_batch_move_layout);
        mOriginalStorageLl = findViewById(R.id.inventory_batch_original_storage_ll);
        mOriginalStorageTv = findViewById(R.id.inventory_batch_original_storage_name_tv);
        mTargetStorageLl = findViewById(R.id.inventory_batch_target_storage_ll);
        mTargetStorageTv = findViewById(R.id.inventory_batch_target_storage_name_ll);
        mOriginalAdministratorTv = findViewById(R.id.material_batch_original_administrator_tv);
        mOriginalAdministratorLl = findViewById(R.id.material_batch_original_administrator_ll);
        mTargetAdministratorTv = findViewById(R.id.material_batch_target_administrator_tv);
        mTargetAdministratorLl = findViewById(R.id.material_batch_target_administrator_ll);

        mAdministratorLl = findViewById(R.id.material_batch_administrator_ll);
        mAdministratorTv = findViewById(R.id.material_batch_administrator_tv);

        mReceivingPersonLl = findViewById(R.id.material_batch_receiving_person_ll);
        mReceivingPersonTv = findViewById(R.id.material_batch_receiving_person_tv);

        mSupervisorLl = findViewById(R.id.material_batch_supervisor_ll);
        mSupervisorTv = findViewById(R.id.material_batch_supervisor_tv);

        mDescLl = findViewById(R.id.material_batch_desc_ll);
        mDescAddedTv = findViewById(R.id.material_batch_desc_added_tv);


        mAttachmentRv.setNestedScrollingEnabled(false);
        mPhotoRv.setNestedScrollingEnabled(false);
        mBatchInRv.setNestedScrollingEnabled(false);
        mBatchRv.setNestedScrollingEnabled(false);

        String title = "";
        String menu = "";
        switch (mType) {
            case InventoryConstant.INVENTORY_BATCH_IN://入库
                title = getString(R.string.inventory_in_title);
                menu = getString(R.string.inventory_save);
                mReserveCountLl.setVisibility(View.GONE);
                mPhotoLl.setVisibility(View.GONE);
                mAttachmentLl.setVisibility(View.GONE);
                mBatchInLl.setVisibility(View.VISIBLE);
                mBatchLl.setVisibility(View.GONE);
                mCountLl.setVisibility(View.GONE);
                mInventoryMoveView.setVisibility(View.GONE);
                mAdministratorLl.setVisibility(View.GONE);
                mReceivingPersonLl.setVisibility(View.GONE);
                mSupervisorLl.setVisibility(View.GONE);
                mDescLl.setVisibility(View.GONE);
                break;
            case InventoryConstant.INVENTORY_INFO_BATCH_IN://入库(从物资详情进入)
                title = getString(R.string.inventory_in_title);
                menu = getString(R.string.inventory_in_title);
                mReserveCountLl.setVisibility(View.GONE);
                mPhotoLl.setVisibility(View.GONE);
                mAttachmentLl.setVisibility(View.GONE);
                mBatchInLl.setVisibility(View.VISIBLE);
                mBatchLl.setVisibility(View.GONE);
                mCountLl.setVisibility(View.GONE);
                mInventoryMoveView.setVisibility(View.GONE);
                mAdministratorLl.setVisibility(View.GONE);
                mReceivingPersonLl.setVisibility(View.GONE);
                mSupervisorLl.setVisibility(View.GONE);
                mDescLl.setVisibility(View.VISIBLE);
                if (TextUtils.isEmpty(mDesc)) {
                    mDescAddedTv.setVisibility(View.GONE);
                } else {
                    mDescAddedTv.setVisibility(View.VISIBLE);
                }
                break;
            case InventoryConstant.INVENTORY_BATCH_RESERVE_OUT://预定出库
                title = getString(R.string.inventory_out_adjusst_title);
                menu = getString(R.string.inventory_save);
                mReserveCountLl.setVisibility(View.VISIBLE);
                mPhotoLl.setVisibility(View.GONE);
                mAttachmentLl.setVisibility(View.GONE);
                mBatchInLl.setVisibility(View.GONE);
                mBatchLl.setVisibility(View.VISIBLE);
                mCountLl.setVisibility(View.VISIBLE);
                mReserveCountTv.setText(StringUtils.formatFloatCost(mBookAmount));
                mBatchNameTv.setText(R.string.inventory_material_out_batches);
                mCountNameTv.setText(R.string.inventory_material_out_quantity);
                mCountTv.setText("0.00");
                mInventoryMoveView.setVisibility(View.GONE);
                mAdministratorLl.setVisibility(View.GONE);
                mReceivingPersonLl.setVisibility(View.GONE);
                mSupervisorLl.setVisibility(View.GONE);
                mDescLl.setVisibility(View.GONE);
                break;
            case InventoryConstant.INVENTORY_BATCH_DIRECT_OUT://直接出库
                title = getString(R.string.inventory_out_adjusst_title);
                menu = getString(R.string.inventory_save);
                mReserveCountLl.setVisibility(View.GONE);
                mPhotoLl.setVisibility(View.GONE);
                mAttachmentLl.setVisibility(View.GONE);
                mBatchInLl.setVisibility(View.GONE);
                mBatchLl.setVisibility(View.VISIBLE);
                mCountLl.setVisibility(View.VISIBLE);
                mBatchNameTv.setText(R.string.inventory_material_out_batches);
                mCountNameTv.setText(R.string.inventory_material_out_quantity);
                mCountTv.setText("0.00");
                mInventoryMoveView.setVisibility(View.GONE);
                mAdministratorLl.setVisibility(View.GONE);
                mReceivingPersonLl.setVisibility(View.GONE);
                mSupervisorLl.setVisibility(View.GONE);
                mDescLl.setVisibility(View.GONE);
                break;
            case INVENTORY_INFO_BATCH_OUT://出库(从物资详情进入)
                title = getString(R.string.inventory_out_title);
                menu = getString(R.string.inventory_out_title);
                mReserveCountLl.setVisibility(View.GONE);
                mPhotoLl.setVisibility(View.GONE);
                mAttachmentLl.setVisibility(View.GONE);
                mBatchInLl.setVisibility(View.GONE);
                mBatchLl.setVisibility(View.VISIBLE);
                mCountLl.setVisibility(View.VISIBLE);
                mBatchNameTv.setText(R.string.inventory_material_out_batches);
                mCountNameTv.setText(R.string.inventory_material_out_quantity);
                mCountTv.setText("0.00");
                mInventoryMoveView.setVisibility(View.GONE);
                mAdministratorLl.setVisibility(View.VISIBLE);
                mReceivingPersonLl.setVisibility(View.VISIBLE);
                mSupervisorLl.setVisibility(View.VISIBLE);
                mDescLl.setVisibility(View.VISIBLE);
                if (TextUtils.isEmpty(mDesc)) {
                    mDescAddedTv.setVisibility(View.GONE);
                } else {
                    mDescAddedTv.setVisibility(View.VISIBLE);
                }
                showAdministrator(mAdministratorList);
                break;
            case InventoryConstant.INVENTORY_BATCH_MOVE://移库
                title = getString(R.string.inventory_material);
                menu = getString(R.string.inventory_save);
                mReserveCountLl.setVisibility(View.GONE);
                mPhotoLl.setVisibility(View.GONE);
                mAttachmentLl.setVisibility(View.GONE);
                mBatchInLl.setVisibility(View.GONE);
                mBatchLl.setVisibility(View.VISIBLE);
                mCountLl.setVisibility(View.VISIBLE);
                mBatchNameTv.setText(R.string.inventory_material_move_batches);
                mCountNameTv.setText(R.string.inventory_material_move_quantity);
                mCountTv.setText("0.00");
                mInventoryMoveView.setVisibility(View.GONE);
                mAdministratorLl.setVisibility(View.GONE);
                mReceivingPersonLl.setVisibility(View.GONE);
                mSupervisorLl.setVisibility(View.GONE);
                mDescLl.setVisibility(View.GONE);
                break;
            case InventoryConstant.INVENTORY_INFO_BATCH_MOVE://移库(从物资详情进入)
                title = getString(R.string.inventory_material);
                menu = getString(R.string.inventory_move_title);
                mReserveCountLl.setVisibility(View.GONE);
                mPhotoLl.setVisibility(View.GONE);
                mAttachmentLl.setVisibility(View.GONE);
                mBatchInLl.setVisibility(View.GONE);
                mBatchLl.setVisibility(View.VISIBLE);
                mCountLl.setVisibility(View.VISIBLE);
                mBatchNameTv.setText(R.string.inventory_material_move_batches);
                mCountNameTv.setText(R.string.inventory_material_move_quantity);
                mCountTv.setText("0.00");
                mInventoryMoveView.setVisibility(View.VISIBLE);
                mAdministratorLl.setVisibility(View.GONE);
                mReceivingPersonLl.setVisibility(View.GONE);
                mSupervisorLl.setVisibility(View.GONE);
                mDescLl.setVisibility(View.VISIBLE);
                mOriginalStorageTv.setText(StringUtils.formatString(mWarehouseName));
                if (TextUtils.isEmpty(mDesc)) {
                    mDescAddedTv.setVisibility(View.GONE);
                } else {
                    mDescAddedTv.setVisibility(View.VISIBLE);
                }
                if (mAdministratorList != null && mAdministratorList.size() > 0) {
                    mOriginalAdministratorList.addAll(mAdministratorList);
                    showOriginalAdministrator(mOriginalAdministratorList);
                }
                break;
            case InventoryConstant.INVENTORY_BATCH_CHECK://盘点
                title = getString(R.string.inventory_material);
                menu = getString(R.string.inventory_save);
                mReserveCountLl.setVisibility(View.GONE);
                mPhotoLl.setVisibility(View.GONE);
                mAttachmentLl.setVisibility(View.GONE);
                mBatchInLl.setVisibility(View.GONE);
                mBatchLl.setVisibility(View.VISIBLE);
                mCountLl.setVisibility(View.GONE);
                mBatchNameTv.setText(R.string.inventory_material_check_batches);
                mInventoryMoveView.setVisibility(View.GONE);
                mAdministratorLl.setVisibility(View.GONE);
                mReceivingPersonLl.setVisibility(View.GONE);
                mSupervisorLl.setVisibility(View.GONE);
                mDescLl.setVisibility(View.GONE);
                break;
            case InventoryConstant.INVENTORY_INFO_BATCH_CHECK://盘点(从物资详情进入)
                title = getString(R.string.inventory_material);
                menu = getString(R.string.inventory_check_title);
                mReserveCountLl.setVisibility(View.GONE);
                mPhotoLl.setVisibility(View.GONE);
                mAttachmentLl.setVisibility(View.GONE);
                mBatchInLl.setVisibility(View.GONE);
                mBatchLl.setVisibility(View.VISIBLE);
                mCountLl.setVisibility(View.GONE);
                mBatchNameTv.setText(R.string.inventory_material_check_batches);
                mInventoryMoveView.setVisibility(View.GONE);
                mAdministratorLl.setVisibility(View.GONE);
                mReceivingPersonLl.setVisibility(View.GONE);
                mSupervisorLl.setVisibility(View.GONE);
                mDescLl.setVisibility(View.GONE);
                break;
        }
        setTitle(title);
        setRightTextButton(menu, R.id.inventory_material_save_id);


        mBatchInRv.setLayoutManager(new LinearLayoutManager(getContext()));
        mBatchInList = new ArrayList<>();
        if (mMaterialInfo != null && mMaterialInfo.batch != null) {
            mBatchInList.addAll(mMaterialInfo.batch);
        }
        mBatchInAdapter = new BatchInAdapter(mBatchInList);
        mBatchInRv.setAdapter(mBatchInAdapter);
        mBatchInAdapter.setOnItemClick(this);
        mBatchInAdapter.setOnItemChildClickListener(this);


        mBatchRv.setLayoutManager(new LinearLayoutManager(getContext()));
        mBatchList = new ArrayList<>();
        mTempBatchList = new ArrayList<>();
        if (mMaterialInfo != null && mMaterialInfo.batch != null) {
            mTempBatchList.addAll(mMaterialInfo.batch);
        }
        mBatchAdapter = new BatchAdapter(mBatchList, mType);
        mBatchRv.setAdapter(mBatchAdapter);
        mBatchAdapter.setOnItemClickListener(this);


        //物资信息标题点击事件
        mMaterialInfoTitleLl.setOnClickListener(this);
        //入库添加批次按钮点击事件
        mAddTv.setOnClickListener(this);
        //目标仓库点击事件
        mTargetStorageLl.setOnClickListener(this);
        //原仓库管理员点击事件
        mOriginalAdministratorLl.setOnClickListener(this);
        //目标仓库管理员点击事件
        mTargetAdministratorLl.setOnClickListener(this);
        //仓库管理员点击事件
        mAdministratorLl.setOnClickListener(this);
        //领用人点击事件
        mReceivingPersonLl.setOnClickListener(this);
        //主管点击事件
        mSupervisorLl.setOnClickListener(this);
        //备注点击事件
        mDescLl.setOnClickListener(this);
    }


    /**
     * 联网获取物资信息
     */
    private void getMaterialInfo() {
        getPresenter().getMaterialInfoById(mInventoryId);
        switch (mType) {
            //            case InventoryConstant.INVENTORY_BATCH_IN:
            //            case InventoryConstant.INVENTORY_INFO_BATCH_IN:
            //                dismissLoading();
            //                break;
            case InventoryConstant.INVENTORY_BATCH_RESERVE_OUT:
            case InventoryConstant.INVENTORY_BATCH_DIRECT_OUT:
            case InventoryConstant.INVENTORY_INFO_BATCH_OUT:
            case InventoryConstant.INVENTORY_BATCH_MOVE:
            case InventoryConstant.INVENTORY_INFO_BATCH_MOVE:
            case InventoryConstant.INVENTORY_BATCH_CHECK:
            case InventoryConstant.INVENTORY_INFO_BATCH_CHECK:
                //获取批次列表数据
                Page page = new Page();
                page.reset();
                mBatchList.clear();
                getPresenter().getBatchData(mType, mInventoryId, page);
                break;
        }
    }


    /**
     * 联网获取物资信息成功
     *
     * @param data
     */
    public void getMaterialInfoSuccess(MaterialService.MaterialInfo data) {
        mMaterialInfo = data;
        refreshMaterialInfo();


    }

    /**
     * 刷新显示物资信息
     */
    private void refreshMaterialInfo() {
        if (mMaterialInfo == null) {
            return;
        }

        mInventoryId = mMaterialInfo.inventoryId == null ? 0 : mMaterialInfo.inventoryId;
        mWarehouseId = mMaterialInfo.warehouseId == null ? 0 : mMaterialInfo.warehouseId;
        mOriginalWarehouseId = mMaterialInfo.warehouseId == null ? 0 : mMaterialInfo.warehouseId;
        mOriginalStorageTv.setText(StringUtils.formatString(mMaterialInfo.warehouseName));

        mCodeTv.setText(StringUtils.formatString(mMaterialInfo.code));
        mNameTv.setText(StringUtils.formatString(mMaterialInfo.name));
        mStorageNameTv.setText(StringUtils.formatString(mMaterialInfo.warehouseName));
        mBrandTv.setText(StringUtils.formatString(mMaterialInfo.brand));
        mShelvesTv.setText(StringUtils.formatString(mMaterialInfo.shelves));
        mUnitTv.setText(StringUtils.formatString(mMaterialInfo.unit));
        mModelTv.setText(StringUtils.formatString(mMaterialInfo.model));
        mPriceTv.setText(StringUtils.formatString(mMaterialInfo.price));
        mMinNumberTv.setText(mMaterialInfo.minNumber == null ? "0.00" : StringUtils.formatFloatCost(mMaterialInfo.minNumber));
        mTotalNumberTv.setText(mMaterialInfo.totalNumber == null ? "0.00" : StringUtils.formatFloatCost(mMaterialInfo.totalNumber));
        mReservedNumberTv.setText(mMaterialInfo.reservedNumber == null ? "0.00" : StringUtils.formatFloatCost(mMaterialInfo.reservedNumber));
        mDescTv.setText(StringUtils.formatString(mMaterialInfo.desc));

    }

    /**
     * 联网获取物资信息失败
     */
    public void getMaterialInfoError() {

    }


    @Override
    public void onRightTextMenuClick(View view) {
        super.onRightTextMenuClick(view);
        if (view.getId() == R.id.inventory_material_save_id) {
            //保存数据返回上一级
            Bundle bundle = new Bundle();
            if (mMaterialInfo != null) {
                switch (mType) {
                    case InventoryConstant.INVENTORY_BATCH_IN://入库
                        mMaterialInfo.batch = mBatchInList;
                        bundle.putParcelable(DATA_MATERIAL, mMaterialInfo);
                        setFragmentResult(RESULT_OK, bundle);
                        pop();
                        break;
                    case InventoryConstant.INVENTORY_BATCH_RESERVE_OUT://预定出库
                    case InventoryConstant.INVENTORY_BATCH_DIRECT_OUT://直接出库
                    case INVENTORY_INFO_BATCH_OUT://出库(从物资详情进入)
                        List<BatchService.Batch> outBatchList = new ArrayList<>();
                        if (mBatchList != null && mBatchList.size() > 0) {
                            for (BatchService.Batch batch : mBatchList) {
                                if (batch.number != null && batch.number >= 0) {
                                    outBatchList.add(batch);
                                }
                            }
                        }
                        int totalNum = 0;
                        for (BatchService.Batch batch : outBatchList) {
                            totalNum += batch.number;
                        }
                        if (totalNum > mMaterialInfo.totalNumber) {
                            ToastUtils.showShort(R.string.inventory_out_exception_plss);
                            return;
                        }
                        if (mType == InventoryConstant.INVENTORY_BATCH_RESERVE_OUT && totalNum > mBookAmount) {
                            ToastUtils.showShort(R.string.inventory_out_exception_pls);
                            return;
                        }
                        if ((mType == InventoryConstant.INVENTORY_BATCH_DIRECT_OUT || mType == InventoryConstant.INVENTORY_INFO_BATCH_OUT) && totalNum > mMaterialInfo.realNumber) {
                            ToastUtils.showShort(R.string.inventory_material_out_exception_pls);
                            return;
                        }
                        mMaterialInfo.batch = outBatchList;
                        if (mType == InventoryConstant.INVENTORY_INFO_BATCH_OUT) {
                            InventoryMaterialOut(InventoryConstant.INVENTORY_MATERIAL_DIRECT_OUT);
                        } else {
                            bundle.putParcelable(DATA_MATERIAL, mMaterialInfo);
                            setFragmentResult(RESULT_OK, bundle);
                            pop();
                        }
                        break;
                    case InventoryConstant.INVENTORY_BATCH_MOVE://移库
                    case InventoryConstant.INVENTORY_INFO_BATCH_MOVE://移库(从物资详情进入)
                        List<BatchService.Batch> moveBatchList = new ArrayList<>();
                        if (mBatchList != null && mBatchList.size() > 0) {
                            for (BatchService.Batch batch : mBatchList) {
                                if (batch.number != null && batch.number >= 0) {
                                    moveBatchList.add(batch);
                                }
                            }
                        }
                        mMaterialInfo.batch = moveBatchList;
                        if (mType == InventoryConstant.INVENTORY_INFO_BATCH_MOVE) {
                            InventoryMaterialOut(InventoryConstant.INVENTORY_MATERIAL_MOVE);
                        } else {
                            bundle.putParcelable(DATA_MATERIAL, mMaterialInfo);
                            setFragmentResult(RESULT_OK, bundle);
                            pop();
                        }
                        break;
                    case InventoryConstant.INVENTORY_BATCH_CHECK://盘点
                    case InventoryConstant.INVENTORY_INFO_BATCH_CHECK://盘点(从物资详情进入)
                        if (mBatchList == null || mBatchList.size() == 0) {
                            ToastUtils.showShort(R.string.inventory_stock_no_batch_tip);
                            return;
                        }
                        List<BatchService.Batch> checkBatchList = new ArrayList<>();
                        if (mBatchList != null && mBatchList.size() > 0) {
                            for (BatchService.Batch batch : mBatchList) {
                                if (batch.number != null) {
                                    checkBatchList.add(batch);
                                }
                            }
                        }
                        mMaterialInfo.batch = checkBatchList;
                        if (mType == InventoryConstant.INVENTORY_INFO_BATCH_CHECK) {
                            inventoryMaterialCheck();
                        } else {
                            bundle.putParcelable(DATA_MATERIAL, mMaterialInfo);
                            setFragmentResult(RESULT_OK, bundle);
                            pop();
                        }
                        break;
                    case InventoryConstant.INVENTORY_INFO_BATCH_IN://入库（从物资详情进入）
                        //联网入库保存物资信息
                        mMaterialInfo.batch = mBatchInList;
                        if (mMaterialInfo.batch == null || mMaterialInfo.batch.size() == 0) {
                            ToastUtils.showShort(R.string.inventory_add_batch_pls);
                            return;
                        }
                        List<MaterialService.Inventory> inventoryList = new ArrayList<>();
                        MaterialService.Inventory inventory = new MaterialService.Inventory();
                        inventory.inventoryId = mMaterialInfo.inventoryId;
                        inventory.batch = mMaterialInfo.batch;
                        inventoryList.add(inventory);
                        getPresenter().InventoryIn(mMaterialInfo.warehouseId, mDesc, inventoryList);
                        break;
                }

            }

        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.material_info_title_ll) {//点击物资信息标题
            Boolean tag = (Boolean) v.getTag();
            if (tag == null) {
                tag = false;
            }

            tag = !tag;
            mMaterialInfoLl.setVisibility(tag ? View.VISIBLE : View.GONE);
            mExpandItv.setText(tag ? R.string.icon_arrow_up : R.string.icon_arrow_down);
            v.setTag(tag);
        } else if (v.getId() == R.id.material_batch_in_add_tv) {//点击入库添加批次按钮
            mSelectPosition = -1;
            startForResult(BatchModifyFragment.getInstance(mInventoryId, mMaterialInfo.price), ADD_BATCH_REQUEST_CODE);
        } else if (v.getId() == R.id.material_batch_desc_ll) {//点击备注
            startForResult(DescModifyFragment.getInstance(mDesc), MODIFY_DESC_REQUEST_CODE);
        } else if (v.getId() == R.id.material_batch_receiving_person_ll) {//点击领用人
            //选择领用人
            selectReceivingPerson();
        } else if (v.getId() == R.id.material_batch_supervisor_ll) {//点击主管
            //选择主管
            selectSupervisor();
        } else if (v.getId() == R.id.inventory_batch_target_storage_ll) {//点击目标仓库
            startForResult(InventorySelectDataFragment.getInstance(SELECT_STORAGE), SELECT_TARGET_STORAGE_REQUEST_CODE);
        } else if (v.getId() == R.id.material_batch_original_administrator_ll) {//点击原仓库管理员
            selectOriginalAdministrator();
        } else if (v.getId() == R.id.material_batch_target_administrator_ll) {//点击目标仓库管理员
            selectTargetAdministrator();
        } else if (v.getId() == R.id.material_batch_administrator_ll) {//点击仓库管理员
            //选择仓库管理员
            selectAdministrator();
        }
    }

    /**
     * 选择仓库管理员
     */
    private void selectAdministrator() {
        if (mWarehouseId == -1) {
            ToastUtils.showShort(R.string.inventory_no_storage_or_no_administrator);
            return;
        }
        if (mAdministratorList == null || mAdministratorList.size() == 0) {
            ToastUtils.showShort(R.string.inventory_no_administrator);
            return;
        }
        if (mAdministratorList != null && mAdministratorList.size() > 1) {
            startForResult(InventorySelectDataFragment.getInstance(SELECT_ADMINISTRATOR, mWarehouseId), SELECT_ADMINISTRATOR_REQUEST_CODE);
        }
    }


    /**
     * 选择原仓库管理员
     */
    private void selectOriginalAdministrator() {
        if (TextUtils.isEmpty(mOriginalStorageTv.getText()) || mOriginalWarehouseId == -1) {
            ToastUtils.showShort(R.string.inventory_no_storage_or_no_administrator);
            return;
        }
        if (mOriginalAdministratorList == null || mOriginalAdministratorList.size() == 0) {
            ToastUtils.showShort(R.string.inventory_no_administrator);
            return;
        }
        if (mOriginalAdministratorList != null && mOriginalAdministratorList.size() > 1) {
            startForResult(InventorySelectDataFragment.getInstance(SELECT_ADMINISTRATOR, mOriginalWarehouseId), SELECT_ORIGINAL_ADMINISTRATOR_REQUEST_CODE);
        }
    }


    /**
     * 选择原仓库管理员
     */
    private void selectTargetAdministrator() {
        if (TextUtils.isEmpty(mTargetStorageTv.getText()) || mTargetWarehouseId == -1) {
            ToastUtils.showShort(R.string.inventory_no_storage_or_no_administrator);
            return;
        }
        if (mTargetAdministratorList == null || mTargetAdministratorList.size() == 0) {
            ToastUtils.showShort(R.string.inventory_no_administrator);
            return;
        }
        if (mTargetAdministratorList != null && mTargetAdministratorList.size() > 1) {
            startForResult(InventorySelectDataFragment.getInstance(SELECT_ADMINISTRATOR, mTargetWarehouseId), SELECT_TARGET_ADMINISTRATOR_REQUEST_CODE);
        }
    }

    /**
     * 选择领用人
     */
    private void selectReceivingPerson() {
        mSupervisorTv.setText("");
        mSupervisorId = -1;
        startForResult(InventorySelectDataFragment.getInstance(InventoryConstant.SELECT_RECEIVING_PERSON), SELECT_RECEIVING_PERSON_REQUEST_CODE);
    }

    /**
     * 选择主管
     */
    private void selectSupervisor() {
        if (TextUtils.isEmpty(mReceivingPersonTv.getText()) || mReceivingPersonId == -1) {
            ToastUtils.showShort(R.string.inventory_confrim_select_need_people);
            return;
        }
        startForResult(InventorySelectDataFragment.getInstance(InventoryConstant.SELECT_SUPERVISOR, mReceivingPersonId), SELECT_SUPERVISOR_REQUEST_CODE);
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            return;
        }

        BatchService.Batch batch = data.getParcelable(BatchModifyFragment.DATA_BATCH);
        InventorySelectDataBean selectDataBean = data.getParcelable(InventorySelectDataFragment.SELECT_DATA);

        switch (requestCode) {
            case ADD_BATCH_REQUEST_CODE://入库添加批次
                if (batch != null && !checkBatch(batch)) {
                    mBatchInList.add(batch);
                    mBatchInAdapter.notifyDataSetChanged();
                }
                break;
            case MODIFY_BATCH_REQUEST_CODE://入库批次修改
                if (batch != null || !checkBatch(batch)) {
                    BatchService.Batch tempBatch = mBatchInList.get(mSelectPosition);
                    tempBatch.providerId = batch.providerId;
                    tempBatch.providerName = batch.providerName;
                    tempBatch.dueDate = batch.dueDate;
                    tempBatch.price = batch.price;
                    tempBatch.number = batch.number;
                    mBatchInAdapter.notifyDataSetChanged();
                }
                break;
            case MODIFY_DESC_REQUEST_CODE://备注修改
                mDesc = data.getString(DescModifyFragment.DESC);
                if (TextUtils.isEmpty(mDesc)) {
                    mDescAddedTv.setVisibility(View.GONE);
                } else {
                    mDescAddedTv.setVisibility(View.VISIBLE);
                }
                break;
            case SELECT_ADMINISTRATOR_REQUEST_CODE://选择仓库管理员
                if (selectDataBean != null) {
                    List<StorageService.Administrator> administratorList = (List<StorageService.Administrator>) selectDataBean.target;
                    showAdministrator(administratorList);
                }
                break;
            case SELECT_RECEIVING_PERSON_REQUEST_CODE://选择领用人
                if (selectDataBean != null) {
                    mReceivingPersonTv.setText(selectDataBean.name);
                    mReceivingPersonId = selectDataBean.id;
                }
                break;
            case SELECT_SUPERVISOR_REQUEST_CODE://选择主管
                if (selectDataBean != null) {
                    mSupervisorTv.setText(selectDataBean.name);
                    mSupervisorId = selectDataBean.id;
                }
                break;
            case SELECT_ORIGINAL_ADMINISTRATOR_REQUEST_CODE://选择原仓库管理员
                if (selectDataBean != null) {
                    List<StorageService.Administrator> administratorList = (List<StorageService.Administrator>) selectDataBean.target;
                    showOriginalAdministrator(administratorList);
                }
                break;
            case SELECT_TARGET_ADMINISTRATOR_REQUEST_CODE://选择目标仓库管理员
                if (selectDataBean != null) {
                    List<StorageService.Administrator> administratorList = (List<StorageService.Administrator>) selectDataBean.target;
                    showTargetAdministrator(administratorList);
                }
                break;
            case SELECT_TARGET_STORAGE_REQUEST_CODE://选择目标仓库
                if (selectDataBean != null) {
                    if (mOriginalStorageTv.getText().equals(selectDataBean.name) || mOriginalWarehouseId == selectDataBean.id) {
                        ToastUtils.showShort(R.string.inventory_storage_equal);
                        mTargetStorageTv.setText("");
                        mTargetWarehouseId = -1;
                        mTargetAdministratorList.clear();
                    } else {
                        mTargetStorageTv.setText(selectDataBean.name);
                        mTargetWarehouseId = selectDataBean.id;
                        StorageService.Storage storage = (StorageService.Storage) selectDataBean.target;
                        if (storage.administrator != null) {
                            mTargetAdministratorList.clear();
                            mTargetAdministratorList.addAll(storage.administrator);
                        }
                    }
                    showTargetAdministrator(mTargetAdministratorList);
                }
                break;
        }
    }

    /**
     * 显示仓库管理员
     *
     * @param administratorList
     */
    private void showAdministrator(List<StorageService.Administrator> administratorList) {
        if (administratorList != null && administratorList.size() > 0 && administratorList.get(0) != null) {
            StorageService.Administrator administrator = administratorList.get(0);
            mAdministratorTv.setText(StringUtils.formatString(administrator.name));
            mAdministratorId = administrator.administratorId;
        } else {
            mAdministratorTv.setText("");
            mAdministratorId = -1;
        }
    }


    /**
     * 显示原仓库管理员
     *
     * @param originalAdministratorList
     */
    private void showOriginalAdministrator(List<StorageService.Administrator> originalAdministratorList) {
        if (originalAdministratorList != null && originalAdministratorList.size() > 0 && originalAdministratorList.get(0) != null) {
            StorageService.Administrator administrator = originalAdministratorList.get(0);
            mOriginalAdministratorTv.setText(StringUtils.formatString(administrator.name));
            mOriginalAdministratorId = administrator.administratorId;
        } else {
            mOriginalAdministratorTv.setText("");
            mOriginalAdministratorId = -1;
        }
    }

    /**
     * 显示仓库管理员
     *
     * @param administratorList
     */
    private void showTargetAdministrator(List<StorageService.Administrator> administratorList) {
        if (administratorList != null && administratorList.size() > 0 && administratorList.get(0) != null) {
            StorageService.Administrator administrator = administratorList.get(0);
            mTargetAdministratorTv.setText(StringUtils.formatString(administrator.name));
            mTargetAdministratorId = administrator.administratorId;
        } else {
            mTargetAdministratorTv.setText("");
            mTargetAdministratorId = -1;
        }
    }

    /**
     * 物资出库、移库
     */
    private void InventoryMaterialOut(int type) {
        int totalNum = 0;
        if (mMaterialInfo.batch != null) {
            for (BatchService.Batch batch : mMaterialInfo.batch) {
                totalNum += batch.number;
            }
        }

        if (totalNum <= 0) {
            if (type == InventoryConstant.INVENTORY_MATERIAL_DIRECT_OUT) {
                ToastUtils.showShort(R.string.inventory_select_material_out_and_amount);
            } else if (type == InventoryConstant.INVENTORY_MATERIAL_MOVE) {
                ToastUtils.showShort(R.string.inventory_select_material_and_amount);
            }
            return;
        }

        if (type == InventoryConstant.INVENTORY_MATERIAL_DIRECT_OUT) {
            if (mAdministratorId == -1) {
                ToastUtils.showShort(R.string.inventory_useless_administrator);
                return;
            }
            if (TextUtils.isEmpty(mReceivingPersonTv.getText()) || mReceivingPersonId == -1) {
                ToastUtils.showShort(R.string.inventory_select_need_person_tip);
                return;
            }

            if (mMaterialInfo.batch == null || mMaterialInfo.batch.size() == 0) {
                ToastUtils.showShort(R.string.inventory_select_material_out_and_amount);
                return;
            }
        } else if (type == InventoryConstant.INVENTORY_MATERIAL_MOVE) {
            if (TextUtils.isEmpty(mTargetStorageTv.getText()) || mTargetWarehouseId == -1) {
                ToastUtils.showShort(R.string.inventory_target_storage_empty_hint);
                return;
            }

            if (TextUtils.isEmpty(mOriginalStorageTv.getText()) || mOriginalWarehouseId == -1) {
                ToastUtils.showShort(R.string.inventory_material_storage_none_);
                return;
            }

            if (mOriginalWarehouseId == mTargetWarehouseId) {
                ToastUtils.showShort(R.string.inventory_storage_equal);
                return;
            }

            if (mOriginalAdministratorId == -1 || mTargetAdministratorId == -1) {
                ToastUtils.showShort(R.string.inventory_useless_administrator);
                return;
            }


        }

        List<MaterialService.Inventory> inventoryList = new ArrayList<>();
        MaterialService.Inventory inventory = new MaterialService.Inventory();
        inventory.inventoryId = mMaterialInfo.inventoryId;
        List<BatchService.Batch> batchList = new ArrayList<>();
        for (BatchService.Batch batch : mMaterialInfo.batch) {
            BatchService.Batch tempBatch = new BatchService.Batch();
            tempBatch.batchId = batch.batchId;
            tempBatch.amount = batch.number;
            batchList.add(tempBatch);
        }
        inventory.batch = batchList;
        inventoryList.add(inventory);

        MaterialService.MaterialOutRequest request = new MaterialService.MaterialOutRequest();
        request.inventory = inventoryList;
        request.type = type;
        request.remarks = mDesc;
        if (type == InventoryConstant.INVENTORY_MATERIAL_DIRECT_OUT) {
            request.warehouseId = mWarehouseId;
            request.administrator = mAdministratorId;
            request.receivingPersonId = mReceivingPersonId;
            if (mSupervisorId != -1) {
                request.supervisor = mSupervisorId;
            }
        } else if (type == InventoryConstant.INVENTORY_MATERIAL_MOVE) {
            request.warehouseId = mOriginalWarehouseId;
            request.targetWarehouseId = mTargetWarehouseId;
            request.administrator = mOriginalAdministratorId;
            request.targetAdministrator = mTargetAdministratorId;
        }

        getPresenter().InventoryMaterialOut(request);

    }

    /**
     * 物资盘点
     */
    private void inventoryMaterialCheck() {
        List<MaterialService.Inventory> inventoryList = new ArrayList<>();
        MaterialService.Inventory inventory = new MaterialService.Inventory();
        inventory.inventoryId = mMaterialInfo.inventoryId;
        List<BatchService.Batch> batchList = new ArrayList<>();
        for (BatchService.Batch batch : mMaterialInfo.batch) {
            BatchService.Batch tempBatch = new BatchService.Batch();
            tempBatch.batchId = batch.batchId;
            tempBatch.inventoryNumber = batch.amount;
            tempBatch.number = batch.number;
            if (!TextUtils.isEmpty(batch.desc)) {
                tempBatch.desc = batch.desc;
            }
            batchList.add(tempBatch);
        }
        inventory.batch = batchList;
        inventoryList.add(inventory);

        MaterialService.MaterialCheckRequest request = new MaterialService.MaterialCheckRequest();
        request.inventory = inventoryList;
        request.warehouseId = mWarehouseId;
        getPresenter().inventoryMaterialCheck(request);
    }

    /**
     * 检查该批次是否添加过
     *
     * @param batch
     * @return
     */
    private boolean checkBatch(BatchService.Batch batch) {
        for (int i = 0; i < mBatchInList.size(); i++) {
            BatchService.Batch tempBatch = mBatchInList.get(i);
            if (tempBatch.number.equals(batch.number)
                    && tempBatch.providerName.equals(batch.providerName)
                    && tempBatch.price.equals(batch.price)
                    && ((tempBatch.dueDate == null && batch.dueDate == null)
                    || (tempBatch.dueDate != null && batch.dueDate != null
                    && tempBatch.dueDate.equals(batch.dueDate)))) {
                if (i != mSelectPosition) {
                    ToastUtils.showShort(R.string.inventory_material_add_batch_existed);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 当删除批次item时回调
     *
     * @param batch
     * @param position
     */
    @Override
    public void onBtnDelete(final BatchService.Batch batch, int position) {
        new FMWarnDialogBuilder(getContext())
                .setIconVisible(false)
                .setSureBluBg(true)
                .setTitle(R.string.inventory_remind)
                .setSure(R.string.inventory_sure)
                .setTip(R.string.inventory_delete_batch)
                .addOnBtnSureClickListener(new FMWarnDialogBuilder.OnBtnClickListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, View view) {
                        dialog.dismiss();
                        mBatchInList.remove(batch);
                        mBatchInAdapter.notifyDataSetChanged();
                    }
                }).create(R.style.fmDefaultWarnDialog).show();
    }

    /**
     * 当点击入库批次item时回调
     *
     * @param adapter
     * @param view
     * @param position
     */
    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        BatchService.Batch batch = ((BatchInAdapter) adapter).getData().get(position);
        if (batch != null) {
            mSelectPosition = position;
            startForResult(BatchModifyFragment.getInstance(batch, mInventoryId), MODIFY_BATCH_REQUEST_CODE);
        }

    }

    /**
     * 当点击出库、盘点批次item时回调
     *
     * @param adapter
     * @param view
     * @param position
     */
    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, final int position) {
        final InventoryBottomInputSheetBuilder builder = new InventoryBottomInputSheetBuilder(getContext());
        builder.setOnSaveInputListener(new FMBottomInputSheetBuilder.OnInputBtnClickListener() {
            @Override
            public void onSaveClick(QMUIBottomSheet dialog, String desc) {
                KeyboardUtils.hideSoftInput(MaterialBatchFragment.this.getActivity());
                dialog.dismiss();
                try {
                    float num = Float.parseFloat(builder.getNum());
                    if (mType != InventoryConstant.INVENTORY_BATCH_CHECK && mType != InventoryConstant.INVENTORY_INFO_BATCH_CHECK && num < 0) {
                        return;
                    }
                    switch (mType) {
                        case InventoryConstant.INVENTORY_BATCH_RESERVE_OUT:
                            if (num > mBatchList.get(position).amount || num > mMaterialInfo.totalNumber) {
                                ToastUtils.showShort(R.string.inventory_out_exception_plss);
                                return;
                            }
                            if (num > mBookAmount) {
                                ToastUtils.showShort(R.string.inventory_out_exception_pls);
                                return;
                            }
                            break;
                        case InventoryConstant.INVENTORY_BATCH_DIRECT_OUT:
                        case INVENTORY_INFO_BATCH_OUT:
                            if (num > mBatchList.get(position).amount || num > mMaterialInfo.totalNumber) {
                                ToastUtils.showShort(R.string.inventory_out_exception_plss);
                                return;
                            }
                            if (num > mMaterialInfo.realNumber) {
                                ToastUtils.showShort(R.string.inventory_material_out_exception_pls);
                                return;
                            }
                            break;
                        case InventoryConstant.INVENTORY_BATCH_MOVE:
                        case InventoryConstant.INVENTORY_INFO_BATCH_MOVE:
                            if (num > mBatchList.get(position).amount || num > mMaterialInfo.totalNumber) {
                                ToastUtils.showShort(R.string.inventory_material_move_exception_pls);
                                return;
                            }
                            break;

                    }
                    mBatchList.get(position).adjustNumber = num - mBatchList.get(position).amount;
                    mBatchList.get(position).number = num;
                    if (!TextUtils.isEmpty(desc)) {
                        mBatchList.get(position).desc = desc;
                    }
                    mBatchAdapter.notifyDataSetChanged();
                    //刷新出库批次标题出库数量
                    refreshBatchOutNum();
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onLeftClick(QMUIBottomSheet dialog, String input) {
                dialog.dismiss();
            }

            @Override
            public void onRightClick(QMUIBottomSheet dialog, String input) {
                dialog.dismiss();
            }
        });
        QMUIBottomSheet dialog = builder.build();
        builder.getSingleBtn().setVisibility(View.VISIBLE);
        builder.setTitle(R.string.inventory_material_editor_number);
        String desc = "";
        switch (mType) {
            case InventoryConstant.INVENTORY_BATCH_RESERVE_OUT:
            case InventoryConstant.INVENTORY_BATCH_DIRECT_OUT:
            case InventoryConstant.INVENTORY_INFO_BATCH_OUT:
                builder.setShowDesc(false);
                desc = getString(R.string.inventory_material_input_out_number);
                break;
            case InventoryConstant.INVENTORY_BATCH_MOVE:
            case InventoryConstant.INVENTORY_INFO_BATCH_MOVE:
                builder.setShowDesc(false);
                desc = getString(R.string.inventory_material_input_move_number);
                break;
            case InventoryConstant.INVENTORY_BATCH_CHECK:
            case InventoryConstant.INVENTORY_INFO_BATCH_CHECK:
                builder.setShowTipDesc(true);
                builder.setShowDesc(true);
                builder.setDescHint(R.string.inventory_material_check_hint);
                desc = getString(R.string.inventory_material_input_check_number);
                break;
        }
        builder.setNumberHint(desc);
        if (mBatchList.get(position).number != null) {
            builder.setNum(mBatchList.get(position).number + "");
        }
        builder.setDesc(mBatchList.get(position).desc);
        builder.getSingleBtn().setBackgroundResource(R.drawable.btn_common_bg_selector);
        builder.setSingleNeedInput(false);
        builder.setBtnText(R.string.inventory_filter_sure);
        ViewUtil.setNumberPoint(builder.getDescEt(), 2);
        dialog.show();
    }

    /**
     * 刷新出库、移库批次标题出库、移库数量
     */
    private void refreshBatchOutNum() {
        float num = 0;
        if (mBatchList != null) {
            for (BatchService.Batch batch : mBatchList) {
                if (batch.number != null) {
                    num += batch.number;
                }
            }
        }

        mCountTv.setText(StringUtils.formatFloatCost(num));
    }


    public List<StorageService.Storage> getStorageList() {
        if (mStorageList == null) {
            mStorageList = new ArrayList<>();
        }
        return mStorageList;
    }

    public List<BatchService.Batch> getBatchList() {
        if (mBatchList == null) {
            mBatchList = new ArrayList<>();
        }
        return mBatchList;
    }


    /**
     * 刷新批次数据
     */
    public void refreshBatchData() {
        if (mBatchList != null && mBatchList.size() > 0) {
            if (mType == InventoryConstant.INVENTORY_BATCH_CHECK || mType == InventoryConstant.INVENTORY_INFO_BATCH_CHECK) {
                if (mTempBatchList == null || mTempBatchList.size() <= 0) {
                    for (int i = 0; i < mBatchList.size(); i++) {
                        BatchService.Batch batch = mBatchList.get(i);
                        batch.number = batch.amount;
                    }
                }
            }

            if (mTempBatchList != null) {
                for (int i = 0; i < mBatchList.size(); i++) {
                    for (int j = 0; j < mTempBatchList.size(); j++) {
                        BatchService.Batch batch = mBatchList.get(i);
                        BatchService.Batch tempBatch = mTempBatchList.get(j);
                        if (batch.batchId.equals(tempBatch.batchId)) {
                            batch.number = tempBatch.number;
                            batch.adjustNumber = tempBatch.adjustNumber;
                            batch.desc = tempBatch.desc;
                        }
                    }
                }
            }
            refreshBatchOutNum();
            mBatchRv.setVisibility(View.VISIBLE);
        } else {
            mBatchRv.setVisibility(View.GONE);
        }

        mBatchAdapter.notifyDataSetChanged();
    }

    public static MaterialBatchFragment getInstance(MaterialService.MaterialInfo material, int type) {
        MaterialBatchFragment fragment = new MaterialBatchFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(DATA_MATERIAL, material);
        bundle.putInt(FROM_TYPE, type);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static MaterialBatchFragment getInstance(MaterialService.MaterialInfo material, ArrayList<StorageService.Administrator> administratorList, int type) {
        MaterialBatchFragment fragment = new MaterialBatchFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(DATA_MATERIAL, material);
        bundle.putParcelableArrayList(ADMINISTRATOR_LIST, administratorList);
        bundle.putInt(FROM_TYPE, type);
        fragment.setArguments(bundle);
        return fragment;
    }
}
