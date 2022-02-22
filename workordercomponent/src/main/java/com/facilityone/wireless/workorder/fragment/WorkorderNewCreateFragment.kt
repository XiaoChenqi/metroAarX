package com.facilityone.wireless.workorder.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.facilityone.wireless.a.arch.ec.module.ISelectDataService
import com.facilityone.wireless.a.arch.ec.module.ISelectDataService.*
import com.facilityone.wireless.a.arch.ec.module.LocationBean
import com.facilityone.wireless.a.arch.ec.module.SelectDataBean
import com.facilityone.wireless.a.arch.ec.selectdata.SelectDataFragment
import com.facilityone.wireless.a.arch.mvp.BaseFragment
import com.facilityone.wireless.workorder.R
import com.facilityone.wireless.workorder.adapter.WorkOrderNewOrderAdapter
import com.facilityone.wireless.workorder.databinding.FragmentPrecautionsBinding
import com.facilityone.wireless.workorder.databinding.FragmentWorkordernewcreateBinding
import com.facilityone.wireless.workorder.module.WorkorderHelper
import com.facilityone.wireless.workorder.module.WorkorderService
import com.facilityone.wireless.workorder.presenter.WorkorderNewCreatePresenter
import com.luck.picture.lib.tools.ToastManage

/**
 *  @Author: Karelie
 *  @ClassName：WorkorderNewCreateFragment
 *  @Date：2022/1/20 9:51
 *  @Description：新派工单(转派)
 */
