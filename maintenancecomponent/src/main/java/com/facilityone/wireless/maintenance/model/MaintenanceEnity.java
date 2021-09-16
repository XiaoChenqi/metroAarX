package com.facilityone.wireless.maintenance.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.facilityone.wireless.a.arch.ec.module.Page;

import java.io.Serializable;
import java.util.List;

import retrofit2.http.PUT;

public class MaintenanceEnity {

     /**
      * @Auther: karelie
      * @Date: 2021/8/18
      * @Infor: 列表数据源实体
      */

     public static class MaintenanceListResp {
         public Page page;
         public List<MaintenanceListEnity> contents;
     }
     public static class MaintenanceListEnity implements Serializable {
         private static final long serialVersionUID = 2644063965549678012L;
         public Long woId; //单号
         public String code; //工单编号
         public Long priorityId;
         public String priority;
         public String priorityName;
         public String woDescription; //工单详情
         public Long createDateTime; //创建时间
         public String location; //位置
         public Integer status; //工单状态
         public Integer currentLaborerStatus;
         public String applicantName;
         public String applicantPhone;
         public String serviceTypeName;
         public String workContent;
         public Long actualCompletionDateTime;
         public Integer tag;
         public Integer choice; //是否选中
         public Long pmId; // 计划性维护PM工单Id
         public Long workTeamId ; //工作组Id
         public Integer newStatus ; //新状态
     }

      /**
       * @Auther: karelie
       * @Date: 2021/8/18
       * @Infor: 列表请求体
       */
      public static class MaintenanceListReq{
          public Integer type; //列表类型
          public Page page; //分页
          public MaintenanceService.ConditionBean condition;//筛选
      }

      public static class ElectronicLedgerEntity{
          public ElectronicLedgerEntity(int type, Object content) {
              this.type = type;
              this.content = content;
          }

          public ElectronicLedgerEntity(int type) {
              this.type = type;
          }

          public int type;
          public Object content;
          public String value;
          public String subValue;
          public static final int TYPE_HEADER = 1;
          public static final int TYPE_RADIO = 2;
          public static final int TYPE_EDIT = 3;
          public static final int TYPE_RADIO_SUB = 4;
      }


       /**
        * @Auther: karelie
        * @Date: 2021/8/23
        * @Infor: 批量接单 请求体
        */
       public static class ReceiveOrderReq{
           public List<Long> ids; //工单数组
       }

        /**
         * @Auther: karelie
         * @Date: 2021/8/23
         * @Infor: 批量派单 请求体
         */
        public static class BatchOrderReq{
            public List<Long> ids; //工单数组
            public Long estimatedArrivalDate; //预估到达时间
            public Long estimatedCompletionDate; //预估完成时间
            public Long estimatedWorkingTime ; //预估工作耗时（mm）
            public List<Laborers> laborers; //执行人数组
        }

        public static class Laborers{
            public Long laborerId; //执行人Id
            public Long responsible ; //是否为负责人
        }




}
