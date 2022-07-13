@file:JvmName("WorkOrderStepUtils")

package com.facilityone.wireless.workorder.fragment

import com.facilityone.wireless.workorder.module.WorkorderService
import com.facilityone.wireless.workorder.module.WorkorderService.StepsBean
import java.util.*

fun List<StepsBean>.genStepStatusByWorkTeamId(woTeamIds: List<WorkorderService.WorkTeamEntity>): BooleanArray {
    return this.map{ stepsBean->
        if (woTeamIds.isEmpty()||stepsBean.workTeamId==null){
            false
        }else{
            woTeamIds.any {  it.wtId == stepsBean.workTeamId }
        }

    }.toBooleanArray()
}