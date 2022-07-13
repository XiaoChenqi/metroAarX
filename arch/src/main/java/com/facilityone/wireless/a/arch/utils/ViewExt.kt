@file:JvmName("ViewExt")
package com.facilityone.wireless.a.arch.utils

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import com.facilityone.wireless.a.arch.R


// 所有子View
inline val ViewGroup.children
    get() = (0 until childCount).map { getChildAt(it) }


/**
 * 设置View不可用
 */
fun View.disable(value: Float = 0.5f) {
    isEnabled = false
    alpha = value
}

fun View.disableAll(ignoreId:Int?) {
    isEnabled = false
//    alpha = _alpha
    if (this is ViewGroup) {
        children.forEach {
            if (it.id!= ignoreId){
                it.disableAll(ignoreId)
            }
        }
    }
}

/**
 * 设置View可用
 */
fun View.enable() {
    isEnabled = true
    alpha = 1f
}


fun View.enableAll() {
    isEnabled = true
    if (this is ViewGroup) {
        children.forEach {
            it.enableAll()
        }
    }
}

fun View.visible() {
    visibility = View.VISIBLE
}
fun View.invisible() {
    visibility = View.INVISIBLE
}
