package com.facilityone.wireless.maintenance.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.facilityone.wireless.a.arch.ec.module.AttachmentBean;
import com.facilityone.wireless.a.arch.ec.module.LocationBean;

import java.util.Comparator;
import java.util.List;

/**
 * Created by peter.peng on 2018/11/16.
 */

public class MaintenanceService {




    /**
     * 维护日历数据bean
     */
    public static class MaintenanceCalendarBean implements MultiItemEntity{

        public Long pmId;//计划id
        public String pmName;//计划性维护任务名称
        public Long pmtodoId;//计划任务 ID
        public Long dateTodo;//时间
        public Boolean genStatus;//是否生成了工单
        //1 — 未处理
        //2 — 处理中
        //3 — 已结束
        //4 — 遗漏
        public Integer status;//任务状态
        public List<Long> woIds;//关联工单ID数组
        public int type;//类型
        public String title;//标题



        @Override
        public int getItemType() {
            return type;
        }
    }


    /**
     * 计划性维护详情数据bean
     */
    public static class MaintenanceInfoBean implements Parcelable{
        public Long pmId;//计划 ID
        public String name;//计划名称
        public Integer status;//任务状态
        public Integer priority;//优先级
        public String influence;//	影响
        public String period;//周期
        public Long dateFirstTodo;//首次维护时间
        public Long dateNextTodo;//下次维护时间
        public String estimatedWorkingTime;//预估工作耗时
        public Long startTime;//开始时间
        public Long endTime;//完成时间
        public Boolean autoGenerateOrder;//是否自动生成工单
        public Boolean genStatus;//是否已生成工单
        public Integer ahead;//提前生成天数
        public List<Step> pmSteps;//计划步骤数组
        public List<Material> pmMaterials;//物料数组
        public List<Tool> pmTools;//工具数组
        public List<MaintenanceEquipment> equipments;//维护设备数组
        public List<Space> spaces;//空间位置数组
        public List<MaintenanceWorkOrder> workOrders;//关联工单数组
        public List<AttachmentBean> pictures;//图片 ID 数组

        protected MaintenanceInfoBean(Parcel in) {
            pmId = (Long) in.readValue(Long.class.getClassLoader());
            name = in.readString();
            status = (Integer) in.readValue(Integer.class.getClassLoader());
            priority = (Integer) in.readValue(Integer.class.getClassLoader());
            influence = in.readString();
            period = in.readString();
            dateFirstTodo = (Long) in.readValue(Long.class.getClassLoader());
            dateNextTodo = (Long) in.readValue(Long.class.getClassLoader());
            estimatedWorkingTime = in.readString();
            startTime = (Long) in.readValue(Long.class.getClassLoader());
            endTime = (Long) in.readValue(Long.class.getClassLoader());
            autoGenerateOrder = (Boolean) in.readValue(Boolean.class.getClassLoader());
            genStatus = (Boolean) in.readValue(Boolean.class.getClassLoader());
            ahead = (Integer) in.readValue(Integer.class.getClassLoader());
            pmSteps = in.createTypedArrayList(Step.CREATOR);
            pmMaterials = in.createTypedArrayList(Material.CREATOR);
            pmTools = in.createTypedArrayList(Tool.CREATOR);
            equipments = in.createTypedArrayList(MaintenanceEquipment.CREATOR);
            spaces = in.createTypedArrayList(Space.CREATOR);
            workOrders = in.createTypedArrayList(MaintenanceWorkOrder.CREATOR);
            pictures = in.createTypedArrayList(AttachmentBean.CREATOR);
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeValue(pmId);
            dest.writeString(name);
            dest.writeValue(status);
            dest.writeValue(priority);
            dest.writeString(influence);
            dest.writeString(period);
            dest.writeValue(dateFirstTodo);
            dest.writeValue(dateNextTodo);
            dest.writeString(estimatedWorkingTime);
            dest.writeValue(startTime);
            dest.writeValue(endTime);
            dest.writeValue(autoGenerateOrder);
            dest.writeValue(genStatus);
            dest.writeValue(ahead);
            dest.writeTypedList(pmSteps);
            dest.writeTypedList(pmMaterials);
            dest.writeTypedList(pmTools);
            dest.writeTypedList(equipments);
            dest.writeTypedList(spaces);
            dest.writeTypedList(workOrders);
            dest.writeTypedList(pictures);
        }

