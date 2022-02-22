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

