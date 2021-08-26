package com.facilityone.wireless.inventory.presenter;

import com.facilityone.wireless.inventory.fragment.InventoryDirectOutFragment;
import com.facilityone.wireless.inventory.fragment.InventoryOutFragment;
import com.facilityone.wireless.inventory.model.MaterialService;

/**
 * Created by peter.peng on 2018/12/3.
 */

public class InventoryOutPresenter extends InventoryCommonPresenter<InventoryOutFragment> {

    /**
     * 根据二维码获取物资详情数据成功后回调
     *
     * @param data
     */
    @Override
    public void getMaterialInfoByQRCodeSuccess(MaterialService.MaterialInfo data) {
        super.getMaterialInfoByQRCodeSuccess(data);
        InventoryDirectOutFragment directOutFragment = getV().getDirectOutFragment();
        if(directOutFragment != null) {
            directOutFragment.getPresenter().getMaterialInfoByQrCodeSuccess(data);
        }
    }
}
