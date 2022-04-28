package com.facilityone.wireless.maintenance.presenter;

import android.content.Context;

import com.blankj.utilcode.util.ToastUtils;
import com.facilityone.wireless.a.arch.base.FMJsonCallback;
import com.facilityone.wireless.a.arch.ec.commonpresenter.CommonBasePresenter;
import com.facilityone.wireless.a.arch.ec.module.Page;
import com.facilityone.wireless.a.arch.ec.module.AttachmentBean;
import com.facilityone.wireless.a.arch.ec.module.Page;
import com.facilityone.wireless.basiclib.app.FM;
import com.facilityone.wireless.basiclib.utils.DebounceAction;
import com.facilityone.wireless.basiclib.utils.GsonUtils;
import com.facilityone.wireless.maintenance.R;
import com.facilityone.wireless.maintenance.fragment.MaintenanceListFragment;
import com.facilityone.wireless.maintenance.model.MaintenanceConstant;
import com.facilityone.wireless.maintenance.model.MaintenanceEnity;
import com.facilityone.wireless.maintenance.model.MaintenanceUrl;
import com.facilityone.wireless.maintenance.model.MaintenanceService;
import com.facilityone.wireless.maintenance.model.MaintenanceUrl;
import com.fm.tool.network.model.BaseResponse;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.List;
import static com.facilityone.wireless.maintenance.model.MaintenanceUrl.MAINTENANCE_ORDER_RECEIVE;

public class MaintenanceListPresenter extends CommonBasePresenter<MaintenanceListFragment> {

    /**
     * @Auther: karelie
     * @Date: 2021/8/18
     * @Infor: 获取列表数据
     */

    public void getMaintenanceList(Integer type, Page page,MaintenanceService.ConditionBean conditionBean,boolean refresh){
        getV().showLoading();
        final MaintenanceEnity.MaintenanceListReq request = new MaintenanceEnity.MaintenanceListReq();
        if (type == MaintenanceConstant.FIVE){
            request.searchCondit = conditionBean;
        }else {
            request.searchCondition = conditionBean;
        }


        String url = "";
        switch (type) {
            case MaintenanceConstant.ZERO:
                break;
            case MaintenanceConstant.ONE: //待处理维护工单
                url = MaintenanceUrl.MAINTENANCE_UNDO_URL;
                page.setPageSize(99999); //不分页
                request.type = 0;
                request.page=page;
                break;
            case MaintenanceConstant.TWO: //待派工维护工单
                page.setPageSize(99999); //不分页
                request.page=page;
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
            case MaintenanceConstant.SEVEN: //待抽检维护工单
                url = MaintenanceUrl.MAINTENANCE_SAMPLE_URL;
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
                            getV().noDataRefresh();
                            return;
                        }
                        getV().refreshSuccessUI(data.contents,refresh,data.page);


                    }

                    @Override
                    public void onError(Response<BaseResponse<MaintenanceEnity.MaintenanceListResp>> response) {
                        super.onError(response);
                        getV().dismissLoading();
                    }
                });
    }


    /**
     * @Auther: karelie
     * @Date: 2021/8/19
     * @Infor: 批量接单/接单
     */
    public void receiveOrder(MaintenanceEnity.ReceiveOrderReq req) {
        getV().showLoading();
        String url = "";
        url = MAINTENANCE_ORDER_RECEIVE; //批量接单
        OkGo.<BaseResponse<Object>>post(FM.getApiHost() + url)
                .tag(getV())
                .isSpliceUrl(true)
                .upJson(toJson(req))
                .execute(new FMJsonCallback<BaseResponse<Object>>() {
                    @Override
                    public void onSuccess(Response<BaseResponse<Object>> response) {
                        getV().dismissLoading();
                        ToastUtils.showShort("接单成功");
                        getV().initData(); //列表刷新
                    }

                    @Override
                    public void onError(Response<BaseResponse<Object>> response) {
                        super.onError(response);
                        getV().dismissLoading();
                        ToastUtils.showShort("提交失败");
                    }
                });
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
}
