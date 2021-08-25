package com.facilityone.wireless.patrol.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.facilityone.wireless.a.arch.ec.collect.CollectUtils;
import com.facilityone.wireless.a.arch.ec.utils.SPKey;
import com.facilityone.wireless.a.arch.mvp.BaseFragment;
import com.facilityone.wireless.a.arch.offline.model.entity.PatrolEquEntity;
import com.facilityone.wireless.a.arch.widget.FMWarnDialogBuilder;
import com.facilityone.wireless.patrol.R;
import com.facilityone.wireless.patrol.adapter.PatrolDeviceAdapter;
import com.facilityone.wireless.patrol.presenter.PatrolDevicePresenter;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;

import java.util.ArrayList;
import java.util.List;

import me.yokeyword.fragmentation.SwipeBackLayout;

/**
 * Author：gary
 * Email: xuhaozv@163.com
 * description:巡检设备
 * Date: 2018/11/8 9:37 AM
 */
public class PatrolDeviceFragment extends BaseFragment<PatrolDevicePresenter> implements
        BaseQuickAdapter.OnItemClickListener,BaseQuickAdapter.OnItemChildClickListener
        , SwipeBackLayout.OnSwipeListener {

    private RecyclerView mRecyclerView;

    private static final String PATROL_SPOT_NAME = "patrol_spot_name";
    private static final String PATROL_SPOT_ID = "patrol_spot_id";
    private static final int REQUEST_ITEM = 40001;

    private Long mSpotId;
    private String mSpotName;
    private PatrolDeviceAdapter mAdapter;
    private List<PatrolEquEntity> mEntities;
    private boolean mChange;

    @Override
    public PatrolDevicePresenter createPresenter() {
        return new PatrolDevicePresenter();
    }

    @Override
    public Object setLayout() {
        return R.layout.fragment_patrol_device;
    }

    @Override
    protected int setTitleBar() {
        return R.id.ui_topbar;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
    }

    private void initData() {
        String title = getString(R.string.patrol_device);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mSpotName = arguments.getString(PATROL_SPOT_NAME, getString(R.string.patrol_device));
            title = mSpotName;
            mSpotId = arguments.getLong(PATROL_SPOT_ID);
        }
        setTitle(title);
        if (mSpotId == 0L) {
            pop();
            return;
        }

        mEntities = new ArrayList<>();
        mAdapter = new PatrolDeviceAdapter(mEntities);

        initView();
        getDeviceList();
    }

    private void initView() {
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);
//        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnItemChildClickListener(this);
        getSwipeBackLayout().addSwipeListener(this);
    }

    public void getDeviceList() {
        showLoading();
        getPresenter().getDeviceList(mSpotId);
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
//        ToastUtils.showLong(GsonUtils.toJson(mAdapter.getData().get(position)));
        Long spotId=mAdapter.getData().get(position).getSpotId();
        showLoading();
        getPresenter().judgeTask(spotId,position);

    }

    @Override
    public void onItemClick(BaseQuickAdapter baseQuickAdapter, View view, int position) {
//        showOrderTimeDialog("30",position);
//        getPresenter().judgeTask();

    }





    /**
      * @Auther: karelie
      * @Date: 2021/8/20
      * @Infor: 重置工作任务
      */
    public void showTimeDialog(){
        FMWarnDialogBuilder builder = new FMWarnDialogBuilder(getContext());
        builder.setTitle("提示");
        builder.setCancel("取消");
        builder.setTip("目前已经有巡检任务在执行中，是否立即结束进行中的任务？点击确认后，将重新开始计时");
        builder.addOnBtnSureClickListener(new FMWarnDialogBuilder.OnBtnClickListener() {
            @Override
            public void onClick(QMUIDialog dialog, View view) {
                //撤销任务
                showLoading();
                getPresenter().cancelTask(dialog);
            }
        });
        builder.addOnBtnCancelClickListener(new FMWarnDialogBuilder.OnBtnClickListener() {
            @Override
            public void onClick(QMUIDialog dialog, View view) {
                dialog.dismiss();
            }
        });
        builder.create(R.style.fmDefaultWarnDialog).show();
    }

    public void showOrderTimeDialog(String time,Integer position){
        FMWarnDialogBuilder builder = new FMWarnDialogBuilder(getContext());
        builder.setTitle("提示");
        builder.setCancel("返回");
        builder.setTip("如果您确认开启该任务，最少需要"+time+"分钟才能完成提交。");
        builder.addOnBtnSureClickListener(new FMWarnDialogBuilder.OnBtnClickListener() {
            @Override
            public void onClick(QMUIDialog dialog, View view) {
                getPresenter().executeTask(mAdapter.getData().get(position).getSpotId(),time,position);
//                startForResult(PatrolItemFragment.getInstance(mSpotId, (ArrayList<PatrolEquEntity>) mEntities, position, mSpotName,time), REQUEST_ITEM);
                dialog.dismiss();
            }
        });
        builder.create(R.style.fmDefaultWarnDialog).show();
    }

    public void startTask(String time,Integer position){
        startForResult(PatrolItemFragment.getInstance(mSpotId, (ArrayList<PatrolEquEntity>) mEntities, position, mSpotName,time), REQUEST_ITEM);

    }



    @Override
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            getDeviceList();
            if (data != null) {
                mChange = data.getBoolean("change");
            }
        }
    }

    public void refreshUI(List<PatrolEquEntity> entities) {
        mEntities.clear();
        if (entities != null && entities.size() > 0) {
            mEntities.addAll(entities);
        } else {
            mAdapter.setEmptyView(getNoDataView((ViewGroup) mRecyclerView.getParent()));
        }
        mAdapter.notifyDataSetChanged();
        dismissLoading();
    }

    public void error() {
        dismissLoading();
        mAdapter.setEmptyView(getErrorView((ViewGroup) mRecyclerView.getParent(), getString(R.string.patrol_get_data_error)));
    }

    @Override
    public boolean onBackPressedSupport() {
        popResult();
        return true;
    }

    @Override
    public void leftBackListener() {
        popResult();
    }

    @Override
    public void onDragStateChange(int state) {
        if (state == SwipeBackLayout.STATE_FINISHED) {
            setFragmentResult(RESULT_OK, null);
        }
    }

    @Override
    public void onEdgeTouch(int oritentationEdgeFlag) {

    }

    @Override
    public void onDragScrolled(float scrollPercent) {

    }

    public void popResult() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("change", mChange);
        setFragmentResult(RESULT_OK, bundle);
        pop();
    }

    public static PatrolDeviceFragment getInstance(String spotName, Long spotId) {
        Bundle bundle = new Bundle();
        bundle.putLong(PATROL_SPOT_ID, spotId);
        bundle.putString(PATROL_SPOT_NAME, spotName);
        PatrolDeviceFragment instance = new PatrolDeviceFragment();
        instance.setArguments(bundle);
        return instance;
    }
}
