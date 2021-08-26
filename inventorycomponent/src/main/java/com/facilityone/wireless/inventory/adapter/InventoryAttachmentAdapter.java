package com.facilityone.wireless.inventory.adapter;

import androidx.annotation.Nullable;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facilityone.wireless.a.arch.ec.module.AttachmentBean;
import com.facilityone.wireless.basiclib.utils.StringUtils;
import com.facilityone.wireless.inventory.R;

import java.util.List;

/**
 * Created by peter.peng on 2018/12/12.
 * 附件adapter
 */

public class InventoryAttachmentAdapter extends BaseQuickAdapter<AttachmentBean,BaseViewHolder> {

    public InventoryAttachmentAdapter(@Nullable List<AttachmentBean> data) {
        super(R.layout.adapter_inventory_attachment_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, AttachmentBean item) {
        if(item != null) {
            helper.setText(R.id.inventory_attachment_item_name_tv, StringUtils.formatString(item.name));

            if(helper.getLayoutPosition() == getData().size() - 1) {
                LinearLayout attachmentLl = helper.getView(R.id.inventory_attachment_item);
                attachmentLl.setPadding(0,0,0,0);
            }
        }
    }
}
