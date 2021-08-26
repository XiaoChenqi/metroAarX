package com.facilityone.wireless.inventory.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.view.View;

import com.facilityone.wireless.a.arch.mvp.BaseFragment;
import com.facilityone.wireless.inventory.R;
import com.facilityone.wireless.inventory.adapter.InventoryPagerAdapter;
import com.facilityone.wireless.inventory.model.InventoryApprovalPresenter;
import com.facilityone.wireless.inventory.model.InventoryConstant;
import com.qmuiteam.qmui.widget.QMUITabSegment;
import com.qmuiteam.qmui.widget.QMUIViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by peter.peng on 2018/12/10.
 * 库存审核界面
 */

public class InventoryApprovalFragment extends BaseFragment<InventoryApprovalPresenter> {

    private QMUITabSegment mTabSegment;
    private QMUIViewPager mViewPager;

    private List<String> mTitleList;
    private List<BaseFragment> mFragmentList;
    private InventoryPagerAdapter mPagerAdapter;

    @Override
    public InventoryApprovalPresenter createPresenter() {
        return new InventoryApprovalPresenter();
    }

    @Override
    public Object setLayout() {
        return R.layout.fragment_inventory_approval;
    }

    @Override
    protected int setTitleBar() {
        return R.id.ui_topbar;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    private void initView() {
        setTitle(R.string.inventory_approval_title);

        mTabSegment = findViewById(R.id.tabSegment);
        mViewPager = findViewById(R.id.viewPager);

        int normalColor = ContextCompat.getColor(getContext(), R.color.grey_6);
        int selectColor = ContextCompat.getColor(getContext(), R.color.green_1ab394);
        mTabSegment.setDefaultNormalColor(normalColor);
        mTabSegment.setDefaultSelectedColor(selectColor);
        mTabSegment.setHasIndicator(true);
        mTabSegment.setIndicatorPosition(false);
        mTabSegment.setIndicatorWidthAdjustContent(false);
        mTabSegment.setMode(QMUITabSegment.MODE_FIXED);

        mFragmentList = new ArrayList<>();
        mFragmentList.add(InventoryApprovalListFragment.getInstance(InventoryConstant.INVENTORY_APPROVAL_WAIT));
        mFragmentList.add(InventoryApprovalListFragment.getInstance(InventoryConstant.INVENTORY_APPROVALED));
        mTitleList = new ArrayList<>();
        mTitleList.add(getString(R.string.inventory_wait_approval));
        mTitleList.add(getString(R.string.inventory_approvaled));
        mPagerAdapter = new InventoryPagerAdapter(getChildFragmentManager(), mFragmentList, mTitleList);
        mViewPager.setAdapter(mPagerAdapter);
        mTabSegment.setupWithViewPager(mViewPager);

    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case InventoryApprovalListFragment.INVENTORY_APPROVAL_REQUEST_CODE:
                if (mFragmentList != null) {
                    for (int i = 0; i < mFragmentList.size(); i++) {
                        InventoryApprovalListFragment fragment = (InventoryApprovalListFragment) mFragmentList.get(i);
                        if (fragment != null) {
                            fragment.onRefresh();
                        }
                    }
                }
                break;
        }
    }

    public static InventoryApprovalFragment getInstance() {
        InventoryApprovalFragment fragment = new InventoryApprovalFragment();
        return fragment;
    }
}
