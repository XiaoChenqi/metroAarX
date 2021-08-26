package com.facilityone.wireless.inventory.adapter;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facilityone.wireless.a.arch.utils.NoDoubleClickListener;
import com.facilityone.wireless.basiclib.utils.StringUtils;
import com.facilityone.wireless.inventory.R;
import com.facilityone.wireless.inventory.model.InventoryConstant;
import com.facilityone.wireless.inventory.model.MaterialService;
import com.mcxtzhang.swipemenulib.SwipeMenuLayout;

import java.util.List;

/**
 * Created by peter.peng on 2018/11/28.
 * 入库、出库、移库、盘点、物资预定物资适配器
 */

public class MaterialAdapter extends BaseQuickAdapter<MaterialService.MaterialInfo, BaseViewHolder> {

    private final int mType;
    private OnItemClick mOnItemClick;

    public MaterialAdapter(List<MaterialService.MaterialInfo> data, int type) {
        super(R.layout.adapter_material_item, data);
        this.mType = type;
    }

    @Override
    protected void convert(final BaseViewHolder helper, final MaterialService.MaterialInfo item) {
        if (item != null) {
            helper.setText(R.id.material_item_code_tv, StringUtils.formatString(item.code));
            helper.setText(R.id.material_item_name_tv, StringUtils.formatString(item.name));
            helper.setText(R.id.material_item_brand_tv, StringUtils.formatString(item.brand));
            helper.setText(R.id.material_item_model_tv, StringUtils.formatString(item.model));
            if (mType == InventoryConstant.INVENTORY_OUT || mType == InventoryConstant.INVENTORY_RESERVE) {
                helper.setText(R.id.material_item_count_name_tv, R.string.inventory_effective_number);
                helper.setText(R.id.material_item_count_tv, item.realNumber == null ? "0.00" : StringUtils.formatFloatCost(item.realNumber));
            } else {
                helper.setText(R.id.material_item_count_name_tv, R.string.inventory_total_count);
                helper.setText(R.id.material_item_count_tv, item.totalNumber == null ? "0.00" : StringUtils.formatFloatCost(item.totalNumber));
            }

            String typeValue = mContext.getString(R.string.inventory_number);
            switch (mType) {
                case InventoryConstant.INVENTORY_IN:
                    typeValue = mContext.getString(R.string.inventory_material_in_quantity);
                    break;
                case InventoryConstant.INVENTORY_OUT:
                    typeValue = mContext.getString(R.string.inventory_material_out_quantity);
                    break;
                case InventoryConstant.INVENTORY_MOVE:
                    typeValue = mContext.getString(R.string.inventory_material_move_quantity);
                    break;
                case InventoryConstant.INVENTORY_CHECK:
                    typeValue = mContext.getString(R.string.inventory_material_check_quantity);
                    break;
                case InventoryConstant.INVENTORY_RESERVE:
                    typeValue = mContext.getString(R.string.inventory_material_reserve_quantity);
                    break;
            }
            helper.setText(R.id.material_item_type_tv, typeValue);
            helper.setText(R.id.material_item_out_count_tv, item.number == null ? "0.00" : StringUtils.formatFloatCost(item.number));
            helper.setGone(R.id.material_item_shelve_ll, !(mType == InventoryConstant.INVENTORY_RESERVE));
            helper.setText(R.id.material_item_shelve_tv, StringUtils.formatString(item.shelves));

            helper.setGone(R.id.material_item_dash_line, helper.getLayoutPosition() != getData().size() - 1);
            helper.setGone(R.id.material_item_secant_line, helper.getLayoutPosition() == getData().size() - 1);

            helper.addOnClickListener(R.id.material_item_content_ll);

            helper.setOnClickListener(R.id.btn_delete, new NoDoubleClickListener() {
                @Override
                protected void onNoDoubleClick(View view) {
                    ((SwipeMenuLayout) helper.itemView).quickClose();
                    if (mOnItemClick != null) {
                        mOnItemClick.onBtnDelete(item, helper.getLayoutPosition());
                    }
                }
            });
        }
    }

    public void setOnItemClick(OnItemClick onItemClick) {
        mOnItemClick = onItemClick;
    }

    public interface OnItemClick {
        void onBtnDelete(MaterialService.MaterialInfo material, int position);
    }
}
