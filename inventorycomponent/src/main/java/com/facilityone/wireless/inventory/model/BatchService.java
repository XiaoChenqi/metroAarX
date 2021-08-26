package com.facilityone.wireless.inventory.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.facilityone.wireless.a.arch.ec.module.Page;

import java.util.List;

/**
 * Created by peter.peng on 2018/12/5.
 */

public class BatchService {

    /**
     * 批次列表请求体
     */
    public static class BatchListRequest{
//        请求类型
//        0 — 所有批次
//        1 — 有效批次（账面数量大于0）
//        2 — 盘点批次（如果所有批次都为0 则取最后一次的批次）
        public Integer type;
        public Long inventoryId;//库存id
        public Page page;
    }

    /**
     * 批次请求数据列表返回实例
     */
    public static class BatchListBean{
        public Page page;
        public List<Batch> contents;
    }

    /**
     * 批次
     */
    public static class Batch implements Parcelable {
        public Long batchId;//批次 ID
        public Long providerId;//供应商id
        public String providerName;//供应商名字
        public Long date;//入库日期
        public Long dueDate;//过期时间
        public String cost;//单价
        public String price;//单价
        public Float amount;//有效数量
        public Float number;//数量
        public Float adjustNumber;//盘点调整数量
        public Float inventoryNumber;//系统库存数量(用于盘点)
        public String desc;

        public Batch() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeValue(this.batchId);
            dest.writeValue(this.providerId);
            dest.writeString(this.providerName);
            dest.writeValue(this.date);
            dest.writeValue(this.dueDate);
            dest.writeString(this.cost);
            dest.writeString(this.price);
            dest.writeValue(this.amount);
            dest.writeValue(this.number);
            dest.writeValue(this.adjustNumber);
            dest.writeValue(this.inventoryNumber);
            dest.writeString(this.desc);
        }

        protected Batch(Parcel in) {
            this.batchId = (Long) in.readValue(Long.class.getClassLoader());
            this.providerId = (Long) in.readValue(Long.class.getClassLoader());
            this.providerName = in.readString();
            this.date = (Long) in.readValue(Long.class.getClassLoader());
            this.dueDate = (Long) in.readValue(Long.class.getClassLoader());
            this.cost = in.readString();
            this.price = in.readString();
            this.amount = (Float) in.readValue(Float.class.getClassLoader());
            this.number = (Float) in.readValue(Float.class.getClassLoader());
            this.adjustNumber = (Float) in.readValue(Float.class.getClassLoader());
            this.inventoryNumber = (Float) in.readValue(Float.class.getClassLoader());
            this.desc = in.readString();
        }

        public static final Creator<Batch> CREATOR = new Creator<Batch>() {
            @Override
            public Batch createFromParcel(Parcel source) {
                return new Batch(source);
            }

            @Override
            public Batch[] newArray(int size) {
                return new Batch[size];
            }
        };
    }
}
