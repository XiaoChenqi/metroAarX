package com.facilityone.wireless.construction.presenter

import com.facilityone.wireless.a.arch.base.FMJsonCallback
import com.facilityone.wireless.a.arch.ec.module.Page
import com.facilityone.wireless.a.arch.mvp.BaseFragment
import com.facilityone.wireless.a.arch.mvp.BasePresenter
import com.facilityone.wireless.a.arch.xcq.bean.BaseResponse
import com.facilityone.wireless.basiclib.app.FM
import com.facilityone.wireless.construction.fragment.ConstructionQueryFragment
import com.facilityone.wireless.construction.module.ConstructionService
import com.facilityone.wireless.construction.module.ConstructionUrl
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response

class ConstructionQueryPresenter : BasePresenter<ConstructionQueryFragment>() {

    /**
     *  @Author: Karelie
     *  @Method：getConsQueyList
     *  @Date：2022/3/3 10:52
     *  @Description：获取施工监控记录
     */
    fun getConsQueyList(page: Page, refresh:Boolean){
        val req = ConstructionService.ConstructionQueryReq()
        req.page = page
        OkGo.post<BaseResponse<ConstructionService.ConstructionQueryEnity>>(FM.getApiHost() + ConstructionUrl.CONSTRUCTION_QUERY)
            .isSpliceUrl(true)
            .tag(v)
            .upJson(toJson(req))
            .execute(object :FMJsonCallback<BaseResponse<ConstructionService.ConstructionQueryEnity>>(){
                override fun onSuccess(resp: Response<BaseResponse<ConstructionService.ConstructionQueryEnity>>?) {
                    var enity = resp?.body()?.data
                    if (enity != null){
                        v.querySuccess(enity,refresh)
                    }else{
                        v.queryError()
                    }
                }

                override fun onError(response: Response<BaseResponse<ConstructionService.ConstructionQueryEnity>>?) {
                    super.onError(response)
                    v.queryError()
                }
            })
    }
}