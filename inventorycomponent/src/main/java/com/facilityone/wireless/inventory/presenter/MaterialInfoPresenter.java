package com.facilityone.wireless.inventory.presenter;

import android.text.TextUtils;
import android.view.View;

import com.facilityone.wireless.a.arch.base.FMJsonCallback;
import com.facilityone.wireless.a.arch.ec.module.Page;
import com.facilityone.wireless.a.arch.widget.BottomTextListSheetBuilder;
import com.facilityone.wireless.basiclib.app.FM;
import com.facilityone.wireless.inventory.R;
import com.facilityone.wireless.inventory.fragment.MaterialBatchFragment;
import com.facilityone.wireless.inventory.fragment.MaterialInfoFragment;
import com.facilityone.wireless.inventory.model.InventoryConstant;
import com.facilityone.wireless.inventory.model.InventoryUrl;
import com.facilityone.wireless.inventory.model.MaterialService;
import com.facilityone.wireless.inventory.model.StorageService;
import com.fm.tool.network.model.BaseResponse;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by peter.peng on 2018/11/28.
 */

public class MaterialInfoPresenter extends InventoryCommonPresenter<MaterialInfoFragment> {

    @Override
    public void getMaterialInfoByQRCodeSuccess(MaterialService.MaterialInfo data) {
        super.getMaterialInfoByQRCodeSuccess(data);
        Long warehouseId = null;
        if (data != null) {
            getV().getMaterialInfoSuccess(data);
            warehouseId = data.warehouseId == null ? -1 : data.warehouseId;
        }
        //获取所有的仓库数据，判断是否有权限
        Page page = new Page();
        page.reset();
        List<StorageService.Storage> storageList = getV().getStorageList();
        storageList.clear();
        getAllStorageData(page, warehouseId);
    }

    /**
     * 联网获取物资详情成功后回调
     *
     * @param data
     */
    @Override
    public void getMaterialInfoSuccess(MaterialService.MaterialInfo data) {
        super.getMaterialInfoSuccess(data);
        Long warehouseId = null;
        if (data != null) {
            getV().getMaterialInfoSuccess(data);
            warehouseId = data.warehouseId == null ? -1 : data.warehouseId;
        }
        //获取所有的仓库数据，判断是否有权限
//        Page page = new Page();
//        page.reset();
//        List<StorageService.Storage> storageList = getV().getStorageList();
//        storageList.clear();
//        getAllStorageData(page, warehouseId);
    }

    /**
     * 联网获取物资详情失败后回调
     *
     * @param response
     */
    @Override
    public void getMaterialInfoError(Response<BaseResponse<MaterialService.MaterialInfo>> response) {
        super.getMaterialInfoError(response);
        getV().getMaterialInfoError();
    }


