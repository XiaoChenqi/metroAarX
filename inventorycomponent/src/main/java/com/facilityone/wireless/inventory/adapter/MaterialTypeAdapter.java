package com.facilityone.wireless.inventory.adapter;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facilityone.wireless.a.arch.ec.module.AttachmentBean;
import com.facilityone.wireless.inventory.R;

import java.util.List;

/**
 * Created by peter.peng on 2018/12/11.
 * 库存查询模块物资列表查询界面物资数量类型适配器
 */

public class MaterialTypeAdapter extends BaseQuickAdapter<AttachmentBean, BaseViewHolder> implements BaseQuickAdapter.OnItemClickListener {


    private List<AttachmentBean> mData;


    public MaterialTypeAdapter(@Nullable List<AttachmentBean> data) {
        super(R.layout.adapter_material_query_type_item, data);
        this.mData = data;
        setOnItemClickListener(this);
    }

    @Override
    protected void convert(BaseViewHolder helper, AttachmentBean item) {
        if (item != null) {
            helper.setText(R.id.material_query_type_tv, item.name);

            if (item.check) {
                helper.setVisible(R.id.material_query_status_checked_iv, true);
                helper.setTextColor(R.id.material_query_type_tv, ContextCompat.getColor(mContext, R.color.green_5eba15));
            } else {
                helper.setGone(R.id.material_query_status_checked_iv, false);
                helper.setTextColor(R.id.material_query_type_tv, ContextCompat.getColor(mContext, R.color.grey_6));
            }

            helper.setGone(R.id.material_query_status_dash_line, helper.getLayoutPosition() != getData().size() - 1);
            helper.setGone(R.id.material_query_status_secant_line, helper.getLayoutPosition() == getData().size() - 1);

        }
    }


    /**
     * 当recyclerview的item被点击时回调
     *
     * @param adapter
     * @param view
     * @param position
     */
    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        boolean isCheck = !mData.get(position).check;
        for (int i = 0; i < mData.size(); i++) {
            AttachmentBean attachmentBean = mData.get(i);
            attachmentBean.check = false;
        }
        mData.get(position).check = isCheck;

        notifyDataSetChanged();
    }
}
