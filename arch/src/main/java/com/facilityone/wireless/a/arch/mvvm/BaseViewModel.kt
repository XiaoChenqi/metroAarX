package com.facilityone.wireless.a.arch.mvvm

import androidx.lifecycle.ViewModel
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog

open class BaseViewModel: ViewModel() {

    interface Handlers{
        /**显示隐藏Loading */
        fun showLoading(): QMUITipDialog?
        fun dismissLoading(): QMUITipDialog?
    }

    inner class UILiveEvent {

        val showToastEvent by lazy { SingleLiveEvent<String>() }
        val showLoadingEvent by lazy { SingleLiveEvent<Boolean>() }
        val dismissLoadingEvent by lazy { SingleLiveEvent<Boolean>() }
        val showSnackbarEvent by lazy { SingleLiveEvent<String>() }

    }
}