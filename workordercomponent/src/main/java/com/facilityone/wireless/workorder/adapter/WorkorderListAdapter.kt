package com.facilityone.wireless.workorder.adapter

import android.text.TextUtils
import com.blankj.utilcode.util.TimeUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.facilityone.wireless.basiclib.utils.DateUtils
import com.facilityone.wireless.basiclib.utils.StringUtils
import com.facilityone.wireless.workorder.R
import com.facilityone.wireless.workorder.module.WorkorderConstant
import com.facilityone.wireless.workorder.module.WorkorderHelper
import com.facilityone.wireless.workorder.module.WorkorderService.WorkorderItemBean
import java.util.*

/**
 * Author：gary
 * Email: xuhaozv@163.com
 * description:
 * Date: 2018/7/4 下午4:00
 */
class WorkorderListAdapter(private val type: Int) :
    BaseQuickAdapter<WorkorderItemBean, BaseViewHolder>(R.layout.fragment_workorder_list_item) {
    private var mPriority: Map<Long, String>? = null
    override fun convert(helper: BaseViewHolder, item: WorkorderItemBean) {
        if(type!= null){
            helper.setGone(R.id.ll_quick_show, type != WorkorderConstant.WORKORER_QUERY)
            helper.setGone(R.id.bottom_line_view, type != WorkorderConstant.WORKORER_QUERY)
            helper.setGone(R.id.view_placeholder, type != WorkorderConstant.WORKORER_QUERY)
        }

        helper.setGone(R.id.ll_desc, !TextUtils.isEmpty(item.woDescription))
        if (item.location != null){
            helper.setVisible(R.id.location_tv_order,true)
            helper.setText(R.id.location_tv_order, item.location + "")
        }

        val layoutPosition = helper.layoutPosition
        if (type == WorkorderConstant.WORKORER_QUERY && layoutPosition == data.size - 1) {
            helper.setGone(R.id.bottom_line_view, true)
            helper.setGone(R.id.center_line_view, false)
        } else {
            helper.setGone(R.id.center_line_view, true)
        }
        helper.setText(R.id.code_tv, StringUtils.formatString(item.code))
        helper.setText(R.id.describe_tv, StringUtils.formatString(item.woDescription))
        if (item.createDateTime != null) {
            val date2String =
                TimeUtils.date2String(Date(item.createDateTime), DateUtils.SIMPLE_DATE_FORMAT_ALL)
            helper.setText(R.id.date_tv, date2String)
        }
        if (TextUtils.isEmpty(item.priorityName)) {
            if (mPriority != null) {
                item.priority = mPriority!![item.priorityId]
            }
        } else {
            item.priority = item.priorityName
        }
        if (item.priority != null) {
            helper.setText(R.id.priority_tv, item.priority)
            helper.setVisible(R.id.priority_tv, true)
        } else {
            helper.setText(R.id.priority_tv, "")
            helper.setVisible(R.id.priority_tv, false)
        }
//        helper.setBackgroundRes(
//            R.id.demand_new_status_tv,
//            R.drawable.fm_workorder_tag_fill_created_bg
//        )
        if (item.tag!=null){
            when(item.tag){
                0->{
                    helper.setGone(R.id.demand_new_status_tv,true)
                    helper.setText(R.id.demand_new_status_tv,"暂停申请中")
                    helper.setBackgroundRes(R.id.demand_new_status_tv, R.drawable.fm_workorder_tag_fill_created_bg)
                }
                1->{
                    helper.setGone(R.id.demand_new_status_tv,true)
                    helper.setText(R.id.demand_new_status_tv,"暂停(继续工作)")
                    helper.setBackgroundRes(R.id.demand_new_status_tv, R.drawable.fm_workorder_tag_fill_created_bg)
                }
                2-> {
                    helper.setGone(R.id.demand_new_status_tv,true)
                    helper.setText(R.id.demand_new_status_tv,"暂停(不继续工作)")
                    helper.setBackgroundRes(R.id.demand_new_status_tv, R.drawable.fm_workorder_tag_fill_created_bg)
                }
                3->{
                    helper.setGone(R.id.demand_new_status_tv,true)
                    helper.setText(R.id.demand_new_status_tv,"作废申请中")
                    helper.setBackgroundRes(R.id.demand_new_status_tv, R.drawable.fm_workorder_tag_fill_created_bg)
                }
                4->{
                    helper.setGone(R.id.demand_new_status_tv,true)
                    helper.setText(R.id.demand_new_status_tv,"已终止")
                    helper.setBackgroundRes(R.id.demand_new_status_tv, R.drawable.fm_workorder_tag_fill_created_bg)
                }
                else ->{
                    helper.setGone(R.id.demand_new_status_tv,false)
                }

            }
        }else{
            helper.setGone(R.id.demand_new_status_tv, false)

        }



        if (item.newStatus != null) {
            helper.setVisible(R.id.status_tv, true)
            var resId = R.drawable.workorder_fill_grey_background
            helper.setText(R.id.status_tv, WorkorderHelper.getWorkNewStatusMap(mContext)[item.newStatus])
            when (item.newStatus) {
                WorkorderConstant.WORK_STATUS_CREATED -> resId =
                    R.drawable.fm_workorder_tag_fill_created_bg
                WorkorderConstant.WORK_STATUS_PUBLISHED -> resId =
                    R.drawable.fm_workorder_tag_fill_published_bg
                WorkorderConstant.WORK_STATUS_PROCESS -> resId =
                    R.drawable.fm_workorder_tag_fill_process_bg
                WorkorderConstant.WORK_STATUS_SUSPENDED_GO -> resId =
                    R.drawable.fm_workorder_tag_fill_suspended_go_bg
                WorkorderConstant.WORK_STATUS_TERMINATED -> resId =
                    R.drawable.fm_workorder_tag_fill_terminated_bg
                WorkorderConstant.WORK_STATUS_COMPLETED -> resId =
                    R.drawable.fm_workorder_tag_fill_completed_bg
                WorkorderConstant.WORK_STATUS_VERIFIED -> resId =
                    R.drawable.fm_workorder_tag_fill_verified_bg
                WorkorderConstant.WORK_STATUS_ARCHIVED -> resId =
                    R.drawable.fm_workorder_tag_fill_archived_bg
                WorkorderConstant.WORK_STATUS_APPROVAL -> resId =
                    R.drawable.fm_workorder_tag_fill_approval_bg
                WorkorderConstant.WORK_STATUS_SUSPENDED_NO -> resId =
                    R.drawable.fm_workorder_tag_fill_suspended_no_bg
            }
            helper.setBackgroundRes(R.id.status_tv, resId)
        } else {
            helper.setVisible(R.id.status_tv, false)
        }
    }

    fun setPriority(priority: Map<Long, String>?) {
        mPriority = priority
        notifyDataSetChanged()
    }
}