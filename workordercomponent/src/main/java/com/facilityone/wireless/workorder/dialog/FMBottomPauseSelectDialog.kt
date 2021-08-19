//package com.facilityone.wireless.workorder.dialog
//
//import android.view.View
//import androidx.annotation.StringRes
//import androidx.fragment.app.Fragment
//import com.facilityone.wireless.a.arch.R
//import com.facilityone.wireless.a.arch.databinding.FmBottomSheetPauseSelectBinding
//import com.facilityone.wireless.a.arch.widget.FMBottomPauseSelectSheetBuilder
//
//import razerdp.basepopup.BasePopupWindow
//
//
///**
// * @Created by: kuuga
// * @Date: on 2021/8/13 15:43
// * @Description:底部弹窗
// */
//
//
////类型别名
//typealias XBinding=FmBottomSheetPauseSelectBinding
//
//
//typealias Xdialog=FMBottomPauseSelectDialog
//
//class FMBottomPauseSelectDialog(fragment:Fragment):BasePopupWindow(fragment) {
//
//    lateinit var mBinding:XBinding
//
//    init {
//        setContentView(R.layout.fm_bottom_sheet_pause_select)
//    }
//
//    override fun onViewCreated(contentView: View) {
//       mBinding=XBinding.bind(contentView)
//    }
//
//    fun setTitle(@StringRes title:Int):Xdialog{
//        setTitle(context.resources.getString(title))
//        return this
//    }
//
//    fun setTitle(str:String):Xdialog{
//        mBinding.tvTitle.text = str
//        return this
//    }
//
//
//    fun setTip(tip: String?): Xdialog {
//        mBinding.tvTip.text = tip
//        return this
//    }
//
//    fun setTip(@StringRes tip: Int): Xdialog {
//        setTip(context.resources.getString(tip))
//        return this
//    }
//
//    fun setShowTip(showTip: Boolean): Xdialog {
//        mBinding.tvTip.visibility = if (showTip) View.VISIBLE else View.GONE
//        return this
//    }
//
//
//}