package com.facilityone.wireless.construction.adapter

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.blankj.utilcode.util.TimeUtils
import com.facilityone.wireless.basiclib.utils.DataUtils
import com.facilityone.wireless.basiclib.utils.DateUtils
import com.facilityone.wireless.construction.module.ConstructionService
import java.util.*

object BindingAdapter {
    @BindingAdapter("queryTitle")
    @JvmStatic
    fun queryTitle(title:TextView,enity: ConstructionService.ConstructionQueryContents){
        title.text = enity.guardianName+""
    }

    @BindingAdapter("queryUnit")
    @JvmStatic
    fun queryUnit(title:TextView,enity: ConstructionService.ConstructionQueryContents){
        title.text = enity.constructionUnit+""
    }

    @BindingAdapter("querySite")
    @JvmStatic
    fun querySite(title:TextView,enity: ConstructionService.ConstructionQueryContents){
        title.text = enity.constructionSite+""
    }

    @BindingAdapter("queryData")
    @JvmStatic
    fun queryData(title:TextView,enity: ConstructionService.ConstructionQueryContents){
        if (enity.constructionDate != null){
            title.text = TimeUtils.date2String(Date(enity.constructionDate!!), DateUtils.SIMPLE_DATE_FORMAT_YMD)
        }

    }

}

