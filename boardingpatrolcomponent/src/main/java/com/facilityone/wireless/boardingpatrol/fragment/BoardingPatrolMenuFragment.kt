package com.facilityone.wireless.boardingpatrol.fragment

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
import com.facilityone.wireless.boardingpatrol.moudle.BoardingPatrolConstant.MENU_CHECK
import com.facilityone.wireless.boardingpatrol.moudle.BoardingPatrolConstant.MENU_QUERY
import com.facilityone.wireless.boardingpatrol.presenter.BoardingMenuPresenter
import com.facilityone.wireless.boardingpatrol.R
import com.facilityone.wireless.boardingpatrol.databinding.BoardingpatrolmenuBinding
import java.util.ArrayList

/**
 *  @Author: Karelie
 *  @Method：RidePatrolMenuFragment
 *  @Date：2022/2/11 17:56
 *  @Description：登乘巡查菜单界面
 */
class BoardingPatrolMenuFragment : BaseFragment<BoardingMenuPresenter>(),
    BaseQuickAdapter.OnItemClickListener {
    lateinit var binding : BoardingpatrolmenuBinding
    private var mFunctionAdapter: FunctionAdapter? = null
    private var mFunctionBeanList: MutableList<FunctionBean?>? = null

    override fun setLayout(): Any {
        return R.layout.boardingpatrolmenu
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
//            inflater.inflate(setLayout() as Int, container, false)
            binding= BoardingpatrolmenuBinding.inflate(inflater,container,false)
            binding.root
        } else if (setLayout() is View) {
            setLayout() as View
        } else {
            throw ClassCastException("type of setLayout() must be layout resId or view")
        }
        return mRootView
    }

    override fun createPresenter(): BoardingMenuPresenter {
        return BoardingMenuPresenter()
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

    override fun onDestroy() {
        super.onDestroy()
        CollectUtils.targetPageEnd(this, "ridepatrol")
    }

    override fun leftBackListener() {
        getActivity()!!.finish()
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

        setTitle("登乘巡查")
    }

    companion object {

        @JvmStatic
        fun getInstance(bundle : Bundle): BoardingPatrolMenuFragment {
            val fragment = BoardingPatrolMenuFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onItemClick(p0: BaseQuickAdapter<*, *>?, p1: View?, postion: Int) {
        val functionBean:FunctionBean? = mFunctionBeanList?.get(postion)
        var baseFragment: BaseFragment<*>? = null
        when(functionBean?.name){
            MENU_CHECK -> baseFragment = BoardingCreateFragment.getInstance()
            MENU_QUERY -> baseFragment = BoardingQueryFragment.getInstance()
        }
        start(baseFragment)
    }



}