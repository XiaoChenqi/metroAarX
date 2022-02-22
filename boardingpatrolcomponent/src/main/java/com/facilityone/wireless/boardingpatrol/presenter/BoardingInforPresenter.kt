package com.facilityone.wireless.boardingpatrol.presenter

import com.facilityone.wireless.a.arch.base.FMJsonCallback
import com.facilityone.wireless.a.arch.mvp.BasePresenter
import com.facilityone.wireless.a.arch.xcq.bean.BaseResponse
import com.facilityone.wireless.basiclib.app.FM
import com.facilityone.wireless.boardingpatrol.fragment.BoardingInforFragment
import com.facilityone.wireless.boardingpatrol.moudle.BoardingService
import com.facilityone.wireless.boardingpatrol.moudle.BoardingUrl
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response

class BoardingInforPresenter : BasePresenter<BoardingInforFragment>() {

    fun getQueryInfor(id:Long){
        var req = BoardingService.BoardingDetailReq()
        req.inspectionRegistrationId = id
        OkGo.post<BaseResponse<BoardingService.BoardingInforEnity>>(FM.getApiHost()+BoardingUrl.REGISTRATION_DETAILS)
            .isSpliceUrl(true)
            .tag(v)
            .upJson(toJson(req))
            .execute(object : FMJsonCallback<BaseResponse<BoardingService.BoardingInforEnity>>(){
                override fun onSuccess(resp: Response<BaseResponse<BoardingService.BoardingInforEnity>>?) {
                    if (resp?.body()?.data != null){
                        v.refreshInfro(resp.body().data)
                    }else{
                        v.refreshError()
                    }
                }

                override fun onError(response: Response<BaseResponse<BoardingService.BoardingInforEnity>>?) {
                    super.onError(response)
                    v.refreshError()
                }
            })
    }

}