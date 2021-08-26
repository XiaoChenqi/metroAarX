package com.facilityone.wireless.inventory.adapter;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.TimeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facilityone.wireless.basiclib.utils.DateUtils;
import com.facilityone.wireless.basiclib.utils.StringUtils;
import com.facilityone.wireless.inventory.R;
import com.facilityone.wireless.inventory.model.MaterialService;

import java.util.Date;
import java.util.List;

/**
 * Created by peter.peng on 2018/12/12.
 * 物资详情当前物资记录适配器
 */

public class MaterialRecordAdapter extends BaseQuickAdapter<MaterialService.MaterialRecord, BaseViewHolder> {

    public MaterialRecordAdapter(@Nullable List<MaterialService.MaterialRecord> data) {
        super(R.layout.adapter_material_record_item, data);
    }
    public MaterialRecordAdapter() {
        super(R.layout.adapter_material_record_item);
    }

    @Override
    protected void convert(BaseViewHolder helper, MaterialService.MaterialRecord item) {
        if (item != null) {
            helper.setText(R.id.material_record_item_in_code_tv, StringUtils.formatString(item.code));
            helper.setText(R.id.material_record_item_provider_tv, StringUtils.formatString(item.provider));
            helper.setText(R.id.material_record_item_price_tv, StringUtils.formatString(item.price));
            helper.setText(R.id.material_record_item_in_number_tv, item.number == null ? "0.00" : StringUtils.formatFloatCost(item.number));
            helper.setText(R.id.material_record_item_real_number_tv, item.validNumber == null ? "0.00" : StringUtils.formatFloatCost(item.validNumber));
            helper.setText(R.id.material_record_item_in_date_tv, item.date == null ? "" : TimeUtils.date2String(new Date(item.date), DateUtils.SIMPLE_DATE_FORMAT_YMD));
            helper.setText(R.id.material_record_item_due_date_tv, item.dueDate == null ? "" : TimeUtils.date2String(new Date(item.dueDate), DateUtils.SIMPLE_DATE_FORMAT_YMD));

            helper.setGone(R.id.material_record_item_dash_line, helper.getLayoutPosition() != getData().size() - 1);
            helper.setGone(R.id.material_record_item_secant_line, helper.getLayoutPosition() == getData().size() - 1);
        }
    }
}
