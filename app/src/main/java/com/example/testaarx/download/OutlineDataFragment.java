package com.example.testaarx.download;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.example.testaarx.R;
import com.facilityone.wireless.a.arch.mvp.BaseFragment;
import com.facilityone.wireless.a.arch.offline.dao.OfflineTimeDao;
import com.facilityone.wireless.a.arch.offline.model.service.OfflineService;
import com.facilityone.wireless.a.arch.offline.model.service.OnDownloadListener;


import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by: owen.
 * Date: on 2018/6/8 下午3:11.
 * Description: 离线数据下载页面
 * email:
 */

public class OutlineDataFragment extends BaseFragment<OutlineDataPresenter> {

    private Button mDownloadBtn;
    private RecyclerView mRecyclerView;

    private static final int TASK_COUNT = 10;
    private static final String DATA_UPDATE = "data_update";
    private static final String DATA_ITEM_UPDATE = "data_item_update";
    private static final String REQUEST_TIME = "request_time";
    private int finishedTask;
    private boolean saveDataSuccess;
    private boolean saveLocationDataSuccess;
    private boolean mEnable;
    private OfflineDataStatusEntity mOfflineDataStatusEntity;
    private long mCurrentTimeMillis;
    private Long mRequestTime;
    private List<DownloadProgressEntity> mDownloadProgressEntities;
    private OutlineDataAdapter mAdapter;
    private DownloadProgressEntity mDeviceDownload;
    private DownloadProgressEntity mDeviceTypeDownload;
    private DownloadProgressEntity mLocationDownload;
    private DownloadProgressEntity mDepDownload;
    private DownloadProgressEntity mPriorityDownload;
    private DownloadProgressEntity mFlowDownload;
    private DownloadProgressEntity mServiceTypeDownload;
    private DownloadProgressEntity mDemandTypeDownload;
    private int mLocationCount;
    private int mLocationProgress;

    @Override
    public OutlineDataPresenter createPresenter() {
        return new OutlineDataPresenter();
    }

    @Override
    public Object setLayout() {
        return R.layout.fragment_outline_data;
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
        initListener();
    }


    public void changeOfflineData(OfflineDataStatusEntity entity, boolean showRed) {
        //mDataView.showRedPoint(showRed);
        mOfflineDataStatusEntity = entity;
        //mShowDataStatus = showRed;
    }

