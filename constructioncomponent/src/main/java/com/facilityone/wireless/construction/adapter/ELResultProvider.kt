package com.facilityone.wireless.construction.adapter

import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.provider.BaseItemProvider
import com.facilityone.wireless.construction.R
import com.facilityone.wireless.construction.databinding.ItemResultBinding
import com.facilityone.wireless.construction.module.ConstructionService
import com.facilityone.wireless.construction.module.SelectorModel

class ELResultProvider : BaseItemProvider<ConstructionService.ElectronicLedgerEntity, BaseViewHolder>() {
    override fun viewType(): Int {
        return ElectronicLedgerAdapter.TYPE_RESULT_A
    }

    override fun layout(): Int {
        return R.layout.item_result
    }

    override fun convert(
        helper: BaseViewHolder,
        item: ConstructionService.ElectronicLedgerEntity?,
        p2: Int
    ) {
        val selectorModel=item!!.content as SelectorModel
        val binding=getBinding<ItemResultBinding>(helper.itemView.rootView)!!
        binding.tvTitle.text=selectorModel.name
        if (selectorModel.selectValues!!.isNotEmpty()){
            binding.tvResult.text= selectorModel.selectValues!![0]
        }else{
            binding.tvResult.text= ""
        }

    }

    private fun <DB: ViewDataBinding>getBinding(view: View):DB?{
        return DataBindingUtil.bind<DB>(view)
    }
}