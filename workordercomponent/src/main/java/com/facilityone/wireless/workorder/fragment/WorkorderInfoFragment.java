package com.facilityone.wireless.workorder.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.PhoneUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.facilityone.wireless.a.arch.BR;
import com.facilityone.wireless.a.arch.ec.adapter.AttachmentAdapter;
import com.facilityone.wireless.a.arch.ec.adapter.AudioAdapter;
import com.facilityone.wireless.a.arch.ec.adapter.GridImageAdapter;
import com.facilityone.wireless.a.arch.ec.audio.AudioPlayConnection;
import com.facilityone.wireless.a.arch.ec.audio.AudioPlayManager;
import com.facilityone.wireless.a.arch.ec.audio.AudioPlayService;
import com.facilityone.wireless.a.arch.ec.module.AttachmentBean;
import com.facilityone.wireless.a.arch.ec.module.ISelectDataService;
import com.facilityone.wireless.a.arch.ec.module.LocationBean;
import com.facilityone.wireless.a.arch.ec.module.SelectDataBean;
import com.facilityone.wireless.a.arch.ec.module.UserService;
import com.facilityone.wireless.a.arch.ec.selectdata.SelectDataFragment;

import com.facilityone.wireless.a.arch.ec.ui.SignatureActivity;
import com.facilityone.wireless.a.arch.ec.utils.SPKey;
import com.facilityone.wireless.a.arch.mvp.BaseFragment;
import com.facilityone.wireless.a.arch.offline.dao.DepDao;
import com.facilityone.wireless.a.arch.offline.dao.OfflinePatrolTimeDao;
import com.facilityone.wireless.a.arch.utils.UrlUtils;
import com.facilityone.wireless.a.arch.widget.EditNumberView;
import com.facilityone.wireless.a.arch.widget.FMBottomChoiceSheetBuilder;
import com.facilityone.wireless.a.arch.widget.FMBottomPauseSelectSheetBuilder;
import com.facilityone.wireless.a.arch.widget.FMWarnDialogBuilder;
import com.facilityone.wireless.a.arch.widget.PhoneMenuBuilder;
import com.facilityone.wireless.basiclib.app.FM;
import com.facilityone.wireless.basiclib.utils.DateUtils;
import com.facilityone.wireless.basiclib.utils.FMThreadUtils;
import com.facilityone.wireless.basiclib.utils.StringUtils;
import com.facilityone.wireless.basiclib.video.SimplePlayer;
import com.facilityone.wireless.basiclib.widget.FullyGridLayoutManager;
import com.facilityone.wireless.componentservice.common.permissions.PermissionsManager;
import com.facilityone.wireless.componentservice.inventory.InventoryService;
import com.facilityone.wireless.workorder.R;
import com.facilityone.wireless.workorder.WorkOrderNfcList;
import com.facilityone.wireless.workorder.adapter.WorkOrderLaborerAdapter;
import com.facilityone.wireless.workorder.adapter.WorkorderApprovalContentAdapter;
import com.facilityone.wireless.workorder.adapter.WorkorderHistoryAdapter;
import com.facilityone.wireless.workorder.module.WorkorderConstant;
import com.facilityone.wireless.workorder.module.WorkorderCreateService;
import com.facilityone.wireless.workorder.module.WorkorderDataHolder;
import com.facilityone.wireless.workorder.module.WorkorderHelper;
import com.facilityone.wireless.workorder.module.WorkorderLaborerService;
import com.facilityone.wireless.workorder.module.WorkorderService;
import com.facilityone.wireless.workorder.module.WorkorderUrl;
import com.facilityone.wireless.workorder.presenter.WorkorderInfoPresenter;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.joanzapata.iconify.widget.IconTextView;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.FalsifyFooter;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DefaultObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Author：gary
 * Email: xuhaozv@163.com
 * description:工单信息
 * Date: 2018/7/11 上午10:10
 */
public class WorkorderInfoFragment extends BaseFragment<WorkorderInfoPresenter> implements OnRefreshListener, View.OnClickListener, BaseQuickAdapter.OnItemClickListener, AudioAdapter.onRemoveAudioListener, BaseQuickAdapter.OnItemChildClickListener {
    private TextView mTvRequester;
    private TextView mTvPriority;
    private TextView mTvStatus;
    private TextView mTvDep;
    private TextView mTvLocation;
    private LinearLayout mLlForecastTime;
    private TextView mTvForecastTime;
    private TextView mTvServiceType;
    private TextView mTvDesc;
    private TextView mTvSendContent;
    private TextView mTvSignatureDirector;
    private TextView mTvSignatureCustomer;
    private TextView mTvToolTotal;
    private TextView mTvChargeTotal;
    private TextView mTvDevice;//关联设备
    //    private TextView mTvApprovalContent;
    private LinearLayout mPriorityLl;
    private RecyclerView mRvApprovalContent;
    private LinearLayout mLlAttachment;
    private LinearLayout mLlMedia;
    private LinearLayout mLlExpand;
    private LinearLayout mLlExecutor;
    private LinearLayout mLlRecord;
    private LinearLayout mLlEqu;
    private LinearLayout mLlMaterial;
    private LinearLayout mLlSendContent;
    private LinearLayout mLlInput;
    private LinearLayout mLlSignature;
    private LinearLayout mLlSignatureDirector;
    private LinearLayout mLlSignatureCustomer;
    private LinearLayout mLlBpDetail;
    private LinearLayout mLlTool;
    private LinearLayout mLlToolBp;
    private LinearLayout mLlApprovalContent;
    private LinearLayout mLlPlanStep;
    private LinearLayout mLlSpace;
    private LinearLayout mLlPay;
    private IconTextView mCall;
    private IconTextView mExpand;
    private SmartRefreshLayout mRefreshLayout;
    private RecyclerView mRvPhoto;
    private RecyclerView mRvVideo;
    private RecyclerView mRvAudio;
    private RecyclerView mRvAttachments;
    private RecyclerView mRvHistories;
    private RecyclerView mRvExecutor;
    private View mViewSignature;
    private View mViewToolBp;
    private View mViewToolBpBottom;
    private View mViewApprovalContent;
    //    private LoadingLayout mEui;
    private IconTextView tv_right_icon_tol;

    //2021-11-30
    private LinearLayout ll_object;
    private TextView tv_object_title;
    private LinearLayout ll_fault_object_reason;
    private EditNumberView env_fault_object_reason;
    private IconTextView mItcObject;//故障对象右侧小图标
    /****************/

    public static final String WORKORDER_STATUS = "workorder_status";
    public static final String WORKORDER_NEED_JUMP = "workorder_need_jump";
    public static final String WORKORDER_CODE = "workorder_code";
    public static final String WORKORDER_ID = "workorder_id";
    public static final String IS_MAINTENANCE = "maintenance_order";
    public static final String IS_PENDING = "is_pending";
    public static final String IS_FINISH = "maintenance_finish_order";
    public static final String ARRIVAL_DATE_TIME = "arrival_date_time";
    public static final String ARRIVAL_DATE_END_TIME = "arrival_date_end_time";
    public static final String WORKORDER_LOCATION = "workorder_location";
    public static final String CAN_OPT = "can_opt";
    public static final String FROME_MESSAGE = "from_message";
    private static final int LABORER_REQUEST_CODE = 4002;
    public static final int DISPATCH_REQUEST_CODE = 4003;
    public static final int APPROVAL_REQUEST_CODE = 4004;
    public static final int INPUT_REQUEST_CODE = 4005;
    public static final int CUSTOMER_SIGNATURE_REQUEST_CODE = 4006;
    public static final int FAULT_DEVICE = 4007;
    public static final int TOOLS = 4008;
    public static final int CHARGE = 4009;
    public static final int STEP = 4010;
    public static final int NEW_ORDER = 4013;
    public static final int SPACE_LOCATION = 4011;
    public static final int PAYMENT = 4012;
    private final static int REQUEST_REASON = 20007;
    private final static int CAUSE_REASON = 20009; //故障原因
    private final static int REQUEST_INVALID = 20008;
    private final static int CAUSE_DEFAULT_OBJECT = 20010; //故障对象
    private final static int REFRESH = 500001; // 界面刷新
    private final static int REFRESH_POP = 500009; // 跳回列表
    public Long mWoId;
    private String mCode;
    private int mStatus;
    private int refreshStatus;
    private Long mWoTeamId;
    private String tel;

    //图片
    private GridImageAdapter mGridImageAdapter;
    private List<LocalMedia> mLocalMedias;
    private List<LocalMedia> tem;

    //音频
    private List<LocalMedia> mAudioSelectList;
    private AudioAdapter mAudioAdapter;
    //视频
    private List<LocalMedia> mVideoSelectList;
    private GridImageAdapter mVideoGridImageAdapter;

    //附件
    private List<AttachmentBean> mAttachmentList;
    private AttachmentAdapter mAttachmentAdapter;

    //历史记录
    private ArrayList<WorkorderService.HistoriesBean> mHistoriesList;
    private WorkorderHistoryAdapter mHistoryAdapter;

    //执行人
    private List<WorkorderLaborerService.WorkorderLaborerBean> workOrderLaborers;
    private WorkOrderLaborerAdapter mWorkOrderLaborerAdapter;
    private int laborerPosition;
    private Long mApprovalId;
    private boolean mAllStart;//执行人全部接单

    //签字图片
    private String customerSignatureId;
    private String directorSignatureId;
    //是否可以常规操作
    private boolean mCanOpt;
    //设备
    private ArrayList<WorkorderService.WorkOrderEquipmentsBean> mWorkOrderEquipments;
    //物料
    private List<WorkorderService.WorkorderReserveRocordBean> mWorkOrderMaterials;
    //故障原因
    private ArrayList<WorkorderService.WorkOrderToolsBean> mWorkOrderTools;
    //收取明细
    private ArrayList<WorkorderService.ChargesBean> mCharges;
    private WorkorderService.ChargesBean mToolCost;//工具费用
    //工单状态
    private Integer mCategory;
    //维护步骤
    private ArrayList<WorkorderService.StepsBean> mSteps;
    //空间位置
    private ArrayList<WorkorderService.WorkOrderLocationsBean> mSpaceLocations;
    //新 空间位置
    private ArrayList<WorkorderService.PmSpaceBean> mNewSpace;
    //缴费单
    private ArrayList<WorkorderService.PaymentsBean> mPayments;
    //位置
    private LocationBean mLocationId;
    //当前工单是否属于登录用户
    private boolean mLaborer;
    //是否需要跳转页面
    private boolean mNeedJump;
    private Map<Long, String> mPriority;
    //是否接单
    private boolean mAcceptWorkOrder;
    //派工预估时间和派发内容
    private String mSendContent;
    private Long mEstimateStartTime;
    private Long mEstimateEndTime;
    //pm工单是否需要扫码设备
    private boolean mNeedScan;
    //真实工单状态
    private Integer mRealStatus;

    private List<WorkorderService.ApprovalContentBean> mApprovalContentBeanList;
    private WorkorderApprovalContentAdapter mApprovalContentAdapter;
    private WorkorderService.WorkorderInfoBean data;
    private FMBottomChoiceSheetBuilder builderChoice;
    private QMUIBottomSheet buildInvalid;
    private String invalidReason; //作废原因
    private Long operateReasonId; //申请作废原因Id
    FMBottomPauseSelectSheetBuilder pauseDialogBuilder;
    QMUIBottomSheet pauseDialog;
    private final static String RESULT_REASON = "pause_reason";
    private Long InvalidId;
    private Boolean isMaintenanceOrder; //是否是维护工单
    private Boolean isFinish; //是否待存档工单
    private LinearLayout mOrderLl;

    private Boolean isException = false;//是否异常工单
    private Boolean isAttendance = false;//签到记录是否有效
    private Boolean hasAttendanceData = false;//是否有签到记录
    private TextView mOrderTagTv; //工单标签状态
    private Boolean isSignOn = false; //已签到
    private Integer tagStatus; //工单标签
    private Integer isPending = -1; //是否是待处理工单内内容
    private String workOrderContent; // 工作内容
    private Long operateOrderReasonId; //故障原因Id
    private boolean hasUnFinshTask = false; //判断是否有未完成的任务 默认为没有
    private Long faultObjectId; //故障对象
    private boolean NFCConnect = false; //判断当前工单NFC是否对接
    private boolean fromQuery = false; //是否是从查询进入
    private boolean countAccord; //是否需要输入设备数量
    private String attention; //注意事项
    private List<Integer> currentRoles; //当前人员对该工单所拥有的角色权限数组
    private Boolean fromMessage = false; //从消息中获取工单详情
    private List<Long> allApprovers; //当前工单审批人数组
    public String completeMessage = null; //完成工单提醒文案

