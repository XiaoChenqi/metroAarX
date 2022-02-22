package com.facilityone.wireless.boardingpatrol.presenter

import com.blankj.utilcode.util.ToastUtils
import com.facilityone.wireless.a.arch.base.FMJsonCallback
import com.facilityone.wireless.a.arch.ec.module.VisitUserBean
import com.facilityone.wireless.a.arch.mvp.BasePresenter
import com.facilityone.wireless.basiclib.app.FM
import com.facilityone.wireless.boardingpatrol.fragment.BoardingCreateFragment
import com.facilityone.wireless.boardingpatrol.moudle.BoardingService
import com.facilityone.wireless.boardingpatrol.moudle.BoardingUrl
import com.facilityone.wireless.boardingpatrol.toast
import com.fm.tool.network.model.BaseResponse
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import java.util.*

class BoardingCreatePresenter : BasePresenter<BoardingCreateFragment>() {
    
    /**
    *  @Author: Karelie
    *  @Method：checkUpdata
    *  @Description：
    */
    fun checkUpdata(req: BoardingService.BoardingSaveReq){
        OkGo.post<BaseResponse<Object>>(FM.getApiHost() + BoardingUrl.REGISTRATION_SAVE)
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