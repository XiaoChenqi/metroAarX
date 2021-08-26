package com.facilityone.wireless.inventory.adapter;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.TimeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facilityone.wireless.basiclib.utils.DateUtils;
import com.facilityone.wireless.basiclib.utils.StringUtils;
import com.facilityone.wireless.inventory.R;
import com.facilityone.wireless.inventory.model.BatchService;
import com.facilityone.wireless.inventory.model.InventoryConstant;

import java.util.Date;
import java.util.List;

/**
 * Created by peter.peng on 2018/11/29.
 * 出库、盘点、移库库批次适配器
 */

public class BatchAdapter extends BaseQuickAdapter<BatchService.Batch, BaseViewHolder> {

    private int mType;

    public BatchAdapter(@Nullable List<BatchService.Batch> data, int type) {
        super(R.layout.adapter_batch_item, data);
        this.mType = type;
    }

    @Override
    protected void convert(final BaseViewHolder helper, final BatchService.Batch item) {
        if (item != null) {
            helper.setText(R.id.batch_item_provider_tv, StringUtils.formatString(item.providerName));
            helper.setText(R.id.batch_item_date_tv, item.date == null ? "" : TimeUtils.date2String(new Date(item.date), DateUtils.SIMPLE_DATE_FORMAT_YMD));
            helper.setText(R.id.batch_item_price_tv, StringUtils.formatStringCost(item.cost));
            helper.setText(R.id.batch_item_due_date_tv, item.dueDate == null ? "" : TimeUtils.date2String(new Date(item.dueDate), DateUtils.SIMPLE_DATE_FORMAT_YMD));
            helper.setText(R.id.batch_item_amount_tv, item.amount == null ? "0.00" : StringUtils.formatFloatCost(item.amount));
            helper.setText(R.id.batch_item_number_tv, item.number == null ? "0.00" : StringUtils.formatFloatCost(item.number));
            helper.setGone(R.id.batch_item_desc_tv,false);
            switch (mType) {
                case InventoryConstant.INVENTORY_BATCH_RESERVE_OUT:
                case InventoryConstant.INVENTORY_BATCH_DIRECT_OUT:
                case InventoryConstant.INVENTORY_INFO_BATCH_OUT:
                    helper.setText(R.id.batch_item_number_name_tv, R.string.inventory_material_out_quantity);
                    helper.setGone(R.id.batch_item_desc_ll, false);
                    break;
                case InventoryConstant.INVENTORY_BATCH_MOVE:
                case InventoryConstant.INVENTORY_INFO_BATCH_MOVE:
                    helper.setText(R.id.batch_item_number_name_tv, R.string.inventory_material_move_quantity);
                    helper.setGone(R.id.batch_item_desc_ll, false);
                    break;
                case InventoryConstant.INVENTORY_BATCH_CHECK:
                case InventoryConstant.INVENTORY_INFO_BATCH_CHECK:
                    helper.setText(R.id.batch_item_number_name_tv, R.string.inventory_material_check_quantity);
                    helper.setGone(R.id.batch_item_desc_ll, true);
                    if (item.adjustNumber != null) {
                        helper.setVisible(R.id.batch_item_adjust_number_tv, true);
                        helper.setText(R.id.batch_item_adjust_number_tv, item.adjustNumber > 0
                                ? "+" + StringUtils.formatFloatCost(item.adjustNumber)
                                : StringUtils.formatFloatCost(item.adjustNumber));
                    } else {
                        helper.setGone(R.id.batch_item_adjust_number_tv, false);
                        helper.setText(R.id.batch_item_adjust_number_tv, "");
                    }
                    helper.setGone(R.id.batch_item_desc_tv,true);
                    helper.setText(R.id.batch_item_desc_tv,StringUtils.formatString(item.desc));
                    break;
            }


            helper.setGone(R.id.batch_item_dash_line, helper.getLayoutPosition() != getData().size() - 1);
            helper.setGone(R.id.batch_item_secant_line, helper.getLayoutPosition() == getData().size() - 1);


        }
    }

}
