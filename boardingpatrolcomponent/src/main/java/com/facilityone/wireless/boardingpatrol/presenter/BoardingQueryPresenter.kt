package com.facilityone.wireless.boardingpatrol.presenter

import com.facilityone.wireless.a.arch.base.FMJsonCallback
import com.facilityone.wireless.a.arch.ec.module.Page
import com.facilityone.wireless.a.arch.mvp.BasePresenter
import com.facilityone.wireless.a.arch.xcq.bean.BaseResponse
import com.facilityone.wireless.basiclib.app.FM
import com.facilityone.wireless.boardingpatrol.fragment.BoardingQueryFragment
import com.facilityone.wireless.boardingpatrol.moudle.BoardingService
import com.facilityone.wireless.boardingpatrol.moudle.BoardingUrl
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import okio.Okio

class BoardingQueryPresenter : BasePresenter<BoardingQueryFragment>() {

    /**
     *  @Author: Karelie
     *  @Method：getBoardingQueyList
     *  @Date：2022/2/16 11:47
     *  @Description：获取所有的登乘巡查查询数据
     */
    fun getBoardingQueyList(page:Page,refresh:Boolean){
        val req = BoardingService.BoardingQueryReq()
        req.page = page
        OkGo.post<BaseResponse<BoardingService.BoardingQueryEnity>>(FM.getApiHost() + BoardingUrl.REGISTRATION_QUERY)
            .isSpliceUrl(true)
            .tag(v)
            .upJson(toJson(req))
            .execute(object :FMJsonCallback<BaseResponse<BoardingService.BoardingQueryEnity>>(){
                override fun onSuccess(resp: Response<BaseResponse<BoardingService.BoardingQueryEnity>>?) {
                   var enity = resp?.body()?.data
                    if (enity != null){
                        v.queryListSuccess(enity,refresh)
                    }else{
                        v.queryListError()
                    }
                }

                override fun onError(response: Response<BaseResponse<BoardingService.BoardingQueryEnity>>?) {
                    super.onError(response)
                    v.queryListError()
                }
            })
    }
}