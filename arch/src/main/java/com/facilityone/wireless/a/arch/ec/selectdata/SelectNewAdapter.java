package com.facilityone.wireless.a.arch.ec.selectdata;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facilityone.wireless.a.arch.R;
import com.facilityone.wireless.a.arch.ec.module.SelectDataBean;

import java.util.List;

public class SelectNewAdapter extends BaseQuickAdapter<SelectNewService.SelectNewResp, BaseViewHolder> {
    public SelectNewAdapter( @Nullable List<SelectNewService.SelectNewResp> data) {
        super(R.layout.fragment_arch_select_new_item, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, SelectNewService.SelectNewResp selectDataBean) {
        if (selectDataBean == null){
            return;
        }
    }
}
