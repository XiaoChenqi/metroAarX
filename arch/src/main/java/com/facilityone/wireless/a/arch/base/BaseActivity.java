package com.facilityone.wireless.a.arch.base;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facilityone.wireless.a.arch.mvp.IPresent;
import com.facilityone.wireless.a.arch.mvp.IView;
import com.lzy.okgo.OkGo;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

public abstract class BaseActivity<P extends IPresent> extends AppCompatActivity implements IView<P> {
    private P mPresenter;
    private QMUITipDialog mDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public P getPresenter() {
        if(mPresenter == null) {
            mPresenter = createPresenter();
            if(mPresenter != null) {
                mPresenter.attachV(this);
            }
        }
        return mPresenter;
    }

    @Override
    protected void onDestroy() {
        //清除未完成的网络请求
        OkGo.getInstance().cancelTag(this);

        //绑定的presenter解绑此view
        if (mPresenter != null) {
            mPresenter.detachV();
        }

        mPresenter = null;

        super.onDestroy();

    }

    public QMUITipDialog showLoading() {
        return showLoading(false);
    }

    public QMUITipDialog showLoading(boolean cancelable) {
        if (mDialog != null && !mDialog.isShowing()) {
            mDialog.setCancelable(cancelable);
            mDialog.show();
        }
        return mDialog;
    }

    public QMUITipDialog dismissLoading() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
        return mDialog;
    }

}
