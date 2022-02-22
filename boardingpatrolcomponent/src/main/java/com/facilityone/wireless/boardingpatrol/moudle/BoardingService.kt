package com.facilityone.wireless.boardingpatrol.moudle

import com.facilityone.wireless.a.arch.ec.module.Page

/**
 *  @Author: Karelie
 *  @Method：BoardingService
 *  @Date：2022/2/14 17:35
 *  @Description：登乘巡检实体
 */
class BoardingService {

    class BoardingSaveReq{
        var name : String? = null //检查人
        var unit : String? = null //检查单位
        var startingStationId : Long? = null //开始站点Id
        var endingStationId : Long? = null //结束站点Id
        var interval : Int? = null //区间 0-上行 1-下行
        var inspectionMethod : Int? = null // 0-步行 1-添乘
        var additionCheck : AdditionCheck? = null //添乘具体内容
        var content : String? = null //检查内容
        var condition : String? = null //检查情况
        var resolutionCondition : String? = null //解决情况
        var needTrackedCondition : String? = null //需跟踪情况
    }

    class AdditionCheck{
        var carNumber : String? = null //添乘列车号
        var driver : String? = null //值乘司机
        var boardingTime : Long? = null //添乘上车时间
        var getOffTime : Long? = null //添乘下车时间
    }

     class BoardingQueryEnity{
         var page: Page? = null
         val contents:List<BoardingQueryContents>?= null
    }

    class BoardingQueryReq{
        var page:Page? = null
    }

    class BoardingDetailReq{
        var inspectionRegistrationId:Long? = null //登乘巡查Id
    }

    class BoardingQueryContents{
        var inspectionRegistrationId:Long? = null
        var startingStation:String? = null
        var endingStation:String? = null
        var createDate:Long? = null
        var interval:Int? = null
    }

    class BoardingInforEnity{
        var name:String? = null
        var unit:String? = null
        var inspectionMethod:Int? = null
        var carNumber:String? = null
        var driver:String? = null
        var boardingTime:Long? = null
        var getOffTime:Long? = null
        var content:String? = null
        var condition:String? = null
        var resolutionCondition:String? = null
        var needTrackedCondition:String? = null
        var userName:String? = null
        var createDate:Long? = null
    }

}