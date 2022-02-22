package com.facilityone.wireless.inventory.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.facilityone.wireless.a.arch.ec.module.AttachmentBean;
import com.facilityone.wireless.a.arch.ec.module.Page;

import java.util.List;

/**
 * Created by peter.peng on 2018/11/27.
 */

public class MaterialService {

    /**
     * 新建物资创建请求体
     */
    public static class MaterialCreateRequst {
        public Long warehouseId;//仓库 ID
        public String shelve;//货架
        public String name;//物资名称
        public String code;//物资编码
        public String unit;//单位
        public String brand;//品牌
        public String model;//型号
        public Float checkPrice;//核定价格
        public Float initialNumber;//初始数量
        public Float minNumber;//最低库存量
        public Long providerId;//供应商 ID
        public String providerName;//供应商 名字，providerId 值为空的使用作为新建使用
        public Float price;//单价
        public Long dueDate;//过期时间
        public String desc;//备注
        public List<String> pictures;//图片 ID 数组
        public Integer remindAhead; //提醒提前天数

    }

    /**
     * 请求物资列表请求体
     */
    public static class MaterialListRequest {
        public Long warehouseId;//仓库 ID
        public MaterialCondition condition;//查询条件
        public Page page;
    }

    /**
     * 物资查询条件
     */
    public static class MaterialCondition {
        //0 — 不限
        //1 — 库存充足
        //2 — 紧缺
        //3 — 有批次
        public Integer type;//数量类型
        public String name;//物资名称
        public String param;//模糊查询条件，用来过滤物料编码，品牌，型号
    }

    /**
     * 联网请求物资列表返回数据bean
     */
    public static class MaterialListBean {
        public Page page;
        public List<Material> contents;
    }

    /**
     * 物资实例bean
     */
    public static class Material implements Parcelable {
        public Long inventoryId;//库存 ID
        public String materialCode;//物料编码
        public String materialName;//物料名称
        public String materialBrand;//品牌
        public String materialShelf;//货架
        public String materialModel;//型号
        public String materialUnit;//单位
        public Float totalNumber;//账面库存数量
        public Float minNumber;//最低库存数量
        public Float realNumber;//有效库存数量
        public String cost;//单价
        public List<String> pictures;//图片 ID 数组


        public Material() {
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeValue(this.inventoryId);
            dest.writeString(this.materialCode);
            dest.writeString(this.materialName);
            dest.writeString(this.materialBrand);
            dest.writeString(this.materialShelf);
            dest.writeString(this.materialModel);
            dest.writeString(this.materialUnit);
            dest.writeValue(this.totalNumber);
            dest.writeValue(this.minNumber);
            dest.writeValue(this.realNumber);
            dest.writeString(this.cost);
            dest.writeStringList(this.pictures);
        }

        protected Material(Parcel in) {
            this.inventoryId = (Long) in.readValue(Long.class.getClassLoader());
            this.materialCode = in.readString();
            this.materialName = in.readString();
            this.materialBrand = in.readString();
            this.materialShelf = in.readString();
            this.materialModel = in.readString();
            this.materialUnit = in.readString();
            this.totalNumber = (Float) in.readValue(Float.class.getClassLoader());
            this.minNumber = (Float) in.readValue(Float.class.getClassLoader());
            this.realNumber = (Float) in.readValue(Float.class.getClassLoader());
            this.cost = in.readString();
            this.pictures = in.createStringArrayList();
        }

        public static final Creator<Material> CREATOR = new Creator<Material>() {
            @Override
            public Material createFromParcel(Parcel source) {
                return new Material(source);
            }

            @Override
            public Material[] newArray(int size) {
                return new Material[size];
            }
        };
    }

    /**
     * 预订记录详情物资实例bean
     */
    public static class ReserveMaterial implements Parcelable {
        public Long inventoryId;//库存 ID
        public String materialCode;//物料编码
        public String materialName;//物料名称
        public String materialBrand;//品牌
        public String materialModel;//型号
        public String materialUnit;//单位
        public Float cost;//单价
        public Float amount;//数量
        public Float bookAmount;//预定数量
        public Float receiveAmount;//领用数量


