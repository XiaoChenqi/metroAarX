package com.facilityone.wireless.inventory.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facilityone.wireless.basiclib.utils.StringUtils;
import com.facilityone.wireless.inventory.R;
import com.facilityone.wireless.inventory.model.ProfessionalService;

import java.util.List;

/**
 * Author：gary
 * Email: xuhaozv@163.com
 * description:执行人选择适配器
 * Date: 2018/7/17 下午2:43
 */
public class InventoryProListAdapter extends BaseQuickAdapter<ProfessionalService.InventoryProBean, BaseViewHolder> {

    private Context mContext;
    private boolean showTitle;

    public InventoryProListAdapter(Context context, boolean showTitle, @Nullable List<ProfessionalService.InventoryProBean> data) {
        super(R.layout.adapter_pro_list_item, data);
        mContext = context;
        this.showTitle = showTitle;
    }

    @Override
    protected void convert(BaseViewHolder helper, ProfessionalService.InventoryProBean item) {
        SpannableStringBuilder ssb = new SpannableStringBuilder(StringUtils.formatString(item.configName));
        ForegroundColorSpan span = new ForegroundColorSpan(Color.RED);
        ssb.setSpan(span, item.start, item.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        helper.setText(R.id.tv_name, ssb);
//        helper.setText(R.id.tv_undo_number, item.woNumber == null ? "0" : item.woNumber + "");
        helper.setChecked(R.id.cb_check, item.checked);

        helper.setGone(R.id.tv_undo_number, showTitle);
        helper.setGone(R.id.tv_status, showTitle);

//        if (item.status != null && showTitle) {
//            switch (item.status) {
//                case WorkorderConstant.WORKORDER_STATUS_PERSONAL_ON:
//                    helper.setVisible(R.id.tv_status, true);
//                    helper.setText(R.id.tv_status, R.string.workorder_person_on_guard);
//                    helper.setTextColor(R.id.tv_status, mContext.getResources().getColor(R.color.green_1ab394));
//                    break;
//                case WorkorderConstant.WORKORDER_STATUS_PERSONAL_NO:
//                    helper.setVisible(R.id.tv_status, true);
//                    helper.setText(R.id.tv_status, R.string.workorder_person_off_guard);
//                    helper.setTextColor(R.id.tv_status, mContext.getResources().getColor(R.color.orange_ff9f0e));
//                    break;
//                default:
//                    helper.setVisible(R.id.tv_status, false);
//                    break;
//            }
//        }else{
//            helper.setVisible(R.id.tv_status, false);
//        }

        int layoutPosition = helper.getLayoutPosition();
        helper.setGone(R.id.view_dash, layoutPosition != getData().size() - 1);
        helper.setGone(R.id.view_line, layoutPosition == getData().size() - 1);
    }
}
