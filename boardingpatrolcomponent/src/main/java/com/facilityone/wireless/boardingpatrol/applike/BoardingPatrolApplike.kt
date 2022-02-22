//@file:JvmName("RidePatrolApplike")
package com.facilityone.wireless.boardingpatrol.applike
import com.facilityone.wireless.componentservice.boardingpatrol.BoardingPatrolService
import com.facilityone.wireless.boardingpatrol.serviceimpl.BoardingPatrolServiceImpl
import com.luojilab.component.componentlib.applicationlike.IApplicationLike
import com.luojilab.component.componentlib.router.Router
import com.luojilab.component.componentlib.router.ui.UIRouter

class BoardingPatrolApplike : IApplicationLike {
    var mUIRouter = UIRouter.getInstance()
    var mRouter = Router.getInstance()
    override fun onCreate() {
        mUIRouter.registerUI("ridepatrol")
        mRouter.addService(BoardingPatrolService::class.java.simpleName, BoardingPatrolServiceImpl())
    }

    override fun onStop() {
        mUIRouter.unregisterUI("ridepatrol")
        mRouter.removeService(BoardingPatrolService::class.java.simpleName)
    }
}