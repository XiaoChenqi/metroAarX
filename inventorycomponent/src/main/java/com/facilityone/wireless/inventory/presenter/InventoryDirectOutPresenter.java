package com.facilityone.wireless.inventory.presenter;

import com.facilityone.wireless.inventory.fragment.InventoryDirectOutFragment;
import com.facilityone.wireless.inventory.fragment.InventoryOutFragment;
import com.facilityone.wireless.inventory.fragment.MaterialBatchFragment;
import com.facilityone.wireless.inventory.model.InventoryConstant;
import com.facilityone.wireless.inventory.model.MaterialService;
import com.facilityone.wireless.inventory.model.StorageService;

import java.util.List;

/**
 * Created by peter.peng on 2018/12/3.
 */

public class InventoryDirectOutPresenter extends InventoryCommonPresenter<InventoryDirectOutFragment> {

    /**
     * 根据二维码获取物资详情数据成功后回调
     *
     * @param data
     */
    public void getMaterialInfoByQrCodeSuccess(MaterialService.MaterialInfo data) {
        if (getV().checkMaterial(data)) {
            return;
        }

        List<MaterialService.MaterialInfo> materialList = getV().getMaterialList();
        materialList.add(data);
        int selectPosition = materialList.size() > 0 ? materialList.size() - 1 : 0;
        getV().setSelectPosition(selectPosition);
        InventoryOutFragment patentFragment = (InventoryOutFragment) getV().getParentFragment();
        patentFragment.startForResult(MaterialBatchFragment.getInstance(data, InventoryConstant.INVENTORY_BATCH_DIRECT_OUT), getV().INVENTORY_DIRECT_OUT_QRCODE_REQUEST_CODE);
    }

    /**
     * 获取仓库管理员成功后回调
     * @param administratorList
     */
    @Override
    public void getStorageAdministratorSuccess(List<StorageService.Administrator> administratorList) {
        super.getStorageAdministratorSuccess(administratorList);
        getV().showAdministrator(administratorList);
    }

    /**
     * 物资出库成功后回调
     */
    @Override
    public void InventoryMaterialOutSuccess() {
        super.InventoryMaterialOutSuccess();
        InventoryOutFragment parentFragment = (InventoryOutFragment) getV().getParentFragment();
        parentFragment.pop();
    }
}
