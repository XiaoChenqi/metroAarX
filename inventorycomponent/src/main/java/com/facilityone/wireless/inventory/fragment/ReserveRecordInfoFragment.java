package com.facilityone.wireless.inventory.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.facilityone.wireless.a.arch.mvp.BaseFragment;
import com.facilityone.wireless.a.arch.widget.CustomContentItemView;
import com.facilityone.wireless.a.arch.widget.EditNumberView;
import com.facilityone.wireless.basiclib.utils.DateUtils;
import com.facilityone.wireless.basiclib.utils.StringUtils;
import com.facilityone.wireless.componentservice.inventory.InventoryService;
import com.facilityone.wireless.componentservice.workorder.WorkorderService;
import com.facilityone.wireless.inventory.R;
import com.facilityone.wireless.inventory.adapter.RecordHistoryAdapter;
import com.facilityone.wireless.inventory.adapter.ReserveInfoMaterialAdapter;
import com.facilityone.wireless.inventory.model.BatchService;
import com.facilityone.wireless.inventory.model.InventoryConstant;
import com.facilityone.wireless.inventory.model.InventoryHelper;
import com.facilityone.wireless.inventory.model.InventorySelectDataBean;
import com.facilityone.wireless.inventory.model.MaterialService;
import com.facilityone.wireless.inventory.model.ReserveService;
import com.facilityone.wireless.inventory.presenter.ReserveRecordInfoPresenter;
import com.luojilab.component.componentlib.router.Router;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static com.facilityone.wireless.inventory.model.InventoryConstant.SELECT_RECEIVING_PERSON;

/**
 * Created by peter.peng on 2018/12/4.
 * 物资预定记录详情页面
 */

public class ReserveRecordInfoFragment extends BaseFragment<ReserveRecordInfoPresenter> implements View.OnClickListener, BaseQuickAdapter.OnItemClickListener {

    private static final String FROM_TYPE = "from_type";
    private static final String FROM_MESSAGE = "from_message";
    private static final String ACTIVITY_ID = "activity_id";
    private static final String STATUS = "status";
    private static final String WORKORDER_STATUS = "workorder_status";
    private static final int RESERVE_OUT_SELECT_RECEIVING_PERSON_REQUEST_CODE = 4001;
    private static final int RESERVER_OUT_REQUEST_CODE = 4002;
    private static final int SELECT_ADMINISTRATOR_REQUEST_CODE = 4003;
    private static final int SELECT_RESERVATION_PERSON_REQUEST_CODE = 4004;
    private static final int SELECT_SUPERVISOR_REQUEST_CODE = 4005;

    private TextView mReservationCodeTv;//预订单号
    private TextView mReservationDateTv;//预订时间
    private TextView mStorageNameTv;//仓库名称
    private TextView mRelatedWorkorderCodeTv;//关联工单
    private LinearLayout mBasicDescLl;//备注
    private TextView mDescTv;//备注
    private TextView mStatusTv;
    private CustomContentItemView mAdministratorTv;//仓库管理员
    private CustomContentItemView mReservationPersonTv;//预定人
    private CustomContentItemView mSupervisorTv;//主管
    private LinearLayout mReceivingPersonLl;//领用人
    private CustomContentItemView mReceivingPersonTv;//领用人
    private LinearLayout mDescLl;//备注
    private EditNumberView mDescEnv;//备注
    private RecyclerView mMaterialRv;//物料
    private TextView mTotalMoneyTv;//总计
    private LinearLayout mRelatedWorkorderLl;//关联工单
    private LinearLayout mHistoryLl;//操作记录
    private RecyclerView mHistoryRv;//操作记录
    private LinearLayout mCheckAllLl;//查看全部（操作记录）
    private LinearLayout mReasonLl;//理由
    private TextView mReasonTv;//理由

    private int mFromType = -1;
    private long mActivityid = -1;//预订单id
    private long mReceivingPersonId = -1;//领用人id
    private long mWarehouseId = -1;//仓库id
    private int mStatus = -1;//预定详情状态
    private int mWorkorderStatus = -1;//工单状态

