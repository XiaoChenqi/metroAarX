package com.facilityone.wireless.a.arch.offline.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.facilityone.wireless.a.arch.ec.module.LocationBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Author：gary
 * Email: xuhaozv@163.com
 * description:巡检点位
 * Date: 2018/11/1 3:30 PM
 */
public class PatrolSpotEntity implements Parcelable {
    private Long patrolSpotId;
    private Long spotId;
    private Integer sort;
    private Long taskId;
    private Long startTime;
    private Long endTime;
    private Integer compNumber;
    private Integer equNumber;
    private int exception;
    private int needSync;
    private int completed;
    private int remoteCompleted;
    private Integer deleted;
    private String handler;
    private String name;
    private String code;
    private String locationName;
    private LocationBean location;
    private String taskName;
    private List<PatrolItemEntity> contents;
    private List<PatrolEquEntity> equipments;

    private Long taskDueStartDateTime;
    private Long taskDueEndDateTime;
    private Long taskPlanId;
    private String spotTaskName;
    private Integer taskTime; //开启点位最短完成时间
    private Long  taskStatus;

    public Long getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(Long taskStatus) {
        this.taskStatus = taskStatus;
    }

    public Integer getTaskTime() {
        return taskTime;
    }

    public void setTaskTime(Integer taskTime) {
        this.taskTime = taskTime;
    }

    public Long getTaskPlanId() {
        return taskPlanId;
    }

    public void setTaskPlanId(Long taskPlanId) {
        this.taskPlanId = taskPlanId;
    }

