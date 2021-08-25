package com.facilityone.wireless.a.arch.xcq.core.mvp;

import android.widget.Toast;


import com.facilityone.wireless.a.arch.BaseApplication;
import com.facilityone.wireless.a.arch.xcq.bean.BaseResponse;
import com.facilityone.wireless.a.arch.xcq.net.INetCallback;
import com.facilityone.wireless.a.arch.xcq.utils.ToastUtils;

import org.apache.http.conn.ConnectTimeoutException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import rx.Subscriber;


/**
 * Created by xcq-PC on 2018/4/4.
 */

public class BaseSubcriber<T> extends Subscriber<T> {
    private INetCallback callback;

    public BaseSubcriber(INetCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onCompleted() {
        if (callback != null)
            callback.endRequest();
    }

    @Override
    public void onError(Throwable e) {
        if (callback != null) {
            try {
                callback.endRequest();
                if (e instanceof SocketTimeoutException || e instanceof ConnectTimeoutException || e instanceof ConnectException) {
                    ToastUtils.show(BaseApplication.getInstance().getApplicationContext(), "网络出现问题", Toast.LENGTH_SHORT);
                }
                callback.netError(e);
            } catch (Exception a) {
                BaseApplication.d("--------Exception:" + a.getMessage());
            }
        }
    }

    @Override
    public void onNext(T response) {
        if(response instanceof BaseResponse){
            if(((BaseResponse) response).isOk()){
                if(callback!=null) {
                    callback.getSuccess(((BaseResponse) response).getData());
                }
            } else{
                callback.getErrorCode(((BaseResponse) response).getCode(),((BaseResponse) response).getMsg());
            }
        }else{//TODO 这个地方回头要修改一下
            if (callback != null) {
                callback.getSuccess(response);
            }
        }
    }
}
