package com.facilityone.wireless.workorder.adapter

import com.blankj.utilcode.util.TimeUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.facilityone.wireless.basiclib.utils.DateUtils
import com.facilityone.wireless.workorder.R
import com.facilityone.wireless.workorder.module.WorkorderService
import java.util.*

class WorkOrderSpaceAdapter:
    BaseQuickAdapter<WorkorderService.PmSpaceBean,BaseViewHolder>
    (R.layout.adapter_workorder_newspace_item) {

    override fun convert(hd: BaseViewHolder, item: WorkorderService.PmSpaceBean?) {
        hd.addOnClickListener(R.id.ll_contentitem)
        if (item != null){
            if (item.name != null){
                hd.setText(R.id.tv_newspcae_location,item.name+"")
            }

            if (item.updateDate != null){
                val date: Date = Date(item.updateDate)
                hd.setText(R.id.tv_newspcae_time, TimeUtils.date2String(date, DateUtils.SIMPLE_DATE_FORMAT_SECOND))
            }

            if (item.emName != null){
                hd.setText(R.id.tv_newspcae_person,item.emName+"")
            }
        }

    }
}