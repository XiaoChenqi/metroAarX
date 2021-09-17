package com.facilityone.wireless.a.arch.model;

import com.facilityone.wireless.a.arch.bean.UserBean;
import com.facilityone.wireless.a.arch.ec.module.UserService;
import com.facilityone.wireless.a.arch.model.imodel.IUserModel;
import com.facilityone.wireless.a.arch.net.FmNetApi;
import com.facilityone.wireless.a.arch.net.FmNetWork;
import com.facilityone.wireless.a.arch.xcq.core.mvp.BaseObserver;
import com.facilityone.wireless.a.arch.xcq.net.INetCallback;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class UserModel implements IUserModel {
    @Override
    public void login(FmNetApi.LoginBean requestBean, INetCallback<UserBean> callback) {
        FmNetApi.LoginBean temp = new FmNetApi.LoginBean();
//        temp.loginCode = bean.loginCode;
//        temp.local = bean.local;
//        temp.loginPwd=bean.loginPwd;
//        temp.source = bean.source;
        temp.loginCode = "zhangsan";
        temp.loginPwd="111111";
        temp.source = "app";
        temp.appType = "android";
        temp.appVersion = "0.0.0";

        FmNetWork.getInstance().getFmNetApi().login(temp)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        if (callback != null)
                            callback.startRequest();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver(callback));
    }

    @Override
    public void logout(INetCallback callback) {

    }

    @Override
    public void getUserInfor(INetCallback<UserService.UserInfoBean> callback) {
        FmNetWork.getInstance().getFmNetApi().userInfor()
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        if (callback != null)
                            callback.startRequest();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver(callback));
    }
}
