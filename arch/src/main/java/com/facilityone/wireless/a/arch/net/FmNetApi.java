package com.facilityone.wireless.a.arch.net;
import com.facilityone.wireless.a.arch.xcq.bean.BaseResponse;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface FmNetApi {

    //登录地址
    String LOGON_URL = "/fz_iframe/m/security/login";

    //@FormUrlEncoded
//    @POST("/out/f1-shang/m/v2/inspection/type")
//    Observable<BaseResponse<InspectionTaskType>> getTaskType(@Body InspectionTask task);
//
    @POST(LOGON_URL)
    Observable<BaseResponse> login(@Body LoginBean bean);
//
//
//    @POST("/out/f1-shang/m/v2/inspection/query")
//    Observable<BaseResponse<ListBean<InspectionV2TaskEntity.InspectionTask>>> getTaskList(@Body InspectionTaskEntity.InspectionTaskRequest requestBean);
//
//    @POST("/out/f1-shang/m/v2/inspection/items")
//    Observable<BaseResponse<List<TaskTypeDetail>>> getTaskTypeDetail(@Body InspectionTask task);
//
//    @POST("/out/f1-shang/m/v2/inspection/save")
//    Observable<BaseResponse> saveTaskTypeDetail(@Body InspectionTaskDetailRequest bean);
//
//    @POST("/out/f1-shang/m/v2/inspection/submit")
//    Observable<BaseResponse> submitTaskType(@Body InspectionTask bean);

    class InspectionTask {
        public Long taskId;
        public String typeName;
    }

    class LoginBean {

        public String loginCode;
        public String loginPwd;
        public String local;
        public String appType;
        public String appVersion;
        public String source;

    }
}