    @Override
    public WorkorderInfoPresenter createPresenter() {
        return new WorkorderInfoPresenter();
    }

    @Override
    public Object setLayout() {
        return R.layout.fragment_workorder_info;
    }

    @Override
    protected int setTitleBar() {
        return R.id.ui_topbar;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getPresenter().queryPriority();
        initData();
        initView();
        initMedia();
        onRefresh();
        requestPermission();
    }


    private void requestPermission() {
        PermissionUtils.permission(PermissionConstants.STORAGE,
                PermissionConstants.MICROPHONE,
                PermissionConstants.PHONE)
                .request();
    }

    private void initData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            mStatus = bundle.getInt(WORKORDER_STATUS, WorkorderConstant.WORK_STATUS_NONE);
            if (mStatus == WorkorderConstant.WORK_STATUS_UBNORMAL) {
                isException = true;
            }
            mCode = bundle.getString(WORKORDER_CODE, "");
            mWoId = bundle.getLong(WORKORDER_ID);
            isMaintenanceOrder = bundle.getBoolean(IS_MAINTENANCE, false);
            isFinish = bundle.getBoolean(IS_FINISH, false);
            isPending = bundle.getInt(IS_PENDING, -1);
            fromMessage = bundle.getBoolean(FROME_MESSAGE, false);
        }
        refreshStatus = mStatus;
        if (refreshStatus == WorkorderConstant.WORK_STATUS_NONE) {
            fromQuery = true;
        } else {
            fromQuery = false;
        }
        mToolCost = new WorkorderService.ChargesBean();
        mToolCost.name = getString(R.string.workorder_tool_fee);
        mToolCost.amount = 0D;
        mToolCost.chargeId = -1L;
    }

    private void initView() {
        setTitle(mCode);
        mTvRequester = findViewById(R.id.tv_requester);
        mTvPriority = findViewById(R.id.tv_info_priority);
        mTvStatus = findViewById(R.id.tv_status);
        mTvDep = findViewById(R.id.tv_dep);
        mTvLocation = findViewById(R.id.tv_location);
        mCall = findViewById(R.id.itv_call);
        mTvServiceType = findViewById(R.id.tv_service_type);
        mTvSignatureCustomer = findViewById(R.id.tv_signature);
        mTvSignatureDirector = findViewById(R.id.tv_signature_director);
        mRefreshLayout = findViewById(R.id.refreshLayout);
        mLlForecastTime = findViewById(R.id.work_order_forecast_time_ll);
        mTvForecastTime = findViewById(R.id.tv_forecast_time);
        mTvDesc = findViewById(R.id.tv_desc);
        mTvToolTotal = findViewById(R.id.tv_tool_total);
        mTvChargeTotal = findViewById(R.id.tv_bp_total);
        mTvDevice = findViewById(R.id.tv_device);
        if (!isMaintenanceOrder) {
            mTvDevice.setText(getString(R.string.workorder_fault_device));
        }
        mOrderTagTv = findViewById(R.id.workorder_new_status_tv); //工单标签状态

//        mTvApprovalContent = findViewById(R.id.approvalContent_tv);
        mPriorityLl = findViewById(R.id.priority_ll);
        mRvApprovalContent = findViewById(R.id.approvalContent_rv);
        mLlMedia = findViewById(R.id.ll_media);
        mLlAttachment = findViewById(R.id.ll_attachment);
        mLlExpand = findViewById(R.id.ll_expend);
        mExpand = findViewById(R.id.itv_expand);
        mLlRecord = findViewById(R.id.ll_record);
        mLlExecutor = findViewById(R.id.ll_executor);
        mRvExecutor = findViewById(R.id.rv_executor);
        mLlEqu = findViewById(R.id.ll_device);
        mLlMaterial = findViewById(R.id.ll_material);
        mLlSendContent = findViewById(R.id.ll_send_content);
        mTvSendContent = findViewById(R.id.tv_send_content);
        mLlInput = findViewById(R.id.ll_input);
        mLlSignature = findViewById(R.id.ll_signature);
        mLlSignatureCustomer = findViewById(R.id.ll_signature_customer);
        mLlSignatureDirector = findViewById(R.id.ll_signature_director);
        mLlTool = findViewById(R.id.ll_tool);
        mLlBpDetail = findViewById(R.id.ll_bp_detail);
        mLlToolBp = findViewById(R.id.ll_tool_bp);
        mLlApprovalContent = findViewById(R.id.ll_approvalContent);
        mLlPlanStep = findViewById(R.id.ll_plan_step);
        mLlSpace = findViewById(R.id.ll_space);
        mLlPay = findViewById(R.id.ll_pay);

        mViewSignature = findViewById(R.id.view_signature);
        mViewToolBp = findViewById(R.id.view_tool_bp);
        mViewToolBpBottom = findViewById(R.id.view_tool_bp_bottom);
        mViewApprovalContent = findViewById(R.id.view_approvalContent_bottom_line);
//        mEui = findViewById(R.id.eui);
        mRvPhoto = findViewById(R.id.rv_photo);
        mRvVideo = findViewById(R.id.rv_video);
        mRvAudio = findViewById(R.id.rv_audio);
        mRvAttachments = findViewById(R.id.rv_attachment);
        mRvHistories = findViewById(R.id.rv_history);

        tv_right_icon_tol = findViewById(R.id.tv_tool_right_icon);

        ll_object = findViewById(R.id.ll_object);
        tv_object_title = findViewById(R.id.tv_object_title);
        ll_fault_object_reason = findViewById(R.id.ll_fault_object_input);
        env_fault_object_reason = findViewById(R.id.et_fault_object_desc);
        mItcObject = findViewById(R.id.tv_object_right_icon);

        mRefreshLayout.setEnablePureScrollMode(false);
        mRefreshLayout.setRefreshFooter(new FalsifyFooter(getContext()));
        mRvExecutor.setNestedScrollingEnabled(false);
        mRvApprovalContent.setNestedScrollingEnabled(false);

        mRvApprovalContent.setLayoutManager(new LinearLayoutManager(getContext()));
        mApprovalContentBeanList = new ArrayList<>();
        mApprovalContentAdapter = new WorkorderApprovalContentAdapter(mApprovalContentBeanList);
        mRvApprovalContent.setAdapter(mApprovalContentAdapter);

        mRvApprovalContent.setNestedScrollingEnabled(false);
        mRvAttachments.setNestedScrollingEnabled(false);
        mRvAudio.setNestedScrollingEnabled(false);
        mRvExecutor.setNestedScrollingEnabled(false);
        mRvHistories.setNestedScrollingEnabled(false);
        mRvPhoto.setNestedScrollingEnabled(false);
        mRvVideo.setNestedScrollingEnabled(false);


        mOrderLl = findViewById(R.id.ll_order);

        mRefreshLayout.setOnRefreshListener(this);
        mCall.setOnClickListener(this);
        mLlExpand.setOnClickListener(this);
        mLlInput.setOnClickListener(this);
        mLlEqu.setOnClickListener(this);
        mLlMaterial.setOnClickListener(this);
        mLlSignatureCustomer.setOnClickListener(this);
//        mLlSignatureDirector.setOnClickListener(this);
        mLlTool.setOnClickListener(this);
        mLlBpDetail.setOnClickListener(this);
        mLlPlanStep.setOnClickListener(this);
        mLlSpace.setOnClickListener(this);
        mLlPay.setOnClickListener(this);
        mOrderLl.setOnClickListener(this);
        ll_object.setOnClickListener(this);
//        mEui.showContent();

        currentRoles = new ArrayList<>();
    }

    private void initMedia() {
        FullyGridLayoutManager manager = new FullyGridLayoutManager(getContext(),
                FullyGridLayoutManager.SPAN_COUNT,
                GridLayoutManager.VERTICAL,
                false);
        mRvPhoto.setLayoutManager(manager);
        mLocalMedias = new ArrayList<>();
        tem = new ArrayList<>();
        mGridImageAdapter = new GridImageAdapter(mLocalMedias, true);
        mGridImageAdapter.setOnItemClickListener(this);
        mRvPhoto.setAdapter(mGridImageAdapter);

        FullyGridLayoutManager manager2 = new FullyGridLayoutManager(getContext(),
                FullyGridLayoutManager.SPAN_COUNT,
                GridLayoutManager.VERTICAL,
                false);

        mRvVideo.setLayoutManager(manager2);
        mVideoSelectList = new ArrayList<>();
        mVideoGridImageAdapter = new GridImageAdapter(mVideoSelectList, true);
        mVideoGridImageAdapter.setOnItemClickListener(this);
        mRvVideo.setAdapter(mVideoGridImageAdapter);

        mAudioSelectList = new ArrayList<>();
        mAudioAdapter = new AudioAdapter(mAudioSelectList, true, getContext());
        mAudioAdapter.setOnRemoveAudioListener(this);
        mRvAudio.setLayoutManager(new LinearLayoutManager(getContext()));
        mRvAudio.setAdapter(mAudioAdapter);

        mAttachmentList = new ArrayList<>();
        mAttachmentAdapter = new AttachmentAdapter(mAttachmentList);
        mAttachmentAdapter.setOnItemClickListener(this);
        mAttachmentAdapter.setOnItemChildClickListener(this);
        mRvAttachments.setLayoutManager(new LinearLayoutManager(getContext()));
        mRvAttachments.setAdapter(mAttachmentAdapter);

        mRvHistories.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onMoreMenuClick(View view) {
        if (isPending != 1) {
            if (tagStatus != null && (tagStatus == WorkorderConstant.APPLICATION_FOR_SUSPENSION || tagStatus == WorkorderConstant.APPLICATION_VOID)) {
                if (isException) {
                    getPresenter().onMoreMenuClick(getContext(), true, true, WorkorderConstant.WORK_STATUS_UBNORMAL, mAcceptWorkOrder, mWoId, mApprovalId, mCode, mSendContent, mEstimateStartTime, mEstimateEndTime, mWorkOrderMaterials, isSignOn, isMaintenanceOrder, currentRoles, fromMessage);
                } else {
                    getPresenter().onMoreMenuClick(getContext(), true, true, WorkorderConstant.WORK_STATUS_APPROVAL, mAcceptWorkOrder, mWoId, mApprovalId, mCode, mSendContent, mEstimateStartTime, mEstimateEndTime, mWorkOrderMaterials, isSignOn, isMaintenanceOrder, currentRoles, fromMessage);
                }
            } else if (isException) {
                getPresenter().onMoreMenuClick(getContext(), true, true, WorkorderConstant.WORK_STATUS_UBNORMAL, mAcceptWorkOrder, mWoId, mApprovalId, mCode, mSendContent, mEstimateStartTime, mEstimateEndTime, mWorkOrderMaterials, isSignOn, isMaintenanceOrder, currentRoles, fromMessage);
            } else if (isFinish) {
                if (isMaintenanceOrder) {
                    if (tagStatus != null && tagStatus == WorkorderConstant.STOP) {
                        getPresenter().onMoreMenuClick(getContext(), true, true, WorkorderConstant.WORK_STATUS_TERMINATED, mAcceptWorkOrder, mWoId, mApprovalId, mCode, mSendContent, mEstimateStartTime, mEstimateEndTime, mWorkOrderMaterials, isSignOn, isMaintenanceOrder, currentRoles, fromMessage);
                    } else {
                        if (refreshStatus == WorkorderConstant.WORK_STATUS_VERIFIED) {

                            getPresenter().onMoreMenuClick(getContext(), true, true, WorkorderConstant.WORK_STATUS_MAINTENCE_NOT, mAcceptWorkOrder, mWoId, mApprovalId, mCode, mSendContent, mEstimateStartTime, mEstimateEndTime, mWorkOrderMaterials, isSignOn, isMaintenanceOrder, currentRoles, fromMessage);
                        } else {
                            getPresenter().onMoreMenuClick(getContext(), true, true, WorkorderConstant.WORK_STATUS_MAINTENCE, mAcceptWorkOrder, mWoId, mApprovalId, mCode, mSendContent, mEstimateStartTime, mEstimateEndTime, mWorkOrderMaterials, isSignOn, isMaintenanceOrder, currentRoles, fromMessage);
                        }
                    }
                } else {
                    getPresenter().onMoreMenuClick(getContext(), true, true, WorkorderConstant.WORK_STATUS_COMPLETED, mAcceptWorkOrder, mWoId, mApprovalId, mCode, mSendContent, mEstimateStartTime, mEstimateEndTime, mWorkOrderMaterials, isSignOn, isMaintenanceOrder, currentRoles, fromMessage);
                }
            } else {
                getPresenter().onMoreMenuClick(getContext(), true, true, refreshStatus, mAcceptWorkOrder, mWoId, mApprovalId, mCode, mSendContent, mEstimateStartTime, mEstimateEndTime, mWorkOrderMaterials, isSignOn, isMaintenanceOrder, currentRoles, fromMessage);
            }
        } else {
            if (tagStatus != null && (tagStatus == WorkorderConstant.APPLICATION_FOR_SUSPENSION || tagStatus == WorkorderConstant.APPLICATION_VOID)) {
                getPresenter().onMoreMenuClick(getContext(), hasAttendanceData, isAttendance, WorkorderConstant.WORK_STATUS_APPROVAL, mAcceptWorkOrder, mWoId, mApprovalId, mCode, mSendContent, mEstimateStartTime, mEstimateEndTime, mWorkOrderMaterials, isSignOn, isMaintenanceOrder, currentRoles, fromMessage);
            } else if (isException) {
                getPresenter().onMoreMenuClick(getContext(), hasAttendanceData, isAttendance, WorkorderConstant.WORK_STATUS_UBNORMAL, mAcceptWorkOrder, mWoId, mApprovalId, mCode, mSendContent, mEstimateStartTime, mEstimateEndTime, mWorkOrderMaterials, isSignOn, isMaintenanceOrder, currentRoles, fromMessage);
            } else if (isFinish) {
                if (isMaintenanceOrder) {
                    if (tagStatus != null && tagStatus == WorkorderConstant.STOP) {
                        getPresenter().onMoreMenuClick(getContext(), hasAttendanceData, isAttendance, WorkorderConstant.WORK_STATUS_TERMINATED, mAcceptWorkOrder, mWoId, mApprovalId, mCode, mSendContent, mEstimateStartTime, mEstimateEndTime, mWorkOrderMaterials, isSignOn, isMaintenanceOrder, currentRoles, fromMessage);
                    } else {
                        getPresenter().onMoreMenuClick(getContext(), hasAttendanceData, isAttendance, WorkorderConstant.WORK_STATUS_MAINTENCE, mAcceptWorkOrder, mWoId, mApprovalId, mCode, mSendContent, mEstimateStartTime, mEstimateEndTime, mWorkOrderMaterials, isSignOn, isMaintenanceOrder, currentRoles, fromMessage);
                    }
                } else {
                    getPresenter().onMoreMenuClick(getContext(), hasAttendanceData, isAttendance, WorkorderConstant.WORK_STATUS_COMPLETED, mAcceptWorkOrder, mWoId, mApprovalId, mCode, mSendContent, mEstimateStartTime, mEstimateEndTime, mWorkOrderMaterials, isSignOn, isMaintenanceOrder, currentRoles, fromMessage);
                }
            } else {
                getPresenter().onMoreMenuClick(getContext(), hasAttendanceData, isAttendance, refreshStatus, mAcceptWorkOrder, mWoId, mApprovalId, mCode, mSendContent, mEstimateStartTime, mEstimateEndTime, mWorkOrderMaterials, isSignOn, isMaintenanceOrder, currentRoles, fromMessage);
            }

        }

    }

    /**
     * @Auther: karelie
     * @Date: 2021/8/12
     * @Infor: 新派工单
     */
    public void newOrder() {
//        WorkorderCreateService.newOrderCreateReq newOrderBunder = new WorkorderCreateService.newOrderCreateReq();
//        WorkorderCreateService.newOrderCreateAllName nameAll = new WorkorderCreateService.newOrderCreateAllName();
//        newOrderBunder.userId = FM.getEmId(); //userId
//        newOrderBunder.name = data.applicantName+""; //操作人名字
//        newOrderBunder.phone = data.applicantPhone+""; //操作人电话
//        newOrderBunder.organizationId = data.orgId; //部门Id
//        newOrderBunder.serviceTypeId = data.serviceTypeId; //服务类型Id
//        newOrderBunder.scDescription = data.woDescription; //描述
//        newOrderBunder.priorityId = data.priorityId; //优先级Id
//        newOrderBunder.processId = data.flowId; //流程ID
//        newOrderBunder.woType = data.type; //工单类型
//        newOrderBunder.reqId = data.woId; //工单Id
//        //名称
//        nameAll.loactionName = data.location;//位置信息
//        nameAll.serviceType = data.serviceTypeName;//服务类型名称
//        nameAll.priority = data.priorityName; //优先级名称
//        nameAll.departmentName = data.organizationName;//部门名称
//
//        newOrderBunder.equipmentSystemName = data.workOrderEquipments;
//        newOrderBunder.location = data.locationId;
//        newOrderBunder.nameAll = nameAll; //需要用到的名称
        SelectDataBean mServiceTypeSelectData = new SelectDataBean();
        mServiceTypeSelectData.setId(data.serviceTypeId);
        mServiceTypeSelectData.setFullName(data.serviceTypeName);
        SelectDataBean mDepSelectData = new SelectDataBean();
        mDepSelectData.setId(data.orgId);
        mDepSelectData.setFullName(data.organizationName);
        startForResult(WorkorderNewCreateFragment.getInstance(mServiceTypeSelectData, data.woId, data.applicantName, data.applicantPhone, Long.parseLong(data.type.toString()), data.locationId, data.location, mDepSelectData, data.woDescription,data.isBunchingOrder), NEW_ORDER);
    }

    /**
     * 弹出完成工作提醒 维修工单
     */
    public void showCompleteDiaglog(Long woId, String input, Long operateReasonId, int type, Long componentId, String causeOther) {
        new FMWarnDialogBuilder(getContext()).setIconVisible(false)
                .setSureBluBg(true)
                .setIconVisible(true)
                .showTitle(false)
                .setCancelGone(true)
                .setCanceledOnTouchOutside(false)
                .setSure(R.string.workorder_confirm)
                .setTip(completeMessage)
                .addOnBtnCancelClickListener(new FMWarnDialogBuilder.OnBtnClickListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, View view) {
                        dialog.dismiss();
                    }
                })
                .addOnBtnSureClickListener(new FMWarnDialogBuilder.OnBtnClickListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, View view) {
                        dialog.dismiss();
                        showLoading();
                        getPresenter().workorderOptCommon(woId, null, getOperateReasonId(), WorkorderConstant.WORKORDER_OPT_TYPE_COMPLETION, componentId, causeOther);
                    }
                }).create(R.style.fmDefaultWarnDialog).show();
    }

    //获取完成工作提醒内容
    public String getWorkDoneReminder() {
        return completeMessage;
    }

    public Integer getNewStatus() {
        return mRealStatus;
    }

    public Integer getTagStatus() {
        return tagStatus;
    }

    /**
     * @Auther: karelie
     * @Date: 2021/8/16
     * @Infor: 作废申请
     */
    public void invalidOrder(Long woId) {

        builderChoice = new FMBottomChoiceSheetBuilder(getContext());
        builderChoice.setOnSaveInputListener(new FMBottomChoiceSheetBuilder.OnInputBtnClickListener() {
            @Override
            public void onSaveClick(QMUIBottomSheet dialog, String input) {

            }

            @Override
            public void onLeftClick(QMUIBottomSheet dialog, String input) {
                dialog.dismiss();

            }

            @Override
            public void onRightClick(QMUIBottomSheet dialog, String input) {
                dialog.dismiss();
                showLoading();
                getPresenter().invalidOrderPost(woId, input, InvalidId);
            }
        });
        buildInvalid = builderChoice.build();
        builderChoice.getLLTwoBtn().setVisibility(View.VISIBLE);
        builderChoice.getLeftBtn().setVisibility(View.GONE);
        builderChoice.getDescEt().setFocusable(false);
        builderChoice.getDescEt().setHint("请选择作废原因");
        EditText et = builderChoice.getDescEt();
        builderChoice.setTitle("作废申请");
        builderChoice.getRightBtn().setBackgroundResource(R.drawable.btn_common_bg_selector);
        builderChoice.setRightBtnText("确定");
        buildInvalid.show();

        et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildInvalid.hide();
                startForResult(
                        SelectDataFragment.getInstance(ISelectDataService.DATA_TYPE_INVALIDD, ISelectDataService.REASON_TYPE_INVALID),
                        REQUEST_INVALID);
            }
        });

    }

    public void getData(WorkorderService.WorkorderInfoBean data) {
        this.data = data;
    }

    private void onRefresh() {
        getPresenter().getWorkorderInfo(mWoId);
        //公共查询不需要传入单号

    }

    //刷新基础信息
    public void refreshBasicInfoUI(WorkorderService.WorkorderInfoBean data) {
        //工作组id
        mWoTeamId = data.workTeamId;

        if (data.workDoneReminder != null) {
            completeMessage = data.workDoneReminder + "";
        }

        if (data.pmInfo != null && data.pmInfo.eqCountAccord != null) {
            countAccord = data.pmInfo.eqCountAccord;
        }
        if (data.pmInfo != null && data.pmInfo.mattersNeedingAttention != null) {
            attention = data.pmInfo.mattersNeedingAttention;
        }
        workOrderContent = data.workContent;

        if ((refreshStatus == WorkorderConstant.WORK_STATUS_COMPLETED
                || refreshStatus == WorkorderConstant.WORK_STATUS_NONE
                || (refreshStatus == WorkorderConstant.WORK_STATUS_NONE && (
                data.newStatus == WorkorderConstant.WORK_NEW_STATUS_PROCESS ||
                        data.newStatus == WorkorderConstant.WORK_NEW_STATUS_ARCHIVED_WAIT))) && !isMaintenanceOrder) {
            if (data.failueDescription == null) {
                mTvToolTotal.setText("");
                tv_right_icon_tol.setVisibility(View.GONE);
            } else {
                mTvToolTotal.setText(data.failueDescription + "");
                tv_right_icon_tol.setVisibility(View.GONE);
                // TODO 当Id为1->即选择项为其他原因 需要展示具体原因
                if (data.causeId == WorkorderConstant.CAUSE_REASON_OTHER) {
                    ll_fault_object_reason.setVisibility(View.VISIBLE);
                    env_fault_object_reason.setDesc(data.causeOther + "");
                    env_fault_object_reason.canInput(false);
                    env_fault_object_reason.setInputDisp(false);
                } else {
                    ll_fault_object_reason.setVisibility(View.GONE);
                }
            }

        }

        //工单标签
        tagStatus = data.tag;
        //派工需要
        mSendContent = data.sendWorkContent == null ? "" : data.sendWorkContent;
        mEstimateStartTime = data.estimateStartTime == null ? -1L : data.estimateStartTime;
        mEstimateEndTime = data.estimateEndTime == null ? -1L : data.estimateEndTime;
        //工单状态
        mCategory = data.category;
        if (mCategory != null && mCategory == WorkorderConstant.WORK_TYPE_PM) {
            mPriorityLl.setVisibility(View.GONE);
        }
        //根据详情内字段
        mNeedScan = data.needScan == null ? false : data.needScan;
        mCode = data.code;
        setTitle(mCode);
        //列表菜单数据
        customerSignatureId = data.customerSignImgId;
        directorSignatureId = data.supervisorSignImgId;
//        mEui.showContent();
        mApprovalId = data.approvalId;
        mLocationId = data.locationId;
        mRealStatus = data.newStatus;
        //不是工单查询的时候再去赋值
        if (refreshStatus != WorkorderConstant.WORK_STATUS_NONE) {
            refreshStatus = data.status == null ? refreshStatus : data.status;
        }
        allApprovers = new ArrayList<>();
        allApprovers = data.approvers;


        mCanOpt = false;
        if (refreshStatus == WorkorderConstant.WORK_STATUS_PROCESS) {
            Long emId = FM.getEmId();
            List<WorkorderLaborerService.WorkorderLaborerBean> workOrderLaborers = data.workOrderLaborers;
            if (workOrderLaborers != null && workOrderLaborers.size() > 0 && emId != null) {
                for (WorkorderLaborerService.WorkorderLaborerBean workOrderLaborer : workOrderLaborers) {
                    if (workOrderLaborer.status != null
                            && workOrderLaborer.laborerId != null
                            && emId.equals(workOrderLaborer.laborerId)
                            && workOrderLaborer.status == WorkorderConstant.WORKORDER_STATUS_PERSONAL_ACCEPT) {
                        mCanOpt = true;
                        mAcceptWorkOrder = true;
                        break;
                    }
                }
            }
        }

        if (refreshStatus != WorkorderConstant.WORK_STATUS_NONE
                && refreshStatus != WorkorderConstant.WORK_STATUS_ARCHIVED) {
            if (refreshStatus == WorkorderConstant.WORK_STATUS_APPROVAL) {
                Long userId = FM.getEmId();
                if (data.approvers != null && data.approvers.size() > 0 && userId != null) {
                    int i = 0;
                    for (i = 0; i < data.approvers.size(); i++) {
                        Long approverId = data.approvers.get(i);
                        if (userId.equals(approverId)) {
                            setMoreMenu();
                            break;
                        }
                    }
                    if (i >= data.approvers.size()) {
                        setMoreMenuVisible(false);
                    }
                } else {
                    setMoreMenuVisible(false);
                }
            } else {
                setMoreMenu();
            }
        } else {
            setMoreMenuVisible(false);
        }

        showMenuList();


        if (data.applicantName != null && data.createDateTime != null) {
            Date date = new Date(data.createDateTime);
            mTvRequester.setText(String.format(getString(R.string.workorder_work_detail_basic_create),
                    data.applicantName,
                    TimeUtils.date2String(date, DateUtils.SIMPLE_DATE_FORMAT_ALL)));
        }
        if (TextUtils.isEmpty(data.priorityName)) {
            if (mPriority != null && data.priorityId != null) {
                mTvPriority.setText(mPriority.get(data.priorityId));
            }
        } else {
            mTvPriority.setText(data.priorityName);
        }

        refreshTag(data);
        mTvDep.setText(StringUtils.formatString(data.organizationName));
        mTvLocation.setText(StringUtils.formatString(data.location));
        if (!TextUtils.isEmpty(data.applicantPhone)) {
            tel = data.applicantPhone;
            mCall.setVisibility(View.VISIBLE);
        }
        mTvServiceType.setText(StringUtils.formatString(data.serviceTypeName));
        refreshTime(data);
        mTvDesc.setText(StringUtils.formatString(data.woDescription));
        //判断用户类型
        checkRole(mLocationId);
        updateMedia(data);
        updateApprovalContent(data);
        updateCategory(data);
        updateSpace(data);

        currentRoles = data.currentRoles;// 当前人员对该工单所拥有的角色权限数组
        showRightMenu(data.status, currentRoles,data.workOrderLaborers,data.isBunchingOrder);

        mRefreshLayout.finishRefresh(true);

    }

    /**
     * 判断是否需要展示右上角功能按钮
     */
    private void showRightMenu(Integer status, List<Integer> roles, List<WorkorderLaborerService.WorkorderLaborerBean> list,boolean isBunchingOrder) {
        if (status.equals(WorkorderConstant.WORK_STATUS_COMPLETED)) { //待存档状态下 需要验证和存档的权限
            if (isBunchingOrder){
                removeRightView();
            }else {
                if (getTagStatus() != null && getTagStatus().equals(WorkorderConstant.APPLICATION_VOID)){
                    if (!getPresenter().hasPermission(WorkorderConstant.VOID_PERMISSION, currentRoles)) {
                        removeRightView();
                    }
                }else {
                    if (!(getPresenter().hasPermission(WorkorderConstant.ARCHIVE_PERMISSION, roles)
                            || getPresenter().hasPermission(WorkorderConstant.VERIFIER_PERMISSION, roles))) {
                        removeRightView();
                    }
                }
            }

        } else if (status.equals(WorkorderConstant.WORK_STATUS_PROCESS)) {
            if (needShowRightMenu(list)) {
                if (getTagStatus() != null && getTagStatus().equals(WorkorderConstant.APPLICATION_FOR_SUSPENSION)) {
                    if (!(getPresenter().hasPermission(WorkorderConstant.PAUSE_PERMISSION, currentRoles))) {
                        removeRightView();
                    }
                }else if (getTagStatus() != null && getTagStatus().equals(WorkorderConstant.APPLICATION_VOID)){
                    if (!getPresenter().hasPermission(WorkorderConstant.VOID_PERMISSION, currentRoles)) {
                        removeRightView();
                    }
                }
            } else { //非执行人
                if (!getPresenter().hasPermission(WorkorderConstant.PAUSE_PERMISSION,currentRoles)
                ){
                    //处理中非执行人无操作
                    removeRightView();
                }else {
                    setMoreMenuVisible(true);
                }
                refreshStatus =  WorkorderConstant.WORK_STATUS_NONE;
                mLlInput.setVisibility(View.GONE);
            }


        } else if (status.equals(WorkorderConstant.WORK_STATUS_CREATED) || status.equals(WorkorderConstant.WORK_STATUS_PUBLISHED)) {
            if (getTagStatus() != null && getTagStatus().equals(WorkorderConstant.APPLICATION_VOID)){
                if (!getPresenter().hasPermission(WorkorderConstant.VOID_PERMISSION, currentRoles)) {
                    removeRightView();
                }
            }else {
                if (!getPresenter().hasPermission(WorkorderConstant.DISPATCH_STAFF_PERMISSION, currentRoles)) {
                    removeRightView();
                }
            }
        }else if (status.equals(WorkorderConstant.WORK_STATUS_TERMINATED)){
            if (getTagStatus() != null && getTagStatus().equals(WorkorderConstant.APPLICATION_VOID)){
                if (!getPresenter().hasPermission(WorkorderConstant.VOID_PERMISSION, currentRoles)) {
                    removeRightView();
                }
            }else {
                if (!getPresenter().hasPermission(WorkorderConstant.VERIFIER_PERMISSION, currentRoles)) {
                    removeRightView();
                }
            }
        }else if (status.equals(WorkorderConstant.WORK_STATUS_VERIFIED)){
            if (!getPresenter().hasPermission(WorkorderConstant.ARCHIVE_PERMISSION, currentRoles)) {
                removeRightView();
            }
        }else if (status.equals(WorkorderConstant.WORK_STATUS_SUSPENDED_GO)){
            if (!needShowRightMenu(list)) {
                removeRightView();
            }
        }
    }

    public boolean needShowRightMenu(List<WorkorderLaborerService.WorkorderLaborerBean> list) {
        Long emId = FM.getEmId();
        if (list != null && list.size() > 0 && emId != null) {
            for (WorkorderLaborerService.WorkorderLaborerBean workOrderLaborer : list) {
                if (emId.equals(workOrderLaborer.laborerId)) {
                    return true;
                }

            }
        }
        return false;
    }


    /**
     * 判断当前
     */
    public boolean isApprovalSupervisor() {
//        return approvalSupervisr;
        return true;
    }

