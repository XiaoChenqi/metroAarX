package com.facilityone.wireless.inventory.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.facilityone.wireless.a.arch.mvp.BaseFragment;
import com.facilityone.wireless.inventory.R;
import com.facilityone.wireless.inventory.adapter.RecordHistoryAdapter;
import com.facilityone.wireless.inventory.model.ReserveService;
import com.facilityone.wireless.inventory.presenter.RecordHistoryPresenter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by peter.peng on 2019/6/18.
 * 预订单详情操作记录列表页面
 */

public class RecordHistoryFragment extends BaseFragment<RecordHistoryPresenter> {

    private static final String RECORD_HISTORY_DATA = "record_history_data";
    private RecyclerView mRecyclerView;
    private List<ReserveService.RecordHistory> mRecordHistoryList;
    private RecordHistoryAdapter mRecordHistoryAdapter;

    @Override
    public RecordHistoryPresenter createPresenter() {
        return new RecordHistoryPresenter();
    }

    @Override
    public Object setLayout() {
        return R.layout.fragment_record_history;
    }

    @Override
    protected int setTitleBar() {
        return R.id.ui_topbar;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
        initView();
    }

    private void initData() {
        Bundle bundle = getArguments();
        if(bundle != null) {
            mRecordHistoryList = bundle.getParcelableArrayList(RECORD_HISTORY_DATA);
        }
    }

    private void initView() {
        setTitle(R.string.inventory_history);
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        if(mRecordHistoryList == null) {
            mRecordHistoryList = new ArrayList<>();
        }
        mRecordHistoryAdapter = new RecordHistoryAdapter(mRecordHistoryList);
        mRecyclerView.setAdapter(mRecordHistoryAdapter);
        mRecordHistoryAdapter.setEmptyView(getNoDataView((ViewGroup)mRecyclerView.getParent()));
    }

    public static RecordHistoryFragment getInstance(ArrayList<ReserveService.RecordHistory> recordHistoryList){
        RecordHistoryFragment fragment = new RecordHistoryFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(RECORD_HISTORY_DATA,recordHistoryList);
        fragment.setArguments(bundle);
        return fragment;
    }
}
