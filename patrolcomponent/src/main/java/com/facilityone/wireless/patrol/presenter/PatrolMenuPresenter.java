package com.facilityone.wireless.patrol.presenter;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.facilityone.wireless.ObjectBox;
import com.facilityone.wireless.a.arch.base.FMJsonCallback;
import com.facilityone.wireless.a.arch.ec.commonpresenter.CommonBasePresenter;
import com.facilityone.wireless.a.arch.ec.module.FunctionService;
import com.facilityone.wireless.a.arch.ec.module.UserService;
import com.facilityone.wireless.a.arch.ec.ui.FzScanActivity;
import com.facilityone.wireless.a.arch.ec.utils.SPKey;
import com.facilityone.wireless.a.arch.offline.dao.PatrolDeviceDao;
import com.facilityone.wireless.a.arch.offline.dao.PatrolSpotDao;
import com.facilityone.wireless.a.arch.offline.dao.PatrolTaskDao;
import com.facilityone.wireless.a.arch.offline.model.entity.DBPatrolConstant;
import com.facilityone.wireless.a.arch.offline.model.entity.PatrolTaskEntity;
import com.facilityone.wireless.a.arch.offline.objectbox.user.UserInfor;
import com.facilityone.wireless.a.arch.offline.util.PatrolQrcodeUtils;
import com.facilityone.wireless.basiclib.app.FM;
import com.facilityone.wireless.componentservice.common.permissions.PermissionsManager;
import com.facilityone.wireless.patrol.R;
import com.facilityone.wireless.patrol.fragment.PatrolMenuFragment;
import com.facilityone.wireless.patrol.module.PatrolConstant;
import com.facilityone.wireless.patrol.module.PatrolQueryService;
import com.facilityone.wireless.patrol.module.PatrolStatusEntity;
import com.facilityone.wireless.patrol.module.PatrolStatusReq;
import com.facilityone.wireless.patrol.module.PatrolUrl;
import com.fm.tool.network.model.BaseResponse;
import com.fm.tool.scan.ScanActivity;
import com.huawei.hms.ml.scan.HmsScan;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.zdf.activitylauncher.ActivityLauncher;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.objectbox.Box;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DefaultObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Author：gary
 * Email: xuhaozv@163.com
 * description:巡检菜单
 * Date: 2018/10/30 3:01 PM
 */
