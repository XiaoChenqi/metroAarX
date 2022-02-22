package com.facilityone.wireless.workorder.presenter

import android.util.Log
import com.blankj.utilcode.util.ToastUtils
import com.facilityone.wireless.a.arch.base.FMJsonCallback
import com.facilityone.wireless.a.arch.ec.module.CommonUrl
import com.facilityone.wireless.a.arch.ec.module.VisitUserBean
import com.facilityone.wireless.a.arch.mvp.BasePresenter
import com.facilityone.wireless.basiclib.app.FM
import com.facilityone.wireless.workorder.R
import com.facilityone.wireless.workorder.fragment.WorkorderNewCreateFragment
import com.facilityone.wireless.workorder.module.WorkorderService
import com.facilityone.wireless.workorder.module.WorkorderUrl
import com.fm.tool.network.model.BaseResponse
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import kotlinx.coroutines.flow.callbackFlow

class WorkorderNewCreatePresenter : BasePresenter<WorkorderNewCreateFragment>() {

    /**
     * 提交工单
     * */
    fun workOrderUpload(req: WorkorderService.newOrderReq){
        v.showLoading()
        OkGo.post<BaseResponse<List<VisitUserBean>>>(FM.getApiHost() + WorkorderUrl.NEW_ORDER_CREATE)
            .isSpliceUrl(true)
            .tag(v)
            .upJson(toJson(req))
            .execute(object :FMJsonCallback<BaseResponse<List<VisitUserBean>>>(){
                override fun onSuccess(response: Response<BaseResponse<List<VisitUserBean>>>?) {
                    v.dismissLoading()
                    ToastUtils.showShort(R.string.workorder_submit_success)
                    v.popForResult()
                }

                override fun onError(response: Response<BaseResponse<List<VisitUserBean>>>?) {
                    super.onError(response)
                    v.dismissLoading()
                    ToastUtils.showShort(R.string.workorder_submit_failed)
                }
            })
    }

    /**
     *  @Author: Karelie
     *  @Method：checkList
     *  @Description：检查数组内是否有未填入的内容以及重复的内容
     */
    fun checkList(list: ArrayList<WorkorderService.newOrderEnity>?):Boolean{
        list?.forEachIndexed { index, it ->
            var checkId:Long = it.serviceTypeId
            var checkSecondId:Long = it.priorityId
            for ((position,ids) in list.withIndex()) {
                if (position != null && position != index){
                    if (ids.serviceTypeId == checkId && ids.priorityId == checkSecondId){
                        return false
                    }
                }
            }

        }
        return true
    }

    /**
     *  @Author: Karelie
     *  @Method：hasOmission
     *  @Description：判断是否有没填入的内容，所有项需要校验必填
     */
    fun hasOmission(list: ArrayList<WorkorderService.newOrderEnity>?):Boolean{
        list?.forEachIndexed { index, it ->
            if (it.type==null){
                Log.i("工单类型没填", "hasOmission: 位置"+index)
                return false
            }

            if (it.serviceTypeId == null){
                Log.i("服务类型没填", "hasOmission: 位置"+index)
                return false
            }

            if (it.priorityId == null){
                Log.i("优先级没填", "hasOmission: 位置"+index)
                return false
            }
        }
        return true
    }

}