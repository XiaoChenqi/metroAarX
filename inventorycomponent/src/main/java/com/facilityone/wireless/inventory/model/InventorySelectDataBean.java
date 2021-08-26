package com.facilityone.wireless.inventory.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by peter.peng on 2018/11/26.
 */

public class InventorySelectDataBean implements Parcelable {

    public long id;
    public String name;
    public transient String namePinyin;
    public transient String nameFirstLetters;
    public String subStr;
    public transient String subPinyin;
    public transient String subFirstLetters;
    public transient int start;//搜索匹配的开始位置(name 中的位置顺序)
    public transient int end;//搜索匹配的结束位置(name 中的位置顺序)
    public transient int subStart;//搜索匹配的开始位置(subStr 中的位置顺序)
    public transient int subEnd;//搜索匹配的结束位置(subStr 中的位置顺序)
    public transient Object target;

    public InventorySelectDataBean() {
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeString(this.namePinyin);
        dest.writeString(this.nameFirstLetters);
        dest.writeString(this.subStr);
        dest.writeString(this.subPinyin);
        dest.writeString(this.subFirstLetters);
        dest.writeInt(this.start);
        dest.writeInt(this.end);
        dest.writeInt(this.subStart);
        dest.writeInt(this.subEnd);
        dest.writeValue(this.target);
    }

    protected InventorySelectDataBean(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.namePinyin = in.readString();
        this.nameFirstLetters = in.readString();
        this.subStr = in.readString();
        this.subPinyin = in.readString();
        this.subFirstLetters = in.readString();
        this.start = in.readInt();
        this.end = in.readInt();
        this.subStart = in.readInt();
        this.subEnd = in.readInt();
        this.target = in.readValue(Object.class.getClassLoader());
    }

    public static final Creator<InventorySelectDataBean> CREATOR = new Creator<InventorySelectDataBean>() {
        @Override
        public InventorySelectDataBean createFromParcel(Parcel source) {
            return new InventorySelectDataBean(source);
        }

        @Override
        public InventorySelectDataBean[] newArray(int size) {
            return new InventorySelectDataBean[size];
        }
    };
}
