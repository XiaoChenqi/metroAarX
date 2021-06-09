package com.example.scanzxing;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;

import com.example.common.scan.Inventory.InventoryMaterialQrcode;
import com.example.common.scan.assets.AssetQrcode;
import com.example.common.scan.mine.UserQrcode;
import com.example.common.scan.patrol.MixSpotEquipmentQrcode;
import com.example.common.scan.patrol.PatrolBuildingQrcode;
import com.example.common.scan.patrol.PatrolSpotQrCode;
import com.example.common.scan.util.ToastUtils;
import com.facilityone.wireless.fm_library.scan.ScanCodeActivity;
import com.facilityone.wireless.fm_library.zxing.activity.CaptureActivity;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.PermissionChecker;

public class ScanZingMainActivity extends AppCompatActivity {

    public final static int QR_CODE_SCAN_RESULT = 1001;
    private String TAG = "zhouyang";
    private Button button888;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zxing_main);
        initView();
        if (!initPermission()) {
            new AlertDialog.Builder(ScanZingMainActivity.this).setMessage("没有开启摄像机权限，是否去设置开启？")
                    .setPositiveButton("去开启", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //调用系统内部去开启权限
                            ApplicationInfo(ScanZingMainActivity.this);
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).show();
            return;
        }


        button888.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CaptureActivity.startActivity(ScanZingMainActivity.this,
                        QR_CODE_SCAN_RESULT, "测试6种");
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case QR_CODE_SCAN_RESULT: // Scan QRCode back with result.
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    //这个是截取头部三分之一代码
                    String qrcode = bundle.getString(ScanCodeActivity.QR_CODE_CONTENT);
                    Log.d(TAG, "onActivityResult: " + qrcode);
                    //判断这个三分之一代码是什么
                    //TODO  一共6种
                    UserQrcode userQrcode = new UserQrcode(qrcode);
                    InventoryMaterialQrcode materialQrcode = new InventoryMaterialQrcode(qrcode);
                    AssetQrcode assetQrcode = new AssetQrcode(qrcode);
                    PatrolSpotQrCode spotQrCode = new PatrolSpotQrCode(qrcode);
                    PatrolBuildingQrcode buildingQrcode = new PatrolBuildingQrcode(qrcode);
                    MixSpotEquipmentQrcode mixSpotEquipmentQrcode = new MixSpotEquipmentQrcode(qrcode);
                    if (mixSpotEquipmentQrcode.isValid()) {
                        Log.d(TAG, "onActivityResult: mixspot");
                        //showAlertMenu();
                    } else if (userQrcode.isValid()) {//员工二维码
                        //operateUserQrcode(qrcode);
                        Log.d(TAG, "onActivityResult: user");
                    } else if (materialQrcode.isValid()) {//物资二维码
                        ToastUtils.toast(ScanZingMainActivity.this, "不能识别这个二维码");
//                        operateMaterialQrcode(qrcode);
                    } else if (assetQrcode.isValid()) {//设备二维码
                        Log.d(TAG, "onActivityResult: asset");
                        //operateAssetQrcode(qrcode);
                    } else if (buildingQrcode.isValid()) {//车站二维码
                        Log.d(TAG, "onActivityResult: build");
                        // operatePatrolBuildingQrcode(qrcode);
                    } else if (spotQrCode.isValid()) {//点位二维码
                        Log.d(TAG, "onActivityResult: spot");
                        //operatePatrolSpotQrcode(qrcode);
                    } else {
                        ToastUtils.toast(ScanZingMainActivity.this, "不能识别这个二维码");
                    }

                    break;
                }
        }

    }

    //查看是否开启摄像头权限
    private boolean initPermission() {
        //需要在Android里面找到你要开的权限
        String permissions = Manifest.permission.CAMERA;
        boolean ret = false;
        //Android 6.0以上才有动态权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //permission granted 说明权限开了
            ret = PermissionChecker.checkSelfPermission(ScanZingMainActivity.this, permissions) == PermissionChecker.PERMISSION_GRANTED;
        }
        return ret;
    }

    //调用系统内部开启权限
    public static void ApplicationInfo(Activity activity) {
        try {
            Intent localIntent = new Intent();
            localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= 9) {
                localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                localIntent.setData(Uri.fromParts("package", activity.getPackageName(), null));
            } else if (Build.VERSION.SDK_INT <= 8) {
                localIntent.setAction(Intent.ACTION_VIEW);
                localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
                localIntent.putExtra("com.android.settings.ApplicationPkgName", activity.getPackageName());
            }
            activity.startActivity(localIntent);
        } catch (Exception e) {
        }
    }

    private void initView() {

        button888 = (Button) findViewById(R.id.button888);
    }


}
