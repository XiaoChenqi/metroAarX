package com.facilityone.wireless.basiclib.utils

import android.os.SystemClock
import android.view.View
import com.facilityone.wireless.basiclib.R

fun throttleClick(wait: Long = 200, block: ((View) -> Unit)): View.OnClickListener {

    return View.OnClickListener { v ->
        val current = SystemClock.uptimeMillis()
        val lastClickTime = (v.getTag(R.id.qmui_click_timestamp) as? Long) ?: 0
        if (current - lastClickTime > wait) {
            v.setTag(R.id.qmui_click_timestamp, current)
            block(v)
        }
    }
}




 open class onDebounceClickListener(val wait: Long):View.OnClickListener{
    override fun onClick(v: View?) {
        val current = SystemClock.uptimeMillis()
        val lastClickTime = (v?.getTag(R.id.qmui_click_timestamp) as? Long) ?: 0
        if (current - lastClickTime > wait) {
            v?.setTag(R.id.qmui_click_timestamp, current)
            onDebounceClick(v!!)
        }
    }
     open fun onDebounceClick(view: View) {}

}



fun debounceClick(wait: Long = 200, block: ((View) -> Unit)): View.OnClickListener {
    return View.OnClickListener { v ->
        var action = (v.getTag(R.id.qmui_click_debounce_action) as? DebounceAction)
        if(action == null){
            action = DebounceAction(v, block)
            v.setTag(R.id.qmui_click_debounce_action, action)
        }else{
            action.block = block
        }
        v.removeCallbacks(action)
        v.postDelayed(action, wait)
    }
}
class DebounceAction(val view: View,  var block: ((View) -> Unit)): Runnable {
    override fun run() {
        if(view.isAttachedToWindow){
            block(view)
        }
    }
}

fun View.onClick(wait: Long = 200, block: ((View) -> Unit)) {
    setOnClickListener(throttleClick(wait, block))
}

fun View.onDebounceClick(wait: Long = 200, block: ((View) -> Unit)) {
    setOnClickListener(debounceClick(wait, block))
}