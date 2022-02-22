package com.facilityone.wireless.inventory.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.facilityone.wireless.a.arch.ec.module.Page;

import java.util.List;

/**
 * Created by peter.peng on 2018/12/3.
 */

public class ReserveService {

    /**
     * 预订记录请求体
     */
    public static class ReserveRecordRequest {
        public Long userId;//执行人 ID
        //1 — 待审核的（待我审核的预定单记录）
        //2 — 已审核的（我已审核的预定单记录）
        //3 — 我的预定（我提交的预定单记录）
        //4 — 待出库（我管理的所有仓库下的审批通过的预定单）
        //5 — 待驳回（我提交并被审批不通过的）
        //6 — 待出库（我提交并出库的）
        public Integer queryType;//查询类型
        public Long timeStart;//起始时间
        public Long timeEnd;//结束时间
        public Page page;
    }

    /**
     * 获取入定记录返回数据bean
     */
    public static class ReserveRecordListBean {
        public Page page;
        public List<ReserveRecordBean> contents;
    }


    /**
     * 预定记录实例bean
     */
    public static class ReserveRecordBean {

        public Long activityId;//预定单 ID
        public Long warehouseId;//仓库 ID
        public String warehouseName;//仓库名称
        public String reservationCode;//预定单编号
        public String reservationPersonName;//预定人姓名
        public Long woId;//关联工单 ID
        public String woCode;//	关联工单编号
        public Long reservationDate;//预定时间
        //0 — 未审核
        //1 — 通过（待出库)
        //2 — 取消（已驳回)
        //3 — 已出库
        //4 — 取消出库（管理员取消)
        //5 — 取消预定（预订人取消）
        public Integer status;//预定单状态
        public String operateDesc;//操作说明

    }

    /**
     * 预定详情
     */
    public static class ReserveRecordInfoBean{
        public Long activityId;//预定单 ID
        public String reservationCode;//预定单编号
        public Long reservationPersonId;//预订人 ID
        public String reservationPersonName;//预订人姓名
        public Long woId;//关联工单 ID
        public String woCode;//关联工单编号
        public String remarks;//预定备注
        public Long reservationDate;//预定日期
        public String reservationNote;//预定单说明
        public Integer status;//预定单状态
        public String receivingPersonName;//领用人姓名
        public Long receivingDate;//领用日期
        public String receivingNote;//领用说明
        public Long warehouseId;//仓库 ID
        public String warehouseName;//仓库名称
        public Long administrator;//仓库管理员 ID
        public String administratorName;//仓库管理员名称
        public Long supervisor;//主管 ID
        public String supervisorName;//主管姓名
        public String operateDesc;//操作说明（比如审批拒绝说明等）
        public List<MaterialService.ReserveMaterial> materials;
        public List<RecordHistory> histories;//操作记录
        public String sysProfessional;//专业
    }

    /**
     * 预订单详情操作记录
     */
    public static class RecordHistory implements Parcelable{
        public Long historyId;//记录 ID
        public Integer step;//操作步骤 0—预定 1—驳回 2—通过 3—出库 4—取消预定 5—取消出库
        public Long operationDate;//操作时间
        public String handler;//操作人
        public String content;//操作内容

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeValue(this.historyId);
            dest.writeValue(this.step);
            dest.writeValue(this.operationDate);
            dest.writeString(this.handler);
            dest.writeString(this.content);
        }

        public RecordHistory() {
        }

        protected RecordHistory(Parcel in) {
            this.historyId = (Long) in.readValue(Long.class.getClassLoader());
            this.step = (Integer) in.readValue(Integer.class.getClassLoader());
            this.operationDate = (Long) in.readValue(Long.class.getClassLoader());
            this.handler = in.readString();
            this.content = in.readString();
        }

        public static final Creator<RecordHistory> CREATOR = new Creator<RecordHistory>() {
            @Override
            public RecordHistory createFromParcel(Parcel source) {
                return new RecordHistory(source);
            }

            @Override
            public RecordHistory[] newArray(int size) {
                return new RecordHistory[size];
            }
        };
    }

    /**
     * 修改预订单人员请求体
     */
    public static class EditReservationPersonRequest{
        public Long activityId;//预定单 ID
        public Long administrator;//仓库管理员 ID
        public Long supervisor;//主管 ID
        public Long reservePerson;//预定人 ID
    }


}
