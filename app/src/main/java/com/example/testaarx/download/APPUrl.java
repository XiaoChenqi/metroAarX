package com.example.testaarx.download;

/**
 * Author：gary
 * Email: xuhaozv@163.com
 * description:app项目的url
 * Date: 2018/5/29 下午5:03
 */
public interface APPUrl {
    //项目列表
    String PROJECT_LIST_URL = "/m/v3/project/projectlist";
    //项目角标
    String PROJECT_INDEX_URL = "/m/v1/message/amount";
    //消息概况
    String MESSAGE_SUMMARY_LIST_URL = "/m/v1/message/summary";
    //消息概况
    String MESSAGE_COUNT_URL = "/m/v1/message/amount";
    //消息列表
    String MESSAGE_LIST_URL = "/m/v3/message/query";
    //删除消息
    String MESSAGE_DELETE_URL = "/m/v1/message/delete";
    //标记已读
    String MESSAGE_TO_READ_SINGLE_URL = "/m/v1/message/read";
    //所有或某个类型全部已读
    String MESSAGE_TO_READ_ALL_URL = "/m/v1/message/readall";
    //离线数据更新的状态
    String APP_OFFLINE_DATA_STATUS_URL = "/m/v1/common/data/update";
    //获取有报表的项目id
    String PROJECT_REQUEST_CHART_URL = "/m/v3/project/projectlist/chart";
    //意见反馈图片上传地址
    String APP_PHOTO_URL = "/file/newFile";
    //意见反馈
    String USER_FEEDBACK_URL = "/feedback";
    //我得反馈
    String APP_FEEDBACK_MINE_URL = "/feedback/mobile/query/";
    //服务器id
    String APP_SERVER_ID = "/m/v1/user/server";
    //获取收集数据设置
    String COLLECT_UPLOAD = "/collection/mobile/v2/base"; // 用户信息收集上传api
    //上传收集数据
    String COLLECT_SETTING = "/collection/mobile/v1/user/settings"; // 数据采集配置api
    //查询是否使用从相册选择功能
    String SELECT_PHOTO_SETTING_URL = "/m/v1/photo/select";

    //扫一扫签到
    String SCAN_FOR_SIGNON = "/m/v1/user/attendance/operate";
    //我的签到记录
    String SIGN_LIST_INFRO = "/m/v1/user/attendance/list";

}
