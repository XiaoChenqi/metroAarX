package com.facilityone.wireless.construction.presenter

import com.facilityone.wireless.a.arch.base.FMJsonCallback
import com.facilityone.wireless.a.arch.mvp.BasePresenter
import com.facilityone.wireless.basiclib.app.FM
import com.facilityone.wireless.construction.fragment.ConstructionRegistFragment
import com.facilityone.wireless.construction.module.ConstructionService
import com.facilityone.wireless.construction.module.ConstructionUrl
import com.facilityone.wireless.construction.toast
import com.fm.tool.network.model.BaseResponse
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response

class ConstructionRegistPresenter : BasePresenter<ConstructionRegistFragment>() {

    fun upLoadRegist( req : ConstructionService.ConstructionRegReq){
        OkGo.post<BaseResponse<Object>>(FM.getApiHost() + ConstructionUrl.CONSTRUCTION_SAVE)
            .isSpliceUrl(true)
            .tag(v)
            .upJson(toJson(req))
            .execute(object : FMJsonCallback<BaseResponse<Object>>(){
                override fun onSuccess(response: Response<BaseResponse<Object>>?) {
                    v.dismissLoading()
                    toast.show("提交成功")
                    v.pop()
                }

                override fun onError(response: Response<BaseResponse<Object>>?) {
                    super.onError(response)
                    v.dismissLoading()
                    toast.show("提交失败")
                }
            })
    }

}