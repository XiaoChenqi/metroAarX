package com.facilityone.wireless.inventory.adapter;

import android.graphics.Color;
import androidx.core.content.ContextCompat;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facilityone.wireless.basiclib.utils.StringUtils;
import com.facilityone.wireless.inventory.R;
import com.facilityone.wireless.inventory.model.InventoryConstant;
import com.facilityone.wireless.inventory.model.InventorySelectDataBean;
import com.facilityone.wireless.inventory.model.MaterialService;

import java.util.List;

/**
 * Created by peter.peng on 2018/11/26.
 */

public class InventorySelectDataAdapter extends BaseQuickAdapter<InventorySelectDataBean,BaseViewHolder> {

    private final int mType;



    public InventorySelectDataAdapter(List<InventorySelectDataBean> data, int type) {
        super(R.layout.adapter_inventory_select_data_item,data);
        mType = type;
    }

    @Override
    protected void convert(BaseViewHolder helper, InventorySelectDataBean item) {
        if(item != null) {
            SpannableStringBuilder ssb = new SpannableStringBuilder(StringUtils.formatString(item.name));
            ForegroundColorSpan span = new ForegroundColorSpan(Color.RED);
            ssb.setSpan(span, item.start, item.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            helper.setText(R.id.inventory_select_data_item_title_tv, ssb);

            helper.setGone(R.id.inventory_select_data_item_ddash_line,helper.getLayoutPosition() != getData().size() - 1);
            helper.setGone(R.id.inventory_select_data_item_secant_line,helper.getLayoutPosition() == getData().size() - 1);

            if(mType == InventoryConstant.SELECT_MATERIAL
                    || mType == InventoryConstant.SELECT_MATERIAL_OUT
                    || mType == InventoryConstant.SELECT_MATERIAL_MOVE
                    || mType == InventoryConstant.SELECT_MATERIAL_RESERVE) {
                helper.setVisible(R.id.inventory_select_data_item_status_tv,true);
                MaterialService.Material material = (MaterialService.Material) item.target;

                if(!TextUtils.isEmpty(item.subStr)) {
                    helper.setGone(R.id.inventory_select_data_item_sub_ll,true);
                    SpannableStringBuilder subSsb = new SpannableStringBuilder(StringUtils.formatString(item.subStr));
                    ForegroundColorSpan subSpan = new ForegroundColorSpan(Color.RED);
                    subSsb.setSpan(subSpan, item.subStart, item.subEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    helper.setText(R.id.inventory_select_data_item_sub_tv, subSsb);
                }else {
                    helper.setGone(R.id.inventory_select_data_item_sub_ll,false);
                }

                if(material.totalNumber > 0) {
                    helper.setText(R.id.inventory_select_data_item_status_tv,R.string.inventory_material_in_stock);
                    helper.setTextColor(R.id.inventory_select_data_item_status_tv, ContextCompat.getColor(mContext,R.color.inventory_status_green));
                }else {
                    helper.setText(R.id.inventory_select_data_item_status_tv,R.string.inventory_material_not_in_stock);
                    helper.setTextColor(R.id.inventory_select_data_item_status_tv, ContextCompat.getColor(mContext,R.color.inventory_status_red));
                }
            }else {
                helper.setGone(R.id.inventory_select_data_item_sub_ll,false);
                helper.setGone(R.id.inventory_select_data_item_status_tv,false);
            }
        }
    }
}