//    /**
//     * 判断当前登录用户是否为当前工单的审批人
//     * */
//    public boolean isApproval(){
//        if (!fromMessage){
//            return true;
//        }
//        String json = SPUtils.getInstance(SPKey.SP_MODEL_USER).getString(SPKey.USER_INFO);
//        UserService.UserInfoBean userInfoBean = com.facilityone.wireless.basiclib.utils.GsonUtils.fromJson(json, UserService.UserInfoBean.class);
//        Long userEmId = userInfoBean.emId;
//        if (allApprovers != null && userEmId != null){
//            for (Long id : allApprovers) {
//                if (id.equals(userEmId)){
//                    return true;
//                }
//                return false;
//            }
//        }else {
//            return false;
//        }
//        return false;
//
//    }

    //维护步骤
    private void updateCategory(WorkorderService.WorkorderInfoBean data) {
        mSteps = (ArrayList<WorkorderService.StepsBean>) data.steps;
        if (isMaintenanceOrder && mSteps != null && mSteps.size() > 0) {
            mLlPlanStep.setVisibility(View.VISIBLE);
        } else {
            mLlPlanStep.setVisibility(View.GONE);
        }

        if (mSteps == null) {
            mSteps = new ArrayList<>();
        }
    }

    //空间位置
    private void updateSpace(WorkorderService.WorkorderInfoBean data) {
        //TODO 暂不开放
        mSpaceLocations = (ArrayList<WorkorderService.WorkOrderLocationsBean>) data.workOrderLocations;
        mNewSpace = (ArrayList<WorkorderService.PmSpaceBean>) data.pmPositions;

//        if (mCategory != null && mCategory == WorkorderConstant.WORK_TYPE_PM
//                && refreshStatus!= WorkorderConstant.WORK_STATUS_NONE
//                && (data.newStatus == WorkorderConstant.WORK_STATUS_PROCESS ||
//                data.newStatus == WorkorderConstant.WORK_STATUS_COMPLETED)){
//            if (mCanOpt || (mSpaceLocations != null && mSpaceLocations.size() > 0)) {
//                mLlSpace.setVisibility(View.VISIBLE);
//            } else {
//                mLlSpace.setVisibility(View.GONE);
//            }
//        } else {
//            mLlSpace.setVisibility(View.GONE);
//        }
//
//
//        if (mSpaceLocations == null) {
//            mSpaceLocations = new ArrayList<>();
//        }
//        mLlSpace.setVisibility(View.VISIBLE);
    }

    //显示或隐藏审批内容
    private void updateApprovalContent(WorkorderService.WorkorderInfoBean data) {
        mLlApprovalContent.setVisibility(View.GONE);
        if (refreshStatus == WorkorderConstant.WORK_STATUS_APPROVAL || mRealStatus == WorkorderConstant.WORK_NEW_STATUS_APPROVAL_WAIT) {
            mLlApprovalContent.setVisibility(View.VISIBLE);
            List<WorkorderService.ApprovalsBean> approvals = data.approvals;

            if (approvals != null && approvals.size() > 0) {
                StringBuilder sb = new StringBuilder();

                Map<Long, List<WorkorderService.ApprovalContentBean>> acMap = new HashMap<>();
                Map<Long, List<WorkorderService.ApprovalResultsBean>> arMap = new HashMap<>();
                List<Long> keys = new ArrayList<>();
                List<Long> resultKeyList = new ArrayList<>();

                for (WorkorderService.ApprovalsBean as : approvals) {
                    List<WorkorderService.ApprovalContentBean> approvalContent = new ArrayList<>();
                    List<WorkorderService.ApprovalResultsBean> approvalResults = new ArrayList<>();
                    approvalContent.addAll(as.approvalContent);
                    acMap.put(as.approvalId, approvalContent);
                    approvalResults.addAll(as.approvalResults);
                    arMap.put(as.approvalId, approvalResults);
                    keys.add(as.approvalId);
                }

                for (Long l : keys) {
                    List<WorkorderService.ApprovalResultsBean> arList = arMap.get(l);
                    if (arList != null) {
                        for (WorkorderService.ApprovalResultsBean ar : arList) {
                            if (ar.result != null) {
                                resultKeyList.add(l);
                                break;
                            }
                        }
                    }
                }

                keys.removeAll(resultKeyList);

                mApprovalContentBeanList.clear();
                for (Long l : keys) {
                    List<WorkorderService.ApprovalContentBean> acList = acMap.get(l);
                    if (acList != null) {
                        mApprovalContentBeanList.addAll(acList);
                    }
//                    for (WorkorderService.ApprovalContentBean ac : acList) {
//                        if (!TextUtils.isEmpty(ac.name)) {
//                            sb.append(ac.name + " : ");
//                            if (!TextUtils.isEmpty(ac.value)) {
//                                sb.append(ac.value);
//                            }
//                        }
//                    }
                }
                mApprovalContentAdapter.notifyDataSetChanged();

                if (mApprovalContentBeanList == null || mApprovalContentBeanList.size() == 0) {
//                    mTvApprovalContent.setVisibility(View.GONE);
                    mRvApprovalContent.setVisibility(View.GONE);
                    mViewApprovalContent.setVisibility(View.GONE);
                } else {
//                    mTvApprovalContent.setVisibility(View.VISIBLE);
                    mRvApprovalContent.setVisibility(View.VISIBLE);
                    mViewApprovalContent.setVisibility(View.VISIBLE);
//                    mTvApprovalContent.setText(sb.toString());
                }
            } else {
//                mTvApprovalContent.setVisibility(View.VISIBLE);
                mRvApprovalContent.setVisibility(View.VISIBLE);
                mViewApprovalContent.setVisibility(View.VISIBLE);
            }
        }
    }

    //根据工单状态显示和菜单内有无内容显示列表菜单
    private void showMenuList() {
        mLlInput.setVisibility(View.GONE);
        mLlSignature.setVisibility(View.GONE);

        if (refreshStatus == WorkorderConstant.WORK_STATUS_PROCESS) {
            //填写内容
            mLlInput.setVisibility(View.VISIBLE);
            //签字
            mLlSignature.setVisibility(View.VISIBLE);
            mLlSignatureCustomer.setVisibility(View.VISIBLE);
            mTvSignatureCustomer.setVisibility(View.VISIBLE);
            if (customerSignatureId == null) {
                isSignOn = true;
                mTvSignatureCustomer.setTextColor(getContext().getResources().getColor(R.color.grey_9));
                mTvSignatureCustomer.setText(R.string.workorder_hand_no_sign_tip);
            } else {
                isSignOn = false;
                mTvSignatureCustomer.setTextColor(getContext().getResources().getColor(R.color.green_1ab394));
                mTvSignatureCustomer.setText(R.string.workorder_hand_ok_tip);
            }
        }

        //签字
        if (customerSignatureId != null) {
            mLlSignature.setVisibility(View.VISIBLE);
            mLlSignatureCustomer.setVisibility(View.VISIBLE);
            mTvSignatureCustomer.setVisibility(View.VISIBLE);
        }

        if (directorSignatureId != null || (mStatus != WorkorderConstant.WORK_STATUS_NONE
                &&
                (refreshStatus == WorkorderConstant.WORK_STATUS_TERMINATED
                        || refreshStatus == WorkorderConstant.WORK_STATUS_COMPLETED
                ))) {
            mLlSignature.setVisibility(View.VISIBLE);
//            mLlSignatureDirector.setVisibility(View.VISIBLE);
            mLlSignatureDirector.setVisibility(View.GONE);
            if (directorSignatureId == null) {
                mTvSignatureDirector.setTextColor(getContext().getResources().getColor(R.color.grey_c));
                mTvSignatureDirector.setText(R.string.workorder_hand_no_sign_tip);
            } else {
                mTvSignatureDirector.setTextColor(getContext().getResources().getColor(R.color.green_1ab394));
                mTvSignatureDirector.setText(R.string.workorder_hand_ok_tip);
            }
        }

//        if (mLlSignatureCustomer.getVisibility() == View.VISIBLE && mLlSignatureDirector.getVisibility() == View.VISIBLE) {
//            mViewSignature.setVisibility(View.VISIBLE);
//        }
        if (mLlSignatureCustomer.getVisibility() == View.VISIBLE) {
            mViewSignature.setVisibility(View.VISIBLE);
        }
    }

    //刷新历史记录
    public void refreshHistoryUI(WorkorderService.WorkorderInfoBean data) {
        mHistoriesList = new ArrayList<>();
        if (data == null || data.histories == null || data.histories.size() == 0) {
            mLlRecord.setVisibility(View.GONE);
            return;
        }
        mHistoriesList.addAll(data.histories);
        List<WorkorderService.HistoriesBean> tempBeans = new ArrayList<>();
        WorkorderService.HistoriesBean historiesBean = mHistoriesList.get(mHistoriesList.size() - 1);
        tempBeans.add(historiesBean);

        mHistoryAdapter = new WorkorderHistoryAdapter(tempBeans, getContext(), true, this, getPresenter());
        mHistoryAdapter.setOnItemChildClickListener(this);
        mRvHistories.setAdapter(mHistoryAdapter);
        mLlRecord.setVisibility(View.VISIBLE);
    }

    //刷新执行人
    public void refreshLaborerUI(WorkorderService.WorkorderInfoBean data) {

        mAllStart = false;
        if (data.workOrderLaborers != null && data.workOrderLaborers.size() > 0) {
            workOrderLaborers = new ArrayList<>();
            mLaborer = getPresenter().processingLaborer(data.workOrderLaborers, mStatus, data.status);
            if (!mLaborer && refreshStatus == WorkorderConstant.WORK_STATUS_PUBLISHED) {
//                pop();
                hideShowMoreMenu(false);
                return;
            }
            workOrderLaborers.addAll(data.workOrderLaborers);
            int count = 0;
            for (WorkorderLaborerService.WorkorderLaborerBean workOrderLaborer : workOrderLaborers) {
                if (workOrderLaborer != null && workOrderLaborer.status != null && workOrderLaborer.status != WorkorderConstant.WORKORDER_STATUS_PERSONAL_UN_ACCEPT) {
                    count++;
                }
            }
            mAllStart = (count != 0 && count == workOrderLaborers.size());
            mRvExecutor.setLayoutManager(new LinearLayoutManager(getContext()));
            mWorkOrderLaborerAdapter = new WorkOrderLaborerAdapter(workOrderLaborers, getContext());
            mWorkOrderLaborerAdapter.setOnItemClickListener(this);
            mRvExecutor.setAdapter(mWorkOrderLaborerAdapter);
            mLlExecutor.setVisibility(View.VISIBLE);
        } else {
            mLlExecutor.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(data.sendWorkContent)) {
            mLlSendContent.setVisibility(View.VISIBLE);
            mTvSendContent.setText(data.sendWorkContent);
        } else {
            mLlSendContent.setVisibility(View.GONE);
        }
    }

    //设备
    public void refreshEquipmentUI(WorkorderService.WorkorderInfoBean data) {
        mWorkOrderEquipments = (ArrayList<WorkorderService.WorkOrderEquipmentsBean>) data.workOrderEquipments;
        Integer equStatus = data.newStatus;
        if ((equStatus == WorkorderConstant.WORK_NEW_STATUS_ARCHIVED_WAIT ||
                equStatus == WorkorderConstant.WORK_NEW_STATUS_PROCESS ||
                equStatus == WorkorderConstant.WORK_NEW_STATUS_APPROVAL_WAIT)) {
            mLlEqu.setVisibility(View.VISIBLE);
        } else {
            mLlEqu.setVisibility(View.GONE);
        }
    }

    //故障对象
    public void refreshFaultObject(WorkorderService.WorkorderInfoBean data) {
        if ((refreshStatus == WorkorderConstant.WORK_STATUS_PROCESS
                || refreshStatus == WorkorderConstant.WORK_STATUS_COMPLETED
                || refreshStatus == WorkorderConstant.WORK_STATUS_NONE)
                && !isMaintenanceOrder
                && (data.newStatus == WorkorderConstant.WORK_NEW_STATUS_PROCESS || data.newStatus == WorkorderConstant.WORK_NEW_STATUS_ARCHIVED_WAIT)
        ) {
            ll_object.setVisibility(View.VISIBLE);
            if (refreshStatus == WorkorderConstant.WORK_STATUS_COMPLETED
                    || refreshStatus == WorkorderConstant.WORK_STATUS_NONE) {
                ll_object.setClickable(false);
                tv_object_title.setText(data.componentName == null ? "" : data.componentName);
                mItcObject.setVisibility(View.GONE);
            } else {
                mItcObject.setVisibility(View.VISIBLE);
                ll_object.setClickable(true);
            }
        } else {
            ll_object.setVisibility(View.GONE);
        }
    }

    //物料
    public void refreshMaterialUI(List<WorkorderService.WorkorderReserveRocordBean> data) {
        mWorkOrderMaterials = data;
        if (refreshStatus == WorkorderConstant.WORK_STATUS_PROCESS) {
            mLlMaterial.setVisibility(View.VISIBLE);
        } else if (mWorkOrderMaterials != null && mWorkOrderMaterials.size() > 0) {
            mLlMaterial.setVisibility(View.VISIBLE);
        } else {
            mLlMaterial.setVisibility(View.GONE);
        }
    }

    //工具和收支明细
    public void refreshToolsAndBplUI(WorkorderService.WorkorderInfoBean data) {
        mLlTool.setVisibility(View.GONE);
        mLlBpDetail.setVisibility(View.GONE);
        mLlToolBp.setVisibility(View.GONE);
        mViewToolBpBottom.setVisibility(View.GONE);

        if (refreshStatus == WorkorderConstant.WORK_STATUS_PROCESS ||
                refreshStatus == WorkorderConstant.WORK_STATUS_COMPLETED ||
                refreshStatus == WorkorderConstant.WORK_STATUS_NONE
        ) {
            if ((data.newStatus == WorkorderConstant.WORK_NEW_STATUS_ARCHIVED_WAIT ||
                    data.newStatus == WorkorderConstant.WORK_NEW_STATUS_PROCESS)
                    && !isMaintenanceOrder) {
                //工具 收支明细
                mLlTool.setVisibility(View.VISIBLE);
                mLlBpDetail.setVisibility(View.GONE);
//                mLlToolBp.setVisibility(View.VISIBLE);
//                mViewToolBpBottom.setVisibility(View.VISIBLE);
            } else {
                //工具 收支明细
                mLlTool.setVisibility(View.GONE);
                mLlBpDetail.setVisibility(View.GONE);
                mLlToolBp.setVisibility(View.GONE);
                mViewToolBpBottom.setVisibility(View.GONE);
            }

        } else {
            mLlTool.setVisibility(View.GONE);
            mLlBpDetail.setVisibility(View.GONE);
//            mLlToolBp.setVisibility(View.GONE);
            mViewToolBpBottom.setVisibility(View.GONE);
        }

        boolean haveTools = false;
        mWorkOrderTools = (ArrayList<WorkorderService.WorkOrderToolsBean>) data.workOrderTools;
        if (mWorkOrderTools != null && mWorkOrderTools.size() > 0) {
            mLlTool.setVisibility(View.VISIBLE);
//            mViewToolBpBottom.setVisibility(View.VISIBLE);
            haveTools = true;
        } else {
            mWorkOrderTools = new ArrayList<>();
        }

//        toolTotal();

        mCharges = (ArrayList<WorkorderService.ChargesBean>) data.charges;

        if (mCharges != null && mCharges.size() > 0) {
            mLlBpDetail.setVisibility(View.GONE);
            if (haveTools) {
//                mLlToolBp.setVisibility(View.VISIBLE);
            } else {
//                mViewToolBpBottom.setVisibility(View.VISIBLE);
            }
        } else {
            mCharges = new ArrayList<>();
        }

        chargeTotal();
    }

    //刷新工单关联缴费单
    public void refreshPayment(WorkorderService.WorkorderInfoBean data) {
        mPayments = (ArrayList<WorkorderService.PaymentsBean>) data.payments;
        if (refreshStatus == WorkorderConstant.WORK_STATUS_PROCESS
                || refreshStatus == WorkorderConstant.WORK_STATUS_COMPLETED
                || refreshStatus == WorkorderConstant.WORK_STATUS_TERMINATED
                || refreshStatus == WorkorderConstant.WORK_STATUS_VERIFIED) {
//            mLlPay.setVisibility(View.VISIBLE); 原先代码逻辑
            mLlPay.setVisibility(View.GONE);
        } else if (mPayments != null && mPayments.size() > 0) {
//            mLlPay.setVisibility(View.VISIBLE);原先代码逻辑
            mLlPay.setVisibility(View.GONE);
        } else {
            mLlPay.setVisibility(View.GONE);
        }

        if (mPayments == null) {
            mPayments = new ArrayList<>();
        }
    }

    public Long getOperateReasonId() {
        return operateOrderReasonId;
    }

    public Long getFaultObjectId() {
        return faultObjectId;
    }

    private void updateMedia(WorkorderService.WorkorderInfoBean data) {
        if ((data.pictures != null && data.pictures.size() > 0)
                || (data.requirementVideos != null && data.requirementVideos.size() > 0)
                || (data.requirementAudios != null && data.requirementAudios.size() > 0)
                || (data.requirementPictures != null && data.requirementPictures.size() > 0)) {
            mExpand.setVisibility(View.VISIBLE);
        }
        image(data);
        audio(data);
        video(data);
        attachment(data);
    }

    private void image(WorkorderService.WorkorderInfoBean data) {
        List<String> totalImages = new ArrayList<>();
        if (data.pictures != null) {
            totalImages.addAll(data.pictures);
        }
        if (data.requirementPictures != null) {
            totalImages.addAll(data.requirementPictures);
        }
        if (totalImages.size() > 0) {
            tem.clear();
            mLocalMedias.clear();
            mRvPhoto.setVisibility(View.VISIBLE);
            for (String image : totalImages) {
                LocalMedia media = new LocalMedia();
                media.setPath(UrlUtils.getImagePath(image));
                media.setDuration(totalImages.size());
                media.setPictureType(PictureMimeType.JPEG);
                tem.add(media);
            }
            if (tem.size() > FullyGridLayoutManager.SPAN_COUNT) {
                List<LocalMedia> localMedias = tem.subList(0, FullyGridLayoutManager.SPAN_COUNT);
                mLocalMedias.addAll(localMedias);
            } else {
                if (tem.size() == FullyGridLayoutManager.SPAN_COUNT) {
                    LocalMedia localMedia = tem.get(FullyGridLayoutManager.SPAN_COUNT - 1);
                    localMedia.setDuration(-1L);
                }
                mLocalMedias.addAll(tem);
            }
            mGridImageAdapter.notifyDataSetChanged();
        } else {
            mRvPhoto.setVisibility(View.GONE);
        }
    }

    private void audio(final WorkorderService.WorkorderInfoBean data) {
        if (data.requirementAudios != null && data.requirementAudios.size() > 0) {
            mAudioSelectList.clear();
            initAudioPlayService();
            FMThreadUtils.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    final List<LocalMedia> t = new ArrayList<>();
                    for (String audio : data.requirementAudios) {
                        String mediaPath = UrlUtils.getMediaPath(audio);
                        LocalMedia localMedia = new LocalMedia();
                        localMedia.setDuration(UrlUtils.getRingDuring(mediaPath));
                        localMedia.setPath(mediaPath);
                        localMedia.setPictureType("audio/amr");
                        localMedia.setMimeType(PictureMimeType.ofAudio());
                        t.add(localMedia);
                    }

                    FM.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            mAudioSelectList.addAll(t);
                            mAudioAdapter.notifyDataSetChanged();
                            mRvAudio.setVisibility(View.VISIBLE);
                        }
                    });
                }
            });
        } else {
            mRvAudio.setVisibility(View.GONE);
        }
    }

    private void initAudioPlayService() {
        Intent intent = new Intent();
        intent.setClass(getActivity(), AudioPlayService.class);
        AudioPlayConnection audioPlayConnection = new AudioPlayConnection();
        getActivity().bindService(intent, audioPlayConnection, Context.BIND_AUTO_CREATE);
    }

    private void video(final WorkorderService.WorkorderInfoBean data) {
        if (data.requirementVideos != null && data.requirementVideos.size() > 0) {
            mVideoSelectList.clear();
            FMThreadUtils.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    final List<LocalMedia> tt = new ArrayList<>();
                    for (String video : data.requirementVideos) {
                        String mediaPath = UrlUtils.getMediaPath(video);
                        LocalMedia localMedia = new LocalMedia();
                        localMedia.setDuration(UrlUtils.getRingDuring(mediaPath));
                        localMedia.setPath(mediaPath);
                        localMedia.setPictureType("video/mp4");
                        localMedia.setMimeType(PictureMimeType.ofAudio());
                        tt.add(localMedia);
                    }
                    FM.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            mVideoSelectList.addAll(tt);
                            mVideoGridImageAdapter.notifyDataSetChanged();
                            mRvVideo.setVisibility(View.VISIBLE);
                        }
                    });
                }
            });
        } else {
            mRvVideo.setVisibility(View.GONE);
        }
    }

    private void attachment(WorkorderService.WorkorderInfoBean data) {
        if (data.attachment != null && data.attachment.size() > 0) {
            mAttachmentList.clear();
            showOrHideAttachments(true);
            for (AttachmentBean attachmentBean : data.attachment) {
                attachmentBean.url = UrlUtils.getAttachmentPath(attachmentBean.src);
            }
            mAttachmentList.addAll(data.attachment);
            mAttachmentAdapter.notifyDataSetChanged();
        } else {
            showOrHideAttachments(false);
        }
    }

    //附件
    private void showOrHideAttachments(boolean show) {
        mLlAttachment.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void refreshTime(WorkorderService.WorkorderInfoBean data) {
        String estimateStartTime = "";
        String estimateEndTime = "";
        if (data.estimateStartTime != null
                && data.estimateStartTime != 0) {
            estimateStartTime = TimeUtils.millis2String(data.estimateStartTime, DateUtils.SIMPLE_DATE_FORMAT_ALL);
        }

        if (data.estimateEndTime != null
                && data.estimateEndTime != 0) {
            estimateEndTime = TimeUtils.millis2String(data.estimateEndTime, DateUtils.SIMPLE_DATE_FORMAT_ALL);
        }

        if (!TextUtils.isEmpty(estimateStartTime) || !TextUtils.isEmpty(estimateEndTime)) {
            mLlForecastTime.setVisibility(View.VISIBLE);
            StringBuffer stringBuffer = new StringBuffer();
            if (!TextUtils.isEmpty(estimateStartTime)) {
                stringBuffer.append(estimateStartTime);
            }
            stringBuffer.append(" - ");
            if (!TextUtils.isEmpty(estimateEndTime)) {
                stringBuffer.append(estimateEndTime);
            }
            mTvForecastTime.setText(stringBuffer.toString());
        } else {
            mLlForecastTime.setVisibility(View.GONE);
        }
    }

    private void refreshTag(WorkorderService.WorkorderInfoBean data) {
        if (data.newStatus == null) {
            mTvStatus.setVisibility(View.INVISIBLE);
            return;
        }
        mTvStatus.setVisibility(View.VISIBLE);
        int resId = R.drawable.workorder_fill_grey_background;
        mTvStatus.setText(WorkorderHelper.getWorkNewStatusMap(getContext()).get(data.newStatus));
        switch (data.newStatus) {
            case WorkorderConstant.WORK_NEW_STATUS_DISPATCHING:
                resId = R.drawable.fm_workorder_tag_fill_created_bg;
                break;
            case WorkorderConstant.WORK_NEW_STATUS_PROCESS:
                resId = R.drawable.fm_workorder_tag_fill_process_bg;
                break;
            case WorkorderConstant.WORK_NEW_STATUS_ARCHIVED_WAIT:
                resId = R.drawable.fm_workorder_tag_fill_published_bg;
                break;
            case WorkorderConstant.WORK_NEW_STATUS_APPROVAL_WAIT:
                resId = R.drawable.fm_workorder_tag_fill_approval_bg;
                break;
            case WorkorderConstant.WORK_NEW_STATUS_ARCHIVED:
                resId = R.drawable.fm_workorder_tag_fill_archived_bg;
                break;
            case WorkorderConstant.WORK_NEW_STATUS_DESTORY:
                resId = R.drawable.fm_workorder_tag_fill_suspended_go_bg;
                removeRightView(); //已作废状态下无任何操作
                break;
        }
        mTvStatus.setBackgroundResource(resId);

        if (data.tag != null) {
            switch (data.tag) {
                case WorkorderConstant.APPLICATION_FOR_SUSPENSION:
                    mOrderTagTv.setBackgroundResource(R.drawable.fm_workorder_tag_fill_created_bg);
                    break;
                case WorkorderConstant.PAUSE_STILL_WORKING:
                    mOrderTagTv.setBackgroundResource(R.drawable.fm_workorder_tag_fill_created_bg);
                    break;
                case WorkorderConstant.PAUSE_NOT_WORKING:
                    mOrderTagTv.setBackgroundResource(R.drawable.fm_workorder_tag_fill_process_bg);
                    break;
                case WorkorderConstant.APPLICATION_VOID:
                    mOrderTagTv.setBackgroundResource(R.drawable.fm_workorder_tag_fill_process_bg);
                    break;
                case WorkorderConstant.STOP:
                    mOrderTagTv.setBackgroundResource(R.drawable.fm_workorder_tag_fill_suspended_go_bg);
                    break;
            }
            mOrderTagTv.setVisibility(View.VISIBLE);
            mOrderTagTv.setText(WorkorderHelper.getOrderTagStatusMap(getContext()).get(data.tag));
        } else {
            mOrderTagTv.setVisibility(View.GONE);
        }

    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        onRefresh();
    }

    public void refreshError() {
        ToastUtils.showShort(R.string.workorder_get_data_error);
        mRefreshLayout.finishRefresh(false);
//        mEui.showError();
    }

    public boolean canDo() {
        Boolean cando = false;
        if (!isMaintenanceOrder) { // 非计划性维护工单
            if (mTvToolTotal.getText().equals("") || mTvToolTotal.getText() == null) {
                ToastUtils.showShort("选择故障原因");
                cando = false;
            } else {
                if (workOrderContent == null || workOrderContent.equals("")) {
                    ToastUtils.showShort("请填写工作内容");
                    cando = false;
                } else {
                    cando = true;
                }
            }
        } else {
            if (workOrderContent == null || workOrderContent.equals("")) {
                ToastUtils.showShort("请填写工作内容");
                cando = false;
            } else {
                String reason = env_fault_object_reason.getDesc();
                if (!isMaintenanceOrder && operateOrderReasonId == 1 && (reason == null || reason.equals(""))) {
                    ToastUtils.showShort("请填写具体原因");
                    cando = false;
                } else {
                    cando = true;
                }

            }
        }
        return cando;
    }

    public String getOtherReason() {
        String reason = env_fault_object_reason.getDesc();
        return reason;
    }

    public boolean needReason() {
        if (!isMaintenanceOrder && operateOrderReasonId == 1) {
            return true;
        } else {
            return false;
        }
    }

    //判断权限问题后再根据 状态值操作
    public void NFCPression(boolean cando, Integer dowhat) {
        if (!cando) {
            ToastUtils.showShort("请先触碰房间NFC标签。");
            return;
        }
        switch (dowhat) {
            case WorkorderConstant.PRINT:
                startForResult(WorkorderInputFragment.getInstance(mWoId, mCode), INPUT_REQUEST_CODE);
                break;
            case WorkorderConstant.DEPATMENT:
                WorkorderDataHolder.setDeviceData(mWorkOrderEquipments);
                startForResult(WorkorderDeviceFragment.getInstance(fromQuery, mWoId, mCanOpt, mNeedScan, mTvDevice.getText().toString(), isMaintenanceOrder)
                        , FAULT_DEVICE);
                break;
            case WorkorderConstant.SIGNON:
                SignatureActivity.startActivityForResult(getActivity()
                        , this
                        , CUSTOMER_SIGNATURE_REQUEST_CODE
                        , FM.getApiHost() + WorkorderUrl.WORKORDER_SIGNATURE_UPLOAD_URL
                        , SignatureActivity.SIGNATURE_TYPE_CUSTOMER
                        , mWoId
                        , customerSignatureId);
                break;
            case WorkorderConstant.MATERIAL:
                // isMaintenanceOrder 维护工单
                int workOrderType;
                workOrderType = InventoryService.TYPE_FROM_WORKORDER_MAINTENANCE;
                start(WorkorderReserveRecordListFragment.getInstance(workOrderType, refreshStatus, mLaborer, mWoId, mCode));
                break;
            case WorkorderConstant.PLAN_STEP:
                startForResult(WorkorderStepFragment.getInstance(mSteps, mWoId, mWoTeamId, mCanOpt, countAccord, attention), STEP);
                break;


        }

    }

    /**
     * NFCConnect 判断当前工单是否可以进行操作--NFC标签是否进行过对接
     */
    public void WorkOrderCanDo(Integer doWhat) {
        //除待处理工单不做NFC校验
        if (mRealStatus != WorkorderConstant.WORK_NEW_STATUS_PROCESS) {
            NFCPression(true, doWhat);
        } else {
            getPresenter().NFCPremission(mWoId, doWhat);
        }

    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.itv_call) {//电话
            if (TextUtils.isEmpty(tel)) {
                return;
            }
            final String[] phone;
            if (!tel.contains("/")) {
                phone = new String[1];
                phone[0] = tel;
            } else {
                phone = tel.split("/");
            }
            PhoneMenuBuilder builder = new PhoneMenuBuilder(getContext());
            builder.addItems(phone, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    PhoneUtils.dial(phone[which]);
                    dialog.dismiss();
                }
            });
            builder.create(R.style.fmDefaultDialog).show();
        } else if (id == R.id.ll_expend) {//展开更多信息
            Boolean tag = (Boolean) v.getTag();
            if (tag == null) {
                tag = false;
            }
            v.setTag(!tag);
            expand(!tag);
        } else if (id == R.id.ll_input) {//进入输入页面
            if (isPending != 1) {
                startForResult(WorkorderInputFragment.getInstance(mWoId, mCode), INPUT_REQUEST_CODE);
            } else {
                if (hasAttendanceData) {
                    if (isAttendance) {
                        if (isMaintenanceOrder) {
                            WorkOrderCanDo(WorkorderConstant.PRINT);
                        } else {
                            startForResult(WorkorderInputFragment.getInstance(mWoId, mCode), INPUT_REQUEST_CODE);
                        }

                    } else {
                        ToastUtils.showShort(R.string.workorder_sign_error);
                    }
                } else {
                    ToastUtils.showShort("请先签到");
                }

            }


        } else if (id == R.id.ll_signature_customer) {//客户签字
            if (isPending != 1) {

                SignatureActivity.startActivityForResult(getActivity()
                        , this
                        , CUSTOMER_SIGNATURE_REQUEST_CODE
                        , FM.getApiHost() + WorkorderUrl.WORKORDER_SIGNATURE_UPLOAD_URL
                        , SignatureActivity.SIGNATURE_TYPE_CUSTOMER
                        , mWoId
                        , customerSignatureId);
            } else {
                if (hasAttendanceData) {
                    if (isAttendance) {
                        if (isMaintenanceOrder) {
                            WorkOrderCanDo(WorkorderConstant.SIGNON);
                        } else {
                            SignatureActivity.startActivityForResult(getActivity()
                                    , this
                                    , CUSTOMER_SIGNATURE_REQUEST_CODE
                                    , FM.getApiHost() + WorkorderUrl.WORKORDER_SIGNATURE_UPLOAD_URL
                                    , SignatureActivity.SIGNATURE_TYPE_CUSTOMER
                                    , mWoId
                                    , customerSignatureId);
                        }

                    } else {
                        ToastUtils.showShort(R.string.workorder_sign_error);
                    }
                } else {
                    ToastUtils.showShort("请先签到");
                }

            }


        } else if (id == R.id.ll_signature_director) {//主管签字


        } else if (id == R.id.ll_device) {//关联设备

            if (isPending != 1) {
//                boolean pm = mCategory != null && mCategory == WorkorderConstant.WORK_TYPE_PM;
//                if (!pm) {
//                    mNeedScan = false;
//                }
                WorkorderDataHolder.setDeviceData(mWorkOrderEquipments);
                startForResult(WorkorderDeviceFragment.getInstance(fromQuery, mWoId, mCanOpt, mNeedScan, mTvDevice.getText().toString(), isMaintenanceOrder)
                        , FAULT_DEVICE);

            } else {
                if (hasAttendanceData) {
                    if (isAttendance) {
//                        boolean pm = mCategory != null && mCategory == WorkorderConstant.WORK_TYPE_PM;
//                        if (!pm) {
//                            mNeedScan = false;
//                        }
                        if (isMaintenanceOrder) {
                            WorkOrderCanDo(WorkorderConstant.DEPATMENT);
                        } else {
                            WorkorderDataHolder.setDeviceData(mWorkOrderEquipments);
                            startForResult(WorkorderDeviceFragment.getInstance(fromQuery, mWoId, mCanOpt, mNeedScan, mTvDevice.getText().toString(), isMaintenanceOrder)
                                    , FAULT_DEVICE);
                        }
                    } else {
                        ToastUtils.showShort(R.string.workorder_sign_error);
                    }
                } else {
                    ToastUtils.showShort("请先签到");
                }

            }


        } else if (id == R.id.ll_material) {//物料

            if (isPending != 1) {
                String inventory = getResources().getString(R.string.home_inventory_permissions);
                boolean inventoryPermission = PermissionsManager.HomeFunction.getInstance().getFunctionPermission(inventory);
                if (inventoryPermission) {
                    if (refreshStatus == WorkorderConstant.WORK_STATUS_SUSPENDED_GO) {
                        ToastUtils.showShort(R.string.workorder_continue_tip);
                        return;
                    }
                    if (isMaintenanceOrder) {
                        WorkOrderCanDo(WorkorderConstant.MATERIAL);
                    } else {

                        int workOrderType = InventoryService.TYPE_FROM_WORKORDER;
                        LogUtils.d("当前页面" + workOrderType);
                        start(WorkorderReserveRecordListFragment.getInstance(workOrderType, refreshStatus, mLaborer, mWoId, mCode));
                    }


                } else {
                    ToastUtils.showShort(R.string.workorder_no_permission);
                }
            } else {
                if (hasAttendanceData) {
                    if (isAttendance) {
                        String inventory = getResources().getString(R.string.home_inventory_permissions);
                        boolean inventoryPermission = PermissionsManager.HomeFunction.getInstance().getFunctionPermission(inventory);
                        if (inventoryPermission) {
                            if (refreshStatus == WorkorderConstant.WORK_STATUS_SUSPENDED_GO) {
                                ToastUtils.showShort(R.string.workorder_continue_tip);
                                return;
                            }
                            if (isMaintenanceOrder) {
                                WorkOrderCanDo(WorkorderConstant.MATERIAL);
                            } else {
                                int workOrderType = InventoryService.TYPE_FROM_WORKORDER;
                                LogUtils.d("当前页面" + workOrderType);
                                start(WorkorderReserveRecordListFragment.getInstance(workOrderType, refreshStatus, mLaborer, mWoId, mCode));
                            }
                        } else {
                            ToastUtils.showShort(R.string.workorder_no_permission);
                        }
                    } else {
                        ToastUtils.showShort(R.string.workorder_sign_error);
                    }
                } else {
                    ToastUtils.showShort("请先签到");
                }

            }
        } else if (id == R.id.ll_pay) {//缴费单列表
            if (hasAttendanceData) {
                if (isAttendance) {
                    String pay = getResources().getString(R.string.home_pay_permissions);
                    boolean payPermission = PermissionsManager.HomeFunction.getInstance().getFunctionPermission(pay);
                    if (payPermission) {
                        if (mSpaceLocations == null || mSpaceLocations.size() == 0) {
                            startForResult(WorkorderPaymentFragment.getInstance(mPayments, mLocationId, refreshStatus, mWoId, mCanOpt), PAYMENT);
                        } else {
                            startForResult(WorkorderPaymentFragment.getInstance(mPayments, mSpaceLocations, refreshStatus, mWoId, mCanOpt), PAYMENT);
                        }
                    } else {
                        ToastUtils.showShort(R.string.workorder_no_permission);
                    }
                } else {
                    ToastUtils.showShort(R.string.workorder_sign_error);
                }
            } else {
                ToastUtils.showShort("请先签到");
            }

        } else if (id == R.id.ll_tool) {// 故障原因
            if (refreshStatus == WorkorderConstant.WORK_STATUS_PROCESS ) {
                if (hasAttendanceData) {
                    if (isAttendance) {
                        startForResult(
                                SelectDataFragment.getInstance(ISelectDataService.DATA_TYPE_REASON, ISelectDataService.REASON_TYPE_EXCEPTION),
                                CAUSE_REASON);
                    } else {
                        ToastUtils.showShort(R.string.workorder_sign_error);
                    }
                } else {
                    ToastUtils.showShort("请先签到");
                }
            }


        } else if (id == R.id.ll_bp_detail) {//收支明细
            if (hasAttendanceData) {
                if (isAttendance) {
                    startForResult(WorkorderChargeFragment.getInstance(mCharges, mWoId, mToolCost, mCanOpt)
                            , CHARGE);
                } else {
                    ToastUtils.showShort(R.string.workorder_sign_error);
                }

            } else {
                ToastUtils.showShort("请先签到");
            }

        } else if (id == R.id.ll_plan_step) {//维护步骤
            if (hasAttendanceData) {
                if (isAttendance) {
                    if (isMaintenanceOrder){
                        //暂定 把是否开启任务判断关闭
//                        getPresenter().isDoneDevice();
                        WorkOrderCanDo(WorkorderConstant.PLAN_STEP);
                    }else {
                        WorkOrderCanDo(WorkorderConstant.PLAN_STEP);
                    }
                } else {
                    ToastUtils.showShort(R.string.workorder_sign_error);
                }

            } else {
                ToastUtils.showShort("请先签到");
            }


        } else if (id == R.id.ll_space) {//空间位置
            if (hasAttendanceData) {
                if (isAttendance) {
                    Intent intent = new Intent(getActivity(), WorkOrderNfcList.class);
                    intent.putExtra(WORKORDER_LOCATION, mNewSpace);
                    intent.putExtra(CAN_OPT, mCanOpt);
                    intent.putExtra(WORKORDER_ID, mWoId);
                    intent.putExtra(WORKORDER_STATUS, refreshStatus);
                    getContext().startActivity(intent);
                } else {
                    ToastUtils.showShort(R.string.workorder_sign_error);
                }
            } else {
                ToastUtils.showShort("请先签到");
            }
        } else if (id == R.id.ll_object) { //故障对象
            //2021-11-30 发布
            if (hasAttendanceData) {
                if (isAttendance) {
                    startForResult(
                            SelectDataFragment.getInstance(ISelectDataService.DATA_TYPE_FAULT_OBJECT, ISelectDataService.REASON_TYPE_DEFAULT_OBJECT),
                            CAUSE_DEFAULT_OBJECT);
                } else {
                    ToastUtils.showShort(R.string.workorder_sign_error);
                }
            } else {
                ToastUtils.showShort("请先签到");
            }
        }
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        if (adapter == mAttachmentAdapter) {
            //附件
            AttachmentBean attachmentBean = mAttachmentList.get(position);
            getPresenter().openAttachment(attachmentBean.url, attachmentBean.name, getContext());
        } else if (adapter == mHistoryAdapter) {
            //显示全部历史记录
            start(WorkorderHistoryFragment.getInstance(mHistoriesList));
        }
    }

    public void setTaskStatus(boolean cando) {
        hasUnFinshTask = cando;
    }

    private void expand(Boolean expand) {
        mExpand.setText(expand ? R.string.icon_arrow_up : R.string.icon_arrow_down);
        mLlMedia.setVisibility(expand ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onRemove(int position) {

    }

    @Override
    public void onAudioClick(ImageView imageView, String path, int position) {
        if (AudioPlayManager.getAudioPlayService() != null) {
            AudioPlayManager.getAudioPlayService().setImageView(imageView);
            AudioPlayManager.getAudioPlayService().stopPlayVoiceAnimation();
            AudioPlayManager.getAudioPlayService().play(path);
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (adapter == mGridImageAdapter) {
            //图片
            PictureSelector.create(this)
                    .themeStyle(R.style.picture_fm_style)
                    .openExternalPreview(position, tem);
        } else if (adapter == mVideoGridImageAdapter) {
            // 预览视频
            LocalMedia localMedia = mVideoSelectList.get(position);
            SimplePlayer.startActivity(this, localMedia.getPath());
        } else if (adapter == mAttachmentAdapter) {
            //附件
            AttachmentBean attachmentBean = mAttachmentList.get(position);
            getPresenter().openAttachment(attachmentBean.url, attachmentBean.name, getContext());
        } else if (mWorkOrderLaborerAdapter != null && adapter == mWorkOrderLaborerAdapter) {
            //执行人
            if (workOrderLaborers != null && workOrderLaborers.size() > position && workOrderLaborers.get(position).canOpt) {
                laborerPosition = position;
                WorkorderLaborerService.WorkorderLaborerBean workOrderLaborersBean = workOrderLaborers.get(position);
                startForResult(WorkorderExecutorFragment.getInstance(mWoId,
                        workOrderLaborersBean.laborer,
                        workOrderLaborersBean.laborerId,
                        workOrderLaborersBean.actualArrivalDateTime,
                        workOrderLaborersBean.actualCompletionDateTime), LABORER_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        onRefresh();
    }


    @Override
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (requestCode == TOOLS) {
//            toolTotal();
        } else if (requestCode == CHARGE) {
            chargeTotal();
        } else if (requestCode == PAYMENT) {
            onRefresh();
        }

        if (resultCode == REFRESH) {
            onRefresh();
            return;
        }


        if (resultCode == REFRESH_POP) {
            pop();
            return;
        }

        if (resultCode != RESULT_OK || data == null) {
            return;
        }


        switch (requestCode) {
            case LABORER_REQUEST_CODE:
                laborerRefresh(data);
                break;
            case DISPATCH_REQUEST_CODE:
                refreshStatus = WorkorderConstant.WORK_STATUS_PUBLISHED;
                setNeedJump(true);
                pop();
                break;
            case APPROVAL_REQUEST_CODE:
                refreshStatus = WorkorderConstant.WORK_STATUS_APPROVAL;
                setNeedJump(true);
                pop();
                break;
            case INPUT_REQUEST_CODE:
            case CUSTOMER_SIGNATURE_REQUEST_CODE:
            case FAULT_DEVICE:
                mRefreshLayout.autoRefresh();
                break;
            case REQUEST_REASON:
                pauseDialogBuilder.setReasonData(data);
                pauseDialog.show();
                break;
            case REQUEST_INVALID:
                SelectDataBean reason = data.getParcelable(ISelectDataService.SELECT_OFFLINE_DATA_BACK);
                if (reason != null) {
                    builderChoice.getDescEt().setText(reason.getFullName() + "");
                    InvalidId = reason.getId();
                }
                buildInvalid.show();
                break;
            case CAUSE_REASON:
                SelectDataBean cause_reason = data.getParcelable(ISelectDataService.SELECT_OFFLINE_DATA_BACK);
                if (cause_reason != null) {
                    mTvToolTotal.setText(cause_reason.getName() +
                            ((cause_reason.getDesc() == null || cause_reason.getDesc().equals("")) ?
                                    "" : "：" + cause_reason.getDesc()) + "");
                    operateOrderReasonId = cause_reason.getId(); //获取ID
                    Log.i("你说你是超级英雄", "onFragmentResult: " + "故障原因对象ID：" + operateOrderReasonId);
                    if (operateOrderReasonId == 1) {
                        ll_fault_object_reason.setVisibility(View.VISIBLE);
                    } else {
                        ll_fault_object_reason.setVisibility(View.GONE);
                    }
                }
                break;
            case CAUSE_DEFAULT_OBJECT: //故障对象
                SelectDataBean cause_object = data.getParcelable(ISelectDataService.SELECT_OFFLINE_DATA_BACK);
                if (cause_object != null) {
                    tv_object_title.setText(cause_object.getName() +
                            ((cause_object.getDesc() == null || cause_object.getDesc().equals("")) ?
                                    "" : "：" + cause_object.getDesc()) + "");
                    if (cause_object.getId() != null) {
                        faultObjectId = cause_object.getId();
                    }

                }
                break;

        }
    }

    private void toolTotal() {
        double toolTotal = 0;
        if (mWorkOrderTools != null && mWorkOrderTools.size() > 0) {
            for (WorkorderService.WorkOrderToolsBean workOrderToolsBean : mWorkOrderTools) {
                if (workOrderToolsBean.amount != null && workOrderToolsBean.cost != null) {
                    toolTotal += (workOrderToolsBean.amount * workOrderToolsBean.cost);
                }
            }
        }
        mToolCost.amount = toolTotal;
        mTvToolTotal.setText("¥ " + StringUtils.double2String(toolTotal));
    }

    private void chargeTotal() {
        double chargeTotal = 0;
        if (mCharges != null && mCharges.size() > 0) {
            for (WorkorderService.ChargesBean charge : mCharges) {
                if (charge.amount != null) {
                    chargeTotal += charge.amount;
                }
            }
        }
        mTvChargeTotal.setText("¥ " + StringUtils.double2String(chargeTotal));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case CUSTOMER_SIGNATURE_REQUEST_CODE:
                mRefreshLayout.autoRefresh();
                break;
        }
    }

    private void laborerRefresh(Bundle data) {
        Long startTime = data.getLong(ARRIVAL_DATE_TIME);
        Long endTime = data.getLong(ARRIVAL_DATE_END_TIME);
        if (startTime != 0L) {
            workOrderLaborers.get(laborerPosition).actualArrivalDateTime = startTime;
        }

        if (endTime != 0L) {
            workOrderLaborers.get(laborerPosition).actualCompletionDateTime = endTime;
        }

        if (mWorkOrderLaborerAdapter != null) {
            mWorkOrderLaborerAdapter.notifyItemChanged(laborerPosition);
        }
    }

    public void setRefreshStatus(int refreshStatus) {
        this.refreshStatus = refreshStatus;
    }

    public void setLaborer(boolean laborer) {
        this.mLaborer = laborer;
    }

    public int getStatus() {
        return mStatus;
    }

    public void setNeedJump(boolean needJump) {
        mNeedJump = needJump;
    }

    /**
     * 关联设备需要全部完成 pm 扫码的
     *
     * @return
     */
    public int isAllDeviceFinished() {
        if (!mNeedScan || mWorkOrderEquipments == null || mWorkOrderEquipments.size() == 0) {
            return 0;
        }
        int count = 0;
        for (WorkorderService.WorkOrderEquipmentsBean workOrderEquipment : mWorkOrderEquipments) {
            if (workOrderEquipment.finished != null && workOrderEquipment.finished == WorkorderConstant.WO_EQU_STAT_FINISHED) {
                count++;
            }
        }

        return mWorkOrderEquipments.size() - count;
    }

    @Override
    public void onDestroyView() {
        Bundle bundle = new Bundle();
        bundle.putInt(WorkorderInfoFragment.WORKORDER_STATUS, refreshStatus);
        bundle.putBoolean(WorkorderInfoFragment.WORKORDER_NEED_JUMP, mNeedJump);
        setFragmentResult(RESULT_OK, bundle);
        super.onDestroyView();
        if (AudioPlayManager.getAudioPlayService() != null) {
            if (AudioPlayManager.getAudioPlayService().isPlaying()) {
                AudioPlayManager.getAudioPlayService().stopPlaying();
            }
            AudioPlayManager.getAudioPlayService().stopPlayVoiceAnimation();
            AudioPlayManager.getAudioPlayService().quit();
        }
        getPresenter().clearAttachment();
    }

    public SmartRefreshLayout getRefreshLayout() {
        return mRefreshLayout;
    }

    public boolean isAllStart() {
        return mAllStart;
    }

    public void setPriority(Map<Long, String> priority) {
        mPriority = priority;
    }

    public static WorkorderInfoFragment getInstance(Long mWoId) {
        WorkorderInfoFragment infoFragment = new WorkorderInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(WORKORDER_ID, mWoId);
        infoFragment.setArguments(bundle);
        return infoFragment;
    }

    public static WorkorderInfoFragment getInstance(int workorderStatus, String code, Long woId) {
        Bundle bundle = new Bundle();
        bundle.putInt(WORKORDER_STATUS, workorderStatus);
        bundle.putString(WORKORDER_CODE, code);
        bundle.putLong(WORKORDER_ID, woId);
        WorkorderInfoFragment infoFragment = new WorkorderInfoFragment();
        infoFragment.setArguments(bundle);
        return infoFragment;
    }

    public static WorkorderInfoFragment getInstance(boolean fromMessage, int workorderStatus, String code, Long woId) {
        Bundle bundle = new Bundle();
        bundle.putInt(WORKORDER_STATUS, workorderStatus);
        bundle.putString(WORKORDER_CODE, code);
        bundle.putLong(WORKORDER_ID, woId);
        bundle.putBoolean(FROME_MESSAGE, fromMessage);
        WorkorderInfoFragment infoFragment = new WorkorderInfoFragment();
        infoFragment.setArguments(bundle);
        return infoFragment;
    }

    public static WorkorderInfoFragment getInstance(int workorderStatus, String code, Boolean isException, Long woId) {
        Bundle bundle = new Bundle();
        if (isException) {
            bundle.putInt(WORKORDER_STATUS, WorkorderConstant.WORK_STATUS_UBNORMAL);
        } else {
            bundle.putInt(WORKORDER_STATUS, workorderStatus);
        }

        bundle.putString(WORKORDER_CODE, code);
        bundle.putLong(WORKORDER_ID, woId);
        WorkorderInfoFragment infoFragment = new WorkorderInfoFragment();
        infoFragment.setArguments(bundle);
        return infoFragment;
    }

    public static WorkorderInfoFragment getInstance(int workorderStatus, String code, Long woId, boolean isMaintenance) {
        Bundle bundle = new Bundle();
        bundle.putInt(WORKORDER_STATUS, workorderStatus);
        bundle.putString(WORKORDER_CODE, code);
        bundle.putLong(WORKORDER_ID, woId);
        bundle.putBoolean(IS_MAINTENANCE, isMaintenance);
        WorkorderInfoFragment infoFragment = new WorkorderInfoFragment();
        infoFragment.setArguments(bundle);
        return infoFragment;
    }


    //待存档
    public static WorkorderInfoFragment getInstance(int workorderStatus, String code, Long woId, boolean isMaintenance, boolean isFinish) {
        Bundle bundle = new Bundle();
        bundle.putInt(WORKORDER_STATUS, workorderStatus);
        bundle.putString(WORKORDER_CODE, code);
        bundle.putLong(WORKORDER_ID, woId);
        bundle.putBoolean(IS_MAINTENANCE, isMaintenance);
        bundle.putBoolean(IS_FINISH, isFinish);

        WorkorderInfoFragment infoFragment = new WorkorderInfoFragment();
        infoFragment.setArguments(bundle);
        return infoFragment;
    }


    //处理
    public static WorkorderInfoFragment getInstance(int workorderStatus, String code, Long woId, Integer isPending, boolean isMaintenance) {
        Bundle bundle = new Bundle();
        bundle.putInt(WORKORDER_STATUS, workorderStatus);
        bundle.putString(WORKORDER_CODE, code);
        bundle.putLong(WORKORDER_ID, woId);
        if (isPending == null) {
            bundle.putInt(IS_PENDING, -1);
        } else {
            bundle.putInt(IS_PENDING, isPending);
        }
        bundle.putBoolean(IS_MAINTENANCE, isMaintenance);
        WorkorderInfoFragment infoFragment = new WorkorderInfoFragment();
        infoFragment.setArguments(bundle);
        return infoFragment;
    }


    //显示暂停弹窗
    public void showPauseDialog(Context context, Long woId) {
        Long sWoId = woId;
        pauseDialogBuilder = new FMBottomPauseSelectSheetBuilder(context);
        pauseDialogBuilder.setOnPauseInputListener(new FMBottomPauseSelectSheetBuilder.OnPauseInputBtnClickListener() {

            @Override
            public void onLeftClick(QMUIBottomSheet dialog, SelectDataBean dataBean, Long time) {
                if (time != null & dataBean != null) {
                    if (time < System.currentTimeMillis()) {
                        ToastUtils.showShort("暂停结束时间必须晚于当前时间,请重新选择");
                    } else {
                        dialog.dismiss();
                        getPresenter().pauseWorkOrder(woId, dataBean.getDesc(), dataBean.getId(), WorkorderConstant.WORKORDER_OPT_TYPE_PAUSE_NO_FURTHER, time);
                    }
                } else {
                    if (dataBean == null) {
                        ToastUtils.showShort("请选择暂停原因");
                    } else {
                        ToastUtils.showShort("请重新选择暂停结束时间");
                    }

                }


            }

            @Override
            public void onRightClick(QMUIBottomSheet dialog, SelectDataBean dataBean, Long time) {
                if (time != null & dataBean != null) {
                    if (time < System.currentTimeMillis()) {
                        ToastUtils.showShort("暂停结束时间必须晚于当前时间,请重新选择");
                    } else {
                        dialog.dismiss();
                        getPresenter().pauseWorkOrder(woId, dataBean.getDesc(), dataBean.getId(), WorkorderConstant.WORKORDER_OPT_TYPE_PAUSE_CONTINUED, time);
                    }
                } else {
                    if (dataBean == null) {
                        ToastUtils.showShort("请选择暂停原因");
                    } else {
                        ToastUtils.showShort("请重新选择暂停结束时间");
                    }
                }

            }
        });
        pauseDialog = pauseDialogBuilder.build();
        pauseDialogBuilder.getLLTwoBtn().setVisibility(View.VISIBLE);
        pauseDialogBuilder.getDescSelect().setVisibility(View.VISIBLE);
        pauseDialogBuilder.getDescSelect().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseDialog.hide();
                startForResult(
                        SelectDataFragment.getInstance(ISelectDataService.DATA_TYPE_REASON, ISelectDataService.REASON_TYPE_PAUSE),
                        REQUEST_REASON
                );
            }
        });
        pauseDialogBuilder.setTitle(R.string.workorder_pause_tip);
        pauseDialogBuilder.setShowTip(true);
        pauseDialogBuilder.setTip(R.string.workorder_pause_tip_a);
        pauseDialogBuilder.setDescHint(R.string.workorder_pause_reason_hint);
        pauseDialogBuilder.getRightBtn().setBackgroundResource(R.drawable.btn_common_bg_selector);
        pauseDialogBuilder.setTwoBtnLeftInput(false);
        pauseDialogBuilder.setTwoBtnRightInput(false);
        pauseDialogBuilder.setLeftBtnText(R.string.workorder_over_order);
        pauseDialogBuilder.setRightBtnText(R.string.workorder_continue_order);
        pauseDialog.setCanceledOnTouchOutside(true);
        pauseDialog.show();
    }


    /**
     * @Created by: kuuga
     * @Date: on 2021/8/27 9:56
     * @Description: //设置签到可操作
     */
    public void canOpt(boolean canOpt, boolean hasData) {
        this.isAttendance = canOpt;
        this.hasAttendanceData = hasData;
        refreshNeedSign(canOpt, hasData);

    }

    /**
     * @Creator:Karelie
     * @Data: 2021/11/16
     * @TIME: 12:12
     * @Introduce: 部分逻辑需要签到状态 于是于此判断
     **/
    public void refreshNeedSign(boolean isAttendance, boolean hasAttendanceData) {
        //判断空间位置是否需要打开
        //是否签到 是否是维护工单 状态为处理中 待存档
        if (hasAttendanceData &&
                isMaintenanceOrder &&
                (refreshStatus == WorkorderConstant.WORK_STATUS_PROCESS
                        || refreshStatus == WorkorderConstant.WORK_STATUS_COMPLETED
                        || refreshStatus == WorkorderConstant.WORK_STATUS_NONE
                        || mRealStatus == WorkorderConstant.WORK_NEW_STATUS_APPROVAL_WAIT
                )) {
            mLlSpace.setVisibility(View.VISIBLE);
        } else {
            mLlSpace.setVisibility(View.GONE);
        }
    }


    /**
     * @Created by: kuuga
     * @Date: on 2021/8/27 9:56
     * @Description: 判断用户类型
     */
    public void checkRole(LocationBean bean) {
        String userInfo = SPUtils.getInstance(SPKey.SP_MODEL_USER).getString(SPKey.USER_INFO);
        UserService.UserInfoBean infoBean = GsonUtils.fromJson(userInfo, UserService.UserInfoBean.class);
        if (infoBean.type == 1 && mRealStatus == WorkorderConstant.WORKORER_PROCESS) {
            getPresenter().getLastAttendance(bean);
        } else {
            canOpt(true, true);
        }
    }

}
