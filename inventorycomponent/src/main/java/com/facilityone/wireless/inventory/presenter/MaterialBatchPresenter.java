package com.facilityone.wireless.inventory.presenter;

import com.blankj.utilcode.util.ToastUtils;
import com.facilityone.wireless.a.arch.base.FMJsonCallback;
import com.facilityone.wireless.a.arch.ec.module.Page;
import com.facilityone.wireless.basiclib.app.FM;
import com.facilityone.wireless.inventory.R;
import com.facilityone.wireless.inventory.fragment.MaterialBatchFragment;
import com.facilityone.wireless.inventory.model.BatchService;
import com.facilityone.wireless.inventory.model.InventoryConstant;
import com.facilityone.wireless.inventory.model.InventoryUrl;
import com.facilityone.wireless.inventory.model.MaterialService;
import com.facilityone.wireless.inventory.model.StorageService;
import com.fm.tool.network.model.BaseResponse;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.List;

import me.yokeyword.fragmentation.ISupportFragment;

/**
 * Created by peter.peng on 2018/11/28.
 */

public class MaterialBatchPresenter extends InventoryCommonPresenter<MaterialBatchFragment> {

    /**
     * 入库成功后回调
     */
    @Override
    public void InventoryInSuccess() {
        super.InventoryInSuccess();
        getV().setFragmentResult(ISupportFragment.RESULT_OK,null);
        getV().pop();
    }

    /**
     * 物资出库成功后回调
     */
    @Override
    public void InventoryMaterialOutSuccess() {
        super.InventoryMaterialOutSuccess();
        getV().setFragmentResult(ISupportFragment.RESULT_OK,null);
        getV().pop();
    }

    /**
     * 物资盘点成功后回调
     */
    @Override
    public void inventoryMaterialCheckSuccess() {
        super.inventoryMaterialCheckSuccess();
        getV().setFragmentResult(ISupportFragment.RESULT_OK,null);
        getV().pop();
    }

    /**
     * 联网获取物资详情成功后回调
     * @param data
     */
    @Override
    public void getMaterialInfoSuccess(MaterialService.MaterialInfo data) {
        super.getMaterialInfoSuccess(data);
        Long warehouseId = null;
        if (data != null) {
            getV().getMaterialInfoSuccess(data);
            warehouseId = data.warehouseId;
        }
        //获取所有的仓库数据，判断是否有权限
        Page page = new Page();
        page.reset();
        List<StorageService.Storage> storageList = getV().getStorageList();
        storageList.clear();
        getAllStorageData(page, warehouseId);
    }

    /**
     * 联网获取物资详情失败后回调
     * @param response
     */
    @Override
    public void getMaterialInfoError(Response<BaseResponse<MaterialService.MaterialInfo>> response) {
        super.getMaterialInfoError(response);
        getV().getMaterialInfoError();
    }


    /**
     * 获取批次列表数据
     *
     * @param fromType
     * @param inventoryId
     * @param page
     */
    public void getBatchData(final int fromType, final long inventoryId, final Page page) {
        getV().showLoading();
        int type = -1;
        if (fromType == InventoryConstant.INVENTORY_BATCH_CHECK || fromType == InventoryConstant.INVENTORY_INFO_BATCH_CHECK) {//盘点
            type = InventoryConstant.BATCH_TYPE_CHECK;
        } else {
            type = InventoryConstant.BATCH_TYPE_VALID;
        }

        BatchService.BatchListRequest request = new BatchService.BatchListRequest();
        request.inventoryId = inventoryId;
        request.type = type;
        request.page = page;

        OkGo.<BaseResponse<BatchService.BatchListBean>>post(FM.getApiHost() + InventoryUrl.BATCH_LIST_URL)
                .isSpliceUrl(true)
                .tag(getV())
                .upJson(toJson(request))
                .execute(new FMJsonCallback<BaseResponse<BatchService.BatchListBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponse<BatchService.BatchListBean>> response) {
                        getV().dismissLoading();
                        BatchService.BatchListBean data = response.body().data;
                        List<BatchService.Batch> batchList = getV().getBatchList();
                        if (data != null && data.contents != null && data.contents.size() > 0) {
                            batchList.addAll(data.contents);
                        }

                        if (data != null && data.page != null && data.page.haveNext()) {
                            getBatchData(fromType, inventoryId, page.nextPage());
                        } else {
                            getV().refreshBatchData();
                        }
                    }

                    @Override
                    public void onError(Response<BaseResponse<BatchService.BatchListBean>> response) {
                        super.onError(response);
                        getV().dismissLoading();
                    }
                });
    }

    /**
     * 联网获取所有的仓库数据
     */
    private void getAllStorageData(Page page, final Long warehouseId) {
        getV().showLoading();
        StorageService.StorageListRequest request = new StorageService.StorageListRequest();
        request.page = page;
        request.employeeId=FM.getEmId();

        OkGo.<BaseResponse<StorageService.StorageListBean>>post(FM.getApiHost() + InventoryUrl.STORAGE_LIST_URL)
                .isSpliceUrl(true)
                .tag(getV())
                .upJson(toJson(request))
                .execute(new FMJsonCallback<BaseResponse<StorageService.StorageListBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponse<StorageService.StorageListBean>> response) {
                        getV().dismissLoading();
                        StorageService.StorageListBean data = response.body().data;
                        List<StorageService.Storage> storageList = getV().getStorageList();
                        if (data != null && data.contents != null && data.contents.size() > 0) {
                            storageList.addAll(data.contents);
                        }

                        if (data != null && data.page != null && data.page.haveNext()) {
                            getAllStorageData(data.page.nextPage(), warehouseId);
                            return;
                        }

                        boolean canOpt = false;//是否有权限操作
                        if (storageList != null && storageList.size() > 0) {
                            for (StorageService.Storage storage : storageList) {
                                if (warehouseId.equals(storage.warehouseId)) {
                                    canOpt = true;
                                    break;
                                }
                            }
                        }

                        if (!canOpt) {
                            ToastUtils.showShort(R.string.inventory_invalid_token_limited);
                            getV().pop();
                        }
                    }

                    @Override
                    public void onError(Response<BaseResponse<StorageService.StorageListBean>> response) {
                        super.onError(response);
                        getV().dismissLoading();
                    }
                });
    }
}
