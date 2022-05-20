package com.facilityone.wireless.a.arch.ec.selectdata

import com.facilityone.wireless.a.arch.ec.module.ISelectDataService
import com.facilityone.wireless.a.arch.ec.module.SelectDataBean

/**
 *  @Author: Karelie
 *  @Method：SelectDataHelper
 *  @Date：2022/3/10 12:03
 *  @Description：选择数据从接口接收的数据的工具类
 */
object SelectDataHelper {
    /**
     * 过滤列表数据
     * */
    @JvmStatic
    fun sellectList(listBefore: MutableList<SelectDataBean>, key:Long):MutableList<SelectDataBean>?{
        if (key == -1L){
            val list = listBefore.filter {
                it.parentId == null
            }.map { it }
            return list.toMutableList()
        }else{
            val list = listBefore.filter {
               it.parentId == key
            }.map { it }
            return list.toMutableList()
        }
    }

    /**
     * 获取上层父级ID
     * */
    @JvmStatic
    fun backParentId(listBefore: MutableList<SelectDataBean>, parentId:Long):Long?{
        for (selectDataBean in listBefore) {
            if (selectDataBean.id == parentId){
                return selectDataBean.parentId
            }
        }
        return null;
    }

    /**
     * 获取当前最顶上的父级ID
     * */
    @JvmStatic
    fun getParentId(listBefore: MutableList<SelectDataBean>, id:Long): Long? {
        val parent = listBefore.filter { it.id == id }
        var parentId:Long = -1L
        if (parent != null ){
            return parent[0].parentId

        }
        return parentId
    }
    /**
     * 判断当前选中的父级ID获取与父级同层级数据
     * */
    @JvmStatic
    fun getLastList(listBefore: MutableList<SelectDataBean>, parentId:Long): MutableList<SelectDataBean>? {
        return listBefore.filter { it.parentId == parentId }.map { it }.toMutableList()
    }

    /**
     * 根据类型判断是否显示子标题
     * */
    @JvmStatic
    fun showSubTitleByType(type:Int, parentId:Long?): Boolean {
        return when(type){
            ISelectDataService.DATA_TYPE_EQU, ISelectDataService.DATA_TYPE_EQU_ALL->true
            ISelectDataService.DATA_TYPE_FAULT_OBJECT,
            ISelectDataService.DATA_TYPE_REASON,
            ISelectDataService.DATA_TYPE_INVALIDD -> parentId!=null
            else -> {
                false
            }
        }
    }

}