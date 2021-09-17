package com.facilityone.wireless.patrol.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.view.View;
import android.view.ViewGroup;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.facilityone.wireless.a.arch.ec.module.FunctionService;
import com.facilityone.wireless.a.arch.ec.module.LocationBean;
import com.facilityone.wireless.a.arch.mvp.BaseFragment;
import com.facilityone.wireless.a.arch.offline.dao.OfflineTimeDao;
import com.facilityone.wireless.a.arch.offline.model.entity.PatrolSpotEntity;
import com.facilityone.wireless.a.arch.offline.model.entity.PatrolTaskEntity;
import com.facilityone.wireless.a.arch.offline.model.service.OfflineService;
import com.facilityone.wireless.a.arch.offline.model.service.OnDownloadListener;
import com.facilityone.wireless.a.arch.offline.model.service.OnPatrolListener;
import com.facilityone.wireless.a.arch.offline.model.service.PatrolDbService;
import com.facilityone.wireless.basiclib.app.FM;
import com.facilityone.wireless.a.arch.widget.FMWarnDialogBuilder;
import com.facilityone.wireless.patrol.NfcRedTagActivity;
import com.facilityone.wireless.patrol.R;
import com.facilityone.wireless.patrol.adapter.PatrolScanAdapter;
import com.facilityone.wireless.patrol.module.PatrolQueryService;
import com.facilityone.wireless.patrol.presenter.PatrolScanPresenter;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayList;
import java.util.List;

import me.yokeyword.fragmentation.SwipeBackLayout;

/**
 * Author：gary
 * Email: xuhaozv@163.com
 * description:巡检扫码或者nfc页面
 * Date: 2018/11/16 10:18 AM
 */
public class PatrolScanFragment extends BaseFragment<PatrolScanPresenter> implements BaseQuickAdapter.OnItemChildClickListener, OnRefreshLoadMoreListener {

    private RecyclerView mRecyclerView;
    private SmartRefreshLayout mRefreshLayout;

    private static final int REQUEST_SPOT = 70001;
    private static final int REQUEST_DEVICE = 30001;
    private static final String QRCODE = "qrcode";
    private static final String NEEDREFRESH = "needRefresh";

    private String mCode;
    private List<PatrolSpotEntity> mSpotEntityList;
    private PatrolScanAdapter mAdapter;
    private Long mTaskId;
    private PatrolQueryService.AttendanceResp mLocation;
    private boolean isAttentance=false;

    private Long mRequestTime;
    private int finishedTask;
    private boolean saveDataSuccess;
    private float mAllProgress;
    private QMUITipDialog mDialog;
    private float mTempDec;
    private float mTempBaseItem;
    private float mTempBaseSpot;
    private float mTempPatrol;
    private static final int TASK_COUNT = 4;
    private Boolean needRefresh = false;
    private boolean mHasAttentance=false;
    ArrayMap<Long, PatrolTaskEntity> mCurrentTask;

    @Override
    public PatrolScanPresenter createPresenter() {
        return new PatrolScanPresenter();
    }

    @Override
    public Object setLayout() {
        return R.layout.fragment_patrol_scan;
    }

    @Override
    protected int setTitleBar() {
        return R.id.ui_topbar;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
        this.setSwipeBackEnable(false);
    }

    private void initData() {
        String title = getString(R.string.patrol_spot);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mCode = arguments.getString(QRCODE);
        }
        setTitle(title);
        if (TextUtils.isEmpty(mCode)) {
            pop();
            return;
        }

        mDialog = initProgressBarLoading();
        mDialog.setCancelable(false);

