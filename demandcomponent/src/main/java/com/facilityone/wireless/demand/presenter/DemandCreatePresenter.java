package com.facilityone.wireless.demand.presenter;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.facilityone.wireless.a.arch.base.FMJsonCallback;
import com.facilityone.wireless.a.arch.ec.commonpresenter.CommonBasePresenter;
import com.facilityone.wireless.a.arch.ec.module.CommonUrl;
import com.facilityone.wireless.a.arch.ec.module.ConstantMeida;
import com.facilityone.wireless.basiclib.app.FM;
import com.facilityone.wireless.demand.R;
import com.facilityone.wireless.demand.fragment.DemandCreateFragment;
import com.facilityone.wireless.demand.module.DemandCreateService;
import com.facilityone.wireless.demand.module.DemandService;
import com.facilityone.wireless.demand.module.DemandUrl;
import com.fm.tool.network.model.BaseResponse;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.List;

/**
 * Author：gary
 * Email: xuhaozv@163.com
 * description:
 * Date: 2018/6/21 下午4:07
 */
public class DemandCreatePresenter extends CommonBasePresenter<DemandCreateFragment> {
    @Override
    public void uploadFileSuccess(List<String> ids, int type) {
        switch (type) {
            case ConstantMeida.IMAGE:
                if (ids != null) {
//                    List<String> idsList = new ArrayList<>();
//                    idsList = getV().getRequest().photoIds;
//                    idsList.addAll(ids);
                    if (getV().getRequest().photoIds != null){
                        getV().getRequest().photoIds.addAll(ids);
                    }else {
                        getV().getRequest().photoIds = ids;
                    }

                }
                break;
            case ConstantMeida.VIDEO:
                if (ids != null) {
                    getV().getRequest().videoIds = ids;
                }
                break;
            case ConstantMeida.AUDIO:
                if (ids != null) {
                    getV().getRequest().audioIds = ids;
                }
                break;
        }
    }

    @Override
    public void uploadFileFinish(int type) {
        switch (type) {
            case ConstantMeida.IMAGE:
                if (getV().getVideoSelectList().size() > 0) {
                    uploadFile(getV().getVideoSelectList(), CommonUrl.UPLOAD_VIDEO_URL, ConstantMeida.VIDEO);
                } else if (getV().getAudioSelectList().size() > 0) {
                    uploadFile(getV().getAudioSelectList(), CommonUrl.UPLOAD_VOICE_URL, ConstantMeida.AUDIO);
                }
                break;
            case ConstantMeida.VIDEO:
                if (getV().getAudioSelectList().size() > 0) {
                    uploadFile(getV().getAudioSelectList(), CommonUrl.UPLOAD_VOICE_URL, ConstantMeida.AUDIO);
                }
                break;
            case ConstantMeida.AUDIO:
                if (getV().getVideoSelectList().size() >0){
                    uploadFile(getV().getVideoSelectList(), CommonUrl.UPLOAD_VIDEO_URL, ConstantMeida.VIDEO);
                }
                break;
        }
        createDemand();
    }

    public void createDemand() {
        DemandCreateService.DemandCreateReq request = getV().getRequest();
        OkGo.<BaseResponse<Object>>post(FM.getApiHost() + DemandUrl.DEMAND_CREATE_URL)
                .isSpliceUrl(true)
                .tag(getV())
                .upJson(toJson(request))
                .execute(new FMJsonCallback<BaseResponse<Object>>() {
                    @Override
                    public void onSuccess(Response<BaseResponse<Object>> response) {
                        getV().dismissLoading();
                        ToastUtils.showShort(R.string.demand_create_success);
//                        getV().pop();
//                        getV().getActivity().finish();

                        getV().leftBackListener();
                    }

                    @Override
                    public void onError(Response<BaseResponse<Object>> response) {
                        super.onError(response);
                        getV().dismissLoading();
                        ToastUtils.showShort(R.string.demand_create_failed);
                    }
                });

    }
    
    public void completeDevice(){
        DemandCreateService.CompleteDeviceReq request = getV().getCompleteRequest();
        OkGo.<BaseResponse<Object>>post(FM.getApiHost() + DemandUrl.DEMAND_COMPLETE_DEVICE)
                .isSpliceUrl(true)
                .tag(getV())
                .upJson(toJson(request))
                .execute(new FMJsonCallback<BaseResponse<Object>>() {
                    @Override
                    public void onSuccess(Response<BaseResponse<Object>> response) {
                        getV().dismissLoading();
                        ToastUtils.showShort(R.string.demand_create_success);
                        getV().pop();
                    }

                    @Override
                    public void onError(Response<BaseResponse<Object>> response) {
                        super.onError(response);
                        getV().dismissLoading();
                        ToastUtils.showShort(R.string.demand_create_failed);
                    }
                });
    }

    @Override
    public void getUserInfo() {
        getV().showLoading();
        super.getUserInfo();
    }

    @Override
    public void getUserInfoSuccess(String toJson) {
        getV().refreshUserInfo(toJson);
        getV().dismissLoading();
    }


    /**
     * @Created by: kuuga
     * @Date: on 2021/8/25 10:40
     * @Description: 获取最后一次签到记录
     */
    public void getLastAttendance(){
        getV().showLoading();
        String json = "{}";
        OkGo.<BaseResponse<DemandCreateService.AttendanceResp>>post(FM.getApiHost() + DemandUrl.ATTENDANCE_LAST)
                .tag(getV())
                .isSpliceUrl(true)
                .upJson(json)
                .execute(new FMJsonCallback<BaseResponse<DemandCreateService.AttendanceResp>>() {
                    @Override
                    public void onSuccess(Response<BaseResponse<DemandCreateService.AttendanceResp>> response) {
                        getV().dismissLoading();
                        DemandCreateService.AttendanceResp data = response.body().data;
                        if(data != null) {
                            getV().RefreshSigon(data);
                        }
                    }

                    @Override
                    public void onError(Response<BaseResponse<DemandCreateService.AttendanceResp>> response) {
                        super.onError(response);
                        getV().dismissLoading();
                    }
                });
    }
}
