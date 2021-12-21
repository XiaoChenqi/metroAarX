package com.facilityone.wireless.maintenance.presenter;

import android.content.Context;

import com.facilityone.wireless.a.arch.base.FMJsonCallback;
import com.facilityone.wireless.a.arch.ec.module.AttachmentBean;
import com.facilityone.wireless.a.arch.ec.module.Page;
import com.facilityone.wireless.basiclib.app.FM;
import com.facilityone.wireless.maintenance.R;
import com.facilityone.wireless.maintenance.fragment.MaintenanceQueryFragment;


import com.facilityone.wireless.maintenance.model.MaintenanceConstant;
import com.facilityone.wireless.maintenance.model.MaintenanceEnity;
import com.facilityone.wireless.maintenance.model.MaintenanceService;
import com.facilityone.wireless.maintenance.model.MaintenanceUrl;
import com.fm.tool.network.model.BaseResponse;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.facilityone.wireless.basiclib.app.FM.getApiHost;

/**
 * @Created by: kuuga
 * @Date: on 2021/8/19 13:52
 * @Description:
 */
public class MaintenanceQueryPresenter extends BaseMaintenancePresenter<MaintenanceQueryFragment> {

    public void getMaintenanceList(Integer type, Page page, MaintenanceService.ConditionBean conditionBean,boolean refresh){
        getV().showLoading();
        MaintenanceEnity.MaintenanceListReq request = new MaintenanceEnity.MaintenanceListReq();
        request.type=type;
        request.searchCondition=conditionBean;
        String url = "";
        switch (type) {
            case MaintenanceConstant.ZERO: //待处理维护工单
            case MaintenanceConstant.ONE: //接单维护工单
                url = MaintenanceUrl.MAINTENANCE_UNDO_URL;
                break;
            case MaintenanceConstant.TWO: //待派工维护工单
                url = MaintenanceUrl.MAINTENANCE_LIST_DISPATCH_URL;
                break;
            case MaintenanceConstant.THREE: //待审批维护工单
                url = MaintenanceUrl.MAINTENANCE_LIST_APPROVAL_URL;
                request.page = page;
                break;
            case MaintenanceConstant.FOUR: //异常维护工单
                url = MaintenanceUrl.MAINTENANCE_LIST_ABNOMAL_URL;
                request.page = page;
                break;
            case MaintenanceConstant.FIVE: //带存档维护工单
                url = MaintenanceUrl.MAINTENANCE_LIST_TO_CLOSED_URL;
                request.page = page;
                break;
            case MaintenanceConstant.SIX: //维护工单查询
                url = MaintenanceUrl.MAINTENANCE_LIST_TO_QUERY_URL;
                request.page = page;
                break;
        }
        OkGo.<BaseResponse<MaintenanceEnity.MaintenanceListResp>>post(FM.getApiHost() + url)
                .tag(getV())
                .isSpliceUrl(true)
                .upJson(toJson(request))
                .execute(new FMJsonCallback<BaseResponse<MaintenanceEnity.MaintenanceListResp>>() {
                    @Override
                    public void onSuccess(Response<BaseResponse<MaintenanceEnity.MaintenanceListResp>> response) {
                        getV().dismissLoading();
                        MaintenanceEnity.MaintenanceListResp data = response.body().data;
                        if (data == null || data.contents == null || data.contents.size() == 0) {
                            getV().noDataRefresh(data.contents);
                            return;
                        }
                        getV().refreshSuccessUI(data.contents, data.page, refresh);
                    }

                    @Override
                    public void onError(Response<BaseResponse<MaintenanceEnity.MaintenanceListResp>> response) {
                        super.onError(response);
                        getV().dismissLoading();
                    }
                });
    }

    /**
     * 工单状态
     *
     * @param context
     * @return
     */
    public List<AttachmentBean> getWorkorderStatus(Context context) {
        List<AttachmentBean> ts = new ArrayList<>();
        String[] stringArray = context.getResources().getStringArray(R.array.maintenance_query_stat);

        if (stringArray.length > 0) {
            for (int i = 0, length = stringArray.length; i < length; i++) {
                AttachmentBean tb = new AttachmentBean();
                tb.value = (long) i;
                tb.name = stringArray[i];
                ts.add(tb);
            }
        }

        return ts;
    }

    /**
     * 工单标签
     *
     * @param context
     * @return
     */
    public List<AttachmentBean> getWorkorderLabels(Context context) {
        List<AttachmentBean> ts = new ArrayList<>();
        String[] stringArray = context.getResources().getStringArray(R.array.maintenance_label_stat);

        if (stringArray.length > 0) {
            for (int i = 0, length = stringArray.length; i < length; i++) {
                AttachmentBean tb = new AttachmentBean();
                tb.value = (long) i;
                tb.name = stringArray[i];
                ts.add(tb);
            }
        }

        return ts;
    }


    /**
     * 工单周期
     *
     * @param context
     * @return
     */
    public List<AttachmentBean> getWorkorderCycle(Context context) {
        List<AttachmentBean> ts = new ArrayList<>();
        String[] stringArray = context.getResources().getStringArray(R.array.maintenance_cycle_stat);

        if (stringArray.length > 0) {
            for (int i = 0, length = stringArray.length; i < length; i++) {
                AttachmentBean tb = new AttachmentBean();
                tb.value = (long) i;
                tb.name = stringArray[i];
                ts.add(tb);
            }
        }

        return ts;
    }


    @Override
    public void getPriority(List<AttachmentBean> as) {
        getV().setPriorityAs(as);
    }

    @Override
    public void setPriority(Map<Long, String> p) {
        getV().setPriority(p);
    }
}
