package com.facilityone.wireless.workorder.presenter;

import com.blankj.utilcode.util.ToastUtils;
import com.facilityone.wireless.a.arch.base.FMJsonCallback;
import com.facilityone.wireless.basiclib.app.FM;
import com.facilityone.wireless.workorder.R;
import com.facilityone.wireless.workorder.fragment.WorkorderDeviceEditorFragment;
import com.facilityone.wireless.workorder.module.WorkorderService;
import com.facilityone.wireless.workorder.module.WorkorderUrl;
import com.fm.tool.network.model.BaseResponse;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

/**
 * Author：gary
 * Email: xuhaozv@163.com
 * description:
 * Date: 2018/9/21 下午5:31
 */
public class WorkorderDeviceEditorPresenter extends WorkorderBasePresenter<WorkorderDeviceEditorFragment> {
    private boolean taskStatus = false;
    @Override
    public void onEditorWorkorderDeviceSuccess() {
        getV().saveResult();
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
                        getV().dismissLoading();
                        ToastUtils.showShort("任务已开启，请知悉");
                    }

                    @Override
                    public void onError(Response<BaseResponse<Boolean>> response) {
                        ToastUtils.showShort(R.string.workorder_operate_fail);
                        super.onError(response);
                        getV().pop();
                    }
                });

    }

    //判断是否有任务且该任务为当前处理中的任务
    public void isDoneDevice(Long woId,String eqCode){
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
                        ToastUtils.showShort(R.string.workorder_operate_fail);
                        super.onError(response);
                        ToastUtils.showShort("数据异常");
                    }
                });


    }

}
