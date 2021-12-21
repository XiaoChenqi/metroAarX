package com.facilityone.wireless.a.arch.ec.selectdata

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.facilityone.wireless.a.arch.R
import com.facilityone.wireless.a.arch.databinding.FragmentSelectNewBinding
import com.facilityone.wireless.a.arch.ec.adapter.GridImageAdapter
import com.facilityone.wireless.a.arch.mvp.BaseFragment
import me.yokeyword.fragmentation.SwipeBackLayout

/**
 * @Creator:Karelie
 * @Data: 2021/11/10
 * @TIME: 9:08
 * @Introduce: 公共层级选择界面
**/
class SelectNewFragment : BaseFragment<SelectNewPresenter>(),BaseQuickAdapter.OnItemClickListener {
    lateinit var binding: FragmentSelectNewBinding
    private var dataType :  Int? = null
    private var mSelectList: MutableList<SelectNewService.SelectNewResp>? = null
    private var mAdapter: SelectNewAdapter? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
        initRecyclerView()
        initOnClick()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mRootView = if (setLayout() is Int) {
            binding= FragmentSelectNewBinding.inflate(inflater,container,false)
            binding.root
        } else if (setLayout() is View) {
            setLayout() as View
        } else {
            throw ClassCastException("type of setLayout() must be layout resId or view")
        }
        //设置可侧滑返回的区域大小
        swipeBackLayout.setEdgeLevel(SwipeBackLayout.EdgeLevel.MIN)
        return attachToSwipeBack(mRootView)
    }

    private fun initOnClick() {
        binding.searchBox.setOnSearchBox {
            if (it!= null){
                ToastUtils.showShort(it)
            }
        }
    }


    private fun initView() {
        val bundle = arguments
        if (bundle != null) {
            if (bundle.getString(TITLE_NAME)!=null){
                setTitle(bundle.getString(TITLE_NAME))
            }
            if (bundle.getInt(DATA_TYPE)!= null){
                dataType = bundle.getInt(DATA_TYPE)
            }
        }
    }

    private fun initData() {
        if (dataType != null){
            presenter!!.getList(dataType!!)
        }
    }

    private fun initRecyclerView() {
        mSelectList = ArrayList()
        mAdapter = SelectNewAdapter(mSelectList)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = mAdapter

    }

    override fun setLayout(): Any {
        return R.layout.fragment_select_new
    }

    override fun setTitleBar(): Int {
        return R.id.ui_topbar
    }

    override fun createPresenter(): SelectNewPresenter {
        return SelectNewPresenter()
    }

     fun refreshList(){
         //TODO 接口回调渲染
         ToastUtils.showShort("是的我开始渲染了")
    }


    override fun onItemClick(p0: BaseQuickAdapter<*, *>?, p1: View?, p2: Int) {
    }

    companion object{
        private val TITLE_NAME = "title_name"
        private val DATA_TYPE = "data_type"

        @JvmStatic
        fun getInstance(title : String , dataType:Int): SelectNewFragment {
            val fragment = SelectNewFragment()
            val bundle = Bundle()
            bundle.putString(TITLE_NAME, title)
            bundle.putInt(DATA_TYPE, dataType)
            fragment.arguments = bundle
            return fragment
        }
    }
}