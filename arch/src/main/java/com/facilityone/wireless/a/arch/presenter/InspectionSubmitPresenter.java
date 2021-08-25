package com.facilityone.wireless.a.arch.presenter;

import com.facilityone.wireless.a.arch.presenter.ivew.LoginMvpView;
import com.facilityone.wireless.a.arch.xcq.core.mvp.BasePresenter;

public class InspectionSubmitPresenter extends BasePresenter<LoginMvpView> {

//    private static InspectionTaskModel taskModel;
//
//    public synchronized static InspectionTaskModel initInspectionTaskModelModel() {
//        if (taskModel == null) {
//            taskModel = new InspectionTaskModel();
//        }
//        return taskModel;
//    }
//
//    public InspectionSubmitPresenter() {
//        initInspectionTaskModelModel();
//    }
//
//    /**
//     * 提交所有的任务，需要判断是否都完成了，有一个任务类型没有完成，就不能提交
//     * @param dataList
//     * @param bean
//     * @param requestCode
//     */
//    public void submitTaskType(ArrayList<InspectionTaskType.Types> dataList, FmNetApi.InspectionTask bean, final int requestCode){
//        for(int i=0;i<dataList.size();i++){
//            InspectionTaskType.Types temp = dataList.get(i);
//            if(!temp.completed){
//                getMvpView().taskTypeNotComplete();
//                return;
//            }
//        }
//        taskModel.submitTaskType(bean, new BaseNetCallback(getMvpView(), requestCode));
//    }
}
