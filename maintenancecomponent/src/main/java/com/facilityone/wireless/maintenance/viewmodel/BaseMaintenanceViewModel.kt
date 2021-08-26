package com.facilityone.wireless.maintenance.viewmodel

import com.facilityone.wireless.a.arch.base.FMJsonCallback
import com.facilityone.wireless.a.arch.mvvm.BaseDBFragment
import com.facilityone.wireless.a.arch.mvvm.BaseViewModel
import com.fm.tool.network.model.BaseResponse
import com.lzy.okgo.model.Response

open class BaseMaintenanceViewModel(): BaseViewModel() {}
//
////    fun getMaintenanceInfo(woId:Long){
////
////        var json = "{\"woId\":$woId}"
////        OkGo.post<com.fm.tool.network.model.BaseResponse<WorkorderInfoBean?>?>(FM.getApiHost() + WorkorderUrl.WORKORDER_INFO_URL)
////            .tag(getV())
////            .isSpliceUrl(true)
////            .upJson(json)
////            .execute(
////                object : FMJsonCallback<BaseResponse<WorkorderInfoBean?>?>() {
////                    fun onSuccess(response: Response<BaseResponse<WorkorderInfoBean?>>) {
////                        getV().dismissLoading()
////                        val data: WorkorderInfoBean? = response.body().data
////                        if (data != null) {
////                            getWorkorderInfoSuccess(woId, data)
////                        } else {
////                            getWorkorderInfoError()
////                        }
////                    }
////
////                    override fun onError(response: Response<BaseResponse<WorkorderInfoBean?>?>) {
////                        super.onError(response)
////                        getV().dismissLoading()
////                        getWorkorderInfoError()
////                    }
////                })
//    }
//}