package com.facilityone.wireless.demand.module;

import com.facilityone.wireless.a.arch.ec.module.AttachmentBean;
import com.facilityone.wireless.a.arch.ec.module.LocationBean;
import com.facilityone.wireless.a.arch.ec.module.OrdersBean;
import com.facilityone.wireless.a.arch.ec.module.Page;
import com.facilityone.wireless.a.arch.ec.module.RequesterBean;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by: owen.
 * Date: on 2018/6/22 下午12:03.
 * Description: 待处理
 * email:
 */

public class DemandService {
    //需求列表
    public static class DemandResponseBean {
        public Page page;
        public List<DemandBean> contents;
    }

    /**
     * 四运新需求修改
     * Coder:Karelie
     * */

    public static class DeviceInforListEnity{
        public String deviceName; //设备名称
        public String deviceNumber;  //设备编号
        public String deviceLocation; //设备位置
    }


    /***********************************************************************************/

    //需求列表bean
    public static class DemandBean {
        public Long reqId;//需求ID
        public String code;//需求编号
        public String requester;//需求人名字
        public String type; // 需求类型
        public String desc; // 需求描述
        public Integer status; // 需求状态
        public Long createDate; // 需求创建时间
        public Integer origin;//需求来源方式
        public List<RelatedOrder> orders;//关联工单数组
    }

    //
    public static class RelatedOrder{
        public Long woId;//工单 ID
        public String code;//工单编号
        public Integer status;//工单状态
    }

    //需求列表请求体
    public static class DemandListReq {
        public Integer queryType;
        public Long timeStart;
        public Long timeEnd;
        public Page page;
    }

    //需求列表响应体
    public static class DemandListResp {
        public Page page;
        public List<DemandBean> contents;
    }

    public static class DemandInfoBean {
        public Long reqId;
        public String code;
        public String type;
        public Integer status;
        public Integer origin;
        public String desc;
        public Long createDate;
        public String telephone;
        public Long signImgId;
        @SerializedName("location")
        public String locationName;
        public Long reserveStartTime;
        public Long reserveEndTime;
        @SerializedName("locationId")
        public LocationBean location;
        public RequesterBean requester;
        public List<RecordsBean> records;
        public List<OrdersBean> orders;
        public List<String> images;
        public List<String> audios;
        public List<String> videos;
        public List<Integer> currentRoles;
        public List<AttachmentBean> attachment;

         /**
          * @Auther: karelie
          * @Date: 2021/8/27
          * @Infor: 需求类型
          */
         public String serviceType; //需求类型
         public Long serviceTypeId; //需求类型Id

    }

    public static class RecordsBean {
        public Long recordId;
        public Long date;
        public String content;
        public Integer recordType;
        public String handler;
    }

    //需求操作
    public static class DemandOptReq {
        public Long reqId;
        public Integer gradeId;
        public Integer operateType;
        public Long reqTypeId;
        public String desc;
    }
    
    //评价
    public  static class DemandEvaluateReq{
        public Long reqId;
        public Integer quality;
        public Integer speed;
        public Integer attitude;
        public String desc;
    }

    public static class DemandOperationReq{
        public Long reqId; //需求Id
        public Integer gradeId; //满意度
        public String desc; //描述
        /**
         * @param operateType
         *         0 — 审核通过
         *         1 — 审核拒绝
         *         2 — 保存
         *         3 — 处理完成
         *         4 — 满意度操作
         * */
        public Integer operateType; //操作类型

    }

}
