@file:JvmName("WorkOrderUtils")

package com.facilityone.wireless.workorder.fragment

import com.facilityone.wireless.workorder.module.WorkorderService

fun List<WorkorderService.SampleTemplate>.getValueList(): List<String> {
    return this.map {

        if (it.samplePass){
            it.sampleName+"(已抽检)"
        }else{
            it.sampleName
        }
    }

}

fun List<WorkorderService.SampleTemplate>.getKey(obj: String): Long? {
    var objKey: Long?=null

//    for ((k, v) in this) {
//        if (v == obj) objKey = k.toLong();break
//    }
//
    this.forEach outSide@{
        if (it.sampleName==obj){
            objKey=it.sampleId
            return@outSide
        }
    }
    return objKey

}

/**
 * @Author kuuga.wu
 * @Date 2022/6/22
 * @Desc 派工页面来源
 */
enum class WorkOrderDispatchSource( val typeName:String){
    MAINTENNANCE("维护"),
    WORKORDER_REPAIR("维修");
}

fun checkType(source: Boolean): WorkOrderDispatchSource {
    return if (source) {
        WorkOrderDispatchSource.MAINTENNANCE
    } else {
        WorkOrderDispatchSource.WORKORDER_REPAIR
    }
}