    private List<MaterialService.MaterialInfo> mMaterialList;
    private ReserveInfoMaterialAdapter mMaterialAdapter;
    private int mSelectPosition = -1;//物料被点击的位置

    private List<ReserveService.RecordHistory> mRecordHistoryList;
    private ArrayList<ReserveService.RecordHistory> mTempRecordHistoryList;
    private RecordHistoryAdapter mRecordHistoryAdapter;

    private MaterialService.MaterialOutRequest mRequest;//出库请求体

    private long mWoId;//关联工单 ID
    private String mWoCode;//关联工单编号
    private long mAdministratorId;//仓库管理员id
    private long mReservePersonId;//预定人ID
    private long mSupervisorId;//主管id
    private LinearLayout mBasicLl;

    private Boolean fromMessage; //从消息传过来


    @Override
    public ReserveRecordInfoPresenter createPresenter() {
        return new ReserveRecordInfoPresenter();
    }

    @Override
    public Object setLayout() {
        return R.layout.fragment_reserve_record_info;
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
        getReserveRecordInfo();
    }

    private void initData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            mFromType = bundle.getInt(FROM_TYPE, -1);
            mActivityid = bundle.getLong(ACTIVITY_ID, -1);
            mStatus = bundle.getInt(STATUS, -1);
            mWorkorderStatus = bundle.getInt(WORKORDER_STATUS, -1);
            fromMessage =  bundle.getBoolean(FROM_MESSAGE,false);
        }

        mRequest = new MaterialService.MaterialOutRequest();
    }


    private void initView() {
        setTitle(R.string.inventory_reserve_details_title);

        mBasicLl = findViewById(R.id.reserve_record_info_basic_ll);
        mReservationCodeTv = findViewById(R.id.reserve_record_info_reservation_code_tv);
        mReservationDateTv = findViewById(R.id.reserve_record_info_reservation_date_tv);
        mStorageNameTv = findViewById(R.id.reserve_record_info_storage_name_tv);
        mRelatedWorkorderCodeTv = findViewById(R.id.reserve_record_info_related_workorder_code_tv);
        mBasicDescLl = findViewById(R.id.reserve_record_info_basic_desc_ll);
        mDescTv = findViewById(R.id.reserve_record_info_desc_tv);
        mStatusTv = findViewById(R.id.reserve_record_info_status_tv);
        mAdministratorTv = findViewById(R.id.reserve_record_info_administrator_tv);
        mReservationPersonTv = findViewById(R.id.reserve_record_info_reservation_person_tv);
        mSupervisorTv = findViewById(R.id.reserve_record_info_supervisor_tv);
        mReceivingPersonLl = findViewById(R.id.reserve_record_info_receiving_person_ll);
        mReceivingPersonTv = findViewById(R.id.reserve_record_info_receiving_person_tv);
        mDescLl = findViewById(R.id.reserve_record_info_desc_ll);
        mDescEnv = findViewById(R.id.reserve_record_info_desc_env);
        mMaterialRv = findViewById(R.id.reserve_record_info_material_rv);
        mTotalMoneyTv = findViewById(R.id.reserve_record_info_total_money_tv);
        mRelatedWorkorderLl = findViewById(R.id.reserve_record_info_related_workorder_ll);
        mHistoryLl = findViewById(R.id.reserve_record_info_history_ll);
        mHistoryRv = findViewById(R.id.reserve_record_info_history_rv);
        mCheckAllLl = findViewById(R.id.reserve_record_info_check_all_ll);
        mReasonLl = findViewById(R.id.reserve_record_info_reason_ll);
        mReasonTv = findViewById(R.id.reserve_record_info_reason_tv);

        mBasicLl.requestFocus();
        mMaterialRv.setNestedScrollingEnabled(false);
        mHistoryRv.setNestedScrollingEnabled(false);

        mMaterialRv.setLayoutManager(new LinearLayoutManager(getContext()));
        mMaterialList = new ArrayList<>();
        mMaterialAdapter = new ReserveInfoMaterialAdapter(mFromType, mMaterialList);
        mMaterialRv.setAdapter(mMaterialAdapter);
        mMaterialAdapter.setOnItemClickListener(this);

        mHistoryRv.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecordHistoryList = new ArrayList<>();
        mTempRecordHistoryList = new ArrayList<>();
        mRecordHistoryAdapter = new RecordHistoryAdapter(mRecordHistoryList);
        mHistoryRv.setAdapter(mRecordHistoryAdapter);

        //领用人设置点击事件
        mReceivingPersonTv.setOnClickListener(this);
        //关联工单点击事件
        mRelatedWorkorderLl.setOnClickListener(this);
        //查看全部点击事件
        mCheckAllLl.setOnClickListener(this);


        if (!fromMessage){
            switch (mFromType) {
                case InventoryConstant.INVENTORY_OUT://出库
                    removeRightView();
                    setMoreMenu();
                    mBasicDescLl.setVisibility(View.GONE);
                    mReceivingPersonLl.setVisibility(View.VISIBLE);
                    mDescLl.setVisibility(View.VISIBLE);
                    break;
                case InventoryConstant.INVENTORY_MY://我的预定
                    removeRightView();
                    mBasicDescLl.setVisibility(View.VISIBLE);
                    mReceivingPersonLl.setVisibility(View.GONE);
                    mDescLl.setVisibility(View.GONE);
                    if (mStatus == InventoryConstant.RESERVE_STATUS_VERIFY_WAIT) {
                        setRightTextButton(R.string.inventory_reserve_cancel, R.id.inventory_reserve_cancel_id);
                    }
                    break;
                case InventoryService.TYPE_FROM_WORKORDER://来自工单
                    removeRightView();
                    mBasicDescLl.setVisibility(View.VISIBLE);
                    mReceivingPersonLl.setVisibility(View.GONE);
                    mDescLl.setVisibility(View.GONE);
                    if (mStatus == InventoryConstant.RESERVE_STATUS_VERIFY_WAIT) {
                        if (mWorkorderStatus == InventoryConstant.WORK_STATUS_CREATED) {
                            setRightTextButton(R.string.inventory_save, R.id.inventory_reserve_save_id);
                            mAdministratorTv.showRed(true);
                            mAdministratorTv.showIcon(true);
                            mReservationPersonTv.showRed(true);
                            mReservationPersonTv.showIcon(true);
                            mSupervisorTv.showRed(true);
                            mSupervisorTv.showIcon(true);
                            mAdministratorTv.setOnClickListener(this);
                            mReservationPersonTv.setOnClickListener(this);
                            mSupervisorTv.setOnClickListener(this);
                        } else if (mWorkorderStatus == InventoryConstant.WORK_STATUS_PROCESS) {
                            setRightTextButton(R.string.inventory_reserve_cancel, R.id.inventory_reserve_cancel_id);
                        }
                    }
                    break;
                case InventoryConstant.INVENTORY_APPROVAL_WAIT://库存审核(待审核)
                    removeRightView();
                    mBasicDescLl.setVisibility(View.VISIBLE);
                    mReceivingPersonLl.setVisibility(View.GONE);
                    mDescLl.setVisibility(View.GONE);
                    setRightTextButton(R.string.inventory_menu_approval, R.id.inventory_reserve_approval_id);
                    break;
                case InventoryConstant.INVENTORY_APPROVALED://库存审核(已审核)
                    removeRightView();
                    mBasicDescLl.setVisibility(View.VISIBLE);
                    mReceivingPersonLl.setVisibility(View.GONE);
                    mDescLl.setVisibility(View.GONE);
                    break;
            }
        }

    }

    @Override
    public void onMoreMenuClick(View view) {
        super.onMoreMenuClick(view);
        getPresenter().onMoreMenuClick(mActivityid);
    }

    @Override
    public void onRightTextMenuClick(View view) {
        super.onRightTextMenuClick(view);
        if (view.getId() == R.id.inventory_reserve_cancel_id) {
            getPresenter().ShowMaterialCancelOutView(mActivityid, InventoryConstant.INVENTORY_APPROVAL_CANCEL_BOOK);
        } else if (view.getId() == R.id.inventory_reserve_approval_id) {
            getPresenter().ShowInventoryApprovalView(mActivityid);
        } else if (view.getId() == R.id.inventory_reserve_save_id) {
            if (TextUtils.isEmpty(mAdministratorTv.getTipText()) || mAdministratorId == -1) {
                ToastUtils.showShort(R.string.inventory_useless_administrator);
                return;
            }
            if (TextUtils.isEmpty(mReservationPersonTv.getTipText()) || mReservePersonId == -1) {
                ToastUtils.showShort(R.string.inventory_select_reservation_person_tip);
                return;
            }
            if (TextUtils.isEmpty(mSupervisorTv.getTipText()) || mSupervisorId == -1) {
                ToastUtils.showShort(R.string.inventory_select_supervisor_tip);
                return;
            }
            //修改预订单人员
            getPresenter().editReservationPerson(mActivityid, mAdministratorId, mReservePersonId, mSupervisorId);
        }
    }

    /**
     * 联网获取预定详情信息
     */
    private void getReserveRecordInfo() {
        getPresenter().getReserveRecordInfo(mActivityid);
    }


    /**
     * 联网获取预订记录详情成功后回调
     *
     * @param data
     */
    public void getReserveRecordInfoSuccess(ReserveService.ReserveRecordInfoBean data) {
        mActivityid = data.activityId == null ? -1 : data.activityId;
        mWarehouseId = data.warehouseId == null ? -1 : data.warehouseId;
        mAdministratorId = data.administrator == null ? -1 : data.administrator;
        mReservePersonId = data.reservationPersonId == null ? -1 : data.reservationPersonId;
        mSupervisorId = data.supervisor == null ? -1 : data.supervisor;
        mStatus = data.status;

        if (fromMessage){
            removeRightView();
            switch (data.status) {
                case InventoryConstant.RESERVE_STATUS_VERIFY_WAIT:
                    mBasicDescLl.setVisibility(View.VISIBLE);
                    mReceivingPersonLl.setVisibility(View.GONE);
                    mDescLl.setVisibility(View.GONE);
                    setRightTextButton(R.string.inventory_menu_approval, R.id.inventory_reserve_approval_id);
                    break;
                case InventoryConstant.RESERVE_STATUS_VERIFY_PASS:
                    setMoreMenu();
                    mBasicDescLl.setVisibility(View.GONE);
                    mReceivingPersonLl.setVisibility(View.VISIBLE);
                    mDescLl.setVisibility(View.VISIBLE);
                    break;
                case InventoryConstant.RESERVE_STATUS_VERIFY_BACK:
                    mDescLl.setVisibility(View.GONE);
                    mReceivingPersonLl.setVisibility(View.GONE);
                    break;
                case InventoryConstant.RESERVE_STATUS_DELIVERIED:
                    mReceivingPersonLl.setVisibility(View.GONE);
                    mDescLl.setVisibility(View.GONE);
                    break;
            }
        }

        mWoId = data.woId == null ? -1 : data.woId;
        mWoCode = StringUtils.formatString(data.woCode);
        mReservationCodeTv.setText(StringUtils.formatString(data.reservationCode));
        if (data.reservationDate != null) {
            mReservationDateTv.setText(TimeUtils.date2String(new Date(data.reservationDate), DateUtils.SIMPLE_DATE_FORMAT_YMD));
        }
        mStorageNameTv.setText(StringUtils.formatString(data.warehouseName));
        mAdministratorTv.setTipText(StringUtils.formatString(data.administratorName));
        mReservationPersonTv.setTipText(StringUtils.formatString(data.reservationPersonName));
        mSupervisorTv.setTipText(StringUtils.formatString(data.supervisorName));
        mDescEnv.setDesc(StringUtils.formatString(data.remarks));
        if (data.status != null) {
            mStatusTv.setVisibility(View.VISIBLE);
            mStatusTv.setText(StringUtils.formatString(InventoryHelper.getInventoryStatusMap(getContext()).get(data.status)));
            int resId = R.drawable.inventory_tag_fill_orange_background;
            switch (data.status) {
                case InventoryConstant.RESERVE_STATUS_VERIFY_WAIT:
                    resId = R.drawable.inventory_tag_fill_orange_background;
                    break;
                case InventoryConstant.RESERVE_STATUS_VERIFY_PASS:
                    resId = R.drawable.inventory_tag_fill_blue_background;
                    break;
                case InventoryConstant.RESERVE_STATUS_VERIFY_BACK:
                    resId = R.drawable.inventory_tag_fill_red_background;
                    break;
                case InventoryConstant.RESERVE_STATUS_DELIVERIED:
                case InventoryConstant.RESERVE_STATUS_CANCEL:
                case InventoryConstant.RESERVE_STATUS_CANCEL_BOOK:
                    resId = R.drawable.inventory_tag_fill_grey_background;
                    break;
            }
            mStatusTv.setBackgroundResource(resId);
        } else {
            mStatusTv.setVisibility(View.GONE);
            mStatusTv.setText("");
        }

        mMaterialList.clear();
        if (data.materials != null && data.materials.size() > 0) {
            List<MaterialService.MaterialInfo> materialInfoList = getPresenter().MaterialList2MaterInfoList(data.materials);
            mMaterialList.addAll(materialInfoList);
            mMaterialAdapter.setStatus(data.status);
            mMaterialAdapter.notifyDataSetChanged();
        }

//        mRelatedWorkorderLl.setVisibility(TextUtils.isEmpty(data.woCode) ? View.GONE : View.VISIBLE);

        //计算总计花费
        computeTotalCost();

        //历史记录
        mRecordHistoryList.clear();
        if (data.histories != null && data.histories.size() > 0) {
            mHistoryLl.setVisibility(View.VISIBLE);
            mTempRecordHistoryList.addAll(data.histories);
            if (mTempRecordHistoryList.size() > 2) {
                mRecordHistoryList.add(mTempRecordHistoryList.get(mTempRecordHistoryList.size() - 1));
                mRecordHistoryList.add(mTempRecordHistoryList.get(mTempRecordHistoryList.size() - 2));
                mCheckAllLl.setVisibility(View.VISIBLE);
            } else if (mTempRecordHistoryList.size() == 2) {
                mRecordHistoryList.add(mTempRecordHistoryList.get(mTempRecordHistoryList.size() - 1));
                mRecordHistoryList.add(mTempRecordHistoryList.get(mTempRecordHistoryList.size() - 2));
                mCheckAllLl.setVisibility(View.GONE);
            } else {
                mRecordHistoryList.addAll(mTempRecordHistoryList);
                mCheckAllLl.setVisibility(View.GONE);
            }
            mRecordHistoryAdapter.notifyDataSetChanged();
        } else {
            mHistoryLl.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(data.woCode)) {
            mRelatedWorkorderLl.setVisibility(View.VISIBLE);
            mRelatedWorkorderCodeTv.setText(StringUtils.formatString(data.woCode));
        } else {
            mRelatedWorkorderLl.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(data.remarks)) {
            mBasicDescLl.setVisibility(View.VISIBLE);
            mDescTv.setText(StringUtils.formatString(data.remarks));
        } else {
            mBasicDescLl.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(data.operateDesc)) {
            mReasonLl.setVisibility(View.VISIBLE);
            mReasonTv.setText(StringUtils.formatString(data.operateDesc));
        } else {
            mReasonLl.setVisibility(View.GONE);
        }
    }

    /**
     * 联网获取预定记录详情失败后回调
     */
    public void getReserveRecordInfoError() {

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.reserve_record_info_receiving_person_tv) {//点击领用人
            startForResult(InventorySelectDataFragment.getInstance(SELECT_RECEIVING_PERSON), RESERVE_OUT_SELECT_RECEIVING_PERSON_REQUEST_CODE);
        } else if (v.getId() == R.id.reserve_record_info_related_workorder_ll) {//关联工单
            if (!TextUtils.isEmpty(mWoCode) && mWoId != -1) {
                Router router = Router.getInstance();
                WorkorderService workorderService = (WorkorderService) router.getService(WorkorderService.class.getSimpleName());
                if (workorderService != null) {
                    BaseFragment workorderInfoFragment = workorderService.getWorkorderInfoFragment(-1, mWoCode, mWoId);
                    start(workorderInfoFragment);
                }
            }
        } else if (v.getId() == R.id.reserve_record_info_administrator_tv) {//选择仓库管理员
            startForResult(InventorySelectDataFragment.getInstance(InventoryConstant.SELECT_ADMINISTRATOR, mWarehouseId), SELECT_ADMINISTRATOR_REQUEST_CODE);
        } else if (v.getId() == R.id.reserve_record_info_reservation_person_tv) {//选择预订人
            startForResult(InventorySelectDataFragment.getInstance(InventoryConstant.SELECT_RESERVATION_PERSON), SELECT_RESERVATION_PERSON_REQUEST_CODE);
        } else if (v.getId() == R.id.reserve_record_info_supervisor_tv) {//选择主管
            if (TextUtils.isEmpty(mReservationPersonTv.getTipText()) || mReservePersonId == -1) {
                ToastUtils.showShort(R.string.inventory_confrim_select_reservation_people);
                return;
            }
            startForResult(InventorySelectDataFragment.getInstance(InventoryConstant.SELECT_SUPERVISOR, mReservePersonId), SELECT_SUPERVISOR_REQUEST_CODE);
        } else if (v.getId() == R.id.reserve_record_info_check_all_ll) {//查看全部操作记录
            start(RecordHistoryFragment.getInstance(mTempRecordHistoryList));
        }
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            return;
        }

        InventorySelectDataBean selectDataBean = data.getParcelable(InventorySelectDataFragment.SELECT_DATA);

        switch (requestCode) {
            case RESERVE_OUT_SELECT_RECEIVING_PERSON_REQUEST_CODE://选择领用人
                if (selectDataBean != null) {
                    mReceivingPersonTv.setTipText(StringUtils.formatString(selectDataBean.name));
                    mReceivingPersonId = selectDataBean.id;
                }
                break;
            case RESERVER_OUT_REQUEST_CODE:
                MaterialService.MaterialInfo materialInfo = data.getParcelable(MaterialBatchFragment.DATA_MATERIAL);
                if (materialInfo != null) {
                    mMaterialList.get(mSelectPosition).batch = materialInfo.batch;
                    mMaterialList.get(mSelectPosition).receiveAmount = 0f;
                    if (materialInfo.batch != null) {
                        for (BatchService.Batch batch : materialInfo.batch) {
                            if (batch.number != null) {
                                mMaterialList.get(mSelectPosition).receiveAmount += batch.number;
                            }
                        }
                    }
                    mMaterialAdapter.notifyDataSetChanged();
                    //计算总计花费
                    computeTotalCost();
                }
                break;
            case SELECT_ADMINISTRATOR_REQUEST_CODE://选择仓库管理员
                if (selectDataBean != null) {
                    mAdministratorTv.setTipText(StringUtils.formatString(selectDataBean.name));
                    mAdministratorId = selectDataBean.id;
                }
                break;
            case SELECT_RESERVATION_PERSON_REQUEST_CODE://选择预订人
                if (selectDataBean != null) {
                    mReservationPersonTv.setTipText(StringUtils.formatString(selectDataBean.name));
                    mReservePersonId = selectDataBean.id;
                }
                break;
            case SELECT_SUPERVISOR_REQUEST_CODE://选择主管
                if (selectDataBean != null) {
                    mSupervisorTv.setTipText(StringUtils.formatString(selectDataBean.name));
                    mSupervisorId = selectDataBean.id;
                }
                break;
        }
    }

    /**
     * 计算总计花费
     */
    private void computeTotalCost() {
        float totalCost = 0;
        if (mMaterialList != null && mMaterialList.size() > 0) {
            for (MaterialService.MaterialInfo materialInfo : mMaterialList) {
                if (mFromType == InventoryConstant.INVENTORY_OUT) {
                    if (materialInfo.batch != null && materialInfo.batch.size() > 0) {
                        for (BatchService.Batch batch : materialInfo.batch) {
                            float price = Float.parseFloat(batch.cost);
                            float count = batch.number == null ? 0 : batch.number;
                            totalCost += price * count;
                        }
                    }
                } else if (mStatus == InventoryConstant.RESERVE_STATUS_DELIVERIED) {
                    float number = materialInfo.receiveAmount == null ? 0 : materialInfo.receiveAmount;
                    float cost = materialInfo.cost == null ? 0 : materialInfo.cost;
                    totalCost += number * cost;
                } else {
                    float number = materialInfo.bookAmount == null ? 0 : materialInfo.bookAmount;
                    float cost = materialInfo.cost == null ? 0 : materialInfo.cost;
                    totalCost += number * cost;
                }
            }
        }
        mTotalMoneyTv.setText("¥ " + StringUtils.formatFloatCost(totalCost));
        mMaterialAdapter.notifyDataSetChanged();
    }


    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (mStatus == InventoryConstant.RESERVE_STATUS_VERIFY_BACK) {
            ToastUtils.showShort(R.string.inventory_not_operate);
            return;
        }
        mSelectPosition = position;
        MaterialService.MaterialInfo materialInfo = ((ReserveInfoMaterialAdapter) adapter).getData().get(position);
        startForResult(MaterialBatchFragment.getInstance(materialInfo, InventoryConstant.INVENTORY_BATCH_RESERVE_OUT), RESERVER_OUT_REQUEST_CODE);
    }

    /**
     * 判断界面输入是否有效
     */
    public boolean isValid() {
        if (mStatus == InventoryConstant.RESERVE_STATUS_VERIFY_BACK) {
            ToastUtils.showShort(R.string.inventory_not_operate);
            return false;
        }
        if (TextUtils.isEmpty(mReceivingPersonTv.getTipText()) || mReceivingPersonId == -1) {
            ToastUtils.showShort(R.string.inventory_select_need_person_tip);
            return false;
        }
        if (mMaterialList == null || mMaterialList.size() == 0) {
            ToastUtils.showShort(R.string.inventory_select_material_out_and_amount);
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

        mRequest.activityId = mActivityid;
        mRequest.receivingPersonId = mReceivingPersonId;
        mRequest.warehouseId = mWarehouseId;
        mRequest.type = InventoryConstant.INVENTORY_MATERIAL_RESERVE_OUT;
        mRequest.remarks = mDescEnv.getDesc().trim();
        mRequest.inventory = inventoryList;

        return true;
    }

    public MaterialService.MaterialOutRequest getRequest() {
        return mRequest;
    }

    public static ReserveRecordInfoFragment getInstance(int type, long activityId,boolean fromMessage) {
        ReserveRecordInfoFragment fragment = new ReserveRecordInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(FROM_TYPE, type);
        bundle.putLong(ACTIVITY_ID, activityId);
        bundle.putBoolean(FROM_MESSAGE,fromMessage);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static ReserveRecordInfoFragment getInstance(int type, long activityId, int status, int workorderStatus) {
        ReserveRecordInfoFragment fragment = new ReserveRecordInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(FROM_TYPE, type);
        bundle.putLong(ACTIVITY_ID, activityId);
        bundle.putInt(STATUS, status);
        bundle.putInt(WORKORDER_STATUS, workorderStatus);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static ReserveRecordInfoFragment getInstance(int type, long activityId, int status) {
        ReserveRecordInfoFragment fragment = new ReserveRecordInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(FROM_TYPE, type);
        bundle.putLong(ACTIVITY_ID, activityId);
        bundle.putInt(STATUS, status);
        fragment.setArguments(bundle);
        return fragment;
    }


}
