package com.facilityone.wireless.inventory.adapter;

import android.annotation.SuppressLint;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;

import com.facilityone.wireless.a.arch.mvp.BaseFragment;
import com.qmuiteam.qmui.widget.QMUIPagerAdapter;

import java.util.List;

/**
 * Created by peter.peng on 2018/11/12.
 * 出库界面viewpager的适配器
 */

public class InventoryPagerAdapter extends QMUIPagerAdapter {

    private final List<BaseFragment> mData;
    private final FragmentManager mFragmentManager;
    private final List<String> mTitle;

    private FragmentTransaction mCurrentTransaction;
    private Fragment mCurrentPrimaryItem = null;

    public InventoryPagerAdapter(FragmentManager fragmentManager, List<BaseFragment> data, List<String> title) {
        this.mFragmentManager = fragmentManager;
        this.mData = data;
        this.mTitle = title;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((Fragment) object).getView();
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitle.get(position);
    }

    @Override
    protected Object hydrate(ViewGroup container, int position) {
        return mData.get(position);
    }

    @SuppressLint("CommitTransaction")
    @Override
    protected void populate(ViewGroup container, Object item, int position) {
        String name = makeFragmentName(container.getId(), position);
        if (mCurrentTransaction == null) {
            mCurrentTransaction = mFragmentManager.beginTransaction();
        }
        Fragment fragment = mFragmentManager.findFragmentByTag(name);
        if (fragment != null) {
            mCurrentTransaction.attach(fragment);
        } else {
            fragment = (Fragment) item;
            mCurrentTransaction.add(container.getId(), fragment, name);
        }
        if (fragment != mCurrentPrimaryItem) {
            fragment.setMenuVisibility(false);
            fragment.setUserVisibleHint(false);
        }
    }

    @SuppressLint("CommitTransaction")
    @Override
    protected void destroy(ViewGroup container, int position, Object object) {
        if (mCurrentTransaction == null) {
            mCurrentTransaction = mFragmentManager.beginTransaction();
        }
        mCurrentTransaction.detach((Fragment) object);
    }

    @Override
    public void startUpdate(ViewGroup container) {
        if (container.getId() == View.NO_ID) {
            throw new IllegalStateException("ViewPager with adapter " + this
                    + " requires a view id");
        }
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        if (mCurrentTransaction != null) {
            mCurrentTransaction.commitNowAllowingStateLoss();
            mCurrentTransaction = null;
        }
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        Fragment fragment = (Fragment) object;
        if (fragment != mCurrentPrimaryItem) {
            if (mCurrentPrimaryItem != null) {
                mCurrentPrimaryItem.setMenuVisibility(false);
                mCurrentPrimaryItem.setUserVisibleHint(false);
            }
            if (fragment != null) {
                fragment.setMenuVisibility(true);
                fragment.setUserVisibleHint(true);
            }
            mCurrentPrimaryItem = fragment;
        }
    }

    private String makeFragmentName(int viewId, long id) {
        return "Bulletin:" + viewId + ":" + id;
    }
}
