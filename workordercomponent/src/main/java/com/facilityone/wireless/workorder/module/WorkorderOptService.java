package com.facilityone.wireless.workorder.module;

import com.facilityone.wireless.a.arch.ec.module.LocationBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Author：gary
 * Email: xuhaozv@163.com
 * description:工单操作
 * Date: 2018/7/17 下午5:41
 */
public class WorkorderOptService {
    //派工
    public static class WorkorderOptDispatchReq {
        public Long woId;
        public Long estimatedArrivalDate;
        public Long estimatedCompletionDate;
        public Long estimatedWorkingTime;
        public String sendWorkContent;
        public List<UploadLaborerBean> laborers;

        public WorkorderOptDispatchReq() {
            this.laborers = new ArrayList<>();
        }
    }

    public static class UploadLaborerBean {
        public Long laborerId;
        public boolean responsible;
    }

    //工单操作通用
    public static class WorkorderOptCommonReq {
        public Long woId;
        public Integer operateType;
        public String operateDescription;
        public Long operateReasonId; //操作原因Id
    }

    //暂停工单
    public static class WorkorderOptPauseReq {
        public Long woId;
        public Integer type;
        public String desc;
        public Long operateReasonId;
        public Long endTime;
    }

    //审批申请
    public static class WorkorderOptApprovalReq {
        public Long woId;
        public List<Long> approverIds;
        public UploadApprovalBean approval;
    }

    public static class UploadApprovalBean {
        public Long templateId;
        public Integer approvalType;
        public List<UploadApprovalPBean> parameters;
    }

    public static class UploadApprovalPBean {
        public String name;
        public String value;
    }

    //审批
    public static class WorkorderOptApprovalVReq {
        public Long woId;
        public Long approvalId;
        public Integer operateType;
        public String content;
    }

    //异常审批
    public static class WorkorderOptApprovalVErrorReq {
        public Long woId;
        public Integer status;
        public String approveNote;
    }

    /* 处理工单操作 */
    //填写内容
    public static class WorkorderInputSaveReq {
        public Long woId;
        public String workContent;
        public List<String> pictures;
    }

    //故障设备
    public static class WorkOrderDeviceReq {
        public Long woId;
        public Long equipmentId;
        public Integer operateType;
        public String failureDesc;
        public String repairDesc;
    }
    
    //空间位置
    public static class WorkOrderSpaceReq {
        public Long woId;
        public Long recordId;
        public Integer operateType;
        public String repairDesc;
        public LocationBean location;
    }

    //工具
    public static class WorkOrderToolReq {
        public Long woId;
        public Long toolId;
        public Integer operateType;
        public Integer amount;
        public Double cost;
        public String name;
        public String model;
        public String unit;
        public String comment;
    }
    
    //收取明细
    public static class WorkOrderChargeReq {
        public Long woId;
        public Long chargeId;
        public Integer operateType;
        public Double amount;
        public String name;
    }

     /**
      * @Auther: karelie
      * @Date: 2021/8/16
      * @Infor: 作废申请
      */
     public static class InvalidOrderPostReq{
         public Long woId; //工单Id
         public String desc ; //申请作废原因
         public Long operateReasonId; //申请作废原因Id
     }



    //异常工单审批申请
    public static class WorkorderOptExceptionApprovalReq {
        public Long woId;
        public Integer status;
        public String approveNote;
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
        public Boolean responsible ; //是否为负责人
    }



}
