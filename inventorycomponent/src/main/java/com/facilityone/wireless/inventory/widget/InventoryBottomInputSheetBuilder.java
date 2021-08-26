package com.facilityone.wireless.inventory.widget;

import android.content.Context;
import androidx.annotation.StringRes;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.facilityone.wireless.a.arch.widget.FMBottomInputSheetBuilder;
import com.facilityone.wireless.inventory.R;

/**
 * Created by peter.peng on 2018/12/5.
 */

public class InventoryBottomInputSheetBuilder extends FMBottomInputSheetBuilder {

    private EditText mNumEt;
    private FrameLayout mDescFl;

    public InventoryBottomInputSheetBuilder(Context context) {
        super(context);
    }


    @Override
    public void buildViews(View wrapperView) {
        super.buildViews(wrapperView);
        mNumEt = (EditText) wrapperView.findViewById(R.id.desc_et);
        mDescFl = (FrameLayout) wrapperView.findViewById(R.id.fl_desc);
    }

    @Override
    public int getContentViewLayoutId() {
        return R.layout.inventory_bottom_sheet_input;
    }

    public InventoryBottomInputSheetBuilder setNumberHint(@StringRes int hint) {
        setNumberHint(mContext.getResources().getString(hint));
        return this;
    }

    public InventoryBottomInputSheetBuilder setNumberHint(String hint) {
        if (mNumEt != null) {
            mNumEt.setHint(hint);
        }
        return this;
    }

    public InventoryBottomInputSheetBuilder setNum(@StringRes int desc){
        setNum(mContext.getResources().getString(desc));
        return this;
    }

    public InventoryBottomInputSheetBuilder setNum(String desc){
        if(mNumEt != null) {
            mNumEt.setText(desc);
        }
        return this;
    }

    public String getNum(){
        if(mNumEt != null) {
            return mNumEt.getText().toString();
        }
        return null;
    }



    public InventoryBottomInputSheetBuilder setShowTipDesc(boolean showTipDesc){
        if(mNumEt != null) {
            mNumEt.setVisibility(showTipDesc ? View.VISIBLE : View.GONE);
        }
        return this;
    }

    public InventoryBottomInputSheetBuilder setShowDesc(boolean showDesc){
        if(mDescFl != null) {
            mDescFl.setVisibility(showDesc ? View.VISIBLE : View.GONE);
        }
        return this;
    }
}
