package com.facilityone.wireless.inventory.presenter;

import com.facilityone.wireless.a.arch.ec.module.FunctionService;
import com.facilityone.wireless.componentservice.common.permissions.PermissionsManager;
import com.facilityone.wireless.inventory.fragment.InventoryFragment;
import com.facilityone.wireless.inventory.model.InventoryConstant;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by peter.peng on 2018/11/23.
 */

public class InventoryPresenter extends InventoryCommonPresenter<InventoryFragment> {

    @Override
    public void getUndoNumberSuccess(JSONObject data) {
        List<FunctionService.FunctionBean> functionBeanList = getV().getFunctionBeanList();
        for (int i = 0; i < functionBeanList.size(); i++) {
            try {
                FunctionService.FunctionBean functionBean = functionBeanList.get(i);
                switch (functionBean.index) {
                    case InventoryConstant.INVENTORY_OUT :
                        functionBean.undoNum = data.getInt(PermissionsManager.TOBEOUTINVENTORYNUMBER);
                        break;
                    case InventoryConstant.INVENTORY_APPROVAL:
                        functionBean.undoNum = data.getInt(PermissionsManager.UNAPPROVALINVENTORYNUMBER);
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        getV().updateFunction(functionBeanList);
    }
}
