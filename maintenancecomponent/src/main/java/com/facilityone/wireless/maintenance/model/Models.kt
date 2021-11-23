package com.facilityone.wireless.maintenance.model


data class SelectorModel(
    var name:String?=null,
    var value:Int?=null,
    var state:Int?=null,
    var sub: SelectorModel?,
    var pid:Int?=null,
    var tips:String?=null

){
    constructor(name: String?,value: Int?,pid:Int?=null):this(name,value,3,null,pid)
    constructor(name: String, value: Int, sub: SelectorModel,pid: Int) : this(name,value,3,sub,pid)

    constructor(name: String?,value: Int?,pid:Int?=null,tip:String):this(name,value,3,null,pid,tip)

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

