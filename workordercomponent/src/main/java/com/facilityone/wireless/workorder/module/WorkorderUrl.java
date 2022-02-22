package com.facilityone.wireless.workorder.module;

/**
 * Author：gary
 * Email: xuhaozv@163.com
 * description:工单url
 * Date: 2018/7/4 下午5:38
 */
public interface WorkorderUrl {
    //待处理工单
    String WORKORDER_LIST_UNDO_URL = "/m/v1/workorder/undo";
    //待派工工单
    String WORKORDER_LIST_DISPATCH_URL = "/m/v1/workorder/undispatch";
    //待审核工单
    String WORKORDER_LIST_APPROVAL_URL = "/m/v1/workorder/wos/unapproval";
    //待存档工单
    String WORKORDER_LIST_TO_CLOSED_URL = "/m/v1/workorder/to-be-closed";
    //异常工单
    String WORKORDER_LIST_ABNOMAL_URL = "/m/v1/workorder/exception";
    //工单查询
    String WORKORDER_LIST_TO_QUERY_URL = "/m/v1/workorder/hquery";
    //我的报障
    String WORKORDER_LIST_TO_MY_QUERY_URL = "/m/v2/workorder/hquery";
    //创建工单
    String WORKORDER_CREATE_URL = "/m/v2/servicecenter/submit";
    //工单详情
    String WORKORDER_INFO_URL = "/m/v4/workorder/wos/detail";
    //工单执行人工作时间保存
    String WORKORDER_LABORER_SAVE_TIME_URL = "/m/v1/workorder/save/laborer";
    //工单执行人
    String WORKORDER_LABORER_LIST_URL = "/m/v2/workteams/query";
    //派工
    String WORKORDER_OPT_DISPATCH_URL = "/m/v1/workorder/wos/dispatch";
    //工单通用操作
    String WORKORDER_OPT_COMMON_URL = "/m/v2/workorder/wos/operate";
    //审批人
    String WORKORDER_OPT_APPROVALS_URL = "/m/v1/workorder/approvers";
    //审批申请
    String WORKORDER_OPT_APPROVAL_URL = "/m/v1/workorder/approvals/request";
    //审批
    String WORKORDER_OPT_APPROVAL_V_URL = "/m/v1/workorder/wos/approval";
    //填写工作内容
    String WORKORDER_PROCESS_INPUT_URL = "/m/v1/workorder/save/workcontent";
    //签字上传
    String WORKORDER_SIGNATURE_UPLOAD_URL = "/m/v1/workorder/sign";
    //编辑故障设备
    String WORKORDER_EDITOR_DEVICE_URL = "/m/v1/workorder/save/equipment";
    //编辑工具
    String WORKORDER_EDITOR_TOOL_URL = "/m/v1/workorder/save/tool";
    //编辑收取明细
    String WORKORDER_EDITOR_CHARGE_URL = "/m/v1/workorder/save/charge";
    //维护步骤编辑
    String WORKORDER_EDITOR_STEP_URL = "/m/v1/workorder/save/step";
    //空间位置编辑
    String WORKORDER_EDITOR_SPACE_URL = "/m/v1/workorder/update/location";
    //获取工单物资预定记录
    String WORKORDER_RESERVE_RECORD_LIST_URL = "/m/v1/workorder/materialReservation";


     /**
      * @Auther: karelie
      * @Date: 2021/8/13
      * @Infor: 四运定制开发
      */
     //新派工单
    String NEW_ORDER_CREATE = "/m/v1/workorder/redispatch";
    //申请作废
    String INVALID_ORDER_POST = "/m/v1/workorder/operate/request/invalid";
    //暂停工单
    String WORKORDER_OPT_PAUSE_URL="/m/v1/workorder/operate/request/pause";
    //原因列表
    String WORKORDER_REASON_URL="/m/v1/workorder/reason/list";
    //异常工单审批
    String WORKORDER_OPT_EXCEPTION_APPROVAL_V_URL = "/m/v1/workorder/exception/approve";
    //批量派单
    String MAINTENANCE_ORDER_DEISPATCH = "/m/v1/workorder/operate/dispatch/batch";
    //最后一次签到记录
    String WORKORDER_ATTENDANCE_LAST = "/m/v1/user/attendance/last";
    //我的签到记录
    String WORKORDER_ATTENDANCE_LIST = "/m/v1/user/attendance/list";
    //扫一扫签到
    String WORKORDER_ATTENDANCE_OPT = "/m/v1/user/attendance/operate";
    //异常审批
    String WORKORDER_OPT_APPROVAL_V_ERROR_URL = "/m/v1/workorder/exception/approve";
    //查询最短完成时间
    String QUERY_SHORTEST_TIME = "/m/v1/workorder/ppm/countdown/";
    //执行最短完成时间
    String DO_SHORTEST_TASK = "/m/v1/workorder/ppm/addCountdown";

    //NFC添加操作记录
    String SIGNIN_NFC_ADD = "/m/v1/workorder/signIn/nfc";
    //判断NFC是否能执行
    String NFC_CAN_DO = "/m/v1/workorder/validation/nfc";
    //判断维护工单处理完成NFC是否全部匹配
    String COMPLETE_PREMISSION = "/m/v1/workorder/completeSubmission";

    //根据工单ID获取抽检模板数据。
    String SAMPLE_TEMPLATE = "/m/v1/sample/sampleByWoId";


}