        public static final Creator<MaintenanceInfoBean> CREATOR = new Creator<MaintenanceInfoBean>() {
            @Override
            public MaintenanceInfoBean createFromParcel(Parcel in) {
                return new MaintenanceInfoBean(in);
            }

            @Override
            public MaintenanceInfoBean[] newArray(int size) {
                return new MaintenanceInfoBean[size];
            }
        };
    }

    /**
     * 步骤
     */
    public static class Step implements Parcelable{
        public Long pmSId;//步骤 ID
        public String comment;//步骤完成说明
        public Boolean finished;//是否已完成
        public String step;//步骤内容
        public String workTeamName;//相关工作组名称
        public Integer sort;//步骤序号
        public Long woId;//步骤关联工单 ID

        protected Step(Parcel in) {
            pmSId = (Long) in.readValue(Long.class.getClassLoader());
            comment = in.readString();
            finished = (Boolean) in.readValue(Boolean.class.getClassLoader());
            step = in.readString();
            workTeamName = in.readString();
            sort = (Integer) in.readValue(Integer.class.getClassLoader());
            woId = (Long) in.readValue(Long.class.getClassLoader());
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeValue(pmSId);
            dest.writeString(comment);
            dest.writeValue(finished);
            dest.writeString(step);
            dest.writeString(workTeamName);
            dest.writeValue(sort);
            dest.writeValue(woId);
        }

        public static final Creator<Step> CREATOR = new Creator<Step>() {
            @Override
            public Step createFromParcel(Parcel in) {
                return new Step(in);
            }

            @Override
            public Step[] newArray(int size) {
                return new Step[size];
            }
        };
    }

    /**
     * 物料
     */
    public static class Material implements Parcelable{
        public Long pmmId;//计划物料 ID
        public Long materialId;//库存 ID
        public String name;//物料名称
        public String brand;//品牌
        public String model;//型号
        public Double price;//单价
        public String unit;//单位
        public Float amount;//数量
        public String comment;//说明
        public String mpComment;

        protected Material(Parcel in) {
            pmmId = (Long) in.readValue(Long.class.getClassLoader());
            materialId = (Long) in.readValue(Long.class.getClassLoader());
            name = in.readString();
            brand = in.readString();
            model = in.readString();
            price = (Double) in.readValue(Integer.class.getClassLoader());
            unit = in.readString();
            amount = (Float) in.readValue(Float.class.getClassLoader());
            comment = in.readString();
            mpComment = in.readString();
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeValue(pmmId);
            dest.writeValue(materialId);
            dest.writeString(name);
            dest.writeString(brand);
            dest.writeString(model);
            dest.writeValue(price);
            dest.writeString(unit);
            dest.writeValue(amount);
            dest.writeString(comment);
            dest.writeString(mpComment);
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
    }

    /**
     * 工具
     */
    public static class Tool implements Parcelable{
        public Long pmtId;//计划工具 ID
        public String name;//工具名称
        public String model;//型号
        public String unit;//单位
        public Float amount;//数量
        public String comment;//说明
        public String brand;//品牌

        protected Tool(Parcel in) {
            pmtId = (Long) in.readValue(Long.class.getClassLoader());
            name = in.readString();
            model = in.readString();
            unit = in.readString();
            amount = (Float) in.readValue(Float.class.getClassLoader());
            comment = in.readString();
            brand = in.readString();
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeValue(pmtId);
            dest.writeString(name);
            dest.writeString(model);
            dest.writeString(unit);
            dest.writeValue(amount);
            dest.writeString(comment);
            dest.writeString(brand);
        }

        public static final Creator<Tool> CREATOR = new Creator<Tool>() {
            @Override
            public Tool createFromParcel(Parcel in) {
                return new Tool(in);
            }

            @Override
            public Tool[] newArray(int size) {
                return new Tool[size];
            }
        };
    }

    /**
     * 维护设备
     */
    public static class MaintenanceEquipment implements Parcelable{
        public Long eqId;//计划工具 ID
        public String name;//设备名称
        public String code;//设备编号
        public Long sysType;//设备类型
        public String eqSystemName;//设备分类名称
        public String location;//位置

