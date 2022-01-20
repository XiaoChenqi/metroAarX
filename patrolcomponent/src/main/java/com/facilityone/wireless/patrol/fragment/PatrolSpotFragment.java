package com.facilityone.wireless.patrol.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.DragAndDropPermissions;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.facilityone.wireless.ObjectBox;
import com.facilityone.wireless.a.arch.ec.module.LocationBean;
import com.facilityone.wireless.a.arch.ec.module.UserService;
import com.facilityone.wireless.a.arch.ec.utils.SPKey;
import com.facilityone.wireless.a.arch.mvp.BaseFragment;
import com.facilityone.wireless.a.arch.offline.dao.PatrolSpotDao;
import com.facilityone.wireless.a.arch.offline.dao.PatrolTaskDao;
import com.facilityone.wireless.a.arch.offline.model.entity.DBPatrolConstant;
import com.facilityone.wireless.a.arch.offline.model.entity.PatrolSpotEntity;
import com.facilityone.wireless.a.arch.offline.model.entity.PatrolTaskEntity;
import com.facilityone.wireless.a.arch.offline.objectbox.patrol.CompleteTime;
import com.facilityone.wireless.a.arch.widget.FMWarnDialogBuilder;
import com.facilityone.wireless.basiclib.app.FM;
import com.facilityone.wireless.basiclib.utils.SystemDateUtils;
import com.facilityone.wireless.patrol.NfcRedTagActivity;
import com.facilityone.wireless.patrol.R;
import com.facilityone.wireless.patrol.adapter.PatrolSpotAdapter;
import com.facilityone.wireless.patrol.module.PatrolConstant;
import com.facilityone.wireless.patrol.module.PatrolQueryService;
import com.facilityone.wireless.patrol.module.PatrolSaveReq;
import com.facilityone.wireless.patrol.presenter.PatrolSpotPresenter;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayList;
import java.util.List;

import io.objectbox.Box;
import me.yokeyword.fragmentation.SwipeBackLayout;

/**
 * Author：gary
 * Email: xuhaozv@163.com
 * description:巡检点位页面
 * Date: 2018/11/6 9:38 AM
 */
