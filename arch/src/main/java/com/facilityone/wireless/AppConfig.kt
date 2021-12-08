package com.facilityone.wireless

import com.drake.serialize.serialize.serialLazy

object AppConfig {

    var serverHost: String? by serialLazy("http://47.99.236.153:8071/fz_iframe")
}