        if (needRefresh){
            requestData();
        }
        initView();

    }

    private void initView() {
        mRefreshLayout = findViewById(R.id.refreshLayout);
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mSpotEntityList = new ArrayList<>();
        mAdapter = new PatrolScanAdapter(mSpotEntityList);

        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemChildClickListener(this);
        mRefreshLayout.setOnRefreshLoadMoreListener(this);
        onRefresh();

    }

    /**
     * 请求网络删除巡检任务或更新巡检任务
     */
    private void onRefresh() {
        mAdapter.setEmptyView(getLoadingView((ViewGroup) mRecyclerView.getParent()));
        if (NetworkUtils.isConnected()) {
            showLoading();
            getPresenter().getServicePatrolTask();
        } else {
            getSpotList();
        }

    }

    public void getSpotList() {
        getPresenter().getSpotList(mCode);
    }

    public void refreshUI(List<PatrolSpotEntity> spotEntities) {
        mSpotEntityList.clear();
        if (spotEntities != null && spotEntities.size() > 0) {
            mSpotEntityList.addAll(spotEntities);
            setTitle(spotEntities.get(0).getName());
            getPresenter().getCurrentTask();

        } else {
            mAdapter.setEmptyView(getNoDataView((ViewGroup) mRecyclerView.getParent()));
        }
        mAdapter.notifyDataSetChanged();
        mRefreshLayout.finishRefresh();
        //获取最后一次签到记录用于判断
        getPresenter().getLastAttendance();

    }

    public void error() {
        mRefreshLayout.finishRefresh(false);
        mAdapter.setEmptyView(getErrorView((ViewGroup) mRecyclerView.getParent(), R.string.patrol_get_data_error));
    }



    private void requestData() {
        //此处已改判断是否正在下载 如果正在下载则不在发起新的请求 防止按home键后多次下载
        if (mAllProgress > 0) {
            return;
        }


        if (mDialog != null && !mDialog.isShowing()) {
            setTipView(getString(R.string.patrol_dowmload_progress) + "0%");
            mDialog.show();
        }
        mAllProgress = 0;
        finishedTask = 0;
        saveDataSuccess = true;
        mTempDec = 0F;
        mTempBaseItem = 0F;
        mTempBaseSpot = 0F;
        mTempPatrol = 0F;
        PatrolDbService.downloadPatrolOfflineData(new OnDownloadListener() {
            @Override
            public void onDownload(int max, int progress) {
                if(max == 0) {
                    mAllProgress += 20;
                }else {
                    mAllProgress -= (20 * mTempDec);
                    float p = ((float) progress) / max;
                    mAllProgress += (20 * p);
                    mTempDec = p;
                }
                setTipProgress();
            }

            @Override
            public void onAllSuccess() {
                finishedRequest(0L);
            }

            @Override
            public void onError() {
                saveDataSuccess = false;
            }
        }, new OnPatrolListener() {
            @Override
            public void onDownload(int max, int progress) {
                if(max == 0) {
                    mAllProgress += 20;
                }else {
                    mAllProgress -= (20 * mTempBaseSpot);
                    float p = ((float) progress) / max;
                    mAllProgress += (20 * p);
                    mTempBaseSpot = p;
                }
                setTipProgress();
            }

            @Override
            public void onAllSuccess(Long time) {
                finishedRequest(time);
            }

            @Override
            public void onError() {
                saveDataSuccess = false;
            }
        }, new OnPatrolListener() {
            @Override
            public void onDownload(int max, int progress) {
                if(max == 0) {
                    mAllProgress += 20;
                }else {
                    mAllProgress -= (20 * mTempBaseItem);
                    float p = ((float) progress) / max;
                    mAllProgress += (20 * p);
                    mTempBaseItem = p;
                }
                setTipProgress();
            }

            @Override
            public void onAllSuccess(Long time) {
                finishedRequest(time);
            }

            @Override
            public void onError() {
                saveDataSuccess = false;
            }
        }, new OnPatrolListener() {
            @Override
            public void onDownload(int max, int progress) {
                if(max == 0) {
                    mAllProgress += 40;
                }else {
                    mAllProgress -= (40 * mTempPatrol);
                    float p = ((float) progress) / max;
                    mAllProgress += (40 * p);
                    mTempPatrol = p;
                }
                setTipProgress();
            }

            @Override
            public void onAllSuccess(Long time) {
                finishedRequest(time);
            }

            @Override
            public void onError() {
                saveDataSuccess = false;
            }
        });
    }


    @Override
    public void leftBackListener() {
        ActivityUtils.finishActivity(NfcRedTagActivity.class);
        super.leftBackListener();

    }

    private void setTipProgress() {
        if (mDialog != null && mDialog.isShowing()) {
            if (mAllProgress >= 100) {
                mAllProgress = 99.99f;
            }

            float temp = ((float) Math.round(mAllProgress * 100) / 100);
            setTipView(getString(R.string.patrol_dowmload_progress) + temp + "%");
        }
    }

    private void finishedRequest(Long time) {
        if (time != 0L) {
            if (mRequestTime == null) {
                mRequestTime = time;
            } else if (mRequestTime > time) {
                mRequestTime = time;
            }
        }

        finishedTask++;
        if (finishedTask == TASK_COUNT && saveDataSuccess) {
            OfflineService.addOrUpdateDownloadTime(mRequestTime, OfflineTimeDao.TYPE_OFFLINE_PATROL);
            OfflineService.addOrUpdatePatrolDownloadTime(mRequestTime, FM.getEmId());
            LogUtils.d("巡检离线数据下载完成");
            if (mDialog != null && mDialog.isShowing()) {
                mDialog.setTitle(getString(R.string.patrol_dowmload_progress) + "100%");
                mDialog.dismiss();
            }
            mAllProgress = 0;
            ToastUtils.showShort(R.string.patrol_offline_download_success);
        }

        if (!saveDataSuccess) {
            if (mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss();
            }
            mAllProgress = 0;
        }

        LogUtils.d("下载进度:" + finishedTask + "----" + saveDataSuccess + "--" + mAllProgress + "%");
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        PatrolSpotEntity patrolSpotEntity = mSpotEntityList.get(position);
        if (patrolSpotEntity == null) {
            return;
        }
        System.out.println("当前任务ID");
        System.out.println(patrolSpotEntity.getTaskId());
        int id = view.getId();

        System.out.println("当前点位类型");
        System.out.println(mCurrentTask.get(patrolSpotEntity.getTaskId()).getpType());
        if (id == R.id.item_rl) {
            //判断点位类型
            if (mCurrentTask.get(patrolSpotEntity.getTaskId()).getpType().equals(PatrolTaskEntity.TASK_TYPE_INSPECTION)) {
                //是否已签到
                if (mHasAttentance) {
                    if (isLocationNull(mLocation.location, patrolSpotEntity.getLocation())) {
                        //签到比较前先判空,防止程序崩溃
//                        mLocation.buildingId.equals(patrolSpotEntity.getLocation().buildingId)
                        if (mLocation.location.siteId.equals(patrolSpotEntity.getLocation().siteId) &&
                                isInLocationList(patrolSpotEntity.getLocation().buildingId,mLocation)) {

                            mTaskId = patrolSpotEntity.getTaskId();
                            boolean canGo = getPresenter().canGo(patrolSpotEntity, mSpotEntityList);
                            if (canGo) {
                                getPresenter().judgeTask(patrolSpotEntity);

                            } else {
                                ToastUtils.showShort(R.string.patrol_overdue_task_tip);
                            }
                        } else {
                            ToastUtils.showLong("您的签到位置与当前位置不符，请确认！");
                        }
                    } else {
                    ToastUtils.showLong("您的签到位置与当前位置不符，请确认！");
                    }


            }
                else{
                    ToastUtils.showLong("请先签到");
                }

            }else {
                mTaskId = patrolSpotEntity.getTaskId();
                boolean canGo = getPresenter().canGo(patrolSpotEntity, mSpotEntityList);
                if (canGo) {
                    getPresenter().judgeTask(patrolSpotEntity);

                } else {
                    ToastUtils.showShort(R.string.patrol_overdue_task_tip);
                }
            }
        } else if (id == R.id.ll_look_all) {
                Long taskId = patrolSpotEntity.getTaskId();
                String taskName = patrolSpotEntity.getTaskName();
                String code = patrolSpotEntity.getCode();
                if (mCurrentTask.get(patrolSpotEntity.getTaskId()).getpType().equals(PatrolTaskEntity.TASK_TYPE_INSPECTION)) {
                    startForResult(PatrolSpotFragment.getInstance(taskId, taskName, code, true), REQUEST_SPOT);
                } else {
                    startForResult(PatrolSpotFragment.getInstance(taskId, taskName, code, false), REQUEST_SPOT);
                }
            }



//            if (id == R.id.item_rl) {
//                mTaskId = patrolSpotEntity.getTaskId();
//                boolean canGo = getPresenter().canGo(patrolSpotEntity, mSpotEntityList);
//                if (canGo) {
////                    startForResult(PatrolDeviceFragment.getInstance(patrolSpotEntity.getName(), patrolSpotEntity.getPatrolSpotId()), REQUEST_DEVICE);
//
//                    getPresenter().judgeTask(patrolSpotEntity);
//
//                } else {
//                    ToastUtils.showShort(R.string.patrol_overdue_task_tip);
//                }
//            } else if () {
//
//            }

    }
