package com.facilityone.wireless.a.arch.presenter;


import com.facilityone.wireless.a.arch.ec.module.UserService;
import com.facilityone.wireless.a.arch.model.UserModel;
import com.facilityone.wireless.a.arch.net.FmNetApi;
import com.facilityone.wireless.a.arch.xcq.core.mvp.BasePresenter;
import com.facilityone.wireless.a.arch.xcq.core.mvp.MvpView;
import com.facilityone.wireless.a.arch.xcq.net.BaseNetCallback;

public class UserBehaviorPresenter extends BasePresenter<MvpView> {

    private static UserModel userModel;

    public synchronized static UserModel initUserModel() {
        if (userModel == null) {
            userModel = new UserModel();
        }
        return userModel;
    }

    public UserBehaviorPresenter() {
        initUserModel();
    }

//    /**
//     * 获取待检查任务检查项目类型列表
//     * "location": "北京总部/A座/1F",
//     */
//    public void getTaskType(Long taskId, final int requestCode) {
//        taskModel.getTaskType(taskId, new BaseNetCallback<InspectionTaskType>(getMvpView(), requestCode));
//    }
//
//    /**
//     * 获取检查任务项目类型的详情页中的列表
//     * @param bean
//     * @param requestCode
//     */
//    public void getTaskTypeDetailList(FmNetApi.InspectionTask bean, final int requestCode) {
//        taskModel.getTaskTypeDetail(bean, new BaseNetCallback<List<TaskTypeDetail>>(getMvpView(), requestCode));
//    }

    public void login(FmNetApi.LoginBean bean, final int requestCode) {
        userModel.login(bean, new BaseNetCallback(getMvpView(), requestCode));
    }

//    /**
//     * 获取检查列表内容
//     * @param requestBean
//     * @param requestCode
//     */
//    public void getTaskList(InspectionTaskEntity.InspectionTaskRequest requestBean, final int requestCode) {
//        taskModel.getTaskList(requestBean, new BaseNetCallback<ListBean<InspectionV2TaskEntity.InspectionTask>>(getMvpView(), requestCode));
//    }
//
//    /**
//     * 保存检查列表的所有内容，包括图片
//     * @param bean
//     * @param requestCode
//     */
//    public void saveTaskDetail(InspectionTaskDetailRequest bean, final int requestCode) {
//        taskModel.saveTaskDetail(bean, new BaseNetCallback(getMvpView(), requestCode));
//    }


    public void userInfor(final int requestCode) {
        userModel.getUserInfor( new BaseNetCallback<UserService.UserInfoBean>(getMvpView(), requestCode));
    }


}
