package com.facilityone.wireless.workorder.presenter;

import com.facilityone.wireless.a.arch.base.FMJsonCallback;
import com.facilityone.wireless.a.arch.mvp.BasePresenter;
import com.facilityone.wireless.basiclib.app.FM;
import com.facilityone.wireless.workorder.fragment.WorkorderStepFragment;
import com.facilityone.wireless.workorder.module.WorkorderService;
import com.facilityone.wireless.workorder.module.WorkorderUrl;
import com.fm.tool.network.model.BaseResponse;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

/**
 * Author：gary
 * Email: xuhaozv@163.com
 * description:
 * Date: 2018/9/28 2:18 PM
 */
public class WorkorderStepPresenter extends BasePresenter<WorkorderStepFragment> {

    public void getStepInfor(Long woId){
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
                            getV().refreshStep(data);
                        }
                    }

                    @Override
                    public void onError(Response<BaseResponse<WorkorderService.WorkorderInfoBean>> response) {
                        super.onError(response);
                        getV().dismissLoading();
                    }
                });
    }


    /**
     * @Creator:Karelie
     * @Data: 2021/10/11
     * @TIME: 16:09
     * @Introduce: 判断维护工单当前是否有进行中的倒计时
     **/
    private static boolean isDoneDevice(){
        return false;
    }

}
