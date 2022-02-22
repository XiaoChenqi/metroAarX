package com.facilityone.wireless.boardingpatrol.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bigkoo.pickerview.listener.OnTimeSelectListener
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.TimeUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.facilityone.wireless.a.arch.ec.module.ISelectDataService
import com.facilityone.wireless.a.arch.ec.module.SelectDataBean
import com.facilityone.wireless.a.arch.ec.selectdata.SelectDataFragment
import com.facilityone.wireless.a.arch.ec.utils.SPKey
import com.facilityone.wireless.a.arch.mvp.BaseFragment
import com.facilityone.wireless.a.arch.utils.DatePickUtils
import com.facilityone.wireless.a.arch.xcq.utils.ToastUtils
import com.facilityone.wireless.basiclib.utils.DateUtils
import com.facilityone.wireless.basiclib.utils.onClick
import com.facilityone.wireless.boardingpatrol.R
import com.facilityone.wireless.boardingpatrol.databinding.LayoutBoardingcreateBinding
import com.facilityone.wireless.boardingpatrol.moudle.BoardingPatrolConstant.INSPECTION_BOARDING
import com.facilityone.wireless.boardingpatrol.moudle.BoardingPatrolConstant.INSPECTION_WALK
import com.facilityone.wireless.boardingpatrol.moudle.BoardingPatrolConstant.INTERVAL_DWON
import com.facilityone.wireless.boardingpatrol.moudle.BoardingPatrolConstant.INTERVAL_UP
import com.facilityone.wireless.boardingpatrol.moudle.BoardingService
import com.facilityone.wireless.boardingpatrol.presenter.BoardingCreatePresenter
import com.facilityone.wireless.boardingpatrol.toast
import java.util.*
/**
 *  @Author: Karelie
 *  @Method：RideCreateFragment
 *  @Date：2022/2/11 17:57
 *  @Description：巡查登记
 */
class BoardingCreateFragment : BaseFragment<BoardingCreatePresenter>(), BaseQuickAdapter.OnItemClickListener{
    lateinit var binding: LayoutBoardingcreateBinding
    var startTime:Long? = null
    var endTime:Long? = null
    var startSite : Long? = null
    var endSite : Long? = null
    var intervalChoice : Int? = null
    var inspectionChoice : Int? = null
    override fun setLayout(): Any {
        return R.layout.layout_boardingcreate
    }

    override fun setTitleBar(): Int {
        return R.id.ui_topbar
    }

    override fun createPresenter(): BoardingCreatePresenter {
        return BoardingCreatePresenter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mRootView = if (setLayout() is Int) {
            binding= LayoutBoardingcreateBinding.inflate(inflater,container,false)
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
        setTitle(getString(R.string.check_title))
        setRightTextButton(getString(R.string.upload_menu),R.id.boardingpatrol_upload)
        binding.civStartSite.canInput(false)
        binding.civTrainGeton.canInput(false)
        binding.civTrainGetoff.canInput(false)
        binding.civEndSite.canInput(false)

    }

    override fun onRightTextMenuClick(view: View?) {
        super.onRightTextMenuClick(view)
        val id = view?.id
        if (id == R.id.boardingpatrol_upload){
            uploadCheckList()
        }
    }

    /**
    *  @Author: Karelie
    *  @Method：uploadCheckList
    *  @Description：提交巡查登记数据
    */
    fun uploadCheckList(){

        if (binding.civChecker.inputText.isBlank()){
            toast.show("检查人为空")
            return
        }

        if (binding.civCheckplace.inputText.isBlank()){
            toast.show("检查单位为空")
            return
        }

        if (binding.civStartSite.tipText.isBlank()){
            toast.show(getString(R.string.start_site_hint))
            return
        }

        if (binding.civEndSite.tipText.isBlank()){
            toast.show(getString(R.string.end_site_hint))
            return
        }

        if (binding.rgUpDownResult.checkedRadioButtonId == -1){
            toast.show("请选择上/下行区间")
            return
        }

        if (binding.rgGroupCheck.checkedRadioButtonId == -1){
            toast.show("请选择检查方式")
            return
        }

        if (binding.rgGroupCheck.checkedRadioButtonId == binding.rbCheckMultiply.id){

            if (binding.civTrainNumber.inputText.isBlank()){
                toast.show(getString(R.string.train_number_hint))
                return
            }

            if (binding.civTrainGeton.tipText.isBlank()){
                toast.show(getString(R.string.add_train_geton_hint))
                return
            }

            if (binding.civTrainGetoff.tipText.isBlank()){
                toast.show(getString(R.string.add_train_getoff_hint))
                return
            }

        }

        if (binding.envCheckcontentDesc.desc.isBlank()){
            toast.show(getString(R.string.check_the_content_hint))
            return
        }

        if (binding.envCheckconditionDesc.desc.isBlank()){
            toast.show(getString(R.string.check_the_condition_hint))
            return
        }
        var req = BoardingService.BoardingSaveReq()
        var reqCheck = BoardingService.AdditionCheck()
        req?.apply {
            name = binding.civChecker.inputText
            name = binding.civChecker.inputText
            unit = binding.civCheckplace.inputText
            startingStationId = startSite
            endingStationId = endSite
            interval = intervalChoice
            inspectionMethod = inspectionChoice
            if (inspectionChoice == INSPECTION_BOARDING){
                reqCheck?.apply {
                    carNumber = binding.civTrainNumber.inputText
                    driver = binding.civTrainDriver.inputText
                    boardingTime = startTime
                    getOffTime = endTime
                }
                additionCheck = reqCheck
            }
            content = binding.envCheckcontentDesc.desc
            condition = binding.envCheckconditionDesc.desc
            resolutionCondition = binding.envSolveconditionDesc.desc
            needTrackedCondition = binding.envToBeTrakedDesc.desc
        }

        if (req != null) {
            presenter.checkUpdata(req)
        }



    }

    private fun initOnClick() {

        binding.rgUpDownResult.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == binding.rbUp.id){
                intervalChoice = INTERVAL_UP
            }else if(checkedId == binding.rbDown.id){
                intervalChoice = INTERVAL_DWON
            }

        }

