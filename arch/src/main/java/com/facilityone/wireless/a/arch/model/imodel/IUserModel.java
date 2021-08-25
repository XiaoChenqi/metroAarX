package com.facilityone.wireless.a.arch.model.imodel;

import com.facilityone.wireless.a.arch.bean.UserBean;
import com.facilityone.wireless.a.arch.net.FmNetApi;
import com.facilityone.wireless.a.arch.xcq.net.INetCallback;

public interface IUserModel {

    //用户登录
    void login(FmNetApi.LoginBean requestBean, INetCallback<UserBean> callback);
    //修改密码
    //void changePwd(String oldPwd, String newPwd, INetCallback<UserBean> callback);
    //用户注销
    void logout(INetCallback callback);

}