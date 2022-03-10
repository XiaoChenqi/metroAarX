package com.facilityone.wireless.construction.presenter

import com.facilityone.wireless.a.arch.base.FMJsonCallback
import com.facilityone.wireless.a.arch.mvp.BasePresenter
import com.facilityone.wireless.a.arch.xcq.bean.BaseResponse
import com.facilityone.wireless.basiclib.app.FM
import com.facilityone.wireless.cons.ConstructionInforFragment
import com.facilityone.wireless.construction.module.ConstructionService
import com.facilityone.wireless.construction.module.ConstructionUrl
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response

class ConstructionInforPresenter : BasePresenter<ConstructionInforFragment>() {

    fun getQueryInfor(id:Long){
        var req = ConstructionService.ConstructionDetailReq()
        req.templateId = id
        OkGo.post<BaseResponse<ConstructionService.ConstructionInforEnity>>(FM.getApiHost()+ ConstructionUrl.CONSTRUCTION_DETAILS)
            .isSpliceUrl(true)
            .tag(v)
            .upJson(toJson(req))
            .execute(object : FMJsonCallback<BaseResponse<ConstructionService.ConstructionInforEnity>>(){
                override fun onSuccess(resp: Response<BaseResponse<ConstructionService.ConstructionInforEnity>>?) {
                    if (resp?.body()?.data != null){
                        v.refreshInfor(resp.body().data)
                    }else{
                        v.refreshError()
                    }
                }

                override fun onError(response: Response<BaseResponse<ConstructionService.ConstructionInforEnity>>?) {
                    super.onError(response)
                    v.refreshError()
                }
            })
    }

}