package com.facilityone.wireless.construction.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.facilityone.wireless.a.arch.ec.module.Page
import com.facilityone.wireless.a.arch.mvp.BaseFragment
import com.facilityone.wireless.construction.R
import com.facilityone.wireless.construction.adapter.ElectronicLedgerAdapter
import com.facilityone.wireless.construction.databinding.FragmentElectronicLedgerBinding
import com.facilityone.wireless.construction.databinding.LayoutConstructionregistBinding
import com.facilityone.wireless.construction.module.ConstructionService
import com.facilityone.wireless.construction.module.SelectorModel
import com.facilityone.wireless.construction.module.TaskContent
import com.facilityone.wireless.construction.module.TemplateModel
import com.facilityone.wireless.construction.presenter.ConstructionResultPresenter
import com.facilityone.wireless.construction.toast
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener
import java.util.ArrayList

class ConstructionResultFragment: BaseFragment<ConstructionResultPresenter?>(),
    BaseQuickAdapter.OnItemClickListener, OnRefreshLoadMoreListener {
    lateinit var binding:FragmentElectronicLedgerBinding
    private var mType: Int? = null
    private var localWoId: Long? = null //当前选中的Id
    private var mCode: String? = null
    private var mTemplateId: Long? = null
    private var mPage: Page? = null
    var mElAdapter: ElectronicLedgerAdapter? = null
    //当前模板任务数据
    var mTemplateModel: TemplateModel? = null
    var mStartTime: Long? = null

    override fun setLayout(): Any {
        return R.layout.fragment_electronic_ledger
    }

    override fun setTitleBar(): Int {
        return R.id.ui_topbar
    }

    override fun createPresenter(): ConstructionResultPresenter? {
        return ConstructionResultPresenter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mRootView = if (setLayout() is Int) {
            binding= FragmentElectronicLedgerBinding.inflate(inflater,container,false)
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
        setTitle("施工监护项目")
        binding.apply {
            recyclerView!!.layoutManager = LinearLayoutManager(context)
            val datas: List<ConstructionService.ElectronicLedgerEntity> = ArrayList()
            mElAdapter = ElectronicLedgerAdapter(datas)
            mElAdapter!!.tempRecycleView = recyclerView
            recyclerView.setItemViewCacheSize(200)
            recyclerView.adapter = mElAdapter
        }
    }

    private fun initData() {
        val bundle = arguments
        val tempId = bundle?.getLong(TEMPLATEID)
        if (tempId != null) {
            presenter?.getQueryInfor(tempId)
        }
    }

    fun refreshInfor(data: ConstructionService.ConstructionInforEnity){
        if (!data.tasks.isNullOrEmpty()){
            val taskData: MutableList<ConstructionService.ElectronicLedgerEntity> = arrayListOf()


            for (item in data.tasks!!) {
                //获取任务标题
                val headItem = ConstructionService.ElectronicLedgerEntity(1, item.taskName)
                taskData.add(headItem)
                //获取任务内容
                for (subItem in item.contents!!) {
                    //填充列表内容
                    val itemContent = chooseItemType(item.taskId!!, subItem)
                    if (itemContent != null) {
                        taskData.add(itemContent)
                    }
                }


            }
            mElAdapter!!.setNewData(taskData);


        }else{
            refreshError()
        }
    }



    //选择列表项内容
    private fun chooseItemType(
        taskId: Long,
        taskContent: TaskContent
    ): ConstructionService.ElectronicLedgerEntity? {
        return when (taskContent.type) {
            TaskContent.CHOICE -> {
                ConstructionService.ElectronicLedgerEntity(
                    taskId,
                    taskContent.contentId,
                    6,
                    SelectorModel(taskContent.content, 0, taskContent.selectValues,taskContent.selects)
                )
            }
            TaskContent.INPUT -> {
                ConstructionService.ElectronicLedgerEntity(
                    taskId,
                    taskContent.contentId,
                    6,
                    SelectorModel(taskContent.content, 0, taskContent.selectValues,null)
                )
            }
            else -> {
                null
            }

        }
    }


    fun refreshError(){
        toast.show("数据异常")
        pop()
    }


    //刷新页面
    private fun onRefresh() {
        if (mPage == null) {
            mPage = Page()
        }
        mPage!!.reset()
    }

    override fun onItemClick(p0: BaseQuickAdapter<*, *>?, p1: View?, p2: Int) {

    }

    override fun onRefresh(p0: RefreshLayout) {
        onRefresh()
    }

    override fun onLoadMore(p0: RefreshLayout) {

    }

    companion object {
        private val TEMPLATEID = "templateid"

        @JvmStatic
        fun getInstance(
            templateId: Long
        ): ConstructionResultFragment {
            val bundle = Bundle()
            val instance = ConstructionResultFragment()
            bundle.putLong(TEMPLATEID, templateId)
            instance.arguments = bundle
            return instance
        }

    }
}