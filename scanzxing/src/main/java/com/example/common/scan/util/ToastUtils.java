package com.example.common.scan.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;



/**
 * Author：gary.xu
 * <p/>
 * Email: xuhaozv@163.com
 * <p/>
 * description:toast工具
 * <p/>
 * Date: 2016/11/30 14:37
 */
public class ToastUtils {

    public static Toast mToast;
    /**
     * Toast
     *
     * @param content
     */
    public static void toast(final Context _context, final String content) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (mToast == null) {
                    mToast = Toast.makeText(_context, "", Toast.LENGTH_SHORT);
                }
                mToast.setText(content);
                mToast.show();
            }
        });
    }

    /**
     * Toast
     *
     * @param id
     */
    public static void toast(final Context _context,int id) {
        toast(_context,_context.getResources().getString(id));
    }

    public static void toast(final Context _context,String info,int id) {
        toast(_context,info + _context.getResources().getString(id));
    }
}
