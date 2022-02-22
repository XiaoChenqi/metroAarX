package com.example.testaarx

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.facilityone.wireless.AppConfig
import com.facilityone.wireless.basiclib.app.FM

class UserProfileActivity: AppCompatActivity() {

    var etUname: EditText? =null
    var etPwd: EditText? =null
    var btnSaveConfig:Button?=null
    var configName:String?=null
    var configPwd:String?=null

    var btnLine14:Button?=null
    var btnDebug:Button?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        etUname = findViewById(R.id.etUname)
        etPwd = findViewById(R.id.etPwd)
        btnSaveConfig = findViewById(R.id.btnSaveConfig)
        btnLine14 = findViewById(R.id.btnEnvLine14)
        btnDebug = findViewById(R.id.btnEnvDebug)

        configName=AppConfig.uname
        configPwd=AppConfig.upwd
        etUname!!.setText(configName)
        etPwd!!.setText(configPwd)
        btnSaveConfig!!.setOnClickListener {

            val tempName=etUname!!.text.toString()
            val tempPwd=etPwd!!.text.toString()
            if (!TextUtils.isEmpty(tempName)&&!TextUtils.isEmpty(tempPwd)){
                AppConfig.uname=tempName
                AppConfig.upwd=tempPwd
                com.blankj.utilcode.util.ToastUtils.showShort("设置临时账户成功 ")
                Log.d("账户",AppConfig.uname+"")
                Log.d("密码",AppConfig.upwd+"")
            }else{
                com.blankj.utilcode.util.ToastUtils.showShort("用户密码不能为空")
            }

        }

        btnDebug?.setOnClickListener {
            AppConfig.env=AppConfig.DEBUG
            com.blankj.utilcode.util.ToastUtils.showShort("切换至测试模式,可以使用临时账户登录 ")
        }


        btnLine14?.setOnClickListener {
            AppConfig.env=AppConfig.LINE14
            com.blankj.utilcode.util.ToastUtils.showShort("切换至正式环境,只允许使用博坤客户端传递账户 ")
        }

    }
}