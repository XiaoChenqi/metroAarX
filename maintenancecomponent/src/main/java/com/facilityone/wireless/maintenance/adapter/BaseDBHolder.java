package com.facilityone.wireless.maintenance.adapter;

import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.chad.library.adapter.base.BaseViewHolder;

public class BaseDBHolder<DB extends ViewDataBinding>  extends BaseViewHolder {
    public BaseDBHolder(View view) {
        super(view);
    }
    public DB getBinding() {
        return  DataBindingUtil.<DB>bind(itemView);
    }
}
