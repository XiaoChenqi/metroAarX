/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.facilityone.wireless.fm_library.zxing.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.scanzxing.R;

import com.facilityone.wireless.fm_library.tools.CameraPermissionCheckUtil;
import com.facilityone.wireless.fm_library.tools.ShowNotice;
import com.facilityone.wireless.fm_library.tools.TelephoneManagerUtil;
import com.facilityone.wireless.fm_library.zxing.camera.CameraZxingManager;
import com.facilityone.wireless.fm_library.zxing.contants.WeacConstants;
import com.facilityone.wireless.fm_library.zxing.decode.DecodeThread;
import com.facilityone.wireless.fm_library.zxing.utils.AudioPlayer;
import com.facilityone.wireless.fm_library.zxing.utils.BeepManager;
import com.facilityone.wireless.fm_library.zxing.utils.CaptureActivityHandler;
import com.facilityone.wireless.fm_library.zxing.utils.InactivityTimer;
import com.facilityone.wireless.fm_library.zxing.utils.MyUtil;
import com.facilityone.wireless.fm_library.zxing.utils.OttoAppConfig;
import com.google.zxing.Result;

import java.io.IOException;
import java.lang.reflect.Field;

import androidx.fragment.app.Fragment;

/**
 * This activity opens the camera and does the actual scanning on a background
 * thread. It draws a viewfinder to help the user place the barcode correctly,
 * shows feedback as the image processing is happening, and then overlays the
 * results when a scan is successful.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 * @author Sean Owen
 */
