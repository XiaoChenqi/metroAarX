package com.facilityone.wireless.a.arch.offline.objectbox.patrol

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Index

@Entity
data class CompleteTime(
    @Id
    var id:Long = 0,
    @Index
    var taskTip:String? =null, //标记任务
    var taskName:String? = null, //任务名称
    var taskId:Long = 0, //任务Id
    var patrolSpotId:Long = 0,//点位Id
    var starTime:Long = 0, //任务开启名称
    var checkTime:Int = 0  //任务最短完成时间
)