package com.facilityone.wireless.fm_library.tools;

import android.hardware.Camera;

/**
 * Author：gary.xu
 * <p/>
 * Email: xuhaozv@163.com
 * <p/>
 * description: 检测手机摄像头权限是否被用户禁止
 * <p/>
 * Date: 2016/10/24 14:41
 */
public class CameraPermissionCheckUtil {
    /**
     *  返回true 表示可以使用  返回false表示不可以使用
     */
    public  static boolean cameraIsCanUse() {
        boolean isCanUse = true;
        Camera mCamera = null;
        try {
            mCamera = Camera.open();
            Camera.Parameters mParameters = mCamera.getParameters(); //针对魅族手机
            mCamera.setParameters(mParameters);
        } catch (Exception e) {
            isCanUse = false;
        }

        if (mCamera != null) {
            try {
                mCamera.release();
            } catch (Exception e) {
                e.printStackTrace();
                return isCanUse;
            }
        }
        return isCanUse;
    }
}
