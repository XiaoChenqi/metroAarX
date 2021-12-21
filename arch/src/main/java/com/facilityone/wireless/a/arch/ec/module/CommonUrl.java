package com.facilityone.wireless.a.arch.ec.module;

/**
 * Author：gary
 * Email: xuhaozv@163.com
 * description:多个模块用到的放在这里
 * Date: 2018/6/8 上午9:13
 */
public interface CommonUrl {
    //公告列表
    String BULLETIN_LIST_URL = "/m/v1/bulletin/query";
    String COMMON_UNDO_URL = "/m/v2/common/tasks/undo";
    //权限
    String COMMON_PERMISSIONS_URL = "/m/v1/function/permission";
    //上传文件
    String UPLOAD_IMAGE_URL = "/m/v1/files/upload/picture";
    String UPLOAD_VOICE_URL = "/m/v1/files/upload/voicemedia";
    String UPLOAD_VIDEO_URL = "/m/v1/files/upload/videomedia";
    String UPLOAD_ATTACH_URL = "/m/v1/files/upload/attachment";
    //工作组
    String WORK_TEAM_URL = "/m/v1/common/workTeam";
    //更新检查
    String UPDATE_CHECK = "/mobile/v3/version/neweast";
    // 下载
    String UPDATE_DOWNLOAD_URL = "/mobile/download/";
    //获取员工列表（附带部门和位置信息）
    String USER_LIST = "/m/v1/user/detaillist/";

    //暂停，作废，故障原因
    String REASON_LIST="/m/v1/workorder/reason/list";
    //专业
    String PROFESSIONAL_LIST = "/m/v1/common/professional";



    //最后一次签到记录
    String ATTENDANCE_LAST = "/m/v1/user/attendance/last";
    //我的签到记录
    String ATTENDANCE_LIST = "/m/v1/user/attendance/list";
    //扫一扫签到
    String ATTENDANCE_OPT = "/m/v1/user/attendance/operate";
}
