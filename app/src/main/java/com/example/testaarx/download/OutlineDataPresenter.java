package com.example.testaarx.download;

import com.facilityone.wireless.a.arch.base.FMJsonCallback;
import com.facilityone.wireless.a.arch.mvp.BasePresenter;
import com.facilityone.wireless.basiclib.app.FM;
import com.fm.tool.network.model.BaseResponse;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

/**
 * Created by: owen.
 * Date: on 2018/6/8 下午3:12.
 * Description: 离线数据下载
 * email:
 */

public class OutlineDataPresenter extends BasePresenter<OutlineDataFragment> {

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
