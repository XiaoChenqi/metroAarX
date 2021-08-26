package com.facilityone.wireless.inventory.presenter;

import com.facilityone.wireless.inventory.fragment.InventoryMoveFragment;
import com.facilityone.wireless.inventory.fragment.MaterialBatchFragment;
import com.facilityone.wireless.inventory.model.InventoryConstant;
import com.facilityone.wireless.inventory.model.MaterialService;
import com.facilityone.wireless.inventory.model.StorageService;

import java.util.List;

/**
 * Created by peter.peng on 2018/12/6.
 */

public class InventoryMovePresenter extends InventoryCommonPresenter<InventoryMoveFragment> {

    /**
     * 根据二维码获取物资详情数据成功后回调
     *
     * @param data
     */
    @Override
    public void getMaterialInfoByQRCodeSuccess(MaterialService.MaterialInfo data) {
        super.getMaterialInfoByQRCodeSuccess(data);
        if (getV().checkMaterial(data)) {
            return;
        }

        List<MaterialService.MaterialInfo> materialList = getV().getMaterialList();
        materialList.add(data);
        int selectPosition = materialList.size() > 0 ? materialList.size() - 1 : 0;
        getV().setSelectPosition(selectPosition);
        getV().startForResult(MaterialBatchFragment.getInstance(data, InventoryConstant.INVENTORY_BATCH_MOVE), getV().INVENTORY_MOVE_QRCODE_REQUEST_CODE);
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
     * 物资移库成功后回调
     */
    @Override
    public void InventoryMaterialOutSuccess() {
        super.InventoryMaterialOutSuccess();
        getV().pop();
    }
}
