package com.facilityone.wireless.construction.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.facilityone.wireless.a.arch.ec.module.Page
import com.facilityone.wireless.a.arch.mvp.BaseFragment
import com.facilityone.wireless.a.arch.mvp.BasePresenter
import com.facilityone.wireless.cons.ConstructionInforFragment
import com.facilityone.wireless.construction.R
import com.facilityone.wireless.construction.adapter.ConstructionQueryAdapter
import com.facilityone.wireless.construction.databinding.LayoutConstructionqueryBinding
import com.facilityone.wireless.construction.databinding.LayoutConstructionregistBinding
import com.facilityone.wireless.construction.module.ConstructionService
import com.facilityone.wireless.construction.presenter.ConstructionQueryPresenter
import com.facilityone.wireless.construction.presenter.ConstructionRegistPresenter
import com.facilityone.wireless.construction.toast
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener

/**
 *  @Author: Karelie
 *  @Method：ConstructionQueryFragment
 *  @Date：2022/3/3 10:56
 *  @Description：施工监护纪录
 */
class ConstructionQueryFragment : BaseFragment<ConstructionQueryPresenter>(),
    BaseQuickAdapter.OnItemClickListener,
    OnRefreshLoadMoreListener
{
    lateinit var binding: LayoutConstructionqueryBinding
    var adapter:ConstructionQueryAdapter?= null
    var queryList: MutableList<ConstructionService.ConstructionQueryContents>? = null
    var page: Page? = null
    override fun setLayout(): Any {
        return R.layout.layout_constructionquery
    }

    override fun setTitleBar(): Int {
        return R.id.ui_topbar
    }

    override fun createPresenter(): ConstructionQueryPresenter {
        return ConstructionQueryPresenter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mRootView = if (setLayout() is Int) {
            binding= LayoutConstructionqueryBinding.inflate(inflater,container,false)
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
        initData()
        initView()
    }

    private fun initView() {
        setTitle(getString(R.string.construction_query))
    }

    private fun initData() {
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        queryList = ArrayList()
        adapter = ConstructionQueryAdapter()
        binding.recyclerView.adapter = adapter
        adapter?.onItemClickListener = this
        onRefresh()
    }

    fun onRefresh(){
        adapter?.setEmptyView(getLoadingView(binding.refreshLayout))
        if (page == null){
            page = Page()
        }
        page?.reset()
        presenter.getConsQueyList(page!!,true)
    }

    fun querySuccess(enity:ConstructionService.ConstructionQueryEnity,refresh: Boolean){
        page = enity.page
        if (refresh) {
            adapter?.setNewData(enity.contents)
            binding.refreshLayout.finishRefresh()
        } else {
            enity.contents?.let { adapter?.addData(it) }
            binding.refreshLayout.finishLoadMore()
        }
        if (adapter?.getData()?.size == 0) {
            adapter?.setEmptyView(getNoDataView(binding.refreshLayout))
        }
    }

    fun queryError(){
        adapter?.setEmptyView(getErrorView(binding.refreshLayout))
        binding.refreshLayout.finishRefresh(false)
        binding.refreshLayout.finishLoadMore(false)
    }


    override fun onFragmentResult(requestCode: Int, resultCode: Int, data: Bundle?) {
        super.onFragmentResult(requestCode, resultCode, data)
    }


    companion object {
        private val REQUEST_LOCATION_START = 20001
        private val REQUEST_LOCATION_END = 20002
        @JvmStatic
        fun getInstance(): ConstructionQueryFragment {
            val fragment = ConstructionQueryFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onItemClick(help: BaseQuickAdapter<*, *>?, view: View?, index: Int) {
        val templateId:Long? = adapter?.data?.get(index)?.id
        if (templateId != null){
            start(ConstructionInforFragment.getInstance(templateId))
        }else{
            toast.show("数据异常")
        }
    }

    override fun onRefresh(p0: RefreshLayout) {
        onRefresh()
    }

    override fun onLoadMore(p0: RefreshLayout) {
        if (page == null || !(page?.haveNext())!!) {
            binding.refreshLayout.finishLoadMore()
            toast.show("暂无更多数据")
            return
        }
        page?.nextPage()?.let { presenter.getConsQueyList(it,false) }
    }
}