package com.facilityone.wireless.workorder.presenter

import com.blankj.utilcode.util.ToastUtils
import com.facilityone.wireless.a.arch.base.FMJsonCallback
import com.facilityone.wireless.a.arch.mvp.BasePresenter
import com.facilityone.wireless.basiclib.app.FM
import com.facilityone.wireless.componentservice.workorder.WorkorderService
import com.facilityone.wireless.workorder.R
import com.facilityone.wireless.workorder.WorkOrderNfcList
import com.facilityone.wireless.workorder.module.WorkorderUrl
import com.fm.tool.network.model.BaseResponse
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import me.yokeyword.fragmentation.ISupportFragment
import okio.Okio
import java.util.*

/**
 * @Creator:Karelie
 * @Data: 2021/11/16
 * @TIME: 15:09
 * @Introduce: 维护NFC操作
**/
class WorkOrderNFCPresenter : BasePresenter<WorkOrderNfcList?>(){
    /**
     * NFC添加操作记录
     * @param woId 工单ID
     * @param positionId 位置ID
     * */
    fun addSignIn(woId : Long,positionId : Long){
        val params: MutableMap<String, Any> = HashMap()
        params["woId"] = woId
        params["positionId"] = positionId
        OkGo.post<BaseResponse<Any>>(FM.getApiHost() + WorkorderUrl.SIGNIN_NFC_ADD)
            .tag(v)
            .isSpliceUrl(true)
            .upJson(toJson(params))
            .execute(object : FMJsonCallback<BaseResponse<Any>>() {
                override fun onSuccess(response: Response<BaseResponse<Any>>) {
                    v!!.dismissLoading()
                    ToastUtils.showShort("添加成功")
                    v!!.finish()
                }

                override fun onError(response: Response<BaseResponse<Any>>) {
                    super.onError(response)
                    v!!.dismissLoading()
                    ToastUtils.showShort("添加失败")
                }
            })
    }

//
//    /**
//     * 判断NFC是否能执行
//     * @param woId 工单ID
//     * */
//    fun nfcCanDo(woId : Long,positionId : Long){
//        val params: MutableMap<String, Any> = HashMap()
//        params["woId"] = woId
//        OkGo.post<BaseResponse<Boolean>>(FM.getApiHost() + WorkorderUrl.NFC_CAN_DO)
//            .tag(v)
//            .isSpliceUrl(true)
//            .upJson(toJson(params))
//            .execute(object : FMJsonCallback<BaseResponse<Boolean>>() {
//                override fun onSuccess(response: Response<BaseResponse<Boolean>>) {
//                    v!!.dismissLoading()
//                    if (!response.body().data){
//                        ToastUtils.showShort("当前位置不可操作")
//                    }else{
//                        addSignIn(woId, positionId)
//                    }
//                }
//
//                override fun onError(response: Response<BaseResponse<Boolean>>) {
//                    super.onError(response)
//                    v!!.dismissLoading()
//                    ToastUtils.showShort(R.string.workorder_submit_failed)
//                }
//            })
//    }


}