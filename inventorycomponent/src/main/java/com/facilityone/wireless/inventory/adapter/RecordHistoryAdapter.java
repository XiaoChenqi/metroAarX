package com.facilityone.wireless.inventory.adapter;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.TimeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facilityone.wireless.basiclib.utils.DateUtils;
import com.facilityone.wireless.basiclib.utils.StringUtils;
import com.facilityone.wireless.inventory.R;
import com.facilityone.wireless.inventory.model.InventoryHelper;
import com.facilityone.wireless.inventory.model.ReserveService;

import java.util.Date;
import java.util.List;

/**
 * Created by peter.peng on 2019/6/18.
 * 预订单详情操作记录适配器
 */

public class RecordHistoryAdapter extends BaseQuickAdapter<ReserveService.RecordHistory,BaseViewHolder>{

    public RecordHistoryAdapter(@Nullable List<ReserveService.RecordHistory> data) {
        super(R.layout.adapter_record_history_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ReserveService.RecordHistory item) {
        if(item != null) {
            helper.setText(R.id.record_history_item_handler_tv, StringUtils.formatString(item.handler));
            if(item.operationDate != null) {
                helper.setText(R.id.record_history_item_operation_date_tv, TimeUtils.date2String(new Date(item.operationDate), DateUtils.SIMPLE_DATE_FORMAT_ALL));
            }else {
                helper.setText(R.id.record_history_item_operation_date_tv,"");
            }
            if(item.step != null) {
                helper.setText(R.id.record_history_item_step_tv,StringUtils.formatString(InventoryHelper.getRecordHistoryStepMap(mContext).get(item.step)));
            }else {
                helper.setText(R.id.record_history_item_step_tv,"");
            }
            helper.setText(R.id.record_history_item_content_tv,StringUtils.formatString(item.content));

            helper.setGone(R.id.record_history_item_secant_line, helper.getLayoutPosition() == getData().size() - 1);
            helper.setGone(R.id.record_history_item_dash_line, helper.getLayoutPosition() != getData().size() - 1);
        }
    }
}
