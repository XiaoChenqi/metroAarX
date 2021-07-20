package com.example.testaarx.download;

import com.blankj.utilcode.util.ToastUtils;
import com.facilityone.wireless.a.arch.base.FMJsonCallback;
import com.facilityone.wireless.a.arch.ec.commonpresenter.CommonBasePresenter;
import com.facilityone.wireless.a.arch.ec.logon.UserUrl;
import com.facilityone.wireless.a.arch.offline.dao.OfflineTimeDao;
import com.facilityone.wireless.a.arch.offline.model.service.OfflineService;
import com.facilityone.wireless.basiclib.app.FM;
import com.fm.tool.network.model.BaseResponse;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.List;

import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DefaultObserver;

/**
 * Author：gary
 * Email: xuhaozv@163.com
 * description:
 * Date: 2018/5/30 下午3:32
 */
public class MinePresenter extends CommonBasePresenter<MineFragment> {



    /**
     * 获取离线数据状态
     */
    public void requestOfflineStatus() {
        OfflineService.queryOfflineTime(new DefaultObserver<Long>() {
            @Override
            public void onNext(@NonNull Long aLong) {
                getV().setRequestTime(aLong);
                requestOfflineStatus(aLong);
                cancel();
            }

            @Override
            public void onError(@NonNull Throwable e) {
                getV().setRequestTime(0L);
                requestOfflineStatus(0L);
                cancel();
            }

            @Override
            public void onComplete() {

            }
        }, OfflineTimeDao.TYPE_OFFLINE_BASE);
    }

    private void requestOfflineStatus(Long time) {
        String request = "{\"preRequestDate\":" + time + "}";
        OkGo.<BaseResponse<OfflineDataStatusEntity>>post(FM.getApiHost() + APPUrl.APP_OFFLINE_DATA_STATUS_URL)
                .tag(getV())
                .isSpliceUrl(true)
                .upJson(request)
                .execute(new FMJsonCallback<BaseResponse<OfflineDataStatusEntity>>() {
                    @Override
                    public void onSuccess(Response<BaseResponse<OfflineDataStatusEntity>> response) {
                        OfflineDataStatusEntity data = response.body().data;
                        boolean haveData = false;
                        if (data != null) {
                            if (data.getPriorityTypeNew() != null && data.getPriorityTypeNew()) {
                                haveData = data.getPriorityTypeNew();
                            } else if (data.getWorkFlowNew() != null && data.getWorkFlowNew()) {
                                haveData = data.getWorkFlowNew();
                            } else if (data.getDeviceNew() != null && data.getDeviceNew()) {
                                haveData = data.getDeviceNew();
                            } else if (data.getLocationNew() != null && data.getLocationNew()) {
                                haveData = data.getLocationNew();
                            } else if (data.getServiceTypeNew() != null && data.getServiceTypeNew()) {
                                haveData = data.getServiceTypeNew();
                            } else if (data.getDepartmentNew() != null && data.getDepartmentNew()) {
                                haveData = data.getDepartmentNew();
                            } else if (data.getDeviceTypeNew() != null && data.getDeviceTypeNew()) {
                                haveData = data.getDeviceTypeNew();
                            } else if (data.getRequirementTypeNew() != null && data.getRequirementTypeNew()) {
                                haveData = data.getRequirementTypeNew();
                            }

                        }
                        getV().changeOfflineData(data, haveData);
                    }
                });
    }
}
