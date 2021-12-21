package com.facilityone.wireless.patrol.presenter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.facilityone.wireless.ObjectBox;
import com.facilityone.wireless.a.arch.base.FMJsonCallback;
import com.facilityone.wireless.a.arch.ec.logon.LogonManager;
import com.facilityone.wireless.a.arch.ec.module.CommonUrl;
import com.facilityone.wireless.a.arch.ec.module.LocationBean;
import com.facilityone.wireless.a.arch.ec.module.LogonResponse;
import com.facilityone.wireless.a.arch.ec.module.UserService;
import com.facilityone.wireless.a.arch.ec.ui.FzScanActivity;
import com.facilityone.wireless.a.arch.ec.utils.SPKey;
import com.facilityone.wireless.a.arch.mvp.BasePresenter;
import com.facilityone.wireless.a.arch.offline.dao.PatrolDeviceDao;
import com.facilityone.wireless.a.arch.offline.dao.PatrolItemDao;
import com.facilityone.wireless.a.arch.offline.dao.PatrolPicDao;
import com.facilityone.wireless.a.arch.offline.dao.PatrolSpotDao;
import com.facilityone.wireless.a.arch.offline.dao.PatrolTaskDao;
import com.facilityone.wireless.a.arch.offline.model.entity.DBPatrolConstant;
import com.facilityone.wireless.a.arch.offline.model.entity.PatrolEquEntity;
import com.facilityone.wireless.a.arch.offline.model.entity.PatrolItemEntity;
import com.facilityone.wireless.a.arch.offline.model.entity.PatrolPicEntity;
import com.facilityone.wireless.a.arch.offline.model.entity.PatrolSpotEntity;
import com.facilityone.wireless.a.arch.offline.model.entity.PatrolTaskEntity;
import com.facilityone.wireless.a.arch.offline.model.service.PatrolDbService;
import com.facilityone.wireless.a.arch.offline.objectbox.patrol.CompleteTime;
import com.facilityone.wireless.a.arch.offline.objectbox.patrol.CompleteTime_;
import com.facilityone.wireless.a.arch.offline.objectbox.user.UserInfor;
import com.facilityone.wireless.a.arch.offline.objectbox.user.UserInfor_;
import com.facilityone.wireless.a.arch.offline.util.PatrolQrcodeUtils;
import com.facilityone.wireless.a.arch.widget.FMWarnDialogBuilder;
import com.facilityone.wireless.basiclib.app.FM;
import com.facilityone.wireless.basiclib.utils.SystemDateUtils;
import com.facilityone.wireless.patrol.R;
import com.facilityone.wireless.patrol.fragment.PatrolSpotFragment;
import com.facilityone.wireless.patrol.module.PatrolConstant;
import com.facilityone.wireless.patrol.module.PatrolQueryService;
import com.facilityone.wireless.patrol.module.PatrolSaveReq;
import com.facilityone.wireless.patrol.module.PatrolUrl;
import com.fm.tool.network.callback.JsonCallback;
import com.fm.tool.network.model.BaseResponse;
import com.fm.tool.scan.ScanActivity;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.huawei.hms.ml.scan.HmsScan;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.PostRequest;
import com.lzy.okgo.request.base.Request;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.zdf.activitylauncher.ActivityLauncher;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.objectbox.Box;
import io.objectbox.query.Query;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DefaultObserver;
import io.reactivex.schedulers.Schedulers;
import kotlin.jvm.internal.markers.KMutableMap;

/**
 * Author：gary
 * Email: xuhaozv@163.com
 * description:点位
 * Date: 2018/11/7 10:14 AM
 */
public class PatrolSpotPresenter extends BasePresenter<PatrolSpotFragment> {
    private Box<UserInfor> box; //用户信息
    private Box<CompleteTime> boxTask ;  //任务离线

