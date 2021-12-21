package com.facilityone.wireless.maintenance.presenter;

import static com.facilityone.wireless.maintenance.model.MaintenanceUrl.MAINTENANCE_ORDER_RECEIVE;

import android.content.Context;

import com.blankj.utilcode.util.ToastUtils;
import com.facilityone.wireless.a.arch.base.FMJsonCallback;
import com.facilityone.wireless.a.arch.ec.commonpresenter.CommonBasePresenter;
import com.facilityone.wireless.a.arch.ec.module.AttachmentBean;
import com.facilityone.wireless.a.arch.ec.module.Page;
import com.facilityone.wireless.basiclib.app.FM;
import com.facilityone.wireless.maintenance.R;
import com.facilityone.wireless.maintenance.fragment.MaintenanceElectronicLedgerFragment;
import com.facilityone.wireless.maintenance.fragment.MaintenanceListFragment;
import com.facilityone.wireless.maintenance.model.AccountCheck;
import com.facilityone.wireless.maintenance.model.CheckModel;
import com.facilityone.wireless.maintenance.model.MaintenanceConstant;
import com.facilityone.wireless.maintenance.model.MaintenanceEnity;
import com.facilityone.wireless.maintenance.model.MaintenanceService;
import com.facilityone.wireless.maintenance.model.MaintenanceUrl;
import com.facilityone.wireless.maintenance.model.TemplateModel;
import com.facilityone.wireless.maintenance.model.UploadTemplateData;
import com.fm.tool.network.model.BaseResponse;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kotlin.Pair;
import kotlin.collections.MapsKt;

public class MaintenanceELPresenter extends CommonBasePresenter<MaintenanceElectronicLedgerFragment> {



     /**
      * @Created by: kuuga
      * @Date: on 2021/11/26 14:03
      * @Description: 提交抽奖数据
      */

     public void pushAccountCheck(AccountCheck model){
         getV().showLoading();
         String url = "";
         url = MaintenanceUrl.MAINTENANCE_ACCOUNT_CHECK;

         OkGo.<BaseResponse<String>>post(FM.getApiHost() + url)
                 .tag(getV())
                 .isSpliceUrl(true)
                 .upJson(toJson(model))
                 .execute(new FMJsonCallback<BaseResponse<String>>() {
                     @Override
                     public void onSuccess(Response<BaseResponse<String>> response) {
                         getV().dismissLoading();
//                        getV().onUploadSucces();

                     }

                     @Override
                     public void onError(Response<BaseResponse<String>> response) {
                         super.onError(response);
                         getV().dismissLoading();
                     }
                 });
     }



     /**
      * @Created by: kuuga
      * @Date: on 2021/11/26 14:04
      * @Description: 获取抽检数据
      */
     public void getTemplateData(Long woId){
         getV().showLoading();
         String url = "";
         url = MaintenanceUrl.MAINTENANCE_FETCH_CHECK;
         Map<String,Long> woIdMap=new HashMap<>();
         woIdMap.put("woId",woId);

//         String tempUrl="http://192.168.14.2:4523/mock/413312/fz_iframe/m/v1/sample/contents";
        String tempUrl=FM.getApiHost() + url;
         OkGo.<BaseResponse<TemplateModel>>post(tempUrl)
                 .tag(getV())
                 .isSpliceUrl(true)
                 .upJson(toJson(woIdMap))
                 .execute(new FMJsonCallback<BaseResponse<TemplateModel>>() {
                     @Override
                     public void onSuccess(Response<BaseResponse<TemplateModel>> response) {
                         getV().dismissLoading();
                         TemplateModel data=response.body().data;
                         if (data!=null){
                             getV().onLoadTemplateSuccess(data);
                         }else {
                             getV().onLoadTemplateFail();
                         }

                     }

                     @Override
                     public void onError(Response<BaseResponse<TemplateModel>> response) {
                         super.onError(response);
                         getV().dismissLoading();
                         getV().onLoadTemplateFail();
                     }
                 });

     }

    /**
     * @Created by: kuuga
     * @Date: on 2021/11/26 14:04
     * @Description: 保存抽检数据
     */
    public void saveTemplateData(UploadTemplateData uploadTemplate){
        getV().showLoading();
        String url = "";
        url = MaintenanceUrl.MAINTENANCE_SAVE_CHECK;

//
        OkGo.<BaseResponse<TemplateModel>>post(FM.getApiHost() + url)
                .tag(getV())
                .isSpliceUrl(true)
                .upJson(toJson(uploadTemplate))
                .execute(new FMJsonCallback<BaseResponse<TemplateModel>>() {
                    @Override
                    public void onSuccess(Response<BaseResponse<TemplateModel>> response) {
                        getV().dismissLoading();
                        getV().onSubmitTemplateSuccess();

                    }

                    @Override
                    public void onError(Response<BaseResponse<TemplateModel>> response) {
                        super.onError(response);
                        getV().dismissLoading();
                        getV().onSubmitTemplateFail();
                    }
                });

    }



}