        protected MaintenanceEquipment(Parcel in) {
            eqId = (Long) in.readValue(Long.class.getClassLoader());
            name = in.readString();
            code = in.readString();
            sysType = (Long) in.readValue(Long.class.getClassLoader());
            eqSystemName = in.readString();
            location = in.readString();
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeValue(eqId);
            dest.writeString(name);
            dest.writeString(code);
            dest.writeValue(sysType);
            dest.writeString(eqSystemName);
            dest.writeString(location);
        }

        public static final Creator<MaintenanceEquipment> CREATOR = new Creator<MaintenanceEquipment>() {
            @Override
            public MaintenanceEquipment createFromParcel(Parcel in) {
                return new MaintenanceEquipment(in);
            }

            @Override
            public MaintenanceEquipment[] newArray(int size) {
                return new MaintenanceEquipment[size];
            }
        };
    }

    /**
     * 空间位置
     */
    public static class Space implements Parcelable{
        public String location;//空间位置
        public Long pmsId;//位置id

        protected Space(Parcel in) {
            location = in.readString();
            pmsId = (Long) in.readValue(Long.class.getClassLoader());
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(location);
            dest.writeValue(pmsId);
        }

        public static final Creator<Space> CREATOR = new Creator<Space>() {
            @Override
            public Space createFromParcel(Parcel in) {
                return new Space(in);
            }

            @Override
            public Space[] newArray(int size) {
                return new Space[size];
            }
        };
    }

    public static class MaintenanceObject extends MaintenanceCalendarBean implements MultiItemEntity{
        public String title;
        public String name;//设备名称
        public String code;//设备编号
        public Long sysType;//设备类型
        public String eqSystemName;//设备分类名称
        public String location;//空间位置
        public int type;//类型

        @Override
        public int getItemType() {
            return type;
        }
    }
    /**
     * 关联工单
     */
    public static class MaintenanceWorkOrder implements Parcelable{
        public Long woId;//工单 ID
        public String code;//工单编号
        public Long priorityId;//优先级 ID
        public String applicantName;//联系人名名字
        public String applicantPhone;//联系人电话
        public String location;//位置
        public Integer status;//工单状态
        public Long createDateTime;//创建时间
        public Long actualCompletionDateTime;//完成时间
        public String woDescription;//工单描述
        public String workContent;//工作内容
        public String serviceTypeName;//服务类型名称
        public Integer currentLaborerStatus;//当前执行人的状态
        public Integer newStatus; //新的工单状态
        public Integer tag ; //工单标签

        protected MaintenanceWorkOrder(Parcel in) {
            woId = (Long) in.readValue(Long.class.getClassLoader());
            code = in.readString();
            priorityId = (Long) in.readValue(Long.class.getClassLoader());
            applicantName = in.readString();
            applicantPhone = in.readString();
            location = in.readString();
            status = (Integer) in.readValue(Integer.class.getClassLoader());
            createDateTime = (Long) in.readValue(Long.class.getClassLoader());
            actualCompletionDateTime = (Long) in.readValue(Long.class.getClassLoader());
            woDescription = in.readString();
            workContent = in.readString();
            serviceTypeName = in.readString();
            currentLaborerStatus = (Integer) in.readValue(Integer.class.getClassLoader());
            newStatus = (Integer) in.readValue(Integer.class.getClassLoader());
            tag = (Integer) in.readValue(Integer.class.getClassLoader());
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeValue(woId);
            dest.writeString(code);
            dest.writeValue(priorityId);
            dest.writeString(applicantName);
            dest.writeString(applicantPhone);
            dest.writeString(location);
            dest.writeValue(status);
            dest.writeValue(createDateTime);
            dest.writeValue(actualCompletionDateTime);
            dest.writeString(woDescription);
            dest.writeString(workContent);
            dest.writeString(serviceTypeName);
            dest.writeValue(currentLaborerStatus);
            dest.writeValue(newStatus);
            dest.writeValue(tag);
        }

        public static final Creator<MaintenanceWorkOrder> CREATOR = new Creator<MaintenanceWorkOrder>() {
            @Override
            public MaintenanceWorkOrder createFromParcel(Parcel in) {
                return new MaintenanceWorkOrder(in);
            }

            @Override
            public MaintenanceWorkOrder[] newArray(int size) {
                return new MaintenanceWorkOrder[size];
            }
        };
    }

