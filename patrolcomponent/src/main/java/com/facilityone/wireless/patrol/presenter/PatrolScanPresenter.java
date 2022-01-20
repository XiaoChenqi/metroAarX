package com.facilityone.wireless.patrol.presenter;

import android.util.ArrayMap;
import android.util.Log;

import androidx.annotation.Nullable;

import com.facilityone.wireless.ObjectBox;
import com.facilityone.wireless.a.arch.base.FMJsonCallback;
import com.facilityone.wireless.a.arch.ec.module.LocationBean;
import com.facilityone.wireless.a.arch.mvp.BasePresenter;
import com.facilityone.wireless.a.arch.offline.dao.PatrolDeviceDao;
import com.facilityone.wireless.a.arch.offline.dao.PatrolSpotDao;
import com.facilityone.wireless.a.arch.offline.dao.PatrolTaskDao;
import com.facilityone.wireless.a.arch.offline.model.entity.DBPatrolConstant;
import com.facilityone.wireless.a.arch.offline.model.entity.PatrolSpotEntity;
import com.facilityone.wireless.a.arch.offline.model.entity.PatrolTaskEntity;
import com.facilityone.wireless.a.arch.offline.objectbox.patrol.CompleteTime;
import com.facilityone.wireless.a.arch.offline.objectbox.patrol.CompleteTime_;
import com.facilityone.wireless.a.arch.offline.objectbox.user.UserInfor;
import com.facilityone.wireless.a.arch.offline.objectbox.user.UserInfor_;
import com.facilityone.wireless.basiclib.app.FM;
import com.facilityone.wireless.basiclib.utils.GsonUtils;
import com.facilityone.wireless.basiclib.utils.SystemDateUtils;
import com.facilityone.wireless.patrol.fragment.PatrolScanFragment;
import com.facilityone.wireless.patrol.module.PatrolConstant;
import com.facilityone.wireless.patrol.module.PatrolQueryService;
import com.facilityone.wireless.patrol.module.PatrolStatusEntity;
import com.facilityone.wireless.patrol.module.PatrolStatusReq;
import com.facilityone.wireless.patrol.module.PatrolUrl;
import com.fm.tool.network.model.BaseResponse;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.objectbox.Box;
import io.objectbox.query.Query;
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
 * description:巡检扫一扫 nfc
 * Date: 2018/11/16 10:19 AM
 */