    public String getTaskName() {
        return taskName == null ? "" : taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Long getTaskDueStartDateTime() {
        return taskDueStartDateTime;
    }

    public void setTaskDueStartDateTime(Long taskDueStartDateTime) {
        this.taskDueStartDateTime = taskDueStartDateTime;
    }

    public Long getTaskDueEndDateTime() {
        return taskDueEndDateTime;
    }

    public void setTaskDueEndDateTime(Long taskDueEndDateTime) {
        this.taskDueEndDateTime = taskDueEndDateTime;
    }

    public int getRemoteCompleted() {
        return remoteCompleted;
    }

    public void setRemoteCompleted(int remoteCompleted) {
        this.remoteCompleted = remoteCompleted;
    }

    public String getLocationName() {
        return locationName == null ? "" : locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public LocationBean getLocation() {
        return location;
    }

    public void setLocation(LocationBean location) {
        this.location = location;
    }

    public String getCode() {
        return code == null ? "" : code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name == null ? "" : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHandler() {
        return handler == null ? "" : handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public Long getPatrolSpotId() {
        return patrolSpotId;
    }

    public void setPatrolSpotId(Long patrolSpotId) {
        this.patrolSpotId = patrolSpotId;
    }

    public Long getSpotId() {
        return spotId;
    }

    public void setSpotId(Long spotId) {
        this.spotId = spotId;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Integer getCompNumber() {
        return compNumber;
    }

    public void setCompNumber(Integer compNumber) {
        this.compNumber = compNumber;
    }

    public Integer getEquNumber() {
        return equNumber;
    }

    public void setEquNumber(Integer equNumber) {
        this.equNumber = equNumber;
    }

    public int getException() {
        return exception;
    }

    public void setException(int exception) {
        this.exception = exception;
    }

    public int getNeedSync() {
        return needSync;
    }

    public void setNeedSync(int needSync) {
        this.needSync = needSync;
    }

    public int getCompleted() {
        return completed;
    }

    public void setCompleted(int completed) {
        this.completed = completed;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    public String  getSpotTaskName(){
        return spotTaskName;
    }
    public void setSpotTaskName(String spotTaskName){
        this.spotTaskName = spotTaskName;
    }

    public List<PatrolItemEntity> getContents() {
        if (contents == null) {
            return new ArrayList<>();
        }
        return contents;
    }

    public void setContents(List<PatrolItemEntity> contents) {
        this.contents = contents;
    }

    public List<PatrolEquEntity> getEquipments() {
        if (equipments == null) {
            return new ArrayList<>();
        }
        return equipments;
    }

    public void setEquipments(List<PatrolEquEntity> equipments) {
        this.equipments = equipments;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.patrolSpotId);
        dest.writeValue(this.spotId);
        dest.writeValue(this.sort);
        dest.writeValue(this.taskId);
        dest.writeValue(this.startTime);
        dest.writeValue(this.endTime);
        dest.writeValue(this.compNumber);
        dest.writeValue(this.equNumber);
        dest.writeInt(this.exception);
        dest.writeInt(this.needSync);
        dest.writeInt(this.completed);
        dest.writeInt(this.remoteCompleted);
        dest.writeValue(this.deleted);
        dest.writeString(this.handler);
        dest.writeString(this.name);
        dest.writeString(this.code);
        dest.writeString(this.locationName);
        dest.writeParcelable(this.location, flags);
        dest.writeString(this.taskName);
        dest.writeList(this.contents);
        dest.writeTypedList(this.equipments);
        dest.writeValue(this.taskDueStartDateTime);
        dest.writeValue(this.taskDueEndDateTime);
        dest.writeValue(this.taskPlanId);
        dest.writeString(this.spotTaskName);
    }

    public void readFromParcel(Parcel source) {
        this.patrolSpotId = (Long) source.readValue(Long.class.getClassLoader());
        this.spotId = (Long) source.readValue(Long.class.getClassLoader());
        this.sort = (Integer) source.readValue(Integer.class.getClassLoader());
        this.taskId = (Long) source.readValue(Long.class.getClassLoader());
        this.startTime = (Long) source.readValue(Long.class.getClassLoader());
        this.endTime = (Long) source.readValue(Long.class.getClassLoader());
        this.compNumber = (Integer) source.readValue(Integer.class.getClassLoader());
        this.equNumber = (Integer) source.readValue(Integer.class.getClassLoader());
        this.exception = source.readInt();
        this.needSync = source.readInt();
        this.completed = source.readInt();
        this.remoteCompleted = source.readInt();
        this.deleted = (Integer) source.readValue(Integer.class.getClassLoader());
        this.handler = source.readString();
        this.name = source.readString();
        this.code = source.readString();
        this.locationName = source.readString();
        this.location = source.readParcelable(LocationBean.class.getClassLoader());
        this.taskName = source.readString();
        this.contents = new ArrayList<PatrolItemEntity>();
        source.readList(this.contents, PatrolItemEntity.class.getClassLoader());
        this.equipments = source.createTypedArrayList(PatrolEquEntity.CREATOR);
        this.taskDueStartDateTime = (Long) source.readValue(Long.class.getClassLoader());
        this.taskDueEndDateTime = (Long) source.readValue(Long.class.getClassLoader());
        this.taskPlanId = (Long) source.readValue(Long.class.getClassLoader());
        this.spotTaskName = source.readString();
    }

    public PatrolSpotEntity() {
    }

    protected PatrolSpotEntity(Parcel in) {
        this.patrolSpotId = (Long) in.readValue(Long.class.getClassLoader());
        this.spotId = (Long) in.readValue(Long.class.getClassLoader());
        this.sort = (Integer) in.readValue(Integer.class.getClassLoader());
        this.taskId = (Long) in.readValue(Long.class.getClassLoader());
        this.startTime = (Long) in.readValue(Long.class.getClassLoader());
        this.endTime = (Long) in.readValue(Long.class.getClassLoader());
        this.compNumber = (Integer) in.readValue(Integer.class.getClassLoader());
        this.equNumber = (Integer) in.readValue(Integer.class.getClassLoader());
        this.exception = in.readInt();
        this.needSync = in.readInt();
        this.completed = in.readInt();
        this.remoteCompleted = in.readInt();
        this.deleted = (Integer) in.readValue(Integer.class.getClassLoader());
        this.handler = in.readString();
        this.name = in.readString();
        this.code = in.readString();
        this.locationName = in.readString();
        this.location = in.readParcelable(LocationBean.class.getClassLoader());
        this.taskName = in.readString();
        this.contents = new ArrayList<PatrolItemEntity>();
        in.readList(this.contents, PatrolItemEntity.class.getClassLoader());
        this.equipments = in.createTypedArrayList(PatrolEquEntity.CREATOR);
        this.taskDueStartDateTime = (Long) in.readValue(Long.class.getClassLoader());
        this.taskDueEndDateTime = (Long) in.readValue(Long.class.getClassLoader());
        this.taskPlanId = (Long) in.readValue(Long.class.getClassLoader());
        this.spotTaskName = in.readString();
    }

    public static final Creator<PatrolSpotEntity> CREATOR = new Creator<PatrolSpotEntity>() {
        @Override
        public PatrolSpotEntity createFromParcel(Parcel source) {
            return new PatrolSpotEntity(source);
        }

        @Override
        public PatrolSpotEntity[] newArray(int size) {
            return new PatrolSpotEntity[size];
        }
    };
}
