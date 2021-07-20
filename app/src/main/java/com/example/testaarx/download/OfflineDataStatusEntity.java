package com.example.testaarx.download;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Author：gary
 * Email: xuhaozv@163.com
 * description:离线数据状态
 * Date: 2018/10/23 2:54 PM
 */
public class OfflineDataStatusEntity implements Parcelable {
    private Long newestDate;
    private Boolean priorityTypeNew;
    private Boolean workFlowNew;
    private Boolean deviceNew;
    private Boolean locationNew;
    private Boolean serviceTypeNew;
    private Boolean departmentNew;
    private Boolean deviceTypeNew;
    private Boolean requirementTypeNew;
    private Boolean repositoryTypeNew;
    
    public Long getNewestDate() {
        return newestDate;
    }

    public void setNewestDate(Long newestDate) {
        this.newestDate = newestDate;
    }

    public Boolean getPriorityTypeNew() {
        return priorityTypeNew;
    }

    public void setPriorityTypeNew(Boolean priorityTypeNew) {
        this.priorityTypeNew = priorityTypeNew;
    }

    public Boolean getWorkFlowNew() {
        return workFlowNew;
    }

    public void setWorkFlowNew(Boolean workFlowNew) {
        this.workFlowNew = workFlowNew;
    }

    public Boolean getDeviceNew() {
        return deviceNew;
    }

    public void setDeviceNew(Boolean deviceNew) {
        this.deviceNew = deviceNew;
    }

    public Boolean getLocationNew() {
        return locationNew;
    }

    public void setLocationNew(Boolean locationNew) {
        this.locationNew = locationNew;
    }

    public Boolean getServiceTypeNew() {
        return serviceTypeNew;
    }

    public void setServiceTypeNew(Boolean serviceTypeNew) {
        this.serviceTypeNew = serviceTypeNew;
    }

    public Boolean getDepartmentNew() {
        return departmentNew;
    }

    public void setDepartmentNew(Boolean departmentNew) {
        this.departmentNew = departmentNew;
    }

    public Boolean getDeviceTypeNew() {
        return deviceTypeNew;
    }

    public void setDeviceTypeNew(Boolean deviceTypeNew) {
        this.deviceTypeNew = deviceTypeNew;
    }

    public Boolean getRequirementTypeNew() {
        return requirementTypeNew;
    }

    public void setRequirementTypeNew(Boolean requirementTypeNew) {
        this.requirementTypeNew = requirementTypeNew;
    }

    public Boolean getRepositoryTypeNew() {
        return repositoryTypeNew;
    }

    public void setRepositoryTypeNew(Boolean repositoryTypeNew) {
        this.repositoryTypeNew = repositoryTypeNew;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.newestDate);
        dest.writeValue(this.priorityTypeNew);
        dest.writeValue(this.workFlowNew);
        dest.writeValue(this.deviceNew);
        dest.writeValue(this.locationNew);
        dest.writeValue(this.serviceTypeNew);
        dest.writeValue(this.departmentNew);
        dest.writeValue(this.deviceTypeNew);
        dest.writeValue(this.requirementTypeNew);
        dest.writeValue(this.repositoryTypeNew);
    }

    public OfflineDataStatusEntity() {
    }

    protected OfflineDataStatusEntity(Parcel in) {
        this.newestDate = (Long) in.readValue(Long.class.getClassLoader());
        this.priorityTypeNew = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.workFlowNew = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.deviceNew = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.locationNew = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.serviceTypeNew = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.departmentNew = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.deviceTypeNew = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.requirementTypeNew = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.repositoryTypeNew = (Boolean) in.readValue(Boolean.class.getClassLoader());
    }

    public static final Creator<OfflineDataStatusEntity> CREATOR = new Creator<OfflineDataStatusEntity>() {
        @Override
        public OfflineDataStatusEntity createFromParcel(Parcel source) {
            return new OfflineDataStatusEntity(source);
        }

        @Override
        public OfflineDataStatusEntity[] newArray(int size) {
            return new OfflineDataStatusEntity[size];
        }
    };
}
