package com.facilityone.wireless.maintenance.fragment

import androidx.fragment.app.Fragment


fun Fragment.toast(resId:Int){
 com.hjq.toast.ToastUtils.show(resId)
}

fun Fragment.toast(string: String){
    com.hjq.toast.ToastUtils.show(string)
}
