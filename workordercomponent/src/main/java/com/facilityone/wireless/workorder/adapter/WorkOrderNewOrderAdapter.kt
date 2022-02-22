package com.facilityone.wireless.workorder.adapter

import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.facilityone.wireless.a.arch.utils.NumberToChineseUtil
import com.facilityone.wireless.a.arch.widget.CustomContentItemView
import com.facilityone.wireless.workorder.R
import com.facilityone.wireless.workorder.module.WorkorderService
import com.facilityone.wireless.workorder.module.WorkorderService.WorkOrderEquipmentsBean
import com.github.mikephil.charting.formatter.IFillFormatter

class WorkOrderNewOrderAdapter(data: ArrayList<WorkorderService.newOrderEnity>,private val canAdd:Boolean?=true) :
    BaseQuickAdapter<WorkorderService.newOrderEnity, BaseViewHolder>(
        R.layout.workordernewitem
    ) {

    override fun convert(help: BaseViewHolder, item: WorkorderService.newOrderEnity?) {
        var dataSize: Int? = data.size
//        help.setText(R.id.item_title, "工单" + NumberToChineseUtil.intToChinese(help.adapterPosition+1))
        help.addOnClickListener(R.id.iv_add_menu)
        help.addOnClickListener(R.id.tv_neworder_delete)
        help.addOnClickListener(R.id.civ_workorder_type)
        help.addOnClickListener(R.id.civ_service_type)
        help.addOnClickListener(R.id.civ_priority)
        if (help.adapterPosition == (dataSize!! - 1)) {
            if (canAdd == true){
                help.setGone(R.id.ll_add_menu, false);
            }else{
                help.setGone(R.id.ll_add_menu, true);
            }

        } else {
            help.setGone(R.id.ll_add_menu, false);
        }

        if (item!!.typeName != null){
            val typeView: CustomContentItemView = help.getView(R.id.civ_workorder_type)
            typeView.tipText = item.typeName
        }

        if (item!!.serviceName != null){
            val serviceView: CustomContentItemView = help.getView(R.id.civ_service_type)
            serviceView.tipText = item.serviceName
        }

        if (item!!.priorityName != null){
            val priorityView: CustomContentItemView = help.getView(R.id.civ_priority)
            priorityView.tipText = item.priorityName
        }


    }


}