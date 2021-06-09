package com.example.common.scan.tool;

/**
 *
 * 通用二维码
 *
 * @author flynn
 * @version v1.0 2018/9/7
 */
public class CommonQrcode extends FMBaseQrcode {

    public static final String FM_QRCODE_FUNCTION_COMMON = "BASIC";


    public CommonQrcode(String str) {
        super(str);
    }

    public boolean isValid() {
        boolean res = super.isValid();
        if(res) {
            res = function != null && FM_QRCODE_FUNCTION_COMMON.equals(function);
        }
        return res;
    }
}
