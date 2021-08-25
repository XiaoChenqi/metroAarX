package com.facilityone.wireless.patrol.presenter;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.SPUtils;
import com.facilityone.wireless.a.arch.base.FMJsonCallback;
import com.facilityone.wireless.a.arch.ec.utils.SPKey;
import com.facilityone.wireless.a.arch.mvp.BasePresenter;
import com.facilityone.wireless.a.arch.offline.dao.PatrolDeviceDao;
import com.facilityone.wireless.a.arch.offline.model.entity.PatrolEquEntity;
import com.facilityone.wireless.a.arch.offline.model.service.PatrolDbService;
import com.facilityone.wireless.basiclib.app.FM;
import com.facilityone.wireless.patrol.fragment.PatrolDeviceFragment;
import com.facilityone.wireless.patrol.module.PatrolConstant;
import com.facilityone.wireless.patrol.module.PatrolQueryService;
import com.facilityone.wireless.patrol.module.PatrolUrl;
import com.fm.tool.network.model.BaseResponse;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;

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
 * description:巡检设备
 * Date: 2018/11/8 9:42 AM
 */
public class PatrolDevicePresenter extends BasePresenter<PatrolDeviceFragment> {

    public void getDeviceList(final Long spotId) {
        Observable.create(new ObservableOnSubscribe<List<PatrolEquEntity>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<PatrolEquEntity>> emitter) throws Exception {
                PatrolDeviceDao dao = new PatrolDeviceDao();
                List<PatrolEquEntity> entities = dao.getDeviceList(spotId);
                //设置设备的检查数量
                if (entities != null && entities.size() > 0) {
                    PatrolEquEntity equEntity = entities.get(0);
                    if (PatrolDbService.COMPREHENSIVE_EQU_ID == equEntity.getEqId()) {//综合巡检
                        if (entities.size() > 1) {
                            equEntity = entities.get(1);
                        }
                    }

                    if (PatrolDbService.COMPREHENSIVE_EQU_ID != equEntity.getEqId()) {//说明存在不是综合巡检的设备
                        //查看是否需要初始化检查项
                        if (equEntity.getItemUseNumber() == 0 && equEntity.getItemStopNumber() == 0L) {//都是0则需要
                            dao.updateEquItemNumber(entities);
                            //重新获取一次
                            entities = dao.getDeviceList(spotId);
                        }
                    }
                }

                emitter.onNext(entities);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<List<PatrolEquEntity>>() {
                    @Override
                    public void onNext(@NonNull List<PatrolEquEntity> spotEntities) {
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
                            if (data.executable){
                                String time=String.valueOf(data.time/60L);
                                getV().showOrderTimeDialog(time,position);
                            }else {
                                getV().showTimeDialog();
                            }
                        }
                    }
                });
    }


    //执行任务
    public void executeTask(Long spotJobId,String time,Integer position){
        Map<String, Object> jsonObject = new HashMap<>();
        jsonObject.put("patrolTaskSpotId", spotJobId);
        OkGo.<BaseResponse<String>>post(FM.getApiHost() + PatrolUrl.PATROL_EXECUTE_TASK)
                .tag(getV())
                .upJson(toJson(jsonObject))
                .execute(new FMJsonCallback<BaseResponse<String>>() {
                    @Override
                    public void onSuccess(Response<BaseResponse<String>> response) {
                        getV().dismissLoading();
                        String message = response.body().message;
                        if (message != null) {
                            if ("execute".equals(message)){
                                getV().startTask(time,position);
                            }

                        }
                    }
                });
    }


    //撤销任务
    public void cancelTask(QMUIDialog dialog){
        Map<String, Object> jsonObject = new HashMap<>();
        jsonObject.put("patrolTaskSpotId", null);
        OkGo.<BaseResponse<String>>post(FM.getApiHost() + PatrolUrl.PATROL_EXECUTE_TASK)
                .tag(getV())
                .upJson(toJson(jsonObject))
                .execute(new FMJsonCallback<BaseResponse<String>>() {
                    @Override
                    public void onError(Response<BaseResponse<String>> response) {
                        super.onError(response);
                        getV().dismissLoading();
                        dialog.dismiss();
                    }

                    @Override
                    public void onSuccess(Response<BaseResponse<String>> response) {
                        getV().dismissLoading();
                        String message = response.body().message;
                        if (message != null) {
                            if ("cancel".equals(message)){
                               dialog.dismiss();
                            }

                        }
                    }
                });
    }
}
