package com.facilityone.wireless.inventory.adapter;

import androidx.annotation.Nullable;
import android.view.View;

import com.blankj.utilcode.util.TimeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facilityone.wireless.a.arch.utils.NoDoubleClickListener;
import com.facilityone.wireless.basiclib.utils.DateUtils;
import com.facilityone.wireless.basiclib.utils.StringUtils;
import com.facilityone.wireless.inventory.R;
import com.facilityone.wireless.inventory.model.BatchService;
import com.mcxtzhang.swipemenulib.SwipeMenuLayout;

import java.util.Date;
import java.util.List;

/**
 * Created by peter.peng on 2018/11/29.
 * 入库批次适配器
 */

public class BatchInAdapter extends BaseQuickAdapter<BatchService.Batch,BaseViewHolder> {

    private OnItemClick mOnItemClick;

    public BatchInAdapter(@Nullable List<BatchService.Batch> data) {
        super(R.layout.adapter_batch_in_item, data);
    }

    @Override
    protected void convert(final BaseViewHolder helper, final BatchService.Batch item) {
        if(item != null) {
            helper.setText(R.id.batch_in_item_provider_tv, StringUtils.formatString(item.providerName));
            helper.setText(R.id.batch_in_item_due_date_tv, item.dueDate == null ? "" : TimeUtils.date2String(new Date(item.dueDate), DateUtils.SIMPLE_DATE_FORMAT_YMD));
            helper.setText(R.id.batch_in_item_price_tv, StringUtils.formatStringCost(item.price));
            helper.setText(R.id.batch_in_item_number_tv, item.number == null ? "" : StringUtils.formatFloatCost(item.number));

            helper.setGone(R.id.batch_in_item_dash_line, helper.getLayoutPosition() != getData().size() - 1);
            helper.setGone(R.id.batch_in_item_secant_line, helper.getLayoutPosition() == getData().size() - 1);

            helper.addOnClickListener(R.id.batch_in_item_content_ll);

            helper.setOnClickListener(R.id.btn_delete, new NoDoubleClickListener() {
                @Override
                protected void onNoDoubleClick(View view) {
                    ((SwipeMenuLayout)helper.itemView).quickClose();
                    if(mOnItemClick != null) {
                        mOnItemClick.onBtnDelete(item,helper.getLayoutPosition());
                    }
                }
            });

        }
    }

    public void setOnItemClick(OnItemClick onItemClick) {
        mOnItemClick = onItemClick;
    }

    public interface OnItemClick {
        void onBtnDelete(BatchService.Batch batch, int position);
    }
}