public final class CaptureActivity extends Activity implements SurfaceHolder.Callback,
        View.OnClickListener {

    private static final String LOG_TAG = CaptureActivity.class.getSimpleName();

    private CameraZxingManager cameraZxingManager;
    private CaptureActivityHandler handler;
    private InactivityTimer inactivityTimer;
    private BeepManager beepManager;

    private SurfaceView scanPreview = null;
    private LinearLayout scanContainer;
    private ImageView mLightBtn;
    private LinearLayout scanCropView;

    private Rect mCropRect = null;
    private boolean isHasSurface = false;

    public static String QR_CODE_CONTENT = "qr_code_content";
    public static String TITLE_NAME = "title_name";

    public Handler getHandler() {
        return handler;
    }

    public CameraZxingManager getCameraZxingManager() {
        return cameraZxingManager;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        OttoAppConfig.getInstance().register(this);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_capture);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);

        scanPreview = (SurfaceView) findViewById(R.id.capture_preview);
        scanContainer = (LinearLayout) findViewById(R.id.capture_container);
        scanCropView = (LinearLayout) findViewById(R.id.capture_crop_view);
        ImageView scanLine = (ImageView) findViewById(R.id.capture_scan_line);

        inactivityTimer = new InactivityTimer(this);
        beepManager = new BeepManager(this);

        TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation
                .RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,
                0.9f);
        animation.setDuration(2000);
        animation.setRepeatCount(-1);
        animation.setRepeatMode(Animation.RESTART);
        scanLine.startAnimation(animation);

        initButton();
    }

    private void initButton() {
        ImageView backBtn = (ImageView) findViewById(R.id.action_back);
        TextView albumBtn = (TextView) findViewById(R.id.action_album);
        mLightBtn = (ImageView) findViewById(R.id.action_light);

        backBtn.setOnClickListener(this);
        albumBtn.setOnClickListener(this);
        mLightBtn.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        cameraZxingManager = new CameraZxingManager(getApplication());

        handler = null;

        if (isHasSurface) {
            initCamera(scanPreview.getHolder());
        } else {
            scanPreview.getHolder().addCallback(this);
        }
        inactivityTimer.onResume();
    }

    @Override
    public void onPause() {
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        inactivityTimer.onPause();
        beepManager.close();
        cameraZxingManager.closeDriver();
        if (!isHasSurface) {
            scanPreview.getHolder().removeCallback(this);
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
        offLight();
        OttoAppConfig.getInstance().unregister(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (holder == null) {
            Log.e(LOG_TAG, "*** WARNING *** surfaceCreated() gave us a null surface!");
        }
        if (!isHasSurface) {
            isHasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isHasSurface = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    /**
     * A valid barcode has been found, so give an indication of success and show
     * the results.
     *
     * @param rawResult The contents of the barcode.
     * @param bundle    The extras
     */
    public void handleDecode(Result rawResult, Bundle bundle) {
        inactivityTimer.onActivity();

        if (rawResult == null) {
            ShowNotice.showShortNotice(this, R.string.decode_null);
            return;
        }

//        beepManager.playBeepSoundAndVibrate();
        AudioPlayer.getInstance(this).playRaw(R.raw.scan, false, false);
//        MyUtil.vibrate(this);

        operateResult(rawResult);
    }

    private void operateResult(Result rawResult) {
        String codeType = rawResult.getBarcodeFormat().toString();
        String scanResult = rawResult.getText();
        // 二维码
        if ("QR_CODE".equals(codeType) || "DATA_MATRIX".equals(codeType)) {
            displayResult(scanResult, 0);
            // 条形码
        } else if ("EAN_13".equals(codeType)) {
            displayResult(scanResult, 1);
        } else {
            ShowNotice.showShortNotice(this, R.string.decode_null);
        }
    }

    private void openBrowser(String scanResult) {
        boolean isUrl = MyUtil.checkWebSite(scanResult);
        // 不是标准网址
        if (!isUrl) {
            // 如果是没有添加协议的网址
            if (MyUtil.checkWebSitePath(scanResult)) {
                scanResult = "http://" + scanResult;
                isUrl = true;
            }
        }

        // 扫描结果为网址
        if (isUrl) {
            try {
                Intent intent = new Intent("android.intent.action.VIEW");
//                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                Uri uri = Uri.parse(scanResult);
                intent.setData(uri);
                myFinish(intent);
            } catch (Exception e) {
                Log.e(LOG_TAG, "handleDecode: " + e.toString());
                displayResult(scanResult, 0);
            }
        } else {
            displayResult(scanResult, 0);
        }
    }

    public static final String SCAN_RESULT = "scan_result";
    public static final String SCAN_TYPE = "scan_type";

    /**
     * 显示扫描结果
     *
     * @param resultString 扫描内容
     * @param type       扫描类型：0，二维码；1，条形码
     */
    private void displayResult(String resultString, int type) {
        if(TextUtils.isEmpty(resultString)){
            ShowNotice.showShortNotice(this, "Scan failed!");
            return;
        }
        Intent resultIntent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString(QR_CODE_CONTENT, resultString);
//		bundle.putParcelable("bitmap", bitmap);
        resultIntent.putExtras(bundle);
        this.setResult(RESULT_OK, resultIntent);
        this.finish();
    }

    private void myFinish(Intent intent) {
        startActivity(intent);
        overridePendingTransition(R.anim.zoomin, 0);
        finish();
        overridePendingTransition(0, 0);
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (cameraZxingManager.isOpen()) {
            Log.w(LOG_TAG, "initCamera() while already open -- late SurfaceView callback?");
            return;
        }
        try {
            cameraZxingManager.openDriver(surfaceHolder);
            // Creating the handler starts the preview, which can also throw a
            // RuntimeException.
            if (handler == null) {
                handler = new CaptureActivityHandler(this, cameraZxingManager, DecodeThread.ALL_MODE);
            }

            initCrop();
        } catch (IOException ioe) {
            Log.w(LOG_TAG, ioe);
        } catch (RuntimeException e) {
            // Barcode Scanner has seen crashes in the wild of this variety:
            // java.?lang.?RuntimeException: Fail to connect to camera service
            Log.w(LOG_TAG, "Unexpected error initializing camera", e);
        }
    }

    private static final int REQUEST_MY_DIALOG = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        finish();
        overridePendingTransition(0, 0);
    }

    public void restartPreviewAfterDelay(long delayMS) {
        if (handler != null) {
            handler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
        }
    }

    public Rect getCropRect() {
        return mCropRect;
    }

    /**
     * 初始化截取的矩形区域
     */
    @SuppressWarnings("SuspiciousNameCombination")
    private void initCrop() {
        // http://skillcollege.github.io/2015/02/05/-打造极致二维码扫描系列-基于ZBar的Android平台解码/

        // 预览图的高度，也即camera的分辨率宽高
        int cameraWidth = cameraZxingManager.getCameraResolution().y;
        int cameraHeight = cameraZxingManager.getCameraResolution().x;

        /** 获取布局中扫描框的位置信息 */
        int[] location = new int[2];
        scanCropView.getLocationInWindow(location);

        // 布局文件中扫描框的左上角定点坐标
        int cropLeft = location[0];
        int cropTop = location[1] - getStatusBarHeight();

        // 布局文件中扫描框的宽高
        int cropWidth = scanCropView.getWidth();
        int cropHeight = scanCropView.getHeight();

        /** 获取布局容器的宽高 */
        int containerWidth = scanContainer.getWidth();
        int containerHeight = scanContainer.getHeight();

        /** 计算最终截取的矩形的左上角顶点x坐标 */
        int x = cropLeft * cameraWidth / containerWidth;
        /** 计算最终截取的矩形的左上角顶点y坐标 */
        int y = cropTop * cameraHeight / containerHeight;

        /** 计算最终截取的矩形的宽度 */
        int width = cropWidth * cameraWidth / containerWidth;
        /** 计算最终截取的矩形的高度 */
        int height = cropHeight * cameraHeight / containerHeight;

        /** 生成最终的截取的矩形 */
        mCropRect = new Rect(x, y, width + x, height + y);
    }

    private int getStatusBarHeight() {
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            return getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private boolean mIsLightOpen;

    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.action_back){
            // 返回按钮
            finish();
        }else if(v.getId()==R.id.action_light){
            // 开关灯按钮
            operateLight();
        }else if(v.getId()==R.id.action_album){
            // 相册按钮
            if (MyUtil.isFastDoubleClick()) {
                return;
            }

            offLight();

            Intent intent = new Intent(this, LocalAlbumActivity.class);
            intent.putExtra(WeacConstants.REQUEST_LOCAL_ALBUM_TYPE, 1);
            startActivity(intent);
            overridePendingTransition(R.anim.zoomin, 0);
        }
    }

    /**
     * 开关灯
     */
    private void operateLight() {
        if (!mIsLightOpen) {
            cameraZxingManager.openLight();
            mIsLightOpen = true;
            mLightBtn.setImageResource(R.drawable.light_pressed);
        } else {
            cameraZxingManager.offLight();
            mIsLightOpen = false;
            mLightBtn.setImageResource(R.drawable.light_normal);
        }
    }

    /**
     * 关灯
     */
    private void offLight() {
        if (mIsLightOpen) {
            cameraZxingManager.offLight();
            mIsLightOpen = false;
            mLightBtn.setImageResource(R.drawable.light_normal);
        }
    }

    private ViewGroup progressBarLlyt;

    /**
     * 跳转到二维码扫描界面
     * @param fromActivity 关联Context
     * @param resultId 二维码扫描界面返回的值标识码
     */
    public static void startActivity(Fragment fromActivity, Context context, int resultId, String name) {
        if (!CameraPermissionCheckUtil.cameraIsCanUse()) {
            if (android.os.Build.BRAND != null && android.os.Build.BRAND.equals("Xiaomi")) {
                TelephoneManagerUtil.gotoMiuiPermission(context);
                ShowNotice.showLongNotice(context, "相机 → 允许");
            } else {
                Uri packageURI = Uri.parse("package:" + context.getPackageName());
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                fromActivity.startActivity(intent);
                ShowNotice.showLongNotice(context, context.getString(R.string.scan_open_permission));
            }
            return;
            /*else if(android.os.Build.BRAND!=null && android.os.Build.BRAND.equals("Meizu")){
                TelephoneManagerUtil.gotoMeizuPermission(fromActivity);
                ShowNotice.showLongNotice(fromActivity,"拍照和录像 → 允许");
            }*/
        }
        Intent intent = new Intent(context, CaptureActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(TITLE_NAME, name);
        fromActivity.startActivityForResult(intent, resultId);
    }

    /**
     * 跳转到二维码扫描界面
     * @param fromActivity 关联Context
     * @param resultId 二维码扫描界面返回的值标识码
     */
    public static void startActivity(Activity fromActivity, int resultId, String name) {
        if (!CameraPermissionCheckUtil.cameraIsCanUse()) {
            if (android.os.Build.BRAND != null && android.os.Build.BRAND.equals("Xiaomi")) {
                TelephoneManagerUtil.gotoMiuiPermission(fromActivity);
                ShowNotice.showLongNotice(fromActivity, "相机 → 允许");
            } else {
                Uri packageURI = Uri.parse("package:" + fromActivity.getPackageName());
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                fromActivity.startActivity(intent);
                ShowNotice.showLongNotice(fromActivity, fromActivity.getString(R.string.scan_open_permission));
            }
            return;
            /*else if(android.os.Build.BRAND!=null && android.os.Build.BRAND.equals("Meizu")){
                TelephoneManagerUtil.gotoMeizuPermission(fromActivity);
                ShowNotice.showLongNotice(fromActivity,"拍照和录像 → 允许");
            }*/
        }
        Intent intent = new Intent(fromActivity, CaptureActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(TITLE_NAME, name);
        fromActivity.startActivityForResult(intent, resultId);
    }
}