    /**
     * 联网获取所有的仓库数据
     */
    private void getAllStorageData(Page page, final Long warehouseId) {
        StorageService.StorageListRequest request = new StorageService.StorageListRequest();
        request.page = page;
        request.employeeId = FM.getEmId();
        OkGo.<BaseResponse<StorageService.StorageListBean>>post(FM.getApiHost() + InventoryUrl.STORAGE_LIST_URL)
                .isSpliceUrl(true)
                .tag(getV())
                .upJson(toJson(request))
                .execute(new FMJsonCallback<BaseResponse<StorageService.StorageListBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponse<StorageService.StorageListBean>> response) {
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
                                    getV().setStorage(storage);
                                    break;
                                }
                            }
                        }

                        if (!canOpt) {
//                            ToastUtils.showShort("你没有相应的权限，请确认");
//                            getV().pop();
                            getV().setMoreMenuVisible(false);
                        }
                    }

                    @Override
                    public void onError(Response<BaseResponse<StorageService.StorageListBean>> response) {
                        super.onError(response);
                        getV().dismissLoading();
                    }
                });
    }

    /**
     * 根据id联网获取物资记录列表数据
     *
     * @param request
     * @param byId    是否通过id获取数据， ture：是 false：不是，通过二维码编码
     */
    public void getMaterialRecordListData(MaterialService.MaterialRecordListRequest request, boolean byId) {
        String url = "";
        if (byId) {
            url = FM.getApiHost() + InventoryUrl.MATERIAL_RECORD_BY_ID_URL;
        } else {
            url = FM.getApiHost() + InventoryUrl.MATERIAL_RECORD_BY_CODE_URL;
        }

        OkGo.<BaseResponse<MaterialService.MaterialRecordListBean>>post(url)
                .isSpliceUrl(true)
                .tag(getV())
                .upJson(toJson(request))
                .execute(new FMJsonCallback<BaseResponse<MaterialService.MaterialRecordListBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponse<MaterialService.MaterialRecordListBean>> response) {
                        getV().dismissLoading();
                        MaterialService.MaterialRecordListBean data = response.body().data;
                        if (data != null && data.contents != null) {
                            for (int i = 0;i < data.contents.size(); i++) {
                                MaterialService.MaterialRecord materialRecord = data.contents.get(i);
                                if(!TextUtils.isEmpty(materialRecord.price) && materialRecord.price.contains(".") && materialRecord.price.length() - materialRecord.price.indexOf(".") - 1 > 2) {
                                    materialRecord.price = materialRecord.price.substring(0,materialRecord.price.indexOf(".") + 3);
                                }
                            }
                            getV().getMaterialRecordListDataSuccess(data.contents);
                        } else {
                            getV().getMaterialRecordListDataError();
                        }
                    }

                    @Override
                    public void onError(Response<BaseResponse<MaterialService.MaterialRecordListBean>> response) {
                        super.onError(response);
                        getV().dismissLoading();
                        getV().getMaterialRecordListDataError();
                    }
                });

    }

    public void onMoreMenuClick(final MaterialService.MaterialInfo materialInfo) {
        BottomTextListSheetBuilder builder = new BottomTextListSheetBuilder(getV().getContext());
        List<String> menu = new ArrayList<>();
        menu.add(getV().getString(R.string.inventory_in_title));
        menu.add(getV().getString(R.string.inventory_out_title));
        menu.add(getV().getString(R.string.inventory_move_title));
        menu.add(getV().getString(R.string.inventory_check_title));
        menu.add(getV().getString(R.string.inventory_cancel));
        builder.addArrayItem(menu);
        builder.setOnSheetItemClickListener(new BottomTextListSheetBuilder.OnSheetItemClickListener() {
            @Override
            public void onClick(QMUIBottomSheet dialog, View itemView, int position, String tag) {
                dialog.dismiss();
                if(tag.equals(getV().getString(R.string.inventory_in_title))) {
                    getV().startForResult(MaterialBatchFragment.getInstance(materialInfo, InventoryConstant.INVENTORY_INFO_BATCH_IN),
                            getV().INFO_INVENTORY_IN_REQUEST_CODE);
                }else if(tag.equals(getV().getString(R.string.inventory_out_title))) {
                    StorageService.Storage storage = getV().getStorage();
                    if(storage != null && storage.warehouseId.equals(materialInfo.warehouseId)) {
                        getV().startForResult(MaterialBatchFragment.getInstance(materialInfo, storage.administrator,InventoryConstant.INVENTORY_INFO_BATCH_OUT),
                                getV().INFO_INVENTORY_OUT_REQUEST_CODE);
                    }
                }else if(tag.equals(getV().getString(R.string.inventory_move_title))) {
                    StorageService.Storage storage = getV().getStorage();
                    if(storage != null && storage.warehouseId.equals(materialInfo.warehouseId)) {
                        getV().startForResult(MaterialBatchFragment.getInstance(materialInfo, storage.administrator,InventoryConstant.INVENTORY_INFO_BATCH_MOVE),
                                getV().INFO_INVENTORY_MOVE_REQUEST_CODE);
                    }
                }else if(tag.equals(getV().getString(R.string.inventory_check_title))) {
                    getV().startForResult(MaterialBatchFragment.getInstance(materialInfo, InventoryConstant.INVENTORY_INFO_BATCH_CHECK),
                            getV().INFO_INVENTORY_CHECK_REQUEST_CODE);
                }
            }
        });
        builder.build().show();
    }
}