public class PatrolSpotFragment extends BaseFragment<PatrolSpotPresenter> implements
        BaseQuickAdapter.OnItemChildClickListener
        , View.OnClickListener
        , OnRefreshLoadMoreListener
        , SwipeBackLayout.OnSwipeListener {

    private RecyclerView mRecyclerView;
    private SmartRefreshLayout mRefreshLayout;
    private LinearLayout mLlTopMenu;
    private TextView mTvTotal;
    private TextView mTvNotSync;
    private TextView mTvUnfinish;
    private TextView mTvSync;

    private static final String PATROL_TASK_ID = "patrol_task_id";
    private static final String PATROL_TASK_NAME = "patrol_task_name";
    private static final String PATROL_SCAN = "patrol_scan";
    private static final String PATROL_NEED_CHECK = "patrol_check";
    private static final int REQUEST_DEVICE = 30001;

    private List<PatrolSpotEntity> mShowEntities;
    private List<PatrolSpotEntity> mTotalEntities;
    private List<PatrolSpotEntity> mCompletedEntities;
    private List<PatrolSpotEntity> mUncompletedEntities;
    private PatrolSpotAdapter mAdapter;
    private int mMenuId;
    private Long mTaskId;
    private PatrolSaveReq mPatrolSaveReq;
    private String mFromScan;
    private int mNeedNfc;
    private boolean mNeedCheck = false;
    //签到位置
    private PatrolQueryService.AttendanceResp mLocation;
    private boolean mHasAttentance = false;
    private Box<CompleteTime> taskBox; //任务开启
    private PatrolSpotEntity spotEntity;

    @Override
    public PatrolSpotPresenter createPresenter() {
        return new PatrolSpotPresenter();
    }

    @Override
    public Object setLayout() {
        return R.layout.fragment_patrol_spot;
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
            title = arguments.getString(PATROL_TASK_NAME, getString(R.string.patrol_spot));
            mTaskId = arguments.getLong(PATROL_TASK_ID);
            mFromScan = arguments.getString(PATROL_SCAN, "");
            mNeedCheck = arguments.getBoolean(PATROL_NEED_CHECK, false);
            if (mNeedCheck) {
                //离线数据存储在数据库中
                getPresenter().getLastAttendanceOutLine();

            }
        }

        setTitle(title);
        if (mTaskId == 0L) {
            pop();
            return;
        }
        PatrolSpotDao dao = new PatrolSpotDao();
        initView();
        getSpotList();
        mNeedNfc = SPUtils.getInstance(SPKey.SP_MODEL_PATROL).getInt(SPKey.PATROL_NEED_NFC, 0);

    }

    private void initView() {

        setRightTextButton(R.string.patrol_submit, R.id.patrol_spot_upload_id);

        mRefreshLayout = findViewById(R.id.refreshLayout);
        mLlTopMenu = findViewById(R.id.ll_top);
        mTvTotal = findViewById(R.id.all_tag_tv);
        mMenuId = R.id.all_tag_tv;
        mTvNotSync = findViewById(R.id.not_sync_tv);
        mTvUnfinish = findViewById(R.id.unfinish_tv);
        mTvSync = findViewById(R.id.spot_sync_tv);
        mRecyclerView = findViewById(R.id.recyclerView);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mShowEntities = new ArrayList<>();
        mTotalEntities = new ArrayList<>();
        mCompletedEntities = new ArrayList<>();
        mUncompletedEntities = new ArrayList<>();
        mAdapter = new PatrolSpotAdapter(mShowEntities);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemChildClickListener(this);
        mRefreshLayout.setOnRefreshLoadMoreListener(this);
        mTvTotal.setOnClickListener(this);
        mTvNotSync.setOnClickListener(this);
        mTvUnfinish.setOnClickListener(this);
        mTvSync.setOnClickListener(this);
        getSwipeBackLayout().addSwipeListener(this);
    }


    private void getSpotList() {
        showLoading();
        getPresenter().getSpotList(mTaskId);
    }

    @Override
    public void onRightTextMenuClick(View view) {
        //syncDta(PatrolConstant.PATROL_OPT_TYPE_UPLOAD);
        //判断是否需要验证签到状态
        if (mNeedCheck) {
            if (mHasAttentance) {
                getPresenter().submitTask(mTaskId, PatrolConstant.PATROL_OPT_TYPE_UPLOAD);
//                syncDta(PatrolConstant.PATROL_OPT_TYPE_UPLOAD);
            } else {
                ToastUtils.showLong("请先签到");
            }
        } else {
            getPresenter().submitTask(mTaskId, PatrolConstant.PATROL_OPT_TYPE_UPLOAD);
//            syncDta(PatrolConstant.PATROL_OPT_TYPE_UPLOAD);
        }

    }

    public void error() {
        mLlTopMenu.setVisibility(View.GONE);
        mRefreshLayout.finishRefresh(false);
        mAdapter.setEmptyView(getErrorView((ViewGroup) mRecyclerView.getParent(), R.string.patrol_get_data_error));
        dismissLoading();
    }

    @SuppressLint("DefaultLocale")
    public void refreshUI(List<PatrolSpotEntity> spotEntities) {
        mTotalEntities.clear();
        mShowEntities.clear();
        mCompletedEntities.clear();
        mUncompletedEntities.clear();
        if (spotEntities != null && spotEntities.size() > 0) {
            mLlTopMenu.setVisibility(View.VISIBLE);
            mTotalEntities.addAll(spotEntities);

            for (PatrolSpotEntity totalEntity : mTotalEntities) {
                if (totalEntity.getRemoteCompleted() == DBPatrolConstant.TRUE_VALUE || totalEntity.getCompleted() == DBPatrolConstant.TRUE_VALUE) {
                    mCompletedEntities.add(totalEntity);
                } else {
                    mUncompletedEntities.add(totalEntity);
                }
            }
        } else {
            mLlTopMenu.setVisibility(View.VISIBLE);
            mAdapter.setEmptyView(getNoDataView((ViewGroup) mRecyclerView.getParent()));
        }
        mTvTotal.setText(String.format(getString(R.string.patrol_task_spot_all) + "(%d)", mTotalEntities.size()));
        mTvNotSync.setText(String.format(getString(R.string.patrol_task_spot_finish) + "(%d)", mCompletedEntities.size()));
        mTvUnfinish.setText(String.format(getString(R.string.patrol_task_spot_unfinish) + "(%d)", mUncompletedEntities.size()));

        refreshAll(mMenuId, true);

        mAdapter.notifyDataSetChanged();
        mRefreshLayout.finishRefresh();
        dismissLoading();
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {


        PatrolSpotEntity patrolSpotEntity = mShowEntities.get(position);
        patrolSpotEntity.setTaskId(mTaskId);


        //是否需要判断签到状态
        if (mNeedCheck) {
            if (mHasAttentance) {
//                patrolSpotEntity.getLocation().buildingId.equals(mLocation.buildingId)
                if (isLocationNull(mLocation, patrolSpotEntity.getLocation()) &&
                        //判断当前点位是否在签到区间中
                        isInLocationList(patrolSpotEntity.getLocation().buildingId, mLocation)) {
                    mHasAttentance = true;
                    //如果远程完成了也不需要扫描二维码 加上  || patrolSpotEntity.getRemoteCompleted() == DBPatrolConstant.TRUE_VALUE

                    if (mNeedNfc == PatrolConstant.PATROL_NEED_NFC && patrolSpotEntity.getCompleted() != DBPatrolConstant.TRUE_VALUE) {
                        ToastUtils.showShort(R.string.patrol_spot_operate_tip);
                    } else {
                        String code = patrolSpotEntity.getCode();
                        if ((!TextUtils.isEmpty(mFromScan) && !TextUtils.isEmpty(code) && mFromScan.equals(code))
                                || patrolSpotEntity.getCompleted() == DBPatrolConstant.TRUE_VALUE
                                || patrolSpotEntity.getRemoteCompleted() == DBPatrolConstant.TRUE_VALUE) {
//                startForResult(PatrolDeviceFragment.getInstance(patrolSpotEntity.getName(), patrolSpotEntity.getPatrolSpotId()), REQUEST_DEVICE);

                            getPresenter().judgeTask(patrolSpotEntity, false);
                        } else {
                            getPresenter().judgeTask(patrolSpotEntity, true);
                            //需要扫描二维码
                            //getPresenter().scan(patrolSpotEntity);
                        }
                    }
                } else {
                    ToastUtils.showLong("您的签到位置与当前位置不符，请确认！");
                }
            } else {
                ToastUtils.showLong("请先签到");
            }

        } else {
            //如果远程完成了也不需要扫描二维码 加上  || patrolSpotEntity.getRemoteCompleted() == DBPatrolConstant.TRUE_VALUE

            if (mNeedNfc == PatrolConstant.PATROL_NEED_NFC && patrolSpotEntity.getCompleted() != DBPatrolConstant.TRUE_VALUE) {
                ToastUtils.showShort(R.string.patrol_spot_operate_tip);
            } else {
                String code = patrolSpotEntity.getCode();
                if ((!TextUtils.isEmpty(mFromScan) && !TextUtils.isEmpty(code) && mFromScan.equals(code))
                        || patrolSpotEntity.getCompleted() == DBPatrolConstant.TRUE_VALUE
                        || patrolSpotEntity.getRemoteCompleted() == DBPatrolConstant.TRUE_VALUE) {
//                startForResult(PatrolDeviceFragment.getInstance(patrolSpotEntity.getName(), patrolSpotEntity.getPatrolSpotId()), REQUEST_DEVICE);

                    getPresenter().judgeTask(patrolSpotEntity, false);
                } else {
                    getPresenter().judgeTask(patrolSpotEntity, true);
                    //需要扫描二维码
//                getPresenter().scan(patrolSpotEntity);
                }
            }
        }


    }

    /**
     * 扫描结果匹配
     *
     * @param patrolSpotEntity
     */
    public void scanResult(PatrolSpotEntity patrolSpotEntity, Long time) {

        if (time != 0) {
            PatrolSpotDao dao = new PatrolSpotDao();
            PatrolSpotEntity item = dao.getSpot(patrolSpotEntity.getPatrolSpotId());
            if (item.getTaskStatus() > 0) {
                enterDeviceList(patrolSpotEntity);
            } else {
                showOrderTimeDialog(time, patrolSpotEntity);
            }
        } else {
            enterDeviceList(patrolSpotEntity);
        }
    }

    /**
     * 扫描结果匹配
     *
     * @param patrolSpotEntity
     */
    public void workingScanResult(PatrolSpotEntity patrolSpotEntity, Long time) {


        enterDeviceList(patrolSpotEntity);

//        executeTask(patrolSpotEntity);
//        startForResult(PatrolDeviceFragment.getInstance(patrolSpotEntity.getName(), patrolSpotEntity.getPatrolSpotId()), REQUEST_DEVICE);
    }


    public void needScanQrcode(PatrolSpotEntity patrolSpotEntity, Long time) {
        getPresenter().scan(patrolSpotEntity, time);

    }

    public void needWorkingScanQrcode(PatrolSpotEntity patrolSpotEntity, Long time) {
        getPresenter().scan(patrolSpotEntity, time);

    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            getSpotList();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.all_tag_tv) {
            refreshAll(id, false);
        } else if (id == R.id.not_sync_tv) {
            refreshAll(id, false);
        } else if (id == R.id.unfinish_tv) {
            refreshAll(id, false);
        } else if (id == R.id.spot_sync_tv) {
            syncDta(PatrolConstant.PATROL_OPT_TYPE_SYNC);
        }
    }

    private void refreshAll(int id, boolean canClick) {
        if (!canClick) {
            mTvTotal.setTextColor(getResources().getColor(R.color.green_1ab394));
            mTvNotSync.setTextColor(getResources().getColor(R.color.green_1ab394));
            mTvUnfinish.setTextColor(getResources().getColor(R.color.green_1ab394));
            mTvTotal.setBackgroundResource(R.drawable.fm_patrol_left_empty_bg);
            mTvNotSync.setBackgroundResource(R.drawable.fm_patrol_center_empty_bg);
            mTvUnfinish.setBackgroundResource(R.drawable.fm_patrol_right_empty_bg);
        }

        if (id == R.id.all_tag_tv && (mMenuId != R.id.all_tag_tv || canClick)) {
            mTvTotal.setTextColor(getResources().getColor(R.color.white));
            mTvTotal.setBackgroundResource(R.drawable.fm_patrol_left_fill_bg);
            mMenuId = id;
            mShowEntities.clear();
            mShowEntities.addAll(mTotalEntities);
        } else if (id == R.id.not_sync_tv && (mMenuId != R.id.not_sync_tv || canClick)) {
            mTvNotSync.setTextColor(getResources().getColor(R.color.white));
            mTvNotSync.setBackgroundResource(R.drawable.fm_patrol_center_fill_bg);
            mMenuId = id;
            mShowEntities.clear();
            mShowEntities.addAll(mCompletedEntities);
        } else if (id == R.id.unfinish_tv && (mMenuId != R.id.unfinish_tv || canClick)) {
            mTvUnfinish.setTextColor(getResources().getColor(R.color.white));
            mTvUnfinish.setBackgroundResource(R.drawable.fm_patrol_right_fill_bg);
            mMenuId = id;
            mShowEntities.clear();
            mShowEntities.addAll(mUncompletedEntities);
        }


        mRecyclerView.scrollToPosition(0);
        mAdapter.setEmptyView(getNoDataView((ViewGroup) mRecyclerView.getParent()));
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 同步数据
     */
    public void syncDta(int operateType) {
        mPatrolSaveReq = new PatrolSaveReq();
        mPatrolSaveReq.operateType = operateType;
        mPatrolSaveReq.userId = FM.getEmId();
        getPresenter().syncData(mPatrolSaveReq, mTotalEntities, operateType, mTaskId);
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {

    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        getSpotList();
    }

    @Override
    public boolean onBackPressedSupport() {
        popResult();
//        ActivityUtils.finishActivity(NfcRedTagActivity.class);
        return true;

    }

    @Override
    public void leftBackListener() {
        popResult();
//        ActivityUtils.finishActivity(NfcRedTagActivity.class);
    }

    public void popResult() {
        setFragmentResult(RESULT_OK, null);
        pop();
    }

    @Override
    public void onDragStateChange(int state) {
        if (state == SwipeBackLayout.STATE_FINISHED) {
            ActivityUtils.finishActivity(NfcRedTagActivity.class);
            setFragmentResult(RESULT_OK, null);
        }
    }

    @Override
    public void onEdgeTouch(int oritentationEdgeFlag) {

    }

    @Override
    public void onDragScrolled(float scrollPercent) {

    }

    public void refresh() {
        mAdapter.notifyDataSetChanged();
    }

    public Long getTaskId() {
        return mTaskId;
    }

    public PatrolSaveReq getPatrolSaveReq() {
        return mPatrolSaveReq;
    }

    public static PatrolSpotFragment getInstance(Long taskId, String name, boolean checkAttentance) {
        if (checkAttentance) {
            return getInstance(taskId, name, null, true);
        } else {
            return getInstance(taskId, name, null);
        }

    }

    public static PatrolSpotFragment getInstance(Long taskId, String name, String scan) {
        Bundle bundle = new Bundle();
        bundle.putLong(PATROL_TASK_ID, taskId);
        bundle.putString(PATROL_TASK_NAME, name);
        bundle.putString(PATROL_SCAN, scan);
        PatrolSpotFragment instance = new PatrolSpotFragment();
        instance.setArguments(bundle);
        return instance;
    }

    /**
     * @Created by: kuuga
     * @Date: on 2021/8/30 15:38
     * @Description: 类型为巡检时增加判断参数
     */
    public static PatrolSpotFragment getInstance(Long taskId, String name, String scan, boolean checkAttentance) {
        Bundle bundle = new Bundle();
        bundle.putLong(PATROL_TASK_ID, taskId);
        bundle.putString(PATROL_TASK_NAME, name);
        bundle.putString(PATROL_SCAN, scan);
        bundle.putBoolean(PATROL_NEED_CHECK, checkAttentance);
        PatrolSpotFragment instance = new PatrolSpotFragment();
        instance.setArguments(bundle);
        return instance;
    }


    /**
     * @Created by: kuuga
     * @Date: on 2021/8/27 16:26
     * @Description:请求执行当前任务
     */
    public void executeTask(PatrolSpotEntity entity) {
        getPresenter().executeTask(entity);
    }

    /**
     * @Created by: kuuga
     * @Date: on 2021/8/27 16:26
     * @Description:进入设备列表
     */
    public void enterDeviceList(PatrolSpotEntity entity) {
        startForResult(PatrolDeviceFragment.getInstance(entity.getName(), entity.getPatrolSpotId(), entity), REQUEST_DEVICE);
    }


    /**
     * @Created by: kuuga
     * @Date: on 2021/8/30 9:58
     * @Description: 剩余时间提示
     */
    public void showOrderTimeDialog(Long leftTime) {
        String messageFormat = "";
        String leftTimeStr = "";


        //保存此页面数据到数据库
        FMWarnDialogBuilder builder = new FMWarnDialogBuilder(getContext());
        builder.setTitle("提示");
        if (leftTime > 60) {
            leftTimeStr = String.valueOf(leftTime / 60);
            messageFormat = "存在已执行的任务,还剩%s分钟";
        } else {
            leftTimeStr = String.valueOf(leftTime);
            messageFormat = "存在已执行的任务,还剩%s秒";
        }
//        String messageFormat="完成任务最少需要%s分钟，为满足期限。（还剩%s分钟）";
        builder.setTip(String.format(messageFormat, leftTimeStr));
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
     * @Date: on 2021/8/30 9:57
     * @Description: 开启任务时提示
     */
    public void showOrderTimeDialog(Long time, PatrolSpotEntity entity) {
        String timeStr = String.valueOf(time / 60);
        FMWarnDialogBuilder builder = new FMWarnDialogBuilder(getContext());
        builder.setTitle("提示");
        builder.setCancel("返回");
        builder.setTip("如果您确认开启该任务，最少需要" + timeStr + "分钟才能完成提交。");
        builder.addOnBtnSureClickListener(new FMWarnDialogBuilder.OnBtnClickListener() {
            @Override
            public void onClick(QMUIDialog dialog, View view) {

                /**
                 * 做成离线的形式处理
                 * */
                Long timeForNow = SystemDateUtils.getCurrentTimeMillis();
                PatrolSpotDao spotDao = new PatrolSpotDao();
                spotDao.upDateTaskTime(entity.getPatrolSpotId(), timeForNow);
                taskBox = ObjectBox.INSTANCE.getBoxStore().boxFor(CompleteTime.class);
                taskBox.removeAll();
                CompleteTime da = new CompleteTime();
                da.setStarTime(timeForNow);
                da.setTaskId(entity.getTaskId());
                da.setCheckTime(entity.getTaskTime());
                da.setPatrolSpotId(entity.getPatrolSpotId());
                da.setTaskTip(PatrolConstant.PATROL_TASK_OUTLINE);
                taskBox.put(da);
                Log.i("任务开启", "onClick: " + da + "");
                enterDeviceList(entity);

//                executeTask(entity);

//                startForResult(PatrolItemFragment.getInstance(mSpotId, (ArrayList<PatrolEquEntity>) mEntities, position, mSpotName,time), REQUEST_ITEM);
                dialog.dismiss();
            }
        });
        builder.create(R.style.fmDefaultWarnDialog).show();
    }

    public void saveAttentanceLocation(PatrolQueryService.AttendanceResp locationBean) {
        this.mLocation = locationBean;
    }


    /**
     * @return
     * @Created by: kuuga
     * @Date: on 2021/8/31 14:16
     * @Description: 位置信息判空
     */
    private static boolean isLocationNull(PatrolQueryService.AttendanceResp remoteBean, LocationBean localBean) {
        String userInfo = SPUtils.getInstance(SPKey.SP_MODEL_USER).getString(SPKey.USER_INFO);
        UserService.UserInfoBean infoBean = GsonUtils.fromJson(userInfo, UserService.UserInfoBean.class);
        if (!(infoBean.type == PatrolConstant.OUT_SOURCING_CODE)) {
            return true;
        } else {
            if (remoteBean != null && localBean != null) {
                if (remoteBean.location.siteId != null && localBean.siteId != null) {
                    return remoteBean.location.buildingId != null && localBean.buildingId != null;
                } else {
                    return false;
                }

            }
        }

        return false;
    }

    public void hasAttentanceData(boolean mHasData) {
        String userInfo = SPUtils.getInstance(SPKey.SP_MODEL_USER).getString(SPKey.USER_INFO);
        UserService.UserInfoBean infoBean = GsonUtils.fromJson(userInfo, UserService.UserInfoBean.class);
        if (!(infoBean.type == PatrolConstant.OUT_SOURCING_CODE)) {
            this.mHasAttentance = true; //非委外不需要判断签到状态
        } else {
            this.mHasAttentance = mHasData;
        }
    }


    /**
     * @return
     * @Created by: kuuga
     * @Date: on 2021/8/31 14:16
     * @Description: 工单位置是否处于签到区间
     */
    private static boolean isInLocationList(Long orderBuildingId, PatrolQueryService.AttendanceResp remoteData) {
        String userInfo = SPUtils.getInstance(SPKey.SP_MODEL_USER).getString(SPKey.USER_INFO);
        UserService.UserInfoBean infoBean = GsonUtils.fromJson(userInfo, UserService.UserInfoBean.class);
        if (!(infoBean.type == PatrolConstant.OUT_SOURCING_CODE)) {
            return true;
        } else {
            for (Long id : remoteData.buildingIds) {
                if (id.equals(orderBuildingId)) {
                    return true;
                }
            }
        }

        return false;

    }
}
