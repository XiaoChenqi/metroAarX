package com.facilityone.wireless.boardingpatrol.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.facilityone.wireless.a.arch.ec.module.Page
import com.facilityone.wireless.a.arch.mvp.BaseFragment
import com.facilityone.wireless.boardingpatrol.presenter.BoardingQueryPresenter
import com.facilityone.wireless.boardingpatrol.R
import com.facilityone.wireless.boardingpatrol.adapter.BoardingQueryAdapter
import com.facilityone.wireless.boardingpatrol.databinding.BoardingpatrolqueryBinding
import com.facilityone.wireless.boardingpatrol.moudle.BoardingService
import com.facilityone.wireless.boardingpatrol.toast
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener

/**
 *  @Author: Karelie
 *  @Method：RideQueryFragment
 *  @Date：2022/2/11 17:58
 *  @Description：巡查记录
 */
class BoardingQueryFragment :
    BaseFragment<BoardingQueryPresenter>(),
    BaseQuickAdapter.OnItemClickListener,
    OnRefreshLoadMoreListener
{
    lateinit var binding: BoardingpatrolqueryBinding
    var adapter: BoardingQueryAdapter? = null
    var queryList: MutableList<BoardingService.BoardingQueryContents>? = null
    var page:Page? = null
    override fun setLayout(): Any {
        return R.layout.boardingpatrolquery
    }

    override fun setTitleBar(): Int {
        return R.id.ui_topbar
    }

    override fun createPresenter(): BoardingQueryPresenter {
        return BoardingQueryPresenter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mRootView = if (setLayout() is Int) {
            binding= BoardingpatrolqueryBinding.inflate(inflater,container,false)
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
    }

    private fun initData() {
        setTitle(getString(R.string.query_title))
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        queryList = ArrayList()
        adapter = BoardingQueryAdapter()
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
        presenter.getBoardingQueyList(page!!,true)
    }

    fun queryListSuccess(enity: BoardingService.BoardingQueryEnity?,refresh:Boolean){
        page = enity?.page
        if (refresh) {
            adapter?.setNewData(enity?.contents)
            binding.refreshLayout.finishRefresh()
        } else {
            enity?.contents?.let { adapter?.addData(it) }
            binding.refreshLayout.finishLoadMore()
        }
        if (adapter?.getData()?.size == 0) {
            adapter?.setEmptyView(getNoDataView(binding.refreshLayout))
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

        page?.nextPage()?.let { presenter.getBoardingQueyList(it,false) }
    }

    fun queryListError(){
        adapter?.setEmptyView(getErrorView(binding.refreshLayout))
        binding.refreshLayout.finishRefresh(false)
        binding.refreshLayout.finishLoadMore(false)
    }

    override fun onItemClick(help: BaseQuickAdapter<*, *>?, v: View?, position: Int) {
        val enity  = adapter!!.data.get(position)
        enity?.apply {
            start(BoardingInforFragment.getInstance(this.inspectionRegistrationId!!,this.startingStation!!,this.endingStation!!,this.interval!!))
        }
    }

    companion object {
        @JvmStatic
        fun getInstance(): BoardingQueryFragment {
            val fragment = BoardingQueryFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }


}