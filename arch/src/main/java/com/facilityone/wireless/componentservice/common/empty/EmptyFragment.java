package com.facilityone.wireless.componentservice.common.empty;

import android.os.Bundle;
import androidx.annotation.Nullable;

import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facilityone.wireless.a.arch.R;
import com.facilityone.wireless.a.arch.mvp.BaseFragment;
import com.facilityone.wireless.a.arch.net.FmNetApi;
import com.facilityone.wireless.a.arch.presenter.UserBehaviorPresenter;
import com.facilityone.wireless.a.arch.presenter.ivew.LoginMvpView;

/**
 * Author：gary
 * Email: xuhaozv@163.com
 * description:其他组件单独使用的时候的初始化(登录等)
 * Date: 2018/10/15 5:23 PM
 */
public class EmptyFragment extends BaseFragment<EmptyPresenter> implements LoginMvpView {

    private Button mBtn;

    private static final String MENU_TYPE = "menu_type";
    private int mType = -1;

    UserBehaviorPresenter ubPresenter;

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

        /**
         * 初始化presenter
         */
        ubPresenter = new UserBehaviorPresenter();
        ubPresenter.attachView(this);


        //TODO xcq
        showLoading();
        getPresenter().logon("zhangsan", "111111");
        showLogonButton();
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO xcq

                showLoading();
                //todo ???
                getPresenter().logon("zhangsan", "111111");
                //ubPresenter.login(new FmNetApi.LoginBean(),6666);

            }
        });
    }

    public void goFragment(Bundle bundle) {
        if (mOnGoFragmentListener != null) {
            mOnGoFragmentListener.goFragment(bundle);
        }
    }

    public void showLogonButton() {
        mBtn.setVisibility(View.VISIBLE);
    }

    public void setOnGoFragmentListener(OnGoFragmentListener onGoFragmentListener) {
        mOnGoFragmentListener = onGoFragmentListener;
    }

    private OnGoFragmentListener mOnGoFragmentListener;

    @Override
    public void loginNameNull() {

    }

    @Override
    public void loginPwdNull() {

    }

    String TAG = "林晓旭";
    @Override
    public void onStartRequest(int requestCode) {
        Log.d(TAG, "onStartRequest: ");
    }

    @Override
    public void onSuccess(int requestCode, Object o) {
        Log.d(TAG, "onSuccess: ");
        /**
         * todo
         * 成功以后需要去获取权限接口
         * 然后再回到首页
         * 要干的事情太多了，不需要写测试
         * 回头使用原来的token，cookie和权限
         */

    }

    @Override
    public void onErrorCode(int resultCode, String msg, int requestCode) {

        //Log.d(TAG, "onErrorCode: "+requestCode+"~~~"+msg);
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