    public static class ConditionBean {
        public String woCode;
        public String woDescription;
        public List<Long> priority;
        public List<Long> status;
        public List<Long> newStatus;
        public List<Long> tag;
        public Long typeId;
        public Long startDateTime;
        public Long endDateTime;
        public LocationBean location;
        public Long emId;
        public String planName;
        public List<Long>period;
        public Long locationId; //站点ID
        public Long specialty; //专业Id

    }

    //工单详情
    public static class WorkorderInfoBean {
        public Long woId; // 工单ID
        public Long approvalId; // 审批ID
        public String code; // 工单号
        public String serviceTypeName; // 服务类型
        public Integer status; // 工单状态
        public Integer newStatus; // 工单状态
        public String pfmCode;//dtz 专用
        public Long priorityId; // 工单优先级id
        public String priorityName; // 工单优先级
        public String woDescription; // 工单描述
        public Long createDateTime; // 工单创建时间
        public Long actualArrivalDateTime; // 工单开始时间
        public Long actualCompletionDateTime; // 工单完成时间
        public String actualWorkingTime; // 实际工作时间
        public String location; // 地理位置
        public String applicantName; // 工单请求者名称
        public String applicantPhone; // 工单请求者的电话
        /**
         * @Auther: karelie
         * @Date: 2021/8/12
         * @Infor: 补充部分字段
         */
        public Long orgId; //部门Id
        public Long serviceTypeId; //服务类型Id
        public Long flowId; //流程Id
        public Integer tag; // 工单标签
        public String workDoneReminder; //完成工作提醒

        /* 旧版字段 */
        public String laborer; // 执行人
        public String workContent; // 工作内容
        public Integer currentLaborerStatus;//当前用户工单状态
        public Long approvalSubmitDateTime; // 审批时间
        //抢单相关字段
        public Integer grabType;//抢单类型 0-普通工单  1-抢单工单
        public Integer grabStatus;//当前请求人对该工单的抢单状态
        public Integer category;//工单类型  0 — CM工单    1 — ZM工单   2 — PM工单
        public Integer type; // 工单类型
        public LocationBean locationId;//位置ID类
        public String organizationName; // 部门
        public Long workTeamId; // 工单组ID
        public Long estimateStartTime;//估计开始时间
        public Long estimateEndTime;//估计到达时间
        public Long reserveStartTime;//预约开始时间
        public Long reserveEndTime;//预约到达时间
        public String customerSignImgId; // 客户签名
        public String supervisorSignImgId; // 主管签名
        public List<String> requirementPictures; // 图片
        public List<String> requirementAudios; // 音频
        public List<String> requirementVideos; // 视频
        public List<String> requirementShortVideos; // 短视频
        public List<HistoriesBean> histories; //历史记录
        public List<ApprovalsBean> approvals; // 审批内容键值对
        public List<WorkOrderEquipmentsBean> workOrderEquipments; // 设备
        public List<WorkOrderLocationsBean> workOrderLocations;//空间位置
        public List<WorkorderLaborerBean> workOrderLaborers; // 执行人
        public String sendWorkContent;//派发工作内容
        public List<WorkOrderToolsBean> workOrderTools; // 工具
        public List<ChargesBean> charges;//收费项
        public PmInfoBean pmInfo; // 计划性维护
        public List<StepsBean> steps; // 计划性维护信息 步骤
        public List<Integer> currentRoles; //当前角色权限
        public List<AttachmentBean> attachment;//附件
        public List<Long> approvers;//审批人列表
        public Boolean needScan; //故障设备是否需要扫描
        public List<PaymentsBean> payments;
        public List<String> pictures; // 图片
        //四运需求
        public List<WorkOrderEquipmentsBean> equipmentSystemName; //故障设备名称
        public List<RelatedOrder> relatedOrder; //关联工单
        public String failueDescription;//故障描述
        public List<PmSpaceBean> pmPositions; //空间位置信息
        //11.30
        //TODO 故障对象名称 ID 故障原因ID 具体故障原因输入
        public Long componentId; //故障对象Id
        public String componentName; //故障名称
        public Long causeId; //故障原因
        public String causeOther; //故障原因ID——其他原因时显示具体原因

        //2021-12-8
        public Boolean needSample;   //是否需要抽检

        public Boolean isBunchingOrder;//	是否为聚群工单
    }

