package com.facilityone.wireless.workorder.presenter;

import com.blankj.utilcode.util.ToastUtils;
import com.facilityone.wireless.a.arch.base.FMJsonCallback;
import com.facilityone.wireless.a.arch.ec.commonpresenter.CommonBasePresenter;
import com.facilityone.wireless.basiclib.app.FM;
import com.facilityone.wireless.workorder.R;
import com.facilityone.wireless.workorder.fragment.WorkorderStepUpdateFragment;
import com.facilityone.wireless.workorder.module.WorkorderService;
import com.facilityone.wireless.workorder.module.WorkorderUrl;
import com.fm.tool.network.model.BaseResponse;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.List;

/**
 * Author：gary
 * Email: xuhaozv@163.com
 * description:
 * Date: 2018/9/27 10:44 AM
 */
public class WorkorderStepUpdatePresenter extends CommonBasePresenter<WorkorderStepUpdateFragment> {

    @Override
    public void uploadFileSuccess(List<String> ids, int type) {
        WorkorderService.WorkorderStepUpdateReq request = getV().getRequest();
        getV().setPhoto(ids);
//        request.photos = ids;
    }

    @Override
    public void uploadFileFinish(int type) {
        updateStep();
    }

    public void updateStep() {
        WorkorderService.WorkorderStepUpdateReq request = getV().getRequest();
        OkGo.<BaseResponse<Object>>post(FM.getApiHost() + WorkorderUrl.WORKORDER_EDITOR_STEP_URL)
                .tag(getV())
                .isSpliceUrl(true)
                .upJson(toJson(request))
                .execute(new FMJsonCallback<BaseResponse<Object>>() {
                    @Override
                    public void onSuccess(Response<BaseResponse<Object>> response) {
                        getV().dismissLoading();
                        ToastUtils.showShort(R.string.workorder_operate_success);
                        getV().uploadSuccess();
                    }

                    @Override
                    public void onError(Response<BaseResponse<Object>> response) {
                        super.onError(response);
                        getV().dismissLoading();
                        ToastUtils.showShort(R.string.workorder_operate_fail);
                    }
                });
    }

    public void getInfor(Long woId){
        getV().showLoading();
        String json = "{\"woId\":" + woId + "}";
        OkGo.<BaseResponse<WorkorderService.WorkorderInfoBean>>post(FM.getApiHost() + WorkorderUrl.WORKORDER_INFO_URL)
                .tag(getV())
                .isSpliceUrl(true)
                .upJson(json)
                .execute(new FMJsonCallback<BaseResponse<WorkorderService.WorkorderInfoBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponse<WorkorderService.WorkorderInfoBean>> response) {
                        getV().dismissLoading();
                        WorkorderService.WorkorderInfoBean data = response.body().data;
                        if(data != null) {
                            getV().getInforSuccess(data);
                        }else {
                            getV().getInforError();
                        }
                    }

                    @Override
                    public void onError(Response<BaseResponse<WorkorderService.WorkorderInfoBean>> response) {
                        super.onError(response);
                        getV().dismissLoading();
                        getV().getInforError();
                    }
                });
    }
}