public class PatrolMenuPresenter extends CommonBasePresenter<PatrolMenuFragment> {
    private Box<UserInfor> box; //用户信息
    @Override
    public void getUndoNumberSuccess(JSONObject data) {
        List<FunctionService.FunctionBean> functionBeanList = getV().getFunctionBeanList();
        for (FunctionService.FunctionBean functionBean : functionBeanList) {
            try {
                switch (functionBean.index) {
                    case PatrolConstant.PATROL_TASK:
                        //巡检任务
                        functionBean.undoNum = data.getInt(PermissionsManager.PATROLTASKNUMBER);
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        getV().updateFunction(functionBeanList);
    }

    /**
     * 二维码扫描相关逻辑
     */
    public void scan() {
        Intent intent = new Intent(getV().getContext(), FzScanActivity.class);
        ActivityLauncher.init(getV().getActivity())
                .startActivityForResult(intent, new ActivityLauncher.Callback() {
                    @Override
                    public void onActivityResult(int resultCode, Intent data) {
                        if (data != null){
                            HmsScan result=data.getParcelableExtra("scanResult");
                            if (result!=null){
                                if (result.originalValue != null){
                                    getV().scanResult(PatrolQrcodeUtils.parseSpotCode(result.originalValue));
                                }else {
                                    ToastUtils.showShort(R.string.patrol_qrcode_no_match);
                                }
                            }
                        }

                    }
                });
    }

    /**
     * @Created by: kuuga
     * @Date: on 2021/8/25 12:07
     * @Description:华为统一扫码服务
     */
    public void hmsScan(){
        Intent intent = new Intent(getV().getContext(), FzScanActivity.class);


        // 启动Activity（方式二）
        ActivityLauncher.init(getV().getActivity())
                .startActivityForResult(intent, new ActivityLauncher.Callback() {
                    @Override
                    public void onActivityResult(int resultCode, Intent data) {
                        HmsScan result=data.getParcelableExtra("scanResult");
                        if (result!=null){
                            ToastUtils.showLong(result.originalValue);
                            System.out.println(result.originalValue);
                        }else {

                        }

                    }
                });
    }

    /**
     * 此项目是否需要nfc才可以工作
     */
    public void requestProjectNeedNfc() {
        Map<String, Object> jsonObject = new HashMap<>();
        jsonObject.put("projectId", FM.getProjectId());
        OkGo.<BaseResponse<Integer>>post(FM.getApiHost() + PatrolUrl.PATROL_NEED_NFC_DEL)
                .tag(getV())
                .isSpliceUrl(true)
                .upJson(toJson(jsonObject))
                .execute(new FMJsonCallback<BaseResponse<Integer>>() {
                    @Override
                    public void onSuccess(Response<BaseResponse<Integer>> response) {
                        Integer data = response.body().data;
                        if (data != null) {
                            SPUtils.getInstance(SPKey.SP_MODEL_PATROL).put(SPKey.PATROL_NEED_NFC, data);
                            getV().refreshScan(data == PatrolConstant.PATROL_NEED_NFC);
                        }
                    }
                });
    }

    public void getServicePatrolTask() {
        Observable.create(new ObservableOnSubscribe<List<Long>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<Long>> emitter) throws Exception {
                PatrolTaskDao dao = new PatrolTaskDao();
                List<Long> taskIds = dao.getTaskIds();
                emitter.onNext(taskIds);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<List<Long>>() {
                    @Override
                    public void onNext(@NonNull List<Long> ids) {
                        if (ids == null) {
                            Log.i("暂无任务", "onNext: ====暂无任务");
                        } else {
                            getV().showLoading();
                            getServicePatrolTask(ids);
                        }
                        cancel();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        cancel();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void getServicePatrolTask(final List<Long> ids) {
        PatrolStatusReq request = new PatrolStatusReq(ids);
        OkGo.<BaseResponse<List<PatrolStatusEntity>>>post(FM.getApiHost() + PatrolUrl.PATROL_TASK_STATUS)
                .tag(getV())
                .upJson(toJson(request))
                .isSpliceUrl(true)
                .execute(new FMJsonCallback<BaseResponse<List<PatrolStatusEntity>>>() {
                    @Override
                    public void onSuccess(Response<BaseResponse<List<PatrolStatusEntity>>> response) {
                        getV().dismissLoading();
                        updateTaskInfo(response.body().data, ids);
                    }

                    @Override
                    public void onError(Response<BaseResponse<List<PatrolStatusEntity>>> response) {
                        getV().dismissLoading();
                        super.onError(response);
                    }
                });
    }

    private void updateTaskInfo(final List<PatrolStatusEntity> data, final List<Long> ids) {
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> emitter) throws Exception {
                List<Long> deletedId = new ArrayList<>();
                List<PatrolTaskEntity> patrolTaskEntities = new ArrayList<>();
                List<PatrolStatusEntity.PatrolSpotsBean> patrolSpotsBeen = new ArrayList<>();
                List<PatrolStatusEntity.PatrolEquBean> patrolEquBeen = new ArrayList<>();
                if (data != null && data.size() > 0) {
                    String emName = FM.getEmName();
                    for (PatrolStatusEntity patrolStatusEntity : data) {
                        if (patrolStatusEntity.getDeleted() != null && patrolStatusEntity.getDeleted()) {
                            deletedId.add(patrolStatusEntity.getPatrolTaskId());
                            continue;
                        }

                        boolean del = true;
                        for (Long id : ids) {
                            if (patrolStatusEntity.getPatrolTaskId() != null &&
                                    id.equals(patrolStatusEntity.getPatrolTaskId())) {
                                del = false;
                                break;
                            }

                        }
                        if (del) {
                            deletedId.add(patrolStatusEntity.getPatrolTaskId());
                            continue;
                        }

                        if (patrolStatusEntity.getStatus() != null && patrolStatusEntity.getStatus() > PatrolConstant.PATROL_STATUS_ING) {
                            deletedId.add(patrolStatusEntity.getPatrolTaskId());
                            continue;
                        }

                        List<PatrolStatusEntity.PatrolSpotsBean> patrolSpots = patrolStatusEntity.getPatrolSpots();
//                        boolean taskCompleted = true;
                        if (patrolSpots != null && patrolSpots.size() > 0) {
                            for (PatrolStatusEntity.PatrolSpotsBean patrolSpot : patrolSpots) {
                                patrolSpot.setTaskId(patrolStatusEntity.getPatrolTaskId());
//                                if (patrolSpot.getFinished() != null && !patrolSpot.getFinished()) {
//                                    taskCompleted = false;
//                                }
                                if (patrolStatusEntity.getHandler() != null && patrolStatusEntity.getHandler()) {
                                    patrolSpot.setHandler(emName);
                                }
                            }
                            patrolSpotsBeen.addAll(patrolSpots);
                        }
                        PatrolTaskEntity entity = new PatrolTaskEntity();
                        entity.setTaskId(patrolStatusEntity.getPatrolTaskId());
                        if (patrolStatusEntity.getStatus() != null && patrolStatusEntity.getStatus() == PatrolConstant.PATROL_STATUS_ING) {
                            entity.setStatus(patrolStatusEntity.getStatus());
                        }
                        System.out.println("服务器状态");
                        System.out.println(patrolStatusEntity.getPtype());
                        entity.setpType(patrolStatusEntity.getPtype());

//                        entity.setCompleted(taskCompleted ? DBPatrolConstant.TRUE_VALUE : DBPatrolConstant.FALSE_VALUE);
                        patrolTaskEntities.add(entity);
                    }

                    for (PatrolStatusEntity.PatrolSpotsBean patrolSpotsBean : patrolSpotsBeen) {
                        List<PatrolStatusEntity.PatrolEquBean> equipments = patrolSpotsBean.getEquipments();
                        if (equipments != null && equipments.size() > 0) {
                            for (PatrolStatusEntity.PatrolEquBean equipment : equipments) {
                                equipment.setSpotId(patrolSpotsBean.getPatrolSpotId());
                            }
                            patrolEquBeen.addAll(equipments);
                        }
                    }
                }

                if (deletedId.size() > 0) {
                    PatrolTaskDao taskDao = new PatrolTaskDao();
                    taskDao.deleteTask(deletedId);
                }

                if (patrolTaskEntities.size() > 0) {
                    PatrolTaskDao taskDao = new PatrolTaskDao();
                    for (PatrolTaskEntity patrolTaskEntity : patrolTaskEntities) {
                        if (patrolTaskEntity.getStatus() != null) {
                            taskDao.update(patrolTaskEntity.getStatus(), patrolTaskEntity.getTaskId());
                        }
                        else if (patrolTaskEntity.getpType()!=null){
                            //只判断任务类型
                            taskDao.update(patrolTaskEntity.getTaskId(),patrolTaskEntity.getpType());
                        }
                    }
                }

                if (patrolSpotsBeen.size() > 0) {
                    PatrolSpotDao spotDao = new PatrolSpotDao();
                    for (PatrolStatusEntity.PatrolSpotsBean patrolSpotsBean : patrolSpotsBeen) {
                        if ((patrolSpotsBean.getFinished() != null && patrolSpotsBean.getFinished()) || patrolSpotsBean.getHandler() != null) {
                            spotDao.updateRemoteCompleted(patrolSpotsBean.getPatrolSpotId(), patrolSpotsBean.getHandler(), patrolSpotsBean.getFinished());
                        }
                    }
                }

                if (patrolEquBeen.size() > 0) {
                    PatrolDeviceDao deviceDao = new PatrolDeviceDao();
                    for (PatrolStatusEntity.PatrolEquBean equBean : patrolEquBeen) {
                        if (equBean.getFinished() != null && equBean.getFinished()) {
                            deviceDao.updateRemoteCompleted(DBPatrolConstant.TRUE_VALUE, equBean.getEqId(), equBean.getSpotId());
                        }
                    }
                }


                emitter.onNext(true);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<Boolean>() {
                    @Override
                    public void onNext(@NonNull Boolean b) {
                        cancel();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        cancel();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * @Creator:Karelie
     * @Data: 2021/12/22
     * @TIME: 11:21
     * @Introduce:获取离线数据
     **/
    public void getLastAttendance() {
        getV().showLoading();
        box = ObjectBox.INSTANCE.getBoxStore().boxFor(UserInfor.class);
        String userInfo = SPUtils.getInstance(SPKey.SP_MODEL_USER).getString(SPKey.USER_INFO);
        UserService.UserInfoBean infoBean = GsonUtils.fromJson(userInfo, UserService.UserInfoBean.class);
        String json = "{}";
        OkGo.<BaseResponse<PatrolQueryService.AttendanceResp>>post(FM.getApiHost() + PatrolUrl.ATTENDANCE_LAST)
                .tag(getV())
                .isSpliceUrl(true)
                .upJson(json)
                .execute(new FMJsonCallback<BaseResponse<PatrolQueryService.AttendanceResp>>() {
                    @Override
                    public void onSuccess(Response<BaseResponse<PatrolQueryService.AttendanceResp>> response) {
                        getV().dismissLoading();
                        PatrolQueryService.AttendanceResp data = response.body().data;
                        if (data != null) {
                            UserInfor user = new UserInfor();
                            box.removeAll();
                            user.setId(0L);
                            user.setUserKey(PatrolConstant.USERLOGIN_ID);
                            if (data.location != null) {
                                user.setLocationBean(data.location);
                                user.setBuidlings(data.buildingIds);
                            }
                            box.put(user);
                            Log.e("LAST_ATTENDANCE", "===============***=============" + user.toString());
                        }
                    }

                    @Override
                    public void onError(Response<BaseResponse<PatrolQueryService.AttendanceResp>> response) {
                        super.onError(response);
                        getV().dismissLoading();
                    }
                });


    }
}
