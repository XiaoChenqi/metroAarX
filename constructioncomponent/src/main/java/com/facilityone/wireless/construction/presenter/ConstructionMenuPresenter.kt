package com.facilityone.wireless.construction.presenter

import com.facilityone.wireless.a.arch.ec.commonpresenter.CommonBasePresenter
import com.facilityone.wireless.construction.fragment.ConstructionMenuFragment
import org.json.JSONObject

class ConstructionMenuPresenter : CommonBasePresenter<ConstructionMenuFragment>() {

    override fun getUndoNumberSuccess(data: JSONObject?) {
        super.getUndoNumberSuccess(data)
        v.updateFunction(functionBeanList = v.getFunctionBeanList())
    }



}