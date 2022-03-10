package com.facilityone.wireless.componentservice.common.permissions

object PermissionsHelper {

    @JvmStatic
    fun strList2strArray(strList:List<String>): Array<String> {
        return strList.toTypedArray()
    }
}