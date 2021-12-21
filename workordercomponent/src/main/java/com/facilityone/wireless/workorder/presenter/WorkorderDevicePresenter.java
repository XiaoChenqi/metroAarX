package com.facilityone.wireless.workorder.presenter;

import android.content.Intent;
import android.text.TextUtils;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.facilityone.wireless.a.arch.base.FMJsonCallback;
import com.facilityone.wireless.a.arch.base.ScanInterface;
import com.facilityone.wireless.a.arch.ec.ui.FzScanActivity;
import com.facilityone.wireless.basiclib.app.FM;
import com.facilityone.wireless.workorder.R;
import com.facilityone.wireless.workorder.fragment.WorkorderDeviceFragment;
import com.facilityone.wireless.workorder.module.WorkorderService;
import com.facilityone.wireless.workorder.module.WorkorderUrl;
import com.fm.tool.network.model.BaseResponse;
import com.fm.tool.scan.ScanActivity;
import com.huawei.hms.ml.scan.HmsScan;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.zdf.activitylauncher.ActivityLauncher;

/**
 * Author：gary
 * Email: xuhaozv@163.com
 * description:
 * Date: 2018/9/20 下午5:02
 */
public class WorkorderDevicePresenter extends WorkorderScanPresenter<WorkorderDeviceFragment> {
    private boolean taskStatus = false;
    @Override
    public void onEditorWorkorderDeviceSuccess() {
        getV().refreshList();
    }

    public void scan(final WorkorderService.WorkOrderEquipmentsBean equipmentCode) {
        getV().FMScan(getV().getContext(),getV().getActivity(),getV());
    }

    public void getWorkorderInfo(final Long woId) {
        getV().showLoading();
        String json = "{\"woId\":" + woId + "}";
        OkGo.<BaseResponse<WorkorderService.WorkorderInfoBean>>post(FM.getApiHost() + WorkorderUrl.WORKORDER_INFO_URL)
                .tag(getV())
                .isSpliceUrl(true)
                .upJson(json)
                .execute(new FMJsonCallback<BaseResponse<WorkorderService.WorkorderInfoBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponse<WorkorderService.WorkorderInfoBean>> response) {
                        WorkorderService.WorkorderInfoBean data = response.body().data;
                        if (data != null) {
                            getV().refreshEquipmentUI(data);
                        } else {
                            getV().refreshError();
                        }
                        getV().dismissLoading();
                    }

                    @Override
                    public void onError(Response<BaseResponse<WorkorderService.WorkorderInfoBean>> response) {
                        super.onError(response);
                        getV().refreshError();
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
    //判断是否有任务且该任务为当前处理中的任务
    public void isDoneDevice(Long woId,String eqCode,WorkorderService.WorkOrderEquipmentsBean  bean){
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
                        if (resp.executable != null){
                            if (resp.executable == true){
                                if (woId.equals(resp.woId) && eqCode.equals(resp.eqCode)){
                                    taskStatus = false;
                                }else {
                                    taskStatus = true;
                                }
                                getV().setCando(taskStatus,bean);
                            }else {
                                taskStatus = false;
                                getV().setCando(taskStatus,bean);
                            }
                        }else {
                            taskStatus = false;
                            getV().setCando(taskStatus,bean);
                        }

                    }

                    @Override
                    public void onError(Response<BaseResponse<WorkorderService.ShortestTimeResp>> response) {
                        ToastUtils.showShort(R.string.workorder_operate_fail);
                        super.onError(response);
                        ToastUtils.showShort("数据异常");
                    }
                });

    }
    
    /**
     * @Creator:Karelie
     * @Data: 2021/10/11
     * @TIME: 16:23
     * @Introduce: 开始当前设备任务
    **/
    public void beginToDo(){

    }

    
    
}
