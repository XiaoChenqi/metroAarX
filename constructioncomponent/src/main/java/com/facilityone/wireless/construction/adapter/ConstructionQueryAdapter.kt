package com.facilityone.wireless.construction.adapter

import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.facilityone.wireless.construction.R
import com.facilityone.wireless.construction.databinding.AdapterCqueryBinding
import com.facilityone.wireless.construction.module.ConstructionService

class ConstructionQueryAdapter():BaseQuickAdapter<ConstructionService.ConstructionQueryContents,BaseViewHolder>(
    R.layout.adapter_cquery
){
    override fun convert(helper: BaseViewHolder, item: ConstructionService.ConstructionQueryContents?) {
        val binding=getBinding<AdapterCqueryBinding>(helper.itemView.rootView)!!
        binding.data=item
        binding.executePendingBindings()
    }

    fun <DB: ViewDataBinding>getBinding(view: View):DB?{
        return DataBindingUtil.bind(view)
    }
}