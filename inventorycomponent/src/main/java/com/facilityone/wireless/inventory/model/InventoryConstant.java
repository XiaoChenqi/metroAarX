package com.facilityone.wireless.inventory.model;

/**
 * Created by peter.peng on 2018/11/26.
 * 库存常量
 */

public interface InventoryConstant {

    //菜单基础下标
    int INVENTORY_CREATE = 0;//新建物资
    int INVENTORY_IN = 1;//入库
    int INVENTORY_OUT = 2;//出库
    int INVENTORY_MOVE = 3;//移库
    int INVENTORY_CHECK = 4;//盘点
    int INVENTORY_RESERVE = 5;//库存预定
    int INVENTORY_MY = 6;//我的预定
    int INVENTORY_APPROVAL = 7;//库存审核
    int INVENTORY_QUERY = 8;//库存查询

    int INVENTORY_APPROVAL_WAIT = 9;//库存审核(待审核)
    int INVENTORY_APPROVALED = 10;//库存审核(已审核)

    //选择数据界面的类型
    int SELECT_STORAGE = 0;//选择仓库
    int SELECT_MATERIAL = 1;//入库、盘点选择物料
    int SELECT_MATERIAL_OUT = 2;//出库选择物料
    int SELECT_MATERIAL_MOVE = 3;//移库选择物料
    int SELECT_MATERIAL_RESERVE = 4;//物资预定选择物料
    int SELECT_PROVIDER = 5;//选择供应商
    int SELECT_SUPERVISOR = 6;//选择审批主管
    int SELECT_RECEIVING_PERSON = 7;//选择领用人
    int SELECT_ADMINISTRATOR = 8;//选择仓库管理员
    int SELECT_RESERVATION_PERSON = 9;//选择预订人

    //物资是否存在
    int NEVER_EXIST = 0;//系统中不存在
    int THIS_EXIST = 1;//本仓库中已经存在
    int OTHER_EXIST = 2;//其他仓库中存在，本仓库中不存在

    //批次
    int INVENTORY_BATCH_IN = 0;//入库批次
    int INVENTORY_BATCH_RESERVE_OUT = 1;//预定出库批次
    int INVENTORY_BATCH_DIRECT_OUT = 2;//直接出库批次
    int INVENTORY_BATCH_MOVE = 3;//移库库批次
    int INVENTORY_BATCH_CHECK = 4;//盘点批次
    int INVENTORY_INFO_BATCH_IN = 5;//入库批次(从物资详情进入)
    int INVENTORY_INFO_BATCH_OUT = 6;//出库批次(从物资详情进入)
    int INVENTORY_INFO_BATCH_MOVE = 7;//移库库批次(从物资详情进入)
    int INVENTORY_INFO_BATCH_CHECK = 8;//盘点批次(从物资详情进入)

    //库存预定状态
    int RESERVE_STATUS_VERIFY_WAIT = 0;// 未审核
    int RESERVE_STATUS_VERIFY_PASS = 1;// 通过（待出库）
    int RESERVE_STATUS_VERIFY_BACK = 2;// 取消（已驳回）
    int RESERVE_STATUS_DELIVERIED = 3;// 已出库
    int RESERVE_STATUS_CANCEL = 4;// 取消出库（仓库管理员取消）
    int RESERVE_STATUS_CANCEL_BOOK = 5;//取消预定（预定人取消）

    //预订单操作记录步骤
    int RECORD_STEP_RESERVE = 0;
    int RECORD_STEP_REJECT = 1;
    int RECORD_STEP_PASS = 2;
    int RECORD_STEP_OUT = 3;
    int RECORD_STEP_CANCEL_OUT = 4;
    int RECORD_STEP_CANCEL_RESERVE = 5;
    
    //预订记录查询类型
    int RESERVE_QUERY_APPROVAL_WAIT = 1;// 待审核的（待我审核的预定单记录）
    int RESERVE_QUERY_APPROVALED = 2;// 已审核的（我已审核的预定单记录）
    int RESERVE_QUERY_MY_BOOK = 3;// 我的预定（我提交的预定单记录）
    int RESERVE_QUERY_READY_OUT = 4;// 待出库（我管理的所有仓库下的审批通过的预定单）
    int RESERVE_QUERY_BACK = 5;// 待驳回（我提交并被审批不通过的）
    int RESERVE_QUERY_OVER_OUT = 6;// 待出库（我提交并出库的）

    //批次请求类型
    int BATCH_TYPE_ALL = 0;//所有批次
    int BATCH_TYPE_VALID = 1;//有效批次
    int BATCH_TYPE_CHECK = 2;//盘点批次

    //移库出库请求类型
    int INVENTORY_MATERIAL_DIRECT_OUT = 0;//直接出库
    int INVENTORY_MATERIAL_RESERVE_OUT = 1;//预定出库
    int INVENTORY_MATERIAL_MOVE = 2;//移库

    //库存审核请求类型
    int INVENTORY_APPROVAL_PASS = 0;//审核通过(主管审核)
    int INVENTORY_APPROVAL_NOT_PASS = 1;//审核不通过(主管审核)
    int INVENTORY_APPROVAL_CANCEL_OUT = 2;//取消出库（仓库管理员取消）
    int INVENTORY_APPROVAL_CANCEL_BOOK = 3;//取消预定（预定人取消）

    //物资预定添加物资界面类型
    int TYPE_MATERIAL_ADD = 0;
    int TYPE_MATERIAL_MODIFY = 1;

    //工单状态
    int WORK_STATUS_NONE = -1;                      // 无
    int WORK_STATUS_CREATED = 0;                    // 已创建
    int WORK_STATUS_PUBLISHED = 1;                  // 已发布
    int WORK_STATUS_PROCESS = 2;                    // 处理中
    int WORK_STATUS_SUSPENDED_GO = 3;               // 已暂停(继续工作)
    int WORK_STATUS_TERMINATED = 4;                 // 已终止
    int WORK_STATUS_COMPLETED = 5;                  // 已完成
    int WORK_STATUS_VERIFIED = 6;                   // 已验证
    int WORK_STATUS_ARCHIVED = 7;                   // 已存档
    int WORK_STATUS_APPROVAL = 8;                   // 已待审批
    int WORK_STATUS_SUSPENDED_NO = 9;               // 已暂停(不继续工作)

}
