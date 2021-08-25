package com.facilityone.wireless.a.arch.xcq.core.mvp;


//import com.ezdata.xcqframeopencv.data.DataManager;

//import com.ezdata.commonlib.data.DataManager;
//
//import io.reactivex.disposables.Disposable;

import io.reactivex.disposables.Disposable;

/**
 * <p>
 * Base class that implements the Presenter interface and provides a base implementation for
 * attachView() and detachView(). It also handles keeping a reference to the mvpView that
 * can be accessed from the children classes by calling getMvpView().
 * <p>
 * Created by MSI-PC on 2018/4/3.
 */

public class BasePresenter<T extends MvpView> implements Presenter<T> {

    //protected Subscription subscription;//这是rxjava1的用法
    private T mMvpView;
    //public DataManager mDataManager;


    //protected CompositeDisposable mCompositeDisposable;手动控制解绑订阅事件的方式时候，才使用，暂时不用这种方式
    //public static Disposable disposable;//观察者对被观察者的订阅对象，可用于取消订阅

    @Override
    public void attachView(T mvpView) {
        this.mMvpView = mvpView;
        //this.mDataManager = DataManager.getInstance();
    }

    @Override
    public void detachView() {
        this.mMvpView = null;
        unsubscribe();
    }

    /**
     * 把绑定添加到集合中
     * @param d
     */
    protected void suscribe(Disposable d){
        /**
         * 现在在baseObserver中使用自动解绑的方法，不使用手动解绑
         */
//        if(mCompositeDisposable==null){
//            mCompositeDisposable = new CompositeDisposable();
//        }
//        mCompositeDisposable.add(d);
    }



//    private void unsubscribe() {
//        if(subscription != null && !subscription.isUnsubscribed()){
//            subscription.unsubscribe();
//        }
//    }
    /**
     * rxjava2的取消订阅
     */
    private void unsubscribe(){
        /**
         * 现在在baseObserver中使用自动解绑的方法，不使用手动解绑
         */
//        if(mCompositeDisposable != null&&!mCompositeDisposable.isDisposed()){
//            mCompositeDisposable.dispose();
//        }
        //mCompositeDisposable.clear();
    }

    public T getMvpView() {
        return mMvpView;
    }


    public boolean isViewAttached() {
        return mMvpView != null;
    }


    public void checkViewAttached() {
        if (!isViewAttached()) throw new MvpViewNotAttachedException();
    }


    public static class MvpViewNotAttachedException extends RuntimeException {
        public MvpViewNotAttachedException() {
            super("Please call Presenter.attachView(MvpView) before" +
                    " requesting data to the Presenter");
        }
    }
}
