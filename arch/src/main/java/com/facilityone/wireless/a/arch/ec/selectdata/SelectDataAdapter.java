package com.facilityone.wireless.a.arch.ec.selectdata;

import android.graphics.Color;
import androidx.annotation.Nullable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facilityone.wireless.a.arch.R;
import com.facilityone.wireless.a.arch.ec.module.ISelectDataService;
import com.facilityone.wireless.a.arch.ec.module.SelectDataBean;
import com.facilityone.wireless.basiclib.utils.StringUtils;

import java.util.List;

/**
 * Author：gary
 * Email: xuhaozv@163.com
 * description:
 * Date: 2018/10/25 12:13 PM
 */
public class SelectDataAdapter extends BaseQuickAdapter<SelectDataBean, BaseViewHolder> {

    private int mFromType;
    private boolean isShowSubTitle;

    public SelectDataAdapter(@Nullable List<SelectDataBean> data, int type) {
        super(R.layout.fragment_arch_select_data_item, data);
        mFromType = type;
        isShowSubTitle = false;
    }

    @Override
    protected void convert(BaseViewHolder helper, SelectDataBean item) {
        if (item == null) {
            return;
        }
        SpannableStringBuilder ssb = null;
        if (item.getParentId() == null){
            ssb = new SpannableStringBuilder(StringUtils.formatString(item.getName()));
        }else {
            if (item.getDesc().isEmpty()){
                ssb = new SpannableStringBuilder(StringUtils.formatString(item.getName()));
            }else {
                ssb = new SpannableStringBuilder(StringUtils.formatString(item.getDesc()));
            }
        }
        ForegroundColorSpan span = new ForegroundColorSpan(Color.RED);
        ssb.setSpan(span, item.getStart(), item.getEnd(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        helper.setText(R.id.title_tv, ssb);

        helper.setGone(R.id.subtitle_tv, (mFromType == ISelectDataService.DATA_TYPE_EQU ||
                mFromType == ISelectDataService.DATA_TYPE_EQU_ALL ||
                mFromType == ISelectDataService.DATA_TYPE_FAULT_OBJECT||
                mFromType == ISelectDataService.DATA_TYPE_REASON||
                mFromType== ISelectDataService.DATA_TYPE_INVALIDD||
                isShowSubTitle) && item.getParentId() != null);
        helper.setGone(R.id.right_icon, item.getHaveChild());

        SpannableStringBuilder fullSsb = new SpannableStringBuilder(StringUtils.formatString(item.getFullName()));
        ForegroundColorSpan fullSpan = new ForegroundColorSpan(Color.RED);
        fullSsb.setSpan(fullSpan, item.getSubStart(), item.getSubEnd(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        helper.setText(R.id.subtitle_tv, fullSsb);

        helper.addOnClickListener(R.id.root_ll);

        int layoutPosition = helper.getLayoutPosition();
        helper.setGone(R.id.view_dash, getData().size() - 1 != layoutPosition);
        helper.setGone(R.id.view_line, getData().size() - 1 == layoutPosition);
    }

    public void setShowSubTitle(boolean showSubTitle) {
        isShowSubTitle = showSubTitle;
    }
}
