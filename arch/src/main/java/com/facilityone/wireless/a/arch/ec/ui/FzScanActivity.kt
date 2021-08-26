package com.facilityone.wireless.a.arch.ec.ui

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.facilityone.wireless.a.arch.R
import com.fm.tool.scan.utils.AppUtils
import com.fm.tool.scan.utils.StatusBarUtils
import com.huawei.hms.hmsscankit.OnLightVisibleCallBack
import com.huawei.hms.hmsscankit.OnResultCallback
import com.huawei.hms.hmsscankit.RemoteView
import com.huawei.hms.hmsscankit.RemoteView.REQUEST_CODE_PHOTO
import com.huawei.hms.hmsscankit.ScanUtil
import com.huawei.hms.ml.scan.HmsScan
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions
import java.io.IOException

class FzScanActivity: FragmentActivity() {
    //扫码RemoteView
    private lateinit var remoteView: RemoteView
    //承接扫码RemoteView的容器
    private lateinit var rim:FrameLayout
    val SCAN_FRAME_SIZE = 240
    var mScreenWidth = 0
    var mScreenHeight = 0
    val SCAN_RESULT = "scanResult"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtils.setNoTitle(this)
        this.setContentView(R.layout.activity_fz_scan)
        StatusBarUtils.setTransparentStatusBar(this)
        AppUtils.syncIsDebug(this.applicationContext)
        this.initPermission()
        this.initView(savedInstanceState)
    }

    private fun initView(savedInstanceState: Bundle?) {
        rim=findViewById(R.id.rim)
        val dm = resources.displayMetrics
        val density = dm.density
        mScreenWidth = resources.displayMetrics.widthPixels
        mScreenHeight = resources.displayMetrics.heightPixels
        val scanFrameSize = (SCAN_FRAME_SIZE * density)
        val rect = Rect()
        rect.left = (mScreenWidth / 2 - scanFrameSize / 2).toInt()
        rect.right = (mScreenWidth / 2 + scanFrameSize / 2).toInt()
        rect.top = (mScreenHeight / 2 - scanFrameSize / 2).toInt()
        rect.bottom = (mScreenHeight / 2 + scanFrameSize / 2).toInt()

        remoteView= RemoteView.Builder().setContext(this).setBoundingBox(rect)
            .setFormat(HmsScan.ALL_SCAN_TYPE).build()

        remoteView.setOnLightVisibleCallback(OnLightVisibleCallBack { visible ->
            if (visible) {
//                flushBtn.setVisibility(View.VISIBLE)
            }
        })

        remoteView.setOnResultCallback(OnResultCallback { result -> //Check the result.
            if (result != null && result.size > 0 && result[0] != null && !TextUtils.isEmpty(result[0].getOriginalValue())) {
                val intent = Intent()
                intent.putExtra(SCAN_RESULT, result[0])
                setResult(RESULT_OK, intent)
                this.finish()
            }
        })

        remoteView.onCreate(savedInstanceState)
        val params = FrameLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )

        rim.addView(remoteView, params)
    }
    override fun onStart() {
        super.onStart()
        remoteView.onStart()
    }

    override fun onResume() {
        super.onResume()
        remoteView.onResume()
    }

    override fun onPause() {
        super.onPause()
        remoteView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        remoteView.onDestroy()
    }

    override fun onStop() {
        super.onStop()
        remoteView.onStop()
    }

    private fun initPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                "android.permission.CAMERA"
            ) != 0 && ContextCompat.checkSelfPermission(
                this,
                "android.permission.WRITE_EXTERNAL_STORAGE"
            ) != 0
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf("android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"),
                1
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_PHOTO) {
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, data?.data)
                val hmsScans = ScanUtil.decodeWithBitmap(
                    this,
                    bitmap,
                    HmsScanAnalyzerOptions.Creator().setPhotoMode(true).create()
                )
                if (hmsScans != null && hmsScans.size > 0 && hmsScans[0] != null && !TextUtils.isEmpty(
                        hmsScans[0]!!.getOriginalValue()
                    )
                ) {
//                    ToastUtils.showShort(hmsScans[0].getOriginalValue())
                    val intent = Intent()
                    intent.putExtra(SCAN_RESULT, hmsScans[0])
                    setResult(RESULT_OK, intent)
                    this@FzScanActivity.finish()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}