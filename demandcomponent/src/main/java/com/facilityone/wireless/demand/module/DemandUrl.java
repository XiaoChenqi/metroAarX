package com.facilityone.wireless.demand.module;

/**
 * Created by: owen.
 * Date: on 2018/6/22 下午5:41.
 * Description: 需求 接口
 * email:
 */

public interface DemandUrl {
    //获取需求列表。
    String DEMAND_LIST_URL = "/m/v1/servicecenter/list";
    //创建需求
    String DEMAND_CREATE_URL = "/m/v2/servicecenter/create";
    //需求详情
    String DEMAND_INFO_URL = "/m/v1/servicecenter/detail";
    //需求操作 需求审核，保存，完成
    String DEMAND_OPT_URL = "/m/v1/servicecenter/operation";
    //满意度评价
    String DEMAND_EVALUATE_URL = "/m/v1/servicecenter/evaluate";

    //完善需求
    String DEMAND_COMPLETE_DEVICE  = "/m/v1/servicecenter/improve";
    //需求操作
    String DEMAND_REPORT_OBSTACLE_OPERATION  = "/m/v1/servicecenter/operation";

}
