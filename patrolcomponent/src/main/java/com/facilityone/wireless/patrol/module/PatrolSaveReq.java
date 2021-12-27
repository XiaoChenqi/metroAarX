package com.facilityone.wireless.patrol.module;

import java.io.File;
import java.util.List;

/**
 * Author：gary
 * Email: xuhaozv@163.com
 * description:巡检数据保存
 * Date: 2018/11/14 4:49 PM
 */
public class PatrolSaveReq {
    public Long userId;
    public Integer operateType;
    public PatrolTaskReq patrolTask;

    public static class PatrolTaskReq {
        public Long patrolTaskId;
        public Long startDateTime;
        public Long endDateTime;
        public List<PatrolSpotReq> spots;
    }

    public static class PatrolSpotReq {
        public Long patrolSpotId;
        public Long startDateTime;
        public Long endDateTime;
        public Boolean finished;
        public List<PatrolEquReq> exceptionEquipment;
        public List<PatrolItemReq> contents;
    }

    public static class PatrolEquReq {
        public Long eqId;
        public Integer status;
        public String desc;
    }

    public static class PatrolItemReq {
        public Long patrolTaskSpotResultId;
        public String resultSelect; //选择值
        public Double resultInput; //输入值（数字）
        public String comment; //巡检项描述
        public String resultText; //输入文本
        public List<String> photoIds;
    }

    public static class PicList{
        public String name;
        public File file;
    }
}