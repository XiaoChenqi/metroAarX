package com.facilityone.wireless.a.arch.xcq.core.mvp;

import android.widget.Toast;

import com.facilityone.wireless.a.arch.BaseApplication;
import com.facilityone.wireless.a.arch.xcq.bean.BaseResponse;
import com.facilityone.wireless.a.arch.xcq.net.INetCallback;
import com.facilityone.wireless.a.arch.xcq.utils.ToastUtils;

import org.apache.http.conn.ConnectTimeoutException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

//import com.facilityone.wireless.a.xcq.bean.BaseResponse;


public  class BaseObserver<T> implements Observer<T> {

    private INetCallback callback;

    protected Disposable disposable;//用于解除订阅，目前不知道杂用

    public BaseObserver(INetCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onSubscribe(Disposable d) {
        disposable=d;
        //BasePresenter.disposable = d;
    }

    @Override
    public void onNext(T response) {
        if(response instanceof BaseResponse){
            if(((BaseResponse) response).isOk()){
                if(callback!=null) {
                    callback.getSuccess(((BaseResponse) response).data);
                }
            } else{
                callback.getErrorCode(((BaseResponse) response).code,((BaseResponse) response).message);
                //callback.getErrorCode(((BaseResponse) response).getCode(),((BaseResponse) response).getErrMsg());
            }
        }else{//TODO 这个地方回头要修改一下
            if (callback != null) {
                callback.getSuccess(response);
            }
        }
    }

    @Override
    public void onError(Throwable e) {
        if (callback != null) {
            try {
                callback.endRequest();
                if (e instanceof SocketTimeoutException || e instanceof ConnectTimeoutException || e instanceof ConnectException) {
                    ToastUtils.show(BaseApplication.getInstance().getApplicationContext(), "出现未知网络错误", Toast.LENGTH_SHORT);
                }
                callback.netError(e);
            } catch (Exception a) {
                BaseApplication.d("--------Exception:" + a.getMessage());
            }
        }
        /**每次请求错误时候自动解绑*/
        if (disposable != null && !disposable.isDisposed()) {//网络出错直接取消订阅
            disposable.dispose();
        }
    }

    @Override
    public void onComplete() {
        if (callback != null)
            callback.endRequest();
        /**每次请求结束自动解绑*/
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }

    }
}
