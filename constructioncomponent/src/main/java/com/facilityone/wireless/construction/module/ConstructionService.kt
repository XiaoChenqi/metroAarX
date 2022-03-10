package com.facilityone.wireless.construction.module

import com.facilityone.wireless.a.arch.ec.module.Page
import java.util.*

/**
 *  @Author: Karelie
 *  @Method：BoardingService
 *  @Date：2022/2/14 17:35
 *  @Description：登乘巡检实体
 */
class ConstructionService {

    class ConstructionRegReq{
        var guardian:String? = null //监护人员
        var guardianStartTime:Long? = null //监护开始时间
        var guardianEndTime:Long? = null //监护结束时间
        var guardianName:String? = null //施工项目名称
        var constructionDate:Long? = null //施工工期
        var constructionSite:String? = null //施工站点
        var constructionUnit:String? = null //施工单位
        var constructionPrincipal:String? = null //现场施工负责人
        var principal:String? = null //现场负责人
        var notePerson:String? = null //备注
        var status:Int? = null //状态0 : 未完成 1:完成
        var resultValue:String? = null //检查结果
    }

     class ConstructionQueryEnity{
         var page: Page? = null
         val contents:MutableList<ConstructionQueryContents>?= null
    }

    class ConstructionQueryReq{
        var page:Page? = null
    }

    class ConstructionDetailReq{
        var templateId:Long? = null //施工监护记录Id
    }

    class ConstructionQueryContents{
        var id:Long? = null //记录Id
        var guardianName:String? = null //项目名称
        var constructionDate:Long? = null //施工日期
        var constructionSite:String? = null //施工站点
        var constructionUnit:String? = null //施工单位
    }

    class ConstructionInforEnity{
        var templateId:Long? = null //模板ID
        val registDate:Long? = null//提交日期 yy-mm-dd hh-mm
        var tasks:List<TemplateTask>? = null //任务List
        var construction:Construction? = null //施工监护
    }

    data class Construction(
        val guardian:String,//监护人员
        val guardianStartTime:Long?,//监护开始时间
        val guardianEndTime:Long?,//监护结束时间
        val guardianName:String,//施工项目名称
        val constructionDate:Long?,//施工日期
        val constructionSite:String,//施工站点
        val constructionUnit:String,//施工单位
        val constructionPrincipal:String,//现场施工负责人
        val principal:String,//现场负责人
        val notePerson:String,//备注
        val status:Int//状态 0:未完成 1:完成
    )

    data class ElectronicLedgerEntity(
        var type: Int?=0,
        var parent :Int?= 0,
        var content: Any? = null,
        var value: String? = null,
        var subValue: String? = null,
        var contentId: Long? = null,
        var taskId: Long? = null
    ) {
        constructor(type: Int, content: Any?, parent: Int):this(
            type,parent,content,null,null,null,null)

        constructor(type: Int, content: Any?):this(
           type,content,-1)
        constructor(taskId: Long?, contentId: Long?, type: Int, content: Any?):this(
            type, null,content,null,null,contentId,taskId
        )

        constructor(type: Int):this(type,null,null,null,null,null,null)


        companion object {
            const val TYPE_HEADER = 1
            const val TYPE_SUB_HEADER = 5
            const val TYPE_RADIO = 2
            const val TYPE_EDIT = 3
            const val TYPE_RADIO_SUB = 4
            const val TYPE_RESULT = 6
        }
    }

}