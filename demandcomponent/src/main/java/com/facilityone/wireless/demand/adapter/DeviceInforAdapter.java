package com.facilityone.wireless.demand.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facilityone.wireless.demand.R;
import com.facilityone.wireless.demand.module.DemandService;

import java.util.List;

public class DeviceInforAdapter extends BaseQuickAdapter<DemandService.DeviceInforListEnity, BaseViewHolder> {
    public DeviceInforAdapter(@Nullable List<DemandService.DeviceInforListEnity> data) {
        super(R.layout.demand_create_deviceinfo_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, DemandService.DeviceInforListEnity item) {
        helper.setText(R.id.demand_device_number_item,item.deviceNumber);
        helper.setText(R.id.demand_device_name_item,item.deviceName);
        helper.setText(R.id.demand_device_location_item,item.deviceLocation);
        helper.addOnClickListener(R.id.btn_delete_device_item);
    }
}
