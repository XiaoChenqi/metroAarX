package com.facilityone.wireless.a.arch.ec.module;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class ReasonResponseBean {
    public Page page;
    public List<WorkorderReasonBean> contents;
    public Long newestDate;

    public static class WorkorderReasonBean implements Parcelable {
        public Long id;
        public String code;
        public Long parentId;
        public String description;
        public String fullName;
        public Integer sort;
        public Boolean deleted;

        protected WorkorderReasonBean(Parcel in) {
            if (in.readByte() == 0) {
                id = null;
            } else {
                id = in.readLong();
            }
            code = in.readString();
            if (in.readByte() == 0) {
                parentId = null;
            } else {
                parentId = in.readLong();
            }
            description = in.readString();
            fullName = in.readString();
            if (in.readByte() == 0) {
                sort = null;
            } else {
                sort = in.readInt();
            }
            byte tmpDeleted = in.readByte();
            deleted = tmpDeleted == 0 ? null : tmpDeleted == 1;
        }

        public static final Creator<WorkorderReasonBean> CREATOR = new Creator<WorkorderReasonBean>() {
            @Override
            public WorkorderReasonBean createFromParcel(Parcel in) {
                return new WorkorderReasonBean(in);
            }

            @Override
            public WorkorderReasonBean[] newArray(int size) {
                return new WorkorderReasonBean[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(this.id);
            dest.writeString(this.code);
            dest.writeLong(this.parentId);
            dest.writeString(this.description);
            dest.writeString(this.fullName);
            dest.writeInt(this.sort);
            dest.writeBoolean(this.deleted);
        }
    }
}
