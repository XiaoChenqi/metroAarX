package com.example.testaarx.mine;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.testaarx.R;
import com.facilityone.wireless.a.arch.ec.module.UserService;
import com.facilityone.wireless.a.arch.mvp.BaseFragment;
import com.facilityone.wireless.a.arch.presenter.UserBehaviorPresenter;
import com.facilityone.wireless.a.arch.utils.UrlUtils;
import com.facilityone.wireless.a.arch.xcq.core.mvp.MvpView;
import com.facilityone.wireless.basiclib.utils.ImageLoadUtils;

import androidx.annotation.Nullable;


/**
  * @Auther: karelie
  * @Date: 2021/8/6
  * @Infor: 我的二维码界面
  */
public class MineSignQrcodeFragment extends BaseFragment<MineSignQrcodePresenter> implements MvpView {
    private ImageView mUserImgae;
    private TextView mUserName;
    private TextView mPostion;
    private ImageView im_qrcode;
    private UserBehaviorPresenter mpresenter;
    @Override
    public Object setLayout() {
        return R.layout.mine_sign_qrcode_fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initData();
        setSwipeBackEnable(false);
    }

    private void initData() {
        mpresenter = new UserBehaviorPresenter();
        getPresenter().getInfor(); // 获取数据
//        mpresenter.attachView(this);
//        mpresenter.userInfor(1);
    }

    @Override
    public void leftBackListener() {
       requireActivity().finish();
    }

    private void initView() {
        setTitle("我的二维码");
        mUserImgae = findViewById(R.id.civ_user_photo);
        mUserName = findViewById(R.id.tv_user_name);
        mPostion = findViewById(R.id.tv_user_position);
        im_qrcode = findViewById(R.id.iv_qrcode_pic);

    }
    @Override
    protected int setTitleBar() {
        return R.id.ui_topbar;
    }

    public void refreshView(UserService.UserInfoBean data){
        if (data == null){
            return;
        }
        ImageLoadUtils.loadImageView(getActivity(), UrlUtils.getImagePath(data.pictureId+""), mUserImgae, R.drawable.user_default_head, R.drawable.user_default_head);
        if (data.name != null){
            mUserName.setText(data.name+"");
        }else {
            mUserName.setText("");
        }

        if (data.position != null ){
            mPostion.setText(data.position+"");
        }else {
            mPostion.setText("");
        }

        ImageLoadUtils.loadImageOverRideView(getActivity(), UrlUtils.getImagePath(data.qrcodeId+""), im_qrcode, R.drawable.default_small_image, R.drawable.default_big_image);

    }

    @Override
    public MineSignQrcodePresenter createPresenter() {
        return new MineSignQrcodePresenter();
    }

    public static MineSignQrcodeFragment getInstance() {
        MineSignQrcodeFragment signQrcodeFragment = new MineSignQrcodeFragment();
        return signQrcodeFragment;
    }


    @Override
    public void onStartRequest(int requestCode) {

    }

    @Override
    public void onSuccess(int requestCode, Object o) {
        UserService.UserInfoBean data = (UserService.UserInfoBean) o;
        refreshView(data);
    }

    @Override
    public void onErrorCode(int resultCode, String msg, int requestCode) {

    }

    @Override
    public void onEndRequest(int requestCode) {

    }

    @Override
    public void onFailure(Throwable e) {

    }
}
