package com.facilityone.wireless.construction.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bigkoo.pickerview.listener.OnTimeSelectListener
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.TimeUtils
import com.facilityone.wireless.a.arch.ec.module.UserService
import com.facilityone.wireless.a.arch.ec.utils.SPKey
import com.facilityone.wireless.a.arch.mvp.BaseFragment
import com.facilityone.wireless.a.arch.offline.objectbox.ObjectBoxHelper
import com.facilityone.wireless.a.arch.utils.DatePickUtils
import com.facilityone.wireless.a.arch.widget.FMWarnDialogBuilder
import com.facilityone.wireless.basiclib.utils.DateUtils
import com.facilityone.wireless.basiclib.utils.GsonUtils
import com.facilityone.wireless.basiclib.utils.onClick
import com.facilityone.wireless.construction.R
import com.facilityone.wireless.construction.databinding.LayoutConstructionregistBinding
import com.facilityone.wireless.construction.module.ConstructionConstant.COMPLETE
import com.facilityone.wireless.construction.module.ConstructionConstant.UNCOMPLETE
import com.facilityone.wireless.construction.module.ConstructionService
import com.facilityone.wireless.construction.presenter.ConstructionRegistPresenter
import com.facilityone.wireless.construction.toast
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

/**
 *  @Author: Karelie
 *  @Method：ConstructionRegistFragment
 *  @Date：2022/3/3 10:57
 *  @Description：施工监护登记
 */
class ConstructionRegistFragment : BaseFragment<ConstructionRegistPresenter>() {
    lateinit var binding: LayoutConstructionregistBinding
    var startTime:Long? = null
    var endTime:Long? = null
    var mreqDate : Long? = null
    var mResultValue : String? = null //保存数据
    var mStatus:Int? = -1 //状态
    override fun setLayout(): Any {
        return R.layout.layout_constructionregist
    }

    override fun setTitleBar(): Int {
        return R.id.ui_topbar
    }

