package com.facilityone.wireless.demand.module;

import com.facilityone.wireless.a.arch.ec.module.LocationBean;

import java.util.List;

/**
 * Author：gary
 * Email: xuhaozv@163.com
 * description:需求创建
 * Date: 2018/6/22 下午12:05
 */
public class DemandCreateService {

    public static class DemandCreateReq {
        public String requester;
        public String contact;
        public String desc;
        public Long serviceTypeId;
        public List<String> photoIds;
        public List<String> audioIds;
        public List<String> videoIds;
        public List<String> equipment;
        public LocationBean location;
        public Long resultId; //巡检Id
    }


    public static class CompleteDeviceReq{
        public Long reqId;
        public String desc;
        public LocationBean location;
        public List<String> equipment;
        public List<String> photoIds;
        public List<String> audioIds;
        public List<String> videoIds;
    }


    /**
     * @Created by: kuuga
     * @Date: on 2021/8/25 10:55
     * @Description: 签到记录响应体
     */
    public static class AttendanceResp {
        public Long contactId;
        public String contactName;
        public String locationName;
        public Boolean signStatus;
        public LocationBean location;
        public Long createTime;
        //站点和管理的所有区间
        public List<Long> buildingIds;
        //签入、签出判断
    }
}
