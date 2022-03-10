package com.facilityone.wireless.cons

import com.facilityone.wireless.basiclib.utils.onClick
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.TimeUtils
import com.facilityone.wireless.a.arch.ec.utils.SPKey
import com.facilityone.wireless.a.arch.mvp.BaseFragment
import com.facilityone.wireless.basiclib.utils.DateUtils
import com.facilityone.wireless.construction.R
import com.facilityone.wireless.construction.databinding.LayoutConstructioninforBinding
import com.facilityone.wireless.construction.databinding.LayoutConstructionregistBinding
import com.facilityone.wireless.construction.fragment.ConstructionResultFragment
import com.facilityone.wireless.construction.module.ConstructionService
import com.facilityone.wireless.construction.presenter.ConstructionInforPresenter
import com.facilityone.wireless.construction.presenter.ConstructionRegistPresenter
import com.facilityone.wireless.construction.toast
import java.nio.BufferUnderflowException
import java.util.*

/**
 *  @Author: Karelie
 *  @Method：ConstructionInforFragment
 *  @Date：2022/3/3 10:57
 *  @Description：施工监护详情
 */
class ConstructionInforFragment : BaseFragment<ConstructionInforPresenter>() {
    lateinit var binding: LayoutConstructioninforBinding
    var tempId:Long?= null
    override fun setLayout(): Any {
        return R.layout.layout_constructioninfor
    }

    override fun setTitleBar(): Int {
        return R.id.ui_topbar
    }

    override fun createPresenter(): ConstructionInforPresenter {
        return ConstructionInforPresenter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mRootView = if (setLayout() is Int) {
            binding= LayoutConstructioninforBinding.inflate(inflater,container,false)
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
        initOnClick()
    }

    private fun initView() {
        setTitle(getString(R.string.construction_query))
        binding.apply {
            enRemark.showHint(false)
            enRemark.canInput(false)
            enRemark.setInputDisp(false)
        }
    }


    /**
     *  @Author: Karelie
     *  @Method：refreshInfor
     *  @Description：刷新监控记录
     */
    fun refreshInfor(date: ConstructionService.ConstructionInforEnity){
        binding.apply {
            val registor:String = SPUtils.getInstance(SPKey.SP_MODEL_USER).getString(SPKey.EM_NAME)
            if (date.registDate != null){
                tvRegistor.text = registor+"填写于"+ TimeUtils.date2String(Date(date.registDate), DateUtils.SIMPLE_DATE_FORMAT_ALL)
            }
            date.construction?.apply {
                tvGuardian.text = guardian+""
                if (guardianStartTime != null){
                    tvTutelageStart.text = TimeUtils.date2String(Date(guardianStartTime), DateUtils.SIMPLE_DATE_FORMAT_ALL)+""
                }
                if (guardianEndTime != null){
                    tvTutelageEnd.text = TimeUtils.date2String(Date(guardianEndTime), DateUtils.SIMPLE_DATE_FORMAT_ALL)+""
                }
                tvConstProject.text = guardianName+""
                if (constructionDate != null){
                    tvConstData.text = TimeUtils.date2String(Date(constructionDate), DateUtils.SIMPLE_DATE_FORMAT_YMD)+""
                }
                tvConstSite.text = constructionSite+""
                tvConstUnit.text = constructionUnit+""
                tvConstLeader.text = constructionPrincipal+""
                tvAllConstData.text = principal+""

                if (notePerson.isBlank()){
                    llRemark.visibility = View.GONE
                }else{
                    llRemark.visibility = View.VISIBLE
                    enRemark.desc = notePerson+""
                }

            }

            if (date.tasks.isNullOrEmpty()){
                llListIn.visibility = View.GONE
            }else{
                llListIn.visibility = View.VISIBLE
            }
        }
    }

    fun refreshError(){
        toast.show("数据异常")
    }

    private fun initOnClick() {
        binding.apply {
            llListIn.onClick {
                if (tempId != null){
                    start(ConstructionResultFragment.getInstance(tempId!!))
                }
            }
        }
    }

    private fun initData() {
        val bundle = arguments
        if (bundle != null){
            val temId = bundle.getLong(TEMPLATEID)
            presenter.getQueryInfor(temId)
            tempId =temId
        }

    }

    override fun onFragmentResult(requestCode: Int, resultCode: Int, data: Bundle?) {
        super.onFragmentResult(requestCode, resultCode, data)
    }


    companion object {
        private val TEMPLATEID = "templateid"
        @JvmStatic
        fun getInstance(templateId:Long): ConstructionInforFragment {
            val fragment = ConstructionInforFragment()
            val bundle = Bundle()
            bundle.putLong(TEMPLATEID,templateId)
            fragment.arguments = bundle
            return fragment
        }
    }
}