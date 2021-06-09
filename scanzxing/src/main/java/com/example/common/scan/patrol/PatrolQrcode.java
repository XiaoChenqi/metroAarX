package com.example.common.scan.patrol;


import com.example.common.scan.tool.FMBaseQrcode;

/**
 *
 * 巡检二维码
 *
 * */
public class PatrolQrcode extends FMBaseQrcode {

    public static final String FM_QRCODE_FUNCTION_PATROL = "PATROL";

    public PatrolQrcode(String str) {
        super(str);
    }

    public boolean isValid() {
        boolean res = super.isValid();
        if(res) {
            res = function != null && FM_QRCODE_FUNCTION_PATROL.equals(function);
        }
        return res;
    }
}
