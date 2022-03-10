package com.example.testaarx.download;

import android.os.Bundle;
import android.view.View;
import android.widget.Switch;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;

import com.example.testaarx.R;
import com.facilityone.wireless.a.arch.ec.utils.SPKey;
import com.facilityone.wireless.a.arch.mvp.BaseFragment;
import com.facilityone.wireless.a.arch.offline.model.service.OfflineService;
import com.facilityone.wireless.a.arch.offline.model.service.PatrolDbService;
import com.facilityone.wireless.a.arch.utils.FMFileUtils;
import com.facilityone.wireless.a.arch.widget.FMWarnDialogBuilder;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DefaultObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Author：gary
 * Email: xuhaozv@163.com
 * description:清除缓存
 * Date: 2018/10/23 4:24 PM
 */
public class ClearCacheFragment extends BaseFragment<ClearCachePresenter> {

    private Switch mSwitchFile;
    private Switch mSwitchOffline;
    private Switch mSwitchPatrol;
    private Switch mSwitchBase;

    @Override
    public ClearCachePresenter createPresenter() {
        return new ClearCachePresenter();
    }

    @Override
    public Object setLayout() {
        return R.layout.fragment_clear_cache;
    }

    @Override
    protected int setTitleBar() {
        return R.id.ui_topbar;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setSwipeBackEnable(false);

        initView();
    }

    private void initView() {
        setTitle("清理缓存");
        setRightTextButton("清除", R.id.app_clear_clear_right_menu);
        mSwitchFile = findViewById(R.id.clear_file_switch);
        mSwitchOffline = findViewById(R.id.clear_offline_switch);
        mSwitchPatrol = findViewById(R.id.clear_patrol_switch);
        mSwitchBase = findViewById(R.id.clear_base_switch);
    }

    @Override
    public void onRightTextMenuClick(View view) {
        if (mSwitchFile.isChecked() || mSwitchOffline.isChecked()
                || mSwitchPatrol.isChecked() || mSwitchBase.isChecked()) {
            FMWarnDialogBuilder builder = new FMWarnDialogBuilder(getContext());
            builder.setTitle("提示");
            builder.setTip("是否清除所选缓存数据");
            builder.addOnBtnSureClickListener(new FMWarnDialogBuilder.OnBtnClickListener() {
                @Override
                public void onClick(QMUIDialog dialog, View view) {
                    dialog.dismiss();
                    clearFmCache();
                }
            });
            builder.create(R.style.fmDefaultWarnDialog).show();
        }else {
            ToastUtils.showShort("请先选择需要清除的缓存数据");
        }
    }

    private void clearFmCache() {
        QMUITipDialog qmuiTipDialog = showLoading();
        qmuiTipDialog.setCancelable(true);
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> emitter) throws Exception {
                if (mSwitchFile.isChecked()) {
                    //清除文件
                    FileUtils.deleteAllInDir(FMFileUtils.getCrashPath());
                    FileUtils.deleteAllInDir(FMFileUtils.getAttachmentPath());
                }

                if (mSwitchOffline.isChecked()) {
                    //离线数据
                    OfflineService.deleteAllData();
                }

                if (mSwitchPatrol.isChecked()) {
                    //巡检
                    PatrolDbService.deleteAllData();
                }

                if (mSwitchBase.isChecked()) {
                    SPUtils.getInstance(SPKey.SP_MODEL).put(SPKey.HAVE_LOGON, false);
                    SPUtils.getInstance(SPKey.SP_MODEL).put(SPKey.FIRST_USE, true);
                }

                emitter.onNext(true);
                emitter.onComplete();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<Boolean>() {
                    @Override
                    public void onNext(@NonNull Boolean aBoolean) {
                        cancel();
                        dismissLoading();
                        ToastUtils.showShort("清理完成");
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        cancel();
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    public static ClearCacheFragment getInstance() {
        return new ClearCacheFragment();
    }
}
