package com.facilityone.wireless.componentservice.inventory;

import com.facilityone.wireless.a.arch.ec.module.IService;
import com.facilityone.wireless.a.arch.mvp.BaseFragment;

/**
 * Created by peter.peng on 2018/11/23.
 */

public interface InventoryService extends IService {
    int TYPE_FROM_WORKORDER = 3001;
    int TYPE_FROM_WORKORDER_MAINTENANCE = 3002;

    /**
     * 获取物资预定详情页面
     *
     * @param type
     * @param activityId
     * @param status
     * @return
     */
    BaseFragment getReserveRecordInfoFragment(int type, long activityId, int status,int workorderStatus);

    /**
     * 消息界面进入预定详情
     * @param activityId
     * @return
     */
    BaseFragment getReserveRecordInfoFragment(long activityId,boolean fromMessage);

    /**
     * 获取物资预定界面
     *
     * @param type
     * @param woId
     * @param woCode
     * @return
     */
    BaseFragment getInventoryReserveFragment(int type, long woId, String woCode);

    BaseFragment getInventoryReserveFragment(int type,int workOrderType, long woId, String woCode);

    /**
     * 库存查询
     *
     * @return
     */
    BaseFragment getInventoryQueryFragment();

    /**
     * 物料详情
     *
     * @param inventoryId
     * @return
     */
    BaseFragment getMaterialInfoFragment(Long inventoryId,Boolean isScan);


     /**
      * @Auther: karelie
      * @Date: 2021/8/5
      * @Infor: 扫一扫查看物资详情
      */
     BaseFragment ScanForInventoryInfor(String code, long warehouseId, boolean scan);
}
