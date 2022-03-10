package com.facilityone.wireless.construction.adapter

import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.provider.BaseItemProvider
import com.facilityone.wireless.construction.R
import com.facilityone.wireless.construction.databinding.ItemClHeaderBinding
import com.facilityone.wireless.construction.module.ConstructionService



class ELHeaderProvider : BaseItemProvider<ConstructionService.ElectronicLedgerEntity,BaseViewHolder>() {

    override fun viewType(): Int {
        return ElectronicLedgerAdapter.TYPE_HEADER
    }

    override fun layout(): Int {
        return R.layout.item_cl_header
    }

    override fun convert(
        helper: BaseViewHolder,
        item: ConstructionService.ElectronicLedgerEntity?,
        position: Int
    ) {
        val headerText=item!!.content as String
        val binding=getBinding<ItemClHeaderBinding>(helper.itemView.rootView)!!
        binding.taskTitleTv.text=headerText
    }


    private fun <DB:ViewDataBinding>getBinding(view:View):DB?{
       return DataBindingUtil.bind<DB>(view)
    }
}