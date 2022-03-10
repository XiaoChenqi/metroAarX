package com.facilityone.wireless.construction.serviceimpl

import android.os.Bundle
import com.facilityone.wireless.a.arch.mvp.BaseFragment
import com.facilityone.wireless.a.arch.mvp.IPresent
import com.facilityone.wireless.componentservice.construction.ConstructionService
import com.facilityone.wireless.construction.fragment.ConstructionMenuFragment

class ConstructionServiceImpl :
    ConstructionService {
    override fun getFragment(bundle: Bundle?): BaseFragment<out IPresent<*>>? {
        return bundle?.let { ConstructionMenuFragment.getInstance(it) }
    }

}