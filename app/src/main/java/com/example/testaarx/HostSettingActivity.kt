package com.example.testaarx

import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.facilityone.wireless.AppConfig
import com.facilityone.wireless.basiclib.app.FM

class HostSettingActivity: AppCompatActivity() {

    var etServerHost: EditText? =null
    var btnSaveConfig:Button?=null
    var serverHost:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_host_server)
        etServerHost = findViewById(R.id.etServerHost)
        btnSaveConfig = findViewById(R.id.btnSaveConfig)

        serverHost=AppConfig.serverHost
        etServerHost!!.setText(serverHost)
        btnSaveConfig!!.setOnClickListener {

            val tempHost=etServerHost!!.text.toString()
            if (!TextUtils.isEmpty(tempHost)){
                AppConfig.serverHost=tempHost
                FM.getConfigurator().withApiHost(AppConfig.serverHost)
                com.blankj.utilcode.util.ToastUtils.showShort("服务器地址已设置成 "+FM.getApiHost())
            }else{
                com.blankj.utilcode.util.ToastUtils.showShort("服务器地址不能为空")
            }

        }


    }
}