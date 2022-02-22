package com.facilityone.wireless

import android.content.Context
import android.util.Log
import com.facilityone.wireless.a.arch.offline.objectbox.MyObjectBox
import io.objectbox.BoxStore
import io.objectbox.android.AndroidObjectBrowser
import io.objectbox.android.BuildConfig

object ObjectBox {

    public var boxStore: BoxStore?= null


    @JvmStatic
    fun init(context: Context) {
        boxStore = MyObjectBox.builder()
            .androidContext(context.applicationContext)
            .build()
        val started = AndroidObjectBrowser(boxStore).start(context.applicationContext)
        if (BuildConfig.DEBUG) {
            Log.d(
                "APP", String.format(
                    "Using ObjectBox %s (%s)",
                    BoxStore.getVersion(), BoxStore.getVersionNative()
                )
            )
//            val started = AndroidObjectBrowser(boxStore).start(context.applicationContext)
            Log.i("ObjectBrowser", "Started: $started")
        }

    }
}