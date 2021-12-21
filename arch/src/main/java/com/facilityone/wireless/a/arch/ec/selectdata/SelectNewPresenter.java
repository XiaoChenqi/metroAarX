package com.facilityone.wireless.a.arch.ec.selectdata;

import com.blankj.utilcode.util.ToastUtils;
import com.facilityone.wireless.a.arch.base.FMJsonCallback;
import com.facilityone.wireless.a.arch.ec.commonpresenter.CommonBasePresenter;
import com.facilityone.wireless.basiclib.app.FM;
import com.fm.tool.network.model.BaseResponse;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

/**
 * @Creator:Karelie
 * @Data: 2021/11/10
 * @TIME: 9:44
 * @Introduce: 多层级Presenter
**/
public class SelectNewPresenter extends CommonBasePresenter<SelectNewFragment> {

    /**
     * @param type = 接口类型
     * */
    public void getList(int type){
        SelectNewService.SelectNewReq request = new SelectNewService.SelectNewReq();
        String url = "";
        switch (type){
            case SelectNewApi.FAULT_OBJECT:
                url = SelectNewApi.FALUT_OBJECT_URL;
                getV().refreshList();
                break;
        }
//        OkGo.<BaseResponse<Object>>post(FM.getApiHost() + url)
//                .tag(getV())
//                .isSpliceUrl(true)
//                .upJson(toJson(request))
//                .execute(new FMJsonCallback<BaseResponse<Object>>() {
//                    @Override
//                    public void onSuccess(Response<BaseResponse<Object>> response) {
//                        getV().dismissLoading();
//
//                        getV().pop();
//                    }
//
//                    @Override
//                    public void onError(Response<BaseResponse<Object>> response) {
//                        super.onError(response);
//                        getV().dismissLoading();
//                    }
//                });
    }
}