    public static class HistoriesBean implements Parcelable {
        public Long historyId;
        public Integer step;
        public Long operationDate;
        public String handler;
        public String handlerImgId;
        public String content;
        public List<String> pictures;
        public List<AttachmentBean> attachment;

        public HistoriesBean() {
        }

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
            dest.writeString(this.handlerImgId);
            dest.writeString(this.content);
            dest.writeStringList(this.pictures);
            dest.writeTypedList(this.attachment);
        }

        protected HistoriesBean(Parcel in) {
            this.historyId = (Long) in.readValue(Long.class.getClassLoader());
            this.step = (Integer) in.readValue(Integer.class.getClassLoader());
            this.operationDate = (Long) in.readValue(Long.class.getClassLoader());
            this.handler = in.readString();
            this.handlerImgId = in.readString();
            this.content = in.readString();
            this.pictures = in.createStringArrayList();
            this.attachment = in.createTypedArrayList(AttachmentBean.CREATOR);
        }

        public static final Creator<HistoriesBean> CREATOR = new Creator<HistoriesBean>() {
            @Override
            public HistoriesBean createFromParcel(Parcel source) {
                return new HistoriesBean(source);
            }

            @Override
            public HistoriesBean[] newArray(int size) {
                return new HistoriesBean[size];
            }
        };
    }

    public static class ApprovalsBean {
        public Long approvalId;
        public List<ApprovalResultsBean> approvalResults;
        public List<ApprovalContentBean> approvalContent;
    }

    public static class ApprovalResultsBean {
        public Long approverId;
        public String arDescription;
        public String approver;
        public Integer result;
    }

    public static class ApprovalContentBean {
        public String name;
        public String value;
    }

    public static class WorkOrderEquipmentsBean implements Parcelable {
        public Long equipmentId; // 设备ID
        public String equipmentCode; // 设备编码
        public String equipmentName; // 设备名称
        public String location; // 设备安装位置
        public String equipmentSystemName; // 设备所属系统
        public String failureDesc; // 故障描述
        public String repairDesc; // 维修描述
        public Integer finished;//设备维保状态
        public boolean isLocal;//本地添加

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeValue(this.equipmentId);
            dest.writeString(this.equipmentCode);
            dest.writeString(this.equipmentName);
            dest.writeString(this.location);
            dest.writeString(this.equipmentSystemName);
            dest.writeString(this.failureDesc);
            dest.writeString(this.repairDesc);
            dest.writeValue(this.finished);
            dest.writeByte(this.isLocal ? (byte) 1 : (byte) 0);
        }

        public WorkOrderEquipmentsBean() {
        }

        protected WorkOrderEquipmentsBean(Parcel in) {
            this.equipmentId = (Long) in.readValue(Long.class.getClassLoader());
            this.equipmentCode = in.readString();
            this.equipmentName = in.readString();
            this.location = in.readString();
            this.equipmentSystemName = in.readString();
            this.failureDesc = in.readString();
            this.repairDesc = in.readString();
            this.finished = (Integer) in.readValue(Integer.class.getClassLoader());
            this.isLocal = in.readByte() != 0;
        }