    /**
     * 获取巡检任务下的点位
     *
     * @param taskId
     */
    public void getSpotList(final Long taskId) {
        Observable.create(new ObservableOnSubscribe<List<PatrolSpotEntity>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<PatrolSpotEntity>> emitter) throws Exception {
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

                emitter.onNext(entities);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<List<PatrolSpotEntity>>() {
                    @Override
                    public void onNext(@NonNull List<PatrolSpotEntity> spotEntities) {
                        getV().refreshUI(spotEntities);
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

    private int picTotal;
    private List<PatrolSpotEntity> mSync;

    /**
     * 同步或提交数据前的准备
     *
     * @param req
     * @param totalEntities
     * @param operateType
     * @param taskId
     */
    public void syncData(PatrolSaveReq req, List<PatrolSpotEntity> totalEntities, int operateType, Long taskId) {
        getV().showLoading();
        picTotal = 0;
        //漏检个数
        if (operateType == PatrolConstant.PATROL_OPT_TYPE_SYNC) {
            //查询是否有需要同步的数据
            mSync = haveData2Sync(totalEntities, true);
            if (mSync == null || mSync.size() == 0) {
                getV().dismissLoading();
                ToastUtils.showShort(R.string.patrol_no_sync_spot);
                return;
            }
            getData(req, mSync, operateType, taskId);
        } else {
            //查询漏检个数
            getMissItem(totalEntities, req, operateType, taskId);
        }
    }

    private int haveMiss(PatrolEquEntity equEntity){
        PatrolItemDao db = new PatrolItemDao();
        List<PatrolItemEntity> list = db.getItemList(equEntity.getEqId(),equEntity.getSpotId(),null);
        String choice = list.get(0).getSelect();
        if (choice.equals("通风")){
            for (PatrolItemEntity item : list) {
                if (!item.getContent().equals("车站工况")&&item.getResultType() != null
                        && (item.getResultType() == PatrolDbService.QUESTION_TYPE_INPUT
                        ||item.getResultType() == PatrolDbService.QUESTION_TYPE_TEXT
                ) && TextUtils.isEmpty(item.getInput()) && ((item.getValidStatus()==  PatrolConstant.EQU_STOP
                        || item.getValidStatus() == PatrolConstant.EQU_ALL))) {
                    return 1;
                }
            }

        }else {
            for (PatrolItemEntity item : list) {
                if (item.getResultType() != null
                        && (item.getResultType() == PatrolDbService.QUESTION_TYPE_INPUT
                        ||item.getResultType() == PatrolDbService.QUESTION_TYPE_TEXT
                ) && TextUtils.isEmpty(item.getInput()) && ((item.getValidStatus()==  PatrolConstant.EQU_USE
                        || item.getValidStatus() == PatrolConstant.EQU_ALL))) {
                    return 1;
                }
            }
        }
        return 0;
    }


    /**
     * 获取漏检个数
     *
     * @param totalEntities
     * @param req
     * @param operateType
     * @param taskId
     */
    private void getMissItem(final List<PatrolSpotEntity> totalEntities, final PatrolSaveReq req, final int operateType, final Long taskId) {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Exception {
                PatrolDeviceDao deviceDao = new PatrolDeviceDao();
                List<PatrolEquEntity> equEntities = new ArrayList<>();
                for (PatrolSpotEntity totalEntity : totalEntities) {
                    //如果本地没做过并且远程完成了点位
                    if (totalEntity.getNeedSync() == DBPatrolConstant.DEFAULT_VALUE && totalEntity.getRemoteCompleted() == DBPatrolConstant.TRUE_VALUE) {
                        continue;
                    }
                    List<PatrolEquEntity> deviceList = deviceDao.getDeviceList(totalEntity.getPatrolSpotId());
                    if (deviceList != null) {
                        equEntities.addAll(deviceList);
                    }
                }

                int miss = 0;
                PatrolItemDao itemDao = new PatrolItemDao();

                for (PatrolEquEntity equEntity : equEntities) {
                    //如果以后设备也需要根据远程做没做完来判断需不需要提交 可以在这里加条件筛选
                    PatrolSpotDao db = new PatrolSpotDao();
                    String name = db.getSpot(equEntity.getSpotId()).getTaskName();
                    if (name.equals("空调设备状况")){
                        miss += haveMiss(equEntity);
                    }else {
                        miss += itemDao.getMissItemList(equEntity.getEqId(), equEntity.getSpotId(), equEntity.isDeviceStatus());
                    }

                }

                emitter.onNext(miss);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<Integer>() {

                    @SuppressLint("DefaultLocale")
                    @Override
                    public void onNext(@NonNull Integer miss) {
                        mSync = haveData2Sync(totalEntities, false);
                        if (miss == 0) {
                            boolean haveUncompletedSpot = false;
                            for (PatrolSpotEntity totalEntity : totalEntities) {
                                if (!(totalEntity.getRemoteCompleted() == DBPatrolConstant.TRUE_VALUE || totalEntity.getCompleted() == DBPatrolConstant.TRUE_VALUE)) {
                                    haveUncompletedSpot = true;
                                    break;
                                }
                            }
                            if (haveUncompletedSpot) {
                                getV().dismissLoading();
                                FMWarnDialogBuilder builder = new FMWarnDialogBuilder(getV().getActivity());
                                builder.setTitle(R.string.patrol_remind);
                                builder.setSure(R.string.patrol_submit);
                                builder.setCancel(R.string.patrol_cancel);
                                builder.setTip(R.string.patrol_task_submit_unfinish_detail_pre);
                                builder.addOnBtnSureClickListener(new FMWarnDialogBuilder.OnBtnClickListener() {
                                    @Override
                                    public void onClick(QMUIDialog dialog, View view) {
                                        dialog.dismiss();
                                        getV().showLoading();
                                        //查询是否有需要同步的数据
                                        if (mSync == null || mSync.size() == 0) {//说明都被同步了
                                            getTaskData();
                                            return;
                                        }
                                        getData(req, mSync, operateType, taskId);
                                    }
                                });
                                builder.create(R.style.fmDefaultWarnDialog).show();
                            } else {
                                getData(req, mSync, operateType, taskId);
                            }

                        } else {
                            getV().dismissLoading();
                            FMWarnDialogBuilder builder = new FMWarnDialogBuilder(getV().getActivity());
                            builder.setTitle(R.string.patrol_remind);
                            builder.setSure(R.string.patrol_submit);
                            builder.setCancel(R.string.patrol_cancel);
                            builder.setTip(String.format(getV().getString(R.string.patrol_task_submit_hip_detail), miss));
                            builder.addOnBtnSureClickListener(new FMWarnDialogBuilder.OnBtnClickListener() {
                                @Override
                                public void onClick(QMUIDialog dialog, View view) {
                                    dialog.dismiss();
                                    getV().showLoading();
                                    //查询是否有需要同步的数据
                                    if (mSync == null || mSync.size() == 0) {//说明都被同步了
                                        getTaskData();
                                        return;
                                    }
                                    getData(req, mSync, operateType, taskId);
                                }
                            });
                            builder.create(R.style.fmDefaultWarnDialog).show();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        LogUtils.d("save onError");
                        getV().dismissLoading();
                        ToastUtils.showShort(R.string.patrol_operate_fail);
                    }

                    @Override
                    public void onComplete() {
                        LogUtils.d("save onComplete");
                    }
                });
    }

    /**
     * 都同步过了直接提交任务信息即可
     */
    private void getTaskData() {
        Observable.create(new ObservableOnSubscribe<PatrolTaskEntity>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<PatrolTaskEntity> emitter) throws Exception {
                PatrolTaskDao taskDao = new PatrolTaskDao();
                PatrolTaskEntity task = taskDao.getTask(getV().getTaskId());
                emitter.onNext(task);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<PatrolTaskEntity>() {
                    @Override
                    public void onNext(@NonNull PatrolTaskEntity patrolTaskEntity) {
                        cancel();
                        if (patrolTaskEntity == null) {
                            getV().dismissLoading();
                            ToastUtils.showShort(R.string.patrol_operate_fail);
                            return;
                        }
                        PatrolSaveReq req = getV().getPatrolSaveReq();
                        req.patrolTask = new PatrolSaveReq.PatrolTaskReq();
                        req.patrolTask.endDateTime = patrolTaskEntity.getEndTime();
                        req.patrolTask.startDateTime = patrolTaskEntity.getStartTime();
                        req.patrolTask.patrolTaskId = patrolTaskEntity.getTaskId();
                        data2Server(req);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        cancel();
                        getV().dismissLoading();
                        ToastUtils.showShort(R.string.patrol_operate_fail);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 获取任务下的点位 停用设备  检查项 组装
     *
     * @param req
     * @param totalEntities
     * @param operateType
     * @param taskId
     */
    private void getData(final PatrolSaveReq req, final List<PatrolSpotEntity> totalEntities, final int operateType, final Long taskId) {
        final Map<PatrolSaveReq.PatrolItemReq, List<PatrolPicEntity>> itemPic = new HashMap<>();
        Observable.create(new ObservableOnSubscribe<PatrolSaveReq>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<PatrolSaveReq> emitter) throws Exception {

                PatrolTaskDao taskDao = new PatrolTaskDao();
                PatrolTaskEntity task = taskDao.getTask(taskId);

                if (task == null) {
                    emitter.onNext(null);
                    emitter.onComplete();
                    return;
                }

                req.patrolTask = new PatrolSaveReq.PatrolTaskReq();
                req.patrolTask.patrolTaskId = taskId;
                req.patrolTask.startDateTime = task.getStartTime();
                req.patrolTask.endDateTime = task.getEndTime();

                if (totalEntities != null && totalEntities.size() > 0) {
                    PatrolDeviceDao deviceDao = new PatrolDeviceDao();
                    PatrolItemDao itemDao = new PatrolItemDao();
                    PatrolPicDao picDao = new PatrolPicDao();

                    req.patrolTask.spots = new ArrayList<>();
                    for (PatrolSpotEntity totalEntity : totalEntities) {
                        PatrolSaveReq.PatrolSpotReq spot = new PatrolSaveReq.PatrolSpotReq();
                        spot.endDateTime = (totalEntity.getEndTime() == null || totalEntity.getEndTime() == 0L) ? null : totalEntity.getEndTime();
                        spot.startDateTime = (totalEntity.getStartTime() == null || totalEntity.getStartTime() == 0L) ? null : totalEntity.getStartTime();
                        spot.patrolSpotId = totalEntity.getPatrolSpotId();
                        spot.finished = totalEntity.getCompleted() == DBPatrolConstant.TRUE_VALUE;
                        req.patrolTask.spots.add(spot);

                        List<PatrolEquEntity> deviceList = deviceDao.getDeviceList(totalEntity.getPatrolSpotId());

                        if (deviceList != null && deviceList.size() > 0) {
                            spot.exceptionEquipment = new ArrayList<>();
                            spot.contents = new ArrayList<>();
                            for (PatrolEquEntity equEntity : deviceList) {
                                if (!equEntity.isDeviceStatus()) {
                                    PatrolSaveReq.PatrolEquReq equReq = new PatrolSaveReq.PatrolEquReq();
                                    equReq.eqId = equEntity.getEqId();
                                    equReq.status = PatrolConstant.EQU_STOP;
                                    spot.exceptionEquipment.add(equReq);
                                }

                                List<PatrolItemEntity> itemList = itemDao.getItemList(equEntity.getEqId(), totalEntity.getPatrolSpotId(), equEntity.isDeviceStatus());

                                if (itemList != null && itemList.size() > 0) {
                                    for (PatrolItemEntity patrolItemEntity : itemList) {
                                        PatrolSaveReq.PatrolItemReq item = new PatrolSaveReq.PatrolItemReq();
                                        item.comment = patrolItemEntity.getComment();
                                        item.patrolTaskSpotResultId = patrolItemEntity.getContentResultId();
                                        try {
                                            /**
                                             * 输入类型以及文本类型 传值对应对象不同
                                             * */
                                            if (patrolItemEntity.getResultType() == PatrolDbService.QUESTION_TYPE_INPUT) {

                                                if (!TextUtils.isEmpty(patrolItemEntity.getInput())) {
                                                    item.resultInput = Double.parseDouble(patrolItemEntity.getInput());
                                                }
                                            }else if(patrolItemEntity.getResultType() == PatrolDbService.QUESTION_TYPE_TEXT){
                                                if (!TextUtils.isEmpty(patrolItemEntity.getInput())){
                                                    item.resultText = patrolItemEntity.getInput()+"";
                                                }
                                            } else {
                                                item.resultSelect = patrolItemEntity.getSelect();
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        List<PatrolPicEntity> picSyncList = picDao.getPicSyncList(patrolItemEntity.getContentResultId());
                                        if (picSyncList != null && picSyncList.size() > 0) {
                                            item.photoIds = new ArrayList<>();
                                            List<PatrolPicEntity> t = new ArrayList<>();
                                            for (PatrolPicEntity patrolPicEntity : picSyncList) {
                                                if (patrolPicEntity.getSrc() != null) {//有图片id了
                                                    item.photoIds.add(patrolPicEntity.getSrc());
                                                } else {
                                                    t.add(patrolPicEntity);
                                                }
                                            }

                                            itemPic.put(item, t);
                                        }
                                        spot.contents.add(item);
                                    }
                                }

                            }
                        }

                    }
                }

                emitter.onNext(req);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<PatrolSaveReq>() {
                    @Override
                    public void onNext(@NonNull final PatrolSaveReq req) {
                        cancel();
                        if (req == null) {
                            getV().dismissLoading();
                            ToastUtils.showShort(R.string.patrol_operate_fail);
                        } else {
                            if (itemPic.size() > 0) {
                                Set<PatrolSaveReq.PatrolItemReq> patrolItemReqs = itemPic.keySet();
                                for (PatrolSaveReq.PatrolItemReq patrolItemReq : patrolItemReqs) {
                                    List<PatrolPicEntity> patrolPicEntities = itemPic.get(patrolItemReq);
                                    //上传图片
                                    uploadFile(patrolPicEntities, patrolItemReq, itemPic.size());
                                }
                            } else {
                                //没有检查项 -- 直接网络请求
                                data2Server(req);
                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        cancel();
                        getV().dismissLoading();
                        ToastUtils.showShort(R.string.patrol_operate_fail);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /*********************上传图片****************************/
    public void uploadFile(final List<PatrolPicEntity> paths, final PatrolSaveReq.PatrolItemReq itemReq, final int total) {
        final String userName = SPUtils.getInstance(SPKey.SP_MODEL).getString(SPKey.USERNAME);
        final String password = SPUtils.getInstance(SPKey.SP_MODEL).getString(SPKey.PASSWORD);
        LogonManager.getInstance().logon(userName, password, new JsonCallback<BaseResponse<LogonResponse>>() {

            @Override
            public void onStart(Request<BaseResponse<LogonResponse>, ? extends Request> request) {
                getV().showLoading();
            }

            @Override
            public void onSuccess(Response<BaseResponse<LogonResponse>> response) {
                BaseResponse<LogonResponse> body = response.body();
                if (body.data != null) {
                    LogonManager.getInstance().saveToken(body.data);
                    SPUtils.getInstance(SPKey.SP_MODEL).put(SPKey.USERNAME, userName);
                    SPUtils.getInstance(SPKey.SP_MODEL).put(SPKey.PASSWORD, password);
                }

                uploadFiles(paths,itemReq,total);
            }


            @Override
            public void onError(Response<BaseResponse<LogonResponse>> response) {
                super.onError(response);
                uploadFiles(paths,itemReq,total);
            }
        });

    }

    public void uploadFiles(final List<PatrolPicEntity> paths, final PatrolSaveReq.PatrolItemReq itemReq, final int total) {
        if (paths == null || paths.size() == 0) {
            picTotal++;
            if (picTotal == total) {
                data2Server(getV().getPatrolSaveReq());
            }
            return;
        }

        PostRequest<BaseResponse<List<String>>> request = OkGo.<BaseResponse<List<String>>>post(FM.getApiHost() + CommonUrl.UPLOAD_IMAGE_URL)
                .tag(getV())
                .isSpliceUrl(true);

        for (PatrolPicEntity pic : paths) {
            String path = pic.getPath();
            if (!TextUtils.isEmpty(path)) {
                File file = new File(path);
                if (file.exists()) {
                    request.params("file-" + file.getName(), file);
                }
            }

        }

        request.execute(new FMJsonCallback<BaseResponse<List<String>>>() {
            @Override
            public void onSuccess(Response<BaseResponse<List<String>>> response) {
                picTotal++;
                List<String> data = response.body().data;
                if (data != null && data.size() > 0) {
                    itemReq.photoIds.addAll(data);
                    for (int i = 0; i < data.size(); i++) {
                        String aLong = data.get(i);
                        if (i < paths.size()) {
                            paths.get(i).setSrc(aLong);
                        }
                    }

                    PatrolPicDao picDao = new PatrolPicDao();
                    picDao.update(paths);
                }
                if (picTotal == total) {
                    data2Server(getV().getPatrolSaveReq());
                }
            }

            @Override
            public void onError(Response<BaseResponse<List<String>>> response) {
                super.onError(response);
                picTotal++;
                if (picTotal == total) {
                    data2Server(getV().getPatrolSaveReq());
                }
            }

            @Override
            public void onFinish() {
            }
        });
    }
    private List<PatrolSpotEntity> haveData2Sync(List<PatrolSpotEntity> totalEntities, boolean sync) {
        List<PatrolSpotEntity> temp = null;
        if (totalEntities != null && totalEntities.size() > 0) {
            temp = new ArrayList<>();
            for (PatrolSpotEntity totalEntity : totalEntities) {
                if (sync) {
                    if (totalEntity.getNeedSync() == DBPatrolConstant.TRUE_VALUE) {
                        temp.add(totalEntity);
                    }
                } else {
                    if (!(totalEntity.getNeedSync() == DBPatrolConstant.FALSE_VALUE ||
                            (totalEntity.getNeedSync() == DBPatrolConstant.DEFAULT_VALUE
                                    && totalEntity.getRemoteCompleted() == DBPatrolConstant.TRUE_VALUE))) {
                        temp.add(totalEntity);
                    }
                }

            }
        }

        return temp;
    }

    /**
     * 组装后的req上传到服务器
     *
     * @param req
     */
    private void data2Server(final PatrolSaveReq req) {
        if (req.operateType == PatrolConstant.PATROL_OPT_TYPE_UPLOAD) {
            if (req.patrolTask.startDateTime == 0) {
                req.patrolTask.startDateTime = System.currentTimeMillis();
            }

            req.patrolTask.endDateTime = System.currentTimeMillis();
            //测试说提交的时候 实际完成时间取提交巡检任务的时间
//            if (req.patrolTask.endDateTime == 0) {
//                req.patrolTask.endDateTime = System.currentTimeMillis();
//            }

            if (req.patrolTask.startDateTime > req.patrolTask.endDateTime) {
                req.patrolTask.endDateTime = req.patrolTask.startDateTime;
            }
        } else {
            req.patrolTask.endDateTime = null;
        }
        OkGo.<BaseResponse<Object>>post(FM.getApiHost() + PatrolUrl.PATROL_TASK_UPLOAD)
                .tag(getV())
                .isSpliceUrl(true)
                .upJson(toJson(req))
                .execute(new FMJsonCallback<BaseResponse<Object>>() {
                    @Override
                    public void onSuccess(Response<BaseResponse<Object>> response) {
                        if (req.operateType == PatrolConstant.PATROL_OPT_TYPE_SYNC) {
                            endOpt(true, new ObservableOnSubscribe<Boolean>() {
                                @Override
                                public void subscribe(@NonNull ObservableEmitter<Boolean> emitter) throws Exception {
                                    //同步
                                    if (mSync != null && mSync.size() > 0) {
                                        PatrolSpotDao spotDao = new PatrolSpotDao();
                                        for (PatrolSpotEntity patrolSpotEntity : mSync) {
                                            patrolSpotEntity.setNeedSync(DBPatrolConstant.FALSE_VALUE);
                                            spotDao.update(patrolSpotEntity.getPatrolSpotId(), patrolSpotEntity.getCompleted(), patrolSpotEntity.getException(), patrolSpotEntity.getNeedSync(), patrolSpotEntity.getStartTime(), patrolSpotEntity.getEndTime());
                                        }
                                    }
                                    PatrolTaskDao taskDao = new PatrolTaskDao();
                                    taskDao.updateSync(DBPatrolConstant.FALSE_VALUE, getV().getTaskId());
                                    emitter.onNext(true);
                                    emitter.onComplete();
                                }
                            });
                        } else {
                            //删除任务相关
                            endOpt(false, new ObservableOnSubscribe<Boolean>() {
                                @Override
                                public void subscribe(@NonNull ObservableEmitter<Boolean> emitter) throws Exception {
                                    Long patrolTaskId = req.patrolTask.patrolTaskId;
                                    List<Long> deletedId = new ArrayList<>();
                                    deletedId.add(patrolTaskId);
                                    PatrolTaskDao taskDao = new PatrolTaskDao();
                                    taskDao.deleteTask(deletedId);
                                    emitter.onNext(true);
                                    emitter.onComplete();
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(Response<BaseResponse<Object>> response) {
                        super.onError(response);
                        getV().dismissLoading();
                        ToastUtils.showShort(R.string.patrol_operate_fail);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                    }
                });
    }

    private void endOpt(final boolean sync, ObservableOnSubscribe<Boolean> observer) {
        Observable.create(observer).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<Boolean>() {
                    @Override
                    public void onNext(@NonNull Boolean aBoolean) {
                        endOptAfter(sync);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        endOptAfter(sync);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 上传服务器之后的操作
     *
     * @param sync
     */
    private void endOptAfter(boolean sync) {
        ToastUtils.showShort(R.string.patrol_operate_ok);
        getV().dismissLoading();
        if (!sync) {
            getV().popResult();
        } else {
            getV().refresh();
        }
    }

    /**
     * 二维码扫描相关逻辑
     */
    public void scan(final PatrolSpotEntity spotEntity,Long time) {
        Intent intent = new Intent(getV().getContext(), FzScanActivity.class);
        ActivityLauncher.init(getV().getActivity())
                .startActivityForResult(intent, new ActivityLauncher.Callback() {
                    @Override
                    public void onActivityResult(int resultCode, Intent data) {
                        if (data != null){
                            HmsScan result=data.getParcelableExtra("scanResult");
                            if (result!=null){
                                if (result.originalValue != null){
                                    String spotCode = PatrolQrcodeUtils.parseSpotCode(result.originalValue);
                                    if (!spotCode.equals(spotEntity.getCode())){
                                        ToastUtils.showShort("点位匹配出错，请确认点位");
                                        return;
                                    }
                                    getV().scanResult(spotEntity,time);
                                }else {
                                    ToastUtils.showShort(R.string.patrol_qrcode_no_match);
                                }
                            }
                        }

                    }
                });
    }



    /**
     * 二维码扫描相关逻辑
     */
    public void workingScan(final PatrolSpotEntity spotEntity,Long time) {
        Intent intent = new Intent(getV().getContext(), ScanActivity.class);
        getV().startActivity(intent);

        ScanActivity.setOnScanResultListener(new ScanActivity.OnScanResultListener() {
            @Override
            public void success(String QRCode) {
                LogUtils.d("TAG", "扫描结果==" + QRCode);
                String spotCode = PatrolQrcodeUtils.parseSpotCode(QRCode);
                if (TextUtils.isEmpty(QRCode)) {
                    ToastUtils.showShort(R.string.patrol_qrcode_no_match);
                    return;
                }
                if (!spotCode.equals(spotEntity.getCode())){
                    ToastUtils.showShort("点位匹配出错，请确认点位");
                    return;
                }
                getV().workingScanResult(spotEntity,time);
            }
        });
    }



    /**
     * @Created by: kuuga
     * @Date: on 2021/8/27 16:27
     * @Description: 判断巡检任务点位可继续性
     */
    public void judgeTask(PatrolSpotEntity entity,boolean needScan){
        PatrolQueryService.PatrolJudgeReq req=new PatrolQueryService.PatrolJudgeReq();
        req.patrolTaskId=entity.getTaskId();
        req.patrolTaskSpotId=entity.getPatrolSpotId();
//        jsonObject.put("patrolTaskSpotId", entity.getPatrolSpotId());
//        jsonObject.put("patrolTaskId", entity.getTaskId());
        if (!NetworkUtils.isConnected()){
            if (judgeTaskOutLine(entity)){
                taskStart(Long.parseLong(entity.getTaskTime()+""),entity,needScan);
            }else {
                //当前有任务在执行
                getV().showOrderTimeDialog(getLastTime());
            }
        }else {
            OkGo.<BaseResponse<PatrolQueryService.PatrolJudgeBean>>post(FM.getApiHost() + PatrolUrl.PATROL_JUDGE_TASK)
                    .isSpliceUrl(true)
                    .upJson(toJson(req))
                    .tag(getV())
                    .execute(new FMJsonCallback<BaseResponse<PatrolQueryService.PatrolJudgeBean>>() {
                        @Override
                        public void onSuccess(Response<BaseResponse<PatrolQueryService.PatrolJudgeBean>> response) {
                            getV().dismissLoading();
                            PatrolQueryService.PatrolJudgeBean data = response.body().data;
                            if (data != null) {
                                //是否可以开启新任务
                                if (data.executable){
                                    if (data.time!=0){
                                        //判断是否需要扫码,不需要则直接进去
                                        if (!needScan){
                                            getV().showOrderTimeDialog(data.time,entity);
                                        }else {
                                            getV().needScanQrcode(entity,data.time);
                                        }
                                    }else {
                                        //判断是否需要扫码,不需要则直接进去
                                        if (!needScan){
                                            getV().enterDeviceList(entity);
                                        }else {
                                            getV().needScanQrcode(entity,data.time);

                                        }

                                    }

                                }else {
                                    //当前有任务是进入,判断是否同一个任务及点位
                                    if (data.patrolTaskId.equals(entity.getTaskId())&&data.patrolTaskSpotId.equals(entity.getPatrolSpotId())){

                                        if (!needScan){
                                            getV().enterDeviceList(entity);
                                        }else {
                                            getV().needWorkingScanQrcode(entity,data.time);
                                        }


                                        //非同一个任务及点位时,提示当前有任务进行以及剩余时间
                                    }else {
                                        getV().showOrderTimeDialog(data.time);
                                    }
                                }
                            }
                        }
                    });
        }

    }

    public Long  getLastTime(){
        Long oneMin = 60000L; //一分钟时间戳
        Long timeForNow = SystemDateUtils.getCurrentTimeMillis();
        boxTask = ObjectBox.INSTANCE.getBoxStore().boxFor(CompleteTime.class);
        Query<CompleteTime> query = boxTask.query().equal(CompleteTime_.taskTip,PatrolConstant.PATROL_TASK_OUTLINE).build();
        CompleteTime db = query.findFirst();
        if (db == null){
            return 0L;
        }else {
            Long startTime = db.getStarTime()+oneMin * db.getCheckTime();
            if (timeForNow >= startTime){
                return 0L;
            }else {
                return (startTime-timeForNow)/oneMin ;
            }
        }
    }

    /**
     * @Creator:Karelie
     * @Data: 2021/12/14
     * @TIME: 15:52
     * @Introduce: 执行开启任务
    **/
    public void taskStart(Long time,PatrolSpotEntity entity,boolean needScan){
        if (time != 0){
            //判断是否需要扫码,不需要则直接进去
            if (!needScan){
                //任务已完成
                if (entity.getCompleted() == DBPatrolConstant.TRUE_VALUE ||
                        entity.getRemoteCompleted() == DBPatrolConstant.TRUE_VALUE){
                    getV().enterDeviceList(entity);
                }else {
                    getV().showOrderTimeDialog(time,entity);
                }

            }else {
                getV().needScanQrcode(entity,time);
            }
        }else {
            //判断是否需要扫码,不需要则直接进去
            if (!needScan){
                getV().enterDeviceList(entity);
            }else {
                getV().needScanQrcode(entity,time);
            }
        }
    }

    /**
     * @Creator:Karelie
     * @Data: 2021/12/14
     * @TIME: 11:45
     * @Introduce: 离线查询数据库是否可以开启任务
    **/
    public boolean judgeTaskOutLine(PatrolSpotEntity entity){
        Long timeForNow = SystemDateUtils.getCurrentTimeMillis();
        boxTask = ObjectBox.INSTANCE.getBoxStore().boxFor(CompleteTime.class);

        Query<CompleteTime> query = boxTask.query().equal(CompleteTime_.taskTip,PatrolConstant.PATROL_TASK_OUTLINE).build();
        CompleteTime db = query.findFirst();
        if (db == null){
            return true;
        }else {
            if (entity.getPatrolSpotId() == db.getPatrolSpotId()
                    &&
                    entity.getSpotId() == db.getSportId()){
                return true;
            }else {
                if (timeForNow >= (db.getStarTime()+db.getCheckTime())){
                    return true;
                }else {
                    return false;
                }
            }
        }
    }


    /**
     * @Created by: kuuga
     * @Date: on 2021/8/30 10:25
     * @Description: 提交任务时判断
     */
    public void submitTask(Long taskId,int type){
        Map<String, Object> jsonObject = new HashMap<>();
        jsonObject.put("patrolTaskId", taskId);
        OkGo.<BaseResponse<PatrolQueryService.PatrolJudgeBean>>post(FM.getApiHost() + PatrolUrl.PATROL_JUDGE_TASK)
                .tag(getV())
                .upJson(toJson(jsonObject))
                .execute(new FMJsonCallback<BaseResponse<PatrolQueryService.PatrolJudgeBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponse<PatrolQueryService.PatrolJudgeBean>> response) {
                        getV().dismissLoading();
                        PatrolQueryService.PatrolJudgeBean data = response.body().data;
                        if (data != null) {
                            if (data.executable) {
                                getV().syncDta(type);
                            } else {
                                getV().showOrderTimeDialog(data.time);
                            }
                        }
                    }
                });

    }

    /**
     * @Created by: kuuga
     * @Date: on 2021/8/27 16:27
     * @Description:  执行任务
     */
    public void executeTask(PatrolSpotEntity entity){
//        Map<String, Object> jsonObject = new HashMap<>();
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
     * @Date: on 2021/8/25 10:40
     * @Description: 获取最后一次签到记录
     */
    public void getLastAttendance(){
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
                        UserInfor user = new UserInfor();
                        box.removeAll();
                        user.setId(0L);
                        user.setUserKey(PatrolConstant.USERLOGIN_ID);
                        if (data.location != null){
                            user.setLocationBean(data.location);
                            user.setBuidlings(data.buildingIds);
                        }
                        box.put(user);
                        Log.e("LAST_ATTENDANCE","===============***============="+user.toString());
                        if(data != null) {
                            getV().hasAttentanceData(true);
                            getV().saveAttentanceLocation(data);
                        }else {
                            if (!infoBean.type.equals(1)){
                                getV().hasAttentanceData(true);
                                getV().saveAttentanceLocation(data);
                            }else {
                                getV().hasAttentanceData(false);
                            }

                        }
                    }

                    @Override
                    public void onError(Response<BaseResponse<PatrolQueryService.AttendanceResp>> response) {
                        super.onError(response);
                        getV().dismissLoading();
                    }
                });




    }


    /**
     * @Creator:Karelie
     * @Data: 2021/12/14
     * @TIME: 9:31
     * @Introduce: 离线状态下 获取最后一次签到记录数据
    **/
    public void getLastAttendanceOutLine(){
        box = ObjectBox.INSTANCE.getBoxStore().boxFor(UserInfor.class);
        PatrolQueryService.AttendanceResp data = new PatrolQueryService.AttendanceResp();
        Query<UserInfor> query = box.query().equal(UserInfor_.userKey,PatrolConstant.USERLOGIN_ID).build();
        List<UserInfor> user = query.find();
        LocationBean locationData = new LocationBean();
        locationData = user.get(0).getLocationBean();
        data.location = locationData;
        data.buildingIds = user.get(0).getBuidlings();
        getV().hasAttentanceData(true);
        getV().saveAttentanceLocation(data);

    }


}
