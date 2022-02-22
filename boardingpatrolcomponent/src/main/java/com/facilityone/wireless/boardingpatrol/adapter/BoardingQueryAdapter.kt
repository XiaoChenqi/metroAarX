package com.facilityone.wireless.boardingpatrol.adapter

import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.facilityone.wireless.boardingpatrol.R
import com.facilityone.wireless.boardingpatrol.databinding.AdapterBoardingqueryBinding
import com.facilityone.wireless.boardingpatrol.databinding.LayoutBoardingcreateBinding
import com.facilityone.wireless.boardingpatrol.moudle.BoardingService

class BoardingQueryAdapter() : BaseQuickAdapter<BoardingService.BoardingQueryContents, BaseViewHolder>(
    R.layout.adapter_boardingquery) {
    override fun convert(helper: BaseViewHolder, item: BoardingService.BoardingQueryContents?) {
        val binding=getBinding<AdapterBoardingqueryBinding>(helper.itemView.rootView)!!
        binding.data=item
        binding.executePendingBindings()
    }

    fun <DB: ViewDataBinding>getBinding(view: View):DB?{
        return DataBindingUtil.bind(view)
    }

}