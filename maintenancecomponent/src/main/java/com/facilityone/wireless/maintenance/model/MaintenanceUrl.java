package com.facilityone.wireless.maintenance.model;

/**
 * Created by peter.peng on 2018/11/16.
 */

public interface MaintenanceUrl {

    //获取维护日历url
    String MAINTENANCE_CALENDAR_URL = "/m/v1/preventive/todos/query";
    //获取计划性维护详情
    String MAINTENANCE_INFO_URL = "/m/v2/preventive/info";

     /**
      * @Auther: karelie
      * @Date: 2021/8/19
      * @Infor: 四运
      */
    //待处理维护工单
    String MAINTENANCE_LIST_UNDO_URL = "/m/v1/workorder/ppm/undo";
    //待派工维护工单
    String MAINTENANCE_LIST_DISPATCH_URL = "/m/v1/workorder/ppm/undispatch";
    //待审核维护工单
    String MAINTENANCE_LIST_APPROVAL_URL = "/m/v1/workorder/ppm/wos/unapproval";
    //异常维护工单
    String MAINTENANCE_LIST_ABNOMAL_URL = "/m/v1/workorder/ppm/exception";
    //待存档维护工单
    String MAINTENANCE_LIST_TO_CLOSED_URL = "/m/v1/workorder/ppm/to-be-closed";
    //维护工单查询
    String MAINTENANCE_LIST_TO_QUERY_URL = "/m/v1/workorder/ppm/hquery";
    //待处理工单
    String MAINTENANCE_UNDO_URL = "/m/v1/workorder/ppm/undo";
    //批量接单
    String MAINTENANCE_ORDER_RECEIVE = "/m/v1/workorder/operate/receive/batch";

}
