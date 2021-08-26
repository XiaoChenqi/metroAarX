package com.facilityone.wireless.inventory.presenter;

import com.facilityone.wireless.inventory.fragment.InventoryReserveOutFragment;
import com.facilityone.wireless.inventory.model.ReserveService;
import com.fm.tool.network.model.BaseResponse;
import com.lzy.okgo.model.Response;

/**
 * Created by peter.peng on 2018/12/3.
 */

public class InventoryReserveOutPresenter extends InventoryCommonPresenter<InventoryReserveOutFragment> {

    /**
     * 联网获取预订记录列表数据成功后回调
     * @param data
     * @param refresh
     */
    @Override
    public void getReserveRecordListSuccess(ReserveService.ReserveRecordListBean data, boolean refresh) {
        super.getReserveRecordListSuccess(data, refresh);
        if(data != null && data.contents != null) {
            getV().getReserveRecordListSuccess(data.contents, data.page, refresh);
        } else {
            getV().getReserveRecordListError();
        }
    }

    /**
     * 联网获取预订记录列表数据失败后回调
     * @param response
     */
    @Override
    public void getReserveRecordListError(Response<BaseResponse<ReserveService.ReserveRecordListBean>> response) {
        super.getReserveRecordListError(response);
        getV().getReserveRecordListError();
    }
}
