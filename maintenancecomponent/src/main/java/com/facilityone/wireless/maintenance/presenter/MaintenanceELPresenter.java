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
import com.fm.tool.network.model.BaseResponse;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.List;

public class MaintenanceELPresenter extends CommonBasePresenter<MaintenanceElectronicLedgerFragment> {

     /**
      * @Auther: karelie
      * @Date: 2021/8/18
      * @Infor: 获取列表数据
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
                        getV().onUploadSucces();

                     }

                     @Override
                     public void onError(Response<BaseResponse<String>> response) {
                         super.onError(response);
                         getV().dismissLoading();
                     }
                 });
     }


}
