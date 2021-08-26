package com.facilityone.wireless.maintenance.presenter;

import com.facilityone.wireless.a.arch.base.FMJsonCallback;
import com.facilityone.wireless.a.arch.ec.commonpresenter.CommonBasePresenter;
import com.facilityone.wireless.a.arch.ec.module.AttachmentBean;
import com.facilityone.wireless.a.arch.ec.module.SelectDataBean;
import com.facilityone.wireless.a.arch.mvp.BaseFragment;
import com.facilityone.wireless.a.arch.offline.dao.PriorityDao;
import com.facilityone.wireless.basiclib.app.FM;


import com.facilityone.wireless.maintenance.R;
import com.facilityone.wireless.maintenance.model.MaintenanceService;
import com.facilityone.wireless.maintenance.model.MaintenanceUrl;
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
 * description:
 * Date: 2019/3/1 3:47 PM
 */
public class BaseMaintenancePresenter<V extends BaseFragment> extends CommonBasePresenter<V> {

    public void queryPriority() {
        queryPriority(false);
    }

    public void queryPriority(final boolean needFilterDelete) {
        Observable.create(new ObservableOnSubscribe<List<SelectDataBean>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<SelectDataBean>> emitter) throws Exception {
                List<SelectDataBean> temp = null;
                PriorityDao priorityDao = new PriorityDao();
                temp = priorityDao.queryPriority(needFilterDelete);
                emitter.onNext(temp);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<List<SelectDataBean>>() {
                    @Override
                    public void onNext(@NonNull List<SelectDataBean> temp) {
                        if (temp != null && temp.size() > 0) {
                            Map<Long, String> p = new HashMap<>();
                            for (SelectDataBean selectDataBean : temp) {
                                Long id = selectDataBean.getId();
                                String name = selectDataBean.getName();
                                if (id != null) {
                                    p.put(id, name);
                                }
                            }
                            setPriority(p);
                        }
                        List<AttachmentBean> as = new ArrayList<>();
                        AttachmentBean bb = new AttachmentBean();
                        bb.value = -1L;
                        bb.name = getV().getString(R.string.maintenance_unlimited);
                        bb.check = true;
                        as.add(bb);
                        if (temp != null) {
                            for (SelectDataBean bean : temp) {
                                AttachmentBean b = new AttachmentBean();
                                b.value = bean.getId();
                                b.name = bean.getName();
                                as.add(b);
                            }
                        }
                        getPriority(as);
                        cancel();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        List<AttachmentBean> as = new ArrayList<>();
                        AttachmentBean bb = new AttachmentBean();
                        bb.value = -1L;
                        bb.name = getV().getString(R.string.maintenance_unlimited);
                        bb.check = true;
                        as.add(bb);
                        getPriority(as);
                        cancel();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }
    
    public void getPriority(List<AttachmentBean> as){
        
    }
    
    public void setPriority(Map<Long, String> p){
        
    }


    /**
     * 联网获取工单物资预定记录列表数据
     *
     * @param woId
     */
    public void getWorkorderReserveRecordList(long woId) {

            String request = "{\"woId\":" + woId + "}";
            OkGo.<BaseResponse<List<MaintenanceService.Material>>>post(FM.getApiHost() + MaintenanceUrl.MAINTENANCE_INFO_URL)
                    .isSpliceUrl(true)
                    .tag(getV())
                    .upJson(request)
                    .execute(new FMJsonCallback<BaseResponse<List<MaintenanceService.Material>>>() {
                        @Override
                        public void onSuccess(Response<BaseResponse<List<MaintenanceService.Material>>> response) {
                            List<MaintenanceService.Material> data = response.body().data;
                            if (data != null) {
                                getWorkOrderMaterialSuccess(data);
                            } else {
                                getWorkOrderMaterialError();
                            }
                        }

                        @Override
                        public void onError(Response<BaseResponse<List<MaintenanceService.Material>>> response) {
                            super.onError(response);
                            getWorkOrderMaterialError();
                        }
                    });


    }
    
    public void getWorkOrderMaterialSuccess(List<MaintenanceService.Material> data){
        
    }
    
    public void getWorkOrderMaterialError(){
        
    }


}
