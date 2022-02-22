package com.facilityone.wireless.inventory.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ProfessionalService {
    public static class InventoryProBean implements Parcelable {

        public Long id ;
        public String configName;
        public boolean checked;

        public transient String configNamePinyin;
        public transient String configNameFirstLetters;

        public transient int start;//搜索匹配的开始位置(name 中的位置顺序)
        public transient int end;//搜索匹配的结束位置(name 中的位置顺序)

        public InventoryProBean(){

        }

        public InventoryProBean(Parcel in) {
            if (in.readByte() == 0) {
                id = null;
            } else {
                id = in.readLong();
            }
            configName = in.readString();
            this.checked = in.readByte() != 0;
            this.configNamePinyin = in.readString();
            this.configNameFirstLetters = in.readString();
            this.start= in.readInt();
            this.end = in.readInt();
        }

        public static final Creator<InventoryProBean> CREATOR = new Creator<InventoryProBean>() {
            @Override
            public InventoryProBean createFromParcel(Parcel in) {
                return new InventoryProBean(in);
            }

            @Override
            public InventoryProBean[] newArray(int size) {
                return new InventoryProBean[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(this.id);
            dest.writeString(this.configName);
            dest.writeByte(this.checked ? (byte) 1 : (byte) 0);
            dest.writeString(this.configNamePinyin);
            dest.writeString(this.configNameFirstLetters);
        }
    }
}
