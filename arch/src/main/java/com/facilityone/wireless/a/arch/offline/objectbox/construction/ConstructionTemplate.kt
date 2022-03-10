package com.facilityone.wireless.a.arch.offline.objectbox.construction

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Index
/**
 * @Creator:Karelie
 * @Data: 2021/12/23
 * @TIME: 18:10
 * @Introduce: 巡检模块离线模块 若开启任务则存储在当前数据库中 只存一条
**/
@Entity
data class ConstructionTemplate(
    @Id
    var id:Long = 0,
    var status:Boolean?=false,
    //待提交数据
    var upLoadContent:String?="",
    //页面原始数据,用于重复进入修改
    var originContent:String?="",
    //原始响应数据,用于跳过非空判断
    var originResponse:String?=""
)