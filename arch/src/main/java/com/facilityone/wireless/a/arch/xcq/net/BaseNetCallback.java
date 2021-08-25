package com.facilityone.wireless.a.arch.xcq.net;


import com.facilityone.wireless.a.arch.xcq.core.mvp.MvpView;

/**
 * Created by MSI-PC on 2018/4/4.
 */

public class BaseNetCallback<T> implements INetCallback<T> {

    private MvpView view;
    private int requestCode;

    public BaseNetCallback(MvpView view, int requestCode) {
        this.view = view;
        this.requestCode = requestCode;
    }

    @Override
    public void getErrorCode(int code, String msg) {
        if(view!=null)
            view.onErrorCode(code,msg,requestCode);
    }

    @Override
    public void getSuccess(T data) {
        //data是成功获取到的数据
        if(view!=null) {//view什么情况下会等于null
            view.onSuccess(requestCode, data);
        }
    }

    @Override
    public void netError(Throwable e) {
        if(view!=null)
            view.onFailure(e);
    }

    @Override
    public void startRequest() {
        if(view!=null)
            view.onStartRequest(requestCode);
    }

    @Override
    public void endRequest() {
        if(view!=null)
            view.onEndRequest(requestCode);
    }
}
