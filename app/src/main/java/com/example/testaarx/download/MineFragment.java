package com.example.testaarx.download;

import android.content.Intent;
import android.os.Bundle;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.SPUtils;

import com.example.testaarx.HostSettingActivity;
import com.example.testaarx.MainActivity;
import com.example.testaarx.R;
import com.facilityone.wireless.a.arch.ec.module.UserService;
import com.facilityone.wireless.a.arch.ec.utils.SPKey;
import com.facilityone.wireless.a.arch.ec.utils.UpdateUtils;
import com.facilityone.wireless.a.arch.mvp.BaseFragment;
import com.facilityone.wireless.a.arch.utils.NoDoubleClickListener;
import com.facilityone.wireless.a.arch.utils.PictureSelectorManager;
import com.facilityone.wireless.a.arch.utils.UrlUtils;
import com.facilityone.wireless.a.arch.widget.BottomTextListSheetBuilder;
import com.facilityone.wireless.a.arch.widget.CustomListItemView;

import com.facilityone.wireless.basiclib.utils.DataUtils;
import com.facilityone.wireless.basiclib.utils.GsonUtils;
import com.facilityone.wireless.basiclib.utils.ImageLoadUtils;
import com.facilityone.wireless.componentservice.workorder.WorkorderService;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.luojilab.component.componentlib.router.Router;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;

import java.util.List;

import androidx.annotation.Nullable;
import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Author：gary
 * Email: xuhaozv@163.com
 * description:主页我的页面
 * Date: 2018/5/30 下午3:32
 */
public class MineFragment extends BaseFragment<MinePresenter> implements View.OnClickListener {


    private CustomListItemView mDataView;

    private CustomListItemView mCheckVersion;

    private CustomListItemView mServerConfig;

    private CustomListItemView mCache;

    public static final int REQUEST_PHONE_CODE = 1001;
    private boolean mShowUserInfo;
    private boolean mShowDataStatus;
    private OfflineDataStatusEntity mOfflineDataStatusEntity;
    private Long mRequestTime;

    @Override
    public MinePresenter createPresenter() {
        return new MinePresenter();
    }

    @Override
    public Object setLayout() {
        return R.layout.fragment_home_mine;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setSwipeBackEnable(false);
        initView();
    }

    private void initView() {
//        final ImageView parallax = findViewById(R.id.parallax);
//        final RefreshLayout refreshLayout = findViewById(R.id.refreshLayout);

        mDataView = findViewById(R.id.item_data);
        mCheckVersion = findViewById(R.id.item_update);
        mServerConfig = findViewById(R.id.item_server);
        mCache = findViewById(R.id.item_cache);


        mDataView.setOnClickListener(this);
        mCheckVersion.setOnClickListener(this);
        mCheckVersion.showRedPoint(false);
        mCache.setOnClickListener(this);
        mServerConfig.setOnClickListener(this);


//        refreshLayout.setOnMultiPurposeListener(new SimpleMultiPurposeListener() {
//            @Override
//            public void onHeaderMoving(RefreshHeader header, boolean isDragging, float percent, int offset, int headerHeight, int maxDragHeight) {
//                parallax.setTranslationY(offset / 2);
//            }
//        });


    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        if (!mShowUserInfo) {
            String userInfo = SPUtils.getInstance(SPKey.SP_MODEL_USER).getString(SPKey.USER_INFO);
            mShowUserInfo = true;
        }
        getPresenter().requestOfflineStatus();
    }


    @Override
    public void onClick(View v) {
        BaseFragment toFragment = null;
        switch (v.getId()) {
            case R.id.item_data:
                toFragment = OutlineDataFragment.newInstance(mOfflineDataStatusEntity, mShowDataStatus, mRequestTime);
                break;
            case R.id.item_update:
                checkUpdate();
                break;
            case R.id.item_cache:
                toFragment=ClearCacheFragment.getInstance();
                break;
            case R.id.item_server:
                getActivity().startActivity( new Intent(getActivity(), HostSettingActivity.class));
                break;
        }

        if (toFragment != null) {
            start(toFragment);
        }
    }


    public void setRequestTime(Long requestTime) {
        mRequestTime = requestTime;
    }

    /**
     * @param showRed 是否显示红点（有离线数据需要下载）
     */
    public void changeOfflineData(OfflineDataStatusEntity entity, boolean showRed) {
        mDataView.showRedPoint(showRed);
        mOfflineDataStatusEntity = entity;
        mShowDataStatus = showRed;
    }

    public void checkUpdate(){
        UpdateUtils.updateCheck(this,null);
    }


    public static MineFragment newInstance() {
        return new MineFragment();
    }

    @Override
    public void leftBackListener() {
        getActivity().finish();
    }
}
