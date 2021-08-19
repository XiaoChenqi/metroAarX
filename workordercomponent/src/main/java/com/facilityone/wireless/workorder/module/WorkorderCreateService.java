package com.facilityone.wireless.workorder.module;

import com.facilityone.wireless.a.arch.ec.module.LocationBean;

import java.util.List;

/**
 * Author：gary
 * Email: xuhaozv@163.com
 * description:创建工单
 * Date: 2018/7/4 上午9:23
 */
public class WorkorderCreateService {

    //创建工单请求
    public static class WorkorderCreateReq {
        public Long userId;
        public String name;
        public String phone;
        public Long organizationId;
        public Long serviceTypeId;
        public String scDescription;
        public Long priorityId;
        public LocationBean location;
        public Long processId;
        public Long patrolItemDetailId;
        public Long reqId;
        public Long woType;
        public List<Long> equipmentIds;
        public List<String> pictures;
    }

     /**
      * @Auther: karelie
      * @Date: 2021/8/12
      * @Infor: 四运地铁新加新派工单功能
      */
    public static class newOrderCreateReq{
         public Long userId;
         public String name;
         public String phone;
         public Long organizationId;
         public Long serviceTypeId;
         public String scDescription;
         public Long priorityId;
         public LocationBean location;
         public Long processId;
         public Long patrolItemDetailId;
         public Long reqId;
         public Integer woType;
         public List<Long> equipmentIds;
         public List<String> pictures;
         public newOrderCreateAllName nameAll; //所有需要用到的名称
         public List<WorkorderService.WorkOrderEquipmentsBean> equipmentSystemName; //故障设备名称
    }

     /**
      * @Auther: karelie
      * @Date: 2021/8/12
      * @Infor: 新派工单所有名称
      */
    public static class newOrderCreateAllName{
        public String departmentName ; //部门名称
         public String loactionName; //位置名称
         public String orderType;//工单类型名称
         public String serviceType ; //服务类型名称
         public String priority; //优先级名称
    }

     /**
      * @Auther: karelie
      * @Date: 2021/8/13
      * @Infor: 新派工单请求体
      */
     public static class newOrderCreate{
         public Long woId; //工单Id
         public Long serviceTypeId; //新的服务类型Id
         public Long flowId; //新的流程Id
         public Long priorityId ; //新的优先级Id
         public Integer orderType ; //新的工单类型 0-自检 1-纠正性维护
     }


}