    private void initData() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mEnable = arguments.getBoolean(DATA_UPDATE, true);
            mRequestTime = arguments.getLong(REQUEST_TIME, 0L);
            mOfflineDataStatusEntity = arguments.getParcelable(DATA_ITEM_UPDATE);
        }

        mDownloadProgressEntities = new ArrayList<>();

        mDeviceDownload = new DownloadProgressEntity();
        mDeviceDownload.setName(getString(R.string.app_device_info_tip));
        mDeviceDownload.setType(DownloadStatus.DOWNLOAD_STATUS_NOT_DOWNLOAD);
        if (mRequestTime != 0L
                && mOfflineDataStatusEntity != null
                && mOfflineDataStatusEntity.getDeviceNew() != null
                && !mOfflineDataStatusEntity.getDeviceNew()) {
            mDeviceDownload.setType(DownloadStatus.DOWNLOAD_STATUS_HAVE_DOWNLOAD);
        } else if (mRequestTime != 0L
                && mOfflineDataStatusEntity != null
                && mOfflineDataStatusEntity.getDeviceNew() != null
                && mOfflineDataStatusEntity.getDeviceNew()) {
            mDeviceDownload.setType(DownloadStatus.DOWNLOAD_STATUS_HAVE_UPDATE);
        }
        mDownloadProgressEntities.add(mDeviceDownload);

        mDeviceTypeDownload = new DownloadProgressEntity();
        mDeviceTypeDownload.setName(getString(R.string.app_device_type));
        mDeviceTypeDownload.setType(DownloadStatus.DOWNLOAD_STATUS_NOT_DOWNLOAD);
        if (mRequestTime != 0L
                && mOfflineDataStatusEntity != null
                && mOfflineDataStatusEntity.getDeviceTypeNew() != null
                && !mOfflineDataStatusEntity.getDeviceTypeNew()) {
            mDeviceTypeDownload.setType(DownloadStatus.DOWNLOAD_STATUS_HAVE_DOWNLOAD);
        } else if (mRequestTime != 0L
                && mOfflineDataStatusEntity != null
                && mOfflineDataStatusEntity.getDeviceTypeNew() != null
                && mOfflineDataStatusEntity.getDeviceTypeNew()) {
            mDeviceTypeDownload.setType(DownloadStatus.DOWNLOAD_STATUS_HAVE_UPDATE);
        }
        mDownloadProgressEntities.add(mDeviceTypeDownload);

        mLocationDownload = new DownloadProgressEntity();
        mLocationDownload.setName(getString(R.string.app_location));
        mLocationDownload.setType(DownloadStatus.DOWNLOAD_STATUS_NOT_DOWNLOAD);
        if (mRequestTime != 0L
                && mOfflineDataStatusEntity != null
                && mOfflineDataStatusEntity.getLocationNew() != null
                && !mOfflineDataStatusEntity.getLocationNew()) {
            mLocationDownload.setType(DownloadStatus.DOWNLOAD_STATUS_HAVE_DOWNLOAD);
        } else if (mRequestTime != 0L
                && mOfflineDataStatusEntity != null
                && mOfflineDataStatusEntity.getLocationNew() != null
                && mOfflineDataStatusEntity.getLocationNew()) {
            mLocationDownload.setType(DownloadStatus.DOWNLOAD_STATUS_HAVE_UPDATE);
        }
        mDownloadProgressEntities.add(mLocationDownload);

        mDepDownload = new DownloadProgressEntity();
        mDepDownload.setName(getString(R.string.app_department));
        mDepDownload.setType(DownloadStatus.DOWNLOAD_STATUS_NOT_DOWNLOAD);
        if (mRequestTime != 0L
                && mOfflineDataStatusEntity != null
                && mOfflineDataStatusEntity.getDepartmentNew() != null
                && !mOfflineDataStatusEntity.getDepartmentNew()) {
            mDepDownload.setType(DownloadStatus.DOWNLOAD_STATUS_HAVE_DOWNLOAD);
        } else if (mRequestTime != 0L
                && mOfflineDataStatusEntity != null
                && mOfflineDataStatusEntity.getDepartmentNew() != null
                && mOfflineDataStatusEntity.getDepartmentNew()) {
            mDepDownload.setType(DownloadStatus.DOWNLOAD_STATUS_HAVE_UPDATE);
        }
        mDownloadProgressEntities.add(mDepDownload);

        mPriorityDownload = new DownloadProgressEntity();
        mPriorityDownload.setName(getString(R.string.app_priority));
        mPriorityDownload.setType(DownloadStatus.DOWNLOAD_STATUS_NOT_DOWNLOAD);
        if (mRequestTime != 0L
                && mOfflineDataStatusEntity != null
                && mOfflineDataStatusEntity.getPriorityTypeNew() != null
                && !mOfflineDataStatusEntity.getPriorityTypeNew()) {
            mPriorityDownload.setType(DownloadStatus.DOWNLOAD_STATUS_HAVE_DOWNLOAD);
        } else if (mRequestTime != 0L
                && mOfflineDataStatusEntity != null
                && mOfflineDataStatusEntity.getPriorityTypeNew() != null
                && mOfflineDataStatusEntity.getPriorityTypeNew()) {
            mPriorityDownload.setType(DownloadStatus.DOWNLOAD_STATUS_HAVE_UPDATE);
        }
        mDownloadProgressEntities.add(mPriorityDownload);

        mFlowDownload = new DownloadProgressEntity();
        mFlowDownload.setName(getString(R.string.app_work_flow));
        mFlowDownload.setType(DownloadStatus.DOWNLOAD_STATUS_NOT_DOWNLOAD);
        if (mRequestTime != 0L
                && mOfflineDataStatusEntity != null
                && mOfflineDataStatusEntity.getWorkFlowNew() != null
                && !mOfflineDataStatusEntity.getWorkFlowNew()) {
            mFlowDownload.setType(DownloadStatus.DOWNLOAD_STATUS_HAVE_DOWNLOAD);
        } else if (mRequestTime != 0L
                && mOfflineDataStatusEntity != null
                && mOfflineDataStatusEntity.getWorkFlowNew() != null
                && mOfflineDataStatusEntity.getWorkFlowNew()) {
            mFlowDownload.setType(DownloadStatus.DOWNLOAD_STATUS_HAVE_UPDATE);
        }
        mDownloadProgressEntities.add(mFlowDownload);

        mServiceTypeDownload = new DownloadProgressEntity();
        mServiceTypeDownload.setName(getString(R.string.app_service_type));
        mServiceTypeDownload.setType(DownloadStatus.DOWNLOAD_STATUS_NOT_DOWNLOAD);
        if (mRequestTime != 0L
                && mOfflineDataStatusEntity != null
                && mOfflineDataStatusEntity.getServiceTypeNew() != null
                && !mOfflineDataStatusEntity.getServiceTypeNew()) {
            mServiceTypeDownload.setType(DownloadStatus.DOWNLOAD_STATUS_HAVE_DOWNLOAD);
        } else if (mRequestTime != 0L
                && mOfflineDataStatusEntity != null
                && mOfflineDataStatusEntity.getServiceTypeNew() != null
                && mOfflineDataStatusEntity.getServiceTypeNew()) {
            mServiceTypeDownload.setType(DownloadStatus.DOWNLOAD_STATUS_HAVE_UPDATE);
        }
        mDownloadProgressEntities.add(mServiceTypeDownload);

        mDemandTypeDownload = new DownloadProgressEntity();
        mDemandTypeDownload.setName(getString(R.string.app_requirement_type));
        mDemandTypeDownload.setType(DownloadStatus.DOWNLOAD_STATUS_NOT_DOWNLOAD);
        if (mRequestTime != 0L
                && mOfflineDataStatusEntity != null
                && mOfflineDataStatusEntity.getRequirementTypeNew() != null
                && !mOfflineDataStatusEntity.getRequirementTypeNew()) {
            mDemandTypeDownload.setType(DownloadStatus.DOWNLOAD_STATUS_HAVE_DOWNLOAD);
        } else if (mRequestTime != 0L
                && mOfflineDataStatusEntity != null
                && mOfflineDataStatusEntity.getRequirementTypeNew() != null
                && mOfflineDataStatusEntity.getRequirementTypeNew()) {
            mDemandTypeDownload.setType(DownloadStatus.DOWNLOAD_STATUS_HAVE_UPDATE);
        }
        mDownloadProgressEntities.add(mDemandTypeDownload);

    }

    private void initView() {
        setTitle(R.string.app_outline_load);
        mDownloadBtn = findViewById(R.id.outline_data_download_btn);
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new OutlineDataAdapter(mDownloadProgressEntities);
        mRecyclerView.setAdapter(mAdapter);
        //mDownloadBtn.setEnabled(mEnable);
    }

    private void initListener() {
        mDownloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDataSuccess = true;
                //mDownloadBtn.setEnabled(false);
                finishedTask = 0;
                saveLocationDataSuccess = true;
                mLocationCount = 0;
                mLocationProgress = 0;
                mCurrentTimeMillis = (mOfflineDataStatusEntity == null || mOfflineDataStatusEntity.getNewestDate() == null)
                        ? System.currentTimeMillis()
                        : mOfflineDataStatusEntity.getNewestDate();
                OfflineService.downloadOfflineData(new OnDownloadListener() {//设备
                    @Override
                    public void onDownload(int max, int progress) {
                        changeDownloadStatus(mDeviceDownload, max, progress, 0);
                    }

                    @Override
                    public void onAllSuccess() {
                        mDeviceDownload.setType(DownloadStatus.DOWNLOAD_STATUS_HAVE_DOWNLOAD);
                        changeDownloadStatus(mDeviceDownload, 1, 1, 0);
                        finishedTaskAdd();
                    }

                    @Override
                    public void onError() {
                        mDeviceDownload.setType(DownloadStatus.DOWNLOAD_STATUS_FAILED_DOWNLOAD);
                        changeDownloadStatus(mDeviceDownload, 1, 1, 0);
                        saveDataSuccess = false;
                    }
                }, new OnDownloadListener() {//设备类型
                    @Override
                    public void onDownload(int max, int progress) {
                        changeDownloadStatus(mDeviceTypeDownload, max, progress, 1);
                    }

                    @Override
                    public void onAllSuccess() {
                        mDeviceTypeDownload.setType(DownloadStatus.DOWNLOAD_STATUS_HAVE_DOWNLOAD);
                        changeDownloadStatus(mDeviceTypeDownload, 1, 1, 1);
                        finishedTaskAdd();
                    }

                    @Override
                    public void onError() {
                        mDeviceTypeDownload.setType(DownloadStatus.DOWNLOAD_STATUS_FAILED_DOWNLOAD);
                        changeDownloadStatus(mDeviceTypeDownload, 1, 1, 1);
                        saveDataSuccess = false;
                    }
                }, new OnDownloadListener() {//位置
                    @Override
                    public void onDownload(int max, int progress) {
                    }

                    @Override
                    public void onAllSuccess() {
                        mLocationProgress += 20;
                        mLocationCount++;
                        changeDownloadStatus(mLocationDownload, 100, mLocationProgress, 2);
                        finishedLocation();
                        finishedTaskAdd();
                    }

                    @Override
                    public void onError() {
                        saveLocationDataSuccess = false;
                        saveDataSuccess = false;
                        finishedLocation();
                    }
                }, new OnDownloadListener() {//位置 楼层
                    private int floor = 0;

                    @Override
                    public void onDownload(int max, int progress) {
                        int floorProgress = (int) (30 * ((float) progress / max));
                        int step = floorProgress - floor;
                        floor = floorProgress;
                        mLocationProgress += step;
                        changeDownloadStatus(mLocationDownload, 100, mLocationProgress, 2);
                    }

                    @Override
                    public void onAllSuccess() {
                        mLocationCount++;
                        finishedLocation();
                        finishedTaskAdd();
                    }

                    @Override
                    public void onError() {
                        saveLocationDataSuccess = false;
                        saveDataSuccess = false;
                        finishedLocation();
                    }
                }, new OnDownloadListener() {//位置 房间
                    private int room = 0;

                    @Override
                    public void onDownload(int max, int progress) {
                        int roomProgress = (int) (30 * ((float) progress / max));
                        int step = roomProgress - room;
                        room = roomProgress;
                        mLocationProgress += step;
                        changeDownloadStatus(mLocationDownload, 100, mLocationProgress, 2);
                    }

                    @Override
                    public void onAllSuccess() {
                        mLocationCount++;
                        finishedLocation();
                        finishedTaskAdd();
                    }

                    @Override
                    public void onError() {
                        saveLocationDataSuccess = false;
                        saveDataSuccess = false;
                        finishedLocation();
                    }
                }, new OnDownloadListener() {//部门
                    @Override
                    public void onDownload(int max, int progress) {
                        changeDownloadStatus(mDepDownload, max, progress, 3);
                    }

                    @Override
                    public void onAllSuccess() {
                        mDepDownload.setType(DownloadStatus.DOWNLOAD_STATUS_HAVE_DOWNLOAD);
                        changeDownloadStatus(mDepDownload, 1, 1, 3);
                        finishedTaskAdd();
                    }

                    @Override
                    public void onError() {
                        mDepDownload.setType(DownloadStatus.DOWNLOAD_STATUS_FAILED_DOWNLOAD);
                        changeDownloadStatus(mDepDownload, 1, 1, 3);
                        saveDataSuccess = false;
                    }
                }, new OnDownloadListener() {//优先级
                    @Override
                    public void onDownload(int max, int progress) {
                        changeDownloadStatus(mPriorityDownload, max, progress, 4);
                    }

                    @Override
                    public void onAllSuccess() {
                        mPriorityDownload.setType(DownloadStatus.DOWNLOAD_STATUS_HAVE_DOWNLOAD);
                        changeDownloadStatus(mPriorityDownload, 1, 1, 4);
                        finishedTaskAdd();
                    }

                    @Override
                    public void onError() {
                        mPriorityDownload.setType(DownloadStatus.DOWNLOAD_STATUS_FAILED_DOWNLOAD);
                        changeDownloadStatus(mPriorityDownload, 1, 1, 4);
                        saveDataSuccess = false;
                    }
                }, new OnDownloadListener() {//工作流程
                    @Override
                    public void onDownload(int max, int progress) {
                        changeDownloadStatus(mFlowDownload, max, progress, 5);
                    }

                    @Override
                    public void onAllSuccess() {
                        mFlowDownload.setType(DownloadStatus.DOWNLOAD_STATUS_HAVE_DOWNLOAD);
                        changeDownloadStatus(mFlowDownload, 1, 1, 5);
                        finishedTaskAdd();
                    }

                    @Override
                    public void onError() {
                        mFlowDownload.setType(DownloadStatus.DOWNLOAD_STATUS_FAILED_DOWNLOAD);
                        changeDownloadStatus(mFlowDownload, 1, 1, 5);
                        saveDataSuccess = false;
                    }
                }, new OnDownloadListener() {//服务类型
                    @Override
                    public void onDownload(int max, int progress) {
                        changeDownloadStatus(mServiceTypeDownload, max, progress, 6);
                    }

                    @Override
                    public void onAllSuccess() {
                        mServiceTypeDownload.setType(DownloadStatus.DOWNLOAD_STATUS_HAVE_DOWNLOAD);
                        changeDownloadStatus(mServiceTypeDownload, 1, 1, 6);
                        finishedTaskAdd();
                    }

                    @Override
                    public void onError() {
                        mServiceTypeDownload.setType(DownloadStatus.DOWNLOAD_STATUS_FAILED_DOWNLOAD);
                        changeDownloadStatus(mServiceTypeDownload, 1, 1, 6);
                        saveDataSuccess = false;
                    }
                }, new OnDownloadListener() {//需求类型
                    @Override
                    public void onDownload(int max, int progress) {
                        changeDownloadStatus(mDemandTypeDownload, max, progress, 7);
                    }

                    @Override
                    public void onAllSuccess() {
                        mDemandTypeDownload.setType(DownloadStatus.DOWNLOAD_STATUS_HAVE_DOWNLOAD);
                        changeDownloadStatus(mDemandTypeDownload, 1, 1, 7);
                        finishedTaskAdd();
                    }

                    @Override
                    public void onError() {
                        mDemandTypeDownload.setType(DownloadStatus.DOWNLOAD_STATUS_FAILED_DOWNLOAD);
                        changeDownloadStatus(mDemandTypeDownload, 1, 1, 7);
                        saveDataSuccess = false;
                    }
                });
            }
        });
    }

    private void finishedLocation() {
        if (mLocationCount == 3) {//csb floor room三个请求
            mLocationDownload.setType(DownloadStatus.DOWNLOAD_STATUS_HAVE_DOWNLOAD);
            changeDownloadStatus(mLocationDownload, 100, 100, 2);//类表第二个
        }

        if (!saveLocationDataSuccess) {
            mLocationDownload.setType(DownloadStatus.DOWNLOAD_STATUS_FAILED_DOWNLOAD);
            if (mLocationCount == 3) {//csb floor room三个请求
                changeDownloadStatus(mLocationDownload, 100, 100, 2);//类表第二个
            }
        }
    }

    private void finishedTaskAdd() {
        finishedTask++;
        if (finishedTask == TASK_COUNT && saveDataSuccess) {
            OfflineService.addOrUpdateDownloadTime(mCurrentTimeMillis, OfflineTimeDao.TYPE_OFFLINE_BASE);
            ToastUtils.showShort(R.string.app_download_success);
            LogUtils.d("离线数据下载完成");
        }
    }

    /**
     * @param download list的实体信息
     * @param max      下载最大进度
     * @param progress 当前进度
     * @param position 实体在list中的position
     */
    private void changeDownloadStatus(DownloadProgressEntity download, int max, int progress, int position) {
        download.setMax(max);
        download.setProgress(progress);
        mAdapter.notifyItemChanged(position);
    }

    @Override
    public boolean isUseDialog() {
        return false;
    }

    public static OutlineDataFragment newInstance(OfflineDataStatusEntity statusEntity
            , boolean update, Long requestTime) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(DATA_UPDATE, update);
        bundle.putLong(REQUEST_TIME, requestTime);
        bundle.putParcelable(DATA_ITEM_UPDATE, statusEntity);
        OutlineDataFragment instance = new OutlineDataFragment();
        instance.setArguments(bundle);
        return instance;
    }
}
