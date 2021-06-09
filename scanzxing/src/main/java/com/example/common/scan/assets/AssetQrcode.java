package com.example.common.scan.assets;


import com.example.common.scan.tool.FMBaseQrcode;

/**
 *
 * 资产 二维码
 *
 * @author flynn
 * @version v1.0 2018/9/7
 */
public class AssetQrcode extends FMBaseQrcode {

    public static final String FM_QRCODE_FUNCTION_ASSET = "ASSET";


    public AssetQrcode(String str) {
        super(str);
    }

    public boolean isValid() {
        boolean res = super.isValid();
        if(res) {
            String function = getFunction();
            res = function != null && FM_QRCODE_FUNCTION_ASSET.equals(function);
        }
        return res;
    }
}
