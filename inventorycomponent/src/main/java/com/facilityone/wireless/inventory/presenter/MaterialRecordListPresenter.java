package com.facilityone.wireless.inventory.presenter;

import android.text.TextUtils;

import com.facilityone.wireless.a.arch.base.FMJsonCallback;
import com.facilityone.wireless.a.arch.ec.module.Page;
import com.facilityone.wireless.basiclib.app.FM;
import com.facilityone.wireless.inventory.fragment.MaterialRecordListFragment;
import com.facilityone.wireless.inventory.model.InventoryUrl;
import com.facilityone.wireless.inventory.model.MaterialService;
import com.fm.tool.network.model.BaseResponse;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

/**
 * Created by peter.peng on 2018/12/12.
 */

public class MaterialRecordListPresenter extends InventoryCommonPresenter<MaterialRecordListFragment> {

    /**
     * 根据id联网获取物资记录列表数据
     * @param inventoryId
     * @param page
     * @param refresh
     */
    public void getMaterialRecordListData(long inventoryId, Page page, final boolean refresh) {
        MaterialService.MaterialRecordListRequest request = new MaterialService.MaterialRecordListRequest();
        request.inventoryId = inventoryId;
        request.page = page;

        OkGo.<BaseResponse<MaterialService.MaterialRecordListBean>>post(FM.getApiHost() + InventoryUrl.MATERIAL_RECORD_BY_ID_URL)
                .isSpliceUrl(true)
                .tag(getV())
                .upJson(toJson(request))
                .execute(new FMJsonCallback<BaseResponse<MaterialService.MaterialRecordListBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponse<MaterialService.MaterialRecordListBean>> response) {
                        MaterialService.MaterialRecordListBean data = response.body().data;
                        if(data != null && data.contents != null) {
                            for (int i = 0;i < data.contents.size(); i++) {
                                MaterialService.MaterialRecord materialRecord = data.contents.get(i);
                                if(!TextUtils.isEmpty(materialRecord.price) && materialRecord.price.contains(".") && materialRecord.price.length() - materialRecord.price.indexOf(".") - 1 > 2) {
                                    materialRecord.price = materialRecord.price.substring(0,materialRecord.price.indexOf(".") + 3);
                                }
                            }
                            getV().getMaterialRecordListDataSuccess(data.contents,data.page,refresh);
                        }else {
                            getV().getMaterialRecordListDataError();
                        }
                    }

                    @Override
                    public void onError(Response<BaseResponse<MaterialService.MaterialRecordListBean>> response) {
                        super.onError(response);
                        getV().getMaterialRecordListDataError();
                    }
                });

    }
}
