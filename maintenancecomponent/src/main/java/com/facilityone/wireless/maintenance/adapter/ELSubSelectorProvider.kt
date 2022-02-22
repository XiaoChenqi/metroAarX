package com.facilityone.wireless.maintenance.adapter


import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.provider.BaseItemProvider

import com.facilityone.wireless.maintenance.R
import com.facilityone.wireless.maintenance.databinding.ItemElSelectorBinding
import com.facilityone.wireless.maintenance.databinding.ItemElSubSelectorBinding
import com.facilityone.wireless.maintenance.model.MaintenanceEnity
import com.facilityone.wireless.maintenance.model.SelectorModel


class ELSubSelectorProvider : BaseItemProvider<MaintenanceEnity.ElectronicLedgerEntity,BaseViewHolder>() {



    override fun viewType(): Int {
        return ElectronicLedgerAdapter.TYPE_RADIO_SUB
    }

    override fun layout(): Int {
        return R.layout.item_el_sub_selector
    }

    override fun convert(
        helper: BaseViewHolder,
        item: MaintenanceEnity.ElectronicLedgerEntity?,
        position: Int
    ) {
        val selectorModel = item!!.content as SelectorModel
        val binding = getBinding<ItemElSubSelectorBinding>(helper.itemView.rootView)!!
        binding.taskTitleTv.text = selectorModel.name
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
        binding.rgSelect.setOnCheckedChangeListener { group, checkedId ->
            kotlin.run {

                val id = binding.rgSelect.checkedRadioButtonId
                when (id) {
                    R.id.rbNormal -> {
                        item.value = "0"
                        selectorModel.state=0
                    }
                    R.id.rbException -> {
                        item.value = "1"
                        selectorModel.state=1
                    }
                }

            }
        }




            binding.taskSubTitleTv.text = selectorModel.sub!!.name
            if (3==selectorModel.sub!!.state){
                binding.rgSelect2.clearCheck()
            }else{
                when(selectorModel.sub!!.state){
                    0->{
                        binding.rgSelect2.check(R.id.rbNormal2)
                    }
                    1->{
                        binding.rgSelect2.check(R.id.rbException2)
                    }
                }

            }
            binding.rgSelect2.setOnCheckedChangeListener { group, checkedId ->
                kotlin.run {

                    var id = binding.rgSelect2.checkedRadioButtonId
                    when (id) {
                        R.id.rbNormal2 ->{
                            item.subValue = "0"
                            selectorModel.sub!!.state=0
                        }
                        R.id.rbException2 -> {
                            item.subValue = "1"
                            selectorModel.sub!!.state=1
                        }
                    }
                }
            }



    }

    fun <DB: ViewDataBinding>getBinding(view: View):DB?{
        return DataBindingUtil.bind(view)
    }


}