package com.facilityone.wireless.inventory.presenter;

import android.content.Intent;
import android.text.TextUtils;

import com.blankj.utilcode.util.ToastUtils;
import com.facilityone.wireless.a.arch.base.FMJsonCallback;
import com.facilityone.wireless.a.arch.ec.commonpresenter.CommonBasePresenter;
import com.facilityone.wireless.a.arch.ec.module.Page;
import com.facilityone.wireless.a.arch.ec.ui.FzScanActivity;
import com.facilityone.wireless.a.arch.mvp.BaseFragment;
import com.facilityone.wireless.basiclib.app.FM;
import com.facilityone.wireless.inventory.R;
import com.facilityone.wireless.inventory.model.InventoryUrl;
import com.facilityone.wireless.inventory.model.MaterialService;
import com.facilityone.wireless.inventory.model.ReserveService;
import com.facilityone.wireless.inventory.model.StorageService;
import com.fm.tool.network.model.BaseResponse;
import com.fm.tool.scan.ScanActivity;
import com.huawei.hms.ml.scan.HmsScan;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.zdf.activitylauncher.ActivityLauncher;

import java.util.List;

import static com.blankj.utilcode.util.ActivityUtils.startActivity;

/**
 * Created by peter.peng on 2018/12/6.
 */

public class InventoryCommonPresenter<V extends BaseFragment> extends CommonBasePresenter<V> {


    /**
     * 扫描物资二维码
     */
    public void scanMaterialQRCode(final long warehouseId, final String selectStorage) {
        Intent intent = new Intent(getV().getContext(), FzScanActivity.class);
        ActivityLauncher.init(getV().getActivity())
                .startActivityForResult(intent, new ActivityLauncher.Callback() {
                    @Override
                    public void onActivityResult(int resultCode, Intent data) {
                        if (data != null){
                            HmsScan result=data.getParcelableExtra("scanResult");
                            if (result!=null){
                                if (result.originalValue != null){
                                    MaterialService.InventoryQRCodeBean inventoryQRCodeBean = getInventoryQRCodeBean(result.originalValue);
                                    try {
                                        long tempWarehouseId = Long.parseLong(inventoryQRCodeBean.wareHouseId);
                                        if (warehouseId != -1 && warehouseId != tempWarehouseId) {
                                            if (!TextUtils.isEmpty(selectStorage)) {
                                                ToastUtils.showShort(String.format(getV().getString(R.string.inventory_material_no_exist_inventory_tip), selectStorage));
                                            } else {
                                                ToastUtils.showShort(String.format(getV().getString(R.string.inventory_material_no_exist_inventory_tip), ""));
                                            }

                                            return;
                                        }

                                        if (TextUtils.isEmpty(inventoryQRCodeBean.code) || tempWarehouseId == -1) {
                                            ToastUtils.showShort(R.string.inventory_qr_code_error);
                                            return;
                                        }
                                        //根据二维码获取物资详情
                                        getMaterialInfoByQRCode(tempWarehouseId, inventoryQRCodeBean.code);
                                    } catch (NumberFormatException e) {
                                        e.printStackTrace();
                                    }
                                }else {
                                    ToastUtils.showShort(R.string.inventory_qr_code_error);
                                }
                            }
                        }

                    }
                });
    }

    /**
     * 获取物资二维码
     *
     * @param QRCode
     * @return
     */
    public MaterialService.InventoryQRCodeBean getInventoryQRCodeBean(String QRCode) {
        if (TextUtils.isEmpty(QRCode)) {
            return null;
        }

        String[] strArr = QRCode.split("\\|");
        if (strArr.length != 5) {
            return null;
        }

        MaterialService.InventoryQRCodeBean inventoryQRCodeBean = new MaterialService.InventoryQRCodeBean();
        inventoryQRCodeBean.function = strArr[0];
        inventoryQRCodeBean.subfunction = strArr[1];
        inventoryQRCodeBean.wareHouseId = strArr[2];
        inventoryQRCodeBean.code = strArr[3];
        inventoryQRCodeBean.companyName = strArr[4];

        return inventoryQRCodeBean;
    }

