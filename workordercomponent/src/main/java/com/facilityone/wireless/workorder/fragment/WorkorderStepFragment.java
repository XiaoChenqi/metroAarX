package com.facilityone.wireless.workorder.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.facilityone.wireless.a.arch.mvp.BaseFragment;
import com.facilityone.wireless.a.arch.widget.FMWarnDialogBuilder;
import com.facilityone.wireless.basiclib.app.FM;
import com.facilityone.wireless.basiclib.utils.GsonUtils;
import com.facilityone.wireless.workorder.R;
import com.facilityone.wireless.workorder.adapter.WorkorderStepAdapter;
import com.facilityone.wireless.workorder.module.WorkorderConstant;
import com.facilityone.wireless.workorder.module.WorkorderOptService;
import com.facilityone.wireless.workorder.module.WorkorderService;
import com.facilityone.wireless.workorder.presenter.WorkorderStepPresenter;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Author：gary
 * Email: xuhaozv@163.com
 * description:维护步骤列表
 * Date: 2018/9/28 2:18 PM
 */
public class WorkorderStepFragment extends BaseFragment<WorkorderStepPresenter> implements BaseQuickAdapter.OnItemChildClickListener {

    private RecyclerView mRecyclerView;
    private SmartRefreshLayout mRefreshLayout;

    private WorkorderStepAdapter mAdapter;

    private static final String WORKORDER_STEPS = "workorder_steps";
    private static final String CAN_OPT = "can_opt";
    private static final String WORKORDER_ID = "workorder_id";
    private static final String WORK_TEAM_ID = "work_team_id";
    private static final String COUNT_ACCORD = "count_accord";
    private static final String ATTENTION = "attention";
    private static final String EQCODE = "eqcode";
    private static final String EQID = "eqid";
    private static final String SHOWDIALOG = "showdialog";
    private static final int UPDATE = 7000;
    private static final int REFRESH = 1001;

    private ArrayList<WorkorderService.StepsBean> mSteps;
    private List<WorkorderService.WorkTeamEntity> mWorkTeams;
    private boolean mCanOpt;
    private long mWoId;
    private Long mWoTeamId;
    private Boolean countAccord;//是否需要输入设备数量
    private String attention; //注意事项
    private Integer time; //完成任务的时间--Min
    private boolean isMaintence; //是否是维护工单
    private String eqCode; //设备编号
    private Long eqId; //设备编号Id
    private Boolean showDialog; //Shi否需要展示弹窗提示任务开启 -> 工单界面不需要弹
    boolean[] mStepStatus;//当前用户与步骤中工作组可操作表

    @Override

    public WorkorderStepPresenter createPresenter() {
        return new WorkorderStepPresenter();
    }

    @Override
    public Object setLayout() {
        return R.layout.fragment_workorder_step_list;
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
        Bundle arguments = getArguments();
        mWorkTeams = new ArrayList<>();

        if (arguments != null) {
            mWoId = arguments.getLong(WORKORDER_ID, -1L);
            mWoTeamId = arguments.getLong(WORK_TEAM_ID, -1L);
            mSteps = arguments.getParcelableArrayList(WORKORDER_STEPS);
            mCanOpt = arguments.getBoolean(CAN_OPT, false);
            countAccord = arguments.getBoolean(COUNT_ACCORD);
            attention = arguments.getString(ATTENTION);
            eqCode = arguments.getString(EQCODE);
            eqId = arguments.getLong(EQID);
            showDialog = arguments.getBoolean(SHOWDIALOG);
        }
        getPresenter().getWorkTeams(FM.getEmId());
        if (showDialog){
            getPresenter().isDoneDevice(mWoId,eqCode);
        }

    }
    public void initWorkTeams(List<WorkorderService.WorkTeamEntity> workTeamEntityList){
        mWorkTeams=workTeamEntityList;
        mStepStatus = WorkOrderStepUtils.genStepStatusByWorkTeamId(mSteps,mWorkTeams);
        System.out.println(GsonUtils.toJson(mStepStatus));
    }


    public void initStepView(){
        getPresenter().getStepInfor(mWoId);
    }

    public void refreshStep(WorkorderService.WorkorderInfoBean data){
        if (data.steps != null && data.steps.size() > 0){
//            mSteps.clear();
//            mSteps.addAll(data.steps);
            mAdapter.replaceData(data.steps);
        }
    }
    public void setTaskTime(Integer min){
        time = min;
    }

    public void setHasDoneDevice(Boolean cando){
        if (cando){
            showTaskDialog(time);
        }
    }

