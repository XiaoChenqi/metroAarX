package com.facilityone.wireless.maintenance.model;

/**
 * Created by peter.peng on 2018/11/16.
 * 计划性维护常量
 */

public interface MaintenanceConstant {

    //计划性维护工单状态
    int MAINTENANCE_WORKORDER_UNDO = 1;
    int MAINTENANCE_WORKORDER_DOING = 2;
    int MAINTENANCE_WORKORDER_FINISHED = 3;
    int MAINTENANCE_WORKORDER_MISS = 4;

    int TAG_MENU_MAINTENANCE_CONTENT = 0;//维护内容
    int TAG_MENU_MAINTENANCE_OBJECT = 1;//对象
    int TAG_MENU_MAINTENANCE_WORK_ORDER = 2;//维护工单


    long JANUARY = 1; //一月
    long FEBRUARY = 2; // 二月
    long MARCH = 3; // 三月
    long APRIL = 4; // 四月
    long MAY = 5; // 五月
    long JUNE = 6; // 六月
    long JULY = 7; // 七月
    long AUGUST = 8; // 八月
    long SEPTEMBER = 9; // 九月
    long OCTOBER = 10; // 十月
    long NOVEMBER = 11; // 十一月
    long DECEMBER = 12; // 十二月

    //维护工单状态
    int WORKORDER_STATUS_NONE = -1;                      // 无
    int WORKORDER_STATUS_CREATED = 0;                    // 已创建
    int WORKORDER_STATUS_PUBLISHED = 1;                  // 已发布
    int WORKORDER_STATUS_PROCESS = 2;                    // 处理中
    int WORKORDER_STATUS_SUSPENDED_GO = 3;               // 已暂停(继续工作)
    int WORKORDER_STATUS_TERMINATED = 4;                 // 已终止
    int WORKORDER_STATUS_COMPLETED = 5;                  // 已完成
    int WORKORDER_STATUS_VERIFIED = 6;                   // 已验证
    int WORKORDER_STATUS_ARCHIVED = 7;                   // 已存档
    int WORKORDER_STATUS_APPROVAL = 8;                   // 已待审批
    int WORKORDER_STATUS_SUSPENDED_NO = 9;               // 已暂停(不继续工作)

    //新工单状态
    int WORK_NEW_STATUS_DISPATCHING = 0;                // 待派工
    int WORK_NEW_STATUS_PROCESS = 1;                    // 处理中
    int WORK_NEW_STATUS_ARCHIVED_WAIT = 2;               // 待存档
    int WORK_NEW_STATUS_APPROVAL_WAIT = 3;              // 待审核
    int WORK_NEW_STATUS_ARCHIVED = 4;                   // 已存档
    int WORK_NEW_STATUS_DESTORY = 5;                    // 已作废

    //日历切换状态
    int CALENDAR_STATUS_SELECT_DAY = 0;//选中某一天
    int CALENDAR_STATUS_SWITCH_MONTH = 1;//切换月份
    int CALENDAR_STATUS_SWITCH_LAST_SELECT_DAY = 2;//切换到上次选中的日期

    //recyclerview的显示类型
    int TYPE_TITLE = 0;
    int TYPE_CONTENT = 1;
    int TYPE_EQUIPEMNT = 2;
    int TYPE_SPACE = 3;


    //二级菜单
    int MAINTENANCE_ONE = 0;          //维护日历
    int MAINTENANCE_TWO = 1;         //待处理维护工单
    int MAINTENANCE_THREE = 2;     //待派工维护工单
    int MAINTENANCE_FOUR = 3;           //待审批维护工单
    int MAINTENANCE_FIVE = 4;      //异常维护工单
    int MAINTENANCE_EIGHT = 5;         //待抽检维护工单
    int MAINTENANCE_SIX = 6;      //待存档维护工单
    int MAINTENANCE_SEVEN = 7;         //维护工单查询



    int ZERO = 0;
    int ONE = 1;
    int TWO = 2;
    int THREE = 3;
    int FOUR = 4;
    int FIVE = 5;
    int SIX = 6;
    int SEVEN = 7;

    int CHOICE_NO = 0; //默认状态
    int CHOICE_All = 1; //打开状态
    int CHOICE_UP = 2; //选中状态
    int CHOICE_DOWN = 3; //未选中状态
    int CHOICE_OFF = 4; //不可选中状态
    /**
     * 执行人状态
     */
    int STATUS_PERSONAL_UN_ACCEPT = 0;//未接单
    int STATUS_PERSONAL_ACCEPT = 1;   //已接单
    int STATUS_PERSONAL_BACK = 2;     //已退单
    int STATUS_PERSONAL_SUBMIT = 3;   //已提交


    /**
     * @Auther: karelie
     * @Date: 2021/8/27
     * @Infor: 工单标签
     */
    int APPLICATION_FOR_SUSPENSION = 0 ; //暂停申请中
    int PAUSE_STILL_WORKING = 1 ; //暂停（继续工作）
    int PAUSE_NOT_WORKING = 2 ; //暂停(不继续工作)
    int APPLICATION_VOID = 3 ; //作废申请中
    int STOP = 4 ; //终止


}
