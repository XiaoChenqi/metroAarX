package com.facilityone.wireless.componentservice.common.empty;

import static com.facilityone.wireless.a.arch.xcq.Constants.Constant.PASSWORD;
import static com.facilityone.wireless.a.arch.xcq.Constants.Constant.USERNAME;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.facilityone.wireless.AppConfig;
import com.facilityone.wireless.a.arch.BuildConfig;
import com.facilityone.wireless.a.arch.R;
import com.facilityone.wireless.a.arch.mvp.BaseFragment;
import com.facilityone.wireless.a.arch.net.FmNetApi;
import com.facilityone.wireless.a.arch.presenter.UserBehaviorPresenter;
import com.facilityone.wireless.a.arch.xcq.Constants.Constant;
import com.facilityone.wireless.a.arch.xcq.core.mvp.MvpView;


/**
 * Author：gary
 * Email: xuhaozv@163.com
 * description:其他组件单独使用的时候的初始化(登录等)
 * Date: 2018/10/15 5:23 PM
 */
public class EmptyFragment extends BaseFragment<EmptyPresenter> implements MvpView {

    private Button mBtn;

    private static final String MENU_TYPE = "menu_type";
    private int mType = -1;

    private UserBehaviorPresenter xcqPresenter;

    @Override
    public EmptyPresenter createPresenter() {
        return new EmptyPresenter(mType);
    }

    @Override
    public Object setLayout() {
        return R.layout.fragment_empty;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mType = arguments.getInt(MENU_TYPE, -1);
        }
        mBtn = findViewById(R.id.btn);
        setSwipeBackEnable(false);


        //xcqPresenter = new UserBehaviorPresenter();
        //xcqPresenter.attachView(this);

        showLoading();
        // USERNAME = "cylz";
        // PASSWORD = "111111";
//         getPresenter().logon(Constant.USERNAME, PASSWORD);

        if (AppConfig.getEnv().equals(AppConfig.LINE14)){
             getPresenter().logon(Constant.USERNAME, PASSWORD);
        }else {
            String uname=AppConfig.getUname();
            String upwd=AppConfig.getUpwd();
            if (!TextUtils.isEmpty(uname)&&!TextUtils.isEmpty(upwd)){
                getPresenter().logon(uname, upwd);

            }else {
                ToastUtils.showShort("请先到临时页面设置临时账户登录,正在前往");
            }
        }
//        if (BuildConfig.DEBUG){
//            String uname=AppConfig.getUname();
//            String upwd=AppConfig.getUpwd();
//            if (!TextUtils.isEmpty(uname)&&!TextUtils.isEmpty(upwd)){
//                getPresenter().logon(uname, upwd);
//
//            }else {
//                ToastUtils.showShort("请先到临时页面设置临时账户登录,正在前往");
//            }
//
//        }else {
//            getPresenter().logon(Constant.USERNAME, PASSWORD);
//        }
        showLogonButton();
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO xcq

                showLoading();
                getPresenter().logon("weiwai", "111111");

                //xcqPresenter.login(new FmNetApi.LoginBean(),4444);
            }
        });
    }

    public void goFragment(Bundle bundle) {
        if (mOnGoFragmentListener != null) {
            mOnGoFragmentListener.goFragment(bundle);
        }

    }

    public void showLogonButton() {
//        mBtn.setVisibility(View.VISIBLE);
    }

    public void setOnGoFragmentListener(OnGoFragmentListener onGoFragmentListener) {
        mOnGoFragmentListener = onGoFragmentListener;
    }

    private OnGoFragmentListener mOnGoFragmentListener;

    @Override
    public void onStartRequest(int requestCode) {

    }

    private String TAG="周杨";

    @Override
    public void onSuccess(int requestCode, Object o) {
        Log.d(TAG, "onSuccess: "+o);

    }

    @Override
    public void onErrorCode(int resultCode, String msg, int requestCode) {
        Log.d(TAG, "onErrorCode: ");
    }

    @Override
    public void onEndRequest(int requestCode) {
        Log.d(TAG, "onEndRequest: ");
    }

    @Override
    public void onFailure(Throwable e) {
        Log.d(TAG, "onFailure: ");
    }

    public interface OnGoFragmentListener {
        void goFragment(Bundle bundle);
    }

    public static EmptyFragment getInstance(int type) {
        Bundle bundle = new Bundle();
        bundle.putInt(MENU_TYPE, type);
        EmptyFragment fragment = new EmptyFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

}
