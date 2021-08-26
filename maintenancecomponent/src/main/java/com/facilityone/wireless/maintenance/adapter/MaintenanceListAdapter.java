package com.facilityone.wireless.maintenance.adapter;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.TimeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facilityone.wireless.basiclib.utils.DateUtils;
import com.facilityone.wireless.maintenance.R;
import com.facilityone.wireless.maintenance.model.MaintenanceConstant;
import com.facilityone.wireless.maintenance.model.MaintenanceEnity;

import java.util.Date;
import java.util.List;

public class MaintenanceListAdapter extends BaseQuickAdapter<MaintenanceEnity.MaintenanceListEnity, BaseViewHolder> {

    int type;
    public MaintenanceListAdapter( @Nullable List<MaintenanceEnity.MaintenanceListEnity> data) {
        super(R.layout.adpater_maintenance_list_item, data);
    }
    public MaintenanceListAdapter( @Nullable int type) {
        super(R.layout.adpater_maintenance_list_item);
        this.type=type;
    }

    @Override
    protected void convert(BaseViewHolder helper, MaintenanceEnity.MaintenanceListEnity item) {
        if (item != null){
            helper.setText(R.id.code_tv,item.code+"");
            String  date2String = TimeUtils.date2String(new Date(item.createDateTime), DateUtils.SIMPLE_DATE_FORMAT_ALL);
            helper.setText(R.id.date_tv,date2String+"");
            helper.setText(R.id.describe_tv,item.woDescription+"");
            helper.setText(R.id.location_tv,item.location+"");

            switch (item.choice){
                case MaintenanceConstant.CHOICE_NO:
                    helper.setGone(R.id.ll_chekbox,false);
                    break;
                case MaintenanceConstant.CHOICE_All:
                    helper.setVisible(R.id.ll_chekbox,true);
                    helper.setBackgroundRes(R.id.im_checkbox,R.drawable.btn_check_off);
                    break;
                case MaintenanceConstant.CHOICE_UP:
                    helper.setVisible(R.id.ll_chekbox,true);
                    helper.setBackgroundRes(R.id.im_checkbox,R.drawable.btn_check_on);
                    break;
                case MaintenanceConstant.CHOICE_DOWN:
                    helper.setVisible(R.id.ll_chekbox,true);
                    helper.setBackgroundRes(R.id.im_checkbox,R.drawable.btn_check_off);
                    break;
                case MaintenanceConstant.CHOICE_OFF:
                    helper.setVisible(R.id.ll_chekbox,true);
                    helper.setBackgroundRes(R.id.im_checkbox,R.drawable.fm_workorder_tag_fill_archived_bg);
                    break;
            }
            int layoutPosition = helper.getLayoutPosition();
            if (type == MaintenanceConstant.MAINTENANCE_SEVEN && layoutPosition == getData().size() - 1) {
                helper.setGone(R.id.bottom_line_view, true);
                helper.setGone(R.id.center_line_view, false);
            } else {
                helper.setGone(R.id.center_line_view, true);
            }
        }

        }

}
