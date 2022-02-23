package com.facilityone.wireless.boardingpatrol.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.blankj.utilcode.util.TimeUtils
import com.facilityone.wireless.a.arch.mvp.BaseFragment
import com.facilityone.wireless.basiclib.utils.DateUtils
import com.facilityone.wireless.boardingpatrol.BR
import com.facilityone.wireless.boardingpatrol.R
import com.facilityone.wireless.boardingpatrol.databinding.AdapterBoardingqueryBinding
import com.facilityone.wireless.boardingpatrol.databinding.FragmentBoardingInforBinding
import com.facilityone.wireless.boardingpatrol.databinding.LayoutBoardingcreateBinding
import com.facilityone.wireless.boardingpatrol.moudle.BoardingPatrolConstant.INSPECTION_BOARDING
import com.facilityone.wireless.boardingpatrol.moudle.BoardingPatrolConstant.INSPECTION_WALK
import com.facilityone.wireless.boardingpatrol.moudle.BoardingPatrolConstant.INTERVAL_UP
import com.facilityone.wireless.boardingpatrol.moudle.BoardingService
import com.facilityone.wireless.boardingpatrol.presenter.BoardingInforPresenter
import com.facilityone.wireless.boardingpatrol.toast
import kotlinx.coroutines.channels.ticker
import java.util.*

/**
 *  @Author: Karelie
 *  @Method：BoardingInforFragment
 *  @Date：2022/2/11 17:57
 *  @Description：检查详情
 */
class BoardingInforFragment : BaseFragment<BoardingInforPresenter>() {
    lateinit var b: FragmentBoardingInforBinding
    override fun setLayout(): Any {
        return R.layout.fragment_boarding_infor
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
            b = FragmentBoardingInforBinding.inflate(inflater, container, false)
            b.root
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
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        setTitle("巡查记录")
        val bundle = arguments
        bundle?.getLong(REQUEST_ID).apply {
            presenter.getQueryInfor(this!!)
        }
        b.tvInterval.text = bundle?.getString(START_SITE,"")+"至"+
                bundle?.getString(END_SITE,"") + if(bundle?.getInt(DIRECTION) == INTERVAL_UP) "上行" else { "下行" }+"区间"



    }

    fun refreshInfro(enity: BoardingService.BoardingInforEnity) {
        enity.userName.apply {
            if (this != null && enity.createDate != null){
                b.tvCreator.text = this+"填写于"+TimeUtils.date2String(Date(enity.createDate!!), DateUtils.SIMPLE_DATE_FORMAT_ALL)
            }
        }
        enity.name.apply {
            b.tvChecker.text = this
        }
        enity.unit.apply { b.checkUnit.text = this }
        enity.inspectionMethod.apply {
            b.checkWay.text = if (this == INSPECTION_WALK) "步行" else "添乘"
            if (this != null && this == INSPECTION_BOARDING){
                b.llWay.visibility = View.VISIBLE
            }else{
                b.llWay.visibility = View.GONE
            }
        }
        enity.carNumber.apply { b.tvCarNumber.text = this }
        enity.driver.apply { b.tvDriver.text = this }

        enity.boardingTime.apply {
            if (this != null) {
                b.tvBoarding.text =
                    TimeUtils.date2String(Date(this), DateUtils.SIMPLE_DATE_FORMAT_ALL)
            }
        }

        enity.getOffTime.apply {
            if (this != null) {
                b.tvGetoff.text =
                    TimeUtils.date2String(Date(this), DateUtils.SIMPLE_DATE_FORMAT_ALL)
            }
        }

        enity.content.apply { b.tvCheckContent.text = this }
        enity.condition.apply { b.tvCheckCondition.text = this }
        enity.resolutionCondition.apply { b.tvSolveCondition.text = this }
        enity.needTrackedCondition.apply { b.tvNeedTracked.text = this }



    }

    fun refreshError() {
        toast.show("获取信息失败")
    }

    override fun createPresenter(): BoardingInforPresenter {
        return BoardingInforPresenter()
    }

    companion object {
        private val REQUEST_ID = "request_id"
        private val START_SITE = "start_site"
        private val END_SITE = "end_site"
        private val DIRECTION = "direction"

        @JvmStatic
        fun getInstance(id: Long,startSite:String,endSite:String,direction:Int): BoardingInforFragment {
            val fragment = BoardingInforFragment()
            val bundle = Bundle()
            bundle.putLong(REQUEST_ID, id)
            bundle.putString(START_SITE,startSite)
            bundle.putString(END_SITE,endSite)
            bundle.putInt(DIRECTION,direction)
            fragment.arguments = bundle
            return fragment
        }
    }
}