public class PatrolScanPresenter extends BasePresenter<PatrolScanFragment> {
    private Box<UserInfor> box; //用户信息
    private Box<CompleteTime> boxTask;  //任务离线
    //根据点位查询点位以及点位相关的任务
    public void getSpotList(final String code) {
        getV().showLoading();
        Observable.create(new ObservableOnSubscribe<List<PatrolSpotEntity>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<PatrolSpotEntity>> emitter) throws Exception {
                PatrolSpotDao spotDao = new PatrolSpotDao();
                //为防止二维码出现空格之类的，做如下处理code.trim();
                List<PatrolSpotEntity> temp = spotDao.getSpotList(code.trim());
                emitter.onNext(temp);
                emitter.onComplete();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<List<PatrolSpotEntity>>() {
                    @Override
                    public void onNext(@NonNull List<PatrolSpotEntity> patrolSpotEntities) {
                        cancel();
                        getV().refreshUI(patrolSpotEntities);
                        getV().dismissLoading();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        cancel();
                        getV().error();
                        getV().dismissLoading();
                    }

                    @Override
                    public void onComplete() {

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
                            getV().refreshUI(null);
                        } else {
                            getServicePatrolTask(ids);
                        }
                        cancel();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        getV().error();
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
                        updateTaskInfo(response.body().data, ids);
                    }

                    @Override
                    public void onError(Response<BaseResponse<List<PatrolStatusEntity>>> response) {
                        super.onError(response);
                        getV().getSpotList();
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
                        getV().getSpotList();
                        cancel();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        cancel();
                        getV().getSpotList();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 是否有比这个任务更早的同计划任务
     *
     * @param patrolTaskEntity
     * @param showEntities
     * @return
     */
    public boolean canGo(PatrolSpotEntity patrolTaskEntity, List<PatrolSpotEntity> showEntities) {
        for (PatrolSpotEntity showEntity : showEntities) {
            if (showEntity.getTaskPlanId().equals(patrolTaskEntity.getTaskPlanId())) {
                if (showEntity.getTaskDueStartDateTime() != null &&
                        patrolTaskEntity.getTaskDueStartDateTime() != null &&
                        showEntity.getTaskDueStartDateTime() < patrolTaskEntity.getTaskDueStartDateTime()
                        || (showEntity.getTaskDueStartDateTime() != null &&
                        patrolTaskEntity.getTaskDueStartDateTime() != null &&
                        showEntity.getTaskDueEndDateTime() != null &&
                        patrolTaskEntity.getTaskDueEndDateTime() != null &&
                        showEntity.getTaskDueStartDateTime() <= patrolTaskEntity.getTaskDueStartDateTime()
                        && showEntity.getTaskDueEndDateTime() < patrolTaskEntity.getTaskDueEndDateTime())) {
                    return false;
                }

            }
        }
        return true;
    }

    public void setTaskSpotDb(final Long taskId) {
        if (taskId == null) {
            return;
        }

        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> emitter) throws Exception {
                PatrolSpotDao dao = new PatrolSpotDao();
                List<PatrolSpotEntity> entities = dao.getSpotList(taskId);

                if (entities != null && entities.size() > 0) {
                    boolean exception = false;
                    boolean sync = false;
                    int defaultCount = 0;
                    int count = 0;
                    Long startTime = entities.get(0).getStartTime();
                    Long endTime = entities.get(0).getEndTime();

                    for (PatrolSpotEntity entity : entities) {
                        if (entity.getException() == DBPatrolConstant.TRUE_VALUE) {
                            exception = true;
                        }

                        if (entity.getCompleted() == DBPatrolConstant.TRUE_VALUE || entity.getRemoteCompleted() == DBPatrolConstant.TRUE_VALUE) {
                            count++;
                        }

                        if (entity.getNeedSync() == DBPatrolConstant.TRUE_VALUE) {
                            sync = true;
                        }

                        if (entity.getNeedSync() == DBPatrolConstant.DEFAULT_VALUE) {
                            defaultCount++;
                        }

                        if (entity.getStartTime() != 0) {
                            if (startTime == 0) {
                                startTime = entity.getStartTime();
                            } else if (startTime > entity.getStartTime()) {
                                startTime = entity.getStartTime();
                            }
                        }

                        if (entity.getEndTime() != 0) {
                            if (endTime == 0) {
                                endTime = entity.getEndTime();
                            } else if (endTime < entity.getEndTime()) {
                                endTime = entity.getEndTime();
                            }
                        }

                    }

                    PatrolTaskDao taskDao = new PatrolTaskDao();
                    int e = exception ? DBPatrolConstant.TRUE_VALUE : DBPatrolConstant.FALSE_VALUE;
                    int s = sync ? DBPatrolConstant.TRUE_VALUE : DBPatrolConstant.FALSE_VALUE;
                    s = defaultCount == entities.size() ? DBPatrolConstant.DEFAULT_VALUE : s;
                    int c = count == entities.size() ? DBPatrolConstant.TRUE_VALUE : DBPatrolConstant.FALSE_VALUE;
                    if (c != DBPatrolConstant.TRUE_VALUE) {
                        endTime = 0L;
                    }
                    Long updateTime = null;
                    if (sync) {
                        updateTime = System.currentTimeMillis();
                    }

                    taskDao.update(taskId, e, s, c, startTime, endTime, updateTime);
                }

                emitter.onNext(true);
                emitter.onComplete();
            }
        })
                .subscribeOn(Schedulers.io())
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
     * @Created by: kuuga
     * @Date: on 2021/8/25 10:40
     * @Description: 获取最后一次签到记录
     */
    public void getLastAttendance(){
        getV().showLoading();
        String json = "{}";
        box = ObjectBox.INSTANCE.getBoxStore().boxFor(UserInfor.class);
        OkGo.<BaseResponse<PatrolQueryService.AttendanceResp>>post(FM.getApiHost() + PatrolUrl.ATTENDANCE_LAST)
                .tag(getV())
                .isSpliceUrl(true)
                .upJson(json)
                .execute(new FMJsonCallback<BaseResponse<PatrolQueryService.AttendanceResp>>() {
                    @Override
                    public void onSuccess(Response<BaseResponse<PatrolQueryService.AttendanceResp>> response) {
                        getV().dismissLoading();
                        box.removeAll();
                        PatrolQueryService.AttendanceResp data = response.body().data;
                        if(data != null) {
                            UserInfor user = new UserInfor();
                            user.setId(0L);
                            user.setUserKey(PatrolConstant.USERLOGIN_ID);
                            if (data.location != null) {
                                user.setLocationBean(data.location);
                                user.setBuidlings(data.buildingIds);
                            }
                            box.put(user);
                            Log.e("LAST_ATTENDANCE", "===============***=============" + user.toString());
                            getV().hasAttentanceData(true);
                            getV().saveAttentanceLocation(data);
                        }else {
                            getV().hasAttentanceData(false);
                        }
                    }

                    @Override
                    public void onError(Response<BaseResponse<PatrolQueryService.AttendanceResp>> response) {
                        super.onError(response);
                        getV().dismissLoading();
                        box.removeAll();
                    }
                });
    }

//    /**
//     * @Created by: kuuga
//     * @Date: on 2021/8/31 9:22
//     * @Description: 判断点位任务是否可执行
//     * @param 'patrolTaskId'  巡检任务ID
//     * @param 'patrolTaskSpotId' 巡检点位ID
//     */
//    public void judgeTask(PatrolSpotEntity entity){
//        Map<String, Object> jsonObject = new HashMap<>();
//        jsonObject.put("patrolTaskSpotId", entity.getPatrolSpotId());
//        jsonObject.put("patrolTaskId", entity.getTaskId());
//        OkGo.<BaseResponse<PatrolQueryService.PatrolJudgeBean>>post(FM.getApiHost() + PatrolUrl.PATROL_JUDGE_TASK)
//                .tag(getV())
//                .upJson(toJson(jsonObject))
//                .execute(new FMJsonCallback<BaseResponse<PatrolQueryService.PatrolJudgeBean>>() {
//                    @Override
//                    public void onSuccess(Response<BaseResponse<PatrolQueryService.PatrolJudgeBean>> response) {
//                        getV().dismissLoading();
//                        PatrolQueryService.PatrolJudgeBean data = response.body().data;
//                        if (data != null) {
//                            if (data.executable){
//
//                                if (data.time!=0){
//                                    getV().showOrderTimeDialog(data.time,entity);
//
//                                }else {
//                                    getV().enterDeviceList(entity);
//                                }
//                            }else {
//                                if (data.patrolTaskId.equals(entity.getTaskId())&&data.patrolTaskSpotId.equals(entity.getPatrolSpotId())){
//                                    getV().enterDeviceList(entity);
//                                }else {
//                                    getV().showLefTimeDialog(data.time);
//                                }
//                            }
//                        }
//                    }
//                });
//    }


    /**
     * @Created by: kuuga
     * @Date: on 2021/8/27 16:27
     * @Description:  执行任务
     */
    public void executeTask(PatrolSpotEntity entity){
        PatrolQueryService.PatrolJudgeReq req=new PatrolQueryService.PatrolJudgeReq();
        req.patrolTaskId=entity.getTaskId();
        req.patrolTaskSpotId=entity.getPatrolSpotId();
        OkGo.<BaseResponse<String>>post(FM.getApiHost() + PatrolUrl.PATROL_EXECUTE_TASK)
                .tag(getV())
                .upJson(toJson(req))
                .execute(new FMJsonCallback<BaseResponse<String>>() {
                    @Override
                    public void onSuccess(Response<BaseResponse<String>> response) {
                        getV().dismissLoading();
                        getV().enterDeviceList(entity);

                    }
                });

    }


    /**
     * @Created by: kuuga
     * @Date: on 2021/9/2 15:33
     * @Description: 获取当前所有任务
     */
    public void getCurrentTask() {
        Observable.create(new ObservableOnSubscribe<ArrayMap<Long,PatrolTaskEntity>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<ArrayMap<Long,PatrolTaskEntity>> emitter) throws Exception {
                PatrolTaskDao dao = new PatrolTaskDao();
                ArrayMap<Long,PatrolTaskEntity> taskIds = dao.getTaskMap(null);
                System.out.println(GsonUtils.toJson(taskIds));
                emitter.onNext(taskIds);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<ArrayMap<Long,PatrolTaskEntity>>() {
                    @Override
                    public void onNext(@NonNull ArrayMap<Long,PatrolTaskEntity> taskEntitys) {
                        if (taskEntitys.size()==0) {
                            getV().refreshUI(null);
                        } else {
                            getV().checkCurrentTaskEntity(taskEntitys);
                        }
                        cancel();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        getV().error();
                        cancel();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }



    /**
     * @Creator:Karelie
     * @Data: 2021/12/14
     * @TIME: 9:31
     * @Introduce: 离线状态下 获取最后一次签到记录数据
     **/
    public void getLastAttendanceOutLine() {
        box = ObjectBox.INSTANCE.getBoxStore().boxFor(UserInfor.class);
        PatrolQueryService.AttendanceResp data = new PatrolQueryService.AttendanceResp();
        Query<UserInfor> query = box.query().equal(UserInfor_.userKey, PatrolConstant.USERLOGIN_ID).build();
        List<UserInfor> user = query.find();
        LocationBean locationData = new LocationBean();
        if (user.size()>0){
            locationData = user.get(0).getLocationBean();
            data.location = locationData;
            data.buildingIds = user.get(0).getBuidlings();
            getV().hasAttentanceData(true);
            getV().saveAttentanceLocation(data);
        }


    }


    /**
     * @Creator:Karelie
     * @Data: 2021/12/22
     * @TIME: 15:35
     * @Introduce: 判断巡检任务点位可继续性(离线形式)
     **/
    public void judgeTask(PatrolSpotEntity entity,boolean needScan){
        /**
         * 离线操作
         * */
        PatrolQueryService.PatrolJudgeBean bean = new PatrolQueryService.PatrolJudgeBean();
        boxTask = ObjectBox.INSTANCE.getBoxStore().boxFor(CompleteTime.class);
        Query<CompleteTime> query = boxTask.query().equal(CompleteTime_.taskTip, PatrolConstant.PATROL_TASK_OUTLINE).build();
        CompleteTime queryData = query.findFirst();
        bean.executable = getExecutable(entity);
        bean.time = getLastTime(entity);
        if (queryData != null) {
            bean.patrolTaskId = queryData.getTaskId();
            bean.patrolTaskSpotId = queryData.getPatrolSpotId();
        }


        //执行任务
        doWork(bean, entity, needScan);
    }

    /**
     * @Creator:Karelie
     * @Data: 2021/12/21
     * @TIME: 9:30
     * @Introduce: 根据当前任务数据装填以及任务实体数据已经 needScan判断下一步操作
     **/
    public void doWork(PatrolQueryService.PatrolJudgeBean data, PatrolSpotEntity entity, boolean needScan) {
        if (data != null) {
            /**
             *  executable == true
             *  此时当前任务列表中无数据 或者 任务已超过最短完成时间
             *  当前点击的任务可以开启任务
             * */
            if (data.executable) { //可以开启任务 当前点击
                /**
                 * 此时任务列表中有任务 或者当前点击的任务可以开启
                 * */
                if (data.time != 0) {
                    //判断是否需要扫码,不需要则直接开启
                    if (!needScan) {
                        getV().showOrderTimeDialog(data.time, entity);

                    } else {
                        getV().scanResult(entity, data.time);
                    }
                } else {
                    //判断是否需要扫码,不需要则直接进去
                    if (!needScan) {
                        getV().enterDeviceList(entity);
                    } else {
                        getV().scanResult(entity, data.time);

                    }
                }

            } else { //当前任务不可开启-- 任务已开启过 当前有处理中的任务
                //当前有任务是进入,判断是否同一个任务及点位
                if (data.patrolTaskId == null || data.patrolTaskSpotId == null){
                    canNotDo(data, entity);
                }else {
                    if (data.patrolTaskId.equals(entity.getTaskId()) && data.patrolTaskSpotId.equals(entity.getPatrolSpotId())) {
                        if (!needScan) {
                            getV().enterDeviceList(entity);
                        } else {
                            if (entity.getCompleted() == DBPatrolConstant.TRUE_VALUE || entity.getRemoteCompleted() == DBPatrolConstant.TRUE_VALUE){
                                getV().enterDeviceList(entity);
                            }else {
                                getV().scanResult(entity, data.time);
                            }
                        }
                        //非同一个任务及点位时,提示当前有任务进行以及剩余时间
                    } else {
                        canNotDo(data, entity);
                    }
                }

            }
        }
    }

    /**
     * @Creator:Karelie
     * @Data: 2021/12/22
     * @TIME: 15:09
     * @Introduce: 当前任务不可开启
     **/
    private void canNotDo(PatrolQueryService.PatrolJudgeBean data, PatrolSpotEntity entity) {
        if (data.time > 0){

            getV().showLefTimeDialog(data.time);
        }else {
            if (entity.getCompleted() == DBPatrolConstant.TRUE_VALUE || entity.getRemoteCompleted() == DBPatrolConstant.TRUE_VALUE){
                getV().enterDeviceList(entity);
            }else {
                PatrolSpotDao db = new PatrolSpotDao();
                PatrolSpotEntity item = db.getSpot(entity.getPatrolSpotId());
                if (item.getTaskStatus() > 0){
                    getV().scanResult(entity, 0L); //开启了任务但是没有保存
                }else {
                    getV().showOrderTimeDialog(data.time,entity);
                }

            }
        }
    }

    /**
     * 判断当前任务是否可以开启
     */
    public boolean getExecutable(PatrolSpotEntity entity) {
        boxTask = ObjectBox.INSTANCE.getBoxStore().boxFor(CompleteTime.class);
        Query<CompleteTime> query = boxTask.query().equal(CompleteTime_.taskTip, PatrolConstant.PATROL_TASK_OUTLINE).build();
        CompleteTime queryData = query.findFirst();
        PatrolSpotDao db = new PatrolSpotDao();
        PatrolSpotEntity data = db.getSpot(entity.getPatrolSpotId());
        Long systemTime = SystemDateUtils.getCurrentTimeMillis(); //获取当前时间

        if (queryData == null) {
            if (data.getTaskStatus() >0){
                return false; // 当前任务列表中无数据 且当前点击的任务开启过任务
            }else {
                if (entity.getCompleted() == DBPatrolConstant.TRUE_VALUE || entity.getRemoteCompleted() == DBPatrolConstant.TRUE_VALUE) {
                    return false; //此时该任务已经结束 不要再开启
                }else {
                    return true; //当前任务表中无数据 且当前点击的任务没有开启过任务
                }

            }
        } else {
            Long taskTime = queryData.getStarTime() + queryData.getCheckTime() * 1000L;
            if (data.getTaskStatus() > 0) { //说明开启过任务 不可再次开启该任务
                return false;
            } else { //任务未开启
                if (systemTime >= taskTime) { //任务表中刚结束或者已经结束了
                    if (entity.getCompleted() == DBPatrolConstant.TRUE_VALUE || entity.getRemoteCompleted() == DBPatrolConstant.TRUE_VALUE) {
                        return false; //此时该任务已经结束 不要再开启
                    }else {
                        return true; //当前任务表中无数据 且当前点击的任务没有开启过任务
                    }
                } else {
                    return false; //任务列表中未结束 不可开启任务
                }
            }

        }
    }


    /**
     * @Creator:Karelie
     * @Data: 2021/12/16
     * @TIME: 14:18
     * @Introduce: 获取剩余时间
     **/
    public Long getLastTime(PatrolSpotEntity enity) {
        Long timeForNow = SystemDateUtils.getCurrentTimeMillis();
        boxTask = ObjectBox.INSTANCE.getBoxStore().boxFor(CompleteTime.class);
        Query<CompleteTime> query = boxTask.query().equal(CompleteTime_.taskTip, PatrolConstant.PATROL_TASK_OUTLINE).build();
        CompleteTime db = query.findFirst();
        if (db != null) {
            Long startTime = db.getStarTime() + 1000L * db.getCheckTime();
            if (timeForNow >= startTime) {
                if (enity.getTaskStatus() > 0){
                    return 0L; // 过了最短完成时间且任务开启过
                }else {
                    if (enity.getCompleted() == DBPatrolConstant.TRUE_VALUE || enity.getRemoteCompleted() == DBPatrolConstant.TRUE_VALUE){
                        return 0L; //状态是已完成的情况下
                    }else {
                        return Long.parseLong(enity.getTaskTime()+""); //过了最短完成时间 且任务未开启过
                    }
                }
            } else {
                return (startTime - timeForNow) / 1000L; //当前有任务在执行
            }
        } else {
            if (enity.getTaskStatus() > 0 || enity.getCompleted() == DBPatrolConstant.TRUE_VALUE || enity.getRemoteCompleted() == DBPatrolConstant.TRUE_VALUE) {
                return 0L; //当前没有执行中的任务 且任务已开启
            } else {
                return Long.parseLong(enity.getTaskTime()+""); //当前五任务开启 且任务未开启过
            }
        }
    }

}