    override fun createPresenter(): ConstructionRegistPresenter {
        return ConstructionRegistPresenter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mRootView = if (setLayout() is Int) {
            binding= LayoutConstructionregistBinding.inflate(inflater,container,false)
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
        setTitle(getString(R.string.construction_regist))
        setRightTextButton(getString(R.string.construction_upload),R.id.construction_upload)


    }

    override fun onRightTextMenuClick(view: View?) {
        super.onRightTextMenuClick(view)
        val id = view?.id
        if (id == R.id.construction_upload){
            uploadCheckList()
        }
    }

    /**
    *  @Author: Karelie
    *  @Method：uploadCheckList
    *  @Description：提交施工监护登记
    */
    fun uploadCheckList(){
        binding.apply {

            if (civTutelagePerson.inputText.isBlank()){
                toast.show("监护人员为空")
                return
            }

            if (civTutelageStarttime.tipText.isBlank()){
                toast.show("请选择监护开始时间")
                return
            }

            if (civTutelageEndtime.tipText.isBlank()){
                toast.show("请选择监护结束时间")
                return
            }

            if (civConstructionName.inputText.isBlank()){
                toast.show("请输入施工项目名称")
                return
            }

            if (civConstructionData.tipText.isBlank()){
                toast.show("请输入施工日期")
                return
            }

            if (civConstructionSite.inputText.isBlank()){
                toast.show("请输入施工站点")
                return
            }

            if (civConstructionUnit.inputText.isBlank()){
                toast.show("请输入施工单位")
                return
            }

            if (civConstructionLeader.inputText.isBlank()){
                toast.show("请输入现场施工负责人")
                return
            }

            if (civAllLeader.inputText.isBlank()){
                toast.show("请输入现场负责人")
                return
            }

            if (mStatus == COMPLETE){
                presenter.upLoadRegist(getReq())
            }else{
                val builder = FMWarnDialogBuilder(activity)
                builder.setTitle("")
                builder.setSure(R.string.maintenance_submit_ok)
                builder.setCancel(R.string.maintenance_submit_cancel)
                builder.setTip("施工监护项目未完成，确定是否提交")
                builder.addOnBtnSureClickListener(FMWarnDialogBuilder.OnBtnClickListener { dialog, view ->
                    dialog.dismiss()
                    presenter.upLoadRegist(getReq())
                })
                builder.create(R.style.fmDefaultWarnDialog).show()
            }



        }
    }

    fun getReq(): ConstructionService.ConstructionRegReq{
        val req = ConstructionService.ConstructionRegReq()
        req.apply {
            binding.apply {
                guardian = civTutelagePerson.inputText
                guardianStartTime = startTime
                guardianEndTime = endTime
                guardianName = civConstructionName.inputText
                constructionDate = mreqDate
                constructionSite = civConstructionSite.inputText
                constructionUnit = civConstructionUnit.inputText
                constructionPrincipal = civConstructionLeader.inputText  //现场施工负责人
                principal = civAllLeader.inputText //总负责人
                notePerson = envCheckconditionDesc.desc //备注
                resultValue = mResultValue?:""
                status = mStatus
            }
        }
        return req
    }


    fun Long.toDate():String{
        val date: Date = Date(this)
        val dateFormat = DateUtils.SIMPLE_DATE_FORMAT_SECOND
        return dateFormat.format(date)
    }

    private fun initOnClick() {
        binding.apply {
            //监护开始时间
            civTutelageStarttime.onClick {
                val tempStartTime = if (startTime == null) Calendar.getInstance().timeInMillis else startTime
                val calendar = Calendar.getInstance()
                if (tempStartTime != null) {
                    calendar.timeInMillis = tempStartTime
                }
                DatePickUtils.pickDateDefaultYMDHMS(activity, calendar,
                    OnTimeSelectListener { date, v ->
                        if (endTime != null && date.time > endTime!!) {
                            toast.show(R.string.workorder_time_start_error)
                            return@OnTimeSelectListener
                        }
                        startTime =  date.time
                        civTutelageStarttime.tipText = TimeUtils.millis2String(startTime!!,
                            DateUtils.SIMPLE_DATE_FORMAT_SECOND)
                    })
            }
            //监护结束时间
            civTutelageEndtime.onClick(100) {
                val tempEndTime = if (endTime == null) Calendar.getInstance().timeInMillis else endTime
                val calendar = Calendar.getInstance()
                if (tempEndTime != null) {
                    calendar.timeInMillis = tempEndTime
                }
                DatePickUtils.pickDateDefaultYMDHMS(activity, calendar,
                    OnTimeSelectListener { date, v ->
                        if (startTime != null && date.time < startTime!!) {
                            toast.show(R.string.workorder_time_end_error)
                            return@OnTimeSelectListener
                        }
                        endTime = date.time
                        civTutelageEndtime.tipText = TimeUtils.millis2String(endTime!!,
                            DateUtils.SIMPLE_DATE_FORMAT_SECOND)
                    })
            }
            //施工日期
            civConstructionData.onClick {
                val tempData = Calendar.getInstance().timeInMillis
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = tempData
                DatePickUtils.pickDateDefaultYMD(activity, calendar, OnTimeSelectListener { date, view ->
                    mreqDate = date.time
                    civConstructionData.tipText = TimeUtils.millis2String(date.time!!,
                        DateUtils.SIMPLE_DATE_FORMAT_YMD)
                })
            }

           llList.onClick {
               startForResult(ConstructionTemplateFragment.getInstance(),LIST_REFRESH)
            }


        }
    }

    private fun initData() {
        binding.apply {
            civTutelagePerson.inputText = SPUtils.getInstance(SPKey.SP_MODEL_USER).getString(SPKey.EM_NAME)
            civConstructionData.tipText = TimeUtils.millis2String(System.currentTimeMillis(), DateUtils.SIMPLE_DATE_FORMAT_YMD)
            mreqDate = System.currentTimeMillis()
            val data:String = SPUtils.getInstance(SPKey.SP_MODEL_USER).getString(SPKey.USER_INFO)
            Log.i("Karelie", "initData: "+data)
            val userInfor  = GsonUtils.fromJson<UserService.UserInfoBean>(data,object :TypeToken<UserService.UserInfoBean>(){}.type)
            Log.i("Karelie", "initData: "+userInfor)
            if (userInfor != null){
                civConstructionSite.inputText =  userInfor.locationName+""
            }
        }
        initDBdata()




    }

    fun initDBdata(){
        binding.apply {
            val lastData = ObjectBoxHelper.getLastConstructionData()
            if (lastData != null){
                lastData.apply {
                    mResultValue = upLoadContent
                    if (status!!){ //完成
                        tvConstructionProjectList.text= getString(R.string.complete)
                        tvConstructionProjectList.setBackgroundResource(R.drawable.construction_complete)
                        mStatus = COMPLETE
                    }else{
                        tvConstructionProjectList.text= getString(R.string.un_complete)
                        tvConstructionProjectList.setBackgroundResource(R.drawable.construction_uncomplete)
                        mStatus = UNCOMPLETE
                    }
                }
            }else{
                tvConstructionProjectList.text= getString(R.string.un_complete)
                tvConstructionProjectList.setBackgroundResource(R.drawable.construction_uncomplete)
                mStatus = UNCOMPLETE
            }

        }
    }

    override fun onFragmentResult(requestCode: Int, resultCode: Int, data: Bundle?) {
        super.onFragmentResult(requestCode, resultCode, data)
        if (requestCode == LIST_REFRESH){
            initDBdata()
        }
    }




    override fun onDestroy() {
        clearTemplateData()
        super.onDestroy()
    }


    private fun clearTemplateData(){
        //清理临时保存的数据
        ObjectBoxHelper.getConstructionBox().removeAll()
    }


    companion object {
        private val CONSTRUCTION_LIST = "construction_list"
        private val LIST_REFRESH = 10001
        @JvmStatic
        fun getInstance(): ConstructionRegistFragment {
            val fragment = ConstructionRegistFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }


}