package com.example.testaarx.mine;

import com.blankj.utilcode.util.ToastUtils;
import com.facilityone.wireless.a.arch.base.FMJsonCallback;
import com.facilityone.wireless.a.arch.ec.logon.UserUrl;
import com.facilityone.wireless.a.arch.ec.module.UserService;
import com.facilityone.wireless.a.arch.mvp.BasePresenter;
import com.facilityone.wireless.basiclib.app.FM;
import com.fm.tool.network.model.BaseResponse;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

public class MineSignQrcodePresenter extends BasePresenter<MineSignQrcodeFragment> {
    public void getInfor(){
        OkGo.<BaseResponse<UserService.UserInfoBean>>post(FM.getApiHost() + UserUrl.USER_INFO_URL)
                .tag(getV())
                .isSpliceUrl(true)
                .upJson("{}")
                .execute(new FMJsonCallback<BaseResponse<UserService.UserInfoBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponse<UserService.UserInfoBean>> response) {
                        UserService.UserInfoBean data = response.body().data;
                        getV().refreshView(data);
                    }

                    @Override
                    public void onError(Response<BaseResponse<UserService.UserInfoBean>> response) {
                        super.onError(response);
                        ToastUtils.showShort("数据异常");
                    }
                });
    }
}
