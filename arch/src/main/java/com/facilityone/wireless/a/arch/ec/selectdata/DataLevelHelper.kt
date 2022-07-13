package com.facilityone.wireless.a.arch.ec.selectdata

import com.facilityone.wireless.a.arch.ec.module.SelectDataBean


//是否存在子元素
fun List<SelectDataBean>.hasSonElement(parentId:Long)= this.any { it.parentId==parentId }


fun List<SelectDataBean>.getParentData(sonId:Long):SelectDataBean{
    return this.single {
        it.id == sonId
    }.let{ son->
        this.single {
            it.id==son.parentId
        }
    }



}
