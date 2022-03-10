package com.facilityone.wireless.a.arch.offline.objectbox

import com.facilityone.wireless.ObjectBox
import com.facilityone.wireless.ObjectBox.boxStore
import com.facilityone.wireless.a.arch.offline.objectbox.construction.ConstructionTemplate
import io.objectbox.Box
import io.objectbox.BoxStore
import io.objectbox.kotlin.boxFor

/**数据库助手*/
object ObjectBoxHelper {
    private val boxStore: BoxStore = ObjectBox.boxStore!!


    //施工监护数据集合
    private val constructionBox: Box<ConstructionTemplate> = boxStore.boxFor()



    @JvmStatic
    fun getConstructionBox():Box<ConstructionTemplate>{
        return constructionBox
    }

    @JvmStatic
    fun getLastConstructionData():ConstructionTemplate?{
       return constructionBox.query().build().findFirst()
    }



}