        public ReserveMaterial() {
        }


        protected ReserveMaterial(Parcel in) {
            inventoryId = (Long) in.readValue(Long.class.getClassLoader());
            materialCode = in.readString();
            materialName = in.readString();
            materialBrand = in.readString();
            materialModel = in.readString();
            materialUnit = in.readString();
            cost = (Float) in.readValue(Float.class.getClassLoader());
            ;
            amount = (Float) in.readValue(Float.class.getClassLoader());
            bookAmount = (Float) in.readValue(Float.class.getClassLoader());
            receiveAmount = (Float) in.readValue(Float.class.getClassLoader());

        }

        public static final Creator<Material> CREATOR = new Creator<Material>() {
            @Override
            public Material createFromParcel(Parcel in) {
                return new Material(in);
            }

            @Override
            public Material[] newArray(int size) {
                return new Material[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeValue(inventoryId);
            dest.writeString(materialCode);
            dest.writeString(materialName);
            dest.writeString(materialBrand);
            dest.writeString(materialModel);
            dest.writeString(materialUnit);
            dest.writeValue(cost);
            dest.writeValue(amount);
            dest.writeValue(bookAmount);
            dest.writeValue(receiveAmount);

        }
    }

    /**
     * 物资详情实例bean
     */
    public static class MaterialInfo implements Parcelable {
        public Long inventoryId;//库存 ID
        public String code;//库存编号
        public String name;//库存名称
        public Long warehouseId;//仓库 ID
        public String warehouseName;//仓库名称
        public String brand;//品牌
        public String shelves;//货架
        public String model;//型号
        public String unit;//单位
        public String price;//单价
        public Float cost;//单价
        public Float minNumber;//最低库存量
        public Float totalNumber;//账面库存数量
        public Float realNumber;//有效库存数量
        public Float reservedNumber;//被预定的库存数量
        public String desc;//备注
        public List<String> pictures;//图片 ID 数组
        public List<AttachmentBean> attachment;//附件数组

        //--自加
        public Float number;//入库、出库、移库、盘点、物资预定添加物资预定数量
        public List<BatchService.Batch> batch;//批次

        public Float amount;//数量
        public Float bookAmount;//预定数量
        public Float receiveAmount;//领用数量

        public MaterialInfo() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeValue(this.inventoryId);
            dest.writeString(this.code);
            dest.writeString(this.name);
            dest.writeValue(this.warehouseId);
            dest.writeString(this.warehouseName);
            dest.writeString(this.brand);
            dest.writeString(this.shelves);
            dest.writeString(this.model);
            dest.writeString(this.unit);
            dest.writeString(this.price);
            dest.writeValue(this.cost);
            dest.writeValue(this.minNumber);
            dest.writeValue(this.totalNumber);
            dest.writeValue(this.realNumber);
            dest.writeValue(this.reservedNumber);
            dest.writeString(this.desc);
            dest.writeStringList(this.pictures);
            dest.writeTypedList(this.attachment);
            dest.writeValue(this.number);
            dest.writeTypedList(this.batch);
            dest.writeValue(this.amount);
            dest.writeValue(this.bookAmount);
            dest.writeValue(this.receiveAmount);
        }

        protected MaterialInfo(Parcel in) {
            this.inventoryId = (Long) in.readValue(Long.class.getClassLoader());
            this.code = in.readString();
            this.name = in.readString();
            this.warehouseId = (Long) in.readValue(Long.class.getClassLoader());
            this.warehouseName = in.readString();
            this.brand = in.readString();
            this.shelves = in.readString();
            this.model = in.readString();
            this.unit = in.readString();
            this.price = in.readString();
            this.cost = (Float) in.readValue(Float.class.getClassLoader());
            this.minNumber = (Float) in.readValue(Float.class.getClassLoader());
            this.totalNumber = (Float) in.readValue(Float.class.getClassLoader());
            this.realNumber = (Float) in.readValue(Float.class.getClassLoader());
            this.reservedNumber = (Float) in.readValue(Float.class.getClassLoader());
            this.desc = in.readString();
            this.pictures = in.createStringArrayList();
            this.attachment = in.createTypedArrayList(AttachmentBean.CREATOR);
            this.number = (Float) in.readValue(Float.class.getClassLoader());
            this.batch = in.createTypedArrayList(BatchService.Batch.CREATOR);
            this.amount = (Float) in.readValue(Float.class.getClassLoader());
            this.bookAmount = (Float) in.readValue(Float.class.getClassLoader());
            this.receiveAmount = (Float) in.readValue(Float.class.getClassLoader());
        }

        public static final Creator<MaterialInfo> CREATOR = new Creator<MaterialInfo>() {
            @Override
            public MaterialInfo createFromParcel(Parcel source) {
                return new MaterialInfo(source);
            }

            @Override
            public MaterialInfo[] newArray(int size) {
                return new MaterialInfo[size];
            }
        };
    }

