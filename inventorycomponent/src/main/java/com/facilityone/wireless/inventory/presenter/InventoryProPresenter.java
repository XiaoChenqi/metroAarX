package com.facilityone.wireless.inventory.presenter;

import com.facilityone.wireless.a.arch.base.FMJsonCallback;
import com.facilityone.wireless.a.arch.ec.commonpresenter.CommonBasePresenter;
import com.facilityone.wireless.a.arch.ec.module.CommonUrl;
import com.facilityone.wireless.a.arch.ec.module.SelectDataBean;
import com.facilityone.wireless.basiclib.app.FM;
import com.facilityone.wireless.inventory.fragment.InventoryProfessionalListFragment;
import com.facilityone.wireless.inventory.model.ProfessionalService;
import com.fm.tool.network.model.BaseResponse;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.List;

/**
 * Author：gary
 * Email: xuhaozv@163.com
 * description:
 * Date: 2018/7/17 下午3:00
 */
public class InventoryProPresenter extends CommonBasePresenter<InventoryProfessionalListFragment> {

    /**
     * 获取专业全部数据
     * */
    public void getProfessional(){
        getV().showLoading();
        OkGo.<BaseResponse<List<SelectDataBean.ProfessionalList>>>post(FM.getApiHost() + CommonUrl.PROFESSIONAL_LIST)
                .isSpliceUrl(true)
                .tag(getV())
                .execute(new FMJsonCallback<BaseResponse<List<SelectDataBean.ProfessionalList>>>() {
                    @Override
                    public void onSuccess(Response<BaseResponse<List<SelectDataBean.ProfessionalList>>> response) {
//                        //转换
                        List<SelectDataBean.ProfessionalList> data = response.body().data;
                        if (data == null || data.size() == 0) {
                            getV().refreshData(null);
                        } else {
                            List<ProfessionalService.InventoryProBean> selectDataBeans = new ArrayList<>();
                            for (SelectDataBean.ProfessionalList datum : data) {
                                ProfessionalService.InventoryProBean s = new ProfessionalService.InventoryProBean();
                                s.id=(datum.id);
                                s.configName=(datum.configName);
                                selectDataBeans.add(s);
                            }
                            getV().refreshData(selectDataBeans);
//                            getV().getReasonAll(selectDataBeans);
                        }
                        getV().dismissLoading();
                    }

                    @Override
                    public void onError(Response<BaseResponse<List<SelectDataBean.ProfessionalList>>> response) {
                        super.onError(response);
                        getV().refreshData(null);
                        getV().dismissLoading();
                    }
                });
    }
}