        binding.rgGroupCheck.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == binding.rbCheckWalk.id){
                binding.llTrain.visibility = View.GONE
                inspectionChoice = INSPECTION_WALK
            }else if (checkedId == binding.rbCheckMultiply.id){
                binding.llTrain.visibility = View.VISIBLE
                inspectionChoice = INSPECTION_BOARDING
            }
        }

        //选择开始站点
        binding.civStartSite.onClick(100){
            startForResult(
                SelectDataFragment.getInstance(ISelectDataService.DATA_TYPE_LOCATION_BOARDING),
                REQUEST_LOCATION_START
            )
        }

        //选择结束站点
        binding.civEndSite.onClick(100){
            startForResult(
                SelectDataFragment.getInstance(ISelectDataService.DATA_TYPE_LOCATION_BOARDING),
                REQUEST_LOCATION_END
            )
        }

        //添乘上车时间
        binding.civTrainGeton.onClick(100){
            val tempStartTime = if (startTime == null) Calendar.getInstance().timeInMillis else startTime
            val calendar = Calendar.getInstance()
            if (tempStartTime != null) {
                calendar.timeInMillis = tempStartTime
            }
            DatePickUtils.pickDateDefaultYMDHM(activity, calendar,
                OnTimeSelectListener { date, v ->
                    if (endTime != null && date.time > endTime!!) {
                        toast.show(R.string.workorder_time_start_error)
                        return@OnTimeSelectListener
                    }
                    startTime = date.time
                    binding.civTrainGeton.tipText = TimeUtils.millis2String(startTime!!,
                        DateUtils.SIMPLE_DATE_FORMAT_ALL)
                })
        }

        //添乘下车时间
        binding.civTrainGetoff.onClick(100) {
            val tempEndTime = if (endTime == null) Calendar.getInstance().timeInMillis else endTime
            val calendar = Calendar.getInstance()
            if (tempEndTime != null) {
                calendar.timeInMillis = tempEndTime
            }
            DatePickUtils.pickDateDefaultYMDHM(activity, calendar,
                OnTimeSelectListener { date, v ->
                    if (startTime != null && date.time < startTime!!) {
                        toast.show(R.string.workorder_time_end_error)
                        return@OnTimeSelectListener
                    }
                    endTime = date.time
                    binding.civTrainGetoff.tipText = TimeUtils.millis2String(endTime!!,
                        DateUtils.SIMPLE_DATE_FORMAT_ALL)
                })
        }

    }

    private fun initData() {
        //默认传当前登录的账号名称
        binding.civChecker.inputText = SPUtils.getInstance(SPKey.SP_MODEL_USER).getString(SPKey.EM_NAME)
    }

    override fun onFragmentResult(requestCode: Int, resultCode: Int, data: Bundle?) {
        super.onFragmentResult(requestCode, resultCode, data)
        val bean: SelectDataBean? = data?.getParcelable(ISelectDataService.SELECT_OFFLINE_DATA_BACK)
        when(requestCode){
            REQUEST_LOCATION_START ->{
                binding.civStartSite.tipTv.text = bean?.name
                startSite = bean?.id
            }
            REQUEST_LOCATION_END ->{
                binding.civEndSite.tipTv.text = bean?.name
                endSite = bean?.id
            }
        }
    }


    override fun onItemClick(help: BaseQuickAdapter<*, *>?, v: View?, index: Int) {

    }

    companion object {
        private val REQUEST_LOCATION_START = 20001
        private val REQUEST_LOCATION_END = 20002
        @JvmStatic
        fun getInstance(): BoardingCreateFragment {
            val fragment = BoardingCreateFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }



}
