package com.facilityone.wireless.inventory.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facilityone.wireless.basiclib.utils.StringUtils;
import com.facilityone.wireless.inventory.R;
import com.facilityone.wireless.inventory.model.StorageService;

/**
 * Created by peter.peng on 2018/12/11.
 * 库存查询仓库列表适配器
 */

public class StorageListAdapter extends BaseQuickAdapter<StorageService.WareHouse,BaseViewHolder>{

    public StorageListAdapter() {
        super(R.layout.adapter_storage_item);
    }

    @Override
    protected void convert(BaseViewHolder helper, StorageService.WareHouse item) {
        if(item != null) {
            helper.setText(R.id.storage_item_name_tv, StringUtils.formatString(item.warehouseName));
            helper.setText(R.id.storage_item_administrator_tv, StringUtils.formatString(item.contact));
            helper.setText(R.id.storage_item_location_tv, StringUtils.formatString(item.location));
            helper.setText(R.id.storage_item_material_type_tv, StringUtils.formatString(item.materialTypeCount));
            helper.setText(R.id.storage_item_material_amount_tv, StringUtils.formatString(item.materialCount));
            helper.setText(R.id.storage_item_lack_material_count_tv, StringUtils.formatString(item.lackMaterialCount));

            helper.setGone(R.id.storage_item_dash_line, helper.getLayoutPosition() != getData().size() - 1);
            helper.setGone(R.id.storage_item_secant_line, helper.getLayoutPosition() == getData().size() - 1);
        }
    }
}