    /**
     * 根据二维码获取物资详情
     *
     * @param warehouseId
     * @param code
     */
    public void getMaterialInfoByQRCode(long warehouseId, String code) {
        getV().showLoading();
        String request = "{\"code\":\"" + code + "\",\"warehouseId\":" + warehouseId + "}";
        OkGo.<BaseResponse<MaterialService.MaterialInfo>>post(FM.getApiHost() + InventoryUrl.MATERIAL_INFO_QRCODE_URL)
                .isSpliceUrl(true)
                .tag(getV())
                .upJson(request)
                .execute(new FMJsonCallback<BaseResponse<MaterialService.MaterialInfo>>() {
                    @Override
                    public void onSuccess(Response<BaseResponse<MaterialService.MaterialInfo>> response) {
                        getV().dismissLoading();
                        MaterialService.MaterialInfo data = response.body().data;
                        if (data == null || data.inventoryId == null) {
                            ToastUtils.showShort(R.string.inventory_qr_code_error);
                            getV().pop();
                            return;
                        }

                        getMaterialInfoByQRCodeSuccess(data);

                    }

                    @Override
                    public void onError(Response<BaseResponse<MaterialService.MaterialInfo>> response) {
                        super.onError(response);
                        getV().dismissLoading();
                        ToastUtils.showShort(R.string.inventory_qr_code_error);
                        getV().pop();
                    }
                });
    }

    public void getMaterialInfoByQRCodeSuccess(MaterialService.MaterialInfo data) {

    }

    /**
     * 物资出库、移库
     */
    public void InventoryMaterialOut(MaterialService.MaterialOutRequest request) {
        getV().showLoading();
        OkGo.<BaseResponse<Object>>post(FM.getApiHost() + InventoryUrl.INVENTORY_OUT_URL)
                .isSpliceUrl(true)
                .tag(getV())
                .upJson(toJson(request))
                .execute(new FMJsonCallback<BaseResponse<Object>>() {
                    @Override
                    public void onSuccess(Response<BaseResponse<Object>> response) {
                        getV().dismissLoading();
                        ToastUtils.showShort(R.string.inventory_operate_success);
                        InventoryMaterialOutSuccess();
                    }

                    @Override
                    public void onError(Response<BaseResponse<Object>> response) {
                        ToastUtils.showShort(R.string.inventory_operate_fail);
                        super.onError(response);
                        getV().dismissLoading();
                    }
                });
    }

    public void InventoryMaterialOutSuccess() {

    }