class WorkorderNewCreateFragment : BaseFragment<WorkorderNewCreatePresenter>(),
    BaseQuickAdapter.OnItemChildClickListener {
    lateinit var binding: FragmentWorkordernewcreateBinding
    override fun setLayout(): Any {
        return R.layout.fragment_workordernewcreate
    }

    override fun createPresenter(): WorkorderNewCreatePresenter {
        return WorkorderNewCreatePresenter()
    }

    override fun setTitleBar(): Int {
        return R.id.ui_topbar
    }

    var newOrderAdapter: WorkOrderNewOrderAdapter? = null
    var newOrderList: ArrayList<WorkorderService.newOrderEnity>? = null

    var mServiceTypeSelectData : SelectDataBean? = null  //服务类型
    var orderTypeId : Long? = null  //工单类型
    var location : LocationBean? = null  //位置
    var mDepSelectData : SelectDataBean? = null //部门
    var woId:Long? = null //工单ID
    var orderName:String? = null //工单类型名称
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mRootView = if (setLayout() is Int) {
            binding = FragmentWorkordernewcreateBinding.inflate(inflater, container, false)
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
        initClick()
    }

    fun initView() {
        setTitle(getString(R.string.workorder_newcreatr_title))
        setRightTextButton(getString(R.string.workorder_submit), R.id.workorder_newcreate_id)
    }

    fun initData() {
        val bundle = arguments
        if (bundle != null){
            mServiceTypeSelectData =bundle.getParcelable(NEWORDER_SERVICETYPE)
            orderTypeId = bundle.getLong(NEWORDER_ORDERTY)
            location = bundle.getParcelable(NEWORDER_LOCATION)
            mDepSelectData = bundle.getParcelable(NEWORDER_DEPSELECT)
            binding.civContact.inputText = bundle.getString(NEWORDER_APPLICANTNAME)
            binding.civTel.inputText = bundle.getString(NEWORDER_APPLICANTPHONE)
            binding.civLocation.tipText = bundle.getString(NEWORDER_LOCATIONNAME)
            woId = bundle.getLong(NEWORDER_WOID,-1)
            if (mDepSelectData != null){
                binding.civDep.tipText = mDepSelectData?.fullName
            }
            when(orderTypeId){
                0L->{ orderName="自检" }
                1L->{ orderName="纠正性维护" }
                3L->{ orderName="混合" }
            }
            binding.envDesc.desc = bundle.getString(NEWORDER_DESC)

        }
        binding.civContact.canInput(false)
        binding.civTel.canInput(false)
        binding.civDep.canInput(false)
        binding.civLocation.canInput(false)
        binding.envDesc.setInputDisp(false)
        binding.envDesc.canInput(false)



        binding.rvNeworder.layoutManager = LinearLayoutManager(context)
        newOrderList = ArrayList()
        newOrderAdapter = WorkOrderNewOrderAdapter(newOrderList!!)
        newOrderList?.add(addNewItem())
        binding.rvNeworder.adapter = newOrderAdapter
        newOrderAdapter?.setNewData(newOrderList)
        newOrderAdapter?.onItemChildClickListener = this
    }

    fun initClick() {

    }

    fun popForResult(){
        val bundle = Bundle()
        setFragmentResult(REFRESH_POP, bundle)
        pop()
    }

    fun addNewItem(): WorkorderService.newOrderEnity {
        var data = WorkorderService.newOrderEnity()
        data.type = orderTypeId
        data.typeName = orderName
        return data
    }

    override fun onRightTextMenuClick(view: View?) {
        super.onRightTextMenuClick(view)
        var view = view?.id
        when (view) {
            R.id.workorder_newcreate_id -> judgeReq()
        }
    }


    /**
    *  @Author: Karelie
    *  @Method：orderUpload
    *  @Description：提交工单
    */
    fun orderUpload() {
        var req = WorkorderService.newOrderReq()
        req?.woId = woId
        var reqList = ArrayList<WorkorderService.newOrderItemEnity>()
        newOrderList!!.forEach {
            var reqItem = WorkorderService.newOrderItemEnity()
            reqItem?.orderType = it.type
            reqItem?.serviceTypeId = it.serviceTypeId
            reqItem?.priorityId = it.priorityId
            reqItem?.flowId = it.flowId
            if (reqItem != null) {
                reqList?.add(reqItem)
            }
        }
        req?.workOrderResult = reqList
        req?.orderType = orderTypeId?.toInt()
        if (req != null) {
            presenter.workOrderUpload(req)
        }
    }

    /**
    *  @Author: Karelie
    *  @Method：judgeReq
    *  @Description：校验请求体
    */
    fun judgeReq(){
        if (presenter.hasOmission(newOrderList)){ //先去校验底部新派工单中是否遗漏的内容
            if (presenter.checkList(newOrderList)){ //判断是否有重复的内容
                orderUpload()
            }else{
                ToastUtils.showShort("存在重复的服务类型和优先级，请确认！")
            }
        }else{
            ToastUtils.showShort("存在未填写的服务类型或优先级，请补充完整！")
        }
    }



    companion object {
        private val REQUEST_PRIORITY = 20005
        private val REQUEST_WORKORDER_TYPE = 20004
        private val REQUEST_SERVICE_TYPE = 20003
        private val REFRESH = 500001 // 界面刷新
        private val REFRESH_POP = 500009 // 跳回列表
        @JvmStatic
        fun getInstance(
            mServiceTypeSelectData:SelectDataBean, //服务类型
            woId:Long, //工单Id
            applicantName:String, //创建人
            applicantPhone:String, //创建人
            orderTypeId:Long, //工单类型
            location:LocationBean,  //位置
            locationName:String, //位置名称
            mDepSelectData:SelectDataBean, //部门
            desc:String //问题描述
                        ): WorkorderNewCreateFragment {
            val fragment = WorkorderNewCreateFragment()
            val bundle = Bundle()
            bundle.putParcelable(NEWORDER_SERVICETYPE,mServiceTypeSelectData)
            bundle.putLong(NEWORDER_ORDERTY,orderTypeId)
            bundle.putParcelable(NEWORDER_LOCATION,location)
            bundle.putParcelable(NEWORDER_DEPSELECT,mDepSelectData)
            bundle.putString(NEWORDER_APPLICANTNAME,applicantName)
            bundle.putString(NEWORDER_APPLICANTPHONE,applicantPhone)
            bundle.putString(NEWORDER_LOCATIONNAME,locationName)
            bundle.putLong(NEWORDER_WOID,woId)
            bundle.putString(NEWORDER_DESC,desc)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onFragmentResult(requestCode: Int, resultCode: Int, data: Bundle?) {
        super.onFragmentResult(requestCode, resultCode, data)
        if (data!=null&&resultCode != RESULT_OK) {
            return
        }
        when (requestCode) {
            REQUEST_WORKORDER_TYPE ->{
                if (data != null) {
                    writeIntoList(REQUEST_WORKORDER_TYPE,data)
                }
            }
            REQUEST_SERVICE_TYPE ->{
                if (data != null) {
                    writeIntoList(REQUEST_SERVICE_TYPE,data)
                }
            }
            REQUEST_PRIORITY ->{
                if (data != null) {
                    writeIntoList(REQUEST_PRIORITY,data)
                }
            }
        }

    }


    /**
    *  @Author: Karelie
    *  @Method：writeIntoList
    *  @Description：选择好的数据传入Adapter关联数组内并且刷新
    */
    fun writeIntoList(requestCode: Int,data: Bundle){
        val index = data.getString(ISelectDataService.SELECT_NEWORDER_POSITION)!!.toInt()
        val reason: SelectDataBean? = data.getParcelable(ISelectDataService.SELECT_OFFLINE_DATA_BACK)
        val enity :WorkorderService.newOrderEnity = newOrderList!![index]
        when(requestCode){
            REQUEST_WORKORDER_TYPE ->{ //工单类型
                if (reason != null){
                    newOrderList!![index].type = reason.id
                    newOrderList!![index].typeName = reason.fullName
                }
            }
            REQUEST_SERVICE_TYPE ->{ //服务类型
                if (reason != null){
                    newOrderList!![index].serviceTypeId = reason.id
                    newOrderList!![index].serviceName = reason.fullName
                }
            }
            REQUEST_PRIORITY ->{ //优先级
                if (reason != null){
                    newOrderList!![index].priorityId = reason.id
                    newOrderList!![index].priorityName = reason.fullName
                    newOrderList!![index].flowId = reason.parentId
                }
            }
        }
        newOrderAdapter!!.notifyDataSetChanged()

    }



    override fun onItemChildClick(helper: BaseQuickAdapter<*, *>?, v: View?, p: Int) {
        if (v?.id == R.id.iv_add_menu) {
            newOrderList!!.add(addNewItem())
            newOrderAdapter!!.setNewData(newOrderList)
        } else if (v?.id == R.id.tv_neworder_delete) {
            newOrderAdapter!!.remove(p)
            newOrderAdapter!!.notifyDataSetChanged();
            Log.i("karelie", "onItemChildClick: "+newOrderList!!.size);
        }else if (v?.id == R.id.civ_workorder_type){ //工单类型
            startForResult(
                SelectDataFragment.getInstance(ISelectDataService.DATA_TYPE_WORKORDER_TYPE,p.toString()),
                REQUEST_WORKORDER_TYPE
            )
        }else if (v?.id == R.id.civ_service_type){ //服务类型
            startForResult(
                SelectDataFragment.getInstance(ISelectDataService.DATA_TYPE_SERVICE_TYPE,p.toString()),
                REQUEST_SERVICE_TYPE
            )
        }else if (v?.id == R.id.civ_priority){ //优先级
            var servcie = SelectDataBean()
            servcie.id = newOrderList?.get(p)?.serviceTypeId
            startForResult(
                SelectDataFragment.getInstance(
                    ISelectDataService.DATA_TYPE_FLOW_PRIORITY,
                    mDepSelectData,
                    servcie,
                    newOrderList?.get(p)?.type,
                    location,
                    p.toString()),
                REQUEST_PRIORITY
            )
        }

    }


}