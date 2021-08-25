package com.facilityone.wireless.a.arch.xcq.core.mvp;

/**
 * mvp模式的基础接口
 * Created by xcq-PC on 2018/4/3.
 */

public interface MvpView {

    /**
     * 开始访问
     */
    void onStartRequest(int requestCode);
    /**
     * 访问成功
     */
    void onSuccess(int requestCode, Object o);
    /**
     * 后台错误码
     */
    void onErrorCode(int resultCode, String msg, int requestCode);
    /**
     * 结束访问
     */
    void onEndRequest(int requestCode);
    /**
     * 网络发生错误
     */
    void onFailure(Throwable e);
}
