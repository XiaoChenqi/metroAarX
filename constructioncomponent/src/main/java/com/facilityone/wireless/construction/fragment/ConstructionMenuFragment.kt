package com.facilityone.wireless.construction.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.facilityone.wireless.a.arch.ec.adapter.FunctionAdapter
import com.facilityone.wireless.a.arch.ec.collect.CollectUtils
import com.facilityone.wireless.a.arch.ec.module.FunctionService
import com.facilityone.wireless.a.arch.ec.module.FunctionService.FunctionBean
import com.facilityone.wireless.a.arch.ec.module.IService
import com.facilityone.wireless.a.arch.mvp.BaseFragment
import com.facilityone.wireless.construction.presenter.ConstructionMenuPresenter


import com.facilityone.wireless.construction.R
import com.facilityone.wireless.construction.databinding.FragmentConstructionMenuBinding
import com.facilityone.wireless.construction.module.ConstructionConstant.MENU_QUERY
import com.facilityone.wireless.construction.module.ConstructionConstant.MENU_REGIST
import java.util.ArrayList

/**
 *  @Author: Karelie
 *  @Method：ConstructionMenuFragment
 *  @Date：2022/2/11 17:56
 *  @Description：施工监护模块
 */
class ConstructionMenuFragment : BaseFragment<ConstructionMenuPresenter>(),
    BaseQuickAdapter.OnItemClickListener {
    lateinit var binding : FragmentConstructionMenuBinding
    private var mFunctionAdapter: FunctionAdapter? = null
    private var mFunctionBeanList: MutableList<FunctionBean?>? = null

    override fun setLayout(): Any {
        return R.layout.fragment_construction_menu
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
            binding= FragmentConstructionMenuBinding.inflate(inflater,container,false)
            binding.root
        } else if (setLayout() is View) {
            setLayout() as View
        } else {
            throw ClassCastException("type of setLayout() must be layout resId or view")
        }
        return mRootView
    }

    override fun createPresenter(): ConstructionMenuPresenter {
        return ConstructionMenuPresenter()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
    }

    override fun onStart() {
        super.onStart()
        CollectUtils.targetPageStart(this, "ridepatrol")
    }

    override fun leftBackListener() {
        getActivity()!!.finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        CollectUtils.targetPageEnd(this, "ridepatrol")
    }

    private fun initData() {
        mFunctionBeanList = mutableListOf()
        val arguments = arguments
        if (arguments != null) {
            val bean =
                arguments.getSerializable(IService.FRAGMENT_CHILD_KEY) as ArrayList<FunctionBean>?
            if (bean != null) {
                mFunctionBeanList?.addAll(bean)
            } else {
                ToastUtils.showShort("暂无功能")
            }
            val runAlone = arguments.getBoolean(IService.COMPONENT_RUNALONE, false)
            if (runAlone) {
                setSwipeBackEnable(false)
            }
        }
    }

    fun getFunctionBeanList(): List<FunctionBean?>? {
        return if (mFunctionBeanList == null) {
            mutableListOf()
        } else mFunctionBeanList
    }

    override fun onSupportVisible() {
        super.onSupportVisible()
        presenter.getUndoNumber(FunctionService.UNDO_TYPE_RIDEPATROL)
    }

    fun updateFunction(functionBeanList: List<FunctionBean?>?) {
        if (functionBeanList != null){
            mFunctionAdapter?.replaceData(functionBeanList)
        }

    }

    private fun initView() {
        //初始化
        binding.recyclerView.layoutManager = GridLayoutManager(context,FunctionService.COUNT)
        mFunctionAdapter = FunctionAdapter(mFunctionBeanList)
        binding.recyclerView.adapter = mFunctionAdapter
        mFunctionAdapter?.onItemClickListener = this

        setTitle("现场施工监护")
    }

    companion object {

        @JvmStatic
        fun getInstance(bundle : Bundle): ConstructionMenuFragment {
            val fragment = ConstructionMenuFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onItemClick(p0: BaseQuickAdapter<*, *>?, p1: View?, postion: Int) {
        val functionBean:FunctionBean? = mFunctionBeanList?.get(postion)
        var baseFragment: BaseFragment<*>? = null
        when(functionBean?.name){
            getString(R.string.construction_regist) -> baseFragment = ConstructionRegistFragment.getInstance()
            getString(R.string.construction_query) -> baseFragment = ConstructionQueryFragment.getInstance()
        }
        if (baseFragment != null){
            start(baseFragment)
        }
    }



}