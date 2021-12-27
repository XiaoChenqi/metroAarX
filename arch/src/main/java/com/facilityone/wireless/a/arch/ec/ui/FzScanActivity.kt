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
import android.graphics.Bitmap
import android.view.View

import com.facilityone.wireless.a.arch.R;
import androidx.annotation.Nullable

import com.gyf.barlibrary.ImmersionBar

import com.fm.tool.scan.view.FMIconfontView




class FzScanActivity: FragmentActivity() {
    //扫码容器
    private var mScanContainer: FrameLayout? = null

    //扫码视图
    private var mRemoteView: RemoteView? = null

    private var mBack: FMIconfontView? = null
    private var mFlashlight: FMIconfontView? = null
    private val flashlightStatus = false
    private var mPic: FMIconfontView? = null

    //扫码尺寸
    val SCAN_FRAME_SIZE = 240

    //屏幕宽高
    var mScreenWidth = 0
    var mScreenHeight = 0

    //扫码回调
    val SCAN_RESULT = "scanResult"

    val REQUEST_CODE_PHOTO = 0X1113


    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtils.setNoTitle(this)
        setContentView(R.layout.activity_fz_scan)
        //        StatusBarUtils.setTransparentStatusBar(this);
       val title = findViewById<View>(R.id.topbar)
        ImmersionBar.with(this).fitsSystemWindows(true,R.color.wx_topbar_bg_color)
        AppUtils.syncIsDebug(this.applicationContext)
        initPermission()
        initView(savedInstanceState)

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

    private fun initView(savedInstanceState: Bundle?) {
        mScanContainer = findViewById(R.id.scanContainter)
        mBack = findViewById(R.id.btn_back)
        mFlashlight = findViewById(R.id.btn_flashlight)
        mPic = findViewById(R.id.btn_pic)


        //设置扫码视图尺寸
        val dm = resources.displayMetrics
        val density = dm.density
        mScreenWidth = resources.displayMetrics.widthPixels
        mScreenHeight = resources.displayMetrics.heightPixels
        val scanFrameSize = (SCAN_FRAME_SIZE * density).toInt()

        //3.计算取景器的矩形，它位于布局的中间。
        //设置扫描区域。 （可选。矩形可以为空。如果没有指定设置，它将位于布局的中间。）
        val rect = Rect()
        rect.left = mScreenWidth / 2 - scanFrameSize / 2
        rect.right = mScreenWidth / 2 + scanFrameSize / 2
        rect.top = mScreenHeight / 2 - scanFrameSize / 2
        rect.bottom = mScreenHeight / 2 + scanFrameSize / 2

        //构建扫码视图
        mRemoteView = RemoteView.Builder().setContext(this).setBoundingBox(rect)
            .setFormat(HmsScan.ALL_SCAN_TYPE).build()


        //直接扫码回调
        mRemoteView!!.setOnResultCallback(OnResultCallback { result -> //判断回调
            if (result != null && result.size > 0 && result[0] != null && !TextUtils.isEmpty(
                    result[0].getOriginalValue()
                )
            ) {
                val intent = Intent()
                intent.putExtra(SCAN_RESULT, result[0])
                setResult(RESULT_OK, intent)
                finish()
            }
        })

        //扫码视图创建生命周期
        mRemoteView!!.onCreate(savedInstanceState)
        //将扫码视图添加到扫码容器中
        val params = FrameLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        mScanContainer!!.addView(mRemoteView, params)

        //设置返回、照片扫描和闪光灯操作。
        setBackOperation()
        setPictureScanOperation()
        setFlashOperation()
    }

    /**
     * @Created by: kuuga
     * @Date: on 2021/9/1 14:56
     * @Description: 相册扫码
     */
    private fun setPictureScanOperation() {
        mPic!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val pickIntent = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
                pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
                this@FzScanActivity.startActivityForResult(pickIntent, REQUEST_CODE_PHOTO)
            }
        })
    }


    /**
     * @Created by: kuuga
     * @Date: on 2021/9/1 14:56
     * @Description: 闪光灯操作
     */
    private fun setFlashOperation() {
        mFlashlight!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (mRemoteView!!.lightStatus) {
                    mRemoteView!!.switchLight()
                    mFlashlight!!.setTextColor(this@FzScanActivity.resources.getColor(R.color.wx_topbar_font_color))
                    mFlashlight!!.text =
                        this@FzScanActivity.resources.getString(R.string.wx_topbar_flashlight_off_icon)
                    //                    flushBtn.setImageResource(img[1]);
                } else {
                    mRemoteView!!.switchLight()
                    mFlashlight!!.setTextColor(this@FzScanActivity.resources.getColor(R.color.qrcv_cornerColor))
                    mFlashlight!!.text =
                        this@FzScanActivity.resources.getString(R.string.wx_topbar_flashlight_on_icon)
                }
            }
        })
    }

    /**
     * @Created by: kuuga
     * @Date: on 2021/9/1 14:57
     * @Description: 关闭操作
     */
    private fun setBackOperation() {
        mBack!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                finish()
            }
        })
    }

    /**
     * @Created by: kuuga
     * @Date: on 2021/9/1 14:58
     * @Description: 生命周期
     */
    override fun onStart() {
        super.onStart()
        mRemoteView!!.onStart()
    }

    override fun onResume() {
        super.onResume()
        mRemoteView!!.onResume()
    }

    override fun onPause() {
        super.onPause()
        mRemoteView!!.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mRemoteView!!.onDestroy()
    }

    override fun onStop() {
        super.onStop()
        mRemoteView!!.onStop()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_PHOTO) {
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, data!!.data)
                val hmsScans = ScanUtil.decodeWithBitmap(
                    this@FzScanActivity,
                    bitmap,
                    HmsScanAnalyzerOptions.Creator().setPhotoMode(true).create()
                )
                if (hmsScans != null && hmsScans.size > 0 && hmsScans[0] != null && !TextUtils.isEmpty(
                        hmsScans[0]!!.getOriginalValue()
                    )
                ) {
                    val intent = Intent()
                    intent.putExtra(SCAN_RESULT, hmsScans[0])
                    setResult(RESULT_OK, intent)
                    finish()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}

//相册扫码消息回调
//    protected  fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_PHOTO) {
//            try {
//                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, data.data)
//                val hmsScans = ScanUtil.decodeWithBitmap(
//                    this@FzScanActivity,
//                    bitmap,
//                    HmsScanAnalyzerOptions.Creator().setPhotoMode(true).create()
//                )
//                if (hmsScans != null && hmsScans.size > 0 && hmsScans[0] != null && !TextUtils.isEmpty(
//                        hmsScans[0]!!.getOriginalValue()
//                    )
//                ) {
//                    val intent = Intent()
//                    intent.putExtra(SCAN_RESULT, hmsScans[0])
//                    setResult(RESULT_OK, intent)
//                    finish()
//                }
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
//        }
//    }

//}