package com.facilityone.wireless.maintenance.adapter


import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.provider.BaseItemProvider

import com.facilityone.wireless.maintenance.R
import com.facilityone.wireless.maintenance.databinding.ItemElSelectorBinding
import com.facilityone.wireless.maintenance.model.MaintenanceEnity
import com.facilityone.wireless.maintenance.model.SelectorModel


class ELSelectorProvider : BaseItemProvider<MaintenanceEnity.ElectronicLedgerEntity,BaseViewHolder>() {



    override fun viewType(): Int {
        return ElectronicLedgerAdapter.TYPE_RADIO
    }

    override fun layout(): Int {
        return R.layout.item_el_selector
    }

    override fun convert(
        helper: BaseViewHolder,
        item: MaintenanceEnity.ElectronicLedgerEntity?,
        position: Int
    ) {

        val selectorModel=item!!.content as SelectorModel
        val binding=getBinding<ItemElSelectorBinding>(helper.itemView.rootView)!!
        binding.setVariable(position,item)
        binding.taskTitleTv.text=selectorModel.name
        if (selectorModel.value==1){
            binding.rbNormal.text="携带"
            binding.rbException.text="未携带"
        }

        if (3==selectorModel.state){
            binding.rgSelect.clearCheck()
        }else{
            when(selectorModel.state){
                0->{
                    binding.rgSelect.check(R.id.rbNormal)
                }
                1->{
                    binding.rgSelect.check(R.id.rbException)
                }
            }

        }


        binding.rgSelect.setOnCheckedChangeListener { group, checkedId ->kotlin.run {

            val id=binding.rgSelect.checkedRadioButtonId
            when(id){
                   R.id.rbNormal-> {
                       item.value="0"
                       selectorModel.state=0
                   }
                   R.id.rbException->{
                       item.value="1"
                       selectorModel.state=1
                   }

            }


        }
        }

    }

    fun <DB: ViewDataBinding>getBinding(view: View):DB?{
        return DataBindingUtil.bind(view)
    }


}