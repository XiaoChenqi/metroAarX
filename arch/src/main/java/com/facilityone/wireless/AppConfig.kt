package com.facilityone.wireless

import com.drake.serialize.serialize.serial
import com.drake.serialize.serialize.serialLazy
import okio.JvmField

object AppConfig {

    var serverHost: String? by serialLazy("http://222.66.139.92:9999/fz_iframe")

    @JvmStatic
    var uname:String? by serial("")
    @JvmStatic
    var upwd:String? by serial("")

    @JvmStatic
    var env:String by serial("line14")
    //默认环境为line14博坤,测试环境为debug


    const val LINE14="line14"
    const val DEBUG="debug"
}