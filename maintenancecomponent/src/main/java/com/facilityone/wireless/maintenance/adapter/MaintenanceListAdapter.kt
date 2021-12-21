package com.facilityone.wireless.maintenance.adapter

import com.blankj.utilcode.util.TimeUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.facilityone.wireless.basiclib.utils.DateUtils
import com.facilityone.wireless.maintenance.R
import com.facilityone.wireless.maintenance.model.MaintenanceConstant
import com.facilityone.wireless.maintenance.model.MaintenanceEnity.MaintenanceListEnity
import com.facilityone.wireless.maintenance.model.MaintenanceHelper
import java.util.*

class MaintenanceListAdapter : BaseQuickAdapter<MaintenanceListEnity?, BaseViewHolder> {
    var type = 0

    constructor(data: List<MaintenanceListEnity?>?) : super(R.layout.adpater_maintenance_list_item, data) {}
    constructor(type: Int) : super(R.layout.adpater_maintenance_list_item) {
        this.type = type
    }

    override fun convert(helper: BaseViewHolder, item: MaintenanceListEnity?) {
        if (item != null) {
            helper.setText(R.id.code_tv, item.code + "")
            val date2String = TimeUtils.date2String(Date(item.createDateTime), DateUtils.SIMPLE_DATE_FORMAT_ALL)
            helper.setText(R.id.date_tv, date2String + "")
            helper.setText(R.id.describe_tv, item.woDescription + "")
            if (item.location != null) {
                helper.setVisible(R.id.location_tv, true)
                helper.setText(R.id.location_tv, item.location + "")
            }else{
                helper.setVisible(R.id.location_tv, false)
            }
            when (item.choice) {
                MaintenanceConstant.CHOICE_NO -> helper.setGone(R.id.ll_chekbox, false)
                MaintenanceConstant.CHOICE_All -> {
                    helper.setVisible(R.id.ll_chekbox, true)
                    helper.setBackgroundRes(R.id.im_checkbox, R.drawable.btn_check_off)
                }
                MaintenanceConstant.CHOICE_UP -> {
                    helper.setVisible(R.id.ll_chekbox, true)
                    helper.setBackgroundRes(R.id.im_checkbox, R.drawable.btn_check_on)
                }
                MaintenanceConstant.CHOICE_DOWN -> {
                    helper.setVisible(R.id.ll_chekbox, true)
                    helper.setBackgroundRes(R.id.im_checkbox, R.drawable.btn_check_off)
                }
                MaintenanceConstant.CHOICE_OFF -> {
                    helper.setVisible(R.id.ll_chekbox, true)
                    helper.setBackgroundRes(R.id.im_checkbox, R.drawable.fm_workorder_tag_fill_archived_bg)
                }
            }
            val layoutPosition = helper.layoutPosition
            if (type == MaintenanceConstant.MAINTENANCE_SEVEN && layoutPosition == data.size - 1) {
                helper.setGone(R.id.bottom_line_view, true)
                helper.setGone(R.id.center_line_view, false)
            } else {
                helper.setGone(R.id.center_line_view, true)
            }


            if (item.newStatus != null) {
                helper.setVisible(R.id.status_tv, true)
                var resId = R.drawable.maintenacne_fill_grey_background
                helper.setText(R.id.status_tv, MaintenanceHelper.getMaintenanceWorkorderStatusMap(mContext)[item.newStatus])
                when (item.newStatus) {
//                    MaintenanceConstant.WORKORDER_STATUS_CREATED -> resId =
//                            R.drawable.fm_workorder_tag_fill_created_bg
//                    MaintenanceConstant.WORKORDER_STATUS_PUBLISHED -> resId =
//                            R.drawable.fm_workorder_tag_fill_published_bg
//                    MaintenanceConstant.WORKORDER_STATUS_PROCESS -> resId =
//                            R.drawable.fm_workorder_tag_fill_process_bg
//                    MaintenanceConstant.WORKORDER_STATUS_SUSPENDED_GO -> resId =
//                            R.drawable.fm_workorder_tag_fill_suspended_go_bg
//                    MaintenanceConstant.WORKORDER_STATUS_TERMINATED -> resId =
//                            R.drawable.fm_workorder_tag_fill_terminated_bg
//                    MaintenanceConstant.WORKORDER_STATUS_COMPLETED -> resId =
//                            R.drawable.fm_workorder_tag_fill_completed_bg
//                    MaintenanceConstant.WORKORDER_STATUS_VERIFIED -> resId =
//                            R.drawable.fm_workorder_tag_fill_verified_bg
//                    MaintenanceConstant.WORKORDER_STATUS_ARCHIVED -> resId =
//                            R.drawable.fm_workorder_tag_fill_archived_bg
//                    MaintenanceConstant.WORKORDER_STATUS_APPROVAL -> resId =
//                            R.drawable.fm_workorder_tag_fill_approval_bg
//                    MaintenanceConstant.WORKORDER_STATUS_SUSPENDED_NO -> resId =
//                            R.drawable.fm_workorder_tag_fill_suspended_no_bg

                    MaintenanceConstant.WORK_NEW_STATUS_DISPATCHING -> resId = //待派工
                        R.drawable.fm_workorder_tag_fill_created_bg
                    MaintenanceConstant.WORK_NEW_STATUS_PROCESS -> resId =     //处理中
                        R.drawable.fm_workorder_tag_fill_process_bg
                    MaintenanceConstant.WORK_NEW_STATUS_ARCHIVED_WAIT -> resId =  //待存档
                        R.drawable.fm_workorder_tag_fill_published_bg
                    MaintenanceConstant.WORK_NEW_STATUS_APPROVAL_WAIT -> resId =  //待审核
                        R.drawable.fm_workorder_tag_fill_approval_bg
//                        R.drawable.fm_workorder_tag_fill_archived_bg
                    MaintenanceConstant.WORK_NEW_STATUS_ARCHIVED -> resId =   //已存档
                        R.drawable.fm_workorder_tag_fill_archived_bg
//                        R.drawable.fm_workorder_tag_fill_approval_bg
                    MaintenanceConstant.WORK_NEW_STATUS_DESTORY -> resId =     //已作废
                        R.drawable.fm_workorder_tag_fill_suspended_go_bg
                }
                helper.setBackgroundRes(R.id.status_tv, resId)
            } else {
                helper.setVisible(R.id.status_tv, false)
            }

            if (item.tag != null) {
                helper.setVisible(R.id.tag_tv, true)
                var resId = R.drawable.maintenacne_fill_grey_background
                helper.setText(R.id.tag_tv, MaintenanceHelper.getMaintenanceTagStatusMap(mContext)[item.tag])
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
                helper.setBackgroundRes(R.id.tag_tv, resId)
            } else {
                helper.setVisible(R.id.tag_tv, false)
            }




        }
    }
}