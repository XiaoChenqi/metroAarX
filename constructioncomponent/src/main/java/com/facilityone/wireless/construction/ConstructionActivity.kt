package com.facilityone.wireless.construction

import com.facilityone.wireless.construction.fragment.ConstructionMenuFragment.Companion.getInstance
import com.luojilab.router.facade.annotation.RouteNode
import com.facilityone.wireless.a.arch.mvp.BaseFragmentActivity
import com.facilityone.wireless.componentservice.common.empty.EmptyFragment.OnGoFragmentListener
import com.facilityone.wireless.componentservice.common.empty.EmptyFragment
import com.facilityone.wireless.a.arch.base.FMFragment
import com.facilityone.wireless.componentservice.common.permissions.CommonConstant
import android.os.Bundle
import com.blankj.utilcode.util.ToastUtils
import com.facilityone.wireless.a.arch.mvp.IPresent
import com.facilityone.wireless.a.arch.utils.MetroUtils

//包全局toast
typealias toast = com.hjq.toast.ToastUtils
@RouteNode(path = "/constructionHome", desc = "施工监护")
class ConstructionActivity : BaseFragmentActivity<IPresent<*>>(), OnGoFragmentListener {
    private var TOUCH_TIME = 0L
    private var mInstance: EmptyFragment? = null
    override fun getContextViewId(): Int {
        return R.id.construction_main_id
    }

    override fun setRootFragment(): FMFragment? {
        mInstance = EmptyFragment.getInstance(CommonConstant.MESSAGE_CONSTRUCTION)
        mInstance?.setOnGoFragmentListener(this)
        return mInstance
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSwipeBackEnable(false)
        MetroUtils.getParamFromMetro(this)
        toast.setDebugMode(false);
        toast.init(application)
    }

    override fun goFragment(bundle: Bundle) {
        mInstance!!.startWithPop(getInstance(bundle))
    }

    override fun isImmersionBarEnabled(): Boolean {
        return true
    }

    override fun onBackPressedSupport() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            pop()
        } else {
            if (System.currentTimeMillis() - TOUCH_TIME < WAIT_TIME) {
                finish()
            } else {
                TOUCH_TIME = System.currentTimeMillis()
                ToastUtils.showShort("再按一次退出")
            }
        }
    }

    override fun createPresenter(): IPresent<*>? {
        return null
    }

    companion object {
        //再点一次退出程序时间设置
        private const val WAIT_TIME = 2000L
    }
}