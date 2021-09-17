package com.facilityone.wireless.inventory.serviceimpl;

import android.os.Bundle;

import com.facilityone.wireless.a.arch.mvp.BaseFragment;
import com.facilityone.wireless.componentservice.inventory.InventoryService;
import com.facilityone.wireless.inventory.fragment.InventoryFragment;
import com.facilityone.wireless.inventory.fragment.InventoryQueryFragment;
import com.facilityone.wireless.inventory.fragment.InventoryReserveFragment;
import com.facilityone.wireless.inventory.fragment.MaterialInfoFragment;
import com.facilityone.wireless.inventory.fragment.ReserveRecordInfoFragment;
import com.facilityone.wireless.inventory.model.InventoryConstant;

/**
 * Created by peter.peng on 2018/11/23.
 */

public class InventoryServiceImpl implements InventoryService {
    @Override
    public BaseFragment getFragment(Bundle bundle) {
        return InventoryFragment.getInstance(bundle);
    }


    @Override
    public BaseFragment getReserveRecordInfoFragment(int type, long activityId, int status, int workorderStatus) {
        return ReserveRecordInfoFragment.getInstance(type, activityId, status,workorderStatus);
    }

    @Override
    public BaseFragment getReserveRecordInfoFragment(long activityId,boolean fromMessage) {
        return ReserveRecordInfoFragment.getInstance(InventoryConstant.INVENTORY_MY,activityId,fromMessage);
    }

    @Override
    public BaseFragment getInventoryReserveFragment(int type, long woId, String woCode) {
        return InventoryReserveFragment.getInstance(type, woId, woCode);
    }

    @Override
    public BaseFragment getInventoryQueryFragment() {
        return InventoryQueryFragment.getInstance();
    }

    @Override
    public BaseFragment getMaterialInfoFragment(Long inventoryId,Boolean fromMessage) {
        return MaterialInfoFragment.getInstance(inventoryId,fromMessage);
    }

    @Override
    public BaseFragment ScanForInventoryInfor(String code, long warehouseId, boolean scan) {
        return MaterialInfoFragment.getInstance(code,warehouseId,scan);
    }
}