    /**
     * @Creator:Karelie
     * @Data: 2021/10/12
     * @TIME: 14:58
     * @Introduce: 弹出弹窗提示用户是否需要开启任务 （当前没有开启任务）
     **/
    public void showTaskDialog(Integer time){
        FMWarnDialogBuilder builder = new FMWarnDialogBuilder(getContext());
        builder.setTitle("提示");
        builder.setCanceledOnTouchOutside(false);
        String messageFormat="如果您确认开启该维护计划，最少需要%s分钟才能完成提交。同时不能开启其他设备的维护计划。";
        builder.setTip(String.format(messageFormat,time));
        builder.addOnBtnCancelClickListener(new FMWarnDialogBuilder.OnBtnClickListener() {
            @Override
            public void onClick(QMUIDialog dialog, View view) {
                pop();
                dialog.dismiss();
            }
        });
        builder.addOnBtnSureClickListener(new FMWarnDialogBuilder.OnBtnClickListener() {
            @Override
            public void onClick(QMUIDialog dialog, View view) {
                dialog.dismiss();
                getPresenter().beganToTask(mWoId,eqCode);

            }
        });
        builder.create(R.style.fmDefaultWarnDialog).show();
    }


    private void initView() {
        setTitle(R.string.workorder_menu_step);
        mRecyclerView = findViewById(R.id.recyclerView);
        mRefreshLayout = findViewById(R.id.refreshLayout);

        mAdapter = new WorkorderStepAdapter(mSteps, getActivity(), this);
        mAdapter.setOnItemChildClickListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);

        if (mSteps == null || mSteps.size() == 0) {
            View noDataView = getNoDataView((ViewGroup) mRecyclerView.getParent());
            mAdapter.setEmptyView(noDataView);
        }
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        WorkorderService.StepsBean stepsBean = mSteps.get(position);
        //获取当前是否在工作组中
        boolean isInWorkTeam = mStepStatus[position];
        if (mCanOpt
                && stepsBean != null
                && stepsBean.workTeamId != null
                && isInWorkTeam) {
            startForResult(WorkorderStepUpdateFragment.getInstance(stepsBean, mWoId,countAccord,attention,position,mStepStatus), UPDATE);
        }
    }

    @Override
    public void leftBackListener() {
        Bundle bundle = new Bundle();
        setFragmentResult(REFRESH, bundle);
        pop();
    }

    @Override
    public boolean onBackPressedSupport() {
        Bundle bundle = new Bundle();
        setFragmentResult(REFRESH, bundle);
        pop();
        return true;
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            initStepView();
        }else if (resultCode == REFRESH){
            initStepView();
        }
    }


    public void workStart(){
        WorkorderOptService.WorkOrderDeviceReq request = new WorkorderOptService.WorkOrderDeviceReq();
        request.woId = mWoId;
        request.failureDesc = "";
        request.repairDesc = "";
        request.operateType = WorkorderConstant.WORKORDER_DEVICE_UPDATE_OPT_TYPE;
        request.equipmentId = eqId;
        getPresenter().editorWorkorderDevice(request);
    }

    public static WorkorderStepFragment getInstance(ArrayList<WorkorderService.StepsBean> s
            , Long woId
            , Long workTeamId
            , boolean canOpt
            , boolean eqCountAccord //是否需要输入设备数量
            , String mattersNeedingAttention //注意事项
    ) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(WORKORDER_STEPS, s);
        bundle.putLong(WORKORDER_ID, woId);
        bundle.putLong(WORK_TEAM_ID, workTeamId);
        bundle.putBoolean(CAN_OPT, canOpt);
        bundle.putBoolean(COUNT_ACCORD,eqCountAccord);
        bundle.putString(ATTENTION,mattersNeedingAttention);
        bundle.putBoolean(SHOWDIALOG,false); //默认值，从工单详情进入不需要展示弹窗
        WorkorderStepFragment instance = new WorkorderStepFragment();
        instance.setArguments(bundle);
        return instance;
    }

    public static WorkorderStepFragment getInstance(ArrayList<WorkorderService.StepsBean> s
            , Long woId
            , Long workTeamId
            , boolean canOpt
            , boolean eqCountAccord //是否需要输入设备数量
            , String mattersNeedingAttention //注意事项
            , String eqCode //点击的设备号
            , Long eqId //点击的设备Id
    ) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(WORKORDER_STEPS, s);
        bundle.putLong(WORKORDER_ID, woId);
        bundle.putLong(WORK_TEAM_ID, workTeamId);
        bundle.putBoolean(CAN_OPT, canOpt);
        bundle.putBoolean(COUNT_ACCORD,eqCountAccord);
        bundle.putString(ATTENTION,mattersNeedingAttention);
        bundle.putString(EQCODE,eqCode);
        bundle.putLong(EQID,eqId);
        bundle.putBoolean(SHOWDIALOG,true); //此处跳转需要传默认值
        WorkorderStepFragment instance = new WorkorderStepFragment();
        instance.setArguments(bundle);
        return instance;
    }
}
