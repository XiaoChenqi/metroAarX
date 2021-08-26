package com.facilityone.wireless.maintenance.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.MultipleItemRvAdapter;
import com.chad.library.adapter.base.provider.BaseItemProvider;
import com.chad.library.adapter.base.util.MultiTypeDelegate;
import com.chad.library.adapter.base.util.ProviderDelegate;
import com.facilityone.wireless.maintenance.model.MaintenanceEnity;

import java.util.List;

public class ElectronicLedgerAdapter extends MultipleItemRvAdapter<MaintenanceEnity.ElectronicLedgerEntity, BaseViewHolder> {

    public static final int TYPE_HEADER = 100;
    public static final int TYPE_RADIO = 200;
    public static final int TYPE_RADIO_SUB = 210;
    public static final int TYPE_EDIT = 300;

    public ElectronicLedgerAdapter(@Nullable  List<MaintenanceEnity.ElectronicLedgerEntity> data) {
        super(data);
        finishInitialize();
    }





    @Override
    protected int getViewType(MaintenanceEnity.ElectronicLedgerEntity entity) {
        if (entity.type == MaintenanceEnity.ElectronicLedgerEntity.TYPE_HEADER) {
            return TYPE_HEADER;
        } else if (entity.type == MaintenanceEnity.ElectronicLedgerEntity.TYPE_RADIO) {
            return TYPE_RADIO;
        }
        else if (entity.type == MaintenanceEnity.ElectronicLedgerEntity.TYPE_RADIO_SUB) {
            return TYPE_RADIO_SUB;
        }else if (entity.type == MaintenanceEnity.ElectronicLedgerEntity.TYPE_EDIT) {
            return TYPE_EDIT;
        }
        return 0;
    }

    @Override
    public void registerItemProvider() {
        mProviderDelegate.registerProvider(new ELHeaderProvider());
        mProviderDelegate.registerProvider(new ELSelectorProvider());
        mProviderDelegate.registerProvider(new ELTextProvider());
        mProviderDelegate.registerProvider(new ELSubSelectorProvider());
    }
}
