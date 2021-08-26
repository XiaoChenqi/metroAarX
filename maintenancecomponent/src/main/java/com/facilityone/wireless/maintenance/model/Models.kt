package com.facilityone.wireless.maintenance.model


data class SelectorModel(
    var name:String?=null,
    var value:Int?=null,
    var state:Int?=null,
    var sub: SelectorModel?

){
    constructor(name: String?,value: Int?):this(name,value,3,null)
    constructor(name: String, value: Int, sub: SelectorModel) : this(name,value,3,sub) {

    }

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