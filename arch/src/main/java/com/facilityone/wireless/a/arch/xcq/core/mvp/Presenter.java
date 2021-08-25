package com.facilityone.wireless.a.arch.xcq.core.mvp;

/**
 *
 * Created by MSI-PC on 2018/4/3.
 */

public interface Presenter<V extends MvpView> {

    /**
     * 绑定mvp模式
     * @param mvpView
     */
    void attachView(V mvpView);

    /**
     * 解绑观察者模式
     */
    void detachView();
}
