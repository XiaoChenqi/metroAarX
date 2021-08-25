package com.facilityone.wireless.a.arch.xcq.net;

/**
 * 网络请求回掉抽象接口
 * Created by MSI-PC on 2018/4/3.
 */

public interface INetCallback<T> {
    /**
     * 网络出错code和错误信息
     * @param code
     * @param msg
     */
    void getErrorCode(int code, String msg);

    /**
     * 网络请求成功
     * @param data
     */
    void getSuccess(T data);

    /**
     * 网络异常抛出错误
     * @param e
     */
    void netError(Throwable e);

    /**
     * 网络开始请求前的回调，比如可以在这里添加加载框
     */
    void startRequest();

    /**
     * 网络结束请求时的回调，比如可以在这里把加载框隐藏
     */
    void endRequest();
}
