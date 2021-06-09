package com.facilityone.wireless.fm_library.scan;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;


import com.cbs.zxing.CaptureConfig;
import com.cbs.zxing.CaptureHelper;
import com.cbs.zxing.OnCapturedListener;
import com.cbs.zxing.camera.open.OpenCameraInterface;
import com.example.scanzxing.BuildConfig;
import com.example.scanzxing.R;
import com.facilityone.wireless.fm_library.tools.CameraPermissionCheckUtil;
import com.facilityone.wireless.fm_library.tools.ShowNotice;
import com.facilityone.wireless.fm_library.tools.TelephoneManagerUtil;
import com.facilityone.wireless.fm_library.zxing.utils.OttoAppConfig;
import com.google.zxing.client.result.ParsedResult;
import com.nostra13.universalimageloader.utils.L;

import androidx.fragment.app.Fragment;


/**
 * Created by gary.xu @ 2016-10-21
 */

public class ScanCodeActivity extends Activity implements View.OnClickListener {

    private static final String LOG_TAG = ScanCodeActivity.class.getSimpleName();

    public static String QR_CODE_CONTENT = "qr_code_content";
    public static String TITLE_NAME = "title_name";

    private CaptureHelper captureHelper;
    private ImageView backView;
    private ImageView lightView;
    private boolean mIsLightOpen;
    private Camera camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scancode);

        backView = (ImageView) findViewById(R.id.action_back);
        lightView = (ImageView) findViewById(R.id.action_light);
        backView.setOnClickListener(this);
        lightView.setOnClickListener(this);
        Point screenSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(screenSize);
        float width = screenSize.x;
        float height = screenSize.y - getResources().getDimension(R.dimen.scanqr_height);
        float length = width < height ? width : height;
        length = length * 2 / 3; // 扫码区域为屏幕短边的2/3
        float left = (width - length) / 2;
        float top = (height - length) / 2;
        float right = width - left;
        float bottom = height - top;

        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.scancode_qr);
        CaptureConfig.setDecode1D(true);
//        CaptureConfig.setVibrate(false);
//        CaptureConfig.setFlashText("测试小酱油");
        captureHelper = new CaptureHelper(this, frameLayout, new CapturedListener()
                , new Rect((int) left, (int) top, (int) right, (int) bottom));
        //captureHelper = new CaptureHelper(this,frameLayout,new CapturedListener());
        captureHelper.onCreate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (captureHelper != null) {
                captureHelper.onResume();
            }
        } catch (Exception e) {
            Log.w(this.getClass().getName(), "", e);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (captureHelper != null) {
            captureHelper.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (captureHelper != null) {
            captureHelper.onDestroy();
        }
        offLight();
        OttoAppConfig.getInstance().unregister(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.action_back) {
            //返回
            finish();
        } else if (v.getId() == R.id.action_light) {
            // 开关灯按钮
            operateLight();
        }
    }

    /**
     * 开关灯
     */
    private void operateLight() {
        if (!mIsLightOpen) {
            OpenLightOn();
            mIsLightOpen = true;
            lightView.setBackgroundResource(R.drawable.light_pressed);
        } else {
            CloseLightOff();
            mIsLightOpen = false;
            lightView.setBackgroundResource(R.drawable.light_normal);
        }
    }

    /**
     * 关灯
     */
    private void offLight() {
        if (mIsLightOpen) {
            CloseLightOff();
            mIsLightOpen = false;
            lightView.setImageResource(R.drawable.light_normal);
        }
    }

    private void OpenLightOn() {
        if (this.camera != null) {
            Camera.Parameters mParameters = camera.getParameters();
            mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            camera.setParameters(mParameters);
            camera.startPreview();
            camera.autoFocus(new Camera.AutoFocusCallback() {
                public void onAutoFocus(boolean success, Camera camera) {
                }
            });
        }else {
            camera = OpenCameraInterface.open();
            // TODO: 2017/3/6
        }
    }

    private void CloseLightOff() {
        if (this.camera != null) {
            Camera.Parameters mParameters = camera.getParameters();
            mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(mParameters);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    private class CapturedListener implements OnCapturedListener {
        @Override
        public void onCaptured(ParsedResult parsedResult) {
            String qrcode = parsedResult.getDisplayResult();
            if (BuildConfig.DEBUG) {
                L.e("二维码扫描结果: " + qrcode);
            }
            if (TextUtils.isEmpty(qrcode)) {
                ShowNotice.showShortNotice(ScanCodeActivity.this, "Scan failed!");
                return;
            }
            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString(QR_CODE_CONTENT, qrcode);
            resultIntent.putExtras(bundle);
            setResult(RESULT_OK, resultIntent);
            finish();
        }

        @Override
        public void onError(Exception e) {
            ShowNotice.showShortNotice(ScanCodeActivity.this, "Scan failed!");
        }
    }

    /**
     * 跳转到二维码扫描界面
     *
     * @param fromActivity 关联Context
     * @param resultId     二维码扫描界面返回的值标识码
     */
    public static void startActivityForResult(Fragment fromActivity, Context context, int resultId, String name) {
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
        Intent intent = new Intent(context, ScanCodeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(TITLE_NAME, name);
        fromActivity.startActivityForResult(intent, resultId);
    }

    /**
     * 跳转到二维码扫描界面
     *
     * @param fromActivity 关联Context
     * @param resultId     二维码扫描界面返回的值标识码
     */
    public static void startActivityForResult(Activity fromActivity, int resultId, String name) {
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
        Intent intent = new Intent(fromActivity, ScanCodeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(TITLE_NAME, name);
        fromActivity.startActivityForResult(intent, resultId);
    }

}