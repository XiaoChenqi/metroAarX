package com.example.testaarx

import android.app.Activity
import android.os.Bundle
import com.didi.drouter.api.DRouter
import com.didi.drouter.api.Extend
import com.facilityone.wireless.RouteTable


/**
* @Author kuuga.wu
* @Date 2022/5/9
* @Desc 外部跳转页面
*/
class SchemeActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DRouter.build(intent.data.toString())
            .putExtra(Extend.START_ACTIVITY_WITH_DEFAULT_SCHEME_HOST, RouteTable.SCHEMA_HOST)
            .putExtra(RouteTable.FROM_BK_MSG,true)
            .putExtra("USERNAME",intent.getStringExtra("USERNAME")?:"")
            .putExtra("PASSWORD",intent.getStringExtra("PASSWORD")?:"")
            .putExtra("COLOR",intent.getStringExtra("COLOR"))
            .start(this)
        finish()
    }

}