package com.facilityone.wireless.construction.adapter

import com.chad.library.adapter.base.MultipleItemRvAdapter
import com.chad.library.adapter.base.BaseViewHolder
import androidx.recyclerview.widget.RecyclerView
import com.facilityone.wireless.construction.module.ConstructionService


class ElectronicLedgerAdapter(data: List<ConstructionService.ElectronicLedgerEntity?>?) :
    MultipleItemRvAdapter<ConstructionService.ElectronicLedgerEntity, BaseViewHolder?>(data) {
    var tempRecycleView: RecyclerView? = null
    override fun getViewType(entity: ConstructionService.ElectronicLedgerEntity): Int {
       return when(entity.type){
            ConstructionService.ElectronicLedgerEntity.TYPE_HEADER->   TYPE_HEADER
           ConstructionService.ElectronicLedgerEntity.TYPE_SUB_HEADER->   TYPE_SUB_HEADER
           ConstructionService.ElectronicLedgerEntity.TYPE_RADIO->   TYPE_RADIO
           ConstructionService.ElectronicLedgerEntity.TYPE_RADIO_SUB->   TYPE_RADIO_SUB
           ConstructionService.ElectronicLedgerEntity.TYPE_EDIT->   TYPE_EDIT
           ConstructionService.ElectronicLedgerEntity.TYPE_RESULT->   TYPE_RESULT_A
           else -> {
               0
           }
       }

    }

    override fun registerItemProvider() {
        mProviderDelegate.registerProvider(ELHeaderProvider())
        mProviderDelegate.registerProvider(ELSubTitleProvider())
        mProviderDelegate.registerProvider(ELSelectorProvider())
        mProviderDelegate.registerProvider(ELTextProvider())
        mProviderDelegate.registerProvider(ELSubSelectorProvider())
        mProviderDelegate.registerProvider(ELResultProvider())
    }

    companion object {
        const val TYPE_HEADER = 100
        const val TYPE_SUB_HEADER = 101
        const val TYPE_RADIO = 200
        const val TYPE_RADIO_SUB = 210
        const val TYPE_EDIT = 300
        const val ITEM_0_PAYLOAD = 901
        const val TYPE_RESULT_A = 400
    }

    init {
        finishInitialize()
    }
}