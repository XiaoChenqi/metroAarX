package com.facilityone.wireless

class RouteTable {

    companion object{
        //来自博坤的消息推送
        const val FROM_BK_MSG="from_bk_msg"
        const val SECHEMA = "fm"
        const val HOST = "line14c"
        const val SCHEMA_HOST = "fmone://line14c"


        const val PATROL: String="/patrol"

        //巡检查询页面
        const val PATROL_QUERY: String="query"
        //巡检点位
        const val PATROL_QUERY_SPOT: String="querySpot"


        const val WORKORDER:String="/workOrder"
        const val WORKORDER_DETAIL:String="detail"


        const val PPM:String="/ppm"
        const val PPM_CONTENT:String="content"



        const val DEMAND:String="/requirement"
        const val DEMAND_DETAIL:String="detail"

        const val INVENTORY:String="/stock"
        const val INVENTORY_DETAIL:String="detail"
        const val INVENTORY_SEARCH:String="search"

    }






}