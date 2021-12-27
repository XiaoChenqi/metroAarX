package com.facilityone.wireless.patrol.presenter;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.facilityone.wireless.a.arch.base.FMJsonCallback;
import com.facilityone.wireless.a.arch.mvp.BasePresenter;
import com.facilityone.wireless.a.arch.offline.dao.BuildingDao;
import com.facilityone.wireless.a.arch.offline.dao.PatrolDeviceDao;
import com.facilityone.wireless.a.arch.offline.dao.PatrolItemDao;
import com.facilityone.wireless.a.arch.offline.dao.PatrolPicDao;
import com.facilityone.wireless.a.arch.offline.dao.PatrolSpotDao;
import com.facilityone.wireless.a.arch.offline.model.entity.DBPatrolConstant;
import com.facilityone.wireless.a.arch.offline.model.entity.PatrolEquEntity;
import com.facilityone.wireless.a.arch.offline.model.entity.PatrolItemEntity;
import com.facilityone.wireless.a.arch.offline.model.entity.PatrolPicEntity;
import com.facilityone.wireless.a.arch.offline.model.entity.PatrolSpotEntity;
import com.facilityone.wireless.a.arch.offline.model.service.PatrolDbService;
import com.facilityone.wireless.basiclib.app.FM;
import com.facilityone.wireless.patrol.fragment.PatrolItemFragment;
import com.facilityone.wireless.patrol.module.PatrolConstant;
import com.facilityone.wireless.patrol.module.PatrolQueryService;
import com.facilityone.wireless.patrol.module.PatrolUrl;
import com.fm.tool.network.model.BaseResponse;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * description:巡检检查项
 * Date: 2018/11/8 5:12 PM
 */
public class PatrolItemPresenter extends BasePresenter<PatrolItemFragment> {

    private Long mStartTime;

    public PatrolItemPresenter() {
        mStartTime = System.currentTimeMillis();
    }

