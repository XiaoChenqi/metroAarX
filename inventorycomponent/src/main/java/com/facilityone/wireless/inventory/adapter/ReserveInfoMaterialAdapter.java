package com.facilityone.wireless.inventory.adapter;

import androidx.annotation.Nullable;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facilityone.wireless.basiclib.utils.StringUtils;
import com.facilityone.wireless.inventory.R;
import com.facilityone.wireless.inventory.model.InventoryConstant;
import com.facilityone.wireless.inventory.model.MaterialService;

import java.util.List;

/**
 * Created by peter.peng on 2018/12/4.
 * 预定详情界面物资适配器
 */

public class ReserveInfoMaterialAdapter extends BaseQuickAdapter<MaterialService.MaterialInfo, BaseViewHolder> {

    private int mFromType;
    private int mStatus;

    public ReserveInfoMaterialAdapter(int fromType, @Nullable List<MaterialService.MaterialInfo> data) {
        super(R.layout.adapter_reserver_info_material_item, data);
        this.mFromType = fromType;
    }

    @Override
    protected void convert(BaseViewHolder helper, MaterialService.MaterialInfo item) {
        if (item != null) {
            helper.setText(R.id.reserve_info_material_item_code_tv, StringUtils.formatString(item.code));
            helper.setText(R.id.reserve_info_material_item_name_tv, StringUtils.formatString(item.name));
            helper.setText(R.id.reserve_info_material_item_model_tv, StringUtils.formatString(item.model));
            helper.setText(R.id.reserve_info_material_item_brand_tv, StringUtils.formatString(item.brand));
            helper.setText(R.id.reserve_info_material_item_unit_tv, StringUtils.formatString(item.unit));

            helper.setText(R.id.reserve_info_material_item_reserve_count_tv, item.bookAmount != null ?
                    mContext.getString(R.string.inventory_reserve_count) + StringUtils.formatFloatCost(item.bookAmount) : mContext.getString(R.string.inventory_reserve_count)+"0.00");

            if(mStatus == InventoryConstant.RESERVE_STATUS_VERIFY_PASS) {
                if (mFromType == InventoryConstant.INVENTORY_OUT ) {
                    helper.setGone(R.id.reserve_info_material_item_price_tv, false);
                }else {
                    helper.setGone(R.id.reserve_info_material_item_price_tv, true);
                }
                helper.setText(R.id.reserve_info_material_item_price_tv, "¥ "+StringUtils.formatFloatCost(item.cost));
                helper.setText(R.id.reserve_info_material_item_count_tv, item.receiveAmount != null ?
                        mContext.getString(R.string.inventory_receiver_count) + StringUtils.formatFloatCost(item.receiveAmount)
                        : mContext.getString(R.string.inventory_receiver_count)+ "0.00");
                View itemView = helper.itemView;
                itemView.setEnabled(true);
            }else if(mStatus == InventoryConstant.RESERVE_STATUS_DELIVERIED) {
                helper.setVisible(R.id.reserve_info_material_item_price_tv, true);
                helper.setText(R.id.reserve_info_material_item_price_tv, "¥ "+StringUtils.formatFloatCost(item.cost));
                helper.setText(R.id.reserve_info_material_item_count_tv, item.receiveAmount != null ?
                        mContext.getString(R.string.inventory_receiver_count) + StringUtils.formatFloatCost(item.receiveAmount)
                        : mContext.getString(R.string.inventory_receiver_count)+ "0.00");
                helper.setVisible(R.id.reserve_info_material_item_count_tv, true);
                View itemView = helper.itemView;
                itemView.setEnabled(false);
            }else {
                helper.setVisible(R.id.reserve_info_material_item_price_tv, true);
                helper.setText(R.id.reserve_info_material_item_price_tv, "¥ "+StringUtils.formatFloatCost(item.cost));
                helper.setText(R.id.reserve_info_material_item_count_tv, mContext.getString(R.string.inventory_receiver_count)+"0.00");
                helper.setVisible(R.id.reserve_info_material_item_count_tv, false);
                View itemView = helper.itemView;
                itemView.setEnabled(false);
            }

            helper.setGone(R.id.reserve_info_material_item_dash_line, helper.getLayoutPosition() != getData().size() - 1);
            helper.setGone(R.id.reserve_info_material_item_secant_line, helper.getLayoutPosition() == getData().size() - 1);

        }
    }

    public void setStatus(int status) {
        mStatus = status;
    }
}
