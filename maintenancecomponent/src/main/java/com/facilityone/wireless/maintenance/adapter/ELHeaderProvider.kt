package com.facilityone.wireless.maintenance.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.provider.BaseItemProvider
import com.facilityone.wireless.maintenance.R
import com.facilityone.wireless.maintenance.databinding.ItemElHeaderBinding
import com.facilityone.wireless.maintenance.model.MaintenanceEnity


class ELHeaderProvider : BaseItemProvider<MaintenanceEnity.ElectronicLedgerEntity,BaseViewHolder>() {

    override fun viewType(): Int {
        return ElectronicLedgerAdapter.TYPE_HEADER
    }

    override fun layout(): Int {
        return R.layout.item_el_header
    }

    override fun convert(
        helper: BaseViewHolder,
        item: MaintenanceEnity.ElectronicLedgerEntity?,
        position: Int
    ) {
        val headerText=item!!.content as String
        val binding=getBinding<ItemElHeaderBinding>(helper.itemView)!!
        binding.taskTitleTv.text=headerText
    }


    private fun <DB:ViewDataBinding>getBinding(view:View):DB?{
       return DataBindingUtil.bind<DB>(view)
    }
}