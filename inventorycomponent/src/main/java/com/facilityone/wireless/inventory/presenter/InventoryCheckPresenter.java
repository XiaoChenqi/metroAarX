package com.facilityone.wireless.inventory.presenter;

import com.facilityone.wireless.inventory.fragment.InventoryCheckFragment;
import com.facilityone.wireless.inventory.fragment.MaterialBatchFragment;
import com.facilityone.wireless.inventory.model.InventoryConstant;
import com.facilityone.wireless.inventory.model.MaterialService;

import java.util.List;

/**
 * Created by peter.peng on 2018/12/6.
 */

public class InventoryCheckPresenter extends InventoryCommonPresenter<InventoryCheckFragment> {

    /**
     * 根据二维码获取物资详情后回调
     * @param data
     */
    @Override
    public void getMaterialInfoByQRCodeSuccess(MaterialService.MaterialInfo data) {
        super.getMaterialInfoByQRCodeSuccess(data);

        if (getV().checkMaterial(data)) {
            return;
        }

        List<MaterialService.MaterialInfo> materialList = getV().getMaterialList();
        data.number = data.totalNumber;
        materialList.add(data);
        int selectPosition = materialList.size() > 0 ? materialList.size() - 1 : 0;
        getV().setSelectPosition(selectPosition);
        getV().startForResult(MaterialBatchFragment.getInstance(data, InventoryConstant.INVENTORY_BATCH_CHECK), getV().INVENTORY_CHECK_QRCODE_REQUEST_CODE);
    }

    /**
     * 物资盘点成功后回调
     */
    @Override
    public void inventoryMaterialCheckSuccess() {
        super.inventoryMaterialCheckSuccess();
        getV().pop();
    }
}