//
//           if (id == R.id.ll_look_all) {
//            mTaskId = null;
//            Long taskId = patrolSpotEntity.getTaskId();
//            String taskName = patrolSpotEntity.getTaskName();
//            String code = patrolSpotEntity.getCode();
//            startForResult(PatrolSpotFragment.getInstance(taskId, taskName, code,true), REQUEST_SPOT);
//            }



    @Override
    public boolean onBackPressedSupport() {
        ActivityUtils.finishActivity(NfcRedTagActivity.class);
        return super.onBackPressedSupport();

    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            getPresenter().setTaskSpotDb(mTaskId);
            getSpotList();
        }
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {

    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        onRefresh();
    }

    @Override
    public void onNoDataOrErrorClick(View view) {
        onRefresh();
    }

    public static PatrolScanFragment getInstance(String code,boolean needRefresh) {
        Bundle bundle = new Bundle();
        bundle.putString(QRCODE, code);
        bundle.putBoolean(NEEDREFRESH,needRefresh);
        PatrolScanFragment instance = new PatrolScanFragment();
        instance.setArguments(bundle);
        return instance;
    }



    /**
     * @Created by: kuuga
     * @Date: on 2021/8/31 9:28
     * @Description: 最后一次签到记录回调
     */
    public void saveAttentanceLocation(PatrolQueryService.AttendanceResp locationBean){
        this.mLocation=locationBean;
    }


    /**
     * @Created by: kuuga
     * @Date: on 2021/8/30 9:57
     * @Description: 开启任务时提示
     */
    public void showOrderTimeDialog(Long time,PatrolSpotEntity entity){
        String timeStr=String.valueOf(time/60);
        FMWarnDialogBuilder builder = new FMWarnDialogBuilder(getContext());
        builder.setTitle("提示");
        builder.setCancel("返回");
        builder.setTip("如果您确认开启该任务，最少需要"+timeStr+"分钟才能完成提交。");
        builder.addOnBtnSureClickListener(new FMWarnDialogBuilder.OnBtnClickListener() {
            @Override
            public void onClick(QMUIDialog dialog, View view) {
                executeTask(entity);
                dialog.dismiss();
            }
        });
        builder.create(R.style.fmDefaultWarnDialog).show();
    }


    /**
     * @Created by: kuuga
     * @Date: on 2021/8/30 9:58
     * @Description: 剩余时间提示
     */
    public void showLefTimeDialog(Long leftTime){
        String messageFormat="";
        String leftTimeStr="";


        //保存此页面数据到数据库
        FMWarnDialogBuilder builder = new FMWarnDialogBuilder(getContext());
        builder.setTitle("提示");
        if (leftTime>60){
            leftTimeStr=String.valueOf(leftTime/60);
            messageFormat="存在已执行的任务,还剩%s分钟";
        }else {
            leftTimeStr=String.valueOf(leftTime);
            messageFormat="存在已执行的任务,还剩%s秒";
        }
//        String messageFormat="完成任务最少需要%s分钟，为满足期限。（还剩%s分钟）";
        builder.setTip(String.format(messageFormat,leftTimeStr));
//        builder.setTip(String.format(messageFormat,mNeedTime,leftTime));
        builder.setCancelVisiable(false);
        builder.addOnBtnSureClickListener(new FMWarnDialogBuilder.OnBtnClickListener() {
            @Override
            public void onClick(QMUIDialog dialog, View view) {
//                saveDataBefore();
                dialog.dismiss();
            }
        });
        builder.create(R.style.fmDefaultWarnDialog).show();
    }


    /**
     * @Created by: kuuga
     * @Date: on 2021/8/27 16:26
     * @Description: 请求执行当前任务
     */
    public void executeTask(PatrolSpotEntity entity) {
        getPresenter().executeTask(entity);
    }


    /**
     * @Created by: kuuga
     * @Date: on 2021/8/27 16:26
     * @Description: 进入设备列表
     */
    public void enterDeviceList(PatrolSpotEntity entity){
        startForResult(PatrolDeviceFragment.getInstance(entity.getName(), entity.getPatrolSpotId()), REQUEST_DEVICE);
    }

    /**
     * @Created by: kuuga
     * @Date: on 2021/8/31 14:16
     * @Description: 位置信息判空
     * @return
     */
    private static boolean isLocationNull(LocationBean remoteBean, LocationBean localBean){
        if (remoteBean!=null&&localBean!=null){
            if (remoteBean.siteId!=null&&localBean.siteId!=null){
                return remoteBean.buildingId != null && localBean.buildingId != null;
            }else {
                return false;
            }

        }
        return false;
    }

    public void hasAttentanceData(boolean mHasData){
        this.mHasAttentance=mHasData;
    }


    public void checkCurrentTaskEntity(ArrayMap<Long,PatrolTaskEntity> taskEntity){

        this.mCurrentTask=taskEntity;
    }

    /**
     * @return
     * @Created by: kuuga
     * @Date: on 2021/8/31 14:16
     * @Description: 工单位置是否处于签到区间
     */
    private static boolean isInLocationList(Long orderBuildingId, PatrolQueryService.AttendanceResp remoteData){
        for (Long id: remoteData.buildingIds) {
            if (id.equals(orderBuildingId)){
                return true;
            }
        }
        return false;

    }

}
