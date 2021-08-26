package com.facilityone.wireless.maintenance.presenter;

import com.facilityone.wireless.a.arch.ec.commonpresenter.CommonBasePresenter;
import com.facilityone.wireless.a.arch.ec.module.FunctionService;
import com.facilityone.wireless.componentservice.common.permissions.PermissionsManager;
import com.facilityone.wireless.maintenance.fragment.MaintenanceMenuFragment;
import com.facilityone.wireless.maintenance.model.MaintenanceConstant;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
  * @Auther: karelie
  * @Date: 2021/8/17
  * @Infor: 计划性维护
  */
public class MaintenanceMenuPresenter extends CommonBasePresenter<MaintenanceMenuFragment> {

    @Override
    public void getUndoNumberSuccess(JSONObject data) {
        List<FunctionService.FunctionBean> functionBeanList = getV().getFunctionBeanList();
        for (FunctionService.FunctionBean functionBean : functionBeanList) {
            try {
                switch (functionBean.index) {
                    case MaintenanceConstant.MAINTENANCE_ONE:
                        //维护日历
                        functionBean.undoNum = data.getInt(PermissionsManager.UNDOORDERNUMBER);
                        break;
                    case MaintenanceConstant.MAINTENANCE_TWO:
                        //待处理维护工单
                        functionBean.undoNum = data.getInt(PermissionsManager.UNARRANGEORDERNUMBER);
                        break;
                    case MaintenanceConstant.MAINTENANCE_THREE:
                        //待派工维护工单
                        functionBean.undoNum = data.getInt(PermissionsManager.UNAPPROVALORDERNUMBER);
                        break;
                    case MaintenanceConstant.MAINTENANCE_FOUR:
                        //待审批维护工单
                        functionBean.undoNum = data.getInt(PermissionsManager.UNARCHIVEDORDERNUMBER);
                        break;
                    case MaintenanceConstant.MAINTENANCE_FIVE:
                        //异常维护工单
                        functionBean.undoNum = data.getInt(PermissionsManager.UNARCHIVEDORDERNUMBER);
                        break;
                    case MaintenanceConstant.MAINTENANCE_SIX:
                        //带存档维护工单
                        functionBean.undoNum = data.getInt(PermissionsManager.UNARCHIVEDORDERNUMBER);
                        break;
                    case MaintenanceConstant.MAINTENANCE_SEVEN:
                        //维护工单查询
                        functionBean.undoNum = data.getInt(PermissionsManager.UNARCHIVEDORDERNUMBER);
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        getV().updateFunction(functionBeanList);
    }

}
