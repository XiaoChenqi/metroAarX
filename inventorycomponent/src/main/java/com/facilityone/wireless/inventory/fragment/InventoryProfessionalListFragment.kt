package com.facilityone.wireless.inventory.fragment

import com.facilityone.wireless.a.arch.mvp.BaseFragment
import com.facilityone.wireless.inventory.presenter.InventoryProPresenter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.facilityone.wireless.a.arch.widget.SearchBox.OnSearchBox
import androidx.recyclerview.widget.RecyclerView
import com.facilityone.wireless.a.arch.widget.SearchBox
import android.widget.LinearLayout
import com.facilityone.wireless.inventory.adapter.InventoryProListAdapter
import com.facilityone.wireless.inventory.model.ProfessionalService.InventoryProBean
import com.facilityone.wireless.inventory.R
import android.os.Bundle
import android.os.Parcelable
import com.facilityone.wireless.inventory.fragment.InventoryProfessionalListFragment
import com.facilityone.wireless.basiclib.utils.PinyinUtils
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.ViewGroup
import me.yokeyword.fragmentation.ISupportFragment
import android.text.TextUtils
import android.view.View
import com.blankj.utilcode.util.ToastUtils
import com.facilityone.wireless.inventory.model.ProfessionalService
import kotlin.collections.ArrayList


/**
 * Author：gary
 * Email: xuhaozv@163.com
 * description:
 * Date: 2018/7/17 下午3:00
 */
