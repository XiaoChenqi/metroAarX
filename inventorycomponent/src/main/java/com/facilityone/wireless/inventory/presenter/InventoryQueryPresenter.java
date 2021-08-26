package com.facilityone.wireless.inventory.presenter;

import com.blankj.utilcode.util.ToastUtils;
import com.facilityone.wireless.a.arch.base.FMJsonCallback;
import com.facilityone.wireless.a.arch.ec.module.Page;
import com.facilityone.wireless.a.arch.mvp.BasePresenter;
import com.facilityone.wireless.basiclib.app.FM;
import com.facilityone.wireless.inventory.R;
import com.facilityone.wireless.inventory.fragment.InventoryQueryFragment;
import com.facilityone.wireless.inventory.model.InventoryUrl;
import com.facilityone.wireless.inventory.model.StorageService;
import com.fm.tool.network.model.BaseResponse;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

/**
 * Created by peter.peng on 2018/12/11.
 */

public class InventoryQueryPresenter extends BasePresenter<InventoryQueryFragment> {
    /**
     * 联网获取仓库列表数据
     * @param page
     * @param refresh
     */
    public void getStorageListData(Page page, final boolean refresh) {
        StorageService.WareHouseListRequest request = new StorageService.WareHouseListRequest();
        request.page = page;

        OkGo.<BaseResponse<StorageService.WareHouseListBean>>post(FM.getApiHost() + InventoryUrl.WAREHOUSE_LIST_URL)
                .isSpliceUrl(true)
                .tag(getV())
                .upJson(toJson(request))
                .execute(new FMJsonCallback<BaseResponse<StorageService.WareHouseListBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponse<StorageService.WareHouseListBean>> response) {
                        StorageService.WareHouseListBean data = response.body().data;
                        if(data != null && data.contents != null) {
                            getV().getStorageListDataSuccess(data.contents,data.page,refresh);
                        }
                    }

                    @Override
                    public void onError(Response<BaseResponse<StorageService.WareHouseListBean>> response) {
                        super.onError(response);
                        ToastUtils.showShort(R.string.inventory_operate_fail);
                        getV().getStorageListDataError();
                    }
                });
    }
}
