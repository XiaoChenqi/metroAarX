package com.facilityone.wireless.construction.presenter

import com.facilityone.wireless.a.arch.base.FMJsonCallback
import com.facilityone.wireless.a.arch.mvp.BasePresenter
import com.facilityone.wireless.basiclib.app.FM
import com.facilityone.wireless.construction.fragment.ConstructionTemplateFragment
import com.facilityone.wireless.construction.module.ConstructionService
import com.facilityone.wireless.construction.module.ConstructionUrl
import com.facilityone.wireless.construction.module.TemplateModel
import com.fm.tool.network.model.BaseResponse
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import java.util.HashMap

class ConstructionTemplatePresenter : BasePresenter<ConstructionTemplateFragment>() {
    fun getTemplateData(woId: Long, templateId: Long) {
        v.showLoading()
        var url = ""
        url = ConstructionUrl.CONSTRUCTION_TEMPLATE
        val woIdMap: MutableMap<String, Long> = HashMap()
        woIdMap["woId"] = woId
        woIdMap["sampleId"] = templateId

//         String tempUrl="http://192.168.14.2:4523/mock/413312/fz_iframe/m/v1/sample/contents";
        val tempUrl = FM.getApiHost() + url
        OkGo.post<BaseResponse<TemplateModel>>(tempUrl)
            .tag(v)
            .isSpliceUrl(true)
            .upJson(toJson(woIdMap))
            .execute(object :
                FMJsonCallback<BaseResponse<TemplateModel>?>() {
                override fun onSuccess(response: Response<BaseResponse<TemplateModel>?>) {
                    v.dismissLoading()
                    val data: TemplateModel? =
                        response.body()?.data
                    if (data != null) {
                        v.onLoadTemplateSuccess(data)
                    } else {
                        v.onLoadTemplateFail()
                    }
                }

                override fun onError(response: Response<BaseResponse<TemplateModel>?>) {
                    super.onError(response)
                    v.dismissLoading()
                    v.onLoadTemplateFail()
                }
            })
    }


}