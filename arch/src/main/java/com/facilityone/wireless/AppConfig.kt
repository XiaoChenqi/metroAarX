package com.facilityone.wireless

import com.drake.serialize.serialize.serialLazy

object AppConfig {

    var serverHost: String? by serialLazy("http://222.66.139.92:9999/fz_iframe")
}