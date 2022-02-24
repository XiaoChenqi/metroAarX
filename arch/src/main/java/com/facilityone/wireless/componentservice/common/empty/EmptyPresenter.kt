package com.facilityone.wireless.componentservice.common.empty

import com.facilityone.wireless.a.arch.ec.commonpresenter.CommonBasePresenter
import com.facilityone.wireless.componentservice.common.empty.EmptyFragment
import com.facilityone.wireless.basiclib.app.FM
import com.blankj.utilcode.util.SPUtils
import com.facilityone.wireless.a.arch.ec.utils.SPKey
import com.lzy.okgo.OkGo
import com.facilityone.wireless.a.arch.ec.module.FunctionService.FunctionBean
import com.facilityone.wireless.componentservice.common.permissions.PermissionsManager
import android.os.Bundle
import com.blankj.utilcode.util.ToastUtils
import com.facilityone.wireless.a.arch.ec.module.IService
import com.fm.tool.network.model.BaseResponse
import com.lzy.okgo.model.HttpParams
import com.lzy.okgo.model.Response

/**
 * Author：gary
 * Email: xuhaozv@163.com
 * description:
 * Date: 2018/10/15 5:24 PM
 */
class EmptyPresenter(private val type: Int) : CommonBasePresenter<EmptyFragment?>() {
    override fun onLogonSuccess() {
        val pId = 1L
        FM.getConfigurator().withProjectId(pId)
        SPUtils.getInstance(SPKey.SP_MODEL).put(SPKey.PROJECT_ID, pId)
        SPUtils.getInstance(SPKey.SP_MODEL).put(SPKey.PROJECT_NAME, "14号线", true)
        val httpParams = HttpParams("current_project", pId.toString())
        OkGo.getInstance().addCommonParams(httpParams)
        SPUtils.getInstance(SPKey.SP_MODEL).put(SPKey.HAVE_LOGON, true)
        getPermissions()
        getUserInfo()
    }

    override fun getPermissionsSuccess(data: String) {
        val list = PermissionsManager.HomeFunction.getInstance().show(data)
        var typeIndex:Int=-1
        for ((index,bean) in list.withIndex()) {
            if (bean.type == type) {
                typeIndex=index
            }
        }
        if (!list.isNullOrEmpty()){
            val bundle = Bundle()
            bundle.putBoolean(IService.COMPONENT_RUNALONE, true)
            if (typeIndex!=-1){
                if (list[typeIndex].sortChildMenu != null) {
                    bundle.putSerializable(IService.FRAGMENT_CHILD_KEY,list[typeIndex].sortChildMenu)
                }
                v!!.goFragment(bundle)
            }else{
                ToastUtils.showShort("您当前模块没有权限")
                v!!.requireActivity().finish()
            }

        }else{
            ToastUtils.showShort("您当前没有任何权限")
            v!!.requireActivity().finish()
        }

    }

    override fun onLogonError() {
        v!!.showLogonButton()
        v!!.requireActivity().finish()
    }

    override fun getPermissionsError(response: Response<BaseResponse<List<String>>>) {
        v!!.showLogonButton()
        v!!.requireActivity().finish()

    }
}