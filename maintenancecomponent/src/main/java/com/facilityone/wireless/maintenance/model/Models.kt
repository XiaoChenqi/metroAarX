package com.facilityone.wireless.maintenance.model

import android.os.Parcelable

data class SelectorModel(
    var name:String?=null,
    var value:Int?=null,
    var state:Int?=null,
    var sub: SelectorModel?,
    var pid:Long?=null,
    var tips:String?=null,
    var selectValues: List<String>?,
    var selects: List<SelectValueStatus>?

){
    constructor(name: String?,value: Int?,pid:Long?=null):this(name,value,3,null,pid,null,null,null)
    constructor(name: String, value: Int, sub: SelectorModel,pid: Long) : this(name,value,3,sub,pid,null,null,null)

    constructor(name: String?,value: Int?,pid:Long?=null,tip:String):this(name,value,3,null,pid,tip,null,null)
    constructor(name: String?,value: Int?,selectValues: List<String>?,selects: List<SelectValueStatus>?):this(name,value,3,null,null,null,selectValues,selects)
}


class ElectronicLedgerEntity{

    public var type:Int?=null
    public var content:Any?=null

    constructor(type: Int?, content: Any?) {
        this.type = type
        this.content = content
    }

    constructor(type: Int?) {
        this.type = type
    }

    companion object{
        val HEADER = 1
        val RADIO = 2
        val EDIT = 3
    }

}


data class AccountCheck(
    var titles:List<CheckModel>?=null
)


data class CheckModel(
    var title:String?=null,
    var contents:List<CheckContentModel>?=null,
    var remark:String?=null
){
    constructor():this(null,null,null)
}

data class CheckContentModel(
    var name: String?=null,
    var value:String?=null,
    var otherValue:String?=null,
    var selectedValue:String?=null
){
    constructor():this(null,null,null,null)
}


/**
 * @Created by: kuuga
 * @Date: on 2022/07/11
 * @Description:选择项正常异常状态
 */
data class SelectValueStatus(
    var value:String?,
    var correct:Boolean?
)



/**
 * @Created by: kuuga
 * @Date: on 2021/11/26 14:19
 * @Description:抽检模板数据
 */
data class TemplateModel(
    var templateId:Long?=null,
    var tasks:List<TemplateTask>?=null
)

/**
 * @Created by: kuuga
 * @Date: on 2021/11/26 14:19
 * @Description:模板任务数据
 */
data class TemplateTask(
    var taskId:Long?=null,
    var taskName:String?=null,
    var contents: List<TaskContent>?
)

/**
 * @Created by: kuuga
 * @Date: on 2021/11/26 14:19
 * @Description:任务内容数据
 */
data class TaskContent(
    var content: String?,
    var contentId: Long?,
    var selectValues: List<String>?,
    var type: Int?,
    var unit: String?,
    var value:String?,
    var selects:List<SelectValueStatus>?
){
    companion object{
        const val CHOICE=0
        const val INPUT=1

    }
}


/**
 * @Created by: kuuga
 * @Date: on 2021/11/29 10:47
 * @Description:提交模板数据
 */
data class UploadTemplateData(
    //抽检新一批需求需要判断两种状态
    var type:Int?=null, //1保存2提交
    var woId:Long?=null,
    var templateId:Long?=null,
    var startTime:Long?=null,
    var pass:Boolean?=null,
    var tasks: List<UploadTask>?=null
){
    constructor():this(null,null,null,null,null)
}

/**
 * @Created by: kuuga
 * @Date: on 2021/11/29 10:47
 * @Description:提交模板任务
 */
data class UploadTask (
    var taskId:Long?=null,
    var taskName:String?=null,
    var contents:List<UploadTaskContent>?=null
){
//    constructor(takId: Long?,taskName: String?):this(taskId,taskName,null)
//    constructor():this(null,null,null)
}
/**
 * @Created by: kuuga
 * @Date: on 2021/11/29 10:49
 * @Description:提交任务内容
 */
data class UploadTaskContent(
    val contentId:Long,
    val content:String,
    val inputValue:String?=null,
    val selectValue:String?=null
)





