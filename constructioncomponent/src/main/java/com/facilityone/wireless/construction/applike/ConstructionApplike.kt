//@file:JvmName("RidePatrolApplike")
package com.facilityone.wireless.construction.applike

import com.facilityone.wireless.componentservice.construction.ConstructionService
import com.facilityone.wireless.construction.serviceimpl.ConstructionServiceImpl
import com.luojilab.component.componentlib.applicationlike.IApplicationLike
import com.luojilab.component.componentlib.router.Router
import com.luojilab.component.componentlib.router.ui.UIRouter

class ConstructionApplike : IApplicationLike {
    var mUIRouter = UIRouter.getInstance()
    var mRouter = Router.getInstance()
    override fun onCreate() {
        mUIRouter.registerUI("construction")
        mRouter.addService(ConstructionService::class.java.simpleName, ConstructionServiceImpl())
    }

    override fun onStop() {
        mUIRouter.unregisterUI("construction")
        mRouter.removeService(ConstructionService::class.java.simpleName)
    }
}