    public void getPatrolItemList(final PatrolEquEntity equEntity) {
        Observable.create(new ObservableOnSubscribe<List<PatrolItemEntity>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<PatrolItemEntity>> emitter) throws Exception {
                PatrolItemDao dao = new PatrolItemDao();
                List<PatrolItemEntity> itemList = dao.getItemList(equEntity.getEqId(), equEntity.getSpotId(), equEntity.isDeviceStatus());
                if (itemList != null && itemList.size() > 0) {
                    PatrolPicDao picDao = new PatrolPicDao();
                    for (PatrolItemEntity patrolItemEntity : itemList) {
                        if (equEntity.getCompleted() != DBPatrolConstant.TRUE_VALUE) {
                            if (!patrolItemEntity.getContent().equals("车站工况")){
                                patrolItemEntity.setSelect(patrolItemEntity.getDefaultSelectValue());
                            }
                            patrolItemEntity.setInput(patrolItemEntity.getDefaultInputValue() == null ? "" : patrolItemEntity.getDefaultInputValue().toString());
                        }
                        List<PatrolPicEntity> p = picDao.getPicSyncList(patrolItemEntity.getContentResultId());
                        patrolItemEntity.setPicEntities(p);
                    }
                }
                emitter.onNext(itemList);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<List<PatrolItemEntity>>() {
                    @Override
                    public void onNext(@NonNull List<PatrolItemEntity> itemEntities) {
                        getV().refreshUI(itemEntities);
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
     * 保存数据
     *
     * @param itemEntities
     * @param miss
     * @param lastOne
     * @param change
     * @param back
     */
    public void saveData2Db(List<PatrolItemEntity> itemEntities, final List<PatrolEquEntity> equs, int devicePosition,
                            final boolean miss, final boolean lastOne, final boolean change, final boolean back) {
        if (itemEntities == null) {
            itemEntities = new ArrayList<>();
        }
        final PatrolEquEntity equEntity = equs.get(devicePosition);
        equEntity.setMiss(miss);
        getV().showLoading();
        final List<PatrolItemEntity> finalItemEntities = itemEntities;
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> emitter) throws Exception {
                boolean exception = false;
                PatrolDeviceDao deviceDao = new PatrolDeviceDao();
                PatrolSpotDao spotDao = new PatrolSpotDao();
                PatrolItemDao itemDao = new PatrolItemDao();
                PatrolPicDao picDao = new PatrolPicDao();
                for (PatrolItemEntity itemEntity : finalItemEntities) {
                    List<Long> picIds = itemEntity.getPicIds();
                    if (picIds != null && picIds.size() > 0) {
                        picDao.deleteItemPic(picIds);
                    }
                    List<PatrolPicEntity> picEntityList = itemEntity.getPicEntities();
                    if (picEntityList != null && picEntityList.size() > 0) {
                        List<PatrolPicEntity> pics = new ArrayList<>();
                        for (PatrolPicEntity patrolPicEntity : picEntityList) {
                            if (patrolPicEntity.getId() == null) {
                                patrolPicEntity.setTaskId(itemEntity.getTaskId());
                                patrolPicEntity.setItemId(itemEntity.getContentResultId());
                                pics.add(patrolPicEntity);
                            }
                        }
                        picDao.addItemPic(pics);
                    }
                    if (!exception) {
                        switch (itemEntity.getResultType()) {
                            //输入
                            case PatrolDbService.QUESTION_TYPE_INPUT:
                                String input = itemEntity.getInput();
                                PatrolItemEntity dbFirst = finalItemEntities.get(0);

                                if (dbFirst.getContent().equals("车站工况")){
                                    if (dbFirst.getSelect().equals("通风")){
                                        if (itemEntity.getResultType() != null
                                                && ((itemEntity.getValidStatus()==  PatrolConstant.EQU_STOP
                                                || itemEntity.getValidStatus() == PatrolConstant.EQU_ALL))){
                                            if (!TextUtils.isEmpty(input)) {
                                                Double d = 0D;
                                                try {
                                                    d = Double.parseDouble(input);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                                if (itemEntity.getInputUpper() != null) {
                                                    if (d > itemEntity.getInputUpper()) {
                                                        exception = true;
                                                    }
                                                }
                                                if (itemEntity.getInputFloor() != null) {
                                                    if (d < itemEntity.getInputFloor()) {
                                                        exception = true;
                                                    }
                                                }
                                            }
                                        }
                                    }else if (dbFirst.getSelect().equals( "空调")){
                                        if (itemEntity.getResultType() != null  &&
                                                ((itemEntity.getValidStatus()==  PatrolConstant.EQU_USE
                                                        || itemEntity.getValidStatus() == PatrolConstant.EQU_ALL))){
                                            if (!TextUtils.isEmpty(input)) {
                                                Double d = 0D;
                                                try {
                                                    d = Double.parseDouble(input);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                                if (itemEntity.getInputUpper() != null) {
                                                    if (d > itemEntity.getInputUpper()) {
                                                        exception = true;
                                                    }
                                                }
                                                if (itemEntity.getInputFloor() != null) {
                                                    if (d < itemEntity.getInputFloor()) {
                                                        exception = true;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }else {
                                    if (!TextUtils.isEmpty(input)) {
                                        Double d = 0D;
                                        try {
                                            d = Double.parseDouble(input);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        if (itemEntity.getInputUpper() != null) {
                                            if (d > itemEntity.getInputUpper()) {
                                                exception = true;
                                            }
                                        }
                                        if (itemEntity.getInputFloor() != null) {
                                            if (d < itemEntity.getInputFloor()) {
                                                exception = true;
                                            }
                                        }
                                    }
                                }

                                break;
                            //单选
                            case PatrolDbService.QUESTION_TYPE_SINGLE:
                                if (TextUtils.isEmpty(itemEntity.getSelect())) {
                                    if (itemEntity.getContent().equals("车站工况")){
                                        itemEntity.setSelect(itemEntity.getSelect());
                                    }else {
                                        itemEntity.setSelect(itemEntity.getSelectRightValue() == null ? "" : itemEntity.getSelectRightValue());
                                    }

                                }
                                PatrolItemEntity dbOne = finalItemEntities.get(0);
                                String[] right = itemEntity.getSelectRightValue().split(",");
                                if (itemEntity.getSelectRightValue() != null && !TextUtils.isEmpty(itemEntity.getSelectRightValue())) {
                                    boolean ex = true;
                                    if (dbOne.getContent().equals("车站工况")){
                                        if (itemEntity.getContent().equals("车站工况")){
                                            ex = false;
                                        }else {
                                            if (!TextUtils.isEmpty(dbOne.getSelect())){
                                                if (dbOne.getSelect().equals("通风")){
                                                    if (itemEntity.getResultType() != null
                                                            && ((itemEntity.getValidStatus()==  PatrolConstant.EQU_STOP
                                                            || itemEntity.getValidStatus() == PatrolConstant.EQU_ALL))){
                                                        for (String s : right) {
                                                            if (s.equals(itemEntity.getSelect())) {
                                                                ex = false;
                                                                break;
                                                            }
                                                        }
                                                    }else {
                                                        ex = false;
                                                    }
                                                }else if (dbOne.getSelect().equals( "空调")){
                                                    if (itemEntity.getResultType() != null  &&
                                                            ((itemEntity.getValidStatus()==  PatrolConstant.EQU_USE
                                                                    || itemEntity.getValidStatus() == PatrolConstant.EQU_ALL))){
                                                        for (String s : right) {
                                                            if (s.equals(itemEntity.getSelect())) {
                                                                ex = false;
                                                                break;
                                                            }
                                                        }
                                                    }else {
                                                        ex = false;
                                                    }
                                                }
                                            }else {
                                                if (itemEntity.getResultType() != null  &&
                                                        ((itemEntity.getValidStatus()==  PatrolConstant.EQU_USE
                                                                || itemEntity.getValidStatus() == PatrolConstant.EQU_STOP
                                                                || itemEntity.getValidStatus() == PatrolConstant.EQU_ALL))){
                                                    for (String s : right) {
                                                        if (s.equals(itemEntity.getSelect())) {
                                                            ex = false;
                                                            break;
                                                        }
                                                    }
                                                }else {
                                                    ex = false;
                                                }
                                            }

                                        }
                                    }else {
                                        for (String s : right) {
                                            if (s.equals(itemEntity.getSelect())) {
                                                ex = false;
                                                break;
                                            }
                                        }
                                    }
                                    if (ex) {
                                        exception = true;
                                    }
                                }
                                break;
                            case PatrolDbService.QUESTION_TYPE_TEXT:
                                exception = false;

                                break;
                            default:
                                break;
                        }
                    }
                }

                itemDao.updatePatrolItem(finalItemEntities);
                equEntity.setCompleted(DBPatrolConstant.TRUE_VALUE);
                PatrolItemEntity enity = null;


                equEntity.setException(exception ? DBPatrolConstant.TRUE_VALUE : DBPatrolConstant.FALSE_VALUE);

                deviceDao.updateStatus(equEntity.getEqId(), equEntity.getSpotId(), equEntity.isDeviceStatus(), equEntity.isMiss(), equEntity.getException(), equEntity.getCompleted());
                PatrolSpotEntity spot = spotDao.getSpot(equEntity.getSpotId());
                if (spot != null) {
                    boolean spotException = false;
                    int completedCount = 0;
                    for (PatrolEquEntity equ : equs) {
                        if (equ.getException() == DBPatrolConstant.TRUE_VALUE) {
                            spotException = true;
                        }

                        if (equ.getCompleted() == DBPatrolConstant.TRUE_VALUE) {
                            completedCount++;
                        }
                    }

                    if (completedCount == equs.size() && lastOne) {
                        spot.setCompleted(DBPatrolConstant.TRUE_VALUE);
                    }

                    if (spot.getNeedSync() != DBPatrolConstant.TRUE_VALUE && change) {
                        spot.setNeedSync(DBPatrolConstant.TRUE_VALUE);
                    }

                    if (lastOne) {
                        spot.setEndTime(System.currentTimeMillis());
                    }

                    if (change) {
                        if (spot.getEndTime() != 0L) {
                            spot.setEndTime(System.currentTimeMillis());
                        }
                    }

                    if (spot.getStartTime() == 0) {
                        spot.setStartTime(mStartTime);
                    }

                    spot.setException(spotException ? DBPatrolConstant.TRUE_VALUE : DBPatrolConstant.FALSE_VALUE);
                    spotDao.update(equEntity.getSpotId(), spot.getCompleted(), spot.getException(), spot.getNeedSync(), spot.getStartTime(), spot.getEndTime());
                }

                emitter.onNext(true);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<Boolean>() {
                    @Override
                    public void onNext(@NonNull Boolean result) {
                        cancel();
                        if (lastOne || back) {
                            getV().popResult();
                        } else {
                            getV().getItemList();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        cancel();
                        if (lastOne || back) {
                            getV().popResult();
                        } else {
                            getV().getItemList();
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    public int haveMiss(List<PatrolItemEntity> itemEntities,Integer choice) {
        if (choice == -1){
            for (PatrolItemEntity itemEntity : itemEntities) {
                if (itemEntity.getResultType() != null
                        && (itemEntity.getResultType() == PatrolDbService.QUESTION_TYPE_INPUT
                        ||itemEntity.getResultType() == PatrolDbService.QUESTION_TYPE_TEXT
                )
                        && TextUtils.isEmpty(itemEntity.getInput())) {
                    return itemEntities.indexOf(itemEntity);
                }
            }
        }else if (choice == 1){
            for (PatrolItemEntity itemEntity : itemEntities) {
                if (itemEntity.getResultType() != null
                        && (itemEntity.getResultType() == PatrolDbService.QUESTION_TYPE_INPUT
                        ||itemEntity.getResultType() == PatrolDbService.QUESTION_TYPE_TEXT
                ) && TextUtils.isEmpty(itemEntity.getInput()) && ((itemEntity.getValidStatus()==  PatrolConstant.EQU_STOP
                        || itemEntity.getValidStatus() == PatrolConstant.EQU_ALL))) {
                    return itemEntities.indexOf(itemEntity);
                }
            }
        }else if (choice == 2){
            for (PatrolItemEntity itemEntity : itemEntities) {
                if (itemEntity.getResultType() != null
                        && (itemEntity.getResultType() == PatrolDbService.QUESTION_TYPE_INPUT
                        ||itemEntity.getResultType() == PatrolDbService.QUESTION_TYPE_TEXT
                ) && TextUtils.isEmpty(itemEntity.getInput()) && ((itemEntity.getValidStatus()==  PatrolConstant.EQU_USE
                        || itemEntity.getValidStatus() == PatrolConstant.EQU_ALL))) {
                    return itemEntities.indexOf(itemEntity);
                }
            }
        }else {
            return -1;
        }
        return -1;
    }

    //判断点位任务是否可执行
    public void judgeTask(Long spotJobId,@Nullable Integer position){
        Map<String, Object> jsonObject = new HashMap<>();
        jsonObject.put("patrolTaskSpotId", spotJobId);
        OkGo.<BaseResponse<PatrolQueryService.PatrolJudgeBean>>post(FM.getApiHost() + PatrolUrl.PATROL_JUDGE_TASK)
                .tag(getV())
                .upJson(toJson(jsonObject))
                .execute(new FMJsonCallback<BaseResponse<PatrolQueryService.PatrolJudgeBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponse<PatrolQueryService.PatrolJudgeBean>> response) {
                        getV().dismissLoading();
                        PatrolQueryService.PatrolJudgeBean data = response.body().data;
                        if (data != null) {
                            if (!data.executable){
                                String time=String.valueOf(data.time/60L);
                                getV().showOrderTimeDialog(time);
                            }
                        }
                    }
                });
    }
}
