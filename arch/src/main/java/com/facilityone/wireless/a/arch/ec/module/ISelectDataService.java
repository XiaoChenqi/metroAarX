package com.facilityone.wireless.a.arch.ec.module;

/**
 * Author：gary
 * Email: xuhaozv@163.com
 * description:选择数据类型
 * Date: 2018/10/25 11:53 AM
 */
public interface ISelectDataService {

    String SELECT_OFFLINE_DATA_BACK = "select_offline_data_back";

    /****新派工单****/
    String SELECT_NEWORDER_POSITION = "SELECT_NEWORDER_POSITION";
    String NEWORDER_SERVICETYPE = "NEWORDER_SERVICETYPE";
    String NEWORDER_ORDERTY = "NEWORDER_ORDERTY";
    String NEWORDER_LOCATION = "NEWORDER_LOCATION";
    String NEWORDER_DEPSELECT = "NEWORDER_DEPSELECT";
    String NEWORDER_APPLICANTNAME = "NEWORDER_APPLICANTNAME";
    String NEWORDER_APPLICANTPHONE = "NEWORDER_APPLICANTPHONE";
    String NEWORDER_LOCATIONNAME = "NEWORDER_LOCATIONNAME";
    String NEWORDER_WOID = "NEWORDER_WOID";
    String NEWORDER_DESC = "NEWORDER_DESC";
    String NEW_ISBUNCHINGORDER ="new_isBunchingOrder";

    /***********************************************************/
    int DATA_TYPE_LOCATION = 0;
    int DATA_TYPE_DEP = 1;
    int DATA_TYPE_FLOW_PRIORITY = 2;
    int DATA_TYPE_PRIORITY = 3;
    int DATA_TYPE_WORKORDER_TYPE = 4;
    int DATA_TYPE_DEMAND_TYPE = 5;
    int DATA_TYPE_SERVICE_TYPE = 6;
    int DATA_TYPE_EQU = 7;
    int DATA_TYPE_EQU_TYPE = 8;
    int DATA_TYPE_PATROL_EXCEPTION = 9;
    int DATA_TYPE_KNOWLEDGE = 10;
    int DATA_TYPE_EQU_ALL = 11;

    int DATA_VISIT_PAY = 12;//拜访对象
    int DATA_TYPE_REASON=13;//原因
    int DATA_TYPE_INVALIDD = 14;//作废原因
    int DATA_TYPE_FAULT_OBJECT = 15;//作废对象
    int DATA_TYPE_SPECIALTY = 16;//专业

    int DATA_TYPE_LOCATION_BOARDING = 17; //登乘 只获取站点


    int LOCATION_CITY = 0;
    int LOCATION_SITE = 1;
    int LOCATION_BUILDING = 2;
    int LOCATION_FLOOR = 3;
    int LOCATION_ROOM = 4;


    int REASON_TYPE_PAUSE=0;
    int REASON_TYPE_INVALID=1;
    int REASON_TYPE_EXCEPTION=2;
    int REASON_TYPE_DEFAULT_OBJECT = 3;
}
