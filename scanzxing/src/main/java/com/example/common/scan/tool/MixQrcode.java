package com.example.common.scan.tool;

public class MixQrcode extends FMBaseQrcode {

    public static final String FM_QRCODE_FUNCTION_MIX = "MIX";  //混合二维码

    public MixQrcode(String str) {
        super(str);
    }

    public boolean isValid() {
        boolean res = super.isValid();
        if(res) {
            res = function != null && FM_QRCODE_FUNCTION_MIX.equals(function);
        }
        return res;
    }
}
