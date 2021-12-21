package com.facilityone.wireless.workorder.fragment

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.SPUtils
import com.facilityone.wireless.a.arch.ec.module.LocationBean
import com.facilityone.wireless.a.arch.ec.module.UserService
import com.facilityone.wireless.a.arch.ec.utils.SPKey
import com.facilityone.wireless.a.arch.mvp.BaseFragment
import com.facilityone.wireless.basiclib.utils.StringUtils
import com.facilityone.wireless.workorder.R
import com.facilityone.wireless.workorder.databinding.FragmentPrecautionsBinding
import com.facilityone.wireless.workorder.databinding.FragmentWorkorderCreateBinding
import com.facilityone.wireless.workorder.module.WorkorderCreateService
import com.facilityone.wireless.workorder.presenter.PrecautionsPresenter
import com.luck.picture.lib.entity.LocalMedia

/**
 * @Creator:Karelie
 * @Data: 2021/12/6
 * @TIME: 15:08
 * @Introduce: 注意事项 或其余空白只显示文字界面
**/
class PrecautionsFragment : BaseFragment<PrecautionsPresenter>() {
    lateinit var binding : FragmentPrecautionsBinding
    private var infor :String?=""
    override fun setLayout(): Any {
        return R.layout.fragment_precautions
    }

    override fun createPresenter(): PrecautionsPresenter {
       return PrecautionsPresenter()
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
//            inflater.inflate(setLayout() as Int, container, false)
            binding= FragmentPrecautionsBinding.inflate(inflater,container,false)
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
        binding.envDesc.canInput(false)
        binding.envDesc.setInputDisp(false)
        setTitle(getString(R.string.workorder_step_warning))
        binding.envDesc.showHint(false)

    }

    private fun initData() {
        val bundle = arguments
        if (bundle != null){
            infor = bundle.getString(PrecautionsFragment.PRECAUTIONS)
        }
        if (infor != null){
            binding.envDesc.desc = infor+""
        }



    }


    companion object {
        private val PRECAUTIONS = "precautions"
        @JvmStatic
        fun getInstance(precautions: String): PrecautionsFragment {
            val fragment = PrecautionsFragment()
            val bundle = Bundle()
            bundle.putString(PrecautionsFragment.PRECAUTIONS, precautions)
            fragment.arguments = bundle
            return fragment
        }
    }

}