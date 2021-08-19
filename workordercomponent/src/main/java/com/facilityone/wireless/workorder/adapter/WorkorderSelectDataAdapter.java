package com.facilityone.wireless.workorder.adapter;

import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facilityone.wireless.a.arch.R;
import com.facilityone.wireless.a.arch.ec.module.ISelectDataService;
import com.facilityone.wireless.a.arch.ec.module.SelectDataBean;
import com.facilityone.wireless.basiclib.utils.StringUtils;
import com.facilityone.wireless.workorder.module.WorkorderService;

import java.util.List;

/**
 * Author：gary
 * Email: xuhaozv@163.com
 * description:
 * Date: 2018/10/25 12:13 PM
 */
public class WorkorderSelectDataAdapter extends BaseQuickAdapter<WorkorderService.WorkorderReasonBean, BaseViewHolder> {

    private int mFromType;
    private boolean isShowSubTitle;

    public WorkorderSelectDataAdapter(@Nullable List<WorkorderService.WorkorderReasonBean> data, int type) {
        super(R.layout.fragment_arch_select_data_item, data);
        mFromType = type;
        isShowSubTitle = false;
    }

    @Override
    protected void convert(BaseViewHolder helper, WorkorderService.WorkorderReasonBean item) {
        if (item == null) {
            return;
        }

//        SpannableStringBuilder ssb = new SpannableStringBuilder(StringUtils.formatString(item.getName()));
        ForegroundColorSpan span = new ForegroundColorSpan(Color.RED);
//        ssb.setSpan(span, item.getStart(), item.getEnd(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        helper.setText(R.id.title_tv, ssb);

        helper.setGone(R.id.subtitle_tv, mFromType == ISelectDataService.DATA_TYPE_EQU || mFromType == ISelectDataService.DATA_TYPE_EQU_ALL || isShowSubTitle);
//        helper.setGone(R.id.right_icon, item.getHaveChild());

//        SpannableStringBuilder fullSsb = new SpannableStringBuilder(StringUtils.formatString(item.getFullName()));
        ForegroundColorSpan fullSpan = new ForegroundColorSpan(Color.RED);
//        fullSsb.setSpan(fullSpan, item.getSubStart(), item.getSubEnd(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        helper.setText(R.id.subtitle_tv, fullSsb);

        helper.addOnClickListener(R.id.root_ll);

        int layoutPosition = helper.getLayoutPosition();
        helper.setGone(R.id.view_dash, getData().size() - 1 != layoutPosition);
        helper.setGone(R.id.view_line, getData().size() - 1 == layoutPosition);
    }

    public void setShowSubTitle(boolean showSubTitle) {
        isShowSubTitle = showSubTitle;
    }
}
