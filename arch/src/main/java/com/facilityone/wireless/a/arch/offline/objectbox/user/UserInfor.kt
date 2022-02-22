package com.facilityone.wireless.a.arch.offline.objectbox.user

import com.facilityone.wireless.a.arch.ec.module.LocationBean
import com.facilityone.wireless.basiclib.utils.GsonUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.converter.PropertyConverter
import java.lang.Exception
import java.lang.reflect.Type

/**
 * @Creator:Karelie
 * @Data: 2021/12/23
 * @TIME: 16:27
 * @Introduce: 用户签到信息存储 只存储这一条
 *
 * PropertyConverter : 非基础类型状况下需要设置转换器 将数据以Json的形式保存
**/
@Entity
data class UserInfor(
    @Id
    var id:Long? = 0, //默认值
    var lastBuilding:Long? = 0,
    var lastFloor:Long? = 0,
    @Convert(converter = LocationConverter::class,dbType = String::class)
    var locationBean:LocationBean? = null,
    @Convert(converter = Buildings ::class,dbType = String::class)
    var buidlings:List<Long>?= null, //管理区域Id
    var userKey:String? = null //用户身份认证
)

  class LocationConverter :PropertyConverter<LocationBean?,String?>{
    override fun convertToEntityProperty(locationDate: String?): LocationBean? {
        if (locationDate == null){
            return null
        }
        try {
            val data : LocationBean = GsonUtils.fromJson(locationDate,LocationBean::class.java)
            return data
        }catch (e:Exception){
            e.printStackTrace()
            return null
        }
    }

    override fun convertToDatabaseValue(location : LocationBean?): String? {
       return GsonUtils.toJson(location)
    }
}

class Buildings : PropertyConverter<List<Long>?,String?>{

    override fun convertToEntityProperty(locationDate: String?): List<Long>? {
        if (locationDate == null){
            return null
        }
        val type : Type = object :TypeToken<List<Long>>(){}.type
        try {
            val data : List<Long> = GsonUtils.fromJson(locationDate,type)
            return data
        }catch (e:Exception){
            e.printStackTrace()
            return null
        }
    }

    override fun convertToDatabaseValue(location: List<Long>?): String? {
        return GsonUtils.toJson(location)
    }
}
