package com.facilityone.wireless.inventory.adapter;

import android.text.TextUtils;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facilityone.wireless.a.arch.utils.UrlUtils;
import com.facilityone.wireless.basiclib.utils.ImageLoadUtils;
import com.facilityone.wireless.basiclib.utils.StringUtils;
import com.facilityone.wireless.inventory.R;
import com.facilityone.wireless.inventory.model.MaterialService;

/**
 * Created by peter.peng on 2018/12/11.
 * 库存查询物资列表适配器
 */

public class MaterialListAdapter extends BaseQuickAdapter<MaterialService.Material, BaseViewHolder> {

    public MaterialListAdapter() {
        super(R.layout.adapter_material_list_item);
    }

    @Override
    protected void convert(BaseViewHolder helper, MaterialService.Material item) {
        if (item != null) {
            helper.setText(R.id.material_list_item_name_tv, StringUtils.formatString(item.materialName)
                    + (TextUtils.isEmpty(item.materialCode) ? "" : "(" + StringUtils.formatString(item.materialCode) + ")"));
            helper.setGone(R.id.material_list_item_brand_tv, !TextUtils.isEmpty(item.materialBrand));
            helper.setText(R.id.material_list_item_brand_tv, StringUtils.formatString(item.materialBrand));
            helper.setGone(R.id.material_list_item_model_tv, !TextUtils.isEmpty(item.materialModel));
            helper.setText(R.id.material_list_item_model_tv, StringUtils.formatString(item.materialModel));
            helper.setGone(R.id.material_list_brand_model_ll, !(TextUtils.isEmpty(item.materialBrand) && TextUtils.isEmpty(item.materialModel)));
            helper.setText(R.id.material_list_item_total_number_tv, item.totalNumber == null ? "0.00" : StringUtils.formatFloatCost(item.totalNumber));
            helper.setText(R.id.material_list_item_min_number_tv, item.minNumber == null ? "0.00" : StringUtils.formatFloatCost(item.minNumber));

            if (item.totalNumber != null) {
                String text = mContext.getString(R.string.inventory_enough);
                int resId = R.drawable.inventory_tag_fill_blue_background;
                if ((item.minNumber != null && item.totalNumber <= item.minNumber) || item.totalNumber == 0) {
                    text = mContext.getString(R.string.inventory_lack);
                    resId = R.drawable.inventory_tag_fill_red_background;
                }
                helper.setBackgroundRes(R.id.material_list_item_status_tv, resId);
                helper.setText(R.id.material_list_item_status_tv, text);
            }

            if (item.pictures != null && item.pictures.size() > 0) {
                ImageLoadUtils.loadImageView(mContext, UrlUtils.getImagePath(item.pictures.get(0)),
                        ((ImageView) helper.getView(R.id.material_list_item_image_iv)), R.drawable.material_default_image, R.drawable.material_default_image);
            } else {
                helper.setImageResource(R.id.material_list_item_image_iv, R.drawable.material_default_image);
            }

            helper.setGone(R.id.material_list_item_dash_line, helper.getLayoutPosition() != getData().size() - 1);
            helper.setGone(R.id.material_list_item_secant_line, helper.getLayoutPosition() == getData().size() - 1);
        }
    }
}
