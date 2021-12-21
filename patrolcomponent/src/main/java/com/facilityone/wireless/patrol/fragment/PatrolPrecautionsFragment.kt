package com.facilityone.wireless.patrol.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.facilityone.wireless.a.arch.mvp.BaseFragment
import com.facilityone.wireless.patrol.R
import com.facilityone.wireless.patrol.databinding.FragmentPatrolPrecautionsBinding
import com.facilityone.wireless.patrol.presenter.PatrolPrecautionsPresenter

/**
 * @Creator:Karelie
 * @Data: 2021/12/9
 * @TIME: 12:10
 * @Introduce: 巡检模块注意事项
**/
class PatrolPrecautionsFragment : BaseFragment<PatrolPrecautionsPresenter>() {
    lateinit var binding : FragmentPatrolPrecautionsBinding
    private var infor :String?=""
    override fun setLayout(): Any {
        return R.layout.fragment_patrol_precautions
    }

    override fun createPresenter(): PatrolPrecautionsPresenter {
        return PatrolPrecautionsPresenter()
    }

    override fun setTitleBar(): Int {
        return R.id.ui_topbar
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mRootView = if (setLayout() is Int) {
            binding= FragmentPatrolPrecautionsBinding.inflate(inflater,container,false)
            binding.root
        } else if (setLayout() is View) {
            setLayout() as View
        } else {
            throw ClassCastException("type of setLayout() must be layout resId or view")
        }
        return mRootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
    }

    private fun initView() {
        binding.envDesc.canInput(false)
        binding.envDesc.setInputDisp(false)
        setTitle(getString(R.string.patrol_task_type_patrol_precautions))

    }

    private fun initData() {
        val bundle = arguments
        if (bundle != null){
            infor = bundle.getString(PRECAUTIONS)
        }
        binding.envDesc.desc = infor+""


    }
    companion object {
        private val PRECAUTIONS = "precautions"
        @JvmStatic
        fun getInstance(precautions: String): PatrolPrecautionsFragment {
            val fragment = PatrolPrecautionsFragment()
            val bundle = Bundle()
            bundle.putString(PRECAUTIONS, precautions)
            fragment.arguments = bundle
            return fragment
        }
    }
}