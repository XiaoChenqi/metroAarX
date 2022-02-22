package com.facilityone.wireless.boardingpatrol.serviceimpl

import android.os.Bundle
import com.facilityone.wireless.a.arch.mvp.BaseFragment
import com.facilityone.wireless.a.arch.mvp.IPresent
import com.facilityone.wireless.componentservice.boardingpatrol.BoardingPatrolService
import com.facilityone.wireless.boardingpatrol.fragment.BoardingPatrolMenuFragment

class BoardingPatrolServiceImpl :
    BoardingPatrolService {
    override fun getFragment(bundle: Bundle?): BaseFragment<out IPresent<*>>? {
        return bundle?.let { BoardingPatrolMenuFragment.getInstance(it) }
    }

}