package com.facilityone.wireless.inventory.presenter;

import com.blankj.utilcode.util.ToastUtils;
import com.facilityone.wireless.a.arch.base.FMJsonCallback;
import com.facilityone.wireless.basiclib.app.FM;
import com.facilityone.wireless.componentservice.inventory.InventoryService;
import com.facilityone.wireless.inventory.R;
import com.facilityone.wireless.inventory.fragment.InventoryReserveFragment;
import com.facilityone.wireless.inventory.model.InventoryUrl;
import com.facilityone.wireless.inventory.model.MaterialService;
import com.facilityone.wireless.inventory.model.SupervisorService;
import com.fm.tool.network.model.BaseResponse;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.List;

import me.yokeyword.fragmentation.ISupportFragment;

/**
 * Created by peter.peng on 2018/12/7.
 */

public class InventoryReservePresenter extends InventoryCommonPresenter<InventoryReserveFragment> {


    /**
     * 联网获取主管数据列表
     *
     * @param laborerId
     */
    public void getSupervisorListData(long laborerId) {
        getV().showLoading();
        laborerId = laborerId == -1 ? FM.getEmId() : laborerId;
        String request = "{\"laborerId\":" + laborerId + "}";
        OkGo.<BaseResponse<List<SupervisorService.Supervisor>>>post(FM.getApiHost() + InventoryUrl.SUPERVISOR_LIST_URL)
                .isSpliceUrl(true)
                .tag(getV())
                .upJson(request)
                .execute(new FMJsonCallback<BaseResponse<List<SupervisorService.Supervisor>>>() {
                    @Override
                    public void onSuccess(Response<BaseResponse<List<SupervisorService.Supervisor>>> response) {
                        getV().dismissLoading();
                        List<SupervisorService.Supervisor> data = response.body().data;
                        List<SupervisorService.Supervisor> supervisorList = getV().getSupervisorList();
                        supervisorList.clear();
                        if (data != null && data.size() > 0) {
                            supervisorList.addAll(data);
                        }

                        if (supervisorList != null && supervisorList.size() > 0) {
                            getV().refreshSupervisor();
                        }
                    }

                    @Override
                    public void onError(Response<BaseResponse<List<SupervisorService.Supervisor>>> response) {
                        super.onError(response);
                        getV().dismissLoading();
                    }
                });
    }

    /**
     * 物资预定
     * @param fromtype
     * @param request
     */
    public void InventoryMaterialReserve(final int fromtype, MaterialService.MaterialReserveRequest request) {
        getV().showLoading();
        OkGo.<BaseResponse<Object>>post(FM.getApiHost() + InventoryUrl.INVENTORY_RESERVE_URL)
                .isSpliceUrl(true)
                .tag(getV())
                .upJson(toJson(request))
                .execute(new FMJsonCallback<BaseResponse<Object>>() {
                    @Override
                    public void onSuccess(Response<BaseResponse<Object>> response) {
                        getV().dismissLoading();
                        ToastUtils.showShort(R.string.inventory_operate_success);
                        if(fromtype == InventoryService.TYPE_FROM_WORKORDER) {
                            getV().setFragmentResult(ISupportFragment.RESULT_OK,null);
                        }
                        getV().pop();
                    }

                    @Override
                    public void onError(Response<BaseResponse<Object>> response) {
                        ToastUtils.showShort(R.string.inventory_operate_fail);
                        super.onError(response);
                        getV().dismissLoading();
                    }
                });
    }
}
