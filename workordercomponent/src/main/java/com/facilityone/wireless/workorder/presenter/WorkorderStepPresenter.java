package com.facilityone.wireless.workorder.presenter;

import com.blankj.utilcode.util.ToastUtils;
import com.facilityone.wireless.a.arch.base.FMJsonCallback;
import com.facilityone.wireless.a.arch.mvp.BasePresenter;
import com.facilityone.wireless.basiclib.app.FM;
import com.facilityone.wireless.workorder.R;
import com.facilityone.wireless.workorder.fragment.WorkorderStepFragment;
import com.facilityone.wireless.workorder.module.WorkorderOptService;
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
    private boolean taskStatus = false;
    //判断是否有任务且该任务为当前处理中的任务
    public void isDoneDevice(Long woId,String eqCode){
        getV().showLoading();
        WorkorderService.ShortestTimeReq request = new WorkorderService.ShortestTimeReq();
        request.woId = woId;
        request.eqCode = eqCode;
        OkGo.<BaseResponse<WorkorderService.ShortestTimeResp>>post(FM.getApiHost() + WorkorderUrl.QUERY_SHORTEST_TIME)
                .tag(getV())
                .isSpliceUrl(true)
                .upJson(toJson(request))
                .execute(new FMJsonCallback<BaseResponse<WorkorderService.ShortestTimeResp>>() {
                    @Override
                    public void onSuccess(Response<BaseResponse<WorkorderService.ShortestTimeResp>> response) {
                        getV().dismissLoading();
                        WorkorderService.ShortestTimeResp resp = response.body().data;
                        if (resp.status != null){
                            if (resp.status == 0 ){
                                taskStatus = true;
                                if (resp.countdown == null){
                                    getV().setTaskTime(1);
                                }else {
                                    getV().setTaskTime(resp.countdown);
                                }

                            }else {
                                taskStatus = false;
                            }
                        }else {
                            taskStatus = true;
                            if (resp.countdown == null){
                                getV().setTaskTime(1);
                            }else {
                                getV().setTaskTime(resp.countdown);
                            }
                        }
                        getV().setHasDoneDevice(taskStatus);

                    }

                    @Override
                    public void onError(Response<BaseResponse<WorkorderService.ShortestTimeResp>> response) {
                        getV().dismissLoading();
                        ToastUtils.showShort(R.string.workorder_operate_fail);
                        super.onError(response);
                        ToastUtils.showShort("数据异常");
                    }
                });


    }

    //任务开启
    public void beganToTask(Long woId,String eqCode){
        WorkorderService.DoShortestTaskReq request = new WorkorderService.DoShortestTaskReq();
        request.woId = woId;
        request.eqCode = eqCode;
        OkGo.<BaseResponse<Boolean>>post(FM.getApiHost() + WorkorderUrl.DO_SHORTEST_TASK)
                .tag(getV())
                .isSpliceUrl(true)
                .upJson(toJson(request))
                .execute(new FMJsonCallback<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponse<Boolean>> response) {
                        ToastUtils.showShort("任务已开启，请知悉");
                        getV().workStart();
                    }

                    @Override
                    public void onError(Response<BaseResponse<Boolean>> response) {
                        ToastUtils.showShort(R.string.workorder_operate_fail);
                        super.onError(response);
                        getV().pop();
                    }
                });

    }

    //编辑故障设备
    public void editorWorkorderDevice(WorkorderOptService.WorkOrderDeviceReq request) {
        getV().showLoading();
        OkGo.<BaseResponse<Object>>post(FM.getApiHost() + WorkorderUrl.WORKORDER_EDITOR_DEVICE_URL)
                .tag(getV())
                .upJson(toJson(request))
                .isSpliceUrl(true)
                .execute(new FMJsonCallback<BaseResponse<Object>>() {
                    @Override
                    public void onSuccess(Response<BaseResponse<Object>> response) {
                        getV().dismissLoading();
                        ToastUtils.showShort(R.string.workorder_operate_success);
                    }

                    @Override
                    public void onError(Response<BaseResponse<Object>> response) {
                        super.onError(response);
                        getV().dismissLoading();
                        ToastUtils.showShort(R.string.workorder_device_exist);
                    }

                    @Override
                    public void onFinish() {
                        getV().dismissLoading();
                    }
                });
    }

}
