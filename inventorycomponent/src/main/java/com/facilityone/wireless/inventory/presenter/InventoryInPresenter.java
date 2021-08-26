package com.facilityone.wireless.inventory.presenter;

import com.facilityone.wireless.inventory.fragment.InventoryInFragment;
import com.facilityone.wireless.inventory.fragment.MaterialBatchFragment;
import com.facilityone.wireless.inventory.model.InventoryConstant;
import com.facilityone.wireless.inventory.model.MaterialService;

import java.util.List;

/**
 * Created by peter.peng on 2018/11/27.
 */

public class InventoryInPresenter extends InventoryCommonPresenter<InventoryInFragment> {

    /**
     * 入库成功后回调
     */
    @Override
    public void InventoryInSuccess() {
        super.InventoryInSuccess();
        getV().pop();
    }




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
        getV().startForResult(MaterialBatchFragment.getInstance(data, InventoryConstant.INVENTORY_BATCH_IN), getV().INVENTORY_IN_QRCODE_REQUEST_CODE);
    }
}