    /**
     * 联网获取指定仓库id的仓库管理员
     *
     * @param page
     * @param warehouseId
     */
    public void getStorageAdministrator(Page page, final long warehouseId, final List<StorageService.Administrator> administratorList) {
        getV().showLoading();
        StorageService.StorageListRequest request = new StorageService.StorageListRequest();
        request.page = page;
        OkGo.<BaseResponse<StorageService.StorageListBean>>post(FM.getApiHost() + InventoryUrl.STORAGE_LIST_URL)
                .isSpliceUrl(true)
                .tag(getV())
                .upJson(toJson(request))
                .execute(new FMJsonCallback<BaseResponse<StorageService.StorageListBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponse<StorageService.StorageListBean>> response) {
                        getV().dismissLoading();
                        boolean isFind = false;
                        StorageService.StorageListBean data = response.body().data;
                        if (data != null && data.contents != null && data.contents.size() > 0) {
                            for (int i = 0; i < data.contents.size(); i++) {
                                StorageService.Storage storage = data.contents.get(i);
                                if (storage.warehouseId.equals(warehouseId)) {
                                    if (storage.administrator != null) {
                                        administratorList.addAll(storage.administrator);
                                    }
                                    isFind = true;
                                    break;
                                }
                            }
                        }

                        if (data != null && data.page != null && data.page.haveNext() && !isFind) {
                            getStorageAdministrator(data.page.nextPage(), warehouseId, administratorList);
                        } else if (administratorList != null && administratorList.size() > 0) {
                            getStorageAdministratorSuccess(administratorList);
                        }
                    }

                    @Override
                    public void onError(Response<BaseResponse<StorageService.StorageListBean>> response) {
                        super.onError(response);
                        getV().dismissLoading();
                    }
                });

    }

    public void getStorageAdministratorSuccess(List<StorageService.Administrator> administratorList) {

    }

    /**
     * 获取预订记录数据列表
     *
     * @param page
     * @param queryType 查询类型
     */
    public void getReserveRecordList(Page page, int queryType, final boolean refresh) {
        ReserveService.ReserveRecordRequest request = new ReserveService.ReserveRecordRequest();
        request.page = page;
        request.queryType = queryType;
        request.userId = FM.getEmId();

        OkGo.<BaseResponse<ReserveService.ReserveRecordListBean>>post(FM.getApiHost() + InventoryUrl.RESERVE_RECORD_LIST_URL)
                .isSpliceUrl(true)
                .tag(getV())
                .upJson(toJson(request))
                .execute(new FMJsonCallback<BaseResponse<ReserveService.ReserveRecordListBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponse<ReserveService.ReserveRecordListBean>> response) {
                        ReserveService.ReserveRecordListBean data = response.body().data;
                        getReserveRecordListSuccess(data, refresh);
                    }

                    @Override
                    public void onError(Response<BaseResponse<ReserveService.ReserveRecordListBean>> response) {
                        super.onError(response);
                        ToastUtils.showShort(R.string.inventory_operate_fail);
                        getReserveRecordListError(response);
                    }
                });
    }

    public void getReserveRecordListError(Response<BaseResponse<ReserveService.ReserveRecordListBean>> response) {
    }

    public void getReserveRecordListSuccess(ReserveService.ReserveRecordListBean data, boolean refresh) {
    }


    /**
     * 联网获取物资信息
     *
     * @param inventoryId
     */
    public void getMaterialInfoById(long inventoryId) {
        getV().showLoading();
        String request = "{\"inventoryId\":" + inventoryId + "}";
        OkGo.<BaseResponse<MaterialService.MaterialInfo>>post(FM.getApiHost() + InventoryUrl.MATERIAL_INFO_ID_URL)
                .isSpliceUrl(true)
                .tag(getV())
                .upJson(request)
                .execute(new FMJsonCallback<BaseResponse<MaterialService.MaterialInfo>>() {
                    @Override
                    public void onSuccess(Response<BaseResponse<MaterialService.MaterialInfo>> response) {
                        getV().dismissLoading();
                        MaterialService.MaterialInfo data = response.body().data;
                        getMaterialInfoSuccess(data);
                    }

                    @Override
                    public void onError(Response<BaseResponse<MaterialService.MaterialInfo>> response) {
                        super.onError(response);
                        getV().dismissLoading();
                        ToastUtils.showShort(R.string.inventory_operate_fail);
                        getMaterialInfoError(response);
                    }
                });
    }

    public void getMaterialInfoError(Response<BaseResponse<MaterialService.MaterialInfo>> response) {

    }

    public void getMaterialInfoSuccess(MaterialService.MaterialInfo data) {

    }


    /**
     * 联网入库，保存物资信息
     *
     * @param warehouseId
     * @param desc
     * @param inventoryList
     */
    public void InventoryIn(Long warehouseId, String desc, List<MaterialService.Inventory> inventoryList) {
        getV().showLoading();
        MaterialService.InventoryInRequest request = new MaterialService.InventoryInRequest();
        request.warehouseId = warehouseId;
        request.remarks = desc;
        request.inventory = inventoryList;

        OkGo.<BaseResponse<Object>>post(FM.getApiHost() + InventoryUrl.INVENTORY_IN_URL)
                .isSpliceUrl(true)
                .tag(getV())
                .upJson(toJson(request))
                .execute(new FMJsonCallback<BaseResponse<Object>>() {
                    @Override
                    public void onSuccess(Response<BaseResponse<Object>> response) {
                        getV().dismissLoading();
                        ToastUtils.showShort(R.string.inventory_operate_success);
                        InventoryInSuccess();
                    }

                    @Override
                    public void onError(Response<BaseResponse<Object>> response) {
                        super.onError(response);
                        getV().dismissLoading();
                        ToastUtils.showShort(R.string.inventory_operate_fail);
                    }
                });

    }

    public void InventoryInSuccess() {

    }

    /**
     * 联网进行物资盘点
     * @param request
     */
    public void inventoryMaterialCheck(MaterialService.MaterialCheckRequest request) {
        getV().showLoading();
        OkGo.<BaseResponse<Object>>post(FM.getApiHost() + InventoryUrl.INVENTORY_CHECK_URL)
                .isSpliceUrl(true)
                .tag(getV())
                .upJson(toJson(request))
                .execute(new FMJsonCallback<BaseResponse<Object>>() {
                    @Override
                    public void onSuccess(Response<BaseResponse<Object>> response) {
                        getV().dismissLoading();
                        ToastUtils.showShort(R.string.inventory_operate_success);
                        inventoryMaterialCheckSuccess();
                    }

                    @Override
                    public void onError(Response<BaseResponse<Object>> response) {
                        super.onError(response);
                        getV().dismissLoading();
                        ToastUtils.showShort(R.string.inventory_operate_fail);
                    }
                });
    }

    public void inventoryMaterialCheckSuccess() {

    }


    public MaterialService.MaterialInfo Material2MaterInfo(MaterialService.Material material) {
        MaterialService.MaterialInfo materialInfo = new MaterialService.MaterialInfo();
        materialInfo.inventoryId = material.inventoryId;
        materialInfo.code = material.materialCode;
        materialInfo.name = material.materialName;
        materialInfo.brand = material.materialBrand;
        materialInfo.shelves = material.materialShelf;
        materialInfo.model = material.materialModel;
        materialInfo.unit = material.materialUnit;
        materialInfo.totalNumber = material.totalNumber;
        materialInfo.minNumber = material.minNumber;
        materialInfo.realNumber = material.realNumber;
        materialInfo.price = material.cost;
        materialInfo.pictures = material.pictures;
        return materialInfo;
    }
}
