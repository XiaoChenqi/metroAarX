package com.facilityone.wireless.inventory.model;

import android.content.Context;

import com.facilityone.wireless.inventory.R;

import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Created by peter.peng on 2018/12/4.
 * 库存帮助类
 */

public class InventoryHelper {

    /**
     * 获取库存状态
     * @param context
     * @return
     */
    public static Map<Integer, String> getInventoryStatusMap(Context context) {
        Map<Integer, String> v = new LinkedHashMap<Integer, String>();
        String[] workerStat = context.getResources().getStringArray(R.array.inventory_status);
        v.put(InventoryConstant.RESERVE_STATUS_VERIFY_WAIT, workerStat[0]);
        v.put(InventoryConstant.RESERVE_STATUS_VERIFY_PASS, workerStat[1]);
        v.put(InventoryConstant.RESERVE_STATUS_VERIFY_BACK, workerStat[2]);
        v.put(InventoryConstant.RESERVE_STATUS_DELIVERIED, workerStat[3]);
        v.put(InventoryConstant.RESERVE_STATUS_CANCEL, workerStat[4]);
        v.put(InventoryConstant.RESERVE_STATUS_CANCEL_BOOK, workerStat[5]);
        return v;
    }

    /**
     * 获取预订单详情预订单操作记录步骤
     * @param context
     * @return
     */
    public static Map<Integer,String> getRecordHistoryStepMap(Context context){
        Map<Integer, String> v = new LinkedHashMap<Integer, String>();
        String[] stepArray = context.getResources().getStringArray(R.array.record_history_step);
        v.put(InventoryConstant.RECORD_STEP_RESERVE, stepArray[0]);
        v.put(InventoryConstant.RECORD_STEP_REJECT, stepArray[1]);
        v.put(InventoryConstant.RECORD_STEP_PASS, stepArray[2]);
        v.put(InventoryConstant.RECORD_STEP_OUT, stepArray[3]);
        v.put(InventoryConstant.RECORD_STEP_CANCEL_OUT,  stepArray[4]);
        v.put(InventoryConstant.RECORD_STEP_CANCEL_RESERVE, stepArray[5]);
        return v;
    }
}