        public static final Creator<WorkOrderEquipmentsBean> CREATOR = new Creator<WorkOrderEquipmentsBean>() {
            @Override
            public WorkOrderEquipmentsBean createFromParcel(Parcel source) {
                return new WorkOrderEquipmentsBean(source);
            }

            @Override
            public WorkOrderEquipmentsBean[] newArray(int size) {
                return new WorkOrderEquipmentsBean[size];
            }
        };
    }

    public static class WorkOrderLocationsBean implements Parcelable {
        public Long recordId;
        public LocationBean location;
        public String locationName;
        public String repairDesc;
        public Boolean finished;


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeValue(this.recordId);
            dest.writeParcelable(this.location, flags);
            dest.writeString(this.locationName);
            dest.writeString(this.repairDesc);
            dest.writeValue(this.finished);
        }

        public WorkOrderLocationsBean() {
        }

        protected WorkOrderLocationsBean(Parcel in) {
            this.recordId = (Long) in.readValue(Long.class.getClassLoader());
            this.location = in.readParcelable(LocationBean.class.getClassLoader());
            this.locationName = in.readString();
            this.repairDesc = in.readString();
            this.finished = (Boolean) in.readValue(Boolean.class.getClassLoader());
        }

        public static final Creator<WorkOrderLocationsBean> CREATOR = new Creator<WorkOrderLocationsBean>() {
            @Override
            public WorkOrderLocationsBean createFromParcel(Parcel source) {
                return new WorkOrderLocationsBean(source);
            }

            @Override
            public WorkOrderLocationsBean[] newArray(int size) {
                return new WorkOrderLocationsBean[size];
            }
        };
    }

    public static class WorkorderLaborerGroupBean {
        public Long wtId;
        public String name;
        public List<WorkorderLaborerBean> members;
    }

    public static class WorkorderLaborerBean implements Parcelable {
        public Long laborerId; // 执行人ID
        public Long woLaborerId; //该工单执行人ID
        public String laborer; // 执行人名称
        public String positionName; // 岗位名称
        public String phone; // 执行人电话
        public Long actualArrivalDateTime; // 实际到场时间
        public Long actualCompletionDateTime; // 实际完成时间
        public String actualWorkingTime; // 实际工作时间
        public Integer status; // 该执行人该工单的状态(添加执行人列表中的意思 执行人在岗状态0 — 离岗 1 — 在岗 2 — 没有参与考勤 )
        public Boolean responsible; // 是否是负责人
        public boolean canOpt;

        public String name; // 执行人名称
        public String number;
        public Integer woNumber;//该执行人未完成工单数量
        public Long emId; // 执行人ID
        public Long approverId; // 审批人 ID
        public boolean checked;
        public boolean leader;
        public transient String namePinyin;
        public transient String nameFirstLetters;
        public transient int start;//搜索匹配的开始位置(name 中的位置顺序)
        public transient int end;//搜索匹配的结束位置(name 中的位置顺序)


        public WorkorderLaborerBean() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeValue(this.laborerId);
            dest.writeValue(this.woLaborerId);
            dest.writeString(this.laborer);
            dest.writeString(this.positionName);
            dest.writeString(this.phone);
            dest.writeValue(this.actualArrivalDateTime);
            dest.writeValue(this.actualCompletionDateTime);
            dest.writeString(this.actualWorkingTime);
            dest.writeValue(this.status);
            dest.writeValue(this.responsible);
            dest.writeByte(this.canOpt ? (byte) 1 : (byte) 0);
            dest.writeString(this.name);
            dest.writeString(this.number);
            dest.writeValue(this.woNumber);
            dest.writeValue(this.emId);
            dest.writeValue(this.approverId);
            dest.writeByte(this.checked ? (byte) 1 : (byte) 0);
            dest.writeByte(this.leader ? (byte) 1 : (byte) 0);
            dest.writeString(this.namePinyin);
            dest.writeString(this.nameFirstLetters);
            dest.writeInt(this.start);
            dest.writeInt(this.end);
        }

        protected WorkorderLaborerBean(Parcel in) {
            this.laborerId = (Long) in.readValue(Long.class.getClassLoader());
            this.woLaborerId = (Long) in.readValue(Long.class.getClassLoader());
            this.laborer = in.readString();
            this.positionName = in.readString();
            this.phone = in.readString();
            this.actualArrivalDateTime = (Long) in.readValue(Long.class.getClassLoader());
            this.actualCompletionDateTime = (Long) in.readValue(Long.class.getClassLoader());
            this.actualWorkingTime = in.readString();
            this.status = (Integer) in.readValue(Integer.class.getClassLoader());
            this.responsible = (Boolean) in.readValue(Boolean.class.getClassLoader());
            this.canOpt = in.readByte() != 0;
            this.name = in.readString();
            this.number = in.readString();
            this.woNumber = (Integer) in.readValue(Integer.class.getClassLoader());
            this.emId = (Long) in.readValue(Long.class.getClassLoader());
            this.approverId = (Long) in.readValue(Long.class.getClassLoader());
            this.checked = in.readByte() != 0;
            this.leader = in.readByte() != 0;
            this.namePinyin = in.readString();
            this.nameFirstLetters = in.readString();
            this.start = in.readInt();
            this.end = in.readInt();
        }

        public static final Creator<WorkorderLaborerBean> CREATOR = new Creator<WorkorderLaborerBean>() {
            @Override
            public WorkorderLaborerBean createFromParcel(Parcel source) {
                return new WorkorderLaborerBean(source);
            }

            @Override
            public WorkorderLaborerBean[] newArray(int size) {
                return new WorkorderLaborerBean[size];
            }
        };
    }

    public static class WorkOrderToolsBean implements Parcelable {
        public Long toolId; // 物料或工具ID
        public String name; // 物料或工具名称
        public String brand; // 物料或工具品牌
        public String model; // 型号
        public Integer amount; // 物料或工具个数
        public String unit; // 物料或工具单位
        public Double cost; // 物料或工具费用
        public String comment; // 物料或工具备注


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeValue(this.toolId);
            dest.writeString(this.name);
            dest.writeString(this.brand);
            dest.writeString(this.model);
            dest.writeValue(this.amount);
            dest.writeString(this.unit);
            dest.writeValue(this.cost);
            dest.writeString(this.comment);
        }

        public WorkOrderToolsBean() {
        }

        protected WorkOrderToolsBean(Parcel in) {
            this.toolId = (Long) in.readValue(Long.class.getClassLoader());
            this.name = in.readString();
            this.brand = in.readString();
            this.model = in.readString();
            this.amount = (Integer) in.readValue(Integer.class.getClassLoader());
            this.unit = in.readString();
            this.cost = (Double) in.readValue(Double.class.getClassLoader());
            this.comment = in.readString();
        }

        public static final Creator<WorkOrderToolsBean> CREATOR = new Creator<WorkOrderToolsBean>() {
            @Override
            public WorkOrderToolsBean createFromParcel(Parcel source) {
                return new WorkOrderToolsBean(source);
            }

            @Override
            public WorkOrderToolsBean[] newArray(int size) {
                return new WorkOrderToolsBean[size];
            }
        };
    }

    public static class PmInfoBean {
        public Long pmId; // 计划性维护ID
        public String name; // 计划性维护名称
        public String influence; // 计划性维护影响
        public Long priority; // 优先级
        public Boolean eqCountAccord; //是否需求输入设备数量
        public String mattersNeedingAttention; //注意事项
    }

    public static class StepsBean implements Comparator<StepsBean>, Parcelable {
        public Long stepId;//步骤ID
        public String step;//步骤
        public Integer sort;//序号
        public Boolean finished;//是否完成
        public String comment;//工作描述
        public Long workTeamId;//工作组ID
        public String workTeamName;//工作组名称
        public boolean isOperator;//是否为执行人，用于item是否可编辑
        public List<String> photos;// 步骤图片
        public Boolean accordText; //是否需要输入框
        public Boolean stepStatus; //判断步骤是否完成
        public Integer eqNumber ;//设备数量
        public String enterText; //输入内容

        public boolean isOperator() {
            return isOperator;
        }

        public void setIsOperator(boolean isOperator) {
            this.isOperator = isOperator;
        }

        @Override
        public int compare(StepsBean lhs, StepsBean rhs) {
            return lhs.sort >= rhs.sort ? lhs.sort : rhs.sort;
        }


        public StepsBean() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeValue(this.stepId);
            dest.writeString(this.step);
            dest.writeValue(this.sort);
            dest.writeValue(this.finished);
            dest.writeString(this.comment);
            dest.writeValue(this.workTeamId);
            dest.writeString(this.workTeamName);
            dest.writeByte(this.isOperator ? (byte) 1 : (byte) 0);
            dest.writeStringList(this.photos);
            dest.writeBoolean(this.accordText);
            dest.writeBoolean(this.stepStatus);
            dest.writeValue(this.eqNumber);
            dest.writeString(this.enterText);
        }

        protected StepsBean(Parcel in) {
            this.stepId = (Long) in.readValue(Long.class.getClassLoader());
            this.step = in.readString();
            this.sort = (Integer) in.readValue(Integer.class.getClassLoader());
            this.finished = (Boolean) in.readValue(Boolean.class.getClassLoader());
            this.comment = in.readString();
            this.workTeamId = (Long) in.readValue(Long.class.getClassLoader());
            this.workTeamName = in.readString();
            this.isOperator = in.readByte() != 0;
            this.photos = in.createStringArrayList();
            this.accordText = in.readBoolean();
            this.stepStatus = in.readBoolean();
            this.eqNumber = (Integer) in.readValue(Integer.class.getClassLoader());
            this.enterText = in.readString();
        }

        public static final Creator<StepsBean> CREATOR = new Creator<StepsBean>() {
            @Override
            public StepsBean createFromParcel(Parcel source) {
                return new StepsBean(source);
            }

            @Override
            public StepsBean[] newArray(int size) {
                return new StepsBean[size];
            }
        };
    }

    public static class PaymentsBean implements Parcelable {
        public Long paymentId; //缴费单 ID
        public String code;
        public Long createDateTime;
        public String location;
        public Integer status;
        public String customer;//缴费客户
        public String cost;//	缴费金额

        protected PaymentsBean(Parcel in) {
            if (in.readByte() == 0) {
                paymentId = null;
            } else {
                paymentId = in.readLong();
            }
            code = in.readString();
            if (in.readByte() == 0) {
                createDateTime = null;
            } else {
                createDateTime = in.readLong();
            }
            location = in.readString();
            if (in.readByte() == 0) {
                status = null;
            } else {
                status = in.readInt();
            }
            customer = in.readString();
            cost = in.readString();
        }

        public static final Creator<PaymentsBean> CREATOR = new Creator<PaymentsBean>() {
            @Override
            public PaymentsBean createFromParcel(Parcel in) {
                return new PaymentsBean(in);
            }

            @Override
            public PaymentsBean[] newArray(int size) {
                return new PaymentsBean[size];
            }
        };

        public PaymentsBean() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            if (paymentId == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeLong(paymentId);
            }
            dest.writeString(code);
            if (createDateTime == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeLong(createDateTime);
            }
            dest.writeString(location);
            if (status == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeInt(status);
            }
            dest.writeString(customer);
            dest.writeString(cost);
        }
    }

    public static class ChargesBean implements Parcelable {
        public Long chargeId; //收费项ID
        public String name; //收费项名称
        public Double amount; //收费金额
        public Double tax; //收费税率


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeValue(this.chargeId);
            dest.writeString(this.name);
            dest.writeValue(this.amount);
            dest.writeValue(this.tax);
        }

        public ChargesBean() {
        }

        protected ChargesBean(Parcel in) {
            this.chargeId = (Long) in.readValue(Long.class.getClassLoader());
            this.name = in.readString();
            this.amount = (Double) in.readValue(Double.class.getClassLoader());
            this.tax = (Double) in.readValue(Double.class.getClassLoader());
        }

        public static final Creator<ChargesBean> CREATOR = new Creator<ChargesBean>() {
            @Override
            public ChargesBean createFromParcel(Parcel source) {
                return new ChargesBean(source);
            }

            @Override
            public ChargesBean[] newArray(int size) {
                return new ChargesBean[size];
            }
        };
    }

    public static class RelatedOrder {
        public Long woId; // 工单Id
        public String code; //工单编码
    }


    public static class PmSpaceBean implements Parcelable{
        public Long spotId;
        public String blName; //点位名称
        public String name; //名称
        public Long woId;
        public Long updateDate;
        public String nfcTag; //点位信息
        public Long emId; //匹配人员ID
        public String emName; //执行人

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeValue(this.spotId);
            parcel.writeString(this.blName);
            parcel.writeString(this.name);
            parcel.writeValue(this.woId);
            parcel.writeValue(this.updateDate);
            parcel.writeString(this.nfcTag);
            parcel.writeValue(this.emId);
            parcel.writeString(this.emName);
        }

        public PmSpaceBean() {
        }

        protected PmSpaceBean(Parcel in) {
            this.spotId = (Long) in.readValue(Long.class.getClassLoader());
            this.blName = in.readString();
            this.name = in.readString();
            this.woId = (Long) in.readValue(Long.class.getClassLoader());
            this.updateDate = (Long) in.readValue(Long.class.getClassLoader());
            this.nfcTag = in.readString();
            this.emId = (Long) in.readValue(Long.class.getClassLoader());
            this.emName = in.readString();
        }

        public static final Creator<PmSpaceBean> CREATOR = new Creator<PmSpaceBean>() {
            @Override
            public PmSpaceBean createFromParcel(Parcel source) {
                return new PmSpaceBean(source);
            }

            @Override
            public PmSpaceBean[] newArray(int size) {
                return new PmSpaceBean[size];
            }
        };
    }

}
