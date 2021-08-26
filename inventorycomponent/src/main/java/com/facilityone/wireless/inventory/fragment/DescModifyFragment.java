package com.facilityone.wireless.inventory.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;

import com.facilityone.wireless.a.arch.mvp.BaseFragment;
import com.facilityone.wireless.a.arch.widget.EditNumberView;
import com.facilityone.wireless.basiclib.utils.StringUtils;
import com.facilityone.wireless.inventory.R;
import com.facilityone.wireless.inventory.presenter.DescModifyPresenter;

/**
 * Created by peter.peng on 2018/12/12.
 * 备注编辑页面
 */

public class DescModifyFragment extends BaseFragment<DescModifyPresenter>{
    public static final String DESC = "desc";

    private EditNumberView mDescEnv;

    private String mDesc;

    @Override
    public DescModifyPresenter createPresenter() {
        return new DescModifyPresenter();
    }

    @Override
    public Object setLayout() {
        return R.layout.fragment_desc_modify;
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
            mDesc = bundle.getString(DESC,"");
        }
    }

    private void initView() {
        setTitle(R.string.inventory_edit_desc_title);

        mDescEnv = findViewById(R.id.desc_env);
        mDescEnv.setDesc(StringUtils.formatString(mDesc));

        setRightTextButton(R.string.inventory_save,R.id.inventory_batch_save_id);
    }

    @Override
    public void onRightTextMenuClick(View view) {
        super.onRightTextMenuClick(view);
        if(view.getId() == R.id.inventory_batch_save_id) {
            mDesc = mDescEnv.getDesc().trim();
            Bundle bundle = new Bundle();
            bundle.putString(DESC,mDesc);
            setFragmentResult(RESULT_OK,bundle);
            pop();
        }
    }

    public static DescModifyFragment getInstance(String desc){
        DescModifyFragment fragment = new DescModifyFragment();
        Bundle bundle = new Bundle();
        bundle.putString(DESC,desc);
        fragment.setArguments(bundle);
        return fragment;
    }
}
