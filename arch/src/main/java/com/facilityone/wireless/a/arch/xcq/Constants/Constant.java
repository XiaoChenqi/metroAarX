package com.facilityone.wireless.a.arch.xcq.Constants;


/**
 * Created by MSI-PC on 2018/4/4.
 */

public class Constant {

//    www.efacedata.com
//    API：9092
//    回放：9025
//    直播：9026

    //公司测试地址
    //public static final String BASE_URL = "http://192.168.3.101:9092";//主地址
    //public static final String HLS_URL = "http://192.168.3.101:9025";//主地址
//    public static final String BASE_URL = "http://192.168.3.80:9092";
//    public static final String HLS_URL =   "http://192.168.3.80:9025";//TODO 回头获取
    public static final String BASE_URL = "http://192.168.3.252:9092";
    public static final String HLS_URL =   "http://192.168.3.252:9025";//TODO 回头获取
    //公司外网地址
//    public static final String BASE_URL = "http://120.195.205.78:9092";
//    public static final String HLS_URL =   "http://120.195.205.78:9025";//TODO 回头获取


    //网络返回正确
    public static final int CODE_OK=200;//

    public static  int THEME_COLOR=0;//
    public static  String USERNAME="";//
    public static  String PASSWORD="";//


    //列表筛选条件，str字符串，存入shreprefrence用
    public static final String SP_FACE = "SP_FACE";//数据库名称
    public static final String SELECTION_DATA = "SELECTION_DATA";//筛选信息
    public static final String USER_DATA = "USER_DATA";//用户信息
    public static final String BAIDU_FACE_DATA = "BAIDU_FACE_FATA";//百度人脸会用的数据库名称


    /**
     * 密码验证页面的跳转str字符串
     */
    public static final String MACHINE_ID = "machine_id";//哪台洗衣机
    public static final String INTENT_SOURCE = "intent_source";//从哪个页面进行验证页面
    public static final String LOGIN_PAGE = "login_page";//表示从注册页面进行验证
    public static final String PERSONAL_PAGE = "personal_page";//表示从个人中心进行验证
    /**
     * 增量更新app使用的常量
     */
    public static final String APP_PATCH_NAME = "/currentVersion.patch";//下载的补丁的名称
    public static final String LAST_APK_NAME = "/prison.apk";//合成的apk的名称
    /**
     * 百度前后屏跳转常量
     */
    public static final String BAIDU_CAMERA_DIRECTION = "baidu_camera_direction";//前置还是后置摄像头
}
