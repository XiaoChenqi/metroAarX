package com.facilityone.wireless.inventory.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.facilityone.wireless.a.arch.ec.module.Page;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by peter.peng on 2018/11/26.
 * 仓库
 */

public class StorageService {

    /**
     * 联网请求选择仓库列表数据请求体
     */
    public static class StorageListRequest
    {
        public Long employeeId;
        public Page page;
    }

    /**
     * 联网请求选择仓库列表数据返回实例bean
     */
    public static class StorageListBean {
        public Page page;
        public List<Storage> contents;
    }

    /**
     * 仓库实例
     */
    public static class Storage {
        public Long warehouseId;//仓库id
        public String name;//仓库名称
        public ArrayList<Administrator> administrator;//管理员数组
        public boolean spareParts;
    }

    /**
     * 仓库管理员
     */
    public static class Administrator implements Parcelable {
        public Long administratorId;//管理员id
        public String name;//姓名

        public Administrator() {
        }

        protected Administrator(Parcel in) {
            administratorId = (Long) in.readValue(Long.class.getClassLoader());
            name = in.readString();
        }

        public static final Creator<Administrator> CREATOR = new Creator<Administrator>() {
            @Override
            public Administrator createFromParcel(Parcel in) {
                return new Administrator(in);
            }

            @Override
            public Administrator[] newArray(int size) {
                return new Administrator[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeValue(administratorId);
            dest.writeString(name);
        }
    }


    /**
     * 请求仓库列表数据请求体
     */
    public static class WareHouseListRequest{
        public Page page;
    }


    /**
     * 联网请求仓库列表数据返回实例bean
     */
    public static class WareHouseListBean {
        public Page page;
        public List<WareHouse> contents;
    }


    /**
     * 仓库列表数据实例bean
     */
    public static class WareHouse implements Parcelable{
        public Long warehouseId;//仓库 ID
        public String warehouseName;//仓库名称
        public String location;//仓库位置
        public String contact;//联系人
        public String materialTypeCount;//物料类型(数量)
        public String materialCount;//库存数量
        public String lackMaterialCount;//短缺的物料的数量

        protected WareHouse(Parcel in) {
            warehouseId = (Long) in.readValue(Long.class.getClassLoader());
            warehouseName = in.readString();
            location = in.readString();
            contact = in.readString();
            materialTypeCount = in.readString();
            materialCount = in.readString();
            lackMaterialCount = in.readString();
        }

        public static final Creator<WareHouse> CREATOR = new Creator<WareHouse>() {
            @Override
            public WareHouse createFromParcel(Parcel in) {
                return new WareHouse(in);
            }

            @Override
            public WareHouse[] newArray(int size) {
                return new WareHouse[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeValue(warehouseId);
            dest.writeString(warehouseName);
            dest.writeString(location);
            dest.writeString(contact);
            dest.writeString(materialTypeCount);
            dest.writeString(materialCount);
            dest.writeString(lackMaterialCount);
        }
    }
}
