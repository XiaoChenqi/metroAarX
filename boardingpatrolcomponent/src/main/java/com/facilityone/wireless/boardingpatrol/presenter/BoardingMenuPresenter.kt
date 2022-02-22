package com.facilityone.wireless.boardingpatrol.presenter

import com.facilityone.wireless.a.arch.ec.commonpresenter.CommonBasePresenter
import com.facilityone.wireless.boardingpatrol.fragment.BoardingPatrolMenuFragment
import org.json.JSONObject

class BoardingMenuPresenter : CommonBasePresenter<BoardingPatrolMenuFragment>() {

    override fun getUndoNumberSuccess(data: JSONObject?) {
        super.getUndoNumberSuccess(data)
        v.updateFunction(functionBeanList = v.getFunctionBeanList())
    }



}