    /**
     * 入库请求体
     */
    public static class InventoryInRequest {
        public Long warehouseId;//仓库id
        public String remarks;//入库备注
        public List<Inventory> inventory;//物料数组
    }

    /**
     * 库存
     */
    public static class Inventory {
        public Long inventoryId;//库存id
        public List<BatchService.Batch> batch;//批次数组
    }


    /**
     * 库存二维码实例bean
     */
    public static class InventoryQRCodeBean {
        public String function;// 功能--比如能源管理（energy）
        public String subfunction;// 子功能--比如抄表项（meter）
        public String companyName;//公司名称--facilityone--简写--F-ONE
        public String wareHouseId;
        public String code;
    }

    /**
     * 物资出库、移库请求体
     */
    public static class MaterialOutRequest {
        //        0 — 直接出库
        //        1 — 预定出库
        //        2 — 移库
        public Integer type;//	请求类型
        public Long activityId;//预订单id
        public Long warehouseId;//仓库 ID
        public Long targetWarehouseId;//目标仓库 ID
        public String remarks;//出库移库备注
        public Long receivingPersonId;//领用人 ID
        public Long administrator;//管理员 ID
        public Long targetAdministrator;//目标仓库管理员 ID
        public Long supervisor;//主管 ID
        public List<Inventory> inventory;//物料数组
    }

    /**
     * 取消出库、库存预定审核请求体
     */
    public static class InventoryApprovalRequest {
        //0 — 审核通过（主管审核）
        //1 — 审核不通过（主管审核）
        //2 — 取消出库（仓库管理员取消）
        //3 — 取消预定（预定人取消）
        public Integer type;//请求类型
        public Long activityId;//预订单id
        public String desc;//操作说明
    }


    /**
     * 物资盘点请求体
     */
    public static class MaterialCheckRequest {
        public Long warehouseId;//仓库id
        public List<Inventory> inventory;//物料数组
    }

    /**
     * 物资预定请求体
     */
    public static class MaterialReserveRequest {
        public Long userId;//预订人id
        public Long date;//预定时间
        public String desc;//描述
        public String remarks;//备注
        public Long woId;//关联工单id
        public String woCode;//关联工单编号
        public Long warehouseId;//仓库id
        public Long administrator;//仓库管理员id
        public Long supervisor;//主管id
        public List<Long> sysPareParts;//专业id数组
        public List<MaterialReserve> materials;//物料数组
    }


    /**
     * 物资预定物资实例
     */
    public static class MaterialReserve {
        public Long inventoryId;//库存id
        public Float amount;//数量
    }

    /**
     * 通过id(编码)获取物资记录列表数据请求体
     */
    public static class MaterialRecordListRequest{
        public Long inventoryId;
        public String code;//物资编号
        public Long warehouseId;//仓库id
        public Page page;
    }


    /**
     * 联网获取物资列表数据返回实例
     */
    public static class MaterialRecordListBean{
        public Page page;
        public List<MaterialRecord> contents;
    }

    /**
     * 物资记录实例
     */
    public static class MaterialRecord{
        public Long recordId;//记录 ID
        public String code;//记录编号
        public String provider;//供应商
        public String price;//单价
        public Float number;//入库数量
        public Float validNumber;//有效数量
        public Long date;//入库时间戳
        public Long dueDate;//过期时间

    }

}
