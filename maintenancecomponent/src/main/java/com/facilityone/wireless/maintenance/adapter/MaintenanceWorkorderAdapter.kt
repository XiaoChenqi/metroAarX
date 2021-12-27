package com.facilityone.wireless.maintenance.adapter

import com.blankj.utilcode.util.TimeUtils
import com.facilityone.wireless.maintenance.model.MaintenanceService.MaintenanceWorkOrder
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.facilityone.wireless.basiclib.utils.DateUtils
import com.facilityone.wireless.basiclib.utils.StringUtils
import com.facilityone.wireless.maintenance.R
import com.facilityone.wireless.maintenance.model.MaintenanceConstant
import com.facilityone.wireless.maintenance.model.MaintenanceHelper
import java.util.*

/**
 * Created by peter.peng on 2018/11/21.
 * 计划性维护详情维护工单适配器
 */
class MaintenanceWorkorderAdapter(data: List<MaintenanceWorkOrder?>?) :
    BaseQuickAdapter<MaintenanceWorkOrder?, BaseViewHolder>(
        R.layout.adapter_maintenance_workorder_item,
        data
    ) {
    override fun convert(helper: BaseViewHolder, item: MaintenanceWorkOrder?) {
        if (item != null) {
            helper.setText(R.id.maintenance_workorder_code_tv, StringUtils.formatString(item.code))
            if (item.createDateTime != null) {
                helper.setText(
                    R.id.maintenance_workorder_create_time_tv,
                    TimeUtils.date2String(Date(item.createDateTime), DateUtils.SIMPLE_DATE_FORMAT)
                )
            } else {
                helper.setGone(R.id.maintenance_workorder_create_time_tv, false)
                helper.setText(R.id.maintenance_workorder_create_time_tv, "")
            }
            helper.setText(
                R.id.maintenance_workorder_applicant_name_tv,
                StringUtils.formatString(item.applicantName)
            )
            helper.setText(
                R.id.maintenance_workorder_position_tv,
                StringUtils.formatString(item.location)
            )
            if (item.newStatus != null) {
                helper.setVisible(R.id.maintenance_workorder_status_tv, true)
                helper.setText(
                    R.id.maintenance_workorder_status_tv,
                    MaintenanceHelper.getMaintenanceWorkorderStatusMap(mContext)[item.newStatus]
                )
                var resId = R.drawable.maintenance_workorder_tag_fill_created_bg
                when(item.newStatus){
                    MaintenanceConstant.WORK_NEW_STATUS_DISPATCHING -> resId = //待派工
                        R.drawable.fm_workorder_tag_fill_created_bg
                    MaintenanceConstant.WORK_NEW_STATUS_PROCESS -> resId =     //处理中
                        R.drawable.fm_workorder_tag_fill_process_bg
                    MaintenanceConstant.WORK_NEW_STATUS_ARCHIVED_WAIT -> resId =  //待存档
                        R.drawable.fm_workorder_tag_fill_published_bg
                    MaintenanceConstant.WORK_NEW_STATUS_APPROVAL_WAIT -> resId =  //待审核
                        R.drawable.fm_workorder_tag_fill_approval_bg
                    MaintenanceConstant.WORK_NEW_STATUS_ARCHIVED -> resId =   //已存档
                        R.drawable.fm_workorder_tag_fill_archived_bg
                    MaintenanceConstant.WORK_NEW_STATUS_DESTORY -> resId =     //已作废
                        R.drawable.fm_workorder_tag_fill_suspended_go_bg
                }
                helper.setBackgroundRes(R.id.maintenance_workorder_status_tv, resId)
            } else {
                helper.setGone(R.id.maintenance_workorder_status_tv, false)
            }

            if (item.tag != null) {
                helper.setVisible(R.id.maintenance_workorder_tag_tv, true)
                helper.setText(
                    R.id.maintenance_workorder_tag_tv,
                    MaintenanceHelper.getMaintenanceTagStatusMap(mContext)[item.tag]
                )
                var resId = R.drawable.maintenance_workorder_tag_fill_created_bg
                when(item.tag){
                    MaintenanceConstant.APPLICATION_FOR_SUSPENSION -> resId = //暂停申请中
                        R.drawable.fm_workorder_tag_fill_created_bg
                    MaintenanceConstant.PAUSE_STILL_WORKING -> resId =     //暂停(继续工作)
                        R.drawable.fm_workorder_tag_fill_created_bg
                    MaintenanceConstant.PAUSE_NOT_WORKING -> resId =  //暂停(不继续工作)
                        R.drawable.fm_workorder_tag_fill_process_bg
                    MaintenanceConstant.APPLICATION_VOID -> resId =  //作废申请中
                        R.drawable.fm_workorder_tag_fill_process_bg
                    MaintenanceConstant.STOP -> resId =  //终止
                        R.drawable.fm_workorder_tag_fill_suspended_go_bg
                }
                helper.setBackgroundRes(R.id.maintenance_workorder_tag_tv, resId)
            } else {
                helper.setGone(R.id.maintenance_workorder_tag_tv, false)
            }

            helper.setGone(
                R.id.maintenance_workorder_item_dash_line,
                helper.layoutPosition != data.size - 1
            )
            helper.setGone(
                R.id.maintenance_workorder_item_secant_line,
                helper.layoutPosition == data.size - 1
            )
        }
    }
}