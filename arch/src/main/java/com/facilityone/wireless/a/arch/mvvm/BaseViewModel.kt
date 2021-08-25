package com.facilityone.wireless.a.arch.mvvm

import androidx.lifecycle.ViewModel
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog

class BaseViewModel: ViewModel() {

    interface Handlers{
        /**显示隐藏Loading */
        fun showLoading(): QMUITipDialog?
        fun dismissLoading(): QMUITipDialog?
    }
}