class InventoryProfessionalListFragment : BaseFragment<InventoryProPresenter?>(),
    BaseQuickAdapter.OnItemClickListener, OnSearchBox {
    private var mRecyclerView: RecyclerView? = null
    private var mSearchBox: SearchBox? = null
    private var mLLTitle: LinearLayout? = null
    private var mViewLine: View? = null
    private var mAdapter: InventoryProListAdapter? = null
    private var mBeanList: MutableList<InventoryProBean>? = null
    private var mShowBeanList: MutableList<InventoryProBean>? = null
    private var mSelectList: LongArray? = null
    private var showTitle = false
    private var mTitle: String? = null


    override fun createPresenter(): InventoryProPresenter {
        return InventoryProPresenter()
    }

    override fun setLayout(): Any {
        return R.layout.fragment_professional_list
    }

    override fun setTitleBar(): Int {
        return R.id.ui_topbar
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        initView()
    }

    private fun initData() {
        mBeanList = ArrayList()
        mShowBeanList = ArrayList()
        val arguments = arguments
        if (arguments != null) {
            showTitle = arguments.getBoolean(SHOW_TITLE, false)
            mTitle = arguments.getString(TITLE_STR, "")
            mSelectList = arguments.getLongArray(SELECT_PRO_LIST);
        }


    }

    fun refreshData(dataList: List<InventoryProBean>?) {
        if (dataList != null) {

            for (i in dataList.indices) {
                val proBean = dataList[i]
                proBean.configNamePinyin = PinyinUtils.ccs2Pinyin(proBean.configName)
                proBean.configNameFirstLetters =
                    PinyinUtils.getPinyinFirstLetters(proBean.configName)

                //检测到就跳出本次循环开启下一次

                mSelectList?.forEach outSide@{
                    if (proBean.id == it) proBean.checked = true;return@outSide
                }




                mBeanList?.add(proBean)
            }




            mShowBeanList!!.addAll(mBeanList!!)
        }
        mAdapter!!.notifyDataSetChanged()

    }

    private fun initView() {
        setTitle(mTitle)
        setRightTextButton("确定", R.id.inventory_pro_confirm_id);
        mRecyclerView = findViewById(R.id.recyclerView)
        mSearchBox = findViewById(R.id.search_box)
        mLLTitle = findViewById(R.id.person_search_rl)
        mViewLine = findViewById(R.id.top_view_line)
        mLLTitle?.setVisibility(if (showTitle) View.VISIBLE else View.GONE)
        mViewLine?.setVisibility(if (showTitle) View.VISIBLE else View.GONE)
        mRecyclerView?.setLayoutManager(LinearLayoutManager(context))
        mAdapter = InventoryProListAdapter(context, showTitle, mShowBeanList)
        mAdapter!!.emptyView = getNoDataView(mRecyclerView?.getParent() as ViewGroup)
        mRecyclerView?.setAdapter(mAdapter)
        mAdapter!!.setOnItemClickListener(this)
        mSearchBox?.setOnSearchBox(this)
        presenter!!.getProfessional()

    }

    override fun onRightTextMenuClick(view: View) {
        var checked = false
        val callBackDataList: ArrayList<InventoryProBean> = arrayListOf()
        for (proBean in mShowBeanList!!) {
            if (proBean.checked) {
                callBackDataList.add(proBean)
                checked = true
            }
        }
        if (!checked) {
            ToastUtils.showShort("没有选择");
            return
        }


        val bundle = Bundle()
        bundle.putParcelableArrayList(PRO_LIST, callBackDataList)
        setFragmentResult(RESULT_OK, bundle)
        pop()
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View, position: Int) {
        mShowBeanList!![position].checked = !mShowBeanList!![position].checked
        mAdapter!!.notifyItemChanged(position)
    }

    override fun onSearchTextChanged(curCharacter: String) {
        var curCharacter = curCharacter
        for (proBean in mBeanList!!) {
            proBean.start = 0
            proBean.end = 0
        }
        if (!TextUtils.isEmpty(curCharacter)) {
            curCharacter = curCharacter.replace(" ", "")
            curCharacter = curCharacter.toLowerCase()
            mShowBeanList!!.clear()
            if (mBeanList != null) {
                for (proBean in mBeanList!!) {
                    if (!TextUtils.isEmpty(proBean.configName) && proBean.configName.toLowerCase()
                            .contains(curCharacter)
                    ) {
                        mShowBeanList!!.add(proBean)
                        val start = proBean.configName.toLowerCase().indexOf(curCharacter)
                        val end = start + curCharacter.length
                        proBean.start = start
                        proBean.end = end
                        continue
                    }
                    if (!TextUtils.isEmpty(proBean.configNameFirstLetters) && proBean.configNameFirstLetters.contains(
                            curCharacter
                        )
                    ) {
                        mShowBeanList!!.add(proBean)
                        val start = proBean.configNameFirstLetters.indexOf(curCharacter)
                        val end = start + curCharacter.length
                        proBean.start = start
                        proBean.end = end
                        continue
                    }
                    if (!TextUtils.isEmpty(proBean.configNamePinyin)) {
                        val strArr = presenter!!.pinyinToStrArr(proBean.configNamePinyin)
                        val start = presenter!!.isMatch(strArr, curCharacter)
                        if (start != -1) {
                            mShowBeanList!!.add(proBean)
                            proBean.start = start
                            if (strArr[start].length >= curCharacter.length) {
                                proBean.end = start + 1
                            } else {
                                proBean.end = presenter!!.endIndex(
                                    strArr, curCharacter.substring(
                                        strArr[start].length
                                    ), start + 1
                                )
                            }
                            continue
                        }
                    }
                }
            }
        } else {
            mShowBeanList!!.clear()
            if (mBeanList != null) {
                mShowBeanList!!.addAll(mBeanList!!)
            }
        }
        mAdapter!!.notifyDataSetChanged()
    }

    companion object {
        //        const val WORKORDER_LABORER_LIST = "workorder_laborer_list"
        const val PRO_LIST = "inventory_pro_list"
        const val SELECT_PRO_LIST = "select_inventory_pro_list"
        private const val TITLE_STR = "title_str"
        private const val SHOW_TITLE = "show_title"

        @JvmStatic
        val instance: InventoryProfessionalListFragment
            get() = getInstance(null, null, true)

        //
        @JvmStatic
        fun getInstance(
            bs: List<Long>?,
            title: String?,
            showTitle: Boolean
        ): InventoryProfessionalListFragment {
            val bundle = Bundle()
            bundle.putLongArray(SELECT_PRO_LIST, bs?.toLongArray())
            bundle.putString(TITLE_STR, title)
            bundle.putBoolean(SHOW_TITLE, showTitle)
            val instance = InventoryProfessionalListFragment()
            instance.arguments = bundle
            return instance
        }
    }
}