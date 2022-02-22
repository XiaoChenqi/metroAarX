package com.facilityone.wireless.boardingpatrol.adapter

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.blankj.utilcode.util.TimeUtils
import com.facilityone.wireless.basiclib.utils.DateUtils
import com.facilityone.wireless.boardingpatrol.moudle.BoardingPatrolConstant.INTERVAL_UP
import com.facilityone.wireless.boardingpatrol.moudle.BoardingService
import java.util.*

object BindingAdapter {
    @BindingAdapter("querySite")
    @JvmStatic
    fun querySite(title:TextView,enity: BoardingService.BoardingQueryContents){
        title.text = "检查线路区段："+"${enity.startingStation}至${enity.endingStation}${ if(enity.interval==INTERVAL_UP) "上行" else "下行" }区间"
    }

    @BindingAdapter("queryText")
    @JvmStatic
    fun queryText(tV:TextView,date:Long){
        tV.text = "日期："+TimeUtils.date2String(Date(date), DateUtils.SIMPLE_DATE_FORMAT_YMD)
    }

}

